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
package iepp.ui.ireferentiel.popup;

import iepp.Application;
import iepp.application.areferentiel.CAjouterComposantRef;

import javax.swing.* ;
import java.awt.event.* ;


/**
 * Menu contextuel associ� au paquetage des composants dans l'arbre du r�f�rentiel.
 */
public class PopupPaqComposants extends JPopupMenu implements ActionListener
{

	private JMenuItem ajouterComposantRef ;
	
	/**
	 * Constructeur.
	 */
	public PopupPaqComposants ()
	{
		// Cr�er les �l�ments
		this.ajouterComposantRef = new JMenuItem (Application.getApplication().getTraduction("Ajouter_Composant_Ref")) ;
		// Ajouter les �l�ments au menu
		this.add (this.ajouterComposantRef) ;
		// Ecouter les �v�nements
		this.ajouterComposantRef.addActionListener (this) ;
	}


	/** Clics sur les �l�ments du menu.
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed (ActionEvent e)
	{
		// Si on clique sur l'�l�ment "ajouter un composant", lancer la commande
		// d'enregistrement dans le r�f�rentiel
		(new CAjouterComposantRef()).executer() ;
	}

}
