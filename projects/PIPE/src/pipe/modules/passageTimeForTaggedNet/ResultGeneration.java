package pipe.modules.passageTimeForTaggedNet;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.RenderedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import pipe.modules.clientCommon.HTMLPane;
import pipe.modules.passage.ProgressBarHTMLPane;

public class ResultGeneration {
	
	private JPanel resultsPanel;

	String results="";
	String results_cdf="";
	String statusMesg;

	XYDataset PDFDataset, CDFDataset;
	
	private JFreeChart pdfGraph, cdfGraph;
	private ChartPanel pdfChartPanel, cdfChartPanel;
	private CardLayout graphFlip;
	private JPanel graphPanel;
	JButton switchViewBtn;
	private boolean currentlyPDF = true; // Is the graph currently showing a PDF view
	private boolean errorCalResult = false;
	private boolean steadyStateFailed = false;
	
	public ResultGeneration(JPanel resultPanel){
		
		this.resultsPanel = resultPanel;	
	}
	
	public int init()
	{
		
		run_hydra();
		if(!this.errorCalResult && !this.steadyStateFailed){
			generating_graph();	
			return 0;
		}
		else
		{	
			if(this.steadyStateFailed)return 1;
			else return 2;
			
		}
		
	}
	
	public void generating_graph(){
		
	try
	{
			
		String[] lines,lines_cdf,values;
		
		//Generate a set of points from the results
		XYSeries points = new XYSeries("Passage Time Results");
		XYSeries CDFpoints = new XYSeries("Passage Time Results (CDF)");
		
		
		// Divide the results into lines
		String pe = "\n";
		Pattern p = Pattern.compile(pe);
		lines = p.split(results);
		lines_cdf = p.split(results_cdf);
		
		//The first two values of each line are the X,Y cordinates			
		for(int i=0; i<lines.length;i++)
		{
			if(lines[i].indexOf("DATA0")>=0 && lines[i].indexOf("elapsed")==-1 )
			{
				values = lines[i].split("\\s+");
				points.add(new Double(values[0]).doubleValue(), new Double(values[1]).doubleValue() );
				
			}
		}
		


		{
			for(int i=0; i<lines_cdf.length;i++)
			{
				if(lines_cdf[i].indexOf("DATA0")>=0 && lines_cdf[i].indexOf("elapsed")==-1 )
				{
					values = lines_cdf[i].split("\\s+");
					CDFpoints.add(new Double(values[0]).doubleValue(), new Double(values[1]).doubleValue() );
				}
			}
			
			System.out.println("\ndisplay graph");
			
			//Create CDF graph panel
			CDFDataset = new XYSeriesCollection(CDFpoints);
			cdfGraph = ChartFactory.createXYLineChart("Passage Time Results", "Time","Probability Density", CDFDataset, PlotOrientation.VERTICAL, false, false, false); 
			cdfGraph.setBackgroundPaint(Color.white); 
			cdfChartPanel = new ChartPanel(cdfGraph);
			
			// Create PDF graph panel
			PDFDataset = new XYSeriesCollection(points);				
			pdfGraph = ChartFactory.createXYLineChart("Passage Time Results", "Time","Probability Density", PDFDataset, PlotOrientation.VERTICAL, false, false, false); 
			pdfGraph.setBackgroundPaint(Color.white);							
			pdfChartPanel = new ChartPanel(pdfGraph);
			
			graphFlip = new CardLayout();
			graphPanel = new JPanel(graphFlip);
			graphPanel.add(pdfChartPanel, "PDF");
			
			graphPanel.add(cdfChartPanel, "CDF");
			

			//	 Create the results panel
			resultsPanel.removeAll(); // clear if previously used
			resultsPanel.setLayout(new BorderLayout());
			resultsPanel.add(graphPanel, BorderLayout.CENTER);
			
			
			
			// Create button panel then add
			JPanel buttons = new JPanel();
			
			//if (doCDF)
			{
				switchViewBtn = new JButton("Show CDF");
				switchViewBtn.addActionListener(switchView);
				switchViewBtn.setMnemonic(KeyEvent.VK_V);
				
				buttons.add(switchViewBtn);
			}
			
			JButton saveImageBtn = new JButton("Save Graph");
			saveImageBtn.addActionListener(pngListener);
			saveImageBtn.setMnemonic(KeyEvent.VK_S);
			
			JButton saveCordBtn = new JButton("Save Points");
			saveCordBtn.addActionListener(cvsListener);
			saveCordBtn.setMnemonic(KeyEvent.VK_C);		
			
			buttons.add(saveImageBtn);
			buttons.add(saveCordBtn);		
			
			resultsPanel.add(buttons, BorderLayout.PAGE_END);
			
		}

	}
	
	// If error occurs on the server then the results will be the error message 
	catch(Exception exp)
	{
		HTMLPane errorText = new HTMLPane("Error calculating results");
		//errorText.setText(results[1] + exp);
	
		resultsPanel.removeAll(); // clear if previously used
		resultsPanel.setLayout(new BorderLayout());
		resultsPanel.add(errorText, BorderLayout.CENTER);			
	}
    
		
		
  }
	public void run_hydra(){
		
		try
	    {
	      Runtime rt = Runtime.getRuntime();
	      
	      Process proc = rt.exec(new String[]{"hydra-s", "current.mod"});
	      
	      InputStream stdin = proc.getInputStream();
	      InputStreamReader isr = new InputStreamReader(stdin);
	      
	      InputStream stderr = proc.getErrorStream();
	      InputStreamReader isr2 = new InputStreamReader(stderr);

	      BufferedReader br = new BufferedReader(isr); 
	      BufferedReader br2 = new BufferedReader(isr2);
	      
	      String line = null;

	      int num=0;
	      
	      boolean start_cdf = false;
	      
	      while ( (line = br.readLine()) != null)
	      {
	    	  System.out.println(line);  
	    	  if( line.indexOf("steady state vector status........ ***FAILED***")>0  )
	    		  this.steadyStateFailed = true;
	    	  if( line.indexOf("hydra-s/uniform -cdf")>0  )start_cdf = true;
	    	  
	    	  
	    	  if(!start_cdf){
	    	    results+=line;
	    	    results+="\n";
	    	  }
	    	  else{
	    		  results_cdf+=line;
		    	  results_cdf+="\n";
	    		  
	    	  }
	    	  
	    	  
	    	  if( line.indexOf("erlang terms have not decayed to 0 by n=10000")>0  )
	    		  this.errorCalResult = true;
	    	  
	    	  
	    	  
	      }
	
	      
	      
	      while ( (line = br2.readLine()) != null) 
	    	  System.out.println("2"+line);
	       
	    /*
	      proc = rt.exec(new String[]{"./uniform"}, null, new File("/homes/wl107/Desktop/hydra-s"));
	      stdin = proc.getInputStream();
	      br = new BufferedReader( new InputStreamReader (stdin ));
	      stderr = proc.getErrorStream();
	      br2 = new BufferedReader( new InputStreamReader(stderr) );
	      String line_result = null;
    
	      while( (line_result = br.readLine()) != null)
	      {
	    	  System.out.println(line_result);
	    	  results+=line_result;
	    	  results+="\n";
	    	  if( line_result.indexOf("erlang terms have not decayed to 0 by n=10000")>0  )
	    		  this.errorCalResult = true;
	    	  
	    	  
	    	  
	    	 
	      }
	      
	     
	      
	      while ( (line_result = br2.readLine()) != null) 
	    	  System.out.println("2"+line_result);
	      
	      
	   
	      
	      //-----generating cdf values
		    
	      if(!this.errorCalResult)
	      {
	    	  
	    	  proc = rt.exec(new String[]{"./uniform","-cdf"}, null, new File("/homes/wl107/Desktop/hydra-s"));
		      stdin = proc.getInputStream();
		      br = new BufferedReader( new InputStreamReader (stdin ));
		      stderr = proc.getErrorStream();
		      br2 = new BufferedReader( new InputStreamReader(stderr) );

		      while( (line_result = br.readLine()) != null)
		      {
		    	  System.out.println(line_result);
		    	  results_cdf+=line_result;
		    	  results_cdf+="\n";
		    	 
		      }
		      
		      while ( (line_result = br2.readLine()) != null) 
		    	  System.out.println("2"+line_result);
		    	  
		    	  
	    	  
	      }
*/
	      int exitVal = proc.waitFor();  
	     
	      System.out.println("Process exitValue: " + exitVal); 
	    } 
		
		catch (Throwable t) 
	    { 
	      t.printStackTrace(); 
	    }    
		
		
	}
	
