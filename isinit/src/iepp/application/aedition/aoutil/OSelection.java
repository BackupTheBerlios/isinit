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
import iepp.application.aedition.*;
import iepp.ui.iedition.VueDPGraphe;
import iepp.ui.iedition.dessin.rendu.*;
import iepp.ui.iedition.dessin.rendu.handle.Handle;
import iepp.ui.iedition.dessin.rendu.handle.elements.HandleElement;
import iepp.ui.iedition.dessin.rendu.handle.liens.HandleLien;
import iepp.ui.iedition.dessin.rendu.liens.FLien;
import iepp.ui.iedition.popup.PopupDiagramme;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import util.Vecteur;


/**
* La sélection est l'outil principal à toutes les applications de dessin.
* C'est l'outil le plus complexe, car il gère à la fois la sélection multiple
* et la translation.
*/
public class OSelection extends Outil
{

	/**
	* Couleur de la ligne de sélection
	*/
	private Color lineColor = Color.BLUE;
	
    /**
    * Dernière figure cliquée. NULL si on a cliqué sur le diagramme.
    */
	private Figure figureCliquee;

    /**
    * Dernière poignée cliquée. NULL si on a cliqué sur le diagramme.
    */
	private Handle handleClique;

    /**
    * L'outil déplace les figures sélectionnées.
    */
    public static final int TRANSLATE_STATE = 1;

    /**
    * L'outil sélectionne toutes les figures dans le rectangle.
    */
    public static final int SELRECT_STATE = 2;

    /**
    * L'outil redimensionne la figure.
    */
    public static final int MOVE_HANDLE = 3;

	/**
	* Création d'un outil de sélection d'élement dans le diagramme
	* @param diag, diagramme sur lequel on va effectuer la sélection
	*/
	public OSelection(VueDPGraphe diag)
	{
		super(diag);
	}

	/**
    * Dessine le rectangle de sélection OU les "ombres" des objets déplacés.
    * @param g, contexte graphique sur lequel on va afficher les éléments
    */
    public void draw( Graphics g )
    {
    	// si on essaye de redimensionner un élement
        if (this.state == MOVE_HANDLE)
        {
            this.handleClique.drawNewFigureShadow(g, lineColor, current.x, current.y);
        }
        // si on sélectionne des éléments avec un carré de sélection
        if (this.state == SELRECT_STATE)
        {
        	// récupérer le contexte 2D
            Graphics2D g2d = (Graphics2D) g;
            // pour afficher une ligne en pointillé
            Stroke stroke = g2d.getStroke();

			// dessin du carré de sélection, selon la couleur définie
            float dot1[] = {3.0f};
            BasicStroke dotted = new BasicStroke(1.0f,BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 4.0f, dot1, 0.0f);
            g2d.setStroke(dotted);
            g2d.setColor(lineColor);
            g2d.drawRect(Math.min((int)start.x, (int)current.x), Math.min((int)start.y, (int)current.y), Math.abs((int)(current.x-start.x)), Math.abs((int)(current.y-start.y)));
            g2d.setStroke(stroke);
        }
        // si on essaye de déplacer un ou plusieurs éléments
        if (this.state == TRANSLATE_STATE)
        {
        	// sauvegarder les propriétés du déplacement (de la translation) effectué
            Vecteur translation = new Vecteur();
            translation.setSubstraction(current,start);
            // récupérer tous les éléments sélectionnés dans le diagramme courant
            Enumeration e = diagramme.selectedElements();
            // figure en train d'être traitée
            Figure f;
            // parcourir ces éléments sélectionnés
            while(e.hasMoreElements())
            {
            	// on récupère une figure sélectionnée
                f = (Figure)e.nextElement();
                // si on sélectionne un élément (composant ou produit)
                if (f instanceof FElement)
                {
                	// dessiner l'ombre de cette figure en train d'être déplacée
                  ((FElement) f).drawElementShadow(g, lineColor, translation.x, translation.y, 0, 0);
                }
                // sinon si c'est un lien que l'on déplace
                else if (f instanceof FLien)
                {
                	// si on a aussi sélectionné les sources et les destinations du lien
                	// on déplace ce lien
                  	if (((FLien) f).getSource().estSelectionne()
                   	&& ((FLien) f).getDestination().estSelectionne())
                   	{
                        ((FLien) f).drawLinkShadow(g, lineColor, ((FLien) f).getPath(translation.x, translation.y));
                   	}
                }
            }
        }
    }


