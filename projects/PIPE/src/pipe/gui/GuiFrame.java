package pipe.gui;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import pipe.common.dataLayer.DataLayer;
import pipe.common.dataLayer.DataLayerWriter;
import pipe.common.dataLayer.PNMLTransformer;
import pipe.gui.widgets.FileBrowser;
import pipe.modules.clientCommon.HTMLPane;



/** @author Edwin Chung changed the code so that the firedTransitions array list is
 *  reset when the animation mode is turned off
 *  
 *  @author Ben Kirby, 10 Feb 2007: Changed the saveNet() method so that it calls new 
 *  DataLayerWriter class and passes in current net to save.
 *  
 *  @author Ben Kirby, 10 Feb 2007: Changed the createNewTab method so that it loads an 
 *  XML file using the new PNMLTransformer class and createFromPNML DataLayer method.
 *  
 *   @author Edwin Chung modifed the createNewTab method so that it assigns the file name
 *   of the newly created DataLayer object in the dataLayer class (Mar 2007) 
 *   @author Oliver Haggarty modified initaliseActions to fix a bug that meant 
 *   not all example nets were loaded if there was a non .xml file in the folder
 */

public class GuiFrame extends JFrame implements Constants, ActionListener, Observer{

	// HAK
	
	// for zoom combobox and dropdown
	private final String[] zoomExamples={"40%","60%","80%","100%","120%","140%","160%","180%","200%","300%","350%","400%"}; 
	
	private String frameTitle;  //Frame title
	private DataLayer appModel;
	private GuiFrame appGui;
	private GuiView appView;
	private int mode, prev_mode;				// *** mode WAS STATIC ***
	private int newNameCounter = 1;
	private JTabbedPane appTab;
	private StatusBar statusBar;
	private JMenuBar menuBar;
	private Map actions = new HashMap();
	private JComboBox zoomComboBox;

