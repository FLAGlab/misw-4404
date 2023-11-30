/**
 * State Space Module
 * @author James D Bloom (UI) & Clare Clark (Maths)
 * @author Maxim (better UI)
 */

package pipe.modules.stateSpace;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JDialog;

import pipe.common.dataLayer.DataLayer;
import pipe.common.dataLayer.PNMatrix;
import pipe.dataLayer.calculations.TreeTooBigException;
import pipe.dataLayer.calculations.myTree;
import pipe.gui.CreateGui;
import pipe.gui.widgets.ButtonBar;
import pipe.gui.widgets.PetriNetChooserPanel;
import pipe.gui.widgets.ResultsHTMLPane;
import pipe.modules.Module;


public class StateSpace implements Module {

  // Main Frame
  private static final String MODULE_NAME = "State Space Analysis";
  
  private PetriNetChooserPanel sourceFilePanel;
  private ResultsHTMLPane results;

  public void run(DataLayer pnmlData) {
    // Build interface
    JDialog guiDialog = new JDialog(CreateGui.getApp(),MODULE_NAME,true);
    
    // 1 Set layout
    Container contentPane=guiDialog.getContentPane();
    contentPane.setLayout(new BoxLayout(contentPane,BoxLayout.PAGE_AXIS));
    
    // 2 Add file browser
    contentPane.add(sourceFilePanel=new PetriNetChooserPanel("Source net",pnmlData));
    
    // 3 Add results pane
    contentPane.add(results=new ResultsHTMLPane(pnmlData.getURI()));
    
    // 4 Add button
    contentPane.add(new ButtonBar("Analyse",analyseButtonClick));   

    // 5 Make window fit contents' preferred size
    guiDialog.pack();
    
    // 6 Move window to the middle of the screen
    guiDialog.setLocationRelativeTo(null);
    
    guiDialog.setVisible(true);
    
    
//    warnUser(pnmlData.getURI(), guiFrame);
//    StateSpace stateSpace = new StateSpace(pnmlData);
  }

  public String getName() {
    return MODULE_NAME;
  }

  /**
   * Analyse button click handler
   */
  ActionListener analyseButtonClick=new ActionListener() {
    public void actionPerformed(ActionEvent arg0) {
      DataLayer sourceDataLayer=sourceFilePanel.getDataLayer();
      
      int[] markup = sourceDataLayer.getCurrentMarkingVector();
      int[][] forwards = sourceDataLayer.getForwardsIncidenceMatrix();
      int[][] backwards = sourceDataLayer.getBackwardsIncidenceMatrix();
      myTree tree = null;
      
      String s="<h2>Petri net state space analysis results</h2>";
	  if((sourceDataLayer==null)||!sourceDataLayer.getPetriNetObjects().hasNext()) s+="No Petri net objects defined!";
	  else if(markup!=null && forwards!=null && backwards!=null) 
	  
	  try {
	  	tree = new myTree(markup, new PNMatrix(forwards), new PNMatrix(backwards));
	    boolean bounded = !tree.Found_An_Omega;
	    boolean safe = !tree.more_Than_One_Token;
	    boolean deadlock = tree.no_Enabled_Transitions;
	    if (tree.tooBig) s+="<div class=warning>State space tree expansion aborted because it grew too large. Results will be incomplete.</div>";
	        
	    s+=ResultsHTMLPane.makeTable(new String[]{
	            "Bounded" ,""+bounded,
	            "Safe"    ,""+safe,
	            "Deadlock",""+deadlock
	           },2,false,true,false,true);
	        
	        if(deadlock) {
	          s+="<b>Shortest path to deadlock:</b> ";
	          if(tree.pathToDeadlock.length==0) s+="Initial state is deadlocked";
	          else
	          for(int i=0;i<tree.pathToDeadlock.length; i++)
	          if(sourceDataLayer.getTransition(tree.pathToDeadlock[i]-1)!=null &&
	             sourceDataLayer.getTransition(tree.pathToDeadlock[i]-1).getName()!=null) {
	            s+=sourceDataLayer.getTransition(tree.pathToDeadlock[i]-1).getName()+" ";
	          }
	        }
	      	} catch (TreeTooBigException e){
	      	s+= e.getMessage();
	      	}
	      	
	      
	      	else {
        s+="Error performing analysis";
      }
      
      results.setText(s);
    }
  };
}