//######################################################################################
/*
 * Created on 06-Mar-2004
 * Author is Michael Camacho
 *
 */
//######################################################################################
package pipe.gui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import pipe.common.dataLayer.AnnotationNote;
//######################################################################################
public class EditAnnotationAction extends AbstractAction {
//######################################################################################
	private AnnotationNote selected;
	
//######################################################################################
	public EditAnnotationAction(AnnotationNote component) {
		selected = component;
	}
//######################################################################################
	/** Action for editing the text in an AnnotationNote */
	public void actionPerformed(ActionEvent e) {
		selected.enableEditMode();
	}
//######################################################################################
}
//######################################################################################