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

/**
 * Classe permettant de déplacer un point d'ancrage d'un lien
 */
public class CDeplacerPointAncrage extends CommandeAnnulable 
{
	/**
	 * Listes des points qui constitue le lien brisé
	 */
	private Vecteur pointAncrage;
	private Vecteur newPointAncrage;
	
	
	public CDeplacerPointAncrage(Vecteur pointAncrage, Vecteur newPointAncrage)
	{
	  this.pointAncrage = pointAncrage;
	  this.newPointAncrage = newPointAncrage;
	}
	
	/**
	* Retourne le nom de la commande.
	*/
	public String getNomEdition()
	{
		return "Deplacer point ancrage";
	}

	/**
	* Déplace le point d'ancrage.
	*/
	public boolean executer()
	{
	  // création d'un vecteur temporaire
	  Vecteur temp = new Vecteur(this.pointAncrage);
	  
	  // modifier le point qui a été changé dans le diagramme
	  // sauvegarder les anciens pour pouvoir annuler la commande
	  this.pointAncrage.x = newPointAncrage.x;
	  this.pointAncrage.y = newPointAncrage.y;
	  this.newPointAncrage.x = temp.x;
	  this.newPointAncrage.y = temp.y;
	  
	  return true;
	}
}
