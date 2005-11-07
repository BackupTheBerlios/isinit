package twoxmi.utils.xml;

import java.util.* ;



/**
 * Classe pour mod�liser un �l�ment d'un fichier XML. Cet �l�ment peut contenir
 * d'autres �l�ments.<br/>
 * Permet de stocker les nom, attributs et sous �l�ments d'un �l�ment.
 *
 * @author chaoukhi MHAMEDI
 * @version 1.0
 */
public class SimpleXmlElement
{
  /**
   * Nom de l'�l�ment
   */
  private String nom = null ;

  /**
   * Sous �l�ments de l'�l�ment. Collection de SimpleXMLElement
   */
  private ArrayList fils = null ;

  /**
   * Attributs de l'�l�ment
   */
  private Map attributs = null ;

  /**
   * PCDATA de l'�l�ment (caract�res entre les balises d�but et fin)
   */
  private String pcData = null ;

  /**
   * Objet utilisateur pour personnaliser l'�l�ment
   */
  private SimpleXmlInterfacable userObject = null ;

  /**
   * Constructeur pour initialiser un �l�ment
   *
   * @param _nodeName Nom de l'�l�ment (nom de la balise XML)
   */
  public SimpleXmlElement (String _nodeName)
  {
    this.nom = _nodeName ;
    this.fils = new ArrayList () ;
    this.attributs = new HashMap () ;
  }



  /**
   * Remplacer l'�l�ment XML g�n�rique par un objet utilisateur
   *
   * @param _object L'objet sp�cifique
   */
  public void setUserObject (SimpleXmlInterfacable _object)
  {
    this.userObject = _object ;
  }



  /**
   * M�thode pour d�finir le PCDATA de l'�l�ment
   *
   * @param _pcData PCDATA pour l'�l�ment
   */
  public void setPCDATA (String _pcData)
  {
    this.pcData = _pcData ;
  }



  /**
   * M�thode pour obtenir le PCDATA de l'�l�ment
   *
   * @return Le PCDATA sous forme de String
   */
  public String getPCDATA ()
  {
    return this.pcData ;
  }



  /**
   * Obtenir l'objet utilisateur de l'�l�ment
   *
   * @return L'objet utilisateur si d�finit, null sinon
   */
  public SimpleXmlInterfacable getUserObject ()
  {
    return this.userObject ;
  }



  /**
   * M�thode pour obtenir le nom de l'�l�ment
   *
   * @return Nom de l'�l�ment
   */
  public String getName ()
  {
    return this.nom ;
  }



  /**
   * M�thode pour d�finir un attribut de l'�l�ment
   *
   * @param _attributeName Nom de l'attribut � d�finir
   * @param _attributeValue Valeur de l'attribut
   */
  public void setAttribute (String _attributeName, String _attributeValue)
  {
    this.attributs.put (_attributeName, _attributeValue) ;
  }



  /**
   * M�thode pour obtenir la valeur d'un attribut de l'�l�ment
   *
   * @param _attributeName Nom de l'attribut
   * @return Valeur de l'attribut
   */
  public String getAttribute (String _attributeName)
  {
    return (String)this.attributs.get (_attributeName) ;
  }



  /**
   * M�thode pour tester l'existence de certains attributs pour l'�l�ment
   *
   * @param _attributesName Liste d'attributs que l'�l�ment doit v�rifier
   * @return Renvoi true si tous les attributs pass�s en param�tre sont poss�d�s,
   * false sinon
   */
  public boolean hasAttributes (List _attributesName)
  {
    boolean resultat = true ;
    for (Iterator it = _attributesName.iterator () ; it.hasNext () ; )
    {
      if (!this.attributs.containsKey (it.next ()))
      {
	resultat = false ;
	break ;
      }
    }
    return resultat ;
  }



  /**
   * M�thode pour supprimer un attribut de l'�l�ment
   *
   * @param _attributeName Nom de l'attribut � supprimer
   * @return Renvoi la valeur de l'attribut supprim�
   */
  public String removeAttribute (String _attributeName)
  {
    return (String)this.attributs.remove (_attributeName) ;
  }



  /**
   * Ajouter un sous �l�ment � la liste des fils
   *
   * @param _simpleXMLElement Sous �l�ment � ajouter
   * @return Retourne true si l'�l�ment pass� en param�tre est ajout�, false sinon
   */
  public boolean addChild (SimpleXmlElement _simpleXMLElement)
  {
    return this.fils.add (_simpleXMLElement) ;
  }



  /**
   * Supprime le sous �l�ment pass� en param�tre
   *
   * @param _simpleXMLElement Sous �l�ment � supprimer
   * @return Retourne true si l'�l�ment est supprim�, false sinon
   */
  public boolean removeChild (SimpleXmlElement _simpleXMLElement)
  {
    return this.fils.remove (_simpleXMLElement) ;
  }



