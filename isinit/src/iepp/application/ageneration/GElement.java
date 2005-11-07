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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import util.ToolKit;

import java.net.URI;


import iepp.Application;
import iepp.domaine.ElementPresentation;
import iepp.domaine.IdObjetModele;
import iepp.domaine.PaquetagePresentation;

import iepp.domaine.Guide;

import iepp.application.areferentiel.Referentiel;
import iepp.application.areferentiel.ElementReferentiel;
import iepp.domaine.ComposantProcessus;
import util.TransformationXHTML;
import java.net.*;

/**
 * Classe contenant des m�thodes de base (cr�ation des r�pertoires, remplissage du fichier tree.js etc...)
 * communes � tous les �l�ments � publier
 */
public class GElement {

  /**
   * Arbre courant, permettant d'obtenir des informations sur la position de l'�l�ment courant
   */
  protected ArbreGeneration arbre;

  /**
   * Element de pr�sentation courant � traiter
   */
  protected ElementPresentation element;

  /**
   * Lien vers le fichier tree.js � remplir lors de la g�n�ration
   */
  protected PrintWriter pwFicTree;

  /**
   * Chemin absolu du fichier � creer
   */
  protected String cheminAbsolu = null;
  protected String cheminParent = null;

  /**
   * ID du gelement
   */
  protected long ID;
  protected long IDParent;

  /**
   * Constructeur du gestionnaire de g�n�ration
   * @param elem
   * @param pwFicTree2
   */
  public GElement(ElementPresentation elem, PrintWriter pwFicTree) {
    this.element = elem;
    this.pwFicTree = pwFicTree;
  }

  /**
   * Traitement commun � tous les �l�ments � g�n�rer
   * ecriture dans l'arbre et cr�ation du fichier de contenu
   * @param feuille, indique si l'�l�ment courant est une feuille ou non
   * @param id
   */
  public void traiterGeneration(long id) throws IOException {
    this.IDParent = id;
    // cr�er le r�pertoire
    this.creerRep();
    // on �crit dans l'arbre
    this.ecrireArbre();
    // on cr�e le fichier correspondant
    this.creerFichierDescription();
  }

  /**
   *
   */
  public void creerRep() {
    File f = new File(this.cheminParent);
    f.mkdirs();
  }

  /**
   * Retourne l'�l�ment de pr�sentation associ� au GNoeud courant
   */
  public ElementPresentation getElementPresentation() {
    return this.element;
  }

  public void setChemin(String chemin) {
    this.cheminAbsolu = chemin;
    File f2 = (new File(this.cheminAbsolu)).getParentFile();
    this.cheminParent = ToolKit.removeSlashTerminatedPath(f2.getPath());
  }

  public String getChemin() {
    return this.cheminAbsolu;
  }

  public String getCheminParent() {
    return this.cheminParent;
  }

  public String getCheminRelatif() {
    File f = new File(GenerationManager.getInstance().getCheminGeneration());
    String cheminRel = "./" + ToolKit.getRelativePathOfAbsolutePath(this.cheminAbsolu,
        ToolKit.removeSlashTerminatedPath(f.getAbsolutePath()));
    return ToolKit.removeSlashTerminatedPath(cheminRel);
  }

  public String getCheminIcone(String icone) {
    String res = "./" + GenerationManager.APPLET_PATH + "/images/" + icone;
    ArbreGeneration aux = this.arbre;
    // on remonte jusqu'� la racine
    while (!aux.isRacine()) {
      res = "../" + res;
      aux = aux.getArbreParent();
    }
    return res;
  }

  /**
   * Renvoie le chemin relatif de la feuille de style par rapport � l'�l�ment courant
   */
  public String getCheminStyle() {
    String res = "./" + GenerationManager.STYLES_PATH + "/" + GenerationManager.getInstance().getFeuilleCss();
    ArbreGeneration aux = this.arbre;
    // on remonte jusqu'� la racine
    while (!aux.isRacine()) {
      res = "../" + res;
      aux = aux.getArbreParent();
    }
    return res;
  }

  //modif 2XMI S�basitien
  /**
   * Renvoie le chemin relatif du fichier iepp.dtd par rapport � l'�l�ment courant
   */
  public String getFichierDtd() {
    String res = "./" + GenerationManager.STYLES_PATH + "/" + GenerationManager.getInstance().getDtd();
    ArbreGeneration aux = this.arbre;
    // on remonte jusqu'� la racine
    while (!aux.isRacine()) {
      res = "../" + res;
      aux = aux.getArbreParent();
    }
    return res;
  }

