package iepp.ui.iedition.dessin.vues;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.EventObject;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.jgraph.JGraph;
import org.jgraph.graph.CellMapper;
import org.jgraph.graph.CellView;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.DefaultGraphCellEditor;
import org.jgraph.graph.GraphCellEditor;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.VertexView;

public class TextView extends VertexView {

	//Constructor for Superclass
	public TextView(Object cell, JGraph jg, CellMapper cm) {
		super(cell, jg, cm);
	}
	
	protected static transient MultiLinedEditor editor = new MultiLinedEditor();
	protected static transient MultiLinedRenderer renderer = new MultiLinedRenderer();
    
    public GraphCellEditor getEditor() {
        return editor;
    }
    
    public static class MultiLinedEditor extends DefaultGraphCellEditor {
        public class RealCellEditor extends AbstractCellEditor implements GraphCellEditor {
            JTextArea editorComponent = new JTextArea();
            public RealCellEditor() {
                editorComponent.setBorder(UIManager.getBorder("Tree.editorBorder"));
                editorComponent.setLineWrap(true);
                editorComponent.setWrapStyleWord(true);

                //substitute a JTextArea's VK_ENTER action with our own that will stop an edit.
				editorComponent.getInputMap(JComponent.WHEN_FOCUSED).put(
						KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter");
				editorComponent.getInputMap(JComponent.WHEN_FOCUSED).put(
						KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,
								KeyEvent.SHIFT_DOWN_MASK), "shiftEnter");
				editorComponent.getInputMap(JComponent.WHEN_FOCUSED).put(
						KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,
								KeyEvent.CTRL_DOWN_MASK), "metaEnter");
                editorComponent.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter");
                editorComponent.getActionMap().put("enter", new AbstractAction(){
                    public void actionPerformed(ActionEvent e) {
                        stopCellEditing();
                    }
                });
                AbstractAction newLineAction = new AbstractAction() {

					public void actionPerformed(ActionEvent e) {
						Document doc = editorComponent.getDocument();
						try {
							doc.insertString(editorComponent
									.getCaretPosition(), "\n", null);
						} catch (BadLocationException e1) {
							e1.printStackTrace();
						}
					}
                };
				editorComponent.getActionMap().put("shiftEnter",
						newLineAction);
				editorComponent.getActionMap().put("metaEnter",
						newLineAction);
            }

            
            
            public Component getGraphCellEditorComponent(
                    JGraph graph,
                    Object value,
                    boolean isSelected) {
                editorComponent.setText(value.toString());
                editorComponent.selectAll();
                return editorComponent;
            }

            public Object getCellEditorValue() {
                return editorComponent.getText();
            }

            public boolean stopCellEditing() {
                //set the size of a vertex to that of an editor.
                CellView view = graph.getGraphLayoutCache().getMapping(graph.getEditingCell(), false);
                Map map = view.getAllAttributes();
                Rectangle2D cellBounds = GraphConstants.getBounds(map);
                Rectangle editingBounds = editorComponent.getBounds();
                GraphConstants.setBounds(map, new Rectangle((int) cellBounds.getX(), (int) cellBounds.getY(), editingBounds.width, editingBounds.height));

                return super.stopCellEditing();
            }

            public boolean shouldSelectCell(EventObject event) {
				editorComponent.requestFocus();
				return super.shouldSelectCell(event);
			}
        }

        public MultiLinedEditor() {
            super();
        }
        /**
         * Overriding this in order to set the size of an editor to that of an edited view.
         */
        public Component getGraphCellEditorComponent(
                JGraph graph,
                Object cell,
                boolean isSelected) {

            Component component = super.getGraphCellEditorComponent(graph, cell, isSelected);

            //set the size of an editor to that of a view
			CellView view = graph.getGraphLayoutCache().getMapping(cell, false);
			Rectangle2D tmp = view.getBounds();
			editingComponent.setBounds((int) tmp.getX(), (int) tmp.getY(),
					(int) tmp.getWidth(), (int) tmp.getHeight());

            //I have to set a font here instead of in the RealCellEditor.getGraphCellEditorComponent() because
            //I don't know what cell is being edited when in the RealCellEditor.getGraphCellEditorComponent().
            Font font = GraphConstants.getFont(view.getAllAttributes());
            editingComponent.setFont((font != null) ? font : graph.getFont());

            return component;
        }

        protected GraphCellEditor createGraphCellEditor() {
            return new MultiLinedEditor.RealCellEditor();
        }

