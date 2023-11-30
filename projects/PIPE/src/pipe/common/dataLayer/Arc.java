package pipe.common.dataLayer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;

import javax.swing.JLayeredPane;

import pipe.gui.ArrowHead;
import pipe.gui.Constants;
import pipe.gui.CreateGui;
import pipe.gui.GuiView;

/**
 * <b>Arc</b> - Petri-Net Arc Class
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
 * @author Pere Bonet modifed the delete method so that the weight label of an
 *         arc is deleted when the associated arc is deleted
 * 
 * @author Edwin Chung 16 Mar 2007: modified the constructor and several other
 *         functions so that DataLayer objects can be created outside the GUI
 * 
 * @author Nick Dingle 18 Oct 2007: added the ability for an arc to be "tagged"
 *         (permit the passage of tagged tokens).
 */
public class Arc extends PetriNetObject implements Constants, Cloneable
{

	/**
	 * 
	 */
	private static final long		serialVersionUID	= 2523281047830259900L;
	public final static String		type				= "Arc";
	private static Point2D.Double	point;
	/** Current Marking */
	private int						weight				= 1;
	/** Initial Marking X-axis Offset */
	private Double					weightOffsetX		= null;
	/** Initial Marking Y-axis Offset */
	private Double					weightOffsetY		= null;
	/**
	 * Whether or not the Arc is capable of carrying tagged tokens By default it
	 * is not
	 */
	private Boolean					tagged				= false;
	/** Inscription */
// private String inscription = null;
	/** Inscription X-axis Offset */
// private Double inscriptionOffsetX = null;
	/** Inscription Y-axis Offset */
// private Double inscriptionOffsetY = null;
	/** Arc is of type Line2D.Double */
// private Line2D.Double arc = new Line2D.Double(0, 0, 0, 0);
// private Line2D.Double realarc;
	private final ArrowHead			arrow				= new ArrowHead();

// private Rectangle bounds = new Rectangle();

	private final NameLabel			weightLabel			= new NameLabel();

	/** references to the objects this arc connects */
	private PlaceTransitionObject	source				= null;
	private PlaceTransitionObject	target				= null;
	private boolean					deleted				= false;				// Used
	// for
	// cleanup
	// purposes

	private final ArcPath			myPath				= new ArcPath(this);

	/**
	 * Create Petri-Net Arc object
	 * 
	 * @param startPositionXInput
	 *            Start X-axis Position
	 * @param startPositionYInput
	 *            Start Y-axis Position
	 * @param endPositionXInput
	 *            End X-axis Position
	 * @param endPositionYInput
	 *            End Y-axis Position
	 * @param colorInput
	 *            Color
	 */
	public Arc(	final double startPositionXInput,
				final double startPositionYInput,
				final double endPositionXInput,
				final double endPositionYInput) {
// arc.setLine(startPositionXInput, startPositionYInput, endPositionXInput,
// endPositionYInput);
		this.arrow.setLocation(startPositionXInput, startPositionYInput, endPositionXInput, endPositionYInput);
		this.myPath.addPoint((float) startPositionXInput, (float) startPositionYInput, ArcPathPoint.STRAIGHT);
// myPath.addPoint(100,100, ArcPath.ArcPathPoint.STRAIGHT);
		this.myPath.addPoint((float) endPositionXInput, (float) endPositionYInput, ArcPathPoint.STRAIGHT);
		this.myPath.createPath();
		this.updateBounds();

	}

	/**
	 * Create Petri-Net Arc object
	 * 
	 * @param startPositionXInput
	 *            Start X-axis Position
	 * @param startPositionYInput
	 *            Start Y-axis Position
	 * @param endPositionXInput
	 *            End X-axis Position
	 * @param endPositionYInput
	 *            End Y-axis Position
	 * @param sourceInput
	 *            Arc source
	 * @param targetInput
	 *            Arc target
	 * @param idInput
	 *            Arc id
	 * @param inputTagged
	 *            TODO
	 * @param colorInput
	 *            Color
	 */
	public Arc(	final double startPositionXInput,
				final double startPositionYInput,
				final double endPositionXInput,
				final double endPositionYInput,
				final PlaceTransitionObject sourceInput,
				final PlaceTransitionObject targetInput,
				final int weightInput,
				final String idInput,
				final boolean taggedInput,
				final Color colorInput) {

		this(	startPositionXInput,
				startPositionYInput,
				endPositionXInput,
				endPositionYInput,
				sourceInput,
				targetInput,
				idInput,
				taggedInput,
				colorInput);
		this.setWeight(weightInput);
		this.setTagged(taggedInput);
	}

