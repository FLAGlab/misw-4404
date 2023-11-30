package pipe.common.dataLayer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import pipe.modules.passageTimeForTaggedNet.ConditionPlace;

/*
 * State is the collection of conditions that constitute a source / destination
 * state for passage time analysis. 
 *  
 */

public class StateGroup implements Serializable
{
	private static final long					serialVersionUID	= 1L;

	private final HashMap<String, StateElement>	elements;
	private String								id, name;

//	private DataLayer currentPnml;
	
	public StateGroup(DataLayer pnml) {
//		currentPnml = pnml;

		this.id = "";
		this.name = "";
		this.elements = new HashMap<String, StateElement>();
	}

	public StateGroup(String id, String title) {
		this.id = id;
		this.name = title;
		this.elements = new HashMap<String, StateElement>();
	}

	/**
	 * This method adds a State Element to the current state group. If an
	 * element with an empty condition is passed, this has the effect of
	 * removing any existing condition on that place.
	 * 
	 * @param newState
	 *            The new State Element being added to the group
	 */

	public void addState(StateElement newState)
	{
		if (newState.getOperator() == "" || newState.getPlaceB() == "")
		{
			if (newState.getPlaceA() != "")
			{
				this.elements.remove(newState.getPlaceA());
			}
		}
		else
		{
			this.elements.put(newState.getPlaceA(), newState);
		}
	}

	// We map the id of the place to the condition associated with that place
	public void addState(String placeId, String operator, String target)
	{
		this.elements.put(placeId, new StateElement(placeId, operator, target));
	}

	public StateElement getCondition(String id)
	{
		return this.elements.get(id);
	}
	


	public String[] getConditions()
	{
		int i = 0;
		final String[] conditions = new String[this.elements.size()];
		final Iterator stateElementIter = this.elements.values().iterator();

		while (stateElementIter.hasNext())
		{
			final StateElement currElement = (StateElement) stateElementIter.next();
			
			if(currElement.getOperator()== "T"  ){
//				conditions[i] = "tagged_location == " +  currentPnml.getPlaceIndex(currElement.getPlaceA() );
				conditions[i] = "tagged_location == " +  currElement.getPlaceB();
			}
			else{ 
			conditions[i] = currElement.getPlaceA() + " " + currElement.getOperator() + " " +
							currElement.getPlaceB();
			}
			i++;
		}

		return conditions;

	}

	public String getId()
	{
		return this.id;
	}

	public String getName()
	{
		return this.name;
	}

	public int numElements()
	{
		return this.elements.size();
	}

	public void removeState(String id)
	{
		this.elements.remove(id);
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public void setName(String title)
	{
		this.name = title;
	}

}
