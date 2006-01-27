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

package iepp.ui.iedition.dessin.rendu.handle;



import iepp.ui.iedition.dessin.rendu.Figure;


import util.Vecteur;

import java.awt.*;
import java.io.Serializable;



public abstract class Handle implements Serializable{

  protected int x;
  protected int y;
  protected boolean visible;

  protected Figure figure;

  /**
  * Couleur des poignées normales non fixes.
  */
  final public Color HANDLE_COLOR = Color.black;

  /**
  * Couleur des poignées fixes.
  */
  final public Color HANDLE_FIXED_COLOR = Color.lightGray;

  /**
  * Taille en pixels des poignées.
  */
  final static public int HANDLE_SIZE = 6;

  protected boolean fixe;

  public Handle(Figure f, boolean fixe) {
    figure = f;
    setFixe(fixe);
    setVisible(true);
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
  public void setX(int x) {
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
  public void setY(int y) {
      this.y = y;
  }

  /**
  True si la poignée contient le point v.
  */
  public boolean contient(Vecteur v) {
    return ((v.x >= getX())
         && (v.y >= getY())
         && (v.x <= getX() + HANDLE_SIZE)
         && (v.y <= getY() + HANDLE_SIZE)
    );
  }

  protected abstract void rafraichirCoordonnees();

  public abstract void drawNewFigureShadow(Graphics g, Color lineColor, int mousePosX, int mousePosY);

  public Figure getFigure() {
    return figure;
  }

  public void paintComponent(Graphics g){
    if (getVisible() == false)
      return;

    int s = HANDLE_SIZE;

    rafraichirCoordonnees();

    if (getFixe())
      g.setColor(HANDLE_FIXED_COLOR);
    else
      g.setColor(HANDLE_COLOR);

    g.fillRect(getX(),getY(),s,s);
  }

  public boolean getFixe() {
    return fixe;
  }
  public void setFixe(boolean b) {
    fixe = b;
  }

  public boolean getVisible() {
    return visible;
  }
  public void setVisible(boolean b) {
    visible = b;
  }
}