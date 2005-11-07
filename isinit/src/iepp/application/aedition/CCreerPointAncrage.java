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
 
package iepp.application.aedition;

import util.Vecteur;
import iepp.application.CommandeAnnulable;
import iepp.ui.iedition.dessin.rendu.liens.FLien;

/**
 * Classe permettant d'ajouter un point d'ancrage sur un lien
 */
public class CCreerPointAncrage extends CommandeAnnulable 
{
	/**
	 * Lien sur lequel on va cr�er un point d'ancrage
	 */
	private FLien lien;
	
	/**
	 * Coordonn� du point d'ancrage � creer
	 */
	private Vecteur p;


	/**
	 * Construit un point d'ancrage sur un lien, poign�e disponible pour modifier le lien
	 * @param lien, lien sur lequel on veut ajouter un point d'ancrage
	 * @param p, coordonn�es du point d'ancrage � cr�er
	 */
	public CCreerPointAncrage(FLien lien, Vecteur p)
	{
	  this.lien = lien;
	  this.p = p;
	}

	/**
	* Retourne le nom de la commande
	*/
	public String getNomEdition()
	{
	  return "Creer point ancrage";
	}
	
	
	public boolean executer()
	{
	  this.lien.creerPointAncrage(p);
	  return true;
	}
}
