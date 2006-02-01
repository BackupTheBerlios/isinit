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

import iepp.Application;
import iepp.Projet;
import iepp.domaine.ComposantProcessus;
import iepp.domaine.DefinitionProcessus;
import iepp.domaine.IdObjetModele;
import iepp.ui.iedition.dessin.rendu.ComposantCell;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jgraph.graph.GraphConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import util.ErrorManager;
import util.MonitoredTaskBase;
import util.TaskMonitorDialog;

public class ChargeurDP extends MonitoredTaskBase
{
	private File mFile = null;

	private double zoom;
	/**
	 * Projet à charger
	 */
	private Projet projetCharge = null ;
	
	private DefinitionProcessus dp;
	/**
	 * Boite de dialogue permettant d'afficher l'avancement des tâches
	 */
	private TaskMonitorDialog mTask = null;
	
	
	/**
	 * Constructeur à partir du fichier contenant la définition de processus à ouvrir
	 * @param file
	 */
	public ChargeurDP(File file)
	{
		mFile = file;
	}

	public void chargerDP()
	{
		 dp = new DefinitionProcessus();
		 DataInputStream data = null;
		try 
		{
			data = findData("DefinitionProcessus.xml");
		} 
		catch (IOException e)
		{
			this.projetCharge = null;
			this.traiterErreur();
			ErrorManager.getInstance().displayError(e.getMessage());
			e.printStackTrace();
		}
		if( data != null )
		{
			this.print(Application.getApplication().getTraduction("dp_trouve"));
			
			Document document;
			DocumentBuilderFactory fabrique = DocumentBuilderFactory.newInstance();
			DocumentBuilder analyseur;
			try {
				this.print(Application.getApplication().getTraduction("creation_parseur"));
				analyseur = fabrique.newDocumentBuilder();
				document = analyseur.parse(data);
				
				if ( document.getChildNodes().getLength() == 1) {
					Node racine = document.getChildNodes().item(0);
					String nom = racine.getNodeName();
					if ( nom == "ieppnit")
					{
						NodeList listeracine = racine.getChildNodes();
						boolean proprietestrouvees = false;
						boolean statiquetrouvee = false;
						for(int i = 0; i < listeracine.getLength(); i++) {
							Node n = listeracine.item(i);
							String nomracine = n.getNodeName();
							if (nomracine == "proprietes") {
								proprietestrouvees = true;
								chargerProprietes(n);
							}
							if (nomracine == "statique") {
								statiquetrouvee = true;
								chargerStatique(n);
							}
						}
						if((!proprietestrouvees) || (!statiquetrouvee)) {
							System.out.println("non");
							ErrorManager.getInstance().display("ERR","ERR_Fichier_DP_Corrompu");
						}
						else {
							this.projetCharge = new Projet( dp, null, null,20);
							this.projetCharge.getFenetreEdition().getVueDPGraphe().setScale(zoom);
							this.print(Application.getApplication().getTraduction("dp_succes"));
						}
					}
					else {
						ErrorManager.getInstance().display("ERR","ERR_Fichier_DP_Corrompu");
					}
				}
				else {
					ErrorManager.getInstance().display("ERR","ERR_Fichier_DP_Corrompu");
				}
				this.print(Application.getApplication().getTraduction("recupere_donnees"));
			}
			catch(Exception er){ System.out.println("ChargerComposant - Erreur Parser"); }
			/*
			ObjIn in = null;
			Vector v = null;
			try
			{
				this.print(Application.getApplication().getTraduction("creation_parseur"));
				in = new ObjIn(data);
				this.print(Application.getApplication().getTraduction("recupere_donnees"));
				v = (Vector)in.readObject();
				
			}
			catch (Exception e1)
			{
				this.projetCharge = null ;
				this.traiterErreur();
				ErrorManager.getInstance().displayError(e1.getMessage());
				e1.printStackTrace();
			}

			if( v.size() == 5 )
			{	
				try
				{
					DefinitionProcessus dp = (DefinitionProcessus)v.get(0);
					this.print(Application.getApplication().getTraduction("liste_composants"));
					//MDDiagramme diag = (MDDiagramme)v.get(1);
					this.print(Application.getApplication().getTraduction("liste_figures"));
					Vector elements = (Vector)v.get(2);
					this.print(Application.getApplication().getTraduction("liste_elements"));
					Vector liens = (Vector)v.get(3);
					this.print(Application.getApplication().getTraduction("liste_liens"));
					int dernierId = ((Integer)v.get(4)).intValue();
					this.projetCharge = new Projet(dp, elements, liens, dernierId);
					this.print(Application.getApplication().getTraduction("dp_succes"));
					
					// modif NIT Guillaume
					// On charge le processus
					this.projetCharge.getFenetreEdition().getVueDPGraphe().charger( );
					// fin modif NIT Guillaume
				}
				catch (Exception e1)
				{
					// problème de désérialisation
					this.projetCharge = null;
					this.traiterErreur();
					ErrorManager.getInstance().display("ERR","ERR_Fichier_DP_Corrompu");
					e1.printStackTrace();
				}
			}
			else
			{
				// le fichier ne contient pas les 5 éléments demandés
				this.projetCharge = null;
				this.traiterErreur();
				ErrorManager.getInstance().display("ERR","ERR_Fichier_DP_Corrompu");
			}
			*/
		}
		else
		{
			// le fichier definition.xml n'a pas été trouvé
			this.projetCharge = null;
			this.traiterErreur();
			ErrorManager.getInstance().display("ERR","ERR_Fichier_DP_Non_Trouve");
		}
	}

