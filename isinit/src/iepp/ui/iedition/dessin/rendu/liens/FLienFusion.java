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

import iepp.ui.iedition.dessin.vues.MDLienClassic;
import iepp.ui.iedition.popup.PopupFLienFusion;
import iepp.ui.iedition.VueDPGraphe;

/**
 * Classe permettant de représenter un lien entre deux produits 
 */
public class FLienFusion extends FLienClassic 
{

  public FLienFusion(MDLienClassic m) 
  {
	super(m);
  }

  public void doOnRightClick(VueDPGraphe parent, int x, int y)
  {
	PopupFLienFusion p = new PopupFLienFusion(parent,this, x, y);
	p.show(parent, x, y);	
  }

}

