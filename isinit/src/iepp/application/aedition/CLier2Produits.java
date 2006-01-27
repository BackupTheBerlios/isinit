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
import iepp.application.averification.VGestVerification;
import iepp.domaine.ComposantProcessus;
import iepp.domaine.IdObjetModele;
import iepp.domaine.LienProduits;
import iepp.ui.iedition.VueDPGraphe;
import iepp.ui.iedition.dessin.rendu.IeppCell;
import iepp.ui.iedition.dessin.rendu.ProduitCell;
import iepp.ui.iedition.dessin.rendu.ProduitCellEntree;
import iepp.ui.iedition.dessin.rendu.ProduitCellFusion;
import iepp.ui.iedition.dessin.rendu.ProduitCellSortie;
import iepp.ui.iedition.dessin.rendu.liens.LienEdge;

import java.util.Map;
import java.util.Vector;

import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;


/**
 * Commande annulable permettant de lier 2 éléments (produit)
 * Selon certaines règles suivantes:
 */
public class CLier2Produits extends CommandeAnnulable
{

	/**
	 * Diagramme sur lequel on effectue la liaison
	 */
	private VueDPGraphe diagramme;

	
	/**
	 * Liens créés au niveau du modèle entre les produits
	 */
	private LienProduits lienModele1 ;
	private LienProduits lienModele2 ;

	/**
	 * Id des éléments sélectionnés pour effectuer la liaison
	 */
	private IdObjetModele src, dest, fusion;

	/**
	 * Indique si la fusion est possible ou pas
	 */
	private boolean executable ;


