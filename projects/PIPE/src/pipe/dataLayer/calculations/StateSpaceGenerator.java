/*
 * Created on 29-Jun-2005
 */
package pipe.dataLayer.calculations;

import java.io.EOFException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

import pipe.common.dataLayer.DataLayer;
import pipe.common.dataLayer.Transition;
import pipe.gui.widgets.ResultsHTMLPane;
import pipe.io.ImmediateAbortException;
import pipe.io.RGFileHeader;
import pipe.io.StateRecord;
import pipe.io.TransitionRecord;

/**
 * @author Nadeem
 * @author Will Master minor changes 02/2007
 * @author Edwin Chung/Matthew Worthington/Will Master- changes to 
 * accomodate requirements of reachability graph module, overloading generate 
 * method to produce both tangible and non tangible states of net. 
 * This class is used to generate the state space of a
 * General Stochastic Petri-Net model. It uses an algorithm
 * developed by Dr. William Knottenbelt in his MSc. thesis
 * "Generalised Markovian Analysis of Timed Transition
 * Systems" June 1996 Dept. of Computer Science, University
 * of Cape Town. The algorithm eliminates vanishing states
 * 'on the fly' and creates the infinitessimal generator
 * matrix 'Q' at the same time.
 * 
 * There is just one public method and it is static.
 * It returns the state space array and the matrix Q.
 */
public class StateSpaceGenerator {
	
	private static final boolean DEBUG = true;
	private static final boolean RATE = false;
	private static final boolean PROBABILITY = true;
	private static final int NUMHASHROWS = 46567;

//  Array storing the transitions fired
	
	private static Stack transitions = new Stack();

	StateSpaceGenerator(){
		// Empty constructor
		// This class will never be instantiated.
	}
	
	public static void test(){
		int[] array1 = {1,2,3};
		int[] array2 = {1,2,1};
		
		State state1 = new State(array1);
		State state2 = new State(array2);
	}
	
