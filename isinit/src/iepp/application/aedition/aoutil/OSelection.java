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
import iepp.application.aedition.CCreerPointAncrage;
import iepp.application.aedition.CDeplacerElement;
import iepp.application.aedition.CDeplacerPointAncrage;
import iepp.application.aedition.CRedimensionnerElement;
import iepp.application.aedition.CSupprimerPointAncrage;
import iepp.ui.iedition.VueDPGraphe;
import iepp.ui.iedition.dessin.rendu.ComposantCell;
import iepp.ui.iedition.dessin.rendu.FElement;
import iepp.ui.iedition.dessin.rendu.Figure;
import iepp.ui.iedition.dessin.rendu.IeppCell;
import iepp.ui.iedition.dessin.rendu.LienEdge;
import iepp.ui.iedition.dessin.rendu.ProduitCellFusion;
import iepp.ui.iedition.dessin.rendu.TextCell;
import iepp.ui.iedition.dessin.rendu.handle.Handle;
import iepp.ui.iedition.dessin.rendu.handle.elements.HandleElement;
import iepp.ui.iedition.dessin.rendu.handle.liens.HandleLien;
import iepp.ui.iedition.dessin.rendu.liens.FLien;
import iepp.ui.iedition.popup.PopupDiagramme;
import iepp.ui.iedition.popup.PopupFComposantProcessus;
import iepp.ui.iedition.popup.PopupFLienFusion;
import iepp.ui.iedition.popup.PopupFNote;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import util.Vecteur;

/**
 * La s�lection est l'outil principal � toutes les applications de dessin. C'est
 * l'outil le plus complexe, car il g�re � la fois la s�lection multiple et la
 * translation.
 */
public class OSelection extends Outil {

	/**
	 * Derni�re figure cliqu�e. NULL si on a cliqu� sur le diagramme.
	 */
	private Figure figureCliquee;

	/**
	 * Derni�re poign�e cliqu�e. NULL si on a cliqu� sur le diagramme.
	 */
	private Handle handleClique;

	/**
	 * L'outil d�place les figures s�lectionn�es.
	 */
	public static final int TRANSLATE_STATE = 1;

	/**
	 * L'outil s�lectionne toutes les figures dans le rectangle.
	 */
	public static final int SELRECT_STATE = 2;

	/**
	 * L'outil redimensionne la figure.
	 */
	public static final int MOVE_HANDLE = 3;

	/**
	 * L'outil clique sur controle.
	 */
	public static final int CTRL_HANDLE = 4;

	/**
	 * Cr�ation d'un outil de s�lection d'�lement dans le diagramme
	 * 
	 * @param diag,
	 *            diagramme sur lequel on va effectuer la s�lection
	 */
	public OSelection(VueDPGraphe diag) {
		super(diag);
	}

	// ------------------------------------------------------------------------
	// Gestion des �vennements
	// ------------------------------------------------------------------------

	/**
	 * Un click sur le diagramme ou un lien d�selectionne tout. Un click (sur un
	 * �l�ment non-s�lectionn�) d�selectionne tous les �l�ments et s�lectionne
	 * celui click�. Un click avec la touche CTRL enfonc�e, ajoute/enl�ve
	 * l'�l�ment click� de la s�lection.
	 */
	public void mousePressed(MouseEvent event) {
		// mettre � jour les coordonn�es du dernier click
		super.mousePressed(event);
		//System.out.println(diagramme.getFirstCellForLocation(event.getX(), event.getY()).getClass().getName());
		// on v�rifie si on a cliqu� sur une figure
		if (diagramme.getFirstCellForLocation(event.getX(), event.getY()) instanceof IeppCell) {

			if (event.isControlDown()) {
				this.state = CTRL_HANDLE;
			}

		} else {

			this.state = IDLE_STATE;

		}

		diagramme.repaint();
		this.update();

	}

