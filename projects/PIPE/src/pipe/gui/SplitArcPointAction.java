/*
 * Created on 21-Jun-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package pipe.gui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import pipe.common.dataLayer.ArcPathPoint;

/**
 * @author Nadeem
 *
 * This class is used to split a point on an arc into two to
 * allow the arc to be manipulated further.
 */
public class SplitArcPointAction extends AbstractAction {
	  private ArcPathPoint selected;
	  
	  public SplitArcPointAction(ArcPathPoint component) {
	    selected = component;
	  }
	  
	  public void actionPerformed(ActionEvent e) {
		selected.splitPoint();
	}

}
