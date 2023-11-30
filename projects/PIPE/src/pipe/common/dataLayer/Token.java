package pipe.common.dataLayer;

//import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.ImageObserver;

/**
 * <b>Token</b> - Petri-Net Token Class
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

public class Token extends PlaceTransitionObject
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 8984507410134238798L;
	// public static final int WIDTH = 5;
// public static final int HEIGHT = 5;
	public static final int		DIAMETER			= 5;
	/** Ellipse2D.Double token */
	protected Ellipse2D.Double	token;

	
	/**
	 * Create empty Petri-Net Token object
	 * 
	 */
	public Token() {
	}

	/**
	 * Create Petri-Net Token object
	 * 
	 * @param positionXInput
	 *            X-axis Position
	 * @param positionYInput
	 *            Y-axis Position
	 * @param color -
	 *            modified by aed02
	 */
	public Token(final int positionXInput, final int positionYInput) {
		super(positionXInput, positionYInput);
		this.componentWidth = ImageObserver.WIDTH;
		this.componentHeight = ImageObserver.HEIGHT;
		this.token = new Ellipse2D.Double(positionXInput, positionYInput, Token.DIAMETER, Token.DIAMETER);
	}

	protected void decCount()
	{
	}

	/**
	 * Get boundaries of java.awt.Rectangle
	 * 
	 * @return Boundaries of Token
	 */
	@Override
	public Rectangle getBounds()
	{
		return this.token.getBounds();
	}

	protected int getCount()
	{
		return 0;
	}

	/**
	 * Get shape of object for drawing on screen
	 * 
	 * @return token
	 */
	public Shape getShape()
	{
		return this.token;
	}

	/** returns a unique id for an arc of the form to_n */
	public String getUniqueId()
	{
		return null;

	}

	/** Methods used to manipulate the transition count */
	protected void incCount()
	{
	}

	/**
	 * Modifies start and end X and Y coords - does nothing.
	 * 
	 * @param startX
	 *            Start X-axis Position
	 * @param startY
	 *            Start Y-axis Position
	 * @param endX
	 *            End X-axis Position
	 * @param endY
	 *            End Y-axis Position
	 */
	public void modify(final double startX, final double startY, final double endX, final double endY)
	{
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pipe.dataLayer.PlaceTransitionObject#updateEndPoint(pipe.dataLayer.Arc)
	 */
	@Override
	public void updateEndPoint(final Arc arc)
	{

	}

	public void updateSize(final MouseEvent e)
	{
	}
	
	
	
}
