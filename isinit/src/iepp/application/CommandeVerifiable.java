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

import iepp.application.averification.VResultatVerif;

/**
 *
 */
public abstract class CommandeVerifiable extends CommandeAnnulable
{
	/**
	 * M�thode executer red�finie. La m�thode appelle les m�thodes lancer() et verifier().
	 * La m�thode est finale pour ne pas �tre red�finie par m�garde.
	 * @see iepp.application.Commande#executer()
	 * @return true si la commande a r�ussi � s'ex�cuter
	 */
	public final boolean executer ()
	{
		boolean resultatCommande = this.lancer() ;
		this.verifier() ;
		// Afficher le r�sultat de la commande avant de quitter
		// Pour cela, il faudrait fournir aux CommandeVerifiable l'objet charg� de l'affichage
		// en direct (ou au moins un objet capable de stocker les donn�es) dans le constructeur
		// La m�thode executer devrait �tre modifi�e pour utiliser cet objet 
		return resultatCommande ;
	}
	
	/**
	 * Ex�cute le traitement r�el de la commande.
	 * @return true si la commande a r�ussi � s'ex�cuter
	 */
	public abstract boolean lancer() ;
	
	/**
	 * Renvoie un rapport sur la situation des objets concern�s par modification entra�n�e
	 * par la commande. Cette m�thode doit appeler les m�thodes de v�rification corespondant
	 * aux objets modifi�s et remettre un rapport.
	 * @return le rapport sur l'ex�cution de la commande (erreurs ou avertissements par
	 * rapport au processus mod�lis�).
	 */
	public abstract VResultatVerif verifier() ;
	
	/**
	 * Renvoie un r�sultat de v�rification avant que la commande ne soit ex�cut�e, juste
	 * apr�s sa cr�ation. Ceci permet de savoir si la commande va marcher sans l'ex�cuter.
	 * @return un rapport sur la possibilit� d'ex�cution de la commande
	 */
	public abstract VResultatVerif preverifier() ;
}
