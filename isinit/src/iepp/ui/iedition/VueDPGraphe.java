package iepp.ui.iedition;

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

import iepp.Application;
import iepp.application.CAjouterComposantDP;
import iepp.application.aedition.CAjouterComposantGraphe;
import iepp.application.aedition.CAjouterComposantGrapheDyn;
import iepp.application.aedition.aoutil.OCreerElement;
import iepp.application.aedition.aoutil.OLier2Elements;
import iepp.application.aedition.aoutil.OSelection;
import iepp.application.aedition.aoutil.Outil;
import iepp.application.areferentiel.Referentiel;
import iepp.domaine.ComposantProcessus;
import iepp.domaine.DefinitionProcessus;
import iepp.domaine.IdObjetModele;
import iepp.ui.iedition.dessin.GraphModelView;
import iepp.ui.iedition.dessin.rendu.ComposantCell;
import iepp.ui.iedition.dessin.rendu.ComposantCellDyn;
import iepp.ui.iedition.dessin.rendu.ComposantCellElementDyn;
import iepp.ui.iedition.dessin.rendu.IeppCell;
import iepp.ui.iedition.dessin.rendu.ProduitCell;
import iepp.ui.iedition.dessin.rendu.ProduitCellEntree;
import iepp.ui.iedition.dessin.rendu.ProduitCellFusion;
import iepp.ui.iedition.dessin.rendu.ProduitCellSortie;
import iepp.ui.iedition.dessin.rendu.TextCell;
import iepp.ui.iedition.dessin.rendu.liens.LienEdge;
import iepp.ui.iedition.dessin.vues.ComposantElementDynView;
import iepp.ui.iedition.dessin.vues.ComposantView;
import iepp.ui.iedition.dessin.vues.LienEdgeView;
import iepp.ui.iedition.dessin.vues.ProduitView;
import iepp.ui.iedition.dessin.vues.TextView;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import org.jgraph.JGraph;
import org.jgraph.graph.CellMapper;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphModel;
import org.jgraph.graph.VertexView;

/**
 * Classe permettant d'afficher un diagramme d'assemblage de composant
 */
