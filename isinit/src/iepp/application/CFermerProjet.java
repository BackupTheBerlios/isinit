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
import javax.swing.JPanel;

import iepp.Application;
import iepp.Projet;
import iepp.application.areferentiel.Referentiel;

/**
 * Commande non annulable permettant de fermer le projet courant
 */
public class CFermerProjet extends CommandeNonAnnulable{

		Projet leProjetCourant;
		
		/**
		 * Indique si on doit demander confirmation si un projet est en cours
		 * et pas encore sauvegard�
		 */
		private boolean confirmation = false;
		
		
		
		/**
		 * Constructeur de la commande
		 * @param confirm indique si lors de la fermeture du projet, il faut demander
		 * la sauvegarde du projet en cours ou non
		 */
		public CFermerProjet (boolean confirm)
		{
			this.confirmation = confirm;
		}
		
		/**
		 * Constructeur par d�faut, on ne demande pas de confirmation
		 */
		public CFermerProjet (){}
		
		
		/**
		 * Ferme le projet courant
		 * @see iepp.application.Commande#executer()
		 * @return true si un projet a �t� ferm�
		 */
		public boolean executer()
		{
			// on v�rifie qu'il y a un projet en cours d'�dition
			if (Application.getApplication().getProjet() != null )
			{
				if ( Application.getApplication().getProjet().estModifie() && confirmation)
				{
					// demander confirmation
					int choice = JOptionPane.showConfirmDialog(
				  			 Application.getApplication().getFenetrePrincipale(),
							 Application.getApplication().getTraduction("BD_SAUV_AVAT_FERMER"),
							 Application.getApplication().getTraduction("Confirmation"),
							 JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);

					  // si "oui" l'utilisateur accepte, on demande la sauvegarde
					  if( choice == JOptionPane.YES_OPTION )
					  {
					  		// sauver la dp courante
					  		Referentiel ref = Application.getApplication().getReferentiel() ;
					  		ref.sauverDefProc (Application.getApplication().getProjet().getDefProc()) ;
					  		Application.getApplication().getProjet().setModified(false);
					  }
					  if ( choice == JOptionPane.CANCEL_OPTION)
					  {
					  	return false;
					  }
				}
				// sinon dans les autres cas, ou apr�s sauvegarde de la DP, on ferme le projet
				//copie du projet courant
				this.leProjetCourant = Application.getApplication().getProjet(); 
				//Suppression du contenu du diagramme
				this.leProjetCourant.getFenetreEdition().getVueDPGraphe().effacerDiagramme();
				this.leProjetCourant.getFenetreEdition().removeVueDPGraphe();
				Application.getApplication().getFenetrePrincipale().setPanneauGenerique (new JPanel());
				this.leProjetCourant.setFenEdit(null);				
				//Suppression des composants de la DP
				this.leProjetCourant.getDefProc().retirerComposantTous();
				this.leProjetCourant.getDefProc().maj("DELETED");
				Application.getApplication().getFenetrePrincipale().getVueDPArbre().setModel(null);
				// Retrait de la DP
				this.leProjetCourant.getDefProc().deleteObservers();
				this.leProjetCourant.deleteDefProc();
				// Retrait du porjet courant
				Application.deleteProjet();
				System.gc();
				// R�initialiser les associations entre r�f�rences en m�moire et id
				// dans le r�f�rentiel
				Application.getApplication().getReferentiel().supprimerTousLesElementsCharges() ;
				Application.getApplication().getFenetrePrincipale().majEtat();
				return true;
			}
			return false;
		}

}
