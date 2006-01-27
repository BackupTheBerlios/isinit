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

import iepp.Application;
import iepp.domaine.IdObjetModele;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class FenetreRenommerProduit extends JDialog
{
	private JPanel panelCentre = new JPanel();
	private JPanel panelBas = new JPanel();
	
	private JLabel nomFusion = new JLabel(Application.getApplication().getTraduction("Proprietes"));
	private JTextField comboNom = new JTextField();
	private JButton okButton = new JButton();
	private JButton cancelButton = new JButton();
	private IdObjetModele id ;
	
	public FenetreRenommerProduit(JFrame parent, IdObjetModele id)
	{
		super(parent,Application.getApplication().getTraduction("Proprietes"),true);
		this.id = id ;
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		okButton.setText(Application.getApplication().getTraduction("Valider"));
		okButton.setDefaultCapable(true);
		
		okButton.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{ okAction(); }
				}
		);
		
		
		comboNom.setEditable(true);
		comboNom.setMinimumSize(new Dimension(300, 25));
		comboNom.setMaximumSize(new Dimension(500, 25));
		comboNom.setPreferredSize(new Dimension(300, 25));
		
		comboNom.setText(id.toString());
				
		panelCentre.add(nomFusion);
		panelCentre.add(comboNom);
		
		panelBas.add(okButton);
	
		this.pack();
		Rectangle bounds = parent.getBounds();
		this.setLocation(bounds.x+ (int) bounds.width / 2 - this.getWidth() / 2, bounds.y + bounds.height / 2 - this.getHeight() / 2);
		this.setResizable(false);
		this.getContentPane().add(panelCentre, BorderLayout.CENTER);
		this.getContentPane().add(panelBas, BorderLayout.SOUTH);
	}
	
	/**
	 * Action effectuée par le bouton de validation.
	 * Affecte le nom choisi au produit
	 */
	public void okAction()
	{ 
		if (this.comboNom.getText().length() != 0)
		{
			id.setNomElement(this.comboNom.getText());
			Application.getApplication().getProjet().setModified(true);
		}
		this.dispose();
	}
}

