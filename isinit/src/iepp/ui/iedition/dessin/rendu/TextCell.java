package iepp.ui.iedition.dessin.rendu;

/* IEPP: Isi Engineering Process Publisher
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

import iepp.ui.iedition.dessin.vues.MDNote;

import java.awt.Rectangle;

import org.jgraph.graph.GraphConstants;

public class TextCell extends IeppCell {
	
	protected double abscisse;
	protected double ordonnee;
	protected double largeur;
	protected double hauteur;
	protected MDNote mdnote;
	protected FNote fnote;
	
	public TextCell( FNote fnote ) {
		
		super();
	
		this.fnote = fnote;
		this.mdnote = (MDNote)fnote.getModele();
		
		// On garde dans l'objet un trace de la position du composant sur le graph
		abscisse=mdnote.getX();
		ordonnee=mdnote.getY();		
		// On garde aussi une trace de la largeur et de la hauteur du composant
		largeur=mdnote.getLargeur();
		hauteur=mdnote.getHauteur();
		
		// Mise en place des Attributs de la cellule text pas défaut
		GraphConstants.setBounds(getAttributs(), new Rectangle((int)abscisse,(int)ordonnee,(int)largeur,(int)hauteur));
		GraphConstants.setEditable(getAttributs(), true);
		GraphConstants.setAutoSize(getAttributs(), true);
		GraphConstants.setOpaque(getAttributs(), true);
		GraphConstants.setFont(getAttributs(),mdnote.getPolice());
		GraphConstants.setBackground(getAttributs(),mdnote.getFillColor());
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
	 * @return Returns the fnote.
	 */
	public FNote getFnote() {
		return fnote;
	}

	/**
	 * @param fnote The fnote to set.
	 */
	public void setFnote(FNote fnote) {
		this.fnote = fnote;
	}

	/**
	 * @return Returns the mdnote.
	 */
	public MDNote getMdnote() {
		return mdnote;
	}

	/**
	 * @param mdnote The mdnote to set.
	 */
	public void setMdnote(MDNote mdnote) {
		this.mdnote = mdnote;
	}
	
	

	
}