	/**
	 * Create Petri-Net Arc object
	 * 
	 * @param startPositionXInput
	 *            Start X-axis Position
	 * @param startPositionYInput
	 *            Start Y-axis Position
	 * @param endPositionXInput
	 *            End X-axis Position
	 * @param endPositionYInput
	 *            End Y-axis Position
	 * @param sourceInput
	 *            Arc source
	 * @param targetInput
	 *            Arc target
	 * @param idInput
	 *            Arc id
	 * @param inputTagged
	 *            TODO
	 * @param colorInput
	 *            Color
	 */
	public Arc(	final double startPositionXInput,
				final double startPositionYInput,
				final double endPositionXInput,
				final double endPositionYInput,
				final PlaceTransitionObject sourceInput,
				final PlaceTransitionObject targetInput,
				final String idInput,
				final boolean taggedInput,
				final Color colorInput) {

		this(startPositionXInput, startPositionYInput, endPositionXInput, endPositionYInput);

		this.id = idInput;
		this.setSource(sourceInput);
		this.setTarget(targetInput);
		if (CreateGui.getApp() != null)
		{
			this.updateArcPosition();
			this.updateArcPosition();
			this.setTagged(taggedInput);
		}

	}

	/**
	 * Create Petri-Net Arc object
	 * 
	 * @param startPositionXInput
	 *            Start X-axis Position
	 * @param startPositionYInput
	 *            Start Y-axis Position
	 * @param endPositionXInput
	 *            End X-axis Position
	 * @param endPositionYInput
	 *            End Y-axis Position
	 * @param idInput
	 *            Arc id
	 * @param colorInput
	 *            Color
	 */
	public Arc(	final double startPositionXInput,
				final double startPositionYInput,
				final double endPositionXInput,
				final double endPositionYInput,
				final String idInput) {
		this(startPositionXInput, startPositionYInput, endPositionXInput, endPositionYInput);
		this.id = idInput;

	}

	/**
	 * Create Petri-Net Arc object
	 * 
	 */
	public Arc(final PlaceTransitionObject newSource) {
		this.source = newSource;
		this.myPath.addPoint();
		this.myPath.addPoint();
		this.myPath.createPath();
	}

	// ######################################################################################
	@Override
	public void addedToGui() // called by GuiView / State viewer when adding
	// component.
	{
		if (this.getParent() instanceof GuiView)
		{
			this.myPath.addPointsToGui((GuiView) this.getParent());
		}
		else
		{
			this.myPath.addPointsToGui((JLayeredPane) this.getParent());
		}

		this.myPath.createPath();
		this.updateArcPosition();
	}

	public void addWeightLabelToContainer()
	{
		if (this.getParent() != null)
		{
			// If this is the first time the weight label
			// is being added then the remove operation
			// will have no affect. However, for subsequent
			// updates to the weight label, the remove will
			// prevent it from being added more than once.
			// Nadeem Akharware 03/06/2006
			this.getParent().remove(this.weightLabel);
			this.getParent().add(this.weightLabel);
		}
	}

	/**
	 * Method to clone an Arc object
	 */

	@Override
	public PetriNetObject clone()
	{
		return super.clone();
	}

	@Override
	public boolean contains(final int x, final int y)
	{
		Arc.point = new Point2D.Double(	x + this.myPath.getBounds().getX() -
										PetriNetObject.COMPONENT_DRAW_OFFSET,
										y + this.myPath.getBounds().getY() -
										PetriNetObject.COMPONENT_DRAW_OFFSET);
		if (CreateGui.getApp().getMode() == Constants.SELECT)
		{
			if (this.myPath.proximityContains(Arc.point) || this.selected)
			{
				// also
				// if
				// Arc
				// itself
				// selected
				this.myPath.showPoints();
			}
			else
			{
				this.myPath.hidePoints();
			}
		}
		return this.myPath.contains(Arc.point);
	}

