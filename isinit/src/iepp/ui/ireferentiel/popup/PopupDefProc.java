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
import iepp.application.areferentiel.CChargerDP;
import iepp.application.areferentiel.CRetirerDP;

import java.awt.event.* ;

import javax.swing.* ;

/**
 * Menu contextuel associ� aux d�finitions de processus dans l'arbre du r�f�rentiel.
 */
public class PopupDefProc extends JPopupMenu implements ActionListener
{
	private long idDP ;	// Identifiant de la d�finition de processus concern�e

	// Options du menu
	private JMenuItem ouvrirDP ;
	private JMenuItem retirerDP ;


	/**
	 * Constructeur.
	 * @param idDefProc identifiant dans le r�f�rentiel de la DP associ� au menu.
	 */
	public PopupDefProc (long idDefProc)
	{
		// Enregistrer les param�tres
		this.idDP = idDefProc ;
		// Cr�er les �l�ments
		this.ouvrirDP = new JMenuItem (Application.getApplication().getTraduction("Ouvrir_DP")) ;
		this.retirerDP = new JMenuItem (Application.getApplication().getTraduction("Retirer_DP_Ref")) ;
		// Ajouter les �l�ments au menu
		this.add (this.ouvrirDP) ;
		this.add (this.retirerDP) ;
		// Ecouter les �v�nements
		this.ouvrirDP.addActionListener (this) ;
		this.retirerDP.addActionListener (this) ;
	}


	/** Clics sur les �l�ments du menu.
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed (ActionEvent e)
	{
		Object source = e.getSource() ;
		// Supprimer la d�finition du r�f�rentiel
		if (source == this.retirerDP)
		{
			(new CRetirerDP (this.idDP)).executer() ;
		}
		// Ouvrir une DP du r�f�rentiel
		else if (source == this.ouvrirDP)
		{
			(new CChargerDP (this.idDP)).executer() ;
		}
	}

}
