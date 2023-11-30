/**
 * StateView
 * 
 * Visualises the underlying SPN model in such a way that conditions can be specified 
 * on it
 * 
 * @author Barry Kearns
 * @author Tamas Suto
 * @date 18/08/07
 */


package pipe.modules.queryeditor.gui.performancetrees.valuenodes.labels;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JDialog;
import javax.swing.JLayeredPane;

import pipe.common.QueryConstants;
import pipe.common.dataLayer.AnnotationNote;
import pipe.common.dataLayer.Arc;
import pipe.common.dataLayer.ArcPathPoint;
import pipe.common.dataLayer.DataLayer;
import pipe.common.dataLayer.PetriNetObject;
import pipe.common.dataLayer.Place;
import pipe.common.dataLayer.StateElement;
import pipe.common.dataLayer.StateGroup;
import pipe.common.dataLayer.Transition;


public class StateView extends JLayeredPane implements QueryConstants {

	private static final long serialVersionUID = 1L;

	DataLayer stateDataLayer;
	StateGroup activeStateGroup;
	ArrayList<ConditionPlace> condPlaces;
	JDialog parent;

	public StateView() {
		setLayout(null);
		setOpaque(true);
		setDoubleBuffered(true);
		setAutoscrolls(true);
		setBackground(ELEMENT_FILL_COLOUR);			
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));				
	}

	
	public void setParent(JDialog parent) {
		this.parent = parent;	
	}

	/**
	 * Create a clone of the DataLayer object as only one GUI can display an instance of 
	 * a PetriNetObject at a time and it is currently being displayed by the primary 
	 * window (GuiView)
	 * 
	 * @param pnmldata
	 * @param stateGroupData
	 */
	public void drawPetriNet(DataLayer pnmldata, StateGroup stateGroupData) {
		stateDataLayer = pnmldata.clone();
		activeStateGroup = stateGroupData;
		condPlaces = new ArrayList<ConditionPlace>();

		// Iterate through the petri-net objects adding them to the GUI
		Iterator PNObjects = stateDataLayer.getPetriNetObjects();
		while(PNObjects.hasNext())
			insertUI( PNObjects.next() );			

		updatePreferredSize();		
	}

	public void insertUI(Object diffObj) {
		if((diffObj instanceof PetriNetObject) && (diffObj != null))
			add((PetriNetObject)diffObj);			
		repaint();
	}

	public void updatePreferredSize() {
		// iterate over net objects and setPreferredSize() accordingly
		Component[] components=getComponents();
		Dimension d=new Dimension(0,0);
		int x,y;
		for(int i = 0; i < components.length; i++) {
			Rectangle r=components[i].getBounds();
			x = r.x + r.width + 100;
			y = r.y + r.height + 100;
			if (x > d.width)  
				d.width = x;
			if (y > d.height) 
				d.height = y;
		}
		setPreferredSize(d);
	}

	public void add(PetriNetObject currentObj) {
		if (currentObj instanceof Place) {
			ConditionPlace place = new ConditionPlace((Place)currentObj);
			// Set the state group condition associated with the place
			StateElement placeCondition = activeStateGroup.getCondition(place.getId());
			if (placeCondition != null)
				place.setCondition(placeCondition.getOperator(), placeCondition.getPlaceB());

			ConditionPlaceHandler handler = new ConditionPlaceHandler(parent,place);
			place.addMouseListener(handler);			
			super.add(place);
			setLayer(place, DEFAULT_LAYER.intValue() + PLACE_TRANSITION_LAYER_OFFSET);
			place.addedToGui(); // this will add the place labels
			condPlaces.add(place);
		}
		else {
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
	public void setEqualZeroCond() {
		for(ConditionPlace curPlace : condPlaces) {
			// Update the 'change buffer' in PassageState
			((StateGroupEditor)parent).addStateElement(curPlace.getId(), "=", "0");
			// Update the UI
			curPlace.setCondition("=", "0");
		}
	}


	/**
	 * This method clears the marking on all states
	 */
	public void clearAllCond() {
		for(ConditionPlace curPlace : condPlaces) {
			// Update the 'change buffer' with a blank condition; this will remove the condition
			((StateGroupEditor)parent).addStateElement(curPlace.getId(), "", "");
			// Update the UI
			curPlace.removeCondition();
		}
	}

	/**
	 * This method sets the condition on each place to be equal its initial marking
	 */
	public void setInitialCond() {
		for(ConditionPlace curPlace : condPlaces) {
			String currentMarking = Integer.toString(curPlace.getCurrentMarking() );
			((StateGroupEditor)parent).addStateElement(curPlace.getId(), "=", currentMarking);
			// Update the UI
			curPlace.setCondition("=", currentMarking);			
		}
	}	
	
	/**
	 * Checks whether at least one condition has been specified on the model
	 * @return
	 */
	public boolean someConditionHasBeenSpecified() {
		boolean conditionHasBeenSpecified = false;
		for(ConditionPlace curPlace : condPlaces) {
			if (curPlace.conditionHasBeenSpecified())
				conditionHasBeenSpecified = true;
		}
		return conditionHasBeenSpecified;
	}

}