	// ######################################################################################
	@Override
	public void delete()
	{
		if (!this.deleted)
		{
			if (this.getParent() != null)
			{
				this.getParent().remove(this.weightLabel);
			}
			this.myPath.delete();
			super.delete();
			this.deleted = true;
		}
	}

	public ArcPath getArcPath()
	{
		return this.myPath;
	}

	/**
	 * Get X-axis value of end position
	 * 
	 * @return Double value for X-axis of end position
	 */
	public double getEndPositionX()
	{
		return this.myPath.getPoint(this.myPath.getEndIndex()).getX();
	}

	/**
	 * Get Y-axis value of end position
	 * 
	 * @return Double value for Y-axis of end position
	 */
	public double getEndPositionY()
	{
		return this.myPath.getPoint(this.myPath.getEndIndex()).getY();
	}

	/**
	 * Set inscription
	 * 
	 * @param inscriptionInput
	 *            String value for Arc inscription;
	 */
// public void setInscription(String inscriptionInput) {
// inscription = inscriptionInput;
// }
	/**
	 * Set X-axis offset for inscription position
	 * 
	 * @param inscriptionOffsetXInput
	 *            Double value for inscription X-axis offset
	 */
// public void setInscriptionOffsetX(double inscriptionOffsetXInput) {
// inscriptionOffsetX = new Double(inscriptionOffsetXInput);
// }
	/**
	 * Set Y-axis offset for inscription position
	 * 
	 * @param inscriptionOffsetYInput
	 *            Double value for inscription Y-axis offset
	 */
// public void setInscriptionOffsetY(double inscriptionOffsetYInput) {
// inscriptionOffsetY = new Double(inscriptionOffsetYInput);
// }
	/**
	 * Get id
	 * 
	 * @return String value for Arc id;
	 */
	@Override
	public String getId()
	{
		if (this.id != null)
		{
			return this.id;
		}
		else
		{
			if (this.source != null && this.target != null)
			{
				return this.source.getId() + " to " + this.target.getId();
			}
			else
			{
				return "";
			}
		}
	}

	/**
	 * Get source returns null if value not yet entered
	 * 
	 * @return String value for Arc source;
	 */
	public PlaceTransitionObject getSource()
	{
		return this.source;
	}

	/**
	 * Get X-axis value of start position
	 * 
	 * @return Double value for X-axis of start position
	 */
	public double getStartPositionX()
	{
		return this.myPath.getPoint(0).getX();
	}

	/**
	 * Get Y-axis value of start position
	 * 
	 * @return Double value for Y-axis of start position
	 */
	public double getStartPositionY()
	{
		return this.myPath.getPoint(0).getY();
	}

	/**
	 * Get target returns null if value not yet entered
	 * 
	 * @return String value for Arc target;
	 */
	public PlaceTransitionObject getTarget()
	{
		return this.target;
	}

	/**
	 * Get weight
	 * 
	 * @return Integer value for Arc weight;
	 */
	public int getWeight()
	{
		return this.weight;
	}

	/**
	 * Get weight returns null if value not yet entered
	 * 
	 * 
	 * @return Integer value for Arc weight;
	 */
	public int getWeightObject()
	{
		return this.weight;
	}

	/**
	 * Get X-axis offset for weight position
	 * 
	 * @return Double value for weight X-axis offset
	 */
	public double getWeightOffsetX()
	{
		if (this.weightOffsetX == null)
		{
			return 0;
		}
		return this.weightOffsetX.doubleValue();
	}

	/**
	 * Get X-axis offset for weight position returns null if value not yet
	 * entered
	 * 
	 * @return Double value for weight X-axis offset
	 */
	public Double getWeightOffsetXObject()
	{
		return this.weightOffsetX;
	}

	/**
	 * Get Y-axis offset for weight position
	 * 
	 * @return Double value for weight Y-axis offset
	 */
	public double getWeightOffsetY()
	{
		if (this.weightOffsetY == null)
		{
			return 0;
		}
		return this.weightOffsetY.doubleValue();
	}

	/**
	 * Get Y-axis offset for weight position returns null if value not yet
	 * entered
	 * 
	 * @return Double value for weight Y-axis offset
	 */
	public Double getWeightOffsetYObject()
	{
		return this.weightOffsetY;
	}

