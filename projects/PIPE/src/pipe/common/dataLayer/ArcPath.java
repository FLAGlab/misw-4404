/**
 * Created on 12-Feb-2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 * 
 * @author Edwin Chung 16 Mar 2007: modified the constructor and several
 * other functions so that DataLayer objects can be created outside the
 * GUI
 */
package pipe.common.dataLayer;

import java.awt.BasicStroke;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLayeredPane;

import pipe.gui.ArcPathPointHandler;
import pipe.gui.Constants;
import pipe.gui.GuiView;

/**
 * @author Peter Kyme, Tom Barnwell and Michael Camacho
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class ArcPath implements Shape, Cloneable, Constants
{

	private static Stroke	proximityStroke		= new BasicStroke(Constants.ARC_PATH_PROXIMITY_WIDTH/*
																									 * ,
																									 * BasicStroke.CAP_BUTT,
																									 * BasicStroke.JOIN_MITER
																									 */);
	private static Stroke	stroke				= new BasicStroke(Constants.ARC_PATH_SELECTION_WIDTH);
	// private static final int CONTROL_POINT_CONSTANT = 2;
// private static final int PATH_SELECTION_RADIUS = 6;
	private GeneralPath		path				= new GeneralPath();
	private GeneralPath		arcSelection;
	private GeneralPath		pointSelection;
	private final List		pathPoints			= new ArrayList();
	private final Arc		myArc;
	ArcPathPoint			currentPoint;
	private boolean			pointLock			= false;
	private Shape			shape, proximityShape;
	private int				transitionAngle;
	private final boolean	showControlPoints	= false;

	public ArcPath(Arc a) {
		this.myArc = a;
		this.transitionAngle = 0;
	}

	public void addPoint()
	{
		this.pathPoints.add(new ArcPathPoint(this));
	}

	public void addPoint(double x, double y, boolean type)
	{
		this.pathPoints.add(new ArcPathPoint((float) x, (float) y, type, this));
	}

	public void addPoint(float x, float y, boolean type)
	{
		this.pathPoints.add(new ArcPathPoint(x, y, type, this));
	}

	public void addPointsToGui(GuiView editWindow)
	{
		ArcPathPoint pathPoint;
		((ArcPathPoint) this.pathPoints.get(0)).setDraggable(false);
		((ArcPathPoint) this.pathPoints.get(this.pathPoints.size() - 1)).setDraggable(false);

		ArcPathPointHandler pointHandler;
		for (int i = 0; i < this.pathPoints.size(); i++)
		{
			pathPoint = (ArcPathPoint) this.pathPoints.get(i);
			pathPoint.setVisible(false);

			// Check whether the point has already been added to the gui
			// as addPointsToGui() may have been called after the user
			// split an existing point. If this is the case, we don't want
			// to add all the points again along with new action listeners,
			// we just want to add the new point.
			// Nadeem 21/06/2005
			if (editWindow.getIndexOf(pathPoint) < 0)
			{
				editWindow.add(pathPoint);
				pointHandler = new ArcPathPointHandler(editWindow, pathPoint);
				pathPoint.addMouseListener(pointHandler);
				pathPoint.addMouseMotionListener(pointHandler);
				pathPoint.updatePointLocation();
			}
		}
	}

	public void addPointsToGui(JLayeredPane editWindow)
	{
		ArcPathPoint pathPoint;
		((ArcPathPoint) this.pathPoints.get(0)).setDraggable(false);
		((ArcPathPoint) this.pathPoints.get(this.pathPoints.size() - 1)).setDraggable(false);

		ArcPathPointHandler pointHandler;
		for (int i = 0; i < this.pathPoints.size(); i++)
		{
			pathPoint = (ArcPathPoint) this.pathPoints.get(i);
			pathPoint.setVisible(false);

			// Check whether the point has already been added to the gui
			// as addPointsToGui() may have been called after the user
			// split an existing point. If this is the case, we don't want
			// to add all the points again along with new action listeners,
			// we just want to add the new point.
			// Nadeem 21/06/2005
			if (editWindow.getIndexOf(pathPoint) < 0)
			{
				editWindow.add(pathPoint);
				pointHandler = new ArcPathPointHandler(editWindow, pathPoint);
				pathPoint.addMouseListener(pointHandler);
				pathPoint.addMouseMotionListener(pointHandler);
				pathPoint.updatePointLocation();
			}
		}
	}

	public Cubic[] calcNaturalCubic(int n, int[] x)
	{
		final float[] gamma = new float[n + 1];
		final float[] delta = new float[n + 1];
		final float[] D = new float[n + 1];
		int i;
		/*
		 * We solve the equation [2 1 ] [D[0]] [3(x[1] - x[0]) ] |1 4 1 | |D[1]|
		 * |3(x[2] - x[0]) | | 1 4 1 | | . | = | . | | ..... | | . | | . | | 1 4
		 * 1| | . | |3(x[n] - x[n-2])| [ 1 2] [D[n]] [3(x[n] - x[n-1])]
		 * 
		 * by using row operations to convert the matrix to upper triangular and
		 * then back sustitution. The D[i] are the derivatives at the knots.
		 */

		gamma[0] = 1.0f / 2.0f;
		for (i = 1; i < n; i++)
		{
			gamma[i] = 1 / (4 - gamma[i - 1]);
		}
		gamma[n] = 1 / (2 - gamma[n - 1]);

		delta[0] = 3 * (x[1] - x[0]) * gamma[0];
		for (i = 1; i < n; i++)
		{
			delta[i] = (3 * (x[i + 1] - x[i - 1]) - delta[i - 1]) * gamma[i];
		}
		delta[n] = (3 * (x[n] - x[n - 1]) - delta[n - 1]) * gamma[n];

		D[n] = delta[n];
		for (i = n - 1; i >= 0; i--)
		{
			D[i] = delta[i] - gamma[i] * D[i + 1];
		}

		/* now compute the coefficients of the cubics */
		final Cubic[] C = new Cubic[n];
		for (i = 0; i < n; i++)
		{
			C[i] = new Cubic(x[i], D[i], 3 * (x[i + 1] - x[i]) - 2 * D[i] - D[i + 1], 2 * (x[i] - x[i + 1]) +
																						D[i] + D[i + 1]);
		}
		return C;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Shape#contains(double, double)
	 */
	public boolean contains(double arg0, double arg1)
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Shape#contains(double, double, double, double)
	 */
	public boolean contains(double arg0, double arg1, double arg2, double arg3)
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Shape#contains(java.awt.geom.Point2D)
	 */
	public boolean contains(Point2D p)
	{
		return this.shape.contains(p);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Shape#contains(java.awt.geom.Rectangle2D)
	 */
	public boolean contains(Rectangle2D arg0)
	{
		return false;
	}

	public void createPath()
	{

		this.setControlPoints();

		this.currentPoint = null;
		this.path = new GeneralPath();
		this.currentPoint = (ArcPathPoint) this.pathPoints.get(0);
		this.path.moveTo(this.currentPoint.getPoint().x, this.currentPoint.getPoint().y);

		this.getArc().setWeightLabelPosition();

		this.currentPoint.setPointType(ArcPathPoint.STRAIGHT);

		for (int c = 1; c <= this.getEndIndex(); c++)
		{

			this.currentPoint = (ArcPathPoint) this.pathPoints.get(c);

			if (this.currentPoint.getPointType() == ArcPathPoint.STRAIGHT)
			{
				this.path.lineTo(this.currentPoint.getPoint().x, this.currentPoint.getPoint().y);
			}
			else if (this.currentPoint.getPointType() == ArcPathPoint.CURVED)
			{
				if (this.showControlPoints)
				{// draw control lines for illustrative purposes
					this.path.lineTo(this.currentPoint.getControl1().x, this.currentPoint.getControl1().y);
					this.path.lineTo(this.currentPoint.getControl2().x, this.currentPoint.getControl2().y);
					this.path.lineTo(this.currentPoint.getPoint().x, this.currentPoint.getPoint().y);
					this.path.moveTo(	((ArcPathPoint) this.pathPoints.get(c - 1)).getPoint().x,
										((ArcPathPoint) this.pathPoints.get(c - 1)).getPoint().y);
				}
				this.path.curveTo(	this.currentPoint.getControl1().x,
									this.currentPoint.getControl1().y,
									this.currentPoint.getControl2().x,
									this.currentPoint.getControl2().y,
									this.currentPoint.getPoint().x,
									this.currentPoint.getPoint().y);
			}

		}
		this.shape = ArcPath.stroke.createStrokedShape(this);
		this.proximityShape = ArcPath.proximityStroke.createStrokedShape(this);
	}

	public void delete()
	{ // Michael: Tells the arc points to remove themselves
		while (!this.pathPoints.isEmpty())
		{
			((ArcPathPoint) this.pathPoints.get(0)).kill(); // force delete of
															// ALL points
		}
	}

	public void deletePoint(ArcPathPoint a)
	{
		this.pathPoints.remove(a);
	}

	public void deselectPoint(int index)
	{
		((ArcPathPoint) this.pathPoints.get(index)).deselect();
	}

	public Arc getArc()
	{
		return this.myArc;
	}

	public String[][] getArcPathDetails()
	{

		final int length = this.getEndIndex() + 1;

		final String[][] details = new String[length][3];

		for (int c = 0; c < length; c++)
		{

			details[c][0] = String.valueOf(((ArcPathPoint) this.pathPoints.get(c)).getX());
			details[c][1] = String.valueOf(((ArcPathPoint) this.pathPoints.get(c)).getY());
			details[c][2] = String.valueOf(((ArcPathPoint) this.pathPoints.get(c)).getPointType());
		}
		return details;
	}

	public GeneralPath getArcSelection()
	{
		return this.arcSelection;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Shape#getBounds()
	 */
	public Rectangle getBounds()
	{
		return this.path.getBounds();
	}

// public void setPointLocation(int index, float x, float y) {
// ((ArcPathPoint)pathPoints.get(index)).setPointLocation(x,y);
// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Shape#getBounds2D()
	 */
	public Rectangle2D getBounds2D()
	{
		return null;
	}

	/* returns a control point for curve CD with incoming vector AB */
	private Point2D.Float getControlPoint(Point2D.Float A, Point2D.Float B, Point2D.Float C, Point2D.Float D)
	{
		Point2D.Float p = new Point2D.Float(0, 0);

		final double modAB = this.getMod(A, B);
		final double modCD = this.getMod(C, D);

		final double ABx = (B.x - A.x) / modAB;
		final double ABy = (B.y - A.y) / modAB;

		if (modAB < 7)
		{
			// if the points are virtually superimposed anyway
			p = (Point2D.Float) C.clone();
		}
		else
		{
			p.x = C.x + (float) (ABx * modCD / Constants.ARC_CONTROL_POINT_CONSTANT);
			p.y = C.y + (float) (ABy * modCD / Constants.ARC_CONTROL_POINT_CONSTANT);
		}
		return p;
	}

	/* modified to use control points, ensures a curve hits a place tangetially */
	public double getEndAngle()
	{
		if (this.getEndIndex() > 0)
		{
			if (this.getArc().getTarget() instanceof Transition)
			{
				return ((ArcPathPoint) this.pathPoints.get(this.getEndIndex())).getAngle(((ArcPathPoint) (this.pathPoints.get(this.getEndIndex()))).getControl2());
			}
			else
			{
				return ((ArcPathPoint) this.pathPoints.get(this.getEndIndex())).getAngle(((ArcPathPoint) (this.pathPoints.get(this.getEndIndex()))).getControl1());
			}
		}
		return 0;
	}

	public int getEndIndex()
	{
		return this.pathPoints.size() - 1;
	}

	private double getMod(Point2D.Float A, Point2D.Float B)
	{
		final double ABx = A.x - B.x;
		final double ABy = A.y - B.y;

		return Math.sqrt(ABx * ABx + ABy * ABy);
	}

	public int getNumPoints()
	{
		return this.pathPoints.size();
	}

	public GeneralPath getPath()
	{
		return this.path;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Shape#getPathIterator(java.awt.geom.AffineTransform)
	 */
	public PathIterator getPathIterator(AffineTransform arg0)
	{
		return this.path.getPathIterator(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Shape#getPathIterator(java.awt.geom.AffineTransform,
	 *      double)
	 */
	public PathIterator getPathIterator(AffineTransform arg0, double arg1)
	{
		return this.path.getPathIterator(arg0, arg1);
	}

	public ArcPathPoint getPathPoint(int index)
	{
		return ((ArcPathPoint) this.pathPoints.get(index));
	}

	public Point2D.Float getPoint(int index)
	{
		return ((ArcPathPoint) this.pathPoints.get(index)).getPoint();
	}

	public GeneralPath getPointSelection()
	{
		return this.pointSelection;
	}

	public double getStartAngle()
	{
		if (this.getEndIndex() > 0)
		{
			return ((ArcPathPoint) this.pathPoints.get(0)).getAngle(((ArcPathPoint) (this.pathPoints.get(1))).getControl2());
		}
		return 0;
	}

	public void hidePoints()
	{
		if (!this.pointLock)
		{
			for (int i = 0; i < this.pathPoints.size(); i++)
			{
				this.currentPoint = ((ArcPathPoint) this.pathPoints.get(i));
				if (!this.currentPoint.isSelected())
				{
					this.currentPoint.setVisible(false);
				}
			}
		}
	}

	/**
	 * insertPoint()
	 * 
	 * Inserts a new point into the Array List of path points at the specified
	 * index and shifts all the following points along
	 * 
	 * @param index
	 * @param newpoint
	 * 
	 * @author Nadeem
	 */
	public void insertPoint(int index, ArcPathPoint newpoint)
	{
		this.pathPoints.add(index, newpoint);

		if (this.myArc.getParent() instanceof GuiView)
		{
			this.addPointsToGui((GuiView) this.myArc.getParent());
		}
		else
		{
			this.addPointsToGui((JLayeredPane) this.myArc.getParent());
		}
	}

// public double getEndAngle() {
// if (getEndIndex()>0)
// return
// ((ArcPathPoint)pathPoints.get(getEndIndex())).getAngle((ArcPathPoint)(pathPoints.get(getEndIndex()-1)));
// return 0;
// }
//	
// public double getStartAngle() {
// if (getEndIndex()>0)
// return
// ((ArcPathPoint)pathPoints.get(0)).getAngle((ArcPathPoint)(pathPoints.get(1)));
// return 0;
// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Shape#intersects(double, double, double, double)
	 */
	public boolean intersects(double arg0, double arg1, double arg2, double arg3)
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Shape#intersects(java.awt.geom.Rectangle2D)
	 */
	public boolean intersects(Rectangle2D r)
	{
		return this.shape.intersects(r);
	}

	public boolean isPointSelected(int index)
	{
		return ((ArcPathPoint) this.pathPoints.get(index)).isSelected();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Shape#contains(java.awt.geom.Point2D)
	 */
	public boolean proximityContains(Point2D p)
	{
		return this.proximityShape.contains(p);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Shape#intersects(java.awt.geom.Rectangle2D)
	 */
	public boolean proximityIntersects(Rectangle2D r)
	{
		return this.proximityShape.intersects(r);
	}

	public void purgePathPoints() // Dangerous! Only called from DataLayer
									// when loading ArcPaths
	{
		this.pathPoints.clear();
	}

	public void selectPoint(int index)
	{
		((ArcPathPoint) this.pathPoints.get(index)).select();
	}

	private void setControlPoints()
	{
		this.setCurveControlPoints(); // must be in this order
		this.setStraightControlPoints();
		this.setEndControlPoints();
	}

	/* function sets control points for any curved sections of the path */
	private void setCurveControlPoints()
	{
		if (this.pathPoints.size() < 1)
		{
			return;
		}
		ArcPathPoint myCurrentPoint = (ArcPathPoint) this.pathPoints.get(0);
		myCurrentPoint.setPointType(ArcPathPoint.STRAIGHT);

// ArcPathPoint myPreviousButOnePoint = null;
// ArcPathPoint myNextPoint = null;
// ArcPathPoint myPreviousPoint = null;

		Cubic[] X, Y;

		final int endIndex = this.getEndIndex();

		for (int c = 1; c <= endIndex;)
		{
			int curveStartIndex = 0;
			int curveEndIndex = 0;
			myCurrentPoint = (ArcPathPoint) this.pathPoints.get(c);

			if (myCurrentPoint.getPointType() == true)
			{
				curveStartIndex = c - 1;

				for (; c <= endIndex && myCurrentPoint.getPointType() == true; c++)
				{
					myCurrentPoint = (ArcPathPoint) this.pathPoints.get(c);
					curveEndIndex = c;
				}
				/* calculate a cubic for each section of the curve */
				final int lengthOfCurve = curveEndIndex - curveStartIndex;
				int k1;
				final int x[] = new int[lengthOfCurve + 2];
				final int y[] = new int[lengthOfCurve + 2];
				X = new Cubic[lengthOfCurve + 2];
				Y = new Cubic[lengthOfCurve + 2];

				for (k1 = 0; k1 <= (curveEndIndex - curveStartIndex); k1++)
				{
					x[k1] = (int) ((ArcPathPoint) this.pathPoints.get(curveStartIndex + k1)).getPoint().x;
					y[k1] = (int) ((ArcPathPoint) this.pathPoints.get(curveStartIndex + k1)).getPoint().y;
				}
				x[k1] = x[k1 - 1];
				y[k1] = y[k1 - 1];

				X = this.calcNaturalCubic(k1, x);
				Y = this.calcNaturalCubic(k1, y);

				for (int k2 = 1; k2 <= lengthOfCurve; k2++)
				{
					myCurrentPoint = (ArcPathPoint) this.pathPoints.get(k2 + curveStartIndex);
					myCurrentPoint.setControl1(X[k2 - 1].getX1(), Y[k2 - 1].getX1());
					myCurrentPoint.setControl2(X[k2 - 1].getX2(), Y[k2 - 1].getX2());

				}

			}

			else
			{
				c++;
			}
		}
	}

	private void setEndControlPoints()
	{

// Transition endTransition;
		final PlaceTransitionObject source = this.getArc().getSource();
		final PlaceTransitionObject target = this.getArc().getTarget();
		final double anAngle = Math.toRadians(this.transitionAngle);

		if (!(this.getEndIndex() > 0))
		{
			return;
		}
		else if (source != null && source instanceof Transition &&
					((ArcPathPoint) this.pathPoints.get(1)).getPointType() == true)
		{

			ArcPathPoint myPoint = (ArcPathPoint) this.pathPoints.get(1);
			final ArcPathPoint myLastPoint = (ArcPathPoint) this.pathPoints.get(0);
			final float distance = (float) this.getMod(myPoint.getPoint(), myLastPoint.getPoint()) /
									Constants.ARC_CONTROL_POINT_CONSTANT;
			myPoint.setControl1((float) (myLastPoint.getPoint().x + Math.cos(anAngle) * distance),
								(float) (myLastPoint.getPoint().y + Math.sin(anAngle) * distance));

			myPoint = (ArcPathPoint) this.pathPoints.get(this.getEndIndex());
			myPoint.setControl2(this.getControlPoint(	myPoint.getPoint(),
														myPoint.getControl1(),
														myPoint.getPoint(),
														myPoint.getControl1()));
		}
		else if (target != null && source instanceof Place &&
					((ArcPathPoint) this.pathPoints.get(this.getEndIndex())).getPointType() == true)
		{

			ArcPathPoint myPoint = (ArcPathPoint) this.pathPoints.get(this.getEndIndex());
			final ArcPathPoint myLastPoint = (ArcPathPoint) this.pathPoints.get(this.getEndIndex() - 1);
			final float distance = (float) this.getMod(myPoint.getPoint(), myLastPoint.getPoint()) /
									Constants.ARC_CONTROL_POINT_CONSTANT;
			myPoint.setControl2((float) (myPoint.getPoint().x + Math.cos(anAngle) * distance),
								(float) (myPoint.getPoint().y + Math.sin(anAngle) * distance));

			myPoint = (ArcPathPoint) this.pathPoints.get(1);
			myPoint.setControl1(this.getControlPoint(	((ArcPathPoint) this.pathPoints.get(0)).getPoint(),
														myPoint.getControl2(),
														((ArcPathPoint) this.pathPoints.get(0)).getPoint(),
														myPoint.getControl2()));

		}
	}

	public void setFinalPointType(boolean type)
	{
		((ArcPathPoint) this.pathPoints.get(this.getEndIndex())).setPointType(type);
	}

	public void setPointLocation(int index, double x, double y)
	{
		if (index < this.pathPoints.size() && index >= 0)
		{
			((ArcPathPoint) this.pathPoints.get(index)).setPointLocation((float) x, (float) y);
		}
	}

	public void setPointLocation(int index, Point2D.Double point)
	{
		((ArcPathPoint) this.pathPoints.get(index)).setPointLocation((float) point.x, (float) point.y);
	}

	public void setPointType(int index, boolean type)
	{
		((ArcPathPoint) this.pathPoints.get(index)).setPointType(type);
	}

/*
 * (non-Javadoc)
 * 
 * @see java.awt.Shape#intersects(java.awt.geom.Rectangle2D)
 */
	public void setPointVisibilityLock(boolean lock)
	{
		this.pointLock = lock;
	}

	/*
	 * fuction sets the control points for any straight sections and for smooth
	 * intersection between straight and curved sections
	 */
	private void setStraightControlPoints()
	{
		ArcPathPoint myCurrentPoint = (ArcPathPoint) this.pathPoints.get(0);
		ArcPathPoint myPreviousButOnePoint = null;
		ArcPathPoint myNextPoint = null;
		ArcPathPoint myPreviousPoint = null;

		for (int c = 1; c <= this.getEndIndex(); c++)
		{
			myPreviousPoint = (ArcPathPoint) this.pathPoints.get(c - 1);
			myCurrentPoint = (ArcPathPoint) this.pathPoints.get(c);

			if (myCurrentPoint.getPointType() == false)
			{
				myCurrentPoint.setControl1(this.getControlPoint(myPreviousPoint.getPoint(),
																myCurrentPoint.getPoint(),
																myPreviousPoint.getPoint(),
																myCurrentPoint.getPoint()));
				myCurrentPoint.setControl2(this.getControlPoint(myCurrentPoint.getPoint(),
																myPreviousPoint.getPoint(),
																myCurrentPoint.getPoint(),
																myPreviousPoint.getPoint()));
			}

			else
			{
				if (c > 1 && myPreviousPoint.getPointType() == false)
				{

					myPreviousButOnePoint = (ArcPathPoint) this.pathPoints.get(c - 2);
					myCurrentPoint.setControl1(this.getControlPoint(myPreviousButOnePoint.getPoint(),
																	myPreviousPoint.getPoint(),
																	myPreviousPoint.getPoint(),
																	myCurrentPoint.getPoint()));
				}
				if (c < this.getEndIndex())
				{
					myNextPoint = (ArcPathPoint) this.pathPoints.get(c + 1);
					if (myNextPoint.getPointType() == false)
					{
						myCurrentPoint.setControl2(this.getControlPoint(myNextPoint.getPoint(),
																		myCurrentPoint.getPoint(),
																		myCurrentPoint.getPoint(),
																		myPreviousPoint.getPoint()));
					}
				}
			}
		}
	}

	public void setTransitionAngle(int angle)
	{
		this.transitionAngle = angle;
		this.transitionAngle %= 360;
	}

	public void showPoints()
	{
		if (!this.pointLock)
		{
			for (int i = 0; i < this.pathPoints.size(); i++)
			{
				((ArcPathPoint) this.pathPoints.get(i)).setVisible(true);
			}
		}
	}

	/**
	 * splitSegment() Goes through neighbouring pairs of ArcPathPoints
	 * determining the midpoint between them. Then calculates the distance from
	 * midpoint to the point passed as an argument. The pair of ArcPathPoints
	 * resulting in the shortest distance then have an extra point added between
	 * them at the midpoint effectively splitting that segment into two.
	 * 
	 * @param
	 */
	public void splitSegment(Point2D.Float mouseposition)
	{
		// An array to store all the distances from the midpoints
		final double[] distances = new double[this.pathPoints.size() - 1];

		// Calculate the midpoints and distances to them
		for (int index = 0; index < (this.pathPoints.size() - 1); index++)
		{
			final ArcPathPoint first = (ArcPathPoint) this.pathPoints.get(index);
			final ArcPathPoint second = (ArcPathPoint) this.pathPoints.get(index + 1);
			final Point2D.Float midpoint = first.getMidPoint(second);
			distances[index] = midpoint.distance(mouseposition);
		}

		// Now determine the shortest midpoint
		double shortest = distances[0];
		int wantedpoint = 0;
		for (int index = 0; index < this.pathPoints.size() - 1; index++)
		{
			if (distances[index] < shortest)
			{
				shortest = distances[index];
				wantedpoint = index;
			}
		}

		// wantedpoint is now the index of the first point
		// in the pair of arc points marking the segment to
		// be split. So we have all we need to split the arc.
		final ArcPathPoint first = (ArcPathPoint) this.pathPoints.get(wantedpoint);
		final ArcPathPoint second = (ArcPathPoint) this.pathPoints.get(wantedpoint + 1);
		final ArcPathPoint newpoint = new ArcPathPoint(second.getMidPoint(first), first.getPointType(), this);
		this.insertPoint(wantedpoint + 1, newpoint);
		this.createPath();
		this.myArc.updateArcPosition();
	}

	public void togglePointType(int index)
	{
		((ArcPathPoint) this.pathPoints.get(index)).togglePointType();
	}

	public void translatePoint(int index, float x, float y)
	{
		final ArcPathPoint point = (ArcPathPoint) this.pathPoints.get(index);
		point.setPointLocation(point.getPoint().x + x, point.getPoint().y + y);
	}

	public void updateArc()
	{
		this.myArc.updateArcPosition();
	}
}

class Cubic
{

	float	a, b, c, d; /* a + b*u + c*u^2 +d*u^3 */

	public Cubic(float a, float b, float c, float d) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}

	/** evaluate cubic */
	public float eval(float u)
	{
		return (((this.d * u) + this.c) * u + this.b) * u + this.a;
	}

	// Return first control point coordinate (calculated from coefficients)
	public float getX1()
	{
		return ((this.b + 3 * this.a) / 3);
	}

	// Return second control point coordinate (calculated from coefficients)
	public float getX2()
	{
		return ((this.c + 2 * this.b + 3 * this.a) / 3);
	}

}
