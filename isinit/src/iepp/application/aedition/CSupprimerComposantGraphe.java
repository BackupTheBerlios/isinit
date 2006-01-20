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
import iepp.domaine.ComposantProcessus;
import iepp.domaine.IdObjetModele;
import iepp.domaine.LienProduits;
import iepp.ui.iedition.VueDPGraphe;
import iepp.ui.iedition.dessin.rendu.ComposantCell;
import iepp.ui.iedition.dessin.rendu.FComposantProcessus;
import iepp.ui.iedition.dessin.rendu.FElement;
import iepp.ui.iedition.dessin.rendu.FProduit;
import iepp.ui.iedition.dessin.rendu.IeppCell;
import iepp.ui.iedition.dessin.rendu.LienEdge;
import iepp.ui.iedition.dessin.rendu.ProduitCell;
import iepp.ui.iedition.dessin.rendu.ProduitCellEntree;
import iepp.ui.iedition.dessin.rendu.ProduitCellFusion;
import iepp.ui.iedition.dessin.rendu.ProduitCellSortie;
import iepp.ui.iedition.dessin.vues.MDElement;
import iepp.ui.iedition.dessin.vues.MDProduit;


/**
 * Commande annulable permettant de supprimer un composant se trouvant dans le graphe
 * Supprime toutes les figures liées au composant à supprimer (défait les produits fusion)
 * supprimer les produits simples et le composant lui-même mais uniquement sur le diagramme
 * et pas dans le modèle (arbre)
 */
public class CSupprimerComposantGraphe extends CommandeAnnulable
{
	/**
	 * Id du composant à supprimer du graphe
	 */
	private IdObjetModele composant;
	
	/**
	 * Cellule du composant à supprimer du graphe
	 */
	private ComposantCell composantCell;
	
	
	/**
	 * Diagramme duquel on veut supprimer un composant
	 */
	private VueDPGraphe diagramme;
	
	
	/**
	 * Constructeur de la commande, récupère le composant à supprimer 
	 * et le diagramme courant de l'application
	 * @param compo id du composant à supprimer
	 */
	public CSupprimerComposantGraphe (ComposantCell compo)
	{
		// initialiser le composant à supprimer
		this.composantCell = compo;
		this.composant = compo.getMdcomp().getId() ;
		this.diagramme = Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe();
		
	}
	
