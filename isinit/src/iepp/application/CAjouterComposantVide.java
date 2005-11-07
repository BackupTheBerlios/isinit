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

import iepp.* ;
import iepp.application.areferentiel.ElementReferentiel;
import iepp.application.areferentiel.Referentiel;
import iepp.domaine.* ;

/**
 * Commande non annulable permettant d'ajouter un composant dans la d�finition de processus courante
 */
public class CAjouterComposantVide extends CommandeNonAnnulable
{

	/**
	 * Ajoute un composant dans la d�finition de processus
	 * @see iepp.application.Commande#executer()
	 * @return true si le composant a �t� ajout�
	 */
	public boolean executer()
	{
		// on cr�e notre composant de processus vide
		ComposantProcessus compProc = new ComposantProcessus (Application.getApplication().getProjet().getDefProc()) ;
		compProc.setVide(true);
		compProc.setNomComposant(Projet.getNouveauNom());
		// ajouter le composant cr�� � la d�finition de processus du projet courant
		Application.getApplication().getProjet().getDefProc().ajouterComposant(compProc) ;
		// Ajouter le composant au r�f�rentiel (cr�ation de la structure logique et du r�pertoire,
		// sans rien enregistrer dedans)
		Referentiel ref = Application.getApplication().getReferentiel() ;
		long id = ref.ajouterElement (compProc.getNomComposant(), ElementReferentiel.COMPOSANT_VIDE) ;
		if (id < 0)
			return false ;
		// Ajouter au r�f�rentiel l'association id-r�f�rence
		ref.ajouterReferenceMemoire (compProc, id) ;
		// Sauver r�ellement le composant
		ref.sauverComposantVide (compProc) ;
               return true ;
	}
}
