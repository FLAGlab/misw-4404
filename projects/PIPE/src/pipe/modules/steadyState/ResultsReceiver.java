package pipe.modules.steadyState;

import pipe.common.dataLayer.DataLayer;
import pipe.common.dataLayer.Place;
import pipe.common.dataLayer.Transition;
import pipe.gui.widgets.ResultsHTMLPane;
import pipe.modules.clientCommon.SocketIO;

public class ResultsReceiver
{
	private SocketIO server;
	private ResultsHTMLPane resultsPane;
	String statusHTML = "";
	
	
	
	public ResultsReceiver(SocketIO server, ResultsHTMLPane resultsPane, String currentStatus)
	{
		this.server = server;
		this.resultsPane = resultsPane;
	}
	
	public String receive(DataLayer pnmlData)
	{
		// Retrive output
		String results = server.receiveFileContent();
		
		// Parse to display in HTML format
		String resultsOutput = "<h2>Steady State Analysis Results</h2>\n";
		
		
		// Convert place / transition IDs into labels
		Place currentPlace = null;
		Transition currentTransition = null;
		boolean inTable = false; // record whether we can currently constructing a table
		String[] lines, values = null;
		String type;
		
		
		
		// Divide the results into lines
		lines = results.split("\\r+|\n+");
		
		//	The check each line if it refers to a meaure, then check which type
		for(int i=0; i<lines.length;i++)
		{
			
			values = lines[i].split("\\s+");
			
			
			if(values.length >= 3 && values[1].equals("Measure"))
			{
				if (inTable)
				{
					resultsOutput += "</table>";
					inTable = false;
				}
					
				
				if (values[0].equals("State"))
				{
					// Convert place Id to label
					currentPlace = pnmlData.getPlaceById(values[3]);
					values[3] = currentPlace.getName();				

				}
				else if (values[0].equals("Count"))
				{
					// Convert transition Id to label
					currentTransition = pnmlData.getTransitionById(values[3]);
					values[3] = currentTransition.getName();
				}
				
				
				// rebuild the line with the new label
				resultsOutput += "<b> ";
				for(int j=0; j< values.length; j++)
					resultsOutput += values[j] + " ";
				resultsOutput += " </b>\n";
				
				
			}	
			
			// Mean or Variance
			else if (values.length == 4 && (values[1].equals("mean") || values[1].equals("variance")) )
			{
				if (!inTable)
				{
					resultsOutput += "<table width=\"300\" border=\"0\">";
					inTable = true;
				}
				
				if (values[1].equals("mean"))
						type = "Mean";
				else
						type = "Variance";
				
				resultsOutput += "<tr><td>" + type + "</td> <td>&nbsp;</td> <td>" + values[2] + "</td></tr>\n";
				
			}
			
			// Standard Deviation
			else if (values.length == 5 && values[1].equals("std") )
			{
				if (!inTable)
				{
					resultsOutput += "<table width=\"300\" border=\"0\">";
					inTable = true;
				}
				
				resultsOutput += "<tr><td> Standard deviation"+ "</td> <td>&nbsp;</td> <td>" + values[3] + "</td></tr>\n";
				
			}
			
			// Distribution
			else if (values.length == 3 && values[1].equals("distribution"))
			{
				if (!inTable)
				{
					resultsOutput += "<table width=\"300\" border=\"0\">";
					inTable = true;
				}
				
				resultsOutput += "<tr><td> Distribution </td> <td>&nbsp;</td> <td>&nbsp;</td></tr>\n";
			}
			
			// Value of distribution
			else if (values.length == 4 && Character.isDigit( (values[1].charAt(0) )) )
			{
				// Must be within table
				
				resultsOutput += "<tr> <td>&nbsp;</td> <td> " + values[1] + "</td> <td>" + values[2] + "</td></tr>\n";
			}
			
			else		
			{
				if (inTable)
				{
					resultsOutput += "</table>";
					inTable = false;
				}
				resultsOutput += lines[i];
			}
		}
		
		// Ensure that any open table is closed
		if (inTable)
		{
			resultsOutput += "</table>";
			inTable = false;
		}
		
		
		resultsPane.setText(resultsOutput);
		return resultsOutput;
	}


}
