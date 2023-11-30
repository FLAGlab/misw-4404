//######################################################################################
/*
 * Created on 04-Mar-2004
 * Author is Michael Camacho
 *
 */
//######################################################################################
package pipe.common.dataLayer;

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.geom.RectangularShape;

import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.event.MouseInputAdapter;
import javax.swing.text.DefaultHighlighter;

import pipe.gui.Constants;
import pipe.gui.CreateGui;
import pipe.gui.Grid;
import pipe.gui.Zoomable;

/**
 * 
 * @author Edwin Chung modified the constructor so that DataLayer objects can be
 *         constructed outside the GUI (Mar 2007)
 * 
 */

public class AnnotationNote extends PetriNetObject implements Zoomable
{

	public class AnnotationKeyUpdateHandler implements KeyListener
	{

		private final AnnotationNote	myAnnotation;

		public AnnotationKeyUpdateHandler(final AnnotationNote annotation) {
			this.myAnnotation = annotation;
		}

		public void keyPressed(final KeyEvent e)
		{
			this.myAnnotation.repaint();
		}

		public void keyReleased(final KeyEvent e)
		{
			// empty
		}

		public void keyTyped(final KeyEvent e)
		{
			// empty
		}
	}
	public class ResizePoint extends JComponent
	{
		/**
		 * 
		 */
		private static final long	serialVersionUID	= 4908000789057562422L;
		public static final int		SIZE				= 4;
		public static final int		TOP					= 1;
		public static final int		BOTTOM				= 2;
		public static final int		LEFT				= 4;
		public static final int		RIGHT				= 8;

		private final Rectangle		shape				= new Rectangle(0,
																		0,
																		2 * ResizePoint.SIZE,
																		2 * ResizePoint.SIZE);
		boolean						isPressed			= false;
		AnnotationNote				myNote;
		private final int			typeMask;

		public ResizePoint(final AnnotationNote obj, final int type) {
			this.myNote = obj;
			this.setOpaque(false);
			this.setBounds(	-ResizePoint.SIZE,
							-ResizePoint.SIZE,
							2 * ResizePoint.SIZE + Constants.ANNOTATION_SIZE_OFFSET,
							2 * ResizePoint.SIZE + Constants.ANNOTATION_SIZE_OFFSET);
			this.typeMask = type;
		}

		public void drag(final int x, final int y)
		{
			if ((this.typeMask & ResizePoint.TOP) == ResizePoint.TOP)
			{
				this.myNote.adjustTop(y);
			}
			if ((this.typeMask & ResizePoint.BOTTOM) == ResizePoint.BOTTOM)
			{
				this.myNote.adjustBottom(y);
			}
			if ((this.typeMask & ResizePoint.LEFT) == ResizePoint.LEFT)
			{
				this.myNote.adjustLeft(x);
			}
			if ((this.typeMask & ResizePoint.RIGHT) == ResizePoint.RIGHT)
			{
				this.myNote.adjustRight(x);
// myNote.updateBounds();
			}
		}

		@Override
		public void paintComponent(final Graphics g)
		{
			if (this.myNote.selected && !PetriNetObject.ignoreSelection)
			{
				super.paintComponent(g);
				final Graphics2D g2 = (Graphics2D) g;
				g2.setStroke(new BasicStroke(1.0f));
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				if (this.isPressed)
				{
					g2.setPaint(Constants.RESIZE_POINT_DOWN_COLOUR);
				}
				else
				{
					g2.setPaint(Constants.ELEMENT_FILL_COLOUR);
				}
				g2.fill(this.shape);
				g2.setPaint(Constants.ELEMENT_LINE_COLOUR);
				g2.draw(this.shape);
			}
		}

		public void setLocation(final double x, final double y)
		{
			super.setLocation((int) (x - ResizePoint.SIZE), (int) (y - ResizePoint.SIZE));
		}
	}
	public class ResizePointHandler extends MouseInputAdapter
	{

		private final ResizePoint	myPoint;
		private int					startX, startY;

