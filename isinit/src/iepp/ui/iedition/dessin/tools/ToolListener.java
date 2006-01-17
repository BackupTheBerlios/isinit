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

/**
 * Interface for an observer to register to receive notifications of usage of a Tool
 *
 * @version $Revision: 1.3 $
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