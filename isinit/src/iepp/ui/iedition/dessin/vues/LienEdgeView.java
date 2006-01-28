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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;

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
 * @version $Revision: 1.1 $
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
		private PortView mPort = null, mFirstPort = null;
		private Point mStart, mCurrent;
		
		public LienEdgeHandle(EdgeView edge, GraphContext cxt)
		{
			super(edge, cxt);
		}
		
		public void mousePressed(MouseEvent event)
		{
			System.out.println("Edge mousePressed");
			
			//isRemovePointEvent()
			
			int index = indexOfPoint(event.getPoint());
				
			if( index != -1 )
			{
				if( index == 0 )
				{
					mFirstPort = (PortView)getTarget();
					mStart = edge.getPoint(r.length-1);
				}
				else
				{
					mFirstPort = (PortView)getSource();
					mStart = edge.getPoint(0);
				}
				event.consume();			
			}

			super.mousePressed(event);
		}
		
		public void mouseReleased(MouseEvent e)
		{
			System.out.println("Edge mouseReleased");
			if( e!=null && !e.isConsumed() && mPort!=null && mFirstPort!=null && mFirstPort!=mPort)
			{
				graph.clearSelection();
				
				if( mFirstPort != null && mPort != null )
				{
					//((SpemGraphAdapter)graph.getModel()).moveEdge( (DefaultEdge)getCell(),(ApesGraphCell)mPort.getParentView().getCell(), mFirstPort == getTarget() );
					System.out.println("Edge");
				}
				
				graph.repaint();
			}
			else
			{
				graph.repaint();

			}
			
			mFirstPort = mPort = null;
			mStart = mCurrent = null;

			//don't add an undoable change for the mouse release in a diagram
			//Context.getInstance().getUndoManager().save();
			super.mouseReleased(e);
			//Context.getInstance().getUndoManager().restore();
		}
		
		public void mouseDragged(MouseEvent e)
		{
			System.out.println("Edge mouseDragged");
			
			if( mStart!=null && !e.isConsumed())
			{
				//Graphics g = graph.getGraphics();
				
				//paintConnector(Color.black, graph.getBackground(), g);
				
				mPort = getTargetPortAt(e.getPoint());
					
				if(mPort != null)
				{
					mCurrent = graph.toScreen(mPort.getLocation(null));
				}
				else
				{
					mCurrent = graph.snap(e.getPoint());
				}
					
				//paintConnector(graph.getBackground(), Color.black, g);
				
				e.consume();
			}
			super.mouseDragged(e);
		}
		
		public void mouseMoved(MouseEvent e)
		{
			System.out.println("Edge mouseMoved");
			
			
			if( e != null && indexOfPoint(e.getPoint()) != -1 )
			{
				graph.setCursor(new Cursor(Cursor.HAND_CURSOR));
				e.consume();
			}
			else
			{
				super.mouseMoved(e);
			}
		}
		
	
		private PortView getTargetPortAt(Point point)
		{
			Object cell = graph.getFirstCellForLocation(point.x, point.y);
			
			for(int i=0; i<graph.getModel().getChildCount(cell); i++)
			{
				Object tmp = graph.getModel().getChild(cell, i);
				
				tmp = graph.getGraphLayoutCache().getMapping(tmp, false);
				
				if(tmp instanceof PortView)
				{
					PortView portView = (PortView)tmp;
					if( portView.getCell() != ((DefaultEdge)getCell()).getSource() 
							&& portView.getCell() != ((DefaultEdge)getCell()).getTarget())
					{	
						return (PortView)tmp;
					}
				}
			}

			return null;
		}
		
		private int indexOfPoint( Point p )
		{
			int x = p.x;
			int y = p.y;
			// Detect hit on control point
			int index = 0;
			while( index < r.length && !r[index].contains(x, y) ) 
			{
				index++;
			}
			return (index == 0 || index == r.length-1)? index : -1;
		}
	}
}
