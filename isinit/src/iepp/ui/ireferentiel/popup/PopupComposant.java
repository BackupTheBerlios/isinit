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
package iepp.ui.ireferentiel.popup;

import iepp.Application;
import iepp.application.CAjouterComposantDP;
import iepp.application.aedition.CAjouterComposantGraphe;
import iepp.application.areferentiel.CRetirerComposant;
import iepp.application.areferentiel.Referentiel;
import iepp.domaine.ComposantProcessus;

import javax.swing.* ;

import java.awt.event.* ;
import iepp.ui.FenetreVersionComposant;


/**
 * Menu contextuel associ� aux composants dans l'arbre du r�f�rentiel.
 */
public class PopupComposant extends JPopupMenu implements ActionListener
{
	private long idComp ;	// Identifiant du composant concern�
	// Options du menu
	private JMenuItem ajouterComposantDP ;
        private JMenuItem actualiserComposantDP;// modif 2xmi Albert
	private JMenuItem ajouterComposantDiagramme ;
	private JMenuItem retirerComposant ;


	/**
	 * Constructeur.
	 * @param idComp identifiant dans le r�f�rentiel du composant associ� au menu.
	 */
	public PopupComposant (long idComp)
	{
		// Enregistrer les param�tres
		this.idComp = idComp ;
		// Ajouter les �l�ments optionnels au menu
		// (uniquement s'il existe un projet en cours)
		if (Application.getApplication().getProjet() != null)
		{
			this.ajouterComposantDP = new JMenuItem (Application.getApplication().getTraduction("Ajouter_Composant_DP2")) ;
			// DEBUT modif 2xmi Albert
			this.actualiserComposantDP = new JMenuItem (Application.getApplication().getTraduction("Actualiser_Composant_DP2")) ;
			// FIN modif 2xmi Albert
			this.ajouterComposantDiagramme = new JMenuItem (Application.getApplication().getTraduction("Ajouter_Composant")) ;
			this.add (this.ajouterComposantDP) ;
                        this.add(this.actualiserComposantDP) ; // modif 2xmi Albert
			this.add (this.ajouterComposantDiagramme) ;
			this.ajouterComposantDP.addActionListener (this) ;
                        this.actualiserComposantDP.addActionListener (this); // modif 2xmi Albert
			this.ajouterComposantDiagramme.addActionListener (this) ;
		}
		// Cr�er les �l�ments
		this.retirerComposant = new JMenuItem (Application.getApplication().getTraduction("Retirer_Composant_Ref")) ;
		this.add (this.retirerComposant) ;
		// Ecouter les �v�nements
		this.retirerComposant.addActionListener (this) ;
	}


	/** Clics sur les �l�ments du menu.
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed (ActionEvent e)
	{
		Object source = e.getSource() ;
		// Ajout � la d�finition de processus
		if (source == this.ajouterComposantDP)
		{
			if ((new CAjouterComposantDP(this.idComp)).executer())
			{
				Application.getApplication().getProjet().setModified(true);
			}
		}
                // DEBUT modif 2xmi Albert
                // Actualiser le composant � la d�finition de processus
                else if (source == this.actualiserComposantDP)
                {
                        FenetreVersionComposant fVersion = new FenetreVersionComposant(Application.getApplication().getFenetrePrincipale(),idComp);

                }
                // FIN modif 2xmi Albert
		// Ajout au diagramme
		else if (source == this.ajouterComposantDiagramme)
		{
			Referentiel ref = Application.getApplication().getReferentiel() ;
			// Regarder si le composant est d�j� charg� en m�moire
			Object obj = ref.chercherReference (this.idComp) ;
			// S'il n'est pas en m�moire, le charger
			if (obj == null)
			{
				if (!(new CAjouterComposantDP(this.idComp)).executer())
					return ;
				obj = ref.chercherReference (this.idComp) ;
			}
			// R�cup�rer le composant et l'ajouter au diagramme
			ComposantProcessus comp = (ComposantProcessus) obj ;
			if ((new CAjouterComposantGraphe(comp.getIdComposant())).executer())
			{
				Application.getApplication().getProjet().setModified(true);
			}
		}
		// Supprimer le composant du r�f�rentiel
		else if (source == this.retirerComposant)
		{
			(new CRetirerComposant (this.idComp)).executer() ;
		}
	}

}
