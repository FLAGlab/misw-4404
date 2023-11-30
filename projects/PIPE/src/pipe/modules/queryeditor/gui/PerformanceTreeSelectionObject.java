/**
 * PerformanceTreeSelectionObject
 * 
 * Handles selection rectangle functionality
 * 
 * @author Tamas Suto
 * @date 22/04/07
 */

package pipe.modules.queryeditor.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;

import pipe.common.QueryConstants;
import pipe.modules.queryeditor.QueryManager;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeArc;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeArcPath;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeArcPathPoint;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeNode;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeObject;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroManager;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroNode;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroView;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.ResultNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.SequentialNode;


public class PerformanceTreeSelectionObject extends JComponent implements MouseListener, MouseMotionListener, QueryConstants {

	private static final long serialVersionUID = 1L;

	private Point selectionInit;
	private Rectangle selectionRectangle = new Rectangle(-1,-1);
	private Rectangle tempBounds = new Rectangle();
	private static final Color selectionColor = new Color(0,0,255,30);
	private static final Color selectionColorOutline = new Color(0,0,100);	
	private boolean isSelecting;
	private boolean enabled;
	private JLayeredPane edit_window;


	public PerformanceTreeSelectionObject(QueryView _edit_window) {
		this();
		edit_window = _edit_window;
	}

	public PerformanceTreeSelectionObject(MacroView _edit_window) {
		this();
		edit_window = _edit_window;
	}

	public PerformanceTreeSelectionObject() {
		addMouseListener(this);
		addMouseMotionListener(this);
	}


	public void enableSelection() {
		if (!enabled) {			
			edit_window.add(this);
			enabled = true;
			updateBounds();
		}
	}

	public void disableSelection() {
		if (enabled) {
			edit_window.remove(this);
			enabled = false;
		}
	}

	public void updateBounds() {
		if (enabled) {
			setBounds(0,0,edit_window.getWidth(),edit_window.getHeight());	
		}
	}

	public void processSelection(MouseEvent e) {	
		if (!e.isShiftDown()) 
			clearSelection();

		// Get all the objects in the current window
		Component netObj[] = edit_window.getComponents();

		for (int i=0; i<netObj.length; i++) {
			// Handle PerformanceTreeNodes
			if ((netObj[i] instanceof PerformanceTreeNode) && ((PerformanceTreeObject)netObj[i]).isSelectable()) {
				if (selectionRectangle.intersects(netObj[i].getBounds(tempBounds))) {		
					((PerformanceTreeNode)netObj[i]).select();
				}
			}	

			// Handle PerformanceTreeArcs 
			if ((netObj[i] instanceof PerformanceTreeArc) && ((PerformanceTreeObject)netObj[i]).isSelectable())	{
				PerformanceTreeArc thisArc = (PerformanceTreeArc)netObj[i];
				PerformanceTreeArcPath thisArcPath = thisArc.getArcPath();			
				if (thisArcPath.proximityIntersects(selectionRectangle)) 
					thisArcPath.showPoints();
				else
					thisArcPath.hidePoints();
				if (thisArcPath.intersects(selectionRectangle)) {
					thisArc.select();
				}
			}

			// Handle PerformanceTreeArcPathPoints
			if ((netObj[i] instanceof PerformanceTreeArcPathPoint) && ((PerformanceTreeObject)netObj[i]).isSelectable()) {
				if (selectionRectangle.intersects(netObj[i].getBounds(tempBounds))) {
					((PerformanceTreeArcPathPoint)netObj[i]).select();	
				}
			}
		}									
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;	
		g2d.setPaint(selectionColor);
		g2d.fill(selectionRectangle);
		g2d.setPaint(selectionColorOutline);
		g2d.draw(selectionRectangle);
	}

	public boolean contains(int x, int y) {
		return true;
	}

	public void mouseClicked(MouseEvent arg0) {
		// Not needed
	}

	public void mouseEntered(MouseEvent arg0) {
		// Not needed
	}

	public void mouseExited(MouseEvent arg0) {
		// Not needed
	}

	public void mousePressed(MouseEvent arg0) {		
		isSelecting = true;
		edit_window.setLayer(this, SELECTION_LAYER_OFFSET);
		selectionInit = arg0.getPoint();
		selectionRectangle.setRect(selectionInit.getX(), selectionInit.getY(), 0, 0);
		// Select anything that intersects with the rectangle.
		processSelection(arg0);	
		repaint();
	}

