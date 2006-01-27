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
 
package iepp;

import java.io.Serializable;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import iepp.ui.* ;
import iepp.ui.iedition.* ;
import iepp.ui.iedition.dessin.vues.MDDiagramme;
import iepp.domaine.* ;
import iepp.domaine.adaptateur.*;

/**
 * Construit le projet � �diter dans le logiciel
 */
public class Projet implements Serializable, Observer
{

	/**
	 * Lien vers la fen�tre d'�dition qui contient le graphe
	 */
	private FenetreEdition fenEdit = null ;
	
	/**
	 * Lien vers la d�finition de processus qui est �dit�e dans le logiciel
	 */
	private DefinitionProcessus defProc = null ;
	
	
	/**
	 * Nombre maximal de commandes annulables
	 */
	private static final int NBCOMMANDES = 5 ;
	
	/**
	 * Gestionnaire des undo/redo
	 */
	//private VGestAnnulation gestionnaireAnnulation ;
	
	/**
	 * Permet de nommer les �l�ments cr��s
	 */
	private static int nbSansNom = 0 ;
	
	/**
	 * Indique si le projet a �t� modifi� depuis la derni�re sauvegarde ou le chargement
	 */
	private boolean modifie = false ;
	
	
	/**
	 * Cr�er un projet avec l'ensemble des fen�tres li�es au projet
	 *
	 */	
	public Projet()
	{
		// cr�ation d'une d�finition de processus vide
		this.defProc = new DefinitionProcessus() ;
		this.defProc.addObserver(this);
		// cr�ation d'un adaptateur qui fournira les donn�es � la vue
		AdaptateurDPArbre adaptateur = new AdaptateurDPArbre(defProc);
		// Cr�ation vue arbre
		VueDPArbre vue = new VueDPArbre(adaptateur);
		// cr�ation du gestionnaire d'annulation
		//this.gestionnaireAnnulation = new VGestAnnulation(NBCOMMANDES);
		// afficher la vue sur la fenetre principale
		Application.getApplication().getFenetrePrincipale().enregistrerVueArbre (vue) ;
		// Cr�er la fen�tre d'�dition et la vue du graphe
		this.fenEdit = new FenetreEdition (new VueDPGraphe (defProc)) ;
		Application.getApplication().getFenetrePrincipale().setPanneauGenerique(this.fenEdit) ;
		this.rafraichirLangue();
		Projet.nbSansNom = 0;
		this.setModified(true) ;
	}
	
	/**
	 * Constructeur de projet � partir d'�l�ments sauvegard�s
	 * @param dp d�finition de processus sauvegard�
	 * @param diag diagramme sauvegard�
	 * @param elements	liste des �l�ments du diagramme
	 * @param liens liste des liens pr�sents sur le diagramme
	 * @param dernierIdUtilise dernier id utilis� pour la dp charg�e, permet de continuer l� o� on s'est arr�t�
	 */
	public Projet(DefinitionProcessus dp, MDDiagramme diag, Vector elements , Vector liens, int dernierIdUtilise)
	{
		this.defProc = dp;
		this.defProc.addObserver(this);
		// cr�ation d'un adaptateur qui fournira les donn�es � la vue
		AdaptateurDPArbre adaptateur = new AdaptateurDPArbre(defProc);
		// Cr�ation vue arbre
		VueDPArbre vue = new VueDPArbre(adaptateur);
		// cr�ation du gestionnaire d'annulation
		//this.gestionnaireAnnulation = new VGestAnnulation(NBCOMMANDES);
		// afficher la vue sur la fenetre principale
		Application.getApplication().getFenetrePrincipale().enregistrerVueArbre (vue) ;
		// Cr�er la fen�tre d'�dition et la vue du graphe
		VueDPGraphe diagr = new VueDPGraphe (this.defProc);
		diagr.setModele(diag);
		diagr.setElements(elements);
		diagr.setLiens(liens);
		this.fenEdit = new FenetreEdition (diagr) ;
		Application.getApplication().getFenetrePrincipale().setPanneauGenerique(this.fenEdit) ;
		this.rafraichirLangue();
		Projet.nbSansNom = dernierIdUtilise;
		this.setModified(false) ;
	}
	
	
	public static String getNouveauNom()
	{
		String retour =  "noname" + Projet.nbSansNom ;
		Projet.nbSansNom += 1;
		return retour;
	}
	
	/**
	 * Renvoie le dernier ID utilis� pour les nouveaux �l�ments
	 * @return un id
	 */
	public static int getDernierId()
	{
		return Projet.nbSansNom;
	}
	
	/**
	 * R�cup�re le lien vers la fen�tre d'�dition
	 * @return la fenetre d'edition
	 */
	public FenetreEdition getFenetreEdition()
	{
		return this.fenEdit ;
	}
	
	/**
	 * Modification de la fen�tre d'�dition
	 * @param FE, Nouvelle fen�tre d'�dition
	 */
	public void setFenetreEdition(FenetreEdition FE)
	{
		this.fenEdit = FE;
	}
	
	/**
	 * R�cup�re la d�finition de processus courante de l'application
	 * @return
	 */
	public DefinitionProcessus getDefProc()
	{
		return this.defProc ;
	}
	
	/*
	public boolean estAnnulerVide()
	{
		return this.gestionnaireAnnulation.estAnnulerVide();
	}
	
	public boolean estRefaireVide()
	{
		return this.gestionnaireAnnulation.estRefaireVide();
	}
	*/
	
	/**
	 * Indique si le projet a �t� modifi� et doit �tre sauvegard�
	 */
	public boolean estModifie()
	{
		return this.modifie ;
	}
	
	public void setModified(boolean modif)
	{
		this.modifie = modif ;
		if (modif)
		{
			Application.getApplication().getFenetrePrincipale().setTitle(
				Application.getApplication().getConfigPropriete("titre")
				+ " " + Application.getApplication().getReferentiel().getNomReferentiel()
				+ "::" + this.defProc.getNomDefProc() + " *" );
		}
		else
		{
			Application.getApplication().getFenetrePrincipale().setTitle(
					Application.getApplication().getConfigPropriete("titre")
					+ " " + Application.getApplication().getReferentiel().getNomReferentiel()
					+ "::" + this.defProc.getNomDefProc());
		}
		Application.getApplication().getFenetrePrincipale().majEtat();
	}
	
	
	/**
	 * Modifie la d�finition processus courante
	 * @param DP, nouvelle d�finition processus
	 */
	public void setDefProc(DefinitionProcessus DP)
	{
		defProc = DP;
		this.setModified(true) ;
		this.defProc.addObserver(this);
	}
	
	/**
	 * Modifie la d�finition processus courante
	 * @param DP, nouvelle d�finition processus
	 */
	public void deleteDefProc()
	{
		defProc = null;
		Application.getApplication().getFenetrePrincipale().setTitle(
							Application.getApplication().getConfigPropriete("titre"));
	}
	
	/**
	* Modifie la fen�tre d'�dition courante
	* @param FE, fen�tre �dition
    */
	public void setFenEdit(FenetreEdition FE)
	{
		fenEdit = FE;
	}
	
	
	/**
	 * Demande de rafraichissement de toutes les fenetres g�r�es par le projet
	 *
	 */
	public void rafraichirLangue()
	{
		this.fenEdit.rafraichirLangue();
	}


	public void update(Observable o, Object arg)
	{
		this.setModified(true) ;
	}
}
