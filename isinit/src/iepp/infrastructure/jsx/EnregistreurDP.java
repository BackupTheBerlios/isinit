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

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import JSX.ObjOut;

public class EnregistreurDP
{
	/*
	 * Fichier zip contenant la sauvegarde XML de la définition processus
	 */
	private ZipOutputStream mZipFile;
	
	/**
	 * Constructeur à partir du fichier zip à remplir
	 * @param zipFile
	 */
	public EnregistreurDP(ZipOutputStream zipFile)
	{
		mZipFile = zipFile;
	}
	
	/**
	 * Construit la sauvegarde au format XML et la met dans le fichier zip
	 * @throws IOException
	 */
	public void sauver() throws IOException
	{	
		Projet project = Application.getApplication().getProjet();
		
		ZipEntry entryZip = new ZipEntry("DefinitionProcessus.xml");
		mZipFile.putNextEntry(entryZip);
		DataOutputStream data = new DataOutputStream( new BufferedOutputStream(mZipFile) );
		ObjOut out = new ObjOut( data );
		
		//vector to save
		Vector  v = new Vector();

		//ajouter la définition de processus à l'indice 0
		v.add(project.getDefProc());
		// liste des éléments du diagramme 2
		v.add(project.getFenetreEdition().getVueDPGraphe().getElementsCell());
		// liste des liens du diagramme 3
		v.add(project.getFenetreEdition().getVueDPGraphe().getLiens());
		// dernier indice utilisé dans la DP
		v.add(new Integer(Projet.getDernierId()));
		
		out.writeObject(v);
		mZipFile.closeEntry();
	}
}
