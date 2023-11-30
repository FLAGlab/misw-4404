package pipe.common.dataLayer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.ImageObserver;

import pipe.gui.Constants;
import pipe.gui.CreateGui;

/**
 * <b>Place</b> - Petri-Net Place Class
 * 
 * @see
 * <p>
 * <a href="..\PNMLSchema\index.html">PNML - Petri-Net XMLSchema (stNet.xsd)</a>
 * @see
 * </p>
 * <p>
 * <a href="..\..\..\UML\dataLayer.html">UML - PNML Package </a>
 * </p>
 * @version 1.0
 * @author James D Bloom
 * 
 * @author Edwin Chung corresponding states of matrixes has been set to change
 *         when markings are altered. Users will be prompted to save their work
 *         when the markings of places are altered. (6th Feb 2007)
 * 
 * @author Edwin Chung 16 Mar 2007: modified the constructor and several other
 *         functions so that DataLayer objects can be created outside the GUI
 */
public class Place extends PlaceTransitionObject implements Cloneable
{

	/**
	 * 
	 */
	private static final long		serialVersionUID	= 6423099005115956647L;
	public final static String		type				= "Place";
	public static final int			DIAMETER			= Constants.PLACE_TRANSITION_HEIGHT;

	/** Token Width */
	public static int				tWidth				= 5;

	/** Token Height */
	public static int				tHeight				= 5;

	/** Ellipse2D.Double place */
	private static Ellipse2D.Double	place				= new Ellipse2D.Double(	0,
																				0,
																				Place.DIAMETER,
																				Place.DIAMETER);

	private static Shape			proximityPlace		= (new BasicStroke(Constants.PLACE_TRANSITION_PROXIMITY_RADIUS)).createStrokedShape(Place.place);

	/** Initial Marking */
	private Integer					initialMarking		= null;

	/** Current Marking */
	private Integer					currentMarking		= null;

	/** Initial Marking X-axis Offset */
	private Double					markingOffsetX		= null;

	/** Initial Marking Y-axis Offset */
	private Double					markingOffsetY		= null;
	private boolean					taggedToken			= false;

	/**
	 * Create empty Petri-Net Place object
	 * 
	 */
	public Place() {
	}

	/**
	 * Create Petri-Net Place object
	 * 
	 * @param positionXInput
	 *            X-axis Position
	 * @param positionYInput
	 *            Y-axis Position
	 * @param color -
	 *            modified by aed02
	 */
	public Place(final double positionXInput, final double positionYInput) {
		super(positionXInput, positionYInput);
		this.componentWidth = Place.DIAMETER;
		this.componentHeight = Place.DIAMETER;
		this.setCentre((int) this.positionX, (int) this.positionY);
		this.updateBounds();

	}

	/**
	 * Create Petri-Net Place object
	 * 
	 * @param positionXInput
	 *            X-axis Position
	 * @param positionYInput
	 *            Y-axis Position
	 * @param idInput
	 *            Place id
	 * @param color
	 *            Color
	 */
	public Place(final double positionXInput, final double positionYInput, final String idInput) {
		super(positionXInput, positionYInput, idInput);
		this.componentWidth = Place.DIAMETER;
		this.componentHeight = Place.DIAMETER;
		this.updateBounds();
	}

