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

package iepp.application.aedition;

import java.util.Enumeration;

import iepp.Application;
import iepp.Projet;
import iepp.application.CommandeNonAnnulable;
import iepp.ui.iedition.VueDPGraphe;
import iepp.ui.iedition.dessin.rendu.FComposantProcessus;
import iepp.ui.iedition.dessin.rendu.FProduit;
import iepp.ui.iedition.dessin.rendu.FProduitFusion;
import iepp.ui.iedition.dessin.rendu.Figure;
import iepp.ui.iedition.dessin.vues.MDElement;

/**
 * Classe permettant d'effectuer un zoom sur le diagramme d'assemblage
 *
 */
public class CZoomer extends CommandeNonAnnulable 
{
	/**
	 * Facteur du zoom à appliquer lors de la transformation
	 */
	private double facteurZoom ; 
	
	
	/**
	 * Constructeur du zoom à partir du facteur à appliquer
	 * @param facteur facteur de zoom à appliquer
	 */
	public CZoomer (double facteur)
	{
		this.facteurZoom = facteur;
	}
	
	/**
	 * Effectue le zoome si une DP est en cours d'édition
	 */
	public boolean executer()
	{
		// récupérer les projet en cours
		Projet p = Application.getApplication().getProjet();
		// s'il y en a effectivement un
		if (p != null)
		{
			VueDPGraphe vue = p.getFenetreEdition().getVueDPGraphe();
			// récupérer toutes les figures présentes dans le diagramme
			Enumeration e = vue.elements();
			while(e.hasMoreElements())
			{
				Figure  figure = (Figure) e.nextElement();
				// récupérer le modèle de dessin courant pour les produits et les composants
				if ((figure instanceof FProduit) 
					|| (figure instanceof FComposantProcessus)
					||(figure instanceof FProduitFusion))
				{
					MDElement md = (MDElement)figure.getModele();
					// appliquer les transformations
					int x = (int) (md.getX() * this.facteurZoom);
					if (x == 0) md.setX(1); else md.setX(x);
				  	int y = (int) (md.getY() * this.facteurZoom);
				  	if (y == 0) md.setY(1); else md.setY(y);
				  	int l = (int) (md.getLargeur() * this.facteurZoom);
				  	if (l == 0) md.setLargeur(1); else md.setLargeur(l);
				  	int h = (int) (md.getHauteur() * this.facteurZoom);
				  	if (h == 0) md.setHauteur(1); else md.setHauteur(h);
				}
			}
			vue.getModele().setFacteurZoom(this.facteurZoom);
			vue.repaint();
		}
		return false;
	}
}