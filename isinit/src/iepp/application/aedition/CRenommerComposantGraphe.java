package iepp.application.aedition;

import iepp.Application;
import iepp.application.CommandeAnnulable;
import iepp.domaine.ComposantProcessus;
import iepp.domaine.IdObjetModele;
import iepp.ui.iedition.VueDPGraphe;
import iepp.ui.iedition.dessin.rendu.ComposantCell;
import iepp.ui.iedition.dessin.rendu.IeppCell;

import java.util.Vector;

//fait par Youssef

public class CRenommerComposantGraphe extends CommandeAnnulable
{
		/**
         * Id du composant à renommer du graphe
         */
        private IdObjetModele composant;

        
        /**
         * Cell du composant à renommer du graphe
         */
        private IeppCell cell;
        
        /**
         * Diagramme duquel on veut renommer un composant
         */
        private VueDPGraphe diagramme;

        /**
         * nouveau nom du composant
         */
        String nom;

        /**
         * Constructeur de la commande, récupère le composant à supprimer
         * et le diagramme courant de l'application
         * @param compo id du composant à supprimer
         * */

		  public CRenommerComposantGraphe(IdObjetModele compo,String n)
		  {
		             // initialiser le composant à renommer
		                this.composant = compo ;
		                this.diagramme = Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe();
		                this.nom=n;
		                this.cell = this.diagramme.contient(compo);
		  }
        /**
         * La commande renvoie si elle s'est bien passée ou non
         * Parcours la liste des produits du composant, vérifie s'il n'y a pas
         * de produits fusion "à défusionner", supprime les figures des produits et du composant
         * @return true si l'export s'est bien passé false sinon
         */
        public boolean executer()
        {
//                // récupère la liste des liens du composant à renommer
//                Vector listeLiens = ((ComposantProcessus)this.composant.getRef()).getLien();
//
//                // récupérer la liste des produits en entrée du composant
//                Vector listeEntree = composant.getProduitEntree();
//                for (int i = 0; i < listeEntree.size(); i++)
//                {
//                        // récupérer le produit courant
//                        IdObjetModele produitCourant = (IdObjetModele)listeEntree.elementAt(i);
//                }
        		
        		System.out.println("Coucou nom : "+nom+" cell : "+cell);
        		cell.setNomCompCell(nom);
        		System.out.println("Coucou nom : "+nom+" cell : "+cell);
        		this.diagramme.repaint();
        		return true;


}
}
