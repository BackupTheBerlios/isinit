/**
 * 
 */
package iepp.ui.iedition.dessin.rendu;

import java.util.Map;

import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.GraphConstants;

/**
 * @author Stéphane
 *
 */
public class LienEdge extends DefaultEdge {

	protected Map edgeAttribute;
	/**
	 * 
	 */
	public LienEdge() {
		super();
		  
		edgeAttribute = GraphConstants.createMap();
		
		GraphConstants.setLineEnd(edgeAttribute, GraphConstants.ARROW_CLASSIC);
		GraphConstants.setEndFill(edgeAttribute, true);
		GraphConstants.setDisconnectable(edgeAttribute,false);
		GraphConstants.setEditable(edgeAttribute,false);
	}

	/**
	 * @param userObject
	 */
	public LienEdge(Object userObject) {
		super(userObject);
		
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public LienEdge(Object arg0, boolean arg1) {
		super(arg0, arg1);
		
	}

	/**
	 * @return Returns the edgeAttribute.
	 */
	public Map getEdgeAttribute() {
		return edgeAttribute;
	}

	/**
	 * @param edgeAttribute The edgeAttribute to set.
	 */
	public void setEdgeAttribute(Map edgeAttribute) {
		this.edgeAttribute = edgeAttribute;
	}

}
