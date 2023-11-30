package pipe.common.dataLayer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.text.StyleConstants;

import pipe.gui.Constants;
import pipe.gui.CreateGui;
import pipe.gui.FireTaggedTokenAction;

/**
 * <b>Transition</b> - Petri-Net Transition Class
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
 * 
 */

public class Transition extends PlaceTransitionObject implements Constants
{

	class ArcAngleCompare implements Comparable
	{
		public final static boolean	SOURCE	= false;
		public final static boolean	TARGET	= true;
		Arc							arc;
		private final Transition	transition;
		double						angle;

		public ArcAngleCompare(final Arc _arc, final Transition _transition) {
			this.arc = _arc;
			this.transition = _transition;
			this.calcAngle();
		}

		void calcAngle()
		{
			final int index = this.sourceOrTarget() ? this.arc.getArcPath().getEndIndex() - 1 : 1;
			final Point2D.Double p1 = new Point2D.Double(	Transition.this.positionX +
															Transition.this.centreOffsetLeft(),
															Transition.this.positionY +
															Transition.this.centreOffsetTop());
			final Point2D.Double p2 = new Point2D.Double(	this.arc.getArcPath().getPoint(index).x,
															this.arc.getArcPath().getPoint(index).y);

			if (p1.y <= p2.y)
			{
				this.angle = Math.atan((p1.x - p2.x) / (p2.y - p1.y));
			}
			else
			{
				this.angle = Math.atan((p1.x - p2.x) / (p2.y - p1.y)) + Math.PI;
			}

			// This makes sure the angle overlap lies at the intersection
			// between edges of a transition
			// Yes it is a nasty hack (a.k.a. ingenious solution). But it works!
			if (this.angle < Math.toRadians(30 + this.transition.getAngle()))
			{
				this.angle += 2 * Math.PI;
			}

			// Needed to eliminate an exception on Windows
			if (p1.equals(p2))
			{
				this.angle = 0;
			}

// if (sourceOrTarget())
// angle = arc.getArcPath().getEndAngle();
// else
// angle = arc.getArcPath().getStartAngle();
		}

		public int compareTo(final Object arg0)
		{
			final double angle2 = ((ArcAngleCompare) arg0).angle;
			return this.angle < angle2 ? -1 : this.angle == angle2 ? 0 : 1;
		}

		public boolean sourceOrTarget()
		{
			return this.arc.getSource() == this.transition ? ArcAngleCompare.SOURCE : ArcAngleCompare.TARGET;
		}
	}

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -8909295423027483506L;

	public final static String	type				= "Transition";
	/** Place Width */
	public static final int		TRANSITION_HEIGHT	= Constants.PLACE_TRANSITION_HEIGHT;
	/** Place Width */
	public static final int		TRANSITION_WIDTH	= Transition.TRANSITION_HEIGHT / 3;
	private static final double	rootThreeOverTwo	= 0.5 * Math.sqrt(3);

	private FiringTaggedButton	firingTaggedButton;
	
	/** Transition is of Rectangle2D.Double */
	private GeneralPath			transition;
	private Shape				proximityTransition;
	private int					angle;
	private boolean				enabled				= false;

	private boolean				taggedTokenEnabled	= false;
	
	private boolean				enabledBackwards	= false;

	public boolean				highlighted			= false;
	
	private boolean				firingTaggedVisible	= false;

	private boolean				infiniteServer		= false;

	/** The transition rate */
	private double				rate				= 1;
	
	/*
	 * the transition rate to fire tagged token
	 */
	private double				rate_tagged			= 1;

	/** Is this a timed transition or not? */
	private boolean				timed				= false;

	private final ArrayList		arcAngleList		= new ArrayList();

	/**
	 * Create Petri-Net Transition object
	 * 
	 */
	public Transition() {
	}

	/**
	 * Create Petri-Net Transition object
	 * 
	 * @param positionXInput
	 *            X-axis Position
	 * @param positionYInput
	 *            Y-axis Position
	 * @param color
	 *            Color
	 */
	public Transition(final double positionXInput, final double positionYInput) {
		super(positionXInput, positionYInput);
		this.componentWidth = Transition.TRANSITION_HEIGHT; // sets width
		this.componentHeight = Transition.TRANSITION_HEIGHT;// sets height

		this.constructTransition();
		this.setCentre((int) this.positionX, (int) this.positionY);
		this.updateBounds();
	}

