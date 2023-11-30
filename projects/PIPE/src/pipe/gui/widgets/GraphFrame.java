package pipe.gui.widgets;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import pipe.gui.CreateGui;
import att.grappa.Graph;
import att.grappa.GrappaAdapter;
import att.grappa.GrappaConstants;
import att.grappa.GrappaPanel;
import att.grappa.GrappaSupport;

public class GraphFrame extends JFrame implements GrappaConstants
{
	GrappaPanel gp;
	Graph graph = null;
	JPanel panel = null;

	public void constructGraphFrame(Graph graph) {
		this.setIconImage(new ImageIcon(CreateGui.imgPath + "icon.png").getImage());

		this.graph = graph;


		setSize(600,600);
		setLocation(100,100);


		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent wev) {
				Window w = wev.getWindow();
				w.setVisible(false);
				w.dispose();
			}
		});

		JScrollPane jsp = new JScrollPane();
		gp = new GrappaPanel(graph);
		gp.addGrappaListener(new GrappaAdapter());
		gp.setScaleToFit(true);
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		panel = new JPanel();
		panel.setLayout(gbl);
		getContentPane().add("Center", jsp);
		getContentPane().add("West", panel);
		
		JTextArea legend = new JTextArea("Right click in graph area for options\nHover mouse over nodes to view state marking\nRed nodes represent vanishing states\nBlack nodes represent tangible states");
		legend.setEditable(false);
		
		getContentPane().add("South", legend );
		
		
		setVisible(true);
		jsp.setViewportView(gp);
		Object connector = null;
		try {
			connector = (new URL("http://www.research.att.com/~john/cgi-bin/format-graph")).openConnection();
			URLConnection urlConn = (URLConnection)connector;
			urlConn.setDoInput(true);
			urlConn.setDoOutput(true);
			urlConn.setUseCaches(false);
			urlConn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
		} catch(Exception ex) {
			System.err.println("Exception while setting up URLConnection: " + ex.getMessage() + "\nLayout not performed.");
			connector = null;
		}

		if(connector != null) {
			if(!GrappaSupport.filterGraph(graph,connector)) {
				System.err.println("ERROR: somewhere in filterGraph");
			}
			if(connector instanceof Process) {
				try {
					int code = ((Process)connector).waitFor();
					if(code != 0) {
						System.err.println("WARNING: proc exit code is: " + code);
					}
				} catch(InterruptedException ex) {
					System.err.println("Exception while closing down proc: " + ex.getMessage());
					ex.printStackTrace(System.err);
				}
			}
			connector = null;
		}
		graph.repaint();
	}
}
