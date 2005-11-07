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

package iepp.application.averification;

import java.util.Vector;

/**
 * Contient le r�sultat d'une v�rification (erreurs, avertissements).
 */
public class VResultatVerif
{
	private String titre = "" ;	// Titre de la v�rification

	private Vector listeErr = new Vector() ;	// Liste d'erreurs
	private Vector listeAvert = new Vector() ;	// Liste d'avertissements

	// Ces constantes permettent de d�finir des niveaux d'erreur
	// Elles doivent rester dans l'ordre croissant, ce qui permet d'�crire dans le programme
	// if (niveau < ResultatVerif.ERREUR) ...)
	/** Des erreurs se sont produites */
	public static int ERREUR = 2 ;
	/** Des avertissmeents ont �t� �mis (mais pas d'erreur)*/
	public static int AVERTISSEMENT = 1 ;
	/** Ni avertissement ni erreur*/
	public static int OK = 0 ;


	/**
	 * @param titre titre de la v�rification effectu�e
	 */
	public VResultatVerif (String titre)
	{
		this.titre = titre ;
	}

	/**
	 * Ajoute un message d'erreur au r�sultat.
	 * @param msg message d'erreur � ajouter
	 */
	public void ajouterErr (String msg)
	{	
		this.listeErr.add (msg) ;
	}
	
	/**
	 * Ajoute un message d'avertissement au r�sultat.
	 * @param msg message d'avertissement � ajouter
	 */
	public void ajouterAvert (String msg)
	{	
		this.listeAvert.add (msg) ;
	}
	
	/**
	 * Renvoie le titre de la v�rification effectu�e pour arriver � ce r�sultat.
	 */
	public String getTitre ()
	{
		return this.titre ;
	}

	/**
	 * Renvoie la liste des messages d'erreur du r�sultat.
	 * @return la liste de messages d'erreur
	 */
	public Vector recupErr ()
	{
		return (Vector) this.listeErr.clone() ;
	}
	
	/**
	 * Renvoie la liste des messages d'avertissement du r�sultat.
	 * @return la liste des messages d'avertissement
	 */
	public Vector recupAvert ()
	{
		return (Vector) this.listeAvert.clone() ;
	}
	
	/**
	 * Renvoie le nombre de messages d'erreur du r�sultat
	 */
	public int nbErr ()
	{
		return this.listeErr.size() ;
	}

	/**
	 * Renvoie le nombre de messages d'avertissement du r�sultat 
	 */
	public int nbAvert ()
	{
		return this.listeAvert.size() ;
	}
	
	/**
	 * Revoie le niveau d'erreur du r�sultat. Le niveau doit �tre compar� aux constantes
	 * publiques de la classe et non utilis� lui-m�me. Une seul principe restera n�cessairement
	 * stable dans l'attribution des niveaux d'erreur : plus l'erreur est grave, plus
	 * le num�ro qui lui est attribu� est important (ok < avertissement < erreur), ce qui
	 * permet d'�crire dans le programme : if (niveau < ResultatVerif.ERREUR) ...
	 * @return la valeur du niveau d'erreur atteint
	 */
	public int niveauErreur ()
	{
		if (!this.listeErr.isEmpty())
			return VResultatVerif.ERREUR ;
		if (!this.listeAvert.isEmpty())
			return VResultatVerif.AVERTISSEMENT ;
		// Si aucun message, renvoyer OK
		return VResultatVerif.OK ;
		
	}
	
}
