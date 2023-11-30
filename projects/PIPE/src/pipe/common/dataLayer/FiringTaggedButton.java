package pipe.common.dataLayer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;

import javax.swing.JButton;
import javax.swing.JComponent;

import pipe.gui.Constants;
import pipe.gui.CreateGui;


public class FiringTaggedButton extends PetriNetObject {

	private Ellipse2D.Double firingTaggedButton;
	
	private double positionX, positionY, locationX, locationY;
	final double DIAMETER = Token.DIAMETER+5;
	//private GeneralPath firingTaggedButton;
	
	
	public FiringTaggedButton(final double x, final double y){


		if (CreateGui.getApp() != null)
		{
			this.addZoomController(CreateGui.getView().getZoomController());
		}
		this.setPositionX(x);
		this.setPositionY(y);

	
		
		//this.firingTaggedButton = new GeneralPath();
		//this.firingTaggedButton.append( new Ellipse2D.Double(x,y,DIAMETER, DIAMETER),false  );

		this.firingTaggedButton = new Ellipse2D.Double(x,y,DIAMETER, DIAMETER);
	
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
	
	public void paintComponent(final Graphics g)
	{
		super.paintComponent(g);

		final Graphics2D g2 = (Graphics2D) g;
	

		
			g2.setPaint(Constants.ENABLED_TRANSITION_COLOUR);
			g2.draw( new Ellipse2D.Double(positionX,positionY,DIAMETER, DIAMETER));
			g2.fill(new Ellipse2D.Double(positionX,positionY,DIAMETER, DIAMETER));

	}
	
	public void zoomUpdate()
	{
		this.updateBounds();
		//this.updateConnected();
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
								(int) (DIAMETER * scaleFactor / 100.0),
								(int) (DIAMETER * scaleFactor / 100.0));
		this.bounds.grow(PetriNetObject.COMPONENT_DRAW_OFFSET, PetriNetObject.COMPONENT_DRAW_OFFSET);
		this.setBounds(this.bounds);

		/* updates Namelabel for zoomed object 
		this.pnName.setPosition((int) this.positionX, (int) this.positionY + this.getHeight() + 7);*/
	}
}
