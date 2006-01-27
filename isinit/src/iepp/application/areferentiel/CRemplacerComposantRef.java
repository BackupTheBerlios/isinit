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
package iepp.application.areferentiel;

import java.util.*;

import javax.swing.*;

import org.ipsquad.utils.*;
import iepp.*;
import iepp.application.*;
import iepp.infrastructure.jsx.ChargeurComposant;
import iepp.domaine.ComposantProcessus;
import iepp.application.aedition.CSupprimerComposant;

/**
 * Remplacement d'un composant au r�f�rentiel.
 */
public class CRemplacerComposantRef extends CommandeNonAnnulable
{

    /**
     * Remplace un composant choisi par l'utilisateur au r�f�rentiel.
     * @see iepp.application.Commande#executer()
     * @return true si la commande s'est ex�cut�e correctement
     */

    public boolean executer()
    {
        return (executer(-1,ElementReferentiel.DEFAUT_VERSION, ElementReferentiel.FORMATEUR.format( (new Date()))));
    }

    /**
     * Remplace un composant choisi par l'utilisateur au r�f�rentiel suivant une version donne et une date fixee.
     * @see iepp.application.Commande#executer(String _version, String _datePlacement)
     * @return true si la commande s'est ex�cut�e correctement
     */
    public boolean executer(long idCompASupprimer, String _version, String _datePlacement)
    {
        String cheminComp; // Chemin du composant � charger

        // Demander � l'utilisateur de choisir un composant
        JFileChooser chooser = new JFileChooser(Application.getApplication().getConfigPropriete("rep_composant"));
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(new SimpleFileFilter(new String[]
                                                   {"pre"}, Application.getApplication().getTraduction("Composants_Publiables")));
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        // r�cup�re le nom de fichier s�lectionn� par l'utilisateur
        if (chooser.showOpenDialog(Application.getApplication().getFenetrePrincipale()) != JFileChooser.APPROVE_OPTION)
        {
            return false;
        }

        // Recuperer le r�f�rentiel courant
        Referentiel ref = Application.getApplication().getReferentiel();
        // suppression du composant
        ComposantProcessus comp=(ComposantProcessus)ref.chercherReference(idCompASupprimer);
        // Enlever le composant de la DP en cours s'il y est
        if(comp!=null)
        {
          CSupprimerComposant c=new CSupprimerComposant(comp.getIdComposant());
          if(c.executer())
            Application.getApplication().getProjet().setModified(true);
        }

        //demander de charger un composant
        long id = ref.ajouterElement(chooser.getSelectedFile().getAbsolutePath(), ElementReferentiel.COMPOSANT, _version, _datePlacement);
        if (id == -2)
        {
            JOptionPane.showMessageDialog(Application.getApplication().getFenetrePrincipale(), Application.getApplication().getTraduction("ERR_Non_Composant"), Application.getApplication().getTraduction("ERR"), JOptionPane.ERROR_MESSAGE);
        }
        if (id == -3)
        {
            //le composant est deja dans le referentiel
            // Demander au r�f�rentiel de retirer l'ancien composant
            try
            {
                ChargeurComposant cc = new ChargeurComposant(chooser.getSelectedFile().getAbsolutePath());
                long idASupprimer = ref.nomComposantToId(cc.chercherNomComposant(chooser.getSelectedFile().getAbsolutePath()));
                if(idASupprimer == idCompASupprimer){
                  ref.supprimerElement(idASupprimer, ElementReferentiel.COMPOSANT);
                  id = ref.ajouterElement(chooser.getSelectedFile().getAbsolutePath(), ElementReferentiel.COMPOSANT, _version, _datePlacement);
                }
                else
                {
                  id=ref.ajouterElement(chooser.getSelectedFile().getAbsolutePath(), ElementReferentiel.COMPOSANT, _version, _datePlacement);
                  if(id==-2)
                  {
                    JOptionPane.showMessageDialog(Application.getApplication().getFenetrePrincipale(),
                                                  Application.getApplication().getTraduction("ERR_Non_Composant"),
                                                  Application.getApplication().getTraduction("ERR"),
                                                  JOptionPane.ERROR_MESSAGE);
                  }
                  if(id==-3)
                  {
                    //le composant qu'on souhaite ajouter est deja dans le referentiel
                      JOptionPane.showMessageDialog(Application.getApplication().getFenetrePrincipale(),
                                                    Application.getApplication().getTraduction("ERR_Composant_Deja"),
                                                    Application.getApplication().getTraduction("ERR"),
                                                    JOptionPane.ERROR_MESSAGE);
                  }
                  else
                    ref.supprimerElement(idCompASupprimer, ElementReferentiel.COMPOSANT);
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        else{
          //cas ou on actualise un composant qui n'a pas le meme nom et qui ne se trouve pas deja dans le referentiel
          //on supprimer l'ancien composant
          ref.supprimerElement(idCompASupprimer, ElementReferentiel.COMPOSANT);
          //on ajoute le composant choisi
          id = ref.ajouterElement(chooser.getSelectedFile().getAbsolutePath(), ElementReferentiel.COMPOSANT, _version, _datePlacement);
        }
        // Retourner vrai si �a s'est bien pass�
        return (id >= 0);
    }
}
