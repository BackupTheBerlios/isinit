package twoxmi.utils.xml;

import java.io.* ;
import java.util.* ;
import java.util.logging.Level ;
import javax.xml.parsers.* ;

import org.xml.sax.* ;
import org.xml.sax.helpers.* ;
import org.w3c.dom.Document ;
import org.w3c.dom.Node ;
import org.w3c.dom.Element ;

import javax.xml.transform.TransformerFactory ;
import javax.xml.transform.Transformer ;
import javax.xml.transform.dom.DOMSource ;
import javax.xml.transform.stream.StreamResult ;
import javax.xml.transform.Source ;
import javax.xml.transform.Result ;

import twoxmi.utils.Tracer ;

import twoxmi.utils.Utilitaire;



/**
 * Classe pour interfacer un fichier XML permettant de le charger en m�moire et
 * / ou de l'�crire sur disque. Les noeuds du fichier XML sont mod�lis�s par
 * la classe SimpleXmlElement.
 * Attention, la version 1.0 ignore les zones PCDATA
 *
 * @author chaoukhi MHAMEDI
 * @version 1.0
 */
public class SimpleXmlInterface extends DefaultHandler
{
  /**
   * Objet pour localiser les erreurs �ventuelles
   */
  private Locator locator = null ;

  /**
   * Fichier XML � manipuler
   */
  private File fichierXML = null ;

  /**
   * SimpleXmlElement p�re. Racine de l'arborescence en m�moire (purement logique)
   * pour pouvoir manipuler une structure vide par exemple
   */
  private static final SimpleXmlElement racineSimpleElement = new SimpleXmlElement ("ROOT") ; // @todo verifier utilit�

  /**
   * Pile de SimpleXmlElement utilis�e lors de l'analyse du flux du fichier XML
   */
  private Stack xmlElements = null ;

  /**
   * Instructions de d�but de fichier (celles entre <? et ?> except� <?xml ?>)
   */
  private Map processInstructions = null ;

  /**
   * Constructeur
   *
   * @param _fichierXML Fichier XML � lire
   * @throws IOException Probl�me d'acc�s avec le fichier pass� en param�tre
   */
  public SimpleXmlInterface(File _fichierXML) throws IOException
  {
      //Seb
      super();
      //seb
      Tracer.entering(new Object[]{_fichierXML});
      if (_fichierXML.exists() && _fichierXML.isFile() && _fichierXML.canRead() && _fichierXML.canWrite())
      {
          this.fichierXML = _fichierXML;
          this.xmlElements = new Stack(); // cr�ation de la pile
          this.processInstructions = new HashMap(); // instructions
      }
      else
      {
          throw new IOException();
      }
  }



  /**
   * M�thode pour charger en m�moire les �l�ments du fichier XML
   *
   * @throws SAXException Le fichier pass� en param�tre n'est pas bien form�, ou invalide
   * @throws ParserConfigurationException
   * @throws IOException Le fichier pass� en param�tre n'est pas trouv�, ou
   * lecture impossible
   */
  public void read() throws SAXException, ParserConfigurationException, IOException
  {
      Tracer.entering(new Object[]{});
      SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
      SAXParser saxParser = saxParserFactory.newSAXParser();
      saxParserFactory.setValidating(true);
      saxParserFactory.setNamespaceAware(false);
      saxParser.parse(this.fichierXML, this);
  }



  /**
   * M�thode pour sauver sur disque les �l�ments en m�moire
   *
   * @param _fichier Fichier destination de l'�criture des objets en m�moire
   */
  public boolean write (File _fichier)
  {
    Tracer.entering (new Object[]
		     {_fichier}) ;
    boolean resultat = false ;
    if (_fichier.exists ()) // source d'anomalies...
    {
      if (_fichier.isFile ())
      {
	_fichier.delete () ;
      }
      else
      {
	// on supprime toute l'arborescence du fichier pass� en param�tre
	// qui est en fait un r�pertoire
	Utilitaire.deleteDirectory (_fichier, true) ;
      }
    }
    try
    {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance () ;
      DocumentBuilder builder = factory.newDocumentBuilder () ;
      Document documentTmp = builder.newDocument () ;

      for (Iterator it = this.processInstructions.keySet ().iterator () ;
			 it.hasNext () ; )
      {
	String nom = (String) it.next () ;
	documentTmp.appendChild (documentTmp.createProcessingInstruction (
	    nom, (String)this.processInstructions.get (nom))) ;
      }
      //Element root = documentTmp.createElement ("root") ; // purement logique
      documentTmp.appendChild (this.writeFile (documentTmp,
					       SimpleXmlInterface.
					       racineSimpleElement.
					       getChild (0))) ; // 1er noeud 'officiel'
      TransformerFactory tfactory = TransformerFactory.newInstance () ;
      Transformer transformer = tfactory.newTransformer () ;
      transformer.setOutputProperty ("indent", "yes") ;
      transformer.setOutputProperty ("method", "xml") ;
      transformer.setOutputProperty ("encoding", "ISO-8859-1") ;
      transformer.setOutputProperty ("omit-xml-declaration", "no") ;
      Source entree = new DOMSource (documentTmp) ;
      Result sortie = new StreamResult (new FileOutputStream (_fichier)) ;
      transformer.transform (entree, sortie) ;
      resultat = true ;
    }
    catch (Throwable ex)
    {
      Tracer.log (Level.SEVERE, "throw", ex) ;
    }
    return resultat ;
  }



