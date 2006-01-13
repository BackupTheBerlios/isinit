package iepp.ui.iedition.dessin.tools;

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

import java.util.Vector;

import org.jgraph.JGraph;

/**
 * Base class for tools
 *
 * @version $Revision: 1.3 $
 */
public abstract class Tool
{
	private Vector listeners = new Vector();

	
	/**
	 * Specify is the tool is stable
	 *
	 * @return boolean if it returns false, it should be uninstalled after each use
	 */
	abstract public boolean isStable();
	
	
	/**
	 * Operations needed to install a tool to a graph
	 */
	abstract public void install(JGraph graph);

	
	/**
	 * Operations needed to uninstall a tool from a graph
	 */
	abstract public void uninstall(JGraph graph);

	
	/**
	 * Add an observer to this tool
	 */
	public void addToolListener(ToolListener l)
	{
		listeners.add(l);
	}


	/**
	 * Remove an observer of this tool
	 */
	public void removeToolListener(ToolListener l)
	{
		listeners.remove(l);
	}


	/**
	 * Notify the observers that the tool is started
	 */
	protected void fireToolStarted()
	{
		for(int i=0; i<listeners.size(); i++)
		{
			ToolListener l = (ToolListener)listeners.get(i);
			if(l!=null)
			{
				l.toolStarted(this);
			}
		}
	}


	/**
	 * Notify the observers that the tool has finished its work
	 */
	protected void fireToolFinished()
	{
		for(int i=0; i<listeners.size(); i++)
		{
			ToolListener l = (ToolListener)listeners.get(i);
			if(l!=null)
			{
				l.toolFinished(this);
			}
		}
	}

}
