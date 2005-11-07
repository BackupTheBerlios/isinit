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
package iepp.application.averification;

import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import iepp.Application;
import iepp.application.CommandeNonAnnulable;
import iepp.domaine.ComposantProcessus;
import iepp.domaine.DefinitionProcessus;
import iepp.domaine.IdObjetModele;
import iepp.domaine.LienProduits;


/**
 * Commande lan�ant diff�rentes v�rifications sur le processus en cours de d�finition. 
 */
public class CVerification extends CommandeNonAnnulable
{

	private Vector listeRes ;	// Vecteur contenant la liste de r�sultats

	/**
	 * Lance la commande de v�rification g�n�rale de la d�finition de processus.
	 * La commande remplit une liste avec les r�sultats obtenus ; ces r�sultats peuvent
	 * �tre r�cup�r�s gr�ce � la commande getResultats().
	 * Une commande doit pouvoir s'ex�cuter et indiquer si elle s'est bien d�roul�e ou non
	 * @return true (sauf une �ventuelle exception, la commande s'ex�cute toujours bien)
	 */
	public boolean executer()
	{
		// Lancer les v�rifications et noter les r�sultats
		this.listeRes = new Vector() ;
		VGestVerification gestVerif = new VGestVerification() ;
		// (noms de composants uniques)
		this.listeRes.add (gestVerif.nomsComposantsUniques()) ;
		this.recupererListe();
		// La commande s'ex�cute toujours bien
		return true ;
	}
	
	public boolean estLie(IdObjetModele id, Vector liens)
	{
		for (int k=0; k < liens.size() ; k++)
		{
			LienProduits lienCourant = (LienProduits) liens.elementAt(k);
			
			if (lienCourant.getProduitEntree().equals(id) || lienCourant.getProduitSortie().equals(id))
			{
				return true;
			}
		}
		return false;
	}

	public void recupererListe()
	{
		Vector listeProdEntree = new Vector();
		Vector listeProdSortie = new Vector();
		Vector listeComposants = new Vector();
		
		
		DefinitionProcessus defProc = Application.getApplication().getProjet().getDefProc();
		for (int i = 0; i < defProc.getListeComp().size(); i++)
		{
			IdObjetModele id = (IdObjetModele) defProc.getListeComp().elementAt(i);
			ComposantProcessus cp = (ComposantProcessus)id.getRef();
		
			int nbInterfaceNonResolues = 0 ;
			
			Vector liens = cp.getLien();
			// r�cup�rer la liste des produits en entr�e qui ne sont pas li�s
			Vector produitEntree = cp.getProduitEntree();
			
			for (int j = 0; j < produitEntree.size();j++)
			{
				IdObjetModele idproduits = (IdObjetModele)produitEntree.elementAt(j);
				if ( !this.estLie(idproduits, liens))
				{
					listeProdEntree.addElement(idproduits);
					nbInterfaceNonResolues++;
				}
			}
			
			
			// r�cup�rer la liste des produits en sortie qui ne sont pas li�s
			Vector produitSortie = cp.getProduitSortie();
			for (int j = 0; j < produitSortie.size();j++)
			{
				IdObjetModele idproduits = (IdObjetModele)produitSortie.elementAt(j);
				if ( !this.estLie(idproduits, liens))
				{
					listeProdSortie.addElement(idproduits);
					nbInterfaceNonResolues++;
				}
			}
			
			if  ( nbInterfaceNonResolues != 0 )
			{
				listeComposants.addElement(id);
			}
		}
		
		if (listeProdEntree.size() != 0 || listeProdSortie.size() != 0 || listeComposants.size() != 0 )
		{
			// afficher le panneau de v�rification
			Application.getApplication().getFenetrePrincipale().afficherPanneauVerification(listeProdEntree, listeProdSortie, listeComposants);    
		}
		else
		{
        	Application.getApplication().getFenetrePrincipale().getPanneauVerif().clearErrorArea();
        	 JOptionPane.showMessageDialog(Application.getApplication().getFenetrePrincipale(),Application.getApplication().getTraduction("projet_valide"),
	 				Application.getApplication().getTraduction("Valider"),
					JOptionPane.INFORMATION_MESSAGE);
		}
	}


	/**
	 * Renvoie un vecteur contenant les r�sultats de la v�rification. le vecteur
	 * contient une s�rie d'objets VResultatVerif indiquant les messages obtenus.
	 * @return un vecteur contenant les r�sultats de la v�rification
	 */
	public Vector getResultats()
	{	
		return (Vector) this.listeRes.clone() ;
	}

}
