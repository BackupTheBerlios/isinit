/*
 * APES is a Process Engineering Software
 * Copyright (C) 2003-2004 IPSquad
 * team@ipsquad.tuxfamily.org
 *
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
 */

package iepp.ui.iedition.dessin.tools;

import iepp.Application;
import iepp.ui.iedition.dessin.rendu.IeppCell;
import iepp.ui.iedition.dessin.rendu.LienEdge;
import iepp.ui.iedition.dessin.rendu.ProduitCell;
import iepp.ui.iedition.dessin.rendu.ProduitCellEntree;
import iepp.ui.iedition.dessin.rendu.ProduitCellSortie;
import iepp.ui.iedition.dessin.rendu.TextCell;
import iepp.ui.iedition.dessin.vues.MDProduit;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import org.jgraph.JGraph;
import org.jgraph.graph.BasicMarqueeHandler;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.Port;
import org.jgraph.graph.PortView;

/**
 * This tool allows to create edges in the graph It use the prototype design
 * pattern to clone edges
 * 
 * @version $Revision: 1.1 $
 */
public class EdgeTool extends Tool {
	protected JGraph mGraph;

	protected DefaultEdge mPrototype;

	protected EdgeHandler mHandler = new EdgeHandler();

	protected boolean mStable = true;

	/**
	 * Build a new EdgeTool
	 * 
	 * @param prototype
	 *            the edge to clone
	 */
	public EdgeTool(DefaultEdge prototype) {
		mPrototype = prototype;
	}

	public void install(JGraph graph) {
		mGraph = graph;
		graph.setMarqueeHandler(mHandler);
		graph.setMoveable(false);
		graph.setSizeable(false);
		graph.setPortsVisible(true);
	}

	public void uninstall(JGraph graph) {
		mGraph = null;
		graph.setMoveable(true);
		graph.setSizeable(true);
		graph.setPortsVisible(false);
	}

	public boolean isStable() {
		boolean oldStable = mStable;
		if (!mStable) {
			mStable = true;
		}

		return oldStable;
	}

	protected class EdgeHandler extends BasicMarqueeHandler {
		protected PortView mPort, mFirstPort;

		protected Point mStart, mCurrent;

		public boolean isForceMarqueeEvent(MouseEvent e) {
			mPort = getSourcePortAt(e.getPoint());
			if (mPort != null && mGraph.isPortsVisible())
				return true;
			return false;
		}

		public void mousePressed(MouseEvent e) {
			if (mPort != null && !e.isConsumed() && mGraph.isPortsVisible()) {
				fireToolStarted();
				mStart = mGraph.toScreen(mPort.getLocation(null));
				mFirstPort = mPort;
				e.consume();
			}
		}

