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
import iepp.application.areferentiel.ElementReferentiel;
import iepp.application.areferentiel.Referentiel;
import iepp.domaine.DefinitionProcessus;
import iepp.ui.FenetreCreerProcessus;

/**
 * Commande non annulable permettant de créer un nouveau projet dans l'outil
 */
public class CNouveauProjet extends CommandeNonAnnulable
{

	/**
	 * Crée un projet vide
	 * Si un projet est déjà ouvert et modifié demande confirmation de la perte
	 *  du projet courant
	 * @return true si un projet a été créé
	 */
	public boolean executer()
	{
		// on vérifie s'il y a un projet en cours d'édition
		if (Application.getApplication().getProjet() != null )
		{
			// sinon affiche une boite de dialogue qui demande si l'utilisateur veut sauvegarder le processus en cours
			if (Application.getApplication().getProjet().estModifie())
			{
				
			  int choice = JOptionPane.showConfirmDialog(
		  			 Application.getApplication().getFenetrePrincipale(),
					 Application.getApplication().getTraduction("BD_SAUV_AVAT_CREER"),
					 Application.getApplication().getTraduction("Confirmation"),
					 JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
			  

			  // si "oui" l'utilisateur accepte, on demande la sauvegarde
			  if( choice == JOptionPane.YES_OPTION )
			  {
					Referentiel ref = Application.getApplication().getReferentiel() ;
					ref.sauverDefProc (Application.getApplication().getProjet().getDefProc()) ;
			  }
			  // si "annuler" on annule la création
			  else if ( choice == JOptionPane.CANCEL_OPTION)
			  {
			  	return false;
			  }
			}
			// fermer le projet en cours
			CFermerProjet c = new CFermerProjet();
			c.executer();
		}
			
		FenetreCreerProcessus f = new FenetreCreerProcessus(Application.getApplication().getFenetrePrincipale());
		if (f.isProcessusCree())
		{
			// Réinitialiser les associations entre références en mémoire et id
			// dans le référentiel
			Referentiel ref = Application.getApplication().getReferentiel() ;
			//ref.supprimerTousLesElementsCharges() ;
			// Ajout de la DP au référentiel (création de la structure et sauvegarde)
			DefinitionProcessus dp = Application.getApplication().getProjet().getDefProc() ;
			String nomDP = dp.getNomDefProc() ;
			long idDP = Application.getApplication().getReferentiel().ajouterElement (nomDP, ElementReferentiel.DP) ;
			if (idDP == -3)
			{
				JOptionPane.showMessageDialog ( Application.getApplication().getFenetrePrincipale(),
					Application.getApplication().getTraduction("ERR_Processus_Deja"),
					Application.getApplication().getTraduction("ERR"),
					JOptionPane.ERROR_MESSAGE );
			}
			if (idDP < 0)
				return false ;
			// Ajouter la référence de la DP au référentiel (association avec l'id)
			ref.ajouterReferenceMemoire (dp, idDP) ;
			Application.getApplication().getProjet().setModified(true);
			return true ;
		}
		return false;
	}
}
