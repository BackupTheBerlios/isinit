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

package iepp.application.aedition.annulation;

/**
 * Classe Memento qui permet de m�moriser � un instant donn�
 * l'�tat d'un objet annulable 
 */
public class Memento
{
	/**
	 * AJOUTER COMMENTAIRES avec @ param
	 */
	private ObjetAnnulable objetSauve;
	private String etat;
	
	/**
	 * Constructeur de l'obet memento avec en param�tre la r�f�rence de l'objet � m�moriser
	 */
	public Memento(ObjetAnnulable objetSauve)
	{
		this.objetSauve = objetSauve;
	}
	
	/**
	 * M�thode permettant la sauvegarde de l'�tat de l'objet
	 */
	public void sauverEtat(String etat)
	{
		this.etat = etat;
	}
	
	/**
	 * M�thode permettant la restauration de l'objet
	 */
	public void restaurerEtat()
	{
		objetSauve.restaurer(etat);
	}
	
}