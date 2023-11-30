package pipe.gui;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;


/*
 * Created on Jan 27, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

/**
 * @author pete
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ArrowHead extends Object
	implements Shape, Cloneable {
	
	GeneralPath head = new GeneralPath();
	private Point2D.Double pointTo = new Point2D.Double();
	private double angle;
	
	public ArrowHead() {
		head.moveTo(0,0);
		head.lineTo(5,-10);
		head.lineTo(0,-7);
		head.lineTo(-5,-10);
		head.closePath();
	}
	
	public void setLocation(double xTo, double yTo, double _angle) {
		pointTo.setLocation(xTo, yTo);
		angle = _angle;
	}
	
	public void setLocation(Point2D.Double from, Point2D.Double to) {
		this.setLocation(from.getX(), from.getY(), to.getX(), to.getY());
	}

	public void setLocation(double xFrom, double yFrom, double xTo, double yTo) {
		pointTo.setLocation(xTo, yTo);
		if (yFrom <= yTo)
			angle = Math.atan((xFrom - xTo) / (yTo - yFrom));
		else
			angle = Math.atan((xFrom - xTo) / (yTo - yFrom))+Math.PI;
		
		// Needed to eliminate an exception on Windows
		if ((xFrom == xTo) && (yFrom == yTo))
			angle = 0;
	}
	
	public double getAngle() {
		return angle;
	}
	
	/* 
	 * Kind of kludgy, maybe I should define a new class for doing this? 
	 */
  private Rectangle addPoint(Rectangle r,float x,float y) {
    Point p=new Point((int)x,(int)y);
    if(r==null) return new Rectangle(p);
    else {
      r.add(p);
      return r;
    }
  }
  
	public Rectangle getBounds() {
    return head.getBounds();
	}

	/* (non-Javadoc)
	 * @see java.awt.Shape#getBounds2D()
	 */
	public Rectangle2D getBounds2D() {
    return head.getBounds2D();
	}

	/* (non-Javadoc)
	 * @see java.awt.Shape#contains(double, double)
	 */
	public boolean contains(double x, double y) {
	  return head.contains(x,y);     
	}

	/* (non-Javadoc)
	 * @see java.awt.Shape#contains(java.awt.geom.Point2D)
	 */
	public boolean contains(Point2D p) {
    return head.contains(p);
	}

	/* (non-Javadoc)
	 * @see java.awt.Shape#intersects(double, double, double, double)
	 */
	public boolean intersects(double x, double y, double w, double h) {
		return head.intersects(x,y,w,h);
	}

	/* (non-Javadoc)
	 * @see java.awt.Shape#intersects(java.awt.geom.Rectangle2D)
	 */
	public boolean intersects(Rectangle2D r) {
		return head.intersects(r);
	}

	/* (non-Javadoc)
	 * @see java.awt.Shape#contains(double, double, double, double)
	 */
	public boolean contains(double x, double y, double w, double h) {
		return head.contains(x,y,w,h);
	}

	/* (non-Javadoc)
	 * @see java.awt.Shape#contains(java.awt.geom.Rectangle2D)
	 */
	public boolean contains(Rectangle2D r) {
		return head.contains(r);
	}

	/* (non-Javadoc)
	 * @see java.awt.Shape#getPathIterator(java.awt.geom.AffineTransform)
	 */
	public PathIterator getPathIterator(AffineTransform at) {
		AffineTransform transform = new AffineTransform();
		transform.translate(pointTo.getX(),pointTo.getY());
		transform.rotate(angle);
		return head.createTransformedShape(transform).getPathIterator(at);
	}

	/* (non-Javadoc)
	 * @see java.awt.Shape#getPathIterator(java.awt.geom.AffineTransform, double)
	 */
	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		AffineTransform transform = new AffineTransform();
		transform.translate(pointTo.getX(),pointTo.getY());
		transform.rotate(angle);
		return head.createTransformedShape(transform).getPathIterator(at, flatness);
	}
}
