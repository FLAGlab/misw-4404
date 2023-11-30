/*
 * Created on 10-Feb-2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package pipe.gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;

/**
 * @author Peter Kyme
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class Grid implements Constants {
	private static float gridSpacing = PLACE_TRANSITION_HEIGHT/2;
	private static final Color gridColor = new Color(240,240,255);
	private static GeneralPath gridDisplay;
	private static boolean enabled;
	private static int gridHeight, gridWidth;
	
	private static int gridCount = 1;

	private static void createGrid() {
		gridDisplay = new GeneralPath();
		for(float i=gridSpacing; i<=gridWidth; i+= gridSpacing) {
			gridDisplay.moveTo(i,2);
			gridDisplay.lineTo(i,gridHeight);
		}
		for(float i= gridSpacing; i<=gridHeight; i+= gridSpacing) {
			gridDisplay.moveTo(2,i);
			gridDisplay.lineTo(gridWidth,i);
		}
	}
	
	public static void enableGrid() {
		if (!enabled) {
			enabled = true;
		}
	}
	
	public static void increment(){
		gridCount++;
		gridCount %= 4;
							
		if (gridCount == 3) {	
			disableGrid();
		} else {	
			enableGrid();
			setGridSpacing(Math.pow(2, gridCount-2));
		}
	}
	
	public static void setGridSpacing(double spacing) {
		gridSpacing = (float)(spacing * PLACE_TRANSITION_HEIGHT);
	}
	
	public static void disableGrid() {
		if (enabled) {
			enabled = false;
		}
	}
	
	public static boolean enabled() {
		return enabled;
	}
	
	public static void updateSize(Container parent) {
		if (enabled) {
			gridHeight = parent.getHeight();
			gridWidth = parent.getWidth();
			createGrid();
		}
	}
	
	public static void drawGrid(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		
		g2d.setPaint(gridColor);
		g2d.draw(gridDisplay);
	}
	
	public static int getModifiedX(double x) {
		if (!enabled) return (int)x;
		return (int)(Math.round(x/gridSpacing) * gridSpacing);

	}
	
	public static int getModifiedY(double y) {
		if (!enabled) return (int)y;
		return (int)(Math.round(y/gridSpacing) * gridSpacing);
	}
	
	public static float getGridSpacing() {
		if (enabled)
			return gridSpacing;
		else
			return 1;			// Pretend we're using a pixel-fine grid
	}
}
