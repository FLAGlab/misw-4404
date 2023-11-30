package pipe.gui;


import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;

import pipe.common.dataLayer.Transition;;

public class FireTaggedTokenAction extends MouseAdapter {
	private static final long serialVersionUID = 2001;
	private Transition selected;


	public FireTaggedTokenAction(Transition component) 
	{
		selected = component;
	}
  
	
	/* this was done for right click on pop-up menu to fire tagged token
	
	// Action for toggling tagging on/off 
	public void actionPerformed(ActionEvent e) 
	{
		
		
		if(selected.isTaggedTokenEnabled()){
			
			System.out.println("\n****in fireTaggedAction taggedTokenEnabled");
			CreateGui.getAnimationHistory().clearStepsForward();
	        CreateGui.getAnimator().fireTaggedTransition(selected);
	        CreateGui.getApp().setRandomAnimationMode(false);
			
		}
		
	}
	
	*/
	
	
	public void mouseClicked(MouseEvent e) 
	{
		
		//if(selected.isTaggedTokenEnabled() && SwingUtilities.isRightMouseButton(e)){
		
		
		if(selected.isTaggedTokenEnabled() && SwingUtilities.isLeftMouseButton(e))
		{	
			//System.out.println("\n****in fireTaggedAction taggedTokenEnabled");
			CreateGui.getAnimationHistory().clearStepsForward();
	        CreateGui.getAnimator().fireTaggedTransition(selected);
	        CreateGui.getApp().setRandomAnimationMode(false);
			
		}
		
	}
	
}		

