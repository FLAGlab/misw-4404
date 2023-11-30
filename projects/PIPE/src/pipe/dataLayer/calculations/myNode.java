/*
 * Created on Feb 10, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package pipe.dataLayer.calculations;

/**
 * @author Matthew Cook after James Bloom/Clare Clark
 *Class used in state space and GSPN modules for generation of trees and arrays of possible state spaces
 *
 */
public class  myNode  {

	boolean[] trans_array;     //array of transitions
	myNode parent;             //parent node
	myNode[] children;         //child nodes
	myTree tree;               //tree that contains the node
	int[] Markup;      //The marking of the node
	myNode previousInstance;   //same node found in tree
	int depth;        // The depth this node is in the tree

	//Attributes used for assessing whether a node has occured before
	boolean Repeated_Node = false;

	/*-------------------------------------------------------------------
	 Function: void myNode(int[] marking_array, myTree tree)

	 Node constructor called by the tree object

	 -------------------------------------------------------------------*/

	public myNode(int [] marking_array, myTree atree, int treeDepth) {
		int i;             //counter for 'for printing loop'

		//Node now knows its tree in order to use its attributes
		tree = atree;

		depth = treeDepth;

		//Update the count of nodes
		tree.nodeCount++;

		//Set up marking for this node
		Markup = marking_array;  //created in myTree
//        System.out.print("Node "+tree.nodeCount+" Created \n" );

		//Assign the root nodes parents (this constructor only called by tree)
		parent = tree.root; //Just get root to have itself as parent

		//Create an array of transitions for each node
		trans_array = new boolean[tree.number_transitions];

		//Create an array of nodes for each node (for children)
		//Number of children limited by total number of transitions
		children = new myNode[tree.number_transitions];

		//Initialise trans_array
		for(i=0; i< tree.number_transitions; i++){
			trans_array[i] = false;
		}

//        for(i=0; i< Markup.length; i++) {
//          System.out.print(this.Markup[i]);
//          System.out.print(" ");
//        }



	}


	/*-------------------------------------------------------------------
	 Function: void myNode(int[] marking_array, myNode parent_node, myTree atree)

	 The overloaded Node constructor called by a node object

	 -------------------------------------------------------------------*/

	public myNode(int [] marking_array, myNode parent_node, myTree atree, int treeDepth) {
		int i;             //counter for 'for printing loop'

		//Node now knows its tree in order to use its attributes
		tree = atree;

		depth = treeDepth;

		//Update the count of nodes
		tree.nodeCount++;

		//Set up marking for this node
		Markup = marking_array;  //created in myTree

		//Assign the nodes parents
		parent = parent_node;

//        System.out.print("Node "+tree.nodeCount+" Created \n" );

		//Create an array of transitions for each node
		trans_array = new boolean[tree.number_transitions];

		//Create an array of nodes for each node (for children)
		//Number of children limited to total number of transitions.
		children = new myNode[tree.number_transitions];

		//Initialise trans_array
		for(i=0; i< tree.number_transitions; i++){
			trans_array[i] = false;
		}

//        for(i=0; i< Markup.length; i++) {
//          System.out.print(this.Markup[i]);
//          System.out.print(" ");
//        }

	}
	/*---------------------------------------------------------------------
	 Function: boolean TransitionEnabled(int transIndex)

	 Tests to see if a particular transition is enabled by consulting C-
	 and the current markup contained in the tree.
	 transIndex = usual petri net description of a transition by an integer

	 ---------------------------------------------------------------------*/

	public  boolean TransitionEnabled(int transIndex) {
		int count;        //index for 'for loop'
		int CMinusValue;
		
		
		for(count=0; count < tree.number_places; count++)
		{

			CMinusValue = (tree.CMinus).get(count,(transIndex-1));
			if((Markup[count] < CMinusValue) && Markup[count]!=-1)
			{
				//There is a place where marking is less than required in CMinus
				return false;
			}
		}

		//All places satisfy the marking criteria
		return true;

	}

	/*---------------------------------------------------------------------
	 Function: int[] fire(int trans)

	 Produces a new markup vector to simulate the firing of a transition.
	 Destroys the number of tokens shown in CMinus for a given place and
	 transition, and creates the number of tokens shown in CPlus.

	 TransIndex refers to the actual transition number ie starting at 1.
	 ---------------------------------------------------------------------*/