	public static void generate(DataLayer pnmlData, File reachGraph) throws OutOfMemoryError, TimelessTrapException, ImmediateAbortException, IOException{

		State current_marking = new State(pnmlData.getCurrentMarkingVector());
		int statearraysize = current_marking.getState().length;

		Queue statesQueue = new Queue();// States waiting to be explored
		Stack tansuccessor = new Stack();
		// Objects for temporarily storing states that haven't been identified as tangible or vanishing
		State sprime = null;
		Marking currentState = null; // Used in some loops
		Marking s = null; // Used in some loops

		// A record of all explored states. The actual states themselves
		// are not stored here. Instead, just their two hashcodes are used
		// to represent them, one as a key to the hashtable row, the other
		// as the entry in a list at each hashtable row.
		LinkedList[] exploredStates = new LinkedList[NUMHASHROWS];

		// This list is used to temporarily store details
		// of the arcs between states on the reachability graph.
		LinkedList localarcs = new LinkedList();

		// Counters used for creating the reachability graph file
		int numStates = 0;
		int numTransitions = 0;
		int numtransitionsfired = 0;

		// Temporary files for storing tangible states and
		// the transitions between them. They are later
		// combined into one file by the createRGFile method
		RandomAccessFile outputFile;
		RandomAccessFile esoFile;
		File intermediate = new File("graph.irg");

		if(intermediate.exists()){
			if(!intermediate.delete()){
				System.err.println("Could not delete intermediate file.");
			}
		}

		try {
			outputFile = new RandomAccessFile(intermediate, "rw");
			esoFile = new RandomAccessFile(reachGraph, "rw");
			// Write a blank file header as a place holder for later
			RGFileHeader header = new RGFileHeader();
			header.write(esoFile);
		} catch (IOException e) {
			System.out.println("Could not create intermediate files.");
			return;
		}

		numStates++;
		currentState = new Marking(current_marking, numStates-1, isTangible(pnmlData, current_marking));
		statesQueue.enqueue(currentState);
		addExplored(currentState, exploredStates, esoFile, true);

		while(!statesQueue.isEmpty()){

			s = (Marking)statesQueue.dequeue();

			numtransitionsfired += fire(pnmlData, s, tansuccessor, true);
			while(!tansuccessor.isEmpty()){
				sprime = (State)tansuccessor.pop();
				if(!explored(sprime, exploredStates)){
					numStates++;
					currentState = new Marking(sprime, numStates-1, isTangible(pnmlData, sprime));
					statesQueue.enqueue(currentState);
					addExplored(currentState, exploredStates, esoFile, true);
				}
				else{
					int id = identifyState(sprime, exploredStates);
					if(id==-1)
						throw new ImmediateAbortException("Could not identify previously explored tangible state.");
					currentState = new Marking(sprime, id);
				}
				numTransitions += transition(currentState, rate(pnmlData, s, sprime), localarcs);
			}
			// Write all the arcs for the reachability graph to file
			writeTransitions(s, localarcs, outputFile,true);
			// Clear the list so can start again with the next set of arcs
			localarcs = new LinkedList();
			if (numTransitions>400) throw new OutOfMemoryError("The net generates in excess of 20000 states");
		}
		try {
			outputFile.close();
		} catch (IOException e1) {
			System.err.println("\nCould not close intermediate file.");
		}
		System.out.println("\nGenerate Ends, " + numStates + " states found with " + numTransitions + " arcs.");
		createRGFile(intermediate, esoFile, statearraysize, numStates, numTransitions,true);

		if(DEBUG){
			/* Write a csv file indicating the Hashtable distribution */
			FileWriter htdist = new FileWriter("HashTableDist.csv");
			for(int row = 0; row < NUMHASHROWS; row++){
				htdist.write(Integer.toString(row) + ",");
				if(exploredStates[row] == null)
					htdist.write("0\n");
				else
					htdist.write(Integer.toString(exploredStates[row].size()) + "\n");
			}
			htdist.close();
			System.out.println("Finished writing hashtable distribution to file.");
		}

		if(intermediate.exists()){
			if(!intermediate.delete()){
				System.err.println("\nCould not delete intermediate file.");
			}
		}
	}

	
	/**
	 * generate()
	 * 
	 * This static method generates the statespace from a GSPN
	 * It uses a hashtable so that it can quickly check whether
	 * a state has already been explored.
	 * @throws TimelessTrapException
	 * @throws ImmediateAbortException
	 * @throws IOException
	 * 
	 * @author Nadeem
	 * 30/06/2005
	 *
	 */
	public static void generate(DataLayer pnmlData, File reachGraph, ResultsHTMLPane resultspane) throws OutOfMemoryError, TimelessTrapException, ImmediateAbortException, IOException{

		// This is used to catch timeless traps. It's the maximum number
		// of attempts to try and get successors to vanishing states.
		final int MAX_TRIES = 100000;
		
		State current_marking = new State(pnmlData.getCurrentMarkingVector());
		int statearraysize = current_marking.getState().length;
		
		Queue tangibleStates = new Queue();// Tangible states waiting to be explored
		Stack vanishingStates = new Stack();// Vanishing states waiting to be explored and then eliminated
		Stack vansuccessor = new Stack();	// Temporary stacks for storing states resulting from a transition firing
		Stack tansuccessor = new Stack();
		// Objects for temporarily storing states that haven't been identified as tangible or vanishing
		State vprime = null;
		State sprime = null;
		Marking tangible = null; // Used in some loops
		Marking s = null; // Used in some loops
		VanishingState v = null;	// Used in some loops
				
		// A record of all explored states. The actual states themselves
		// are not stored here. Instead, just their two hashcodes are used
		// to represent them, one as a key to the hashtable row, the other
		// as the entry in a list at each hashtable row.
		LinkedList[] exploredStates = new LinkedList[NUMHASHROWS];
		
		// This list is used to temporarily store details
		// of the arcs between states on the reachability graph.
		LinkedList localarcs = new LinkedList();
				
		// These are used when calculating effective transition rates
		// from vanishing states to tangible states
		double p;
		double pprime;
		double epsilon = 0.0000001;
		
		// Counters used for creating the reachability graph file
		int numtangiblestates = 0;
		int numtransitions = 0;
		int numtransitionsfired = 0;
		
		// The following two variables form a crude progress monitor
		final int UPDATEAFTER = 10;
		int progress = UPDATEAFTER;
	
		// Temporary files for storing tangible states and
		// the transitions between them. They are later
		// combined into one file by the createRGFile method
		RandomAccessFile outputFile;
		RandomAccessFile esoFile;
		File intermediate = new File("graph.irg");
		
		if(intermediate.exists()){
			if(!intermediate.delete()){
				System.err.println("Could not delete intermediate file.");
			}
		}
		
		try {
			outputFile = new RandomAccessFile(intermediate, "rw");
			esoFile = new RandomAccessFile(reachGraph, "rw");
			// Write a blank file header as a place holder for later
			RGFileHeader header = new RGFileHeader();
			header.write(esoFile);
		} catch (IOException e) {
			System.out.println("Could not create intermediate files.");
			return;
		}
		
		/*
		 * Phase I
		 * Initialise the tangibleStates stack with initial
		 * tangible states.
		 */
		System.out.println("Beginning Phase I: Determining initial tangible states...");
		
		// Start state space exploration
		if(isTangible(pnmlData, current_marking)){
			numtangiblestates++;
			tangible = new Marking(current_marking, numtangiblestates-1);
			tangibleStates.enqueue(tangible);
			addExplored(tangible, exploredStates, esoFile, false);
		}
		else{
			int attempts = 0; // This is a counter used to detect timeless traps
			vanishingStates.push(new VanishingState(current_marking, (double)1.0));
			while(!vanishingStates.isEmpty()&&attempts!=MAX_TRIES){
				attempts++;
				v = (VanishingState)vanishingStates.pop();
				p = v.getRate();
				numtransitionsfired += fire(pnmlData, v, vansuccessor, false);
				while(!vansuccessor.isEmpty()){
					vprime = (State)vansuccessor.pop();
					if(isTangible(pnmlData, vprime)){
						if(!explored(vprime, exploredStates)){
							numtangiblestates++;
							tangible = new Marking(vprime, numtangiblestates-1);
							tangibleStates.enqueue(tangible);
							addExplored(tangible, exploredStates, esoFile, false);
						}
					}
					else{
						pprime = p * prob(pnmlData, v, vprime);
						if(pprime > epsilon){
							vanishingStates.push(new VanishingState(vprime,(double)pprime));
						}
					}
				}
			}
			if(attempts == MAX_TRIES){
				try {
					outputFile.close();
				} catch (IOException e1) {
					System.err.println("Could not close intermediate file.");
				}
				throw new TimelessTrapException();
			}
		}
		/* Phase I ends */
		
		/*
		 * Phase II
		 * Perform state space exploration, eliminating vanishing states
		 */
		System.out.println("Beginning Phase II: Exploring state space...");
		
		// Continue state space exploration
		while(!tangibleStates.isEmpty()){
			if(progress == UPDATEAFTER){
				progress = 0;	// Reset the counter
				System.out.print(numtangiblestates + " tangible states generated and " + numtransitionsfired + " transitions fired.\r");
			}
			else
				progress++;
			
			s = (Marking)tangibleStates.dequeue();
			
			numtransitionsfired += fire(pnmlData, s, tansuccessor, false);
			while(!tansuccessor.isEmpty()){
				sprime = (State)tansuccessor.pop();
				if(isTangible(pnmlData, sprime)){
					if(!explored(sprime, exploredStates)){
						numtangiblestates++;
						tangible = new Marking(sprime, numtangiblestates-1);
						tangibleStates.enqueue(tangible);
						addExplored(tangible, exploredStates, esoFile, false);
					}
					else{
						int id = identifyState(sprime, exploredStates);
						if(id==-1)
							throw new ImmediateAbortException("Could not identify previously explored tangible state.");
						tangible = new Marking(sprime, id);
					}
					numtransitions += transition(tangible, rate(pnmlData, s, sprime), localarcs);
				}
				else{
					int attempts = 0;
					vanishingStates.push(new VanishingState(sprime, rate(pnmlData, s, sprime)));
					while(!vanishingStates.isEmpty()&&attempts!=MAX_TRIES){
						attempts++;
						v = (VanishingState)vanishingStates.pop();
						p = v.getRate();
						numtransitionsfired += fire(pnmlData, v, vansuccessor, false);
						while(!vansuccessor.isEmpty()){
							vprime = (State)vansuccessor.pop();
							pprime = p * prob(pnmlData, v, vprime);
							if(isTangible(pnmlData, vprime)){
								if(!explored(vprime, exploredStates)){
									numtangiblestates++;
									tangible = new Marking(vprime, numtangiblestates-1);
									tangibleStates.enqueue(tangible);
									addExplored(tangible, exploredStates, esoFile,false);
								}
								else{
									int id = identifyState(vprime, exploredStates);
									if(id==-1)
										throw new ImmediateAbortException("Could not identify previously explored tangible state.");
									tangible = new Marking(vprime, id);
								}
								numtransitions += transition(tangible, pprime, localarcs);
							}
							else{
								if(pprime > epsilon){
									vanishingStates.push(new VanishingState(vprime,(double)pprime));
								}
							}
						}
					} 
					if(attempts==MAX_TRIES){
						try {
							outputFile.close();
						} catch (IOException e1) {
							System.err.println("Could not close intermediate file.");
						}
						throw new TimelessTrapException();
					}
				}
			}
			// Write all the arcs for the reachability graph to file
			writeTransitions(s, localarcs, outputFile,false);
			// Clear the list so can start again with the next set of arcs
			localarcs = new LinkedList();
		}
		try {
			outputFile.close();
		} catch (IOException e1) {
			System.err.println("\nCould not close intermediate file.");
		}
		System.out.println("\nGenerate Ends, " + numtangiblestates + " tangible states found with " + numtransitions + " arcs.");
		createRGFile(intermediate, esoFile, statearraysize, numtangiblestates, numtransitions,false);
		
		if(DEBUG){
			/* Write a csv file indicating the Hashtable distribution */
			FileWriter htdist = new FileWriter("HashTableDist.csv");
			for(int row = 0; row < NUMHASHROWS; row++){
				htdist.write(Integer.toString(row) + ",");
				if(exploredStates[row] == null)
					htdist.write("0\n");
				else
					htdist.write(Integer.toString(exploredStates[row].size()) + "\n");
			}
			htdist.close();
			System.out.println("Finished writing hashtable distribution to file.");
		}
		
		if(intermediate.exists()){
			if(!intermediate.delete()){
				System.err.println("\nCould not delete intermediate file.");
			}
		}
	}

