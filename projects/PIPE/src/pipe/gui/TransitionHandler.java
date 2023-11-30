package pipe.gui;

import java.awt.Container;
import java.awt.event.MouseEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import pipe.common.dataLayer.Transition;


/**
 * Class used to implement methods corresponding to mouse events on transitions.
 *
 */

public class TransitionHandler extends PlaceTransitionObjectHandler {

  static final int ROTATE_ANGLE = 45;

  public TransitionHandler(Container contentpane, Transition obj) {
    super(contentpane, obj);
  }

/*  public void mouseClicked(MouseEvent e) {
//  	if(GuiFrame.animationMode() == true)
//  		return;
  	super.mouseClicked(e);
  	if (e.getClickCount()==2) {
  		// System.out.println("rotate");
  		((Transition)e.getComponent()).rotate(ROTATE_ANGLE);
  	}
  }*/


  /** Creates the popup menu that the user will see when they right click on a component */
  public JPopupMenu getPopup(MouseEvent e) {
  	JPopupMenu popup = super.getPopup(e);
  	
    JMenuItem menuItem = new JMenuItem(new EditRateAction(contentPane, myObject));
    if (((Transition)myObject).isTimed())
    	menuItem.setText("Set rate");
    else
    	menuItem.setText("Set weight");
    popup.add(menuItem);
    
    
    //tagged rate is probabilistic, NOT user-specified
    /*menuItem = new JMenuItem(new EditTaggedRateAction(contentPane, myObject));
    menuItem.setText("Set rate for firing tagged token");
    popup.add(menuItem);*/

    menuItem = new JMenuItem(new EditTimedAction((Transition)myObject));
    if (((Transition)myObject).isTimed())
    	menuItem.setText("Make immediate");
    else
    	menuItem.setText("Make timed");   	
    popup.add(menuItem);
    
    menuItem = new JMenuItem(new EditServerAction((Transition)myObject));
    if (((Transition)myObject).isInfiniteServer())
    	menuItem.setText("Make single server");
    else
    	menuItem.setText("Make infinite server");   	
    popup.add(menuItem);        
    
    JMenu rotateMenu=new JMenu("Rotate");
    rotateMenu.add(new JMenuItem(new RotateTransitionAction(contentPane, (Transition)myObject, 45, "+45")));
    rotateMenu.add(new JMenuItem(new RotateTransitionAction(contentPane, (Transition)myObject, 90, "0")));
    rotateMenu.add(new JMenuItem(new RotateTransitionAction(contentPane, (Transition)myObject, 135, "-45")));
    popup.add(rotateMenu);
    
    
    /*
    menuItem = new JMenuItem(new FireTaggedTokenAction((Transition)myObject));
    if (((Transition)myObject).isTaggedTokenEnabled())menuItem.setText("Fire Tagged Token");
    popup.add(menuItem);
    */
    
    
    return popup;
  }
  
 
}


























