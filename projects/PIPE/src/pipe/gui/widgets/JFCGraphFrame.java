package pipe.gui.widgets;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;

import pipe.gui.CreateGui;


/**
 * Class that encapsulates a JFreeChart chart frame in a JFrame with added button bar to save
 * image of graph or save its data as a CSV file.
 * @author Oliver Haggarty - 08/2007
 *
 */
public class JFCGraphFrame extends JFrame {
	private ButtonBar butBar;
	private JFreeChart graph;
	private ChartPanel chartpnl;
	/**
	 * Configures the frame's components
	 * @param graphName
	 * @param graph graph object containing data for graph
	 */
	public JFCGraphFrame(String graphName, JFreeChart graph) {
		super(graphName);
		this.graph = graph;
		butBar = new ButtonBar(new String[]{"Save As png", "Save As CSV"}, 
				new ActionListener[]{pngListener, cvsListener});
		chartpnl = new ChartPanel(graph);
		Container contentPane=this.getContentPane();
		contentPane.setLayout(new BoxLayout(contentPane,BoxLayout.PAGE_AXIS));
		contentPane.add(chartpnl);
		contentPane.add(butBar);
		this.setIconImage(new ImageIcon(CreateGui.imgPath + "icon.png").getImage());
	}
	
	/**
	 * Contains code to save the graph as a png file
	 */
	ActionListener pngListener = new ActionListener() {
		public void actionPerformed(ActionEvent eve) {
			RenderedImage graphImage = graph.createBufferedImage(800, 600);
			
			
			File saveFile;
			JFileChooser fc = new JFileChooser();
			int returnVal = fc.showSaveDialog(JFCGraphFrame.this);
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				saveFile = fc.getSelectedFile();
			}
			else
				return;
			try {
				ImageIO.write(graphImage, "png", saveFile);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	};
	
	/**
	 * Contains code to save graph as a csv file
	 */
	private ActionListener cvsListener=new ActionListener() {
	  	public void actionPerformed(ActionEvent arg0) {
	  		XYDataset graphData = graph.getXYPlot().getDataset();
	  		int size = graphData.getItemCount(0);
	  		
	  		FileWriter fw = null;
	  		StringBuffer content = new StringBuffer();
	  		
	  		File saveFile;
		
			JFileChooser fc = new JFileChooser();
			int returnVal = fc.showSaveDialog(JFCGraphFrame.this);
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				saveFile = fc.getSelectedFile();
			}
			else
				return;
			
			try {
				fw = new FileWriter(saveFile);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			for(int i = 0; i < size; i++) {
				content.append(graphData.getXValue(0, i));
				content.append(",");
				content.append(graphData.getYValue(0, i));
				content.append(",\n");				
			}
	  		String content1 = content.toString();
	  		
	  		try {
		  		fw.write(content1);
		  		fw.close();
	  		}
	  		catch (Exception e) {
	  			e.printStackTrace();
	  		}
	  	}
	  };
}
