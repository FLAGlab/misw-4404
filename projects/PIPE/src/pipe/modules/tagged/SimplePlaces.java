package pipe.modules.tagged;

import java.io.Serializable;

import pipe.common.dataLayer.DataLayer;
import pipe.common.dataLayer.Place;


public class SimplePlaces implements Serializable
{
	/**
	 *	Simple Places is a simplified version of the pipe.dataLayer.Place class
	 *	Its purpose is to provide a simple serializable object for socket transmission
	 *	to processing clusters.
	 *	The necessary attributes from Places for building a 'mod' file are IDs, current markings and length
	**/
	private static final long serialVersionUID = 1L;

	public String[] ids;
	public int[] marking;
	public int length;
	public boolean[] tagged;
	
	public SimplePlaces(DataLayer pnmldata) 
	{
		int i;
		Place[] places = pnmldata.getPlaces();
		
		length = places.length;
		
		ids = new String[length];
		marking = new int[length];
		tagged = new boolean[length];
		
		
		for (i=0; i< length; i++)
		{
			ids[i] = places[i].getId();
			marking[i] = places[i].getCurrentMarking();		
			tagged[i] = places[i].isTagged();
			//if(tagged[i]==true)
			//	System.out.println("Found a tagged place!!!");
		}
	}	
}

