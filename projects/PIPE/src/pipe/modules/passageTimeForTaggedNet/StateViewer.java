package pipe.modules.passageTimeForTaggedNet;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JDialog;
import javax.swing.JLayeredPane;

import pipe.common.dataLayer.AnnotationNote;
import pipe.common.dataLayer.Arc;
import pipe.common.dataLayer.ArcPathPoint;
import pipe.common.dataLayer.DataLayer;
import pipe.common.dataLayer.PetriNetObject;
import pipe.common.dataLayer.Place;
import pipe.common.dataLayer.StateElement;
import pipe.common.dataLayer.StateGroup;
import pipe.common.dataLayer.Transition;
import pipe.gui.Constants;



public class StateViewer extends JLayeredPane implements Constants
{
	private static final long serialVersionUID = 1L;
	
	DataLayer stateDataLayer;
	StateGroup activeStateGroup;
	ArrayList<ConditionPlace> condPlaces;
	JDialog parent;

	public StateViewer()
	{
			setLayout(null);
			setOpaque(true);
			setDoubleBuffered(true);
			setAutoscrolls(true);
			setBackground(ELEMENT_FILL_COLOUR);			
			setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));				
	}
	
	public void setParent(JDialog parent)
	{
		this.parent = parent;	
	}
	
	
	public void drawPetriNet(DataLayer pnmldata, StateGroup stateGroupData)
	{

		// Create a clone of the DataLayer object as only one GUI can display an instance of a PetriNetObject at a time
		// and it is currently being displayed by the primary window (GuiView)
		stateDataLayer = pnmldata.clone();
		activeStateGroup = stateGroupData;
		condPlaces = new ArrayList<ConditionPlace>();
		
		// Iterate through the petri-net objects adding them to the GUI
		Iterator PNObjects = stateDataLayer.getPetriNetObjects();

		while(PNObjects.hasNext())
			insertUI( PNObjects.next() );			
		
		updatePreferredSize();		
	}
	
	public void insertUI(Object diffObj)
	{
		if (diffObj!=null  &&  diffObj instanceof PetriNetObject)
			add((PetriNetObject)diffObj);			
		
		repaint();
	}
	
	public void updatePreferredSize() {
		// iterate over net objects and setPreferredSize() accordingly
		Component[] components=getComponents();
		Dimension d=new Dimension(0,0);
		int x,y;
		
		for(int i=0;i<components.length;i++)
		{
			Rectangle r=components[i].getBounds();
			x=r.x+r.width+100;
			y=r.y+r.height+100;
			if (x>d.width)  d.width =x;
			if (y>d.height) d.height=y;
		}
		setPreferredSize(d);
	}

	
	public void add(PetriNetObject currentObj)
	{
		if (currentObj instanceof Place)
		{
			ConditionPlace place = new ConditionPlace((Place)currentObj);
			
			// Set the state group condition associated with the place
			StateElement placeCondition = activeStateGroup.getCondition(place.getId());
			if (placeCondition != null){
				if(placeCondition.getOperator()!="T"){
					place.setCondition(placeCondition.getOperator(), placeCondition.getPlaceB());
				}
				else{
					place.setCondition(placeCondition.getOperator(), "T");					
				}
			}

			ConditionPlaceHandler handler = new ConditionPlaceHandler(parent,place,stateDataLayer);
			place.addMouseListener(handler);
			place.deselect();
					
			super.add(place);
			
			setLayer(place, DEFAULT_LAYER.intValue() + PLACE_TRANSITION_LAYER_OFFSET);
			place.addedToGui(); // this will add the place labels
			
			condPlaces.add(place);
		}

		// We ignore the Annotation nodes - these nodes will need further development
		else if (currentObj instanceof AnnotationNote);
			
		else
		{
			currentObj.deselect();
			super.add(currentObj);
			
	
			if (currentObj instanceof ArcPathPoint)
				setLayer(currentObj, DEFAULT_LAYER.intValue() + ARC_POINT_LAYER_OFFSET);
			
			else if (currentObj instanceof Arc)
				setLayer(currentObj, DEFAULT_LAYER.intValue() + ARC_LAYER_OFFSET);	
			
			else if (currentObj instanceof Transition)
				setLayer(currentObj, DEFAULT_LAYER.intValue() + PLACE_TRANSITION_LAYER_OFFSET);
				
			else if (currentObj instanceof AnnotationNote)
				setLayer(currentObj, DEFAULT_LAYER.intValue() + ANNOTATION_LAYER_OFFSET);	
		}
	}
	
	
	/**
	 * This method sets the condition on all places to be equal zero
	 */
	
	public void setEqualZeroCond()
	{
		for(ConditionPlace curPlace : condPlaces)
		{
			// Update the 'change buffer' in PassageState
			((StateEditor)parent).addStateElement(curPlace.getId(), "=", "0");
			
			// Update the UI
			curPlace.setCondition("=", "0");
		}
	}
	



	/**
	 * This method clears the marking on all states
	 */
	
	public void clearAllCond()
	{
		for(ConditionPlace curPlace : condPlaces)
		{
			// Update the 'change buffer' with a blank condition; this will remove the condition
			((StateEditor)parent).addStateElement(curPlace.getId(), "", "");
			
			// Update the UI
			curPlace.removeCondition();
		}
	}
	
	/**
	 * This method sets the condition on each place to be equal its initial marking
	 */
	
	public void setInitialCond()
	{
		for(ConditionPlace curPlace : condPlaces)
		{
			String currentMarking = Integer.toString(curPlace.getCurrentMarking() );
			((StateEditor)parent).addStateElement(curPlace.getId(), "=", currentMarking);
			
			// Update the UI
			curPlace.setCondition("=", currentMarking);			
		}
	}	
	

}

