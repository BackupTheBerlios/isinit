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

package iepp.ui.iedition.dessin.vues;

import java.awt.Color;
import java.util.Vector;
import java.util.Enumeration;


public class MDDiagramme extends MDDessin
{

    /**
     * Modèles de figures du diagramme
     */
     protected Vector figures;

    /**
     * Couleur de remplissage
     */
    protected Color fillColor;

    /**
    * Couleur des lignes et du texte
    */
    protected Color lineColor;
    
    /**
     * Facteur de zoom courant
     */
    protected double facteurZoom ;



    /**
     * Constructeur
     */
    public MDDiagramme() {
        super();
        figures = new Vector();
        // Valeurs par défaut.
        fillColor = new Color(255, 255, 255);
        lineColor = new Color(0, 0, 0);
        facteurZoom = 1 ;
    }


    /**
    Retourne la couleur des traits.
    */
    public Color getLineColor() {
        return lineColor;
    }
    /**
    Fixe la couleur des traits.
    */
    public void setLineColor(Color c) {
        lineColor = c;
    }

    /**
    Retourne la couleur de remplissage.
    */
    public Color getFillColor() {
        return fillColor;
    }
    /**
    Fixe la couleur de remplissage.
    */
    public void setFillColor(Color c) {
        fillColor = c;
    }
    
    /**
     * Retourne le facteur de zoom courant
     * @return
     */
    public double getFacteurZoom()
	{
    	return facteurZoom;
    }
    
    /**
     * Appliquer encore un autre facteur
     */
    public void setFacteurZoom(double fac)
	{
    	this.facteurZoom = this.facteurZoom * fac ;
    	if (this.facteurZoom == 0)
    	{
    		this.facteurZoom = 0.01;
    	}
    }

    //---------------------------------------------------------------------
    //                       Gestion des modèles de figures
    //---------------------------------------------------------------------

    public void ajouterModeleFigure(MDFigure mf) 
	{	
    	if (mf instanceof MDNote){}
    	else if (mf instanceof MDElement)
    	{
    		this.ajusterAuZoom((MDElement)mf);
    	}
    	figures.addElement(mf);
    }

    public void supprimerModeleFigure(MDFigure mf) {
        figures.removeElement(mf);
    }

    public Enumeration modeleFigures() {
        return figures.elements();
    }
    
    public void ajusterAuZoom(MDElement md)
	{
    	md.setLargeur((int) (md.getLargeur() * this.facteurZoom));
	  	md.setHauteur((int) (md.getHauteur() * this.facteurZoom));
    }
}

