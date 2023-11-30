//######################################################################################
/**
 * Created on 07-Mar-2004
 * @author Michael Camacho
 *
 * @author Pere Bonet resolved a repainting issue during the enabling and
 * disenabling of the annotation border 
 */
//######################################################################################
package pipe.gui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import pipe.common.dataLayer.AnnotationNote;

public class EditAnnotationBorderAction extends AbstractAction {

	private AnnotationNote selected;
	

	public EditAnnotationBorderAction(AnnotationNote component) {
		selected = component;
	}

	/**
	 * Action for editing the border of an AnnotationNote
	 */
	public void actionPerformed(ActionEvent e) {
		selected.showBorder(!selected.isShowingBorder());
		selected.repaint();
	}
}
