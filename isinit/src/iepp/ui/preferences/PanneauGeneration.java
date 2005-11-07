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
import iepp.Projet;
import iepp.application.CGenererSite;
import iepp.application.ageneration.ArbreGeneration;
import iepp.domaine.DefinitionProcessus;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import util.IconManager;

public class PanneauGeneration extends PanneauOption
{
        private DefinitionProcessus defProc;
        private JButton bGenerer;
        private FenetrePreference fenPref;
        private JTextArea mOutput;

        public static final String GENERATION_PANEL_KEY = "Generation_GO_Desc";

        public PanneauGeneration(String name, FenetrePreference fenPref)
        {
                this.fenPref = fenPref;
                Projet p = Application.getApplication().getProjet();
            if (p != null)
            {
                this.defProc = Application.getApplication().getProjet().getDefProc();
            }



                this.mTitleLabel = new JLabel (name) ;
                this.setLayout(new BorderLayout());
                mPanel = new JPanel() ;
                GridBagLayout gridbag = new GridBagLayout();
                mPanel.setLayout(gridbag);
                GridBagConstraints c = new GridBagConstraints();
                ManagerButton man = new ManagerButton();

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

                //linefeed
                c.weighty = 1;
                c.gridx = 0; c.gridy = 1;
                c.gridwidth = GridBagConstraints.REMAINDER; //end row
                makeLabel(" ", gridbag, c);

                //linefeed
                c.weighty = 1;
                c.gridx = 0; c.gridy = 2;
                c.fill = GridBagConstraints.BOTH; //end row
                this.mOutput = new JTextArea(13, 25);
                this.mOutput.setAutoscrolls(true);
                this.mOutput.setEditable(false);
                JScrollPane js = new JScrollPane(this.mOutput);
                mPanel.add(js, c);


                this.bGenerer = new JButton(IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + "ToolsGenerate.gif"));
                this.bGenerer.setText( Application.getApplication().getTraduction("Generer_le_site_Web")) ;
                this.bGenerer.addActionListener(man);
                c.gridy = 4;
                c.gridwidth = GridBagConstraints.REMAINDER; //end row
                mPanel.add(this.bGenerer, c);

                //linefeed
                c.weighty = 1;
                c.gridx = 0; c.gridy = 5;
                c.gridwidth = GridBagConstraints.REMAINDER; //end row
                makeLabel(" ", gridbag, c);

                this.add(new JLabel("    "),BorderLayout.WEST);
                this.add(mPanel,BorderLayout.CENTER);
        }

        /**
         *
         */
        public void afficherStatistiques()
        {
                this.mOutput.append(Application.getApplication().getTraduction("NB_COMPOSANTS") + " " + ArbreGeneration.mapCompteur.get("nbComposants")+ "\n");
                this.mOutput.append(Application.getApplication().getTraduction("NB_ROLES") + " " + ArbreGeneration.mapCompteur.get("nbRoles") + "\n");
                this.mOutput.append(Application.getApplication().getTraduction("NB_PRODUITS") + " " + ArbreGeneration.mapCompteur.get("nbProduits") + "\n");
                this.mOutput.append(Application.getApplication().getTraduction("NB_ACTIVITES") + " " + ArbreGeneration.mapCompteur.get("nbActivites") + "\n");
                this.mOutput.append(Application.getApplication().getTraduction("NB_DEFS_TRAVAIL") + " " + ArbreGeneration.mapCompteur.get("nbDefinitionsTravail") + "\n");
                this.mOutput.append(Application.getApplication().getTraduction("NB_DIAGRAMMES") + " " + ArbreGeneration.mapCompteur.get("nbDiagrammes") + "\n");
                this.mOutput.append(Application.getApplication().getTraduction("NB_GUIDES") + " " + ArbreGeneration.mapCompteur.get("nbGuides") + "\n");
                this.mOutput.append(Application.getApplication().getTraduction("NB_PAQ_PRESENT") + " " + ArbreGeneration.mapCompteur.get("nbPaquetagesPresentation") + "\n");
                this.mOutput.append(Application.getApplication().getTraduction("NB_PAQ") + " " + ArbreGeneration.mapCompteur.get("nbPaquetages")+ "\n");
                this.mOutput.append(Application.getApplication().getTraduction("NB_ELEM_PRESENT") + " " + ArbreGeneration.mapCompteur.get("nbElementPresentation")  + "\n");
                this.mOutput.append(Application.getApplication().getTraduction("NB_ACCUEIL") + " " + "1" + "\n");

                this.mOutput.append("-------------------------------------------------------------------------------\n");

                this.mOutput.append(Application.getApplication().getTraduction("NB_PAGES_TOTAL") + " " + ArbreGeneration.mapCompteur.get("nbPagesTotal") + "\n");
        }

        public PanneauOption openPanel(String key)
        {
                this.setName(Application.getApplication().getTraduction(key)) ;
                return this ;
        }


        private class ManagerButton implements ActionListener
        {
                public void actionPerformed(ActionEvent e)
                {
                        //r�cup�rer l'objet source de l'�v�nement re�u
                        Object source = e.getSource();


                        if (source == PanneauGeneration.this.bGenerer)
                        {
                                //sauver les param�tres
                                PanneauGeneration.this.fenPref.save();
                                CGenererSite c = new CGenererSite(PanneauGeneration.this.defProc) ;
                                if (c.executer())
                                {

                                         // afficher les statistiques
                                         PanneauGeneration.this.mOutput.setText("");
                                         PanneauGeneration.this.afficherStatistiques();

                                         //2XMI modif S�bastien
                                         //ajout avertissement en lorsque des erreurs de transformation XHTML sont rencontr�s
                                         if (ArbreGeneration.listeErreurs.isEmpty())
                                         {
                                             JOptionPane.showMessageDialog(Application.getApplication().getFenetrePrincipale(), Application.getApplication().getTraduction("Generation_ok"),
                                                                           Application.getApplication().getTraduction("Generation_site_titre"),
                                                                           JOptionPane.INFORMATION_MESSAGE);
                                         }
                                         else
                                         {
                                             String[] options = {"OK", Application.getApplication().getTraduction("bouton_details")};
                                             int reponse = JOptionPane.showOptionDialog(Application.getApplication().getFenetrePrincipale(), Application.getApplication().getTraduction("Generation_XHTML_Non_ok"),
                                                                                        Application.getApplication().getTraduction("Generation_site_titre"),
                                                                                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
                                             if (reponse == JOptionPane.NO_OPTION)
                                             {
                                                 ArbreGeneration.afficheListeErreurs();
                                             }
                                             //on vide la liste d'erreurs
                                             ArbreGeneration.listeErreurs.clear();
                                         }
                                         //fin modif S�bastien
                                }
                        }
                }
        }



}