  /**
   * M�thode r�cursive appel�e par <code>write</code> pour "remplir" le fichier
   * avec les �l�ments en m�moire en tenant compte de leur arborescence
   *
   * @param _document Document XML
   * @param _simpleXMLElement SimpleXMLElement � ajouter au fichier
   * @return Retourne la racine du fragment XML cr��e pour �tre ajout�e au fichier
   */
  private Node writeFile (Document _document,
			  SimpleXmlElement _simpleXMLElement)
  {
    Tracer.entering (new Object[]
		     {_document, _simpleXMLElement}) ;
    Element noeudRacine = _document.createElement (_simpleXMLElement.getName ()) ;
    if (_simpleXMLElement.getUserObject () != null)
    {
      // on demande � l'objet 'attach�' de se convertir en fragment XML
      SimpleXmlInterfacable userObject = _simpleXMLElement.getUserObject () ;
      noeudRacine = (Element)this.writeFile (_document,
					     userObject.toSimpleXmlElement ()) ;
    }
    else
    {
      // on parcours la structure SimpleXmlElement pour construire le fragment XML
      // ajout des attributs
      for (Iterator it = _simpleXMLElement.attributesIterator () ; it.hasNext () ; )
      {
	String cleAttribut = (String) it.next () ;
	noeudRacine.setAttribute (cleAttribut,
				  _simpleXMLElement.getAttribute (cleAttribut)) ;

      }
      // ajout du texte �ventuel entre les balises d�but et fin
      String pcData = _simpleXMLElement.getPCDATA () ;
      if (pcData != null)
      {
	noeudRacine.appendChild (_document.createTextNode (pcData)) ;
      }
      for (Iterator it = _simpleXMLElement.childrenIterator () ; it.hasNext () ; )
      {
	noeudRacine.appendChild (writeFile (_document,
					    (SimpleXmlElement) it.next ())) ;
      }
    }
    return noeudRacine ;
  }



  /**
   * M�thode appel�e � chaque fois que l'analyseur rencontre des caract�res
   * (entre deux balises)
   *
   * @param _ch Les caract�res proprement dits.
   * @param _start Le rang du premier caract�re a traiter effectivement.
   * @param _end Le rang du dernier caract�re a traiter effectivement
   * @see org.xml.sax.ContentHandler#characters(char[], int, int)
   */
  public void characters (char[] _ch, int _start, int _end)
      throws SAXException
  {
    SimpleXmlElement elementCourant = (SimpleXmlElement)this.xmlElements.peek () ;
    elementCourant.setPCDATA (new String (_ch, _start, _end)) ;
  }



  /**
   * M�thode appel�e � la fin de l'analyse du flux XML
   *
   * @throws SAXException En cas de probl�me quelquonque ne permettant pas de
   * considerer l'analyse du document comme etant complete
   * @see org.xml.sax.ContentHandler#endDocument()
   */
  public void endDocument ()
      throws SAXException
  {
    Tracer.entering (new Object[]
		     {}) ;
  }



  /**
   * M�thode appel�e � chaque fermeture de balise lors de l'analyse du flux
   *
   * @param _nameSpaceURI L'url de l'espace de nommage
   * @param _localName Le nom local de la balise
   * @param _rawName Nom de la balise en version 1.0 <code>nameSpaceURI + ":" + localName</code>
   * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
   */
  public void endElement (String _nameSpaceURI, String _localName,
			  String _rawName)
      throws SAXException
  {
    Tracer.entering (new Object[]
                     {_nameSpaceURI, _localName, _rawName}) ;
    this.xmlElements.pop () ; // on d�pile le dernier �l�ment
  }



  /**
   * @todo Implement method "endPrefixMapping"
   */
  public void endPrefixMapping (String prefix)
      throws SAXException
  {
    // @todo � traiter?
  }



  /**
   * @todo Implement method "ignorableWhitespace"
   */
  public void ignorableWhitespace (char[] ch, int start, int length)
      throws SAXException
  {
    throw new java.lang.UnsupportedOperationException (
	"Method ignorableWhitespace() not yet implemented.") ;
  }



