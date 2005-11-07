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

package iepp.application.ageneration;

import java.io.IOException;
import java.io.PrintWriter;
import iepp.domaine.ElementPresentation;
import iepp.domaine.IdObjetModele;

/**
 * Classe contenant des m�thodes communes � tous les �l�ments � publier qui sont associ�s
 * � un �l�ment du mod�le
 */
public class GElementModele
    extends GElement {

  /**
   * Id de l'�l�ment du mod�le auquel est associ� l'�l�ment de pr�sentation courant
   */
  protected IdObjetModele modele;

  /**
   * @param elem element de pr�sentation associ� � l'�l�ment courant
   * @param lien vers le fichier tree.js construit durant la g�n�ration du site
   */
  public GElementModele(ElementPresentation elem, PrintWriter pwFicTree) {
    super(elem, pwFicTree);
    this.modele = this.element.getElementModele();
  }

  /**
   * M�thode permettant de traiter les �l�ments de pr�sentation li�s � un �l�ment de mod�le
   */
  public void traiterGeneration(long id) throws IOException {
    // r�cup�rer le mod�le associ�
    if (this.element.getID_Apes() != -1) {
      this.IDParent = id;
      // cr�er le r�pertoire
      this.creerRep();
      // on �crit dans l'arbre
      this.ecrireArbre();
      // on cr�e le fichier correspondant
      this.creerFichierDescription();
    }
  }

}
