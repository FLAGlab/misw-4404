/*
 * Created on 07-Mar-2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package pipe.gui;

import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;


/**
 * GuiAction class
 * @author Maxim and others
 *
 * Handles loading icon based on action name and setting up other stuff
 *  
 */
public abstract class GuiAction extends AbstractAction {
	GuiAction (String name, String tooltip, String keystroke) {
		super(name);
		URL iconURL = Thread.currentThread().getContextClassLoader(
				).getResource(CreateGui.imgPath + name + ".png");
		if (iconURL != null){
			putValue(SMALL_ICON, new ImageIcon(iconURL));
		}
		
		if(tooltip != null)
			putValue(SHORT_DESCRIPTION, tooltip);
		
		if(keystroke != null)
			putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(keystroke));
		putValue("selected",new Boolean(false));
	}	
	
	public boolean isSelected(){
		return ((Boolean)getValue("selected")).booleanValue();
	}
	
	public void setSelected(boolean selected){
		putValue("selected",new Boolean(selected));
	}
}