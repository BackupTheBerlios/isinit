package iepp.ui.iedition.dessin.rendu;

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

import iepp.Application;

import java.util.Map;
import java.util.Vector;

import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;

public class IeppCell extends DefaultGraphCell {

	public static final String refImageProduit = "produit.png";
	public static final String refImageComposant = "composant.png";
	public static final String refImageProduitLier = "produitLie.png";
	
	protected String cheminImageComposant = Application.getApplication().getConfigPropriete("dossierImagesIepp");
	public String nomComposantCellule;
	protected Map attributs;
	protected DefaultPort portComposant;
	protected String imageComposant;
	protected Vector listeLien;
	
	public IeppCell() {
		super();
		listeLien = new Vector();
		nomComposantCellule = new String();
		portComposant = new DefaultPort();
		attributs=GraphConstants.createMap();
		imageComposant = new String();
		this.add(portComposant);
	}
	
	public IeppCell(String nomComp) {
		super(nomComp);
		listeLien = new Vector();
		nomComposantCellule=nomComp;
		portComposant = new DefaultPort();
		attributs=GraphConstants.createMap();
		imageComposant = new String();
		this.add(portComposant);
	}

	public void setAttribut(Map map) {
		attributs=map;
	}
	
	public Map getAttributs() {
		return(attributs);
	}
	
	public void setCheminImageComposant(String s) {
		cheminImageComposant=s;
	}
		
	public String getCheminImageComposant() {
		return(cheminImageComposant);
	}
	
	public void setNomCompCell(String s) {
		nomComposantCellule=s;
		this.setUserObject(nomComposantCellule);
	}
	
	public String getNomCompCell() {
		return(nomComposantCellule);
	}
	
	public void setPortComp(DefaultPort dp) {
		portComposant=dp;
	}
	
	public DefaultPort getPortComp() {
		return(portComposant);
	}
	
	public void ajoutLien(LienEdge lien){
		listeLien.add(lien);
	}
	
	public Vector getListeLien(){
		return listeLien;
	}
	
	/**
	 * @return Returns the imageComposant.
	 */
	public String getImageComposant() {
		return imageComposant;
	}

	/**
	 * @param imageComposant The imageComposant to set.
	 */
	public void setImageComposant(String imageComposant) {
		this.imageComposant = imageComposant;
	}
	
}
