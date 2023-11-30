package pipe.gui;
import java.awt.Color;

public interface Constants{

//  Filesystem Definitions
//######################################################################################
	String PROPERTY_FILE_EXTENSION = ".properties";
	String PROPERTY_FILE_DESC = "PIPE Properties file";
	String CLASS_FILE_EXTENSION = ".class";
	String CLASS_FILE_DESC = "Java Class File";
//	File DEFAULT_DIRECTORY = new File("Petri-Nets");
//	String DEFAULT_FILENAME = "PetriNet.xml";

//PetriNet Object Type Definitions
	int ANIMATE = 98;
	int RANDOM = 99;
	int START = 100;
	int FIRE = 101;
	int STEPFORWARD = 102;
	int STEPBACKWARD = 103;
	int STOP = 104;
	
	int PLACE = 105;
	int IMMTRANS = 106;
	int TIMEDTRANS = 114;
	int ADDTOKEN = 107;
	int DELTOKEN = 108;
	int ANNOTATION = 109;
	int SELECT = 110;
	int DELETE = 111;
	int ARC = 112;
	int GRID = 113;
	
	int DRAW = 115;
	int DRAG = 116;

	
	// Special
	int CREATING = 200;	// Parsing in a PNML file - creating components
	
	int DEFAULT_ELEMENT_TYPE = SELECT;
	
	int PLACE_TRANSITION_HEIGHT=30;
	
	Color ENABLED_TRANSITION_COLOUR = new Color(192,0,0);
	Color ELEMENT_LINE_COLOUR = Color.BLACK;
	Color ELEMENT_FILL_COLOUR = Color.WHITE;
	Color SELECTION_LINE_COLOUR = new Color(0,0,192);
	Color SELECTION_FILL_COLOUR = new Color(192,192,255);
	
	// For ArcPath:
	int ARC_CONTROL_POINT_CONSTANT = 3;
	int ARC_PATH_SELECTION_WIDTH = 6;
	int ARC_PATH_PROXIMITY_WIDTH = 10;
	
	// For Place/Transition Arc Snap-To behaviour:
	int PLACE_TRANSITION_PROXIMITY_RADIUS = 25;
	
	// Object layer positions for GuiView:
	int ARC_POINT_LAYER_OFFSET = 50;
	int ARC_LAYER_OFFSET = 20;
	int PLACE_TRANSITION_LAYER_OFFSET = 30;
	int ANNOTATION_LAYER_OFFSET = 10;
	int SELECTION_LAYER_OFFSET = 90;
	int LOWEST_LAYER_OFFSET = 0;
	
	// For AnnotationNote appearance:
	int RESERVED_BORDER = 12;
	int ANNOTATION_SIZE_OFFSET = 4;
	int ANNOTATION_MIN_WIDTH = 40;
	Color NOTE_DISABLED_COLOUR = Color.BLACK;
	Color NOTE_EDITING_COLOUR = Color.BLACK;
	Color RESIZE_POINT_DOWN_COLOUR = new Color(220,220,255);
	String ANNOTATION_DEFAULT_FONT = "Helvetica";
	int ANNOTATION_DEFAULT_FONT_SIZE = 12;
}

