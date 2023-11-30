/**
 * QueryAction
 * 
 * - Handles loading icon based on action name and setting up other stuff
 * 
 * @author Tamas Suto
 * @date 14/05/07
 */

package pipe.modules.queryeditor.gui;


import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import pipe.modules.queryeditor.QueryManager;


public abstract class QueryAction extends AbstractAction {
	
	public QueryAction (String name, String tooltip, String keystroke) {
		super(name);
		URL iconURL = Thread.currentThread().getContextClassLoader().getResource(QueryManager.imgPath + name + ".png");
		URL selectedIconURL = Thread.currentThread().getContextClassLoader().getResource(QueryManager.imgPath + name + "-selected.png");		
		if (iconURL != null){
			putValue(SMALL_ICON, new ImageIcon(iconURL));
		}	
		if(tooltip != null)
			putValue(SHORT_DESCRIPTION, tooltip);
		if(keystroke != null)
			putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(keystroke));
		
		putValue("selected",new Boolean(false));
		putValue("selectedIconURL",selectedIconURL);	
	}	
	
	
	public boolean isSelected(){
		return ((Boolean)getValue("selected")).booleanValue();
	}
	
	public void setSelected(boolean selected){
		putValue("selected",new Boolean(selected));
	}
}
