/**
 * Created on 28-Feb-2004 Author is Michael Camacho (and whoever wrote the first
 * bit!)
 * 
 * @author Edwin Chung 16 Mar 2007: modified the constructor and several other
 *         functions so that DataLayer objects can be created outside the GUI
 */
// ######################################################################################
package pipe.common.dataLayer;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;

import pipe.gui.Constants;
import pipe.gui.CreateGui;
import pipe.gui.Zoomable;

public class ArcPathPoint extends PetriNetObject implements Constants, Zoomable
{
	/**
	 * 
	 */
	private static final long		serialVersionUID	= -2059806992056260969L;
	public static final boolean		STRAIGHT			= false;
	public static final boolean		CURVED				= true;
	public static final int			SIZE				= 4;
	public static final int			SIZE_OFFSET			= 4;
	// offset
	// in x
	// for
	// the
	// new
	// point
	// resulting
	// from
	// splitting
	// a
	// point
	private static RectangularShape	shape;
	public final int				DELTA				= 10;						// The
	private final ArcPath			myArcPath;
	private final Point2D.Float		point				= new Point2D.Float();
	private final Point2D.Float		realPoint			= new Point2D.Float();

	private final Point2D.Float		control1			= new Point2D.Float();
	private final Point2D.Float		control2			= new Point2D.Float();

	private boolean					pointType;

	ArcPathPoint(final ArcPath a) {

		this.myArcPath = a;
		if (CreateGui.getApp() != null)
		{
			this.addZoomController(CreateGui.getView().getZoomController());
		}
		this.setPointLocation(0, 0);

	}

	ArcPathPoint(final float x, final float y, final boolean _pointType, final ArcPath a) {
		this(a);
		this.setPointLocation(x, y);
		this.pointType = _pointType;
	}

	/**
	 * @author Nadeem
	 */
	ArcPathPoint(final Point2D.Float point, final boolean _pointType, final ArcPath a) {
		this(point.x, point.y, _pointType, a);
	}

	@Override
	public void delete() // Won't delete if only two points left. General
	// delete.
	{
		if (this.isDeleteable())
		{
			this.kill();
			this.myArcPath.updateArc();
		}
	}

	public double getAngle(final ArcPathPoint p2)
	{
		double angle;
		if (this.point.y <= p2.point.y)
		{
			angle = Math.atan((this.point.x - p2.point.x) / (p2.point.y - this.point.y));
		}
		else
		{
			angle = Math.atan((this.point.x - p2.point.x) / (p2.point.y - this.point.y)) + Math.PI;
		}

		// Needed to eliminate an exception on Windows
		if (this.point.equals(p2.point))
		{
			angle = 0;
		}

		return angle;
	}

	public double getAngle(final Point2D.Float p2)
	{
		double angle;
		if (this.point.y <= p2.y)
		{
			angle = Math.atan((this.point.x - p2.x) / (p2.y - this.point.y));
		}
		else
		{
			angle = Math.atan((this.point.x - p2.x) / (p2.y - this.point.y)) + Math.PI;
		}

		// Needed to eliminate an exception on Windows
		if (this.point.equals(p2))
		{
			angle = 0;
		}

		return angle;
	}

	public ArcPath getArcPath()
	{
		return this.myArcPath;
	}

	public Point2D.Float getControl1()
	{
		return this.control1;
	}

	public Point2D.Float getControl2()
	{
		return this.control2;
	}

	public int getIndex()
	{
		for (int i = 0; i < this.myArcPath.getNumPoints(); i++)
		{
			if (this.myArcPath.getPathPoint(i) == this)
			{
				return i;
			}
		}
		return -1;
	}

	public Point2D.Float getMidPoint(final ArcPathPoint target)
	{
		return new Point2D.Float((target.point.x + this.point.x) / 2, (target.point.y + this.point.y) / 2);
	}

	public Point2D.Float getPoint()
	{
		return this.point;
	}

	public boolean getPointType()
	{
		return this.pointType;
	}

	public boolean isDeleteable()
	{
		final int i = this.getIndex();
		return i > 0 && i != this.myArcPath.getNumPoints() - 1;
	}

	public void kill() // delete without the safety check :)
	{ // called internally by ArcPoint and parent ArcPath
		super.removeFromContainer();
		this.myArcPath.deletePoint(this);
	}

