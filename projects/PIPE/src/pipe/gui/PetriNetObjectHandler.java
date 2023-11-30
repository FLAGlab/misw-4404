package pipe.gui;

import java.awt.Container;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.MouseInputAdapter;

import pipe.common.dataLayer.ArcPathPoint;
import pipe.common.dataLayer.PetriNetObject;
import pipe.common.dataLayer.PlaceTransitionObject;
import pipe.common.dataLayer.Transition;


/**
 * Class used to implement methods corresponding to mouse events on all PetriNetObjects.
 *
 */

public class PetriNetObjectHandler extends MouseInputAdapter implements Constants {

  protected Container contentPane;
  protected PetriNetObject myObject = null;
  protected static boolean justSelected = false;	// set to true on press, and false on release;
  protected boolean isDragging = false;
  protected boolean enablePopup = false;
  protected Point dragInit = new Point();
  
  // constructor passing in all required objects
  public PetriNetObjectHandler(Container contentpane, PetriNetObject obj) {
    contentPane = contentpane;
    myObject = obj;
  }


  public void enablePopupMenu(boolean allow)
  {
  	enablePopup = allow;
  }
  
  /** Creates the popup menu that the user will see when they right click on a component */
  public JPopupMenu getPopup(MouseEvent e) {

    JPopupMenu popup = new JPopupMenu();

    JMenuItem menuItem = new JMenuItem(new DeletePetriNetObjectAction(myObject));
    menuItem.setText("Delete");
    popup.add(menuItem);

    return popup;
  }


  /** Displays the popup menu */
  
  
  private void checkForPopup(MouseEvent e) {
    
	  if (e.isPopupTrigger()  &&  !( CreateGui.getView().animationmode  )) {	
    
      JPopupMenu m=getPopup(e);
      if(m!=null) m.show(myObject, e.getX(), e.getY());
    }
    
    
  }
   
  
  public void mousePressed(MouseEvent e) {
  	
  	if (enablePopup)
  		checkForPopup(e);
  	
  	switch(CreateGui.getApp().getMode()){
  		case SELECT:
  		{
  			if (!myObject.isSelected())
  			{
  				if (!e.isShiftDown())
  				{
  					((GuiView)contentPane).getSelectionObject().clearSelection();
  				}
  				myObject.select();
  				justSelected = true;
  			}	
  			dragInit = e.getPoint();
  		}
  	}
  }

  /** Event handler for when the user releases the mouse, used in conjunction with mouseDragged and mouseReleased to implement the moving action */

  public void mouseReleased(MouseEvent e) {
//  	if(GuiFrame.animationMode() == true)
//  		return;
  	// Have to check for popup here as well as on pressed for crossplatform!!
  	if (enablePopup)
  		checkForPopup(e);
  	switch(CreateGui.getApp().getMode()){
  		case SELECT:
  			if (isDragging)
  			{	
  				isDragging = false;
  			}
  			else
  			{
  				if (!justSelected)
  				{
  					if (e.isShiftDown())
  					{	
  						myObject.deselect();
  					}
  					else
  					{
  						((GuiView)contentPane).getSelectionObject().clearSelection();
  						myObject.select();
  					}
  				}
  			}
  			break;
  	}
  	justSelected = false;
  }  
  
  /** Handler for dragging PlaceTransitionObjects around */
  public void mouseDragged(MouseEvent e) {
  	
  	switch (CreateGui.getApp().getMode()) {
  	case SELECT:
  		if (!isDragging && ((GuiView)contentPane).getSelectionObject().getSelectionCount()==1) {
  			if (myObject instanceof PlaceTransitionObject) {
  				((PlaceTransitionObject)myObject).setCentre(
  						Grid.getModifiedX(((PlaceTransitionObject)myObject).getCentre().getX()),
						Grid.getModifiedY(((PlaceTransitionObject)myObject).getCentre().getY()));
  			} else if (myObject instanceof ArcPathPoint) {
  				((ArcPathPoint)myObject).setPointLocation(
  						Grid.getModifiedX(((ArcPathPoint)myObject).getPoint().getX()),
						Grid.getModifiedY(((ArcPathPoint)myObject).getPoint().getY()));
  			}
  		}
  		
  		if (myObject.isDraggable())
  			isDragging = true;
  		
  		// Calculate translation in mouse
  		int transX = Grid.getModifiedX(e.getX() - dragInit.x);
  		int transY = Grid.getModifiedY(e.getY() - dragInit.y);
      ((GuiView)contentPane).getSelectionObject().translateSelection(transX, transY);
  	}
  }
  
}














