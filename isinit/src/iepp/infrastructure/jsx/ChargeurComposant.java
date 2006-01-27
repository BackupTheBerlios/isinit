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
package iepp.infrastructure.jsx;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.ipsquad.apes.model.spem.process.components.ProcessComponent;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import util.ErrorManager;
import util.MonitoredTaskBase;
import util.TaskMonitorDialog;

import JSX.ObjIn;

import iepp.Application;
import iepp.Projet;
import iepp.domaine.*;

/**
 * Classe permettant de charger un composant publiable ou composant vide au format XML
 * Les 3 fichiers XML (component, interface et presentation ) doivent être obligatoirement dans le zip
 * sinon ce n'est pas un composant publiable
 */
public class ChargeurComposant extends MonitoredTaskBase
{

	/**
	 * Composant publiable a charger
	 */
	private ComposantProcessus cp = null;

	/**
	 * Boite de dialogue permettant d'afficher l'avancement des tâches
	 */
	private TaskMonitorDialog mTask = null;

	/**
	 * Nom du fichier  charger
	 */
	private String nomFic = null ;

	private String nomComposant = null;

	private HashMap listePresentation = null ;

	/**
	 * Constructeur du chargeur
	 */
	public ChargeurComposant(String nomFic)
	{
		this.nomFic = nomFic ;
		this.listePresentation = new HashMap() ;
	}

	/**
	 * Indique si le fichier component.xml a été trouvé
	 */
	private boolean componentTrouve = false ;

	/**
	 * Indique si le fichier interface.xml a été trouvé
	 */
	private boolean interfaceTrouve = false ;

	/**
	 * Indique si le fichier component.xml a été trouvé
	 */
	private boolean presentationTrouve = false ;


	/**
	 * Charge un composant publiable à partir d'un fichier ZIP contenant 3 fichiers XML
	 * - Component.xml (outil modélisation) contient tout le contenu d'un composant de processus
	 * - Interfaces.xml (outil modélisation) contient l'interface d'un composant
	 * - Presentation.xml (outil presentation) contient la liste des éléments de présentation
	 */
	public void chargerComposant ()
	{
		// Vérifier qu'une définition de processus est chargée
		// Pas de message d'erreur, mais on est censé vérifier ça dans l'interface
		Projet projet = Application.getApplication().getProjet() ;
		if (projet == null)
			return ;
		// création du composant publiable
		this.cp = new ComposantProcessus(projet.getDefProc());
		this.cp.setNomFichier(this.nomFic);

		this.print(Application.getApplication().getTraduction("Initialisation_chargement"));

		ZipInputStream zipFile = null ;
		try
		{
			// récupérer un flux vers le fichier zip
			zipFile = new ZipInputStream( new FileInputStream (new File(this.nomFic)));
			chargerInterfaces(zipFile);

			// revenir au début du flux pour un autre parsing
			zipFile = new ZipInputStream( new FileInputStream (new File(this.nomFic)));
			chargerComposant(zipFile);

			// revenir au début du flux pour un dernier parsing
			zipFile = new ZipInputStream( new FileInputStream (new File(this.nomFic)));
			chargerPresentation(zipFile);

			// si on arrive ici c'est que l'interface a été trouvée
			// pour que ce soit un composant vide il ne faut ni de composant ni de presentation
			if ((! this.componentTrouve) && (! this.presentationTrouve ))
			{
				// on a un compo vide
				this.cp.setVide(true);
				return;
			}
			// sinon, on a essayé de charger un composant publiable et il manque des fichiers
			// c'est une erreur
			if (! this.presentationTrouve )
			{
				throw new FichierException ("Presentation");
			}
			if (! this.componentTrouve )
			{
				throw new FichierException ("Component");
			}

			this.print("------------------------------------");
			this.print(Application.getApplication().getTraduction("component_succes"));

		}
		catch (FileNotFoundException e)
		{
			// ne devrait pas arriver
			this.cp = null ;
			this.traiterErreur();
			ErrorManager.getInstance().display("ERR","ERR_Fichier_Non_Trouve");

		}
		catch (FichierException e)
		{
			this.cp = null ;
			String fic = e.getMessage();
			this.traiterErreur();
			ErrorManager.getInstance().display("ERR","ERR_" + fic + "_Non_Trouve");

		}
		catch (ParserConfigurationException e)
		{
			this.cp = null ;
			e.printStackTrace();
			this.traiterErreur();
			ErrorManager.getInstance().displayError(e.getMessage());
		}
		catch (SAXException e)
		{
			this.cp = null ;
			e.printStackTrace();
			this.traiterErreur();
			ErrorManager.getInstance().displayError(e.getMessage());
		}
		catch (IOException e)
		{
			this.cp = null ;
			e.printStackTrace();
			this.traiterErreur();
			ErrorManager.getInstance().displayError(e.getMessage());
		}
		catch (ClassNotFoundException e)
		{
			this.cp = null ;
			e.printStackTrace();
			this.traiterErreur();
			ErrorManager.getInstance().displayError(e.getMessage());
		}
		// aucune erreur
	}

