package iepp.ui.iedition.dessin.rendu;

import iepp.ui.iedition.dessin.vues.MDProduit;

public class ProduitCellFusion extends ProduitCell {

	protected ComposantCell compParent;
	public ProduitCellFusion(MDProduit mprod , ComposantCell comp) {
		super(mprod);
		setImageComposant("produitLie.png");
		compParent = comp;
	}
	/**
	 * @return Returns the compParent.
	 */
	public ComposantCell getCompParent() {
		return compParent;
	}
	/**
	 * @param compParent The compParent to set.
	 */
	public void setCompParent(ComposantCell compParent) {
		this.compParent = compParent;
	}

}
