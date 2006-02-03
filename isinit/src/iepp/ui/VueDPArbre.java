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
 
package iepp.ui;



import iepp.Application;
import iepp.domaine.IdObjetModele;
import iepp.domaine.adaptateur.*;
import iepp.ui.popup.*;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.Component;
import java.awt.dnd.*;
import java.awt.event.*;


import util.IconManager;

/**
 * Classe permettant de construire et d'afficher sous forme d'arbre la d�finition de processus
 * en cours d'�dition dans l'outil.
 * On peut faire des glisser/deposer de composants de processus de l'arbre vers le diagramme d'assemblage.
 */
public class VueDPArbre extends JTree implements DragGestureListener, DragSourceListener
{
	
	/**
	 * Liste des icons � utiliser dans l'arbre pour diff�rencier les �l�ments
	 */
	public static final String iconeProduit = "TreeWorkProduct.gif";
	public static final String iconeDefProc = "blockdevice.png";
	public static final String iconeComposant = "TreeComponent.gif";
	public static final String iconeInterfaceReq = "TreeRequiredInterface.gif";
	public static final String iconeInterfaceFour = "TreeProvidedInterface.gif";
	public static final String iconeScenario = "TreeComponent.gif";
	
	/**
	 * lien vers l'arbre courant
	 */
	private VueDPArbre moiMeme ;
	
