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
import iepp.domaine.IdObjetModele;
import iepp.ui.iedition.dessin.rendu.liens.LienEdge;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Map;
import java.util.Vector;

import javax.swing.ImageIcon;

import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;

/**
 * @author Hubert
 *
 */
public class DocumentCellDyn extends DefaultGraphCell {

	// icones possibles
	public static final String refImageProduit = "produit.png";
	public static final String refImageComposant = "composant.png";
	public static final String refImageProduitLier = "produitLie.png";

	// icones d'IEPP
	protected String cheminImageDocument = Application.getApplication().getConfigPropriete("dossierImagesIepp");
	
	// attributs de la classe
	protected Map attributs;
	protected Vector listeLien;
	protected String imageDocument;
	protected int abscisse;
	protected int ordonnee;
	protected int largeur;
	protected int hauteur;
	protected ImageIcon i;

// Constructeurs
	public DocumentCellDyn() {
		super();
		listeLien = new Vector();
		attributs=GraphConstants.createMap();
		setImageDocument(DocumentCellDyn.refImageProduitLier);
		i = new ImageIcon(getCheminImageDocument()+ imageDocument);
		
		// On garde aussi une trace de la largeur et de la hauteur du composant
		//Rectangle2D dim = this.police.getStringBounds(this.getNomCompCell(),new FontRenderContext(new AffineTransform(),false,false));
		this.largeur = i.getIconWidth();
		this.hauteur=i.getIconHeight();
		
		GraphConstants.setIcon(getAttributs(), i);
	}

	public DocumentCellDyn(int x, int y) {
		super();
		listeLien = new Vector();
		attributs=GraphConstants.createMap();
		GraphConstants.setSize(getAttributs(),new Dimension(x,y));
	}

	
	

	// Getteurs et Setteurs
	public void setAttributs(Map map) {
		attributs=map;
	}

	public Map getAttributs() {
		return(attributs);
	}

	public void ajouterLien(LienEdge lien){
		listeLien.add(lien);
	}
	
	public void supprimerLien(LienEdge lien){
		listeLien.removeElement(lien);
	}
	
	public Vector getListeLien(){
		return listeLien;
	}
	
	public IdObjetModele getId(){
		return null;
	}
	
	// Méthodes spéciales
	public void setCheminImageDocument(String s) {
		cheminImageDocument=s;
	}
		
	public String getCheminImageDocument() {
		return(cheminImageDocument);
	}
	
	public String getImageDocument() {
		return imageDocument;
	}

	public void setImageDocument(String imageDocument) {
		this.imageDocument = imageDocument;
	}
}