	public void mouseDragged(MouseEvent arg0) {
		if (isSelecting) {
			selectionRectangle.setSize((int)Math.abs(arg0.getX()-selectionInit.getX()),
					(int)Math.abs(arg0.getY()-selectionInit.getY()));
			selectionRectangle.setLocation((int)Math.min(selectionInit.getX(), arg0.getX()),
					(int)Math.min(selectionInit.getY(), arg0.getY()));
			// Select anything that intersects with the rectangle.
			processSelection(arg0);	
		}
		repaint();
	}

	public void mouseReleased(MouseEvent arg0) {
		if (isSelecting) {		
			isSelecting = false;
			edit_window.setLayer(this, LOWEST_LAYER_OFFSET);
			selectionRectangle.setRect(-1,-1,0,0);
			repaint();
		}
	}

	public void mouseMoved(MouseEvent arg0) {
		// Not needed
	}

	public void deleteSelection() {
		if (MacroManager.getEditor() == null)
			QueryManager.clearInfoBox();
		else
			MacroManager.getEditor().writeToInfoBox("");

		Component[] netObj;
		if (MacroManager.getEditor() == null)
			netObj = ((QueryView)edit_window).getComponents();
		else 
			netObj = ((MacroView)edit_window).getComponents();

		for (int i=0; i<netObj.length; i++) {
			if ((netObj[i] instanceof PerformanceTreeObject) && (((PerformanceTreeObject)netObj[i]).isSelected())) {
				if (netObj[i] instanceof PerformanceTreeArc) {
					if (QueryManager.allowDeletionOfArcs) {
						((PerformanceTreeArc)netObj[i]).delete();
					}
				}
				else if (netObj[i] instanceof PerformanceTreeNode) {
					//delete node is not allowed in text query editor mode
					if (((PerformanceTreeNode)netObj[i]).enablePopup){
						if (netObj[i] instanceof ResultNode) {
							String msg = QueryManager.addColouring("Deletion of the topmost node in the tree is not permitted.");
							if (MacroManager.getEditor() == null)
								QueryManager.writeToInfoBox(msg);
							else
								MacroManager.getEditor().writeToInfoBox(msg);
						}
						else if ((netObj[i] instanceof MacroNode) && MacroManager.getEditor() != null) {
							String msg = QueryManager.addColouring("Deletion of the topmost macro node in the tree is not permitted.");
							MacroManager.getEditor().writeToInfoBox(msg);
						}
						else {
							if(!sequentialNodeCase((PerformanceTreeNode)netObj[i])) {
								// just delete the node, not the associated arc
								((PerformanceTreeNode)netObj[i]).delete();
							}
						}
					//not permit delete in text editing mode
					}else{
						String msg = QueryManager.addColouring("Deletion in the text query editing mode is not permitted.");
						if (MacroManager.getEditor() == null)
							QueryManager.writeToInfoBox(msg);
						else
							MacroManager.getEditor().writeToInfoBox(msg);
					}
				}
				else {
					((PerformanceTreeObject)netObj[i]).delete();
				}
			}
		}
	}

	public void clearSelection() {
		Component netObj[] = edit_window.getComponents();
		// Get all the objects in the current window
		for (int i=0; i<netObj.length; i++) {
			if ((netObj[i] instanceof PerformanceTreeArc) && ((PerformanceTreeObject)netObj[i]).isSelectable()) {
				PerformanceTreeArc thisArc = (PerformanceTreeArc)netObj[i];
				thisArc.deselect();
				PerformanceTreeArcPath thisArcPath = thisArc.getArcPath();
				thisArcPath.hidePoints();
				for(int j=1; j < thisArcPath.getEndIndex(); j++) {
					thisArcPath.deselectPoint(j);
				}	
			} 
			else if ((netObj[i] instanceof PerformanceTreeObject) && ((PerformanceTreeObject)netObj[i]).isSelectable()) {
				((PerformanceTreeObject)netObj[i]).deselect();
			}
		}
	}

