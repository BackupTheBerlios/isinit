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

import iepp.application.CommandeNonAnnulable;
import iepp.ui.iedition.VueDPGraphe;
import iepp.ui.iedition.dessin.rendu.IeppCell;
import iepp.ui.iedition.dessin.rendu.ProduitCellEntree;
import iepp.ui.iedition.dessin.rendu.ProduitCellSortie;
import iepp.ui.iedition.dessin.rendu.liens.LienEdge;

import java.util.Map;
import java.util.Vector;

import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.Port;


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
	public CLierInterface(VueDPGraphe d, Vector pointsAncrageIntermediaires, IeppCell ieppsource, IeppCell ieppdestination)
	{
		this.diagramme = d;
						
		AllAttribute = GraphConstants.createMap();
		cellS = ieppsource;
		cellD = ieppdestination;
		portS = ieppsource.getPortComp();
		portD = ieppdestination.getPortComp();
	}
	
	
	/**
	 * La commande renvoie toujours true
	 * Ajoute le nouveau lien cr�� entre le composant et le produit
	 * @return true 
	 */
	public boolean executer()
	{
		LienEdge lienComp = new LienEdge(cellS,cellD);
		  
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
		// On declare les sources et les destinations des liens
		lienComp.setSourceEdge((IeppCell) cellS);
		lienComp.setDestination((IeppCell) cellD);
		
        vecObj.add(lienComp);
        
        diagramme.ajouterLien( lienComp );
        
        ConnectionSet cs = new ConnectionSet(lienComp, portS, portD);
     
        diagramme.getModel().insert(vecObj.toArray(), AllAttribute, null, null, null);
		diagramme.getModel().insert(null, null, cs, null, null);
	        
		diagramme.show();
		
		return true;
	}
}
