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

package iepp.application.aedition;

import java.io.File;
import java.io.FileOutputStream;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import util.ErrorManager;
import util.ImageUtil;
import util.SimpleFileFilter;
import util.SmartChooser;
import iepp.Application;
import iepp.application.CommandeNonAnnulable;
import iepp.ui.iedition.VueDPGraphe;

/**
 * Classe permettant de sauvegarder le diagramme sous la forme dans fichier image PNG
 */
public class CExporterJPEG extends CommandeNonAnnulable
{
	/**
	 * Diagramme � exporter en fichier image
	 */
	private VueDPGraphe diagramme;
	
	/**
	 * Filtre utilis� pour r�cup�rer un nom de fichier
	 */
	private SimpleFileFilter filter = new SimpleFileFilter(new String[]{"jpeg","jpg"}, "JPEG File");
	
	
	/**
	 * Constructeur de l'exporteur vers JPEG � partir du diagramme � convertir
	 * en fichier image
	 * @param diagramme composant � exporter en image
	 */
	public CExporterJPEG(VueDPGraphe diagramme)
	{
		this.diagramme = diagramme;
	}
	
	/**
	 * La commande renvoie si elle s'est bien pass�e ou non
	 * R�cup�re le nom du fichier � cr�er, v�rifie si le fichier existe d�j�, auquel cas
	 * demande � l'utilisateur la confirmation pour �craser le fichier existant
	 * Une erreur peut survenir lors de l'export, un message s'affiche
	 * @return true si l'export s'est bien pass� false sinon
	 */
	public boolean executer()
	{
		
		JFileChooser chooser = SmartChooser.getChooser();
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileFilter(filter);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		// Ouverture de la boite de dialogue
		if (chooser.showSaveDialog(Application.getApplication().getFenetrePrincipale()) !=  JFileChooser.APPROVE_OPTION)
		{
			return false;
		}
		String selected_file=chooser.getSelectedFile().getAbsolutePath();
		File fic = new File(selected_file);
		// v�rifier si le fichier existe d�j�, auquel cas on demande confirmation pour l'�crasement
		if(fic.exists())
		{
			int choice = JOptionPane.showConfirmDialog( Application.getApplication().getFenetrePrincipale(),
														Application.getApplication().getTraduction("msgConfirmEcrasement") ,
														Application.getApplication().getTraduction("msgSauvegarde"),
														JOptionPane.YES_NO_OPTION,
														JOptionPane.QUESTION_MESSAGE);

			if(choice!=JOptionPane.YES_OPTION)
			{
				return false;
			}
		}
		
		// cr�ation du fichier jpeg
		try
		{
			FileOutputStream fout ;
			if (! chooser.getSelectedFile().getAbsolutePath().endsWith(".jpg")
				&& ! chooser.getSelectedFile().getAbsolutePath().endsWith(".jpeg")
				&& ! chooser.getSelectedFile().getAbsolutePath().endsWith(".JPEG"))
			{
				fout = new FileOutputStream(new File(chooser.getSelectedFile().getAbsolutePath() + ".jpeg"));
			}
			else
			{
				fout = new FileOutputStream(new File(chooser.getSelectedFile().getAbsolutePath()));
			}
			ImageUtil.encoderImage(this.diagramme, fout, "jpeg");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			ErrorManager.getInstance().display("ERR", "ERR_Export");
		}
		return true;
	}
	
}
