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

package iepp.ui.apropos;

import iepp.Application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.JTable;
import javax.swing.JScrollPane;

public class PanneauAuteurs extends PanneauOptionPropos
{
  private JTextArea mDescriptionLabel ;

  private JTable listeEtudiants;
  private String[] columnNames = {Application.getApplication().getTraduction("colonne_nom"), Application.getApplication().getTraduction("colonne_contact"), Application.getApplication().getTraduction("colonne_role")};
  private Object[][] data;
  private int nb_etudiants = 17; //nombre d'etudiants ayant travaille sur IEPP depuis 2004

  public static final String AUTEURS_KEY="auteursTitle";

  public PanneauAuteurs(String name)
  {
    this.mTitleLabel=new JLabel(name);
    this.setLayout(new BorderLayout());
    mPanel=new JPanel();
    GridBagLayout gridbag=new GridBagLayout();
    mPanel.setLayout(gridbag);
    GridBagConstraints c=new GridBagConstraints();

    // Title
    c.weightx=1.0;
    c.weighty=0;
    c.fill=GridBagConstraints.BOTH;
    c.gridwidth=GridBagConstraints.REMAINDER; //end row			//	title
    this.mTitleLabel=new JLabel(name);
    TitledBorder titleBor=BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK));
    titleBor.setTitleJustification(TitledBorder.CENTER);
    mTitleLabel.setBorder(titleBor);
    gridbag.setConstraints(mTitleLabel, c);
    mPanel.add(mTitleLabel);

    // linefeed
    c.weighty=0;
    c.gridwidth=GridBagConstraints.REMAINDER; //end row
    makeLabel(" ", gridbag, c);

    c.weighty=0;
    c.weightx=0;
    c.fill=GridBagConstraints.BOTH;
    c.gridwidth=GridBagConstraints.REMAINDER; //next-to-last in row

    this.mDescriptionLabel=new JTextArea();
    this.setDescription(name);
    gridbag.setConstraints(this.mDescriptionLabel, c);
    mPanel.add(this.mDescriptionLabel);

    //linefeed
    c.weighty=0;
    c.gridwidth=GridBagConstraints.REMAINDER; //end row
    makeLabel(" ", gridbag, c);

    //liste des etudiants
    c.weighty=0;
    c.weightx=0;
    c.fill=GridBagConstraints.BOTH;
    c.gridwidth=GridBagConstraints.REMAINDER;

    data=new Object[nb_etudiants][3];
    data[0][0]="Sandra Poulain";
    data[0][1]="sandrapoulain@free.fr";
    data[0][2]=Application.getApplication().getTraduction("chefProjet");

    data[1][0]="Amandine Jean";
    data[1][1]="2xmi@free.fr";
    data[1][2]=Application.getApplication().getTraduction("chefProjet");

    data[2][0]="Sylvain Lavalley";
    data[2][1]="sylvain.lavalley@club-internet.fr";
    data[2][2]=Application.getApplication().getTraduction("architecte");

    data[3][0]="Chaouki Mhamedi";
    data[3][1]="2xmi@free.fr";
    data[3][2]=Application.getApplication().getTraduction("architecte");

    data[4][0]="Nicolas Michel";
    data[4][1]="njlmichel@free.fr";
    data[4][2]=Application.getApplication().getTraduction("specialiste_outil");

    data[5][0]="Jean Gaston";
    data[5][1]="2xmi@free.fr";
    data[5][2]=Application.getApplication().getTraduction("specialiste_outil");

    data[6][0]="Nicolas Pujos";
    data[6][1]="nicolaspujos@hotmail.com";
    data[6][2]=Application.getApplication().getTraduction("ing_processus");

    data[7][0]="Sébastien René";
    data[7][1]="2xmi@free.fr";
    data[7][2]=Application.getApplication().getTraduction("ing_processus");

    data[8][0]="Natalia Boursier";
    data[8][2]=Application.getApplication().getTraduction("analyste_dev");

    data[9][0]="Robin Eysseric";
    data[9][2]=Application.getApplication().getTraduction("analyste_dev");

    data[10][0]="Cédric Bouhours";
    data[10][2]=Application.getApplication().getTraduction("analyste_dev");

    data[11][0]="Vinvent Marillaud";
    data[11][2]=Application.getApplication().getTraduction("analyste_dev");

    data[12][0]="Stéphane Anrigo";
    data[12][2]=Application.getApplication().getTraduction("analyste_dev");

    data[13][0]="Mourad Sadok";
    data[13][2]=Application.getApplication().getTraduction("analyste_dev");

    data[14][0]="Hubert Nouhen";
    data[14][2]=Application.getApplication().getTraduction("analyste_dev");

    data[15][0]="Julien Sanmartin";
    data[15][2]=Application.getApplication().getTraduction("analyste_dev");

    data[16][0]="Youssef Mounasser";
    data[16][2]=Application.getApplication().getTraduction("analyste_dev");

    this.listeEtudiants=new JTable(data, columnNames);

    gridbag.setConstraints(this.listeEtudiants, c);
    mPanel.add(this.listeEtudiants);

    //		linefeed
    c.fill=GridBagConstraints.VERTICAL;
    c.weighty=2.0;
    c.gridwidth=GridBagConstraints.REMAINDER; //end row
    makeLabel(" ", gridbag, c);

    this.add(new JLabel("    "), BorderLayout.WEST);
    this.add(mPanel, BorderLayout.CENTER);

  }

  public PanneauOptionPropos openPanel(String key)
  {
    this.setName(Application.getApplication().getTraduction(key));
    return this;
  }

  public void setDescription(String key)
  {
     this.mDescriptionLabel.setText(Application.getApplication().getTraduction("etudiants_texte1") + Application.getApplication().getTraduction("etudiants_texte2"));
  }

}