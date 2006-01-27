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

package iepp.ui.iedition.dessin.rendu.liens;

import java.awt.geom.GeneralPath;


public class ArrowHead {

  public final static int CROOKED = 0;
  public final static int FLAT = 1;
  public final static int OPEN = 2;

  /**
   * Create an arrow head
   *
   * @param double
   * @param int type
   * @param GeneralPath
   *
   * @return GeneralPath
   */
  public static GeneralPath createArrowHead(double size, int type, double x, double y) {
    return createArrowHead(size, type, x, y, null);
  }

  public static GeneralPath createArrowHead(double size, int type, double x, double y, GeneralPath path) {

    if(path == null)
      path = new GeneralPath();
    else
      path.reset();

    double l1 = size*1.0;
    double l2 = size*1.0;
    double l3 = size*0.5;

    path.moveTo((float)x, (float)y);
    path.lineTo((float)(x+l2), (float)(y+l3));
    
    if(type != OPEN) {

      if(type == CROOKED)
        path.lineTo((float)(x+l1*0.85), (float)y);
      
      path.lineTo((float)(x+l2), (float)(y-l3));
      
      path.closePath();

    } else {

      path.moveTo((float)x, (float)y);
      path.lineTo((float)(x+l2), (float)(y-l3));

    }

    return path;

  }

}