	//------------------------------------------------------------------------
	//                      Gestion des évennements
	//------------------------------------------------------------------------
	  	   
    /**
    * Un click sur le diagramme ou un lien déselectionne tout.
    * Un click (sur un élément non-sélectionné) déselectionne tous les éléments et sélectionne celui clické.
    * Un click avec la touche CTRL enfoncée, ajoute/enlève l'élément clické de la sélection.
    */
    public void mousePressed( MouseEvent event )
    {
    	// mettre à jour les coordonnées du dernier click
        super.mousePressed(event);

        // Si il n'y a qu'un seul élément sélectionné
        // on teste si on a cliqué sur une de ses poignées.
        if (diagramme.nbSelectedElements() == 1)
        {
        	this.handleClique = diagramme.chercherHandleFigure(start.x, start.y);
        }
		
		// on a cliqué sur une poignée
        if (this.handleClique != null)
        {
			// on a cliqué sur le un lien et sur shift
          	if ((this.handleClique instanceof HandleLien) && event.isShiftDown())
          	{
          		// on supprimer le point d'ancrage du lien
            	this.state = IDLE_STATE;
           		// ajouterEditionDiagramme(new SupprimerPointAncrage((FLien) handleClique.getFigure(), ((HandleLien) handleClique).getPointAncrage()));
            	CSupprimerPointAncrage c = new CSupprimerPointAncrage((FLien) handleClique.getFigure(), ((HandleLien) handleClique).getPointAncrage());
            	if (c.executer())
  			    {
  			   		Application.getApplication().getProjet().setModified(true);
  			    }
            	this.terminer();
          	}
        }
        else
        {
			
			
        	// on vérifie si on a cliqué sur une figure
        	this.figureCliquee = this.diagramme.chercherFigure(this.getStart().x, this.getStart().y);

         	 // Click sur le diagramme
          	 if (this.figureCliquee == null)
          	 {
				// Sur les machines Solaris, mousePressed
              	// fixe le PopupTrigger (en général le bouton droit).
              	if (event.isPopupTrigger())
              	{
                	this.state = IDLE_STATE;
                	this.showPopupMenuDiagramme();
              	}
              	if(!event.isControlDown())
              	{
              	  	this.diagramme.clearSelection();
              	}
         	 }
         	 // Click sur une figure
          	 else
          	 {
             	// Sur les machines Solaris, mousePressed
              	// fixe le PopupTrigger (en général le bouton droit).
              	if (event.isPopupTrigger())
              	{
                  	this.state = IDLE_STATE;
                  	this.showPopupMenuFigure();
             	}
              	else
              	{
                  	if (this.figureCliquee instanceof FLien)
                  	{
						if (this.figureCliquee.contient(this.getStart()) && event.isShiftDown())
                    	{
                      		this.state = IDLE_STATE;
                     		// ajouterEditionDiagramme(new CreerPointAncrage((FLien) figureCliquee, getStart()));
                      		CCreerPointAncrage c = new CCreerPointAncrage((FLien) figureCliquee, getStart());
                      		if (c.executer())
              			    {
              			   		Application.getApplication().getProjet().setModified(true);
              			    }
                      		this.terminer();
                      	}
                  	}
                  	if (!this.figureCliquee.estSelectionne())
                  	{
                      	if(!event.isControlDown())
                      	{
                      	   this.diagramme.clearSelection();
                      	}
                      	this.diagramme.changeSelection(this.figureCliquee);
                  	}
                 	else
                 	{
                      	if(event.isControlDown())
                      	{
                       		this.diagramme.changeSelection(this.figureCliquee);
                      	} 
                  	}
             	}
          	}
        }
        this.update();
        this.state = IDLE_STATE;
    }


    /**
    * Translation d'éléments, ou affichage du rectangle de sélection.
    */
    public void mouseDragged( MouseEvent event )
    {
        super.mouseDragged(event);
        
        // Drag sur le diagramme
        if (this.handleClique != null)
        {
         	 if (! this.handleClique.getFixe())
             {
             	this.state = MOVE_HANDLE;
             } 
        }
        else
        {
        	// on fait un carré de sélection
          	if (figureCliquee == null)
          	{
            	this.state = SELRECT_STATE;
         	}
         	// Drag sur une figure
          	else
          	{
	            if(! event.isControlDown())
	            { 
		              if (figureCliquee instanceof FLien)
		              {
		                // Si on essaie de déplacer uniquement un lien, on arrête tout.
		                if (! ((FLien) figureCliquee).getSource().estSelectionne()
		                 	|| ! ((FLien) figureCliquee).getDestination().estSelectionne())
		                {
		                     this.state = IDLE_STATE;
		                }
		                else
		                {
		                  	this.state = TRANSLATE_STATE;
		                } 
		              }
		              else
		              {
		                	this.state = TRANSLATE_STATE;
		              }
            	}
         	}
        }
        this.update();
    }