	private void chargerComposant(ZipInputStream projectZip) throws IOException, ClassNotFoundException, FichierException
	{
		ZipEntry zipEntry = projectZip.getNextEntry();
		while( zipEntry != null && !this.componentTrouve)
		{
			DataInputStream data = new DataInputStream( new BufferedInputStream(projectZip) );
			if( zipEntry.getName().equals("Component.xml") )
			{
				this.componentTrouve = true ;

				ObjIn in = new ObjIn(data);
				// récupérer le vecteur
				Vector v = (Vector)in.readObject();
				this.cp.setProcessComponent((ProcessComponent)v.get(0));
				this.cp.setMapDiagram((HashMap)v.get(1));
				this.print(Application.getApplication().getTraduction("Component_charge"));
			}
			else
			{
				zipEntry = projectZip.getNextEntry();
			}
		}
		projectZip.close();
	}

	/**
	 * Permet de rajouter au composant publiable à retourner des interfaces présentes dans le fichier
	 * Interfaces.xml.
	 * @param projectZip
	 * @param cp
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private void chargerInterfaces(ZipInputStream projectZip) throws ParserConfigurationException, SAXException, IOException, FichierException
	{

		// récupérer le premier fichier du zip
		ZipEntry zipEntry = projectZip.getNextEntry();

		// tant que l'on a pas trouvé le fichier interfaces.xml
		while( zipEntry != null && !this.interfaceTrouve )
		{
			// récupérer un flux de données
			DataInputStream data = new DataInputStream( new BufferedInputStream(projectZip) );
			// on vérifie si l'on a trouvé le bon fichier
			if( zipEntry.getName().equals("Interfaces.xml") )
			{
				// on a trouvé le fichier, ok
				this.interfaceTrouve = true;

				this.print(Application.getApplication().getTraduction("SAX_initialisation"));

				// préparation du parsing du fichier xml, méthode SAX
				SAXParserFactory factory = SAXParserFactory.newInstance();
				SAXParser analyzer = factory.newSAXParser();
				this.print(Application.getApplication().getTraduction("interfaces_parsing"));

				// construction d'un récupérateur d'évènement lors du parsing du fichier
				InterfacesHandler handler = new InterfacesHandler(this.cp);
				// lancer le parsing
				analyzer.parse( data, handler );
				this.print(Application.getApplication().getTraduction("Interfaces_charge"));
			}
			else
			{
				// fichier pas trouvé, passer au fichier suivant
				zipEntry = projectZip.getNextEntry();
			}
		}
		// fermer le flux vers le fichier zip
		projectZip.close();
		if (! this.interfaceTrouve)
		{
			throw new FichierException ("Interface");
		}
	}

	/**
	 *
	 * @param projectZip
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws FichierException
	 */

	private void chargerPresentation(ZipInputStream projectZip) throws ParserConfigurationException, SAXException, IOException, FichierException
	{
		ZipEntry zipEntry = projectZip.getNextEntry();

		while( zipEntry != null && !this.presentationTrouve )
		{
			DataInputStream data = new DataInputStream( new BufferedInputStream(projectZip) );
			if( zipEntry.getName().equals("Presentation.xml") )
			{
				this.presentationTrouve = true;
				this.print(Application.getApplication().getTraduction("SAX_initialisation"));
				// preparation du parsing
				SAXParserFactory factory = SAXParserFactory.newInstance();
				SAXParser saxparser = factory.newSAXParser();
				this.print(Application.getApplication().getTraduction("presentation_parsing"));
				PresentationHandler handler = new PresentationHandler(this.cp);
				saxparser.parse( data, handler );
				this.print(Application.getApplication().getTraduction("Presentation_charge"));
			}
			else
			{
				zipEntry = projectZip.getNextEntry();
			}
		}
		projectZip.close();
	}

