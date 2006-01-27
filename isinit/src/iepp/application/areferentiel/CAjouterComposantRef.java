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

/**
 * Ajout d'un composant au r�f�rentiel.
 */
public class CAjouterComposantRef
    extends CommandeNonAnnulable {

  /**
   * Ajoute un composant choisi par l'utilisateur au r�f�rentiel.
   * @see iepp.application.Commande#executer()
   * @return true si la commande s'est ex�cut�e correctement
   */

  public boolean executer() {
    return (executer(ElementReferentiel.DEFAUT_VERSION, ElementReferentiel.FORMATEUR.format( (new Date()))));
  }

  /**
   * Ajoute un composant choisi par l'utilisateur au r�f�rentiel suivant une version donne et une date fixee.
   * @see iepp.application.Commande#executer(String _version, String _datePlacement)
   * @return true si la commande s'est ex�cut�e correctement
   */
  public boolean executer(String _version, String _datePlacement) {
    String cheminComp; // Chemin du composant � charger

    // Demander � l'utilisateur de choisir un composant
    JFileChooser chooser = new JFileChooser(Application.getApplication().getConfigPropriete("rep_composant"));
    chooser.setAcceptAllFileFilterUsed(false);
    chooser.setFileFilter(new SimpleFileFilter
                          (new String[] {"pre"}, Application.getApplication().getTraduction("Composants_Publiables")));
    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

    // r�cup�re le nom de fichier s�lectionn� par l'utilisateur
    if (chooser.showOpenDialog(Application.getApplication().getFenetrePrincipale()) != JFileChooser.APPROVE_OPTION) {
      return false;
    }

    // Recuperer le r�f�rentiel courant et lui demander de charger un composant
    Referentiel ref = Application.getApplication().getReferentiel();
    long id = ref.ajouterElement(chooser.getSelectedFile().getAbsolutePath(), ElementReferentiel.COMPOSANT, _version, _datePlacement);
    if (id == -2) {
      JOptionPane.showMessageDialog(Application.getApplication().getFenetrePrincipale(),
                                    Application.getApplication().getTraduction("ERR_Non_Composant"),
                                    Application.getApplication().getTraduction("ERR"),
                                    JOptionPane.ERROR_MESSAGE);
    }
    if (id == -3) {
      JOptionPane.showMessageDialog(Application.getApplication().getFenetrePrincipale(),
                                    Application.getApplication().getTraduction("ERR_Composant_Deja"),
                                    Application.getApplication().getTraduction("ERR"),
                                    JOptionPane.ERROR_MESSAGE);
    }
    // Retourner vrai si �a s'est bien pass�
    return (id >= 0);
  }
}
