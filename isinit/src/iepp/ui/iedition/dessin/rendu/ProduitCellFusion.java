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

import iepp.domaine.IdObjetModele;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import javax.swing.ImageIcon;

import org.jgraph.graph.GraphConstants;


public class ProduitCellFusion extends IeppCell {

	protected double abscisse;
	protected double ordonnee;
	protected double largeur;
	protected double hauteur;
	protected IdObjetModele produit;
	protected ProduitCellEntree produitCellEntree;
	protected ProduitCellSortie produitCellSortie;
	protected ImageIcon i;
		
	public ProduitCellFusion(IdObjetModele prodf, ProduitCellEntree entree, ProduitCellSortie sortie) {
		
		super(prodf.getRef().toString(prodf.getNumRang(), prodf.getNumType()));
		
		this.produit = prodf;
		this.police = new Font("Arial", Font.PLAIN, 12);
			
		this.imageComposant = refImageProduitLier;
		
		// On garde dans l'objet un trace de la position du composant sur le graph
		abscisse=((entree.getAbscisse() + sortie.getAbscisse()) / 2);
		ordonnee=((entree.getOrdonnee() + sortie.getOrdonnee()) / 2);
		
		// D�finition de l'image
		i = new ImageIcon(getCheminImageComposant()+ imageComposant);
		
		// On garde aussi une trace de la largeur et de la hauteur du composant
		Rectangle2D dim = this.police.getStringBounds(this.getNomCompCell(),new FontRenderContext(new AffineTransform(),false,false));
		this.largeur = Math.max(i.getIconWidth(), (int)dim.getWidth()) + 1;
		this.hauteur=i.getIconHeight()+(int)dim.getHeight() + 7;
	
		
		// D�finition des attributs de la cellule
		GraphConstants.setIcon(getAttributs(), i);
		Rectangle r = new Rectangle((int)abscisse,(int)ordonnee,(int)largeur,(int)hauteur);
		GraphConstants.setBounds(getAttributs(), r);
		GraphConstants.setEditable(getAttributs(), false);
		GraphConstants.setAutoSize(getAttributs(),true);
		GraphConstants.setSizeable (getAttributs(), false);
		GraphConstants.setFont(getAttributs(),this.police);
		
		// Garde en m�moirs les produit qu'elle a remplac�e
		this.produitCellEntree = entree;
		this.produitCellSortie = sortie;
		
	}

	/**
	 * @return Returns the produitCellEntree.
	 */
	public ProduitCellEntree getProduitCellEntree() {
		return produitCellEntree;
	}

	/**
	 * @param produitCellEntree The produitCellEntree to set.
	 */
	public void setProduitCellEntree(ProduitCellEntree produitCellEntree) {
		this.produitCellEntree = produitCellEntree;
	}

	/**
	 * @return Returns the produitCellSortie.
	 */
	public ProduitCellSortie getProduitCellSortie() {
		return produitCellSortie;
	}

	/**
	 * @param produitCellSortie The produitCellSortie to set.
	 */
	public void setProduitCellSortie(ProduitCellSortie produitCellSortie) {
		this.produitCellSortie = produitCellSortie;
	}

	
	public IdObjetModele getId()
    {
    	return this.produit;
    }
	
	public int getAbscisse() {
		return (int)(GraphConstants.getBounds(getAttributs()).getX());
	}

	public void setAbscisse(int abscisse) {
		this.abscisse = abscisse;
		GraphConstants.setBounds(getAttributs(), new Rectangle(abscisse,getOrdonnee(),getLargeur(),getHauteur()));
	}

	public int getHauteur() {
		return (int)(GraphConstants.getBounds(getAttributs()).getHeight());
	}

	public void setHauteur(int hauteur) {
		this.hauteur = hauteur;
		GraphConstants.setBounds(getAttributs(), new Rectangle(getAbscisse(),getOrdonnee(),getLargeur(),hauteur));
	}

	public String getImageComposant() {
		return imageComposant;
	}

	public void setImageComposant(String imageComposant) {
		this.imageComposant = imageComposant;
		i = new ImageIcon(getCheminImageComposant()+ imageComposant);
		// On garde aussi une trace de la largeur et de la hauteur du composant
		Graphics2D g2 = (Graphics2D)i.getImage().getGraphics();
		this.largeur = Math.max(i.getIconWidth(), g2.getFontMetrics(this.police).stringWidth(this.getNomCompCell()));//charsWidth( (this.getNomCompCell()).toCharArray(), 0, this.getNomCompCell().length()));
		this.hauteur=i.getIconHeight()+g2.getFontMetrics(this.police).getHeight();
		
		// D�finition des attributs du composant
		GraphConstants.setIcon(getAttributs(), i);
		GraphConstants.setBounds(getAttributs(), new Rectangle((int)abscisse,(int)ordonnee,(int)largeur,(int)hauteur));
	}

	public int getLargeur() {
		return (int)(GraphConstants.getBounds(getAttributs()).getWidth());
	}


	public void setLargeur(int largeur) {
		this.largeur = largeur;
		GraphConstants.setBounds(getAttributs(), new Rectangle(getAbscisse(),getOrdonnee(),largeur,getHauteur()));
	}


	public int getOrdonnee() {
		return (int)(GraphConstants.getBounds(getAttributs()).getY());
	}


	public void setOrdonnee(int ordonnee) {
		this.ordonnee = ordonnee;
		GraphConstants.setBounds(getAttributs(), new Rectangle(getAbscisse(),ordonnee,getLargeur(),getHauteur()));
	}
	
	public void setNomCompCell(String s) {
		
		this.nomComposantCellule=s;
		this.setUserObject(nomComposantCellule);
		
		// On garde aussi une trace de la largeur et de la hauteur du composant
		Rectangle2D dim = this.police.getStringBounds(this.getNomCompCell(),new FontRenderContext(new AffineTransform(),false,false));
		this.largeur = Math.max(i.getIconWidth(), (int)dim.getWidth()) + 1;
		this.hauteur=i.getIconHeight()+(int)dim.getHeight() + 7;
		
		
		
		GraphConstants.setBounds(getAttributs(), new Rectangle(getAbscisse(),getOrdonnee(),getLargeur(),getHauteur()));
	}
	
}