	public void translateSelection(int transX, int transY) {
		if(transX == 0 && transY == 0)
			return;
		// Get all the objects in the current window
		Component netObj[] = edit_window.getComponents();	
		// First see if translation will put anything at a negative location
		Point p,topleft = null;
		for (int i=0; i<netObj.length; i++) {
			if (netObj[i] instanceof PerformanceTreeObject) {
				if (((PerformanceTreeObject)netObj[i]).isSelected()){
					p = ((PerformanceTreeObject)netObj[i]).getLocation();
					if(topleft == null) {
						topleft = p;
					}
					else {
						if(p.x < topleft.x)
							topleft.x = p.x;
						if(p.y < topleft.y)
							topleft.y = p.y;
					}
				}
			} 
		}

		if(topleft!=null) {
			topleft.translate(transX,transY);
			if(topleft.x<0) 
				transX -= topleft.x;
			if(topleft.y<0) 
				transY -= topleft.y;
			if(transX==0 && transY==0)
				return;
		}

		for (int i=0; i<netObj.length; i++) {
			if (netObj[i] instanceof PerformanceTreeNode) {
				if (((PerformanceTreeNode)netObj[i]).isSelected()){
					// Translate the object
					((PerformanceTreeNode)netObj[i]).translate(transX,transY);
					// Update all attached arcs to the new location
					((PerformanceTreeNode)netObj[i]).updateConnected();
				}
			}
			else if (netObj[i] instanceof PerformanceTreeArc) {
				PerformanceTreeArc thisArc = (PerformanceTreeArc) netObj[i];
				for(int j=1; j <= thisArc.getArcPath().getEndIndex(); j++)
					if (thisArc.getArcPath().isPointSelected(j))
						thisArc.getArcPath().translatePoint(j,transX,transY);
				thisArc.updateArcPosition();
				thisArc.updateLabelPosition();
			}
		}

		if (MacroManager.getEditor() == null)
			((QueryView)edit_window).updatePreferredSize();
		else 
			((MacroView)edit_window).updatePreferredSize();
	}

	public int getSelectionCount() {
		Component netObj[] = edit_window.getComponents();
		int selectionCount = 0;
		// Get all the objects in the current window
		for (int i=0; i<netObj.length; i++) {
			// Handle PerformanceTreeNodes
			if ((netObj[i] instanceof PerformanceTreeNode) && ((PerformanceTreeObject)netObj[i]).isSelectable()) {
				if (((PerformanceTreeNode)netObj[i]).isSelected()) {
					selectionCount++;
				}
			}

			// Handle Arcs and PerformanceTreeArc Points
			if ((netObj[i] instanceof PerformanceTreeArc) && ((PerformanceTreeObject)netObj[i]).isSelectable())	{				
				PerformanceTreeArc thisArc = (PerformanceTreeArc)netObj[i];
				PerformanceTreeArcPath thisArcPath = thisArc.getArcPath();
				for(int j=1; j <= thisArcPath.getEndIndex(); j++) {
					if (thisArcPath.isPointSelected(j)) {
						selectionCount++;
					}
				}
			}
		}									
		return selectionCount;
	}

	/** This method takes care of the case when a node is linked directly to
	 *  a SequentialNode through an optional arc. In such as case, the arc 
	 *  should be removed along with the node.
	 * @return
	 */
	private boolean sequentialNodeCase(PerformanceTreeNode node) {
		if (node.getIncomingArc() != null) {
			PerformanceTreeArc incomingArc = node.getIncomingArc();
			PerformanceTreeNode parentNode = incomingArc.getSource();
			if (!incomingArc.isRequired() && 
					parentNode instanceof SequentialNode &&
					sequentialNodeHasAtLeastOneOptionalArc(parentNode)) {
				node.delete();
				incomingArc.delete();
				return true;
			}
			else
				return false;
		}
		else
			return false;
	}

	/**
	 * We should only allow deletion of the associated optional arc if there are
	 * at least two optional arcs. This is so, because a new optional arc is only
	 * created whenever the last free arc is assigned to a node.
	 * @param node
	 * @return
	 */
	private boolean sequentialNodeHasAtLeastOneOptionalArc(PerformanceTreeNode node) {
		if (node instanceof SequentialNode) {
			SequentialNode seqNode = (SequentialNode)node;
			ArrayList<String> outgoingArcIDs = (ArrayList<String>)seqNode.getOutgoingArcIDs();
			Iterator<String> i = outgoingArcIDs.iterator();
			int optionalArcCount = 0;
			while (i.hasNext()) {
				PerformanceTreeArc arc = QueryManager.getData().getArc(i.next());
				if (!arc.isRequired()) 
					optionalArcCount++;
			}
			if (optionalArcCount > 1) 
				return true;
			else
				return false;
		}
		else return false;
	}
}