	public int[] fire(int transIndex) {
		int count;                     //index for 'for loop'
		int CMinusValue;         //Value from C- matrix
		int CPlusValue;          //Value from C+ matrix

		//Create marking array to return
		int[] marking = new int[tree.number_places];

		for(count = 0; count < tree.number_places; count++)
		{
			CMinusValue = (tree.CMinus).get(count, (transIndex - 1));
			CPlusValue = (tree.CPlus).get(count, (transIndex - 1));

			if (Markup[count]!=-1)
				marking[count] = Markup[count] - CMinusValue + CPlusValue;
			else
				marking[count] = Markup[count];
		}

		//Return this new marking to RecursiveExpansion function
		//int size = marking.length;
		//for (int t = 0; t < size; t++){
		//	System.out.print(Markup[t]+ " ");
		//}
		//System.out.println();
		return marking;
	}

	/*----------------------------------------------------------------------
	 Function: void RecursiveExpansion()

	 Undertakes a recursive expansion of the tree
	 Called on root node from within the tree constructor.

	 -----------------------------------------------------------------------*/

	public void RecursiveExpansion() throws TreeTooBigException {
		int transIndex;                              //Index to count transitions
		int[] new_markup;                            //markup used to create new node
		int i;                     //index for loops
		boolean A_Transition_Is_Enabled = false;     //To check for deadlock
		boolean allOmegas;

		
		
		//For each transition
		for(transIndex = 1; transIndex <= tree.number_transitions; transIndex++)
		{
			//If it is enabled as described by CMinus and Markup
			if(TransitionEnabled(transIndex) == true)
			{
				//Set trans_array of to true for this index
				//index 0 refers to transition 1 here.
				trans_array[transIndex - 1] = true;

//          System.out.println("\n Transition " +transIndex+ " Enabled \n" );
				A_Transition_Is_Enabled = true;

				//Fire transition to produce new markup vector
				new_markup = fire(transIndex);

//          System.out.println("\n Old Markup is");
//          for(i=0; i< Markup.length; i++){
//            System.out.print(Markup[i]);
//            System.out.print(" ");
//        }
//        System.out.print("\n");

				//Check for safeness. If any of places have > 1 token set variable.
				for(i=0; i< new_markup.length; i++){
					if(new_markup[i] > 1 || new_markup[i]==-1) {
						tree.more_Than_One_Token = true;
					}

					//Print new markup
//            System.out.print(new_markup[i]);
				}

				//Create a new node using the new markup vector and attach it
				//to the current node as a child.

				children[transIndex - 1] = new myNode(new_markup, this, tree, depth+1);

				/* Now need to (a) check if any omegas (represented by -1) need to be
				 inserted in the markup vector of the new node, and (b) if the resulting
				 markup vector has appeared anywhere else in the tree. We must do (a) before
				 (b) as we need to know if the new node contains any omegas to be able to
				 compare it with existing nodes. */

				allOmegas = children[transIndex - 1].InsertOmegas();

				//check if the resulting markup vector has occured anywhere else in the tree

				Repeated_Node = (tree.root).FindMarkup(children[transIndex-1]);

				if (tree.nodeCount>=10000 && !tree.tooBig)
				{
					tree.tooBig = true;
					throw new TreeTooBigException();
				}

				if(!Repeated_Node && !allOmegas)
				{
					children[transIndex - 1].RecursiveExpansion();
				}
			}

		}

		if(!A_Transition_Is_Enabled)
		{
//          System.out.println("No transition enabled");
			if(!tree.no_Enabled_Transitions || tree.pathToDeadlock.length<depth-1)
			{
				RecordDeadlockPath();
				tree.no_Enabled_Transitions = true;
			}
			else
			{
//			System.out.println("Deadlocked node found, but path is not shorter than current path.");
			}
		}
		else
		{
//			System.out.println("Transitions enabled.");
		}
	}

	/*----------------------------------------------------------------------
	 Function: void RecordDeadlockPath()

	 If there is a deadlock, calculates the path
	 -----------------------------------------------------------------------*/