	/**
	 * Get inscription returns null if value not yet entered
	 * 
	 * @return String value for Arc inscription;
	 */
// public String getInscription() {
// return inscription;
// }
	/**
	 * Get X-axis offset for inscription position
	 * 
	 * @return Double value for inscription X-axis offset
	 */
// public double getInscriptionOffsetX() {
// if(inscriptionOffsetX == null)
// return 0;
// return inscriptionOffsetX.doubleValue();
// }
	/**
	 * Get Y-axis offset for inscription position
	 * 
	 * @return Double value for inscription Y-axis offset
	 */
// public double getInscriptionOffsetY() {
// if(inscriptionOffsetY == null)
// return 0;
// return inscriptionOffsetY.doubleValue();
// }
	// Accessor function to check whether or not the Arc is tagged
	public boolean isTagged()
	{
		return this.tagged;
	}

	/**
	 * Get X-axis offset for inscription position returns null if value not yet
	 * entered
	 * 
	 * @return Double value for inscription X-axis offset
	 */
// public Double getInscriptionOffsetXObject() {
// return inscriptionOffsetX;
// }
	/**
	 * Get Y-axis offset for inscription position returns null if value not yet
	 * entered
	 * 
	 * @return Double value for inscription Y-axis offset
	 */
// public Double getInscriptionOffsetYObject() {
// return inscriptionOffsetY;
// }
	@Override
	public void paintComponent(final Graphics g)
	{
		super.paintComponent(g);
		final Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
// g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
// RenderingHints.VALUE_STROKE_NORMALIZE);

		g2.translate(	PetriNetObject.COMPONENT_DRAW_OFFSET - this.myPath.getBounds().getX(),
						PetriNetObject.COMPONENT_DRAW_OFFSET - this.myPath.getBounds().getY());

		if (this.selected && !PetriNetObject.ignoreSelection)
		{
			g2.setPaint(Constants.SELECTION_LINE_COLOUR);
		}
		else
		{
			g2.setPaint(Constants.ELEMENT_LINE_COLOUR);
		}

		g2.fill(this.arrow);
		g2.draw(this.myPath);

// if (selected)
// {
// myPath.createSelection();
// g2.fill(myPath.getArcSelection());
// g2.setPaint(SELECTION_LINE_COLOUR);
// g2.fill(myPath.getPointSelection());
// }

	}

	public void setEndPoint(final double x, final double y, final boolean type)
	{
		this.myPath.setPointLocation(this.myPath.getEndIndex(), x, y);
		this.myPath.setPointType(this.myPath.getEndIndex(), type);
		this.updateArcPosition();
	}

	/**
	 * Set id
	 * 
	 * @param idInput
	 *            String value for Arc id;
	 */
	@Override
	public void setId(final String idInput)
	{
		this.id = idInput;
	}

	// ######################################################################################
	public void setPathToTransitionAngle(final int angle)
	{
		this.myPath.setTransitionAngle(angle);
	}

	/**
	 * Set source
	 * 
	 * @param sourceInput
	 *            PlaceTransitionObject value for Arc source;
	 */
	public void setSource(final PlaceTransitionObject sourceInput)
	{
		this.source = sourceInput;
	}

	public void setSourceLocation(final double x, final double y)
	{
		this.myPath.setPointLocation(0, x, y);
		this.updateArrow();
		this.myPath.createPath();
		this.updateBounds();
		this.repaint();
	}

