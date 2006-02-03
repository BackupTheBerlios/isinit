package iepp.ui.iedition.dessin.rendu;

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
import iepp.domaine.ComposantProcessus;
import iepp.domaine.IdObjetModele;
import iepp.ui.iedition.dessin.rendu.liens.LienEdge;
import iepp.ui.iedition.dessin.rendu.liens.LigneEdgeDyn;
import iepp.ui.iedition.dessin.vues.LienEdgeView;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Map;
import java.util.Vector;

import javax.swing.ImageIcon;

import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.ParentMap;

/**
 * 
 * 
 * @author Hubert
 *
 */

public class ComposantCellDyn extends DefaultGraphCell {
	
	protected int abscisse;
	protected int ordonnee;
	protected int largeur;
	protected int hauteur;
	protected ComposantProcessus compProc;
	protected ComposantCellElementDyn cell1;
	protected DocumentCellDyn cell2;
	
	protected ParentMap parentMap;
	protected Map attributesFilsMap;
	
	protected Vector lignesEdges;
	protected Vector connectionsSets;
	protected Vector documents;
	protected Vector ports;
	
	public ComposantCellDyn(IdObjetModele comp, int x, int y) { 
		
		super(((ComposantProcessus)comp.getRef()).getNomComposant());

		this.compProc = (ComposantProcessus)comp.getRef();
		
		// On garde dans l'objet un trace de la position du composant sur le graph
		this.abscisse=x;
		this.ordonnee=y;	
		
		// Init Vector
		lignesEdges = new Vector();
		documents = new Vector();
		connectionsSets = new Vector();
		ports = new Vector();
		
		// On créé les deux cellules
		cell1 = new ComposantCellElementDyn(comp,x,y);
		cell2 = new DocumentCellDyn(cell1.getLargeur(),5);
		
		lignesEdges.add(new LigneEdgeDyn(cell1,cell2));
		documents.add(cell2);
		
		DefaultPort port = new DefaultPort();
		cell2.add(port);
		ports.add(port);
		
		ConnectionSet cs = new ConnectionSet((LigneEdgeDyn) lignesEdges.get(0), cell1.getPortComp(), port);
		//connectionsSets = new Vector();
		connectionsSets.add(cs);
		GraphConstants.setBounds(cell2.getAttributes(),new Rectangle(cell1.getAbscisse(),cell1.getOrdonnee()+40+cell1.getHauteur(),cell1.getLargeur(),5));
		
		
		// On créé les deux maps
		parentMap = new ParentMap();
		attributesFilsMap = GraphConstants.createMap();
		
		// On ajoute les attributs des fils dans la map des fils
		attributesFilsMap.put(cell1, cell1.getAttributs());
		attributesFilsMap.put(cell2, cell2.getAttributes());
		attributesFilsMap.put((LigneEdgeDyn) lignesEdges.get(0), ((LigneEdgeDyn) lignesEdges.get(0)).getEdgeAttribute());
		

		// On ajoute les composants a la parent map
		parentMap.addEntry(cell1, this);
		parentMap.addEntry(cell2, this);
		parentMap.addEntry((LigneEdgeDyn) lignesEdges.get(0), this);
		parentMap.addEntry(connectionsSets.get(0),this);
		//parentMap.addEntry(port0, this);
				
		// Définition des attributs du composant
		GraphConstants.setBounds(getAttributes(), new Rectangle((int)abscisse,(int)ordonnee,(int)largeur,(int)hauteur));
		//GraphConstants.setAutoSize(getAttributs(), true);
		GraphConstants.setEditable(getAttributes(), false);
		GraphConstants.setSizeable (getAttributes(), false);
		GraphConstants.setSizeable (cell2.getAttributes(), false);
		GraphConstants.setBendable(getAttributes(), false);
		//GraphConstants.setMoveable(cell1.getAttributes(),false);
		//GraphConstants.setMoveable(cell2.getAttributes(),false);
		GraphConstants.setMoveable(getAttributes(),false);
	}
	
	public Map getAttributesFilsMap() {
		return attributesFilsMap;
	}

	public ParentMap getParentMap() {
		return parentMap;
	}

	public Vector getVectorConnectionsSets() {
		return connectionsSets;
	}

	public Vector getDocuments() {
		return documents;
	}

	public IdObjetModele getId()
    {
    	return this.compProc.getIdComposant();
    }
	


	public IeppCell getDerniereCellule() {
		return (IeppCell) documents.lastElement();
	}
	
	public void incrementerLigneDeVie() {
		cell2 = new DocumentCellDyn();
		cell2.setImageDocument(DocumentCellDyn.refImageProduit);
		lignesEdges.add(new LigneEdgeDyn((DocumentCellDyn) documents.lastElement(),cell2));
		documents.add(cell2);
		
		DefaultPort port = new DefaultPort();
		cell2.add(port);
		
		
		ConnectionSet cs = new ConnectionSet((LigneEdgeDyn) lignesEdges.lastElement(),(DefaultPort) ports.lastElement(), port);
		ports.add(port);
		connectionsSets.add(cs);
		GraphConstants.setBounds(cell2.getAttributs(),new Rectangle(cell1.getAbscisse(),cell1.getOrdonnee()+( 40 * documents.size() )+cell1.getHauteur(),cell1.getLargeur(),5));
		
		attributesFilsMap.put(cell2, cell2.getAttributs());
		attributesFilsMap.put((LigneEdgeDyn) lignesEdges.lastElement(), ((LigneEdgeDyn) lignesEdges.lastElement()).getEdgeAttribute());
		
		parentMap.addEntry(cell2, this);
		parentMap.addEntry((LigneEdgeDyn) lignesEdges.lastElement(), this);
		parentMap.addEntry(cs, this);
		
		Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().getModel().insert(null, attributesFilsMap, null, getParentMap(),null );
		Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().getModel().insert(null, null, (ConnectionSet) cs, null,null );
	}
}
