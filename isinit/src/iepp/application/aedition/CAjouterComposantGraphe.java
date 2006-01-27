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

package iepp.application.aedition;

import iepp.Application;
import iepp.application.CommandeAnnulable;
import iepp.domaine.ComposantProcessus;
import iepp.domaine.IdObjetModele;
import iepp.ui.iedition.FenetreEdition;
import iepp.ui.iedition.dessin.rendu.ComposantCell;
import iepp.ui.iedition.dessin.rendu.ProduitCellEntree;
import iepp.ui.iedition.dessin.rendu.ProduitCellSortie;

import java.awt.Point;
import java.util.Map;
import java.util.Vector;

import org.jgraph.graph.GraphConstants;

import util.ErrorManager;

/**
 * Commande annulable permettant d'ajouter un composant avec son interface
 * au diagramme. V�rifie si le composant n'est pas d�j� dans le diagramme
 */
public class CAjouterComposantGraphe extends CommandeAnnulable
{
	/**
	 * Id du Composant � rajouter dans le graphe
	 */
	private IdObjetModele composant ;

	/**
	 * Endroit o� doit �tre ajout� le composant
	 */
	private Point point = new Point(200, 0);

	/**
	 * Les attributs pour l'affichage des composants
	 */
	private Map AllAttrubiteCell = GraphConstants.createMap();
	
	/**
	 * Cr�ation de la commande � partir du composant � ajouter
	 * @param comp
	 */
	public CAjouterComposantGraphe (IdObjetModele comp)
	{
		// sauvegarder le composant
		this.composant = comp ;
		this.AllAttrubiteCell = GraphConstants.createMap();
	}

	/**
	 * Cr�ation de la commande � partir du composant � ajouter
	 * @param comp
	 */
	public CAjouterComposantGraphe (IdObjetModele comp, Point endroitClick)
	{
		// sauvegarder le composant
		this.composant = comp ;
		this.point = endroitClick ;
		this.AllAttrubiteCell = GraphConstants.createMap();
	}

	/**
	 * Ex�cuter la commande, ajouter au diagramme le composant
	 * et les interfaces en entr�e et sortie
	 * @return true si la commande s'est bien ex�cut�e
	 */
	public boolean executer()
	{

		FenetreEdition fenetre = Application.getApplication().getProjet().getFenetreEdition() ;
		
		// d�selectionner tous les �l�ments
		fenetre.getVueDPGraphe().clearSelection();
		fenetre.getVueDPGraphe().setSelectionCells(null);

		// v�rifier que le composant n'est pas d�j� pr�sent dans le diagramme
		if (fenetre.getVueDPGraphe().contient(this.composant) != null)
		{
			ErrorManager.getInstance().display("ERR","ERR_Composant_Present");
			return false;
		}

		// Construire la vue associ�� au composant
		ComposantCell composantCell = new ComposantCell(this.composant,(int)this.point.getX(),(int)this.point.getY());
		
		fenetre.getVueDPGraphe().ajouterCell(composantCell);
		fenetre.getVueDPGraphe().selectionneCell(composantCell);

		AllAttrubiteCell.put(composantCell,composantCell.getAttributs());
		
		// R�cup�ration des produits en entr�e du composant
		Vector prod_entree = ((ComposantProcessus)this.composant.getRef()).getProduitEntree();

		for (int i=0; i < prod_entree.size(); i++)
		{
			ProduitCellEntree produitCell = new ProduitCellEntree((IdObjetModele)prod_entree.elementAt(i),(int)this.point.getX(),(int)this.point.getY(),composantCell);

			produitCell.setAbscisse(produitCell.getAbscisse() - 100 - (produitCell.getLargeur()/2));
			produitCell.setOrdonnee(produitCell.getOrdonnee() + (i * (produitCell.getHauteur() + 30)));
			
			fenetre.getVueDPGraphe().ajouterCell(produitCell);
			fenetre.getVueDPGraphe().selectionneCell(produitCell);
			
			AllAttrubiteCell.put(produitCell,produitCell.getAttributs());
			
			// Liaison du produit avec le composant
			CLierInterface c = new CLierInterface(fenetre.getVueDPGraphe(),
												  new Vector(),
												  produitCell,
												  composantCell);
			c.executer();
		}


		 //	R�cup�ration des produits en sortie du composant
		 Vector prod_sortie = ((ComposantProcessus)this.composant.getRef()).getProduitSortie();

		 for (int i=0; i < prod_sortie.size(); i++)
		 {
			 ProduitCellSortie produitCell = new ProduitCellSortie((IdObjetModele)prod_sortie.elementAt(i),(int)this.point.getX(),(int)this.point.getY(),composantCell);
			 produitCell.setAbscisse(produitCell.getAbscisse() + composantCell.getLargeur() - (produitCell.getLargeur()/2) + 100);
			 produitCell.setOrdonnee(produitCell.getOrdonnee() + (i * (produitCell.getHauteur() + 30)));
			 
			 fenetre.getVueDPGraphe().ajouterCell(produitCell);
			 fenetre.getVueDPGraphe().selectionneCell(produitCell);
			 
			 AllAttrubiteCell.put(produitCell,produitCell.getAttributs());
			
			 // Liaison du produit avec le composant
			 CLierInterface c = new CLierInterface(fenetre.getVueDPGraphe(),
					 							   new Vector(),
					 							   composantCell,
					 							   produitCell);
			 c.executer();
		 }
		 
		 fenetre.getVueDPGraphe().getModel().insert(new Object[]{composantCell}, AllAttrubiteCell, null, null,null );
		 fenetre.getVueDPGraphe().setSelectionCells(fenetre.getVueDPGraphe().getVectorSelectionCells().toArray());
		 
		 return (true);
	}
       
	public boolean est_vide()
    {
    	return (this.composant.estComposantVide());
    }
	
	
	
}