  /**
   * Renvoie le chemin des fichiers de contenu de l'�l�ment courant
   */
  public String getCheminContenu() {
    ArbreGeneration aux = this.arbre;
    // on remonte jusqu'� un composant ou un paquetage de pr�sentation
    while (!aux.isRacine() && (! (aux.getElement() instanceof GPaquetagePresentation))
           && (! (aux.getElement() instanceof GComposantPubliable))) {
      aux = aux.getArbreParent();
    }
    if (aux.isRacine()) {
      return "";
    }
    return (aux.getElement().getCheminParent() + File.separator + GenerationManager.CONTENU_PATH);
  }

  /**
   * Renvoie le nom du composant ou le nom du paquetage de pr�sentation racine
   */
  public String getNomRacine() {
    ArbreGeneration aux = this.arbre;
    // on remonte jusqu'� un composant ou un paquetage de pr�sentation
    while (!aux.isRacine() && (! (aux.getElement() instanceof GPaquetagePresentation))
           && (! (aux.getElement() instanceof GComposantPubliable))) {
      aux = aux.getArbreParent();
    }
    if (aux.isRacine()) {
      return "";
    }
    return (aux.getElement().getElementPresentation().getNomPresentation());
  }

  public String getBarreNavigation() {
    String res = "<img src=\"" + this.getCheminIcone(this.element.getNomIcone())
        + "\" width=\"16\" height=\"16\" border=\"0\"> " + this.element.getNomPresentation() + "</div>\n";
    if (this.arbre.isRacine()) {
      return "<div class=\"navigation_barre\"> " + res;
    }
    ArbreGeneration aux = this.arbre.getArbreParent();
    // on remonte jusqu'� la racine
    int niveau = 1;
    while (!aux.isRacine()) {
      res = aux.getElement().getElementPresentation().getNomPresentation() + "</a>\n >> " + res;
      res = CodeHTML.normalizeName(aux.getElement().getElementPresentation().getNomPresentation()) + "_" + aux.getElement().getID() + ".html\" >"
          + "<img src=\"" + this.getCheminIcone(aux.getElement().getElementPresentation().getNomIcone()) + "\" width=\"16\" height=\"16\" border=\"0\"> " + res;
      for (int i = 0; i < niveau; i++) {
        res = "../" + res;
      }
      res = "<a href=\"" + res;
      aux = aux.getArbreParent();
      niveau++;
    }
    return "<div class=\"navigation_barre\"> " + res;
  }

  /**
   * Renvoie le lien vers le contenu associ� � l'�l�ment courant
   */
  public String getLienContenu() {
    String res = "./" + GenerationManager.CONTENU_PATH + "/" + this.element.getContenu();
    ArbreGeneration aux = this.arbre;
    // on remonte jusqu'� un composant ou un paquetage de pr�sentation
    while (!aux.isRacine() && (! (aux.getElement() instanceof GPaquetagePresentation))
           && (! (aux.getElement() instanceof GComposantPubliable))) {
      res = "../" + res;
      aux = aux.getArbreParent();
    }
    return (res);
  }

  public String getLienImage() {
    //modif 2XMI pour la demande de SPWIZ : ajout de GenerationManager.CONTENU_PATH +
    //suppression de "./images"
    String res = GenerationManager.CONTENU_PATH + "/" + GenerationManager.IMAGES_PATH;
    ArbreGeneration aux = this.arbre;
    // on remonte jusqu'� un composant ou un paquetage de pr�sentation
    while (!aux.isRacine() && (! (aux.getElement() instanceof GPaquetagePresentation))
           && (! (aux.getElement() instanceof GComposantPubliable))) {
      res = "../" + res;
      aux = aux.getArbreParent();
    }
    return (res);
  }