		public ResizePointHandler(final ResizePoint point) {
			this.myPoint = point;
		}

		@Override
		public void mouseDragged(final MouseEvent e)
		{
			this.myPoint.drag(Grid.getModifiedX(e.getX() - this.startX), Grid.getModifiedY(e.getY() -
																							this.startY));
		}

		@Override
		public void mousePressed(final MouseEvent e)
		{
			this.myPoint.myNote.disableEditMode();
			this.myPoint.myNote.setDraggable(false);
			this.myPoint.isPressed = true;
			this.myPoint.repaint();
			this.startX = e.getX();
			this.startY = e.getY();
		}

		@Override
		public void mouseReleased(final MouseEvent e)
		{
			this.myPoint.myNote.setDraggable(true);
			this.myPoint.isPressed = false;
			this.myPoint.repaint();
		}
	}
	/**
	 * 
	 */
	private static final long		serialVersionUID	= -4440645594377973974L;
	private final JTextArea			note				= new JTextArea();
	private boolean					drawBorder			= true;
	private boolean					fillNote			= false;
	private final RectangularShape	noteRect			= new Rectangle();

	private final ResizePoint[]		dragPoints			= new ResizePoint[8];

	private int						originalX;

	private int						originalY;

	public AnnotationNote(final int x, final int y) {
		if (CreateGui.getApp() != null)
		{
			this.addZoomController(CreateGui.getView().getZoomController());
			this.originalX = this.getZoomController().getUnzoomedValue(x);
			this.originalY = this.getZoomController().getUnzoomedValue(y);
		}
		this.note.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.note.setAlignmentY(Component.CENTER_ALIGNMENT);
		this.note.setOpaque(false);
		this.note.setEditable(false);
		this.note.setEnabled(false);
		this.note.setLineWrap(true);
		this.note.setWrapStyleWord(true);
		// Set minimum size the preferred size for an empty string:
		this.note.setText("");
		this.note.setFont(new Font(	Constants.ANNOTATION_DEFAULT_FONT,
									Font.PLAIN,
									Constants.ANNOTATION_DEFAULT_FONT_SIZE));
		this.note.setSize(this.note.getPreferredSize().width, this.note.getPreferredSize().height);
		this.note.setMinimumSize(this.note.getPreferredSize());
		this.note.setHighlighter(new DefaultHighlighter());
		this.note.setDisabledTextColor(Constants.NOTE_DISABLED_COLOUR);
		this.note.setForeground(Constants.NOTE_EDITING_COLOUR);
		this.note.setCaretColor(Constants.NOTE_EDITING_COLOUR);
		this.note.addKeyListener(new AnnotationKeyUpdateHandler(this));

		this.add(this.note);
		this.setLocation(x - Constants.RESERVED_BORDER / 2, y - Constants.RESERVED_BORDER / 2);

		this.dragPoints[0] = new ResizePoint(this, ResizePoint.TOP | ResizePoint.LEFT);
		this.dragPoints[1] = new ResizePoint(this, ResizePoint.TOP);
		this.dragPoints[2] = new ResizePoint(this, ResizePoint.TOP | ResizePoint.RIGHT);
		this.dragPoints[3] = new ResizePoint(this, ResizePoint.RIGHT);
		this.dragPoints[4] = new ResizePoint(this, ResizePoint.BOTTOM | ResizePoint.RIGHT);
		this.dragPoints[5] = new ResizePoint(this, ResizePoint.BOTTOM);
		this.dragPoints[6] = new ResizePoint(this, ResizePoint.BOTTOM | ResizePoint.LEFT);
		this.dragPoints[7] = new ResizePoint(this, ResizePoint.LEFT);

		ResizePointHandler handler;
		for (int i = 0; i < 8; i++)
		{
			handler = new ResizePointHandler(this.dragPoints[i]);
			this.dragPoints[i].addMouseListener(handler);
			this.dragPoints[i].addMouseMotionListener(handler);
			this.add(this.dragPoints[i]);
		}
	}

