package iepp.application.aedition;

import iepp.Application;
import iepp.application.CommandeAnnulable;
import iepp.domaine.IdObjetModele;
import iepp.ui.iedition.VueDPGraphe;

//fait par Youssef

public class CRenommerComposant extends CommandeAnnulable
{
         /**
         * Id du composant � renommer du graphe
         */
        private IdObjetModele composant;

        /**
         * nouveau nom du composant
         */
        private String nom;

        /**
         * Diagramme duquel on veut renommer un composant
         */
        private VueDPGraphe diagramme;


        /**
         * Constructeur de la commande, r�cup�re le composant � renommer
         * et le diagramme courant de l'application
         * @param compo id du composant � renommer
         */

  public CRenommerComposant(IdObjetModele compo,String n)
  {
    // initialiser le composant � renommer
                this.composant = compo ;
                this.diagramme = Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe();
                this.nom=n;
              }
  /**
         * La commande renvoie si elle s'est bien pass�e ou non
         * Parcours la liste des produits du composant, v�rifie s'il n'y a pas
         * de produits fusion "� d�fusionner", supprime les figures des produits et du composant
         * @return true si l'export s'est bien pass� false sinon
         */
        public boolean executer()
        {
                // Verifier si le composant est dans le diagramme, et si oui, le renommer
                if (diagramme.contient(this.composant) != null)
                {
                        new CRenommerComposantGraphe(this.composant,this.nom).executer();
                }

                // Renommer le composant de la definition processus
                Application.getApplication().getProjet().getDefProc().renommerComposant(this.composant,this.nom);

                // Mettre a jour l'arbre
               Application.getApplication().getFenetrePrincipale().getVueDPArbre().updateUI();

                return true;
        }

}
