package twoxmi.utils.xml;

import twoxmi.utils.xml.*;

/**
 * Interface pour pouvoir interfacer une classe avec un fichier XML
 *
 * @author chaoukhi MHAMEDI
 * @version 1.0
 */
public interface SimpleXmlInterfacable
{
  /*****************************************
   *              ATTENTION                *
   * Rajouter dans la classe impl�mentant  *
   * cette interface la m�thode statique   *
   * dont la javadoc suit (en commentaire) *
   ****************************************/
  // M�thode pour cr�er un utilisateur � partir d'un �l�ment XML
  // @param _simpleXMLElement L'�l�ment XML
  // @return Renvoi un objet si cr�ation possible depuis le param�tre, null sinon
  // public static Object create (SimpleXMLElement _simpleXMLElement) ;



  /**
   * M�thode pour cr�er un �l�ment XML � partir d'un objet
   *
   * @return Renvoi un �l�ment XML
   */
  public SimpleXmlElement toSimpleXmlElement () ;
}
