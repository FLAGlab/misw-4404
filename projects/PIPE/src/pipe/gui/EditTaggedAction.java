package pipe.gui;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import pipe.common.dataLayer.Arc;
import pipe.common.dataLayer.Transition;
/**
 * Action object that can be used to alternate an Arc between
 * tagged and non-tagged. 
 * 
 * @author Nick Dingle

 * 
 */
public class EditTaggedAction extends AbstractAction {

	private static final long serialVersionUID = 2001;
	private Container contentPane;
	private Arc selected;


	public EditTaggedAction(Container cP, Arc component) 
	{
		contentPane = cP;
		selected = component;
	}
  
	/** Action for toggling tagging on/off */
	public void actionPerformed(ActionEvent e) 
	{
		boolean currentTagged = selected.isTagged();
		int currentWeight = selected.getWeight(); 
		
		//if arc is not tagged, make it tagged
		//if transition connected to it got one output arc, make it tagged as well
		if(!currentTagged){
			//if it's an input arc
			if(selected.getTarget() instanceof Transition){
				final Transition tran = (Transition)selected.getTarget();
				Iterator arcFromTransitions = tran.getConnectFromIterator();
				int arcNum = 0;
				Arc arc=null;
				while(arcFromTransitions.hasNext())
		  		{
					arcNum++;
		  			arc = ((Arc)arcFromTransitions.next());		  				
		  		}
				if(arcNum==1 && arc!=null)arc.setTagged(true);
				
			}//it's an output arc
			else{
				
				final Transition tran = (Transition)selected.getSource();
				Iterator arcToTransitions = tran.getConnectToIterator();
				int arcNum = 0;
				Arc arc=null;
				while(arcToTransitions.hasNext())
		  		{
					arcNum++;
		  			arc = ((Arc)arcToTransitions.next());		  				
		  		}
				if(arcNum==1 && arc!=null)arc.setTagged(true);
				
			}
			
		}//else it is tagged
		else{
			
			//if it's an input arc
			if(selected.getTarget() instanceof Transition){
				final Transition tran = (Transition)selected.getTarget();
				Iterator arcFromTransitions = tran.getConnectFromIterator();
				int arcNum = 0;
				Arc arc=null;
				while(arcFromTransitions.hasNext())
		  		{
					arcNum++;
		  			arc = ((Arc)arcFromTransitions.next());		  				
		  		}
				if(arcNum==1 && arc!=null)arc.setTagged(false);
				
			}//it's an output arc
			else{
				
				final Transition tran = (Transition)selected.getSource();
				Iterator arcToTransitions = tran.getConnectToIterator();
				int arcNum = 0;
				Arc arc=null;
				while(arcToTransitions.hasNext())
		  		{
					arcNum++;
		  			arc = ((Arc)arcToTransitions.next());		  				
		  		}
				if(arcNum==1 && arc!=null)arc.setTagged(false);
				
			}
			
			
			
		}
		
		/*
		if(currentWeight>1)
			JOptionPane.showMessageDialog(contentPane, 
					"Arc weight is greater than 1 and so it cannot be tagged.");
		else {
			// if currentTagged it true, set it false, if false, set it true
			selected.setTagged( ! currentTagged );
		}
		*/
		//doesn't place restriction on multiplicity on tagged arcs
		selected.setTagged( ! currentTagged );
		CreateGui.getModel().setValidate(false);
		
	}
}		// end of class EditTImedAction