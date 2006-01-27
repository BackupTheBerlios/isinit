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

package iepp.domaine.adaptateur;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.*;

import iepp.Application;
import iepp.domaine.*;

/**
 * Classe adapteur permettant de faire l'interm�diaire entre la vue (arbre) et les donn�es � afficher
 */
public class AdaptateurDPArbre implements TreeModel, Observer
{

	/**
	 * Lien vers les donn�es � mette � disposition de la vue
	 */
	private DefinitionProcessus defProc ;
	
	/**
	 * Liste des ecouteurs de l'adaptateur, notamment la vue
	 */
	private EventListenerList ecouteursModele ;
	

	
	/**
	 * Construction d'un adaptateur permettant d'acc�der aux donn�es du domaine
	 * @param defProc, d�finition de processus � afficher dans l'arbre
	 */
	public AdaptateurDPArbre( DefinitionProcessus defProc )
	{
		// r�cup�rer le processus � afficher
		this.defProc = defProc ;
		// indiquer � la d�finition que l'adapteur ecoute les �ventuelles modifications des donn�es
		this.defProc.addObserver(this);
		// cr�er une nouvelle liste d'�couteurs (l'arbre par exemple) de l'adapteur
		this.ecouteursModele = new EventListenerList();
	}


	//------------------------------------------------------------------
	//						Implemente TreeModel
	//------------------------------------------------------------------
	
	/**
	 * Renvoie la racine de l'arbre, l'id de l'objet modele Definition de Processus
	 * @return l'Id de la d�finition de processus
	 */
	public Object getRoot()
	{
		return this.defProc.getIdDefProc();
	}

	/**
	 * Renvoie le nombre de fils de l'objet courant obj
	 */
	public int getChildCount(Object obj)
	{
		// renvoyer le nombre de fils qui ont pour parent obj
		return ((IdObjetModele)obj).getNbFils();
	}

	/**
	 * Indique si l'objet courant obj est une feuille de l'arbre ou non
	 */
	public boolean isLeaf(Object obj)
	{
		return (((IdObjetModele)obj).getNbFils() == 0 );
	}

	/**
	 * M�thode appel�e par les �couteurs de l'adapteur pour s'enregistrer aupr�s de lui
	 * @param ecouteur, ecouteur de l'adapteur (donc du mod�le)
	 */
	public void addTreeModelListener(TreeModelListener ecouteur)
	{
		this.ecouteursModele.add(TreeModelListener.class, ecouteur);

	}

	/**
	 * M�thode permettant de supprimer un �couteur dans la liste des 
	 * �couteurs de l'adapteur
	 * @param ecouteur, ecouteur de l'adapteur � supprimer
	 */
	public void removeTreeModelListener(TreeModelListener ecouteur)
	{
		this.ecouteursModele.remove(TreeModelListener.class, ecouteur);
	}

	/**
	 * Renvoie le ieme fils d'un objet parent
	 * @param parent, objet dont on recherche un fils
	 * @param ieme, num�ro du fils que l'on recherche
	 * @return l'Id du fils recherch�
	 */
	public Object getChild(Object parent, int ieme)
	{
		return ((IdObjetModele)parent).getFils(ieme);
	}

	/**
	 * Renvoie l'indice auquel se trouve l'enfant d'un parent donn�
	 * @param parent, objet dont on recherche l'indice du fils
	 * @param fils, objet dont on recherche l'indice
	 * @return l'indice du fils recherch� parmis l'ensemble des fils
	 */
	public int getIndexOfChild(Object parent, Object enfant)
	{
		// si le parent est une d�finition de processus
		if (((IdObjetModele)parent).estDefProc())
		{
			// on renvoie l'indice du composant enfant dans la liste des composants
			return (this.defProc.getListeComp().indexOf(enfant));
		}
		// sinon l'indice est directement trouv� dans l'IdobjetModele
		return ((IdObjetModele)parent).getNumRang();
	}

	/**
	 * M�thode appel�e lorsque l'utilisateur � modifier la valeur d'un item identifi�
	 * par path pour une nouvelle valeur newValue, si la nouvelle valeur est valide,
	 *  on prend en compte la modification
	 */
	public void valueForPathChanged(TreePath path, Object valeur)
	{
		
	}
	
	//------------------------------------------------------------------
	//						Implemente Observer
	//------------------------------------------------------------------
	
	/**
	 * M�thode appel�e par le mod�le lorsque celui-ci a �t� modifi�
	 * @param observe, objet modifi� ayant appel� la m�thode
	 * @param param, objet pass� en param�tre
	 */
	public void update(Observable observe, Object param)
	{
		// Peu elegant, peu performant, mais beaucoup plus simple
		// que les treemodelevent, etc...
		Application.getApplication().getFenetrePrincipale().getVueDPArbre().updateUI();
	}
}
