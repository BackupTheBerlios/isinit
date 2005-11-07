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
import iepp.application.CFermerProjet;
import iepp.application.CNouveauProjet;
import iepp.application.areferentiel.CAjouterComposantRef;
import iepp.application.areferentiel.CAjouterPaqPresRef;

import javax.swing.* ;
import java.awt.event.* ;

/**
 * Menu contextuel associ� au r�f�rentiel dans l'arbre du r�f�rentiel.
 */
public class PopupReferentiel extends JPopupMenu implements ActionListener
{
	// El�ments de menu
	private JMenuItem ajouterComposantRef ;
	private JMenuItem creerDP ;
	private JMenuItem ajouterPresentationRef ;

	/**
	 * Constructeur.
	 */
	public PopupReferentiel ()
	{
		// Cr�er les �l�ments
		this.ajouterComposantRef = new JMenuItem (Application.getApplication().getTraduction("Ajouter_Composant_Ref")) ;
		this.creerDP = new JMenuItem (Application.getApplication().getTraduction("Creer_DP")) ;
		this.ajouterPresentationRef = new JMenuItem (Application.getApplication().getTraduction("Ajouter_Presentation_Ref")) ;
		// Ajouter les �l�ments au menu
		this.add (this.ajouterComposantRef) ;
		this.add (this.creerDP) ;
		this.add (this.ajouterPresentationRef) ;
		// Ecouter les �v�nements
		this.ajouterComposantRef.addActionListener (this) ;
		this.creerDP.addActionListener (this) ;
		this.ajouterPresentationRef.addActionListener (this) ;
	}


	/** Clics sur les �l�ments du menu.
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed (ActionEvent e)
	{
		Object source = e.getSource() ;
		// Ajouter un composant au r�f�rentiel
		if (source == this.ajouterComposantRef)
			(new CAjouterComposantRef()).executer() ;
		// Cr�er une nouvelle DP
		else if (source == this.creerDP)
		{
		    if(! new CNouveauProjet().executer())
		    {
		        new CFermerProjet().executer() ;
		    }
		}  
		else if (source == this.ajouterPresentationRef)
			(new CAjouterPaqPresRef()).executer() ;
	}

}
