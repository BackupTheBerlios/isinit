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
import iepp.ui.iedition.dessin.rendu.ComposantCell;
import iepp.ui.iedition.dessin.rendu.FElement;
import iepp.ui.iedition.dessin.rendu.FProduit;
import iepp.ui.iedition.dessin.rendu.IeppCell;
import iepp.ui.iedition.dessin.rendu.LienEdge;
import iepp.ui.iedition.dessin.rendu.ProduitCell;
import iepp.ui.iedition.dessin.rendu.ProduitCellEntree;
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
	private ComposantCell composant;
	
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
		this.composant = compo ;
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
		// modif Aldo Nit 15/01/06
		//selection des cellules impliquées par le clic
		Object[] cells=this.diagramme.getSelectionCells();
		// suppression des liens 
		Vector liens=this.composant.getListeLien();
		LienEdge[] li=new LienEdge[liens.size()];
		for (int j=0; j<liens.size();j++)
		{
			li[j]=(LienEdge)liens.elementAt(j);
		}
		this.diagramme.getModel().remove((Object[])li);

		// suppression des produits avec leur ports
		
		Vector entree=this.composant.getMdcomp().getComposant().getProduitEntree();
		Object[]prodEntree=new Object[entree.size()];
		int n1=0;
		int n2=0;
		for (int i1=0;i1<entree.size();i1++)
		{
			FElement f=this.diagramme.contient((IdObjetModele)entree.elementAt(i1));
			MDProduit pp=(MDProduit)f.getModele();
			int x1 = pp.getX();
			int y1 = pp.getY();
			System.out.println(this.diagramme.getFirstCellForLocation(x1,y1).getClass().getName());
			ProduitCell pc=(ProduitCell)this.diagramme.getFirstCellForLocation(x1,y1);
			
			System.out.println(pc.getNomCompCell());
			System.out.println(pc.getImageComposant());
			
			if (pc.getImageComposant()=="produitLie.png")
			{
				pc.setImageComposant("produit.png");
				pc.setNomCompCell("huhu1");
			}
			else
			{
				pc.remove(pc.getPortComp());
				prodEntree[n1]=pc;
				n1++;
			}
		}
		this.diagramme.getModel().remove(prodEntree);
		
		Vector sortie=this.composant.getMdcomp().getComposant().getProduitSortie();
		Object[]prodSortie=new Object[sortie.size()];
		
		for (int i2=0;i2<sortie.size();i2++)
		{
			FElement f=this.diagramme.contient((IdObjetModele)sortie.elementAt(i2));
			MDProduit pp=(MDProduit)f.getModele();
			int x2 = pp.getX();
			int y2 = pp.getY();
			ProduitCell pc=(ProduitCell)this.diagramme.getFirstCellForLocation(x2,y2);
			if (pc.getImageComposant()=="produitLie.png")
			{
				pc.setImageComposant("produit.png");
				pc.setNomCompCell("huhu2");
			}
			else
			{
				pc.remove(pc.getPortComp());
				prodSortie[n2]=pc;
				n2++;
			}
			
		}
		this.diagramme.getModel().remove(prodSortie);
		
		// suppression du composant
		this.composant.remove(this.composant.getPortComp());
		
		this.diagramme.getModel().remove(cells);
		/*
		// récupère la liste des liens du composant à supprimer
		Vector listeLiens = ((ComposantProcessus)this.composant.getRef()).getLien();
		
		// récupérer la liste des produits en entrée du composant
		Vector listeEntree = composant.getProduitEntree();
		for (int i = 0; i < listeEntree.size(); i++)
		{
			// récupérer le produit courant
			IdObjetModele produitCourant = (IdObjetModele)listeEntree.elementAt(i);
			// supprimer les éventuels produits fusion et supprimer le produit
			this.supprimerFusion(produitCourant, listeLiens);		
		}
		
		// récupérer la liste des produits en sortie du composant
		Vector listeSortie = composant.getProduitSortie();
		for (int i = 0; i < listeSortie.size(); i++)
		{
			// récupérer le produit courant
			IdObjetModele produitCourant = (IdObjetModele)listeSortie.elementAt(i);
			// supprimer les éventuels produits fusion et supprimer le produit
			this.supprimerFusion(produitCourant, listeLiens);		
		}

		// suppression du composant
		this.diagramme.supprimerFigure(this.diagramme.contient(this.composant));
		*/
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