        /**
         * Overriting this so that I could modify an eiditor container.
         * see http://sourceforge.net/forum/forum.php?thread_id=781479&forum_id=140880
         */
        protected Container createContainer() {
            return new MultiLinedEditor.ModifiedEditorContainer();
        }

        class ModifiedEditorContainer extends EditorContainer {
            public void doLayout() {
                super.doLayout();
                //substract 2 pixels that were added to the preferred size of the container for the border.
                Dimension cSize = getSize();
                Dimension dim = editingComponent.getSize();
                editingComponent.setSize(dim.width - 2, dim.height);

                //reset container's size based on a potentially new preferred size of a real editor.
                setSize(cSize.width, getPreferredSize().height);
            }
        }
    }

	//Returns the Renderer for this View
	public CellViewRenderer getRenderer()
	{
		return renderer;
	}
	
	public static class MultiLinedRenderer extends JTextArea implements CellViewRenderer {
    	
    	protected transient JGraph graph = null;

    	transient protected Color gradientColor = null;
    	
    	/** Cached hasFocus and selected value. */
    	transient protected boolean hasFocus,
    		selected,
    		preview;

    	public MultiLinedRenderer() {
            setLineWrap(true);
            setWrapStyleWord(true);
        }

        public Component getRendererComponent(
                JGraph graph,
                CellView view,
                boolean sel,
                boolean focus,
                boolean preview) {
            setText(view.getCell().toString());
            this.graph = graph;
            this.selected = sel;
            this.preview = preview;
            this.hasFocus = focus;
            Map attributes = view.getAllAttributes();
            installAttributes(graph, attributes);
            return this;
        }
        
    	public void paint(Graphics g) {
    		try {
    			Graphics gTemp = g;
    			int x = 0;
			    int y = 0;
			    int l = getWidth()-1;
			    int h = getHeight()-1;
			    
    			gTemp.setClip(x,y,l-10,h);
    			super.paint(gTemp);
    			gTemp.setClip(l-10,y+10,10,h-10);
    			super.paint(gTemp);
    			
    			g.setClip(0,0,getWidth(),getHeight());
    			
    			setOpaque(true);
				Graphics2D g2d = (Graphics2D) g;
				
				//Avoir un dégradé de couleur
				//g2d.setPaint(new GradientPaint(0, 0, new Color(255, 255, 204), getWidth(),getHeight(), new Color(255, 255, 204), true));
			
			    int[] tabXLP={x,x+l-10,x+l,x+l,x};
			    int[] tabYLP={y,y,y+10,y+h,y+h};

			    //g2d.setPaint(new Color(255, 255, 204));
			    //g2d.fillPolygon(tabXLP,tabYLP,5);
			   
			    g2d.setPaint(new Color(153, 0, 51));
			    g2d.drawPolygon(tabXLP,tabYLP,5);
			    g2d.draw(new Line2D.Double(x+l-10, y,x+l-10,y+10));
			    g2d.draw(new Line2D.Double(x+l-10,y+10,x+l,y+10));
			     
    			
    			paintSelectionBorder(g);
    			
    		} catch (IllegalArgumentException e) {
    			// JDK Bug: Zero length string passed to TextLayout constructor
    		}
    	}

    	/**
    	 * Provided for subclassers to paint a selection border.
    	 */
    	protected void paintSelectionBorder(Graphics g) {
    		((Graphics2D) g).setStroke(GraphConstants.SELECTION_STROKE);
    		if (hasFocus && selected)
    			g.setColor(graph.getLockedHandleColor());
    		else if (selected)
    			g.setColor(graph.getHighlightColor());
    		if (selected) {
    			Dimension d = getSize();
    			g.drawRect(0, 0, d.width - 1, d.height - 1);
    		}
    	}

        protected void installAttributes(JGraph graph, Map attributes) {
            setOpaque(GraphConstants.isOpaque(attributes));
            Color foreground = GraphConstants.getForeground(attributes);
            setForeground((foreground != null) ? foreground : graph.getForeground());
            Color background = GraphConstants.getBackground(attributes);
            setBackground((background != null) ? background : graph.getBackground());
            Font font = GraphConstants.getFont(attributes);
            setFont((font != null) ? font : graph.getFont());
            Border border= GraphConstants.getBorder(attributes);
            Color bordercolor = GraphConstants.getBorderColor(attributes);
            if(border != null)
                setBorder(border);
            else if (bordercolor != null) {
                int borderWidth = Math.max(1, Math.round(GraphConstants.getLineWidth(attributes)));
                setBorder(BorderFactory.createLineBorder(bordercolor, borderWidth));
            }
    		//gradientColor = GraphConstants.getGradientColor(attributes);
        }
    }
}
