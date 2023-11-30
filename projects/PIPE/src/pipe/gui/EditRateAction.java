package pipe.gui;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import pipe.common.dataLayer.Transition;

/**
 * Action object that can be used to add a rate to a transition.
 * 
 * @author unknown
 * 
 * @author Dave Patterson May 3, 2007: Fixed code to handle user cancel of the
 * change to the transition rate. 
 */
public class EditRateAction extends AbstractAction {

	private static final long serialVersionUID = 2000;
	private Container contentPane;
	private Transition selected;
	//private Checkbox checkbox;

	public EditRateAction(Container contentPane, Component component) 
	{
		this.contentPane = contentPane;		
		selected = (Transition)component;
		//checkbox = new Checkbox("Infinite Server", selected.isInfiniteServer());
	}

	/** Action changing an attribute of the transition */
	public void actionPerformed(ActionEvent e) 
	{
		double currentRate = selected.getRate();
		String input = null;
		
		if (selected.isTimed() )
			input = JOptionPane.showInputDialog("Rate: ", 
					String.valueOf(currentRate));
		else
			input = JOptionPane.showInputDialog("Weight: ", 
					String.valueOf(currentRate));
		
		if ( input == null )		// if user cancels the action
		{
			return;
		}
		try 
		{
			double newRate = Double.parseDouble(input);
			if (newRate < 0)
			{
				JOptionPane.showMessageDialog(contentPane,
					"Please enter a positive real number.");
			}
			else
			{
				selected.setRate(newRate);
			}
		}
		catch( NumberFormatException nfe )
		{
			JOptionPane.showMessageDialog(contentPane,"Please enter a real number.",
        		"Invalid entry", JOptionPane.ERROR_MESSAGE);
		}
		catch (Exception exc) 
		{
			System.err.println(exc.toString());
			JOptionPane.showMessageDialog(contentPane,
				"Please enter a real number.",
        		"Invalid entry", JOptionPane.ERROR_MESSAGE);
		}
	}		// end of method actionPerformed

}		// end of class EditRateAction