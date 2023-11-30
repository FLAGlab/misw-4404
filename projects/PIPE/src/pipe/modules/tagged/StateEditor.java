package pipe.modules.tagged;


import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import pipe.common.dataLayer.DataLayer;
import pipe.common.dataLayer.StateElement;
import pipe.common.dataLayer.StateGroup;
import pipe.gui.CreateGui;

/**
 * This class provides the interface for building and editing state groups.
 *  
 * 
 * @author Barry Kearns
 * @date August 2007 
 */


public class StateEditor extends JDialog
{

	private static final long serialVersionUID = 1L;

	DataLayer appModel;
	StateViewer viewer;
	ArrayList<StateElement> changeBuffer;
	StateGroup activeStateGroup;
	
	
	// Required to update 'Passage' with any new groups
	TaggedModule parent =null;
	boolean newStateGroup = false;
	
	JDialog stateDialog;
	JTextField stateNameTextField;
	JButton saveStateGroupBtn, cancelBtn;
	JButton initialStateBtn, allZeroBtn, clearStatesBtn;
	
	
	public void addState(DataLayer pnmlData)
	{
		appModel = pnmlData;
		activeStateGroup = new StateGroup(pnmlData);
		newStateGroup = true;
		init();
	}
	
	public void editState(DataLayer pnmlData, StateGroup editStateGroup)
	{
		appModel = pnmlData;
		activeStateGroup = editStateGroup;
		init();
	}
	
	public void setParent(TaggedModule parentRef)
	{
		parent = parentRef;
	}
	

	private void init()
	{			
		changeBuffer = new ArrayList<StateElement>();
		
		stateDialog = new JDialog(CreateGui.getApp(),"State Editor",true);
		
		Container stateContainer = stateDialog.getContentPane();

		
		JPanel stateViewPanel = new JPanel();
		
		
		stateViewPanel.setBorder((new TitledBorder(new EtchedBorder(),"State Editor")) );
		stateViewPanel.setLayout(new BorderLayout());
		
		
		// Create a panel for the state's title to be entered
		JPanel stateNamePanel = new JPanel();

		JLabel stateNameLabel = new JLabel("Title:");
		stateNameTextField= new JTextField(20);
		stateNameTextField.setText(activeStateGroup.getName());
		
		stateNamePanel.add(stateNameLabel);
		stateNamePanel.add(stateNameTextField);
		
		// Save buttons
		saveStateGroupBtn = new JButton("Save State Group");
		saveStateGroupBtn.addActionListener(saveState);
			
		cancelBtn = new JButton("Cancel");
		cancelBtn.addActionListener(saveState);		
		
		JPanel saveButtonPanel = new JPanel();
		saveButtonPanel.add(saveStateGroupBtn);
		saveButtonPanel.add(cancelBtn);
		
		// Create the top panel
		JPanel topPanel = new JPanel();
		topPanel.add(stateNamePanel);
		topPanel.add(saveButtonPanel);
		
		
		//	Main state editor
		viewer = new StateViewer();
		viewer.setParent(this);
		viewer.drawPetriNet(appModel, activeStateGroup);
		
		
		JScrollPane viewPanel = new JScrollPane(viewer);
		viewPanel.setBorder((new TitledBorder(new EtchedBorder(),"Click on individual places to set up conditions that uniquely identify the state")) );
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int viewPanelPreferredWidth = screenSize.width*60/100;
		int viewPanelPreferredHeight = screenSize.height*60/100;
		Dimension viewPanelPreferredSize = new Dimension(viewPanelPreferredWidth, viewPanelPreferredHeight);
		viewPanel.setPreferredSize(viewPanelPreferredSize);
		

		
		// Create auto state group button panel
		JPanel autoConfigPanel = new JPanel();
		autoConfigPanel.setBorder((new TitledBorder(new EtchedBorder(),"Automatic Configurations")));
		
		// Auto configuration buttons
		initialStateBtn = new JButton("Initial Marking");		
		allZeroBtn = new JButton("All Equal Zero");
		clearStatesBtn = new JButton("Clear All");
		
		// Add action listeners
		initialStateBtn.addActionListener(autoSetState);
		allZeroBtn.addActionListener(autoSetState);
		clearStatesBtn.addActionListener(autoSetState);
		
		// Add to panel
		autoConfigPanel.add(initialStateBtn);
		autoConfigPanel.add(allZeroBtn);
		autoConfigPanel.add(clearStatesBtn);
		
		
		// Add the panels to the main stateView Panel
		stateViewPanel.add(topPanel, BorderLayout.PAGE_START);
		stateViewPanel.add(viewPanel, BorderLayout.CENTER);
		stateViewPanel.add(autoConfigPanel, BorderLayout.PAGE_END);
		
		
		stateContainer.add(stateViewPanel);
		stateDialog.pack();
		stateDialog.setLocationRelativeTo(null);
		
		stateDialog.setVisible(true);		
	}
	
	// This method adds to the changes buffer, which is committed when the state group is saved
	public void addStateElement(String placeA, String operator, String placeB)
	{
		// Convert equals to double equals for comparison
		if (operator.equals("=" ))
			operator = "==";
		
		// Convert from unicode character to ASCII
		else if (operator.equals("\u2264" ) )
			operator = "<=";
		else if (operator.equals("\u2265"))
			operator = ">=";
				
		
		changeBuffer.add(new StateElement(placeA, operator, placeB));
	}
	
	// This method applies the changes that occurs in the change buffer
	private void applyChanges()
	{
		// Set the title
		activeStateGroup.setName(stateNameTextField.getText());
		
		// Apply each of the changes made to the state group 
		for(StateElement currElement : changeBuffer)
			activeStateGroup.addState(currElement);
		
		
		// If a new state group, add it to the state group arrayList in DataLayer
		if (newStateGroup)
		{
			appModel.addStateGroup(activeStateGroup);

		}	
		
		// Inform CreateGui that the model has been modified
		CreateGui.getView().netChanged = true;
	}
	
	ActionListener saveState = new ActionListener()
	{
		 public void actionPerformed(ActionEvent event)
		 {
			 if (event.getSource() == saveStateGroupBtn)
			 {
				 if (stateNameTextField.getText().equals(""))
					 JOptionPane.showMessageDialog(null, "Please enter a title for this state");
				 
				 else
				 {
					applyChanges();

					if (parent != null)
						parent.updateStateLists();

					closeWindow();
				 }
				 			 
			 }
			 
			 else if (event.getSource() == cancelBtn)
			 	 closeWindow();			 
		 }
		 
		 private void closeWindow()
		 {
			 stateDialog.setVisible(false);
			 stateDialog.dispose();		 
		 }
	};
	
	
	ActionListener autoSetState = new ActionListener()
	{
		 public void actionPerformed(ActionEvent event)
		 {
			 if (event.getSource() == initialStateBtn)
			 	 viewer.setInitialCond();
			 
			 else if (event.getSource() == allZeroBtn)
				 viewer.setEqualZeroCond();
			 
			 else if (event.getSource() == clearStatesBtn)
			 	 viewer.clearAllCond();
			 	
		 }
	};
	
		 
}