	/**
	 * Create Petri-Net Transition object
	 * 
	 * @param positionXInput
	 *            X-axis Position
	 * @param positionYInput
	 *            Y-axis Position
	 * @param idInput
	 *            Transition id
	 * @param nameInput
	 *            Name
	 * @param nameOffsetXInput
	 *            Name X-axis Position
	 * @param nameOffsetYInput
	 *            Name Y-axis Position
	 * @param infServer
	 *            TODO
	 * @param color
	 *            Color
	 */
	public Transition(	final double positionXInput,
						final double positionYInput,
						final String idInput,
						final String nameInput,
						final double nameOffsetXInput,
						final double nameOffsetYInput,
						final double aRate,
						final boolean timedTransition,
						final boolean infServer,
						final int angleInput) {
		super(positionXInput, positionYInput, idInput, nameInput, nameOffsetXInput, nameOffsetYInput);
		this.componentWidth = Transition.TRANSITION_HEIGHT; // sets width
		this.componentHeight = Transition.TRANSITION_HEIGHT;// sets height
		this.rate = aRate;
		this.timed = timedTransition;
		this.infiniteServer = infServer;
		this.constructTransition();
		this.angle = 0;
		this.setCentre((int) this.positionX, (int) this.positionY);
		this.rotate(angleInput);
		this.updateBounds();
	}

	/**
	 * Returns the height bounds we want to use when initially creating the
	 * place on the gui
	 */
	@Override
	public int boundsHeight()
	{
		return ImageObserver.HEIGHT + 15;
	}

	/**
	 * Returns the width bounds we want to use when initially creating the place
	 * on the gui
	 */
	@Override
	public int boundsWidth()
	{
		return ImageObserver.WIDTH + 25;
	}

