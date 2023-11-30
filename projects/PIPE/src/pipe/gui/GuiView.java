package pipe.gui;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLayeredPane;
import javax.swing.JViewport;
import javax.swing.event.MouseInputAdapter;

import pipe.common.dataLayer.AnnotationNote;
import pipe.common.dataLayer.Arc;
import pipe.common.dataLayer.ArcPathPoint;
import pipe.common.dataLayer.PetriNetObject;
import pipe.common.dataLayer.Place;
import pipe.common.dataLayer.PlaceTransitionObject;
import pipe.common.dataLayer.Transition;

/**
 * The petrinet is drawn onto this frame.
 */
public class GuiView extends JLayeredPane implements Observer, Constants, Printable {
	public boolean netChanged = false;
	public boolean animationmode = false;
	public Arc createArc;	//no longer static

	private AnimationHandler animationHandler = new AnimationHandler();
	boolean shiftDown = false;
	private SelectionObject selection;
	private ZoomController zoomControl;

	public GuiView() {
		setLayout(null);
		setOpaque(true);
		setDoubleBuffered(true);
		setAutoscrolls(true);
		setBackground(ELEMENT_FILL_COLOUR);
		zoomControl = new ZoomController(100, this);

		MouseHandler handler = new MouseHandler();

		setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));

		addMouseListener(handler);
		addMouseMotionListener(handler);

		selection = new SelectionObject(this);
	}

	public void addNewPetriNetObject(PetriNetObject newObject) {

		if (newObject != null) {

			int l =  newObject.getMouseListeners().length;

			if (l==0) {
				
				if (newObject instanceof Place) {
					PlaceHandler placeHandler = new PlaceHandler(this, (Place)newObject);
					newObject.addMouseListener(placeHandler);
					newObject.addMouseMotionListener(placeHandler);
					add(newObject);	
				}
				else if (newObject instanceof Transition) {
					
//					AnimationHandler animationHandler = new AnimationHandler();
					TransitionHandler transitionHandler = new TransitionHandler(this, (Transition)newObject);
					newObject.addMouseListener(transitionHandler);
					newObject.addMouseMotionListener(transitionHandler);
					newObject.addMouseListener(animationHandler);
					//newObject.addMouseListener( new FireTaggedTokenAction((Transition)newObject)   );
					
					add(newObject);	
				}
				else if (newObject instanceof Arc) {
					add(newObject);
					ArcHandler arcHandler = new ArcHandler(this, (Arc)newObject);
					newObject.addMouseListener(arcHandler);
					newObject.addMouseMotionListener(arcHandler);
				}
				else if (newObject instanceof AnnotationNote) {
					add(newObject);
					AnnotationNoteHandler noteHandler =
						new AnnotationNoteHandler(this, (AnnotationNote)newObject);
					newObject.addMouseListener(noteHandler);
					newObject.addMouseMotionListener(noteHandler);					
					((AnnotationNote)newObject).getNote().addMouseListener(noteHandler);
					((AnnotationNote)newObject).getNote().addMouseMotionListener(noteHandler);
				}
			}
		}
		validate();
		repaint();
	}

	public void update(Observable o, Object diffObj) {
		

		if((diffObj instanceof PetriNetObject) && (diffObj != null))
		{
			if (CreateGui.appGui.getMode() == CREATING)
			{
				addNewPetriNetObject((PetriNetObject)diffObj);
				CreateGui.getModel().setValidate(false);
			}
			repaint();
		} 
	}

	public int print(Graphics g, PageFormat pageFormat, int pageIndex) throws PrinterException {
		if(pageIndex>0)
			return Printable.NO_SUCH_PAGE;
		Graphics2D g2D = (Graphics2D) g;
		//Move origin to page printing area corner
		g2D.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
		g2D.scale(0.5,0.5);
		print(g2D); // Draw the net

		return Printable.PAGE_EXISTS;
	}

	/**
	 * This method is called whenever the frame is moved, resized etc.
	 * It iterates over the existing petrinet objects and repaints them.
	 * TODO: write a better description than this since it is now totally not happening.
	 */
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		super.paintComponent(g2);

		if (Grid.enabled()) {
			Grid.updateSize(this);
			Grid.drawGrid(g);
		}
		selection.updateBounds();
	}

	public void updatePreferredSize() {
		// iterate over net objects
		// setPreferredSize() accordingly
		Component[] components=getComponents();
		Dimension d=new Dimension(0,0);
		for(int i=0;i<components.length;i++) {
			if(components[i] instanceof SelectionObject) continue; // SelectionObject not included
			Rectangle r=components[i].getBounds();
			int x=r.x+r.width+100;
			int y=r.y+r.height+100;
			if(x>d.width)  d.width =x;
			if(y>d.height) d.height=y;
		}
		setPreferredSize(d);

		getParent().validate();
	}
	
	public void zoom() {
		Component[] children = getComponents();
		
		for (int i = 0; i < children.length; i++){
			if (children[i] instanceof Zoomable)
				((Zoomable)children[i]).zoomUpdate();
		}
		
		validate();
	}

	public void changeAnimationMode(boolean status) {
		animationmode = status;
	}

	public SelectionObject getSelectionObject() {
		return selection;
	}

	public PetriNetObject add(PetriNetObject c)
	{
		super.add(c);
		c.addedToGui();

		if (c instanceof ArcPathPoint)
			setLayer(c, DEFAULT_LAYER.intValue() + ARC_POINT_LAYER_OFFSET);
		else if (c instanceof Arc)
			setLayer(c, DEFAULT_LAYER.intValue() + ARC_LAYER_OFFSET);	
		else if (c instanceof PlaceTransitionObject)
			setLayer(c, DEFAULT_LAYER.intValue() + PLACE_TRANSITION_LAYER_OFFSET);
		else if (c instanceof AnnotationNote)
			setLayer(c, DEFAULT_LAYER.intValue() + ANNOTATION_LAYER_OFFSET);			
		return c;
	}  
	
	public void setCursorType(String type) {
		if (type.equals("arrow")) 
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		else if (type.equals("crosshair")) 	
			setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		else if (type.equals("move")) 
			setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
	}

	public void setShiftDown(boolean down){
		shiftDown = down;
		if (createArc != null) {
			createArc.getArcPath().setFinalPointType(shiftDown);
			createArc.getArcPath().createPath();
		}
	}

	class MouseHandler extends MouseInputAdapter{
		
		private Point dragStart;

		public void mousePressed(MouseEvent e){
			
			PetriNetObject pnObject;
			
			if (e.getButton() == MouseEvent.BUTTON1) {

				Point start = e.getPoint();
				switch(CreateGui.getApp().getMode()){
				case PLACE:
					pnObject = new Place(Grid.getModifiedX(start.x),Grid.getModifiedY(start.y));
					CreateGui.getModel().addPetriNetObject(pnObject);
					addNewPetriNetObject(pnObject);
					break;

				case IMMTRANS:
					pnObject = new Transition(Grid.getModifiedX(start.x),Grid.getModifiedY(start.y));
					CreateGui.getModel().addPetriNetObject(pnObject);
					addNewPetriNetObject(pnObject);
					break;

				case TIMEDTRANS:
					pnObject = new Transition(Grid.getModifiedX(start.x),Grid.getModifiedY(start.y));
					((Transition)pnObject).setTimed(true);
					CreateGui.getModel().addPetriNetObject(pnObject);
					addNewPetriNetObject(pnObject);
					break;

				case ARC:
					// Add point to arc in creation
					if (createArc != null) {
						createArc.setEndPoint(Grid.getModifiedX(e.getX()), Grid.getModifiedY(e.getY()), shiftDown);
						createArc.getArcPath().addPoint(Grid.getModifiedX(e.getX()), Grid.getModifiedY(e.getY()), shiftDown);
					}
					break;

				case ANNOTATION:
					pnObject = new AnnotationNote("", "", Grid.getModifiedX(e.getX()), Grid.getModifiedY(e.getY()));
					CreateGui.getModel().addPetriNetObject(pnObject);
					addNewPetriNetObject(pnObject);
					break;
					
				case DRAG:
					dragStart = new Point(start);
				}

				//updatePreferredSize();
			}
		}

		public void mouseMoved(MouseEvent e) {
			if (createArc != null) {
				createArc.setEndPoint(Grid.getModifiedX(e.getX()), Grid.getModifiedY(e.getY()), shiftDown);
			}
		}

		/**
		 * 
		 * @see javax.swing.event.MouseInputAdapter#mouseDragged(java.awt.event.MouseEvent)
		 */
		public void mouseDragged(MouseEvent e) {
			if (CreateGui.getApp().getMode() == DRAG){
				JViewport viewer = (JViewport)getParent();
				Point offScreen = viewer.getViewPosition();
				if (dragStart.x > e.getX()){
					offScreen.translate(viewer.getWidth(), 0);
				}
				if (dragStart.y > e.getY()){
					offScreen.translate(0, viewer.getHeight());
				}
				offScreen.translate(dragStart.x - e.getX(), dragStart.y - e.getY());
				Rectangle r = new Rectangle(offScreen.x, offScreen.y, 1, 1);
				scrollRectToVisible(r);
				super.mouseDragged(e);
			}
		}
	}

	public ZoomController getZoomController() {
		return zoomControl;
	}

	/* (non-Javadoc)
	 * @see java.awt.Container#validate()
	 */
	public void validate() {
		Component[] children = getComponents();
		for (int i = 0; i < children.length; i++)
			if (children[i] instanceof Arc){
				((Arc)children[i]).getArcPath().createPath();
			}
		super.validate();
	}

}
