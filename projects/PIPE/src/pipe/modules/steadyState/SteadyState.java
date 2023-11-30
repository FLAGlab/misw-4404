package pipe.modules.steadyState;



/**
 * Steady State Analysis module.
 * @author Barry Kearns 
 */

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import pipe.common.PerformanceMeasure;
import pipe.common.dataLayer.DataLayer;
import pipe.common.dataLayer.Place;
import pipe.common.dataLayer.Transition;
import pipe.gui.CreateGui;
import pipe.gui.widgets.ButtonBar;
import pipe.gui.widgets.ResultsHTMLPane;
import pipe.modules.Module;
import pipe.modules.clientCommon.HTMLPane;
import pipe.modules.clientCommon.ServerInfo;
import pipe.modules.clientCommon.ServerPanel;


public class SteadyState implements Module
{
	
	private static final String MODULE_NAME = "Steady State Analysis";
	
	private DataLayer pnmlData;		// Petri Net to be analysed

  
	public SteadyState() {}

	/**  The module name  */
	public String getName()	{
    return MODULE_NAME;
	}
 
  
  // Main GUI elements
  private JDialog guiDialog;
  private JTabbedPane tabbedPane;
  
  
  private FileBrowserPanel sourceFilePanel;

  private HTMLPane progressPane = null;
  private ResultsHTMLPane resultsPane = null;
  private ServerPanel serverPanel = null;

  
  // JList for place names
  JList placesList, transitionList;
  JCheckBox meanStateCBx, varianceStateCBx, stddevStateCBx, distrStateCBx;
  
  
  
  public void run (DataLayer pnmlDataIn)
  {
   	pnmlData = pnmlDataIn;
  
  	// Create tabbed pane
  	tabbedPane = new JTabbedPane();

  	
    // Build interface
    guiDialog = new JDialog(CreateGui.getApp(),MODULE_NAME,true);
       
    Container contentPane=guiDialog.getContentPane();
    contentPane.setLayout(new BoxLayout(contentPane,BoxLayout.PAGE_AXIS));
    
    // 1 Set layout
  	JPanel setupPanel = new JPanel();
    setupPanel.setLayout(new BoxLayout(setupPanel,BoxLayout.PAGE_AXIS));
    
    // 2 Add file browser
    sourceFilePanel=new FileBrowserPanel("Source net",pnmlData);        
    setupPanel.add(sourceFilePanel);
   
    // 3 Add Server selection panel 	
    serverPanel = new ServerPanel(guiDialog);
    setupPanel.add(serverPanel.getPanel() );
	
	// 4 Add State Measure panel
    setupPanel.add(getStateMeasurePanel() );
    
    // 5 Add Count Measure panel
    setupPanel.add(getCountMeasurePanel() );    

    // 6 Add Analyse button
    setupPanel.add(new ButtonBar("Analyse",analyseButtonClick));
    
    
    // 7 Add setup panel to tabbed pane, add tabbed pane to guiDialog
    tabbedPane.addTab("Setup", setupPanel);    
    contentPane.add(tabbedPane);
    
    
    // 8 Make window fit contents' preferred size, centre on screen, display
    guiDialog.pack();
    guiDialog.setLocationRelativeTo(null);    
    guiDialog.setVisible(true);

  }

  ActionListener analyseButtonClick = new ActionListener()
  {
	    public void actionPerformed(ActionEvent arg0)
	    {
	    	// Returns reference to either current / selected P-N      
	    	pnmlData = sourceFilePanel.getDataLayer();
	    		      
	    	// Returns the currently selected server (-1 if none selected)
	    	int selectedServer = serverPanel.getSelectedServerIndex();   	
	    
	    	// Returns the selected places / transitions for analysis
	    	PerformanceMeasure selectedMeasures = getSelectedEstimators();
	    		    	
	    	// Create Progress Tab and set it as selected	
	    	if (progressPane == null)
	    	{
	    		progressPane = new HTMLPane("Analysis Progress");
	    		tabbedPane.addTab("Progress", progressPane);
	    	}
	    	
	    	if (resultsPane == null)
	    		resultsPane = new ResultsHTMLPane(pnmlData.getURI());
	    	

	    	
	    	tabbedPane.setSelectedComponent(progressPane);
	    		    	
	    	String statusMesg="<h2>Steady State Analysis</h2>";
	      	
	    	
	    	
	    	if(pnmlData==null) return;
	      
	    	else if(!pnmlData.getPetriNetObjects().hasNext())
	    		statusMesg+="No Petri net objects defined!";
	      
	    	else if (selectedServer == -1)
		    	statusMesg+="No server selected!";
	    	
	    	else if (selectedMeasures.getStatesSize() > 0 && selectedMeasures.getEstimatorsSize() < 1)
	    		statusMesg+="States selected but no estimator(s) choosen"; 
		   	          
	    	else
	    	{
	    		ServerInfo serverInfo= serverPanel.getSelectedServer();
	    		
	    		Analyse analyse = new Analyse(pnmlData, progressPane, resultsPane);
	    		analyse.setServer(serverInfo.getAddress(), serverInfo.getPort());	    		
	    		analyse.setStateMeasure(selectedMeasures);
	    		analyse.setTabbedPane(tabbedPane);	    		
	    		
	    		
	    		// Start Analyse thread
	    		Thread analyseTrd = new Thread(analyse);    		
	    		analyseTrd.start();
	    		return;
	    	}
	      
	      progressPane.setText(statusMesg);	      
	    }
	  };
	  

