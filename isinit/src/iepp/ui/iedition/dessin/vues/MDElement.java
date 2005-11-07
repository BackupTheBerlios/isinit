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

package iepp.ui.iedition.dessin.vues;

import java.awt.Color;

public class MDElement extends MDFigure {

    protected int x, y, largeur, hauteur, largeurMini, hauteurMini;
    protected Color fillColor, lineColor;

    public MDElement() {
        super();
        setFillColor(new Color(255, 255, 204));
        setLineColor(new Color(153, 0, 51));
        setX(0);
        setY(0);
        setLargeur(50);
        setHauteur(450);
        setLargeurMini(30);
        setHauteurMini(40);
    }

    /**
    Retourne la couleur des traits.
    */
    public Color getLineColor() {
        return lineColor;
    }
    /**
    Fixe la couleur des traits.
    */
    public void setLineColor(Color c) {
        lineColor = c;
    }

    /**
    Retourne la couleur de fond.
    */
    public Color getFillColor() {
        return fillColor;
    }
    /**
    Fixe la couleur de fond.
    */
    public void setFillColor(Color c) {
        fillColor = c;
    }

    /**
    Retourne l'abscisse.
    */
    public int getX() {
        return x;
    }
    /**
    Fixe l'abscisse.
    */
    public void setX(int x)
    {
    	if ( x < 0 )
    	{
    		this.x = 0;
    		return;
    	}
        this.x = x;
    }

    /**
    Retourne l'ordonnée.
    */
    public int getY() {
        return y;
    }
    /**
    Fixe l'ordonnée.
    */
    public void setY(int y)
    {
		if ( y < 0 )
		{
			this.y = 0;
			return;
		}
        this.y = y;
    }

    /**
    Retourne la largeur.
    */
    public int getLargeur() {
        return largeur;
    }
    /**
    Fixe la largeur.
    */
    public void setLargeur(int l) {
        largeur = l;
    }

    /**
    Retourne la hauteur.
    */
    public int getHauteur() {
        return hauteur;
    }
    /**
    Fixe la hauteur.
    */
    public void setHauteur(int h) {
        hauteur = h;
    }

    /**
    Retourne la largeur minimale.
    */
    public int getLargeurMini() {
        return largeurMini;
    }
    /**
    Fixe la largeur minimale.
    */
    public void setLargeurMini(int l) {
        largeurMini = l;
    }


    /**
    Retourne la hauteur minimale.
    */
    public int getHauteurMini() {
        return hauteurMini;
    }
    /**
    Fixe la hauteur minimale.
    */
    public void setHauteurMini(int h) {
        hauteurMini = h;
    }
}