package pipe.common.dataLayer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.ImageObserver;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import pipe.gui.Constants;
import pipe.gui.CreateGui;
import pipe.gui.Zoomable;

/**
 * <b>PlaceTransitionObject</b> - Petri-Net PLace or Transition SuperClass<b> -
 * <i>Abstract</i></b>
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
 * @author Edwin Chung 16 Mar 2007: modified the constructor and several other
 *         functions so that DataLayer objects can be created outside the GUI
 */

public abstract class PlaceTransitionObject extends PetriNetObject implements Zoomable, Cloneable
{

	protected static Arc		someArc;
	/** X-axis Position on screen */
	protected double			positionX;

	/** Y-axis Position on screen */
	protected double			positionY;
	protected double			componentWidth;

	protected double			componentHeight;
	private final Collection	connectTo	= new LinkedList();
	private final Collection	connectFrom	= new LinkedList();

	/**
	 * The "real" x coordinate of this place or transition in the net. i.e. the
	 * x position at 100% zoom.
	 */
	private double				locationX;

	/**
	 * The "real" y coordinate of this place or transition in the net. i.e. the
	 * y position at 100% zoom.
	 */
	private double				locationY;

	/**
	 * Create empty Petri-Net Object
	 * 
	 */
	public PlaceTransitionObject() {

	}

	/**
	 * Create Petri-Net Object This constructor does all the work, the others
	 * just call it.
	 * 
	 * @param positionXInput
	 *            X-axis Position
	 * @param positionYInput
	 *            Y-axis Position
	 * @param colorInput
	 *            Color
	 * 
	 */
	public PlaceTransitionObject(final double positionXInput, final double positionYInput) {
		if (CreateGui.getApp() != null)
		{
			this.addZoomController(CreateGui.getView().getZoomController());
		}
		this.setPositionX(positionXInput);
		this.setPositionY(positionYInput);

// s setBorder(BorderFactory.createLineBorder(Color.BLACK));

		/* sets up Namelabel for each PN object */
		this.pnName = new NameLabel();
		this.pnName.setPosition((int) positionXInput, (int) (positionYInput + 30));
	}

	/**
	 * Create Petri-Net Object
	 * 
	 * @param positionXInput
	 *            X-axis Position
	 * @param positionYInput
	 *            Y-axis Position
	 * @param idInput
	 *            Place id
	 * @param colorInput
	 *            Color
	 */
	public PlaceTransitionObject(	final double positionXInput,
									final double positionYInput,
									final String idInput) {
		this(positionXInput, positionYInput);
		this.id = idInput;

	}

	/**
	 * Create Petri-Net Object
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
	 */
	public PlaceTransitionObject(	final double positionXInput,
									final double positionYInput,
									final String idInput,
									final String nameInput,
									final double nameOffsetXInput,
									final double nameOffsetYInput) {

		this(positionXInput, positionYInput, idInput);
		this.setName(nameInput);
	}

	/** Adds inwards arc to place/transition */
	public void addConnectFrom(final Arc newArc)
	{
		//System.out.println("\nfrom");
		this.connectFrom.add(newArc);

		
	}

	/** Adds outwards arc to place/transition */
	public void addConnectTo(final Arc newArc)
	{
		//System.out.println("\nto");
		this.connectTo.add(newArc);
		
	}

	@Override
	public void addedToGui()
	{
		this.addLabelToContainer();
	}

	public boolean areNotSameType(final PlaceTransitionObject o)
	{
		return this instanceof Place && o instanceof Transition || this instanceof Transition &&
				o instanceof Place;
	}

	/**
	 * Returns the height bounds we want to use when initially creating the
	 * place on the gui
	 * 
	 * @return Height bounds of Place
	 */
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
	public int boundsWidth()
	{
		return ImageObserver.WIDTH + 1;
	}

	/**
	 * Returns the distance between the outside of the component to the centre,
	 * in order to position the centre of the place where the mouse clicks on
	 * the screen
	 * 
	 * @return Left offset of Place
	 */
	public int centreOffsetLeft()
	{
		final double zoomBy = this.getZoomController().getPercent() * 0.01;
		return (int) (zoomBy * this.componentWidth / 2.0);
	}

	/**
	 * Returns the distance between the outside of the component to the centre,
	 * in order to position the centre of the place where the mouse clicks on
	 * the screen
	 * 
	 * @return Top offset of Place
	 */
	public int centreOffsetTop()
	{
		final double zoomBy = this.getZoomController().getPercent() * 0.01;
		return (int) (zoomBy * this.componentHeight / 2.0);
	}

	// Clone object and deep copy the pnNames
	@Override
	public PetriNetObject clone()
	{
		final PetriNetObject pnObjectCopy = super.clone();
		pnObjectCopy.pnName = (NameLabel) this.pnName.clone();

		return pnObjectCopy;

	}

	@Override
	public void delete()
	{
		this.getParent().remove(this.pnName);
		super.delete();
	}

	public Point2D.Double getCentre()
	{
		return new Point2D.Double(this.positionX + this.getWidth() / 2.0, this.positionY + this.getHeight() /
																			2.0);
	}

	public Iterator getConnectFromIterator()
	{
		return this.connectFrom.iterator();
	}

	public Iterator getConnectToIterator()
	{
		return this.connectTo.iterator();
	}

	/**
	 * Get id
	 * 
	 * @return String value for Place id;
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
			return this.pnName.getText();
		}
	}

	/**
	 * Get name
	 * 
	 * @return String value for Place name;
	 */
	@Override
	public String getName()
	{
		/*
		if (this.pnName != null)
		{
			//if( this.pnName.getText().indexOf("TAG")>0  ) return this.id;
			return this.pnName.getText();
		}
		else*/
		{
			return this.id;
		}
	}