	/**
	 * Translation d'�l�ments, ou affichage du rectangle de s�lection.
	 */
	public void mouseDragged(MouseEvent event) {
		// super.mouseDragged(event);

		if (event.isControlDown() && state == CTRL_HANDLE ) {

			if (diagramme.getFirstCellForLocation(event.getX(), event.getY()) instanceof IeppCell) {
				// ne rien faire pour eviter la copie de cellule
				diagramme.setDropEnabled(false);
				
			}

		} else {
			diagramme.setDropEnabled(true);
		}

		//this.update();
	}

	/**
	 * Si on rel�che la souris : - apr�s une s�lection : tous les �l�ments dans
	 * le rectangle sont s�lectionn�s. - apr�s une translation : ajout de la
	 * translation dans la pile undo. - ...
	 */
	public void mouseReleased(MouseEvent event) 
	{
		super.mouseReleased(event);

			// Hubert : popup menu sur le graph (hors cellules et lien)
			if (!((diagramme
					.getFirstCellForLocation(event.getX(), event.getY()) instanceof IeppCell) || (diagramme
					.getFirstCellForLocation(event.getX(), event.getY()) instanceof LienEdge))) {
				if (event.isPopupTrigger()) {
					showPopupMenuDiagramme();
				}
			}
			// modif aldo nit 15/01/06
			else if (event.isPopupTrigger()) {
						if (diagramme.getFirstCellForLocation(event.getX(), event.getY()) instanceof ComposantCell){
							ComposantCell ic = (ComposantCell) diagramme.getFirstCellForLocation(event.getX(), event.getY());
							
							PopupFComposantProcessus p = new PopupFComposantProcessus(ic);
							
							p.show(diagramme, event.getX(), event.getY());
						}
						else if (diagramme.getFirstCellForLocation(event.getX(), event.getY()) instanceof TextCell) {
								showPopupMenuNote((TextCell)diagramme.getFirstCellForLocation(event.getX(), event.getY()));
							
						}
						else if (diagramme.getFirstCellForLocation(event.getX(), event.getY()) instanceof ProduitCellFusion){
							ProduitCellFusion pf=(ProduitCellFusion) diagramme.getFirstCellForLocation(event.getX(), event.getY());
							PopupFLienFusion f=new PopupFLienFusion(diagramme,pf,event.getX(),event.getY());
							f.show(diagramme,event.getX(),event.getY());
						}
				// On bouge un objet
				Vecteur translation = new Vecteur();
				translation.setSubstraction(this.current, this.start);
				CDeplacerElement c = new CDeplacerElement(this.diagramme,translation);
				if (c.executer()) {
					Application.getApplication().getProjet().setModified(true);
				}
			}

				
				
			
		this.update();
	}


	/**
	 * La touche SUPPR d�truit les �l�ments s�lectionn�s du diagramme.
	 */
	public void keyReleased(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.VK_DELETE) {
			if (this.diagramme.nbSelectedElements() > 0) {
				// ajouterEditionDiagramme(new SupprimerSelection(diagramme));
			}
		}
		this.terminer();
	}

	// ------------------------------------------------------------------------
	// Menus contextuels
	// ------------------------------------------------------------------------

	/**
	 * Affiche le menu popup (contextuel) pour un �l�ment.
	 */
	protected void showPopupMenuFigure(ComposantCell ic) {
		PopupFComposantProcessus p = new PopupFComposantProcessus(ic);
		p.show(diagramme, getStart().x, getStart().y);
	}

	/**
	 * Affiche le menu popup (contextuel) pour un diagramme.
	 */
	protected void showPopupMenuDiagramme() {
		PopupDiagramme p = new PopupDiagramme(diagramme, getStart().x,
				getStart().y);
		p.show(diagramme, getStart().x, getStart().y);
	}
	
	/**
	 * Affiche le menu popup (contextuel) pour un diagramme.
	 */
	protected void showPopupMenuNote(TextCell note) {
		PopupFNote p = new PopupFNote(diagramme, note);
		System.out.println("Modification note");
		p.show(diagramme, getStart().x, getStart().y);
	}
	
}
