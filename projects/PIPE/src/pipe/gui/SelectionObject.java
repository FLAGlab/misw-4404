//######################################################################################
/*
 * Created on 08-Feb-2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
//######################################################################################
package pipe.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;

import pipe.common.dataLayer.AnnotationNote;
import pipe.common.dataLayer.Arc;
import pipe.common.dataLayer.ArcPath;
import pipe.common.dataLayer.ArcPathPoint;
import pipe.common.dataLayer.PetriNetObject;
import pipe.common.dataLayer.PlaceTransitionObject;

/**
 * @author Peter Kyme, Michael Camacho
 *
 * Class to handle selection rectangle functionality
 */

public class SelectionObject extends JComponent 
							implements MouseListener, MouseMotionListener, Constants {

	private Point selectionInit;
	private Rectangle selectionRectangle = new Rectangle(-1,-1);
	private boolean isSelecting;
	private static final Color selectionColor = new Color(0,0,255,30);
	private static final Color selectionColorOutline = new Color(0,0,100);	
	private GuiView edit_window;
	private boolean enabled;
	private Rectangle tempBounds = new Rectangle();

	public SelectionObject(GuiView _edit_window) {
		this();
		edit_window = _edit_window;
	}

	public SelectionObject() {
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	public void updateBounds() {
		if (enabled)
		{
			setBounds(0,0,edit_window.getWidth(),edit_window.getHeight());	
		}
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

	public void processSelection(MouseEvent e)
	{	
		if (!e.isShiftDown()) clearSelection();
		Component netObj[] = edit_window.getComponents();
		// Get all the objects in the current window
		for (int i=0; i<netObj.length; i++) {
			// Handle Arcs and Arc Points
			if ((netObj[i] instanceof Arc) && ((PetriNetObject)netObj[i]).isSelectable())	{
				Arc thisArc = (Arc)netObj[i];
				ArcPath thisArcPath = thisArc.getArcPath();
				
				if (thisArcPath.proximityIntersects(selectionRectangle))
					thisArcPath.showPoints();
				else
					thisArcPath.hidePoints();
				
				if (thisArcPath.intersects(selectionRectangle)) {
					thisArc.select();
				}
			}
		
			if ((netObj[i] instanceof ArcPathPoint) && ((PetriNetObject)netObj[i]).isSelectable())
				if (selectionRectangle.intersects(netObj[i].getBounds(tempBounds)))
				((ArcPathPoint)netObj[i]).select();			
			
			// Handle PlaceTransition Objects
			if ((netObj[i] instanceof PlaceTransitionObject) && ((PetriNetObject)netObj[i]).isSelectable())
				if (selectionRectangle.intersects(netObj[i].getBounds(tempBounds)))		
					((PlaceTransitionObject)netObj[i]).select();
				
			// Handle AnnotationNote Objects
			if ((netObj[i] instanceof AnnotationNote) && ((PetriNetObject)netObj[i]).isSelectable())
				if (selectionRectangle.intersects(netObj[i].getBounds(tempBounds)))
				((AnnotationNote)netObj[i]).select();
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

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent arg0) {
		isSelecting = true;
		edit_window.setLayer(this, SELECTION_LAYER_OFFSET);
		selectionInit = arg0.getPoint();
		selectionRectangle.setRect(selectionInit.getX(), selectionInit.getY(), 0, 0);
		processSelection(arg0);	// Select anything that intersects with the rectangle.
		repaint();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent arg0) {
		if (isSelecting) {		
			isSelecting = false;
			edit_window.setLayer(this, LOWEST_LAYER_OFFSET);
			selectionRectangle.setRect(-1,-1,0,0);
			repaint();
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	public void mouseDragged(MouseEvent arg0) {
		if (isSelecting) {
			selectionRectangle.setSize((int)Math.abs(arg0.getX()-selectionInit.getX()),
									   (int)Math.abs(arg0.getY()-selectionInit.getY()));
			selectionRectangle.setLocation((int)Math.min(selectionInit.getX(), arg0.getX()),
										   (int)Math.min(selectionInit.getY(), arg0.getY()));
			processSelection(arg0);	// Select anything that intersects with the rectangle.
		}
		repaint();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	public void mouseMoved(MouseEvent arg0) {
		// Not needed
	}

	public void deleteSelection()
	{
		Component netObj[] = edit_window.getComponents();
		for (int i=0; i<netObj.length; i++)
		{
			if ((netObj[i] instanceof PetriNetObject)
										&& (((PetriNetObject)netObj[i]).isSelected()))
			{
				deletePetriNetObject((PetriNetObject)netObj[i]);
			}
		}
    //edit_window.updatePreferredSize();
	}

	public void deletePetriNetObject(PetriNetObject o)
	{
			o.delete();
	}

	public void clearSelection() {
		Component netObj[] = edit_window.getComponents();
		// Get all the objects in the current window
		for (int i=0; i<netObj.length; i++)
		{
			if ((netObj[i] instanceof Arc)
			&& ((PetriNetObject)netObj[i]).isSelectable())
			{
				Arc thisArc = (Arc)netObj[i];
				thisArc.deselect();

				ArcPath thisArcPath = thisArc.getArcPath();
				thisArcPath.hidePoints();
				for(int j=1; j < thisArcPath.getEndIndex(); j++) {
					thisArcPath.deselectPoint(j);
				}	
			} else if ((netObj[i] instanceof PetriNetObject)
			&& ((PetriNetObject)netObj[i]).isSelectable())
			{
        ((PetriNetObject)netObj[i]).deselect();
			}
		}
	}

	public void translateSelection(int transX, int transY) {
		if(transX==0 && transY==0)return;
			
		Component netObj[] = edit_window.getComponents();	// Get all the objects in the current window

    // First see if translation will put anything at a negative location
    Point p,topleft=null;
//    Point2D.Float fp;
    for (int i=0; i<netObj.length; i++) {
      if (netObj[i] instanceof PetriNetObject) {
        if (((PetriNetObject)netObj[i]).isSelected()){
          p=((PetriNetObject)netObj[i]).getLocation();
          if(topleft==null) {
            topleft=p;
          }
          else {
            if(p.x<topleft.x)topleft.x=p.x;
            if(p.y<topleft.y)topleft.y=p.y;
          }
        }
      } 
    }
    
    if(topleft!=null) {
      topleft.translate(transX,transY);
      if(topleft.x<0) transX-=topleft.x;
      if(topleft.y<0) transY-=topleft.y;
      if(transX==0 && transY==0)return;
    }
        
		for (int i=0; i<netObj.length; i++) {
			if (netObj[i] instanceof PlaceTransitionObject) {
				if (((PlaceTransitionObject)netObj[i]).isSelected()){
					// Translate the object
					((PlaceTransitionObject)netObj[i]).translate(transX,transY);
					// Update all attached arcs to the new location
					((PlaceTransitionObject)netObj[i]).updateConnected();
				}
			}
			else if (netObj[i] instanceof AnnotationNote) {
				if (((AnnotationNote)netObj[i]).isSelected()){
					// Translate the object
					((AnnotationNote)netObj[i]).translate(transX,transY);
				}
			}
			
			else if (netObj[i] instanceof Arc) {
				Arc thisArc = (Arc) netObj[i];
				for(int j=1; j < thisArc.getArcPath().getEndIndex(); j++)
					if (thisArc.getArcPath().isPointSelected(j))
						thisArc.getArcPath().translatePoint(j,transX,transY);
				thisArc.updateArcPosition();
			}
		}
    edit_window.updatePreferredSize();
	}

	public int getSelectionCount() {
		Component netObj[] = edit_window.getComponents();
		int selectionCount = 0;
		// Get all the objects in the current window
		for (int i=0; i<netObj.length; i++) {
			// Handle Arcs and Arc Points
			if ((netObj[i] instanceof Arc) && ((PetriNetObject)netObj[i]).isSelectable())	{
				Arc thisArc = (Arc)netObj[i];
				ArcPath thisArcPath = thisArc.getArcPath();
					for(int j=1; j < thisArcPath.getEndIndex(); j++)
						if (thisArcPath.isPointSelected(j)) {
							selectionCount++;
						}
			}
			
			// Handle PlaceTransition Objects
			if ((netObj[i] instanceof PlaceTransitionObject) && ((PetriNetObject)netObj[i]).isSelectable())
				if (((PlaceTransitionObject)netObj[i]).isSelected()) {
					selectionCount++;
				}
		}									
		return selectionCount;
	}
}