	/**
	 * Get X-axis position
	 * 
	 * @return Double value for X-axis position
	 */
	public double getPositionX()
	{
		return this.positionX;
	}

	/**
	 * Get X-axis position returns null if value not yet entered
	 * 
	 * @return Double value for X-axis position
	 */
	public Double getPositionXObject()
	{
		return new Double(this.locationX);
	}

	/**
	 * Get Y-axis position
	 * 
	 * @return Double value for Y-axis position
	 */
	public double getPositionY()
	{
		return this.positionY;
	}

	/**
	 * Get Y-axis position returns null if value not yet entered
	 * 
	 * @return Double value for Y-axis position
	 * 
	 */
	public Double getPositionYObject()
	{
		return new Double(this.locationY);
	}

	/** 
	 */
	@Override
	public void paintComponent(final Graphics g)
	{
		super.paintComponent(g);
	}

	public void removeFromArc(final Arc oldArc)
	{
		this.connectFrom.remove(oldArc);
	}

	public void removeToArc(final Arc oldArc)
	{
		this.connectTo.remove(oldArc);
	}

	/** Handles selection for Place/Transitions */
	@Override
	public void select()
	{
		if (this.selectable && !this.selected)
		{
			super.select();
			final Iterator arcsFrom = this.connectFrom.iterator();
			while (arcsFrom.hasNext())
			{
				((Arc) arcsFrom.next()).select();
			}

			final Iterator arcsTo = this.connectTo.iterator();
			while (arcsTo.hasNext())
			{
				((Arc) arcsTo.next()).select();
			}
		}
	}

	/** Sets the center of the component to position x, y */
	public void setCentre(final double x, final double y)
	{
		this.setPositionX(x - this.getWidth() / 2.0);
		this.setPositionY(y - this.getHeight() / 2.0);
		this.updateBounds();
		this.updateLabelLocation();
		this.updateConnected();
	}

	/**
	 * Set id
	 * 
	 * @param idInput
	 *            String value for Place id;
	 */
	@Override
	public void setId(final String idInput)
	{
		this.id = idInput;
		this.setName(this.id);
// System.out.println("setting id to: " + idInput);
	}

	/**
	 * Set name
	 * 
	 * @param nameInput
	 *            String value for Place name;
	 */
	@Override
	public void setName(final String nameInput)
	{
		// sets the text within the label
// System.out.println("setting name to: " + nameInput);
		this.pnName.setText(nameInput);
		this.pnName.updateSize();

	}

	/**
	 * Set X-axis position
	 * 
	 * @param positionXInput
	 *            Double value for X-axis position
	 */
	public void setPositionX(final double positionXInput)
	{
		this.positionX = positionXInput;
		if (this.getZoomController() != null)
		{
			this.locationX = this.getZoomController().getUnzoomedValue(this.positionX);
		}
		else
		{
			this.locationX = (int) this.positionX;
		}
	}

	/**
	 * Set Y-axis position
	 * 
	 * @param positionYInput
	 *            Double value for Y-axis position
	 */
	public void setPositionY(final double positionYInput)
	{
		this.positionY = positionYInput;
		if (this.getZoomController() != null)
		{
			this.locationY = this.getZoomController().getUnzoomedValue(this.positionY);
		}
		else
		{
			this.locationY = (int) this.positionY;
		}
	}

	/** Translates the component by x,y */
	public void translate(final int x, final int y)
	{
		this.setPositionX(this.positionX + x);
		this.setPositionY(this.positionY + y);
		this.updateBounds();
	}

	/** Calculates the BoundsOffsets used for setBounds() method */
	public void updateBounds()
	{
		
		int scaleFactor = 100;
		if (this.getZoomController() != null)
		{
			scaleFactor = this.getZoomController().getPercent();
		}
		this.positionX = this.locationX * scaleFactor / 100.0;
		this.positionY = this.locationY * scaleFactor / 100.0;
		this.bounds.setBounds(	(int) this.positionX,
								(int) this.positionY,
								(int) (this.componentWidth * scaleFactor / 100.0),
								(int) (this.componentHeight * scaleFactor / 100.0));
		this.bounds.grow(PetriNetObject.COMPONENT_DRAW_OFFSET, PetriNetObject.COMPONENT_DRAW_OFFSET);
		this.setBounds(this.bounds);

		/* updates Namelabel for zoomed object */
		this.pnName.setPosition((int) this.positionX, (int) this.positionY + this.getHeight() + 7);
	}

	/** Updates location of any attached arcs */
	public void updateConnected()
	{
		final Iterator arcsFrom = this.connectFrom.iterator();
		while (arcsFrom.hasNext())
		{
			PlaceTransitionObject.someArc = (Arc) arcsFrom.next();
			this.updateEndPoint(PlaceTransitionObject.someArc);
			PlaceTransitionObject.someArc.updateArcPosition();
		}

		final Iterator arcsTo = this.connectTo.iterator();
		while (arcsTo.hasNext())
		{
			PlaceTransitionObject.someArc = (Arc) arcsTo.next();
			this.updateEndPoint(PlaceTransitionObject.someArc);
			PlaceTransitionObject.someArc.updateArcPosition();
		}
	}

	abstract public void updateEndPoint(Arc arc);

	private void updateLabelLocation()
	{
		this.pnName.setPosition((int) this.positionX, (int) (this.positionY + 30));
	}

	public void zoomUpdate()
	{
		this.updateBounds();
		this.updateConnected();
	}
}