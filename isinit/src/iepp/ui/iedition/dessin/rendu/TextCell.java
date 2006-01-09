package iepp.ui.iedition.dessin.rendu;

import iepp.ui.iedition.dessin.vues.MDNote;

import java.awt.Rectangle;

import org.jgraph.graph.GraphConstants;

public class TextCell extends IeppCell {
	
	protected double abscisse;
	protected double ordonnee;
	protected double largeur;
	protected double hauteur;
	protected MDNote mdcomp;
	
	public TextCell( MDNote mdcomp ) {
		super();
	
		this.mdcomp = mdcomp;
		
		// On garde dans l'objet un trace de la position du composant sur le graph
		abscisse=mdcomp.getX();
		ordonnee=mdcomp.getY();		
		// On garde aussi une trace de la largeur et de la hauteur du composant
		largeur=mdcomp.getLargeur();
		hauteur=mdcomp.getHauteur();
		
		// Mise en place des Attributs de la cellule text pas défaut
		GraphConstants.setBounds(getAttributs(), new Rectangle((int)abscisse,(int)ordonnee,(int)largeur,(int)hauteur));
		GraphConstants.setEditable(getAttributs(), true);
		GraphConstants.setOpaque(getAttributs(), true);
		GraphConstants.setFont(getAttributs(),mdcomp.getPolice());
		GraphConstants.setBackground(getAttributs(),mdcomp.getFillColor());
	}
	
	public double getAbscisse() {
		return abscisse;
	}


	public void setAbscisse(double abscisse) {
		this.abscisse = abscisse;
		GraphConstants.setBounds(getAttributs(), new Rectangle((int)this.abscisse,(int)this.ordonnee,(int)this.largeur,(int)this.hauteur));
	}


	public double getHauteur() {
		return hauteur;
	}


	public void setHauteur(double hauteur) {
		this.hauteur = hauteur;
		GraphConstants.setBounds(getAttributs(), new Rectangle((int)this.abscisse,(int)this.ordonnee,(int)this.largeur,(int)this.hauteur));
	}


	public double getLargeur() {
		return largeur;
	}


	public void setLargeur(double largeur) {
		this.largeur = largeur;
		GraphConstants.setBounds(getAttributs(), new Rectangle((int)this.abscisse,(int)this.ordonnee,(int)this.largeur,(int)this.hauteur));
	}


	public double getOrdonnee() {
		return ordonnee;
	}


	public void setOrdonnee(double ordonnee) {
		this.ordonnee = ordonnee;
		GraphConstants.setBounds(getAttributs(), new Rectangle((int)this.abscisse,(int)this.ordonnee,(int)this.largeur,(int)this.hauteur));
	}

	/**
	 * @return Returns the mdcomp.
	 */
	public MDNote getMdcomp() {
		return mdcomp;
	}

	/**
	 * @param mdcomp The mdcomp to set.
	 */
	public void setMdcomp(MDNote mdcomp) {
		this.mdcomp = mdcomp;
		GraphConstants.setFont(getAttributs(),this.mdcomp.getPolice());
		GraphConstants.setBackground(getAttributs(),this.mdcomp.getFillColor());
	}
}
