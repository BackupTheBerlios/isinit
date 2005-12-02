package iepp.ui.iedition.dessin.rendu;

import iepp.Application;
import iepp.ui.iedition.dessin.vues.MDComposantProcessus;

import java.awt.Rectangle;
import java.util.Map;

import javax.swing.ImageIcon;

import org.jgraph.graph.GraphConstants;

public class ComposantCell extends IeppCell {
	
	
	protected double abscisse;
	protected double ordonnee;
	protected double largeur;
	protected double hauteur;
	protected MDComposantProcessus mdcomp;
	
	public ComposantCell( MDComposantProcessus mdcomp ) {
		super(mdcomp.getNom());
		
		this.mdcomp = mdcomp;
		
		this.imageComposant="composant.png";
		
		// On garde dans l'objet un trace de la position du composant sur le graph
		abscisse=mdcomp.getX();
		ordonnee=mdcomp.getY();		
		ImageIcon i = new ImageIcon(getCheminImageComposant()+ imageComposant);
		// On garde aussi une trace de la largeur et de la hauteur du composant
		largeur=mdcomp.getLargeur();
		hauteur=mdcomp.getHauteur();
		
		GraphConstants.setIcon(getAttributs(), i);
		GraphConstants.setBounds(getAttributs(), new Rectangle((int)abscisse,(int)ordonnee,(int)largeur,(int)hauteur));
		GraphConstants.setAutoSize(getAttributs(), true);
		GraphConstants.setEditable(getAttributs(), false);
		GraphConstants.setSizeable (getAttributs(), false);
		GraphConstants.setFont(getAttributs(),mdcomp.getPolice());
		
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

	/**
	 * @return Returns the mdcomp.
	 */
	public MDComposantProcessus getMdcomp() {
		return mdcomp;
	}

	/**
	 * @param mdcomp The mdcomp to set.
	 */
	public void setMdcomp(MDComposantProcessus mdcomp) {
		this.mdcomp = mdcomp;
	}
}
