package pipe.gui;
import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

/* Status Bar to let users know what to do*/
public class StatusBar extends JPanel implements Constants{
  /* Provides the appropriate text for the mode that the user is in */
  public String textforDrawing   = "Drawing Mode: Click on a button to start adding components to the Editor";
  public String textforPlace     = "Place Mode: Right click on a Place to see menu options";
  public String textforTrans     = "Immediate Transition Mode: Double-Click on a Transition to rotate & right click for menu";
  public String textforTimedTrans= "Timed Transition Mode: Double-Click on a Transition to rotate & right click for menu";
  public String textforAddtoken  = "Add Token Mode: Click on a Place to add a Token";
  public String textforDeltoken  = "Delete Token Mode: Click on a Place to delete a Token ";
  public String textforAnimation = "Animation Mode: Red transitions are enabled, click a transition to fire it";
  public String textforArc       = "Arc Mode: Right-Click on an Arc to add weighting";
  public String textforMove      = "Select Mode: Click/drag to select objects; drag to move them";
  
  private JLabel label; 

  public StatusBar(){
    super();
    label = new JLabel(" ");
    this.setLayout(new BorderLayout(0,0));
    this.add(label);
  }

  public void changeText(String newText){
    label.setText(newText);
  }

  public void changeText(int type ){
    switch(type){
      case PLACE:
        changeText(textforPlace);
        break;

      case IMMTRANS:
        changeText(textforTrans);
        break;

      case TIMEDTRANS:
        changeText(textforTimedTrans);
        break;

      case ARC:
        changeText(textforArc);
        break;

      case ADDTOKEN:
        changeText(textforAddtoken);
        break;

      case DELTOKEN:
        changeText(textforDeltoken);
        break;

      case SELECT:
        changeText(textforMove);
        break;

      case DRAW:
        changeText(textforDrawing);
        break;
      default:
        break;
    }
  }
}