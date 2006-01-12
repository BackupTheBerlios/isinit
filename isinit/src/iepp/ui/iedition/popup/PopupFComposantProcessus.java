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
import iepp.application.aedition.CSupprimerComposantGraphe;
import iepp.application.aedition.CRenommerComposantGraphe;
import iepp.ui.iedition.dessin.rendu.FComposantProcessus;
import iepp.application.aedition.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import iepp.domaine.*;



/**
 *
 */
public class PopupFComposantProcessus extends JPopupMenu implements ActionListener
{
	/**
	 * Items du menu à afficher
	 */
	private JMenuItem supprimer;
	private JMenuItem renommer;

	/**
	* composant sur lequel on a cliqué.
	*/
	private FComposantProcessus compo;

	/**
	 * Création du menu contextuel
	 */
	public PopupFComposantProcessus(FComposantProcessus cp )
	{
		this.compo = cp;

		// création des items
		this.supprimer = new JMenuItem(Application.getApplication().getTraduction("Supprimer_Composant"));
		this.renommer = new JMenuItem(Application.getApplication().getTraduction("Renommer_Composant"));//modif 2xmi youssef

		// ajouter les items au menu
		this.add(this.supprimer);
		if(this.compo.getModele().getId().estComposantVide())
        {
			this.add(this.renommer);//modif 2xmi youssef
        }

		// pouvoir réagr aux clicks des utilisateurs
		this.supprimer.addActionListener(this);
		this.renommer.addActionListener(this);//modif 2xmi youssef
	}

	/**
	 * Gestionnaire de clicks sur les items
	 */
	public void actionPerformed(ActionEvent event)
	{
		 if (event.getSource() == this.supprimer)
		 {
			CSupprimerComposantGraphe c = new CSupprimerComposantGraphe(this.compo.getModele().getId());
			if (c.executer())
			{
				Application.getApplication().getProjet().setModified(true);
			}
		 }
         if (event.getSource() == this.renommer)
         {
           //Modif 2xmi appel du constructeur avec Fenetre Principal en paramètre pour centrage de la fenetre de renommage
           DialogRenommerComposant c = new DialogRenommerComposant(Application.getApplication().getFenetrePrincipale(),this.compo.getModele().getId());
         }
	 }
}
