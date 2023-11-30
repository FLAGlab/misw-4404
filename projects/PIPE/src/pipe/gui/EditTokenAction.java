package pipe.gui;
import java.awt.Container;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import pipe.common.dataLayer.Place;

/**
 * Action object that can be used to add or remove tokens from a place
 * 
 * @author unknown 
 * 
 * @author Dave Patterson May 4, 2007: handle the cancel choice from the user
 * without an exception being thrown and caught. 
 */
public class EditTokenAction extends AbstractAction {

	private static final long serialVersionUID = 2002;
	private Container contentPane;
	private Place selected;

	public EditTokenAction(Container contentPane, Place place) 
	{
		this.contentPane = contentPane;
		selected = place;
	}

	/** Prompts the user for the number of tokens they want the place to have */
	public void actionPerformed(ActionEvent e) 
	{
		int currentMarking = selected.getCurrentMarking();
		String input = JOptionPane.showInputDialog("Number of tokens: ", 
				String.valueOf(currentMarking));
		if ( input == null )
		{
			return;			// user cancelled the change
		}
		
		try 
		{
			int newMarking = Integer.parseInt(input);
			if (newMarking < 0)
			{
				JOptionPane.showMessageDialog(contentPane,
					"Please enter a positive number.");
			}
			else
			{
				selected.setCurrentMarking(newMarking);
			}
		} 
		catch (Exception exc) 
		{
			JOptionPane.showMessageDialog(contentPane,"Please enter a number.",
					"Invalid entry",JOptionPane.ERROR_MESSAGE);
			System.err.println(exc.getMessage() );
		}
		selected.repaint();
	}

}		// end of method EditTokenAction