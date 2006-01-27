package iepp.application.aedition;

import java.util.Vector;

import iepp.Application;
import iepp.application.CommandeAnnulable;
import iepp.domaine.ComposantProcessus;
import iepp.domaine.IdObjetModele;
import iepp.domaine.LienProduits;
import iepp.ui.iedition.VueDPGraphe;
import iepp.ui.iedition.dessin.rendu.FProduit;

//fait par Youssef

public class CRenommerComposantGraphe extends CommandeAnnulable
{
  /**
         * Id du composant � renommer du graphe
         */
        private IdObjetModele composant;

        /**
         * Diagramme duquel on veut renommer un composant
         */
        private VueDPGraphe diagramme;

        /**
         * nouveau nom du composant
         */
        String nom;

        /**
         * Constructeur de la commande, r�cup�re le composant � supprimer
         * et le diagramme courant de l'application
         * @param compo id du composant � supprimer
      * */

  public CRenommerComposantGraphe(IdObjetModele compo,String n)
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
                // r�cup�re la liste des liens du composant � renommer
                Vector listeLiens = ((ComposantProcessus)this.composant.getRef()).getLien();

                // r�cup�rer la liste des produits en entr�e du composant
                Vector listeEntree = composant.getProduitEntree();
                for (int i = 0; i < listeEntree.size(); i++)
                {
                        // r�cup�rer le produit courant
                        IdObjetModele produitCourant = (IdObjetModele)listeEntree.elementAt(i);
                }
                        return true;


}
}