    /**
    * Si on relâche la souris :
    * - après une sélection : tous les éléments dans le rectangle sont sélectionnés.
    * - après une translation : ajout de la translation dans la pile undo.
    * - ...
    */
    public void mouseReleased( MouseEvent event )
    {
      	super.mouseReleased(event);
      	if (this.handleClique != null)
      	{
      		this.state = IDLE_STATE;
          	
      		if (this.handleClique instanceof HandleElement)
          	{
            	HandleElement h = (HandleElement) this.handleClique;
           		CRedimensionnerElement c = new CRedimensionnerElement((FElement) h.getFigure(), h.newFigureX,  h.newFigureY,  h.newFigureLargeur,  h.newFigureHauteur);
           		if (c.executer())
			    {
			   		Application.getApplication().getProjet().setModified(true);
			    }
          	}
          	else if (this.handleClique instanceof HandleLien)
          	{
            	HandleLien h = (HandleLien) handleClique;
           		CDeplacerPointAncrage c = new CDeplacerPointAncrage(h.getPointAncrage(), new Vecteur(getCurrent()));
           		if (c.executer())
			    {
			   		Application.getApplication().getProjet().setModified(true);
			    }
          	}
      	}
      	else
      	{
      		 // On a cliqué sur le diagramme
        	if (this.figureCliquee == null)
        	{
          		// Sur les machines avec Windows, mouseReleased
          		// fixe le PopupTrigger (en général le bouton droit).
          		if (event.isPopupTrigger())
          		{
            		this.state = IDLE_STATE;
            		this.showPopupMenuDiagramme();
          		}
          		else
          		{
            		if (this.state == SELRECT_STATE)
            		{
              			this.state = IDLE_STATE;
              			if(!event.isControlDown())
                		{
                			this.diagramme.clearSelection();
                		} 
						this.diagramme.changeSelectionFigures(
                			new Vecteur(Math.min(this.start.x, this.current.x),Math.min(this.start.y, this.current.y)),
               			 	new Vecteur(Math.max(this.start.x, this.current.x),Math.max(this.start.y, this.current.y)));
            		}
          		}
       		}
			else
			{
				// On a cliqué sur une figure
          		// Sur les machines avec Windows, mouseReleased
          		// fixe le PopupTrigger (en général le bouton droit).
          		if (event.isPopupTrigger())
          		{
            		this.state = IDLE_STATE;
            		this.showPopupMenuFigure();
          		}
          		else
          		{
            		if (this.state == TRANSLATE_STATE)
            		{
              			this.state = IDLE_STATE;
              			Vecteur translation = new Vecteur();
              			translation.setSubstraction(this.current,this.start);
              			CDeplacerElement c = new CDeplacerElement(this.diagramme,translation);
              			if (c.executer())
          			    {
          			   		Application.getApplication().getProjet().setModified(true);
          			    }
              			
            		}
            		else
            		{
              			this.state = IDLE_STATE;
            		}
          		}
        	}
      	}
      	this.update();
    }

    /**
    * La touche SUPPR détruit les éléments sélectionnés du diagramme.
    */
    public void keyReleased( KeyEvent event )
    {
        if(event.getKeyCode() == KeyEvent.VK_DELETE)
        {
        	if (this.diagramme.nbSelectedElements() > 0)
        	{
          	// ajouterEditionDiagramme(new SupprimerSelection(diagramme));
        	}
      	}
	    this.terminer();
    }


	//------------------------------------------------------------------------
	//                      Menus contextuels
	//------------------------------------------------------------------------
	
    /**
    * Affiche le menu popup (contextuel) pour un élément.
    */
    protected void showPopupMenuFigure()
    {
        this.figureCliquee.doOnRightClick(this.diagramme, this.getStart().x, this.getStart().y);
    }

    /**
    * Affiche le menu popup (contextuel) pour un diagramme.
    */
    protected void showPopupMenuDiagramme()
    {
        PopupDiagramme p = new PopupDiagramme(diagramme, getStart().x, getStart().y);
        p.show(diagramme, getStart().x, getStart().y);
    }
}
