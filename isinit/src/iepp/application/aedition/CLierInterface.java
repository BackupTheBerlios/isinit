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

import java.util.HashSet;
import java.util.Map;
import java.util.Vector;

import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.Port;

import util.Vecteur;

import iepp.application.CommandeNonAnnulable;
import iepp.ui.iedition.VueDPGraphe;
import iepp.ui.iedition.dessin.rendu.ComposantCell;
import iepp.ui.iedition.dessin.rendu.FComposantProcessus;
import iepp.ui.iedition.dessin.rendu.FElement;
import iepp.ui.iedition.dessin.rendu.FProduit;
import iepp.ui.iedition.dessin.rendu.IeppCell;
import iepp.ui.iedition.dessin.rendu.LienEdge;
import iepp.ui.iedition.dessin.rendu.ProduitCell;
import iepp.ui.iedition.dessin.rendu.liens.FLien;
import iepp.ui.iedition.dessin.rendu.liens.FLienInterface;
import iepp.ui.iedition.dessin.vues.MDElement;
import iepp.ui.iedition.dessin.vues.MDLien;


/**
 * Commande annulable permettant de lier 2 �l�ments (1 produit et un composant) avec un lien
 * en pointill�. Cette commande n'est pas appel�e directement apr�s un �v�nement utilisateur.
 * Elle n'est appel�e que lorsqu'un composant est ajout� au diagramme
 * ou lorsqu'on supprime un lien fusion, ce lien est remplac� par le lien interface
 * d'origine
 */
public class CLierInterface extends CommandeNonAnnulable
{

	/**
	 * Diagramme sur lequel on effectue la liaison
	 */
	private VueDPGraphe diagramme;
	
	/**
	 * Lien permettant de lier un composant � un produit
	 */
	private FLien lien;
	
	private Port portS;
	 
	private Port portD;
	
	private IeppCell cellS;
	
	private IeppCell cellD;
	
	private Map AllAttribute;
	/**
	* Constructeur de la commande, cr�� un lien entre un composant et un produit simple
	* Cr�er une nouvelle figure pour le lien
	* @param d diagramme sur lequel on effectue la liaison
	* @param l lien � cr�er entre le composant et le produit
	* @param source figure source d'o� pour le lien
	* @param destination figure o� arrive le lien
	* @param pointsAncrageIntermediaires liste des points d'ancrages du lien � cr�er
	*/
	//public CLierInterface(VueDPGraphe d, IeppCell source, IeppCell destination)
	public CLierInterface(VueDPGraphe d, FLien l, FElement source, FElement destination, Vector pointsAncrageIntermediaires, IeppCell ieppsource, IeppCell ieppdestination)
	{
		this.diagramme = d;
		
		this.lien = l;
		((MDLien) lien.getModele()).setSource((MDElement) source.getModele());
		((MDLien) lien.getModele()).setDestination((MDElement) destination.getModele());
		this.lien.setSource(source);
		this.lien.setDestination(destination);
		this.lien.initHandles();

		// ajouter ce lien interface � la figure produit pour pouvoir la supprimer par la suite
		if ( source instanceof FProduit )
		{
			((FProduit)source).setLienIterface((FLienInterface)this.lien);
			((FProduit)source).setComposantInterface((FComposantProcessus)destination);
		}
		else
		{
			((FProduit)destination).setLienIterface((FLienInterface)this.lien);
			((FProduit)destination).setComposantInterface((FComposantProcessus)source);
		}
		
		for (int i = pointsAncrageIntermediaires.size()-1; i >= 0; i--)
		{
		  Vecteur p = (Vecteur) pointsAncrageIntermediaires.elementAt(i);
		  this.lien.creerPointAncrage(p, 1);
		}
		
		AllAttribute = GraphConstants.createMap();
		cellS = ieppsource;
		cellD = ieppdestination;
		portS = ieppsource.getPortComp();
		portD = ieppdestination.getPortComp();
	}

	/**
	* Retourne le nom de l'�dition.
	*/
	public String getNomEdition()
	{
		return "Lier element";
	}

	/**
	 * Renvoie le lien en cours d'initialisation
	 * @return le lien cr��
	 */
	public FLien getLien()
	{
		return this.lien;
	}
	
	
	/**
	 * La commande renvoie toujours true
	 * Ajoute le nouveau lien cr�� entre le composant et le produit
	 * @return true 
	 */
	public boolean executer()
	{
		LienEdge lienComp = new LienEdge();
		  
		AllAttribute.put(lienComp, lienComp.getEdgeAttribute());
	
		Vector vecObj = new Vector();
		
		if (!diagramme.getModel().contains(cellS))
		{
			vecObj.add(cellS);
		}
		if (!diagramme.getModel().contains(cellD))
		{
			vecObj.add(cellD);
		}
		
		((IeppCell)cellS).ajoutLien(lienComp);
		((IeppCell)cellD).ajoutLien(lienComp);
		
        vecObj.add(lienComp);
        
        //diagramme.setLiens(vecObj);
        diagramme.ajouterFigure( this.lien );
        
        ConnectionSet cs = new ConnectionSet(lienComp, portS, portD);
     
        diagramme.getModel().insert(vecObj.toArray(), AllAttribute, null, null, null);
		diagramme.getModel().insert(null, null, cs, null, null);
	        
		diagramme.show();
		
		return true;
	}
}
