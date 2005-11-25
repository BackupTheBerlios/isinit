package iepp.ui.iedition.dessin.rendu;

import iepp.Application;

import java.util.Map;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;

public class IeppCell extends DefaultGraphCell {

	public static int TAILLE_TEXTE = 30;
	
	protected String cheminImageComposant = Application.getApplication().getConfigPropriete("dossierImagesIepp");
	public String nomComposantCellule;
	protected Map attributs;
	protected DefaultPort portComposant;
	
	public IeppCell(String nomComp) {
		super(nomComp);
		nomComposantCellule=nomComp;
		portComposant = new DefaultPort();
		attributs=GraphConstants.createMap();
	}

	public void setAttribut(Map map) {
		attributs=map;
	}
	
	public Map getAttributs() {
		return(attributs);
	}
	
	public void setCheminImageComposant(String s) {
		cheminImageComposant=s;
	}
		
	public String getCheminImageComposant() {
		return(cheminImageComposant);
	}
	
	public void setNomCompCell(String s) {
		nomComposantCellule=s;
	}
	
	public String getNomCompCell() {
		return(nomComposantCellule);
	}
	
	public void setProtComp(DefaultPort dp) {
		portComposant=dp;
	}
	
	public DefaultPort getProtComp() {
		return(portComposant);
	}
}
