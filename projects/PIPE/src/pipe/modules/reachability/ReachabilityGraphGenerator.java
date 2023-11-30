package pipe.modules.reachability;
import java.awt.Checkbox;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.Date;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JDialog;

import pipe.common.dataLayer.DataLayer;
import pipe.dataLayer.calculations.Marking;
import pipe.dataLayer.calculations.State;
import pipe.dataLayer.calculations.StateSpaceGenerator;
import pipe.dataLayer.calculations.TimelessTrapException;
import pipe.gui.CreateGui;
import pipe.gui.widgets.ButtonBar;
import pipe.gui.widgets.GraphFrame;
import pipe.gui.widgets.PetriNetChooserPanel;
import pipe.gui.widgets.ResultsHTMLPane;
import pipe.io.AbortDotFileGenerationException;
import pipe.io.ImmediateAbortException;
import pipe.io.IncorrectFileFormatException;
import pipe.io.RGFileHeader;
import pipe.io.StateRecord;
import pipe.io.TransitionRecord;
import pipe.modules.Module;
import att.grappa.Graph;
import att.grappa.Parser;
import circularbuffers.CircularByteBuffer;

/**
 * @author Matthew Worthington / Edwin Chung / Will Master - Created module to produce the reachability graph 
 * representation of a  petri net. This module makes use of modifications that were made to the state space generator 
 * to produce a list of possible states (both tangible and non tangible) which are then transformed into a dot file. 
 * The file is then translated into its graphical layout using www.research.att.com hosting of Graphviz. It should 
 * therefore be noted that a live internet connection is required. (Feb/March,2007)
 */
public class ReachabilityGraphGenerator implements Module{

	private static final String MODULE_NAME = "Reachability Graph";
	private PetriNetChooserPanel sourceFilePanel;
	private static ResultsHTMLPane results;
	private JDialog guiDialog = new JDialog(CreateGui.getApp(),MODULE_NAME,true);
	private static Checkbox checkBox1 =  new Checkbox("Set the intial state(S0) to be on top of the graph", false);
	private static String dataLayerName;

	public void run(DataLayer pnmlData) {
		// Build interface

		// 1 Set layout
		Container contentPane=guiDialog.getContentPane();
		contentPane.setLayout(new BoxLayout(contentPane,BoxLayout.PAGE_AXIS));

		// 2 Add file browser
		contentPane.add(sourceFilePanel=new PetriNetChooserPanel("Source net",pnmlData));

		// 3 Add results pane
		contentPane.add(results=new ResultsHTMLPane(pnmlData.getURI()));

		// 4 Add button's
		contentPane.add(new ButtonBar("Generate Reachability Graph", generateGraph));

		// 5 Make window fit contents' preferred size
		guiDialog.pack();

		// 6 Move window to the middle of the screen
		guiDialog.setLocationRelativeTo(null);

		contentPane.add(checkBox1);
		checkBox1.setState(false);
		guiDialog.setModal(false);
		guiDialog.setVisible(false);
		guiDialog.setVisible(true);
	}

	ActionListener generateGraph = new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			long start = new Date().getTime();
			long gfinished;
			long allfinished;
			double graphtime;
			double constructiontime;
			double totaltime;

			DataLayer sourceDataLayer=sourceFilePanel.getDataLayer();
			dataLayerName = DataLayer.pnmlName;

			// This will be used to store the reachability graph data
			File reachabilityGraph = new File("results.rg");

			// This will be used to store the steady state distribution
			String s="<h2>Reachability Graph Results</h2>";