	/**
	 * Create Petri-Net Place object
	 * 
	 * @param positionXInput
	 *            X-axis Position
	 * @param positionYInput
	 *            Y-axis Position
	 * @param idInput
	 *            Place id
	 * @param nameInput
	 *            Name
	 * @param nameOffsetXInput
	 *            Name X-axis Position
	 * @param nameOffsetYInput
	 *            Name Y-axis Position
	 * @param initialMarkingInput
	 *            Initial Marking
	 * @param markingOffsetXInput
	 *            Marking X-axis Position
	 * @param markingOffsetYInput
	 *            Marking Y-axis Position
	 * @param tagged
	 * @param color
	 *            Color
	 */
	public Place(	final double positionXInput,
					final double positionYInput,
					final String idInput,
					final String nameInput,
					final double nameOffsetXInput,
					final double nameOffsetYInput,
					final int initialMarkingInput,
					final double markingOffsetXInput,
					final double markingOffsetYInput,
					final boolean tagged) {
		super(positionXInput, positionYInput, idInput, nameInput, nameOffsetXInput, nameOffsetYInput);
		this.initialMarking = new Integer(initialMarkingInput);
		this.currentMarking = new Integer(initialMarkingInput);
		this.markingOffsetX = new Double(markingOffsetXInput);
		this.markingOffsetY = new Double(markingOffsetYInput);
		this.taggedToken = tagged;
		this.componentWidth = Place.DIAMETER;
		this.componentHeight = Place.DIAMETER;
		this.setCentre((int) this.positionX, (int) this.positionY);
		this.updateBounds();
	}

	/**
	 * Returns the height bounds we want to use when initially creating the
	 * place on the gui
	 * 
	 * @return Height bounds of Place
	 */
	@Override
	public int boundsHeight()
	{
		return ImageObserver.HEIGHT + 1;
	}

	/**
	 * Returns the width bounds we want to use when initially creating the place
	 * on the gui
	 * 
	 * @return Width bounds of Place
	 */
	@Override
	public int boundsWidth()
	{
		return ImageObserver.WIDTH + 1;
	}

	@Override
	public PetriNetObject clone()
	{
		return super.clone();
	}

	/**
	 * Determines whether the point (x,y) is "in" this component. This method is
	 * called when mouse events occur and only events at points for which this
	 * method returns true will be dispatched to mouse listeners
	 */
	@Override
	public boolean contains(final int x, final int y)
	{

		final int zoomPercentage = this.getZoomController().getPercent();
		final double unZoomedX = (x - PetriNetObject.COMPONENT_DRAW_OFFSET) / (zoomPercentage / 100.0);
		final double unZoomedY = (y - PetriNetObject.COMPONENT_DRAW_OFFSET) / (zoomPercentage / 100.0);

		PlaceTransitionObject.someArc = CreateGui.getView().createArc;
		if (PlaceTransitionObject.someArc != null) // Must be drawing a
		// new Arc if
		// non-NULL.
		{
			if ((Place.proximityPlace.contains((int) unZoomedX, (int) unZoomedY) || Place.place.contains(	(int) unZoomedX,
																											(int) unZoomedY)) &&
				this.areNotSameType(PlaceTransitionObject.someArc.getSource()))
			{
				// assume we are only snapping the target...
				if (PlaceTransitionObject.someArc.getTarget() != this)
				{
					PlaceTransitionObject.someArc.setTarget(this);
				}
				PlaceTransitionObject.someArc.updateArcPosition();
				return true;
			}
			else
			{
				if (PlaceTransitionObject.someArc.getTarget() == this)
				{
					PlaceTransitionObject.someArc.setTarget(null);
					this.updateConnected();
				}
				return false;
			}

		}
		else
		{
			return Place.place.contains((int) unZoomedX, (int) unZoomedY);
		}
	}

	/**
	 * Get current marking
	 * 
	 * @return Integer value for current marking
	 */
	public int getCurrentMarking()
	{
		if (this.currentMarking == null)
		{
			return 0;
		}
		else
		{
			return this.currentMarking.intValue();
		}
	}

	/**
	 * Get current marking
	 * 
	 * @return Integer value for current marking
	 */
	public Integer getCurrentMarkingObject()
	{
		return this.currentMarking;
	}

	/**
	 * Returns the diameter of this Place at the current zoom
	 */
	public int getDiameter()
	{
		final int zoomBy = this.getZoomController().getPercent();
		return (int) (Place.DIAMETER * zoomBy * 0.01);
	}