	ActionListener switchView = new ActionListener()
	{
		public void actionPerformed(ActionEvent eve)
		{
			if (currentlyPDF)
			{
				graphFlip.show(graphPanel, "CDF");
				switchViewBtn.setText("Show PDF");
			}
			else
			{
				graphFlip.show(graphPanel, "PDF");
				switchViewBtn.setText("Show CDF");				
			}
			
			currentlyPDF = !currentlyPDF; // flip bool
		}
	};
	ActionListener pngListener = new ActionListener()
	{
		public void actionPerformed(ActionEvent eve) {
			
			RenderedImage graphImage;
			
			if (currentlyPDF)
				graphImage = pdfGraph.createBufferedImage(800, 600);
			else
				graphImage = cdfGraph.createBufferedImage(800, 600);
			
			
			File saveFile;
			JFileChooser fc = new JFileChooser();
			int returnVal = fc.showSaveDialog(resultsPanel);
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
	


	private ActionListener cvsListener = new ActionListener() {
	  	public void actionPerformed(ActionEvent arg0) {
	  		XYDataset graphData; 
	  		
	  		if(currentlyPDF)
	  			graphData = pdfGraph.getXYPlot().getDataset();
	  		else
	  			graphData = cdfGraph.getXYPlot().getDataset();
	  			
	  		int size = graphData.getItemCount(0);
	  		
	  		FileWriter fw = null;
	  		StringBuffer content = new StringBuffer();
	  		
	  		File saveFile;
		
			JFileChooser fc = new JFileChooser();			
			
			int returnVal = fc.showSaveDialog(resultsPanel);
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
