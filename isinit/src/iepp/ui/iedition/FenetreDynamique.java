package iepp.ui.iedition;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import javax.swing.*;

public class FenetreDynamique extends JDialog
{
	
	final static int HAUTEUR=200;
	final static int LARGEUR=300;
	public FenetreDynamique()
	{
			setTitle("Nouveau Lien");

			setSize(LARGEUR,HAUTEUR);
			
			JPanel monpanneau = new JPanel();
			
			monpanneau.setLayout(new GridLayout(5,1));
			

			
			
			//bouton radio
			JRadioButton defini = new JRadioButton("Défini");
			monpanneau.add(defini);
			
			//premiere liste deroulante
			String elements[] = {"element 1", "salut", "jsdfkjds", "sfh"};
			
			final JComboBox list = new JComboBox(elements);
			monpanneau.add(list);
		
			
			//monpanneau.add(pano1);
			//bouton radio
			JRadioButton ndefini=new JRadioButton("Non défini");
			final ButtonGroup group=new ButtonGroup();
			defini.setSelected(true);
			group.add(defini);
			group.add(ndefini);
		
			monpanneau.add(ndefini);
			
			
			// textbox
			final JTextField texte=new JTextField(20);
			monpanneau.add(texte);
			
			//seconde liste deroulant
			String elements1[] = {"element 1", "salut", "jsdfkjds", "sfh"};
			final JComboBox list2 = new JComboBox(elements1);
			//list2.setBackground(Color.WHITE);
			monpanneau.add(list2);
			
			list2.setEnabled(false);
			texte.setEnabled(false);
			list.setEnabled(true);
			
			getContentPane().add(monpanneau);
			pack();
			setVisible(true);
			
			defini.addActionListener(new ActionListener() 
					{	 
						public void actionPerformed(ActionEvent e)
						{
							list2.setEnabled(false);
							texte.setEnabled(false);
							list.setEnabled(true);
						}
					
					});
			

			ndefini.addActionListener(new ActionListener() 
					{	 
						public void actionPerformed(ActionEvent e)
						{
							list2.setEnabled(true);
							texte.setEnabled(true);
							list.setEnabled(false);
						}
					
					});
			
	}
	
}
