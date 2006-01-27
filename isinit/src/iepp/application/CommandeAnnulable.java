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
 * Classe abstraite impl�mentant Commande, elle indique uniquement le type de la commande
 */
public abstract class CommandeAnnulable implements Commande
{
	
	/**
	 * Les commande sont annulables
	 * return true tout le temps
	 */
	public boolean estAnnulable()
	{
		return true ;
	}
	
	/**
	 * M�thode appel�e par GestAnnulation lors de l'annulation
	 */
	//TODO Passer la m�thode abstraite
	public void defaire()
	{
	}
	
	/**
	 * M�thode appel�e par GestAnnulation lors de la r�cup�ration d'une annulation
	 */
	//TODO Passer la m�thode abstraite
	public void refaire()
	{
	}
	
}
