package pipe.gui;

import pipe.common.dataLayer.PetriNetObject;

public class ViewExpansionComponent extends PetriNetObject implements Zoomable{
	
	private int originalX = 0;
	private int originalY = 0;

	public ViewExpansionComponent() {
		super();
		setSize(1,1);
	}
	
	public ViewExpansionComponent(int x, int y){
		this();
		setOriginalX(x);
		setOriginalY(y);
		setLocation(x,y);
	}
	
	
	public void setOriginalX(int x) {
		this.originalX = x;
	}

	
	public void setOriginalY(int y) {
		this.originalY = y;
	}

	
	public void zoomUpdate() {
		double scaleFactor = getZoomController().getPercent() * 0.01;
		setLocation((int)(originalX * scaleFactor),
				(int)(originalY * scaleFactor));
	}

}
