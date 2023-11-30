package pipe.gui;

import java.awt.Container;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import pipe.common.dataLayer.Place;


/**
 * Class used to implement methods corresponding to mouse events on places.
 *
 */

public class PlaceHandler extends PlaceTransitionObjectHandler {

  public PlaceHandler(Container contentpane, Place obj) {
    super(contentpane, obj);
  }

  /** Creates the popup menu that the user will see when they right click on a component */
  public JPopupMenu getPopup(MouseEvent e) {
  	JPopupMenu popup = super.getPopup(e);

    JMenuItem menuItem = new JMenuItem(new EditTokenAction(contentPane,(Place)myObject));
    menuItem.setText("Edit Tokens");
    popup.add(menuItem);

    menuItem = new JMenuItem(new EditTaggedTokenAction(contentPane,(Place)myObject));
    
    if (((Place)myObject).isTagged() && ((Place)myObject).getCurrentMarking()>0) {
    	menuItem.setText("Make token non-tagged");
    	popup.add(menuItem);
    }
    //allow adding Tagged Token
    else if(!((Place)myObject).isTagged() && ((Place)myObject).getCurrentMarking()>0){
    	menuItem.setText("Make a token tagged");   	
    	popup.add(menuItem);
    }
    /*
    else if(((Place)myObject).getCurrentMarking()>0) {
    	menuItem.setText("Make Token Tagged");   	
    	popup.add(menuItem);
    }
    */
    
    return popup;
  }

  public void mouseClicked(MouseEvent e) {
  	super.mouseClicked(e);

    int currentMarking = ((Place)myObject).getCurrentMarking();
    switch(CreateGui.getApp().getMode()) {

      case ADDTOKEN:
        if (e.getButton() == MouseEvent.BUTTON1) {
          if (currentMarking == 0)
          	((Place)myObject).setCurrentMarking(1);
          else {
            currentMarking++;
            ((Place)myObject).setCurrentMarking(currentMarking);
          }
          ((Place)myObject).repaint();
        };  break;
        case DELTOKEN:
          if (e.getButton() == MouseEvent.BUTTON1) {
            if (currentMarking > 0) {
              currentMarking--;
              ((Place)myObject).setCurrentMarking(currentMarking);
              ((Place)myObject).repaint();
            }
          }; break;
          default: ;
    }
  }
}