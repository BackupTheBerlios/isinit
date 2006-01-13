package iepp.ui.iedition.dessin.tools;

import java.util.Vector;

import org.jgraph.JGraph;

/**
 * Base class for tools
 *
 * @version $Revision: 1.2 $
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