	public AnnotationNote(	final String text,
							final int x,
							final int y,
							final int w,
							final int h,
							final boolean border) {
		this(x, y);

		this.note.setText(text);
		this.drawBorder = border;
// int width = note.getPreferredSize().width;
// int height = note.getPreferredSize().height;
// width = (w > width) ? w : width;
// height = (h > height) ? h : height;
		this.note.setSize(w, h);
		this.updateBounds();
	}

	public AnnotationNote(final String id, final String text, final int x, final int y) {
		this(x, y);
		this.id = id;
		this.note.setText(text);
		this.note.setSize(this.note.getPreferredSize().width, this.note.getPreferredSize().height);
		this.updateBounds();
	}

	public void adjustBottom(final int dy)
	{
		if (this.note.getPreferredSize().height <= this.note.getHeight() + dy)
		{
			this.note.setSize(new Dimension(this.note.getWidth(), this.note.getHeight() + dy));
		}
	}

	public void adjustLeft(final int dx)
	{
		if (Constants.ANNOTATION_MIN_WIDTH <= this.note.getWidth() - dx)
		{
			this.note.setSize(new Dimension(this.note.getWidth() - dx, this.note.getHeight()));
			this.setLocation(this.getX() + dx, this.getY());
			this.originalX += dx;
		}
	}

	public void adjustRight(final int dx)
	{
		if (Constants.ANNOTATION_MIN_WIDTH <= this.note.getWidth() + dx)
		{
			this.note.setSize(new Dimension(this.note.getWidth() + dx, this.note.getHeight()));
		}
	}

	public void adjustTop(final int dy)
	{
		if (this.note.getPreferredSize().height <= this.note.getHeight() - dy)
		{
			this.note.setSize(new Dimension(this.note.getWidth(), this.note.getHeight() - dy));
			this.setLocation(this.getX(), this.getY() + dy);
			this.originalY += dy;
		}
	}

	public void changeBackground()
	{
		this.fillNote = !this.fillNote;
		this.note.setOpaque(!this.note.isOpaque());
	}

	@Override
	public boolean contains(final int x, final int y)
	{
		boolean pointContains = false;
		for (int i = 0; i < 8; i++)
		{
			pointContains |= this.dragPoints[i].contains(	x - this.dragPoints[i].getX(),
															y - this.dragPoints[i].getY());
		}
		return this.noteRect.contains(x, y) || pointContains;
	}

	@Override
	public void deselect()
	{
		super.deselect();
		this.disableEditMode();
	}

	public void disableEditMode()
	{
		this.note.setOpaque(false);
		this.note.setEditable(false);
		this.note.setEnabled(false);
		CreateGui.getApp().enableGuiMenu();
	}

	public void enableEditMode()
	{
		this.note.setEditable(true);
		this.note.setEnabled(true);
		this.note.setOpaque(true);
		this.note.requestFocus();
		CreateGui.getApp().disableGuiMenu();
	}

	public String getID()
	{
		return this.id;
	}

	public JTextArea getNote()
	{
		return this.note;
	}

	public int getNoteHeight()
	{
		return this.note.getHeight();
	}

	public String getNoteText()
	{
		return this.note.getText();
	}

	public int getNoteWidth()
	{
		return this.note.getWidth();
	}

	public int getOriginalX()
	{

		return this.originalX;
	}

	public int getOriginalY()
	{

		return this.originalY;
	}

	public boolean isFilled()
	{
		return this.fillNote;
	}

	public boolean isShowingBorder()
	{
		return this.drawBorder;
	}

