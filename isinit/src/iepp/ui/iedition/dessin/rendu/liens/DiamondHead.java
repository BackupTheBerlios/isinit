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



public class DiamondHead {


  /**
   * Create an diamond head
   *
   * @param GeneralPath
   *
   * @return GeneralPath
   */
  public static GeneralPath createDiamondHead(double x, double y, double w, double h) {
    return createDiamondHead(x, y, w, h, null);
  }

  public static GeneralPath createDiamondHead(double x, double y, double w, double h, GeneralPath path) {

    if(path == null)
      path = new GeneralPath();
    else
      path.reset();

    path.moveTo((float)x, (float)y);   
    path.lineTo((float)(x+1.5*w), (float)(y-h));
    path.lineTo((float)(x+3.0*w), (float)y);
    path.lineTo((float)(x+1.5*w), (float)(y+h));

    path.closePath();

    return path;

  }

}
