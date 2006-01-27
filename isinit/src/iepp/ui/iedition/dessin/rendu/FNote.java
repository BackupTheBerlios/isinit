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
import iepp.ui.iedition.dessin.vues.MDElement;
import iepp.ui.iedition.dessin.vues.MDNote;
import iepp.ui.iedition.popup.PopupFNote;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

/**
 *
 */
public class FNote extends FElement {

    /**
     * Constructeur
     */
    public FNote(MDNote m) {
      super(m);
    }

    /**
     * Procédure d'affichage de la figure.
     * Appelée uniquement depuis la méhode paint() du diagramme.
     * @param g, contexte graphique 2D
     */
    public void paintComponent(Graphics g) {
      MDElement m = (MDElement) getModele();
      drawBody(g, m.getFillColor(), m.getLineColor(), 0, 0, 0, 0);
    }

   /**
     * Affiche l'ombre de la figure.
     * Appelée uniquement pour les déplacements de figures avec la souris.
     * @param g, contexte graphique 2D
     */
    public void drawElementShadow(Graphics g, Color shadowLineColor, int decalageX, int decalageY, int resizeX, int resizeY) {
      drawBody(g, null, shadowLineColor, decalageX, decalageY, resizeX, resizeY);
    }

    
    public void doOnRightClick(VueDPGraphe parent, int x, int y)
    {
    	
    }
    
    /**
    Dessine le corps de la figure.
    */
    protected void drawBody(Graphics g, Color fillColor, Color lineColor, int decalageX, int decalageY, int resizeX, int resizeY) {
      Graphics2D g2d = (Graphics2D) g;

      int x = ((MDElement) getModele()).getX() + decalageX;
      int y = ((MDElement) getModele()).getY() + decalageY;
      int l = ((MDElement) getModele()).getLargeur() + resizeX;
      int h = ((MDElement) getModele()).getHauteur() + resizeY;

      int[] tabXLP={x,x+l-10,x+l,x+l,x};
      int[] tabYLP={y,y,y+10,y+h,y+h};

      if (fillColor != null) {
        g2d.setPaint(fillColor);
        g2d.fillPolygon(tabXLP,tabYLP,5);
      }

      g2d.setPaint(lineColor);
      g2d.drawPolygon(tabXLP,tabYLP,5);

      g2d.draw(new Line2D.Double(x+l-10, y,x+l-10,y+10));
      g2d.draw(new Line2D.Double(x+l-10,y+10,x+l,y+10));
      
      //ajout du texte 
      g2d.setFont(((MDNote) getModele()).getPolice());
      //bidouille pour afficher les retour a la ligne (en attendant de trouver mieux)
      String msg = ((MDNote) getModele()).getMessage();
      Vector tabmsg = new Vector();
      int i, j = 0;
      int borneInf = 0;
      
      for(i = 0; i < msg.length(); i++)
      {
        if(msg.charAt(i) == '\n')
        {
          tabmsg.add(j, msg.substring(borneInf, i));
          j++;
          borneInf = i+1;
        }
      }
      tabmsg.add(j, msg.substring(borneInf, i)); //on y met ce qui reste
      
      int abscisse = x+10;
      int ordonnee = y+10+(((MDNote) getModele()).getPolice()).getSize(); //pour ne pas avoir le texte qui depasse!
      
      for(i = 0; i < j; i++)
        {
          g2d.drawString((String)tabmsg.get(i), abscisse, ordonnee);
          ordonnee += (((MDNote) getModele()).getPolice()).getSize();
        }
      g2d.drawString((String)tabmsg.get(j), abscisse, ordonnee);
    }

	public int getDebutChaine() {return 0;}
	public int getFinChaine() {return 0;}
	
}
