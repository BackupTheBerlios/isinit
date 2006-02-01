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
import iepp.ui.iedition.dessin.rendu.liens.LienEdgeFusion;
import iepp.ui.iedition.dessin.rendu.liens.LienEdgeNote;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;


public class EnregistreurDP
{
	/*
	 * Fichier zip contenant la sauvegarde XML de la définition processus
	 */
	private ZipOutputStream mZipFile;
	
	private VueDPGraphe vdpg; 
	
	// Propriets pour le xml
	boolean statiqueTrouve;
	
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
		
		vdpg = Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe();
		statiqueTrouve = false;
		
		Element racine = new Element("ieppnit");
		
		//On crée un nouveau Document JDOM basé sur la racine que l'on vient de créer
		Document document = new Document(racine);
		
		Element proprietes = sauverProprietes();
		if (proprietes.getChildren().size()>0) {
			racine.addContent(proprietes);
		}
		
		Element statique = sauverStatique();
		if (statique.getChildren().size()>0) {
			racine.addContent(statique);
		}

		// On sauve le fichier xml dans le zip
		XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
		sortie.output(document, new DataOutputStream( new BufferedOutputStream(mZipFile)));
		
		// On ferme l'entrée du zip
		mZipFile.closeEntry();
	}
	
	private Element sauverProprietes() {
		Element proprietes = new Element("proprietes");
		
		Element auteur = new Element("auteur");
		auteur.setText(Application.getApplication().getProjet().getDefProc().getAuteur());
		proprietes.addContent(auteur);
		
		Element commentaires = new Element("commentaires");
		commentaires.setText(Application.getApplication().getProjet().getDefProc().getCommentaires());
		proprietes.addContent(commentaires);
		
		Element definition = new Element("definition");
		definition.setText(Application.getApplication().getProjet().getDefProc().getNomDefProc());
		proprietes.addContent(definition);
		
		Element email = new Element("email");
		email.setText(Application.getApplication().getProjet().getDefProc().getEmailAuteur());
		proprietes.addContent(email);
		
		Element zoom = new Element("zoom");
		zoom.setText(Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().getScale()+"");
		proprietes.addContent(zoom);
		
		return proprietes;		
	}
	
	private Element sauverStatique() {
		Element statique = new Element("statique");
		
		// On sauve les composants
		Element composants = sauverComposants();
		if (composants.getChildren().size()>0) {
			statique.addContent(composants);
		}
		
		// On sauve les produits
		Element produits = sauverProduits();
		if (produits.getChildren().size()>0) {
			statique.addContent(produits);
		}
		
		// On sauve les liens
		Element liens = sauverLiens();
		if (liens.getChildren().size()>0) {
			statique.addContent(liens);
		}

		// On sauvegarde les notes
		Element notes = sauverNotes();
		if (notes.getChildren().size()>0) {
			statique.addContent(notes);
		}
		
		return statique;
	}
	
	private Element sauverComposants() {
		Element composants = new Element("composants");
		
		// On sauve chaque composants
		Vector listComposants = vdpg.getComposantCellCells();
		for( int i = 0 ; i < listComposants.size() ; i++) {
			ComposantCell cc = (ComposantCell)listComposants.get(i);
			Element compsosant = new Element("composant");
			
			Element identifiant = new Element("identifiant");
			identifiant.setText(Application.getApplication().getReferentiel().chercherId( cc.getCompProc() )+"");
			compsosant.addContent(identifiant);
			
			Element nom = new Element("nom");
			nom.setText(cc.nomComposantCellule);
			compsosant.addContent(nom);
			
			Element positionx = new Element("positionx");
			positionx.setText(cc.getAbscisse()+"");
			compsosant.addContent(positionx);
			
			Element positiony = new Element("positiony");
			positiony.setText(cc.getOrdonnee()+"");
			compsosant.addContent(positiony);
			
			Element largeur = new Element("largeur");
			largeur.setText(cc.getLargeur()+"");
			compsosant.addContent(largeur);
			
			Element hauteur = new Element("hauteur");
			hauteur.setText(cc.getHauteur()+"");
			compsosant.addContent(hauteur);
			
			Element imageprod = new Element("imageprod");
			imageprod.setText(cc.getImageComposant());
			compsosant.addContent(imageprod);
			
			composants.addContent(compsosant);
		}
		return composants;
	}

	private Element sauverProduits() {
		Element produits = new Element("produits");
		
		// On sauvegarde les produits en entree
		sauverProduitsEntree(produits);
		
		// On sauvegarde les produits en sortie
		sauverProduitsSortie(produits);
		
		// On sauvegarde les produits fusion
		sauverProduitsFusion(produits);
		
		return produits;
	}
	
	private void sauverProduitsEntree(Element produits) {
		// On sauve chaque produits en entree
		Vector listProduitsEntree = vdpg.getProduitCellEntreeCells();
		int nb = listProduitsEntree.size();
		for( int i = 0 ; i < nb ; i++) {
			ProduitCellEntree pce = (ProduitCellEntree)listProduitsEntree.get(i);
			Element produit = new Element("produitsentree");
			
			Element identifiant = new Element("identifiant");
			identifiant.setText(Application.getApplication().getReferentiel().chercherId(pce.getId().getRef())+"");
			produit.addContent(identifiant);
			
			Element nom = new Element("nom");
			nom.setText(pce.nomComposantCellule);
			produit.addContent(nom);
			
			Element positionx = new Element("positionx");
			positionx.setText(pce.getAbscisse()+"");
			produit.addContent(positionx);
			
			Element positiony = new Element("positiony");
			positiony.setText(pce.getOrdonnee()+"");
			produit.addContent(positiony);
			
			Element largeur = new Element("largeur");
			largeur.setText(pce.getLargeur()+"");
			produit.addContent(largeur);
			
			Element hauteur = new Element("hauteur");
			hauteur.setText(pce.getHauteur()+"");
			produit.addContent(hauteur);
			
			Element imageprod = new Element("imageprod");
			imageprod.setText(pce.getImageComposant());
			produit.addContent(imageprod);
			
			Element visible = new Element("visible");
			visible.setText("false");
			produit.addContent(visible);
			
			Element lie = new Element("lie");
			lie.setText(pce.isCellLiee()+"");
			produit.addContent(lie);

			produits.addContent(produit);
		}
	}

	private void sauverProduitsSortie(Element produits) {
		// On sauve chaque produits en sortie
		Vector listProduitsSortie = vdpg.getProduitCellSortieCells();
		for( int i = 0 ; i < listProduitsSortie.size() ; i++) {
			ProduitCellSortie pcs= (ProduitCellSortie)listProduitsSortie.get(i);
			Element produit = new Element("produitssortie");
			
			Element identifiant = new Element("identifiant");
			identifiant.setText(Application.getApplication().getReferentiel().chercherId(pcs.getId().getRef())+"");
			produit.addContent(identifiant);
			
			Element nom = new Element("nom");
			nom.setText(pcs.nomComposantCellule);
			produit.addContent(nom);
			
			Element positionx = new Element("positionx");
			positionx.setText(pcs.getAbscisse()+"");
			produit.addContent(positionx);
			
			Element positiony = new Element("positiony");
			positiony.setText(pcs.getOrdonnee()+"");
			produit.addContent(positiony);
			
			Element largeur = new Element("largeur");
			largeur.setText(pcs.getLargeur()+"");
			produit.addContent(largeur);
			
			Element hauteur = new Element("hauteur");
			hauteur.setText(pcs.getHauteur()+"");
			produit.addContent(hauteur);
			
			Element imageprod = new Element("imageprod");
			imageprod.setText(pcs.getImageComposant());
			produit.addContent(imageprod);
			
			Element visible = new Element("visible");
			visible.setText("false");
			produit.addContent(visible);
			
			Element lie = new Element("lie");
			lie.setText(pcs.isCellLiee()+"");
			produit.addContent(lie);

			produits.addContent(produit);
		}
	}

	private void sauverProduitsFusion(Element produits) {
		// On sauve chaque produits en sortie
		Vector listProduitsFusion = vdpg.getProduitCellFusionCells();
		for( int i = 0 ; i < listProduitsFusion.size() ; i++) {
			ProduitCellFusion pcf= (ProduitCellFusion)listProduitsFusion.get(i);
			Element produit = new Element("produitsfusion");
			
			Element identifiant = new Element("identifiant");
			identifiant.setText(Application.getApplication().getReferentiel().chercherId(pcf.getId().getRef())+"");
			produit.addContent(identifiant);
			
			Element nom = new Element("nom");
			nom.setText(pcf.nomComposantCellule);
			produit.addContent(nom);
			
			Element positionx = new Element("positionx");
			positionx.setText(pcf.getAbscisse()+"");
			produit.addContent(positionx);
			
			Element positiony = new Element("positiony");
			positiony.setText(pcf.getOrdonnee()+"");
			produit.addContent(positiony);
			
			Element largeur = new Element("largeur");
			largeur.setText(pcf.getLargeur()+"");
			produit.addContent(largeur);
			
			Element hauteur = new Element("hauteur");
			hauteur.setText(pcf.getHauteur()+"");
			produit.addContent(hauteur);
			
			Element imageprod = new Element("imageprod");
			imageprod.setText(pcf.getImageComposant());
			produit.addContent(imageprod);
			
			Element produitsorigine = new Element("produitsorigine");
			
				Element p1 = new Element("produit");
					
					Element id1 = new Element("id");
					id1.setText(Application.getApplication().getReferentiel().chercherId(pcf.getProduitCellEntree().getId().getRef())+"");
					p1.addContent(id1);
				
					Element nom1 = new Element("nom");
					nom1.setText(pcf.getProduitCellEntree().getNomCompCell());
					p1.addContent(nom1);
				
				Element p2 = new Element("produit");
					
					Element id2 = new Element("id");
					id2.setText(Application.getApplication().getReferentiel().chercherId(pcf.getProduitCellSortie().getId().getRef())+"");
					p2.addContent(id2);
				
					Element nom2 = new Element("nom");
					nom2.setText(pcf.getProduitCellSortie().getNomCompCell());
					p2.addContent(nom2);
				
				produitsorigine.addContent(p1);
				produitsorigine.addContent(p2);

			produit.addContent(produitsorigine);
			
			produits.addContent(produit);
		}
	}

	private Element sauverLiens() {
		Element liens = new Element("liens");
		Element lien;
		
		Vector listLiens = vdpg.getLiens();
		
		for( int i = 0 ; i < listLiens.size() ; i++) {
			if( listLiens.get(i) instanceof LienEdgeNote) {
				lien = sauverLienEdgeNote((LienEdgeNote)listLiens.get(i));
			}
			else if( listLiens.get(i) instanceof LienEdgeFusion) {
				lien = sauverLienEdgeFusion((LienEdgeFusion)listLiens.get(i));
			}
			else {
				lien = sauverLienEdge((LienEdge)listLiens.get(i));
			}
			liens.addContent(lien);
		}
		return liens;
	}
	
	private Element sauverLienEdgeNote(LienEdgeNote len) {
		TextCell tc = (TextCell) len.getSourceEdge();
		
		Element LienEdgeNote = new Element("liennote");
		
			Element source = new Element("source");
			
				Element positionx = new Element("positionx");
				positionx.setText(tc.getAbscisse()+"");
			
				Element positiony = new Element("positiony");
				positiony.setText(tc.getOrdonnee()+"");
			
			source.addContent(positionx);
			source.addContent(positiony);
				
			Element destination = new Element("destination");
			
				Element typedestination = new Element("typedestination");
				typedestination.setText(len.getDestination().getClass()+"");
				
				Element iddestination = new Element("iddestination");
				iddestination.setText(Application.getApplication().getReferentiel().chercherId( len.getDestination().getId().getRef() )+"");
		
				Element nomdestination = new Element("nomdestination");
				nomdestination.setText(len.getDestination().getNomCompCell());
		
			destination.addContent(typedestination);
			destination.addContent(iddestination);
			destination.addContent(nomdestination);
		
		LienEdgeNote.addContent(source);
		LienEdgeNote.addContent(destination);
		
		Element pointsancrage = new Element("pointsancrage");
		pointsancrage.setText("");
		LienEdgeNote.addContent(pointsancrage);
		return LienEdgeNote;
	}

	private Element sauverLienEdgeFusion(LienEdgeFusion lef) {
		Element LienEdgeFusion = new Element("lienfusion");
		
		Element source = new Element("source");
		
			Element typesource = new Element("typesource");
			typesource.setText(lef.getSourceEdge().getClass()+"");
			
			Element idsource = new Element("idsource");
			idsource.setText(Application.getApplication().getReferentiel().chercherId( lef.getSourceEdge().getId().getRef() )+"");
	
			Element nomsource = new Element("nomsource");
			nomsource.setText(lef.getSourceEdge().getNomCompCell());
	
		source.addContent(typesource);
		source.addContent(idsource);
		source.addContent(nomsource);
			
		Element destination = new Element("destination");
		
			Element typedestination = new Element("typedestination");
			typedestination.setText(lef.getDestination().getClass()+"");
			
			Element iddestination = new Element("iddestination");
			iddestination.setText(Application.getApplication().getReferentiel().chercherId( lef.getDestination().getId().getRef() )+"");
	
			Element nomdestination = new Element("nomdestination");
			nomdestination.setText(lef.getDestination().getNomCompCell());
	
		destination.addContent(typedestination);
		destination.addContent(iddestination);
		destination.addContent(nomdestination);
	
	LienEdgeFusion.addContent(source);
	LienEdgeFusion.addContent(destination);
	
	Element pointsancrage = new Element("pointsancrage");
	pointsancrage.setText("");
	LienEdgeFusion.addContent(pointsancrage);
	return LienEdgeFusion;
	}

	private Element sauverLienEdge(LienEdge le) {
		Element LienEdge = new Element("lien");
		
		Element source = new Element("source");
		
			Element typesource = new Element("typesource");
			typesource.setText(le.getSourceEdge().getClass()+"");
			
			Element idsource = new Element("idsource");
			idsource.setText(Application.getApplication().getReferentiel().chercherId( le.getSourceEdge().getId().getRef() )+"");
	
			Element nomsource = new Element("nomsource");
			nomsource.setText(le.getSourceEdge().getNomCompCell());
	
		source.addContent(typesource);
		source.addContent(idsource);
		source.addContent(nomsource);
			
		Element destination = new Element("destination");
		
			Element typedestination = new Element("typedestination");
			typedestination.setText(le.getDestination().getClass()+"");
			
			Element iddestination = new Element("iddestination");
			iddestination.setText(Application.getApplication().getReferentiel().chercherId( le.getDestination().getId().getRef() )+"");
	
			Element nomdestination = new Element("nomdestination");
			nomdestination.setText(le.getDestination().getNomCompCell());
	
		destination.addContent(typedestination);
		destination.addContent(iddestination);
		destination.addContent(nomdestination);
	
	LienEdge.addContent(source);
	LienEdge.addContent(destination);
	
	Element pointsancrage = new Element("pointsancrage");
	pointsancrage.setText("");
	LienEdge.addContent(pointsancrage);
	return LienEdge;
	}
	
	private Element sauverNotes() {
		Element notes = new Element("notes");
		
		Vector listNotes = vdpg.getNoteCellCells();
		for( int i = 0 ; i < listNotes.size() ; i++) {
			TextCell tc = (TextCell)listNotes.get(i);
			
			Element note = new Element("note");
			
			Element nom = new Element("texte");
			nom.setText(tc.getMessage());
			note.addContent(nom);
			
			Element positionx = new Element("positionx");
			positionx.setText(tc.getAbscisse()+"");
			note.addContent(positionx);
			
			Element positiony = new Element("positiony");
			positiony.setText(tc.getOrdonnee()+"");
			note.addContent(positiony);
			
			Element largeur = new Element("largeur");
			largeur.setText(tc.getLargeur()+"");
			note.addContent(largeur);
			
			Element hauteur = new Element("hauteur");
			hauteur.setText(tc.getHauteur()+"");
			note.addContent(hauteur);
			
			notes.addContent(note);
		}
		return notes;
	}
}