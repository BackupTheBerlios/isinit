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
package iepp.ui.iedition.dessin.vues;

import iepp.Application;
import iepp.ui.iedition.VueDPGraphe;
import iepp.ui.iedition.dessin.rendu.liens.LienEdge;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Vector;

import org.jgraph.JGraph;
import org.jgraph.graph.CellHandle;
import org.jgraph.graph.CellMapper;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphContext;
import org.jgraph.graph.PortView;

/**
 * 
 * @version $Revision: 1.2 $
 */
public class LienEdgeView extends EdgeView
{
	public LienEdgeView(Object cell, JGraph graph, CellMapper mapper) 
	{	
		super(cell, graph, mapper);
	}	
	
	public CellHandle getHandle(GraphContext context)
	{
		return new LienEdgeHandle(this, context);
	}

	public class LienEdgeHandle extends EdgeView.EdgeHandle
	{
		
		public LienEdgeHandle(EdgeView edge, GraphContext cxt)
		{
			super(edge, cxt);
		}
		
		public void mousePressed(MouseEvent event)
		{
			super.mousePressed(event);
		}
		
		public void mouseReleased(MouseEvent e)
		{
			LienEdge lien = (LienEdge)this.edge.getCell();
			System.out.println("mouseDragged Liste PointAncrage lien "+lien.getPointAncrage());
			
			lien.supprimerToutPointAncrage();
			
			for(int i = 1;i<this.edge.getPoints().size()-1;i++)	{
				lien.creerPointAncrage(this.edge.getPoint(i));
				System.out.println("mouseReleased Liste point lien ajouter "+this.edge.getPoint(i));
			}
			
			System.out.println("mouseDragged Liste points "+this.edge.getPoints());
			System.out.println("mouseDragged Liste PointAncrage lien "+lien.getPointAncrage());
			System.out.println("");
		
			super.mouseReleased(e);
			
			
		}
		
		public void mouseDragged(MouseEvent e)
		{
			super.mouseDragged(e);
		}
		
		public void mouseMoved(MouseEvent e)
		{
				super.mouseMoved(e);
		}
		

	}
}
