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

package iepp.ui.iedition.dessin.rendu;



import util.Vecteur;
import iepp.ui.iedition.VueDPGraphe;
import iepp.ui.iedition.dessin.rendu.handle.Handle;
import iepp.ui.iedition.dessin.vues.MDFigure;

import java.awt.*;
import java.util.*;
import java.io.Serializable;

/**
Classe de base pour tous les �l�ments graphiques d�un diagramme.
*/
public abstract class Figure implements Serializable{



    /**
    Mod�le de l'�l�ment.
    */
    protected MDFigure modele;

    /**
    true si l'�l�ment est s�lectionn�.
    */
    protected boolean bSelected;

    /**
    Liste de handle de la figure.
    */
    protected Vector handles;

    /**
    Constructeur
    */
    public Figure(MDFigure m) {
        modele = m;
        bSelected = false;
        handles = new Vector();
    }

    public abstract void doOnRightClick(VueDPGraphe parent, int x, int y);

    /**
    * Retourne la fen�tre de propri�t�s associ�e au mod�le.
    */
   // public abstract FenetrePropriete getFenetreProprietes();

    /**
    True si la figure contient le point v.
    */
    public abstract boolean contient(Vecteur v);

    /**
    True si la figure appartient au rectangle d�fini par les deux points A (en haut � gauche)
    et B (en bas � droite).
    */
    public abstract boolean appartient(Vecteur A, Vecteur B);

    /**
    * Proc�dure d'affichage de la figure.
    * Appel�e uniquement depuis la m�hode paintComponent() du diagramme.
    * @param g, contexte graphique 2D
    */
    public abstract void paintComponent(Graphics g);


    public void displayHandles(Graphics g)
    {
      for (int i = 0; i < handles.size(); i++)
        ((Handle) handles.elementAt(i)).paintComponent(g);
    }

    public Handle getHandle(int x, int y) {
      int n = handles.size();
      Vecteur v = new Vecteur(x,y);
      Handle h;

      for(int i=0;i<n;i++) {
         h = (Handle) handles.elementAt(i);
         if(h.contient(v))
          return h;
      }

      return null;
    }

    /**
    Retourne true si la figure est s�lectionn�.
    */
    public boolean estSelectionne() {
        return bSelected;
    }
    /**
    Fixe l'�tat s�lectionn� de la figure.
    */
    public void setSelectionne(boolean b) {
        bSelected = b;
    }

    protected void ajouterHandle(Handle h){
      handles.add(h);
    }

    /**
    Translate une figure suivant un vecteur 2D.
    */
    public abstract void translate(Vecteur v);

    //-------------------------------------------------------------------------
    //                         Relations avec le mod�le
    //-------------------------------------------------------------------------

    /**
    Retourne le mod�le repr�sent� par la figure.
    */
    public MDFigure getModele() {
        return modele;
    }
    /**
    Fixe le mod�le repr�sent� par la figure.
    */
    public void setModele(MDFigure m) {
        modele = m;
    }
}
