package iepp.ui.iedition.dessin;

import java.util.Vector;

import org.jgraph.graph.DefaultGraphModel;

public class GraphModelView extends DefaultGraphModel {
	private String nomDiagModel;
	private Vector elementCells;
	
	/**
	 * ComposantCell présents sur le diagramme (Cellule).
	 */
	private Vector composantCellCells;
	
	/**
	 * ProduitCellEntree présents sur le diagramme (Cellule).
	 */
	private Vector produitCellEntreeCells;

	/**
	 * ProduitCellSortie présents sur le diagramme (Cellule).
	 */
	private Vector produitCellSortieCells;
	
	/**
	 * produitCellFusion présents sur le diagramme (Cellule).
	 */
	private Vector produitCellFusionCells;
	
	/**
	 * Liens présents sur le diagramme.
	 */
	private Vector liens;
	
	/**
	 * Notes présents sur le diagramme.
	 */
	private Vector noteCellCells;

	// renvoie le type du modele si true alors statique sinon dynamique
	private boolean type;
	
	public Vector getElementCells() {
		return elementCells;
	}

	public void setElementCells(Vector elementCells) {
		this.elementCells = elementCells;
	}

	public GraphModelView(String nom, boolean typeMod){
		super();
		this.type=typeMod;
		this.nomDiagModel=nom;
		this.elementCells=new Vector();
		this.liens = new Vector();
		this.composantCellCells = new Vector();
		this.produitCellEntreeCells = new Vector();
		this.produitCellSortieCells = new Vector();
		this.produitCellFusionCells = new Vector();
		this.noteCellCells = new Vector();
	}

	public String getNomDiagModel() {
		return nomDiagModel;
	}

	public void setNomDiagModel(String nomDiagModel) {
		this.nomDiagModel = nomDiagModel;
	}

	public boolean getType() {
		return type;
	}

	public void setType(boolean type) {
		this.type = type;
	}

	public Vector getComposantCellCells() {
		return composantCellCells;
	}

	public void setComposantCellCells(Vector composantCellCells) {
		this.composantCellCells = composantCellCells;
	}

	public Vector getLiens() {
		return liens;
	}

	public void setLiens(Vector liens) {
		this.liens = liens;
	}

	public Vector getProduitCellEntreeCells() {
		return produitCellEntreeCells;
	}

	public void setProduitCellEntreeCells(Vector produitCellEntreeCells) {
		this.produitCellEntreeCells = produitCellEntreeCells;
	}

	public Vector getProduitCellFusionCells() {
		return produitCellFusionCells;
	}

	public void setProduitCellFusionCells(Vector produitCellFusionCells) {
		this.produitCellFusionCells = produitCellFusionCells;
	}

	public Vector getProduitCellSortieCells() {
		return produitCellSortieCells;
	}

	public void setProduitCellSortieCells(Vector produitCellSortieCells) {
		this.produitCellSortieCells = produitCellSortieCells;
	}

	public Vector getNoteCellCells() {
		return noteCellCells;
	}

	public void setNoteCellCells(Vector noteCellCells) {
		this.noteCellCells = noteCellCells;
	}

	
}