  /**
   * M�thode permettant de remplir le fichier tree.js pour l'�l�ment de pr�sentation
   * qui est en train d'�tre trait�. Il faut savoir si c'est une feuille de l'arbre ou
   * si c'est un dossier. On le sait gr�ce � l'�l�ment suivant et � l'id interne de
   * l'�l�ment suivant par rapport � l'id interne de l'�l�ment courant
   */
  protected void ecrireArbre() {
    String noeudParent = null;
    // sous la racine
    if (this.IDParent == 0) {
      noeudParent = "foldersTree";
    }
    else {
      noeudParent = "p_" + this.IDParent;
    }
    // on ajoute une feuille � l'arbre
    if (this.arbre.isFeuille()) {
      pwFicTree.println("docAux = insDoc(" + noeudParent + ", gLnk(\"R\",\" " + this.element.getNomPresentation() + "\", \"../" + this.getCheminRelatif() + "\"))");
      if (this.element.getNomIcone() != null) {
        pwFicTree.println("docAux.iconSrc = ICONPATH + \"" + this.element.getNomIcone() + "\"");
      }
    }
    else {
      // on ajoute un noeud
      pwFicTree.println("p_" + this.ID + " = insFld(" + noeudParent + ", gFld(\"" + this.element.getNomPresentation() + "\", \"../" + this.getCheminRelatif() + "\"))");
      if (this.element.getNomIcone() != null) {
        pwFicTree.println("p_" + this.ID + ".iconSrcClosed = ICONPATH + \"" + this.element.getNomIcone() + "\"");
        pwFicTree.println("p_" + this.ID + ".iconSrc = ICONPATH + \"" + this.element.getNomIcone() + "\"");
      }
    }
  }

  /**
   * Cr�er le fichier de contenu d'un �l�ment de pr�sentation simple, sans mod�le
   */
  protected void creerFichierDescription() throws IOException {
    File ficHTML = new File(this.cheminAbsolu);
    FileWriter fd = new FileWriter(ficHTML);

    fd.write("<HTML><head> <link rel='STYLESHEET' type='text/css' href='" + this.getCheminStyle() + "'>"
             + "</head>" + "<body><center>\n"
             + "<table width=\"84%\" align=\"center\">\n"
             + "<tr><td width=\"100%\" class=\"titrePage\">\n"
             + "<p align=\"center\" class=\"entete\">\n"
             + "<b>" + this.element.getNomPresentation() + "</b>\n"
             + "</p></td></tr></table></center><BR>\n");

    fd.write(getBarreNavigation() + "<br>");

    //modif 2XMI Amandine
    //affichage de la liste de guides
    if (! (this.getElementPresentation() instanceof Guide)) {
      this.ajouterGuidesAssocies(fd);
    }

    this.ajouterDescription(fd);
    //modif 2XMI S�bastien
    //ajout ds la page du chemin relatif
    File f = new File(GenerationManager.getInstance().getCheminGeneration());
    fd.write("<span class=\"cheminRelatif\" id=\"" + ficHTML.getAbsolutePath().substring(f.getAbsolutePath().length()+1,ficHTML.getAbsolutePath().length()) + "\"></span>");
    //fin modif 2XMI S�bastien
    this.ajouterContenu(fd);
    this.ajouterMail(fd);

    //modif 2XMI amandine
    //affichage de la date de derni�re modification d'un composant
    if ( (this.getElementPresentation() instanceof Guide)) {
      GElement gElement = this;
      String nomComposant = "";
      if (!this.arbre.isRacine()) {
        int niveau = 1;
        ArbreGeneration aux = this.arbre.getArbreParent();
        // on remonte jusqu'� la racine
        while (!aux.isRacine()) {
          nomComposant = aux.getElement().getElementPresentation().getNomPresentation();
          gElement = aux.getElement();
          for (int i = 0; i < niveau; i++) {
            nomComposant = "../" + nomComposant;
          }
          aux = aux.getArbreParent();
          niveau++;
        }
      }
      if (! (gElement instanceof GPaquetagePresentation)) {
        this.ajouterDerniereModif(fd);
      }
    }

    this.ajouterVersionDate(fd);
    this.ajouterPiedPage(fd);
    fd.write("</BODY></HTML>");
    fd.close();
    //modif 2XMI S�bastien
    //Transformation du fichier en XHTML pour permettre l'export en PDF
    TransformationXHTML transformateur = new TransformationXHTML();
    if (!transformateur.executer(ficHTML, this.getFichierDtd()))
    //si erreur on ajoute l'erreur ds la liste
    {
        ArbreGeneration.listeErreurs.add(Application.getApplication().getTraduction("ERR_Element_XHTML") + " - " + this.element.getNomPresentation() + " :\n" + transformateur.getErreur());
    }
    //fin modif S�bastien

  }

