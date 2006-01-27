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




import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import util.ErrorManager;
import util.SimpleFileFilter;


import iepp.Application;
import iepp.application.areferentiel.Referentiel;
import iepp.ui.FenetreChoixProcessus;
import iepp.ui.FenetreChoixReferentiel;




/**
 * Classe permettant de lancer la proc�dure de chargement d'un r�f�rentiel
 * sauvegard�e. V�rifie si un processus n'est pas d�j� ouvert, demande confirmation
 * pour perdre le travail en cours et demande le r�f�rentiel � charger
 */
public class COuvrirReferentiel extends CommandeNonAnnulable
{

	/**
	 * Filtre utilis� par la bo�te de dialogue demandant le r�f�rentiel � ouvrr
	 */
	private SimpleFileFilter filter = new SimpleFileFilter("ref", "Referentiel") ;
	
	
	public boolean executer()
	{
		// v�rifier qu'il n'y ait pas d�j� de Dp ouverte
		if (Application.getApplication().getProjet() != null)
		{
			// on v�rifie si la dp doit �tre sauvegard�e
			if (Application.getApplication().getProjet().estModifie())
			{
				
				int choice = JOptionPane.showConfirmDialog(
		  			 Application.getApplication().getFenetrePrincipale(),
					 Application.getApplication().getTraduction("BD_SAUV_AVAT_OUVRIR_REF"),
					 Application.getApplication().getTraduction("Confirmation"),
					 JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);

				  // si "oui" l'utilisateur accepte, on demande la sauvegarde
				  if( choice == JOptionPane.YES_OPTION )
				  {
				  		Referentiel ref = Application.getApplication().getReferentiel() ;
				  		ref.sauverDefProc (Application.getApplication().getProjet().getDefProc()) ;
				  		Application.getApplication().getProjet().setModified(false);
				  }
				  if ( choice == JOptionPane.CANCEL_OPTION)
				  {
				  	return false;
				  }
			}
		}

		// Demander le composant � l'utilisateur � l'aide d'une boite de dialogue
		JFileChooser chooser = new JFileChooser(Application.getApplication().getConfigPropriete("chemin_referentiel"));
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileFilter(filter);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		// r�cup�re le nom de fichier s�lectionn� par l'utilisateur
		if(chooser.showOpenDialog(Application.getApplication().getFenetrePrincipale()) != JFileChooser.APPROVE_OPTION )
		{
			return false;
		}
		
		// on  ferme le projet en cours
		CFermerProjet c = new CFermerProjet();
		c.executer();
		
		CChargerReferentielDemarrage c2 = new CChargerReferentielDemarrage(chooser.getSelectedFile());
		if (!c2.executer())
		{
			return false;
		}
		return true;
	}
}

