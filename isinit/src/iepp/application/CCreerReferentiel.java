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
 
 
package iepp.application;


import javax.swing.JOptionPane;

import iepp.* ;
import iepp.application.areferentiel.Referentiel;
import iepp.ui.FenetreCreerReferentiel;

/**
 * Commande non annulable permettant de cr�er un nouveau r�f�rentiel dans l'outil
 */
public class CCreerReferentiel extends CommandeNonAnnulable
{

	/**
	 * Cr�e un projet vide
	 * Si un projet est d�j� ouvert, demande confirmation de la fermeture du projet courant
	 * @see iepp.application.Commande#executer()
	 * @return true si un projet a �t� cr��
	 */
	public boolean executer()
	{
		// on v�rifie s'il y a un projet en cours d'�dition
		if (Application.getApplication().getProjet() == null )
		{
			// si non, on cr�e le nouveau r�f�rentiel
			FenetreCreerReferentiel f =  new FenetreCreerReferentiel(Application.getApplication().getFenetrePrincipale(), null);
			return (f.isReferentielCree());
		}
		else
		{
			
			// on v�rifie si la dp doit �tre sauvegard�e
			if (Application.getApplication().getProjet().estModifie())
			{
				
				int choice = JOptionPane.showConfirmDialog(
		  			 Application.getApplication().getFenetrePrincipale(),
					 Application.getApplication().getTraduction("BD_SAUV_AVAT_CREER_REF"),
					 Application.getApplication().getTraduction("Confirmation"),
					 JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);

				  // si "oui" l'utilisateur accepte, on demande la sauvegarde
				  if( choice == JOptionPane.YES_OPTION )
				  {
				  		//sauver la dp courante
				  		Referentiel ref = Application.getApplication().getReferentiel() ;
				  		ref.sauverDefProc (Application.getApplication().getProjet().getDefProc()) ;
				  		Application.getApplication().getProjet().setModified(false);
				  }
				  if ( choice == JOptionPane.CANCEL_OPTION)
				  {
				  	return false;
				  }
			}
			
			FenetreCreerReferentiel f =  new FenetreCreerReferentiel(Application.getApplication().getFenetrePrincipale(), null);
			return (f.isReferentielCree());
		}
	}
}
