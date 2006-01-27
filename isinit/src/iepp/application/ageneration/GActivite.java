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

import iepp.Application;
import iepp.domaine.ElementPresentation;
import iepp.domaine.IdObjetModele;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;
import util.TransformationXHTML;


/**
 * Classe permettant de cr�er une page dont le contenu correspond � une Activit�
 */
public class GActivite extends GElementModele
{

	/**
	 * @param elem
	 * @param writer
	 */
	public GActivite(ElementPresentation elem, PrintWriter writer)
	{
		super(elem,writer);
	}

	/**
	 * Renvoie le nom de la d�finition de travail dans lequel on a l'activit� courante
	 */
	public String getNomRacine()
	{
		String retour = super.getNomRacine();
		ArbreGeneration aux = this.arbre;
		// on remonte jusqu'� un composant ou un paquetage de pr�sentation
		while (!aux.isRacine() && (! (aux.getElement() instanceof GDefinitionTravail)))
		{
			aux = aux.getArbreParent();
		}
		if (aux.isRacine())
		{
			return retour + " :: ";
		}
		return ( retour + " :: " + aux.getElement().getElementPresentation().getNomPresentation());
	}


	/**
	 * M�thode permettant de cr�er le contenu de la page associ�e � l'activit� courante
	 * Affichage des listes de produits et du r�le responsable de l'activit�
	 */
	public void creerFichierDescription() throws IOException
	{
		// cr�ation du fichier de contenu
		File ficHTML = new File (this.cheminAbsolu) ;
		//System.out.println("Fichier  � cr��r : " + ficHTML);
		FileWriter fd = new FileWriter(ficHTML);

		fd.write("<HTML><head> <link rel='STYLESHEET' type='text/css' href='" + this.getCheminStyle() + "'>"
								+ "</head>" + "<body><center>\n"
								+ "<table width=\"84%\" align=\"center\">\n"
								+ "<tr><td width=\"100%\" class=\"titrePage\">\n"
								+ "<p align=\"center\" class=\"entete\">\n"
								+ "<b>" + this.element.getNomPresentation() + "</b>\n"
								+ "</p></td></tr></table></center><BR>\n");

		fd.write(getBarreNavigation() + "<br>");

		// affiche les produits en entr�e / sortie
        fd.write("<div class=\"titreliste\">" + Application.getApplication().getTraduction("WEB_PROD_ENTREE") + "</div>\n");
        // affiche la liste des produits entr�e de l'activite
        Vector listeProduits = this.modele.getProduitEntree();
        for (int i = 0; i < listeProduits.size(); i++)
        {
            IdObjetModele id = (IdObjetModele) listeProduits.elementAt(i);
            if (GenerationManager.estProduitExterieur(id) == 0)
            {
                if (GenerationManager.getProduitChange(id) == null)
                {
                    fd.write("<div class=\"elementliste\"><a href=\"" + this.getLienChemin(id) + "\" target=\"_new\" >" + id.toString() + "</a></div>\n");
                }
                else
                {
                    fd.write("<div class=\"elementliste\"><a href=\"" + this.getLienChemin(GenerationManager.getProduitChange(id)) + "\" target=\"_new\" >" + id.toString() + "</a></div>\n");
                }
            }
            else
            {
                fd.write("<div class=\"elementliste\">" + id.toString() + "</div>\n");
            }
        }

        fd.write("<div class=\"titreliste\">" + Application.getApplication().getTraduction("WEB_PROD_SORTIE") + "</div>\n");
        // affiche la liste des produits sortie de l'activite
        listeProduits = this.modele.getProduitSortie();
        for (int i = 0; i < listeProduits.size(); i++)
        {
            IdObjetModele id = (IdObjetModele) listeProduits.elementAt(i);
            if (GenerationManager.estProduitExterieur(id) == 0)
            {
                if (GenerationManager.getProduitChange(id) == null)
                {
                    fd.write("<div class=\"elementliste\"><a href=\"" + this.getLienChemin(id) + "\" target=\"_new\" >" + id.toString() + "</a></div>\n");
                }
                else
                {
                    fd.write("<div class=\"elementliste\"><a href=\"" + this.getLienChemin(GenerationManager.getProduitChange(id)) + "\" target=\"_new\" >" + id.toString() + "</a></div>\n");
                }
            }
            else
            {
                fd.write("<div class=\"elementliste\">" + id.toString() + "</div>\n");
            }
        }

		// affiche le r�le responsable de l'activit�
		fd.write("<div class=\"titreliste\">" + Application.getApplication().getTraduction("WEB_ROLE_RESP") + "</div>\n");
		IdObjetModele id = this.modele.getRoleResponsable();
		if (id != null)
		{
			fd.write("<div class=\"elementliste\"><a href=\"" + this.getLienChemin(id) + "\" target=\"_new\" >" + id.toString() + "</a></div>\n");
		}

                //modif 2XMI Amandine
                //affichage de la liste de guides
                this.ajouterGuidesAssocies(fd);

                this.ajouterDescription(fd);
                //modif 2XMI S�bastien
                //ajout ds la page du chemin relatif
                File f=new File(GenerationManager.getInstance().getCheminGeneration());
                fd.write("<span class=\"cheminRelatif\" id=\""+ficHTML.getAbsolutePath().substring(f.getAbsolutePath().length()+1, ficHTML.getAbsolutePath().length())+"\"></span>");
                //fin modif 2XMI S�bastien
                this.ajouterContenu(fd);
                this.ajouterMail(fd);

                //modif 2XMI amandine
                //affichage de la date de derni�re modification d'un composant
                this.ajouterDerniereModif(fd);

		this.ajouterVersionDate(fd);
                this.ajouterPiedPage(fd);
		fd.write("</BODY></HTML>") ;
		fd.close();
                //modif 2XMI S�bastien
                //Transformation du fichier en XHTML pour permettre l'export en PDF
                TransformationXHTML transformateur = new TransformationXHTML();
                if( !transformateur.executer(ficHTML,this.getFichierDtd()))
                //si erreur on ajoute l'erreur ds la liste
                {
                    ArbreGeneration.listeErreurs.add(Application.getApplication().getTraduction("ERR_Activite_XHTML") + " - " + this.element.getNomPresentation() + " :\n" + transformateur.getErreur());
                }
                //fin modif S�bastien
	}

	/**
	 *
	 */
	public void recenser()
	{
		Integer oldValue = (Integer)ArbreGeneration.mapCompteur.get("nbActivites");
		ArbreGeneration.mapCompteur.put("nbActivites", new Integer(oldValue.intValue() + 1));

		oldValue = (Integer)ArbreGeneration.mapCompteur.get("nbPagesTotal");
		ArbreGeneration.mapCompteur.put("nbPagesTotal", new Integer(oldValue.intValue() + 1));

		Vector oldVecteur = (Vector)ArbreGeneration.mapRecap.get("activites");
		oldVecteur.addElement(this);
	}
}
