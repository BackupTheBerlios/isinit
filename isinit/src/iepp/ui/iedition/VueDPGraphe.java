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
import iepp.application.aedition.aoutil.OCreerElement;
import iepp.application.aedition.aoutil.OSelection;
import iepp.application.aedition.aoutil.Outil;
import iepp.application.areferentiel.Referentiel;
import iepp.domaine.ComposantProcessus;
import iepp.domaine.DefinitionProcessus;
import iepp.domaine.IdObjetModele;
import iepp.ui.iedition.dessin.rendu.ComposantCell;
import iepp.ui.iedition.dessin.rendu.FComposantProcessus;
import iepp.ui.iedition.dessin.rendu.FElement;
import iepp.ui.iedition.dessin.rendu.FNote;
import iepp.ui.iedition.dessin.rendu.FProduit;
import iepp.ui.iedition.dessin.rendu.FProduitFusion;
import iepp.ui.iedition.dessin.rendu.Figure;
import iepp.ui.iedition.dessin.rendu.IeppCell;
import iepp.ui.iedition.dessin.rendu.LienEdge;
import iepp.ui.iedition.dessin.rendu.ProduitCell;
import iepp.ui.iedition.dessin.rendu.ProduitCellEntree;
import iepp.ui.iedition.dessin.rendu.ProduitCellFusion;
import iepp.ui.iedition.dessin.rendu.ProduitCellSortie;
import iepp.ui.iedition.dessin.rendu.TextCell;
import iepp.ui.iedition.dessin.rendu.handle.Handle;
import iepp.ui.iedition.dessin.rendu.liens.FLien;
import iepp.ui.iedition.dessin.rendu.liens.FLienInterface;
import iepp.ui.iedition.dessin.tools.EdgeTool;
import iepp.ui.iedition.dessin.vues.ComposantView;
import iepp.ui.iedition.dessin.vues.MDDiagramme;
import iepp.ui.iedition.dessin.vues.MDElement;
import iepp.ui.iedition.dessin.vues.MDLien;
import iepp.ui.iedition.dessin.vues.MDLienDotted;
import iepp.ui.iedition.dessin.vues.MDNote;
import iepp.ui.iedition.dessin.vues.MDProduit;
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
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphModel;
import org.jgraph.graph.VertexView;

import util.Vecteur;

/**
 * Classe permettant d'afficher un diagramme d'assemblage de composant
 */
