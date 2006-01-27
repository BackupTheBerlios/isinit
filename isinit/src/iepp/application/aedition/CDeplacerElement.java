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

import iepp.application.CommandeAnnulable;
import iepp.ui.iedition.VueDPGraphe;
import iepp.ui.iedition.dessin.rendu.ComposantCell;
import iepp.ui.iedition.dessin.rendu.Figure;
import iepp.ui.iedition.dessin.rendu.ProduitCell;
import iepp.ui.iedition.dessin.rendu.ProduitCellFusion;
import iepp.ui.iedition.dessin.rendu.TextCell;

import java.util.Enumeration;
import java.util.Vector;

import util.Vecteur;

/**
 * Commande annulable permettant de d�placer un �l�ment (produit, lien...) sur un diagramme
 */
public class CDeplacerElement extends CommandeAnnulable
{

	/**
	 * Diagramme sur lequel on applique le d�placement
	 */
	private VueDPGraphe diagramme;
	
	/**
	 * Liste des �l�ments s�lectionn�s qui doivent �tre d�plac�s
	 */
	private Vector selection;
	
	/**
	 * Propri�t� de la translation � effectuer
	 */
	private Vecteur translation;
	

	/**
	* Construction d'une commande permettant de d�placer un �l�ment
	* @param d, diagramme sur lequel on veut d�placer l'�l�ment
	* @param v, propri�t�s du d�placement � effectuer
	*/
	public CDeplacerElement(VueDPGraphe d, Vecteur v)
	{
		// garder un lien vers le diagramme
		this.diagramme = d ;
		
		this.diagramme.clearSelection();
		
		// initialiser les �l�ments � d�placer
		this.selection = new Vector();
		
		for(int i= 0;i<diagramme.getSelectionCells().length;i++){
			Object obj = diagramme.getSelectionCells()[i];
			selection.addElement(obj);
			
			if(obj instanceof ComposantCell){
				this.diagramme.selectionneFigure((Figure)((ComposantCell)obj).getFcomp());
			}else if(obj instanceof ProduitCell){
				this.diagramme.selectionneFigure((Figure)((ProduitCell)obj).getFprod());
			}else if(obj instanceof ProduitCellFusion){
				this.diagramme.selectionneFigure((Figure)((ProduitCellFusion)obj).getFprod());
			}else if(obj instanceof TextCell){
				this.diagramme.selectionneFigure((Figure)((TextCell)obj).getFnote());
			}
		}
		
		/*Enumeration e = diagramme.selectedElements();
		// parcourir la liste des �l�ments s�lectionn�s
		while( e.hasMoreElements() )
		{
			// ajouter chacun de ces �l�ments dans la liste des �l�ments � d�placer
			selection.addElement(e.nextElement());
		} 
		*/
		// r�cup�rer les propri�t�s du d�placement
		this.translation = v;
	}

	/**
	* Retourne le nom de la commande
	*/
	public String getNomEdition()
	{
		return "Deplacer element";
	}

	/**
	* Translate l'�l�ment par -(translation).
	*/
	private void annule(Object p)
	{
		Figure figure;
		if(p instanceof ComposantCell){
			figure = (Figure) ((ComposantCell)p).getFcomp();
		}else if(p instanceof ProduitCell){
			figure = (Figure) ((ProduitCell)p).getFprod();
		}else if(p instanceof ProduitCellFusion){
			figure = (Figure) ((ProduitCellFusion)p).getFprod();
		}else if(p instanceof TextCell){
			figure = (Figure) ((TextCell)p).getFnote();
		}else{
			return;
		}
		
		Vecteur v = new Vecteur(this.translation);
		v.negate();
		figure.translate(v);
	}
	
	/**
	 * Effectuer le d�placement de l'�l�ment courant
	 * @param p, figure � d�placer sur le diagramme
	 * @return, true si le d�placement a �t� effectu�
	 */
	private boolean execute(Object p)
	{
		// r�cup�rer la figure � d�placer
		Figure figure;
		if(p instanceof ComposantCell){
			figure = (Figure) ((ComposantCell)p).getFcomp();
		}else if(p instanceof ProduitCell){
			figure = (Figure) ((ProduitCell)p).getFprod();
		}else if(p instanceof ProduitCellFusion){
			figure = (Figure) ((ProduitCellFusion)p).getFprod();
		}else if(p instanceof TextCell){
			figure = (Figure) ((TextCell)p).getFnote();
		}else{
			return false;
		}
		
		// d�placer la figure
		figure.translate(this.translation);
		return true;
	}


	/**
	* Ex�cute l'�dition sur tous les objets de la s�lection.
	*/
	public boolean executer()
	{
		// r�cup�rer la liste des �l�ments s�lectionn�s
		Enumeration e = this.selection.elements();
		// parcourir cette liste
		if (translation.x == 0 && translation.y == 0){
			return false;
		}
		
		while( e.hasMoreElements() )
		{
			// d�placer chaque �l�ment
			execute(e.nextElement());
		}
		return true;
	}

	/**
	* Annule l'�dition sur tous les objets de la s�lection.
	*/
	public void annuler()
	{
		Enumeration e = selection.elements();
		// pour tous les �l�ments d�plac�s, annuler le d�placement
		while( e.hasMoreElements() )
		{
			annule(e.nextElement());
		}
	}

}
