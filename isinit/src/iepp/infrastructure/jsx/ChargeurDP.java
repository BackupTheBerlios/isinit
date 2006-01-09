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
import iepp.domaine.DefinitionProcessus;
import iepp.ui.iedition.dessin.vues.MDDiagramme;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import util.ErrorManager;
import util.MonitoredTaskBase;
import util.TaskMonitorDialog;

import JSX.ObjIn;


public class ChargeurDP extends MonitoredTaskBase
{
	private File mFile = null;

	
	/**
	 * Projet à charger
	 */
	private Projet projetCharge = null ;
	
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
					MDDiagramme diag = (MDDiagramme)v.get(1);
					this.print(Application.getApplication().getTraduction("liste_figures"));
					Vector elements = (Vector)v.get(2);
					this.print(Application.getApplication().getTraduction("liste_elements"));
					Vector liens = (Vector)v.get(3);
					this.print(Application.getApplication().getTraduction("liste_liens"));
					int dernierId = ((Integer)v.get(4)).intValue();
					this.projetCharge = new Projet(dp, diag, elements, liens, dernierId);
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
}
