/*
 * Created on 29-Jun-2005
 */
package pipe.modules.gspn;

import jama.Matrix;

import java.text.DecimalFormat;
import java.util.ArrayList;

import pipe.common.dataLayer.Arc;
import pipe.common.dataLayer.DataLayer;
import pipe.common.dataLayer.PNMatrix;
import pipe.common.dataLayer.Place;
import pipe.common.dataLayer.PlaceTransitionObject;
import pipe.common.dataLayer.Transition;
import pipe.dataLayer.calculations.StateList;
import pipe.dataLayer.calculations.TreeTooBigException;
import pipe.dataLayer.calculations.myTree;
import pipe.gui.widgets.PetriNetChooserPanel;
import pipe.gui.widgets.ResultsHTMLPane;
import pipe.modules.classification.Classification;

/**
 * @author Nadeem
 * @author minor changes Will Master 02/2007
 *
 * This class is used by all the GSPN analysis modules. It contains data and functions
 * required by all of them.
 */
public class GSPN extends Classification{
	
	protected PetriNetChooserPanel sourceFilePanel;
	protected ResultsHTMLPane results;
	
	//	######################################################################################################################
	/**Qualitative analysis - see if the supplied GSPN is an EFC-GSPN.  This is a necessary precondition
	 * for quantitative analysis.
	 * @param DataLayer
	 * 
	 */
	private boolean isEFCGSPN (DataLayer pnmlData) {
		return extendedFreeChoiceNet(pnmlData);
	}
	
	//	######################################################################################################################
	/**Get the initial marking of the supplied net
	 * 
	 * @param pnmlData
	 * @return
	 */
	private int[] getMarking(DataLayer pnmlData){

		int places = pnmlData.getPlacesCount();
		int[] marking = new int[places];
		for (int i = 0; i <places; i++){
			marking[i] = pnmlData.getPlace(i).getInitialMarking();
		}
		return marking;
	}
	
	//	######################################################################################################################	
	/**Caluculate whether a transition is enabled given a specific marking
	 * 
	 * @param DataLayer - the net  
	 * @param int[] - the marking
	 * @param int - the specific transition to test for enabled status
	 * @return boolean - an array of booleans specifying which transitions are enabled in the specified marking
	 */
	protected boolean getTransitionEnabledStatus(DataLayer pnmlData, int[] marking, int transition) {
//		int transCount = pnmlData.getTransitions().length;
		int transCount = pnmlData.getTransitionsCount();
		boolean[] result;
		result = new boolean[transCount];
		boolean answer;
		int[][] CMinus = pnmlData.getBackwardsIncidenceMatrix();
		int placeCount = pnmlData.getPlacesCount();
		
		for (int k = 0; k < transCount; k++) { //initialise matrix to true
			result[k] = true;
		}
		for (int i = 0; i <transCount;i++) {
			for (int j = 0; j <placeCount; j++){
				if (marking[j] < CMinus[j][i])
					result[i] = false;
			}
		}
		//print(result);
		return result[transition];
	}
	
	//######################################################################################################################	
	/**Generate the reachability set using myTree function
	 * Add each marking to an arraylist, testing to see if the
	 * marking is already present before adding.
	 * 
	 *
	 * @param DataLayer
	 * @return
	 */
	protected StateList getReachabilitySet (DataLayer pnmlData) throws TreeTooBigException {
		int [][] fim = pnmlData.getForwardsIncidenceMatrix();
		int [][] bim = pnmlData.getBackwardsIncidenceMatrix();
		PNMatrix plus = new PNMatrix (fim);
		PNMatrix minus = new PNMatrix(bim);
		int[] marking = pnmlData.getCurrentMarkingVector();
		int markSize = pnmlData.getPlacesCount();
		StateList reachSetArray = new StateList();
		myTree reachSet = new myTree(marking, plus, minus, reachSetArray, pnmlData);
		return reachSetArray;
	}
	
