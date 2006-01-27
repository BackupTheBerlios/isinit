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

import iepp.ui.iedition.dessin.vues.MDComposantProcessus;
import java.awt.Rectangle;
import javax.swing.ImageIcon;
import org.jgraph.graph.GraphConstants;

public class ComposantCell extends IeppCell {
	
	protected double abscisse;
	protected double ordonnee;
	protected double largeur;
	protected double hauteur;
	protected FComposantProcessus fcomp;
	protected MDComposantProcessus mdcomp;
	
	public ComposantCell( FComposantProcessus fcomp ) { 
		
		super(((MDComposantProcessus)(fcomp.getModele())).getNom());
		
		this.fcomp = fcomp;
		this.mdcomp = (MDComposantProcessus)fcomp.getModele();
		
		this.imageComposant = refImageComposant;
		
		// On garde dans l'objet un trace de la position du composant sur le graph
		abscisse=mdcomp.getX();
		ordonnee=mdcomp.getY();	
		// On garde aussi une trace de la largeur et de la hauteur du composant
		largeur=mdcomp.getLargeur();
		hauteur=mdcomp.getHauteur();
		
		// Définition de l'image du composant
		ImageIcon i = new ImageIcon(getCheminImageComposant()+ imageComposant);
		
		// Définition des attributs du composant
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

	/**
	 * @return Returns the fcomp.
	 */
	public FComposantProcessus getFcomp() {
		return fcomp;
	}

	/**
	 * @param fcomp The fcomp to set.
	 */
	public void setFcomp(FComposantProcessus fcomp) {
		this.fcomp = fcomp;
	}
}
