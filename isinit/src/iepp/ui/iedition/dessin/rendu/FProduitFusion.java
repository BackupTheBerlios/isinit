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


import iepp.Application;
import iepp.domaine.IdObjetModele;
import iepp.domaine.ObjetModele;
import iepp.ui.FenetreRenommerProduitFusion;
import iepp.ui.iedition.dessin.rendu.liens.FLienFusion;
import iepp.ui.iedition.dessin.vues.MDElement;
import iepp.ui.iedition.dessin.vues.MDProduit;
import java.awt.*;
import java.util.HashMap;
import java.util.Vector;


/**
 * Classe permettant la création d'un produit résultant de la fusion de 
 * deux produits existants (un produit en sortie d'un composant et un autre en entrée 
 * d'un autre composant
 */
public class FProduitFusion extends FElement
{
	
	/**
	 * Taille en caractères du libellé du produit
	 */
	private int taille;
	
	/**
	 * Taille en pixel du nom du produit
	 */
	private int largeurChaine;
	
	
	private String nomFusion;
	private Vector vecteurLienFusion;
	private HashMap mapFProduits;
	
	/**
	 * Constructeur
	 */
	public FProduitFusion(MDProduit m , FProduit un, FLienFusion lUn, FProduit deux, FLienFusion lDeux)
	{
	  super(m);
	  vecteurLienFusion = new Vector();
	  mapFProduits = new HashMap();
	  vecteurLienFusion.add(lUn);
	  vecteurLienFusion.add(lDeux);
	  mapFProduits.put(lUn,un);
	  mapFProduits.put(lDeux,deux);
	  nomFusion = m.getNom();
	}
	
	/** 
	 * Méthode ajouterProduit permettant d'ajouter
	 * à un produit fusion un nouveau produit 
	 * @param lObjet Produit à ajouter au produit fusion
	 */
	public void ajouterProduit(FProduit lObjet, FLienFusion lLien)
	{
		vecteurLienFusion.add(lLien);
	    mapFProduits.put(lLien,lObjet);
	}
	
	/**
	 * Procédure de renommage du produit de fusion
	 * Appelle une boite de dialogue permettant de choisir le nom
	 */
	public void renommer()
	{
		FenetreRenommerProduitFusion f = new FenetreRenommerProduitFusion(Application.getApplication().getFenetrePrincipale(),this);
		f.pack();
		f.show();
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
	public void drawElementShadow(Graphics g, Color shadowLineColor, int decalageX, int decalageY, int resizeX, int resizeY) {
	  drawBody(g, null, shadowLineColor, decalageX, decalageY, resizeX, resizeY);
	}

	/**
	 * Dessine le corps de la figure.
	 */
	protected void drawBody(Graphics g, Color fillColor, Color lineColor, int decalageX, int decalageY, int resizeX, int resizeY)
	{
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
			//Color c = new Color(223,146,126);
			//Color c = new Color(255,146,126);
			Color c = new Color(255,255,0);
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
		this.taille = getNomFusion().length();
		this.largeurChaine = g2.getFontMetrics(((MDProduit) getModele()).getPolice()).charsWidth(
													  getNomFusion().toCharArray(),
													  0, this.taille);
		g2.drawString(getNomFusion(), x + (l/2) - (this.largeurChaine/2), y + h + 15);
		
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
	
	public String getNomFusion()
	{
		return nomFusion;
	}
	
	public void setNomFusion(String nom)
	{
		nomFusion = nom;		
	}
	
	public void setNomFusionAll(String nom)
	{
		nomFusion = nom;
		for (int i=0;i<mapFProduits.size();i++)
			((FProduit)mapFProduits.get(vecteurLienFusion.get(i))).getModele().getId().setNomElement(nom);

		Application.getApplication().getFenetrePrincipale().getVueDPArbre().invalidate();
	}
	
	public int getNombreProduits()
	{
		return mapFProduits.size();
	}
	
	public FProduit getProduits(int index)
	{
		return (FProduit)mapFProduits.get(vecteurLienFusion.get(index));
	}
	
	public FProduit removeProduit(FLienFusion laClef)
	{
		vecteurLienFusion.remove(laClef);
		FProduit retour = (FProduit)mapFProduits.remove(laClef);
		
		/*
		Normalement la fusion n'a alors plus de sens (plus de prod en entree), donc il faudra la supprimer
		Code laissé en cas de relaxation des regles de liaison
		
		// Si l'id du produit fusion est celle du produit que l'on enleve, on doit le changer
		if (this.getModele() == retour.getModele() && !this.mapFProduits.isEmpty())
		{
		    this.setModele(((FProduit)(mapFProduits.values().toArray()[0])).getModele());
		}
		*/
		
		return retour;
	}
	
	public FProduit getLastProduit()
	{
		if (mapFProduits.size() != 1)
			return null;
		else
			return (FProduit)mapFProduits.get(vecteurLienFusion.elementAt(0));
	}
	
	public FLienFusion getLienFusion(int index)
	{
		return (FLienFusion)vecteurLienFusion.elementAt(index);
	}
	
	public boolean isLinkedComponent(ObjetModele leComposant)
	{
		boolean trouve=false;
		int i=-1;
		while(++i < vecteurLienFusion.size() && !trouve)
		{
			if (((FLienFusion)vecteurLienFusion.get(i)).getSource().getModele().getId().getRef() == leComposant ||
			((FLienFusion)vecteurLienFusion.get(i)).getDestination().getModele().getId().getRef() == leComposant)
			  trouve = true;
		}
		
		return trouve;
		
	}

    /**
     * @param fusion
     * @return
     */
    public boolean estLienPrimaire(FLienFusion fusion)
    {
        return (((FProduit)this.mapFProduits.get(fusion)).getModele().getId().estProduitSortie());
    }
}