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
import iepp.ui.iedition.dessin.vues.MDComposantProcessus;
import iepp.ui.iedition.dessin.vues.MDElement;
import iepp.ui.iedition.popup.PopupFComposantProcessus;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Figure du composant processus
 */
public class FComposantProcessus extends FElement
{

	/**
	 * Taille en caractères du libellé du composant processus
	 */
	private int taille ;

	/**
	 * Taille en pixel du nom du composant processus
	 */
	private int largeurChaine ;


    /**
     * Constructeur
     */
    public FComposantProcessus(MDComposantProcessus m)
    {
      super(m);
    }

    public void doOnRightClick(VueDPGraphe parent, int x, int y)
    {
    	
    	//PopupFComposantProcessus p = new PopupFComposantProcessus(this);
    	//p.show(parent, x, y);	
    }
    
    public void mouseReleased(MouseEvent e)
    {
    	System.out.println("beeeeeeeed");
    	//Hubert : popup menu sur le graph (hors cellules et lien)
		/*if (!((this.getFirstCellForLocation(e.getX(), e.getY()) instanceof IeppCell)||(this.getFirstCellForLocation(e.getX(), e.getY()) instanceof LienEdge)))
		{
			if (e.isPopupTrigger())
          	{
				showPopupMenuDiagramme(e.getX(), e.getY());
         	}
		}*/
    }
    
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
    public void drawElementShadow(Graphics g, Color shadowLineColor, int decalageX, int decalageY, int resizeX, int resizeY)
    {
    	drawBody(g, null, shadowLineColor, decalageX, decalageY, resizeX, resizeY);
    }

    /**
    Dessine le corps de la figure.
   
    protected void drawBody(Graphics g, Color fillColor, Color lineColor, int decalageX, int decalageY, int resizeX, int resizeY) {
      Graphics2D g2 = (Graphics2D) g;

	   
	  int x = ((MDElement) getModele()).getX() + decalageX;
	  int y = ((MDElement) getModele()).getY() + decalageY;
	  int l = ((MDElement) getModele()).getLargeur() + resizeX;
	  int h = ((MDElement) getModele()).getHauteur() + resizeY;
			
	   int x1[]=new int[6];
	   int y1[]=new int[6];

	   // polygone jaune
	   x1[0]= x + (l / 3);
	   x1[1]= x + 2 * (l / 3);
	   x1[2]= x + 2 * (l / 3) + (l / 6);
	   x1[3]= x + 2 * (l / 3);
	   x1[4]= x + (l / 3);
	   x1[5]= x + (l / 3) + (l / 6);
	   y1[0]= y + 2 * (h / 3) - (h / 6);
	   y1[1]= y + 2 * (h / 3) - (h / 6);
	   y1[3]= y + h ;
	   y1[4]= y + h ;
	   y1[5]= y1[0] + (y1[3] - y1[0]) / 2;
	   y1[2]= y1[0] + (y1[3] - y1[0]) / 2;
	   
 		
	   // produit rouge
	   int x2[]=new int[4];
	   int y2[]=new int[4];
 
	   x2[0]= x + (l / 2) ;
	   y2[0]= y ;
	   x2[1]= x + l ;
	   y2[1]= y ;
	   x2[2]= x + l ;
	   y2[2]= y + 2 * (h / 3) ;
	   x2[3]= x + (l / 2);
	   y2[3]= y + 2 * (h / 3) ;
	 	
	   // bonhomme
		int xtete = x + 2 * (l / 8);
		int ytete = y ;
		int ltete = (l / 8);
		int htete = (l / 8);
		//
		// si l'on déplace le composant
		if (fillColor != null)
		{ 
			// produit
			g2.setPaint(new Color(223,146,126));
			g2.fillPolygon(x2,y2,4);
					 
			// activité
			g2.setPaint(new Color(255,255,203));
			g2.fillPolygon(x1,y1,6);
			
			// bonhomme
			g2.setPaint(new Color(55,55,203));
			g2.fillOval(xtete, ytete, ltete, htete);
			g2.fillRoundRect(xtete + (ltete / 16),ytete + htete,ltete - (ltete / 8) , htete + (h / 3) - (h / 6),(int)(0.5*ltete),htete);
			g2.fillRoundRect(xtete - (ltete) ,ytete + htete + (htete / 16),ltete * 3 , ( htete / 3 ) ,(int)(0.5*ltete),htete);
				
		}
		g2.setPaint(lineColor);
		g2.drawPolygon(x1,y1,6);
		g2.drawPolygon(x2,y2,4);

		for(int i = y2[0]; i < y2[3] ; i += 5)
		{
			g2.drawLine(x2[0] + (x2[1] - x2[0])/ 4, i , x2[1] - (x2[1] - x2[0])/ 4 , i );
		}
		
		g2.drawRect(x,y,l,h);
		
		g2.setFont(((MDComposantProcessus) getModele()).getPolice());
		// centrer le texte associé au composant
		this.taille = ((MDComposantProcessus) getModele()).getNom().length();
		this.largeurChaine = g2.getFontMetrics(((MDComposantProcessus) getModele()).getPolice()).charsWidth(
												 ((MDComposantProcessus) getModele()).getNom().toCharArray(),
												 0, this.taille);
		g2.drawString(((MDComposantProcessus) getModele()).getNom(), x + (l/2) - (this.largeurChaine/2), y + h + 15);
    }
*/
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
	