	//	######################################################################################################################	
	/**Caluculate which transitions are enabled given a specific marking
	 * 
	 * @param DataLayer - the net  
	 * @param int[] - the marking
	 * @return boolean[] - an array of booleans specifying which transitions are enabled in the specified marking
	 */
	protected boolean[] getTransitionEnabledStatusArray(DataLayer pnmlData, int[] marking) {
//		int transCount = pnmlData.getTransitions().length;
		int transCount = pnmlData.getTransitionsCount();
		boolean[] result;
		result = new boolean[transCount];
		boolean hasTimed = false;
		boolean hasImmediate = false;
		int[][] CMinus = pnmlData.getBackwardsIncidenceMatrix();
		int placeCount = pnmlData.getPlacesCount();
		Transition[] transArray = pnmlData.getTransitions();
		
		for (int k = 0; k < transCount; k++) { //initialise matrix to true
			result[k] = true;
		}
		for (int i = 0; i <transCount;i++) {
			for (int j = 0; j <placeCount; j++){
				if (marking[j] < CMinus[j][i])
					result[i] = false;
			}
		}
		
		return result;
	}
	//######################################################################################################################	
	protected boolean[] getTangibleTransitionEnabledStatusArray(DataLayer pnmlData, int[] marking) {
//		int transCount = pnmlData.getTransitions().length;
		int transCount = pnmlData.getTransitionsCount();
		boolean[] result;
		result = new boolean[transCount];
		boolean hasTimed = false;
		boolean hasImmediate = false;
		int[][] CMinus = pnmlData.getBackwardsIncidenceMatrix();
		int placeCount = pnmlData.getPlacesCount();
		Transition[] transArray = pnmlData.getTransitions();
		
		for (int k = 0; k < transCount; k++) { //initialise matrix to true
			result[k] = true;
		}
		for (int i = 0; i <transCount;i++) {
			for (int j = 0; j <placeCount; j++){
				if (marking[j] < CMinus[j][i])
					result[i] = false;
			}
		}
		for (int i = 0; i<transCount; i++) {
			if (transArray[i].isTimed()==true){
				hasTimed = true;
			}
			else {
				hasImmediate = true;
			}
		}
		if (hasTimed&&hasImmediate){
			for (int i = 0; i<transCount; i++){
				if (transArray[i].isTimed()==true){
					result[i] = false;
				}
			}
		}
		//print(result);
		return result;
	}
	
	//######################################################################################################################
	/**Work out if a specified marking describes a tangible state.
	 * A state is either tangible (all enabled transitions are timed)
	 * or vanishing (there exists at least one enabled state that is transient, i.e. untimed).
	 * If an immediate transition exists, it will automatically fire before a timed transition.
	 * @param DataLayer - the net to be tested
	 * @param int[] - the marking of the net to be tested
	 * @return boolean - is it tangible or not
	 */
	
	protected boolean isTangibleState(DataLayer pnmlData, int[] marking) {
		Transition[] trans = pnmlData.getTransitions();
		int numTrans = trans.length;
		boolean hasTimed = false;
		boolean hasImmediate = false;
		for (int i = 0; i < numTrans; i++ ){
			if (getTransitionEnabledStatus(pnmlData, marking, i) == true){
				if (trans[i].isTimed()== true){  
					//If any immediate transtions exist, the state is vanishing 
					//as they will fire immediately
					hasTimed = true;
				}
				else if (trans[i].isTimed()!= true) {
					hasImmediate = true;
				}
			}
		}
		if (hasTimed == true && hasImmediate == false)	
			return true;
		else
			return false;
	}
	