	private void constructTransition()
	{
		this.transition = new GeneralPath();
		this.transition.append(	new Rectangle2D.Double(	(this.componentWidth - Transition.TRANSITION_WIDTH) / 2,
														0,
														Transition.TRANSITION_WIDTH,
														Transition.TRANSITION_HEIGHT),
								false);

		this.outlineTransition();
	}

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
			if ((this.proximityTransition.contains((int) unZoomedX, (int) unZoomedY) || this.transition.contains(	(int) unZoomedX,
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
					this.removeArcCompareObject(PlaceTransitionObject.someArc);
					this.updateConnected();
				}
				return false;
			}
		}
		else
		{
			return this.transition.contains((int) unZoomedX, (int) unZoomedY);
		}
	}

	public int getAngle()
	{
		return this.angle;
	}

	public double getRate()
	{
		return this.rate;
	}
	
	public double getRateTagged()
	{
		return this.rate_tagged;
	}

	/**
	 * Determines whether Transition is enabled
	 * 
	 * @return True if enabled
	 */
	@Override
	public boolean isEnabled()
	{
		return this.enabled;
	}

	public boolean isTaggedTokenEnabled()
	{
		return this.taggedTokenEnabled;
	}
	
	/**
	 * Determines whether Transition is enabled
	 * 
	 * @param animationStatus
	 *            Anamation status
	 * @return True if enabled
	 */
	public boolean isEnabled(final boolean animationStatus)
	{
		if (animationStatus == true)
		{

			// System.out.println("is Enabled called within Transition");
			if (this.enabled == true)
			{
				this.highlighted = true;
				return true;
			}
			else
			{
				this.highlighted = false;
				return false;
			}
		}
		else
		{
			return false;
		}
	}

	public boolean isTaggedEnabled(final boolean animationStatus)
	{
		if (animationStatus == true)
		{

			// System.out.println("is Enabled called within Transition");
			if (this.taggedTokenEnabled == true)
			{
				this.firingTaggedVisible = true;
				return true;
			}
			else
			{
				this.firingTaggedVisible = false;
				return false;
			}
		}
		else
		{
			return false;
		}
	}
	
	
	/**
	 * Determines whether Transition is enabled backwards
	 * 
	 * @return True if enabled
	 */
	public boolean isEnabledBackwards()
	{
		return this.enabledBackwards;
	}

	public boolean isInfiniteServer()
	{
		return this.infiniteServer;
	}

	/** Get the timed transition attribute (for GSPNs) */
	public boolean isTimed()
	{
		return this.timed;
	}

	/**
	 * Returns the distance between the outside of the component to the centre,
	 * in order to position the centre of the place where the mouse clicks on
	 * the screen
	 */
	public int leftOffset()
	{
		return this.boundsWidth() / 2;
	}

	private void outlineTransition()
	{
		this.proximityTransition = (new BasicStroke(Constants.PLACE_TRANSITION_PROXIMITY_RADIUS)).createStrokedShape(this.transition);
	}

	@Override
	public void paintComponent(final Graphics g)
	{
		//System.out.println("\nin paintCom in transition");
		super.paintComponent(g);

		final Graphics2D g2 = (Graphics2D) g;
		final AffineTransform saveXform = g2.getTransform();
		final AffineTransform scaledXform = this.getZoomController().getTransform();

		g2.translate(PetriNetObject.COMPONENT_DRAW_OFFSET, PetriNetObject.COMPONENT_DRAW_OFFSET);
		g2.transform(scaledXform);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		if (this.selected && !PetriNetObject.ignoreSelection)
		{
			g2.setColor(Constants.SELECTION_FILL_COLOUR);
		}
		else
		{
			g2.setColor(Constants.ELEMENT_FILL_COLOUR);
		}

		if (this.timed)
		{
			if (this.infiniteServer)
			{
				for (int i = 2; i >= 1; i--)
				{
					g2.translate(2 * i, -2 * i);
					g2.fill(this.transition);
					final Paint pen = g2.getPaint();
					if (this.highlighted)
					{
						g2.setPaint(Constants.ENABLED_TRANSITION_COLOUR);
					}
					else if (this.selected && !PetriNetObject.ignoreSelection)
					{
						g2.setPaint(Constants.SELECTION_LINE_COLOUR);
					}
					else
					{
						g2.setPaint(Constants.ELEMENT_LINE_COLOUR);
					}
					g2.draw(this.transition);
					g2.setPaint(pen);
					g2.translate(-2 * i, 2 * i);
				}
			}
			g2.fill(this.transition);
		}
		
		

		if (this.highlighted)
		{
			g2.setPaint(Constants.ENABLED_TRANSITION_COLOUR);
		}
		else if (this.selected && !PetriNetObject.ignoreSelection)
		{
			g2.setPaint(Constants.SELECTION_LINE_COLOUR);
		}
		else
		{
			g2.setPaint(Constants.ELEMENT_LINE_COLOUR);
		}

		g2.draw(this.transition);

		
		
		if(this.firingTaggedVisible)
		{
			//System.out.println("\n visible");
			/*
			this.firingTaggedButton = new FiringTaggedButton((int)this.positionX, (int)this.positionY);		
			this.firingTaggedButton.addMouseListener(new FireTaggedTokenAction(this));
			CreateGui.getView().add(firingTaggedButton);
			this.firingTaggedButton.setBounds((int)this.positionX-1, (int)this.positionY,Token.DIAMETER+5,Token.DIAMETER+5);
			*/
			
			this.pnName.addMouseListener( new FireTaggedTokenAction(this));
			this.pnName.setForeground(Color.red);
			this.pnName.setText(this.id + " TAG");
			this.pnName.setSize(	(int) (this.pnName.getPreferredSize().width * 1.2),
					(int) (this.pnName.getPreferredSize().height * 1.2));
		}
		else
		{
			
			this.pnName.setForeground(Color.black);
			this.pnName.setText(this.id);
			
			/*
			if(this.firingTaggedButton!=null){

				System.out.println("\nnot null");
				
				CreateGui.getView().remove(this.firingTaggedButton);
				firingTaggedButton.removeAll();
				g2.setPaint(Constants.ELEMENT_FILL_COLOUR);
				//g2.setPaint(Color.cyan);
				g2.draw(new Ellipse2D.Double(   ((this.componentWidth - Transition.TRANSITION_WIDTH) / 2) - Transition.TRANSITION_WIDTH -1 ,
						0,Token.DIAMETER+5, Token.DIAMETER+6));
			    g2.fill(new Ellipse2D.Double(  ((this.componentWidth - Transition.TRANSITION_WIDTH) / 2) - Transition.TRANSITION_WIDTH -1,
						0,Token.DIAMETER+5, Token.DIAMETER+6));
	
			}	*/
			
			
		}
		
		
		
		if (!this.timed)
		{
			if (this.infiniteServer)
			{
				for (int i = 2; i >= 1; i--)
				{
					g2.translate(2 * i, -2 * i);
					final Paint pen = g2.getPaint();
					g2.setPaint(Constants.ELEMENT_FILL_COLOUR);
					g2.fill(this.transition);
					g2.setPaint(pen);
					g2.draw(this.transition);
					g2.translate(-2 * i, 2 * i);
				}
			}
			g2.draw(this.transition);
			g2.fill(this.transition);
		}

		/* was implemented for right-click for firing tagged
		if(this.firingTaggedVisible)
		{
			g2.setPaint(Constants.ENABLED_TRANSITION_COLOUR);
			//g2.draw(this.transition);
			//g2.fill(this.transition);
			g2.draw(new Ellipse2D.Double(   ((this.componentWidth - Transition.TRANSITION_WIDTH) / 2) - Transition.TRANSITION_WIDTH -1 ,
					0,Token.DIAMETER+5, Token.DIAMETER+5));
		}*/
		
		
		g2.setTransform(saveXform);
	}


	public void removeArcCompareObject(final Arc a)
	{
		final Iterator arcIterator = this.arcAngleList.iterator();
		while (arcIterator.hasNext())
		{
			if (((ArcAngleCompare) arcIterator.next()).arc == a)
			{
				arcIterator.remove();
			}
		}
	}

	/**
	 * Rotates the Transition through the specified angle around the midpoint
	 */
	public void rotate(final int angleInc)
	{
		this.angle = (this.angle + angleInc) % 360;
		this.transition.transform(AffineTransform.getRotateInstance(Math.toRadians(angleInc),
																	this.componentWidth / 2,
																	this.componentHeight / 2));
		this.outlineTransition();
		final Iterator arcIterator = this.arcAngleList.iterator();
		while (arcIterator.hasNext())
		{
			((ArcAngleCompare) arcIterator.next()).calcAngle();
		}
		Collections.sort(this.arcAngleList);
		this.updateEndPoints();
		this.repaint();
	}

	/**
	 * Sets whether Transition is enabled
	 * 
	 * @return enabled if True
	 */
	@Override
	public void setEnabled(final boolean status)
	{
		this.enabled = status;
	}
	
	public void setTaggedTokenEnabled(final boolean status)
	{
		this.taggedTokenEnabled = status;
	}
	

	/**
	 * Sets whether Transition is enabled
	 * 
	 * @return enabled if True
	 */
	public void setEnabledBackwards(final boolean status)
	{
		this.enabledBackwards = status;
	}

	//called at the end of animation to reset transition to b false for tagged token
	public void setTaggedEngabledFalse()
	{
		this.taggedTokenEnabled = false;
		this.firingTaggedVisible = false;
	}
	
	/* called at the end of animation to reset Transitions to false */
	public void setEnabledFalse()
	{
		this.enabled = false;
		this.highlighted = false;
	}

	public void setHighlighted(final boolean status)
	{
		this.highlighted = status;
	}

	public void setInfiniteServer(final boolean status)
	{
		this.infiniteServer = status;
		this.repaint();
	}

	public void setRate(final double _rate)
	{
		this.rate = _rate;
	}
	
	public void setRateTagged(final double _rate)
	{
		this.rate_tagged = _rate;
	}

	/** Set the timed transition attribute (for GSPNs) */
	public void setTimed(final boolean change)
	{
		if (this.timed != change)
		{
			this.timed = change;
			this.repaint();
		}
	}

	/**
	 * Returns the distance between the outside of the component to the centre,
	 * in order to position the centre of the place where the mouse clicks on
	 * the screen
	 */
	public int topOffset()
	{
		return this.boundsHeight() / 2;
	}


	//method used in fireTransition
	public int hasTaggedPlaceUntaggedArc()
	{
		final Iterator arcIterator = this.arcAngleList.iterator();
		while (arcIterator.hasNext())
		{
			final ArcAngleCompare thisArc = (ArcAngleCompare) arcIterator.next();
			
			if( thisArc.arc.getSource() instanceof Place )
			if(!thisArc.arc.isTagged()  && ((Place)thisArc.arc.getSource()).isTagged() )return ((Place)thisArc.arc.getSource()).getCurrentMarking();
		}
		return 0;
	}
	
	
	//return the marking of the place that contains tagged token
	//retunr 0 if no tagged arc and tagged place
	//return 1 if marking of tagged place == arc weight (not enough ut' token to fire in ut' mode)
	//return 2 if marking of tagged place > arc weight (can fire in both modes)
	public int hasTaggedArcAndPlace()
	{
		final Iterator arcIterator = this.arcAngleList.iterator();
		while (arcIterator.hasNext())
		{
			final ArcAngleCompare thisArc = (ArcAngleCompare) arcIterator.next();
			
			if( thisArc.arc.getSource() instanceof Place )
			if(thisArc.arc.isTagged()  && ((Place)thisArc.arc.getSource()).isTagged() ){
				if( thisArc.arc.getWeight() == ((Place)thisArc.arc.getSource()).getCurrentMarking())
					return 1;
				else return 2;
			}
		}
		return 0;
	}
	
	//return id of output place that connected to tagged arc
	public String getIdOfTaggedPlace()
	{
		final Iterator arcIterator = this.arcAngleList.iterator();
		while (arcIterator.hasNext())
		{
			final ArcAngleCompare thisArc = (ArcAngleCompare) arcIterator.next();
			
			if( thisArc.arc.getTarget() instanceof Place )
			if(thisArc.arc.isTagged())return ((Place)thisArc.arc.getTarget()).getId();
		}
		return null;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see pipe.dataLayer.PlaceTransitionObject#updateEndPoint(pipe.dataLayer.Arc)
	 */
	@Override
	public void updateEndPoint(final Arc arc)
	{
		final Iterator arcIterator = this.arcAngleList.iterator();
		boolean match = false;
		while (arcIterator.hasNext())
		{
			final ArcAngleCompare thisArc = (ArcAngleCompare) arcIterator.next();
			if (thisArc.arc == arc)
			{
				thisArc.calcAngle();
				match = true;
				break;
			}
		}
		if (!match)
		{
			this.arcAngleList.add(new ArcAngleCompare(arc, this));
		}
		Collections.sort(this.arcAngleList);

		this.updateEndPoints();
	}

	public void updateEndPoints()
	{
		Iterator arcIterator = this.arcAngleList.iterator();

		final ArrayList top = new ArrayList();
		final ArrayList bottom = new ArrayList();
		final ArrayList left = new ArrayList();
		final ArrayList right = new ArrayList();

		arcIterator = this.arcAngleList.iterator();
		while (arcIterator.hasNext())
		{
			final ArcAngleCompare thisArc = (ArcAngleCompare) arcIterator.next();
			final double thisAngle = thisArc.angle - Math.toRadians(this.angle);
			if (Math.cos(thisAngle) > Transition.rootThreeOverTwo)
			{
				top.add(thisArc);
				thisArc.arc.setPathToTransitionAngle(this.angle + 90);
			}
			else if (Math.cos(thisAngle) < -Transition.rootThreeOverTwo)
			{
				bottom.add(thisArc);
				thisArc.arc.setPathToTransitionAngle(this.angle + 270);
			}
			else if (Math.sin(thisAngle) > 0)
			{
				left.add(thisArc);
				thisArc.arc.setPathToTransitionAngle(this.angle + 180);
			}
			else
			{
				right.add(thisArc);
				thisArc.arc.setPathToTransitionAngle(this.angle);
			}
		}

		final AffineTransform transform = AffineTransform.getRotateInstance(Math.toRadians(this.angle +
																							Math.PI));
		final Point2D.Double transformed = new Point2D.Double();

		if (this.getZoomController() != null)
		{
			final AffineTransform zoomTransform = this.getZoomController().getTransform();
			transform.concatenate(zoomTransform);
		}

		arcIterator = top.iterator();
		transform.transform(new Point2D.Double(1, 0.5 * Transition.TRANSITION_HEIGHT), transformed); // +1
		// due
		// to
		// rounding
		// making
		// it
		// off
		// by 1
		while (arcIterator.hasNext())
		{
			final ArcAngleCompare thisArc = (ArcAngleCompare) arcIterator.next();
			if (thisArc.sourceOrTarget())
			{
				thisArc.arc.setTargetLocation(	this.positionX + this.centreOffsetLeft() + transformed.x,
												this.positionY + this.centreOffsetTop() + transformed.y);
			}
			else
			{
				thisArc.arc.setSourceLocation(	this.positionX + this.centreOffsetLeft() + transformed.x,
												this.positionY + this.centreOffsetTop() + transformed.y);
			}
			thisArc.arc.updateArrow();
		}

		arcIterator = bottom.iterator();
		transform.transform(new Point2D.Double(0, -0.5 * Transition.TRANSITION_HEIGHT), transformed);
		while (arcIterator.hasNext())
		{
			final ArcAngleCompare thisArc = (ArcAngleCompare) arcIterator.next();
			if (thisArc.sourceOrTarget())
			{
				thisArc.arc.setTargetLocation(	this.positionX + this.centreOffsetLeft() + transformed.x,
												this.positionY + this.centreOffsetTop() + transformed.y);
			}
			else
			{
				thisArc.arc.setSourceLocation(	this.positionX + this.centreOffsetLeft() + transformed.x,
												this.positionY + this.centreOffsetTop() + transformed.y);
			}
			thisArc.arc.updateArrow();
		}

		arcIterator = left.iterator();
		double inc = Transition.TRANSITION_HEIGHT / (left.size() + 1);
		double current = Transition.TRANSITION_HEIGHT / 2 - inc;
		while (arcIterator.hasNext())
		{
			final ArcAngleCompare thisArc = (ArcAngleCompare) arcIterator.next();
			transform.transform(new Point2D.Double(-0.5 * Transition.TRANSITION_WIDTH, current + 1),
								transformed); // +1
			// due
			// to
			// rounding
			// making
			// it
			// off
			// by 1
			if (thisArc.sourceOrTarget())
			{
				thisArc.arc.setTargetLocation(	this.positionX + this.centreOffsetLeft() + transformed.x,
												this.positionY + this.centreOffsetTop() + transformed.y);
			}
			else
			{
				thisArc.arc.setSourceLocation(	this.positionX + this.centreOffsetLeft() + transformed.x,
												this.positionY + this.centreOffsetTop() + transformed.y);
			}
			current -= inc;
			thisArc.arc.updateArrow();
		}

		inc = Transition.TRANSITION_HEIGHT / (right.size() + 1);
		current = -Transition.TRANSITION_HEIGHT / 2 + inc;
		arcIterator = right.iterator();
		while (arcIterator.hasNext())
		{
			final ArcAngleCompare thisArc = (ArcAngleCompare) arcIterator.next();
			transform.transform(new Point2D.Double(+0.5 * Transition.TRANSITION_WIDTH, current), transformed);
			if (thisArc.sourceOrTarget())
			{
				thisArc.arc.setTargetLocation(	this.positionX + this.centreOffsetLeft() + transformed.x,
												this.positionY + this.centreOffsetTop() + transformed.y);
			}
			else
			{
				thisArc.arc.setSourceLocation(	this.positionX + this.centreOffsetLeft() + transformed.x,
												this.positionY + this.centreOffsetTop() + transformed.y);
			}
			current += inc;
			thisArc.arc.updateArrow();
		}
	}
}
