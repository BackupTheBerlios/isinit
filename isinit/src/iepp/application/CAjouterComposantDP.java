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

import iepp.Application ;
import iepp.Projet;
import iepp.application.areferentiel.Referentiel;
import iepp.domaine.ComposantProcessus;

/**
 * Ajout d'un composant � une d�finition de processus.
 */
public class CAjouterComposantDP extends CommandeNonAnnulable
{

	private long idComp ;	// Identifiant dans le r�f�rentiel du compsant � ajouter


	/**
	 * Construit la commande en m�morisant le composant � ajouter
	 * @param idComp identifiant dans la r�f�rentiel du composant � ajouter � la DP
	 */
	public CAjouterComposantDP (long idComp)
	{
		this.idComp = idComp ;
	}


	/**
	 * Ajoute un composant � une d�finition de processus.
	 * @see iepp.application.Commande#executer()
	 * @return true si la commande s'est ex�cut�e correctement
	 */
	public boolean executer()
	{
		// V�rifier qu'un projet est en cours
		Projet projet = Application.getApplication().getProjet() ;
		if (projet == null)
			return false ;
		// V�rifier que le composant n'est pas d�j� charg� en m�moire
		Referentiel ref = Application.getApplication().getReferentiel() ;
		if (ref.chercherReference (this.idComp) != null)
		{
			JOptionPane.showMessageDialog( Application.getApplication().getFenetrePrincipale(),
					 Application.getApplication().getTraduction("ERR_Composant_Deja_DP"),
					 Application.getApplication().getTraduction("ERR"), JOptionPane.ERROR_MESSAGE) ;
			return false ;
		}
		// Demander au r�f�rentiel de nous fournir (charger en m�moire) le composant
		// et l'ajouter � la DP courante s'il n'y est pas d�j�
		ComposantProcessus comp ;
		if ((comp = ref.chargerComposant(idComp)) == null)
			return false ;
		// v�rifier si c'est un composant vide
		if (comp.estVide())
		{
			comp.setNomComposant(Projet.getNouveauNom());
		}
		projet.getDefProc().ajouterComposant(comp) ;
		// Si on arrive jusqu'ici, renvoyer vrai
		return true ;
	}

}
