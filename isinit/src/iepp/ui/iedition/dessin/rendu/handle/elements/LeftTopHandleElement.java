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

package iepp.ui.iedition.dessin.rendu.handle.elements;

import iepp.ui.iedition.dessin.rendu.FElement;
import iepp.ui.iedition.dessin.vues.MDElement;

import java.awt.*;

public class LeftTopHandleElement extends HandleElement {

  public LeftTopHandleElement(FElement f, boolean fixe) {
    super(f, fixe);
  }

  protected void rafraichirCoordonnees() {
    int s = HANDLE_SIZE;
    MDElement m = (MDElement) figure.getModele();
    int x = m.getX();
    int y = m.getY();

    setX(x-s/2);
    setY(y-s/2);
  }

  public void drawNewFigureShadow(Graphics g, Color lineColor, int mousePosX, int mousePosY) {
    int s = HANDLE_SIZE;
    MDElement m = (MDElement) figure.getModele();
    int x = m.getX();
    int y = m.getY();
    int l = m.getLargeur();
    int h = m.getHauteur();
    int lm = m.getLargeurMini();
    int hm = m.getHauteurMini();

    if (l + (x - mousePosX) < lm)
      newFigureX = x + l - lm;
    else
      newFigureX = mousePosX;
    newFigureLargeur = l + (x - newFigureX);
    if (h + (y - mousePosY) < hm)
      newFigureY = y + h - hm;
    else
      newFigureY = mousePosY;
    newFigureHauteur = h + (y - newFigureY);

    ((FElement) figure).drawElementShadow(g, lineColor, newFigureX - x, newFigureY - y, newFigureLargeur - l, newFigureHauteur - h);
  }
}