			if(sourceDataLayer==null) return;
			if(!sourceDataLayer.getPetriNetObjects().hasNext()) s+="No Petri net objects defined!";
			else {
				try {
					StateSpaceGenerator.generate(sourceDataLayer, reachabilityGraph);
					gfinished = new Date().getTime();
					System.gc();
					generateReachabilityGraph(reachabilityGraph);
					allfinished = new Date().getTime();
					graphtime = (gfinished - start)/1000.0;
					constructiontime = (allfinished - gfinished)/1000.0;
					totaltime = (allfinished - start)/1000.0;
					DecimalFormat f=new DecimalFormat();
					f.setMaximumFractionDigits(5);
					s+= "<br>Generating reachability graph took " + f.format(graphtime) + "s";
					s+= "<br>Converting to dot format and constructing took " + f.format(constructiontime) + "s";
					s+= "<br>Total time was " + f.format(totaltime) + "s";	
				}
				catch (AbortDotFileGenerationException e) {
					s +=e.getMessage();
					results.setText(s);
					return;
				}
				catch (OutOfMemoryError e) {
					s += "Memory error: " + e.getMessage();
					results.setText(s);
					return;
				} catch (ImmediateAbortException e) {
					s += "<br>Error: " + e.getMessage();
					results.setText(s);
					return;
				} catch (TimelessTrapException e) {
					s += "<br>" + e.getMessage();
					results.setText(s);
					return;
				} catch (IOException e) {
					s += "<br>" + e.getMessage();
					results.setText(s);
					return;
				}
				catch (Exception e) {
					s += "Grappa parser unable to process remote method calls";
					results.setText(s);
					return;
				}
			}
			results.setText(s);
		}
	};

	public String getName() {
		return MODULE_NAME;
	}


	/**
	 * displayResults()
	 * Takes the reachability graph file and the steady state distribution
	 * and produces nicely formatted output showing these results plus
	 * more results based on them.
	 * 
	 * @param sourceDataLayer		The GSPN model data
	 * @param rgfile				The reachability graph
	 * @param pi					The steady state distribution
	 * @param results				The place to display the results
	 * @throws IOException 
	 */
	public static InputStream generateDotFile(File rgfile) throws IOException{
		RGFileHeader header = new RGFileHeader();
		RandomAccessFile reachabilityFile;
		try {
			reachabilityFile = new RandomAccessFile(rgfile, "r");
			header.read(reachabilityFile);
		} catch (IncorrectFileFormatException e1) {
			System.err.println("Reachability Dot Generator: incorrect file format on state space file");
			return null;
		} catch (IOException e1) {
			System.err.println("Reachability Dot Generator I/O: unable to read header file");
			return null;
		}
		if ((header.getNumStates()+header.getNumTransitions())>400)
		{
			results.setText("The Petri Net contains in excess of 400 elements (state and transitions)");
			throw new IOException("Reachability graph too big for displaying");
		}

		CircularByteBuffer circularBuffer = new CircularByteBuffer(CircularByteBuffer.INFINITE_SIZE);
		OutputStream outStream = circularBuffer.getOutputStream();

		outStream.write("digraph reachability_graph {\nrankdir=TB;\nnode[shape=circle];\n".getBytes());
		if (checkBox1.getState()) outStream.write("{ rank=source; \"0\"}\n".getBytes());
		for (int count=0; count<header.getNumStates(); count++)
		{
			int stateNum=0;
			boolean isTangible=false;
			int stateArraySize = header.getStateArraySize();
			int[] state_array = new int[stateArraySize];
			State state = new State(state_array);
			Marking new_marking = new Marking(state,stateNum,isTangible);
			StateRecord record = new StateRecord(new_marking);
			record.read1(stateArraySize, reachabilityFile);
			outStream.write((record.getID()+" [label=\"S"+count+ " \"tip=\"{").getBytes());
			state_array=record.getState();
			for (int arrayIndex=0; arrayIndex<stateArraySize; arrayIndex++)
			{
				if (!(arrayIndex==stateArraySize-1))
					outStream.write((state_array[arrayIndex]+",").getBytes());
				else
					outStream.write((state_array[arrayIndex]+"").getBytes());
			}
			outStream.write("}\"".getBytes());
			if (!record.getTangible())
			{
				outStream.write(", color=red".getBytes());
			}
			outStream.write("];\n".getBytes());
		}
		reachabilityFile.seek(header.getOffsetToTransitions());
		int numberTransitions = header.getNumTransitions();
		for (int transitionCounter=0; transitionCounter<numberTransitions; transitionCounter++)
		{
			TransitionRecord transitions = new TransitionRecord(0,0,0.0,0);
			transitions.read1(reachabilityFile);
			outStream.write((transitions.getFromState()+" -> "+ transitions.getToState()+"[ label = \"T" + transitions.getTransitionNo()+"\"];\n").getBytes());
		}
		outStream.write("}".getBytes());
		outStream.close();
		return circularBuffer.getInputStream();		
	}


	public void generateReachabilityGraph(File rgfile) throws AbortDotFileGenerationException, IOException, Exception{
		InputStream dotStream;
		Graph graph = null;
		dotStream = generateDotFile(rgfile);
		if (dotStream!=null)
		{
			Parser program = new Parser(dotStream,System.err);
			program.parse();
			graph = program.getGraph();
			GraphFrame frame = new GraphFrame();
			frame.constructGraphFrame(graph);
			frame.toFront();
			frame.setIconImage((new ImageIcon(
					Thread.currentThread().getContextClassLoader().getResource(
							CreateGui.imgPath + "icon.png")).getImage()));
			frame.setTitle(dataLayerName);
		}
		else throw new AbortDotFileGenerationException("Unable to generate dot file");
	}
}
