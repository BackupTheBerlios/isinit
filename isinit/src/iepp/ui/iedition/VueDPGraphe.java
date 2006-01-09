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
import iepp.application.aedition.aoutil.OLier2Elements;
import iepp.application.aedition.aoutil.OSelection;
import iepp.application.aedition.aoutil.Outil;
import iepp.application.areferentiel.Referentiel;
import iepp.domaine.ComposantProcessus;
import iepp.domaine.DefinitionProcessus;
import iepp.domaine.IdObjetModele;
import iepp.ui.iedition.dessin.rendu.ComposantCell;
import iepp.ui.iedition.dessin.rendu.FComposantProcessus;
import iepp.ui.iedition.dessin.rendu.FElement;
import iepp.ui.iedition.dessin.rendu.Figure;
import iepp.ui.iedition.dessin.rendu.IeppCell;
import iepp.ui.iedition.dessin.rendu.LienEdge;
import iepp.ui.iedition.dessin.rendu.ProduitCell;
import iepp.ui.iedition.dessin.rendu.ProduitCellEntree;
import iepp.ui.iedition.dessin.rendu.ProduitCellSortie;
import iepp.ui.iedition.dessin.rendu.TextCell;
import iepp.ui.iedition.dessin.rendu.handle.Handle;
import iepp.ui.iedition.dessin.rendu.liens.FLien;
import iepp.ui.iedition.dessin.rendu.liens.FLienClassic;
import iepp.ui.iedition.dessin.vues.ComposantView;
import iepp.ui.iedition.dessin.vues.MDComposantProcessus;
import iepp.ui.iedition.dessin.vues.MDDiagramme;
import iepp.ui.iedition.dessin.vues.MDElement;
import iepp.ui.iedition.dessin.vues.MDLienClassic;
import iepp.ui.iedition.dessin.vues.MDNote;
import iepp.ui.iedition.dessin.vues.MDProduit;
import iepp.ui.iedition.dessin.vues.ProduitView;
import iepp.ui.iedition.dessin.vues.TextView;
import iepp.ui.iedition.popup.PopupDiagramme;
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
import org.jgraph.graph.DefaultEdge;
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
	 * Eléments présents sur le diagramme.
	 */
	private Vector elements;

	/**
	 * Eléments présents sur le diagramme.
	 */
	private Vector listElements;
	
	/**
	 * Liens présents sur le diagramme.
	 */
	private Vector liens;

	/**
	 * Figures sélectionnés.
	 */
	private Vector selection;

	/**
	 * Dimension de la zone à afficher
	 * sert à indiquer la taille setPreferedSize() du diagramme
	*/
	private Dimension zone_affichage;
	
	
	/**
	 * Memorise la premiere cellule cliquee
	 */
	private MouseEvent firstMouseEvent;

	/**
	 * Gestion des boutons pour les cliques de souris (Lier)
	 */
	private boolean boutonLierActif;

	/**
	 * Gestion des boutons pour les cliques de souris (Note)
	 */
	private boolean boutonNoteActif;
	private TextCell note;
	
	
	/**
	 * Memorise le lieu de départ d'un élément qu'on déplace dans la fenêtre JGraph 
	 */
	private MouseEvent mouseDelta;
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
		this.listElements = new Vector();

		// par défault, on utilise l'outil de sélection
		this.diagramTool = new OSelection(this);

		// ajouter les controleurs
		this.addMouseListener(this);
		this.addMouseMotionListener(this);

		
		boutonLierActif = false;
		boutonNoteActif = false;
		
		this.zone_affichage = this.getSize();
		//this.setPreferredSize(this.zone_affichage);
		this.setAutoscrolls(true);

		// on met la couleur par défaut au diagramme
		modele
				.setFillColor(new Color(Integer.parseInt(Application
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
	 * Repeind le diagramme.
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		this.calculerDimension();
		
		/*
		//A regarder
		//this.updateAutoSize();

		
		 // Couleur de remplissage
		 //g.setColor(getModele().getFillColor());
		 //g.fillRect(0,0,getWidth(),getHeight());

		 // Dessine les liens
		 System.out.println("Début lien");
		 for (int i = 0; i < this.liens.size(); i++)
		 {
			 System.out.println(this.liens.elementAt(i).toString());
		   //((FLien) (this.liens.elementAt(i))).paintComponent(g);
		 } 
		 System.out.println("Fin lien et début elem");
		 // Dessine les éléments
		 for (int i = 0; i < this.elements.size(); i++)
		 {
			 System.out.println(this.elements.elementAt(i).toString());
			 
			 
			 if (i==0){
				 ComposantCell composantCell = new ComposantCell((MDComposantProcessus)(((FElement)(this.elements.elementAt(i))).getModele()));
				 System.out.println(composantCell.toString());
				 getModel().insert( new Object[]{composantCell}, composantCell.getAttributes(), null, null,null );
			 }
		 }
		 System.out.println("Fin elem");
		 
		 // Dessine les poignées (handles)
		 for (int i = 0; i < this.selection.size(); i++)
		 {
		 ((Figure) (this.selection.elementAt(i))).displayHandles(g);
		 } 
		 
		 // Permet de dessiner la représentation de l'outil (ex : cadre de sélection)
		 if (this.diagramTool != null)
		 {
		 //this.diagramTool.draw(g);
		 } 
		 */
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

	public void addElements(Vector l) {
		this.listElements.add(l);
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

		// ajouter la figure au modèle de dessin		
		this.getModele().ajouterModeleFigure(f.getModele());
		// mettre à jour l'affichage
		this.repaint();
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
	}

	/**
	 * Efface complètement le diagramme
	 */
	public void effacerDiagramme() {
		//this.selection.removeAllElements();
		//this.liens.removeAllElements();
		//this.elements.removeAllElements();
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
		// mettre à jour l'affichage
		this.repaint();
	}

	/**
	 * Calculer la zone à afficher pour permettre le scrolling et l'encodage des images
	 * rectangle de la zone à afficher
	 */
	public void calculerDimension() {

		/*
		// récupérer les coordonnées de la figure la plus en bas à droit possible
		int x = 0;
		int y = 0;

		// récupérer les coordonnées de la figure la plus en haut à gauche
		int x2 = this.getWidth();
		int y2 = this.getHeight();

		Enumeration e = this.elements.elements();

		while (e.hasMoreElements()) {

			FElement figure = (FElement) e.nextElement();
			MDElement vue = (MDElement) figure.getModele();
			
			//System.out.println("(" + vue.getX() + " " + vue.getY());
			if (vue.getX() + vue.getLargeur() > x) {
				x = vue.getX() + vue.getLargeur();
				
				if (figure.getFinChaine() > x) {
					x = figure.getFinChaine();
				}
				
			}
			if (vue.getY() + vue.getHauteur() > y) {
				y = vue.getY() + vue.getHauteur();
			}
			
			if (vue.getX() < x2) {
				x2 = vue.getX();
				if (figure.getDebutChaine() > 0 && figure.getDebutChaine() < x2) {
					x2 = figure.getDebutChaine();
				}
			}
			if (vue.getY() < y2) {
				y2 = vue.getY();
			}
			
		}

		// récupère le facteur de zoom
		double zoom = this.getModele().getFacteurZoom();
		// on ajoute 45 à x et à y pour que l'image ait une taille correcte
		this.zone_affichage.setSize(zoom * (x + 45), zoom*(y +45));
		this.setPreferredSize(this.zone_affichage);
		this.revalidate();	
		*/
	}

	
	public Dimension getZoneAffichage() {
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
		boutonLierActif = false;

		this.setOutil(new OSelection(this));

		this.setMoveable(true);

		/*			
		 this.setSelectionCells(new Object[]{});

		 for(int i=0;i<this.getModel().getRootCount();i++){
		 if(this.getModel().getRootAt(i) instanceof IeppCell){
		 IeppCell cell = (IeppCell)this.getModel().getRootAt(i);
		 System.out.println((IeppCell)this.getModel().getRootAt(i));
		 
		 GraphConstants.setMoveable(cell.getAttributes(),true);
		 }
		 }
		 
		 // pour la prise en compte dans le graph
		 * 
		 */
		this.update(this.getGraphics());
	}

	/**
	 * Fixe l'outil courant en tant que OLier2Element.
	 */
	public void setOutilLier() {
		boutonLierActif = true;

		this.setOutil(new OLier2Elements(this, Color.BLACK, new FLienClassic(
				new MDLienClassic())));

		this.setMoveable(false);
		/*

		 System.out.println(this.getModel().getRootCount());
		 
		 
		 this.setSelectionCells(new Object[]{});
		 
		 for(int i=0;i<this.getModel().getRootCount();i++){
		 if(this.getModel().getRootAt(i) instanceof IeppCell){
		 IeppCell cell = (IeppCell)this.getModel().getRootAt(i);
		 System.out.println("Lier:"+(IeppCell)this.getModel().getRootAt(i));
		 
		 GraphConstants.setMoveable(cell.getAttributes(),false);
		 }
		 }
		 
		 // pour la prise en compte dans le graph
		 */
		this.update(this.getGraphics());
	}

	/**
	 * Fixe l'outil courant en tant que OCreerElement
	 */
	public void setOutilCreerElement(FElement e) {
		//this.setOutil(new OCreerElement(this, new Color(153, 0, 51), e));
		boutonNoteActif = true;
		note = new TextCell((MDNote)e.getModele());
	}

	//---------------------------------------------------------------------
	//    Gestion des actions sur le diagramme
	//---------------------------------------------------------------------

	public void mouseClicked(MouseEvent e) {
		//this.diagramTool.mouseClicked(e);
	}
	
	
	public void mousePressed(MouseEvent e) {
		// Enregistre l'évênement utilisée pour la mise en place de la scroll
		// barre
		mouseDelta = e;

		if (boutonNoteActif == true) {

			Map NoteAttribute = GraphConstants.createMap();
			note.setAbscisse(e.getX());
			note.setOrdonnee(e.getY());
			NoteAttribute.put(note, note.getAttributs());

			this.getModel().insert(new Object[] { note }, NoteAttribute, null,
					null, null);

			boutonNoteActif = false;

			// reprendre l'outil de séléction
			Application.getApplication().getProjet().getFenetreEdition()
					.setOutilSelection();
		} else {

			if (this.getFirstCellForLocation(e.getX(), e.getY()) instanceof IeppCell) {
				IeppCell ic = (IeppCell) this.getFirstCellForLocation(e.getX(),
						e.getY());

			} else {
				if (this.getFirstCellForLocation(e.getX(), e.getY()) instanceof IeppCell) {
					IeppCell ic = (IeppCell) this.getFirstCellForLocation(e
							.getX(), e.getY());

					if (boutonLierActif == true) {
						GraphConstants.setMoveable(ic.getAttributes(), false);
						this.repaint();

						if (this.getFirstCellForLocation(e.getX(), e.getY()) != null) {
							firstMouseEvent = e;

						} else {

							firstMouseEvent = null;
						}
					} else {
						GraphConstants.setMoveable(ic.getAttributes(), true);
						this.repaint();
						firstMouseEvent = null;
					}

				} else {
					firstMouseEvent = null;

					// Hubert : menu contextuel
					// le test du bouton droit de la souris n'est pas le mm que
					// dans la version 2xmi
					// pas de isTriggerPopup() ...
					/*
					 * if(e.getButton()==MouseEvent.BUTTON3) {
					 * this.showPopupMenuDiagramme(e.getX(),e.getY()); }
					 */

				}
			}
		}
	}


	

	public void mouseReleased(MouseEvent e) {
		//Hubert : popup menu sur le graph (hors cellules)
		if (!(this.getFirstCellForLocation(e.getX(), e.getY()) instanceof IeppCell))
		{
			if (e.isPopupTrigger())
          	{
				showPopupMenuDiagramme(e.getX(), e.getY());
         	}
		}
		// Julie ( A revoir) Met à jour la liste des figures sélectionnées
		this.clearSelection();
		
		Object tab[] = this.getSelectionCells();
		for (int k=0; k< this.getSelectionCount(); k++) {
			if (tab[k] instanceof IeppCell) {
				IeppCell cell = (IeppCell)tab[k];
				if (cell instanceof ComposantCell) {
					MDElement vue = ((ComposantCell)cell).getMdcomp();
					this.selectionneFigure((chercherFigure(vue.getX(), vue.getY())));
				} else if ((cell instanceof ProduitCellEntree) || (cell instanceof ProduitCellSortie)){
					MDElement vue = ((ProduitCell)cell).getMprod();
					this.selectionneFigure((chercherFigure(vue.getX(), vue.getY())));
	
				} 
			}
		}
	
		// récupération du facteur de zoom
		double zoom = this.getModele().getFacteurZoom();
		//Mise a jour des élements déplacés pour la scrollbar
		tab = this.getSelectionCells();
		//Pour chaque cellule, on met a jour la taille de la fenetre JGraph
		for (int k=0; k< this.getSelectionCount(); k++) {
			if (tab[k] instanceof IeppCell) {
				IeppCell cell = (IeppCell)tab[k];
				if (cell instanceof ComposantCell) {
					MDElement vue = ((ComposantCell)cell).getMdcomp();
					vue.setX(vue.getX() + (int)((e.getX() - mouseDelta.getX())/zoom));
					vue.setY(vue.getY() + (int)((e.getY() - mouseDelta.getY())/zoom));
	
				} else if ((cell instanceof ProduitCellEntree) || (cell instanceof ProduitCellSortie)){
					MDElement vue = ((ProduitCell)cell).getMprod();
					vue.setX(vue.getX() + (int)((e.getX() - mouseDelta.getX())/zoom));
					vue.setY(vue.getY() + (int)((e.getY() - mouseDelta.getY())/zoom));
	
				} else {
					//System.out.println("erreur " + cell.getClass().getName());
				}
			}
		}

		
		// Gestion des liens
		if (boutonLierActif == true) {

			// verifier ke la ou l'on a relacher la souris , il y a un produit

			if (firstMouseEvent != null) {

				Object cellSrc = this.getFirstCellForLocation(firstMouseEvent
						.getX(), firstMouseEvent.getY());
				Object cellDes = this.getFirstCellForLocation(e.getX(), e
						.getY());

				Object cellEnt = null;
				Object cellSor = null;

				if (((cellSrc instanceof ProduitCellEntree) && (cellDes instanceof ProduitCellSortie))
						|| (cellSrc instanceof ProduitCellSortie)
						&& (cellDes instanceof ProduitCellEntree)) {
					// verif ke les 2 soit un produit de type differents

					if (cellDes instanceof ProduitCellEntree) {
						cellEnt = cellDes;
						cellSor = cellSrc;
					} else {
						cellEnt = cellSrc;
						cellSor = cellDes;
					}
					
					// On essaie de relier un produit en entree et en sortie d'un meme composant
					if(((ProduitCellEntree)cellEnt).getCompParent().equals(((ProduitCellSortie)cellSor).getCompParent())){
						return;
					}

					LienEdge edge1 = new LienEdge();
					LienEdge edge2 = new LienEdge();

					MDProduit mdp1 = ((ProduitCell) cellSrc).getMprod();
					MDProduit mdp2 = ((ProduitCell) cellDes).getMprod();
					
					mdp1.setX((mdp1.getX()+mdp2.getX())/2);
					mdp1.setY((mdp1.getY()+mdp2.getY())/2);
					ProduitCell newProdCell = new ProduitCell(mdp1);
					
					if(!((ProduitCell)cellSrc).getNomCompCell().equalsIgnoreCase(((ProduitCell) cellDes).getNomCompCell())){
					newProdCell.setNomCompCell(((ProduitCell) cellSrc)
							.getNomCompCell()
							+ "("
							+ ((ProduitCell) cellDes).getNomCompCell()
							+ ")");
					}
					
					
					newProdCell.setImageComposant("produitLie.png");

					Map AllAttribute = GraphConstants.createMap();

					AllAttribute.put(edge1, edge1.getEdgeAttribute());
					AllAttribute.put(edge2, edge2.getEdgeAttribute());
					AllAttribute.put(newProdCell, newProdCell.getAttributs());

					DefaultPort portS = ((ProduitCellSortie) cellSor)
							.getCompParent().getPortComp();
					DefaultPort portDInt = ((ProduitCell) newProdCell)
							.getPortComp();
					DefaultPort portD = ((ProduitCellEntree) cellEnt)
							.getCompParent().getPortComp();

					ConnectionSet cs1 = new ConnectionSet(edge1, portS,
							portDInt);
					ConnectionSet cs2 = new ConnectionSet(edge2, portDInt,
							portD);

					Vector vecObj = new Vector();
					vecObj.add(newProdCell);
					vecObj.add(edge1);
					vecObj.add(edge2);

					this.getModel().insert(vecObj.toArray(), AllAttribute,
							null, null, null);
					this.getModel().insert(null, null, cs1, null, null);
					this.getModel().insert(null, null, cs2, null, null);

					vecObj.clear();
					
					/*if(listElements.contains(cellSrc)){
						IeppCell temp = (IeppCell)elements.elementAt(elements.indexOf(cellSrc));
						System.out.println(temp);
						System.out.println(temp.getListeLien());
						vecObj.add(temp.getListeLien());
					}
					if(listElements.contains(cellDes)){
						IeppCell temp = (IeppCell)elements.elementAt(elements.indexOf(cellSrc));
						System.out.println(temp);
						System.out.println(temp.getListeLien());
						vecObj.add(temp.getListeLien());
					}*/
					
					for(int i = 0;i<((ProduitCell) cellSrc).getListeLien().size();i++)
						vecObj.add(((ProduitCell) cellSrc).getListeLien().get(i));
					
					for(int i = 0;i<((ProduitCell) cellDes).getListeLien().size();i++)
						vecObj.add(((ProduitCell) cellDes).getListeLien().get(i));
						
					vecObj.add(((ProduitCell) cellSrc).getPortComp());
					vecObj.add(((ProduitCell) cellDes).getPortComp());
					vecObj.add(cellSrc);
					vecObj.add(cellDes);

					this.getModel().remove(vecObj.toArray());
					this.repaint();

				} else {
					//System.out.println("SOURCE & DESTINATION identiques");
				}

			}
		}
		firstMouseEvent = null;
	}

	public void mouseEntered(MouseEvent e) {
		//this.diagramTool.mouseEntered(e);
	}

	public void mouseExited(MouseEvent e) {
		//this.diagramTool.mouseExited(e);
	}

	//private MouseEvent mouseDelta;
	
	public void mouseDragged(MouseEvent e) {
		//this.diagramTool.mouseDragged(e);
		
		/*
		if (mouseDelta == null)
			mouseDelta = e;
		*/
		/*
		for (int i = 0; i < this.selection.size(); i++){

			FElement figure = ((FElement) (this.selection.elementAt(i)));
			MDElement vue = (MDElement) figure.getModele();
						
			vue.setX(vue.getX() + e.getX() - mouseDelta.getX());
			vue.setY(vue.getY() + e.getY() - mouseDelta.getY());

		} 
		mouseDelta = e;
		*/
		
		/*
		Object tab[] = this.getSelectionCells();
		for (int k=0; k< this.getSelectionCount(); k++) {
			IeppCell cell = (IeppCell)tab[k];
			if (cell instanceof ComposantCell) {
				MDElement vue = ((ComposantCell)cell).getMdcomp();
				vue.setX(vue.getX() + e.getX() - mouseDelta.getX());
				vue.setY(vue.getY() + e.getY() - mouseDelta.getY());

			} else if ((cell instanceof ProduitCellEntree) || (cell instanceof ProduitCellSortie)){
				MDElement vue = ((ProduitCell)cell).getMprod();
				vue.setX(vue.getX() + e.getX() - mouseDelta.getX());
				vue.setY(vue.getY() + e.getY() - mouseDelta.getY());

			} else {
				//System.out.println("erreur " + cell.getClass().getName());
			}
			

		}
		mouseDelta = e;
		*/
		
		/*
		if (this.getFirstCellForLocation(e.getX(), e.getY()) instanceof IeppCell) {
			IeppCell ic = (IeppCell) this.getFirstCellForLocation(e.getX(), e
					.getY());
		
				GraphConstants.setHorizontalAlignment(ic.getAttributes(), 10);
				this.repaint();
		}
		*/
		/*
		if (cell != null) {
		if (cell instanceof ComposantCell) {
			((ComposantCell)cell).setAbscisse(e.getX());
			((ComposantCell)cell).setOrdonnee(e.getY());
		} else if ((cell instanceof ProduitCellEntree) || (cell instanceof ProduitCellSortie)){
			((ProduitCell)cell).setAbscisse(e.getX());
			((ProduitCell)cell).setOrdonnee(e.getY());

		} else {
			System.out.println("erreur " + cell.getClass().getName());
		}
		}
		*/
		//FElement figure = (FElement)elements.elementAt(elements.indexOf(cellpp));
		//MDElement vue = (MDElement) figure.getModele();
		//vue.setX(e.getX());
		//vue.setY(e.getY());
	}

	public void mouseMoved(MouseEvent e) {
		//this.diagramTool.mouseMoved(e);
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

	/** 
	 * @see JGraph#createVertexView(java.lang.Object, org.jgraph.graph.CellMapper)
	 */
	protected VertexView createVertexView(Object v, CellMapper cm) {

		// Return the appropriate view
		if (v instanceof ComposantCell) {
			return new ComposantView(v, this, cm);
		} else if (v instanceof ProduitCell) {
			return new ProduitView(v, this, cm);
		} else if (v instanceof TextCell) {
			return new TextView(v, this, cm);
		} else {
			return new VertexView(v, this, cm);
		}

	}
	
	/**
	 * Affiche le menu popup (contextuel) pour un diagramme.
	 */
	// Hubert : ajout fonction pour afficher le menu contextuel
	// la position de la fenetre par rapport au graph
	protected void showPopupMenuDiagramme(int x,int y)
	{
	    PopupDiagramme p = new PopupDiagramme(this, x, y);
	    p.show(this,x,y);
	}
}
