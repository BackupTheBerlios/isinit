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
import iepp.ui.iedition.dessin.rendu.IeppCell;
import iepp.ui.iedition.dessin.rendu.ProduitCellEntree;

import java.util.Vector;

/**
 * Commande permettant d'ajouter un nouveau produit en entrée à un composant vide
 * Créer un produit vierge avec pour nom nomageN et le rajoute dans
 * ses interfaces requises
 */
public class CAjouterProduitEntree extends CommandeAnnulable
{
	/**
	 * Composant auquel on ajoute un produit en entrée
	 */
	private ComposantProcessus cp ;


	/**
	 * Constructeur de la commande à partir de l'id du
	 * composant auquel on rajoute un produit en entrée
	 * @param inter id du composant
	 */
	public CAjouterProduitEntree(IdObjetModele inter) 
	{
		this.cp = (ComposantProcessus)inter.getRef();
	}

	/**
	 * La commande doit renvoyer si son exécution s'est bien passée ou non
	 * Créer un nouveau produit et l'ajoute dans les interfaces du composant
	 * courant. Crée la figure associée au nouveau produit et l'ajoute dans le
	 * diagramme si le composant courant est déjà présent dans le diagramme
	 * @return true si la commande s'est bien passée, false sinon
	 */
	public boolean executer()
	{
		// ajouter un nouveau produit en entrée
		this.cp.ajouterProduitEntree();
		// récupérer la fenetre d'édition courante
		FenetreEdition fenetre = Application.getApplication().getProjet().getFenetreEdition() ;
		
		// Verifier si le composant est affiché sur le graphe
		IeppCell comp = (IeppCell)fenetre.getVueDPGraphe().contient(this.cp.getIdComposant());
		if (comp != null)
		{
			// déselectionner tous les éléments
			fenetre.getVueDPGraphe().clearSelection();
			
			ProduitCellEntree prod = new ProduitCellEntree((IdObjetModele)cp.getProduitEntree().lastElement(),10,10,(ComposantCell)comp);
			
			Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().ajouterCell(prod);
			Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().selectionneCell(prod);
	
			// Liaison du produit avec le composant
			CLierInterface c = new CLierInterface(Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe(),
											   new Vector(),
											   prod,
											   comp);
			c.executer();
			
		 }
		return true;
	}
}
