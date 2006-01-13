package iepp.ui.iedition.dessin.tools;

/**
 * Interface for an observer to register to receive notifications of usage of a Tool
 *
 * @version $Revision: 1.2 $
 */
public interface ToolListener
{
	/**
	 * Gives notification that a tool has been started
	 */
	public void toolStarted(Tool tool);
	
	/**
	 * Gives notification that a tool has finished it editing work
	 */
	public void toolFinished(Tool tool);
}
