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

import iepp.ui.iedition.VueDPGraphe;
import iepp.ui.iedition.dessin.rendu.handle.elements.*;
import iepp.ui.iedition.dessin.rendu.liens.FLien;
import iepp.ui.iedition.dessin.vues.MDElement;

import java.awt.*;
import java.util.*;

import util.Vecteur;

/**
 * 
 */
public abstract class FElement extends Figure {

  protected Vector liens;

  public FElement(MDElement m) {
    super(m);
    liens = new Vector();
    
    ajouterHandle(new LeftTopHandleElement(this, false));
    ajouterHandle(new RightTopHandleElement(this, false));
    ajouterHandle(new LeftBottomHandleElement(this, false));
    ajouterHandle(new RightBottomHandleElement(this, false));
    ajouterHandle(new LeftHandleElement(this, false));
    ajouterHandle(new RightHandleElement(this, false));
    ajouterHandle(new TopHandleElement(this, false));
    ajouterHandle(new BottomHandleElement(this, false));
   
  }

  public void doOnRightClick(VueDPGraphe parent, int x, int y) {
   // PopupFigure p = new PopupFigure(this, x, y);
    //p.show(parent, x, y);
  }

  /**
  True si la figure contient le point v.
  */
  public boolean contient(Vecteur v) {
    MDElement m = (MDElement) getModele();

    return ((v.x >= m.getX())
         && (v.y >= m.getY())
         && (v.x <= m.getX() + m.getLargeur())
         && (v.y <= m.getY() + m.getHauteur())
    );
  }


  public boolean appartient(Vecteur A, Vecteur B) {
    MDElement m = (MDElement) getModele();
    // NON(élément non sélectionné) <==> élément sélectionné
    return (!(m.getY() > B.y
           || m.getY() + m.getHauteur() < A.y
           || m.getX() > B.x
           || m.getX() + m.getLargeur() < A.x));
  }

  public void setSelectionne(boolean b){
    super.setSelectionne(b);
  }

  /**
  * Affiche l'ombre de la figure.
  * Appelée uniquement pour les déplacements/resize de figures avec la souris.
  * @param g, contexte graphique 2D
  */
  public abstract void drawElementShadow(Graphics g, Color shadowLineColor, int decalageX, int decalageY, int resizeX, int resizeY);

  /**
  Fonction privée. Dessine le corps de la figure.
  */
  protected abstract void drawBody(Graphics g, Color fillColor, Color lineColor, int decalageX, int decalageY, int resizeX, int resizeY);


  public abstract int getDebutChaine();
  public abstract int getFinChaine();
  

  public void translate(Vecteur v) {
    MDElement m = (MDElement) getModele();
    m.setX(v.x + m.getX());
    m.setY(v.y + m.getY());
  }
  
  

    //-------------------------------------------------------------------------
    //                         Relations avec les liens
    //-------------------------------------------------------------------------

    /**
    Avise l'élément qu'il est concerné par un nouveau lien (en paramètre).
    */
   
    public void ajouterLien(FLien l) {
        liens.addElement(l);
    }

    /**
    Avise l'élément qu'il n'est plus concerné par un le lien en paramètre.
    */
   
    public void supprimerLien(FLien l) {
        liens.removeElement(l);
    }

    /**
    Avise l'élément qu'il n'est plus concerné par aucun lien.
    */
    public void supprimerTousLiens() {
        liens.removeAllElements();
    }

    /**
    Retourne tous les liens qui concernent l'élément.
    */
    public Vector getLiens() {
        return liens;
    }
}