	public void RecordDeadlockPath() {
		myNode currentNode;      //The current node we're considering
		int[] path;              //returned showing path to deadlock
		int pos;                 //position in path array
		int i;

		//Set up array to return
		tree.pathToDeadlock = new int[depth-1];
		pos = depth-2; // Start filling in at the end of the array

		currentNode = this;

		//For each ancestor node until root
		while(currentNode != tree.root)
		{
			// Work out which transition we followed to get to to currentNode
			loop: for(i = 1; i <= tree.number_transitions; i++)
			{
				if ( currentNode.parent.trans_array[i-1]
													&& currentNode.parent.children[i-1]==currentNode )
				{
					// That's the one!
					break loop;
				}
			}

			tree.pathToDeadlock[pos] = i;
			pos--;
			//Update current node to look at an earlier ancestor
			currentNode = currentNode.parent;
		}

//          System.out.print("Path to deadlock is: ");
//          for (i=0; i<depth-1; i++)
//          {
//            System.out.print(tree.pathToDeadlock[i] + " ");
//          }
//          System.out.print("\n");
	}

	
	/**This function recursively generates potential state spaces from existing state spaces
	 * (as per algorithm written by James Bloom & Clare Clark for PIPE 2003) and adds new state spaces
	 * to the StateList array supplied as a parameter.  The StateList add method checks for duplicate
	 * entries before accepting the add, so once the recursion is complete, the list contains a complete
	 * and unique set of all possible markings of the specified net.  It contains checking
	 * to ensure that states containing both timed and untimed enabled transitions will only allow 
	 * the untimed transitions to fire.
	 * 
	 * @param statespace
	 */
	public void RecursiveExpansion(StateList statespace, boolean[] timedTrans) throws TreeTooBigException {
		int transIndex;                              //Index to count transitions
		int[] new_markup;                            //markup used to create new node
		int i;                     //index for loops
		boolean A_Transition_Is_Enabled = false;     //To check for deadlock
		boolean allOmegas;
		//Matthew: addition of timing restrictions
		boolean hasTimed = false;
		boolean hasUntimed = false;
		int transCount = tree.number_transitions;
		boolean[] adjustForVanishingStates = new boolean[transCount];
		for (i = 1; i <= transCount; i++){
			if (TransitionEnabled(i) == true) {
				adjustForVanishingStates[i-1] = true;
			} else { 
				adjustForVanishingStates[i-1] = false;
			}
			if ((timedTrans[i-1] == true)&& (adjustForVanishingStates[i-1]==true)) {
				hasTimed = true;  //check if any of the timed transitions are are enabled
			} else if ((timedTrans[i-1] == false)&& (adjustForVanishingStates[i-1]==true)){
				hasUntimed = true; //check if any untimed transitions are enabled
			}
		}

		if ((hasTimed == true) && (hasUntimed == true)) {  //if there are both timed and untimed transitions,  
			for (i = 0; i < transCount; i++) {      	   //change their enabled status to false
				if (timedTrans[i] == true) {
					adjustForVanishingStates[i] = false;
				}
			}
		}
		
		//For each transition
		for (transIndex = 1; transIndex <= transCount; transIndex++)
		{
			//If it is enabled as described by CMinus and Markup
			if(adjustForVanishingStates[transIndex - 1] == true)
			{
				//Set trans_array of to true for this index
				//index 0 refers to transition 1 here.
				trans_array[transIndex - 1] = true;
				A_Transition_Is_Enabled = true;
				//Fire transition to produce new markup vector
				new_markup = fire(transIndex);
				statespace.add(new_markup);  //record the new markup in our state space list.
				//Check for safeness. If any of places have > 1 token set variable.
				for(i=0; i< new_markup.length; i++){
					if(new_markup[i] > 1 || new_markup[i]==-1) {
						tree.more_Than_One_Token = true;
					}
				}
				//Create a new node using the new markup vector and attach it
				//to the current node as a child.
				children[transIndex - 1] = new myNode(new_markup, this, tree, depth+1);
				/* Now need to (a) check if any omegas (represented by -1) need to be
				 inserted in the markup vector of the new node, and (b) if the resulting
				 markup vector has appeared anywhere else in the tree. We must do (a) before
				 (b) as we need to know if the new node contains any omegas to be able to
				 compare it with existing nodes. */
				allOmegas = children[transIndex - 1].InsertOmegas();
				//check if the resulting markup vector has occured anywhere else in the tree
				Repeated_Node = (tree.root).FindMarkup(children[transIndex-1]);
				if (tree.nodeCount>=10000 && !tree.tooBig)	{
					tree.tooBig = true;
					throw new TreeTooBigException();
				}
				if(!Repeated_Node && !allOmegas) {
					children[transIndex - 1].RecursiveExpansion(statespace, timedTrans);
				}
			}
		}
		if(!A_Transition_Is_Enabled){
			if(!tree.no_Enabled_Transitions || tree.pathToDeadlock.length<depth-1){
				RecordDeadlockPath();
				tree.no_Enabled_Transitions = true;
			} else {
//			System.out.println("Deadlocked node found, but path is not shorter than current path.");
			}
		} else{
//			System.out.println("Transitions enabled.");
		}
	}

	
	
