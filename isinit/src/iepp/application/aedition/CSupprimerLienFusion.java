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

import iepp.application.CommandeAnnulable;
import iepp.domaine.ComposantProcessus;
import iepp.domaine.IdObjetModele;
import iepp.ui.iedition.VueDPGraphe;

import java.util.Vector;

// pour moi (stéphane) ne sert plus mé a voir

/**
 * Classe permettant la suppression d'un lien fusion 
 */
public class CSupprimerLienFusion extends CommandeAnnulable
{

	/**
	 * Lien à supprimer entre un produit fusion et un composant 
	 */
//	private FLienFusion leLien;
	
	/**
	 * Figure du produit fusion dont on veut enlever un produit
	 */
//	private FProduitFusion produitFusion;
	
	/**
	 * Figure du composant AJOUTER DES COMMENTAIRES 
	 */
//	private FElement leComposant;
//	private FElement leComposant2;
	
	/**
	 * Diagramme duquel on veut supprimer un lien fusion 
	 */
	private VueDPGraphe diagramme;
	
	/**
	 * Indicateur d'une suppression totale du produit fusion 
	 */
	private boolean suppTout;
	/**
	 * Resultat de la suppression globale 
	 */
	private boolean resSupp;
	
	
	/**
	 * Constructeur de la commande
	 * @param leLien lien fusion à supprimer
	 * @param d diagramme duquel on veut supprimer un lien fusion
	 */
	public CSupprimerLienFusion(VueDPGraphe d)//,FLienFusion leLien)
	{
		// garder un lien vers le diagramme
		this.diagramme = d;
		// lien à supprimer
//		this.leLien = leLien;
		
		this.suppTout = false;
		this.resSupp = true;
		
		
/*		//Recherche du produit fusion
		if (leLien.getSource() instanceof FProduitFusion)
		{
			this.produitFusion = (FProduitFusion)leLien.getSource();
			this.leComposant = leLien.getDestination();
		}
		else
		{
			if (leLien.getDestination() instanceof FProduitFusion)
			{
				this.produitFusion = (FProduitFusion)leLien.getDestination();
				this.leComposant = leLien.getSource();
			}
			else
			{
				this.produitFusion = null;
			}
		}
		
		// Tester s'il reste des produits dans le produit fusion, ou s'il ne reste pas que des produits en sortie
		if (this.produitFusion.estLienPrimaire(this.leLien) && this.produitFusion.getNombreProduits() > 2)
		{
		    this.suppTout = true;
		    // Tout effacer
		    for (int i = 0; i<this.produitFusion.getNombreProduits(); i++)
		    {
		        if (this.produitFusion.getLienFusion(i) != this.leLien)
		        {
		            boolean res = new CSupprimerLienFusion(this.diagramme,this.produitFusion.getLienFusion(i)).executer();
		            if (resSupp)
		            {
		                resSupp = res;
		            }
		        }
		    }
		}*/
	}
	
	/**
	 * La commande renvoie si elle s'est bien passée ou non
	 * @return true si la suppression s'est bien passée false sinon
	 */
	public boolean executer() {
		
/*		if (this.produitFusion == null)
			return false;
		else
		{
			//Création du produit
			FProduit fprod = (FProduit)produitFusion.removeProduit(leLien);
			
			//Ajout du produit au diagramme
			this.diagramme.ajouterFigure(fprod);

			// Suppression du lien dans les composants
			((ComposantProcessus)(this.leComposant.getModele().getId().getRef())).supprimerLien(leLien);
			this.effacerLien(fprod.getModele().getId());
			
			//Liaison du produit avec le composant
			
			if (fprod.getModele().getId().estProduitSortie())
			{
				new CLierInterface(diagramme,
								   new FLienInterface(new MDLienDotted()),
								   leComposant,
								   fprod,
								   new Vector()).executer();
			}
			else
			{
				new CLierInterface(diagramme,
								   new FLienInterface(new MDLienDotted()),
								   fprod,
								   leComposant,
								   new Vector()).executer();
			}
			
			diagramme.supprimerFigure((FLien)leLien);
		
		
			if (produitFusion.getNombreProduits() == 1)
			{
				FProduit fprod2 = (FProduit)produitFusion.getLastProduit();
				
				diagramme.supprimerFigure(produitFusion);
				diagramme.supprimerFigure(produitFusion.getLienFusion(0));
								
				diagramme.ajouterFigure(fprod2);
				
				// Suppression du lien dans les composant
				((ComposantProcessus)(this.leComposant.getModele().getId().getRef())).supprimerLien(leLien);
	
				if (fprod2.getModele().getId().estProduitSortie())
				{
					FElement leComposant2 = produitFusion.getLienFusion(0).getSource();
					new CLierInterface(diagramme,
									   new FLienInterface(new MDLienDotted()),
									   leComposant2,
									   fprod2,
									   new Vector()).executer();
				}
				else
				{ 
					FElement leComposant2 = produitFusion.getLienFusion(0).getDestination();
					new CLierInterface(diagramme,
									   new FLienInterface(new MDLienDotted()),
									   fprod2,
									   leComposant2,
									   new Vector()).executer();
				}			
			}
		}
		
		if (suppTout)
		{
		    return resSupp;
		}
		else
		{*/
		    return true;
		/*}*/
	}
	
	/**
	 * Méthode exécutant la suppression du lien
	 */
	public void effacerLien(IdObjetModele id)
	{
//		for (int i = 0; i < produitFusion.getNombreProduits(); i++)
//		{
//			((ComposantProcessus)produitFusion.getProduits(i).getModele().getId().getRef()).supprimerLien(id);
//		}
	}

}
