package twoxmi.utils.xml;

import java.io.File ;
import java.io.IOException ;
import javax.xml.parsers.ParserConfigurationException ;

import org.xml.sax.SAXException ;
import java.util.Map ;
import java.util.HashMap ;
import twoxmi.utils.Tracer ;
import java.util.logging.Level ;
import twoxmi.utils.*;


/**
 * Classe abstraite pour g�rer le contenu d'un fichier XML
 *
 * @author chaoukhi MHAMEDI
 * @version 1.0
 */
public abstract class AbstractXmlFileManager
{
  /**
   * Interface avec le fichier XML
   */
  protected SimpleXmlInterface simpleXmlInterface = null ;

  /**
   * Fichier XML � manipuler
   */
  protected File fichier = null ;

  /**
   * Element racine de la structure de donn�es. Ne sert qu'� pointer sur le d�but
   * de la structure (cette racine est purement logique)
   */
  protected SimpleXmlElement racineStructureDonnees = null ;

  /**
   * Elements de la structure de donn�es
   */
  protected Map listeElements = null ;

  /**
   * Constructeur par d�faut
   *
   * @param _fichierXML Fichier XML � g�rer
   * @throws IOException Probl�me d'acc�s au fichier
   */
  protected AbstractXmlFileManager (File _fichierXML) throws IOException
  {
    Tracer.entering (new Object[]{}) ;
    this.listeElements = new HashMap () ;
    this.fichier = _fichierXML ;
    this.simpleXmlInterface = new SimpleXmlInterface (_fichierXML) ;
  }



  /**
   * M�thode pour charger en m�moire le fichier XML (objets SimpleXmlElement)
   *
   * @return Retourne true si chargement sans probl�me, false sinon
   */
  protected boolean load()
  {
      Tracer.entering(new Object[]{});
      boolean resultat = true;
      try
      {
          this.simpleXmlInterface.read();
          this.racineStructureDonnees = this.simpleXmlInterface.getRoot();
          //System.out.println (this.racineStructureDonnees.getName());
      }
      catch (Throwable ex)
      {
          Tracer.log(Level.SEVERE, "throw", ex);
          resultat = false;
      }
      return resultat;
  }



  /**
   * M�thode pour �crire dans le fichier XML initial les objets en m�moire
   *
   * @return Retourne true si �criture sans probl�me, false sinon
   */
  protected boolean save ()
  {
    Tracer.entering (new Object[]
                     {}) ;
    boolean resultat = true ;
    resultat = this.simpleXmlInterface.write (this.fichier) ;
    return resultat ;
  }



  /**
   * M�thode pour obtenir la racine de la structure de donn�es
   *
   * @return Renvoi l'�l�ment racine
   */
  public SimpleXmlElement getRoot ()
  {
    return this.simpleXmlInterface.getRoot () ;
  }



  /**
   * M�thode � impl�menter dont le but est de 'transformer' les SimpleXmlElement
   * utiles en objets Java. La 'transformation' se fait par appel de la m�thode
   * <code>setUserObjets</code> de la classe SimpleXMLElement
   *
   * @return Renvoi true si op�ration ex�cut�e sans probl�me, false sinon
   */
  public abstract boolean marshal () ;



  /**
   * M�canisme contraire � la m�thode <code>marshal</code>. Le but est de
   * reconstruire les SimpleXmlElement � partir des objets 'transform�s'
   *
   * @return Renvoi true si op�ration ex�cut�e sans probl�me, false sinon
   */
  public abstract boolean unMarshal () ;
}