	/**
	 * Recherche et ouvre le fichier de nom fileName dans le fichier zip
	 */
	private DataInputStream findData(String fileName) throws IOException
	{	
		ZipInputStream zipFile = new ZipInputStream( new FileInputStream(new File(mFile.getAbsolutePath())));
		ZipEntry zipEntry = zipFile.getNextEntry();
		while( zipEntry != null )
		{
			DataInputStream data = new DataInputStream( new BufferedInputStream(zipFile) );
			if( zipEntry.getName().equals(fileName) )
			{
				return data;
			}
			else
			{
				zipEntry = zipFile.getNextEntry();
			}
		}
		zipFile.close();
		return null;
	}

	public Projet getProgetCharge()
	{
		return this.projetCharge;
	}
	
	//-------------------------------------------------------------
	// Monitored task
	//-------------------------------------------------------------

	protected Object processingTask()
	{
		this.chargerDP();
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
	
	//-------------------------------------------------------------
	// Differents chargements
	//-------------------------------------------------------------
	
	private String getPropriete(Node n) {
		String valeurproprietes;
		if (n.getChildNodes().getLength() == 1) {
			valeurproprietes = n.getChildNodes().item(0).getNodeValue();
		}
		else {
			valeurproprietes = "";
		}
		return valeurproprietes;
	}
	
	private void chargerProprietes(Node np) {
		NodeList listeproprietes = np.getChildNodes();
		boolean auteurtrouve = false;
		boolean commentairestrouves = false;
		boolean definitiontrouvee = false;
		boolean emailtrouve = false;
		boolean zoomtrouve = false;
		for(int i = 0; i < listeproprietes.getLength(); i++) {
			Node n = listeproprietes.item(i);
			String nomproprietee = n.getNodeName();
			String valeurproprietes = getPropriete(n);
			if (nomproprietee == "auteur") {
				auteurtrouve = true;
				dp.setAuteur(valeurproprietes);
			}
			if (nomproprietee == "commentaires") {
				commentairestrouves = true;
				dp.setCommentaires(valeurproprietes);
			}
			if (nomproprietee == "definition") {
				definitiontrouvee = true;
				dp.setNomDefProc(valeurproprietes);
			}
			if (nomproprietee == "email") {
				emailtrouve = true;
				dp.setEmailAuteur(valeurproprietes);
			}
			if (nomproprietee == "zoom") {
				zoomtrouve = true;
				zoom = Double.valueOf(valeurproprietes).doubleValue();
			}
		}
		if ((!auteurtrouve) || (!commentairestrouves) || (!definitiontrouvee) || (!emailtrouve) || (!zoomtrouve) ) {
			ErrorManager.getInstance().display("ERR","ERR_Fichier_DP_Corrompu");
		}
	}
	
	private void chargerStatique(Node n) {
		NodeList listestatique = n.getChildNodes();
		for(int i = 0; i < listestatique.getLength(); i++) {
			Node ns = listestatique.item(i);
			String nomproprietee = ns.getNodeName();
			if (nomproprietee == "composants") {
				System.out.println("composants");
				chargerComposants(ns);
			}
		}
	}
	
	private void chargerComposants(Node n) {
		NodeList listestatique = n.getChildNodes();
		int i, j;
		for(i = 0; i < listestatique.getLength(); i++) {
			String nom = "";
			long identifiant = -1;
			int positionx = -1;
			int positiony =- 1;
			int largeur = -1;
			int hauteur = -1;
			String imageprod = "";
			
			Node ncs = listestatique.item(i);
			NodeList listecomposants = ncs.getChildNodes();
			String nomproprietee = ncs.getNodeName();
			if (nomproprietee == "composant") {
				for(j = 0; j < listecomposants.getLength(); j++) {
					Node nc = listecomposants.item(j);
					String nomnoeud = nc.getNodeName();
					String valeurnoeud = getPropriete(nc);
					if (nomnoeud == "identifiant") {
						identifiant = Long.valueOf(valeurnoeud).longValue();
					}
					if (nomnoeud == "nom") {
						nom = valeurnoeud;
					}
					if (nomnoeud == "positionx") {
						positionx = Integer.valueOf(valeurnoeud).intValue();
					}
					if (nomnoeud == "positiony") {
						positiony = Integer.valueOf(valeurnoeud).intValue();
					}
					if (nomnoeud == "largeur") {
						largeur = Integer.valueOf(valeurnoeud).intValue();
					}
					if (nomnoeud == "hauteur") {
						hauteur = Integer.valueOf(valeurnoeud).intValue();
					}
					if (nomnoeud == "imageprod") {
						imageprod = valeurnoeud;
					}	
				}
			}
			if ((nom!="")&&(identifiant!=-1)&&(positionx!=-1)&&(positiony!=-1)&&(largeur!=-1)&&(hauteur!=-1)&&(imageprod!="")) {
				ComposantProcessus comp = new ComposantProcessus(dp);
				
				IdObjetModele id = new IdObjetModele(comp);
				
				ComposantCell cp = new ComposantCell(id,positionx,positiony);
				cp.setNomCompCell(nom);
				cp.setLargeur(largeur);
				cp.setHauteur(hauteur);
				cp.setImageComposant(imageprod);
				
				this.projetCharge.getFenetreEdition().getVueDPGraphe().ajouterCell(cp);
				
				Map AllAttrubiteCell = GraphConstants.createMap();
				AllAttrubiteCell.put(cp,cp.getAttributs());
				this.projetCharge.getFenetreEdition().getVueDPGraphe().getModel().insert(new Object[]{cp}, AllAttrubiteCell, null, null,null );
			}
		}
	}
}

