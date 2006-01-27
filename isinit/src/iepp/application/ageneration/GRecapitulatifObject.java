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
import iepp.domaine.DefinitionProcessus;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 */
public class GRecapitulatifObject extends GElement
{

	public static final int ROLES = 0;
	public static final int PRODUITS = 1;
	public static final int ACTIVITES = 2;
        public static final int GUIDE = 3;//youssef

	private int typeObjet;


	/**
	 * Constructeur du gestionnaire de génération pour la page de statistiques
	 */
	public GRecapitulatifObject(int type, PrintWriter pwFicTree)
	{
		super(null, pwFicTree);

		this.typeObjet = type;
		this.element = new ElementPresentation();
		switch (this.typeObjet)
		{
			case ROLES:
						this.element.setIcone("TreeRole.gif");
						this.element.setNomPresentation(Application.getApplication().getTraduction("WEB_ROLES_RECAP"));
						break;
			case PRODUITS:
						this.element.setIcone("TreeWorkProduct.gif");
						this.element.setNomPresentation(Application.getApplication().getTraduction("WEB_PRODUITS_RECAP"));
						break;
			case ACTIVITES:
						this.element.setIcone("TreeActivity.gif");
						this.element.setNomPresentation(Application.getApplication().getTraduction("WEB_ACTIVITES_RECAP"));
						break;
                      case GUIDE:
                                                this.element.setIcone("TreeWorkProduct.gif");
                                                this.element.setNomPresentation(Application.getApplication().getTraduction("WEB_GUIDE_RECAP"));
                                                break;

		}
	}

	/**
	 * Créer le fichier de contenu d'un élément de présentation simple, sans modèle
	 */
	protected void creerFichierDescription() throws IOException
	{
		File ficHTML = new File (this.cheminAbsolu) ;
		FileWriter fd = new FileWriter(ficHTML);

		fd.write("<HTML><head> <link rel='STYLESHEET' type='text/css' href='" + this.getCheminStyle() + "'>"
								+ "</head>" + "<body><center>\n"
								+ "<table width=\"84%\" align=\"center\">\n"
								+ "<tr><td width=\"100%\" class=\"titrePage\">\n"
								+ "<p align=\"center\" class=\"entete\">\n"
								+ "<b>" + Application.getApplication().getTraduction("WEB_RECAP") + "</b>\n"
								+ "</p></td></tr></table></center><BR><BR>\n");

		Vector listeElement = new Vector();
		switch (this.typeObjet)
		{
			case ROLES:
						fd.write("<div class=\"titreliste\">" + Application.getApplication().getTraduction("WEB_ROLES") + " </div>\n");
						listeElement = (Vector)ArbreGeneration.mapRecap.get("roles");
						break;
			case PRODUITS:
						fd.write("<div class=\"titreliste\">" + Application.getApplication().getTraduction("WEB_PRODUITS") + " </div>\n");
						listeElement = (Vector)ArbreGeneration.mapRecap.get("produits");
						break;
			case ACTIVITES:
						fd.write("<div class=\"titreliste\">" + Application.getApplication().getTraduction("WEB_ACTIVITES") + " </div>\n");
						listeElement = (Vector)ArbreGeneration.mapRecap.get("activites");
						break;
                      case GUIDE://modif 2XMI youssef
                                               fd.write("<div class=\"titreliste\">" + Application.getApplication().getTraduction("WEB_GUIDE") + " </div>\n");
                                               listeElement = (Vector)ArbreGeneration.mapRecap.get("guide");
                                               break;//

		}

		for (int i = 0; i < listeElement.size(); i++)
		{
                  if (this.typeObjet == GUIDE)
                  {
                     GElement elem = (GElement) listeElement.elementAt(i);
                     ElementPresentation el = elem.getElementPresentation();
                     fd.write("<div class=\"elementliste\"><a href=\""+this.getLienCheminVersGuide( el.getCheminPage())+  "\" target=\"_new\" >" + el.getNomPresentation() + " </a> - " + elem.getNomRacine() + "</div>\n");
                  }
                  else if (this.typeObjet == ROLES) {
                    GElementModele elem = (GElementModele) listeElement.elementAt(i);
                    IdObjetModele id = elem.getElementPresentation().getElementModele();
                    DefinitionProcessus defProc = Application.getApplication().getProjet().getDefProc();

                    if (defProc.containsSuperRole(id)){
                      //role super role
                      ArrayList listeSousRole = defProc.getListeSousRole(id);
                      Iterator iterateursousr=listeSousRole.iterator();
                      fd.write("<div class=\"elementliste\"><a href=\"" + this.getLienChemin(id) + "\" target=\"_new\" >" + id.toString() + "</a> - " + elem.getNomRacine() + "</div>\n");
                      while(iterateursousr.hasNext())
                      {
                        IdObjetModele idsousrole=(IdObjetModele)iterateursousr.next();
                        Iterator iterateurrole=listeElement.iterator();
                        boolean trouve = false;
                        while(iterateurrole.hasNext() && !trouve)
                        {
                          GElement elemCourant = (GElement) iterateurrole.next();
                          if (elemCourant.getElementPresentation().getElementModele() == idsousrole){
                            trouve=true;
                            fd.write("<div class=\"elementliste\">&nbsp;&nbsp;&nbsp;<a href=\"" + this.getLienChemin(idsousrole) + "\" target=\"_new\" >" + idsousrole.toString() + "</a> - " + elemCourant.getNomRacine() + "</div>\n");
                          }
                        }
                      }
                    }else if (!defProc.containsSousRole(id)){
                      //role qui n'est ni super role, ni sous role
                      if (id != null && id.getChemin() != null)
                      {
                        fd.write("<div class=\"elementliste\"><a href=\"" + this.getLienChemin(id) + "\" target=\"_new\" >" + id.toString() + "</a> - " + elem.getNomRacine() + "</div>\n");
                      }
                    }
                  }else{
                    GElementModele elem = (GElementModele) listeElement.elementAt(i);
                    IdObjetModele id = elem.getElementPresentation().getElementModele();
                    if (id != null && id.getChemin() != null)
                    {
                      fd.write("<div class=\"elementliste\"><a href=\"" + this.getLienChemin(id) + "\" target=\"_new\" >" + id.toString() + "</a> - " + elem.getNomRacine() + "</div>\n");
                    }
                  }

		}

		this.ajouterMail(fd);
		this.ajouterVersionDate(fd);
                this.ajouterPiedPage(fd);
		fd.write("</BODY></HTML>") ;
		fd.close();

	}

	/**
	 * Méthode permettant d'écrire le code html correspondant au bouton mail à chaque bas de page
	 * @param fd lien vers le fichier html de contenu
	 * @throws IOException
	 */
	public void ajouterMail(FileWriter fd) throws IOException
	{
		fd.write("<br><hr>");
		fd.write("<div align=\"center\" class=\"boutonemail\"><a href=\"mailto:"
					+ Application.getApplication().getProjet().getDefProc().getEmailAuteur()
					+ "?subject=" + this.element.getNomPresentation() + "\">"
					+ Application.getApplication().getTraduction("WEB_MAIL") + "</A></div>");
	}

}
