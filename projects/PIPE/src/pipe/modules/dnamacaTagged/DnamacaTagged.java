package pipe.modules.dnamacaTagged;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTextField;

import pipe.common.dataLayer.Arc;
import pipe.common.dataLayer.DataLayer;
import pipe.common.dataLayer.Place;
import pipe.common.dataLayer.Transition;
import pipe.gui.CreateGui;
import pipe.gui.widgets.ButtonBar;
import pipe.gui.widgets.GraphPanelPane;
import pipe.gui.widgets.ResultsHTMLPane;
import pipe.modules.Module;

public class DnamacaTagged implements Module{

	private static final String MODULE_NAME = "DNAmaca for tagged net";
	  private Place places[];
	  private Transition transitions[];
	  private DataLayer pnmldata;
	  private String modString = "";
	  private String tagPlace = "tagged_location";
	  private int taggedPlaceIndex = -1;
	  final int UNTAGGED = 0;
	  final int ORIGINAL = 1;
	  final int CLONED = 2;
	  private boolean tagged = false;
	  
	  private String[] performanceDesc_original;
	  private String[] performanceDesc_clone;
	  private boolean doTokenDistributionForTagged = false;
	  

	  private JDialog guiDialog;
	  private ResultsHTMLPane resultText;
	  
	  
	  public String getName() {
			return MODULE_NAME;
		}
	  
	  
	  public void run(DataLayer _pnmldata){
		  
		  
		  pnmldata = _pnmldata;
		  places = pnmldata.getPlaces();
		  transitions = pnmldata.getTransitions();
		  performanceDesc_original = new String[transitions.length];
		  performanceDesc_clone = new String[transitions.length];
		  
		  
		  System.out.println("\n running dnamaca tagged");
		  
		  
		  //		 Build interface
			guiDialog = new JDialog(CreateGui.getApp(),MODULE_NAME,true);
			
			Container contentPane=guiDialog.getContentPane();
			contentPane.setLayout(new BoxLayout(contentPane,BoxLayout.PAGE_AXIS));

			resultText = new ResultsHTMLPane(pnmldata.getURI());
			contentPane.add(resultText);
			
			String buttonLabels[] = {"Run DNAmaca", "Validate"};
			ActionListener buttonHandlers[] = {runDnamacaAction, validateAction};
			ButtonBar buttons;
			contentPane.add(buttons = new ButtonBar(buttonLabels, buttonHandlers));
			

		    guiDialog.pack();
			guiDialog.setLocationRelativeTo(null);    
			guiDialog.setVisible(true);
		  
	  }
	  
	  
	  ActionListener runDnamacaAction=new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				
				if( !pnmldata.hasValidatedStructure() )
		    		resultText.setText("The structure contains tagged elements and needs to be validated first" );
				
				
				else
				{
					try{
					  	//modFile = File.createTempFile("dnamaca", ".mod");
					   
						//FileWriter out1 = new FileWriter(modFile);
					  
					    //FileWriter out1 = new FileWriter("/homes/wl107/Desktop/dnamaca/dnamaca/bin/current.mod");
						FileWriter out1 = new FileWriter("current.mod");
					    //FileWriter fstream = new FileWriter("/homes/wl107/Desktop/current.txt");
						FileWriter fstream = new FileWriter("current.txt");
						//FileWriter fstream = new FileWriter("C:/current.txt");
					    BufferedWriter out = new BufferedWriter(fstream);
					    generateMod();
					    out.write(modString);
					    out.close();
					    out1.write(modString);
					    out1.close();
					    
					  }
				  catch (Exception e)
				  {
					    System.err.println("Error: " + e.getMessage());
				  }
		  
					
				  boolean outputPerformanceResult = false;
				  String outputResult = "<br><h2>performance results</h2><br/> <p>";
				  String result = "\n";
				  try
				    {
				      Runtime rt = Runtime.getRuntime();
				      
				      Process proc = rt.exec(new String[]{"dnamaca", "current.mod"});
				      
				      InputStream stdin = proc.getInputStream();
				     InputStreamReader isr = new InputStreamReader(stdin);
				      
				      InputStream stderr = proc.getErrorStream();
				      InputStreamReader isr2 = new InputStreamReader(stderr);

				      BufferedReader br = new BufferedReader(isr); 
				      BufferedReader br2 = new BufferedReader(isr2);
				      
				      String line = null;
				      
				      
				      
				      while ( (line = br.readLine()) != null)
				      {
				        System.out.println(line);
				        result+=line + "\n";
				      }
				    
				      int exitVal = proc.waitFor();  
				     
				      System.out.println("Process exitValue: " + exitVal); 
				    } catch (Throwable t) { 
				      t.printStackTrace(); 
				    }    
					 
				    String pe = "\n";
					Pattern p = Pattern.compile(pe);
					String[] lines = p.split(result);
					

				    	for(int i=0; i<lines.length;i++)
						{
							if(lines[i].indexOf("(begin performance results)")>=0)
								outputPerformanceResult = true;
							
							if(outputPerformanceResult)outputResult+= "<br>" + lines[i] + "<br/>";
						}
				    	
				    	outputResult+= "</p>";
				    if(outputPerformanceResult)	{
				    	resultText.setText(outputResult);
				    }
				    else resultText.setText("Error outputing performance result");
	
					
				}
		
			}
			
	  };
	  
	  
	  ActionListener validateAction=new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				boolean result = pnmldata.validTagStructure();
			}
			
	  };
	  
	  
	  private void generateMod() {
			modString = "";
			model();
			method();
			performance();
			
		}
		
		private void model() {
			modString += "\\model{\n";
			stateVector();
			initial();
			transitions();
			
			modString += "}\n\n";		
		}
	  
		private void performance() {
			modString += "\\performance{\n";		
			transitionMeasures();
			tokenDistribution();
			modString += "}\n";		
			
		}
		
		private void tokenDistribution() {
			for(int i=0; i<places.length; i++) {
				
				modString += "\t\\statemeasure{Mean tokens on place " + places[i].getId() + "}{\n";
				modString += "\t\t\\estimator{mean variance distribution}\n";
				modString += "\t\t\\expression{" + places[i].getId() + "}\n";
				modString += "\t}\n";
				
				//if the net contain any tagged element then calculate
				//probability of each place having tagged token
				if(doTokenDistributionForTagged)
				{
					modString += "\t\\statemeasure{Probability of having tagged token on place " + places[i].getId() + "}{\n";
					modString += "\t\t\\estimator{mean}\n";
					modString += "\t\t\\expression{" + places[i].getId() + " > 0" +
							" && tagged_location == "+ getPlaceIndex(places[i].getId()) +"}\n";
					modString += "\t}\n";		
				}
				
			}
		}

		
		private void transitionMeasures() {
			for(int i=0; i<transitions.length; i++) {
				
				Iterator arcsTo = transitions[i]. getConnectToIterator();
				boolean taggedArc = false;
				
			  	while(arcsTo.hasNext())			  		
			  		if(  ((Arc)arcsTo.next()).isTagged() )
			  		{
			  			taggedArc = true;
			  			doTokenDistributionForTagged = true;
			  		}
			  	
			  	
			  	if(taggedArc)
			  	{
			  		transitionMeasuresDesc(i,ORIGINAL);
			  		transitionMeasuresDesc(i,CLONED);
			  	}
			  	else
			  		transitionMeasuresDesc(i,UNTAGGED);
		
				
			}
		}
		
		private void transitionMeasuresDesc(int i, int type){
			
			String id = transitions[i].getId();
			if(type==CLONED) id += "_tagged";
			
			modString += "\t\\statemeasure{Enabled probability for transition " + id + "}{\n";
			modString += "\t\t\\estimator{mean}\n";
			modString += "\t\t\\expression{(";
			
			if(type==UNTAGGED) 
				modString += getTransitionConditions(i);
				
			else if(type==ORIGINAL) 
				modString += performanceDesc_original[i];
				
			else if(type==CLONED)
				modString += performanceDesc_clone[i];
				
			modString += ") ? 1 : 0}\n";
			modString += "\t}\n";
			
			modString += "\t\\countmeasure{Throughput for transition " + id + "}{\n";
			modString += "\t\t\\estimator{mean}\n";
			modString += "\t\t\\precondition{1}\n";
			modString += "\t\t\\postcondition{1}\n";
			modString += "\t\t\\transition{" + id + "}\n";
			modString += "\t}\n";
			
		}
		
		
		private void method()
		{
			modString += "\\solution{\n\t\\method{sor}\n\n}";
		}
		
		private void stateVector() 
		{	
			
			modString += "\t\\statevector{\n";
			modString += "\t\t\\type{short}{";
			
			modString += places[0].getId();
			if(places[0].isTagged())
			{
				taggedPlaceIndex = 0;
				tagged = true;
			}
			
			for(int i=1; i<places.length; i++) {
				modString += ", "+places[i].getId();
				if(places[i].isTagged())
				{
					taggedPlaceIndex = i;
					tagged = true;
				}
				
			}

			if(tagged)modString += ", " + tagPlace;		
				
			modString += "}\n";
			modString += "\t}\n\n";
		}
	  
		private void initial() {
			modString += "\t\\initial{\n";			
			modString += "\t\t";
			for(int i=0; i<places.length; i++) {
				modString += places[i].getId()+" = " + places[i].getCurrentMarking()+"; ";
			}
			if(tagged) modString += tagPlace + " = " + taggedPlaceIndex + ";";
			modString += "\n\t}\n";					
		}
		
		private void transitions()
		{
			
			for(int i=0; i<transitions.length; i++)
			{
			  	boolean taggedArc = false;
				int numInputArc=0;
			  	Iterator arcsTo = transitions[i]. getConnectToIterator();
			  	Iterator arcsFrom = transitions[i]. getConnectFromIterator();
			  	/*since the net has been validated, if the transition has a tagged input arc
			  	 * it must have corresponding tagged output, hence need to check for tagged input
			  	 */ 
			  	while(arcsTo.hasNext()){
			  		
			  		if(  ((Arc)arcsTo.next()).isTagged() )
			  		{
			  			taggedArc = true;
			  			numInputArc++;
			  		}
			  		
			  	}
			  	
			  	if(taggedArc) 
			  	{
				  	writeTransition(ORIGINAL, i, numInputArc);
			  		writeTransition(CLONED, i,  numInputArc);
			  	}
			  	else 
			  	{
				  	writeTransition(UNTAGGED, i, numInputArc);
			  	}
			  		
			}//end for
		}//end transition
		
		
		private void writeTransition(int type, int i, int numInputArc) 
		{
			Iterator arcToTransitions = transitions[i]. getConnectToIterator();
		  	Iterator arcFromTransitions = transitions[i]. getConnectFromIterator();
			
			int[] tagInputPlaceIndex = new int[numInputArc];
			int taggedInput=0;
			int tagOutputPlaceIndex = -1;

		  	
		  	/* if type is CLONED or ORIGINAL, 
		  	 * need to find tagged input places and tagged output place
		  	 */
		  	if(type != UNTAGGED) 
		  	{
		  		
		  		while(arcToTransitions.hasNext())
		  		{
			  		
		  			/*
		  			 * if the arc is tagged, 
		  			 * there's possibility that the place attach to it
		  			 * may contain a tagged token
		  			 */
		  			final Arc arc = ((Arc)arcToTransitions.next());
			  		if( arc.isTagged()  )
			  		{
			  			tagInputPlaceIndex[taggedInput] = getPlaceIndex(((Place)arc.getSource()).getId());
			  			taggedInput++;
			  		}
			  		
			  	}
		  		
		  		while(arcFromTransitions.hasNext())
		  		{
		  			/*
		  			 * obtain tagged outputPlace index
		  			 */
		  			final Arc arc = ((Arc)arcFromTransitions.next());
			  		if( arc.isTagged()  )
			  		{
			  			tagOutputPlaceIndex = getPlaceIndex(((Place)arc.getTarget()).getId());
			  			break;
			  		}
		  			
		  		}
		  		
		  	}//if !untagged

		  		/*
		  		 * 1) write transition id
		  		 */
		  		if(type == ORIGINAL || type == UNTAGGED) 
					modString += "\t\\transition{"+ transitions[i].getId() +"}{\n";
				
				else if(type == CLONED)	
					modString += "\t\\transition{"+ transitions[i].getId() +"_tagged}{\n";
				
		  		
		  		/*
		  		 * 2) write enabling condition 
		  		 */
		  		if(type == UNTAGGED)
					modString += "\t\t\\condition{" + getTransitionConditions(i) + "}\n";
				
		  		/*
		  		 * this is transition in mode ut'
		  		 * fire when the input places doesn't contain tagged token
		  		 * if it contains tagged token, then it is enable if the marking on this
		  		 * place is greater than 1
		  		 */
				else if(type == ORIGINAL) 
				{
					
				  	modString += "\t\t\\condition{(" + getTransitionConditions(i) + 
			  		" && tagged_location != " + tagInputPlaceIndex[0];
				  	
				  	performanceDesc_original[i] = getTransitionConditions(i) + 
			  		" && tagged_location != " + tagInputPlaceIndex[0];
				  	
				  	for(int x=1;x<taggedInput;x++)
				  	{
				  		modString += " && tagged_location!=" + tagInputPlaceIndex[x];
				  		performanceDesc_original[i] += " && tagged_location!=" + tagInputPlaceIndex[x];
				  	}
				  	
				  	modString+= ") || (" + getTaggedTransitionConditions(i, tagInputPlaceIndex)+" )}\n";
				  	performanceDesc_original[i] += " ) || (  " + getTaggedTransitionConditions(i, tagInputPlaceIndex);
				}
		  		/*
		  		 * transition in mode t' can fire when there's 
		  		 * correct marking and tagged_location must be
		  		 * one of the input places
		  		 */
				else if(type == CLONED) 
				{
				  	modString += "\t\t\\condition{(" + getTransitionConditions(i) + 
				  		") && (tagged_location==" + tagInputPlaceIndex[0];
				  	
				  	performanceDesc_clone[i] = getTransitionConditions(i) + ") && (tagged_location==" + tagInputPlaceIndex[0];
				  	
				  	
				  	for(int x=1;x<taggedInput;x++)
				  	{
				  		modString += " || tagged_location==" + tagInputPlaceIndex[x];
				  		performanceDesc_clone[i] += " || tagged_location==" + tagInputPlaceIndex[x];
				  	}
	
				  	modString += ")}\n";
				}
		  		
		  		
		  		/*
		  		 * 3) write action
		  		 */
		  		modString += "\t\t\\action{\n";
		  		arcToTransitions = transitions[i]. getConnectToIterator();
			  	arcFromTransitions = transitions[i]. getConnectFromIterator();
			  	
			  	int[][] incidenceMatrix = pnmldata.getIncidenceMatrix();
			  	
			  	while (arcToTransitions.hasNext()) 
			  	{
			  		final Arc arc = ((Arc)arcToTransitions.next());
			  		String currentId = arc.getSource().getId();
			  		int placeNo = this.getPlaceIndex(currentId);
			  		if( incidenceMatrix!=null && incidenceMatrix[placeNo][i]<0  )
			  		{
			  			modString += "\t\t\tnext->"+currentId;
			  			modString += " = "+currentId+" - " + arc.getWeight() +  ";\n";
			  		}
			  	}
			  	while (arcFromTransitions.hasNext()) 
			  	{
			  		final Arc arc = ((Arc)arcFromTransitions.next());
			  		String currentId = arc.getTarget().getId();
			  		int placeNo = this.getPlaceIndex(currentId);
			  		if( incidenceMatrix!=null && incidenceMatrix[placeNo][i]>0  )
			  		{
			  			modString += "\t\t\tnext->"+currentId;
			  			modString += " = "+currentId+" + " + arc.getWeight() +  ";\n";
			  		}
			  	}
			  	
			  	if(type==CLONED)
					modString += "\t\t\tnext->tagged_location=" + tagOutputPlaceIndex + ";\n"; 
				
				
				modString += "\t\t}\n";
		  		
				/*
				 * 4) rate and weight
				 */
				if(type == UNTAGGED)
				{
					if (transitions[i].isTimed()) 
				  		modString += "\t\t\\rate{" + transitions[i].getRate();  	
					else 
				  		modString += "\t\t\\weight{" + transitions[i].getRate();
					
					if(transitions[i].isInfiniteServer())
					{
						String tagged_place = "(";
						
						arcToTransitions = transitions[i]. getConnectToIterator();
						if(arcToTransitions.hasNext()) 
					  	{
					  		tagged_place += ((Arc)arcToTransitions.next()).getSource().getId();;
					  	}
						while(arcToTransitions.hasNext()) 
					  	{
					  		final Arc arc = ((Arc)arcToTransitions.next());
					  		String currentId = arc.getSource().getId();
					  		tagged_place += "+" + currentId;
					  	}
							
						tagged_place += ")";
						
						modString += "*" + tagged_place + "}\n";	  	
					}
					else
					{
						modString += "}\n";
					}
					
				  	
				}
				else if(type == ORIGINAL)
				{
					double rate = transitions[i].getRate();
					
					if (transitions[i].isTimed())			  	
				  		modString += "\t\t\\rate{";
				  	
				  	else // not timed transition
				  		modString += "\t\t\\weight{";
					
						
					if(transitions[i].isInfiniteServer())
					{
						
						String tagged_place = "(";
						
						arcToTransitions = transitions[i]. getConnectToIterator();
						if(arcToTransitions.hasNext()) 
					  	{
					  		tagged_place += ((Arc)arcToTransitions.next()).getSource().getId();;
					  	}
						while(arcToTransitions.hasNext()) 
					  	{
					  		final Arc arc = ((Arc)arcToTransitions.next());
					  		String currentId = arc.getSource().getId();
					  		tagged_place += "+" + currentId;
					  	}
							
						tagged_place += ")";
				  	
							
							modString += " tagged_location== " + tagInputPlaceIndex[0] + 
				  			"? ( (double)" + rate + " -  ((double)"+ getArcWeightFromPlace(places[tagInputPlaceIndex[0]].getId(), i ) 
				  			+ "/(double)" + places[tagInputPlaceIndex[0]].getId() +") * " + rate + ") * " + tagged_place + " : ";
				  		
							int index=1;
				  		
							while(index<taggedInput)
							{
								modString += " tagged_location== " + tagInputPlaceIndex[index] + 
								"? ( (double)" + rate + " -  ((double)"+ getArcWeightFromPlace(places[tagInputPlaceIndex[index]].getId(), i ) 
								+ "/(double)" + places[tagInputPlaceIndex[index]].getId() +") * " + rate + ") * " + tagged_place + " : ";
				  		
								index++;
							}
				  		
				  		
							modString += rate + "*" + tagged_place +"}\n";
	
					}
						
					else //not infiniteServer
					{
							modString += " tagged_location== " + tagInputPlaceIndex[0] + 
				  			"? ( (double)" + rate + " -  ((double)"+ getArcWeightFromPlace(places[tagInputPlaceIndex[0]].getId(), i ) 
				  			+ "/(double)" + places[tagInputPlaceIndex[0]].getId() +") * " + rate + ") : ";
				  		
							int index=1;
				  		
							while(index<taggedInput)
							{
								modString += " tagged_location== " + tagInputPlaceIndex[index] + 
								"? ( (double)" + rate + " -  ((double)"+ getArcWeightFromPlace(places[tagInputPlaceIndex[index]].getId(), i ) 
								+ "/(double)" + places[tagInputPlaceIndex[index]].getId() +") * " + rate + ") : ";
				  		
								index++;
							}
							modString += rate +"}\n";	
					}

				}// end else if original
				else if(type == CLONED)
				{
					double rate = transitions[i].getRate();
					
				  	if (transitions[i].isTimed())			  	
				  		modString += "\t\t\\rate{";
				  	
				  	else // not timed transition
				  		modString += "\t\t\\weight{";
				  	
				  	if(transitions[i].isInfiniteServer())
				  	{
				  		
				  		String tagged_place = "(";
						
						arcToTransitions = transitions[i]. getConnectToIterator();
						if(arcToTransitions.hasNext()) 
					  	{
					  		tagged_place += ((Arc)arcToTransitions.next()).getSource().getId();;
					  	}
						while(arcToTransitions.hasNext()) 
					  	{
					  		final Arc arc = ((Arc)arcToTransitions.next());
					  		String currentId = arc.getSource().getId();
					  		tagged_place += "+" + currentId;
					  	}
							
						tagged_place += ")";
							
				  			modString += "tagged_location== " + tagInputPlaceIndex[0] + 
				  			"? ((double)"+ getArcWeightFromPlace(places[tagInputPlaceIndex[0]].getId(), i ) 
				  			+ "/(double)" + places[tagInputPlaceIndex[0]].getId() +") * " + rate + "*" + tagged_place + " : ";
				  		
				  			int index=1;
				  		
				  			while(index<taggedInput)
				  			{
				  				modString += " tagged_location== " + tagInputPlaceIndex[index] + 
				  				"? ((double)"+ getArcWeightFromPlace(places[tagInputPlaceIndex[index]].getId(), i ) 
				  				+ "/(double)" + places[tagInputPlaceIndex[index]].getId() +") * " + rate + "*" + tagged_place + " : ";
				  		
				  				index++;
				  			}
				  		
				  		
				  			modString += rate + "*" + tagged_place +"}\n";
				  			
				  	}
				  		
				  	else // not InfiniteServer
				  	{
				  			
				  			modString += "tagged_location== " + tagInputPlaceIndex[0] + 
				  			"? ((double)"+ getArcWeightFromPlace(places[tagInputPlaceIndex[0]].getId(), i ) 
				  			+ "/(double)" + places[tagInputPlaceIndex[0]].getId() +") * " + rate + " : ";
				  		
				  			int index=1;
				  		
				  			while(index<taggedInput)
				  			{
				  				modString += " tagged_location== " + tagInputPlaceIndex[index] + 
				  				"? ((double)"+ getArcWeightFromPlace(places[tagInputPlaceIndex[index]].getId(), i ) 
				  				+ "/(double)" + places[tagInputPlaceIndex[index]].getId() +") * " + rate + " : ";
				  		
				  				index++;
				  			}
				  		
				  			modString += rate +"}\n";
		
				  	}

				}//end else if cloned
				  	
				modString += "\t}\n";
				
			
		}//end writeTransition
		

		public int getArcWeightFromPlace (String placeId, int i)
		{
			Iterator arcsTo = transitions[i]. getConnectToIterator();
			
			//System.out.println("\n in get arc weight " + placeId);
			while(arcsTo.hasNext())
			{
				final Arc arc = (Arc)arcsTo.next();
				//System.out.println("\n"+arc.getSource().getId());
				if( arc.getSource().getId() == placeId ){
					//System.out.println("\nfound match");
					return arc.getWeight();
				}
			}
			
			return -1;
		}
		
		/*
		 * for each of the place connected to input tagged arc,
		 * check if it equals to tagged_location, if yes then, it must
		 * have marking one more greater than the backward incidence function
		 * otherwise, everything is normal
		 */
		private String getTaggedTransitionConditions(int transitionNum, int[] tagInputPlace) {
			
			String condition = new String();
		  	Iterator arcsTo = transitions[transitionNum]. getConnectToIterator();
		  	if (arcsTo.hasNext()){
		  		final Arc arc = (Arc)arcsTo.next();
		  		if(arc.isTagged())
		  		{
		  			condition += "((tagged_location== "+getPlaceIndex(arc.getSource().getId())
		  				+ " && " + arc.getSource().getId()+" > "+ (arc.getWeight()-1+1)
		  				+ ") || ( tagged_location!="+getPlaceIndex(arc.getSource().getId())
		  				+ " && " + arc.getSource().getId()+" > "+ (arc.getWeight()-1)
		  				+ "))";
		  		}
		  		else condition += arc.getSource().getId()+" > "+ (arc.getWeight() - 1);
		  	}
		  		
		  	while (arcsTo.hasNext())
		  	{
		  		
		  		final Arc arc = (Arc)arcsTo.next();
		  		if(arc.isTagged())
		  		{
		  			condition += " && ((tagged_location== "+getPlaceIndex(arc.getSource().getId())
		  				+ " && " + arc.getSource().getId()+" > "+ (arc.getWeight()-1+1)
		  				+ ") || ( tagged_location!="+getPlaceIndex(arc.getSource().getId())
		  				+ " && " + arc.getSource().getId()+" > "+ (arc.getWeight()-1)
		  				+ "))";
		  		}
		  		else condition += " && "+arc.getSource().getId()+" > "+ (arc.getWeight() - 1);
	  		
		  	}
		  	
		  	return condition;

		}
		
		private int getPlaceIndex(String placeName){		
			int index = -1;
			for(int i=0; i<places.length; i++) {		
				if(places[i].getId()==placeName)
				{
					index = i;
					break;
				}
			}
//			System.out.println("Returning " + index);
			
			return index;
		}
		
		private String getTransitionConditions(int transitionNum) {
			
			String condition = new String();
		  	Iterator arcsTo = transitions[transitionNum]. getConnectToIterator();
		  	if (arcsTo.hasNext()){
		  		final Arc arc = (Arc)arcsTo.next();
		  		condition += arc.getSource().getId()+" > "+ (arc.getWeight() - 1);
		  	}
		  		
		  	while (arcsTo.hasNext())
		  	{
		  		final Arc arc = (Arc)arcsTo.next();
		  		condition += " && "+arc.getSource().getId()+" > "+ (arc.getWeight() - 1);
		  	}
		  	return condition;
			
		}
		
}
