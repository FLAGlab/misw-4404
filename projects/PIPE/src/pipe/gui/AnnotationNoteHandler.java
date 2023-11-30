//######################################################################################
/*
 * Created on 05-Mar-2004
 * Author is Michael Camacho
 *
 */
//######################################################################################
package pipe.gui;

import java.awt.Container;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import pipe.common.dataLayer.AnnotationNote;

public class AnnotationNoteHandler extends PetriNetObjectHandler {

	public AnnotationNoteHandler(Container contentpane, AnnotationNote note) {
		super(contentpane, note);
		enablePopup = true;
	}

	/**
	 *  Creates the popup menu that the user will see when they right
	 *  click on a component
	 */
	public JPopupMenu getPopup(MouseEvent e)
	{
		JPopupMenu popup = super.getPopup(e);	

		JMenuItem menuItem =
			new JMenuItem(new EditAnnotationAction((AnnotationNote)myObject));
		menuItem.setText("Edit text");
		popup.add(menuItem);

		menuItem =
			new JMenuItem(new EditAnnotationBorderAction((AnnotationNote)myObject));
		if (((AnnotationNote)myObject).isShowingBorder())
			menuItem.setText("Disable Border");
		else
			menuItem.setText("Enable Border");
		popup.add(menuItem);

		menuItem = new JMenuItem(
				new EditAnnotationBackgroundAction((AnnotationNote)myObject));
		if (((AnnotationNote)myObject).isFilled()){
			menuItem.setText("Transparent");
		} else {
			menuItem.setText("Solid Background");
		}
		popup.add(menuItem);
		
		return popup;
	}

	public void mousePressed(MouseEvent e)
	{
		if ((e.getComponent() == myObject) || !e.getComponent().isEnabled())
			super.mousePressed(e);
	}

	public void mouseDragged(MouseEvent e) {

		if ((e.getComponent() == myObject) || !e.getComponent().isEnabled())
			super.mouseDragged(e);
	}

	public void mouseReleased(MouseEvent e) {
		if ((e.getComponent() == myObject) || !e.getComponent().isEnabled())
			super.mouseReleased(e);
	}

	public void mouseClicked(MouseEvent e) {
		if ((e.getComponent() == myObject) || !e.getComponent().isEnabled()){
			if ((e.getButton() == 1) && (e.getClickCount() == 2)){
				((AnnotationNote)myObject).enableEditMode();
			}
		}
	}
}
