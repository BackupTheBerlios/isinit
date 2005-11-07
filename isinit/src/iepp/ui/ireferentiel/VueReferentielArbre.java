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
package iepp.ui.ireferentiel;

import java.awt.Component;
import java.awt.dnd.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Observable;
import java.util.Observer;

import iepp.Application;
import iepp.application.areferentiel.ElementReferentiel;
import iepp.application.areferentiel.Referentiel;
import iepp.domaine.ComposantProcessus;
import iepp.domaine.IdObjetModele;

import iepp.ui.IDTransferable;
import iepp.ui.VueDPArbre;
import iepp.ui.ireferentiel.popup.* ;

import javax.swing.* ;
import javax.swing.tree.* ;

import org.ipsquad.utils.IconManager;
import iepp.domaine.*;


/**
 * Arbre graphique pr�sentant les �l�ments contenu dans le r�f�rentiel.
 * On peut faire des glisser/deposer de composants de processus de l'arbre vers le diagramme d'assemblage.
 */
public class VueReferentielArbre extends JTree
								 implements MouseListener, Observer,
								 	DragGestureListener, DragSourceListener
{

	/**
	 * Liste des icons � utiliser dans l'arbre pour diff�rencier les �l�ments
	 */
	// pour unifier la d�finition des ic�nes
	private static final String iconeReferentiel = "edittrash.png" ;

	/**
	 * Objet permettant de r�cup�rer les �vents de drag
	 */
	private DragSource mDragSource;


	/**
	 * Construit une vue du r�f�rentiel � partir de son mod�le.
	 * @param ref r�f�rentiel (mod�le) observ� par cette vue
	 */
	public VueReferentielArbre (Referentiel ref)
	{
		super (ref) ;
		// S'enregistrer comme observateur du mod�le
		ref.addObserver (this) ;
		// D�finir le rendu des �l�ments de l'arbre
		this.setCellRenderer (new RenduArbre()) ;
		// Ecouter les �v�n�ments souris
		this.addMouseListener (this) ;
		// indique que l'arbre est un g�n�rateur d'event drag source
		// on peut dragger les �l�ments de l'arbre
		this.mDragSource = DragSource.getDefaultDragSource();
		this.mDragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, this);
	}


	/**
	 * Construit une vue sur aucun r�f�rentiel.
	 */
	public VueReferentielArbre ()
	{
		super(new DefaultTreeModel(null));
	}


	/** Lance la mise-�-jour de l'arbre graphique. Cette m�thode est appel�e par le mod�le
	 * observ� par la vue quand il est modifi�.
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg)
	{
		// Lancer la mise � jour de l'image
		this.updateUI() ;
	}


	/**
	 * Classe permettant d'afficher les icones associ�es aux diff�rents types de donn�es
	 * (paquetages, composants, d�finitions de processes, ...).
	 */
	protected class RenduArbre extends DefaultTreeCellRenderer
	{

		public Component getTreeCellRendererComponent(
														JTree tree,
														Object value,
														boolean sel,
														boolean expanded,
														boolean leaf,
														int row,
														boolean hasFocus)
		{
			// le renderer par d�faut est un Label, on peut dont lui associer une icone
			super.getTreeCellRendererComponent( tree, value, sel, expanded, leaf, row, hasFocus);
			// associer l'icone selon le type de donn�es � afficher
			Icon icone = VueReferentielArbre.this.getIconeAssociee((ElementReferentiel)value) ;
			if (icone != null)
				this.setIcon (icone) ;
			// aucun commentaire � afficher
			this.setToolTipText(null);
			// retourner le JLabel avec l'icone et le texte associ�
			return this;
		}
	}


	/**
	 * Renvoie l'icone associ�e � l'objet value pass� en param�tre
	 * @param eltRef l'�l�ment du r�f�rentiel � afficher
	 * @return Icone associ�e � cet objet
	 */
	protected Icon getIconeAssociee(ElementReferentiel eltRef)
	{
		int typeElt = eltRef.getType() ;
		if (typeElt == ElementReferentiel.DP)
		{
			return (IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + VueDPArbre.iconeDefProc));
		}
		else if (typeElt == ElementReferentiel.COMPOSANT || typeElt == ElementReferentiel.COMPOSANT_VIDE)
		{
			return (IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + VueDPArbre.iconeComposant));
		}
		else if (typeElt == ElementReferentiel.PAQ_COMP || typeElt == ElementReferentiel.PAQ_DP)
		{
			return null ;
		}
		else if (typeElt == ElementReferentiel.REFERENTIEL)
		{
			return (IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + VueReferentielArbre.iconeReferentiel));
		}
		return null;
	}

	/**
	 * Renvoie le popup � afficher selon le noeud cliqu� dans l'arbre value
	 * @param eltRef l'�l�ment du r�f�rentiel pour lequel on veut un menu
	 * @return le popup associ� au type de l'�l�ment s�lectionn� dans l'arbre, null
	 * si aucun menu ne lui est associ�
	 */
	public JPopupMenu getMenuAssocie (ElementReferentiel eltRef)
	{
		int typeElt = eltRef.getType() ;
		// D�finition de processus
		if (typeElt == ElementReferentiel.DP)
		{
			return new PopupDefProc(eltRef.getIdElement()) ;
		}
		// Composant de processus (plein ou vide)
		else if (typeElt == ElementReferentiel.COMPOSANT)
		{
			return new PopupComposant (eltRef.getIdElement()) ;

		}
		// Paquetages de pr�sentation
		// (composants sans mod�le)
		else if (typeElt == ElementReferentiel.PRESENTATION)
		{
			return new PopupPresentation (eltRef.getIdElement()) ;
		}
		// Paquetage du r�f�rentiel contenant les composants
		else if (typeElt == ElementReferentiel.PAQ_COMP)
		{
			return new PopupPaqComposants() ;
		}
		// Paquetage du r�f�rentiel contenant les d�finitions de processus
		else if (typeElt == ElementReferentiel.PAQ_DP)
		{
			return new PopupPaqDP() ;
		}
		// Paquetage du r�f�rentiel contenant les paquetages de pr�sentation
		// (composants sans mod�le)
		else if (typeElt == ElementReferentiel.PAQ_PRESENTATION)
		{
			return new PopupPaqPresentations() ;
		}
		// Paquetage repr�sentant le r�f�rentiel
		else if (typeElt == ElementReferentiel.REFERENTIEL)
		{
			return new PopupReferentiel() ;
		}
		return null;
	}


	/**
	 * L'utilisateur a cliqu� sur l'arbre
	 */
	public void mousePressed(MouseEvent e)
	{
		// on r�cup�re la ligne sur laquelle on a cliqu�
		int selRow = getRowForLocation(e.getX(), e.getY());
		// r�cup�rer le chemin de l'arbre associ�, le path
		TreePath selPath = getPathForLocation(e.getX(), e.getY());

		// the modifiers test is needed in order to make it work on OSes that don't correctly set the isPopupTrigger flag (swing sux0r)
		if(selRow != -1 && (e.isPopupTrigger() || (e.getModifiers() & MouseEvent.BUTTON3_MASK)!=0) )
		{
			// si on clique droit, on affiche le popupmenu associ� � l'�l�ment s�lectionn�
			JPopupMenu popup = this.getMenuAssocie ((ElementReferentiel)selPath.getLastPathComponent());
			// si aucun menu existe, ne rien faire
			if(popup != null)
			{
				e.consume();
				setSelectionPath(selPath);
				// afficher le menu contextuel
				popup.show(this, e.getX(), e.getY());
			}
		}
		// on a cliqu� sur l'arbre mais sur aucun �l�ment, cliqu� deux fois
		else if(selRow != -1 && e.getClickCount()==2)
		{
			// ne rien faire
			e.consume();
		}
	}

	public void mouseClicked (MouseEvent e) {}
	public void mouseReleased (MouseEvent e) {}
	public void mouseEntered (MouseEvent e) {}
	public void mouseExited (MouseEvent e) {}


	//--------------------------------------------------------------//
	// Gestion du drag & drop de l'arbre vers le diagramme			//
	//--------------------------------------------------------------//

	/**
	 * D�tection d'un d�but de glisser.
	 */
	public void dragGestureRecognized(DragGestureEvent dge)
	{
		if(dge.getTriggerEvent().isConsumed()) return;

		ElementReferentiel elt = (ElementReferentiel) getLastSelectedPathComponent() ;

		if (elt != null)
		{
			// on ne commence le drag que lorsque l'on s�lectionne un composant
			if(elt.getType() == ElementReferentiel.COMPOSANT || elt.getType() == ElementReferentiel.COMPOSANT_VIDE)
			{
				dge.getTriggerEvent().consume();
				Referentiel ref = Application.getApplication().getReferentiel() ;
				ComposantProcessus comp = (ComposantProcessus)ref.chercherReference (elt.getIdElement()) ;
				// Si le composant n'est pas en m�moire passer son id dans le r�f�rentiel
				if (comp == null)
				{
					Long idComp = new Long (elt.getIdElement()) ;
					mDragSource.startDrag(dge, DragSource.DefaultMoveDrop, new IDTransferable(idComp), this) ;
				}
				// Sinon passer son idObjetModele (g�r� normalement par le diagramme)
				else
				{
					IdObjetModele idMod = comp.getIdComposant() ;
					mDragSource.startDrag(dge, DragSource.DefaultMoveDrop, new IDTransferable(idMod), this);
				}
			}
		}
	}

	public void dragEnter(DragSourceDragEvent dsde) { cancelEditing(); }
	public void dragOver(DragSourceDragEvent dsde) { cancelEditing(); }
	public void dropActionChanged(DragSourceDragEvent dsde) { cancelEditing(); }
	public void dragExit(DragSourceEvent dse) { cancelEditing(); }
	public void dragDropEnd(DragSourceDropEvent dsde) { cancelEditing(); }

}
