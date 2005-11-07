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


import iepp.ui.iedition.dessin.rendu.liens.FLienInterface;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;



/**
 * Classe permettant d'afficher un popupmenu contextuel lorsque l'utilisateur
 * clique droit sur un lien entre un composant et son interface (fournie ou requise)
 */
public class PopupFLienInterface extends JPopupMenu implements ActionListener
{
	/**
	 * Items du menu à afficher
	 */	
	private JMenuItem afaire;

	/**
	* Lien sur lequel on a cliqué.
	*/
	private FLienInterface lien;

   /**
	* coordonnées du click droit
	*/
	private int clickX, clickY;
		 
	
	/**
	 * Création du menu contextuel
	 */
	public PopupFLienInterface(FLienInterface l,int clickX, int clickY )
	{
		this.lien = l;
		this.clickX = clickX;
		this.clickY = clickY;
	}
	
	/**
	 * Gestionnaire de clicks sur les items
	 */
	public void actionPerformed(ActionEvent event)
	{
	 }
}
