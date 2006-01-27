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

import iepp.ui.iedition.dessin.vues.MDProduit;

import java.awt.Rectangle;

import javax.swing.ImageIcon;

import org.jgraph.graph.GraphConstants;

public class ProduitCell extends IeppCell {
	
	protected double abscisse;
	protected double ordonnee;
	protected double largeur;
	protected double hauteur;
	protected MDProduit mprod;
	protected FProduit fprod;
	
	public ProduitCell(FProduit fprod ) {
		
		super(((MDProduit)fprod.getModele()).getNom());
		
		this.fprod = fprod;
		this.mprod = (MDProduit)fprod.getModele();
		
		this.imageComposant = refImageProduit;
		
		// On garde dans l'objet un trace de la position du composant sur le graph
		abscisse=mprod.getX();
		ordonnee=mprod.getY();		
		// On garde aussi une trace de la largeur et de la hauteur du composant
		largeur=mprod.getLargeur();
		hauteur=mprod.getHauteur();
		
		// Définition de l'image
		ImageIcon i = new ImageIcon(getCheminImageComposant()+ imageComposant);
		
		// Définition des attributs de la cellule
		GraphConstants.setIcon(getAttributs(), i);
		Rectangle r = new Rectangle((int)abscisse,(int)ordonnee,(int)largeur,(int)hauteur);
		GraphConstants.setBounds(getAttributs(), r);
		GraphConstants.setAutoSize(getAttributs(), true);
		GraphConstants.setEditable(getAttributs(), false);
		GraphConstants.setSizeable (getAttributs(), false);
		GraphConstants.setFont(getAttributs(),mprod.getPolice());
		
	}

	public double getAbscisse() {
		return abscisse;
	}

	public void setAbscisse(double abscisse) {
		this.abscisse = abscisse;
	}

	public double getHauteur() {
		return hauteur;
	}

	public void setHauteur(double hauteur) {
		this.hauteur = hauteur;
	}

	public String getImageComposant() {
		return imageComposant;
	}

	public void setImageComposant(String imageComposant) {
		this.imageComposant = imageComposant;
		ImageIcon i = new ImageIcon(getCheminImageComposant()+ imageComposant);
		GraphConstants.setIcon(getAttributs(), i);
		
	}

	public double getLargeur() {
		return largeur;
	}


	public void setLargeur(double largeur) {
		this.largeur = largeur;
	}


	public double getOrdonnee() {
		return ordonnee;
	}


	public void setOrdonnee(double ordonnee) {
		this.ordonnee = ordonnee;
	}

	/**
	 * @return Returns the mprod.
	 */
	public MDProduit getMprod() {
		return mprod;
	}

	/**
	 * @param mprod The mprod to set.
	 */
	public void setMprod(MDProduit mprod) {
		this.mprod = mprod;
	}

	/**
	 * @return Returns the fprod.
	 */
	public FProduit getFprod() {
		return fprod;
	}

	/**
	 * @param fprod The fprod to set.
	 */
	public void setFprod(FProduit fprod) {
		this.fprod = fprod;
	}
}