public class VueDPGraphe extends JGraph implements Observer, MouseListener,
		MouseMotionListener, Serializable, KeyListener, DropTargetListener {

	/**
	 * Outil courant.
	 */
	private Outil diagramTool;

	/**
	 * Mod�le du diagramme JGraph processus.
	 */
	private GraphModelView Gmodele;

	/**
	 * Mod�les des diagrammes JGraph processus et scenario.
	 */
	private Vector modelesDiagrammes;
	
	/**
	 * Cellule s�lectionn�s.
	 */
	private Vector selectionCells;

	/**
	 * Dimension de la zone � afficher
	 * sert � indiquer la taille setPreferedSize() du diagramme
	*/
	private Dimension zone_affichage;
	
	/**
	 * Outil pour la cr�ation des liens entre produits
	 */
	private OLier2Elements edgeTool = new OLier2Elements();
	
	/**
	 * Construire le diagramme � partir de la d�finition de processus et 
	 * d'un controleur
	 * @param defProc, donn�es � observer
	 */
	public VueDPGraphe(DefinitionProcessus defProc) {
		// la vue observe le mod�le
		defProc.addObserver(this);
		this.Gmodele=new GraphModelView(defProc.getNomDefProc(),true);
		this.setModel(Gmodele);

		this.setOpaque(true);
		this.setLayout(null);

		// initialiser les listes d'�l�ments
		this.selectionCells = new Vector();
                this.modelesDiagrammes=new Vector();
		this.modelesDiagrammes.add(Gmodele);

		// par d�fault, on utilise l'outil de s�lection
		this.diagramTool = new OSelection(this);

		// ajouter les controleurs
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addKeyListener(this);
		
		// Information pour la fenetre
		this.zone_affichage = this.getSize();
		this.setAutoscrolls(true);

		// on met la couleur par d�faut au diagramme
		this.setBackground(new Color(Integer.parseInt(Application
						.getApplication().getConfigPropriete(
								"couleur_fond_diagrmme"))));

		//on peut aussi d�poser des objets dans l'arbre drop target
		new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, this, true);

		this.setFocusable(true);
	}
	
	/**
	 * M�thode appel�e quand l'objet du domaine observ� est modifi�
	 * et qu'il appelle la m�thode notifyObservers()
	 */
	public void update(Observable o, Object arg) {

		this.repaint();
	}

	// recherche un modele a partir de son nom
	public GraphModelView ChercherModel(String nom){
		for (int i =0;i<this.modelesDiagrammes.size();i++)
		{
			GraphModelView courant=(GraphModelView)this.modelesDiagrammes.elementAt(i);
			if (courant.getNomDiagModel().equals(nom))
			{
				return (courant);
			}
		}
		return null;
	}
	//-------------------------------------------------------------------------
	//  Affichage
	//-------------------------------------------------------------------------

	/**
	 * Charge le diagramme
	 */
	public void charger() {
/*		 // Dessine les �l�ments
		 for (int i = 0; i < this.elements.size(); i++)
		 {
			 
			 if ( this.elements.elementAt(i) instanceof FComposantProcessus )
			 {
				 // On recupere l'identifiant de l'objet
				 IdObjetModele idcp = ((Figure)(this.elements.elementAt(i))).getModele().getId();
				 
				 // On cree la collection d'objets et la map
				 Vector vecObj = new Vector();
				 Map AllAttribute = GraphConstants.createMap();
				 
				 // On cree le produit correspond a l'identifiant
				 MDComposantProcessus mdp = new MDComposantProcessus(idcp);
					
				 // On recupere les coordonnees
				 int x = ((MDElement) ((FElement) (this.elements.elementAt(i))).getModele()).getX();
				 int y = ((MDElement) ((FElement) (this.elements.elementAt(i))).getModele()).getY();
				 
				 // On les met a jour
				 mdp.setX( x );
				 mdp.setY( y );
				 
				 // On cree la cellule
				 ComposantCell newProdCell = new ComposantCell( (FComposantProcessus) (this.elements.elementAt(i)) );
				 this.ajouterCell(newProdCell);
					
				 vecObj.add(newProdCell);
				 
				 AllAttribute.put(newProdCell, newProdCell.getAttributs());

				 getModel().insert( vecObj.toArray(), AllAttribute, null, null,null );
				 
				 vecObj.clear();
				 
				 this.repaint();
			 }
			 else if ( this.elements.elementAt(i) instanceof FProduit )
			 {
				 // On recupere l'identifiant de l'objet
				 IdObjetModele idcp = ((Figure)(this.elements.elementAt(i))).getModele().getId();
				 
				 // On cree la collection d'objets et la map
				 Vector vecObj = new Vector();
				 Map AllAttribute = GraphConstants.createMap();
				 
				 // On cree le produit correspond a l'identifiant
				 MDProduit mdp = new MDProduit(idcp);
					
				 // On recupere les coordonnees
				 int x = ((MDElement) ((FElement) (this.elements.elementAt(i))).getModele()).getX();
				 int y = ((MDElement) ((FElement) (this.elements.elementAt(i))).getModele()).getY();
				 
				 // On les met a jour
				 mdp.setX( x );
				 mdp.setY( y );
				 
				 // On cree la cellule
				 ProduitCell newProdCell = new ProduitCell( (FProduit) (this.elements.elementAt(i)) );
					
				 vecObj.add(newProdCell);
				 
				 AllAttribute.put(newProdCell, newProdCell.getAttributs());

				 getModel().insert( vecObj.toArray(), AllAttribute, null, null,null );
				 
				 vecObj.clear();
				 
				 this.repaint();
			 }
			 else if ( this.elements.elementAt(i) instanceof FProduitFusion )
			 {
				 // On recupere l'identifiant de l'objet
				 IdObjetModele idcp = ((Figure)(this.elements.elementAt(i))).getModele().getId();
				 
				 // On cree la collection d'objets et la map
				 Vector vecObj = new Vector();
				 Map AllAttribute = GraphConstants.createMap();
				 
				 // On cree le produit correspond a l'identifiant
				 MDProduit mdp = new MDProduit(idcp);
					
				 // On recupere les coordonnees
				 int x = ((MDElement) ((FElement) (this.elements.elementAt(i))).getModele()).getX();
				 int y = ((MDElement) ((FElement) (this.elements.elementAt(i))).getModele()).getY();
				 
				 // On les met a jour
				 mdp.setX( x );
				 mdp.setY( y );
				 
				 // On cree la cellule
				 /*
				 FProduitFusion pcf = (FProduitFusion) (this.elements.elementAt(i));
				 ProduitCellFusion newProdCellF =  new ProduitCellFusion( pcf, 
						 new ProduitCellEntree((FProduit) pcf.getLienFusion(0).getSource(), new ComposantCell( pcf ) ),
						 new ProduitCellSortie(pcf.getLienFusion(0).getDestination(),new ProduitCell( (FProduit) (this.elements.elementAt(i))) ) );

				// vecObj.add(newProdCellF);
				 
				 //AllAttribute.put(newProdCellF, newProdCellF.getAttributs());

				 getModel().insert( vecObj.toArray(), AllAttribute, null, null,null );
				 
				 vecObj.clear();
				 
				 this.repaint();
			 }
			 else if ( this.elements.elementAt(i) instanceof FNote )
			 {
				 TextCell note = new TextCell((FNote)this.elements.elementAt(i));
				 
				 Map NoteAttribute = GraphConstants.createMap();
				 //note.set = ((MDElement) ((FElement) (this.elements.elementAt(i))).getModele()).getId();
				 note.setAbscisse(((MDElement) ((FElement) (this.elements.elementAt(i))).getModele()).getX());
				 note.setOrdonnee(((MDElement) ((FElement) (this.elements.elementAt(i))).getModele()).getY());
				 note.setLargeur(((MDElement) ((FElement) (this.elements.elementAt(i))).getModele()).getLargeur());
				 note.setHauteur(((MDElement) ((FElement) (this.elements.elementAt(i))).getModele()).getHauteur());
				 note.setNomCompCell(((MDNote) ((FElement) (this.elements.elementAt(i))).getModele()).getMessage());
				 NoteAttribute.put(note, note.getAttributs());

				 this.getModel().insert(new Object[] { note }, NoteAttribute, null, null, null);
			 }
			 
			 else
			 {
				 System.out.println( ((FElement) (this.elements.elementAt(i))).toString() );
			 }
			 
		}
		 // Dessine les liens
		 for (int i = 0; i < this.liens.size(); i++)
		 {
			 FLien lien = ((FLien) (this.liens.elementAt(i)));
			 
			 Map AllAttribute = GraphConstants.createMap();
			 Vector vecObj = new Vector();
			 DefaultPort portS, portD;
			 IeppCell ieppcellS = null;
			 IeppCell ieppcellD = null;
			 // On recupere la cellule source
			 FElement fe1 = ((FLien) (this.liens.elementAt(i))).getSource();
			 FElement fe2 = ((FLien) (this.liens.elementAt(i))).getDestination();
			 
			 if ( (!this.elements.contains(fe1) ) || (!this.elements.contains(fe2) ) ) {
				 this.liens.remove( i );
				 if (!this.elements.contains(fe1) ) {
					 this.elements.remove( fe1 );
				 }
				 if (!this.elements.contains(fe2) ) {
					 this.elements.remove( fe2 );
				 }
			 }
			 else {
			 if ( fe1 instanceof FComposantProcessus ) {				 
				 int x1 = ((MDElement) fe1.getModele()).getX()+1;
				 int y1 = ((MDElement) fe1.getModele()).getY()+1;
				 Object cell1 = this.getFirstCellForLocation(x1, y1);
				 // Si on rencontre ce processus pour la premi�re fois
				 if ( cell1 == null ) {
					 ieppcellS = new ComposantCell( (FComposantProcessus) fe1 );
					 
					 this.ajouterCell(ieppcellS);
					 vecObj.add(ieppcellS);
					 AllAttribute.put(ieppcellS, ieppcellS.getAttributs());
					 getModel().insert( vecObj.toArray(), AllAttribute, null, null,null );
					 vecObj.clear();
				 }
				 // Sinon on r�cup�re l'existant
				 else {
					 ieppcellS = (ComposantCell) cell1;
				 }
			 }
			 if ( fe2 instanceof FComposantProcessus ) {
				 int x2 = ((MDElement) fe2.getModele()).getX()+1;
				 int y2 = ((MDElement) fe2.getModele()).getY()+1;
				 Object cell2 = this.getFirstCellForLocation(x2, y2);
				 // Si on rencontre ce processus pour la premi�re fois
				 if ( cell2 == null ) {
					 ieppcellD = new ComposantCell( (FComposantProcessus) fe2 );
					 
					 this.ajouterCell(ieppcellD);
					 vecObj.add(ieppcellD);
					 AllAttribute.put(ieppcellD, ieppcellD.getAttributs());
					 getModel().insert( vecObj.toArray(), AllAttribute, null, null,null );
					 vecObj.clear();
				 }
				 // Sinon on r�cupere l'existant
				 else {
					 ieppcellD = (ComposantCell) cell2;
				 }
			 }
			 AllAttribute = GraphConstants.createMap();
			 if ( fe1 instanceof FProduit ) {
				 ProduitCellEntree pce = new ProduitCellEntree( (FProduit) fe1, (ComposantCell) ieppcellD );
				 
				 ajouterCell( pce );
				  
				 vecObj.add(pce);
				 AllAttribute.put(pce, pce.getAttributs());
				 getModel().insert( vecObj.toArray(), AllAttribute, null, null,null );
				 vecObj.clear();
				 
				 ieppcellS = pce;
			 }
			 if ( fe1 instanceof FProduitFusion ) {
				 // Produit fusion
				 int x1 = ((MDElement) fe1.getModele()).getX()+1;
				 int y1 = ((MDElement) fe1.getModele()).getY()+1;
				 Object cell1 = this.getFirstCellForLocation(x1, y1);
				 // Si on rencontre ce processus pour la premi�re fois
				 if ( cell1 == null ) {
					 FProduitFusion pf = (FProduitFusion) fe1;
					 FProduit fp0 = pf.getProduits(0);
					 ProduitCellEntree pce = new ProduitCellEntree(fp0, (ComposantCell) ieppcellD);
					 FProduit fp1 = pf.getProduits(1);
					 ProduitCellSortie pcs = new ProduitCellSortie(fp1, (ComposantCell) ieppcellD);
					 ieppcellS = new ProduitCellFusion( (FProduitFusion) fe1, pce, pcs );
					 
					 this.ajouterCell(ieppcellS);
					 vecObj.add(ieppcellS);
					 AllAttribute.put(ieppcellS, ieppcellS.getAttributs());
					 getModel().insert( vecObj.toArray(), AllAttribute, null, null,null );
					 vecObj.clear();
				 }
				 // Sinon on r�cup�re l'existant
				 else {
					 ieppcellS = (ProduitCellFusion) cell1;
				 }
			 }
			 if ( fe2 instanceof FProduit ) {
				 ProduitCellSortie pcs = new ProduitCellSortie( (FProduit) fe2, (ComposantCell) ieppcellS );
				 
				 ajouterCell( pcs );
				 
				 vecObj.add(pcs);
				 AllAttribute.put(pcs, pcs.getAttributs());
				 getModel().insert( vecObj.toArray(), AllAttribute, null, null,null );
				 vecObj.clear();
				 
				 ieppcellD = pcs;
			 }
			 if ( fe2 instanceof FProduitFusion ) {
				 // Produit fusion
				 int x2 = ((MDElement) fe2.getModele()).getX()+1;
				 int y2 = ((MDElement) fe2.getModele()).getY()+1;
				 Object cell2 = this.getFirstCellForLocation(x2, y2);
				 // Si on rencontre ce processus pour la premi�re fois
				 if ( cell2 == null ) {
					 FProduitFusion pf = (FProduitFusion) fe2;
					 FProduit fp0 = pf.getProduits(0);
					 ProduitCellEntree pce = new ProduitCellEntree(fp0, (ComposantCell) ieppcellD);
					 FProduit fp1 = pf.getProduits(1);
					 ProduitCellSortie pcs = new ProduitCellSortie(fp1, (ComposantCell) ieppcellD);
					 ieppcellD = new ProduitCellFusion( (FProduitFusion) fe2, pce, pcs );
					 
					 this.ajouterCell(ieppcellD);
					 vecObj.add(ieppcellD);
					 AllAttribute.put(ieppcellD, ieppcellD.getAttributs());
					 getModel().insert( vecObj.toArray(), AllAttribute, null, null,null );
					 vecObj.clear();
				 }
				 // Sinon on r�cup�re l'existant
				 else {
					 ieppcellD = (ProduitCellFusion) cell2;
				 }
			 }
			 
			 AllAttribute = GraphConstants.createMap();
			 portS = ieppcellS.getPortComp();
			 portD = ieppcellD.getPortComp();

			 LienEdge lienComp = new LienEdge();
				  
			 AllAttribute.put(lienComp, lienComp.getEdgeAttribute());
			
			 ieppcellS.ajoutLien(lienComp);
			 ieppcellD.ajoutLien(lienComp);
				
			 vecObj.add(lienComp);
		     
			 ((MDLien) lien.getModele()).setSource((MDElement) ((FElement) fe1).getModele());
			 ((MDLien) lien.getModele()).setDestination((MDElement) ((FElement) fe2).getModele());
				
		     ConnectionSet cs = new ConnectionSet(lienComp, portS, portD);
		     
		     this.getModel().insert(vecObj.toArray(), AllAttribute, null, null, null);
		     this.getModel().insert(null, null, cs, null, null);
			 }
		 }*/
		 /*
		  * // Dessine les poign�es (handles)
		  * for (int i = 0; i < this.selection.size(); i++)
		  * {
		  * 	((Figure) (this.selection.elementAt(i))).displayHandles(g);
		  * } 
		  */
	}
	
	/**
	 * Repeind le diagramme.
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	}

	//---------------------------------------------------------------------
	//                       Gestion des figures
	//---------------------------------------------------------------------

	/**
	 * Recherche la figure sur laquelle on a click�
	 * @param x, abscisse du click
	 * @param y, ordonn�e du click
	 * @return la figure sur laquelle on a cliqu�, null sinon
	 */
	public IeppCell chercherFigure(int x, int y) {
		/*Vecteur v = new Vecteur(x, y);
		Figure f;
		int n;

		// Recherche parmi les �l�ments
		n = this.elements.size();
		for (int i = n - 1; i >= 0; i--) {
			f = (Figure) this.elements.elementAt(i);
			if (f.contient(v)) {
				return f;
			}
		}

		// Recherche parmi les liens
		n = this.liens.size();
		for (int i = n - 1; i >= 0; i--) {
			f = (Figure) this.liens.elementAt(i);
			if (f.contient(v)) {
				return f;
			}
		}*/
		return null;
	}

	public ComposantCell chercherComposant(long idcomp) {
		ComposantCell cc = null;
		Vector listComposants = this.getComposantCellCells();
		for( int i = 0 ; i < listComposants.size() ; i++) {
			ComposantCell c = (ComposantCell)listComposants.get(i);
			if ( idcomp == Application.getApplication().getReferentiel().chercherId( c.getCompProc() )) {
				cc = c;
			}
		}
		return cc;
	}

	public ProduitCell chercherProduit(long idprod, String nomprod) {
		ProduitCell pce = chercherProduitEntree(idprod, nomprod);
		if (pce == null) {
			ProduitCell pcs = chercherProduitSortie(idprod, nomprod);
			if (pcs == null) {
				System.out.println("Erreur lors d ela recherche du produit");
				return null;
			}
			else {
				return pcs;
			}
		}
		else {
			return pce;
		}
	}

	public ProduitCell chercherProduitEntree(long idprod, String nomprod) {
		ProduitCellEntree pc = null;
		Vector listProduitsEntree = this.getProduitCellEntreeCells();
		for( int i = 0 ; i < listProduitsEntree.size() ; i++) {
			ProduitCellEntree c = (ProduitCellEntree)listProduitsEntree.get(i);
			if (( idprod == Application.getApplication().getReferentiel().chercherId( c.getId().getRef() )) &&
				( nomprod == c.getNomCompCell() ))	{
				pc = c;
			}
		}
		return pc;
	}

	public ProduitCell chercherProduitSortie(long idprod, String nomprod) {
		ProduitCellSortie pc = null;
		Vector listProduitsSortie = this.getProduitCellSortieCells();
		for( int i = 0 ; i < listProduitsSortie.size() ; i++) {
			ProduitCellSortie c = (ProduitCellSortie)listProduitsSortie.get(i);
			if (( idprod == Application.getApplication().getReferentiel().chercherId( c.getId().getRef() )) &&
				( nomprod == c.getNomCompCell() ))	{
				pc = c;
			}
		}
		return pc;
	}
	
	public TextCell chercherTextCell(int x, int y) {
		TextCell c = null;
		Vector listTextCell = this.getNoteCellCells();
		for( int i = 0 ; i < listTextCell.size() ; i++) {
			c = (TextCell) listTextCell.get(i);
		}
		return c;
	}
	
	/**
	 * Retourne tous les liens du diagramme.
	 */
	public Enumeration liens() {
		return ((GraphModelView)this.getModel()).getLiens().elements();
	}

	public Vector getLiens() {
		return ((GraphModelView)this.getModel()).getLiens();
	}

	public void setLiens(Vector l) {
		((GraphModelView)this.getModel()).setLiens(l);
	}
	
	public void ajouterLien(LienEdge c) {
		((GraphModelView)this.getModel()).getLiens().addElement(c);
	}

	/**
	 * D�finition des acceseurs elementCells 
	 */
	
	public Vector getElementsCell() {
		return ((GraphModelView)this.getModel()).getElementCells();
	}
	
	public Enumeration elementsCell() {
		return ((GraphModelView)this.getModel()).getElementCells().elements();
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * D�finition des acceseurs selectionCells 
	 */
	public void setVectorSelectionCells(Vector l) {
		this.selectionCells.add(l);
	}
	
	public Vector getVectorSelectionCells() {
		return this.selectionCells;
	}
	
	public Enumeration selectionCellsVector() {
		return this.selectionCells.elements();
	}
	
	public Vector getComposantCellCells(){
		return ((GraphModelView)this.getModel()).getComposantCellCells();		
	}
	
	public Vector getProduitCellEntreeCells(){
		return ((GraphModelView)this.getModel()).getProduitCellEntreeCells();		
	}
	
	public Vector getProduitCellSortieCells(){
		return ((GraphModelView)this.getModel()).getProduitCellSortieCells();		
	}
	
	public Vector getProduitCellFusionCells(){
		return ((GraphModelView)this.getModel()).getProduitCellFusionCells();
	}
	
	public Vector getNoteCellCells(){
		return ((GraphModelView)this.getModel()).getNoteCellCells();		
	}
		
	/**
	 * Ajoute une cellule au diagramme (�l�ment ou lien).
	 * @param f, figure � ajouter au diagramme
	 */
	public void ajouterCell(DefaultGraphCell c) {
		((GraphModelView)this.getModel()).getElementCells().add(c);
		
		if(c instanceof ComposantCell){
			((GraphModelView)this.getModel()).getComposantCellCells().addElement(c);
		}else if (c instanceof ProduitCellEntree){
			((GraphModelView)this.getModel()).getProduitCellEntreeCells().addElement(c);
		}else if (c instanceof ProduitCellSortie){
			((GraphModelView)this.getModel()).getProduitCellSortieCells().addElement(c);
		}else if (c instanceof ProduitCellFusion){
			((GraphModelView)this.getModel()).getProduitCellFusionCells().addElement(c);
		}
	}
	
	/**
	 * Supprime un �l�ment du diagramme.
	 * @param f, l'�l�ment � supprimer du diagramme
	 */
	public void supprimerCellule(DefaultGraphCell cell) {
		
		// enlever l'�l�ment de toutes les listes disponibles
		this.selectionCells.removeElement(cell);
		((GraphModelView)this.getModel()).getElementCells().removeElement(cell);
		((GraphModelView)this.getModel()).getProduitCellSortieCells().removeElement(cell);
		((GraphModelView)this.getModel()).getProduitCellEntreeCells().removeElement(cell);
		((GraphModelView)this.getModel()).getComposantCellCells().removeElement(cell);
		((GraphModelView)this.getModel()).getProduitCellFusionCells().removeElement(cell);
		
		Vector vecObj = new Vector();
		
		for (int i = 0; i < ((IeppCell) cell).getListeLien().size(); i++){
			((GraphModelView)this.getModel()).getLiens().removeElement(((IeppCell) cell).getListeLien().get(i));
			vecObj.add(((IeppCell) cell).getListeLien().get(i));
		}

		//((IeppCell) cell).removeAllChildren();
		
		vecObj.add(((IeppCell) cell).getPortComp());
		vecObj.add(cell);
		
		this.getModel().remove(vecObj.toArray());
		this.repaint();
		
	}

	public void MasquerCellule(IeppCell cell) {
		Vector vecObj = new Vector();
		
		// On supprime tous les liens pointants vers la cellule
		for (int i = 0; i < ((IeppCell) cell).getListeLien().size(); i++){
			((GraphModelView)this.getModel()).getLiens().removeElement(((IeppCell) cell).getListeLien().get(i));
			vecObj.add(((IeppCell) cell).getListeLien().get(i));
		}

		//((IeppCell) cell).removeAllChildren();
		
		// On supprime le port et la cellule
		vecObj.add(cell.getPortComp());
		vecObj.add(cell);
		
		this.getModel().remove(vecObj.toArray());
		this.repaint();
	}
	
	public void AfficherCelluleMasquee(IeppCell cell) {
		if( cell instanceof ProduitCellEntree) {
			AfficherCelluleEntreeMasquee((ProduitCellEntree) cell);
		}
		else if( cell instanceof ProduitCellSortie) {
			AfficherCelluleSortieMasquee((ProduitCellSortie) cell);
		}
		else
		{
			System.out.println("Impossible d'afficher ce type de cellule");
		}
	}
	
	public void AfficherCelluleEntreeMasquee(ProduitCellEntree cell) {
		ComposantCell cc;
		 
		 ProduitCellEntree pce;
		 
		 // On cree un edge pour la connection
		 LienEdge edge = new LienEdge();
		 
		 // On declare la source et l'extremite
		 cc = this.chercherComposant(Application.getApplication().getReferentiel().chercherId(cell.getCompParent().getCompProc()));
		 pce = (ProduitCellEntree)this.chercherProduit(Application.getApplication().getReferentiel().chercherId( cc.getCompProc() ), cell.getNomCompCell());
	     pce.setPortComp(new DefaultPort());		 
		 pce.setCellLiee(false);
		 edge.setSourceEdge(pce);
		 edge.setDestination(cc);

		 // on cree la map
		 Map AllAttribute = GraphConstants.createMap();

		 // On ajoute l'edge
		 AllAttribute.put(edge, edge.getEdgeAttribute());
		 AllAttribute.put(pce, pce.getAttributs());

		 // On recupere les ports
	     DefaultPort portS = cc.getPortComp();
	     DefaultPort portD = pce.getPortComp();
	     
		 cc.ajoutLien(edge);
		 pce.ajoutLien(edge);
		 
		 ConnectionSet cs = new ConnectionSet(edge, portD, portS);
		 
		 // On l'ajoute au modele
		 Vector vecObj = new Vector();
		 vecObj.add(pce);
		 vecObj.add(edge);

		 this.getModel().insert(vecObj.toArray(), AllAttribute, null, null, null);
		 this.getModel().insert(null, null, cs, null, null);

		 this.ajouterLien(edge);

		 repaint();
	}
	public void AfficherCelluleSortieMasquee(ProduitCellSortie cell) {
		 ProduitCellSortie pcs;
		 
		 // On cree un edge pour la connection
		 LienEdge edge = new LienEdge();
		 
		 // On declare la source et l'extremite
		 ComposantCell cc = this.chercherComposant(Application.getApplication().getReferentiel().chercherId(cell.getCompParent().getCompProc()));
		 pcs = (ProduitCellSortie) this.chercherProduit(Application.getApplication().getReferentiel().chercherId( cc.getCompProc() ), cell.getNomCompCell());
		 pcs.setPortComp(new DefaultPort());
		 pcs.setCellLiee(false);
		 edge.setSourceEdge(cc);
		 edge.setDestination(pcs);

		 // on cree la map
		 Map AllAttribute2 = GraphConstants.createMap();

		 // On ajoute l'edge
		 AllAttribute2.put(edge, edge.getEdgeAttribute());
		 AllAttribute2.put(pcs, pcs.getAttributs());

		 // On recupere les ports
	     DefaultPort portS2 = cc.getPortComp();
	     DefaultPort portD2 = pcs.getPortComp();
		 
		 pcs.ajoutLien(edge);
		 cc.ajoutLien(edge);
		 
		 ConnectionSet cs2 = new ConnectionSet(edge, portS2, portD2);
		 
		 // On l'ajoute au modele
		 Vector vecObj2 = new Vector();
		 vecObj2.add(pcs);
		 vecObj2.add(edge);

		 this.getModel().insert(vecObj2.toArray(), AllAttribute2, null, null, null);
		 this.getModel().insert(null, null, cs2, null, null);

		 //this.diagramme.ajouterCell(ps);
		 this.ajouterLien(edge);
				
		 repaint();
	}
	public void supprimerLien(LienEdge l) {
		((GraphModelView)this.getModel()).getLiens().removeElement(l);
	}

	//---------------------------------------------------------------------
	//                       Gestion de la s�lection
	//---------------------------------------------------------------------



	/**
	 * S�lectionne une cell.
	 */
	public void selectionneCell(DefaultGraphCell cell) {
		if (!this.selectionCells.contains(cell)) {
			this.selectionCells.addElement(cell);
		}
	}
	
	
	/**
	 * D�s�lectionne toutes les figures de la s�lection.
	 */
	public void clearSelection() {
		this.selectionCells.removeAllElements();
		this.setSelectionCells(null);
	}

	/**
	 * Efface compl�tement le diagramme
	 */
	public void effacerDiagramme() {
		((GraphModelView)this.getModel()).getLiens().removeAllElements();
		((GraphModelView)this.getModel()).getElementCells().removeAllElements();
		this.selectionCells.removeAllElements();
		this.removeAll();
		this.repaint();
	}

	/**
	 * S�lectionne tous les �l�ments du diagramme.
	 */
	public void selectionnerTout() {
		
		Enumeration e = ((GraphModelView)this.getModel()).getElementCells().elements();
		while (e.hasMoreElements()) {
			DefaultGraphCell cell = (DefaultGraphCell) e.nextElement();
			if (!this.selectionCells.contains(cell)) {
				this.selectionCells.addElement(cell);
			}
			this.setSelectionCells(selectionCells.toArray());
		}
		
		// mettre � jour l'affichage
		this.repaint();
	}

	
	public Dimension getZoneAffichage() {
		this.zone_affichage.height = this.getHeight();
		this.zone_affichage.width = this.getWidth();
		return this.zone_affichage;
	}


	//-------------------------------------------------------------------------
	//                           Gestion des outils
	//-------------------------------------------------------------------------

	/**
	 * Renvoie l'outil courant.
	 */
	public Outil getOutil() {
		return this.diagramTool;
	}

	/**
	 * Fixe l'outil courant.
	 */
	public void setOutil(Outil o) {
		this.diagramTool = o;
	}

	/**
	 * Fixe l'outil courant en tant que OSelection.
	 */
	public void setOutilSelection() {
		
		this.setOutil(new OSelection(this));
		edgeTool.uninstall(this);
		this.update(this.getGraphics());
	}

	/**
	 * Fixe l'outil courant en tant que OLier2Element.
	 */
	public void setOutilLier() {
		
		edgeTool.install(this);
		
		this.update(this.getGraphics());
	}

	/**
	 * Fixe l'outil courant en tant que OCreerElement
	 */
	public void setOutilCreerElement() {
		
		this.setOutil(new OCreerElement(this));
		edgeTool.uninstall(this);
	}

	//---------------------------------------------------------------------
	//    Gestion des actions sur le diagramme
	//---------------------------------------------------------------------

	public void mouseClicked(MouseEvent e)
	{
		this.diagramTool.mouseClicked(e);
	}

	public void mousePressed( MouseEvent e )
	{
		this.diagramTool.mousePressed(e);
	}

	public void mouseReleased(MouseEvent e) {
		
		this.diagramTool.mouseReleased(e);
	}

	public void mouseEntered(MouseEvent e) {
		
		this.diagramTool.mouseEntered(e);
	}

	public void mouseExited(MouseEvent e) {
		
		this.diagramTool.mouseExited(e);
	}

	public void mouseDragged(MouseEvent e) {
		
		this.diagramTool.mouseDragged(e);
	}
	
	public void mouseMoved(MouseEvent event) {
		
		this.diagramTool.mouseMoved(event);		
	}


	//---------------------------------------------------------------------
	//    gestion du clavier sur le diagramme
	//---------------------------------------------------------------------

	public void keyPressed(KeyEvent e) {
		
	}

	public void keyReleased(KeyEvent e) {
		this.diagramTool.keyReleased(e);
	}

	public void keyTyped(KeyEvent e) {
		
	}
	
	//---------------------------------------------------------------------
	//    gestion du drop sur le diagramme
	//---------------------------------------------------------------------

	public void dragEnter(DropTargetDragEvent arg0) {
	}

	public void dragOver(DropTargetDragEvent arg0) {
	}

	public void dropActionChanged(DropTargetDragEvent arg0) {
	}

	public void dragExit(DropTargetEvent arg0) {
	}

	/**
	 * R�cup�re l'�l�ment qui a �t� gliss� d�pos� sur le diagramme
	 * cet �l�ment est obligatoirement un idcomposant
	 * @see java.awt.dnd.DropTargetListener#drop(java.awt.dnd.DropTargetDropEvent)
	 */
	public void drop(DropTargetDropEvent dtde) {

		// r�cup�rer l'objet d�plac�
		Transferable transferable = dtde.getTransferable();
		DataFlavor[] flavors = transferable.getTransferDataFlavors();

		// r�cup�rer l'endroit o� l'utilisateur � d�placer l'objet
		Point p = dtde.getLocation();

		try {
			// R�cup�rer l'objet transferr� par glissement
			Object obj = transferable.getTransferData(flavors[0]);
			IdObjetModele id = null; // composant � afficher
			// Si c'est un Long, il s'agit en fait de l'id du composant dans le r�f�rentiel
			// => Charger le composant
			if (obj instanceof Long) {
				// R�cup�rer l'id
				long idComp = ((Long) obj).longValue();
				// Ajouter le composant � la DP
				CAjouterComposantDP commande = new CAjouterComposantDP(idComp);
				if (commande.executer()) {
					Application.getApplication().getProjet().setModified(true);
				}
				// R�cup�rer sa r�f�rence et remplir l'Id
				Referentiel ref = Application.getApplication().getReferentiel();
				ComposantProcessus comp = (ComposantProcessus) ref
						.chercherReference(idComp);
				if (comp != null) {
					id = comp.getIdComposant();
				} else {
					dtde.dropComplete(false);
					return;
				}
			}
			// Sinon c'est un vrai id, � r�cup�rer
			else {
				id = (IdObjetModele) obj;
			}
			dtde.dropComplete(true);

			if(((GraphModelView) getModel()).getType())
			{
			        CAjouterComposantGraphe c = new CAjouterComposantGraphe(id, p);
			        if (c.executer()) {
			        	Application.getApplication().getProjet().setModified(true);
	        		}

			}
			else
			{
				int nbre = ((GraphModelView) getModel()).getElementCells().size();
				CAjouterComposantGrapheDyn c = new CAjouterComposantGrapheDyn(id, new Point(50+nbre*200,25));
				if (c.executer()) {
					Application.getApplication().getProjet().setModified(true);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		dtde.dropComplete(false);
		
	}

	/**
	 * @param IdObjetModele de l'objet
	 * @return FElement si l'objet est present dans le graphe, null sinon
	 */
	public DefaultGraphCell contient(IdObjetModele id) {

		if (((GraphModelView)this.getModel()).getType()){
			IeppCell courant;
			for (int i = 0; i < ((GraphModelView)this.getModel()).getElementCells().size(); i++) {
				courant = (IeppCell) ((GraphModelView)this.getModel()).getElementCells().elementAt(i);
				if (courant.getId() != null) {
					if (courant.getId().equals(id)) {
						return courant;
					}
				}
			}
		}
		else{
			ComposantCellDyn courant;
			for (int i = 0; i < ((GraphModelView)this.getModel()).getElementCells().size(); i++) {
				courant = (ComposantCellDyn) ((GraphModelView)this.getModel()).getElementCells().elementAt(i);
				System.out.println("id courant"+courant.getId().toString());
				if (courant.getId() != null) {
					if (courant.getId().equals(id)) {
						return courant;
					}
				}
			}
		}
		/*
		for (int i = 0; i < this.elementCells.size(); i++) {
			courant = (IeppCell) this.elementCells.elementAt(i);
			if (courant.getId() != null) {
				if (courant.getId().equals(id)) {
					return courant;
				}
			}
		}*/
		return null;
	}
	
	
	protected void overlay(JGraph gpgraph, Graphics g, boolean clear) {
	
	
	}


	/** 
	 * @see JGraph#createVertexView(java.lang.Object, org.jgraph.graph.CellMapper)
	 * Gestion des vues des composants
	 */
	protected VertexView createVertexView(Object v, CellMapper cm) {

		// Return the appropriate view
		if (v instanceof ComposantCell) {
			return new ComposantView(v, this, cm);
		} else if (v instanceof ProduitCell) {
			return new ProduitView(v, this, cm);
		} else if (v instanceof ProduitCellFusion) {
			return new ProduitView(v, this, cm);
		} else if (v instanceof TextCell) {
			return new TextView(v, this, cm);
		} else if (v instanceof ComposantCellElementDyn) {
			return new ComposantElementDynView(v, this, cm);
		} else {
			return new VertexView(v, this, cm);
		}

	}

	/* (non-Javadoc)
	 * @see org.jgraph.JGraph#createEdgeView(java.lang.Object, org.jgraph.graph.CellMapper)
	 */
	protected EdgeView createEdgeView(Object v, CellMapper cm) {
		if (v instanceof LienEdge) {
			return new LienEdgeView(v, this, cm);
		}else {
			return new EdgeView(v, this, cm);
		}
	}

	public Vector getModelesDiagrammes() {
		return modelesDiagrammes;
	}

	public void setModelesDiagrammes(Vector modelesDiagrammes) {
		this.modelesDiagrammes = modelesDiagrammes;
	}
	
}
