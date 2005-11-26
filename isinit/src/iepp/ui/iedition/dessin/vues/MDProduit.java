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

import iepp.domaine.IdObjetModele;

import java.awt.Font;


/**
 *
 */
public class MDProduit extends MDElement
{

    protected Font police;
    protected IdObjetModele produit ;

    /**
     * Instance de MDProduit
     */
    public MDProduit(IdObjetModele n)
    {
        super();
        /*
        setLargeur(40);
        setHauteur(50);
        */
		setLargeur(30 + (n.getRef().toString(n.getNumRang(),n.getNumType()).length()*6));
		setHauteur(75);
        police = new Font("Arial", Font.PLAIN, 12);
        this.produit = n ;
    }

    public IdObjetModele getId()
    {
      return this.produit ;
    }

    /**
    Retourne le nom de l'objet
    */
    public String getNom()
    {
      return this.produit.getRef().toString(this.produit.getNumRang(), this.produit.getNumType());
    }

   /**
    Retourne la police de la figure.
    */
    public Font getPolice() {
        return police;
    }
    /**
    Fixe la police de la figure.
    */
    public void setPolice(Font f) {
        police = f;
    }
}