  /**
   * M�thode appel�e � chaque nouvelle instruction en ent�te du fichier XML
   * autre que <?xml ?>
   *
   * @param _target Le nom
   * @param _date La valeur (attributs etc...)
   * @throws SAXException
   */
  public void processingInstruction (String _target, String _data)
      throws SAXException
  {
    Tracer.entering (new Object[]
                     {_target, _data}) ;
    this.processInstructions.put (_target, _data) ;
  }



  /**
   * M�thode pour mettre � jour l'objet qui permet de localiser "le curseur"
   * pendant le traitement du flux (par exemple conna�tre le num�ro de ligne
   * et de colonne du fichier en cours d'analyse)
   *
   * @param _locator Le Locator
   */
  public void setDocumentLocator (Locator _locator)
  {
    this.locator = _locator ;
  }



  /**
   * @todo Implement method "skippedEntity"
   */
  public void skippedEntity (String name)
      throws SAXException
  {
    throw new java.lang.UnsupportedOperationException (
	"Method skippedEntity() not yet implemented.") ;
  }



  /**
   * M�thode appel�e au d�but de la lecture du flux, initialisant la
   * structure de donn�es en m�moire
   */
  public void startDocument () throws SAXException
  {
    Tracer.entering (new Object[]{}) ;
    this.xmlElements.clear () ; // on vide la pile
    this.xmlElements.push (SimpleXmlInterface.racineSimpleElement) ;
    this.processInstructions.clear () ; // on vide les instructions
  }



  /**
   * M�thode appel�e � chaque fois que l'analyseur rencontre une balise XML
   * ouvrante
   *
   * @param _nameSpaceURI L'url de l'espace de nommage
   * @param _localName Le nom local de la balise
   * @param _rawName Nom de la balise en version 1.0 <code>nameSpaceURI + ":" + localName</code>
   * @throws SAXException Si la balise ne correspond pas a ce qui est attendu,
   * comme par exemple non respect d'une DTD
   * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
   */
  public void startElement (String _nameSpaceURI, String _localName, String _rawName, Attributes _attributs) throws SAXException
  {
    Tracer.entering (new Object[]{_nameSpaceURI, _localName, _rawName, _attributs}) ;
    SimpleXmlElement nouveauElement = new SimpleXmlElement (_rawName) ;
    SimpleXmlElement elementPere = (SimpleXmlElement)this.xmlElements.peek () ;
    elementPere.addChild (nouveauElement) ;
    // ajout des attributs
    for (int i = 0 ; i < _attributs.getLength () ; i++)
    {
      nouveauElement.setAttribute (_attributs.getQName (i),
				   _attributs.getValue (i)) ;
    }
    this.xmlElements.push (nouveauElement) ;
  }



  /**
   * @todo Implement method "startPrefixMapping"
   */
  public void startPrefixMapping (String prefix, String uri)
      throws SAXException
  {
    // @todo � traiter?
  }



  /**
   * @todo Implement method "fatalError"
   */
  public void fatalError (SAXParseException ex)
      throws SAXException
  {
    ex.printStackTrace () ;
  }



  /**
   * @todo Implement method "warning"
   */
  public void warning (SAXParseException ex)
      throws SAXException
  {
    Tracer.log(Level.SEVERE, "SEVERE", ex);
    ex.printStackTrace();
  }



  /**
   * @todo Implement method "notationDecl"
   */
  public void notationDecl (String name, String publicId, String systemId)
      throws SAXException
  {
    throw new java.lang.UnsupportedOperationException (
	"Method notationDecl() not yet implemented.") ;
  }



  /**
   * @todo Implement method "unparsedEntityDecl"
   */
  public void unparsedEntityDecl (String name, String publicId, String systemId,
				  String notationName)
      throws SAXException
  {
    throw new java.lang.UnsupportedOperationException (
	"Method unparsedEntityDecl() not yet implemented.") ;
  }



  /**
   * @todo Implement method "resolveEntity"
   */
  public InputSource resolveEntity (String _publicId, String _systemId)
      throws SAXException
  {
    Tracer.entering(new Object[]{_publicId, _systemId});
    if (_publicId == null)
    {
      return new InputSource (_systemId) ;
    }
    return new InputSource (_publicId) ;
  }



  /**
   * M�thode pour obtenir la racine de la structure de donn�es
   * Attention, il faut r�cup�rer le premier fils de ce SimpleXmlElement
   *
   * @return Renvoi l'�l�ment racine
   */
  public SimpleXmlElement getRoot ()
  {
    return SimpleXmlInterface.racineSimpleElement ;
  }
}
