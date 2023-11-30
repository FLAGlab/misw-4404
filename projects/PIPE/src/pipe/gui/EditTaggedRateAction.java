package pipe.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import pipe.common.dataLayer.Transition;




public class EditTaggedRateAction extends AbstractAction {

	private static final long serialVersionUID = 2000;
	private Container contentPane;
	private Transition selected;
	

	public EditTaggedRateAction(Container contentPane, Component component) 
	{
		this.contentPane = contentPane;		
		selected = (Transition)component;
		
	}

	/** Action changing an attribute of the transition */
	public void actionPerformed(ActionEvent e) 
	{
		double currentTaggedRate = selected.getRateTagged();
		String input = null;
		
		
			input = JOptionPane.showInputDialog("Rate to Fire Tagged token: ", 
					String.valueOf(currentTaggedRate));
	
		
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
				selected.setRateTagged(newRate);
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