	/**
	 * Permet de retourner le nom du composant décrit dans un fichier component.xml
	 * Vérifier que l'appel de cette méthode ne se fait que sur un composant publiable
	 * et non sur un composant vide
	 * @param projectZip
	 * @return null si le fichier est un composant vide et le nom sinon
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws FichierException
	 */
	public String chercherNomComposant(String projectZip) throws Exception
	{

		this.componentTrouve = false;
		ZipInputStream zipFile = null ;
		try
		{
			// récupérer un flux vers le fichier zip
			zipFile = new ZipInputStream( new FileInputStream (new File(projectZip)));
			ZipEntry zipEntry = zipFile.getNextEntry();
			while( zipEntry != null && !this.componentTrouve )
			{
				DataInputStream data = new DataInputStream( new BufferedInputStream(zipFile) );
				if( zipEntry.getName().equals("Presentation.xml") )
				{
					this.componentTrouve = true;
					SAXParserFactory factory = SAXParserFactory.newInstance();
					SAXParser saxparser = factory.newSAXParser();
					ComponentHandler handler = new ComponentHandler();
					saxparser.parse( data, handler );
				}
				else
				{
					zipEntry = zipFile.getNextEntry();
				}
			}
			zipFile.close();
		}
		catch (Exception e)
		{
			this.componentTrouve = false;
			this.traiterErreur();
			throw new Exception();
		}

		this.componentTrouve = ChargeurComposant.findData("Component.xml",projectZip);
		this.presentationTrouve = ChargeurComposant.findData("Presentation.xml",projectZip);
		this.interfaceTrouve = ChargeurComposant.findData("Interfaces.xml",projectZip);

		if ((! this.componentTrouve) || (! this.presentationTrouve ) || (! this.interfaceTrouve ))
		{
			return null;
		}
		//return projectZip;
		return  this.nomComposant;

	}

	//-------------------------------------------------------------
	// INTERFACES
	//-------------------------------------------------------------
	/**
	 * Classe permettant de récupérer les évènements survenus lors du parsing d'un fichier xml
	 */
	private class InterfacesHandler extends DefaultHandler
	{
		/**
		 * indique le type du produit sur lequel on est
		 */
		private int typeProduit ;

		/**
		 * composant processus auquel on va ajouter les interfaces
		 */
		private ComposantProcessus cp;

		/**
		 * Constructeur
		 * @param cp, composant publiable auquel on va ajouter les interfaces trouvées
		 */
		public InterfacesHandler(ComposantProcessus cp)
		{
			super();
			this.cp = cp ;
		}

		/**
		 * On récupère l'évènement "je rentre sur une nouvelle balise"
		 */
		public void startElement (String uri, String localName, String baliseName, Attributes attributes)
		{
			// agir selon le nom de la balise courante
			if(baliseName=="ProvidedInterface")
			{
				this.typeProduit = 1 ;
			}
			else if(baliseName=="RequiredInterface")
			{
				this.typeProduit = 0 ;
			}
			else if(baliseName=="WorkProductRef"){}
			else if(baliseName=="WorkProduct")
			{
				this.cp.ajouterProduit(attributes.getValue(0), this.typeProduit);
			}
		}
	}

	//-------------------------------------------------------------
	// COMPOSANT
	//-------------------------------------------------------------
	/**
	 * Classe permettant de récupérer le nom de présentation d'un composant
	 */
	private class ComponentHandler extends DefaultHandler
	{
		private boolean isElement = false;
		private boolean isGuide = false;
		private boolean isIdentificateur = false;
		private String baliseCourante ;
		private String resultat = null;

		public ComponentHandler()
		{
			ChargeurComposant.this.nomComposant = null;
		}