  /**
   * M�thode permettant d'ajouter le contenu d'un fichier en bas de la page en train
   * d'�tre construite
   * @param fd lien vers le fichier contenu � construire
   * @throws IOException
   */
  public void ajouterContenu(FileWriter fd) throws IOException {
    String contenu = this.element.getContenu();
    if (contenu != null) {
      //modif 2XMI S�bastien
      //ajout d'une balise <div class="contenu"> encapsulant le contenu
      //et correction de la balise </br> => <br />
      fd.write("<div class=\"contenu\">");
      fd.write("<hr><br />");
      //fin modif S�bastien

      //modif 2xmi Albert - gestion des liens non locaux
      //on regarde si le contenu commence par "http://"
      //si oui --> on cr�e un lien vers cette page internet
      //sinon on traite le lien local comme il se doit,
      //� savoir l'incorporer dans la page si il s'agit d'un fichier html
      //ou cr�e un lien en local vers ce fichier

      // d�but de la modif Albert - Gestion des liens non locaux
      //on teste si le contenu commence par "http://"
      //modif 2XMI Amandine
      //pris en compte des liens ftp et https
      try
      {
        URI urr = new URI(contenu.replaceAll("\\\\", "/"));
        if (urr.getScheme() == null){
          //Le contenu represente un fichier en local
          //Selon l'extension du fichier de contenu, recopie ou non le contenu de ce
          // fichier � la suite du contenu d�j� �crit
          // si le contenu est un fichier html
          if (contenu.toLowerCase().endsWith(".html") || contenu.toLowerCase().endsWith(".htm") || contenu.toLowerCase().endsWith(".txt")) // modif Albert sur condition de test !!!
          {
            // recopier le fichier � la suite de la description
            this.recopierContenu(contenu, fd);
          }
          else
          {
            // on cr�e un lien vers le fichier en local
            fd.write(Application.getApplication().getTraduction("WEB_LINK") + " : " + "<a href=\"" + this.getLienContenu() + "\" target=\"_new\" >" + this.element.getContenu() + "</a>");
          }
        }
        else{
          fd.write("<a href=\"" + contenu + "\" target=\"_blank\">" + Application.getApplication().getTraduction("WEB_LINK") + " " + contenu + "</a>");
        }
      }
      catch (URISyntaxException ex)
      {
      }
      //Fin modif 2xmi - gestion des liens non locaux


      //modif 2XMI S�bastien
      //fin balise div
      fd.write("</div>");
      //fin modif S�bastien
    }
  }

  /**
   * M�thode permettant d'�crire le code html correspondant au bouton mail � chaque bas de page
   * @param fd lien vers le fichier html de contenu
   * @throws IOException
   */
  public void ajouterMail(FileWriter fd) throws IOException {
    fd.write("<br><hr>");
    fd.write("<div align=\"center\" class=\"boutonemail\"><a href=\"mailto:"
             + Application.getApplication().getProjet().getDefProc().getEmailAuteur()
             + "?subject=" + this.getNomRacine() + " : " + this.element.getNomPresentation() + "\">"
             + Application.getApplication().getTraduction("WEB_MAIL") + "</A></div>");
  }

  /**
   * M�thode permettant d'�crire le code html correspondant � la date de g�n�ration � chaque bas de page
   * @param fd lien vers le fichier html de contenu
   * @throws IOException
   */
  public void ajouterVersionDate(FileWriter fd) throws IOException {
    fd.write("<div align=\"center\" class=\"date\">"
             + Application.getApplication().getTraduction("WEB_DATE_GEN")
             + " " + GenerationManager.getInstance().getDateGeneration() + "</div>");
  }

//ajout 2XMI
  /**
   * Methode permettant d'�crire le code html correspondant au pied de page du processus
   * @param fd FileWriter
   * @throws IOException
   */
  public void ajouterPiedPage(FileWriter fd) throws IOException
  {
      fd.write("<div align=\"center\" class=\"date\">"
             + Application.getApplication().getProjet().getDefProc().getPiedPage() + "</div>");

  }

  /**
   * M�thode permettant d'ajouter la description d'un �l�ment
   * @param fd
   * @throws IOException
   */
  public void ajouterDescription(FileWriter fd) throws IOException {
    if (!GenerationManager.getInstance().estInfoBulle()) {
      String description = this.element.getDescription();
      if (description != null) {
        fd.write("<br><div class=\"description\">" + description + "</div>\n");
      }
    }
  }