	//	######################################################################################################################
	/**Test for condition Equal Conflict.  I.E., for all t1, t2
	 * in the set of transitions, where t1<>t2, that share the same
	 * input place, either t1, t2 are both in the set of timed transitions (T1)
	 * or t1, t2 are both in the set of immediate transitions (T2).
	 * 
	 * @param DataLayer
	 * @return boolean
	 */
	protected boolean testEqualConflict (DataLayer pnmlData) {
		
		Place[] places = pnmlData.getPlaces();		
		Arc[] arcs = pnmlData.getArcs();
		int arcsCount = arcs.length;
		int placesCount = pnmlData.getPlacesCount();
		
		for (int i = 0; i < placesCount ; i++)
		{
			boolean hasTimed = false;
			boolean hasUntimed = false;
			//get arcs with places[i] as source
			for (int j = 0; j < arcsCount; j++)
			{	
				if (arcs[j].getSource()==places[i]){
					PlaceTransitionObject targ = arcs[j].getTarget();
					if (((Transition)targ).isTimed() == true) {	
						hasTimed = true;
					} else {
						hasUntimed = true;
					}
				}
				if (hasTimed== true && hasUntimed == true)
					return false;
			}
		}
		return true;
	}
	
	//	######################################################################################################################	
	
	/**
	 * See if the supplied net has any timed transitions.
	 * @param DataLayer
	 * @return boolean
	 * @author Matthew
	 *
	 */
	public boolean hasTimedTransitions(DataLayer pnmlData){
		Transition[] transitions = pnmlData.getTransitions();
		int transCount = transitions.length;
		boolean hasTimed = false;
		int length = transitions.length;
		
		for (int i = 0; i< length; i++) {
			if (transitions[i].isTimed()==true)
				hasTimed = true;
		}
		
		if (hasTimed == true)
			return true;
		else 
			return false;
		
	}
	
	//######################################################################################################################	
	
	/**
	 * See if the supplied net has any timed transitions.
	 * @param DataLayer
	 * @return boolean
	 * @author Matthew
	 *
	 */
	public boolean hasImmediateTransitions(DataLayer pnmlData){
		Transition[] transitions = pnmlData.getTransitions();
		int transCount = transitions.length;
		boolean hasImmediate = false;
		int length = transitions.length;
		
		for (int i = 0; i< length; i++) {
			if (transitions[i].isTimed()==false)
				hasImmediate = true;
		}
		
		if (hasImmediate == true)
			return true;
		else 
			return false;
		
	}
	
	//	######################################################################################################################
	/**This function performs a Gaussian reduction on a given Matrix, returning an array of values representing the solution.
	 * @param Matrix - the matrix of coefficients to be solved
	 * @return double[] - the array of solutions
	 * 
	 */
	protected double[] reduction(Matrix input) {
		int row = input.getRowDimension();
		int col = input.getColumnDimension();
		double[] result = new double[col-1];
		//initialise results to 0 - have n-1 unknowns in n equations, so result can be 1 less than size of input matrix.
		for (int i = 0; i<col-1; i++){
			result[i] = 0;
		}
		//***********************************************************
		//First stage - reduce matrix of coefficients by substitution
		//***********************************************************
		boolean reducedThisRow = false;
		
		//Start - first row should have 1 as each coefficient.  Test if second row has 0 as coeffiecient - if so, move on and swap.
		for (int i = 0; i < row - 1; i++){
			for (int j = i + 1; j <row; j++){
				if ((input.get(j, i)== 0.0)&& reducedThisRow == false ){ //if the element is 0 and we haven't already  
					int k = j;											//reduced a row, search down the list till we find one, then swap it into the current position
					while ((input.get(k,i)== 0.0)&& k < row - 1 ) {
						k++;
					}
					if (k < row) {
						swapRows(input, j, k);
					} else {
						throw new ArithmeticException("Not enough parameters to calculate result");
					}
				} else if (input.get(j,i)!=0.0){					//reduce the row coeffecients by arithmetic substitution.
					double factor = ((input.get(i, i))/(input.get(j, i)));
					//System.out.println(factor + " Factor");
					//System.out.println(input.get(i,i) +" input.get(i,i)");
					//System.out.println(input.get(j,i) +" input.get(i,j)");
					multiplyRow(input,j,factor);
					subtractRow(input,i,j);
					//input.print(8,5);				//if the coefficient is 0 and we've already performed a reduction in this pass
					reducedThisRow = true;			//take no action and move onto the next
				}								
				
			}
			reducedThisRow = false;					
		}
		//************************************
		//next stage - backwards substitution.
		//************************************
		for (int i= row - 2; i>= 0; i--) {
			double backSub = 0;
			for (int j = i+1; j<row-1; j++) {	
				backSub = backSub + (result[j]*input.get(i,j));
			}
			result[i] = (input.get(i,row - 1) - backSub)/input.get(i,i);
		}
		return result;
	}
	