	/**
    * Dessine le corps de la figure.
    */
    protected void drawBody(Graphics g, Color fillColor, Color lineColor, int decalageX, int decalageY, int resizeX, int resizeY) {
      Graphics2D g2 = (Graphics2D) g;

	   
	  int x = ((MDElement) getModele()).getX() + decalageX;
	  int yaux = ((MDElement) getModele()).getY() + decalageY;
	  int l = ((MDElement) getModele()).getLargeur() + resizeX;
	  int h = ((MDElement) getModele()).getHauteur() + resizeY;
	   
	  // mettre un décalage pour que l'image soit centrée
	  int y = yaux + (int)(l / 15);

	   	int x1[]=new int[5];
		int y1[]=new int[5];
		int x2[]=new int[20];
		int y2[]=new int[20];
		int x3[]=new int[5];
		int y3[]=new int[5];

	// produit
	x1[0]= (int) (x + l/2 + (((float)5 / (float)100 ) * l));
	x1[1]= (int)(x + l/2 + (((float)35 /  (float)100 ) * l));
	x1[2]= (int)(x + l/2 + (((float)45 /  (float)100 ) * l));
	x1[3]= (int)(x + l/2 + (((float)45 /  (float)100 ) * l));
	x1[4]= (int)(x + l/2 + (((float)5 /  (float)100 ) * l));
	y1[0]=(int) (y + h/2- (((float)45 /  (float)100 ) * h));
	y1[1]= (int)(y + h/2- (((float)45 /  (float)100 ) * h));
	y1[2]= (int)(y + h/2- (((float)35 /  (float)100 ) * h));
	y1[3]= (int)(y + h + 1 - (((float)46 /  (float)100 ) * h));
	y1[4]= (int)(y + h + 1 - (((float)46 /  (float)100 ) * h));

	// bonhomme
	x2[0]= (int)(x + l/2- (((float)19 /  (float)100 ) * l));
	x2[1]=(int) (x + l/2- (((float)19 /  (float)100 ) * l));
	x2[2]=(int) (x + l/2- (((float)4 /  (float)100 ) * l));
	x2[3]=(int) (x + l/2- (((float)4 /  (float)100 ) * l));
	x2[4]=(int) (x + l/2- (((float)19 /  (float)100 ) * l));
	x2[5]=(int) (x + l/2- (((float)19 /  (float)100 ) * l));
	x2[6]= (int)(x + l/2- (((float)4 /  (float)100 ) * l));
	x2[7]= (int)(x + l/2- (((float)4 /  (float)100 ) * l));
	x2[8]=(int) (x + l/2- (((float)9 /  (float)100 ) * l));
	x2[9]=(int) (x + l/2- (((float)9 /  (float)100 ) * l));
	x2[10]= (int)(x + l/2- (((float)39 /  (float)100 ) * l));
	x2[11]= (int)(x + l/2- (((float)39 /  (float)100 ) * l));
	x2[12]= (int)(x + l/2- (((float)44 /  (float)100 ) * l));
	x2[13]= (int)(x + l/2- (((float)44 /  (float)100 ) * l));
	x2[14]=(int) (x + l/2- (((float)29 /  (float)100 ) * l));
	x2[15]=(int) (x + l/2- (((float)29 /  (float)100 ) * l));
	x2[16]=(int) (x + l/2- (((float)44 /  (float)100 ) * l));
	x2[17]= (int)(x + l/2- (((float)44 / (float) 100 ) * l));
	x2[18]= (int)(x + l/2- (((float)29 /  (float)100 ) * l));
	x2[19]=(int) (x + l/2- (((float)29 / (float) 100 ) * l));

	y2[0]= (int)(y + (((float)25 /  (float)100 ) * h));
	y2[1]= (int)(y + (((float)30 /  (float)100 ) * h));
	y2[2]= (int)(y + (((float)30 /  (float)100 ) * h));
	y2[3]= (int)(y + (((float)35 /  (float)100 ) * h));
	y2[4]= (int)(y + (((float)35 /  (float)100 ) * h));
	y2[5]= (int)(y + (((float)45 /  (float)100 ) * h));	
	y2[6]= (int)(y + (((float)45 /  (float)100 ) * h));
	y2[7]= (int)(y + (((float)65 /  (float)100 ) * h));
	y2[8]= (int)(y + (((float)65 /  (float)100 ) * h));
	y2[9]= (int)(y + (((float)50 /  (float)100 ) * h));
	y2[10]= (int)(y + (((float)50 /  (float)100 ) * h));	
	y2[11]= (int)(y + (((float)65 /  (float)100 ) * h));
	y2[12]= (int)(y + (((float)65 /  (float)100 ) * h));
	y2[13]= (int)(y + (((float)45 /  (float)100 ) * h));
	y2[14]=(int) (y + (((float)45 /  (float)100 ) * h));
	y2[15]= (int)(y + (((float)35 /  (float)100 ) * h));	
	y2[16]= (int)(y + (((float)35 /  (float)100 ) * h));
	y2[17]= (int)(y + (((float)30 /  (float)100 ) * h));
	y2[18]= (int)(y + (((float)30 /  (float)100 ) * h));
	y2[19]= (int)(y + (((float)25/  (float)100 ) * h));	

	// activité
	x3[0]= (int)(x + l/2- (((float)25 /  (float)100 ) * l));
	x3[1]= (int)(x + l/2+ (((float)30 /  (float)100 ) * l));
	x3[2]= (int)(x + l/2+ (((float)45 /  (float)100 ) * l));
	x3[3]= (int)(x + l/2+(((float)30 /  (float)100 ) * l));
	x3[4]= (int)(x + l/2-(((float)25 /  (float)100 ) * l));
	y3[0]= (int)(y + (((float)50/  (float)100 ) * h));
	y3[1]= (int)(y + (((float)50/  (float)100 ) * h));
	y3[2]= (int)( y + (((float)66/  (float)100 ) * h));	
	y3[3]= (int)(y + (((float)82/  (float)100 ) * h));
	y3[4]= (int)(y + (((float)82/ (float) 100 ) * h));

	
		//
		// si l'on déplace le composant
		if (fillColor != null)
		{ 
			// produit
			g2.setPaint(new Color(223,146,126));
			g2.fillPolygon(x1,y1,5);
			g2.setPaint(Color.BLACK);
			g2.setStroke(new BasicStroke(2));
			g2.drawPolygon(x1,y1,5);

			g2.setStroke(new BasicStroke(1));
			
			for(int i=5+5; i < 15+5 ; i+=5 )
			{
				g.drawLine((int)(x + l/2 + (((float)10/ (float)100 ) * l)),
						(int)(y + (((float)i / (float)100) * h)) ,
						(int)(x + l/2 + (((float)30/ (float)100 ) * l)),
						(int)(y + (((float)i / (float)100) * h)));
			}
			
			for(int i = 15 + 5 ; i < 55; i += 5)
			{
				g.drawLine((int)(x + l/2 + (((float)10/ (float)100 ) * l)),
						(int)(y + (((float)i / (float)100) * h)) ,
						(int)(x + l/2 + (((float)40/ (float)100 ) * l)),
						(int)(y + (((float)i / (float)100) * h)));
			}
			
			// bonhomme
			g2.setPaint(new Color(0,0,255));
			g.fillOval((int)(x + l/2 - (((float)35/ (float)100 ) * l)),
							(int)(y + (((float)5/ (float)100 ) * h)),
							(int)(((float)20/ (float)100 ) * l) ,
							(int)(((float)20/ (float)100 ) * h)) ;
			g.fillPolygon(x2,y2,20);

			
			g2.setPaint(Color.BLACK);
			g2.setStroke(new BasicStroke(2));
			g.drawOval((int)(x + l/2 - (((float)35/ (float)100 ) * l)),
					(int)(y + (((float)5/ (float)100 ) * h)),
					(int)(((float)20/ (float)100 ) * l) ,
					(int)(((float)20/ (float)100 ) * h)) ;
			g.drawPolygon(x2,y2,20);
			
			// activité
			g2.setPaint(new Color(255,252,43));
			g2.fillPolygon(x3,y3,5);	
			g2.setPaint(Color.BLACK);
			g2.setStroke(new BasicStroke(2));
			g2.drawPolygon(x3,y3,5);
		}
		
		g2.setPaint(Color.BLACK);
		g2.setStroke(new BasicStroke(1));
		g2.drawRect(x,yaux,l,h);
		
		g2.setFont(((MDComposantProcessus) getModele()).getPolice());
		// centrer le texte associé au composant
		this.taille = ((MDComposantProcessus) getModele()).getNom().length();
		this.largeurChaine = g2.getFontMetrics(((MDComposantProcessus) getModele()).getPolice()).charsWidth(
												 ((MDComposantProcessus) getModele()).getNom().toCharArray(),
												 0, this.taille);
		g2.drawString(((MDComposantProcessus) getModele()).getNom(), x + (l/2) - (this.largeurChaine/2), y + h + 15);
    }
}
