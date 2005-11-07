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

import iepp.domaine.ComposantProcessus;
import iepp.domaine.IdObjetModele;

import java.awt.Font;


/**
 *
 */
public class MDComposantProcessus extends MDElement {

    protected Font police;
    protected ComposantProcessus compProc;

    /**
     * Instance de MDComposantProcessus
     */
    public MDComposantProcessus(IdObjetModele n) {
        super();
 
        setLargeur(70);
        setHauteur(70);
        police = new Font("Arial", Font.PLAIN, 12);
        compProc = (ComposantProcessus)n.getRef();
    }

    public ComposantProcessus getComposant(){
      return compProc;
    }

    /**
    Retourne le nom de l'objet
    */
    public String getNom(){
      return compProc.getNomComposant();
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
    
    public IdObjetModele getId()
    {
    	return this.compProc.getIdComposant();
    }
}
