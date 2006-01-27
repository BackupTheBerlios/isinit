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
package iepp.application.areferentiel;

import java.util.Vector;

import javax.swing.JOptionPane;

import iepp.Application;
import iepp.Projet;
import iepp.application.CSupprimerPresentationDP;
import iepp.application.CommandeNonAnnulable;
import iepp.application.aedition.CSupprimerComposant;
import iepp.domaine.IdObjetModele;
import iepp.domaine.PaquetagePresentation;

/**
 * Chargement d'une définition de processus depuis le référentiel dans l'application.
 */
public class CChargerDP extends CommandeNonAnnulable
{

	private long idDP ;


	/**
	 * Constructeur.
	 * @param idDP Identifiant dans le référentiel de la DP à charger.
	 */
	public CChargerDP (long idDP)
	{
		this.idDP = idDP ;
	}


	/**
	 * Charge une définition de processus depuis le référentiel dans l'application.
	 * @see iepp.application.Commande#executer()
	 * @return true si la commande s'est exécutée correctement
	 */
	public boolean executer()
	{
		Referentiel ref = Application.getApplication().getReferentiel() ;
		// Si une DP est ouverte
		if (Application.getApplication().getProjet() != null)
		{
			// Demander à l'utilisateur s'il accepte de la fermer
			// (efface les références aux objets de la DP)
			//	on vérifie si la dp doit être sauvegardée
			 if (Application.getApplication().getProjet().estModifie())
			 {
	
				 int choice = JOptionPane.showConfirmDialog(
					  Application.getApplication().getFenetrePrincipale(),
					  Application.getApplication().getTraduction("BD_SAUV_AVAT_OUVRIR"),
					  Application.getApplication().getTraduction("Confirmation"),
					  JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
	
				   // si "oui" l'utilisateur accepte, on demande la sauvegarde
				   if( choice == JOptionPane.YES_OPTION )
				   {
						 // sauver la dp courante
						 ref.sauverDefProc (Application.getApplication().getProjet().getDefProc()) ;
						 Application.getApplication().getProjet().setModified(false);
				   }
				   if ( choice == JOptionPane.CANCEL_OPTION)
				   {
					 return false;
				   }
			 }
		}
		// Charger la DP
		// (l'enregistrement des associations id-références mémoire est effectuée par le référentiel)
		Vector listeComposantSupprimer = new Vector();
		Projet p = ref.chargerDefProc (this.idDP, listeComposantSupprimer) ;
		if (p != null)
		{
			Application.setProjet(p);
			for (int i = 0; i < listeComposantSupprimer.size(); i++)
			{
				if (listeComposantSupprimer.elementAt(i) instanceof IdObjetModele)
				{
					IdObjetModele id = (IdObjetModele)listeComposantSupprimer.elementAt(i);
					CSupprimerComposant c = new CSupprimerComposant (id);
					c.executer();
					
					JOptionPane.showMessageDialog ( Application.getApplication().getFenetrePrincipale(),
									Application.getApplication().getTraduction("ERR_Composant_Supprimer_partie1")+ " " +
									 id.toString() + " " + Application.getApplication().getTraduction("ERR_Composant_Supprimer_partie2"),
									Application.getApplication().getTraduction("ERR"),
									JOptionPane.INFORMATION_MESSAGE );
				}
				// paquetage de présentation
				else
				{
					PaquetagePresentation paquet = (PaquetagePresentation)listeComposantSupprimer.elementAt(i);
					CSupprimerPresentationDP c = new CSupprimerPresentationDP (paquet);
					c.executer();
					
					JOptionPane.showMessageDialog ( Application.getApplication().getFenetrePrincipale(),
									Application.getApplication().getTraduction("ERR_Presentation_Supprimer_partie1")+ " " +
									paquet.toString() + " " + Application.getApplication().getTraduction("ERR_Presentation_Supprimer_partie2"),
									Application.getApplication().getTraduction("ERR"),
									JOptionPane.INFORMATION_MESSAGE );
				}
			}
			return true;
		}
		return false ;
	}


}