		public void mouseReleased(MouseEvent e) {
			if (e != null && !e.isConsumed() && mPort != null
					&& mFirstPort != null && mFirstPort != mPort) {

				mGraph.clearSelection();
				/*
				 * ConnectionSet cs = new ConnectionSet(); Port firstPort =
				 * (Port) mFirstPort.getCell(); Port port = (Port)
				 * mPort.getCell();
				 * 
				 * LienEdge edge = null; if(mFirstPort.getParentView().getCell()
				 * instanceof TextCell || mPort.getParentView().getCell()
				 * instanceof TextCell) { edge = new LienEdge(); } else { edge =
				 * new LienEdge();//(DefaultEdge) mPrototype.clone();
				 * edge.setSource(firstPort); edge.setTarget(port); }
				 * 
				 * cs.connect(edge, firstPort, port);
				 * 
				 * Map attr = edge.getEdgeAttribute();
				 * 
				 * Map attributes = new Hashtable(); attributes.put(edge, attr);
				 * 
				 * createEdge(edge, cs, attributes);
				 * 
				 * e.consume();
				 * 
				 */

				Object cellSrc = mFirstPort.getParentView().getCell();
				Object cellDes = mPort.getParentView().getCell();

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

					// On essaie de relier un produit en entree et en sortie
					// d'un meme composant
					if (((ProduitCellEntree) cellEnt).getCompParent().equals(
							((ProduitCellSortie) cellSor).getCompParent())) {
						return;
					}

					LienEdge edge1 = new LienEdge();
					LienEdge edge2 = new LienEdge();

					MDProduit mdp1 = ((ProduitCell) cellSrc).getMprod();
					MDProduit mdp2 = ((ProduitCell) cellDes).getMprod();

					mdp1.setX((mdp1.getX() + mdp2.getX()) / 2);
					mdp1.setY((mdp1.getY() + mdp2.getY()) / 2);
					ProduitCell newProdCell = new ProduitCell(mdp1);

					if (!((ProduitCell) cellSrc).getNomCompCell()
							.equalsIgnoreCase(
									((ProduitCell) cellDes).getNomCompCell())) {
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

					mGraph.getModel().insert(vecObj.toArray(), AllAttribute,
							null, null, null);
					mGraph.getModel().insert(null, null, cs1, null, null);
					mGraph.getModel().insert(null, null, cs2, null, null);

					vecObj.clear();

					for (int i = 0; i < ((ProduitCell) cellSrc).getListeLien()
							.size(); i++)
						vecObj.add(((ProduitCell) cellSrc).getListeLien()
								.get(i));

					for (int i = 0; i < ((ProduitCell) cellDes).getListeLien()
							.size(); i++)
						vecObj.add(((ProduitCell) cellDes).getListeLien()
								.get(i));

					vecObj.add(((ProduitCell) cellSrc).getPortComp());
					vecObj.add(((ProduitCell) cellDes).getPortComp());
					vecObj.add(cellSrc);
					vecObj.add(cellDes);

					mGraph.getModel().remove(vecObj.toArray());

					e.consume();

				} else {
					// System.out.println("SOURCE & DESTINATION identiques");
				}

				e.consume();
				mGraph.repaint();


			} else {
				mStable = false;
			}

			mFirstPort = mPort = null;
			mStart = mCurrent = null;

			fireToolFinished();
		}

		protected void createEdge(DefaultEdge edge, ConnectionSet cs,
				Map attributes) {
			mGraph.getModel().insert(new Object[] { edge }, attributes, cs,
					null, null);
		}

		public void mouseDragged(MouseEvent e) {
			if (mStart != null && !e.isConsumed()) {
				Graphics g = mGraph.getGraphics();

				paintConnector(Color.black, mGraph.getBackground(), g);

				mPort = getTargetPortAt(e.getPoint());

				if (mPort != null) {
					mCurrent = mGraph.toScreen(mPort.getLocation(null));
				} else {
					mCurrent = mGraph.snap(e.getPoint());
				}

				paintConnector(mGraph.getBackground(), Color.black, g);

				e.consume();
			}

		}

		public void mouseMoved(MouseEvent e) {
			if (e != null && getSourcePortAt(e.getPoint()) != null
					&& !e.isConsumed() && mGraph.isPortsVisible()) {
				mGraph.setCursor(new Cursor(Cursor.HAND_CURSOR));
				e.consume();
			}
		}

		private PortView getSourcePortAt(Point point) {
			if (point == null || mGraph == null) {
				return null;
			}

			Point tmp = mGraph.fromScreen(new Point(point));

			return (PortView) mGraph.getPortViewAt(tmp.x, tmp.y);
		}

		private PortView getTargetPortAt(Point point) {
			Object cell = mGraph.getFirstCellForLocation(point.x, point.y);

			for (int i = 0; i < mGraph.getModel().getChildCount(cell); i++) {
				Object tmp = mGraph.getModel().getChild(cell, i);

				tmp = mGraph.getGraphLayoutCache().getMapping(tmp, false);

				if (tmp instanceof PortView && tmp != mFirstPort) {
					return (PortView) tmp;
				}
			}

			return getSourcePortAt(point);
		}

		private void paintConnector(Color fg, Color bg, Graphics g) {
			g.setColor(fg);
			g.setXORMode(bg);
			paintPort(mGraph.getGraphics());

			if (mFirstPort != null && mStart != null && mCurrent != null) {
				g.drawLine(mStart.x, mStart.y, mCurrent.x, mCurrent.y);
			}
		}

		private void paintPort(Graphics g) {
			if (mPort != null) {
				boolean o = (GraphConstants.getOffset(mPort.getAttributes()) != null);
				Rectangle r = (o) ? mPort.getBounds() : mPort.getParentView()
						.getBounds();
				r = mGraph.toScreen(new Rectangle(r));
				r.setBounds(r.x - 3, r.y - 3, r.width + 6, r.height + 6);
				mGraph.getUI().paintCell(g, mPort, r, true);
			}
		}
	}
}
