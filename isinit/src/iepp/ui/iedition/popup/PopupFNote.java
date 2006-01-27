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
import iepp.ui.iedition.VueDPGraphe;
import iepp.ui.iedition.dessin.rendu.FNote;
import iepp.ui.iedition.dessin.rendu.TextCell;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * 
 */

public class PopupFNote extends JPopupMenu implements ActionListener
{
	/**
	 * Items du menu � afficher
	 */
	private JMenuItem suppr;
	//private JMenuItem propriete;
	
	private VueDPGraphe diagramme;
	
	private FNote note ;
	
	private TextCell noteCell ;
	
	
	/**
	 * @param note
	 */
	public PopupFNote(VueDPGraphe d,TextCell note)
	{
		this.diagramme = d;
		
		this.noteCell = note;
		
		this.note = note.getFnote();
		
		// cr�ation des items
		this.suppr = new JMenuItem(Application.getApplication().getTraduction("Supprimer"));
		//this.propriete = new JMenuItem(Application.getApplication().getTraduction("Proprietes"));
		
		// ajouter les items au menu
		this.add(this.suppr);
		//this.add(this.propriete);
		
		// pouvoir r�agr aux clicks des utilisateurs
		this.suppr.addActionListener(this);
		//this.propriete.addActionListener(this);
	}
	
	/**
	 * Gestionnaire de clicks sur les items
	 */
	public void actionPerformed(ActionEvent event)
	{
		 if (event.getSource() == this.suppr)
		 {
		 	this.diagramme.supprimerFigure(this.note);
		 	this.diagramme.supprimerCellule(this.noteCell);
		 	this.diagramme.repaint();
		 }
		 /*
		 else if (event.getSource() == this.propriete)
		 {
		 	(new FenetreProprieteNote(this.note)).show();
		 }
		 */
	 }
}
