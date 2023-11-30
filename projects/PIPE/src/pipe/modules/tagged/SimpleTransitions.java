package pipe.modules.tagged;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import pipe.common.dataLayer.Arc;
import pipe.common.dataLayer.DataLayer;
import pipe.common.dataLayer.Transition;



public class SimpleTransitions implements Serializable
{
	/**
	 *	Simple Transitions is a simplified version of the pipe.dataLayer.Transition class
	 *	Its purpose is to provide a simple serializable object for socket transmission
	 *	to processing clusters.
	 *	The necessary attributes from Places for building a '.mod' file are the
	 *	place IDs with the respective IDs of all their To/From transition targets
	**/
	
	private static final long serialVersionUID = 1L;
	
	public String[] ids;
	public boolean[] timed;
	public double[] rate;
	public ArrayList<LinkedList<SimpleArc>> arcsTo, arcsFrom;
	public int length;
	
	

	public SimpleTransitions (DataLayer pnmldata) 
	{
		int i;
		Iterator arcsToIter;
		Iterator arcsFromIter;
		
		
		Transition[] transitions;
		transitions = pnmldata.getTransitions();
		
		// Declare the objects variables
		length = transitions.length;
		
		ids = new String[length];
		timed = new boolean[length];
		rate = new double[length];
		arcsTo = new ArrayList<LinkedList<SimpleArc>>(length);
		arcsFrom = new ArrayList<LinkedList<SimpleArc>>(length);
		
		
		
		for (i=0; i< length; i++)
		{
			ids[i] = transitions[i].getId();
			timed[i] = transitions[i].isTimed();
			rate[i] = transitions[i].getRate();
			
			
			arcsTo.add(i, new LinkedList<SimpleArc>());			
			arcsFrom.add(i, new LinkedList<SimpleArc>() );
			
			arcsToIter = transitions[i].getConnectToIterator();
			arcsFromIter = transitions[i].getConnectFromIterator();
			
	
			// Create list of all targets from current place 
			while (arcsToIter.hasNext())
			{
				Arc currentArc = (Arc) arcsToIter.next();
				SimpleArc newTransArc = new SimpleArc( currentArc.getSource().getId(), currentArc.getWeight(), currentArc.isTagged());
				arcsTo.get(i).add ( newTransArc );
			}
			
			// Create list of source places to current
			while (arcsFromIter.hasNext())
			{
				Arc currentArc = (Arc) arcsFromIter.next();
				SimpleArc newTransArc = new SimpleArc( currentArc.getTarget().getId(), currentArc.getWeight(), currentArc.isTagged());
				arcsFrom.get(i).add( newTransArc );
			}
		}
	}
	
}