	//######################################################################################################################	
	//Helper function for reduction function
	private void swapRows (Matrix input, int row1, int row2) {
		int col = input.getColumnDimension();
		double temp;
		for (int i = 0; i < col; i++) {
			temp = input.get(row1,i);
			input.set(row1,i,input.get(row2,i));
			input.set(row2,i,temp);
		}
	}
	
	//######################################################################################################################	
	//Helper function for reduction function
	private void multiplyRow(Matrix input, int row, double factor) {
		int col = input.getColumnDimension();
		for (int i = 0; i <col ; i++){
			double newVal = (input.get(row,i))*factor;
			input.set(row, i,newVal);
		}
	}
	
	//######################################################################################################################	
	//subtract the values of row1 from the values of row2 
	private void subtractRow(Matrix input, int row1, int row2) {
		int col = input.getColumnDimension();
		for (int i = 0; i <col; i++) {
			double r1 = input.get(row1,i);
			double r2 = input.get(row2,i);
			//System.out.println(r1 +" r1 " + r2 + " r2 " + i + " i ");
			input.set(row2,i,(r2 - r1));
		}
	}
	
	//	######################################################################################################################
	
	private void printMatrix(double[][] input) {
		int rows = input.length;
		int cols = input[0].length;
		System.out.println("Printing a matrix of "+ rows +" rows and " + cols +" columns.");
		for (int i = 0; i<rows; i++) {
			for (int j = 0; j < cols; j++) {
				System.out.print(input[i][j] + " ");
			}
			System.out.println("");
		}
	}
	
	//######################################################################################################################
	
	private void printMatrix(int[][] input) {
		int rows = input.length;
		int cols = input[0].length;
		System.out.println("Printing a matrix of "+ rows +" rows and " + cols +" columns.");
		for (int i = 0; i<rows; i++) {
			for (int j = 0; j < cols; j++) {
				System.out.print(input[i][j] + " ");
			}
			System.out.println("");
		}
	}	
	
	//######################################################################################################################
	
	private void printMarking (int[] marking) {
		int rows = marking.length;
		System.out.print("Marking as follows: ");
		for (int i = 0; i < rows; i++) {
			System.out.print(marking[i] + " ");
		}
		System.out.println();
	}
	
	//Format StateList data nicely.
	protected String renderStateSpace(DataLayer pnmlData, StateList data) {
		if((data.size()==0)||(data.get(0).length==0)) return "n/a";
		int markSize = data.get(0).length;
		ArrayList result=new ArrayList();
		// add headers to table
		result.add("");
//		Place[] places = super.getPlaces(pnmlData);
		Place[] places = pnmlData.getPlaces();
		for (int i=0;i<markSize;i++) 
		{result.add(places[i].getName());
		//result.add("<A NAME= 'M" + i + "'></A>");
		}
		
		for (int i=0; i<data.size(); i++) {
			result.add(data.getID(i));
			for (int j=0; j<markSize; j++)
				result.add(Integer.toString(data.get(i)[j]));
		}
		
		return ResultsHTMLPane.makeTable(result.toArray(),markSize + 1,false,true,true,true);
	}
	
	
	//######################################################################################################################
	
