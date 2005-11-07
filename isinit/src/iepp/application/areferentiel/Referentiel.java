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

import java.io.*;
import java.text.*;
import java.util.*;

import javax.swing.event.*;
import javax.swing.tree.*;

import iepp.*;
import iepp.application.*;
import iepp.domaine.*;
import iepp.infrastructure.jsx.*;
import util.*;

/**
 * Classe permettant la gestion du référentiel
 *
 * 2XMI utilise le nom du referentiel + nom de l'element pour l'export
 */
public class Referentiel
    extends Observable implements TreeModel {
  /**
   * Nom du référentiel
   */
  private String nomReferentiel;

  /**
   * Chemin où se situe le fichier référentiel
   */
  private String cheminReferentiel;

  /**
   * Dernier id attribué à un composant
   */
  private long lastId = -1;

  // HashMaps contenant les références et les id des composants ajoutés au diagramme

  /**
   * HashMap faisant le lien entre la référence d'un élément (Composant ou DP) et son id
   */
  private HashMap elementToId = new HashMap();

  /**
   * HashMap faisant le lien entre l'id d'un élément dans le référentiel et sa référence
   */
  private HashMap idToElement = new HashMap();

  /**
   * Arbre contenant les définitions processus et les composants
   */
  private DefaultTreeModel arbre;

  /**
   * Racine de l'arbre
   */
  private ElementReferentiel racine = null;

  /**
   * Noeud contenant tous les composants
   */
  private ElementReferentiel composants = null;

  /**
   * Noeud contenant toutes les DP
   */
  private ElementReferentiel dp = null;

  /**
   * Noeud contenant tous les éléments de présentation
   */
  private ElementReferentiel present = null;

  /**
   * Chemin du répository à utiliser
   */
  private String cheminRepository = null;

  // Constantes représentant les types possibles de mise à jour sur l'arbre
  // Utile pour les observateurs
  public static final int CHANGED = 0;
  public static final int ELEMENT_INSERTED = 1;
  public static final int ELEMENT_REMOVED = 2;
  private ElementReferentiel dernierComposantAjoute = null;

  /**
   * Crée le référentiel, à partir du fichier référentiel
   * @param nom : nom du référentiel
   * @throws FileNotFoundException
   * @throws FileNotFoundException
   */
  public Referentiel(String nom) throws Exception {
    File fic = new File(Application.getApplication().getConfigPropriete("chemin_referentiel") + File.separator + nom);
    //this.cheminRepository = ToolKit.removeSlashTerminatedPath(ToolKit.getAbsoluteDirectory(Application.getApplication().getConfigPropriete("chemin_referentiel")));
    this.cheminRepository = ToolKit.getAbsoluteDirectory(Application.getApplication().getConfigPropriete("chemin_referentiel"));

    if (fic.exists()) {}
    else {
      this.creerReferentiel(nom);
    }
  } // Fin constructeur Referentiel (String chemin)

  /**
   * @param cheminRef
   * @throws FileNotFoundException
   */
  public Referentiel(File cheminRef) throws Exception {
    this.cheminRepository = cheminRef.getParentFile().getParentFile().toString();
    this.cheminRepository = ToolKit.getAbsoluteDirectory(this.cheminRepository);
    this.chargerReferentiel(cheminRef.getName().substring(0, cheminRef.getName().lastIndexOf(".ref")), cheminRef.getParentFile().getName());
  }

  /**
   * Initialise le référentiel et crée un nouveau répertoire contenant le référentiel cité
   * @param nom : nom du référentiel à créer
   * @throws FileNotFoundException
   */
  public void creerReferentiel(String nom) throws Exception {
    // Affectation du nom à l'objet
    nomReferentiel = nom;

    // Id initialisé à 0
    lastId = 0;

    cheminReferentiel = this.cheminRepository + File.separator + nomReferentiel;

    // Création du répertoire du référentiel
    File rep = new File(cheminReferentiel);
    rep.mkdirs();

    // Création du répertoire des composants
    File repComp = new File(cheminReferentiel + File.separator + "Composants");
    repComp.mkdirs();

    // Création du répertoire des DP
    File repDP = new File(cheminReferentiel + File.separator + "DP");
    repDP.mkdirs();

    // Création du répertoire des DP
    File repPresent = new File(cheminReferentiel + File.separator + "Presentation");
    repPresent.mkdirs();

    try {
      // Création du fichier référentiel (vide)
      File ficRef = new File(cheminReferentiel + File.separator + nomReferentiel + ".ref");
      ficRef.createNewFile();

      RandomAccessFile raf = new RandomAccessFile(ficRef, "rw");

      raf.writeBytes("<Derniere ID> 0                               \n");
      raf.close();
    }
    catch (IOException e) {
      e.printStackTrace();
      ErrorManager.getInstance().displayError(e.getMessage());
    }

    // Après avoir créé le référentiel, on le charge
    this.chargerReferentiel(nom, nom);

  } // Fin méthode creerReferentiel

  /**
   * Charge le référentiel à partir du fichier référentiel
   * @param nom : nom du référentiel
   * @throws Exception
   */
  public void chargerReferentiel(String nom, String nomInter) throws Exception {
    RandomAccessFile raf;
    String ligne = null;
    String nomElt = null;
    ChargeurComposant chargeur;
    boolean ligneObsolete = false;

    // Initialisation du nom du référentiel
    nomReferentiel = nom;

    // Initialisation du chemin du référentiel
    cheminReferentiel = this.cheminRepository + File.separator + nomInter;

    // Création de la racine de l'arbre (racine) et des 2 noeuds principaux (composants et dp)
    racine = new ElementReferentiel(nomReferentiel, 0, cheminReferentiel, ElementReferentiel.REFERENTIEL);
    composants = new ElementReferentiel("Composants", 0, cheminReferentiel + File.separator + "Composants", ElementReferentiel.PAQ_COMP);
    dp = new ElementReferentiel("Définitions Processus", 0, cheminReferentiel + File.separator + "DP", ElementReferentiel.PAQ_DP);
    present = new ElementReferentiel("Présentation", 0, cheminReferentiel + File.separator + "Présentation", ElementReferentiel.PAQ_PRESENTATION);

    // Création de l'arbre avec pour racine le nom du référentiel
    arbre = new DefaultTreeModel(racine);

    // Ajout des 2 noeuds principaux: Composants et Définitions Processus
    racine.add(composants);
    racine.add(dp);
    racine.add(present);

    // Lecture du fichier référentiel
    raf = new RandomAccessFile(cheminReferentiel + File.separator + nomReferentiel + ".ref", "rw");

    ligne = raf.readLine();

    // Récupération du dernier ID attribué
    lastId = Long.parseLong(ligne.substring(ligne.lastIndexOf(">") + 2).trim());

    ligne = raf.readLine();

    while (ligne != null) {
      if (ligne.startsWith("CP:")) { //c'est un composant
        // Modif 2XMI jean : detection des anciens referentiels
        if (!verificationConformite(ligne)) {
          //la ligne ne correspond pas a la derniere version du referentiel
          ligneObsolete = true;
        }
        else {
          //la ligne est bien conforme

          // Modif Albert 2XMI
          int longueur = ligne.length();
          //version en fin de ligne
          String version = ligne.substring(longueur - (ElementReferentiel.LONGUEUR_VERSION + ElementReferentiel.LONGUEUR_DATEPLACEMENT + 1), longueur - (ElementReferentiel.LONGUEUR_DATEPLACEMENT + 1));
          //+1 a cause de chaque espace separateur

          String datePlacement = ligne.substring(longueur - ElementReferentiel.LONGUEUR_DATEPLACEMENT);

          // dans le fichier ce sont des liens relatifs au référentiel pour pouvoir le déplacer
          ligne = ToolKit.getConcatePath(this.cheminReferentiel, ligne.substring(3, longueur - (ElementReferentiel.LONGUEUR_VERSION + 1 + ElementReferentiel.LONGUEUR_DATEPLACEMENT + 1)));
          //+1 a cause de chaque espace separateur

          // Création du chargeur permettant de récupérer le nom de l'élément
          chargeur = new ChargeurComposant(ligne);

          //System.out.println("LIGNE : " + ligne);
          // Chargement du nom de l'élément à partir du fichier .pre pour les composants non vides
          nomElt = chargeur.chercherNomComposant(ligne);

          // si c'est un composant vide
          if (nomElt == null) {
            // Le nom du fichier du composant vide est celui du composant
            nomElt = this.extraireNomFichier(ligne);
          }
          this.getNoeudComposants().add(new ElementReferentiel(nomElt, this.extraireIdChemin(ligne), ligne, ElementReferentiel.COMPOSANT, version, datePlacement));
        }
      }
      else if (ligne.startsWith("PP:")) { //c'est un paquetage de presentation
        ligne = ToolKit.getConcatePath(this.cheminReferentiel, ligne.substring(3));
        if ( (this.extraireNomFichier(ligne)).equals("")) {
          throw new Exception();
        }
        this.getNoeudPresentation().add(new ElementReferentiel(this.extraireNomFichier(ligne), this.extraireIdChemin(ligne), ligne, ElementReferentiel.PRESENTATION));
      }
      else if (ligne.startsWith("DP:")) { //c'est une definition de processus
        ligne = ToolKit.getConcatePath(this.cheminReferentiel, ligne.substring(3));
        if ( (this.extraireNomFichier(ligne)).equals("")) {
          throw new Exception();
        }
        this.getNoeudDp().add(new ElementReferentiel(this.extraireNomFichier(ligne), this.extraireIdChemin(ligne), ligne, ElementReferentiel.DP));
      }
      ligne = raf.readLine();
    }
    // Fermeture du flux
    raf.close();

    if (ligneObsolete) {
      //informe que le referentiel est un ancien referentiel
      ErrorManager.getInstance().display("ERR", "ERR_Fic_Ref_Maj2_0");
      throw new Exception("referentiel_ancien_format");
    }
  }

  /*
   * Verifie si la ligne passee en parametre est conforme a la derniere version
   * du referentiel
   * @return true si la ligne est conforme,
   * false si la ligne correspond à un ancien referentiel
   */
  public boolean verificationConformite(String _ligne) {
    //Referentiel v1.0 : version IEPP 1.1.7
    //Referentiel v2.0 : ajout de version et datePlacement aux lignes CP
    int longueur = _ligne.length();
    if (_ligne.startsWith("CP:")) { //c'est un composant

      String espaceVersion = _ligne.substring(longueur - (ElementReferentiel.LONGUEUR_VERSION + 1 + ElementReferentiel.LONGUEUR_DATEPLACEMENT + 1), longueur - (ElementReferentiel.LONGUEUR_DATEPLACEMENT + 1));
      //+1 a cause de chaque espace separateur
      //espaceVersion doit commencer par un espace
      if (!espaceVersion.startsWith(" ")) {
        return false;
      }

      String espaceDate = _ligne.substring(longueur - (ElementReferentiel.LONGUEUR_DATEPLACEMENT + 1));
      //+1 a cause de chaque espace separateur
      //espaceDate doit commencer par un espace
      if (!espaceDate.startsWith(" ")) {
        return false;
      }
      //espaceDate doit etre une date valide
      if (ElementReferentiel.FORMATEUR.parse(espaceDate, new ParsePosition(1)) == null) {
        return false;
      }
      //new ParsePosition(1): on ne commence pas a 0 car on ignore l'espace de debut
      //parse retourne null si la date n'est pas valide
    }
    return true;
  }

  /**
   * Ajoute un élément au référentiel (physique et logique).
   * Pour un composant ou une présentation, mettre le chemin absolu du fichier de l'élément à ajouter
   * Pour une définition processus, mettre UNIQUEMENT le nom de la DP à créer
   * Pour un composant vide, mettre UNIQUEMENT le nom du composant vide à créer
   * @param cheminComp : chemin de l'élément sur le disque
   * @param type : type de l'élément (ElementReferentiel.COMPOSANT, ElementReferentiel.COMPOSANT_VIDE, ElementReferentiel.DP ou ElementReferentiel.PRESENTATION)
   * @return l'id de l'élément ajouté, -1 en cas d'erreur, -2 si le fichier choisi n'est pas de bon type, -3 si le composant est deja insere
   */
  public long ajouterElement(String chemin, int type, String version, String dateDePlacement) {
    String ligne = null;
    String nomRep = null;
    String nomElt = null;
    String pathFic = null;
    File fic = null;

    // Nom du path différent selon le type de l'élément
    switch (type) {
      case (ElementReferentiel.COMPOSANT):
      case (ElementReferentiel.COMPOSANT_VIDE):
        nomRep = "Composants";
        break;

      case (ElementReferentiel.DP):
        nomRep = "DP";
        break;

      case (ElementReferentiel.PRESENTATION):
        nomRep = "Presentation";
        break;
    }

    // Création du path où sera copié le fichier
    pathFic = cheminReferentiel + File.separator + nomRep + File.separator + this.getIdPourNouvelElement();

    try {
      // Création du répertoire où sera copié le fichier de l'élément
      fic = new File(pathFic);
      fic.mkdirs();

      // Ajoute l'élément dans l'arbre
      switch (type) {
        case (ElementReferentiel.COMPOSANT_VIDE):
        case (ElementReferentiel.COMPOSANT):
          if (type == ElementReferentiel.COMPOSANT) {

            // Création du chargeur permettant de récupérer le nom de l'élément
            ChargeurComposant chargeur = new ChargeurComposant(chemin);

            nomElt = chargeur.chercherNomComposant(chemin);

            // Permet de savoir si le fichier est un composant ou pas
            // Renvoie -2 si c'est pas le cas
            if (nomElt == null) {
              // Supprimer le repertoire cree
              fic.delete();
              return -2;
            }
            /*
                                                             else
                                                             {
                    nomElt = this.extraireNomFichier(chemin);
                                                             }
             */

            // Verifie que ce nom n'existe pas deja
            if (nomComposantToId(nomElt) != -1) {
              // Supprimer le repertoire cree
              fic.delete();
              return -3;
            }

            //Copie du fichier dans le référentiel (création de son répertoire + attribution de l'ID)
            pathFic += File.separator + nomElt + ".pre";
            Copie.copieFicCh(chemin, pathFic);

          }
          // cas des composants vides
          else {
            // Création du répertoire où sera copié le fichier de l'élément
            pathFic += File.separator + chemin + ".apes";
            // sauvegarderInterface(pathFic);
            fic = new File(pathFic);
            fic.createNewFile();

            // Le nom du fichier du composant vide est celui du fichier
            nomElt = this.extraireNomFichier(pathFic);
          }
          this.getNoeudComposants().add(new ElementReferentiel(nomElt, this.getLastId(), pathFic, ElementReferentiel.COMPOSANT, version, dateDePlacement));
          break;

        case (ElementReferentiel.DP):

          // Création du fichier DP
          pathFic += File.separator + chemin + ".iepp";
          CSauvegarderDP c = new CSauvegarderDP(pathFic);
          c.executer();

          // On récupère le nom du fichier de l'élément
          nomElt = this.extraireNomFichier(pathFic);

          // Verifie que ce nom n'existe pas deja
          if (nomDefProcToId(nomElt) != -1) {
            // Supprimer le repertoire et les fichiers crees
            if (fic.exists()) {
              File[] files = fic.listFiles();
              files[0].delete();
            }
            fic.delete();

            return -3;
          }

          this.getNoeudDp().add(new ElementReferentiel(nomElt, this.getLastId(), pathFic, ElementReferentiel.DP));
          break;

        case (ElementReferentiel.PRESENTATION):

          // Création du chargeur permettant de récupérer le nom de la présentation
          ChargeurPaquetagePresentation chargeur = new ChargeurPaquetagePresentation(chemin);

          nomElt = chargeur.chercherNomPresentation(chemin);

          // Permet de savoir si le fichier est un composant ou pas
          // Renvoie -2 si c'est pas le cas
          if (nomElt == null) {
            // Supprimer le repertoire cree
            fic.delete();
            return -2;
          }

          /*
                                                   else
                                                   {
                  nomElt = this.extraireNomFichier(chemin);
                                                   }
           */
          // Verifie que ce nom n'existe pas deja
          if (nomPresentationToId(nomElt) != -1) {
            // Supprimer le repertoire cree
            fic.delete();
            return -3;
          }

          //Copie du fichier dans le référentiel (création de son répertoire + attribution de l'ID)
          pathFic += File.separator + nomElt + ".pre";
          Copie.copieFicCh(chemin, pathFic);

          this.getNoeudPresentation().add(new ElementReferentiel(nomElt, this.getLastId(), pathFic, ElementReferentiel.PRESENTATION));
          break;
      }

      // Insertion du path de l'élément dans le fichier referentiel
      RandomAccessFile raf = new RandomAccessFile(cheminReferentiel + File.separator + nomReferentiel + ".ref", "rw");

      // On va en fin de fichier
      raf.seek(raf.length());

      //	Ajoute l'élément dans l'arbre

      switch (type) {
        case (ElementReferentiel.COMPOSANT_VIDE):
        case (ElementReferentiel.COMPOSANT): {
          raf.writeBytes("CP:");
          ElementReferentiel er = this.chercherElement(this.getLastId(), ElementReferentiel.COMPOSANT);
          this.dernierComposantAjoute = this.chercherElement(this.getLastId(), ElementReferentiel.COMPOSANT);
          raf.writeBytes("./" + ToolKit.removeSlashTerminatedPath(ToolKit.getRelativePathOfAbsolutePath(pathFic, this.cheminReferentiel)) + " " + er.getVersionRef() + " " + er.getDatePlacement() + "\n");
        }
        break;
        case (ElementReferentiel.DP): {
          raf.writeBytes("DP:");
          raf.writeBytes("./" + ToolKit.removeSlashTerminatedPath(ToolKit.getRelativePathOfAbsolutePath(pathFic, this.cheminReferentiel)) + "\n");
        }
        break;
        case (ElementReferentiel.PRESENTATION): {
          raf.writeBytes("PP:");
          raf.writeBytes("./" + ToolKit.removeSlashTerminatedPath(ToolKit.getRelativePathOfAbsolutePath(pathFic, this.cheminReferentiel)) + "\n");
        }
        break;
      }

      // Fermeture du flux
      raf.close();

      // MAJ du fichier référentiel
      this.majDerniereIdDansFichier(this.getLastId());

      // Notifie aux observateurs qu'un élément a été ajouté
      this.majObserveurs(ELEMENT_INSERTED);

      // On retourne l'id de l'élément ajouté
      return this.getLastId();
    }
    catch (Exception e) {
      e.printStackTrace();
      ErrorManager.getInstance().displayError(e.getMessage());
      // Supprimer le repertoire cree
      fic.delete();
      return -1;
    }
  }

  public long ajouterElement(String chemin, int type) {
    Date dateCourante = new Date();
    String _date = ElementReferentiel.FORMATEUR.format(dateCourante);
    return (ajouterElement(chemin, type, ElementReferentiel.DEFAUT_VERSION, _date));
  }

  /**
   * Supprime l'élément désigné du référentiel (physique + logique).
   * @param idElt : l'identifiant de l'élément à supprimer
   * @param type : type de l'élément (ElementReferentiel.COMPOSANT, ElementReferentiel.DP ou ElementReferentiel.PRESENTATION)
   * @return booléen indiquant si la suppression s'est bien passée
   */
  public boolean supprimerElement(long idElt, int type) {
    ElementReferentiel feuille = null;
    ElementReferentiel noeud = null;
    RandomAccessFile source = null;
    RandomAccessFile dest = null;
    String ligne = null;
    String nomRep = null;
    File fic = null;
    boolean suppressionOk = true;

    switch (type) {
      case (ElementReferentiel.COMPOSANT):
        feuille = this.chercherElement(idElt, ElementReferentiel.COMPOSANT);
        noeud = this.getNoeudComposants();
        nomRep = "Composants";
        break;

      case (ElementReferentiel.DP):
        feuille = this.chercherElement(idElt, ElementReferentiel.DP);
        noeud = this.getNoeudDp();
        nomRep = "DP";
        break;

      case (ElementReferentiel.PRESENTATION):
        feuille = this.chercherElement(idElt, ElementReferentiel.PRESENTATION);
        noeud = this.getNoeudPresentation();
        nomRep = "Presentation";
        break;
    }

    if (feuille != null) {
      // Suppression du fichier
      fic = new File(feuille.getChemin());
      suppressionOk = fic.delete();

      // Si la suppression du fichier ne se fait pas, on retourne false
      if (suppressionOk == false) {
        return false;
      }

      // Suppression du répertoire où se trouvait le fichier de l'élément
      fic = new File(cheminReferentiel + File.separator + nomRep + File.separator + idElt);
      fic.delete();

      // Suppression du path du fichier de l'élément dans le fichier referentiel
      try {
        // Copie du fichier sans la ligne désirée dans un autre fichier
        source = new RandomAccessFile(cheminReferentiel + File.separator + nomReferentiel + ".ref", "rw");
        dest = new RandomAccessFile(cheminReferentiel + File.separator + nomReferentiel + "2.ref", "rw");

        // ne pas prendre en compte la premiere ligne
        ligne = source.readLine();

        String aux = "";

        // On recopie le fichier en omettant la ligne contenant le path de l'élément à effacer
        while (ligne != null) {
          if (!aux.startsWith("./" + ToolKit.removeSlashTerminatedPath(ToolKit.getRelativePathOfAbsolutePath(
              feuille.getChemin(), this.cheminReferentiel)))) {
            dest.writeBytes(ligne + "\n");
          }

          // On passe à la ligne suivante
          ligne = source.readLine();

          if (ligne != null) {
            //	 enlever les DP: ou CP: ou PP:
            aux = ligne.substring(3);
          }
        }

        source.close();
        dest.close();

        // Suppression de l'ancien fichier
        fic = new File(cheminReferentiel + File.separator + nomReferentiel + ".ref");
        fic.delete();

        // On renomme le fichier modifié avec le nom du fichier qu'on vient de supprimer
        fic = new File(cheminReferentiel + File.separator + nomReferentiel + "2.ref");
        fic.renameTo(new File(cheminReferentiel + File.separator + nomReferentiel + ".ref"));

        // Suppression de l'élément dans l'arbre
        noeud.remove(feuille);

        // Notifie aux observateurs qu'un composant a été supprimé
        this.majObserveurs(ELEMENT_REMOVED);

        return true;
      }
      catch (IOException e) {
        e.printStackTrace();
        ErrorManager.getInstance().displayError(e.getMessage());
        return false;
      }
    }
    else {
      System.out.println("Element inexistant dans le référentiel.");
      return false;
    }
  }

  /**
   * Charge un composant publiable du référentiel en mémoire
   * @param idComp identifiant du composant du référentiel à charger
   * @return le composant chargé en mémoire
   */
  public ComposantProcessus chargerComposant(long idComp) {
    TaskMonitorDialog dialogAvancee;
    ElementReferentiel eltRef = this.chercherElement(idComp, ElementReferentiel.COMPOSANT);

    // Construction du chargeur de composant qui est aussi une tache
    ChargeurComposant chargeur = new ChargeurComposant(eltRef.getChemin());

    // Affiche la boîte d'avancement
    dialogAvancee = new TaskMonitorDialog(Application.getApplication().getFenetrePrincipale(), chargeur);
    dialogAvancee.setTitle(Application.getApplication().getTraduction("Chargement"));
    chargeur.setTask(dialogAvancee);
    dialogAvancee.show();

    // Charger le composant processus et le mettre dans un composant
    ComposantProcessus comp = chargeur.getComposantCharge();
    HashMap presentation = chargeur.getMapPresentation();

    // Reinitialiser le chemin de fichier du composant
    //comp.setNomFichier(this.chercherElement(this.nomComposantToId(comp.toString()),ElementReferentiel.COMPOSANT).getChemin());

    // Remplis les HashMap avec la référence du composant en clé et l'id du composant en valeur
    this.ajouterReferenceMemoire(comp, idComp);

    // on a bien récupéré un composant
    if (comp != null) {
      // on vérifie si c'est un composant vide, auquel cas pas besoin de l'initialiser
      if (!comp.estVide()) {
        comp.initialiser(presentation);
      }
      return comp;
    }
    else {
      // Si une erreur s'est produite
      return null;
    }
  }

  /**
   * Charge un projet contenant une définition processus du référentiel en mémoire
   * @param idDP identifiant de la définition de processus du référentiel à charger
   * @param listeComposant liste des composants qui sont dans la dp mais ne sont plus dans le référentiel
   * @return le projet chargé en mémoire ou null
   */
  public Projet chargerDefProc(long idDp, Vector listeComposant) {
    TaskMonitorDialog dialogAvancee;
    ElementReferentiel eltRef = this.chercherElement(idDp, ElementReferentiel.DP);
    Projet projet = null;
    DefinitionProcessus def = null;
    Vector tab = null;
    IdObjetModele id = null;
    ComposantProcessus comp;
    long idCompo, idPresent = 0;
    PaquetagePresentation paquet = null;

    // Initialisation du chargeur
    ChargeurDP chargeur = new ChargeurDP(new File(eltRef.getChemin()));

    // Affiche la boîte d'avancement
    dialogAvancee = new TaskMonitorDialog(Application.getApplication().getFenetrePrincipale(), chargeur);
    dialogAvancee.setTitle(Application.getApplication().getTraduction("Chargement"));
    chargeur.setTask(dialogAvancee);
    dialogAvancee.show();

    // Réinitialise les associations id-références
    this.supprimerTousLesElementsCharges();

    // Charge la DP et renvoie si le projet construit à partir de la DP si le chargement s'est bien passé
    projet = chargeur.getProgetCharge();

    // On récupère la définition processus du projet
    def = projet.getDefProc();
    //modif 2XMI
    //si le processus chargé ne contient pas de pied de page on le crée
    if(def.getPiedPage()==null)
    {
        def.creerPiedPageVide();
    }

    // On récupère la liste des composants et paquetage utilisés dans cette définition processus
    tab = def.getListeAGenerer();

    // Ajout de la référence et de l'id de la définition processus dans les HashMap
    this.ajouterReferenceMemoire(projet.getDefProc(), idDp);

    // On remplis les Hashmap avec les références et les id des composants chargés
    for (int i = 0; i < tab.size(); i++) {
      // on s'occupe d'un composant
      if (tab.get(i) instanceof IdObjetModele) {
        // On récupère l'IdObjetModele du composant
        id = (IdObjetModele) tab.get(i);

        // Grâce à laquelle on retrouve l'objet Composant
        comp = (ComposantProcessus) id.getRef();

        // si c'est un composant vide on ne vérifie pas
        if (comp.getNomFichier() != null) {
          // Reinitialisation du nom de fichier du composant
          ElementReferentiel eRef = this.chercherElement(this.nomComposantToId(comp.toString()), ElementReferentiel.COMPOSANT);
          if (eRef != null) {
            comp.setNomFichier(eRef.getChemin());
            comp.getPaquetage().setNomFichier(eRef.getChemin());
            // On cherche dans le référentiel l'id correspondant au nom du composant
            idCompo = this.nomComposantToId(this.extraireNomFichier(comp.getNomFichier()));
          }
          else {
            idCompo = -1;
          }

          // si renvoie -1, on essaye de charger un composant qui a été supprimé du référentiel
          if (idCompo == -1) {
            // on l'ajoute à la liste des composants à supprimer
            listeComposant.addElement(comp.getIdComposant());
          }
          else {
            // Remplis les HashMap avec la référence du composant en clé et l'id du composant en valeur
            this.ajouterReferenceMemoire(comp, idCompo);
          }
        }
        else {
          idCompo = this.nomComposantToId(comp.getNomComposant());
          // Remplis les HashMap avec la référence du composant en clé et l'id du composant en valeur
          this.ajouterReferenceMemoire(comp, idCompo);
        }
      }
      // c'est un paquetage de présentation qu'il faut aussi vérifier
      else {
        // on récupère le paquetage
        paquet = (PaquetagePresentation) tab.get(i);

        // Reinitialisation du nom de fichier du composant
        ElementReferentiel eRef = this.chercherElement(this.nomPresentationToId(paquet.toString()), ElementReferentiel.PRESENTATION);
        if (eRef != null) {
          paquet.setNomFichier(eRef.getChemin());
          // On cherche dans le référentiel l'id correspondant au nom du composant
          idPresent = this.nomPresentationToId(this.extraireNomFichier(paquet.getNomFichier()));
        }
        else {
          idPresent = -1;
        }

        //On cherche dans le référentiel l'id correspondant au paquetage
        idPresent = this.nomPresentationToId(this.extraireNomFichier(paquet.getNomFichier()));

        // si renvoie -1, on essaye de charger un composant qui a été supprimé du référentiel
        if (idPresent == -1) {
          // on l'ajoute à la liste des composants à supprimer
          listeComposant.addElement(paquet);
        }
      }
    }

    return projet;
  }

  /**
   * Charge un paquetage de présentation du référentiel en mémoire
   * @param idPres : identifiant du paquetage de présentation du référentiel à charger
   * @return le paquetage de présentation chargé en mémoire
   */
  public PaquetagePresentation chargerPresentation(long idPres) {
    TaskMonitorDialog dialogAvancee = null;
    ElementReferentiel feuille = null;

    feuille = this.chercherElement(idPres, ElementReferentiel.PRESENTATION);

    // Construction du chargeur de composant qui est aussi une tache
    ChargeurPaquetagePresentation chargeur = new ChargeurPaquetagePresentation(feuille.getChemin());

    // Affiche la boîte d'avancement
    dialogAvancee = new TaskMonitorDialog(Application.getApplication().getFenetrePrincipale(), chargeur);
    dialogAvancee.setTitle(Application.getApplication().getTraduction("Chargement"));

    chargeur.setTask(dialogAvancee);
    dialogAvancee.show();

    // Remplis les HashMap avec la référence du composant en clé et l'id du composant en valeur
    PaquetagePresentation paq = chargeur.getPaquetageCharge();
    this.ajouterReferenceMemoire(paq, idPres);

    // Reinitialiser le chemin
    //paq.setNomFichier(this.chercherElement(this.nomComposantToId(paq.toString()),ElementReferentiel.COMPOSANT).getChemin());

    // Retourne le paquetage de présentation
    return paq;
  }

  /**
   * Met à jour la sauvegarde physique d'une définition de processus dans le référentiel.
   * @param comp définition de processus à sauvegarder dans le référentiel (mettre à jour)
   */
  public void sauverDefProc(DefinitionProcessus defProc) {
    ElementReferentiel feuille;
    long idElt;

    //System.out.println(defProc);

    // On récupère l'id de la définition processus dans la HashMap grâce à la référence de l'objet
    idElt = ( (Long) elementToId.get(defProc)).longValue();

    // On va chercher la définition processus correspondante dans l'arbre
    feuille = this.chercherElement(idElt, ElementReferentiel.DP);

    // On sauve la définition processus à l'endroit où elle est stockée dans le référentiel
    CSauvegarderDP saveDp = new CSauvegarderDP(feuille.getChemin());
    saveDp.executer();

  }

  /**
   *
   */
  public void sauverComposantVide(ComposantProcessus cp) {
    ElementReferentiel feuille;
    long idElt;

    //System.out.println(cp);

    // On récupère l'id de la définition processus dans la HashMap grâce à la référence de l'objet
    idElt = ( (Long) elementToId.get(cp)).longValue();

    //System.out.println(idElt);

    // On va chercher le composant vide correspondant dans l'arbre
    feuille = this.chercherElement(idElt, ElementReferentiel.COMPOSANT_VIDE);

    // On sauve la définition processus à l'endroit où elle est stockée dans le référentiel
    CEnregistrerInterface saveCp = new CEnregistrerInterface(cp.getIdComposant());
    saveCp.sauvegarderInterface(feuille.getChemin());
    //saveCp.executer();

  }

  //------------------------------------------------------------------//
  //					  Accesseurs et modificateurs                   //
  //------------------------------------------------------------------//

  /**
   * Retourne le nom du référentiel
   */
  public String getNomReferentiel() {
    return nomReferentiel;
  }

  /**
   * Retourne le chemin physique où se situe le référentiel
   */
  public String getCheminReferentiel() {
    return cheminReferentiel;
  }

  /**
   * Retourne le dernier Id attricbué
   */
  public long getLastId() {
    return lastId;
  }

  /**
   * Retourne l'id pour l'élément à ajouter dans le référentiel
   */
  public long getIdPourNouvelElement() {
    return (++lastId);
  }

  //modif 2XMI jean
  //id du composant
  //les espaces sont remplacés par des '_'
  //id du type : Nom_reférentiel-Nom_composant
  public String getIdReferentielComposant(ComposantProcessus _ComposantProcessus) {
    return new String(this.getNomReferentiel().replace(' ', '_') + "-" + (this.chercherElement(this.nomComposantToId(_ComposantProcessus.getNomComposant()), ElementReferentiel.COMPOSANT)).getNomElement().replace(' ', '_'));
  }

  //id du processus
  //les espaces sont remplacés par des '_'
  //id du type : Nom_reférentiel-Nom_processus
  public String getIdReferentielDefProc(DefinitionProcessus _DefinitionProcessus) {
    return new String(this.getNomReferentiel().replace(' ', '_') + "-" + _DefinitionProcessus.getNomDefProc().replace(' ', '_'));//this.chercherElement(this.chercherId(_DefinitionProcessus), ElementReferentiel.DP).getNomElement().replace(' ', '_'));
  }

  //fin modif 2XMI jean

  /**
   * Retourne l'arborescence des éléments du référentiel
   */
  public DefaultTreeModel getArbre() {
    return arbre;
  }

  /**
   * Retourne le noeud des composants
   */
  public ElementReferentiel getNoeudComposants() {
    return composants;
  }

  /**
   * Retourne le noeud des composants
   */
  public ElementReferentiel getNoeudDp() {
    return dp;
  }

  /**
   * Retourne le noeud des presentations
   */
  public ElementReferentiel getNoeudPresentation() {
    return present;
  }

  /**
   * Renvoie le ieme fils du noeud composant
   * @param index : numéro du fils que l'on recherche
   * @return le composant recherché
   */
  public ElementReferentiel getComposantAt(int index) {
    return ( (ElementReferentiel)this.getNoeudComposants().getChildAt(index));
  }

  /**
   * Renvoie le ieme fils du noeud DP
   * @param index : numéro du fils que l'on recherche
   * @return le DP recherché
   */
  public ElementReferentiel getDefProcAt(int index) {
    return ( (ElementReferentiel)this.getNoeudDp().getChildAt(index));
  }

  /**
   * Renvoie le ieme fils du noeud presentation
   * @param index : numéro du fils que l'on recherche
   * @return l'elt de présentation recherché
   */
  public ElementReferentiel getPresentationAt(int index) {
    return ( (ElementReferentiel)this.getNoeudPresentation().getChildAt(index));
  }

  /**
   * Modifie le nom du référentiel
   */
  public void setNomReferentiel(String nom) {
    nomReferentiel = nom;
    // Notifie aux observateurs que l'arbre a été modifié
    this.majObserveurs(CHANGED);
  }

  /**
   * Incrémente le dernier Id attribué
   */
  public void incrLastId() {
    lastId++;
  }

  /**
   * Retourne l'id correspondant à la référence de l'objet dans la HashMap ayant pour clés les références des objets
   * Retourne -1 si la référence n'est pas trouvée dans la HashMap
   * @param obj : la référence de l'objet
   * @return l'id de l'objet dans le référentiel ou -1 si la référence n'est pas trouvée
   */
  public long chercherId(Object obj) {
    Long idElt;
    idElt = (Long) elementToId.get(obj);
    if (idElt == null) {
      return -1;
    }
    else {
      return idElt.longValue();
    }
  }

  /**
   * Retourne la référence de l'objet correspondant à l'id dans la HashMap ayant pour clés les id des éléments
   * Retourne null si la référence n'est pas trouvée dans la HashMap
   * @param idElt : l'id de l'objet
   * @return la référence de l'objet ou null si la référence n'est pas trouvée
   */
  public Object chercherReference(long idElt) {
    Long id = new Long(idElt);

    // Retourne la référence de l'objet
    return idToElement.get(id);
  }

  /**
   * Ajoute aux listes du référentiel une association id-référence
   * @param obj référence sur l'objet à noter
   * @param id identifiant de l'objet dans le référentiel
   */
  public void ajouterReferenceMemoire(Object obj, long id) {
    elementToId.put(obj, new Long(id));
    idToElement.put(new Long(id), obj);
  }

  /**
   * Supprime les entrées dans les 2 HashMap correspondant à l'élément qu'on veut supprimer du graphe
   * @param obj : la référence de l'objet
   */
  public void supprimerElementEnMemoire(Object obj) {
    Long idElt;

    // On récupère l'id pour pouvoir supprimer la l'entrée dans la 2e HashMap
    idElt = (Long) elementToId.get(obj);

    // Supression de l'entrée dans la 1e HashMap
    elementToId.remove(obj);

    // Supression de l'entrée dans la 2e HashMap
    idToElement.remove(idElt);
  }


  /**
   * Supprime toutes les entrées dans les 2 HashMap
   */
  public void supprimerTousLesElementsCharges() {
    // Vide la 1e HashMap
    elementToId.clear();

    // Vide la 2e HashMap
    idToElement.clear();
  }

  /**
   * Indique si l'objet ayant l'IdObjetModele passé en paramètre est déjà chargé dans la définition processus en cours
   * @param obj : la référence de l'objet
   */
  public boolean isDejaCharge(IdObjetModele idObj) {
    ComposantProcessus comp;
    Vector tab;
    int ind;

    // On récupère le contenu du HashMap dans un vecteur
    tab = (Vector) elementToId.values();

    // On parcours le vecteur
    for (ind = 0; ind < tab.size(); ind++) {
      // On récupère le composant processus à partir de sa référence
      comp = ( (ComposantProcessus) tab.elementAt(ind));
      // Si les 2 objets ont le même IdObjetModele, on retourne vrai
      if (idObj == comp.getIdComposant()) {
        return true;
      }
    }
    // Cas où l'on ne trouve pas la correspondance
    return false;
  }

  /**
   * Cherche l'élément correspondant à l'id donnée dans l'arbre
   * @param idComp : Id de l'élément
   * @param type : Chaîne de caractères permettant de savoir quel type d'élément (ElementReferentiel.COMPOSANT, ElementReferentiel.DP ou ElementReferentiel.PRESENTATION)
   * est recherché ("DP" pour une définition processus et "Composant" pour un composant)
   * @return l'ElementReferentiel recherché
   */
  public ElementReferentiel chercherElement(long idComp, int type) {
    ElementReferentiel feuille;
    boolean trouve = false;
    int indice = 0;

    if (type == ElementReferentiel.COMPOSANT || type == ElementReferentiel.COMPOSANT_VIDE) {
      // Sélectionne la 1e feuille de la branche des DP
      feuille = this.getComposantAt(indice++);
    }
    else {
      if (type == ElementReferentiel.DP) {
        // Sélectionne la 1e feuille de la branche des composants
        feuille = this.getDefProcAt(indice++);
      }
      else { // type == PRESENTATION
        // Sélectionne la 1e feuille de la branche des composants
        feuille = this.getPresentationAt(indice++);
      }

    }

    while ( (feuille != null) && (trouve == false)) {
      // Comparaison de l'ID de la feuille avec l'ID passé en paramètre
      if (feuille.getIdElement() == idComp) {
        trouve = true;
      }
      else {
        // On passe au composant suivant
        feuille = (ElementReferentiel) feuille.getNextLeaf();
      }
    }

    if (trouve == true) {
      return feuille;
    }
    else {
      return null;
    }
  }

  /**
   * Retourne la liste de tous les noms des éléments (dont le type est passé en paramètre) présents dans le référentiel
   * @param type : type des éléments dont on veut le nom (ElementReferentiel.COMPOSANT, ElementReferentiel.DP ou ElementReferentiel.PRESENTATION)
   * @return la liste des noms des éléments sous forme de vector
   */
  public Vector getListeNom(int type) {
    Vector tab = new Vector();
    ElementReferentiel feuille = null;
    int indice = 0;

    try {
      do {
        // On récupère les éléments de l'arbre
        switch (type) {
          case (ElementReferentiel.COMPOSANT):
            feuille = this.getComposantAt(indice++);
            break;
          case (ElementReferentiel.DP):
            feuille = this.getDefProcAt(indice++);
            break;
          case (ElementReferentiel.PRESENTATION):
            feuille = this.getPresentationAt(indice++);
            break;
        }

        // Ajout du nom de l'élément dans le vecteur
        tab.add(feuille.getNomElement());
      }
      while (feuille != null);

      return tab;
    }
    catch (ArrayIndexOutOfBoundsException e) {
      return tab;
    }
  }

  /**
   * Retourne l'id de la définition processus dont on a passé le nom en paramètre
   * @param nom : nom de la définition processus
   * @return l'id de la définition processus ou -1 si le nom ne correspond à aucune DP du référentiel
   */
  public long nomDefProcToId(String nom) {
    ElementReferentiel feuille = null;
    int indice = 0;

    try {
      do {
        // On consulte les différentes DP
        feuille = this.getDefProcAt(indice++);

        // Si les noms correspondent, on renvoie l'id
        if (feuille.getNomElement().equalsIgnoreCase(nom)) {
          return feuille.getIdElement();
        }
      }
      while (feuille != null);

      // Si pas de correspondance, on renvoie -1
      return -1;
    }
    catch (ArrayIndexOutOfBoundsException e) {
      return -1;
    }
  }

  /**
   * Retourne l'id du composant dont on a passé le nom en paramètre
   * @param nom : nom du composant
   * @return l'id du composant ou -1 si le nom ne correspond à aucun composant du référentiel
   */
  public long nomComposantToId(String nom) {
    ElementReferentiel feuille = null;
    int indice = 0;

    try {
      do {
        // On consulte les différentes DP
        feuille = this.getComposantAt(indice++);

        // Si les noms correspondent, on renvoie l'id
        if (feuille.getNomElement().equalsIgnoreCase(nom)) {
          return feuille.getIdElement();
        }
      }
      while (feuille != null);

      // Si pas de correspondance, on renvoie -1
      return -1;
    }
    catch (ArrayIndexOutOfBoundsException e) {
      return -1;
    }
  }

  /**
   * Retourne l'id du paquetage de présentation dont on a passé le nom en paramètre
   * @param nom : nom du paquetage de présentation
   * @return l'id du paquetage ou -1 si le nom ne correspond à aucun paquetage du référentiel
   */
  public long nomPresentationToId(String nom) {
    ElementReferentiel feuille = null;
    int indice = 0;

    try {
      do {
        // On consulte les différentes présentations
        feuille = this.getPresentationAt(indice++);

        // Si les noms correspondent, on renvoie l'id
        if (feuille.getNomElement().equalsIgnoreCase(nom)) {
          return feuille.getIdElement();
        }
      }
      while (feuille != null);

      // Si pas de correspondance, on renvoie -1
      return -1;
    }
    catch (ArrayIndexOutOfBoundsException e) {
      return -1;
    }
  }

  //------------------------------------------------------------------//
  //					       Accès au fichier                         //
  //------------------------------------------------------------------//

  /**
   * Extrait uniquement le nom du fichier (sans l'extension) du chemin d'accès à celui-ci
   * @param cheminFichier : le chemin d'accès au fichier
   * @return Le nom du fichier (sans l'extension)
   * @throws Exception
   */
  public String extraireNomFichier(String cheminFichier) {
    File f = new File(cheminFichier);
    if (f.exists()) {
      return (f.getName()).substring(0, (f.getName()).indexOf("."));
    }
    return "";
  }

  /**
   * Extrait l'ID de la DP à partir du chemin d'accès à son fichier
   * @param cheminFichier : le chemin d'accès au fichier
   * @return L'ID de la DP
   */
  public long extraireIdChemin(String cheminFichier) {
    File f = new File(cheminFichier);
    if (f.exists()) {
      return new Long(f.getParentFile().getName()).longValue();
    }
    return -1;
  }

  /**
   * Met à jour le fichier référentiel en inscrivant le dernier id attribué
   * @param id : Dernier Id attribué
   */
  public void majDerniereIdDansFichier(long id) {
    try {
      RandomAccessFile raf = new RandomAccessFile(cheminReferentiel + File.separator + nomReferentiel + ".ref", "rw");

      //Positionnement dans le fichier pour l'écriture
      raf.seek(14);

      // Ecriture de la nouvelle Id
      raf.writeBytes(String.valueOf(id));
      raf.close();
    }
    catch (IOException e) {
      e.printStackTrace();
      ErrorManager.getInstance().display(e);
    }
  }

  //------------------------------------------------------------------//
  //					  Implementation de TreeModel                   //
  //------------------------------------------------------------------//

  /**
   * Méthode appelée par les écouteurs de l'adapteur pour s'enregistrer auprès de lui
   * @param ecouteur, ecouteur de l'adapteur (donc du modèle)
   */
  public void addTreeModelListener(TreeModelListener ecouteur) {
    this.getArbre().addTreeModelListener(ecouteur);
  }

  /**
   * Renvoie le ieme fils d'un objet parent
   * @param parent, objet dont on recherche un fils
   * @param ieme, numéro du fils que l'on recherche
   * @return l'Id du fils recherché
   */
  public Object getChild(Object parent, int index) {
    return this.getArbre().getChild(parent, index);
  }

  /**
   * Renvoie le nombre de fils de l'objet courant obj
   */
  public int getChildCount(Object obj) {
    return this.getArbre().getChildCount(obj);
  }

  /**
   * Renvoie l'indice auquel se trouve l'enfant d'un parent donné
   * @param parent : objet dont on recherche l'indice du fils
   * @param fils : objet dont on recherche l'indice
   * @return l'indice du fils recherché parmis l'ensemble des fils
   */
  public int getIndexOfChild(Object parent, Object enfant) {
    return this.getArbre().getIndexOfChild(parent, enfant);
  }

  /**
   * Renvoie la racine de l'arbre, l'id de l'objet modele Definition de Processus
   * @return l'Id de la définition de processus
   */
  public Object getRoot() {
    return this.getArbre().getRoot();
  }

  /**
   * Indique si l'objet courant obj est une feuille de l'arbre ou non
   */
  public boolean isLeaf(Object obj) {
    return this.getArbre().isLeaf(obj);
  }

  /**
   * Méthode permettant de supprimer un écouteur dans la liste des
   * écouteurs de l'adapteur
   * @param ecouteur : ecouteur de l'adapteur à supprimer
   */
  public void removeTreeModelListener(TreeModelListener ecouteur) {
    this.getArbre().removeTreeModelListener(ecouteur);
  }

  /**
   * Méthode appelée lorsque l'utilisateur a modifié la valeur d'un item identifié
   * par path pour une nouvelle valeur newValue, si la nouvelle valeur est valide,
   *  on prend en compte la modification
   */
  public void valueForPathChanged(TreePath path, Object valeur) {
    this.getArbre().valueForPathChanged(path, valeur);
  }

  //------------------------------------------------------------------//
  //					  Utilisation de Observable                     //
  //------------------------------------------------------------------//

  /**
   * Notifie les observateurs qu'une modification a été apporté à l'arbre
   */
  public void majObserveurs(int code) {
    this.setChanged();
    this.notifyObservers(new Integer(code));
  }

  public ElementReferentiel getDernierElementAjoute ()
  {
    return this.dernierComposantAjoute;
  }
}