		/**
		 * On récupère l'évènement "je rentre sur une nouvelle balise"
		 */
		public void startElement (String uri, String localName, String baliseName, Attributes attributes)
		{
			this.baliseCourante = baliseName ;
			if(baliseName=="element")
			{
				// on trouve un élément de présentation
				this.isElement = true;
			}
			else if(baliseName=="guide")
			{
				this.isElement = false;
			}
		}

		public void endElement(String namespace, String name, String raw)
		{
			if(raw == "guide") this.isGuide = false;
			if(raw == "element") this.isElement = false;
		}

		public void characters(char buf[], int offset, int len) throws SAXException
		{
			String valeur = new String(buf, offset, len);
			if (!valeur.trim().equals(""))
			{
				if(this.isElement)
				{
					if (this.baliseCourante.equals("nom_presentation"))
					{
						// si on se trouve dans la bonne balise
						if (this.isIdentificateur)
						{
								// alors le nom que l'on récupère est le bon
								ChargeurComposant.this.nomComposant = valeur;
								// tout le reste est ignoré
								this.isIdentificateur = false;
						}
					}
					else if (this.baliseCourante.equals("identificateur_interne"))
					{
						try
						{
							// l'identificateur interne du composant est 1
							if ((new Integer(valeur)).intValue() == 1)
							{
								// onse trouve dans les bonnes balises
								this.isIdentificateur = true;
							}
						}
						catch( Exception excep){}
					}
				}
			}
		}
	}


	//-------------------------------------------------------------
	// PRESENTATION
	//-------------------------------------------------------------
	/**
	 * Classe permettant de récupérer les évènements survenus lors du parsing d'un fichier xml
	 */
	private class PresentationHandler extends DefaultHandler
	{

		private boolean isProprietes = false;
		private boolean isElement = false;
		private boolean isGuide = false;
		private ElementPresentation element = null;
		private Guide guide = null;
		private String baliseCourante ;

		private ComposantProcessus cp;

		public PresentationHandler(ComposantProcessus cp)
		{
			this.cp = cp ;
			ChargeurComposant.this.nomComposant = null;
		}

		/**
		 * On récupère l'évènement "je rentre sur une nouvelle balise"
		 */
		public void startElement (String uri, String localName, String baliseName, Attributes attributes)
                {
			this.baliseCourante = baliseName ;
			if(baliseName=="element")
			{
				// on trouve un élément de présentation
				this.element = new ElementPresentation();
				ChargeurComposant.this.cp.getPaquetage().ajouterElement(this.element);
				this.isElement = true;
			}
			else if(baliseName=="exportation_presentation")
			{
				ChargeurComposant.this.cp.getPaquetage().setNomFichier(ChargeurComposant.this.nomFic);
			}
			else if(baliseName=="proprietes")
			{
				this.isProprietes = true;
			}
			else if(baliseName=="guide")
			{
				this.isGuide = true;
				this.isElement = false;
				this.guide = new Guide();
				ChargeurComposant.this.cp.getPaquetage().ajouterElement(this.guide);
				this.element.ajouterGuide(this.guide);
			}
		}

		public void endElement(String namespace, String name, String raw)
		{
			if(raw == "proprietes") this.isProprietes = false;
			if(raw == "guide") this.isGuide = false;
			if(raw == "element") this.isElement = false;

		}

