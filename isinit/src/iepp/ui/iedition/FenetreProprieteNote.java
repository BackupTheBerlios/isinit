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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;


import iepp.Application;
import iepp.ui.FenetrePropriete;
import iepp.ui.iedition.dessin.rendu.FNote;
import iepp.ui.iedition.dessin.vues.MDNote;


/**
 * FenetreProprieteNote.java
 */
public class FenetreProprieteNote extends FenetrePropriete implements ActionListener
{
	private FNote note;
	
	private JButton BP_couleur ;
	private JButton ok, annuler ;
	private JTextArea sMsgNote;
	
	public FenetreProprieteNote(FNote note)
	{
		this.note = note ;
		this.getContentPane().setLayout(new BorderLayout());
		this.setTitle(Application.getApplication().getTraduction("Proprietes_Note_BD"));
		
		this.setSize(300, 300);
		// propriétés 
		JPanel pGen = new JPanel();
		pGen.setBorder(BorderFactory.createTitledBorder( Application.getApplication().getTraduction("Proprietes_Note")));
		pGen.setLayout(new BorderLayout());
		
		JPanel pGenerales = new JPanel(new GridLayout(2,2,0,15));
		
		JLabel lNomDP = new JLabel(Application.getApplication().getTraduction("Couleur_Note"));
		pGenerales.add(lNomDP);

		this.BP_couleur = new JButton();
		this.BP_couleur.setBackground(((MDNote)this.note.getModele()).getFillColor());
		pGenerales.add(this.BP_couleur);
		this.BP_couleur.addActionListener(this);
		
		JLabel lCommentaireDP = new JLabel(Application.getApplication().getTraduction("Contenu_Note"));
		pGenerales.add(lCommentaireDP);
		pGenerales.add(new JLabel());

		this.sMsgNote = new JTextArea(((MDNote)this.note.getModele()).getMessage());
		this.sMsgNote.setPreferredSize(new Dimension(100,150));
		pGen.add(new JScrollPane(this.sMsgNote), BorderLayout.CENTER);
		pGen.add(pGenerales, BorderLayout.NORTH);
		
		// bouton ok ou annuler
		JPanel pBas = new JPanel();
		this.ok = new JButton(Application.getApplication().getTraduction("OK"));
		this.ok.addActionListener(this);
		this.annuler = new JButton(Application.getApplication().getTraduction("Annuler"));
		this.annuler.addActionListener(this);
		pBas.add(this.ok);
		pBas.add(this.annuler);
		
		this.getContentPane().add(pGen, BorderLayout.CENTER);
		this.getContentPane().add(pBas, BorderLayout.SOUTH);
	}
	
	
	public void actionPerformed( ActionEvent e )
	{
		if (e.getSource() == this.ok)
		{
			((MDNote)this.note.getModele()).setMessage(this.sMsgNote.getText());
			((MDNote)this.note.getModele()).setFillColor(this.BP_couleur.getBackground());
			this.dispose();
			Application.getApplication().getProjet().setModified(true);
			Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().repaint();
		}
		else if (e.getSource() == this.BP_couleur)
		{
			// récupère la couleur choisie dans la bd
			Color couleur_choisie = JColorChooser.showDialog(this,
															Application.getApplication().getConfigPropriete("choix_couleur"),
															((MDNote)this.note.getModele()).getFillColor());
			// si l'utilisateur choisit annuler, la bd renvoie null, donc on vérifie le retour
			if (couleur_choisie != null)
			{
				this.BP_couleur.setBackground(couleur_choisie);
			}
		}
		else if (e.getSource() == this.annuler)
		{
			this.dispose();
		}
	}

}