public class VueDPGraphe extends JGraph implements Observer, MouseListener,
		MouseMotionListener, Serializable, DropTargetListener {

	/**
	 * Outil courant.
	 */
	private Outil diagramTool;

	/**
	 * Modèle du diagramme.
	 */
	private MDDiagramme modele;

	/**
	 * Modèle du diagramme JGraph.
	 */
	private GraphModel Gmodele = new DefaultGraphModel();

	/**
	 * Eléments présents sur le diagramme (Figure).
	 */
	private Vector elements;
	
	/**
	 * Eléments présents sur le diagramme (Cellule).
	 */
	private Vector elementCells;

	/**
	 * Liens présents sur le diagramme.
	 */
	private Vector liens;

	/**
	 * Figures sélectionnés.
	 */
	private Vector selection;
	
	/**
	 * Cellule sélectionnés.
	 */
	private Vector selectionCells;

	/**
	 * Dimension de la zone à afficher
	 * sert à indiquer la taille setPreferedSize() du diagramme
	*/
	private Dimension zone_affichage;
	
	/**
	 * Outil pour la création des liens entre produits
	 */
	private EdgeTool edgeTool = new EdgeTool(new LienEdge());
	
	/**
	 * Construire le diagramme à partir de la définition de processus et 
	 * d'un controleur
	 * @param defProc, données à observer
	 */
	public VueDPGraphe(DefinitionProcessus defProc) {
		// la vue observe le modèle
		defProc.addObserver(this);
		// le diagramme au départ est vide
		this.setModele(new MDDiagramme());

		this.setModel(Gmodele);

		this.setOpaque(true);
		this.setLayout(null);

		// initialiser les listes d'éléments
		this.elements = new Vector();
		this.liens = new Vector();
		this.selection = new Vector();
		this.elementCells = new Vector();
		this.selectionCells = new Vector();

		// par défault, on utilise l'outil de sélection
		this.diagramTool = new OSelection(this);

		// ajouter les controleurs
		this.addMouseListener(this);
		this.addMouseMotionListener(this);

		// Information pour la fenetre
		this.zone_affichage = this.getSize();
		this.setAutoscrolls(true);

		// on met la couleur par défaut au diagramme
		modele.setFillColor(new Color(Integer.parseInt(Application
						.getApplication().getConfigPropriete(
								"couleur_fond_diagrmme"))));

		//on peut aussi déposer des objets dans l'arbre drop target
		new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, this, true);

		this.setFocusable(true);
	}
	
	/**
	 * Méthode appelée quand l'objet du domaine observé est modifié
	 * et qu'il appelle la méthode notifyObservers()
	 */
	public void update(Observable o, Object arg) {

		this.repaint();
	}


	//-------------------------------------------------------------------------
	//  Affichage
	//-------------------------------------------------------------------------

	/**
	 * Charge le diagramme
	 */
	public void charger() {
		 // Dessine les éléments
		 for (int i = 0; i < this.elements.size(); i++)
		 {
			 if ( this.elements.elementAt(i) instanceof FComposantProcessus )
			 {
				 /*
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
				 */
			 }
			 else if ( this.elements.elementAt(i) instanceof FProduit )
			 {
				 /*
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
				 */
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
				*/

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
			 Map AllAttribute = GraphConstants.createMap();
			 Vector vecObj = new Vector();
			 DefaultPort portS, portD;
			 IeppCell ieppcellS = null;
			 IeppCell ieppcellD = null;
			 // On recupere la cellule source
			 FElement fe1 = ((FLien) (this.liens.elementAt(i))).getSource();
			 if ( fe1 instanceof FComposantProcessus ) {
				 ieppcellS = new ComposantCell( (FComposantProcessus) fe1 );
				 
				 int x1 = ((MDElement) fe1.getModele()).getX()+1;
				 int y1 = ((MDElement) fe1.getModele()).getY()+1;
				 Object cell1 = this.getFirstCellForLocation(x1, y1);
				 if ( cell1 == null ) {
					 this.ajouterCell(ieppcellS);
					 vecObj.add(ieppcellS);
					 AllAttribute.put(ieppcellS, ieppcellS.getAttributs());
					 getModel().insert( vecObj.toArray(), AllAttribute, null, null,null );
					 vecObj.clear();
				 }
				 else {
					 ieppcellS = (ComposantCell) cell1;
				 }
			 }
			 FElement fe2 = ((FLien) (this.liens.elementAt(i))).getDestination();
			 if ( fe2 instanceof FComposantProcessus ) {
				 int x2 = ((MDElement) fe2.getModele()).getX()+1;
				 int y2 = ((MDElement) fe2.getModele()).getY()+1;
				 Object cell2 = this.getFirstCellForLocation(x2, y2);
				 if ( cell2 == null ) {
					 ieppcellD = new ComposantCell( (FComposantProcessus) fe2 );
					 
					 this.ajouterCell(ieppcellD);
					 vecObj.add(ieppcellD);
					 AllAttribute.put(ieppcellD, ieppcellD.getAttributs());
					 getModel().insert( vecObj.toArray(), AllAttribute, null, null,null );
					 vecObj.clear();
				 }
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
			 /*
			 if ( fe1 instanceof FProduitFusion ) {
				 // Produit fusion
			 }
			 */
			 if ( fe2 instanceof FProduit ) {
				 ProduitCellSortie pcs = new ProduitCellSortie( (FProduit) fe2, (ComposantCell) ieppcellS );
				 
				 ajouterCell( pcs );
				 
				 vecObj.add(pcs);
				 AllAttribute.put(pcs, pcs.getAttributs());
				 getModel().insert( vecObj.toArray(), AllAttribute, null, null,null );
				 vecObj.clear();
				 
				 ieppcellD = pcs;
			 }
			 AllAttribute = GraphConstants.createMap();
			 portS = ieppcellS.getPortComp();
			 portD = ieppcellD.getPortComp();

			 LienEdge lienComp = new LienEdge();
				  
			 AllAttribute.put(lienComp, lienComp.getEdgeAttribute());
			
			 ieppcellS.ajoutLien(lienComp);
			 ieppcellD.ajoutLien(lienComp);
				
			 vecObj.add(lienComp);
		     
			 FLien lien = new FLienInterface(new MDLienDotted());
			 ((MDLien) lien.getModele()).setSource((MDElement) ((FElement) fe1).getModele());
			 ((MDLien) lien.getModele()).setDestination((MDElement) ((FElement) fe2).getModele());
				
		     //this.ajouterFigure( lien );
		     
		     ConnectionSet cs = new ConnectionSet(lienComp, portS, portD);
		     
		     this.getModel().insert(vecObj.toArray(), AllAttribute, null, null, null);
		     this.getModel().insert(null, null, cs, null, null);
		     
		     /*
			 CLierInterface c = new CLierInterface(this,
					  new FLienInterface(new MDLienDotted()),
					  new Vector(),
					  ieppcellS,
					  ieppcellD);
			 c.executer();
			 
			 /*

			 // On cree un edge pour la connection
			 LienEdge edge = new LienEdge();
			 
			 // On ajoute l'edge
			 AllAttribute.put(edge, edge.getEdgeAttribute());
			 
			 portS = ieppcellS.getPortComp();
			 portD = ieppcellD.getPortComp();
			 ConnectionSet cs = new ConnectionSet(edge, portS, portD);
			 
			 // On ajoute les liens aux cellules
			 ieppcellS.ajoutLien(edge);
			 ieppcellD.ajoutLien(edge);
			 
			 //System.out.println( cell1.getClass()+" - "+cell2.getClass() );
			 
			 /*
			 Vector vecObj = new Vector();
			 
			 if( cell1 instanceof ComposantCell ) {
				 //ComposantProcessus cp1 = new ComposantProcessus( cell1 );
				 if( cell2 instanceof ProduitCell ) {
					 //ProduitCellSortie npc = new ProduitCellSortie( ((ProduitCell)cell2).getFprod(), (ComposantCell) cell1 );
					 
					 //vecObj.add( (ProduitCell)cell2 );
					 
					 //AllAttribute.put( (ProduitCell)cell2, ( ((ProduitCell)cell2).getAttributs()) );
					 
					 //getModel().insert( vecObj.toArray(), AllAttribute, null, null,null );

					 //this.ajouterCell( npc );
					 
				 }
				 //( (ComposantProcessus) cell1).ajouterProduit( ((ProduitCellSortie)cell2).getNomCompCell(), 1);
				 //this.ajouterCell( (ProduitCellSortie) cell2 );
			 }
			 if( cell2 instanceof ComposantCell ) {
				 System.out.println( "ProduitCellEntree" );
				 //( (ComposantProcessus) cell2).ajouterProduit( ((ProduitCellEntree)cell1).getNomCompCell(), 0);
				 //this.ajouterCell( (ProduitCellEntree) cell1 );
			 }
			 
			 // On l'ajoute au modele
			 vecObj.add(edge);

			 this.getModel().insert(vecObj.toArray(), AllAttribute, null, null, null);
			 this.getModel().insert(null, null, cs, null, null);

			 vecObj.clear();
			 */
		 }
		 /*
		  * // Dessine les poignées (handles)
		  * for (int i = 0; i < this.selection.size(); i++)
		  * {
		  * 	((Figure) (this.selection.elementAt(i))).displayHandles(g);
		  * } 
		  */
	}
			 /*On recupere la cellule source
			 FElement fe1 = ((FLien) (this.liens.elementAt(i))).getSource();
			 int x1 = ((MDElement) fe1.getModele()).getX()+1;
			 int y1 = ((MDElement) fe1.getModele()).getY()+1;
			 Object cell1 = this.getFirstCellForLocation(x1, y1);
			 
			 // On recupere la cellule destination
			 FElement fe2 = ((FLien) (this.liens.elementAt(i))).getDestination();
			 int x2 = ((MDElement) fe2.getModele()).getX()+1;
			 int y2 = ((MDElement) fe2.getModele()).getY()+1;
			 Object cell2 = this.getFirstCellForLocation(x2, y2);
			 
			 // On cree un edge pour la connection
			 LienEdge edge = new LienEdge();
			 
			 // on cree la map
			 Map AllAttribute = GraphConstants.createMap();

			 // On ajoute l'edge
			 AllAttribute.put(edge, edge.getEdgeAttribute());

			 // On recupere les ports
			 DefaultPort portS = ((IeppCell) cell1).getPortComp();
			 DefaultPort portD = ((IeppCell) cell2).getPortComp();
			 
			 ConnectionSet cs;
			 cs = new ConnectionSet(edge, portS, portD);
			 
			 // On l'ajoute au modele
			 Vector vecObj = new Vector();
			 vecObj.add(edge);

			 this.getModel().insert(vecObj.toArray(), AllAttribute, null, null, null);
			 this.getModel().insert(null, null, cs, null, null);

			 vecObj.clear();
		 }
		 /*
		  * // Dessine les poignées (handles)
		  * for (int i = 0; i < this.selection.size(); i++)
		  * {
		  * 	((Figure) (this.selection.elementAt(i))).displayHandles(g);
		  * } 
		  */
	
	/**
	 * Repeind le diagramme.
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	}

	//-------------------------------------------------------------------------
	//                         Relations avec le modèle
	//-------------------------------------------------------------------------

	/**
	 * Retourne le modèle représenté par le diagramme.
	 * @return le modèle de dessin associé au diagramme
	 */
	public MDDiagramme getModele() {
		return this.modele;
	}

	/**
	 * Fixe le modèle de dessin représenté par le diagramme.
	 * @param m, modèle de dessin à afficher
	 */
	public void setModele(MDDiagramme m) {
		this.modele = m;
	}

	//---------------------------------------------------------------------
	//                       Gestion des figures
	//---------------------------------------------------------------------

	/**
	 * Recherche parmi les figures sélectionnées, si on a clické sur un handle.
	 * @param x, abscisse du click
	 * @param y, ordonnée du click
	 * @return le handle sur lequel on a cliqué, null su aucun handle n'est sélectionné
	 */
	public Handle chercherHandleFigure(int x, int y) {
		int n = this.selection.size();
		Vecteur v = new Vecteur(x, y);
		Figure f;
		Handle h;

		for (int i = 0; i < n; i++) {
			f = (Figure) this.selection.elementAt(i);
			h = f.getHandle(v.x, v.y);
			if (h != null) {
				if (h.getVisible() == true) {
					return h;
				}
			}
		}
		return null;
	}

	/**
	 * Recherche la figure sur laquelle on a clické
	 * @param x, abscisse du click
	 * @param y, ordonnée du click
	 * @return la figure sur laquelle on a cliqué, null sinon
	 */
	public Figure chercherFigure(int x, int y) {
		Vecteur v = new Vecteur(x, y);
		Figure f;
		int n;

		// Recherche parmi les éléments
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
		}
		return null;
	}

	/**
	 * Retourne tous les éléments du diagramme.
	 */
	public Enumeration elements() {
		return elements.elements();
	}

	/**
	 * Retourne tous les liens du diagramme.
	 */
	public Enumeration liens() {
		return liens.elements();
	}

	public Vector getElements() {
		return this.elements;
	}

	public Vector getLiens() {
		return this.liens;
	}

	public void setLiens(Vector l) {
		this.liens = l;
	}

	public void setElements(Vector l) {
		this.elements = l;
	}

	/**
	 * Définition des acceseurs elementCells 
	 */
	public void setElementsCell(Vector l) {
		this.elementCells.add(l);
	}
	
	public Vector getElementsCell() {
		return this.elementCells;
	}
	
	public Enumeration elementsCell() {
		return this.elementCells.elements();
	}
	
	/**
	 * Définition des acceseurs selectionCells 
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
	
	
	/**
	 * Ajoute une figure au diagramme (élément ou lien).
	 * @param f, figure à ajouter au diagramme
	 */
	public void ajouterFigure(Figure f) {
		
		// selon le type de la figure
		if (f instanceof FElement) {
			this.elements.addElement(f);
		}
		// lien
		else if (f instanceof FLien) {
			this.liens.addElement(f);
			((FLien) f).getSource().ajouterLien((FLien) f);
			((FLien) f).getDestination().ajouterLien((FLien) f);
		}
		this.getModele().ajouterModeleFigure(f.getModele());
		
		// mettre à jour l'affichage
		this.repaint();
	}
		
	/**
	 * Ajoute une cellule au diagramme (élément ou lien).
	 * @param f, figure à ajouter au diagramme
	 */
	public void ajouterCell(IeppCell c) {
		this.elementCells.addElement(c);
	}

	/**
	 * Supprime un élément du diagramme.
	 * @param f, l'élément à supprimer du diagramme
	 */
	public void supprimerFigure(Figure f) {
		
		// enlever l'élément de toutes les listes disponibles
		this.selection.removeElement(f);
		this.elements.removeElement(f);
		this.liens.removeElement(f);

		// enlever la figure du modèle de dessin
		this.getModele().supprimerModeleFigure(f.getModele());
	}
	
	/**
	 * Supprime un élément du diagramme.
	 * @param f, l'élément à supprimer du diagramme
	 */
	public void supprimerCellule(IeppCell cell) {
		
		// enlever l'élément de toutes les listes disponibles
		this.selectionCells.removeElement(cell);
		this.elementCells.removeElement(cell);
		
		Vector vecObj = new Vector();
		
		for (int i = 0; i < ((IeppCell) cell).getListeLien().size(); i++)
			vecObj.add(((IeppCell) cell).getListeLien()
					.get(i));

		vecObj.add(((IeppCell) cell).getPortComp());
		vecObj.add(cell);
		
		this.getModel().remove(vecObj.toArray());
		
	}

	//---------------------------------------------------------------------
	//                       Gestion de la sélection
	//---------------------------------------------------------------------

	/**
	 * Retourne tous les éléments sélectionnés.
	 */
	public Enumeration selectedElements() {
		return this.selection.elements();
	}

	/**
	 * Retourne le nombre d'éléments sélectionnés.
	 */
	public int nbSelectedElements() {
		return this.selection.size();
	}

	/**
	 * Sélectionne une figure.
	 */
	public void selectionneFigure(Figure figure) {
		if (!this.selection.contains(figure)) {
			this.selection.addElement(figure);
			figure.setSelectionne(true);
		}
	}

	/**
	 * Sélectionne une cell.
	 */
	public void selectionneCell(IeppCell cell) {
		if (!this.selectionCells.contains(cell)) {
			this.selectionCells.addElement(cell);
		}
	}
	
	/**
	 * Dé-sélectionne une figure.
	 */
	public void deSelectionneFigure(Figure figure) {
		this.selection.removeElement(figure);
		figure.setSelectionne(false);
	}

	/**
	 * Inverse la sélection d'une figure.
	 */
	public void changeSelection(Figure figure) {
		if (this.selection.contains(figure)) {
			this.deSelectionneFigure(figure);
		} else {
			this.selectionneFigure(figure);
		}
	}

	/**
	 * Inverse la sélection de toutes les figures comprises entre les deux points A (en haut à gauche)
	 * et B (en bas à droite).
	 */
	public void changeSelectionFigures(Vecteur A, Vecteur B) {
		Enumeration e = this.elements.elements();
		while (e.hasMoreElements()) {
			Figure figure = (Figure) e.nextElement();
			if (figure.appartient(A, B)) {
				this.changeSelection(figure);
			}
		}

		e = this.liens.elements();
		while (e.hasMoreElements()) {
			Figure figure = (Figure) e.nextElement();
			if (figure.appartient(A, B)) {
				this.changeSelection(figure);
			}
		}
	}

	/**
	 * Désélectionne toutes les figures de la sélection.
	 */
	public void clearSelection() {
		Figure figure;
		while (selection.size() > 0) {
			figure = (Figure) this.selection.elementAt(0);
			this.deSelectionneFigure(figure);
		}
		selectionCells.removeAllElements();
	}

	/**
	 * Efface complètement le diagramme
	 */
	public void effacerDiagramme() {
		this.selection.removeAllElements();
		this.liens.removeAllElements();
		this.elements.removeAllElements();
		this.elementCells.removeAllElements();
		this.selectionCells.removeAllElements();
		this.removeAll();
		this.repaint();
	}

	/**
	 * Sélectionne tous les éléments du diagramme.
	 */
	public void selectionnerTout() {
		
		Enumeration e = this.elements.elements();
		while (e.hasMoreElements()) {
			Figure figure = (Figure) e.nextElement();
			this.selectionneFigure(figure);
		}

		e = this.liens.elements();
		while (e.hasMoreElements()) {
			Figure figure = (Figure) e.nextElement();
			this.selectionneFigure(figure);
		}
		
		e = this.elementCells.elements();
		while (e.hasMoreElements()) {
			IeppCell cell = (IeppCell) e.nextElement();
			if (!this.selectionCells.contains(cell)) {
				this.selectionCells.addElement(cell);
			}
		}

		/*e = this.liens.elements();
		while (e.hasMoreElements()) {
			Figure figure = (Figure) e.nextElement();
			this.selectionneFigure(figure);
		}*/
		
		// mettre à jour l'affichage
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
		
		//this.setOutil(new OLier2Elements(this, Color.BLACK, new FLienClassic(new MDLienClassic())));

		edgeTool.install(this);
		
		this.update(this.getGraphics());
	}

	/**
	 * Fixe l'outil courant en tant que OCreerElement
	 */
	public void setOutilCreerElement(FElement e) {
		
		this.setOutil(new OCreerElement(this, new Color(153, 0, 51), e));
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
	 * Récupère l'élément qui a été glissé déposé sur le diagramme
	 * cet élément est obligatoirement un idcomposant
	 * @see java.awt.dnd.DropTargetListener#drop(java.awt.dnd.DropTargetDropEvent)
	 */
	public void drop(DropTargetDropEvent dtde) {

		// récupérer l'objet déplacé
		Transferable transferable = dtde.getTransferable();
		DataFlavor[] flavors = transferable.getTransferDataFlavors();

		// récupérer l'endroit où l'utilisateur à déplacer l'objet
		Point p = dtde.getLocation();

		try {
			// Récupérer l'objet transferré par glissement
			Object obj = transferable.getTransferData(flavors[0]);
			IdObjetModele id = null; // composant à afficher
			// Si c'est un Long, il s'agit en fait de l'id du composant dans le référentiel
			// => Charger le composant
			if (obj instanceof Long) {
				// Récupérer l'id
				long idComp = ((Long) obj).longValue();
				// Ajouter le composant à la DP
				CAjouterComposantDP commande = new CAjouterComposantDP(idComp);
				if (commande.executer()) {
					Application.getApplication().getProjet().setModified(true);
				}
				// Récupérer sa référence et remplir l'Id
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
			// Sinon c'est un vrai id, à récupérer
			else {
				id = (IdObjetModele) obj;
			}
			dtde.dropComplete(true);

			CAjouterComposantGraphe c = new CAjouterComposantGraphe(id, p);
			if (c.executer()) {
				Application.getApplication().getProjet().setModified(true);
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
	public FElement contient(IdObjetModele id) {
		FElement courant;

		for (int i = 0; i < this.elements.size(); i++) {
			courant = (FElement) this.elements.elementAt(i);
			if (courant.getModele().getId() != null) {
				if (courant.getModele().getId().equals(id)) {
					return courant;
				}
			}
		}
		return null;
	}
	
	public ProduitCell contientProduit(IdObjetModele id) {
		
		ProduitCell courant;

		for (int i = 0; i < this.elementCells.size(); i++) {
			courant = (ProduitCell) this.elementCells.elementAt(i);
			if (courant.getMprod().getId() != null) {
				if (courant.getMprod().getId().equals(id)) {
					return courant;
				}
			}
		}
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
		} else {
			return new VertexView(v, this, cm);
		}

	}
	
}