	/**
	* Constructeur de la commande à partir du diagramme sur lequel on va effectuer la liaison
	* les deux éléments que l'on veut fusionner et l'ensemble des points d'ancrage utilisés pour
	* faire la liaison (ne servent qu'à la liaison, ils n'apparaissent pas lorsque le produit fusion est créé)
	* @param d diagramme sur lequel on effectue la liaison
	* @param source figure sur laquelle on a cliqué en premier
	* @param destination figure sur laquelle on a cliqué en second
	* @param pointsAncrageIntermediaires liste des points d'ancrage créée lors de la liaison entre les deux figures
	*/
	public CLier2Produits(VueDPGraphe d,  ProduitCell Cellsource, ProduitCell Celldestination, Vector pointsAncrageIntermediaires)
	{
		// garder un lien vers le diagramme
        this.diagramme = d;
        
        

		// si l'objet source est un produit en entrée on permute le sens du lien entrée
		this.src = Cellsource.getId();
		this.dest = Celldestination.getId();

		/*// impossible d'avoir des liens avec les notes
		if (source instanceof FNote || destination instanceof FNote)
		{
			this.executable = false;
		}
		else if (source instanceof FProduitFusion && destination instanceof FProduitFusion)
                {
			this.executable = false;
		}
		else
			if (source instanceof FProduitFusion)
			{
				this.executable = (((new VGestVerification()).connexionPossible((FProduitFusion)source, destination)).nbErr() == 0 );
			}
			else
				if (destination instanceof FProduitFusion)
				{
					this.executable = (((new VGestVerification()).connexionPossible((FProduitFusion)destination, source)).nbErr() == 0 );
 				}
				else
				{
					// vérifier que le nombre d'erreurs = zero
					this.executable = (((new VGestVerification()).connexionPossible(this.src, this.dest)).nbErr() == 0 );
				}

		if (this.executable)
		{
			// Création du produit de la fusion
			// On prend le modele du produit en sortie
			MDProduit mdfusion = null;
			if (source.getModele().getId().estProduitSortie())
			{
				 mdfusion = (MDProduit)source.getModele();
			}
			else
			{
				mdfusion = (MDProduit)destination.getModele();
			}
			
			// Creation des liens fusion
			this.lien1 = new FLienFusion(new MDLienClassic());
			this.lien2 = new FLienFusion(new MDLienClassic());

			// Test si source est déjà un produit fusion
			if (source instanceof FProduitFusion)
			{
				this.fusion = (FProduitFusion)source;

				// Suppression du lien du produit à fusionner
				this.diagramme.supprimerFigure((FLien)destination.getLiens().elementAt(0));
				// Suppression du produit à fusionner
				this.diagramme.supprimerFigure(destination);
				// Affectation des liens
				if (dest.estProduitEntree())
				{
					//Ajout du produit
					this.fusion.ajouterProduit((FProduit)destination,lien2);
					affectationLien(this.fusion,this.fusion,((FProduit)destination).getComposantInterface());
					// Désactivation du lien
					this.lien1 = null;
				}
				else
				{
					//Ajout du produit
					this.fusion.ajouterProduit((FProduit)destination,lien1);
					affectationLien(this.fusion,((FProduit)destination).getComposantInterface(),this.fusion);
					// Désactivation du lien
					this.lien2 = null;
				}

				destination.supprimerLien((FLien)destination.getLiens().elementAt(0));

				if (((FProduit)destination).getComposantInterface().getModele().getId().estComposantVide())
					this.fusion.setNomFusionAll(this.fusion.getNomFusion());
			}
			else
			{
			 	// Test si destination est déjà un produit fusion
			 	if (destination instanceof FProduitFusion)
			 	{
			 		this.fusion = (FProduitFusion)destination;
			 		// Suppression du lien du produit à fusionner
					this.diagramme.supprimerFigure((FLien)source.getLiens().elementAt(0));
					// Suppression du produit à fusionner
					this.diagramme.supprimerFigure(source);
					// Affectation des liens
					if (src.estProduitEntree())
					{
						//Ajout du produit
						this.fusion.ajouterProduit((FProduit)source,lien2);
						affectationLien(this.fusion,this.fusion,((FProduit)source).getComposantInterface());
						// Désactivation du lien
					 	this.lien1 = null;
					}
					else
					{
						//Ajout du produit
						this.fusion.ajouterProduit((FProduit)source,lien1);
						affectationLien(this.fusion,((FProduit)source).getComposantInterface(),this.fusion);
						// Désactivation du lien
						this.lien2 = null;
					}

					source.supprimerLien((FLien)source.getLiens().elementAt(0));

					if (((FProduit)source).getComposantInterface().getModele().getId().estComposantVide())
						this.fusion.setNomFusionAll(this.fusion.getNomFusion());
			 	}
			 	// Sinon création du produit fusion
			 	else
			 	{
					// Identification du produit en entrée et en sortie
					FElement entree;
					FElement sortie;

					if (src.estProduitSortie())
					{
						this.fusion = new FProduitFusion(mdfusion, (FProduit)source, this.lien1, (FProduit)destination, this.lien2);
						entree = destination;
						sortie = source;
					}
					else
					{
						this.fusion = new FProduitFusion(mdfusion, (FProduit)source, this.lien2, (FProduit)destination, this.lien1);
						sortie = destination;
						entree = source;
					}

					//erreur Sur
					// Suppression du lien (FLienInterface) entre les produits effacés et leur composants
					//this.diagramme.supprimerFigure((FLien)destination.getLiens().elementAt(0));
				*/
					
					/////////////////////////////////////////////
					// Ajout pour la prise en compte de JGraph //
					/////////////////////////////////////////////
					
					this.diagramme.clearSelection();
					this.diagramme.setSelectionCells(null);
					
					Object cellSrc = Cellsource;
					Object cellDes = Celldestination;

					Object cellEnt = null;
					Object cellSor = null;

					if (((cellSrc instanceof ProduitCellEntree) && (cellDes instanceof ProduitCellSortie))
							|| (cellSrc instanceof ProduitCellSortie)
							&& (cellDes instanceof ProduitCellEntree)) {
						// verif ke les 2 soit un produit de type differents

						if (cellDes instanceof ProduitCellEntree) {
							cellEnt = cellDes;
							cellSor = cellSrc;
						} else {
							cellEnt = cellSrc;
							cellSor = cellDes;
						}

						// On essaie de relier un produit en entree et en sortie d'un meme composant
						if (((ProduitCellEntree) cellEnt).getCompParent().equals(
								((ProduitCellSortie) cellSor).getCompParent())) {
							this.diagramme.repaint();
							return;
						}

						LienEdge edge1 = new LienEdge();
						LienEdge edge2 = new LienEdge();

						if (src.estProduitSortie())
						{
							this.fusion = src;
						}
						else
						{
							this.fusion = dest;
						}
						
						ProduitCellFusion newProdCell = new ProduitCellFusion(this.fusion,(ProduitCellEntree)cellEnt,(ProduitCellSortie)cellSor);
						newProdCell.ajoutLien(edge1);
						newProdCell.ajoutLien(edge2);
						
						
						this.diagramme.supprimerCellule((IeppCell)cellEnt);
						this.diagramme.supprimerCellule((IeppCell)cellSor);
						
						this.diagramme.ajouterCell(newProdCell);
						this.diagramme.ajouterLien(edge1);
						this.diagramme.ajouterLien(edge2);
						
						if (!((ProduitCell) cellSrc).getNomCompCell()
								.equalsIgnoreCase(
										((ProduitCell) cellDes).getNomCompCell())) {
							newProdCell.setNomCompCell(((ProduitCell) cellSrc)
									.getNomCompCell()
									+ "("
									+ ((ProduitCell) cellDes).getNomCompCell()
									+ ")");
						}

						Map AllAttribute = GraphConstants.createMap();

						AllAttribute.put(edge1, edge1.getEdgeAttribute());
						AllAttribute.put(edge2, edge2.getEdgeAttribute());
						AllAttribute.put(newProdCell, newProdCell.getAttributs());

						DefaultPort portS = ((ProduitCellSortie) cellSor)
								.getCompParent().getPortComp();
						DefaultPort portDInt = ((ProduitCellFusion) newProdCell)
								.getPortComp();
						DefaultPort portD = ((ProduitCellEntree) cellEnt)
								.getCompParent().getPortComp();

						ConnectionSet cs1 = new ConnectionSet(edge1, portS,
								portDInt);
						ConnectionSet cs2 = new ConnectionSet(edge2, portDInt,
								portD);

						Vector vecObj = new Vector();
						vecObj.add(newProdCell);
						vecObj.add(edge1);
						vecObj.add(edge2);

						this.diagramme.getModel().insert(vecObj.toArray(), AllAttribute,
								null, null, null);
						this.diagramme.getModel().insert(null, null, cs1, null, null);
						this.diagramme.getModel().insert(null, null, cs2, null, null);

						this.diagramme.setSelectionCell(newProdCell);
						
						this.diagramme.repaint();
						
						// reprendre l'outil de séléction
						Application.getApplication().getProjet().getFenetreEdition().setOutilSelection();

					} else {
						this.diagramme.repaint();
						// System.out.println("SOURCE & DESTINATION identiques");
					}
			 	
			
		
	}



	/**
	* Retourne le nom de l'édition.
	*/
	public String getNomEdition()
	{
		return "Lier element";
	}


	/**
	 * La commande renvoie si elle s'est bien passée ou non
	 * Si la fusion est possible, créé les figures du produit fusion s'il n'existe pas
	 * créé les liens fusions entre les produits fusion et les composants concernés
	 * Créer les liens au niveau du modèle, chaque composant connait les liens entre
	 * ses produits et les produits des autres composants
	 * @return true si la liaison s'est bien passée false sinon
	 */
	public boolean executer()
	{

			return true;

	}

}
