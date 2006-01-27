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
import iepp.ui.iedition.FenetreEdition;
import iepp.ui.iedition.dessin.rendu.FElement;
import iepp.ui.iedition.dessin.rendu.FProduit;
import iepp.ui.iedition.dessin.rendu.liens.FLienInterface;
import iepp.ui.iedition.dessin.vues.MDLienDotted;
import iepp.ui.iedition.dessin.vues.MDProduit;

/**
 * Commande permettant d'ajouter un nouveau produit en sortie � un composant
 * Cr�er un produit vierge avec pour nom nomageN et le rajoute dans
 * ses interfaces fournies
 */
public class CAjouterProduitSortie extends CommandeAnnulable
{
	/**
	 * Composant auquel on ajoute un produit en sortie
	 */
	private ComposantProcessus cp ;


	/**
	 * Constructeur de la commande � partir de l'id du
	 * composant auquel on rajoute un produit en sortie
	 * @param inter id du composant
	 */
	public CAjouterProduitSortie(IdObjetModele inter) 
	{
		this.cp = (ComposantProcessus)inter.getRef();
	}

	/**
	 * La commande doit renvoyer si son ex�cution s'est bien pass�e ou non
	 * Cr�er un nouveau produit et l'ajoute dans les interfaces du composant
	 * courant. Cr�e la figure associ�e au nouveau produit et l'ajoute dans le
	 * diagramme si le composant courant est d�j� pr�sent dans le diagramme
	 * @return true si la commande s'est bien pass�e, false sinon
	 */
	public boolean executer()
	{
		// ajouter un nouveau produit en sortie
		this.cp.ajouterProduitSortie();
		// r�cup�rer la fenetre d'�dition courante
		FenetreEdition fenetre = Application.getApplication().getProjet().getFenetreEdition() ;
		
		// Verifier si le composant est affich� sur le graphe
		FElement fcomp = fenetre.getVueDPGraphe().contient(this.cp.getIdComposant());
		if (fcomp != null)
		{
			//d�selectionner tous les �l�ments
			fenetre.getVueDPGraphe().clearSelection();
			
			// Rajouter le nouveau produit
			// Cr�ation de la vue associ�e au produit
			MDProduit mprod = new MDProduit((IdObjetModele)cp.getProduitSortie().lastElement());
		 	//mprod.setX((int)this.point.getX() - 100 - mprod.getLargeur());
			//mprod.setY((int)this.point.getY() + (i * (mprod.getHauteur() + 30)) );
			FProduit fprod = new FProduit(mprod);
			Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().ajouterFigure(fprod);
			Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().selectionneFigure(fprod);
			// Liaison du produit avec le composant
		 	CLierInterface c = new CLierInterface(Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe(),
												new FLienInterface(new MDLienDotted()),
												fcomp,
												fprod,
												new Vector());
			c.executer();
			// s�lectionner le nouveau produit
			fenetre.getVueDPGraphe().selectionneFigure(fprod);
		  }
		return true;
	}
	
}