	/**
	 * isTangible()
	 * Tests whether the state passed as an argument is tangible or vanishing.
	 * 
	 * @param pnmlData
	 * @param marking
	 * @return
	 */
	private static boolean isTangible(DataLayer pnmlData, State marking) {
		Transition[] trans = pnmlData.getTransitions();
		int numTrans = trans.length;
		boolean hasTimed = false;
		boolean hasImmediate = false;
		boolean[] enabledTransitions = getTransitionEnabledStatusArray(pnmlData, marking.getState(),false);
		for (int i = 0; i < numTrans; i++ ){
			if(enabledTransitions[i]){ // If the transition is enabled
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
		if (hasTimed == true && hasImmediate == false){
			return true;
		}
		else
			return false;
		
	}
	
	/**
	 * fire()
	 * Determines all the states resulting from firing enabled transitions
	 * in the state passed as an argument
	 * 
	 * @param vs		The state to determine successors from
	 * @param succ		A stack in which to store successors
	 */
	private static int fire(DataLayer pnmlData, State vs, Stack succ, boolean immediateTransition){
		int transCount = pnmlData.getTransitionsCount();
		int transitionsfired = 0;
		int[] newstate = null;
		boolean[] enabledTransitions = getTransitionEnabledStatusArray(pnmlData, vs.getState(), immediateTransition);
		for(int index = 0; index < transCount; index++){
			if(enabledTransitions[index]){ // If the current transition is enabled
				newstate = fireTransition(pnmlData, vs.getState(), index);
				succ.push(new State(newstate));
				transitionsfired++;
				transitions.push(new Integer (index));
							
			}
		}
		return transitionsfired;
	}
	
	/**
	 * fireTransition()
	 * 
	 * Produces a new markup vector to simulate the firing of a transition.
	 * Destroys the number of tokens shown in CMinus for a given place and
	 * transition, and creates the number of tokens shown in CPlus.
	 * 
	 * @author Matthew Cook, James Bloom and Clare Clark (original code)
	 * 			Nadeem Akharware (optimisation)
	 * 
	 * @param pnmlData		The petri net data model
	 * @param marking		The state/marking to fire from
	 * @param transIndex	Which transition to fire
	 * @return				The new marking/state vector resulting from the fired transition
	 */
	private static int[] fireTransition(DataLayer pnmlData, int[] marking, int transIndex) {
		int count;               //index for 'for loop'
		int CMinusValue;         //Value from C- matrix
		int CPlusValue;          //Value from C+ matrix
		
		int[][] CMinus = pnmlData.getBackwardsIncidenceMatrix();
		int[][] CPlus = pnmlData.getForwardsIncidenceMatrix();

		//Create marking array to return
		int[] newmarking = new int[marking.length];

		for(count = 0; count < marking.length; count++)
		{
			CMinusValue = CMinus[count][transIndex];
			CPlusValue = CPlus[count][transIndex];
			newmarking[count] = marking[count] - CMinusValue + CPlusValue;
		}
		return newmarking;
	}

	
	
	/**
	 * explored()
	 * Tests whether the state passed as an argument has already
	 * been explored.
	 * 
	 * @param test				The state to look for
	 * @param exploredStates	The hashtable to check for the state
	 * @return
	 */
	private static boolean explored(State test, LinkedList[] es){
		LinkedList hashrow = es[test.hashCode()%NUMHASHROWS];
		if(hashrow == null) // This row has nothing in it yet
			return false;	// so must be an unexplored state
		Iterator iterator = hashrow.iterator();
		CompressedState current;
		for(int index = 0; index < hashrow.size(); index++){
			current = (CompressedState)iterator.next();
			if(test.hashCode2()==current.getHashCode2())
				return true;
		}
		return false;
	}
	
	/**
	 * identifyState()
	 * Takes a state that we know has been explored before
	 * and works out what id number that state has been
	 * given using the explored states hashtable.
	 * 
	 * @param test		The state to be identified
	 * @param es		The hashtable to look it up in
	 * @return			The id number of that state (-1 indicates an error)
	 */
	private static int identifyState(State test, LinkedList[] es){
		LinkedList hashrow = es[test.hashCode()%NUMHASHROWS];
		Iterator iterator = hashrow.iterator();
		CompressedState current;
		for(int index = 0; index < hashrow.size(); index++){
			current = (CompressedState)iterator.next();
			if(test.hashCode2()==current.getHashCode2())
				return current.getID();
		}
		return -1;
	}
	
	/**
	 * addExplored()
	 * Adds a compressed version of a tangible state to the
	 * explored states hashtable and also writes the full
	 * state to a file for later use.
	 * 
	 * @param newstate		The explored state to be added
	 * @param es			A reference to the hashtable
	 * @param opfile		The file to write the state to
	 */
	private static void addExplored(Marking newstate, LinkedList[] es, RandomAccessFile opfile, boolean vanishingStates){
		LinkedList hashrow = es[newstate.hashCode()%NUMHASHROWS];
		if(hashrow == null){
			// This hashcode hasn't come up before so we need
			// to set up the linked list first
			es[newstate.hashCode()%NUMHASHROWS] = new LinkedList();
			hashrow = es[newstate.hashCode()%NUMHASHROWS];
		}
		hashrow.add(new CompressedState(newstate.hashCode2(), newstate.getIDNum()));
		// Now also write this state to disk for later use
		StateRecord sr = new StateRecord(newstate);
		try {
			if (vanishingStates) sr.write(opfile,newstate.getisTangible());
			else sr.write(opfile);
		} catch (IOException e) {
			System.err.println("IO problem while writing explored states to file.");
		}
	}
	
	private static double prob(DataLayer pnmlData, VanishingState v, State vprime){
		return rateorprob(pnmlData, v, vprime, PROBABILITY);
	}
	
	private static double rate(DataLayer pnmlData, State s, State sprime){
		return rateorprob(pnmlData, s, sprime, RATE);
	}

	/**
	 * rateorprob()
	 * Calculate the PROBABILITY of a transition from a VANISHING state to another
	 * state or the RATE of transition from a TANGIBLE state to another state.
	 * Works out the transitions enabled to fire at a particular
	 * marking, transitions that can be reached from a particular marking and the 
	 * intersection of the two.  Then sums the firing rates of the intersection
	 * and divides it by the sum of the firing rates of the enabled transitions.
	 * 
	 * @author Matthew Cook (original code), Nadeem Akharware (adaption and
	 * optimisation)
	 * 
	 * @param pnmlData  
	 * @param v
	 * @param vprime
	 * @return double - the probability
	 */
	
	private static double rateorprob(DataLayer pnmlData, State s, State sprime, boolean rp){
	
		int[] marking1 = s.getState();
		int[] marking2 = sprime.getState();
		int markSize = marking1.length;
		int[][] incidenceMatrix = pnmlData.getIncidenceMatrix();
		int transCount = pnmlData.getTransitionsCount();
		boolean[] marking1EnabledTransitions = getTransitionEnabledStatusArray(pnmlData, marking1, false); //get list of transitions enabled at marking1
		boolean[] matchingTransition = new boolean[transCount];
		
		
		//**************************************************** *************************************************
		for (int j = 0; j <transCount; j ++) {
			matchingTransition[j] = true;  //initialise matrix of potential transition values to true
		}
		//*****************************************************************************************************
		//get transition needed to fire to get from marking1 to marking2
		for (int i = 0; i < transCount; i++) {
			for (int k = 0; k < markSize; k++) {
			//if the sum of the incidence matrix and marking 1 doesn't equal marking 2, 
			//set that candidate transition possibility to be false 
				if (((int)marking1[k] + (int)incidenceMatrix[k][i])!= (int)marking2[k]){
					matchingTransition[i] = false;
					}
			}
		}
		
		// If the state marking1 is tangible (i.e. we must be calculating a rate),
		// all transitions will be timed, so all can be considered 
		// in the calculation.
		// Otherwise, reset the enabled status of timed transitions to false,
		// as immediate transitions will always fire first.
		if(rp==PROBABILITY){
			Transition[] transitions = pnmlData.getTransitions();
			for (int i = 0; i <transCount; i++) {
				if (transitions[i].isTimed() == true) {
					marking1EnabledTransitions[i] = false;
				}
			}
		}
		
		//*****************************************************************************************************
		//check if there are any potential transitions from marking 1 to marking 2 and whether they are
		// enabled or not.
		boolean enabledAndMatching = false;
		for (int i = 0; i < transCount; i++) {	
			if (matchingTransition[i] == true){
				if(marking1EnabledTransitions[i] == true){
					enabledAndMatching = true;
				}
			}
		}
		if (enabledAndMatching == false) {
			return 0.0;
		}
		
		//******************************************************************************************************	
		//work out the sum of firing weights of input transitions
		double candidateTransitionWeighting = 0.0;
		for (int i = 0; i < transCount; i++) {
			if((matchingTransition[i] == true) && (marking1EnabledTransitions[i] == true)){
				candidateTransitionWeighting += pnmlData.getTransitions()[i].getRate(); 	
			}
		}
		if(rp==RATE){
			return candidateTransitionWeighting;
		}
		else{
			//*****************************************************************************************************	
			//work out the sum of firing weights of enabled transitions
			double enabledTransitionWeighting = 0.0;
			for (int i = 0; i < transCount; i++) {
				if (marking1EnabledTransitions[i] == true) {
					enabledTransitionWeighting += pnmlData.getTransitions()[i].getRate();
				}
			}
			return (candidateTransitionWeighting/enabledTransitionWeighting);
		}
	}

	
	/**
	 * Records the fact that there is a transition firing sequence 
	 * from whatever the current tangible state is to the tangible
	 * state sprime with an effective transition firing rate r.
	 * Note it does not need to know what the current tangible state
	 * actually is, it just needs a reference to the list of arcs
	 * from that state.
	 * 
	 * @param sprime
	 * @param r
	 * @param arclist		A linked list of arcs from the current
	 * 						tangible state.
	 */
	private static int transition(Marking sprime, double r, LinkedList arclist){
		ArcListElement current;
		if(arclist.size() > 0){
			Iterator iterator = arclist.iterator();
			current = (ArcListElement)iterator.next();
			while((current.getTo()!=sprime.getIDNum()) && iterator.hasNext()){
				current = (ArcListElement)iterator.next();
			}
			if(current.getTo() == sprime.getIDNum()){
				double rate = current.getRate();
				current.setRate(r+rate);
				return 0;
			}
			else{
				current = new ArcListElement(sprime.getIDNum(), r, (Integer)transitions.pop());
				arclist.add(current);
				return 1;
			}
		}
		else{
			// This must be a new arc
			current = new ArcListElement(sprime.getIDNum(), r, (Integer)transitions.pop());
			arclist.add(current);
			return 1;
		}
	}

	/**
	 * writeTransitions()
	 * Records all the arcs in the reachability graph from state 'from'.
	 * 
	 * @param from			The tangible state which all the arcs
	 * 						in the linked list are from.
	 * @param arclist		The list of arcs.
	 * @param dataFile		The file that reachability graph data
	 * 						needs to be written to.
	 * 
	 * @throws ImmediateAbortException
	 */
	private static void writeTransitions(Marking from, LinkedList arclist, RandomAccessFile dataFile, boolean writeTransitionsNo) throws ImmediateAbortException{
		TransitionRecord newTransition;
		Iterator iterator = arclist.iterator();
		ArcListElement current;
		while(iterator.hasNext()){
			current = (ArcListElement)iterator.next();
			
			if (writeTransitionsNo) {
				newTransition = new TransitionRecord(from.getIDNum(), current.getTo(), current.getRate(), current.transitionNo);
				try{
					newTransition.write1(dataFile);
				//	System.out.println("From: " + from.getIDNum() + ":(" + from + ")" + " To: " + current.getTo() + " via " + current.transitionNo+ " Rate: " + current.getRate());
				} catch (IOException e){
					System.err.println("IO error when writing transitions to file.");
					throw new ImmediateAbortException();
				}	
				
			} else {
				newTransition = new TransitionRecord(from.getIDNum(), current.getTo(), current.getRate());
				try{
					newTransition.write(dataFile);
					//System.out.println("From: " + from.getIDNum() + ":(" + from + ")" + " To: " + current.getTo() + " Rate: " + current.getRate());
				} catch (IOException e){
					System.err.println("IO error when writing transitions to file.");
					throw new ImmediateAbortException();
				}			

			}
		}
	}

	/**
	 * createRGFile()
	 * Creates a reachability graph file containing all the tangible
	 * states that were found during state space exploration and also
	 * all the transitions between them.
	 * 
	 * @param transource	A file containing all the transitions (arcs) between 
	 * 						tangible states.
	 * @param destination	The file to create as a reachability graph file. The file
	 * 						should already contain a list of all the tangible states
	 * 						and be in the correct position for writing the record
	 * 						of all the transitions between them.
	 * @param statesize		The size of each state array
	 * @param states		The number of tangible states found
	 * @param transitions	The number of transitions recorded
	 */
	private static void createRGFile(File transource, RandomAccessFile destination, int statesize, int states, int transitions, boolean withTransitions) {
		RandomAccessFile transinputFile;
		StateRecord currentstate = new StateRecord();
		TransitionRecord currenttran = new TransitionRecord();
		RGFileHeader header;
		try {
			transinputFile = new RandomAccessFile(transource, "r");
			// The destination file actually already exists with
			// a blank file header as a placeholder and all the
			// tangible states written in order. The file pointer
			// should already be at the end of the file (i.e.
			// after the last tangible state that's been written
			// to the file. Make a note of the file pointer as this
			// is where the transition records begin.
			long offset = destination.getFilePointer();
			// Now copy over all the transitions
			System.out.println("Creating reachability graph, please wait...");
			for(int count = 0; count < transitions; count++){
			//	System.out.print("Recording arc " + (count+1) + " of " + transitions +".\r");
				if (withTransitions)
				{
					currenttran.read1(transinputFile);
					currenttran.write1(destination);	
				}
				else
				{
					currenttran.read(transinputFile);
					currenttran.write(destination);
				}
				
			}
			System.out.println("");
			// Make a note of the transition record size
			// and fill in all the details in the file header.
			int recordsize = currenttran.getRecordSize();
			destination.seek(0); // Go back to the start of the file
			header = new RGFileHeader(states, statesize, transitions, recordsize, offset);
			header.write(destination);
			
			// Done so close all the files.
			transinputFile.close();
			destination.close();
		} catch (EOFException e){
			System.err.println("EOFException");
		} catch (IOException e) {
			System.out.println("Could not create output file.");
			e.getMessage();
			return;
		}
	}

	/**
	 * getTransitionEnabledStatusArray()
	 * Calculate which transitions are enabled given a specific marking.
	 * 
	 * @author Matthew Cook (original code), Nadeem Akharware (optimisation)
	 * 
	 * @param DataLayer - the net  
	 * @param int[] - the marking
	 * @return boolean[] - an array of booleans specifying which transitions are enabled in the specified marking
	 */
	public static boolean[] getTransitionEnabledStatusArray(DataLayer pnmlData, int[] marking, boolean immediateTransitions) {
		int transCount = pnmlData.getTransitionsCount();
		boolean[] result = new boolean[transCount];
		boolean hasTimed = false;
		boolean hasImmediate = false;
		
		Transition[] transArray = pnmlData.getTransitions();
		
		int[][] CMinus = pnmlData.getBackwardsIncidenceMatrix();
		int placeCount = pnmlData.getPlacesCount();
		
		// Initialise the result array
		for(int t = 0; t < transCount; t++){
			result[t] = true;
		}
		
		for (int i = 0; i <transCount ;i++) {
			for (int j = 0; j <placeCount; j++){
				if (marking[j] < CMinus[j][i]){
					result[i] = false;
					//System.err.println("Set transition enabled status to false " + i + "of " + transCount);
				}
			}
		}
		
		/*
		 * Now make sure that if any of the enabled transitions are
		 * immediate transitions, only they can fire as this must then
		 * be a vanishing state.
		 */
		/* wjk - we should always check the following - so am removing check for immediate transition flag */
		/*if (!immediateTransitions){*/
			for (int i = 0; i<transCount; i++) {
				if(result[i]){
					if (transArray[i].isTimed()==true){
						hasTimed = true;
					}
					else {
						hasImmediate = true;
					}
				}
			}
			if (hasTimed && hasImmediate){
				for (int i = 0; i<transCount; i++){
					if (transArray[i].isTimed()==true){
						result[i] = false;
					}
				}
			}
		/*}*/
		
		/*System.out.println("getTransitionEnabledStatusArray:");
		printArray(result);*/
		return result;
	}
	


//	######################################################################################################################
	
	private static void printArray (boolean[] array) {
		int rows = array.length;
		System.out.print("Elements as follows: ");
		for (int i = 0; i < rows; i++) {
			System.out.print(array[i] + " ");
		}
		System.out.println();
	}
	
	private static void printArray (int[] array) {
		int rows = array.length;
		System.out.print("Elements as follows: ");
		for (int i = 0; i < rows; i++) {
			System.out.print(array[i] + " ");
		}
		System.out.println();
	}
	
	private static void printArray (State state) {
		int[] array = state.getState();
		printArray(array);
	}

}

/**
 * 
 * @author Nadeem
 *
 * A simple class used to store arcs between states in the reachability graph.
 * Used as elements in the linked list storing the arcs.
 */
class ArcListElement{
	int tostate;
	double rate;
	int transitionNo;
	
	public ArcListElement(int to, double r, Integer t){
		tostate = to;
		rate = r;
		transitionNo = t.intValue();
	}
			
	public int getTo(){
		return tostate;
	}
	
	public double getRate(){
		return rate;
	}
	
	public void setRate(double r){
		rate = r;
	}
}