	public GuiFrame(String title) {
		// HAK-arrange for frameTitle to be initialized and the default file name to be appended to basic window title
		frameTitle = title;
		setTitle(null);

		// Look & Feel
		String osName = System.getProperty("os.name").toLowerCase();
		try {
			if (osName.indexOf("windows") > -1 ) {
				// it's a Windows system, so safe to use the system L&F
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
			else {
				// Unix or MacOS - Need to use cross-platform L&F, since the system L&F
				// can be dodgy in Ubuntu and in Mac OS
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			}
		} catch ( UnsupportedLookAndFeelException e ) {
			System.err.println("Unsupported L&F Exception: " + e);
		} catch ( ClassNotFoundException e ) {
			System.err.println("L&F CLass Not Found Exception: " + e);
		} catch ( InstantiationException e ) {
			System.err.println("L&F Instantiation Exception: " + e);
		} catch ( IllegalAccessException e ) {
			System.err.println("Illegal L&F Access Exception: " + e);
		} catch ( Exception e ) {
			System.err.println("Error loading L&F: " + e);
		}

		this.setIconImage(new ImageIcon(
				Thread.currentThread().getContextClassLoader().getResource(
						CreateGui.imgPath + "icon.png")).getImage());

//		this.setIconImage(new ImageIcon(CreateGui.imgPath+"icon.png").getImage());

		Dimension screenSize=Toolkit.getDefaultToolkit().getScreenSize();
		this.setSize(screenSize.width*90/100,screenSize.height*90/100);
		this.setLocationRelativeTo(null);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		initialiseActions();
		buildMenus();
		statusBar = new StatusBar();
		getContentPane().add(statusBar, BorderLayout.PAGE_END);
		buildToolbar();
		enableDrawingActions(true);
		addWindowListener(new WindowHandler());

		// Set selection mode at startup 
		setMode(Constants.SELECT);
		((GuiAction)actions.get("Draw Select")).setSelected(true);
	}
	
	/**
	 * This method does initialization.
	 * 
	 * @author unknown
	 * 
	 * @author Dave Patterson - fixed problem on OSX due to invalid character
	 * in URI caused by unescaped blank. The code changes one blank character
	 * if it exists in the string version of the URL. This way works safely in
	 * both OSX and Windows. I also added a printStackTrace if there is an 
	 * exception caught in the setup for the "Example nets" folder.
	 *
	 */
	private void initialiseActions() {
		actions.put("New",new FileAction("New","Create a new Petri net","ctrl N"));
		actions.put("Open",new FileAction("Open","Open","ctrl O"));
		actions.put("Close",new FileAction("Close","Close the current tab","ctrl F4"));
		actions.put("Save",new FileAction("Save","Save","ctrl S"));
		actions.put("Save as",new FileAction("Save as","Save as...","F12"));
		actions.put("PNG",new FileAction("PNG",  "Export the net to PNG format",""));
		actions.put("PS",new FileAction("PostScript",  "Export the net to PostScript format",""));
		actions.put("Print",new FileAction("Print","Print","ctrl P"));
		actions.put("Exit",new FileAction("Exit","Close the program","alt F4"));
		actions.put("Grid",new GridAction("Cycle grid","Change the grid size", "G"));
		actions.put("ZoomOut",new ZoomAction("Zoom out","Zoom out by 10% ", "Z-"));
		actions.put("ZoomIn",new ZoomAction("Zoom in","Zoom in by 10% ", "Z+"));
		actions.put("Zoom",new ZoomAction("Zoom","Select zoom percentage ", ""));
		actions.put("Draw Drag",new TypeAction("Drag",DRAG,"Drag the drawing","DRAG"));
		actions.put("Draw Place",new TypeAction("Place",PLACE,"Add a place","P"));
		actions.put("Draw Trans",new TypeAction("Immediate transition",IMMTRANS,"Add an immediate transition","I"));
		actions.put("Draw TimeTrans",new TypeAction("Timed transition",TIMEDTRANS,"Add a timed transition","T"));
		actions.put("Draw Arc",new TypeAction("Arc",ARC,"Add an arc","A"));
		actions.put("Draw Annotation",new TypeAction("Annotation",ANNOTATION,"Add an annotation","N"));		
		actions.put("Draw New Token",new TypeAction("Add token",ADDTOKEN,"Add a token","ADD"));
		actions.put("Draw Delete Token",new TypeAction("Delete token",DELTOKEN,"Delete a token","SUBTRACT"));
		Action select = new TypeAction("Select",SELECT,"Select components","S");
		select.putValue("default",new Boolean(true));
		actions.put("Draw Select",select);
		actions.put("Delete",new DeleteAction("Delete","Delete selection","DELETE"));
		actions.put("Start",new AnimateAction("Animation mode",START,"Toggle Animation Mode"));
		actions.put("Back",new AnimateAction("Back",STEPBACKWARD,"Step backward a firing"));
		actions.put("Forward",new AnimateAction("Forward",STEPFORWARD, "Step forward a firing"));
		actions.put("Random",new AnimateAction("Random", RANDOM,"Randomly fire a transition"));
		actions.put("Animate",new AnimateAction("Animate",ANIMATE,"Randomly fire a number of transitions"));
		actions.put("Help",new HelpBox("Help","View documentation","F1","index.htm"));
		actions.put("Validate Tagged Net", new ValidateAction("Validate","Validate tagged net","V"));
		
		URL examplesDirURL = null;
		File[] nets = null;
		File examplesDir = null;

		try{
			examplesDirURL = Thread.currentThread().getContextClassLoader(
			).getResource("Example nets");
			/**
			 * The next block fixes a problem that surfaced on Mac OSX with 
			 * PIPE 2.4. In that environment (and not in Windows) any blanks
			 * in the project name in Eclipse are property converted to '%20'
			 * but the blank in "Example nets" is not. The following code
			 * will do nothing on a Windows machine or if the logic on OSX
			 * changess. I also added a stack trace so if the problem 
			 * occurs for another environment (perhaps multiple blanks need
			 * to be manually changed) it can be easily fixed.  DP
			 */
			// examplesDir = new File(new URI(examplesDirURL.toString()));
			String dirURLString = examplesDirURL.toString();
			int index = dirURLString.indexOf( " " );
			if ( index > 0 )
			{
				StringBuffer sb = new StringBuffer( dirURLString );
				sb.replace( index, index+1, "%20" );
				dirURLString = sb.toString();
			}
		
			examplesDir = new File( new URI(dirURLString ) );
			nets = examplesDir.listFiles();

			Arrays.sort(nets,new Comparator(){

				public int compare(Object one, Object two) {	
					return ((File)one).getName().compareTo(((File)two).getName());
				}

			});
			/* Oliver Haggarty - fixed code here so that if folder contains non .xml
			 * file the Example x counter is not incremented when that file is ignored
			 */
			if(nets.length>0) {
				int k = 0;
				for (int i = 0; i < nets.length; i++)
					if(nets[i].getName().toLowerCase().endsWith(".xml"))
						actions.put("Example " + k++,new ExampleFileAction(nets[i]));
			}
		} catch (Exception e){
			System.out.println("Error opening example files");
			e.printStackTrace();
		}
	}

	private void buildMenus() {
		menuBar=new JMenuBar();

		//File menu
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic('F');		
		addMenuItem(fileMenu,((Action)actions.get("New")));
		addMenuItem(fileMenu,((Action)actions.get("Open")));
		addMenuItem(fileMenu,((Action)actions.get("Close")));
		fileMenu.addSeparator();
		addMenuItem(fileMenu,((Action)actions.get("Save")));
		addMenuItem(fileMenu,((Action)actions.get("Save as")));

		// File->Export menu
		JMenu exportMenu=new JMenu("Export");
		exportMenu.setIcon(new ImageIcon(
				Thread.currentThread().getContextClassLoader().getResource(
						CreateGui.imgPath+"Export.png")));
		addMenuItem(exportMenu,((Action)actions.get("PNG")));
		addMenuItem(exportMenu,((Action)actions.get("PS")));
		fileMenu.add(exportMenu);

		// File continued
		fileMenu.addSeparator();
		addMenuItem(fileMenu,((Action)actions.get("Print")));
		fileMenu.addSeparator();

		// File->Example files menu
		JMenu exampleMenu=new JMenu("Example nets");
		exampleMenu.setIcon(new ImageIcon(
				Thread.currentThread().getContextClassLoader().getResource(
						CreateGui.imgPath+"Example.png")));
		int counter = 0;
		String nextName = "Example " + counter;
		Action nextAction = (Action)actions.get(nextName);
		while(nextAction != null){
			addMenuItem(exampleMenu,nextAction);
			counter ++;
			nextName = "Example " + counter;
			nextAction = (Action)actions.get(nextName);			
		}
		fileMenu.add(exampleMenu);

		//Back to File
		fileMenu.addSeparator();
		addMenuItem(fileMenu,((Action)actions.get("Exit")));

		//View menu
		JMenu viewMenu = new JMenu("View");
		viewMenu.setMnemonic('V');
		addMenuItem(viewMenu,((Action)actions.get("Grid")));
		
		//View->Zoom menu
		JMenu zoomMenu=new JMenu("Zoom");
		zoomMenu.setIcon(new ImageIcon(
				Thread.currentThread().getContextClassLoader().getResource(
						CreateGui.imgPath+"Zoom.png")));
		
		addZoomMenuItems(zoomMenu);
		viewMenu.add(zoomMenu);

		//Draw menu
		JMenu drawMenu = new JMenu("Draw");
		drawMenu.setMnemonic('D');
		addMenuItem(drawMenu,((Action)actions.get("Draw Select")));
		drawMenu.addSeparator();
		addMenuItem(drawMenu,((Action)actions.get("Draw Place")));
		addMenuItem(drawMenu,((Action)actions.get("Draw Trans")));
		addMenuItem(drawMenu,((Action)actions.get("Draw TimeTrans")));
		addMenuItem(drawMenu,((Action)actions.get("Draw Arc")));
		addMenuItem(drawMenu,((Action)actions.get("Draw Annotation")));		
		drawMenu.addSeparator();
		addMenuItem(drawMenu,((Action)actions.get("Draw New Token")));
		addMenuItem(drawMenu,((Action)actions.get("Draw Delete Token")));
		drawMenu.addSeparator();	
		addMenuItem(drawMenu,((Action)actions.get("Delete")));
		JMenu animateMenu = new JMenu("Animate");
		animateMenu.setMnemonic('A');
		addMenuItem(animateMenu,((Action)actions.get("Start")));
		animateMenu.addSeparator();
		addMenuItem(animateMenu,((Action)actions.get("Back")));
		addMenuItem(animateMenu,((Action)actions.get("Forward")));
		addMenuItem(animateMenu,((Action)actions.get("Random")));
		addMenuItem(animateMenu,((Action)actions.get("Animate")));

		//Help menu
		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic('H');
		addMenuItem(helpMenu,((Action)actions.get("Help")));    
		JMenuItem aboutItem = helpMenu.add("About PIPE");
		aboutItem.addActionListener(this); // Help - About is implemented differently
		aboutItem.setIcon(new ImageIcon(
				Thread.currentThread().getContextClassLoader().getResource(
						CreateGui.imgPath+"About.png")));

		menuBar.add(fileMenu);
		menuBar.add(viewMenu);
		menuBar.add(drawMenu);
		menuBar.add(animateMenu);
		menuBar.add(helpMenu);
		setJMenuBar(menuBar);
	}

	/**
	 * @author Ben Kirby
	 * Takes the method of setting up the Zoom menu out of the main buildMenus method.
	 * @param JMenu - the menu to add the submenu to
	 */
	
	private void addZoomMenuItems(JMenu zoomMenu) {
		for(int i=0;i<=zoomExamples.length-1;i++)
		{
			JMenuItem newItem=new JMenuItem(new ZoomAction(zoomExamples[i],"Select zoom percentage",""));
			zoomMenu.add(newItem);
		}
	}

	private JMenuItem addMenuItem(JMenu menu, Action action){
		JMenuItem item = menu.add(action);
		KeyStroke keystroke = (KeyStroke)action.getValue(Action.ACCELERATOR_KEY);
		if(keystroke != null)
			item.setAccelerator(keystroke);
		return item;
	}

	private void buildToolbar() {
		JToolBar toolBar=new JToolBar();
		toolBar.setFloatable(false);//Inhibit toolbar floating

		toolBar.add((Action)actions.get("New"));
		toolBar.add((Action)actions.get("Open"));
		toolBar.add((Action)actions.get("Save"));
		toolBar.add((Action)actions.get("Save as"));
		toolBar.add((Action)actions.get("Close"));

		toolBar.addSeparator();
		toolBar.add((Action)actions.get("Print"));

		toolBar.addSeparator();
		ButtonGroup modeButtons = new ButtonGroup();
		modeButtons.add(addIntelligentButton(toolBar,(Action)actions.get("Draw Select")));
		modeButtons.add(addIntelligentButton(toolBar,(Action)actions.get("Draw Place")));
		modeButtons.add(addIntelligentButton(toolBar,(Action)actions.get("Draw Trans")));
		modeButtons.add(addIntelligentButton(toolBar,(Action)actions.get("Draw TimeTrans")));
		modeButtons.add(addIntelligentButton(toolBar,(Action)actions.get("Draw Arc")));
		modeButtons.add(addIntelligentButton(toolBar,(Action)actions.get("Draw Annotation")));
		modeButtons.add(addIntelligentButton(toolBar,(Action)actions.get("Draw New Token")));
		modeButtons.add(addIntelligentButton(toolBar,(Action)actions.get("Draw Delete Token")));	

		toolBar.addSeparator();
		toolBar.add((Action)actions.get("Grid"));

		toolBar.addSeparator();
		addIntelligentButton(toolBar,(Action)actions.get("Start"));
		toolBar.add((Action)actions.get("Back"));
		toolBar.add((Action)actions.get("Forward"));
		toolBar.add((Action)actions.get("Random"));
		toolBar.add((Action)actions.get("Animate"));

		toolBar.addSeparator();
		toolBar.add((Action)actions.get("ZoomIn"));
		toolBar.add((Action)actions.get("ZoomOut"));
		addZoomComboBox(toolBar,(Action)actions.get("Zoom"));
		modeButtons.add(addIntelligentButton(toolBar, (Action)actions.get("Draw Drag")));		
		toolBar.add((Action)actions.get("Help"));
		
		toolBar.addSeparator();
		toolBar.add((Action)actions.get("Validate Tagged Net"));
		
		
		getContentPane().add(toolBar,BorderLayout.PAGE_START);
	}
	
	/**
	 * @author Ben Kirby
	 * Just takes the long-winded method of setting up the ComboBox out of the main buildToolbar method.
	 * Could be adapted for generic addition of comboboxes 
	 * @param toolBar the JToolBar to add the button to
	 * @param action the action that the ZoomComboBox performs
	 */
	
	private void addZoomComboBox(JToolBar toolBar, Action action) {
		
		zoomComboBox = new JComboBox(zoomExamples);
		zoomComboBox.setEditable(true);
		zoomComboBox.setSelectedItem("100%");
		zoomComboBox.setMaximumRowCount(zoomExamples.length);
		zoomComboBox.setMaximumSize(new Dimension(75,28));
		zoomComboBox.setAction(action);
		toolBar.add(zoomComboBox);
	}

	/**
	 * Creates a button that can keep in synch with its associated action
	 * i.e. will be automatically pressed if the equivalent menu option is clicked
	 * The new button is added to the "toolBar" parameter
	 * @param toolBar the JToolBar to add the button to
	 * @param action the action that the button should perform
	 * @return
	 */
	private AbstractButton addIntelligentButton(JToolBar toolBar, Action action) {
		final AbstractButton b = new JToggleButton(action);
		b.setText(null);
		toolBar.add(b);	
		action.addPropertyChangeListener(new PropertyChangeListener(){

			public void propertyChange(PropertyChangeEvent pce) {
				if (pce.getPropertyName().equals("selected")){
					b.setSelected(((Boolean)pce.getNewValue()).booleanValue());
				}
			}

		});
		return b;
	}

	/**
	 * Enables either the drawing buttons ("drawing" = true) or the
	 * animation buttons ("drawing" = false)
	 * @param drawing true for drawing, false for animation
	 */
	public void enableDrawingActions(boolean drawing){

		((Action)actions.get("Save")).setEnabled(drawing);
		((Action)actions.get("Save as")).setEnabled(drawing);
		((Action)actions.get("Draw Place")).setEnabled(drawing);
		((Action)actions.get("Draw Arc")).setEnabled(drawing);
		((Action)actions.get("Draw Annotation")).setEnabled(drawing);
		((Action)actions.get("Draw Trans")).setEnabled(drawing);
		((Action)actions.get("Draw TimeTrans")).setEnabled(drawing);
		((Action)actions.get("Draw New Token")).setEnabled(drawing);
		((Action)actions.get("Draw Delete Token")).setEnabled(drawing);
		((Action)actions.get("Delete")).setEnabled(drawing);
		((Action)actions.get("Draw Select")).setEnabled(drawing);

		((Action)actions.get("Random")).setEnabled(!drawing);
		((Action)actions.get("Animate")).setEnabled(!drawing);
		((Action)actions.get("Back")).setEnabled(!drawing);
		((Action)actions.get("Forward")).setEnabled(!drawing);

	}

	public  StatusBar getStatusBar(){
		return statusBar;
	}


	//HAK set current objects in Frame
	public void setObjects(){
		appModel = CreateGui.getModel();
		appView = CreateGui.getView();
	}

	//set frame objects by array place
	public void setObjects(int place){
		appModel = CreateGui.getModel(place);
		appView = CreateGui.getView(place);
	}

	public void setObjectsNull(int index){
		CreateGui.removeTab(index);
	}

	// set tabbed pane properties and add change listener that updates tab with linked model and view
	public void setTab(){
		appTab =CreateGui.getTab();
		appTab.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e){
				int index = appTab.getSelectedIndex();

				setObjects(index);

				if(appView!=null) {
					appView.setVisible(true);
					appView.repaint();
					updateZoomCombo();
					
					enableDrawingActions(true); //renables all non-animation buttons
					CreateGui.getAnimator().restoreModel();
					CreateGui.removeAnimationHistory();

					setTitle(appTab.getTitleAt(index));
				} else {
					setTitle(null);
				}
			}
		});
		appGui = CreateGui.getApp();
	}

	// Less sucky yet far, far simpler to code About dialogue
	public void actionPerformed(ActionEvent e){
		JOptionPane.showMessageDialog(this,
				"Imperial College DoC MSc Group And MSc Individual Project\n\n" +
				"Original version PIPE(c)\n2003 by Jamie Bloom, Clare Clark, Camilla Clifford, Alex Duncan, Haroun Khan and Manos Papantoniou\n\n" +
				"MLS(tm) Edition PIPE2(c)\n2004 by Tom Barnwell, Michael Camacho, Matthew Cook, Maxim Gready, Peter Kyme and Michail Tsouchlaris\n" +
				"2005 by Nadeem Akharware\n\n" + "http://pipe2.sourceforge.net/",
				"About PIPE2",
				JOptionPane.INFORMATION_MESSAGE);
	}

	// HAK Method called by netModel object when it changes
	public void update(Observable o, Object obj){
		if(mode!=CREATING && !CreateGui.getView().animationmode) appView.netChanged = true;
	}


	private void saveOperation(boolean forceSaveAs){
		if(appView==null) return;
		File modelFile=CreateGui.getFile();
		if(!forceSaveAs && modelFile!=null) { // ordinary save
			//if(!appView.netChanged) return; Disabled as currently ALWAYS prevents the net from being saved - Nadeem 26/05/2005
			saveNet(modelFile);
		} else {                              // save as
			String path=null;
			if(modelFile!=null) path=modelFile.toString();
			else path=appTab.getTitleAt(appTab.getSelectedIndex());
			String filename=new FileBrowser(path).saveFile();
			if (filename!=null) saveNet(new File(filename));
		}
	}

	private void saveNet(File outFile){
		try{
			//BK 10/02/07: changed way of saving to accomodate new DataLayerWriter class
			DataLayerWriter saveModel = new DataLayerWriter(appModel);
			saveModel.savePNML(outFile);

			CreateGui.setFile(outFile,appTab.getSelectedIndex());
			appView.netChanged = false;
			appTab.setTitleAt(appTab.getSelectedIndex(),outFile.getName());
			setTitle(outFile.getName());  // Change the window title
		} catch (Exception e) {
			System.err.println(e);
			JOptionPane.showMessageDialog(GuiFrame.this,
					e.toString(),
					"File Output Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
	}
	/**
	 * Creates a new tab with the selected file, or a new file if filename==null
	 * @param filename Filename of net to load, or <b>null</b> to create a new, empty tab
	 */ 
	public void createNewTab(String filename) {
		int freeSpace = CreateGui.getFreeSpace();
		String name="";
		setObjects(freeSpace);
		appModel.addObserver((Observer)appView); // Add the view as Observer
		appModel.addObserver((Observer)appGui);  // Add the app window as observer
		JScrollPane scroller = new JScrollPane(appView);
		scroller.setBorder(new BevelBorder(BevelBorder.LOWERED)); // make it less bad on XP
		appTab.addTab("",null,scroller,null);
		appTab.setSelectedIndex(freeSpace);
		
		if(filename == null) {
			name = "New Petri net " + newNameCounter++ + ".xml";
		} else {
			try {
				//BK 10/02/07: Changed loading of PNML to accomodate new 
				//PNMLTransformer class

				File inFile=new File(filename);
				PNMLTransformer transformer=new PNMLTransformer();
				appModel.createFromPNML(transformer.transformPNML(inFile.getPath()));
				DataLayer.pnmlName = inFile.getName();
				CreateGui.setFile(inFile,freeSpace);
				name=inFile.getName();
			} catch(Exception e) {
				JOptionPane.showMessageDialog(GuiFrame.this,"Error loading file:\n"+filename+"\nGuru meditation:\n"+e.toString(),"File load error",
						JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
				return;
			}
		}
		ViewExpansionComponent expand = new ViewExpansionComponent(
				getWidth(), getHeight());
		expand.addZoomController(appView.getZoomController());
		appView.add(expand);
		appView.repaint();
		appView.netChanged = false;   // Status is unchanged

		if (filename != null){
			appView.updatePreferredSize();
		}

		setTitle(name);// Change the program caption
		appTab.setTitleAt(freeSpace, name);
		Action a = (Action)actions.get("Draw Select"); //set select mode
		a.actionPerformed(null);
		
	}

	/**
	 * If current net has modifications, asks if you want to save and does it if you want.
	 * @return true if handled, false if cancelled 
	 */
	public boolean checkForSave() {
		if(appView.netChanged) {
			int result=JOptionPane.showConfirmDialog(GuiFrame.this,"Current file has changed. Save current file?",
					"Confirm Save Current File",
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.WARNING_MESSAGE);
			switch(result) {
			case JOptionPane.YES_OPTION: saveOperation(false); break;
			case JOptionPane.CLOSED_OPTION:
			case JOptionPane.CANCEL_OPTION:
				return false;
			}
		}
		return true;
	}

	//On application close,loop through all tabs and check if they have been saved
	public boolean checkForSaveAll(){
		for(int counter = 0;counter < appTab.getTabCount();counter++){
			appTab.setSelectedIndex(counter);
			if (checkForSave()==false) return false;
		}
		return true;
	}

	class GridAction extends GuiAction {
		GridAction (String name, String tooltip, String keystroke) {
			super(name, tooltip, keystroke);
		}

		public void actionPerformed(ActionEvent e) {
			Grid.increment();
			repaint();
		}
	}

	class ZoomAction extends GuiAction {
		ZoomAction (String name, String tooltip, String keystroke) {
			super(name, tooltip, keystroke);
		}

		public void actionPerformed(ActionEvent e) {
			String actionName = (String)getValue(NAME);

			ZoomController zoomer = appView.getZoomController();

			JViewport thisView = ((JScrollPane)appTab.getSelectedComponent()).getViewport();

			double currentXNoZoom, currentYNoZoom;

			currentXNoZoom = zoomer.getUnzoomedValue(
					(thisView.getViewPosition().x)+(thisView.getWidth() * 0.5));
			currentYNoZoom = zoomer.getUnzoomedValue(
					(thisView.getViewPosition().y)+(thisView.getHeight() * 0.5));

			String selection=null, strToTest=null;

			if(actionName.equals("Zoom in")){
				zoomer.zoomIn();
				updateZoomCombo();
			}

			else{

				if(actionName.equals("Zoom out")){
					zoomer.zoomOut();
					updateZoomCombo();
				}

				else{
					if(actionName.equals("Zoom"))
						selection = (String)zoomComboBox.getSelectedItem();

					if(e.getSource() instanceof JMenuItem){
						selection=((JMenuItem)e.getSource()).getText();
					}

					strToTest=validatePercent(selection);
					if(strToTest!=null){
						
						//BK: no need to zoom if already at that level
						if(zoomer.getPercent()==Integer.parseInt(strToTest)) 
							return;
						
						else zoomer.setZoom(Integer.parseInt(strToTest));
						
						updateZoomCombo();
					}

					else return;
				}
			}

			appView.zoom();
			appView.repaint();
			appView.updatePreferredSize();
			appView.getParent().validate();

			double newZoomedX, newZoomedY;

			newZoomedX=zoomer.getZoomPositionForXLocation(currentXNoZoom);
			newZoomedY=zoomer.getZoomPositionForYLocation(currentYNoZoom);

			int newViewX = (int)(newZoomedX - (thisView.getWidth() * 0.5));
			if (newViewX < 0){
				newViewX = 0;
			}
			int newViewY = (int)(newZoomedY -(thisView.getHeight() * 0.5));
			if (newViewY < 0){
				newViewY = 0;
			}

			thisView.setViewPosition(new Point(newViewX, newViewY));
		}

		private String validatePercent(String selection) {

			try{
				String toTest=selection;
				
				if(selection.endsWith("%")){
					toTest=selection.substring(0, (selection.length())-1);
				}
				
				if(Integer.parseInt(toTest)<40 || Integer.parseInt(toTest)>2000)
					throw new Exception();

				else return toTest;

			} catch(Exception e) {
				zoomComboBox.setSelectedItem("");
				return null;
			}
		}
	}

	class FileAction extends GuiAction {
		//constructor
		FileAction(String name, String tooltip, String keystroke) {
			super(name, tooltip, keystroke);
		}

		public void actionPerformed(ActionEvent e) {
			String actionName = (String)getValue(NAME);
			if(actionName.equals("Save")) saveOperation(false);            		 // code for Save operation
			else if(actionName.equals("Save as")) saveOperation(true);           // code for Save As operations
			else if(actionName.equals("Open")){       // code for Open operation
				File filePath = new FileBrowser(CreateGui.userPath).openFile();
				if ((filePath != null) && filePath.exists() && filePath.isFile() && filePath.canRead()) {
					CreateGui.userPath = filePath.getParent();
					createNewTab(filePath.toString());
					appView.getSelectionObject().enableSelection();
				}
			}
			else if(actionName.equals("New")) createNewTab(null);              // Create a new tab
			else if((actionName.equals("Exit"))&& checkForSaveAll()) {
				dispose();
				System.exit(0);
			}
			else if((actionName.equals("Close"))&&(appTab.getTabCount()>0)&&checkForSave()) {
				setObjectsNull(appTab.getSelectedIndex());
				appTab.remove(appTab.getSelectedIndex());
			}
			else if(actionName.equals("PNG")) Export.exportGuiView(appView,Export.PNG);
			else if(actionName.equals("PostScript"))  Export.exportGuiView(appView,Export.POSTSCRIPT);
			else if(actionName.equals("Print"))     Export.exportGuiView(appView,Export.PRINTER);
		}
	}

	class ExampleFileAction extends GuiAction {
		private File file;
		ExampleFileAction(File file) {
			super(file.getName(), "Open example file \"" + file.getName()
					+ "\"", null);
			this.file = file;
			putValue(SMALL_ICON, new ImageIcon(
					Thread.currentThread().getContextClassLoader().getResource(
							CreateGui.imgPath + "Net.png")));
		}

		public void actionPerformed(ActionEvent e){
			createNewTab(file.getAbsolutePath());
			appView.getSelectionObject().enableSelection();
		}
	}

	class DeleteAction extends GuiAction {
		DeleteAction(String name, String tooltip, String keystroke) {
			super(name, tooltip, keystroke);
		}

		public void actionPerformed(ActionEvent e){
			appView.getSelectionObject().deleteSelection();
		}
	}

	class TypeAction extends GuiAction {
		TypeAction(String name, int typeID, String tooltip,String keystroke){
			super(name, tooltip, keystroke);
			this.typeID = typeID;
		}

		public void actionPerformed(ActionEvent e){
			if (!isSelected()){
				setSelected(true);
				resetDrawingActions(this);
				if(appView == null) return;


				setMode(typeID);
				
				// set cursor
				if (typeID == SELECT)
					appView.setCursorType("arrow");
				else if (typeID == DRAG)
					appView.setCursorType("move");
				else
					appView.setCursorType("crosshair");
				
				appView.getSelectionObject().disableSelection();
				appView.getSelectionObject().clearSelection();

				statusBar.changeText(typeID);

				if((typeID != ARC)&&(CreateGui.getView().createArc!=null)) {
					appView.createArc.delete();          
					appView.createArc=null;
					appView.repaint();
				}

				if(typeID == SELECT) {
					//disable drawing to eliminate possiblity of connecting arc to old coord of moved component
					statusBar.changeText(typeID);
					appView.getSelectionObject().enableSelection();
				}
				
			}
		}

		private int typeID;
	}


	class ValidateAction extends GuiAction{
		
		ValidateAction(String name, String tooltip, String keystroke) {
			super(name, tooltip, keystroke);
		}
		
		public void actionPerformed(ActionEvent e){
			//call the function to run validate.....
			boolean result = appModel.validTagStructure();
		}
	}
	

	class AnimateAction extends GuiAction {
		private int typeID;
		private AnimationHistory animBox ;

		AnimateAction(String name,int typeID,String tooltip){
			super(name,tooltip,null);
			this.typeID = typeID;
		}

		public void actionPerformed(ActionEvent e){
			if(appView==null) return;

			animBox = CreateGui.getAnimationHistory();

			switch(typeID){
			case START:
				setSelected(!isSelected());
				boolean goingIntoAnimation = isSelected();
				
				if(!appModel.hasValidatedStructure() && goingIntoAnimation)
				{
					JOptionPane.showMessageDialog(null, "The Structure contains tagged arc or token and need to be validated", "Warning", JOptionPane.ERROR_MESSAGE);			
					goingIntoAnimation = false;
					setSelected(false);	
				}
				
				if(!goingIntoAnimation)
				{
					restoreMode();
				}
				
				else 
				{					
					setMode(typeID);
					appView.getSelectionObject().disableSelection();
					appView.getSelectionObject().clearSelection();
					setSelected(true);	
				}
				
				CreateGui.getView().changeAnimationMode(goingIntoAnimation);
				
				/*don't have to restore mode in the case that 
				 * structure hasn't been validated as it hasn't gone to animation yet
				 * runtime error if this is not done
				 */
				if(!appModel.hasValidatedStructure() && !goingIntoAnimation);
				else setAnimationMode(goingIntoAnimation);

				break;

			case RANDOM:
				CreateGui.getAnimator().doRandomFiring();
				break;

			case STEPFORWARD:
				animBox.stepForward();
				CreateGui.getAnimator().stepForward();
				break;

			case STEPBACKWARD:
				animBox.stepBackwards();
				CreateGui.getAnimator().stepBack();
				break;

			case ANIMATE:
				//Animator a=CreateGui.getAnimator();
				if(CreateGui.getAnimator().getNumberSequences()>0) {
					CreateGui.getAnimator().setNumberSequences(0); // stop animation
				} else {
					((Action)actions.get("Back")).setEnabled(false);
					((Action)actions.get("Forward")).setEnabled(false);
					((Action)actions.get("Random")).setEnabled(false);
					CreateGui.getAnimator().startRandomFiring();
				}
				break;

			default:
				return;
			}			
		}
	}

	public void setRandomAnimationMode(boolean on) {
		((Action)actions.get("Back")).setEnabled(!on);
		((Action)actions.get("Forward")).setEnabled(!on);
		((Action)actions.get("Random")).setEnabled(!on);
	}

	/**
	 * @author Ben Kirby
	 * Remove the listener from the zoomComboBox, so that when the box's
	 * selected item is updated to keep track of ZoomActions called from 
	 * other sources, a duplicate ZoomAction is not called
	 */
	
	public void updateZoomCombo() {
		ActionListener zoomComboListener=(zoomComboBox.getActionListeners())[0];
		zoomComboBox.removeActionListener(zoomComboListener);
		zoomComboBox.setSelectedItem(String.valueOf(appView.getZoomController().getPercent())+"%");
		zoomComboBox.addActionListener(zoomComboListener);
	}

	/**
	 * Updates the value of the "selected" property in all
	 * drawing related Action objects when a new one is selected
	 * @param selected the newly selected Action
	 */
	private void resetDrawingActions(Object selected) {
		Set actionNames = actions.keySet();
		Iterator iter = actionNames.iterator();
		while(iter.hasNext()){
			String nextActionKey = (String)iter.next();
			GuiAction nextAction = null;
			if (nextActionKey.startsWith("Draw")){
				nextAction = (GuiAction)actions.get(nextActionKey);
				if (nextAction != selected){
					nextAction.setSelected(false);
				}
			}
		}
	}

	private void setAnimationMode(boolean on) {
		CreateGui.getAnimator().setNumberSequences(0);
		if (on) {
			statusBar.changeText(getStatusBar().textforAnimation);
			CreateGui.getAnimator().storeModel();
			CreateGui.currentPNMLData().setEnabledTransitions();
			CreateGui.getAnimator().highlightEnabledTransitions();
			CreateGui.addAnimationHistory();
			enableDrawingActions(false);//disables all non-animation buttons
		} else {
			statusBar.changeText(statusBar.textforDrawing);
			CreateGui.getAnimator().restoreModel();
			CreateGui.removeAnimationHistory();
			enableDrawingActions(true); //renables all non-animation buttons
			pipe.gui.Animator.count = 0;
			pipe.gui.Animator.firedTransitions.clear();
			//appView.repaint();
		}
	}


	public void setMode(int _mode) {
		if (mode != _mode){		// Don't bother unless new mode is different.
			prev_mode = mode;
			mode = _mode;
		}
	}

	public int getMode() {
		return mode;
	}

	public void restoreMode() {
		mode = prev_mode;
	}

	public void disableGuiMenu() {
		menuBar.setEnabled(false);	
	}

	public void enableGuiMenu() {
		menuBar.setEnabled(true);
	}

	public void setTitle(String title) {
		if(title==null) super.setTitle(frameTitle);
		else super.setTitle(frameTitle+": "+title);
	}

	class WindowHandler extends WindowAdapter{
		//Handler for window closing event
		public void windowClosing(WindowEvent e){
			dispose();
			System.exit(0);
		}
	}

}
