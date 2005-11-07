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
import iepp.ui.iedition.VueDPGraphe;

import java.awt.*;
import java.awt.event.*;
import java.io.Serializable;

import util.Vecteur;

/**
* Outil est la classe de base pour l'impl�mentation des outils.
* Le but principal des outils est de g�rer les entr�es (souris et clavier) dans le but
* d'impl�menter diff�rents comportements comme la s�lection ou la cr�ation d'�l�ments.
*/
public class Outil implements MouseListener, MouseMotionListener, KeyListener, Serializable
{

    /**
    * Etat initial de tous les �l�ments.
    */
    protected static final int IDLE_STATE = 0;

    /**
    * Etat courant de l'outil.
    */
	protected int state;

    /**
    * start est l'endroit du click de la souris, ou de son premier emplacement si on est
    * en train de dragger.
    */
	protected Vecteur start;

    /**
    * current est la position courante de la souris.
    */
	protected Vecteur current;

	/**
	 * Diagramme sur lequel on va utiliser l'outil courant
	 */
	protected VueDPGraphe diagramme;

	
    /**
     * Constructeur d'Outil
	 * @param diag, diagramme sur lequel on va appliquer l'outil cr��
	 */
	public Outil(VueDPGraphe diag)
    {
    	// �tat initial
        this.state = IDLE_STATE;
        // initialisation  du premier click
        this.start = new Vecteur();
        // initialisation vide de la position courante
        this.current = new Vecteur();
        // garder un lien vers le diagramme
        this.diagramme = diag;
    }

    /**
    Ajoute une �dition dans la pile undo du diagramme actif, apr�s l'avoir ex�cut�e.
    */
    /*
    void ajouterEditionDiagramme(Edition edition)
    {
       // ConsistanceDiagramme c = new ConsistanceDiagramme(edition);

        if (c.evaluer()) {
          edition.execute();
          diagramme.push(edition);
        }
        else
          c.traiterInvalide();

        terminer();
    }
	*/
	
   /**
    * D�truit l'outil courant et fixe OSelection comme nouvel outil courant.
    */
    public void terminer()
    {
       
        this.update();
	    // reprendre l'outil de s�lection
		Application.getApplication().getProjet().getFenetreEdition().setOutilSelection();
    }

    /**
    * Rafra�chit la fen�tre principale apr�s des modifications sur le diagramme
    */
    public void update()
    {
        this.diagramme.repaint();
    }

    /**
    * Retourne la position du click de la souris.
    */
    public Vecteur getStart()
    {
        return this.start;
    }

    /**
    * Retourne la position courante de la souris.
    */
    public Vecteur getCurrent()
    {
        return this.current;
    }

    /**
    * Certains outils (commme la s�lection) n�cessitent une m�thode de dessin.
    */
    public void draw( Graphics g ) {}

    //------------------------------------------------------------------------
    //                      M�thodes MouseListener
    //------------------------------------------------------------------------
    public void mouseClicked( MouseEvent e ) {}
    public void mouseEntered( MouseEvent e ) {}
    public void mouseExited( MouseEvent e ) {}
	public void mousePressed( MouseEvent event )
    {
	   this.start.setValue(event.getX(),event.getY());
    }
    
    public void mouseReleased( MouseEvent event )
    {
		this.current.setValue(event.getX(),event.getY());
    }

    //------------------------------------------------------------------------
    //                   M�thodes MouseMotionListener
    //------------------------------------------------------------------------
    public void mouseDragged( MouseEvent event )
    {
		this.current.setValue(event.getX(),event.getY());
    }

    public void mouseMoved( MouseEvent event )
    {
        this.current.setValue(event.getX(),event.getY());
    }

    //------------------------------------------------------------------------
    //                     M�thodes KeyListener
    //------------------------------------------------------------------------
    public void keyTyped( KeyEvent event ) {}
    public void keyPressed( KeyEvent event ) {}
    public void keyReleased( KeyEvent event ) {}
    
}