	/**
	 * Get initial marking
	 * 
	 * @return Integer value for initial marking
	 */
	public int getInitialMarking()
	{
		if (this.initialMarking == null)
		{
			return 0;
		}
		return this.initialMarking.intValue();
	}

	/**
	 * Get initial marking
	 * 
	 * @return Integer value for initial marking
	 */
	public Integer getInitialMarkingObject()
	{
		return this.initialMarking;
	}

	/**
	 * Get X-axis offset for initial marking
	 * 
	 * @return Double value for X-axis offset of initial marking
	 */
	public double getMarkingOffsetX()
	{
		if (this.markingOffsetX == null)
		{
			return 0;
		}
		return this.markingOffsetX.intValue();
	}

	/**
	 * Get X-axis offset for initial marking
	 * 
	 * @return Double value for X-axis offset of initial marking
	 */
	public Double getMarkingOffsetXObject()
	{
		return this.markingOffsetX;
	}

	/**
	 * Get Y-axis offset for initial marking
	 * 
	 * @return Double value for X-axis offset of initial marking
	 */
	public double getMarkingOffsetY()
	{
		if (this.markingOffsetY == null)
		{
			return 0;
		}
		return this.markingOffsetY.intValue();
	}

	/**
	 * Get Y-axis offset for initial marking
	 * 
	 * @return Double value for X-axis offset of initial marking
	 */
	public Double getMarkingOffsetYObject()
	{
		return this.markingOffsetY;
	}

	public boolean isTagged()
	{
		return this.taggedToken;
	}

	/**
	 * Paints the Place component taking into account the number of tokens from
	 * the currentMarking
	 * 
	 * @param g
	 *            The Graphics object onto which the Place is drawn.
	 */
	@Override
	public void paintComponent(final Graphics g)
	{
		super.paintComponent(g);

		final Graphics2D g2 = (Graphics2D) g;
		final AffineTransform saveXform = g2.getTransform();
		final AffineTransform scaledXform = this.getZoomController().getTransform();
		final Insets insets = this.getInsets();
		final int x = insets.left;
		final int y = insets.top;

		g2.translate(PetriNetObject.COMPONENT_DRAW_OFFSET, PetriNetObject.COMPONENT_DRAW_OFFSET);
		g2.transform(scaledXform);

		g2.setStroke(new BasicStroke(1.0f));
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		if (this.isTagged())
		{
			final AffineTransform oldTransform = g2.getTransform();

			final AffineTransform scaleTransform = new AffineTransform();
			scaleTransform.setToScale(1.2, 1.2);

			g2.transform(scaleTransform);

			g2.translate(-2, -2);

			g2.fill(Place.place);
			// g2.draw(place);

			g2.translate(2, 2);

			g2.setTransform(oldTransform);
			
			/*
			//draw one tagged token
			g2.setColor(Color.red);
			g2.drawOval(x + 12, y + 13, Place.tWidth, Place.tHeight);		
			g2.fillOval(x + 12, y + 13, Place.tWidth, Place.tHeight);
			g2.setColor(Color.black);
			g2.setTransform(saveXform);
			
			*/
		}

		if (this.selected && !PetriNetObject.ignoreSelection)
		{
			g2.setColor(Constants.SELECTION_FILL_COLOUR);
		}
		else
		{
			g2.setColor(Constants.ELEMENT_FILL_COLOUR);
		}
		g2.fill(Place.place);

		if (this.selected && !PetriNetObject.ignoreSelection)
		{
			g2.setPaint(Constants.SELECTION_LINE_COLOUR);
		}
		else
		{
			g2.setPaint(Constants.ELEMENT_LINE_COLOUR);
		}
		g2.draw(Place.place);

		//if the place is tagged, a tagged token is already drawn
		int marking = this.getCurrentMarking();
		//if(this.isTagged()) marking--;
		
		
		// structure sees how many markings there are and fills the place in
		// with the appropriate number.
		switch (marking)
		{
			case 5 :
				g.drawOval(x + 6, y + 6, Place.tWidth, Place.tHeight);
				g.fillOval(x + 6, y + 6, Place.tWidth, Place.tHeight);
			case 4 :
				g.drawOval(x + 18, y + 20, Place.tWidth, Place.tHeight);
				g.fillOval(x + 18, y + 20, Place.tWidth, Place.tHeight);
			case 3 :
				g.drawOval(x + 6, y + 20, Place.tWidth, Place.tHeight);
				g.fillOval(x + 6, y + 20, Place.tWidth, Place.tHeight);
			case 2 :
				g.drawOval(x + 18, y + 6, Place.tWidth, Place.tHeight);
				g.fillOval(x + 18, y + 6, Place.tWidth, Place.tHeight);
			case 1 :
				g.drawOval(x + 12, y + 13, Place.tWidth, Place.tHeight);
				g.fillOval(x + 12, y + 13, Place.tWidth, Place.tHeight);
				break;
			case 0 :
				break;
			default :
				g.drawString(String.valueOf(marking), x + 5, y + 20);
		}

		g2.setTransform(saveXform);

	}

