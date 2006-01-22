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
 
package iepp.ui.iedition.popup;


import iepp.Application;
import iepp.application.aedition.CSupprimerComposant;
import iepp.application.aedition.CSupprimerLienFusion;
import iepp.application.aedition.CSupprimerProduitFusion;
import iepp.ui.iedition.VueDPGraphe;
import iepp.ui.iedition.dessin.rendu.ProduitCellFusion;
import iepp.ui.iedition.dessin.rendu.liens.FLienFusion;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;


/**
 * Classe permettant d'afficher un popupmenu contextuel lorsque l'utilisateur
 * clique droit sur un lien de fusion entre deux produits
 */
public class PopupFLienFusion extends JPopupMenu implements ActionListener
{
	/**
	 * Items du menu à afficher
	 */
	//private JMenuItem 	
	private JMenuItem suppr;
	private VueDPGraphe diagramme;

	/**
	* Lien sur lequel on a cliqué.
	*/
	private ProduitCellFusion produitF;

   /**
	* coordonnées du click droit
	*/
	private int clickX, clickY;
		 
	
	/**
	 * Création du menu contextuel
	 */
	public PopupFLienFusion(VueDPGraphe d,ProduitCellFusion f,int clickX, int clickY )
	{
		diagramme = d;
		this.produitF = f;
		this.clickX = clickX;
		this.clickY = clickY;
		
		// création des items
		this.suppr = new JMenuItem(Application.getApplication().getTraduction("Supprimer_Lien"));
		
		// ajouter les items au menu
		this.add(this.suppr);
		
		// pouvoir réagr aux clicks des utilisateurs
		this.suppr.addActionListener(this);

	}
	
	/**
	 * Gestionnaire de clicks sur les items
	 */
	public void actionPerformed(ActionEvent event)
	{
		if (event.getSource() == this.suppr)
		{
			CSupprimerProduitFusion c = new CSupprimerProduitFusion(this.diagramme,this.produitF);
			if (c.executer())
			{
				Application.getApplication().getProjet().setModified(true);
			}  	
		}
	 }
}
