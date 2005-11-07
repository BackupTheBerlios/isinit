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

import java.util.Vector;

import util.Vecteur;

import iepp.application.CommandeNonAnnulable;
import iepp.ui.iedition.VueDPGraphe;
import iepp.ui.iedition.dessin.rendu.FComposantProcessus;
import iepp.ui.iedition.dessin.rendu.FElement;
import iepp.ui.iedition.dessin.rendu.FProduit;
import iepp.ui.iedition.dessin.rendu.liens.FLien;
import iepp.ui.iedition.dessin.rendu.liens.FLienInterface;
import iepp.ui.iedition.dessin.vues.MDElement;
import iepp.ui.iedition.dessin.vues.MDLien;


/**
 * Commande annulable permettant de lier 2 éléments (1 produit et un composant) avec un lien
 * en pointillé. Cette commande n'est pas appelée directement après un évènement utilisateur.
 * Elle n'est appelée que lorsqu'un composant est ajouté au diagramme
 * ou lorsqu'on supprime un lien fusion, ce lien est remplacé par le lien interface
 * d'origine
 */
public class CLierInterface extends CommandeNonAnnulable
{

	/**
	 * Diagramme sur lequel on effectue la liaison
	 */
	private VueDPGraphe diagramme;
	
	/**
	 * Lien permettant de lier un composant à un produit
	 */
	private FLien lien;
	
	/**
	* Constructeur de la commande, créé un lien entre un composant et un produit simple
	* Créer une nouvelle figure pour le lien
	* @param d diagramme sur lequel on effectue la liaison
	* @param l lien à créer entre le composant et le produit
	* @param source figure source d'où pour le lien
	* @param destination figure où arrive le lien
	* @param pointsAncrageIntermediaires liste des points d'ancrages du lien à créer
	*/
	public CLierInterface(VueDPGraphe d, FLien l, FElement source, FElement destination, Vector pointsAncrageIntermediaires)
	{
		this.diagramme = d;
		this.lien = l;
		((MDLien) lien.getModele()).setSource((MDElement) source.getModele());
		((MDLien) lien.getModele()).setDestination((MDElement) destination.getModele());
		this.lien.setSource(source);
		this.lien.setDestination(destination);
		this.lien.initHandles();

		// ajouter ce lien interface à la figure produit pour pouvoir la supprimer par la suite
		if ( source instanceof FProduit )
		{
			((FProduit)source).setLienIterface((FLienInterface)this.lien);
			((FProduit)source).setComposantInterface((FComposantProcessus)destination);
		}
		else
		{
			((FProduit)destination).setLienIterface((FLienInterface)this.lien);
			((FProduit)destination).setComposantInterface((FComposantProcessus)source);
		}
		
		for (int i = pointsAncrageIntermediaires.size()-1; i >= 0; i--)
		{
		  Vecteur p = (Vecteur) pointsAncrageIntermediaires.elementAt(i);
		  this.lien.creerPointAncrage(p, 1);
		}
	}

	/**
	* Retourne le nom de l'édition.
	*/
	public String getNomEdition()
	{
		return "Lier element";
	}

	/**
	 * Renvoie le lien en cours d'initialisation
	 * @return le lien créé
	 */
	public FLien getLien()
	{
		return this.lien;
	}
	
	
	/**
	 * La commande renvoie toujours true
	 * Ajoute le nouveau lien créé entre le composant et le produit
	 * @return true 
	 */
	public boolean executer()
	{
		diagramme.ajouterFigure(lien);
		return true;
	}
}
