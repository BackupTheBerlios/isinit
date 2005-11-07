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


import java.util.Vector;

import iepp.Application;
import iepp.application.CommandeAnnulable;
import iepp.domaine.ComposantProcessus;
import iepp.domaine.IdObjetModele;
import iepp.domaine.LienProduits;
import iepp.ui.iedition.VueDPGraphe;
import iepp.ui.iedition.dessin.rendu.FProduit;


/**
 * Commande annulable permettant de supprimer un composant se trouvant dans le graphe
 * Supprime toutes les figures li�es au composant � supprimer (d�fait les produits fusion)
 * supprimer les produits simples et le composant lui-m�me mais uniquement sur le diagramme
 * et pas dans le mod�le (arbre)
 */
public class CSupprimerComposantGraphe extends CommandeAnnulable
{
	/**
	 * Id du composant � supprimer du graphe
	 */
	private IdObjetModele composant;
	
	/**
	 * Diagramme duquel on veut supprimer un composant
	 */
	private VueDPGraphe diagramme;
	
	
	/**
	 * Constructeur de la commande, r�cup�re le composant � supprimer 
	 * et le diagramme courant de l'application
	 * @param compo id du composant � supprimer
	 */
	public CSupprimerComposantGraphe (IdObjetModele compo)
	{
		// initialiser le composant � supprimer
		this.composant = compo ;
		this.diagramme = Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe();
		
	}
	
	/**
	 * La commande renvoie si elle s'est bien pass�e ou non
	 * Parcours la liste des produits du composant, v�rifie s'il n'y a pas
	 * de produits fusion "� d�fusionner", supprime les figures des produits et du composant
	 * @return true si l'export s'est bien pass� false sinon
	 */
	public boolean executer()
	{
		// r�cup�re la liste des liens du composant � supprimer
		Vector listeLiens = ((ComposantProcessus)this.composant.getRef()).getLien();
		
		// r�cup�rer la liste des produits en entr�e du composant
		Vector listeEntree = composant.getProduitEntree();
		for (int i = 0; i < listeEntree.size(); i++)
		{
			// r�cup�rer le produit courant
			IdObjetModele produitCourant = (IdObjetModele)listeEntree.elementAt(i);
			// supprimer les �ventuels produits fusion et supprimer le produit
			this.supprimerFusion(produitCourant, listeLiens);		
		}
		
		// r�cup�rer la liste des produits en sortie du composant
		Vector listeSortie = composant.getProduitSortie();
		for (int i = 0; i < listeSortie.size(); i++)
		{
			// r�cup�rer le produit courant
			IdObjetModele produitCourant = (IdObjetModele)listeSortie.elementAt(i);
			// supprimer les �ventuels produits fusion et supprimer le produit
			this.supprimerFusion(produitCourant, listeLiens);		
		}

		// suppression du composant
		this.diagramme.supprimerFigure(this.diagramme.contient(this.composant));
		diagramme.repaint();
		return true;
	}
	
	/**
	 * Recherche dans la liste des liens du composant le produitCourant
	 * pour v�rifier s'il n'y a pas un produit fusion � supprimer
	 * d�fusionne le produit fusion s'il existe, supprime le lien interface et le produit
	 * simple recr�� lorsqu'on supprimer un lien fusion
	 * @param produitCourant id du produit pour lequel on v�rifie s'il fait partie d'un produit fusion
	 * @param listeLiens liens des liens dans le mod�le du composant � supprimer
	 */
	public void supprimerFusion(IdObjetModele produitCourant, Vector listeLiens)
	{
		// v�rifier que les produits ne soient pas dans des produits fusions
		for (int j = 0; j < listeLiens.size(); j++)
		{
			// r�cup�rer le lien courant
			LienProduits lp = (LienProduits)listeLiens.elementAt(j);
			if (lp.getProduitEntree().equals(produitCourant)
					|| lp.getProduitSortie().equals(produitCourant))
			{
				CSupprimerLienFusion c = new CSupprimerLienFusion(this.diagramme, lp.getLienFusion());
				c.executer();
			}
		}
		
		// supprimer le lien
		FProduit fp = (FProduit)this.diagramme.contient(produitCourant);
		this.diagramme.supprimerFigure(fp.getLienInterface());
		// supprimer le produit
		this.diagramme.supprimerFigure(this.diagramme.contient(produitCourant));
	}
}
