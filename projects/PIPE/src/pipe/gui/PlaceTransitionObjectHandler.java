package pipe.gui;

import java.awt.Container;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import pipe.common.dataLayer.Arc;
import pipe.common.dataLayer.PlaceTransitionObject;


/**
 * Class used to implement methods corresponding to mouse events on places.
 *
 *@author Pere Bonet changed the mousePressed method to only allow the
 * creation of an arc by left-clicking
 * @author Matthew Worthington - modified the handler which was causing the null pointer 
 * exceptions and incorrect petri nets xml representation.
 */

public class PlaceTransitionObjectHandler extends PetriNetObjectHandler {

	ArcKeyboardEventHandler keyHandler = null;
	
  // constructor passing in all required objects
  public PlaceTransitionObjectHandler(Container contentpane, PlaceTransitionObject obj) {
    super(contentpane, obj);
    enablePopup = true;
  }
 
  public JPopupMenu getPopup(MouseEvent e) {
  	JPopupMenu popup = super.getPopup(e);
  	  	
  	JMenuItem menuItem = new JMenuItem(new LabelAction(contentPane, myObject));
  	menuItem.setText("Edit label");
  	popup.add(menuItem);
  	
  	return popup;
  }
  
  public void mousePressed(MouseEvent e) {
  	super.mousePressed(e);
  	 // Avoid creating arcs with a right-click or middle-click
    if (e.getButton() != MouseEvent.BUTTON1) return;
    PlaceTransitionObject currentObject = (PlaceTransitionObject)myObject;
  	switch (CreateGui.getApp().getMode()) {
  		case ARC:
  			if (CreateGui.getView().createArc == null) {
  				Arc newArc = new Arc(currentObject);
  				contentPane.add(newArc);
  				currentObject.addConnectFrom(newArc);
  				CreateGui.getView().createArc = newArc;
  				// add a handler for shift & esc actions drawing arc
  				// this is removed when the arc is finished drawing:
  				keyHandler = new ArcKeyboardEventHandler((Arc)newArc);
  				newArc.addKeyListener(keyHandler);
  				newArc.requestFocusInWindow();
  				
  				newArc.setSelectable(false);
  			}
  			else
  				{
  				Arc createArc = CreateGui.getView().createArc;
  				if (!currentObject.getClass().getName().equals(createArc.getSource().getClass().getName())) {
  				}
					createArc.setSelectable(true);
					createArc.setTarget(currentObject);
					currentObject.addConnectTo(createArc);
					// Evil hack to prevent the arc being added to GuiView twice
					contentPane.remove(createArc);
					CreateGui.getModel().addArc(createArc);
					CreateGui.getView().addNewPetriNetObject(createArc);
//					GuiView.createArc.deselect();
					// arc is drawn, remove handler:  					
					createArc.removeKeyListener(keyHandler);
					keyHandler = null;
					CreateGui.getView().createArc = null;
					CreateGui.getView().setShiftDown(false);
					
				}
  	}
  }
  }