  /**
   * Modfi 2xmi AMANDINE
   * M�thode permettant d'obtenir la liste des guides associes a l'element courant
   * @param fd
   */
  public void ajouterGuidesAssocies(FileWriter fd) throws IOException {
    Vector guides = element.getGuides();
    if (guides.size() != 0) {
      fd.write("<div class=\"titreliste\">" + Application.getApplication().getTraduction("WEB_GUIDES") + "</div>\n");
      ElementPresentation element = this.getElementPresentation();
      for (int i = 0; i < guides.size(); i++) {
        fd.write("<div class=\"elementliste\"><a href=\"" + this.getLienCheminVersGuide( ( (ElementPresentation) guides.elementAt(i)).getCheminPage()) + "\" target=\"_new\" >" + ( (Guide) guides.elementAt(i)).getNomPresentation() + "</a></div>\n");
      }
    }
  }

  /**
   * Modfi 2xmi Amandine
   * M�thode permettant d'afficher la date de derniere modification d'un composant
   * pour chaque element composant ce composant
   * @param fd
   */
  public void ajouterDerniereModif(FileWriter fd) throws IOException {
    //recuperer le nom du composant parent
    String nomComposant = this.element.getNomPresentation();
    if (!this.arbre.isRacine()) {
      int niveau = 1;
      ArbreGeneration aux = this.arbre.getArbreParent();
      // on remonte jusqu'� la racine
      while (!aux.isRacine()) {
        nomComposant = aux.getElement().getElementPresentation().getNomPresentation();
        for (int i = 0; i < niveau; i++) {
          nomComposant = "../" + nomComposant;
        }
        aux = aux.getArbreParent();
        niveau++;
      }
    }

    //recuperer le referentiel pour les id composants
    Referentiel ref = Application.getApplication().getReferentiel();
    ElementReferentiel eltRef = ref.chercherElement(ref.nomComposantToId(nomComposant.substring(nomComposant.lastIndexOf("/") + 1, nomComposant.length())), ElementReferentiel.COMPOSANT);
    Date date = ElementReferentiel.FORMATEUR.parse(eltRef.getDatePlacement(), new ParsePosition(0));
    SimpleDateFormat formateur = new SimpleDateFormat("dd/MM/yyyy");
    String dateSite = formateur.format(date);
    fd.write("<div align=\"center\" class=\"date\">" + Application.getApplication().getTraduction("WEB_DATE_VER_COMP") + " [" + dateSite + "] " + "</div>");
  }

  /**
   * M�thode permettant de recopier le contenu associ� � l'�l�ment de pr�sentation
   * courant.
   * @param contenu nom du fichier de contenu associ� � l'�l�ment courant
   * @param fd lien vers le fichier de contenu courant � remplir
   */
  protected void recopierContenu(String contenu, FileWriter fd) throws IOException {
    String regexp = "src=\"images";
    Pattern modele = Pattern.compile(regexp);
    Matcher correspondance;

    File f = new File(this.getCheminContenu() + File.separator + contenu);
    if (f.exists()) {
      FileReader fr = new FileReader(f);
      BufferedReader br = new BufferedReader(fr);

      String ligne;
      char[] retourChariot = new char[] {
          Character.LINE_SEPARATOR};
      while ( (ligne = br.readLine()) != null) {
        // v�rifie si l'on a un lien vers une image
        correspondance = modele.matcher(ligne);
        if (correspondance.find()) {
          ligne = correspondance.replaceAll("src=\"" + this.getLienImage());
        }
        fd.write(ligne);
        fd.write(retourChariot);
      }
      br.close();
    }
    else {
      System.out.println("le fichier n'existe pas : " + f.getPath());
    }
  }

  public String toString() {
    return this.element.getNomPresentation();
  }

  /**
   * @return Returns the iD.
   */
  public long getID() {
    return ID;
  }

  /**
   * @param id The iD to set.
   */
  public void setID(long id) {
    ID = id;
  }

  /**
   * @param arbre The arbre to set.
   */
  public void setArbre(ArbreGeneration arbre) {
    this.arbre = arbre;
  }

