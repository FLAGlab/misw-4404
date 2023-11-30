package pipe.modules.passageTimeForTaggedNet;

/*
 * This extension of the Place object is used in the GUI for creating
 * source / destination states. The primary difference is to the colouring of the Place
 * to mark change and the mouse handlers for editing the state conditions  
 * 
 */

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

import pipe.common.dataLayer.Arc;
import pipe.common.dataLayer.PetriNetObject;
import pipe.common.dataLayer.Place;
import pipe.common.dataLayer.PlaceTransitionObject;

 
public class ConditionPlace extends PlaceTransitionObject implements Cloneable
{
	private static final long serialVersionUID = 1L;
	public final static String type = "ConditionalPlace";
	
	public static final int DIAMETER = PLACE_TRANSITION_HEIGHT;
	private static Ellipse2D.Double	place = new Ellipse2D.Double(0, 0, DIAMETER, DIAMETER);


	/** Current Marking */
	private Integer currentMarking = null;
	

	private Color defaultColor = Color.lightGray,
				validColor = Color.green,
				currentColor = defaultColor;
	private String condOperator="", condOperand="";
	

	private boolean tagged=false;
	
	public boolean isTagged(){
		return tagged;
	}
	public void setTagged(boolean setTo){
		tagged = setTo;
	}

	/**
	 * Create a condition Petri-Net Place from an existing Petri-Net Place
	 */
	public ConditionPlace(Place inputPlace)
	{
		super(inputPlace.getPositionX(), inputPlace.getPositionY(), inputPlace.getId(), inputPlace.getName(), 0, 0 );
		
		currentMarking = new Integer( inputPlace.getCurrentMarking() );
		componentWidth = DIAMETER;
		componentHeight = DIAMETER;				

		setCentre((int)positionX, (int)positionY);
		updateBounds();
	}
	

	/**
	 * Paints the Place component taking into account the number of tokens from the currentMarking
	 * @param g The Graphics object onto which the Place is drawn.
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D)g;	
		AffineTransform saveXform = g2.getTransform();
		AffineTransform scaledXform = getZoomController().getTransform();
		Insets insets = getInsets();
		int x = insets.left;
		int y = insets.top;
		
		g2.translate(COMPONENT_DRAW_OFFSET, COMPONENT_DRAW_OFFSET);
		g2.transform(scaledXform);

		g2.setStroke(new BasicStroke(1.0f));	
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g2.setColor(currentColor);
		g2.fill(place);

		if (selected && !ignoreSelection)
			g2.setPaint(SELECTION_LINE_COLOUR);
		else
			g2.setPaint(ELEMENT_LINE_COLOUR);
		g2.draw(place);

		
		// Print current condition on place
		g.drawString(condOperator+condOperand,x+5,y+20);
		

		g2.setTransform(saveXform);

	}


	/**
	 * Get current marking
	 * @return Integer value for current marking
	 */
	public int getCurrentMarking() {
		if (currentMarking == null)
			return 0;
		else
			return currentMarking.intValue();
	}

	/** Returns the width bounds we want to use when initially creating the place on the gui
	 * @return Width bounds of Place
	 */
	public int boundsWidth() {
		return WIDTH +1;
	}

	/** Returns the height bounds we want to use when initially creating the place on the gui
	 * @return Height bounds of Place
	 */
	public int boundsHeight() {
		return HEIGHT +1;
	}
	
	/**
	 * Returns the diameter of this Place at the current zoom
	 */
	public int getDiameter(){
		int zoomBy = getZoomController().getPercent();
		return (int)(DIAMETER * zoomBy * 0.01);
	}

	/**
	 * Determines whether the point (x,y) is "in" this component. This method is called
	 * when mouse events occur and only events at points for which this method returns true
	 * will be dispatched to mouse listeners
	 */
	public boolean contains(int x, int y) {
		
		int zoomPercentage = getZoomController().getPercent();
		double unZoomedX=(x-COMPONENT_DRAW_OFFSET)/(zoomPercentage/100.0);
		double unZoomedY=(y-COMPONENT_DRAW_OFFSET)/(zoomPercentage/100.0);
		
		return place.contains((int)unZoomedX, (int)unZoomedY);
	}
	
	
	public PetriNetObject clone() {
		return super.clone();
	}
	
	public void setCondition(String operator, String operand)
	{
		// Convert to unicode character for UI appearance
		if (operator.equals("<=") )
			operator = "\u2264";
		else if (operator.equals(">="))
			operator = "\u2265";
		
		if(operator.equals("T")){
			this.setTagged(true);
			operand="T";
		}
		else this.setTagged(false);
		
		condOperator = operator;
		condOperand = operand;
		currentColor = validColor;
		repaint();
	}
	
	public void removeCondition()
	{
		condOperator = "";
		condOperand = "";
		currentColor = defaultColor;
		repaint();
	}


	@Override
	public void updateEndPoint(Arc arc) {}
}
