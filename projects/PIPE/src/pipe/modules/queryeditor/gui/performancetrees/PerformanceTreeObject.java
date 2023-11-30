/**
 * PerformanceTreeObject
 * 
 * Implements the basic methods that every Performance Tree object has
 * 
 * @author Tamas Suto
 * @date 17/04/07
 */

package pipe.modules.queryeditor.gui.performancetrees;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JComponent;

import pipe.common.QueryConstants;
import pipe.gui.Zoomable;
import pipe.modules.queryeditor.QueryManager;
import pipe.modules.queryeditor.gui.PerformanceTreeZoomController;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroManager;


public class PerformanceTreeObject extends JComponent implements QueryConstants, Zoomable, Cloneable {
	
    protected String id = null; 
    
    protected double positionX;  // Current x-axis position on screen
	protected double positionY;  // Current y-axis position on screen
	protected double locationX;  // The "real" x coordinate of this node or arc, i.e. the x position at 100% zoom.
	protected double locationY;  // The "real" y coordinate of this node or arc, i.e. the x position at 100% zoom.
    protected Rectangle bounds = new Rectangle();
   
    protected Color objectColour = ELEMENT_LINE_COLOUR;
    protected Color selectionBorderColour = SELECTION_LINE_COLOUR;
    
    protected boolean selectable = true;  // true if object can be selected.
    protected boolean selected = false;	  // true if part of the current selection.
    protected static boolean ignoreSelection = false;
    
    protected boolean draggable = true;	  // true if object can be dragged.
    protected boolean isDragging;  // Used in the mouse events to control dragging
	
	private PerformanceTreeZoomController zoomControl;  // The PerformanceTreeZoomController of the QueryView this component is part of.
	public boolean enablePopup = true; //pop up is not enabled in text query editor
	
	public PerformanceTreeObject(double positionXInput, double positionYInput, String idInput){
		this(positionXInput,positionYInput);
		id = idInput;
	}
	
	public PerformanceTreeObject(double positionXInput, double positionYInput){
		if (MacroManager.getEditor() == null) {
			if(QueryManager.getEditor()!=null) 
				addZoomController(QueryManager.getView().getZoomController());
		}
		else {
			addZoomController(MacroManager.getView().getZoomController());
		}
		setPositionX(positionXInput);
		setPositionY(positionYInput);
	}
	
	public PerformanceTreeObject(String idInput){
  	    id = idInput;
    }
	
	public PerformanceTreeObject() {
		
	}
	
	public String getId() {
		return id;
	}

	public void setId(String idInput) {
		id = idInput;
	}
	
	public double getPositionX() {
		return positionX;
	}
	
	public Double getPositionXObject() {
		return new Double(locationX);
	}
	 
	public void setPositionX(double positionXInput) {
		positionX = positionXInput;
		if (getZoomController() != null){
			locationX = getZoomController().getUnzoomedValue(positionX);
		} 
		else {
			locationX = (int)positionX;
		}
	}
		
	public double getPositionY() {
		return positionY;
	}
	
	public Double getPositionYObject() {
		return new Double(locationY);
	}

	public void setPositionY(double positionYInput) {
		positionY = positionYInput;
		if (getZoomController() != null){
			locationY = getZoomController().getUnzoomedValue(positionY);
		} 
		else {
			locationY = (int)positionY;
		}
	} 
	
	public void setObjectColour(Color c) {
  	    objectColour = c;
    }

    public void setSelectionBorderColour(Color c) {
  	    selectionBorderColour = c;
    }
    
    public boolean isSelectable() {
  	    return selectable;
    }
    
    public boolean isSelected() {
  	    return selected;
    }

    public void setSelectable(boolean allow) {
  	    selectable = allow;
    }

    public void select() {
  	    if (selectable && !selected) {	
  		  selected = true;
  		  repaint();
  	    }
    }

    public void deselect() {
  	    if (selected) {	
  		  selected = false;
  		  repaint();
  	    }
    }
    
    public void delete() {
    	if (MacroManager.getEditor() == null) 
    		QueryManager.getData().removePerformanceTreeObject(this);
    	else
    		MacroManager.getEditor().removePerformanceTreeObject(this);
	    removeFromContainer();
	    removeAll();
    }

    public void removeFromContainer() {
    	Container c = getParent();
    	if (c != null) 
    		c.remove(this);
    }

    public static void ignoreSelection(boolean ignore) {
  	    ignoreSelection = ignore;
    }

    public boolean isDraggable() {
  	    return draggable;
    }

    public void setDraggable(boolean allow) {
  	    draggable = allow;
    }  
	
	/** Translates the component by x,y */
	public void translate(int x, int y) {
		setPositionX(positionX + x);
		setPositionY(positionY + y);
		updateBounds();
	}
	
	public boolean areNotSameType(PerformanceTreeObject o) {
		return (((this instanceof PerformanceTreeNode) && (o instanceof PerformanceTreeArc)) ||
				((this instanceof PerformanceTreeArc) && (o instanceof PerformanceTreeNode)));
	}
    
    protected PerformanceTreeZoomController getZoomController(){
    	return zoomControl;
    }
  
    public void addZoomController(PerformanceTreeZoomController zoomControl2) {
    	zoomControl = zoomControl2;
    }
 
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
    }
    
    public PerformanceTreeObject clone() {
		try {
			PerformanceTreeObject ptObjectCopy = (PerformanceTreeObject)super.clone();
			return ptObjectCopy;
		} catch (CloneNotSupportedException e){
			e.printStackTrace();	
		}
		return null;
	}
    public void setEnablePopup(boolean allow){
    	this.enablePopup = allow;
    }
    /** Implemented in subclasses */
    public void updateBounds() {}
    
    /** Implemented in subclasses */
    public void addedToGui() {}
    
    /** Implemented in subclasses */
    public void zoomUpdate() {}
    
	

}

