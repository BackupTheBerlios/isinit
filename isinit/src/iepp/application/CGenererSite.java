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

package iepp.application;

import java.awt.Color;

import iepp.application.ageneration.*;
import iepp.domaine.DefinitionProcessus;
import util.TaskMonitorDialog;
import iepp.Application;
import util.index.*;


/**
 *
 */

public class CGenererSite extends CommandeNonAnnulable
{
	private TaskMonitorDialog dialogAvancee = null;
	private TacheGeneration tacheGener;
	private DefinitionProcessus defProc;

	public CGenererSite (DefinitionProcessus defProc)
	{
		this.defProc = defProc;
	}

	public boolean executer()
	{
		this.initGeneration();
		this.tacheGener = new TacheGeneration();
		this.dialogAvancee = new TaskMonitorDialog(Application.getApplication().getFenetrePrincipale(), this.tacheGener);
		this.dialogAvancee.setTitle(Application.getApplication().getTraduction("generation_en_cours"));

		this.tacheGener.setTask(dialogAvancee);
		this.dialogAvancee.show();

		return tacheGener.isGenerationReussie();
	}

	private void initGeneration()
	{
		// on sauvegarde l'ordre de la liste dans le Generation Manager
		GenerationManager.getInstance().setListeAGenerer(this.defProc.getListeAGenerer());
		// on modifie le chemin de generation
		GenerationManager.getInstance().setCheminGeneration(this.defProc.getRepertoireGeneration());
		//on modifie la couleur des éléments sélectionnés dans l'arbre
		GenerationManager.getInstance().setCouleurSurligne(new Color(Integer.parseInt(Application.getApplication().getConfigPropriete("couleur_arbre"))));
		// feuille de style
		GenerationManager.getInstance().setFeuilleCss(Application.getApplication().getConfigPropriete("feuille_style"));
		// contenu
		GenerationManager.getInstance().setPlaceContenu(Application.getApplication().getConfigPropriete("place_contenu"));
                // page AJOUT 2XMI Albert
                GenerationManager.getInstance().setPlacePage(Application.getApplication().getConfigPropriete("place_page"));
                // page AJOUT 2XMI Albert
                GenerationManager.getInstance().setPlaceAssemblage(Application.getApplication().getConfigPropriete("place_assemblage"));
		// info-bulle
                //modif 2XMI Amandine
		GenerationManager.getInstance().setInfoBulle(Application.getApplication().getConfigPropriete("info_bulle"));

                GenerationManager.getInstance().setInfoBulleActivite(Application.getApplication().getConfigPropriete("info_bulle_activite"));
		// statistiques
		GenerationManager.getInstance().setStatistiques(Application.getApplication().getConfigPropriete("statistiques"));
		// recapitulatif
		GenerationManager.getInstance().setRecap(Application.getApplication().getConfigPropriete("recapitulatif"));

	}
}

