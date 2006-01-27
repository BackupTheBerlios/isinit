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
import iepp.ui.iedition.dessin.rendu.FElement;
import iepp.ui.iedition.dessin.rendu.liens.FLien;


/**
 * Commande permettant de supprimer un produit en sortie d'un composant vide
 * Supprime le produit au niveau du diagramme et aussi au niveau du modèle
 */
public class CSupprimerProduit extends CommandeAnnulable
{
	
	/**
	 * Composant auquel on enlève un produit en sortie
	 */
	private ComposantProcessus cp ;
	
	/**
	 * Id du produit à supprimer
	 */
	private IdObjetModele produit;
	
	/**
	 * Indicateur d'entree ou sortie
	 */
	private int es;

	/**
	 * Constructeur de la commande, récupère le composant concerné par la suppression
	 * @param produit id du produit à supprimer
	 */
	public CSupprimerProduit(IdObjetModele produit, int es) 
	{
		// récupère le composant de processus concerné
		this.cp = (ComposantProcessus)produit.getRef();
		// garder un lien vers le produit à supprimer
		this.produit = produit;
		// determiner s'il s'agit d'un produit en entree ou en sortie
		this.es = es;
	}

	/**
	 * La commande renvoie si elle s'est bien passée ou non
	 * AJOUTER COMMENTAIRE
	 * @return true si l'export s'est bien passé false sinon
	 */
	public boolean executer()
	{
		
				
		// Supprimer les produits fusion de ce produit
		for (int i = 0; i < cp.getLien().size(); i++)
		{
			if (((LienProduits)cp.getLien().elementAt(i)).contient(produit))
			{
				// Supprimer le lien fusion
				CSupprimerLienFusion cslf = new CSupprimerLienFusion(Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe(),((LienProduits)cp.getLien().elementAt(i)).getLienFusion());
				cslf.executer();
			}
		}
		
		// Supprimer les liens entre le composant et le produit
		FElement compo;
		FElement prod = Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().contient(produit);
		if (prod != null)
		{
			// Il ne doit rester que le lien vers le composant
			FLien lps = (FLien)prod.getLiens().elementAt(0);
			if (lps.getSource() == prod)
			{
				compo = lps.getDestination();
			}
			else
			{
				compo = lps.getSource();
			}
			// Le supprimer
			compo.supprimerLien(lps);
			
			// Supprimer le dessin du lien
			Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().supprimerFigure(lps);
					
			// Supprimer les dessins du graphe de ce produit
			Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().supprimerFigure(prod);
		}
		
		// Recaler tous les IDs qui suivent ce composant dans la liste
		// Dans le vecteur interface se trouvent les veritables produits
		
		IdObjetModele courant;
		Vector interf;
		
		if (this.es == 1)
		{
			interf = cp.getInterfaceIn();
		}
		else
		{
			interf = cp.getInterfaceOut();
		}
		
		for (int i = produit.getNumRang()+1; i < interf.size(); i++)
		{
			// Recuperer l'ID associee
			courant = (IdObjetModele)cp.getMapId().get(interf.elementAt(i));
			// Decrementer le rang de cet ID
			courant.decrementerRang();
		}
		
		// Supprimer du vecteur le produit à enlever
		interf.removeElementAt(produit.getNumRang());
		
		// Rafraichir l'arbre
		Application.getApplication().getFenetrePrincipale().getVueDPArbre().updateUI();
		
		// Rafraichir le graphe
		Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().repaint();
		
		return true;
		
	}
	
}