	/**
	 * La commande renvoie si elle s'est bien passée ou non
	 * Parcours la liste des produits du composant, vérifie s'il n'y a pas
	 * de produits fusion "à défusionner", supprime les figures des produits et du composant
	 * @return true si l'export s'est bien passé false sinon
	 */
	public boolean executer()
	{
		
		// récupère la liste des liens du composant à supprimer
		Vector listeLiens = ((ComposantProcessus)this.composant.getRef()).getLien();
		
		// récupérer la liste des produits en entrée du composant
		Vector listeEntree = composant.getProduitEntree();
		for (int i = 0; i < listeEntree.size(); i++)
		{
			// récupérer le produit courant
			IdObjetModele produitCourant = (IdObjetModele)listeEntree.elementAt(i);
			// supprimer les éventuels produits fusion et supprimer le produit
		//	this.supprimerFusion(produitCourant, listeLiens);		
		}
		
		// récupérer la liste des produits en sortie du composant
		Vector listeSortie = composant.getProduitSortie();
		for (int i = 0; i < listeSortie.size(); i++)
		{
			// récupérer le produit courant
			IdObjetModele produitCourant = (IdObjetModele)listeSortie.elementAt(i);
			// supprimer les éventuels produits fusion et supprimer le produit
		//	this.supprimerFusion(produitCourant, listeLiens);		
		}

		// suppression du composant
		this.diagramme.supprimerFigure(this.diagramme.contient(this.composant));
		 
		
		// modif Aldo Nit 15/01/06

		// suppression des liens 
		
		
		/*Vector liens=this.composantCell.getListeLien();
		LienEdge[] li=new LienEdge[liens.size()];
		for (int j=0; j<liens.size();j++)
		{
			li[j]=(LienEdge)liens.elementAt(j);
		}
		this.diagramme.getModel().remove((Object[])li);
		
		*/
		
		for (int k=0;k<this.diagramme.getElementsCell().size();k++)
		{
			//System.out.println(this.diagramme.getElementsCell().elementAt(k).toString());
			if (this.diagramme.getElementsCell().elementAt(k) instanceof ProduitCellEntree)
			{
				//System.out.println("produitEntre "+this.diagramme.getElementsCell().elementAt(k).toString());
				ProduitCellEntree prodE=(ProduitCellEntree)this.diagramme.getElementsCell().elementAt(k);
				if (prodE.getCompParent()==this.composantCell)
				{
					this.diagramme.supprimerCellule(prodE);
					k--;
				}
			}
			else if (this.diagramme.getElementsCell().elementAt(k) instanceof ProduitCellSortie)
			{
				//System.out.println("produitSortie "+this.diagramme.getElementsCell().elementAt(k).toString());
				ProduitCellSortie prodS=(ProduitCellSortie)this.diagramme.getElementsCell().elementAt(k);
				if (prodS.getCompParent()==this.composantCell)
				{
					this.diagramme.supprimerCellule(prodS);
					k--;
				}
			}
			else if (this.diagramme.getElementsCell().elementAt(k) instanceof ProduitCellFusion)
			{
				ProduitCellFusion prodF=(ProduitCellFusion)this.diagramme.getElementsCell().elementAt(k);
				
				if (prodF.getProduitCellEntree().getCompParent()==this.composantCell)
				{
					 ProduitCellSortie prodS=prodF.getProduitCellSortie();
					 prodS.setPortComp(new DefaultPort());
					 ((IeppCell) prodS.getCompParent()).setPortComp(new DefaultPort());
					 
					 // On cree un edge pour la connection
					 LienEdge edge = new LienEdge();
					 
					 // on cree la map
					 Map AllAttribute = GraphConstants.createMap();

					 // On ajoute l'edge
					 AllAttribute.put(edge, edge.getEdgeAttribute());
					 AllAttribute.put(prodS, prodS.getAttributs());

					 // On recupere les ports
				     DefaultPort portS = ((IeppCell) prodS.getCompParent()).getPortComp();
					 DefaultPort portD = prodS.getPortComp();
					 
					 prodS.ajoutLien(edge);
					 (prodS.getCompParent()).ajoutLien(edge);
					 
					 ConnectionSet cs = new ConnectionSet(edge, portS, portD);
					 
					 // On l'ajoute au modele
					 Vector vecObj = new Vector();
					 vecObj.add(prodS);
					 vecObj.add(edge);

					 this.diagramme.getModel().insert(vecObj.toArray(), AllAttribute, null, null, null);
					 this.diagramme.getModel().insert(null, null, cs, null, null);

					 
					 this.diagramme.ajouterCell(prodS);
					 this.diagramme.supprimerCellule(prodF);
					
				}
				else if (prodF.getProduitCellSortie().getCompParent()==this.composantCell)
				{
					 ProduitCellEntree prodE=prodF.getProduitCellEntree();
					 prodE.setPortComp(new DefaultPort());
					 (prodE.getCompParent()).setPortComp(new DefaultPort());
					 
					 //On cree un edge pour la connection
					 LienEdge edge = new LienEdge();
					 
					 // on cree la map
					 Map AllAttribute = GraphConstants.createMap();

					 // On ajoute l'edge
					 AllAttribute.put(edge, edge.getEdgeAttribute());
					 AllAttribute.put(prodE, prodE.getAttributs());
					
					 prodE.ajoutLien(edge);
					 (prodE.getCompParent()).ajoutLien(edge);
					 
					 // On recupere les ports
					 DefaultPort portS = (prodE.getCompParent()).getPortComp();
					 DefaultPort portD = prodE.getPortComp();
					 
					 ConnectionSet cs;
					 cs = new ConnectionSet(edge, portD, portS);
					 
					 // On l'ajoute au modele
					 Vector vecObj = new Vector();
					 vecObj.add(prodE);
					 vecObj.add(edge);

					 this.diagramme.getModel().insert(vecObj.toArray(), AllAttribute, null, null, null);
					 this.diagramme.getModel().insert(null, null, cs, null, null);
					 
					this.diagramme.ajouterCell(prodE);
					this.diagramme.supprimerCellule(prodF);
				}
			}
		}
		this.diagramme.supprimerCellule(this.composantCell);
		
		diagramme.repaint();
		return true;
	}
	
	/**
	 * Recherche dans la liste des liens du composant le produitCourant
	 * pour vérifier s'il n'y a pas un produit fusion à supprimer
	 * défusionne le produit fusion s'il existe, supprime le lien interface et le produit
	 * simple recréé lorsqu'on supprimer un lien fusion
	 * @param produitCourant id du produit pour lequel on vérifie s'il fait partie d'un produit fusion
	 * @param listeLiens liens des liens dans le modèle du composant à supprimer
	 */
	public void supprimerFusion(IdObjetModele produitCourant, Vector listeLiens)
	{
		// vérifier que les produits ne soient pas dans des produits fusions
		for (int j = 0; j < listeLiens.size(); j++)
		{
			// récupérer le lien courant
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
