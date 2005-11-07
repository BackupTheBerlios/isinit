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


import java.util.*;
import java.awt.Color;

import util.Vecteur;

public abstract class MDLien extends MDFigure {

  public final static int STYLE_CLASSIC = 0;
  public final static int STYLE_DOTTED = 1;
  public final static int STYLE_DASHED = 2;

  protected MDElement source;
  protected MDElement destination;
  protected Vector pointsAncrage;
  protected Color couleur;
  protected int style;
  protected MDFleche flecheSource, flecheDestination;

  public MDLien() {
    setCouleur(Color.BLACK);
    setStyle(STYLE_CLASSIC);
    setFlecheDestination(new MDFleche());
    setFlecheSource(null);
    pointsAncrage = new Vector();
  }

  public MDLien(MDElement src, MDElement dest) {
    setCouleur(new Color(153, 0, 51));
    setStyle(STYLE_CLASSIC);

    pointsAncrage = new Vector();
    setSource(src);
    setDestination(dest);
    rafraichirExtremites();
  }

  public void setFlecheSource(MDFleche f) {
    flecheSource = f;
  }
  public MDFleche getFlecheSource() {
    return flecheSource;
  }

  public void setFlecheDestination(MDFleche f) {
    flecheDestination = f;
  }
  public MDFleche getFlecheDestination() {
    return flecheDestination;
  }

  public int getStyle() {
    return style;
  }
  public void setStyle(int s) {
    style = s;
  }

  public void setSource(MDElement src) {
    source = src;
    Vecteur ps = new Vecteur();
    ajouterPointAncrage(ps, 0);
  }
  public MDElement getSource() {
    return source;
  }

  public void setDestination(MDElement dest) {
    destination = dest;
    Vecteur pd = new Vecteur();
    ajouterPointAncrage(pd, pointsAncrage.size());
  }
  public MDElement getDestination() {
    return destination;
  }

  /**
   * Cette fonction rafraichit les coordonnées des deux points d'ancrage aux extrémités du lien.
   * Ces 2 points doivent être à l'intersection du lien du rectangle formé par chacun des éléments liés.
   */
  public void rafraichirExtremites() {
    int x1, y1, x2, y2;
    float a, b;

    // Initialisation des 2 points d'ancrage : milieu des éléments liés.
    Vecteur psrc = (Vecteur) pointsAncrage.firstElement();
    psrc.x = source.getX() + source.getLargeur()/2;
    psrc.y = source.getY() + source.getHauteur()/2;
    Vecteur pdest = (Vecteur) pointsAncrage.lastElement();
    pdest.x = destination.getX() + destination.getLargeur()/2;
    pdest.y = destination.getY() + destination.getHauteur()/2;

    /*
    Calcul de l'équation du premier segment
    */

    x1 = source.getX() + source.getLargeur()/2;
    y1 = source.getY() + source.getHauteur()/2;

    Vecteur v = (Vecteur) pointsAncrage.elementAt(1);
    x2 = v.x;
    y2 = v.y;

    // On a maintenant la fonction y = f(x) = a x + b
    a = (float) (y2 - y1) / (float) (x2 - x1);
    b = (float) y1 - a * (float) x1;

    // Calcul du point source du lien (intersection avec le rectangle formé par l'élément source)
    if (Math.abs(a) >= (float) source.getHauteur() / (float) (source.getLargeur())) {
      if (y1 <= y2)
        psrc.y = y1 + source.getHauteur() / 2;
      else
        psrc.y = y1 - source.getHauteur() / 2;

      if (x1 != x2)
        psrc.x = (int) ((float) (psrc.y - b) / a);
      else
        psrc.x = x1;
    }
    else {
      if (x1 <= x2)
        psrc.x = x1 + source.getLargeur() / 2;
      else
        psrc.x = x1 - source.getLargeur() / 2;

      psrc.y = (int) ((float) psrc.x * a + b);
    }

    /*
    Calcul de l'équation du dernier segment
    */

    x1 = destination.getX() + destination.getLargeur()/2;
    y1 = destination.getY() + destination.getHauteur()/2;

    v = (Vecteur) pointsAncrage.elementAt(pointsAncrage.size() - 2);
    x2 = v.x;
    y2 = v.y;

    a = (float) (y2 - y1) / (float) (x2 - x1);
    b = (float) y1 - a * (float) x1;

    // Calcul du point destination du lien (intersection avec le rectangle formé par l'élément destination)
    if (Math.abs(a) >= (float) destination.getHauteur() / (float) (destination.getLargeur())) {
      if (y1 <= y2)
        pdest.y = y1 + destination.getHauteur() / 2;
      else
        pdest.y = y1 - destination.getHauteur() / 2;

      if (x1 != x2)
        pdest.x = (int) ((float) (pdest.y - b) / a);
      else
        pdest.x = x1;
    }
    else {
      if (x1 <= x2)
        pdest.x = x1 + destination.getLargeur() / 2;
      else
        pdest.x = x1 - destination.getLargeur() / 2;

      pdest.y = (int) ((float) pdest.x * a + b);
    }
  }

    /**
    Retourne la couleur.
    */
    public Color getCouleur() {
        return couleur;
    }
    /**
    Fixe la couleur.
    */
    public void setCouleur(Color c) {
        couleur = c;
    }

  public void ajouterPointAncrage(Vecteur p, int index) {
    pointsAncrage.insertElementAt(p, index);
  }

  public void supprimerPointAncrage(Vecteur p) {
    pointsAncrage.remove(p);
  }

  public Vector getPointsAncrage() {
    return pointsAncrage;
  }
}
