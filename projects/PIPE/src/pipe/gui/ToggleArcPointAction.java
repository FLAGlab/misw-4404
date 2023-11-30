//######################################################################################
/*
 * Created on 04-Mar-2004
 * Author is Michael Camacho
 *
 */
//######################################################################################
package pipe.gui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import pipe.common.dataLayer.ArcPathPoint;

//######################################################################################
public class ToggleArcPointAction extends AbstractAction {
//######################################################################################	
	  private ArcPathPoint selected;
//######################################################################################
	  public ToggleArcPointAction(ArcPathPoint component) {
	    selected = component;
	  }		
//######################################################################################	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		selected.togglePointType();		
	}
//######################################################################################
}
//######################################################################################