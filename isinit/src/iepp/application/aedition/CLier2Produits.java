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

import java.util.Map;
import java.util.Vector;

import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;

import iepp.Application;
import iepp.application.CommandeAnnulable;
import iepp.application.averification.VGestVerification;
import iepp.domaine.ComposantProcessus;
import iepp.domaine.IdObjetModele;
import iepp.domaine.LienProduits;
import iepp.ui.iedition.VueDPGraphe;
import iepp.ui.iedition.dessin.rendu.FElement;
import iepp.ui.iedition.dessin.rendu.FNote;
import iepp.ui.iedition.dessin.rendu.FProduit;
import iepp.ui.iedition.dessin.rendu.FProduitFusion;
import iepp.ui.iedition.dessin.rendu.IeppCell;
import iepp.ui.iedition.dessin.rendu.LienEdge;
import iepp.ui.iedition.dessin.rendu.ProduitCell;
import iepp.ui.iedition.dessin.rendu.ProduitCellEntree;
import iepp.ui.iedition.dessin.rendu.ProduitCellFusion;
import iepp.ui.iedition.dessin.rendu.ProduitCellSortie;
import iepp.ui.iedition.dessin.rendu.liens.FLien;
import iepp.ui.iedition.dessin.rendu.liens.FLienFusion;
import iepp.ui.iedition.dessin.vues.MDElement;
import iepp.ui.iedition.dessin.vues.MDLien;
import iepp.ui.iedition.dessin.vues.MDLienClassic;
import iepp.ui.iedition.dessin.vues.MDProduit;


/**
 * Commande annulable permettant de lier 2 �l�ments (produit)
 * Selon certaines r�gles suivantes:
 */
public class CLier2Produits extends CommandeAnnulable
{

	/**
	 * Diagramme sur lequel on effectue la liaison
	 */
	private VueDPGraphe diagramme;

	/**
	 * Liens entre le produit fusion cr�� et les composants concern�s
	 */
	private FLienFusion lien1, lien2;

	/**
	 * Liens cr��s au niveau du mod�le entre les produits
	 */
	private LienProduits lienModele1 ;
	private LienProduits lienModele2 ;

	/**
	 * Id des �l�ments s�lectionn�s pour effectuer la liaison
	 */
	private IdObjetModele src, dest;

	/**
	 * Figure du produit fusion cr�� si on fusionne 2 produits simples
	 */
	private FProduitFusion fusion;

	/**
	 * Indique si la fusion est possible ou pas
	 */
	private boolean executable ;


	/**
	* Constructeur de la commande � partir du diagramme sur lequel on va effectuer la liaison
	* les deux �l�ments que l'on veut fusionner et l'ensemble des points d'ancrage utilis�s pour
	* faire la liaison (ne servent qu'� la liaison, ils n'apparaissent pas lorsque le produit fusion est cr��)
	* @param d diagramme sur lequel on effectue la liaison
	* @param source figure sur laquelle on a cliqu� en premier
	* @param destination figure sur laquelle on a cliqu� en second
	* @param pointsAncrageIntermediaires liste des points d'ancrage cr��e lors de la liaison entre les deux figures
	*/
	public CLier2Produits(VueDPGraphe d,  ProduitCell Cellsource, ProduitCell Celldestination, Vector pointsAncrageIntermediaires)
	{
		FElement source = Cellsource.getFprod();
		FElement destination = Celldestination.getFprod();
		
		// garder un lien vers le diagramme
        this.diagramme = d;

		// si l'objet source est un produit en entr�e on permute le sens du lien entr�e
		this.src = source.getModele().getId();
		this.dest = destination.getModele().getId();

		// impossible d'avoir des liens avec les notes
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
					// v�rifier que le nombre d'erreurs = zero
					this.executable = (((new VGestVerification()).connexionPossible(this.src, this.dest)).nbErr() == 0 );
				}

