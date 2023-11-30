/**
 * QueryStatusBar
 * 
 * - prints status messages to guide users on what to do
 * 
 * @author Tamas Suto
 * @date 15/05/07
 */

package pipe.modules.queryeditor.gui;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import pipe.common.QueryConstants;


public class QueryStatusBar extends JPanel implements QueryConstants {
	
  public String textforDrawing   = "Drawing Mode: Click on a button on the Query Builder on the left to start drawing components";
  public String textforNode      = "Node Mode: Right click on a node to see menu options";
  public String textforArc       = "Arc Mode: Right-Click on an Arc to add weighting";
  public String textforMove      = "Select Mode: Click/Drag to select objects; drag to move them";
  
  private JLabel label; 

  public QueryStatusBar(){
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
      case DRAW:
        changeText(textforDrawing);
        break;
      case SELECT:
          changeText(textforMove);
          break;
      case NODE:
        changeText(textforNode);
        break;
      case ARC:
        changeText(textforArc);
        break;      
      default:
        break;
    }
  }
  
}