	/**
	 * Set current marking
	 * 
	 * @param currentMarkingInput
	 *            Integer value for current marking
	 */
	public void setCurrentMarking(final int currentMarkingInput)
	{
		this.currentMarking = new Integer(currentMarkingInput);
		DataLayer.currentMarkingVectorChanged = true;
		CreateGui.getView().netChanged = true;
	}

	/**
	 * Set initial marking
	 * 
	 * @param initialMarkingInput
	 *            Integer value for initial marking
	 */
	public void setInitialMarking(final int initialMarkingInput)
	{
		this.initialMarking = new Integer(initialMarkingInput);
		DataLayer.initialMarkingVectorChanged = true;
		CreateGui.getView().netChanged = true;

	}

	/**
	 * Set X-axis offset for initial marking
	 * 
	 * @param markingOffsetXInput
	 *            Integer value for X-axis offset of initial marking
	 */
	public void setmarkingOffsetX(final double markingOffsetXInput)
	{
		this.markingOffsetX = new Double(markingOffsetXInput);
	}

	/**
	 * Set Y-axis offset for initial marking
	 * 
	 * @param markingOffsetYInput
	 *            Integer value for Y-axis offset of initial marking
	 */
	public void setmarkingOffsetY(final double markingOffsetYInput)
	{
		this.markingOffsetY = new Double(markingOffsetYInput);
	}

	public void setTagged(final boolean flag)
	{
		this.taggedToken = flag;
		this.repaint();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pipe.dataLayer.PlaceTransitionObject#updateEndPoint(pipe.dataLayer.Arc)
	 */
	@Override
	public void updateEndPoint(final Arc arc)
	{

		if (arc.getSource() == this)
		{
			// Make it calculate the angle from the centre of the place rather
			// than the current start point

			arc.setSourceLocation(this.positionX + this.getDiameter() * 0.5, this.positionY +
																				this.getDiameter() * 0.5);

			final double angle = arc.getArcPath().getStartAngle();
			arc.setSourceLocation(this.positionX + this.centreOffsetLeft() - 0.5 * this.getDiameter() *
									Math.sin(angle), this.positionY + this.centreOffsetTop() + 0.5 *
														this.getDiameter() * Math.cos(angle));
		}
		else
		{
			// Make it calculate the angle from the centre of the place rather
			// than the current target point

			arc.setTargetLocation(this.positionX + this.getDiameter() * 0.5, this.positionY +
																				this.getDiameter() * 0.5);

			final double angle = arc.getArcPath().getEndAngle();
			arc.setTargetLocation(this.positionX + this.centreOffsetLeft() - 0.5 * this.getDiameter() *
									Math.sin(angle), this.positionY + this.centreOffsetTop() + 0.5 *
														this.getDiameter() * Math.cos(angle));
		}
	}

}