  /**
   * Supprime le sous �l�ment dont la position est pass�e param�tre
   *
   * @param _index Position du sous �l�ment � supprimer
   * @return Retourne l'�l�ment supprim�, null sinon
   */
  public SimpleXmlElement removeChild (int _index)
  {
    return (SimpleXmlElement)this.fils.remove (_index) ; // r�ussi m�me si index invalide
  }



  /**
   * M�thode pour pouvoir it�rer sur les sous �l�ments directs
   *
   * @return It�rateur sur les sous �l�ments de l'�l�ment
   */
  public Iterator childrenIterator ()
  {
    return this.fils.iterator () ;
  }



  /**
   * M�thode pour pouvoir it�rer sur les attributs
   *
   * @return It�rateur sur les attributs de l'�l�ment
   */
  public Iterator attributesIterator ()
  {
    return this.attributs.keySet ().iterator () ;
  }



  /**
   * M�thode pour obtenir tous les sous �l�ments ayant un certain nom.
   *
   * @param _nodeName Le nom des sous �l�ments � chercher
   * @param _allLevel D�finir s'il faut explorer en profondeur ou pas
   * @return It�rateur sur les sous �l�ments de nom _nodeName
   */
  public Iterator getChildrenByName (String _nodeName, boolean _allLevel)
  {
    LinkedList resultat = new LinkedList () ;
    for (Iterator it = this.childrenIterator () ; it.hasNext () ; )
    {
      SimpleXmlElement simpleXMLElement = (SimpleXmlElement) it.next () ;
      if (_nodeName.equalsIgnoreCase (simpleXMLElement.getName ()))
      {
	resultat.add (simpleXMLElement) ;
      }
      if (_allLevel)
      {
	for (Iterator it2 = simpleXMLElement.getChildrenByName (_nodeName, true) ;
			    it2.hasNext () ; )
	{
	  resultat.add (it2.next ()) ;
	}
      }
    }
    return resultat.iterator () ;
  }



  /**
   * M�thode pour obtenir tous les sous �l�ments ayant une certaine valeur
   * pour certains attributs
   *
   * @param _attributes Associations {attribut - valeur} � v�rifier
   * @return It�rateur sur les sous �l�ments v�rifiant la condition
   */
  public Iterator getChildrenByAttributeValue (HashMap _attributes)
  {
    LinkedList resultat = new LinkedList () ;
    for (Iterator it = this.childrenIterator () ; it.hasNext () ; )
    {
      SimpleXmlElement simpleXMLElement = (SimpleXmlElement) it.next () ;
      if (simpleXMLElement.checkAttributes (_attributes))
      {
	resultat.add (simpleXMLElement) ;
      }
      // appel r�cursif et r�cup�ration du r�sultat
      for (Iterator it2 = simpleXMLElement.getChildrenByAttributeValue (
	  _attributes) ; it2.hasNext () ; )
      {
	resultat.add (it2.next ()) ;
      }
    }
    return resultat.iterator () ;
  }



  /**
   * M�thode pour v�rifier si l'�l�ment poss�de certains attributs et valeurs
   * associ�es
   *
   * @param _attributes Les attributs et leur valeur � v�rifier
   * @return Renvoi true si l'�l�ment poss�de les attributs et leur valeur
   * associ�e, false sinon
   */
  public boolean checkAttributes (HashMap _attributes)
  {
    boolean resultat = true ;
    for (Iterator it = _attributes.keySet ().iterator () ; it.hasNext () ; )
    {
      String attribut = (String) it.next () ;
      if (!this.attributs.containsKey (attribut))
      {
	resultat = false ; // l'attribut n'est pas poss�d� par l'�l�ment
	break ;
      }
      if (! ( (String) _attributes.get (attribut)).equalsIgnoreCase (this.
	  getAttribute (attribut)))
      {
	resultat = false ; // la valeur attendu n'est pas la valeur obtenu
	break ;
      }
    }
    return resultat ;
  }



  /**
   * M�thode pour obtenir le i�me fils de l'�l�ment
   *
   * @param _index Index du fils demand�
   * @return SimpleXMLElement Le fils demand�, null si index non valide
   */
  public SimpleXmlElement getChild (int _index)
  {
    if (_index < this.fils.size ())
    {
      return (SimpleXmlElement)this.fils.get (_index) ;
    }
    return null ;
  }



  /**
   * getChildCount
   *
   * @return int
   */
  public int getChildCount ()
  {
    return this.fils.size () ;
  }



  public String toString ()
  {
    if (this.userObject == null)
    {
      return this.nom ;
    }
    return this.userObject.toString () ;
  }
}
