package pipe.modules.steadyState;

import java.io.IOException;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.net.UnknownHostException;

import javax.swing.JTabbedPane;

import pipe.common.AnalysisType;
import pipe.common.PerformanceMeasure;
import pipe.common.SimplePlaces;
import pipe.common.SimpleTransitions;
import pipe.common.dataLayer.DataLayer;
import pipe.gui.widgets.ResultsHTMLPane;
import pipe.modules.clientCommon.HTMLPane;
import pipe.modules.clientCommon.SocketIO;

/**
 * This class performs the transfers the pnmlData to the server
 * while updating the user of job progress
 * 
 * @author Barry Kearns
 * @date August 2007
*/

public class Analyse implements Runnable
{
	private String serverAddr = "";
	private int serverPort = 0;
	private PerformanceMeasure performanceMeasure;
	
	private DataLayer pnmlData;
	JTabbedPane tabbedPane;
	private HTMLPane progressPane;
	private ResultsHTMLPane resultsPane;
	String status="<h2>Steady State Analysis Progress</h2>";
	

	  public Analyse(DataLayer pnmlData, HTMLPane progress, ResultsHTMLPane results)
	  {
		this.pnmlData = pnmlData;		 
		progressPane = progress;
		resultsPane = results;
	  }
	  
	  
	  public void setServer(String serverAddr, int serverPort)
	  {
		  this.serverAddr = serverAddr;
		  this.serverPort = serverPort;
	  }
	  
	  public void setStateMeasure(PerformanceMeasure performanceMeasure)
	  {
		 this.performanceMeasure = performanceMeasure;
	  }
	  
	  
	  public void run()
	  {	
		  // Convert the PNML data into a serialisable form for transmission  
		  SimplePlaces splaces = new SimplePlaces(pnmlData);
		  SimpleTransitions sTransitions = new SimpleTransitions(pnmlData);
		  
		  		  
		  
		  try
		  {
			  updateUI("Opening Connection");
			  SocketIO serverSock = new SocketIO(serverAddr, serverPort); 
			  serverSock.send(AnalysisType.STEADYSTATE); // Inform server of the process to be performed
			  
			  updateUI("Sending data");
			  serverSock.send(splaces);
			  serverSock.send(sTransitions);
			  serverSock.send(performanceMeasure);
			    
			  updateUI("Server Scheduling Process");
			  StatusListener serverListener = new StatusListener(serverSock, progressPane, status);
			  status = serverListener.listen();
			  
			  updateUI("Receiving Results");
			  ResultsReceiver resultsReceiver = new ResultsReceiver(serverSock, resultsPane, status);
			  resultsReceiver.receive(pnmlData);
			  			    
			  updateUI("Closing Connection");		  
			  serverSock.close();
			  
						  
			  // Slow the transition between progress tab and results tab
			  try {
				Thread.sleep(800);
			} catch (InterruptedException e) {} 

			  // Add results pane and set active
			tabbedPane.addTab("Results", resultsPane);
			tabbedPane.setSelectedComponent(resultsPane);
		  }

		     catch (StreamCorruptedException sce) {
		    	 updateUI("Stream Corrupted Exception" + sce.getMessage());
		     }
		     catch(UnknownHostException uhe){
		    	 updateUI("Unknown host exception " + uhe.getMessage());
		     }
		     catch (OptionalDataException ode) {
		    	 updateUI("Data Exception" + ode.getMessage());
		       }
		     catch (IOException ioe) {
		    	 updateUI("Unable to connect to server " + serverAddr + " : " +  serverPort + ": " + ioe.getMessage());	    	 
	       }
	  

		
	  }
	  
	  private void updateUI(String update)
	  {
		  // setText is a thread safe operation so we can freely use it within this thread
		  status += update + "<br>";
		  progressPane.setText(status); 
	  }


	  public void setTabbedPane(JTabbedPane inputPane)
	  {
		  tabbedPane = inputPane;
	  }
}
