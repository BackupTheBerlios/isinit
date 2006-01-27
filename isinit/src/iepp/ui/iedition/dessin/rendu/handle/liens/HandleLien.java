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

package iepp.ui.iedition.dessin.rendu.handle.liens;



import iepp.ui.iedition.dessin.rendu.handle.Handle;
import iepp.ui.iedition.dessin.rendu.liens.FLien;

import java.awt.*;
import java.awt.geom.*;

import util.Vecteur;

public class HandleLien extends Handle {

  protected Vecteur pointAncrage;

  public HandleLien(FLien l, boolean fixe, Vecteur pointAncrage) {
    super(l, fixe);
    this.pointAncrage = pointAncrage;
  }

  public void drawNewFigureShadow(Graphics g, Color lineColor, int mousePosX, int mousePosY) {
    GeneralPath newPath = ((FLien) figure).getNewPath(pointAncrage, mousePosX, mousePosY);

    ((FLien) figure).drawLinkShadow(g, lineColor, newPath);
  }

  protected void rafraichirCoordonnees() {
    setX(pointAncrage.x - HANDLE_SIZE / 2);
    setY(pointAncrage.y - HANDLE_SIZE / 2);
  }

  public Vecteur getPointAncrage() {
    return pointAncrage;
  }
}