	//Format StateList data nicely.
	protected String renderStateSpaceLinked(DataLayer pnmlData, StateList data) {
		if((data.size()==0)||(data.get(0).length==0)) return "n/a";
		int markSize = data.get(0).length;
		ArrayList result=new ArrayList();
		// add headers to table
		result.add("");
		Place[] places = pnmlData.getPlaces();
		for (int i=0;i<markSize;i++) 
		{result.add(places[i].getName());
		//result.add("<A NAME= 'M" + i + "'></A>");
		}
		
		for (int i=0; i<data.size(); i++) {
			result.add(data.getID(i)+ "<A NAME= 'M" + i + "'></A>");
			for (int j=0; j<markSize; j++)
				result.add(Integer.toString(data.get(i)[j]));
		}
		
		return ResultsHTMLPane.makeTable(result.toArray(),markSize + 1,false,true,true,true);
	}
	
	//######################################################################################################################
	
	//Format throughput data nicely.
	protected String renderThroughput(DataLayer pnmlData, double[] data) {
		if((data.length)==0) return "n/a";
		int transCount = data.length;
		ArrayList result=new ArrayList();
		// add headers to table
		result.add("Transition");
		result.add("Throughput");
		DecimalFormat f=new DecimalFormat();
		f.setMaximumFractionDigits(5);
		Transition[] transitions = pnmlData.getTransitions();
		for (int i=0;i<transCount;i++) {
			result.add(transitions[i].getName());
			result.add(f.format(data[i]));
		}
		
		return ResultsHTMLPane.makeTable(result.toArray(),2,false,true,true,true);
	}
	
	//######################################################################################################################	
	
	//Format probability matrices nicely
	protected String renderProbabilities(double[][] probabilities, StateList list1, StateList list2) {
		if((list1.size()==0)||(list2.get(0).length==0)) return "n/a";
		int rows = list1.size();
		int cols = list2.size();
		ArrayList result=new ArrayList();
		// add headers to table
		result.add("");
		for (int i=0;i<cols;i++) result.add("<A HREF='#M" + i + "'>" + list2.getID(i)+ "</A>");
		
		DecimalFormat f=new DecimalFormat();
		f.setMaximumFractionDigits(5);
		for (int i=0; i<rows; i++) {
			result.add("<A HREF='#M" + i + "'>" + list1.getID(i)+ "</A>");
			for (int j=0; j<cols; j++)
				result.add(f.format(probabilities[i][j]));
		}
		
		return ResultsHTMLPane.makeTable(result.toArray(),cols + 1,false,true,true,true);
	}
	
	//######################################################################################################################	
	
	//Format lists of doubles nicely
	protected String renderLists(double[] data, StateList list) {
		if((list.size()==0)) return "n/a";
		int rows = list.size();
		
		ArrayList result=new ArrayList();
		// add headers to table
		result.add("Marking");
		result.add("Value");
		
		DecimalFormat f=new DecimalFormat();
		f.setMaximumFractionDigits(5);
		for (int i=0; i<rows; i++) {
			result.add("<A HREF='#" + list.getID(i) + "'>" + list.getID(i).toString().toUpperCase()+ "</A>");
			result.add(f.format(data[i]));
		}
		
		return ResultsHTMLPane.makeTable(result.toArray(),2,false,true,true,true);
	}
	
	protected String renderLists(double[] data, Place[] places, String[] headings) {
		int rows = data.length;
		
		ArrayList result=new ArrayList();
		// add headers to table
		result.add(headings[0]);
		result.add(headings[1]);
		
		DecimalFormat f=new DecimalFormat();
		f.setMaximumFractionDigits(5);
		for (int i=0; i<rows; i++) {
			result.add(places[i].getName());
			result.add(f.format(data[i]));
		}
		
		return ResultsHTMLPane.makeTable(result.toArray(),2,false,true,true,true);
	}
	
}
