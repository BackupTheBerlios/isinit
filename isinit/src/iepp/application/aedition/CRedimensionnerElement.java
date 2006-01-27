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
 
package iepp.application.aedition;

import iepp.application.CommandeAnnulable;

/**
 * Commande annulable permettant de redimensionner un élément du diagramme
 */
public class CRedimensionnerElement extends CommandeAnnulable {

	/**
	 * Nouvelles propriétés de l'élément à redimensionner 
	 */
	private int x, y, largeur, hauteur;
	
	/**
	 * Figure à redimensionner
	 */
	/*private FElement element;*/


	/**
	 * Construit une commande permettant de redimensionner un élément du diagramme
	 * à partir de valeurs données
	 * @param fe, figure à redimensionner
	 * @param x, nouvelle absisse de la figure
	 * @param y, nouvelle ordonnée de la figure
	 * @param largeur, nouvelle largeur de la figure
	 * @param hauteur, nouvelle hauteur de la figure
	 */
	public CRedimensionnerElement(/*FElement fe,*/ int x, int y, int largeur, int hauteur)
	{
//		// récupérer toutes les informations sur la figure à modifier
//		this.element = fe;
//		this.x = x;
//		this.y = y;
//		this.largeur = largeur;
//		this.hauteur = hauteur;
	}

	/**
	* Retourne le nom de la commande
	*/
	public String getNomEdition()
	{
		return "Redimensionner";
	}


	/**
	* Rétablit l'élément.
	*/
	public void annuler()
	{
//		MDElement m = (MDElement) element.getModele();
//
//		// Swap des coordonnées/dimensions
//		int ax = m.getX();
//		int ay = m.getY();
//		int al = m.getLargeur();
//		int ah = m.getHauteur();
//		m.setX(x);
//		m.setY(y);
//		m.setLargeur(largeur);
//		m.setHauteur(hauteur);
//		x = ax;
//		y = ay;
//		largeur = al;
//		hauteur = ah;
	}
	
	/**
	 * Redimensionne la figure courante en modifiant ses propriétés (hauteur, largeur, position)
	 * @return true si la commande s'est bien exécutée
	 */
	public boolean executer()
	{
//		MDElement m = (MDElement) element.getModele();
//
//		// Swap des coordonnées/dimensions
//		int ax = m.getX();
//		int ay = m.getY();
//		int al = m.getLargeur();
//		int ah = m.getHauteur();
//		m.setX(x);
//		m.setY(y);
//		m.setLargeur(largeur);
//		m.setHauteur(hauteur);
//		x = ax;
//		y = ay;
//		largeur = al;
//		hauteur = ah;
		return true;
	}
}
