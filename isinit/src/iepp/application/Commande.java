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

/**
 * Interface indiquant le comportement d'une commande de l'outil
 */
public interface Commande
{
	/**
	 * Une commande doit pouvoir s'ex�cuter et indiquer si elle s'est bien d�roul�e ou non
	 * @return true si la commande s'est ex�cut�e correctement
	 */
	public boolean executer() ;
	
	/**
	 * Doit indique de quel type elle est, annulable ou pas
	 * @return true si la commande est annulable
	 */
	public boolean estAnnulable() ;
	
}