	@Override
	public void paintComponent(final Graphics g)
	{

		this.updateBounds();
		super.paintComponent(g);
		final Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(1.0f));
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);

		if (this.selected && !PetriNetObject.ignoreSelection)
		{
			g2.setPaint(Constants.SELECTION_FILL_COLOUR);
			g2.fill(this.noteRect);
			if (this.drawBorder)
			{
				g2.setPaint(Constants.SELECTION_LINE_COLOUR);
				g2.draw(this.noteRect);
			}
		}
		else
		{
			g2.setPaint(Constants.ELEMENT_FILL_COLOUR);
			if (this.fillNote)
			{
				g2.fill(this.noteRect);
			}
			if (this.drawBorder)
			{
				g2.setPaint(Constants.ELEMENT_LINE_COLOUR);
				g2.draw(this.noteRect);
			}
		}
	}

	public void setID(final String id)
	{
		this.id = id;
	}

	public void showBorder(final boolean show)
	{
		this.drawBorder = show;
	}

	/** Translates the component by x,y */
	public void translate(final int x, final int y)
	{
		this.setLocation(this.getX() + x, this.getY() + y);
		this.originalX += this.getZoomController().getUnzoomedValue(x);
		this.originalY += this.getZoomController().getUnzoomedValue(y);
// updateBounds();
	}

	public void updateBounds()
	{
// setLocation(Grid.getModifiedX(getX() + RESERVED_BORDER/2) -
// RESERVED_BORDER/2,
// Grid.getModifiedY(getY() + RESERVED_BORDER/2) - RESERVED_BORDER/2);

		final int newHeight = this.note.getPreferredSize().height;

		if (this.note.getHeight() < newHeight && newHeight >= this.note.getMinimumSize().height)
		{
			this.note.setSize(this.note.getWidth(), newHeight);
		}

		final int rectWidth = this.note.getWidth() + Constants.RESERVED_BORDER;
		final int rectHeight = this.note.getHeight() + Constants.RESERVED_BORDER;

// float spacing = Grid.getGridSpacing();
//		
// rectWidth = (int)(((int)(rectWidth / spacing) + 1) * spacing);
// rectHeight = (int)(((int)(rectHeight / spacing) + 1) * spacing);

		this.noteRect.setFrame(	Constants.RESERVED_BORDER / 2,
								Constants.RESERVED_BORDER / 2,
								rectWidth,
								rectHeight);

		this.setSize(	rectWidth + Constants.RESERVED_BORDER + Constants.ANNOTATION_SIZE_OFFSET,
						rectHeight + Constants.RESERVED_BORDER + Constants.ANNOTATION_SIZE_OFFSET);

		this.note.setLocation(	(int) this.noteRect.getX() + (rectWidth - this.note.getWidth()) / 2,
								(int) this.noteRect.getY() + (rectHeight - this.note.getHeight()) / 2);

		this.updatePointLocations();
	}

	private void updatePointLocations()
	{
		this.dragPoints[0].setLocation(this.noteRect.getMinX(), this.noteRect.getMinY()); // TOP-LEFT
		this.dragPoints[1].setLocation(this.noteRect.getCenterX(), this.noteRect.getMinY()); // TOP-MIDDLE
		this.dragPoints[2].setLocation(this.noteRect.getMaxX(), this.noteRect.getMinY()); // TOP-RIGHT
		this.dragPoints[3].setLocation(this.noteRect.getMaxX(), this.noteRect.getCenterY()); // MIDDLE-RIGHT
		this.dragPoints[4].setLocation(this.noteRect.getMaxX(), this.noteRect.getMaxY()); // BOTTOM-RIGHT
		this.dragPoints[5].setLocation(this.noteRect.getCenterX(), this.noteRect.getMaxY()); // BOTTOM-MIDDLE
		this.dragPoints[6].setLocation(this.noteRect.getMinX(), this.noteRect.getMaxY()); // BOTTOM-LEFT
		this.dragPoints[7].setLocation(this.noteRect.getMinX(), this.noteRect.getCenterY()); // MIDDLE-LEFT
	}

	public void zoomUpdate()
	{
		if (this.getZoomController() != null)
		{
			this.setLocation(	(int) this.getZoomController().getZoomPositionForXLocation(this.originalX),
								(int) this.getZoomController().getZoomPositionForYLocation(this.originalY));
		}
	}

}
