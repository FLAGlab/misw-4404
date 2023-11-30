/*
 * Created on Feb 10, 2004
 *
 * Class used in state space and GSPN modules to generate trees and arrays of potential state spaces
 */
package pipe.dataLayer.calculations;

import pipe.common.dataLayer.DataLayer;
import pipe.common.dataLayer.PNMatrix;

/**
 * @author Matthew
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class myTree {

	public boolean Found_An_Omega = false;  // bounded
	public boolean more_Than_One_Token = false;  // safe
	public boolean no_Enabled_Transitions = false;  // deadlock

	public myNode root;      //root of the tree
	public boolean[] transitions;    //each bool corresponds to a transition
	public boolean[] nodes;            //each bool corresponds to a node
	public int nodeCount = 0;    //Total number of nodes in tree
	public PNMatrix CPlus;
	public PNMatrix CMinus;                 //incidence matrices of petri net
	public int number_transitions;   //number of transitions in net
	public int number_places;    //number of places in the net
	public myNode[] EndNodes;    //The end nodes (leaves) in the tree
	public int[] pathToDeadlock;   //Gives transitions to deadlock
	public boolean tooBig = false; // Set if the tree gets too large

	//Tree Constructor
	public myTree(int[] tree_root, PNMatrix plus, PNMatrix minus) throws TreeTooBigException {
		CPlus = plus;
		CMinus = minus;
		//Find number transitions in net from incidence matrix dimensions
		number_transitions = CMinus.getColumnDimension();

		//Find number of places in net from incidence matrix dimensions
		number_places = CMinus.getRowDimension();

		//Create root of tree by calling Node constructor
		root = new myNode(tree_root, this, 1);
		
		//Call expansion function on root of tree
		root.RecursiveExpansion();
		
	}
	
	public myTree(int[] tree_root, PNMatrix plus, PNMatrix minus, StateList statespace, DataLayer pnmldata) throws TreeTooBigException{
		CPlus = plus;
		CMinus = minus;
		
			//Matthew: reduce statespaces start
			int transCount = pnmldata.getTransitionsCount();
			boolean[] timedTrans = new boolean[transCount];
			for (int i = 0; i< transCount; i++) {
				if (pnmldata.getTransition(i).isTimed() == true) {
					timedTrans[i] = true;
				}
				else {
					timedTrans[i] = false;
				}
			}
			
			// 
		int markSize = tree_root.length;
		statespace.add(tree_root);

		//Find number transitions in net from incidence matrix dimensions
		number_transitions = CMinus.getColumnDimension();
		//Find number of places in net from incidence matrix dimensions
		number_places = CMinus.getRowDimension();
		//Create root of tree by calling Node constructor
		root = new myNode(tree_root, this, 1);
		
		//Call expansion function on root of tree
	
			root.RecursiveExpansion(statespace, timedTrans);
	
		 
	}
	
	
}