  /**
   * M�thode appel�e que pour les paquetages de pr�sentation et les composants
   * @param paquet
   */
  public void extraireIconeContenu(PaquetagePresentation paquet) throws FileNotFoundException, IOException {
    GenerationManager.print(Application.getApplication().getTraduction("extraction_icone"));

    // Cr�er un flux d'entr�e contenant l'archive ZIP � d�compresser
    File f = new File(paquet.getNomFichier());
    FileInputStream fin = new FileInputStream(f);

    // Mettre ce flux en m�moire tampon
    BufferedInputStream bis = new BufferedInputStream(fin);

    // Identifier le flux tampon comme flux de compression ZIP
    ZipInputStream zin = new ZipInputStream(bis);

    // D�finir un objet ZipEntry
    ZipEntry ze = null;

    // Tant que cet objet est diff�rent de nul (tant qu'il y a des fichiers dans l'archive)...
    while ( (ze = zin.getNextEntry()) != null) {
      if (ze.toString().startsWith(paquet.getChemin_icone())
          || ze.toString().startsWith(paquet.getChemin_contenu())
          || ze.toString().startsWith(GenerationManager.IMAGES_PATH)) {
        String fichier = ze.toString();
        fichier = fichier.substring(fichier.indexOf("/") + 1, fichier.length());
        fichier = fichier.substring(fichier.indexOf("\\") + 1, fichier.length());
        FileOutputStream fout = null;
        if (ze.toString().startsWith(paquet.getChemin_icone())) {
          //System.out.println(GenerationManager.getInstance().getCheminGeneration()+ File.separator + CGenererSite.APPLET_PATH + File.separator + "images" + File.separator + fichier);
          // Cr�er un flux de sortie pour le fichier de l'entr�e courante
          fout = new FileOutputStream(GenerationManager.getInstance().getCheminGeneration()
                                      + File.separator + GenerationManager.APPLET_PATH + File.separator + "images" + File.separator + fichier);
        }
        else if (ze.toString().startsWith(paquet.getChemin_contenu())) {
          //System.out.println(Application.getApplication().getProjet().getDefProc().getRepertoireGeneration()+ "/" + CGenererSite.CONTENU_PATH + "/" + fichier);
          fout = new FileOutputStream(this.cheminParent + File.separator + GenerationManager.CONTENU_PATH + File.separator + fichier);
        }
        // les images
        else {
          //modif 2XMI pour la demande de SPWIZ : ajout de GenerationManager.CONTENU_PATH +
          fout = new FileOutputStream(this.cheminParent + File.separator + GenerationManager.CONTENU_PATH + File.separator + GenerationManager.IMAGES_PATH + File.separator + fichier);
        }
        ToolKit.dezipper(zin, fout);
      }
    }
    // Fermer le flux d'entr�e
    zin.close();
  }

  /**
   * Retourne le lien vers un id donn�
   */
  public String getLienChemin(IdObjetModele id) {
    String res = id.getChemin();
    if (res != null && res.length() > 2) {
      res = res.substring(2);
    }
    else {
      //System.out.println("Chemin : " + id);
    }
    //System.out.println("RES " + id.getChemin() + " : " + id);
    ArbreGeneration aux = this.arbre;
    // on remonte jusqu'� la racine
    while (!aux.isRacine()) {
      res = "../" + res;
      aux = aux.getArbreParent();
    }
    //System.out.println("REtour : " + res);
    return res;
  }

  /**
   * Retourne le lien vers un guide donn�
   */
  public String getLienCheminVersGuide(String chemin) {
    if (chemin != null && chemin.length() > 2) {
      chemin = chemin.substring(2);
    }
    else {

    }
    //System.out.println("RES " + chemin + " : " + chemin);
    ArbreGeneration aux = this.arbre;
    // on remonte jusqu'� la racine
    while (!aux.isRacine()) {
      chemin = "../" + chemin;
      aux = aux.getArbreParent();
    }
    //System.out.println("REtour : " + chemin);
    return chemin;
  }

  /**
   *
   */
  public void recenser() {
    Integer oldValue = (Integer) ArbreGeneration.mapCompteur.get("nbPagesTotal");
    ArbreGeneration.mapCompteur.put("nbPagesTotal", new Integer(oldValue.intValue() + 1));

    oldValue = (Integer) ArbreGeneration.mapCompteur.get("nbElementPresentation");
    ArbreGeneration.mapCompteur.put("nbElementPresentation", new Integer(oldValue.intValue() + 1));
  }
}
