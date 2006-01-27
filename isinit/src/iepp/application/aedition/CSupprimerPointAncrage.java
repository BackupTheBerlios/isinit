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
import iepp.ui.iedition.dessin.vues.MDLien;

/**
 * Classe permettant de supprimer un point d'ancrage sur un lien brisé
 */
public class CSupprimerPointAncrage extends CommandeAnnulable
{
	/**
	 * Lien auquel on enlève un point d'ancrage
	 */
	private FLien lien;
	
	/**
	 * Coordonnée du point d'ancrage à supprimer
	 */
	private Vecteur p;
	
	/**
	 * Indice du point à supprimer dans la liste des points du lien
	 */
	int index;

	public CSupprimerPointAncrage(FLien lien, Vecteur p)
	{
	  this.lien = lien;
	  this.p = p;
	  this.index = lien.getIndexPointAncrage(p);
	}

	/**
	* Retourne le nom de la commande
	*/
	public String getNomEdition()
	{
	  return "Supprimer point ancrage";
	}

	/**
	 * La commande renvoie si elle s'est bien passée ou non
	 * Empêche l'utilisateur d'enlever le premier et le dernier point d'ancrage
	 * ceux qui font aussi partie des handles de la figure
	 * @return true si la suppression s'est bien passée false sinon
	 */
	public boolean executer()
	{
	 	if ((((MDLien)this.lien.getModele()).getPointsAncrage().indexOf(p) == 0 )
	 		|| (((MDLien)this.lien.getModele()).getPointsAncrage().indexOf(p) == ((MDLien)this.lien.getModele()).getPointsAncrage().size() - 1))
		
	 	{
	 		return false;
	 	}
	 	else
	 	{
		  	this.lien.detruirePointAncrage(p);
		  	return true;
	 	}
	}

}
