//######################################################################################
/*
 * Created on 28-Feb-2004
 * Author is Michael Camacho
 *
 */
//######################################################################################
package pipe.gui;

import java.awt.Container;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import pipe.common.dataLayer.ArcPathPoint;

public class ArcPathPointHandler extends PetriNetObjectHandler {
	public ArcPathPointHandler(Container contentpane, ArcPathPoint obj) {
		super(contentpane, obj);
		enablePopup = true;
	}	

		/** Creates the popup menu that the user will see when they right click on a component */
	public JPopupMenu getPopup(MouseEvent e)
	{
		JPopupMenu popup;
		
		if(((ArcPathPoint)myObject).isDeleteable()) popup = super.getPopup(e);
		else popup = new JPopupMenu();
		
		if(((ArcPathPoint)myObject).getIndex()==0) return null;
		else {
			JMenuItem menuItem = new JMenuItem(new ToggleArcPointAction((ArcPathPoint)myObject));
			if (((ArcPathPoint)myObject).getPointType() == ArcPathPoint.STRAIGHT)
				menuItem.setText("Change to Curved");
			else
				menuItem.setText("Change to Straight");
			popup.add(menuItem);
			
			menuItem = new JMenuItem(new SplitArcPointAction((ArcPathPoint)myObject));
			menuItem.setText("Split Point");
			popup.add(menuItem);
			
			// The following commented out code can be used for
			// debugging arc issues - Nadeem 18/07/2005
			/*menuItem = new JMenuItem(new GetIndexAction((ArcPathPoint)myObject, mouseposition));
			 menuItem.setText("Point Index");
			 popup.add(menuItem);*/
		}
		return popup;
	}

	public void mousePressed(MouseEvent e)
	{
		if (myObject.isEnabled())
		{
			((ArcPathPoint)e.getComponent()).setVisibilityLock(true);
			super.mousePressed(e);
		}
	}

	public void mouseDragged(MouseEvent e) {
		super.mouseDragged(e);	
	}

  public void mouseReleased(MouseEvent e) {
		((ArcPathPoint)e.getComponent()).setVisibilityLock(false);
		super.mouseReleased(e);
	}
}
