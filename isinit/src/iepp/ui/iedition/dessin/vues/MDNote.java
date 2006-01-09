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



import java.awt.Font;

/**
 *
 */
public class MDNote extends MDElement {

    private String message;
    protected Font police;

    /**
     * Instance de MDNote.
     */
    public MDNote() {
        super();
        message="";
        setLargeur(90);
        setHauteur(40);
        police = new Font("Arial", Font.PLAIN, 12);
    }
  
    /**
    Retourne le message de la note
    */
    public String getMessage(){
      return message;
    }

    /**
    Fixe le message de la note
    */
    public void setMessage(String message){
      this.message=message;
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
