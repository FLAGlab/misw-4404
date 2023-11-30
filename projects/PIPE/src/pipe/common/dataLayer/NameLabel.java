package pipe.common.dataLayer;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 * <b>NameLabel</b> - This class is for the labels of PN Objects
 * 
 * @see
 * </p>
 * <p>
 * <a href="uml\NameLabel.png">NameLabel UML</a>
 * </p>
 * @version 1.0
 * @author Camilla Clifford
 */
public class NameLabel extends JLabel implements Cloneable
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -828995562411250258L;
	public int					positionX;
	public int					positionY;
	private double				xCoord;
	private double				yCoord;
	public int					arcboundsLeft;
	public int					arcboundsTop;
	private String				id					= null;

	public NameLabel() {
		super();
	}

	public NameLabel(	final double positionXInput,
						final double positionYInput,
						final String text,
						final String idInput) {
		super(text);
		this.id = idInput;
		this.xCoord = positionXInput;
		this.yCoord = positionYInput;
	}

	public NameLabel(final String text) {
		super(text);
		this.setHorizontalAlignment(SwingConstants.LEFT);
		this.setVerticalAlignment(SwingConstants.TOP);
	}

// These functions aren't needed
// public void arcboundsSet(int boundsLoffset, int boundsToffset, int arcWidth,
// int arcHeight){
// arcboundsLeft = boundsLoffset;
// arcboundsTop = boundsToffset;
// setBounds(arcboundsLeft, arcboundsTop, arcWidth, arcHeight);
// }

// public void paintComponent(Graphics g){
// super.paintComponent(g);
// }

	@Override
	public Object clone()
	{
		try
		{
			return super.clone();
		}
		catch (final CloneNotSupportedException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public String getID()
	{
		return this.id;
	}

	public double getXPosition()
	{
		return this.xCoord;
	}

	public double getYPosition()
	{
		return this.yCoord;
	}

	public void setID(final String idInput)
	{
		this.id = idInput;
	}

	public void setPosition(final int x, final int y)
	{
		this.positionX = x;
		this.positionY = y;
		this.updatePosition();
	}

	public void translate(final int x, final int y)
	{
		this.setPosition(this.positionX + x, this.positionY + y);
	}

	public void updatePosition()
	{
		this.setLocation(this.positionX - this.getPreferredSize().width, this.positionY -
																			this.getPreferredSize().height);
	}

	public void updateSize()
	{
		// To get round Java bug #4352983 I have to expand the size a bit
		this.setSize(	(int) (this.getPreferredSize().width * 1.2),
						(int) (this.getPreferredSize().height * 1.2));
		this.updatePosition();
	}
}
