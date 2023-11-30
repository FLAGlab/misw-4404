package pipe.gui;

import java.io.File;
import java.util.ArrayList;

import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;

import pipe.common.dataLayer.DataLayer;

public class CreateGui implements Constants
{
	public static GuiFrame		appGui;
	private static Animator		animator;
	private static JTabbedPane	appTab;
	private static int			freeSpace;

	private static ArrayList	tabs	= new ArrayList();

	public static String		imgPath, userPath;			// useful for stuff

	private static class TabData
	{ // a structure for holding a tab's data
		public DataLayer	appModel;
		public GuiView		appView;
		public File			appFile;
	}

	/**
	 * The Module will go in the top pane, the animation window in the bottom
	 * pane
	 */
	public static JSplitPane		leftPane;
	public static AnimationHistory	animBox;
	public static JScrollPane		scroller;

	public static void init()
	{

		CreateGui.imgPath = "Images" + System.getProperty("file.separator");
		// System.out.println(new File(imgPath).getAbsolutePath());

		CreateGui.userPath = null; // make the initial dir for browsing be My
									// Documents (win), ~ (*nix), etc

		CreateGui.appGui = new GuiFrame("PIPE2: Platform Independent Petri Net Editor 2.6");

		Grid.enableGrid();

		CreateGui.appTab = new JTabbedPane();

		CreateGui.animator = new Animator();
		CreateGui.appGui.setTab(); // sets Tab properties

		// create the tree
		ModuleManager moduleManager = new ModuleManager();
		JTree moduleTree = moduleManager.getModuleTree();

		CreateGui.leftPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, moduleTree, null);
		CreateGui.leftPane.setContinuousLayout(true);
		CreateGui.leftPane.setDividerSize(0);

		JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, CreateGui.leftPane, CreateGui.appTab);
		pane.setContinuousLayout(true);
		pane.setOneTouchExpandable(true);
		pane.setBorder(null); // avoid multiple borders
		pane.setDividerSize(8);

		CreateGui.appGui.getContentPane().add(pane);
		CreateGui.appGui.createNewTab(null);
		CreateGui.getView().getSelectionObject().enableSelection();
		CreateGui.appGui.setVisible(true);

	}

	public static GuiFrame getApp()
	{ // returns a reference to the application
		return CreateGui.appGui;
	}

	public static DataLayer getModel()
	{
		return CreateGui.getModel(CreateGui.appTab.getSelectedIndex());
	}

	public static void nullModel(final int index)
	{
		try
		{
			TabData tab = (TabData) CreateGui.tabs.get(index);
			tab.appModel = null;
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			System.err.println("Tried to null model with bad index");
		}
	}

	public static void nullView(final int index)
	{
		try
		{
			TabData tab = (TabData) CreateGui.tabs.get(index);
			tab.appView = null;
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			System.err.println("Tried to null view with bad index");
		}
	}

	public static DataLayer getModel(final int index)
	{
		if (index < 0)
			return null;
		TabData tab = (TabData) CreateGui.tabs.get(index);
		if (tab.appModel == null)
			tab.appModel = new DataLayer();
		return tab.appModel;
	}

	public static GuiView getView(final int index)
	{
		if (index < 0)
			return null;
		TabData tab = (TabData) CreateGui.tabs.get(index);
		if (tab.appView == null)
			tab.appView = new GuiView();
		return tab.appView;
	}

	public static GuiView getView()
	{
		return CreateGui.getView(CreateGui.appTab.getSelectedIndex());
	}

	public static File getFile()
	{
		TabData tab = (TabData) CreateGui.tabs.get(CreateGui.appTab.getSelectedIndex());
		return tab.appFile;
	}

	public static void setFile(final File modelfile, final int fileNo)
	{
		if (fileNo >= CreateGui.tabs.size())
			return;
		TabData tab = (TabData) CreateGui.tabs.get(fileNo);
		tab.appFile = modelfile;
	}

	public static int getFreeSpace()
	{
		CreateGui.tabs.add(new TabData());
		return CreateGui.tabs.size() - 1;
	}

	public static void removeTab(final int index)
	{
		CreateGui.tabs.remove(index);
	}

	public static JTabbedPane getTab()
	{
		return CreateGui.appTab;
	}

	public static Animator getAnimator()
	{
		return CreateGui.animator;
	}

	/**
	 * returns the current dataLayer object - used to get a reference to pass to
	 * the modules
	 */
	public static DataLayer currentPNMLData()
	{
		if (CreateGui.appTab.getSelectedIndex() < 0)
			return null;
		TabData tab = (TabData) CreateGui.tabs.get(CreateGui.appTab.getSelectedIndex());
		return tab.appModel;
	}

	/** Creates a new animationHistory text area, and returns a reference to it */
	public static void addAnimationHistory()
	{
		try
		{
			CreateGui.animBox = new AnimationHistory("Animation history\n");
			CreateGui.animBox.setEditable(false);

			CreateGui.scroller = new JScrollPane(CreateGui.animBox);
			CreateGui.scroller.setBorder(new EmptyBorder(0, 0, 0, 0)); // make
																		// it
																		// less
																		// bad
																		// on XP

			CreateGui.leftPane.setBottomComponent(CreateGui.scroller);

			CreateGui.leftPane.setDividerLocation(0.5);
			CreateGui.leftPane.setDividerSize(8);
		}
		catch (javax.swing.text.BadLocationException be)
		{
			be.printStackTrace();
		}
	}

	public static void removeAnimationHistory()
	{
		if (CreateGui.scroller != null)
		{
			CreateGui.leftPane.remove(CreateGui.scroller);
			CreateGui.leftPane.setDividerLocation(0);
			CreateGui.leftPane.setDividerSize(0);
		}
	}

	public static AnimationHistory getAnimationHistory()
	{
		return CreateGui.animBox;
	}

}