	/*----------------------------------------------------------------------
	 Function: void InsertOmegas()

	 Checks if any omegas need to be inserted in the places of a given node.
	 Omegas (shown by -1 here) represent unbounded places and are therefore
	 important when testing whether a petri net is bounded. This function
	 checks each of the ancestors of a given node.
	 Returns true iff all places now contain an omega.

	 -----------------------------------------------------------------------*/

	public boolean InsertOmegas() {

		//Attributes used for assessing boundedness of the net
		boolean All_Elements_Greater_Or_Equal = true;
		boolean [] Element_Is_Strictly_Greater;
		myNode ancestor_node;       //one of the ancestors of this node
		int i;                      //counter for the 'for loop'

		//Set up array used for checking boundedness
		Element_Is_Strictly_Greater = new boolean[tree.number_places];

		//Initialise this array to false
		for(i=0; i< tree.number_places; i++){
			Element_Is_Strictly_Greater[i] = false;
		}

		ancestor_node = this;

		//For each ancestor node until root
		while(ancestor_node != tree.root)
		{
			//Update ancestor node to look at an earlier ancestor
			ancestor_node = ancestor_node.parent;

			All_Elements_Greater_Or_Equal = true;

			//compare markup of this node with that of each ancestor node
			//for each place in the markup
			loop: for(i=0; i < tree.number_places; i++)
			{
				if (Markup[i]!=-1)
				{
					//If M(p) for new node less than M(p) for ancestor
					if(Markup[i] < ancestor_node.Markup[i])
					{
						All_Elements_Greater_Or_Equal = false;
						break loop;
					}

					//If M(p) for new node strictly greater than M(p) for ancestor
					Element_Is_Strictly_Greater[i] = (Markup[i] > ancestor_node.Markup[i]);
				}
			}

			//Now assess the information obtained for this node
			if(All_Elements_Greater_Or_Equal == true)
			{
				//for each place p in the markup of this node
				for(i=0; i < tree.number_places; i++)
				{
					if(Markup[i]!=-1 && Element_Is_Strictly_Greater[i])
					{
						//Set M(p) in this new markup to be omega
						Markup[i] = -1;
//              System.out.println("\n Omega added");
//              for(i=0; i< Markup.length; i++) {
//                System.out.print(this.Markup[i]);
//                System.out.print(" ");
//              }
//                System.out.print("\n");

						//Set variable in tree to true - net unbound
						tree.Found_An_Omega = true;
					}
				}
			}
		}

		for(i=0; i< tree.number_places; i++)
		{
			if (Markup[i]!=-1)
			{
				return false;
			}
		}

		return true; // All places have omegas

	}

	/*----------------------------------------------------------------------
	 Function: boolean FindMarkup(int[] mark)

	 Checks if the markup has occured previously in the tree. This means
	 previously during the creation of the tree, and not just on the current
	 branch as a direct ancestor of the node. Function updates arrays of
	 end nodes stored in the tree class for the liveness tests. Explore
	 the tree by starting with the root node and investigating the children.

	 -----------------------------------------------------------------------*/

	public boolean FindMarkup(myNode n)
	{
		if(n==this)
		{
			return false;
		}

		int i;  //counter for 'for loop'

		if(MarkupCompare(n.Markup))
		{

			n.previousInstance = this;
//          System.out.println("\nFound duplicate markup!\n");
			return true;
		}

		for(i=0; i< tree.number_transitions; i++)
		{
			if(trans_array[i])
			{
				if(children[i].FindMarkup(n))
				{
					return true;
				}
			}
		}
		return false;
	}

	/*----------------------------------------------------------------------
	 Function: boolean MarkupCompare(int[] check)

	 Takes two integer arrays (Markups) and compares the values.
	 Returns a boolean showing the results of the comparison.

	 -----------------------------------------------------------------------*/

	public boolean MarkupCompare(int[] check) {
		int i;     //counter for 'for loop'

		//Comparison only possible on same length arrays
		if(this.Markup.length == check.length)
		{
			for(i=0; i < Markup.length; i++)
			{
				if(this.Markup[i] != check[i])
				{
					return false;
				}
			}
			return true;
		}

		else return false;
	}
	//Temp function for debugging - delete when done.
	public void print (boolean[] transitions) {
		int size = transitions.length;
		for (int i = 0; i < size ; i++) {
			System.out.print( transitions[i] +" ");
		}
		System.out.println();
	}

}

