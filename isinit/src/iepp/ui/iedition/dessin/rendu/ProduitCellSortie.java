package iepp.ui.iedition.dessin.rendu;

import iepp.ui.iedition.dessin.vues.MDProduit;

public class ProduitCellSortie extends ProduitCell {

	protected ComposantCell compParent;
	
	public ProduitCellSortie(MDProduit mprod , ComposantCell comp) {
		super(mprod);

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
