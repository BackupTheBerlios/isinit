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
 
package iepp.ui.iedition;

import iepp.Application;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;

import util.IconManager;

/**
 * Classe permettant de créer et d'afficher le panneau d'assemblage des composants
 */
public class FenetreEdition extends JPanel implements ActionListener
{
	/**
	 * Diagrammme d'assemblage
	 */ 
	private VueDPGraphe vueDPGraphe ;
	
	/**
	 * barre d'outils permettant de choisir l'action à effectuer
	 */
	private JPanel barre_outil;
	
	/**
	 *  outils de la barre
	 */
	private JToggleButton oSelection ;
	private JToggleButton oLierElement ;
	private JToggleButton oNote ;
	
	/**
	 * Groupe de boutons à deux états
	 */
	private ButtonGroup gpbouton;
	
	
	/**
	 * Construction du panneau d'assemblage avec le diagramme
	 * @param vue, diagramme à afficher
	 */
	public FenetreEdition (VueDPGraphe vue)
	{
		// garder un lien vers le diagramme à afficher
		this.vueDPGraphe = vue ;
		// gestionnaire de mise en forme
		this.setLayout(new BorderLayout());
		// création de la barre d'outil
		this.barre_outil = new JPanel(new FlowLayout());
		
		// création d'un groupe vide de boutons
		this.gpbouton = new ButtonGroup();
		
		// création du bouton de l'outil de sélection
		this.oSelection = new JToggleButton(IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + "PaletteArrow.gif"));
		this.oSelection.setPreferredSize(new Dimension(35,35));
		this.barre_outil.add(this.oSelection);
		this.oSelection.addActionListener(this);
		
		// création du bouton de l'outil de liaison
		this.oLierElement = new JToggleButton(IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + "PaletteTransition.gif"));
		this.oLierElement.setPreferredSize(new Dimension(35,35));
		this.barre_outil.add(this.oLierElement);
		this.oLierElement.addActionListener(this);
		
		//création du bouton de l'outil pour lier une note à un élément
		this.oNote = new JToggleButton(IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + "edit.png"));
		this.oNote.setPreferredSize(new Dimension(35,35));
		this.barre_outil.add(this.oNote);
		this.oNote.addActionListener(this);
		
		// ajouter ces boutons au groupe de bouton
		// cela permet de n'avoir qu'un seul bouton sélectionné à la foi
		this.gpbouton.add(this.oSelection);
		this.gpbouton.add(this.oLierElement);
		this.gpbouton.add(this.oNote);
		
		// par défaut on prend l'outil de sélection
		this.oSelection.setSelected(true);
		this.barre_outil.setBorder(BorderFactory.createEtchedBorder());
		this.barre_outil.setPreferredSize(new Dimension(45,100));
	
		JScrollPane scroler = new JScrollPane(vue);
		scroler.setPreferredSize(new Dimension(500,500));
	
		// ajouter au panneau tous les éléments créés
		this.add(this.barre_outil, BorderLayout.WEST);
		this.add(scroler , BorderLayout.CENTER);
	}

	/**
	 * Renvoie le diagramme d'assemblage de composant affiché
	 * @return un diagramme
	 */
	public VueDPGraphe getVueDPGraphe()
	{
		return this.vueDPGraphe ;
	}
	
	public void removeVueDPGraphe()
	{
		this.vueDPGraphe = null;
	}
	
	public void setOutilSelection()
	{
		this.oSelection.setSelected(true);
		this.vueDPGraphe.setOutilSelection();
	}
	
	public void rafraichirLangue()
	{
		this.oSelection.setToolTipText(Application.getApplication().getTraduction("Selection"));
		this.oLierElement.setToolTipText(Application.getApplication().getTraduction("Lien"));
	}
	
	/**
	 * Gestionnaire de click sur les boutons
	 */
	public void actionPerformed( ActionEvent e )
	{
		// selon l'objet source, réagir
		if (e.getSource() == this.oSelection)
		{
			this.vueDPGraphe.setOutilSelection();
		}
		else if (e.getSource() == this.oLierElement)
		{
			this.vueDPGraphe.setOutilLier();
		}
		else if (e.getSource() == this.oNote)
		{
			this.vueDPGraphe.setOutilCreerElement();
		}
	}
}