	/**
	 * Objet permettant de r�cup�rer les �vents de drag
	 */
	private DragSource mDragSource;
	
	
	/**
	 * Cr�ation d'une vue sous forme d'arbre � partir d'un adapteur qui se chargera d'aller
	 * chercher les donn�es dans le mod�le (d�finition de processus)
	 * @param adaptateur, objet aupr�s duquel on va chercher les donn�es � afficher dans l'arbre
	 */
	public VueDPArbre (AdaptateurDPArbre adaptateur)
	{
			// cr�er la vue qui affichera les donn�es fournies par l'adaptateur
			super(adaptateur);
			// ajouter un renderer � l'arbre
			RenduArbre rendu = new RenduArbre();
			this.setCellRenderer(rendu);
			// ajouter un editeur � l'arbre
			this.setCellEditor(new EditeurArbre(this, rendu));
			// rendre la vue modifiable
			this.setEditable(false);
			// on ne peut s�lectionner qu'un �l�ment de l'arbre � la fois
			this.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
			// nombre de click � effectuer avant qu'un noeud ne se d�ploie/compacte
			this.setToggleClickCount(0);
			this.moiMeme = this;
			this.addMouseListener(new ArbreMouseAdapter());
			
			// indique que l'arbre est un g�n�rateur d'event drag source
			// on peut dragger les �l�ments de l'arbre
			this.mDragSource = DragSource.getDefaultDragSource();
			this.mDragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, this);

	}
	
	/**
	 * Constructeur de la vue vide
	 * Aucune donn�e n'est affich�e, utilis� lors de la cr�ation de l'application
	 */
	public VueDPArbre()
	{
		super(new DefaultTreeModel(null));
	}
	
	
	/**
	 * Classe permettant d'afficher les icones associ�es aux types de donn�es
	 */
	private class RenduArbre extends DefaultTreeCellRenderer 
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
				this.setIcon(VueDPArbre.this.getIconeAssociee(value));
				// aucun commentaire � afficher
				this.setToolTipText(null);
				// retourner le JLabel avec l'icone et le texte associ�
				return this;
			}
	}
	
	/**
	 * Classe permettant d'afficher l'icone m�me si l'�l�ment est en train d'�tre �dit�
	 */
	private class EditeurArbre extends DefaultTreeCellEditor
	{
		public EditeurArbre(JTree tree, DefaultTreeCellRenderer renderer)
		{
			super(tree, renderer);
		}
	
		public Component getTreeCellEditorComponent(JTree tree, Object value, boolean sel, boolean expanded,
															  boolean leaf, int row)
		{
			Component c = super.getTreeCellEditorComponent(tree, value, sel, expanded, leaf, row);
			// indique l'icone � afficher lorsqu'on �dite la valeur de l'�l�ment courant
			this.editingIcon = VueDPArbre.this.getIconeAssociee(value);
			// s�lectionner tout le texte
			if(this.editingComponent instanceof JTextField)
			{
				((JTextField)editingComponent).selectAll();
			}
			return c;
		}

	}
		
		
		/**
		 * Renvoie l'icone associ�e � l'objet value pass� en param�tre
		 * @param value, objet � afficher dans l'arbre
		 * @return Icone associ�e � cet objet
		 */
		public Icon getIconeAssociee(Object value)
		{
			if (((IdObjetModele)value).estDefProc())
			{
				return (IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + VueDPArbre.iconeDefProc));
			}
			else if (((IdObjetModele)value).estComposant())
			{
				return (IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + VueDPArbre.iconeComposant));
			}
			else if (((IdObjetModele)value).estScenario())
			{
				return (IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + VueDPArbre.iconeScenario));
			}
			else if (((IdObjetModele)value).estProduit())
			{
				return (IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + VueDPArbre.iconeProduit));
			} 
			else if (((IdObjetModele)value).estInterfaceRequise())
			{
				return (IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + VueDPArbre.iconeInterfaceReq));
			}
			else if (((IdObjetModele)value).estInterfaceFournie())
			{
				return (IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + VueDPArbre.iconeInterfaceFour));
			} 
			return null;
		}
		
		/**
		 * Renvoie le popup � afficher selon le noeud cliqu� dans l'arbre value
		 * @param value, objet sur lequel on veut afficher le popupmenu
		 * @return le popup associ� au type de l'�l�ment s�lectionn� dans l'arbre
		 */
		public JPopupMenu getMenuAssocie(Object value)
		{
			if (((IdObjetModele)value).estDefProc())
			{
				return (new PopupDPArbre(((IdObjetModele)value)));
			}
			else if (((IdObjetModele)value).estComposant())
			{
				return (new PopupComposantArbre((IdObjetModele)value));
			}
			else if (((IdObjetModele)value).estScenario())
			{
				return (new PopupComposantArbre((IdObjetModele)value));
			}
			else if (((IdObjetModele)value).estInterfaceFournie() 
				|| ((IdObjetModele)value).estInterfaceRequise() )
			{
				return (new PopupInterfaceArbre((IdObjetModele)value));
			}
			else if (((IdObjetModele)value).estProduitEntree() || ((IdObjetModele)value).estProduitSortie())
			{
				return (new PopupProduitArbre((IdObjetModele)value));
			}
			return null;
		}
		
		/**
		 * Classe interne permettant de r�cup�rer les �v�nements li�s � la souris sur l'arbre
		 */
		private class ArbreMouseAdapter extends  MouseAdapter
		{
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
					JPopupMenu popup = getMenuAssocie(selPath.getLastPathComponent());
					// si aucun menu existe, ne rien faire
					if(popup != null)
					{
						e.consume();
						setSelectionPath(selPath);
						// afficher le menu contextuel
						popup.show(moiMeme, e.getX(), e.getY());
					}
				}
				// on a cliqu� sur l'arbre mais sur aucun �l�ment, cliqu� deux fois
				else if(selRow != -1 && e.getClickCount()==2)
				{
					// ne rien faire
					e.consume();
				}
			}
		}


		//--------------------------------------------------------------//
		// Gestion du drag & drop de l'arbre vers l'arbre 				//
		// Vers le diagramme											//
		//--------------------------------------------------------------//	
		
		public void dragGestureRecognized(DragGestureEvent dge)
	    {
			if(dge.getTriggerEvent().isConsumed()) return;
		
			IdObjetModele id = (IdObjetModele)getLastSelectedPathComponent();
		
			// on ne commence le drag que lorsque l'on s�lectionne un composant
			if(id != null && id.estComposant())
			{
				dge.getTriggerEvent().consume();
				mDragSource.startDrag(dge, DragSource.DefaultMoveDrop, new IDTransferable(id), this);
			}
		}
		
		public void dragEnter(DragSourceDragEvent dsde) { cancelEditing(); }
		public void dragOver(DragSourceDragEvent dsde) { cancelEditing(); }
		public void dropActionChanged(DragSourceDragEvent dsde) { cancelEditing(); }
		public void dragExit(DragSourceEvent dse) { cancelEditing(); }
		public void dragDropEnd(DragSourceDropEvent dsde) { cancelEditing(); }
}
