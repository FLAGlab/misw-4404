package pipe.gui;
import java.awt.Container;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import pipe.common.dataLayer.Place;
import pipe.common.dataLayer.Token;

/**
 * Action object that can be used to add or remove tokens from a place
 * 
 * @author unknown 
 * 
 * @author Dave Patterson May 4, 2007: handle the cancel choice from the user
 * without an exception being thrown and caught. 
 */
public class EditTaggedTokenAction extends AbstractAction {

	private static final long serialVersionUID = 2002;
	private Container contentPane;
	private Place selected_place;


	public EditTaggedTokenAction(Container contentPane, Place place) 
	{
		this.contentPane = contentPane;
		selected_place = place;
	}
	
	/** Prompts the user for the number of tokens they want the place to have */
	public void actionPerformed(ActionEvent e) 
	{
		//int currentMarking = selected.getCurrentMarking();
		boolean currentTagged_place = selected_place.isTagged();
	
		int currentMarking = selected_place.getCurrentMarking();
		
		selected_place.setTagged( ! currentTagged_place );
		selected_place.repaint();
		
		//add new tagged token
		if(!currentTagged_place){
			
			/*
			if (currentMarking == 0)
				selected_place.setCurrentMarking(1);
			*/
			
			//selected_place.setCurrentMarking(currentMarking+1);
	          
			
			//done so that rate for firing tagged token can be set
			CreateGui.currentPNMLData().setEnabledTransitions();
			
		}
		else{
				//currentMarking--;
	            //selected_place.setCurrentMarking(currentMarking);

		}
		
		CreateGui.currentPNMLData().setValidate(false);
		selected_place.repaint();

		
	}

}		// end of method EditTokenAction