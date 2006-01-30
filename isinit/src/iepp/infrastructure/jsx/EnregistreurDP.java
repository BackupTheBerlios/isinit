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
import iepp.ui.iedition.VueDPGraphe;
import iepp.ui.iedition.dessin.rendu.ComposantCell;
import iepp.ui.iedition.dessin.rendu.ProduitCellEntree;
import iepp.ui.iedition.dessin.rendu.ProduitCellFusion;
import iepp.ui.iedition.dessin.rendu.ProduitCellSortie;
import iepp.ui.iedition.dessin.rendu.TextCell;
import iepp.ui.iedition.dessin.rendu.liens.LienEdge;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
		ZipEntry entryZip = new ZipEntry("DefinitionProcessus.xml");
		mZipFile.putNextEntry(entryZip);
		
		DataOutputStream data = new DataOutputStream( new BufferedOutputStream(mZipFile) );
		
		VueDPGraphe vdpg = Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe();
		
		// Entete du fichier xml
		data.writeChars("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		data.writeChars("<ieppnit>\n");
		
		// Propriétés du projet
		data.writeChars("	<proprietes>\n");
		data.writeChars("		<auteur>\n");
		data.writeChars("			"+Application.getApplication().getProjet().getDefProc().getAuteur()+"\n");
		data.writeChars("		</auteur>\n");
		data.writeChars("		<commentaires>\n");
		data.writeChars("			"+Application.getApplication().getProjet().getDefProc().getCommentaires()+"\n");
		data.writeChars("		</commentaires>\n");
		data.writeChars("		<definition>\n");
		data.writeChars("			"+Application.getApplication().getProjet().getDefProc().getNomDefProc()+"\n");
		data.writeChars("		</definition>\n");
		data.writeChars("		<email>\n");
		data.writeChars("			"+Application.getApplication().getProjet().getDefProc().getEmailAuteur()+"\n");
		data.writeChars("		</email>\n");
		data.writeChars("		<zoom>\n");
		data.writeChars("			"+Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().getScale()+"\n");
		data.writeChars("		</zoom>\n");
		data.writeChars("	</proprietes>\n");
		
		
		// Partie Statique
		data.writeChars("	<statique>\n");
		
		// Sauvegarde de la liste des composants
		Vector listComposants = vdpg.getComposantCellCells();
		data.writeChars("		<composants>\n");
		for( int i = 0 ; i < listComposants.size() ; i++) {
			ComposantCell c = (ComposantCell)listComposants.get(i);
			data.writeChars("			<composant>\n");
			data.writeChars("				<identifiant>\n");
			data.writeChars("					"+Application.getApplication().getReferentiel().chercherId( c.getCompProc() )+"\n");
			data.writeChars("				</identifiant>\n");
			data.writeChars("				<nom>\n");
			data.writeChars("					"+c.nomComposantCellule+"\n");
			data.writeChars("				</nom>\n");
			data.writeChars("				<positionx>\n");
			data.writeChars("					"+c.getAbscisse()+"\n");
			data.writeChars("				</positionx>\n");
			data.writeChars("				<positiony>\n");
			data.writeChars("					"+c.getOrdonnee()+"\n");
			data.writeChars("				</positiony>\n");
			data.writeChars("				<largeur>\n");
			data.writeChars("					"+c.getLargeur()+"\n");
			data.writeChars("				</largeur>\n");
			data.writeChars("				<hauteur>\n");
			data.writeChars("					"+c.getHauteur()+"\n");
			data.writeChars("				</hauteur>\n");
			data.writeChars("				<imageprod>\n");
			data.writeChars("					"+c.getImageComposant()+"\n");
			data.writeChars("				</imageprod>\n");
			data.writeChars("			</composant>\n");
		}
		data.writeChars("		</composants>\n");

		
		// Sauvegarde de la liste des produits
		data.writeChars("		<produits>\n");
		// Sauvegarde de la liste des produits en entree
		Vector listProduitsEntree = vdpg.getProduitCellEntreeCells();
		for( int i = 0 ; i < listProduitsEntree.size() ; i++) {
			ProduitCellEntree c = (ProduitCellEntree)listProduitsEntree.get(i);
			data.writeChars("			<produitentree>\n");
			data.writeChars("				<identifiant>\n");
			data.writeChars("					"+Application.getApplication().getReferentiel().chercherId( c.getId().getRef() )+"\n");
			data.writeChars("				</identifiant>\n");
			data.writeChars("				<nom>\n");
			data.writeChars("					"+c.nomComposantCellule+"\n");
			data.writeChars("				</nom>\n");
			data.writeChars("				<positionx>\n");
			data.writeChars("					"+c.getAbscisse()+"\n");
			data.writeChars("				</positionx>\n");
			data.writeChars("				<positiony>\n");
			data.writeChars("					"+c.getOrdonnee()+"\n");
			data.writeChars("				</positiony>\n");
			data.writeChars("				<largeur>\n");
			data.writeChars("					"+c.getLargeur()+"\n");
			data.writeChars("				</largeur>\n");
			data.writeChars("				<hauteur>\n");
			data.writeChars("					"+c.getHauteur()+"\n");
			data.writeChars("				</hauteur>\n");
			data.writeChars("				<imageprod>\n");
			data.writeChars("					"+c.getImageComposant()+"\n");
			data.writeChars("				</imageprod>\n");
			data.writeChars("				<visible>\n");
			data.writeChars("					"+"\n");
			data.writeChars("				</visible>\n");
			data.writeChars("				<lie>\n");
			data.writeChars("					"+c.isCellLiee()+"\n");
			data.writeChars("				</lie>\n");
			data.writeChars("			</produitentree>\n");
		}
		
		// Sauvegarde de la liste des produits en sortie
		Vector listProduitsSortie = vdpg.getProduitCellSortieCells();
		for( int i = 0 ; i < listProduitsSortie.size() ; i++) {
			ProduitCellSortie c = (ProduitCellSortie)listProduitsSortie.get(i);
			data.writeChars("			<produitsortie>\n");
			data.writeChars("				<identifiant>\n");
			data.writeChars("					"+Application.getApplication().getReferentiel().chercherId( c.getId().getRef() )+"\n");
			data.writeChars("				</identifiant>\n");
			data.writeChars("				<nom>\n");
			data.writeChars("					"+c.nomComposantCellule+"\n");
			data.writeChars("				</nom>\n");
			data.writeChars("				<positionx>\n");
			data.writeChars("					"+c.getAbscisse()+"\n");
			data.writeChars("				</positionx>\n");
			data.writeChars("				<positiony>\n");
			data.writeChars("					"+c.getOrdonnee()+"\n");
			data.writeChars("				</positiony>\n");
			data.writeChars("				<largeur>\n");
			data.writeChars("					"+c.getLargeur()+"\n");
			data.writeChars("				</largeur>\n");
			data.writeChars("				<hauteur>\n");
			data.writeChars("					"+c.getHauteur()+"\n");
			data.writeChars("				</hauteur>\n");
			data.writeChars("				<imageprod>\n");
			data.writeChars("					"+c.getImageComposant()+"\n");
			data.writeChars("				</imageprod>\n");
			data.writeChars("				<visible>\n");
			data.writeChars("					"+"\n");
			data.writeChars("				</visible>\n");
			data.writeChars("				<lie>\n");
			data.writeChars("					"+c.isCellLiee()+"\n");
			data.writeChars("				</lie>\n");
			data.writeChars("			</produitsortie>\n");
		}

		// Sauvegarde de la liste des produits fusionnes
		Vector listProduitsFusion = vdpg.getProduitCellFusionCells();
		for( int i = 0 ; i < listProduitsFusion.size() ; i++) {
			ProduitCellFusion c = (ProduitCellFusion)listProduitsFusion.get(i);
			data.writeChars("			<produitfusion>\n");
			data.writeChars("				<identifiant>\n");
			data.writeChars("					"+Application.getApplication().getReferentiel().chercherId( c.getId().getRef() )+"\n");
			data.writeChars("				</identifiant>\n");
			data.writeChars("				<nom>\n");
			data.writeChars("					"+c.nomComposantCellule+"\n");
			data.writeChars("				</nom>\n");
			data.writeChars("				<positionx>\n");
			data.writeChars("					"+c.getAbscisse()+"\n");
			data.writeChars("				</positionx>\n");
			data.writeChars("				<positiony>\n");
			data.writeChars("					"+c.getOrdonnee()+"\n");
			data.writeChars("				</positiony>\n");
			data.writeChars("				<largeur>\n");
			data.writeChars("					"+c.getLargeur()+"\n");
			data.writeChars("				</largeur>\n");
			data.writeChars("				<hauteur>\n");
			data.writeChars("					"+c.getHauteur()+"\n");
			data.writeChars("				</hauteur>\n");
			data.writeChars("				<imageprod>\n");
			data.writeChars("					"+c.getImageComposant()+"\n");
			data.writeChars("				</imageprod>\n");
			data.writeChars("				<produitsorigine>\n");
			data.writeChars("					<produit>\n");
			data.writeChars("						<id>\n");
			data.writeChars("							"+Application.getApplication().getReferentiel().chercherId( c.getProduitCellEntree().getId().getRef() )+"\n");
			data.writeChars("						</id>\n");
			data.writeChars("						<nom>\n");
			data.writeChars("							"+c.getProduitCellEntree().getNomCompCell()+"\n");
			data.writeChars("						</nom>\n");
			data.writeChars("					</produit>\n");
			data.writeChars("					<produit>\n");
			data.writeChars("						<id>\n");
			data.writeChars("							"+Application.getApplication().getReferentiel().chercherId( c.getProduitCellSortie().getId().getRef() )+"\n");
			data.writeChars("						</id>\n");
			data.writeChars("						<nom>\n");
			data.writeChars("							"+c.getProduitCellSortie().getNomCompCell()+"\n");
			data.writeChars("						</nom>\n");
			data.writeChars("					</produit>\n");
			data.writeChars("				</produitsorigine>\n");
			data.writeChars("			</produitfusion>\n");
		}
		data.writeChars("		</produits>\n");

		// Sauvegarde de la liste des liens
		Vector listLiens = vdpg.getLiens();
		data.writeChars("		<liens>\n");
		for( int i = 0 ; i < listLiens.size() ; i++) {
			LienEdge l = (LienEdge)listLiens.get(i);
			data.writeChars("			<lien>\n");
			data.writeChars("				<source>\n");
			data.writeChars("					<typesource>\n");
		data.writeChars("						"+l.getSourceEdge().getClass()+"\n");
			data.writeChars("					</typesource>\n");
			data.writeChars("					<idsource>\n");
			data.writeChars("						"+Application.getApplication().getReferentiel().chercherId( l.getSourceEdge().getId().getRef() )+"\n");
			data.writeChars("					</idsource>\n");
			data.writeChars("					<nomsource>\n");
			data.writeChars("						"+l.getSourceEdge().getNomCompCell()+"\n");
			data.writeChars("					</nomsource>\n");
			data.writeChars("				</source>\n");
			data.writeChars("				<destination>\n");
			data.writeChars("					<typesource>\n");
			data.writeChars("						"+l.getDestination().getClass()+"\n");
			data.writeChars("					</typesource>\n");
			data.writeChars("					<iddestination>\n");
			data.writeChars("						"+Application.getApplication().getReferentiel().chercherId( l.getDestination().getId().getRef() )+"\n");
			data.writeChars("					</iddestination>\n");
			data.writeChars("					<nomdestination>\n");
			data.writeChars("						"+l.getDestination().getNomCompCell()+"\n");
			data.writeChars("					</nomdestination>\n");
			data.writeChars("				</destination>\n");
			/*
			data.writeChars("				<pointsancrage>\n");
			data.writeChars("				</pointsancrage>\n");
			*/
			data.writeChars("			</lien>\n");
		}
		data.writeChars("		</liens>\n");

		// Sauvegarde de la liste des composants
		Vector listNotes = vdpg.getNoteCellCells();
		data.writeChars("		<notes>\n");
		for( int i = 0 ; i < listNotes.size() ; i++) {
			TextCell n = (TextCell)listNotes.get(i);
			data.writeChars("			<note>\n");
			data.writeChars("				<texte>\n");
			data.writeChars("					"+n.getMessage());
			data.writeChars("				</texte>\n");
			data.writeChars("				<positionx>\n");
			data.writeChars("					"+n.getAbscisse());
			data.writeChars("				</positionx>\n");
			data.writeChars("				<positiony>\n");
			data.writeChars("					"+n.getOrdonnee());
			data.writeChars("				</positiony>\n");
			data.writeChars("				<largeur>\n");
			data.writeChars("					"+n.getLargeur());
			data.writeChars("				</largeur>\n");
			data.writeChars("				<hauteur>\n");
			data.writeChars("					"+n.getHauteur());
			data.writeChars("				</hauteur>\n");
			data.writeChars("			</note>\n");
		}
		data.writeChars("		</notes>\n");

		data.writeChars("	</statique>\n");
		data.writeChars("</ieppnit>");
		data.flush();
		
		/*
		Projet project = Application.getApplication().getProjet();
		
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
		*/
		
		mZipFile.closeEntry();
	}
}