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

package iepp.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;

import iepp.Application;
import iepp.application.CFermerProjet;
import iepp.application.areferentiel.Referentiel;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import util.ErrorManager;

/**
 * 
 */
public class FenetreCreerReferentiel extends JDialog 
{
	
	/**
	 * Indique si un rférentiel a été créé en sortant de la bd
	 */
	private boolean estCree = false;
	
	JPanel pfenetreSud = new JPanel();
	JPanel pfenetreCentre = new JPanel();
	GridLayout gfenetreCentre = new GridLayout();
	
	JLabel lnomRef = new JLabel();
	JFormattedTextField nomRef = new JFormattedTextField();
	
	JButton valider = new JButton();
	JButton annuler = new JButton();
	
	FenetreChoixReferentiel fenetreChoixRef= null;
	FenetrePrincipale fparent = null;
	
	/**
	 * Crée un bd permettant de créer un référentiel
	 */
	public FenetreCreerReferentiel(FenetrePrincipale parent, FenetreChoixReferentiel fcr)
	{
		super(parent,Application.getApplication().getTraduction("FCreer_referentiel"),true);
		this.fenetreChoixRef= fcr;
		this.fparent = parent; 
		jbInit();
		
		// affichage de la bd
	    this.setResizable(false);
		this.pack();
		Rectangle bounds = parent.getBounds();
		this.setLocation(bounds.x+ (int) bounds.width / 2 - this.getWidth() / 2, bounds.y + bounds.height / 2 - this.getHeight() / 2);
		this.setVisible(true);		
	}

	/**
	 * Initialise les éléments de la bd
	 */
	private void jbInit() 
	{
		pfenetreCentre.setLayout(gfenetreCentre);
		gfenetreCentre.setRows(2);
		
		// champs nom
		lnomRef.setDisplayedMnemonic('R');
		lnomRef.setText(Application.getApplication().getTraduction("Nom_ref"));
		lnomRef.setLabelFor(lnomRef);
		pfenetreCentre.add(lnomRef, null);
		pfenetreCentre.add(nomRef, null);
		this.getContentPane().add(pfenetreCentre, BorderLayout.CENTER);
		
		// creation du bouton valider
		valider.setMnemonic('I');
		valider.setText(Application.getApplication().getTraduction("Valider"));
		valider.setFocusable(true);
		valider.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				ok(e);
			}
		});
		
		// création du bouton annuler
		annuler.setMnemonic('N');
		annuler.setText(Application.getApplication().getTraduction("Annuler"));
		annuler.addActionListener(new java.awt.event.ActionListener()
		{
		  public void actionPerformed(ActionEvent e)
		  {
			cancel(e);
		  }
		});
		
		// positionnement des deux boutons
		pfenetreSud.add(valider, null);
		pfenetreSud.add(annuler, null);
		this.getContentPane().add(pfenetreSud,BorderLayout.SOUTH);
	}
	
	/**
	 * Action sur le bouton annuler 
	 * @param e
	 */
	private void cancel(ActionEvent e) 
	{
		this.dispose();
	}
	
	/**
	 * Action sur le bouton valider
	 * @param e
	 */
	private void ok(ActionEvent e) 
	{
		// vérification des données saisies
		if(this.verifierDonnees())
		{
			if (fenetreChoixRef != null)
			{
				fenetreChoixRef.dispose();
			}
			// vérifier s'il y a un processus en cours, dans ce cas le fermer
			// si le fichier n'a pas été sauvegardé, c'est pas grave, car on a demandé
			// déjà confirmation avant de sa suppression
			if (Application.getApplication().getProjet() != null)
			{
				CFermerProjet c = new CFermerProjet();
				c.executer();
			}
			// vérification : le nom de référentiel saisi n'existe pas déjà pour un autre référentiel
			File fic = new File(Application.getApplication().getConfigPropriete("chemin_referentiel")+ this.nomRef.getText());
			if(!fic.exists())
			{
				
				try 
				{
					// création du référentiel
					Application.getApplication().setReferentiel(new Referentiel(this.nomRef.getText()));
					this.dispose();
					this.estCree = true ;
					new FenetreChoixProcessus(this.fparent);
				} 
				catch (Exception e1) 
				{
					e1.printStackTrace();
					ErrorManager.getInstance().display("ERR","ERR_Fic_Ref_Corromp"); 
					this.estCree = false ;
				}
			}
			else
				JOptionPane.showMessageDialog(this,Application.getApplication().getTraduction("M_ref_existe"),Application.getApplication().getTraduction("M_creer_proc_titre"),JOptionPane.WARNING_MESSAGE);
		}
	}
	
	/**
	 * Vérifie les caractères saisis : le nom du référentiel ne doit pas être vide
	 * le nom du référentiel ne doit pas contenir les caractères /\":*<>|?
	 * @return
	 */
	private boolean verifierDonnees()
	{
		if(this.nomRef.getText().equals(""))
		{
			JOptionPane.showMessageDialog(this,Application.getApplication().getTraduction("M_creer_ref_nom"),Application.getApplication().getTraduction("M_creer_proc_titre"),JOptionPane.WARNING_MESSAGE);
			return false;
		}
		else
		{
			for(int j = 0; j < this.nomRef.getText().length(); j++)
			{
				char c = this.nomRef.getText().charAt(j);
				if(c=='/'||c=='\\'||c=='"'||c==':'||c=='*'||c=='<'||c=='>'||c=='|'||c=='?')
				{
					JOptionPane.showMessageDialog(this,Application.getApplication().getTraduction("ERR_Nom_Ref_Incorrect"),Application.getApplication().getTraduction("M_creer_proc_titre"),JOptionPane.WARNING_MESSAGE); 
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean isReferentielCree()
	{
		return this.estCree;
	}
	
}
