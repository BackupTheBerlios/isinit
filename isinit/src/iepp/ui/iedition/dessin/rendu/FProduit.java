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


import iepp.ui.iedition.dessin.rendu.liens.FLienInterface;
import iepp.ui.iedition.dessin.vues.MDElement;
import iepp.ui.iedition.dessin.vues.MDProduit;

import java.awt.*;

/**
 *
 */
public class FProduit extends FElement
{
	
	/**
	 * Taille en caractères du libellé du produit
	 */
	private int taille ;
	
	/**
	 * Taille en pixel du nom du produit
	 */
	private int largeurChaine ;
	
	/**
	 * Figure du lien entre le produit courant et un composant
	 */
	private FLienInterface lienInterface = null;
	
	/**
	 * Figure du lien entre le produit courant et un composant
	 */
	private FComposantProcessus composantInterface = null;

    /**
     * Constructeur
     */
    public FProduit(MDProduit m)
    {
      super(m);
    }

	
	
    /**
    Retourne la fenêtre de propriétés associée au modèle.
   
    public FenetreProprietes getFenetreProprietes() {
      return new FPDActivite((MDActivite) getModele());
    }
 */
    
    /**
     * Procédure d'affichage de la figure.
     * Appelée uniquement depuis la méhode paint() du diagramme.
     * @param g, contexte graphique 2D
     */
    public void paintComponent(Graphics g)
    {
      MDElement m = (MDElement) this.getModele();
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

    /**
    Dessine le corps de la figure.
    */
    protected void drawBody(Graphics g, Color fillColor, Color lineColor, int decalageX, int decalageY, int resizeX, int resizeY) {
      Graphics2D g2 = (Graphics2D) g;

	   
	  int x = ((MDElement) getModele()).getX() + decalageX;
	  int y = ((MDElement) getModele()).getY() + decalageY;
	  int l = ((MDElement) getModele()).getLargeur() + resizeX;
	  int h = ((MDElement) getModele()).getHauteur() + resizeY;
			
	   int x1[]=new int[5];
	   int y1[]=new int[5];

				   
	   x1[0]= x ;
	   x1[1]= x + l - ( l / 5 );
	   x1[2]= x + l;
	   x1[3]= x + l ;
	   x1[4]= x ;

	   y1[0]= y ;
	   y1[1]= y ;
	   y1[2]= y + (h / 5) ;
	   y1[3]= y + h ;
	   y1[4]= y + h;
	   
	   
		// code effectué si on ne déplace pas le produit
		if (fillColor != null)
		{ 
			Color c = new Color(223,146,126);
			g2.setPaint(c);
			g2.fillPolygon(x1,y1,5);		
		}
		
		g2.setPaint(Color.BLACK);
		g2.setStroke(new BasicStroke(2));
		g2.drawPolygon(x1,y1,5);
		g2.drawLine(x1[1],y1[1],x1[1],y1[2]);
		g2.drawLine(x1[1],y1[2],x1[2],y1[2]);
		g2.setStroke(new BasicStroke(1));
		
		int i ;
		for( i = y + 5 ; i < y1[2] ; i += 5)
		{
			g2.drawLine(x1[0] + (x1[1] - x1[0])/ 6, i , x1[1] - (x1[1] - x1[0])/ 6 , i );
		}
		for( ; i < y1[4] ; i += 5)
		{
			g2.drawLine(x1[0] + (x1[1] - x1[0])/ 6, i , x1[3] - (x1[1] - x1[0])/ 6 , i );
		}
		
		g2.setStroke(new BasicStroke(1));
						
		g2.setFont(((MDProduit) getModele()).getPolice());
		
		// centrer le texte associé au composant
		this.taille = ((MDProduit) getModele()).getNom().length();
		this.largeurChaine = g2.getFontMetrics(((MDProduit) getModele()).getPolice()).charsWidth(
													  ((MDProduit) getModele()).getNom().toCharArray(),
													  0, this.taille);
		g2.drawString(((MDProduit) getModele()).getNom(), x + (l/2) - (this.largeurChaine/2), y + h + 15);
		
    }
    
    public int getDebutChaine()
    {
    	return (((MDElement) getModele()).getX()
    				+ (((MDElement) getModele()).getLargeur() / 2)
    				-  (this.largeurChaine/2)) ;
    }
    
	public int getFinChaine()
	{
		return (this.getDebutChaine() + this.largeurChaine) ;
	}
	
	public void setLienIterface(FLienInterface f)
	{
		this.lienInterface = f ;
	}
	
	public FLienInterface getLienInterface()
	{
		return this.lienInterface;
	}
	
	public void setComposantInterface(FComposantProcessus f)
	{
		this.composantInterface = f ; 
	}
	
	public FComposantProcessus getComposantInterface ()
	{
		return this.composantInterface;
	}
	
}
