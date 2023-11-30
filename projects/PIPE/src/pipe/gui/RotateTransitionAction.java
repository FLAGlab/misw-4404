package pipe.gui;

import java.awt.Container;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import pipe.common.dataLayer.Transition;

/**
 * @author tb403
 *
 */
public class RotateTransitionAction extends AbstractAction {
	private int angle;
	  private Container contentPane;
	  
	  private Transition selected;

	  public RotateTransitionAction (Container contentPane, Transition t, int a, String name) {
      super(name);
	    this.contentPane = contentPane;
      
	    selected = t;
	    angle = a;
	  }


	  public void actionPerformed(ActionEvent e){
	  	selected.rotate(angle);
	  }
	
	
	
}
