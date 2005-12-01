package iepp.ui.iedition.dessin.rendu;

import iepp.ui.iedition.dessin.vues.MDProduit;

import java.awt.Rectangle;
import java.util.Map;

import javax.swing.ImageIcon;

import org.jgraph.graph.GraphConstants;

public class ProduitCell extends IeppCell {
	
	protected double abscisse;
	protected double ordonnee;
	protected double largeur;
	protected double hauteur;
	
	public ProduitCell(MDProduit mprod ) {
		super(mprod.getNom());
		
		this.imageComposant ="produit.png";
		
		// On garde dans l'objet un trace de la position du composant sur le graph
		abscisse=mprod.getX();
		ordonnee=mprod.getY();		
		
		ImageIcon i = new ImageIcon(getCheminImageComposant()+ imageComposant);
		// On garde aussi une trace de la largeur et de la hauteur du composant
		
		largeur=mprod.getLargeur();
		hauteur=mprod.getHauteur();
		
		GraphConstants.setIcon(getAttributs(), i);
		GraphConstants.setBounds(getAttributs(), new Rectangle((int)abscisse,(int)ordonnee,(int)largeur,(int)hauteur));
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
}
