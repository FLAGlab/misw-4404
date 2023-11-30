package pipe.gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import pipe.common.dataLayer.Arc;

/**
 * @authors Michael Camacho and Tom Barnwell
 *  
 */

public class ArcKeyboardEventHandler implements KeyListener {

	public static boolean shiftDown = false;
	
	private Arc arcBeingDrawn;

	public ArcKeyboardEventHandler(Arc anArc) {
		arcBeingDrawn = anArc;
    shiftDown = false;
	}

	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
			((GuiView)arcBeingDrawn.getParent()).setShiftDown(true);
		}

		else if (e.getKeyCode() == KeyEvent.VK_ESCAPE ||
				 e.getKeyCode() == KeyEvent.VK_DELETE   ) {
			GuiView aView = ((GuiView)arcBeingDrawn.getParent());
			aView.createArc = null;
			arcBeingDrawn.delete();
			aView.repaint();
		}
    
//    if(arcBeingDrawn!=null) e.consume();

	}

	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
			((GuiView)arcBeingDrawn.getParent()).setShiftDown(false);
		}
		e.consume();
	}

	public void keyTyped(KeyEvent e) {

	}

}
