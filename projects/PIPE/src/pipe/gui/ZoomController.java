/**
 * 
 */
package pipe.gui;

import java.awt.geom.AffineTransform;

/**
 * @author Tim Kimber
 *
 */
public class ZoomController {
	
	private int percent;
	private AffineTransform transform = new AffineTransform();
	private GuiView myNet;
	
	public ZoomController(GuiView view){
		this(100, view);
	}

	public ZoomController(int pct, GuiView view) {
		percent = pct;
		myNet = view;
	}

	public void zoomOut() {

		percent -= 10;
	
		if(percent<40){
			percent += 10;
			return;
		}
		transform.setToScale(percent * 0.01,percent * 0.01);
	}
	
	public void zoomIn(){
		percent += 10;
		transform.setToScale(percent * 0.01,percent * 0.01);		
	}

	public AffineTransform getTransform() {
		return transform;
	}

	public int getPercent() {
		return percent;
	}
	
	public void setPercent(int newPercent) {
		if(newPercent>=40 && newPercent<=2000)
			percent=newPercent;
	}

	/**
	 * Calculates the value of the screen distance val at 100% zoom
	 * 
	 * @param val
	 * @return
	 */
	public int getUnzoomedValue(double val) {
		return (int)(val / (percent * 0.01));
	}

	/**
	 * Calculates where the correct screen x position at the current zoom is
	 * for an object with "real" x value locationX.
	 * 
	 * @param locationX
	 * @return
	 */
	public double getZoomPositionForXLocation(double locationX) {
		return locationX * percent * 0.01;
	}

	/**
	 * Calculates where the correct screen y position at the current zoom is
	 * for an object with "real" y value locationY.
	 * 
	 * @param locationY
	 * @return
	 */
	public double getZoomPositionForYLocation(double locationY) {
		return locationY * percent * 0.01;
	}

	public void setZoom(int newPercent) {
		setPercent(newPercent);
		transform.setToScale(percent * 0.01,percent * 0.01);
	}
	
}
