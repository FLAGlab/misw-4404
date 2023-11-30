//######################################################################################
package pipe.gui;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import pipe.common.dataLayer.Arc;
//######################################################################################
/**
 * Class used to implement methods corresponding to mouse events on arcs.
 */
//######################################################################################
public class ArcHandler extends PetriNetObjectHandler {
//######################################################################################	
	public ArcHandler(Container contentpane, Arc obj) {
		super(contentpane, obj);
		enablePopup = true;
	}	
//######################################################################################	
	/** Creates the popup menu that the user will see when they right click on a component */
	public JPopupMenu getPopup(MouseEvent e) {
		
		JPopupMenu popup = super.getPopup(e);		

	    JMenuItem menuItem = new JMenuItem(new EditWeightAction(contentPane,(Arc)myObject));
	    menuItem.setText("Edit Weight");
	    
	    //change so that tagged arc is allowed to change its weight too
	    //if(!((Arc)myObject).isTagged())
	    	popup.add(menuItem);
	    
	    
	    menuItem = new JMenuItem(new EditTaggedAction(contentPane,(Arc)myObject));
	    if (((Arc)myObject).isTagged())
	    	menuItem.setText("Make Non-Tagged");
	    else
	    	menuItem.setText("Make Tagged");   	
	    popup.add(menuItem);
	    
	    menuItem = new JMenuItem(new SplitArcAction((Arc)myObject, e.getPoint()));
	    menuItem.setText("Split Arc Segment");
	    popup.add(menuItem);
		
		return popup;
	}
//###################################################################################### 
	public void mousePressed(MouseEvent e)
	{
		super.mousePressed(e);
		if (e.getClickCount() == 2)
		{
			((Arc)myObject).getSource().select();
			((Arc)myObject).getTarget().select();
			justSelected = true;
		}
	}
//######################################################################################
	public void mouseDragged(MouseEvent e) {
		switch (CreateGui.getApp().getMode()) {
		case SELECT:
			if (!isDragging) break;
			Arc currentObject = (Arc)myObject;
			Point oldLocation = currentObject.getLocation();
			// Calculate translation in mouse
			int transX = (int)(Grid.getModifiedX(e.getX() - dragInit.x));
			int transY = (int)(Grid.getModifiedY(e.getY() - dragInit.y));
			((GuiView)contentPane).getSelectionObject().translateSelection(transX, transY);
			dragInit.translate(-(currentObject.getLocation().x - oldLocation.x - transX),
							   -(currentObject.getLocation().y - oldLocation.y - transY));
		}
	}
}
//######################################################################################
	
	













