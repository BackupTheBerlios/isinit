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
import iepp.domaine.IdObjetModele;
import iepp.ui.iedition.VueDPGraphe;
import iepp.ui.iedition.dessin.rendu.ComposantCellDyn;
import iepp.ui.iedition.dessin.rendu.IeppCell;
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
 * Crée par Julie TAYAC le 01/02/06: Presque identique à CSupprimerComposantGraphe
 * Commande annulable permettant de supprimer un composant se trouvant dans le graphe
 * Supprime toutes les figures liées au composant à supprimer (défait les produits fusion)
 * supprimer les produits simples et le composant lui-même mais uniquement sur le diagramme
 * et pas dans le modèle (arbre)
 */
public class CSupprimerComposantGrapheDyn extends CommandeAnnulable
{
	
	/**
	 * Cellule du composant à supprimer du graphe
	 */
	private ComposantCellDyn composantCellDyn;
	
	
	/**
	 * Diagramme duquel on veut supprimer un composant
	 */
	private VueDPGraphe diagramme;
	
	
	/**
	 * Constructeur de la commande, récupère le composant à supprimer 
	 * et le diagramme courant de l'application
	 * @param compo id du composant à supprimer
	 */
	public CSupprimerComposantGrapheDyn (ComposantCellDyn compo)
	{
		// initialiser le composant à supprimer
		this.composantCellDyn = compo;
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
			/*
		for (int k=0;k<this.diagramme.getElementsCell().size();k++)
		{
			//System.out.println(this.diagramme.getElementsCell().elementAt(k).toString());
			if (this.diagramme.getElementsCell().elementAt(k) instanceof ProduitCellEntree)
			{
				//System.out.println("produitEntre "+this.diagramme.getElementsCell().elementAt(k).toString());
				ProduitCellEntree prodE=(ProduitCellEntree)this.diagramme.getElementsCell().elementAt(k);
				if (prodE.getCompParent()==this.composantCellDyn)
				{
					this.diagramme.supprimerCellule(prodE);
					k--;
				}
			}
			else if (this.diagramme.getElementsCell().elementAt(k) instanceof ProduitCellSortie)
			{
				//System.out.println("produitSortie "+this.diagramme.getElementsCell().elementAt(k).toString());
				ProduitCellSortie prodS=(ProduitCellSortie)this.diagramme.getElementsCell().elementAt(k);
				if (prodS.getCompParent()==this.composantCellDyn)
				{
					this.diagramme.supprimerCellule(prodS);
					k--;
				}
			}
			else if (this.diagramme.getElementsCell().elementAt(k) instanceof ProduitCellFusion)
			{
				ProduitCellFusion prodF=(ProduitCellFusion)this.diagramme.getElementsCell().elementAt(k);
				
				if (prodF.getProduitCellEntree().getCompParent()==this.composantCellDyn)
				{
					 ProduitCellSortie prodS=prodF.getProduitCellSortie();
					 prodS.setPortComp(new DefaultPort());
					 
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

					 this.diagramme.ajouterLien(edge);
					 this.diagramme.ajouterCell(prodS);
					 this.diagramme.supprimerCellule(prodF);
					 k--;
					
				}
				else if (prodF.getProduitCellSortie().getCompParent()==this.composantCellDyn)
				{
					 ProduitCellEntree prodE=prodF.getProduitCellEntree();
					 prodE.setPortComp(new DefaultPort());
					 
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
					 this.diagramme.ajouterLien(edge);
					 this.diagramme.supprimerCellule(prodF);
					 k--;
				}
			}
		}
		 */
		this.diagramme.supprimerCellule(this.composantCellDyn);
		// fin modif aldo nit 
		
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
//		for (int j = 0; j < listeLiens.size(); j++)
//		{
//			// récupérer le lien courant
//			LienProduits lp = (LienProduits)listeLiens.elementAt(j);
//			if (lp.getProduitEntree().equals(produitCourant)
//					|| lp.getProduitSortie().equals(produitCourant))
//			{
//				CSupprimerLienFusion c = new CSupprimerLienFusion(this.diagramme, lp.getLienFusion());
//				c.executer();
//			}
//		}
//		
//		// supprimer le lien
//		FProduit fp = (FProduit)this.diagramme.contient(produitCourant);
//		this.diagramme.supprimerFigure(fp.getLienInterface());
//		// supprimer le produit
//		this.diagramme.supprimerFigure(this.diagramme.contient(produitCourant));
	}
}
