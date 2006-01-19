/*
 * IEPP: Isi Engineering Process Publisher
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */
 
 
package iepp.application.aedition.aoutil;


import iepp.Application;
import iepp.application.aedition.CLier2Produits;
import iepp.ui.iedition.VueDPGraphe;
import iepp.ui.iedition.dessin.rendu.FElement;
import iepp.ui.iedition.dessin.rendu.Figure;
import iepp.ui.iedition.dessin.rendu.liens.FLien;
import iepp.ui.iedition.dessin.vues.MDElement;
import iepp.ui.iedition.dessin.vues.MDLien;

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.util.*;

import util.Vecteur;

/**
* Outil permettant d'interpr�ter les clicks & d�placement de l'utilisateur pour lier
* et afficher le lien entre deux �l�ments
*/
public class OLier2Elements extends Outil
{

	// propri�t�s des liens
    final static BasicStroke classic = new BasicStroke(1.0f,BasicStroke.CAP_ROUND , BasicStroke.JOIN_ROUND);
    final static float dot1[] = {3.0f};
    final static BasicStroke dotted = new BasicStroke(1.0f,BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 4.0f, dot1, 0.0f);
    final static float dash1[] = {10.0f};
    final static BasicStroke dashed = new BasicStroke(1.0f,BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 4.0f, dash1, 0.0f);

    /**
    * Lien qui va �tre cr��
    */
    private FLien lien;

    /**
    * Figures source et destination du lien.
    */
	private FElement source, destination;

    /**
    * Derni�re figure cliqu�e. NULL si on a cliqu� sur le diagramme.
    */
	private Figure figureCliquee;
    
    /**
     * Couleur du lien
     */
	private Color lineColor;

    /**
    * Etat de d�part : on a cliqu� sur la premi�re figure (source du lien).
    */
	private static final int START_STATE = 1;

    /**
    * Fin de la liaison.
    */
	private static final int END_STATE = 2;

	/**
	 * Liste des points d'ancrage du lien courant
	 */
    protected Vector pointsAncrageIntermediaires;


	/**
	 * Construire l'outil � partir du lien � cr�er, de la couleur du lien et du diagramme
	 * sur lequel on veut r�aliser le lien
	 * @param vue, diagramme sur lequel on veut r�aliser le lien
	 * @param lineColorn couleur du lien � dessiner
	 * @param lien, nouveau lien � initialiser
	 */
    public OLier2Elements(VueDPGraphe vue, Color lineColor, FLien lien)
    {
        super(vue);
        this.lien = lien;
        this.lineColor = lineColor;
        this.pointsAncrageIntermediaires = new Vector();
    }


    /**
    * Retourne la couleur des traits.
    */
    public Color getLineColor()
    {
        return this.lineColor;
    }
    /**
    * Fixe la couleur des traits.
    */
    public void setLineColor(Color c) 
    {
        this.lineColor = c;
    }


    /**
    * Dessine le lien en cours de cr�ation.
    */
    public void draw(Graphics g)
    {
        if(this.state == START_STATE)
        {
            Graphics2D g2d = (Graphics2D) g;
            Stroke stroke = g2d.getStroke();

            // Dessine le corps du lien (lignes entre les points d'ancrage)
            if (((MDLien) this.lien.getModele()).getStyle() == MDLien.STYLE_CLASSIC)
            {
            	g2d.setStroke(classic);
            }
            else if (((MDLien) this.lien.getModele()).getStyle() == MDLien.STYLE_DOTTED)
            {
            	g2d.setStroke(dotted);
            } 
            else if (((MDLien) this.lien.getModele()).getStyle() == MDLien.STYLE_DASHED)
            {
            	g2d.setStroke(dashed);
            } 

            g2d.setColor(this.lineColor);

            Vecteur v1, v2;
            MDElement ms = (MDElement) this.source.getModele();
            // v1 contient les coordonn�es du centre de l'�l�ment de d�part.
            v1 = new Vecteur(ms.getX() + ms.getLargeur()/2,
                             ms.getY() + ms.getHauteur()/2);
            for (int i = 0; i < this.pointsAncrageIntermediaires.size(); i++)
            {
              v2 = (Vecteur) this.pointsAncrageIntermediaires.elementAt(i);
              g2d.draw(new Line2D.Double(v1.x, v1.y, v2.x, v2.y));
              v1 = v2;
            }

            g2d.draw(new Line2D.Double(v1.x, v1.y, getCurrent().x, getCurrent().y));
            g2d.setStroke(stroke);
        }
    }

    public void mouseMoved( MouseEvent event )
    {
        super.mouseMoved(event);

        if(this.state == START_STATE)
        {
            this.update();
        }
    }

    /**
    * Un click sur un premier �l�ment d�marre la construction du lien.
    * Un click sur un second �l�ment finalise le lien.
    */
    public void mousePressed( MouseEvent event )
    {
        super.mousePressed(event);

        this.figureCliquee = this.diagramme.chercherFigure(start.x, start.y);

        // Click sur autre chose qu'un �l�ment
        if (this.figureCliquee != null && (this.figureCliquee instanceof FElement))
        {
            // Premier click.
            if (this.state == IDLE_STATE)
            {
                this.source = (FElement) this.figureCliquee;
                this.diagramme.clearSelection();
                this.state = START_STATE;
            }
            // A partir du deuxi�me click.
            else if (this.state == START_STATE)
            {
            	if (this.figureCliquee != source || this.pointsAncrageIntermediaires.size() > 0)
            	{
                	this.destination = (FElement) this.figureCliquee;
              		//  ajouterEditionDiagramme(new Lier2Elements(diagramme, lien, source, destination, pointsAncrageIntermediaires));
              		/*CLier2Produits c = new CLier2Produits(diagramme, source, destination, pointsAncrageIntermediaires);
              		if (c.executer())
      			    {
      			   		Application.getApplication().getProjet().setModified(true);
      			    }*/
                	this.state = END_STATE;
              	}
            }
        }
        else
        {
            if (state == START_STATE)
            {
            	this.pointsAncrageIntermediaires.add(new Vecteur(getCurrent()));
            }
            else
            {
            	this.state = END_STATE;
            } 
        }
    }

    /**
    * On rel�che la souris.
    */
    public void mouseReleased( MouseEvent event )
    {
        //super.mouseReleased(event);

        // Si la liaison est termin�e (qu'elle ait �chou� ou pas)
        if (this.state == END_STATE)
        {
			// rafraichir le diagramme
        	this.terminer();
        }
    }
}