	  /**
	   * This method reads the UI selections (State / Count JLists and Checkboxes)
	   * and produces a Performance measure object containing these values
	   * @return The Performance measure object corresponding to the UI selections
	   */
	  public PerformanceMeasure getSelectedEstimators()
	  {	
		String name;
		Place currPlace;
		Transition currTrans;
		PerformanceMeasure performanceMeasure = new PerformanceMeasure();    	
		
		// 1. Get State Measure information
		
		// Retrieve the names of selected state measures 
    	Object[] selectedStates = placesList.getSelectedValues();
    	
    	// Convert the place names into IDs, and add to output
    	for(int i=0; i<selectedStates.length; i++)
		{
    		name = (String)selectedStates[i];
    		currPlace = pnmlData.getPlaceByName(name);
    		performanceMeasure.addState(currPlace.getId() );
		}		
		
		// Add the set of selected state estimator checkboxes
		if (meanStateCBx.isSelected())
			performanceMeasure.addStateEstimator("mean");

		if (varianceStateCBx.isSelected())
			performanceMeasure.addStateEstimator("variance");

		if (stddevStateCBx.isSelected())
			performanceMeasure.addStateEstimator("stddev");
		
		if (distrStateCBx.isSelected())
			performanceMeasure.addStateEstimator("distribution");
		
		
		// 2. Get Count Measure information		
		
		// Retrieve the names of selected count measures 
    	Object[] selectedCounts = transitionList.getSelectedValues();
    	
    	// Convert the transition names into IDs, and add to output
    	for(int i=0; i<selectedCounts.length; i++)
		{
    		name = (String)selectedCounts[i];
    		currTrans = pnmlData.getTransitionByName(name);
    		performanceMeasure.addCount(currTrans.getId() );			  
		}		
		
		// 3. Return the resulting stateMeaure
		return performanceMeasure;
		  
	  }
  
	  // Panel for State Measure
	  private JPanel getStateMeasurePanel()
	  {
		    JPanel serverPanel = new JPanel();
			serverPanel.setBorder((new TitledBorder(new EtchedBorder(),"State Measure")) );
			serverPanel.setLayout(new BorderLayout());
			
			placesList = new JList();
			// Load the list of place names
			sourceFilePanel.setPlaceList(placesList);
			
			placesList.setLayoutOrientation(JList.VERTICAL);
			placesList.setSelectionModel(new ToggleSelectionModel());
			placesList.setVisibleRowCount(-1);
			
						
			JScrollPane listScroller = new JScrollPane(placesList);
			listScroller.setPreferredSize(new Dimension(250, 160));
			
			
			// Create the estimator check boxes
			meanStateCBx = new JCheckBox("Mean");
			varianceStateCBx = new JCheckBox("Variance");
			stddevStateCBx = new JCheckBox("Standard Deviation");
			distrStateCBx = new JCheckBox("Distribution");
			
			// Create a panel to group the checkboxes
			JPanel checkboxPanel = new JPanel();
			//checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS));
			checkboxPanel.add(meanStateCBx);
			checkboxPanel.add(stddevStateCBx);
			checkboxPanel.add(varianceStateCBx);
			checkboxPanel.add(distrStateCBx);
						
			
			// Add components to panel
			serverPanel.add(listScroller, BorderLayout.CENTER);
			serverPanel.add(checkboxPanel, BorderLayout.SOUTH);
			
			return serverPanel;
	  }	
	 
	  // Panel for Count Measure
	  private JPanel getCountMeasurePanel()
	  {
		    JPanel serverPanel = new JPanel();
			serverPanel.setBorder((new TitledBorder(new EtchedBorder(),"Count Measure")) );
			serverPanel.setLayout(new BorderLayout());
			
			transitionList = new JList();
			// Load the list of transition names
			sourceFilePanel.setTransitionList(transitionList);
			
			transitionList.setLayoutOrientation(JList.VERTICAL);
			transitionList.setSelectionModel(new ToggleSelectionModel());
			
			transitionList.setVisibleRowCount(-1);
			
						
			JScrollPane listScroller = new JScrollPane(transitionList);
			listScroller.setPreferredSize(new Dimension(250, 160));
			
			
			// Add components to panel
			serverPanel.add(listScroller, BorderLayout.CENTER);
		
			return serverPanel;
	  }	 
}
	  


// This class allows a JList to operate in a click toggle fashion - see JList java doc
class ToggleSelectionModel extends DefaultListSelectionModel
{
	private static final long serialVersionUID = 1L;
	boolean gestureStarted = false;
    
    public void setSelectionInterval(int index0, int index1)
    {
    	if (isSelectedIndex(index0) && !gestureStarted)
    	{
    		super.removeSelectionInterval(index0, index1);
    	}
    	else
    	{
    		super.setSelectionInterval(index0, index1);
    	}
    	
    	gestureStarted = true;
    }

    public void setValueIsAdjusting(boolean isAdjusting)
    {
    	if (isAdjusting == false)
    	{
    		gestureStarted = false;
    	}
    }
}