	@Override
	public void paintComponent(final Graphics g)
	{
		if (!PetriNetObject.ignoreSelection)
		{
			super.paintComponent(g);
			final Graphics2D g2 = (Graphics2D) g;
// g2.setStroke(new BasicStroke(1.0f));
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
// g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
// RenderingHints.VALUE_STROKE_NORMALIZE);

// RectangularShape shape;
			if (this.pointType == ArcPathPoint.CURVED)
			{
				ArcPathPoint.shape = new Ellipse2D.Double(0, 0, 2 * ArcPathPoint.SIZE, 2 * ArcPathPoint.SIZE);
			}
			else
			{
				ArcPathPoint.shape = new Rectangle2D.Double(0,
															0,
															2 * ArcPathPoint.SIZE,
															2 * ArcPathPoint.SIZE);
			}

			if (this.selected)
			{
				g2.setPaint(Constants.SELECTION_FILL_COLOUR);
				g2.fill(ArcPathPoint.shape);
				g2.setPaint(Constants.SELECTION_LINE_COLOUR);
				g2.draw(ArcPathPoint.shape);
			}
			else
			{
				g2.setPaint(Constants.ELEMENT_FILL_COLOUR);
				g2.fill(ArcPathPoint.shape);
				g2.setPaint(Constants.ELEMENT_LINE_COLOUR);
				g2.draw(ArcPathPoint.shape);
			}
		}
	}

	public void setControl1(final float _x, final float _y)
	{
		this.control1.x = _x;
		this.control1.y = _y;
	}

	public void setControl1(final Point2D.Float p)
	{
		this.control1.x = p.x;
		this.control1.y = p.y;
	}

	public void setControl2(final float _x, final float _y)
	{
		this.control2.x = _x;
		this.control2.y = _y;
	}

	public void setControl2(final Point2D.Float p)
	{
		this.control2.x = p.x;
		this.control2.y = p.y;
	}

	public void setPointLocation(final float x, final float y)
	{
// System.err.println("Setting ArcPathPoint to position "+x+", "+y);
		if (CreateGui.getApp() != null)
		{
			final double realX = this.getZoomController().getUnzoomedValue(x);
			final double realY = this.getZoomController().getUnzoomedValue(y);

			this.realPoint.setLocation(realX, realY);
		}
		this.point.setLocation(x, y);

		this.setBounds(	(int) x - ArcPathPoint.SIZE,
						(int) y - ArcPathPoint.SIZE,
						2 * ArcPathPoint.SIZE + ArcPathPoint.SIZE_OFFSET,
						2 * ArcPathPoint.SIZE + ArcPathPoint.SIZE_OFFSET);
	}

	public void setPointType(final boolean type)
	{
		if (this.pointType != type)
		{
			this.pointType = type;
			this.myArcPath.createPath();
			this.myArcPath.getArc().updateArcPosition();
		}
	}

	public void setVisibilityLock(final boolean lock)
	{
		this.myArcPath.setPointVisibilityLock(lock);
	}

	/**
	 * splitPoint() This method is called when the user selects the popup menu
	 * option Split Point on an Arc Point. The method determines the index of
	 * the selected point in the listarray of ArcPathPoints that an arcpath has.
	 * Then then a new point is created BEFORE this one in the list and offset
	 * by a small delta in the x direction.
	 * 
	 */
	public void splitPoint()
	{
		final int i = this.getIndex(); // Get the index of this point
		final ArcPathPoint newpoint = new ArcPathPoint(	this.point.x + this.DELTA,
														this.point.y,
														this.pointType,
														this.myArcPath);
		this.myArcPath.insertPoint(i + 1, newpoint);
		this.myArcPath.createPath();
		this.myArcPath.getArc().updateArcPosition();
	}

	public void togglePointType()
	{
		this.pointType = !this.pointType;
		this.myArcPath.createPath();
		this.myArcPath.getArc().updateArcPosition();
	}

	public void updatePointLocation()
	{
		this.setPointLocation(this.point.x, this.point.y);
	}

	public void zoomUpdate()
	{
		if (this.getZoomController() != null)
		{
			final float x = (float) this.getZoomController().getZoomPositionForXLocation(this.realPoint.x);
			final float y = (float) this.getZoomController().getZoomPositionForYLocation(this.realPoint.y);
			this.point.setLocation(x, y);
			this.setBounds(	(int) x - ArcPathPoint.SIZE,
							(int) y - ArcPathPoint.SIZE,
							2 * ArcPathPoint.SIZE + ArcPathPoint.SIZE_OFFSET,
							2 * ArcPathPoint.SIZE + ArcPathPoint.SIZE_OFFSET);

		}

	}

}