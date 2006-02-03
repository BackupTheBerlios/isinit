package iepp.ui.ireferentiel.popup;

import iepp.Application;
import iepp.application.CAjouterComposantDP;
import iepp.application.aedition.CAjouterComposantGraphe;
import iepp.application.areferentiel.CAjouterScenarioRef;
import iepp.application.areferentiel.CRetirerComposant;
import iepp.application.areferentiel.ElementReferentiel;
import iepp.application.areferentiel.Referentiel;
import iepp.domaine.ComposantProcessus;
import iepp.domaine.DefinitionProcessus;
import iepp.ui.FenetreCreerScenario;
import iepp.ui.FenetreVersionComposant;
import iepp.ui.iedition.VueDPGraphe;
import iepp.ui.iedition.dessin.GraphModelView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class PopupScenario extends JPopupMenu implements ActionListener{

	private JMenuItem ajouterScen ;
	
	public PopupScenario ()
	{
		// Ajouter les éléments optionnels au menu
		// (uniquement s'il existe un projet en cours)
		if (Application.getApplication().getProjet() != null)
		{
			this.ajouterScen = new JMenuItem (Application.getApplication().getTraduction("Ajouter_Scenario")) ;
			this.add (this.ajouterScen) ;
			this.ajouterScen.addActionListener (this) ;               
		}
	}


	/** Clics sur les éléments du menu.
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed (ActionEvent e)
	{
		Object source = e.getSource() ;
		// Ajout à la définition de processus
		if (source == this.ajouterScen)
		{
			FenetreCreerScenario fsc=new FenetreCreerScenario(Application.getApplication().getFenetrePrincipale());
			/*if ((new CAjouterScenarioRef()).executer())
			{
				Application.getApplication().getProjet().setModified(true);
			}*/
		}
	}
}