	// Accessor function to set whether or not the Arc is tagged
	// If it becomes tagged we must remove any existing weight....
	// ...and thus we can reuse the weightLabel to display that it's tagged!!!
	// Because remember that a tagged arc must have a weight of 1...
	public void setTagged(final boolean flag)
	{
		final boolean wasTagged = this.tagged;
		this.tagged = flag;
		if (this.tagged)
		{
			// weight = 1;
			this.weightLabel.setText(Integer.toString(this.weight)+"  TAG");
			
			if(this.weight ==1) this.weightLabel.setText("TAG");
			
			
			this.setWeightLabelPosition();
			this.weightLabel.updateSize();
			this.addWeightLabelToContainer();
		}
		else
		{ //un-tagged
			if(wasTagged)
				this.weightLabel.setText(Integer.toString(this.weight));
			if (wasTagged && weight==1)
			{
				this.getParent().remove(this.weightLabel);
			}
		}

		
		this.repaint();
	}

/* original 	
	public void setTagged(final boolean flag)
	{
		final boolean wasTagged = this.tagged;
		this.tagged = flag;
		if (this.tagged)
		{
			// weight = 1;
			this.weightLabel.setText("TAG");
			this.setWeightLabelPosition();
			this.weightLabel.updateSize();
			this.addWeightLabelToContainer();
		}
		else
		{
			if (wasTagged)
			{
				this.getParent().remove(this.weightLabel);
			}
		}

		this.repaint();
	}
	
	*/
	
	
	/**
	 * Set target
	 * 
	 * @param targetInput
	 *            PlaceTransitionObject value for Arc target;
	 */
	public void setTarget(final PlaceTransitionObject targetInput)
	{
		this.target = targetInput;
		if (CreateGui.getApp() != null)
		{
			this.updateArcPosition();
		}
	}

	public void setTargetLocation(final double x, final double y)
	{
		this.myPath.setPointLocation(this.myPath.getEndIndex(), x, y);
		this.updateArrow();
		this.myPath.createPath();
		this.updateBounds();
		this.repaint();
	}

	/**
	 * Set weight
	 * 
	 * @param weightInput
	 *            String value for Arc weight;
	 */
	public void setWeight(final int weightInput)
	{
		this.weight = weightInput;
		
		
		this.weightLabel.setText(Integer.toString(this.weight));
		
		if(this.tagged && this.weight!=1)this.weightLabel.setText(Integer.toString(this.weight)+"  TAG");
		if(this.tagged && this.weight==1)this.weightLabel.setText("TAG");
		
		
		this.setWeightLabelPosition();

		if (this.weight != 1)
		{
			this.weightLabel.updateSize();
			this.addWeightLabelToContainer();
		}
	}

	public void setWeightLabelPosition()
	{
		if (this.myPath.getEndIndex() > 0)
		{
			final Point2D.Float firstPoint = this.myPath.getPathPoint(1).getControl1();
			final Point2D.Float secondPoint = this.myPath.getPathPoint(1).getControl2();
			this.weightLabel.setPosition(	(int) ((secondPoint.x + firstPoint.x) * 0.5),
											(int) ((secondPoint.y + firstPoint.y) * 0.5));
		}
	}

	/**
	 * Set X-axis offset for weight position
	 * 
	 * @param weightOffsetXInput
	 *            Double value for weight X-axis offset
	 */
	public void setWeightOffsetX(final double weightOffsetXInput)
	{
		this.weightOffsetX = new Double(weightOffsetXInput);
	}

	/**
	 * Set Y-axis offset for weight position
	 * 
	 * @param weightOffsetYInput
	 *            Double value for weight Y-axis offset
	 */
	public void setWeightOffsetY(final double weightOffsetYInput)
	{
		this.weightOffsetY = new Double(weightOffsetYInput);
	}

	public void split(final Point2D.Float mouseposition)
	{
		this.myPath.splitSegment(mouseposition);
	}

	/**
	 * Updates the start position of the arc, resets the arrowhead and updates
	 * the bounds
	 */
	public void updateArcPosition()
	{
		if (this.source != null)
		{
			this.source.updateEndPoint(this);
		}

		if (this.target != null)
		{
			this.target.updateEndPoint(this);
		}

		this.myPath.createPath();
	}

	public void updateArrow()
	{
		if (this.myPath.getEndIndex() != -1)
		{
			this.arrow.setLocation(	this.myPath.getPoint(this.myPath.getEndIndex()).getX(),
									this.myPath.getPoint(this.myPath.getEndIndex()).getY(),
									this.myPath.getEndAngle() + Math.PI);
		}
	}

// ######################################################################################

	/** Updates the bounding box of the arc component based on the arcs bounds */
	public void updateBounds()
	{
		this.bounds = this.myPath.getBounds();
		this.bounds.grow(PetriNetObject.COMPONENT_DRAW_OFFSET, PetriNetObject.COMPONENT_DRAW_OFFSET);
		this.setBounds(this.bounds);
	}

}
// ######################################################################################
