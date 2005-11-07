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

package iepp.ui.preferences;



import iepp.Application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import iepp.domaine.IdObjetModele;


public class PanneauProprietesComposant extends PanneauOption
{
        private JLabel mDescriptionLabel ;

        private JLabel sResponsableComposant;
        private JLabel sMail;
        private JLabel sVersionComposant;

        private IdObjetModele composant_choisi;

        public static final String GENERAL_KEY = "GeneralTitle" ;

        public PanneauProprietesComposant(String name, IdObjetModele composant)
        {
          this.composant_choisi = composant;
          this.mTitleLabel = new JLabel (name) ;
          this.setLayout(new BorderLayout());
          mPanel = new JPanel() ;
          GridBagLayout gridbag = new GridBagLayout();
          mPanel.setLayout(gridbag);
          GridBagConstraints c = new GridBagConstraints();

          // Title
          c.weightx = 1.0;
          c.weighty = 0 ;
          c.fill = GridBagConstraints.BOTH;
          c.gridwidth = GridBagConstraints.REMAINDER; //end row			//	title
          this.mTitleLabel = new JLabel (name);
          TitledBorder titleBor = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK));
          titleBor.setTitleJustification(TitledBorder.CENTER);
          mTitleLabel.setBorder(titleBor);
          gridbag.setConstraints(mTitleLabel, c);
          mPanel.add(mTitleLabel);

//		 linefeed
          c.weighty = 0;
          c.gridwidth = GridBagConstraints.REMAINDER; //end row
          makeLabel(" ", gridbag, c);

          c.weighty=0;
          c.weightx=0;
          c.fill=GridBagConstraints.VERTICAL;
          c.gridwidth=3; //next-to-last in row
          JLabel label=new JLabel(Application.getApplication().getTraduction("Responsable"));
          sResponsableComposant=new JLabel();
          gridbag.setConstraints(label, c);
          gridbag.setConstraints(sResponsableComposant, c);
          mPanel.add(label);
          mPanel.add(sResponsableComposant);
          c.gridwidth=GridBagConstraints.REMAINDER; //end row
          makeLabel(" ", gridbag, c);

          c.weighty=0;
          c.weightx=0;
          c.fill=GridBagConstraints.VERTICAL;
          c.gridwidth=3; //next-to-last in row
          JLabel label2=new JLabel(Application.getApplication().getTraduction("Mail"));
          sMail=new JLabel();
          gridbag.setConstraints(label2, c);
          gridbag.setConstraints(sMail, c);
          mPanel.add(label2);
          mPanel.add(sMail);
          c.gridwidth=GridBagConstraints.REMAINDER; //end row
          makeLabel(" ", gridbag, c);

          c.weighty=0;
          c.weightx=0;
          c.fill=GridBagConstraints.VERTICAL;
          c.gridwidth=3; //next-to-last in row
          JLabel label3=new JLabel(Application.getApplication().getTraduction("Version"));
          sVersionComposant=new JLabel();
          gridbag.setConstraints(label3, c);
          gridbag.setConstraints(sVersionComposant, c);
          mPanel.add(label3);
          mPanel.add(sVersionComposant);
          c.gridwidth=GridBagConstraints.REMAINDER; //end row
          makeLabel(" ", gridbag, c);

          // linefeed
          c.fill = GridBagConstraints.VERTICAL;
          c.weighty = 2.0;
           c.gridwidth = GridBagConstraints.REMAINDER; //end row
          makeLabel(" ", gridbag, c);

          this.add(new JLabel("    "),BorderLayout.WEST);
          this.add(mPanel,BorderLayout.CENTER);

          // initialiser les champs
          if (this.composant_choisi != null)
          {
              this.sResponsableComposant.setText(this.composant_choisi.getPaquetagePresentation().getAuteur());
              this.sMail.setText(this.composant_choisi.getPaquetagePresentation().getMail());
              this.sVersionComposant.setText(this.composant_choisi.getPaquetagePresentation().getVersion());
          }

        }

        public PanneauOption openPanel(String key)
        {
                this.setName(Application.getApplication().getTraduction(key)) ;
                return this ;
        }
}
