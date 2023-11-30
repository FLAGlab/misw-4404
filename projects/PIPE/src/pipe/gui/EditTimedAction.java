package pipe.gui;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import pipe.common.dataLayer.Transition;

/**
 * Action object that can be used to alternate a transition between
 * timed and immediate. 
 * 
 * @author unknown 
 * 
 * @author Dave Patterson May 3, 2007: simplify the logic of setting the value
 * 
 */
public class EditTimedAction extends AbstractAction {

	private static final long serialVersionUID = 2001;

	private Transition selected;


	public EditTimedAction(Transition component) 
	{
		selected = component;
	}
  
	/** Action for toggling timing on/off */
	public void actionPerformed(ActionEvent e) 
	{
		boolean currentTimed = selected.isTimed();
		// if currentTimed it true, set it false, if false, set it true
		selected.setTimed( ! currentTimed );
	}
}		// end of class EditTImedAction