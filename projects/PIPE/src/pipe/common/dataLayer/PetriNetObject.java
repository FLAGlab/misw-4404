package pipe.common.dataLayer;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.EventListener;

import javax.swing.JComponent;

import pipe.gui.Constants;
import pipe.gui.CreateGui;
import pipe.gui.ZoomController;

/**
 * <b>PetriNetObject</b> - Petri-Net Object Class<b> - <i>Abstract</i></b>
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
 */

public abstract class PetriNetObject extends JComponent implements Constants, Cloneable
{

	protected final static int	COMPONENT_DRAW_OFFSET	= 5;
	// if
	// object
	// can
	// be
	// dragged.
	protected static boolean	ignoreSelection			= false;

	public static void ignoreSelection(final boolean ignore)
	{
		PetriNetObject.ignoreSelection = ignore;
	}
	/** Id */
	protected String		id						= null;
	// /** Color of PetriNetObject*/
// protected Color color = null;
	/** Name Label for displaying name */
	protected NameLabel		pnName;
	protected Color			objectColour			= Constants.ELEMENT_LINE_COLOUR;
	protected Color			selectionBorderColour	= Constants.SELECTION_LINE_COLOUR;
	protected boolean		selected				= false;							// True
	// if
	// part
	// of
	// the
	// current
	// selection.
	protected boolean		selectable				= true;							// True
	// if
	// object
	// can
	// be
	// selected.
	protected boolean		draggable				= true;							// True

	protected Rectangle		bounds					= new Rectangle();

	/**
	 * The ZoomController of the GuiView this component is part of.
	 */
	private ZoomController	zoomControl;

	/**
	 * Create default PetriNetObject
	 * 
	 */
	public PetriNetObject() {
	}

	/**
	 * Create PetriNetObject
	 * 
	 * @param idInput
	 *            Input Id
	 * @param colorInput
	 *            Input Color
	 */
	public PetriNetObject(final String idInput) {
		this.id = idInput;
	}

	public void addedToGui()
	{
	}

	public void addLabelToContainer()
	{
		if (this.getParent() != null)
		{
			this.getParent().add(this.pnName);
		}
	}

	public void addZoomController(final ZoomController zoomControl2)
	{
		this.zoomControl = zoomControl2;
	}

	@Override
	public PetriNetObject clone()
	{
		try
		{
			final PetriNetObject pnObjectCopy = (PetriNetObject) super.clone();

			// Remove all mouse listeners on the new object
			EventListener[] mouseListeners = pnObjectCopy.getListeners(MouseListener.class);

			for (final EventListener element : mouseListeners)
			{
				pnObjectCopy.removeMouseListener((MouseListener) element);
			}

			mouseListeners = pnObjectCopy.getListeners(MouseMotionListener.class);

			for (final EventListener element : mouseListeners)
			{
				pnObjectCopy.removeMouseMotionListener((MouseMotionListener) element);
			}

			return pnObjectCopy;
		}
		catch (final CloneNotSupportedException e)
		{
			throw new Error(e);
		}
	}

	public void delete()
	{
		CreateGui.getModel().removePetriNetObject(this);
		this.removeFromContainer();
		this.removeAll();
	}

	public void deselect()
	{
		if (this.selected)
		{
			this.selected = false;
			this.repaint();
		}
	}

	/**
	 * Get id returns null if value not yet entered
	 * 
	 * @return String value for id;
	 */
	public String getId()
	{
		return this.id;
	}

	/**
	 * Returns Name Label - is used by GuiView
	 * 
	 * @return PetriNetObject's Name Label (Model View Controller Design
	 *         Pattern)
	 */
	public NameLabel getNameLabel()
	{
		return this.pnName;
	}

	protected ZoomController getZoomController()
	{
		return this.zoomControl;
	}

	public boolean isDraggable()
	{
		return this.draggable;
	}

	public boolean isSelectable()
	{
		return this.selectable;
	}

	public boolean isSelected()
	{
		return this.selected;
	}

	@Override
	public void paintComponent(final Graphics g)
	{
		super.paintComponent(g);
	}

	public void removeFromContainer()
	{
		final Container c = this.getParent();
		if (c != null)
		{
			c.remove(this);
		}
	}

	public void select()
	{
		if (this.selectable && !this.selected)
		{
			this.selected = true;
			this.repaint();
		}
	}

	public void setDraggable(final boolean allow)
	{
		this.draggable = allow;
	}

	/**
	 * Set id
	 * 
	 * @param idInput
	 *            String value for id;
	 */
	public void setId(final String idInput)
	{
		this.id = idInput;
	}

	public void setObjectColour(final Color c)
	{
		this.objectColour = c;
	}

	public void setSelectable(final boolean allow)
	{
		this.selectable = allow;
	}

	public void setSelectionBorderColour(final Color c)
	{
		this.selectionBorderColour = c;
	}

}
