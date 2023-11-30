/*
 * Created on 18-Jul-2005
 */
package pipe.gui;

import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;

import javax.swing.AbstractAction;

import pipe.common.dataLayer.ArcPathPoint;

/**
 * @author Nadeem
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GetIndexAction extends AbstractAction {
	private ArcPathPoint selected;
	private Point2D.Float mp;

	public GetIndexAction(ArcPathPoint component, Point2D.Float mousepos) {
		selected = component;
		mp = mousepos;
	}

	public void actionPerformed(ActionEvent arg0) {
		System.out.println("Index is: " + selected.getIndex());
		System.out.println("At position: " + selected.getPoint().x + ", " + selected.getPoint().y);
		System.out.println("Mousepos: " + mp.x + ", " + mp.y);
	}

}