		public void characters(char buf[], int offset, int len) throws SAXException
		{
			String valeur = new String(buf, offset, len);
			if (!valeur.trim().equals(""))
			{
				if(this.isProprietes)
				{
					if (this.baliseCourante.equals("nom_presentation"))
					{
                                          ChargeurComposant.this.cp.getPaquetage().setNomPresentation(valeur);
                                          //ChargeurComposant.this.nomComposant = valeur;
					}
                                        else if (this.baliseCourante.equals("auteur")){
                                            ChargeurComposant.this.cp.getPaquetage().setAuteur(valeur);
                                        }
                                        else if (this.baliseCourante.equals("email")){
                                          ChargeurComposant.this.cp.getPaquetage().setMail(valeur);
                                        }
                                        else if (this.baliseCourante.equals("version")){
                                          ChargeurComposant.this.cp.getPaquetage().setVersion(valeur);
                                        }
                                        else if (this.baliseCourante.equals("lastexport")){
                                          ChargeurComposant.this.cp.getPaquetage().setLastExport(valeur);
                                        }
					else if (this.baliseCourante.equals("chemin_contenus"))
					{
						if ( valeur.endsWith("/") || valeur.endsWith("\\"))
						{
							valeur = valeur.substring(0, valeur.length()-1);
						}
						ChargeurComposant.this.cp.getPaquetage().setCheminContenu(valeur);
					}

					else if (this.baliseCourante.equals("chemin_icones"))
					{
						if ( valeur.endsWith("/") || valeur.endsWith("\\"))
						{
							valeur = valeur.substring(0, valeur.length()-1);
						}
						ChargeurComposant.this.cp.getPaquetage().setCheminIcone(valeur);
					}
				}
				else if(this.isElement)
				{

					if (this.baliseCourante.equals("nom_presentation"))
						this.element.setNomPresentation(valeur);
					else if (this.baliseCourante.equals("identificateur_externe"))
					{
						this.element.setIdExterne(new Integer(valeur).intValue());
						// rajouter l'élément dans la map des éléments de présentation
						ChargeurComposant.this.listePresentation.put(new Integer(valeur),this.element);
					}
					else if (this.baliseCourante.equals("identificateur_interne"))
						this.element.setIdInterne(valeur);
					else if (this.baliseCourante.equals("icone"))
						this.element.setIcone(valeur);
					else if (this.baliseCourante.equals("contenu"))
						this.element.setContenu(valeur);
					else if (this.baliseCourante.equals("description"))
                                          this.element.setDescription(valeur);
                                        //modif 2XMI amandine
                                        else if (this.baliseCourante.equals("typeproduit")) {
                                          this.element.setTypeProduit(valeur);
                                          //fin modif 2XMI amandine
                                        }
				}
				else if(this.isGuide)
				{
					if (this.baliseCourante.equals("nom_presentation"))
						this.guide.setNomPresentation(valeur);
					else if (this.baliseCourante.equals("identificateur_externe"))
						this.guide.setIdExterne(new Integer(valeur).intValue());
					else if (this.baliseCourante.equals("identificateur_interne"))
						this.guide.setIdInterne(valeur);
					else if (this.baliseCourante.equals("icone"))
						this.guide.setIcone(valeur);
					else if (this.baliseCourante.equals("contenu"))
						this.guide.setContenu(valeur);
					else if (this.baliseCourante.equals("type"))
						this.guide.setType(valeur);
					else if (this.baliseCourante.equals("description"))
						this.guide.setDescription(valeur);
				}
			}
		}
	}


	//-------------------------------------------------------------
	// Résultat du chargement
	//-------------------------------------------------------------
	public ComposantProcessus getComposantCharge()
	{
		return this.cp;
	}

	public HashMap getMapPresentation()
	{
		return this.listePresentation;
	}


	//-------------------------------------------------------------
	// Monitored task
	//-------------------------------------------------------------
	/*
	 * @see util.MonitoredTaskBase#processingTask()
	 */
	protected Object processingTask()
	{
		this.chargerComposant();
		return null;
	}

	public void setTask( TaskMonitorDialog task )
	{
		this.mTask = task;
	}

	/**
	 * Print a new message to the TaskMonitorDialog
	 *
	 * @param msg
	 */
	private void print( String msg )
	{
		setMessage(msg);
		if( mTask != null )
		{
			mTask.forceRefresh();
		}
	}

	/**
	 * Recherche le fichier de nom fileName dans le fichier zip
	 * @return true si le fichier a bien été trouvé, false sinon
	 */
	public static boolean findData(String fileName, String fileZip) throws IOException
	{
		ZipInputStream zipFile = new ZipInputStream( new FileInputStream(new File(fileZip)));
		ZipEntry zipEntry = zipFile.getNextEntry();
		while( zipEntry != null )
		{
			DataInputStream data = new DataInputStream( new BufferedInputStream(zipFile) );
			if( zipEntry.getName().equals(fileName) )
			{
				return true;
			}
			else
			{
				zipEntry = zipFile.getNextEntry();
			}
		}
		zipFile.close();
		return false;
	}
}