		if (this.executable)
		{
			// Cr�ation du produit de la fusion
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

			// Test si source est d�j� un produit fusion
			if (source instanceof FProduitFusion)
			{
				this.fusion = (FProduitFusion)source;

				// Suppression du lien du produit � fusionner
				this.diagramme.supprimerFigure((FLien)destination.getLiens().elementAt(0));
				// Suppression du produit � fusionner
				this.diagramme.supprimerFigure(destination);
				// Affectation des liens
				if (dest.estProduitEntree())
				{
					//Ajout du produit
					this.fusion.ajouterProduit((FProduit)destination,lien2);
					affectationLien(this.fusion,this.fusion,((FProduit)destination).getComposantInterface());
					// D�sactivation du lien
					this.lien1 = null;
				}
				else
				{
					//Ajout du produit
					this.fusion.ajouterProduit((FProduit)destination,lien1);
					affectationLien(this.fusion,((FProduit)destination).getComposantInterface(),this.fusion);
					// D�sactivation du lien
					this.lien2 = null;
				}

				destination.supprimerLien((FLien)destination.getLiens().elementAt(0));

				if (((FProduit)destination).getComposantInterface().getModele().getId().estComposantVide())
					this.fusion.setNomFusionAll(this.fusion.getNomFusion());
			}
			else
			{
			 	// Test si destination est d�j� un produit fusion
			 	if (destination instanceof FProduitFusion)
			 	{
			 		this.fusion = (FProduitFusion)destination;
			 		// Suppression du lien du produit � fusionner
					this.diagramme.supprimerFigure((FLien)source.getLiens().elementAt(0));
					// Suppression du produit � fusionner
					this.diagramme.supprimerFigure(source);
					// Affectation des liens
					if (src.estProduitEntree())
					{
						//Ajout du produit
						this.fusion.ajouterProduit((FProduit)source,lien2);
						affectationLien(this.fusion,this.fusion,((FProduit)source).getComposantInterface());
						// D�sactivation du lien
					 	this.lien1 = null;
					}
					else
					{
						//Ajout du produit
						this.fusion.ajouterProduit((FProduit)source,lien1);
						affectationLien(this.fusion,((FProduit)source).getComposantInterface(),this.fusion);
						// D�sactivation du lien
						this.lien2 = null;
					}

					source.supprimerLien((FLien)source.getLiens().elementAt(0));

					if (((FProduit)source).getComposantInterface().getModele().getId().estComposantVide())
						this.fusion.setNomFusionAll(this.fusion.getNomFusion());
			 	}
			 	// Sinon cr�ation du produit fusion
			 	else
			 	{
					// Identification du produit en entr�e et en sortie
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

					// Suppression du lien (FLienInterface) entre les produits effac�s et leur composants
					this.diagramme.supprimerFigure((FLien)destination.getLiens().elementAt(0));
					this.diagramme.supprimerFigure((FLien)source.getLiens().elementAt(0));
					// Suppression du lien dans le produit
					source.supprimerLien((FLien)source.getLiens().elementAt(0));
					destination.supprimerLien((FLien)destination.getLiens().elementAt(0));

					// Suppression des deux produits fusionn�s
					this.diagramme.supprimerFigure(source);
					this.diagramme.supprimerFigure(destination);
					// Affectation des liens
					if (src.estProduitEntree())
						affectationLien(this.fusion,((FProduit)destination).getComposantInterface(),((FProduit)source).getComposantInterface());
					else
						affectationLien(this.fusion,((FProduit)source).getComposantInterface(),((FProduit)destination).getComposantInterface());

					// Si le produit en sortie est issue d'un composant vide
					if (((FProduit)sortie).getComposantInterface().getModele().getId().estComposantVide())
					{
						//Si le produit en entree est suivi d'un composant vide
						if (((FProduit)entree).getComposantInterface().getModele().getId().estComposantVide())
							this.fusion.renommer();
						else
							this.fusion.setNomFusionAll(entree.getModele().getId().toString());
					}
					else
					{
						//Si le produit en entree est suivi d'un composant vide
						if (((FProduit)entree).getComposantInterface().getModele().getId().estComposantVide())
							this.fusion.setNomFusionAll(sortie.getModele().getId().toString());
						else
						{
							// cas de deux composants pleins
							if(sortie.getModele().getId().toString().equals(entree.getModele().getId().toString()))
								this.fusion.setNomFusion(sortie.getModele().getId().toString());
							else
								this.fusion.setNomFusion(sortie.getModele().getId().toString()+"("+entree.getModele().getId().toString()+")");
						}
						//System.out.println( this.fusion.getNomFusion() );
					}
					
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

						MDProduit mdp1 = ((ProduitCell) cellSrc).getMprod();
						MDProduit mdp2 = ((ProduitCell) cellDes).getMprod();

						mdfusion.setX((mdp1.getX() + mdp2.getX()) / 2);
						mdfusion.setY((mdp1.getY() + mdp2.getY()) / 2);
						
						ProduitCellFusion newProdCell = new ProduitCellFusion(this.fusion,(ProduitCellEntree)cellEnt,(ProduitCellSortie)cellSor);

						newProdCell.ajoutLien(edge1);
						newProdCell.ajoutLien(edge2);
						
						this.diagramme.supprimerCellule((IeppCell)cellEnt);
						this.diagramme.supprimerCellule((IeppCell)cellSor);
						
						this.diagramme.getElementsCell().add(newProdCell);
						
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
						
						// reprendre l'outil de s�l�ction
						Application.getApplication().getProjet().getFenetreEdition().setOutilSelection();

					} else {
						this.diagramme.repaint();
						// System.out.println("SOURCE & DESTINATION identiques");
					}
			 	} 
			}
		}
	}

	/**
	 * Mathode priv�e affectationLien qui affecte aux liens fusion les entr�es et les sorties
	 * @param centre element central auquel les deux liens seront reli�s
	 * @param source1 source du premier lien
	 * @param destination2 destination du deuxi�me lien
	 */
	private void affectationLien(FElement centre, FElement source1, FElement destination2)
	{
		((MDLien) this.lien1.getModele()).setSource((MDElement)source1.getModele());
		((MDLien) this.lien1.getModele()).setDestination((MDElement)centre.getModele());

		this.lien1.setSource(source1);
		this.lien1.setDestination(centre);
		this.lien1.initHandles();

		((MDLien) this.lien2.getModele()).setSource((MDElement)centre.getModele());
		((MDLien) this.lien2.getModele()).setDestination((MDElement)destination2.getModele());

		this.lien2.setSource(centre);
		this.lien2.setDestination(destination2);
		this.lien2.initHandles();

		if (this.src.estProduitEntree())
		{
			this.lienModele1 = new LienProduits(this.dest, this.src, lien1);
			this.lienModele2 = new LienProduits(this.dest, this.src, lien2);
		}
		else
		{
			this.lienModele1 = new LienProduits(this.src, this.dest, lien1);
			this.lienModele2 = new LienProduits(this.src, this.dest, lien2);
			//this.lienModele = new LienProduits(source1.getModele().getId(), destination2.getModele().getId());
		}

	}

	/**
	* Retourne le nom de l'�dition.
	*/
	public String getNomEdition()
	{
		return "Lier element";
	}


	/**
	 * La commande renvoie si elle s'est bien pass�e ou non
	 * Si la fusion est possible, cr�� les figures du produit fusion s'il n'existe pas
	 * cr�� les liens fusions entre les produits fusion et les composants concern�s
	 * Cr�er les liens au niveau du mod�le, chaque composant connait les liens entre
	 * ses produits et les produits des autres composants
	 * @return true si la liaison s'est bien pass�e false sinon
	 */
	public boolean executer()
	{
		if ( this.executable )
		{
			// Ajout du produit de fusion dans la fenetre d'edition
			if (this.fusion.getNombreProduits() <= 2)
			{
				diagramme.ajouterFigure(this.fusion);
			}
			if (this.lien1 != null)
				diagramme.ajouterFigure(this.lien1);
			if (this.lien2 != null)
				diagramme.ajouterFigure(this.lien2);

			// et au niveau du mod�le
			if (this.lien1 != null)
				((ComposantProcessus)this.lienModele1.getProduitSortie().getRef()).ajouterLien(this.lienModele1);

			if (this.lien2 != null)
				((ComposantProcessus)this.lienModele1.getProduitEntree().getRef()).ajouterLien(this.lienModele2);

			return true;
		}
		else
		{
	  		return false;
		}
	}

}
