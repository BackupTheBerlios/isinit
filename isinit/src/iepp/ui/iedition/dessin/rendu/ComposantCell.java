package iepp.ui.iedition.dessin.rendu;

import iepp.Application;

import java.awt.Rectangle;

import javax.swing.ImageIcon;

import org.jgraph.graph.GraphConstants;

public class ComposantCell extends IeppCell {
	
	protected String imageComposant="composant.png";
	protected double abscisse;
	protected double ordonnee;
	protected double largeur;
	protected double hauteur;
	

	public ComposantCell(String nomComp, double abs, double ord) {
		super(nomComp);
		
		// On garde dans l'objet un trace de la position du composant sur le graph
		abscisse=abs;
		ordonnee=ord;		
		ImageIcon i = new ImageIcon(getCheminImageComposant()+ imageComposant);
		// On garde aussi une trace de la largeur et de la hauteur du composant
		largeur=i.getIconWidth();
		hauteur=i.getIconHeight();
		GraphConstants.setIcon(getAttributs(), i);
		GraphConstants.setBounds(getAttributs(), new Rectangle((int)abs,(int)ord,i.getIconWidth(),i.getIconHeight()+IeppCell.TAILLE_TEXTE));
		GraphConstants.setAutoSize(getAttributs(), true);
		GraphConstants.setEditable(getAttributs(), false);
		GraphConstants.setSizeable (getAttributs(), false);
		//GraphConstants.setBorder(cellAttribute,BorderFactory.createLineBorder(Color.BLACK,2));
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
