package pipe.gui;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import pipe.common.dataLayer.PetriNetObject;


/**
 * Action object that can be used to effect labelling a component.
 */
public class LabelAction extends AbstractAction {
  private String label;
  private Container contentPane;
  private PetriNetObject selected;


  public LabelAction(Container contentPane, Component component) {
    this.contentPane = contentPane;
    selected = (PetriNetObject)component;
  }

  /** Prompts the user for a label */
  public void actionPerformed(ActionEvent e) {

    // this isn't quite working to plan- might have to extend the bounds of the component for the text to bet visible, but this might adversely effect the arc snapping which may use the getBounds method
    String text = "";
    String currentName = selected.getName();
    if ((currentName != null) && (currentName.length() > 0))
      text = currentName;
    String input = JOptionPane.showInputDialog("Edit label", text);
    if (input != null)
    	selected.setName(input);

  }

}


