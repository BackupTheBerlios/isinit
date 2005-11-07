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
 * Commande non annulable permettant d'ajouter un composant dans la définition de processus courante
 */
public class CAjouterComposantVide extends CommandeNonAnnulable
{

	/**
	 * Ajoute un composant dans la définition de processus
	 * @see iepp.application.Commande#executer()
	 * @return true si le composant a été ajouté
	 */
	public boolean executer()
	{
		// on crée notre composant de processus vide
		ComposantProcessus compProc = new ComposantProcessus (Application.getApplication().getProjet().getDefProc()) ;
		compProc.setVide(true);
		compProc.setNomComposant(Projet.getNouveauNom());
		// ajouter le composant créé à la définition de processus du projet courant
		Application.getApplication().getProjet().getDefProc().ajouterComposant(compProc) ;
		// Ajouter le composant au référentiel (création de la structure logique et du répertoire,
		// sans rien enregistrer dedans)
		Referentiel ref = Application.getApplication().getReferentiel() ;
		long id = ref.ajouterElement (compProc.getNomComposant(), ElementReferentiel.COMPOSANT_VIDE) ;
		if (id < 0)
			return false ;
		// Ajouter au référentiel l'association id-référence
		ref.ajouterReferenceMemoire (compProc, id) ;
		// Sauver réellement le composant
		ref.sauverComposantVide (compProc) ;
               return true ;
	}
}
