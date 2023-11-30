package pipe.common.dataLayer;

// Collections
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Observable;
import java.util.Random;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import pipe.dataLayer.calculations.StateSpaceGenerator;
import pipe.gui.Constants;
import pipe.gui.CreateGui;

/**
 * <b>DataLayer</b> - Encapsulates entire Petri-Net, also contains functions to
 * perform calculations
 * 
 * @see
 * <p>
 * <a href="..\PNMLSchema\index.html">PNML - Petri-Net XMLSchema (stNet.xsd)</a>
 * @see
 * </p>
 * <p>
 * <a href="uml\DataLayer.png">DataLayer UML</a>
 * </p>
 * @version 1.0
 * @author James D Bloom
 * @author David Patterson Jan 2, 2006: Changed the fireRandomTransition method
 *         to give precedence to immediate transitions.
 * 
 * @author Edwin Chung added a boolean attribute to each matrix generated to
 *         prevent them from being created again when they have not been changed
 *         (6th Feb 2007)
 * 
 * @author Ben Kirby Feb 10, 2007: Removed savePNML method and the
 *         createPlaceElement, createAnnotationElement, createArcElement,
 *         createArcPoint, createTransitionElement methods it uses to a separate
 *         DataLayerWriter class, as part of refactoring to remove XML related
 *         actions from the DataLayer class.
 * 
 * @author Ben Kirby Feb 10, 2007: Split loadPNML into two bits. All XML work
 *         (Files, transformers, documents) is done in new PNMLTransformer
 *         class. The calls to actually populate a DataLayer object with the
 *         info contained in the PNML document have been moved to a
 *         createFromPNML method. The DataLayer constructor which previously
 *         used loadPNML has been changed to reflect these modifications. Also
 *         moved getDOM methods to PNMLTranformer class, as getDom is XML
 *         related. Removed getDom() (no arguments) completely as this is not
 *         called anywhere in the application.
 * 
 * @author Will Master Feb 13 2007: Added methods getPlacesCount and
 *         getTransitionsCount to avoid needlessly copying place and transition
 *         arrayLists.
 * 
 * @author Edwin Chung 15th Mar 2007: modified the createFromPNML function so
 *         that DataLayer objects can be created outside GUI
 * 
 * @author Dave Patterson 24 April 2007: Modified the fireRandomTransition
 *         method so it is quicker when there is only one transition to fire
 *         (just fire it, don't get a random variable first). Also, throw a
 *         RuntimeException if a rate less than 1 is detected. The current code
 *         uses the rate as a weight, and a rate such as 0.5 leads to a
 *         condition like that of bug 1699546 where no transition is available
 *         to fire.
 * 
 * @author Dave Patterson 10 May 2007: Modified the fireRandomTransitino method
 *         so it now properly handles fractional weights. There is no
 *         RuntimeException thrown now. The code for timed transitions uses the
 *         same logic, but will soon be changed to use exponentially distributed
 *         times where fractional rates are valid.
 * 
 * @author Barry Kearns August 2007: Added clone functionality and storage of
 *         state groups.
 * 
 */
public class DataLayer extends Observable implements pipe.gui.Constants, Cloneable
{

	public static Random	randomNumber				= new Random(); // Random
	// number
	// generator

	/** PNML File Name */
	public static String	pnmlName					= null;

	// /** Used to determine whether the markings have been modified */
	static boolean			initialMarkingVectorChanged	= true;
	static boolean			currentMarkingVectorChanged	= true;

	/**
	 * 
	 * @param original
	 *            arraylist to be deep copied
	 * @return a clone of the arraylist
	 */
	private static ArrayList deepCopy(final ArrayList original)
	{
		final ArrayList result = (ArrayList) original.clone();
		final ListIterator listIter = result.listIterator();

		while (listIter.hasNext())
		{
			final PetriNetObject pnObj = (PetriNetObject) listIter.next();
			listIter.set(pnObj.clone());
		}
		return result;
	}
	/** List containing all the Place objects in the Petri-Net */
	private ArrayList	placesArray						= null;
	/** ArrayList containing all the Transition objects in the Petri-Net */
	private ArrayList	transitionsArray				= null;

	/** ArrayList containing all the Arc objects in the Petri-Net */
	private ArrayList	arcsArray						= null;

	/** ArrayList containing all the Token objects in the Petri-Net */
	private ArrayList	tokensArray						= null;

	/** ArrayList containing all the Arrow objects in the Petri-Net */
	private ArrayList	arrowsArray						= null;
	/**
	 * ArrayList for net-level label objects (as opposed to element-level
	 * labels).
	 */
	private ArrayList	labelsArray						= null;
	/**
	 * An ArrayList used to point to either the Arc, Place or Transition
	 * ArrayLists when these ArrayLists are being update
	 */
	private ArrayList	changeArrayList					= null;

	/** Initial Marking Vector */
	private int[]		initialMarkingVector			= null;
	/** Initial Marking Vector */
	private int[]		currentMarkingVector			= null;
	/** Marking Vector Storage used during animation */
	private int[]		markingVectorAnimationStorage	= null;
	
	/* original place where tagged token is located */
	private int originalTaggedPlace = -1;
	
	private ArrayList previoulyTaggedPlace = null;
	private int firedTagged=0;
	private boolean outOfBound = false;
	
	/* boolean determining if tagged structure has been validated*/
	private boolean validated = false;
	
	/** Forward Incidence Matrix */
	private PNMatrix	forwardsIncidenceMatrix			= null;

	/** Backward Incidence Matrix */
	private PNMatrix	backwardsIncidenceMatrix		= null;

	/** Incidence Matrix */
	private PNMatrix	incidenceMatrix					= null;
	/** X-Axis Scale Value */
	private final int	DISPLAY_SCALE_FACTORX			= 7;	// Scale
	// factors
	// for
	// loading
	// other
	// Petri-Nets
	// (not
	// yet
	// implemented)
	/** Y-Axis Scale Value */
	private final int	DISPLAY_SCALE_FACTORY			= 7;	// Scale
	// factors
	// for
	// loading
	// other
	// Petri-Nets
	// (not
	// yet
	// implemented)
	/** X-Axis Shift Value */
	private final int	DISPLAY_SHIFT_FACTORX			= 270;	// Scale

	// factors
	// for
	// loading
	// other
	// Petri-Nets
	// (not
	// yet
	// implemented)
	/** Y-Axis Shift Value */
	private final int	DISPLAY_SHIFT_FACTORY			= 120;	// Scale
	// factors
	// for
	// loading
	// other
	// Petri-Nets
	// (not
	// yet
	// implemented)

	/**
	 * Hashtable which maps PlaceTransitionObjects to their list of connected
	 * arcs
	 */
	private Hashtable	arcsMap							= null;

	/**
	 * An ArrayList used store the source / destination state groups associated
	 * with this Petri-Net
	 */
	private ArrayList	stateGroups						= null;

	/**
	 * Create empty Petri-Net object
	 */
	public DataLayer() {
		this.initializeMatrices();
	}

	/**
	 * Create Petri-Net object from pnmlFile
	 * 
	 * @param pnmlFile
	 *            PNML File
	 */
	public DataLayer(final File pnmlFile) {
		this(pnmlFile.getAbsolutePath());
	}

	public DataLayer(final String pnmlFileName) {
		this.initializeMatrices();
		final PNMLTransformer transform = new PNMLTransformer();
		final File temp = new File(pnmlFileName);
		DataLayer.pnmlName = temp.getName();
		this.createFromPNML(transform.transformPNML(pnmlFileName));

	}

	/*
	 * TK - this method is never called. Populates the arcsMap hashtable
	 * enabling easier cross referencing of places, transitions and the arcs
	 * connected to them.
	 * 
	 * 
	 * private void setArcConnectionMap() { // map (PTO, arcslist) // get all
	 * the PlaceTransitionObjects ArrayList allPTO = new ArrayList(placesArray);
	 * allPTO.addAll(transitionsArray);
	 * 
	 * Iterator pto; Iterator arcs; PlaceTransitionObject current ;
	 * 
	 * pto = allPTO.iterator(); while (pto.hasNext()) { current =
	 * (PlaceTransitionObject)pto.next(); // make an entry for each pto in our
	 * mapping structure arcsMap.put(current,new ArrayList()); }
	 * 
	 * arcs = arcsArray.iterator(); Arc currentArc; PlaceTransitionObject
	 * source; PlaceTransitionObject target; ArrayList arcslist = null; //
	 * iterate over the arcs, getting the source and destination ptos and adding
	 * the arc to their lists while (arcs.hasNext()) { currentArc =
	 * (Arc)arcs.next(); source = currentArc.getSource(); target =
	 * currentArc.getTarget(); // get the list of arcs attached to the source
	 * and target and add the arc to them try {
	 * ((ArrayList)arcsMap.get(source)).add(currentArc); } catch
	 * (NullPointerException ne1) { // System.out.println("Populating arcsMap: " +
	 * ne1.toString()); }
	 * 
	 * try { ((ArrayList)arcsMap.get(target)).add(currentArc); } catch
	 * (NullPointerException ne2) { // System.out.println("Populating arcsMap: " +
	 * ne2.toString()); } } }
	 */

	/*
	 * TK - this method is never called Remove first Place that has an id equal
	 * to idInput
	 * 
	 * @param idInput id of Place object to remove from Petri-Net
	 * 
	 * public void removePlace(String idInput) { for(int i = 0 ; i <
	 * placesArray.size(); i++)
	 * if(idInput.equals(((Place)placesArray.get(i)).getId())) {
	 * placesArray.remove(i); setChanged(); setMatrixChanged(); } }
	 */

	/*
	 * TK - this method is never called Remove first Transition that has an id
	 * equal to idInput
	 * 
	 * @param idInput id of Transition object to remove from Petri-Net
	 * 
	 * public void removeTransition(String idInput) { for(int i = 0 ; i <
	 * transitionsArray.size(); i++)
	 * if(idInput.equals(((Transition)transitionsArray.get(i)).getId())) {
	 * transitionsArray.remove(i); setChanged(); setMatrixChanged(); } }
	 */

	/*
	 * TK - this method is never called Remove first Arc that has an id equal to
	 * idInput
	 * 
	 * @param idInput id of Arc object to remove from Petri-Net
	 * 
	 * public void removeArc(String idInput) {
	 * 
	 * for(int i = 0 ; i < arcsArray.size(); i++)
	 * if(idInput.equals(((Arc)arcsArray.get(i)).getId())) {
	 * arcsArray.remove(i); setChanged(); setMatrixChanged(); } }
	 */

	/**
	 * Add placeInput to the back of the Place ArrayList All observers are
	 * notified of this change (Model-View Architecture)
	 * 
	 * @param placeInput
	 *            Place Object to add
	 */
	private void addAnnotation(final AnnotationNote labelInput)
	{
		this.labelsArray.add(labelInput);
		this.setChanged();
		this.notifyObservers(labelInput);
	}

	/**
	 * Add arcInput to back of the Arc ArrayList All observers are notified of
	 * this change (Model-View Architecture)
	 * 
	 * @param arcInput
	 *            Arc Object to add
	 */
	public void addArc(final Arc arcInput)
	{
		boolean unique = true;
		if (arcInput != null)
		{
			if (arcInput.getId() != null && arcInput.getId().length() > 0)
			{
				for (int i = 0; i < this.arcsArray.size(); i++)
				{
					if (arcInput.getId().equals(((Arc) this.arcsArray.get(i)).getId()))
					{
						unique = false;
					}
				}
			}
			else
			{
				String id = null;
				if (this.arcsArray != null && this.arcsArray.size() > 0)
				{
					int no = this.arcsArray.size();
					do
					{
						for (int i = 0; i < this.arcsArray.size(); i++)
						{
							id = "A" + no;
							if (this.arcsArray.get(i) != null)
							{
								if (id.equals(((Arc) this.arcsArray.get(i)).getId()))
								{
									unique = false;
									no++;
								}
								else
								{
									unique = true;
								}
							}
						}
					} while (!unique);
				}
				else
				{
					id = "A0";
				}

				if (id != null)
				{
					arcInput.setId(id);
				}
				else
				{
					arcInput.setId("error");
				}
			}
			this.arcsArray.add(arcInput);
			this.addArcToArcsMap(arcInput);

			this.setChanged();
			this.setMatrixChanged();
			// notifyObservers(arcInput.getBounds());
			this.notifyObservers(arcInput);
		}
	}

	/**
	 * Update the arcsMap hashtable to reflect the new arc
	 * 
	 * @param arcInput
	 *            New Arc
	 */
	private void addArcToArcsMap(final Arc arcInput)
	{
		// now we want to add the arc to the list of arcs for it's source and
		// target
		final PlaceTransitionObject source = arcInput.getSource();
		final PlaceTransitionObject target = arcInput.getTarget();
		ArrayList newList = null;

		if (source != null)
		{
// Pete: Place/Transitions now always moveable
// source.setMovable(false);
			if (this.arcsMap.get(source) != null)
			{
// System.out.println("adding arc to existing list");
				((ArrayList) this.arcsMap.get(source)).add(arcInput);
			}
			else
			{
// System.out.println("creating new arc list");
				newList = new ArrayList();
				newList.add(arcInput);
				this.arcsMap.put(source, newList);
			}
		}

		if (target != null)
		{
// Pete: Place/Transitions now always moveable
// target.setMovable(false);
			if (this.arcsMap.get(target) != null)
			{
// System.out.println("adding arc to existing list2");
				((ArrayList) this.arcsMap.get(target)).add(arcInput);
			}
			else
			{
// System.out.println("creating new arc list2");
				newList = new ArrayList();
				newList.add(arcInput);
				this.arcsMap.put(target, newList);
			}
		}

	}

	/**
	 * Add any PetriNetObject - the object will be added to the appropriate
	 * list. If the object passed in isn't a Transition, Place or Arc nothing
	 * will happen. All observers are notified of this change.
	 * 
	 * @param pnObject
	 *            The PetriNetObject to be added.
	 */

	public void addPetriNetObject(final PetriNetObject pnObject)
	{

		if (this.setPetriNetObjectArrayList(pnObject))
		{

			if (pnObject instanceof Arc)
			{
				this.addArcToArcsMap((Arc) pnObject);
				this.addArc((Arc) pnObject);
			}
			else if (pnObject instanceof Place)
			{
				this.addPlace((Place) pnObject);
			}
			else if (pnObject instanceof Transition)
			{
				this.addTransition((Transition) pnObject);
			}
			else if (pnObject instanceof AnnotationNote)
			{
				this.labelsArray.add(pnObject);
			}
			else
			{ // arrows, other labels.
				this.changeArrayList.add(pnObject);
				this.setChanged();
				this.setMatrixChanged();
				this.notifyObservers(pnObject);
			}

		}
		// we reset to null so that the wrong ArrayList can't get added to
		this.changeArrayList = null;
		this.validated = false;
	}

	/**
	 * Add placeInput to the back of the Place ArrayList All observers are
	 * notified of this change (Model-View Architecture)
	 * 
	 * @param placeInput
	 *            Place Object to add
	 */
	private void addPlace(final Place placeInput)
	{
		boolean unique = true;
		if (placeInput != null)
		{
			if (placeInput.getId() != null && placeInput.getId().length() > 0)
			{
				for (int i = 0; i < this.placesArray.size(); i++)
				{
					if (placeInput.getId().equals(((Place) this.placesArray.get(i)).getId()))
					{
						unique = false;
					}
				}
			}
			else
			{
				String id = null;
				if (this.placesArray != null && this.placesArray.size() > 0)
				{
					int no = this.placesArray.size();
					// id = "P" + no;
					do
					{
						// System.out.println("in while loop");
						for (int i = 0; i < this.placesArray.size(); i++)
						{
							id = "P" + no;
							if (this.placesArray.get(i) != null)
							{
								if (id.equals(((Place) this.placesArray.get(i)).getId()))
								{
									// System.out.println("testing id: " + id);
									unique = false;
									no++;
								}
								else
								{
									unique = true;
								}
							}
						}
					} while (!unique);
				}
				else
				{
					id = "P0";
				}

				if (id != null)
				{
					placeInput.setId(id);
				}
				else
				{
					placeInput.setId("error");
				}
			}
			this.placesArray.add(placeInput);
			this.setChanged();
			this.setMatrixChanged();
			// notifyObservers(placeInput.getBounds());
			this.notifyObservers(placeInput);
		}
	}

	public void addStateGroup(final StateGroup stateGroupInput)
	{
		boolean unique = true;
		String id = null;
		int no = this.stateGroups.size();

		// Check if ID is set from PNML file
		if (stateGroupInput.getId() != null && stateGroupInput.getId().length() > 0)
		{
			id = stateGroupInput.getId();

			// Check if ID is unique
			for (int i = 0; i < this.stateGroups.size(); i++)
			{
				if (id.equals(((StateGroup) this.stateGroups.get(i)).getId()))
				{
					unique = false;
				}
			}
		}
		else
		{
			unique = false;
		}

		// Find a unique ID for the new state group
		if (!unique)
		{
			id = "SG" + no;
			for (int i = 0; i < this.stateGroups.size(); i++)
			{
				// If a matching ID is found, increment id and reset loop
				if (id.equals(((StateGroup) this.stateGroups.get(i)).getId()))
				{
					id = "SG" + ++no;
					i = 0;
				}
			}

			stateGroupInput.setId(id);

		}

		this.stateGroups.add(stateGroupInput);
	}

	/**
	 * Add transitionInput to back of the Transition ArrayList All observers are
	 * notified of this change (Model-View Architecture)
	 * 
	 * @param transitionInput
	 *            Transition Object to add
	 */
	private void addTransition(final Transition transitionInput)
	{
		boolean unique = true;
		if (transitionInput != null)
		{
			if (transitionInput.getId() != null && transitionInput.getId().length() > 0)
			{
				for (int i = 0; i < this.transitionsArray.size(); i++)
				{
					if (transitionInput.getId().equals(((Transition) this.transitionsArray.get(i)).getId()))
					{
						unique = false;
					}
				}
			}
			else
			{
				String id = null;
				if (this.transitionsArray != null && this.transitionsArray.size() > 0)
				{
					int no = this.transitionsArray.size();
					do
					{
						// System.out.println("transition while loop");
						for (int i = 0; i < this.transitionsArray.size(); i++)
						{
							id = "T" + no;
							if (this.transitionsArray.get(i) != null)
							{
								if (id.equals(((Transition) this.transitionsArray.get(i)).getId()))
								{
									unique = false;
									no++;
								}
								else
								{
									unique = true;
								}
							}
						}
					} while (!unique);
				}
				else
				{
					id = "T0";
				}

				if (id != null)
				{
					transitionInput.setId(id);
				}
				else
				{
					transitionInput.setId("error");
				}
			}
			this.transitionsArray.add(transitionInput);
			this.setChanged();
			this.setMatrixChanged();
			// notifyObservers(transitionInput.getBounds());
			this.notifyObservers(transitionInput);
		}
	}

	/**
	 * Method to clone a DataLayer obejct
	 */
	@Override
	public DataLayer clone()
	{
		DataLayer newClone = null;
		try
		{
			newClone = (DataLayer) super.clone();

			newClone.placesArray = DataLayer.deepCopy(this.placesArray);
			newClone.transitionsArray = DataLayer.deepCopy(this.transitionsArray);
			newClone.arcsArray = DataLayer.deepCopy(this.arcsArray);
			newClone.tokensArray = DataLayer.deepCopy(this.tokensArray);
			newClone.labelsArray = DataLayer.deepCopy(this.labelsArray);

		}
		catch (final CloneNotSupportedException e)
		{
			throw new Error(e);
		}

		return newClone;
	}

	
	 /* TK - this method is never called Add tokenInput to the back of the Token
	 * ArrayList All observers are notified of this change.
	 * 
	 * @param tokenInput Token Object to add
	 *
	 *private void addToken(Token tokenInput) { 
		 
		 tokensArray.add(tokenInput);
		 setChanged(); 
		 setMatrixChanged();
		 //notifyObservers(tokenInput.getBounds()); 
		 notifyObservers(tokenInput); 	 
	 }
	 */

	/**
	 * Creates a Label object from a Label DOM Element
	 * 
	 * @param inputLabelElement
	 *            Input Label DOM Element
	 * @return Label Object
	 */
	private AnnotationNote createAnnotation(final Element inputLabelElement)
	{
		int positionXInput = 0;
		int positionYInput = 0;
		int widthInput = 0;
		int heightInput = 0;
		String text = null;
		boolean borderInput = true;

		final String positionXTempStorage = inputLabelElement.getAttribute("xPosition");
		final String positionYTempStorage = inputLabelElement.getAttribute("yPosition");
		final String widthTemp = inputLabelElement.getAttribute("w");
		final String heightTemp = inputLabelElement.getAttribute("h");
		final String textTempStorage = inputLabelElement.getAttribute("txt");
		final String borderTemp = inputLabelElement.getAttribute("border");

		if (positionXTempStorage.length() > 0)
		{
			positionXInput = Integer.valueOf(positionXTempStorage).intValue() *
								(false ? this.DISPLAY_SCALE_FACTORX : 1) +
								(false ? this.DISPLAY_SHIFT_FACTORX : 1);
		}
		if (positionYTempStorage.length() > 0)
		{
			positionYInput = Integer.valueOf(positionYTempStorage).intValue() *
								(false ? this.DISPLAY_SCALE_FACTORX : 1) +
								(false ? this.DISPLAY_SHIFT_FACTORX : 1);
		}
		if (widthTemp.length() > 0)
		{
			widthInput = Integer.valueOf(widthTemp).intValue() * (false ? this.DISPLAY_SCALE_FACTORY : 1) +
							(false ? this.DISPLAY_SHIFT_FACTORY : 1);
		}
		if (heightTemp.length() > 0)
		{
			heightInput = Integer.valueOf(heightTemp).intValue() * (false ? this.DISPLAY_SCALE_FACTORY : 1) +
							(false ? this.DISPLAY_SHIFT_FACTORY : 1);
		}
		if (borderTemp.length() > 0)
		{
			borderInput = Boolean.valueOf(borderTemp).booleanValue();
		}
		else
		{
			borderInput = true;
		}
		if (textTempStorage.length() > 0)
		{
			text = textTempStorage;
		}
		else
		{
			text = "";
		}

		return new AnnotationNote(text, positionXInput, positionYInput, widthInput, heightInput, borderInput);
	}

	/**
	 * Creates a Arc object from a Arc DOM Element
	 * 
	 * @param inputArcElement
	 *            Input Arc DOM Element
	 * @return Arc Object
	 */
	private Arc createArc(final Element inputArcElement)
	{
		String idInput = null;
		String sourceInput = null;
		String targetInput = null;
		int weightInput = 1;
		double startX = 0;
		double startY = 0;
		boolean taggedArc;

		sourceInput = inputArcElement.getAttribute("source");
		targetInput = inputArcElement.getAttribute("target");
		final String idTempStorage = inputArcElement.getAttribute("id");
		final String sourceTempStorage = inputArcElement.getAttribute("source");
		final String targetTempStorage = inputArcElement.getAttribute("target");
		final String inscriptionTempStorage = inputArcElement.getAttribute("inscription");
		final String taggedTempStorage = inputArcElement.getAttribute("tagged");
// String inscriptionOffsetXTempStorage =
// inputArcElement.getAttribute("inscriptionOffsetX");
// String inscriptionOffsetYTempStorage =
// inputArcElement.getAttribute("inscriptionOffsetY");

		taggedArc = !(taggedTempStorage.length() == 0 || taggedTempStorage.length() == 5);

		if (idTempStorage.length() > 0)
		{
			idInput = idTempStorage;
		}
		if (sourceTempStorage.length() > 0)
		{
			sourceInput = sourceTempStorage;
		}
		if (targetTempStorage.length() > 0)
		{
			targetInput = targetTempStorage;
		}
		if (inscriptionTempStorage.length() > 0)
		{
			weightInput = Integer	.valueOf((inputArcElement.getAttribute("inscription") != null	? inputArcElement.getAttribute("inscription")
																									: "1"))
									.intValue();
// if (inscriptionOffsetXTempStorage.length() > 0)
// inscriptionOffsetXInput =
// Double.valueOf(inputArcElement.getAttribute("inscriptionOffsetX")).doubleValue();
// if (inscriptionOffsetYTempStorage.length() > 0)
// inscriptionOffsetYInput =
// Double.valueOf(inputArcElement.getAttribute("inscriptionOffsetY")).doubleValue();
		}

		if (sourceInput.length() > 0)
		{
			if (this.getPlaceTransitionObject(sourceInput) != null)
			{
// System.out.println("PNMLDATA: sourceInput is not null");
				startX = this.getPlaceTransitionObject(sourceInput).getPositionX();
				startY = this.getPlaceTransitionObject(sourceInput).getPositionY();
				if (CreateGui.getApp() != null)
				{
					startX += this.getPlaceTransitionObject(sourceInput).centreOffsetLeft();
					startY += this.getPlaceTransitionObject(sourceInput).centreOffsetTop();
				}
			}
		}
		final PlaceTransitionObject sourceIn = this.getPlaceTransitionObject(sourceInput);
		final PlaceTransitionObject targetIn = this.getPlaceTransitionObject(targetInput);

		int aStartx;
		int aStarty;
		int aEndx;
		int aEndy;
		aStartx = sourceIn.getX();
		aStarty = sourceIn.getY();
		aEndx = targetIn.getX();
		aEndy = targetIn.getY();
		// add the insets and offset
		if (CreateGui.getApp() != null)
		{
			aStartx += sourceIn.centreOffsetLeft();
			aStarty += sourceIn.centreOffsetTop();
			aEndx += targetIn.centreOffsetLeft();
			aEndy += targetIn.centreOffsetTop();
		}

		final double _startx = aStartx;
		final double _starty = aStarty;
		final double _endx = aEndx;
		final double _endy = aEndy;

		final Arc tempArc = new Arc(_startx,
									_starty,
									_endx,
									_endy,
									sourceIn,
									targetIn,
									weightInput,
									idInput,
									taggedArc,
									Color.BLACK);

		this.getPlaceTransitionObject(sourceInput).addConnectFrom(tempArc);
		this.getPlaceTransitionObject(targetInput).addConnectTo(tempArc);

// **********************************************************************************
// The following section attempts to load and display arcpath
// details****************
// ArcPath tempArcPath = tempArc.getArcPath();

		float arcPointX = 0;
		float arcPointY = 0;
		boolean arcPointType = false;
		String arcTempX = null;
		String arcTempY = null;
		String arcTempType = null;
		Node node = null;
		NodeList nodelist = null;
		nodelist = inputArcElement.getChildNodes();
		if (nodelist.getLength() > 0)
		{
			tempArc.getArcPath().purgePathPoints();
			for (int i = 1; i < nodelist.getLength() - 1; i++)
			{
				node = nodelist.item(i);
				if (node instanceof Element)
				{
					final Element element = (Element) node;
					if ("arcpath".equals(element.getNodeName()))
					{
						arcTempX = element.getAttribute("x");
						arcTempY = element.getAttribute("y");
						arcTempType = element.getAttribute("arcPointType");
						arcPointX = Float.valueOf(arcTempX).floatValue();
						arcPointY = Float.valueOf(arcTempY).floatValue();
						arcPointX += Constants.ARC_CONTROL_POINT_CONSTANT + 1;
						arcPointY += Constants.ARC_CONTROL_POINT_CONSTANT + 1;
						arcPointType = Boolean.valueOf(arcTempType).booleanValue();
						tempArc.getArcPath().addPoint(arcPointX, arcPointY, arcPointType);

					}
				}
			}
		}

// Arc path creation ends
// here***************************************************************
// ******************************************************************************************
		this.setMatrixChanged();
		return tempArc;
	}

	/**
	 * Creates Backwards Incidence Matrix from current Petri-Net
	 * 
	 */
	private void createBackwardsIncidenceMatrix()
	{// Matthew

		int placeNo = 0, transitionNo = 0;
		final int placeSize = this.placesArray.size();
		final int transitionSize = this.transitionsArray.size();
		Arc arc = null;
		Place place = null;
		Transition transition = null;
		this.backwardsIncidenceMatrix = new PNMatrix(placeSize, transitionSize);
		this.backwardsIncidenceMatrix.setToZero();
		for (int i = 0; i < this.arcsArray.size(); i++)
		{
			arc = (Arc) this.arcsArray.get(i);
			if (arc != null)
			{
				PetriNetObject pnObject = arc.getSource();
				if (pnObject != null)
				{
					if (pnObject instanceof Place)
					{
						place = (Place) pnObject;
						pnObject = arc.getTarget();
						if (pnObject != null)
						{
							if (pnObject instanceof Transition)
							{
								transition = (Transition) pnObject;
								transitionNo = this.getListPosition(transition);
								placeNo = this.getListPosition(place);
								this.backwardsIncidenceMatrix.set(placeNo, transitionNo, arc.getWeight());
							}
						}
					}
				}
			}
		}
		this.backwardsIncidenceMatrix.matrixChanged = false;
	}

	/**
	 * Creates Initial Markup Matrix from current Petri-Net
	 * 
	 */
	private void createCurrentMarkingVector()
	{

		int placeNo = 0;
		final int placeSize = this.placesArray.size();
		this.currentMarkingVector = new int[placeSize];
		for (; placeNo < placeSize; placeNo++)
		{
			this.currentMarkingVector[placeNo] = ((Place) this.placesArray.get(placeNo)).getCurrentMarking();
		}
		DataLayer.currentMarkingVectorChanged = false;
	}

	/**
	 * Creates Forward Incidence Matrix from current Petri-Net
	 * 
	 */
	private void createForwardIncidenceMatrix()
	{// m

		int placeNo = 0, transitionNo = 0;
		final int placeSize = this.placesArray.size();
		final int transitionSize = this.transitionsArray.size();

		Arc arc = null;
		Place place = null;
		Transition transition = null;

		this.forwardsIncidenceMatrix = new PNMatrix(placeSize, transitionSize);

		this.forwardsIncidenceMatrix.setToZero();

		for (int i = 0; i < this.arcsArray.size(); i++)
		{
			arc = (Arc) this.arcsArray.get(i);
			if (arc != null)
			{
				PetriNetObject pnObject = arc.getTarget();
				if (pnObject != null)
				{
					if (pnObject instanceof Place)
					{
						place = (Place) pnObject;
						pnObject = arc.getSource();
						if (pnObject != null)
						{
							if (pnObject instanceof Transition)
							{
								transition = (Transition) pnObject;
								transitionNo = this.getListPosition(transition);
								placeNo = this.getListPosition(place);
								this.forwardsIncidenceMatrix.set(placeNo, transitionNo, arc.getWeight());
							}
						}
					}
				}
			}
		}
		this.forwardsIncidenceMatrix.matrixChanged = false;
	}

	/*
	 * TK - this method is never called Set the Forward Incidence Matrix
	 * 
	 * @param forwardIncidenceInput Forward Incidence Matrix object
	 * 
	 * public void setForwardIncidenceMatrix(int[][] forwardIncidenceInput) {
	 * forwardsIncidenceMatrix = new PNMatrix(forwardIncidenceInput);
	 * forwardsIncidenceMatrix.matrixChanged = true; }
	 */

	/*
	 * TK - this method is never called Set the Backward Incidence Matrix
	 * 
	 * @param backwardsIncidenceInput Backward Incidence Matrix object
	 * 
	 * 
	 * public void setBackwardsIncidenceMatrix(int[][] backwardsIncidenceInput) {
	 * backwardsIncidenceMatrix = new PNMatrix(backwardsIncidenceInput);
	 * backwardsIncidenceMatrix.matrixChanged = true; }
	 */

	/*
	 * TK - this method is never called Set the Incidence Matrix
	 * 
	 * @param incidenceInput Backward Incidence Matrix object
	 * 
	 * public void setIncidenceMatrix(int[][] incidenceInput) { incidenceMatrix =
	 * new PNMatrix(incidenceInput); incidenceMatrix.matrixChanged = true; }
	 */

	/*
	 * TK - this method is never called Set the Initial Markup Matrix
	 * 
	 * @param markupInput Initial Markup Matrix object
	 * 
	 * public void setInitalMarkupMatrix(int[][] markupInput) { int i = 0; int j =
	 * 0; Integer intergerInput = null; for( ; i < markupInput.length ; i++) {
	 * fowardsIncidenceMatrix.add(i, new ArrayList(markupInput[i].length)); for( ;
	 * j < markupInput[i].length ; j++) { intergerInput = new
	 * Integer(markupInput[i][j]); fowardsIncidenceMatrix.add(j, intergerInput); } } }
	 */

	/**
	 * Create model from transformed PNML file
	 * 
	 * @author Ben Kirby, 10 Feb 2007
	 * @param filename
	 *            URI location of PNML
	 * 
	 * @author Edwin Chung This code is modified so that dataLayer objects can
	 *         be created outside the GUI
	 */

	public void createFromPNML(final Document PNMLDoc)
	{
		this.emptyPNML();
		Element element = null;
		Node node = null;
		NodeList nodeList = null;

		try
		{
			nodeList = PNMLDoc.getDocumentElement().getChildNodes();
			if (CreateGui.getApp() != null)
			{
				CreateGui.getApp().setMode(Constants.CREATING); // Notifies used
				// to indicate
				// new
				// instances.
			}

			for (int i = 0; i < nodeList.getLength(); i++)
			{
				node = nodeList.item(i);

				if (node instanceof Element)
				{
					element = (Element) node;
					if ("labels".equals(element.getNodeName()))
					{
						this.addAnnotation(this.createAnnotation(element));
					}
					else if ("place".equals(element.getNodeName()))
					{
						this.addPlace(this.createPlace(element));
					}
					else if ("transition".equals(element.getNodeName()))
					{
						this.addTransition(this.createTransition(element));
					}
					else if ("arc".equals(element.getNodeName()))
					{
						final Arc a = this.createArc(element);
						this.addArc(a);
						a.addWeightLabelToContainer();
					}
					else if ("stategroup".equals(element.getNodeName()))
					{
						this.addStateGroup(this.createStateGroup(element));
					}
				}
			}
			if (CreateGui.getApp() != null)
			{
				CreateGui.getApp().restoreMode();
			}

		}
		catch (final Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * Creates Incidence Matrix from current Petri-Net
	 * 
	 */
	private void createIncidenceMatrix()
	{

		this.createForwardIncidenceMatrix();
		this.createBackwardsIncidenceMatrix();
		this.incidenceMatrix = new PNMatrix(this.forwardsIncidenceMatrix.getArrayCopy());
		this.incidenceMatrix = this.incidenceMatrix.minus(this.backwardsIncidenceMatrix);
		this.incidenceMatrix.matrixChanged = false;

	}

	/**
	 * Creates Initial Markup Matrix from current Petri-Net
	 * 
	 */
	private void createInitialMarkingVector()
	{

		int placeNo = 0;
		final int placeSize = this.placesArray.size();
		this.initialMarkingVector = new int[placeSize];
		for (; placeNo < placeSize; placeNo++)
		{
			this.initialMarkingVector[placeNo] = ((Place) this.placesArray.get(placeNo)).getInitialMarking();
		}
		DataLayer.initialMarkingVectorChanged = false;
	}

	/**
	 * Creates all Petri-Net Matrices from current Petri-Net
	 * 
	 */
	private void createMatrices()
	{
		this.createForwardIncidenceMatrix();
		this.createBackwardsIncidenceMatrix();
		this.createIncidenceMatrix();
		this.createInitialMarkingVector();
		this.createCurrentMarkingVector();
	}

	/**
	 * Creates a Place object from a Place DOM Element
	 * 
	 * @param inputPlaceElement
	 *            Input Place DOM Element
	 * @return Place Object
	 */
	private Place createPlace(final Element inputPlaceElement)
	{
		double positionXInput = 0;
		double positionYInput = 0;
		String idInput = null;
		String nameInput = null;
		double nameOffsetYInput = 0;
		double nameOffsetXInput = 0;
		int initialMarkingInput = 0;
		double markingOffsetXInput = 0;
		double markingOffsetYInput = 0;
		boolean taggedPlace;

		final String positionXTempStorage = inputPlaceElement.getAttribute("positionX");
		final String positionYTempStorage = inputPlaceElement.getAttribute("positionY");
		final String idTempStorage = inputPlaceElement.getAttribute("id");
		final String nameTempStorage = inputPlaceElement.getAttribute("name");
		final String nameOffsetYTempStorage = inputPlaceElement.getAttribute("nameOffsetX");
		final String nameOffsetXTempStorage = inputPlaceElement.getAttribute("nameOffsetY");
		final String initialMarkingTempStorage = inputPlaceElement.getAttribute("initialMarking");
		final String markingOffsetXTempStorage = inputPlaceElement.getAttribute("markingOffsetX");
		final String markingOffsetYTempStorage = inputPlaceElement.getAttribute("markingOffsetY");
		final String taggedTempStorage = inputPlaceElement.getAttribute("tagged");

		taggedPlace = !(taggedTempStorage.length() == 0 || taggedTempStorage.length() == 5);

		if (positionXTempStorage.length() > 0)
		{
			positionXInput = Double.valueOf(positionXTempStorage).doubleValue() *
								(false ? this.DISPLAY_SCALE_FACTORX : 1) +
								(false ? this.DISPLAY_SHIFT_FACTORX : 1);
		}
		if (positionYTempStorage.length() > 0)
		{
			positionYInput = Double.valueOf(positionYTempStorage).doubleValue() *
								(false ? this.DISPLAY_SCALE_FACTORY : 1) +
								(false ? this.DISPLAY_SHIFT_FACTORY : 1);
		}

		if (idTempStorage.length() > 0)
		{
			idInput = idTempStorage;
		}
		else if (nameTempStorage.length() > 0)
		{
			idInput = nameTempStorage;
		}

		if (nameTempStorage.length() > 0)
		{
			nameInput = nameTempStorage;
		}
		else if (idTempStorage.length() > 0)
		{
			nameInput = idTempStorage;
		}

		if (nameOffsetYTempStorage.length() > 0)
		{
			nameOffsetXInput = Double.valueOf(nameOffsetYTempStorage).doubleValue();
		}
		if (nameOffsetXTempStorage.length() > 0)
		{
			nameOffsetYInput = Double.valueOf(nameOffsetXTempStorage).doubleValue();
		}
		if (initialMarkingTempStorage.length() > 0)
		{
			initialMarkingInput = Integer.valueOf(initialMarkingTempStorage).intValue();
		}
		if (markingOffsetXTempStorage.length() > 0)
		{
			markingOffsetXInput = Double.valueOf(markingOffsetXTempStorage).doubleValue();
		}
		if (markingOffsetYTempStorage.length() > 0)
		{
			markingOffsetYInput = Double.valueOf(markingOffsetYTempStorage).doubleValue();
			// Pete: Removed colour properties
		}

		positionXInput += Constants.PLACE_TRANSITION_HEIGHT / 2 - 1;
		positionYInput += Constants.PLACE_TRANSITION_HEIGHT / 2 - 1;

		this.setMatrixChanged();
		return new Place(	positionXInput,
							positionYInput,
							idInput,
							nameInput,
							nameOffsetXInput,
							nameOffsetYInput,
							initialMarkingInput,
							markingOffsetXInput,
							markingOffsetYInput,
							taggedPlace);
	}

	/**
	 * Creates a StateGroup object from a DOM element
	 * 
	 * @param inputStateGroupElement
	 *            input state group DOM Element
	 * @return StateGroup Object
	 */
	private StateGroup createStateGroup(final Element inputStateGroupElement)
	{
		// Create the state group with name and id
		final String id = inputStateGroupElement.getAttribute("id");
		final String name = inputStateGroupElement.getAttribute("name");
		final StateGroup newGroup = new StateGroup(id, name);

		Node node = null;
		NodeList nodelist = null;
		StringTokenizer tokeniser;
		nodelist = inputStateGroupElement.getChildNodes();

		// If this state group contains states then add them
		if (nodelist.getLength() > 0)
		{
			for (int i = 1; i < nodelist.getLength() - 1; i++)
			{
				node = nodelist.item(i);
				if (node instanceof Element)
				{
					final Element element = (Element) node;
					if ("statecondition".equals(element.getNodeName()))
					{
						// Loads the condition in the form "P0 > 4"
						final String condition = element.getAttribute("value");

						// Now we tokenise the elements of the condition (i.e.
						// "P0" ">" "4") to create a state
						tokeniser = new StringTokenizer(condition);
						String place_id = tokeniser.nextToken();
						String operator = tokeniser.nextToken();
						String value = tokeniser.nextToken();
						System.out.println("\n### "+place_id+"  "+operator+"  "+value);
						if(place_id.equals("tagged_location")){
							
							System.out.println("\n in if");
							place_id = "P"+value;
							operator = "T";
							value = "T";
							
						}
						newGroup.addState(place_id, operator, value);
					}
				}
			}
		}

		return newGroup;
	}

	/**
	 * Creates a Transition object from a Transition DOM Element
	 * 
	 * @param inputTransitionElement
	 *            Input Transition DOM Element
	 * @return Transition Object
	 */
	private Transition createTransition(final Element inputTransitionElement)
	{
		double positionXInput = 0;
		double positionYInput = 0;
		String idInput = null;
		String nameInput = null;
		double nameOffsetYInput = 0;
		double nameOffsetXInput = 0;
		double rate = 1.0;
		boolean timedTransition, infiniteServer;
		int angle = 0;
		final String positionXTempStorage = inputTransitionElement.getAttribute("positionX");
		final String positionYTempStorage = inputTransitionElement.getAttribute("positionY");
		final String idTempStorage = inputTransitionElement.getAttribute("id");
		final String nameTempStorage = inputTransitionElement.getAttribute("name");
		final String nameOffsetYTempStorage = inputTransitionElement.getAttribute("nameOffsetX");
		final String nameOffsetXTempStorage = inputTransitionElement.getAttribute("nameOffsetY");
		String nameRate = inputTransitionElement.getAttribute("rate");
		final String nameTimed = inputTransitionElement.getAttribute("timed");
		final String nameInfiniteServer = inputTransitionElement.getAttribute("infiniteServer");
		final String nameAngle = inputTransitionElement.getAttribute("angle");

		/*
		 * wjk - a useful little routine to display all attributes of a
		 * transition for (int i=0; ; i++) { Object obj =
		 * inputTransitionElement.getAttributes().item(i); if (obj == null)
		 * break; System.out.println("Attribute " + i + " = " + obj.toString()); }
		 */

		if (nameTimed.length() == 0)
		{
			timedTransition = false;
		}
		else if (nameTimed.length() == 5)
		{
			timedTransition = false;
		}
		else
		{
			timedTransition = true;
		}

		infiniteServer = !(nameInfiniteServer.length() == 0 || nameInfiniteServer.length() == 5);

		if (positionXTempStorage.length() > 0)
		{
			positionXInput = Double.valueOf(positionXTempStorage).doubleValue() *
								(false ? this.DISPLAY_SCALE_FACTORX : 1) +
								(false ? this.DISPLAY_SHIFT_FACTORX : 1);
		}
		if (positionYTempStorage.length() > 0)
		{
			positionYInput = Double.valueOf(positionYTempStorage).doubleValue() *
								(false ? this.DISPLAY_SCALE_FACTORY : 1) +
								(false ? this.DISPLAY_SHIFT_FACTORY : 1);
		}
		if (idTempStorage.length() > 0)
		{
			idInput = idTempStorage;
		}
		else if (nameTempStorage.length() > 0)
		{
			idInput = nameTempStorage;
		}

		if (nameTempStorage.length() > 0)
		{
			nameInput = nameTempStorage;
		}
		else if (idTempStorage.length() > 0)
		{
			nameInput = idTempStorage;
		}

		if (nameOffsetXTempStorage.length() > 0)
		{
			nameOffsetXInput = Double.valueOf(nameOffsetXTempStorage).doubleValue();
		}

		if (nameOffsetYTempStorage.length() > 0)
		{
			nameOffsetYInput = Double.valueOf(nameOffsetYTempStorage).doubleValue();
		}

		if (nameRate.length() == 0)
		{
			nameRate = "1.0";
		}
		if (nameRate != "1.0")
		{
			rate = Double.valueOf(nameRate).doubleValue();
		}
		else
		{
			rate = 1.0;
		}

		if (nameAngle.length() > 0)
		{
			angle = Integer.valueOf(nameAngle).intValue();
		}
		// Pete: Removed colour properties
		positionXInput += Constants.PLACE_TRANSITION_HEIGHT / 2 - 1;
		positionYInput += Constants.PLACE_TRANSITION_HEIGHT / 2 - 1;
		this.setMatrixChanged();

		return new Transition(	positionXInput,
								positionYInput,
								idInput,
								nameInput,
								nameOffsetXInput,
								nameOffsetYInput,
								rate,
								timedTransition,
								infiniteServer,
								angle);
	}

	/**
	 * Empty all attributes, turn into empty Petri-Net
	 */
	private void emptyPNML()
	{
		DataLayer.pnmlName = null;
		this.placesArray = null;
		this.transitionsArray = null;
		this.arcsArray = null;
		this.tokensArray = null;
		this.labelsArray = null;
		this.arrowsArray = null;
		this.changeArrayList = null;
		this.initialMarkingVector = null;
		this.forwardsIncidenceMatrix = null;
		this.backwardsIncidenceMatrix = null;
		this.incidenceMatrix = null;
		this.arcsMap = null;
		this.initializeMatrices();
	}

	/**
	 * This method will fire a random transition, and gives precedence to
	 * immediate transitions before considering "timed" transitions. The "rate"
	 * property of the transition is used as a weighting factor so the
	 * probability of selecting a transition is the rate of that transition
	 * divided by the sum of the weights of the other enabled transitions of its
	 * class. The "rate" property can now be used to give priority among several
	 * enabled, immediate transitions, or when there are no enabled, immediate
	 * transitions to give priority among several enabled, "timed" transitions.
	 * 
	 * Note: in spite of the name "timed" there is no probabilistic rate
	 * calculated -- just a weighting factor among similar transitions.
	 * 
	 * Changed by David Patterson Jan 2, 2006
	 * 
	 * Changed by David Patterson Apr 24, 2007 to clean up problems caused by
	 * fractional rates, and to speed up processing when only one transition of
	 * a kind is enabled.
	 * 
	 * Changed by David Patterson May 10, 2007 to properly handle fractional
	 * weights for immeditate transitions.
	 * 
	 * THe same logic is also used for timed transitions until the exponential
	 * distribution is added. When that happens, the code will only be used for
	 * immediate transitions.
	 */
	public Transition fireRandomTransition()
	{
		Transition result = null;
		Transition t;
		this.setEnabledTransitions();
		int transitionNo = 0;
		double rate = 0.0d;
		double sumOfImmedWeights = 0.0d;
		double sumOfTimedWeights = 0.0d;
		final ArrayList timedTransitions = new ArrayList(); // ArrayList<Transition>
		final ArrayList immedTransitions = new ArrayList(); // ArrayList<Transition>

		for (transitionNo = 0; transitionNo < this.transitionsArray.size(); transitionNo++)
		{
			t = (Transition) this.transitionsArray.get(transitionNo);
			rate = t.getRate();
			if (t.isEnabled() || t.isTaggedTokenEnabled())
			{
				if (t.isTimed()) // is it a timed transition
				{
					timedTransitions.add(t);
					sumOfTimedWeights += rate;
				}
				else
				// immediate transition
				{
					immedTransitions.add(t);
					sumOfImmedWeights += rate;
				}
			} // end of if isEnabled
		} // end of for transitionNo

		// Now, if there are immediate transitions, pick one
		// next block changed by David Patterson to fix bug
		int count = immedTransitions.size();
		switch (count)
		{
			case 0 : // no immediate transitions
				break; // skip out
			case 1 : // only one immediate transition
				result = (Transition) immedTransitions.get(0);
				break; // skip out
			default : // several immediate transitions
				double rval = sumOfImmedWeights * DataLayer.randomNumber.nextDouble();
				for (int index = 0; index < count; index++)
				{
					t = (Transition) immedTransitions.get(index);
					rval -= t.getRate();
					if (rval <= 0.0d)
					{
						result = t;
						break;
					}
				}
		}
		if (result == null) // no immediate transition found
		{
			count = timedTransitions.size(); // count of timed, enabled
			// transitions
			switch (count)
			{
				case 0 : // trouble! No enabled transition found
					break;
				case 1 : // only one timed transition
					result = (Transition) timedTransitions.get(0);
					break;
				default : // several timed transitions -- for now, pick one
					double rval = sumOfTimedWeights * DataLayer.randomNumber.nextDouble();
					for (int index = 0; index < count; index++)
					{
						t = (Transition) timedTransitions.get(index);
						rval -= t.getRate();
						if (rval <= 0.0d)
						{
							result = t;
							break;
						}
					}
			}
		}

		System.out.print("\n timed trans ");
		for(int i=0;i<timedTransitions.size();i++){
			
			System.out.print( ( (Transition)timedTransitions.get(i)  ).getId()  );
			
		}
		System.out.print("\n imme trans ");
		for(int i=0;i<immedTransitions.size();i++){
			
			System.out.print( ( (Transition)immedTransitions.get(i)  ).getId()  );
			
		} 
		
		if (result == null)
		{
			System.out.println("no random transition to fire");
		}
		else	
		{
			//this.createCurrentMarkingVector();
		}
		
		//this.resetEnabledTransitions();
		return result;
	} // end of method fireRandomTransition

/*	
 * fireRandomTransitionBackwards has not been used
 * 
 * 
	public void fireRandomTransitionBackwards()
	{
		this.setEnabledTransitionsBackwards();
		int transitionsSize = this.transitionsArray.size() * this.transitionsArray.size() *
								this.transitionsArray.size();
		int randomTransitionNumber = 0;
		Transition randomTransition = null;
		do
		{
			randomTransitionNumber = DataLayer.randomNumber.nextInt(this.transitionsArray.size());
			randomTransition = (Transition) this.transitionsArray.get(randomTransitionNumber);
			transitionsSize--;
			if (transitionsSize <= 0)
			{
				break;
			}
		} while (!randomTransition.isEnabled());
		this.fireTransitionBackwards(randomTransition);
// System.out.println("Random Fired Transition Backwards" +
// ((Transition)transitionsArray.get(randonTransition)).getId());
	}
*/
	
	
	
	/*
	 *  Fire a tagged token on the specified transition
	 */
	public void fireTaggedTransition(final Transition transition){

		int taggedPlaceNo=0;
		boolean fireTagged=false;
		
		if(this.previoulyTaggedPlace == null) this.previoulyTaggedPlace = new ArrayList();
		
		if (transition != null)
		{
			this.setEnabledTransitions();
			if (transition.isTaggedTokenEnabled() && this.placesArray != null)
			{
				
				final int transitionNo = this.transitionsArray.indexOf(transition);
				final String outputPlaceId = transition.getIdOfTaggedPlace();
				
				for (int placeNo = 0; placeNo < this.placesArray.size(); placeNo++)
				{
					Place currentPlace = ((Place) this.placesArray.get(placeNo));
					currentPlace.setCurrentMarking((this.currentMarkingVector[placeNo] + this.incidenceMatrix.get(placeNo,transitionNo)));
					
					// isTaggedTokenEnabled is true, so there must be an output place to tagged
					if(outputPlaceId == currentPlace.getId())taggedPlaceNo = placeNo;
					
					if( currentPlace.isTagged())
					{
	
						if (firedTagged == this.previoulyTaggedPlace.size())
						{
							previoulyTaggedPlace.add(currentPlace);
						    firedTagged++;
						} 
						else 
						{ 
							  previoulyTaggedPlace.set(firedTagged, currentPlace);
						  	  firedTagged++;
						  	  this.removeStoredTaggedPlace();
						}
						

						System.out.println("\n");
						for(int i=0;i<this.previoulyTaggedPlace.size(); i++){
							if(this.previoulyTaggedPlace.get(i) instanceof Place)System.out.println( ((Place) this.previoulyTaggedPlace.get(i)).getId() );
							else System.out.println(this.previoulyTaggedPlace.get(i));
						}

						fireTagged = true;
						currentPlace.setTagged(false);	
						
						
					}//end if currentPlace.istagged()

				}//end for
			}
		}//end transition!=null
		

		if(fireTagged)( (Place)this.placesArray.get( taggedPlaceNo)).setTagged(true);
		this.setMatrixChanged();
		
		
	}
	
	/*
	 * method to remove all the stored tagged place after animation
	 */
	  private void removeStoredTaggedPlace() {
		  
		  if(this.previoulyTaggedPlace!=null)
	  	  for (int count1 = firedTagged ; count1 < this.previoulyTaggedPlace.size(); count1++)
	  		previoulyTaggedPlace.remove(count1);
	  	  
	  	  //firedTagged=0;
		  
	  }
	
	
	/**
	 * Fire a specified transition, no affect if transtions not enabled
	 * 
	 * @param transition
	 *            Reference of specifiec Transition
	 */
	public void fireTransition(final Transition transition)
	{
		boolean fireUntaggedPossible = true; 
		
		if(this.previoulyTaggedPlace == null) this.previoulyTaggedPlace = new ArrayList();
		
		
		if (transition != null)
		{
			

			this.setEnabledTransitions();
			
			//check if it can fire untagged token
			if( transition.hasTaggedPlaceUntaggedArc()==1 || transition.hasTaggedArcAndPlace()==1 ){
				fireUntaggedPossible = false;
			}
	
			if (transition.isEnabled() && this.placesArray != null && fireUntaggedPossible)
			{

				if (firedTagged == this.previoulyTaggedPlace.size()) {
					//outOfBound = false;
					previoulyTaggedPlace.add(1);
				      firedTagged++;
				   } else { 
					   
					   //if(firedTagged==0)outOfBound = true;
					   					   
					   previoulyTaggedPlace.set(firedTagged, 1);
				  	      firedTagged++;
				  	      this.removeStoredTaggedPlace();

				   }

				
				System.out.println("\n");
				for(int i=0;i<this.previoulyTaggedPlace.size(); i++){
					if(this.previoulyTaggedPlace.get(i) instanceof Place)System.out.println( ((Place) this.previoulyTaggedPlace.get(i)).getId() );
					else System.out.println(this.previoulyTaggedPlace.get(i));
				}
				
				
				final int transitionNo = this.transitionsArray.indexOf(transition);
				for (int placeNo = 0; placeNo < this.placesArray.size(); placeNo++)
				{	
					((Place) this.placesArray.get(placeNo)).setCurrentMarking((this.currentMarkingVector[placeNo] + this.incidenceMatrix.get(placeNo,transitionNo)));				
				}
			}
		}
		this.setMatrixChanged();
	}

	
	public void fireTransitionForward(final Transition transition)
	{
			
		if (transition != null)
		{		
			this.setEnabledTransitions();

			if (  (transition.isEnabled()  || transition.isTaggedTokenEnabled()) && this.placesArray != null )
			{
				
				final String outputPlaceId = transition.getIdOfTaggedPlace();
				final int transitionNo = this.transitionsArray.indexOf(transition);
				for (int placeNo = 0; placeNo < this.placesArray.size(); placeNo++)
				{	
					
					final Place place = ((Place) this.placesArray.get(placeNo));
					
					System.out.print("\n***");
					if(this.previoulyTaggedPlace.get(firedTagged) instanceof Place)
						System.out.print( ( (Place) this.previoulyTaggedPlace.get(firedTagged)).getId());
					else System.out.print(this.previoulyTaggedPlace.get(firedTagged));
					
					if(place.isTagged())
						System.out.print(" current tagged place+ "+ place.getId());
					
					if(this.previoulyTaggedPlace.get(firedTagged) instanceof Place
							&& transition.isTaggedTokenEnabled())
					{
						
						System.out.println("\n**forwardFiredTagged");
						if(place.isTagged())place.setTagged(false);
						//( (Place)this.getTargetPlaceFromTransition(transition)).setTagged(true);						
						if(outputPlaceId == place.getId())( (Place)this.placesArray.get( placeNo)).setTagged(true);
						
					}
			
					place.setCurrentMarking((this.currentMarkingVector[placeNo] + this.incidenceMatrix.get(placeNo,transitionNo)));				
				}
			}
			firedTagged++;
		}
		
		this.setMatrixChanged();
	}
	
	
	
	/*
	 * TK - this method is never called Stores Initial Markup Matrix from
	 * current Petri-Net Markup
	 * 
	 * 
	 * public void storeInitialMarking(){ if (initialMarkupMatrixChanged)
	 * createMatrices(); int placeNo = 0; int placeSize = placesArray.size();
	 * initialMarkupMatrix = new int[placeSize]; for( ; placeNo < placeSize ;
	 * placeNo++) initialMarkupMatrix[placeNo] =
	 * ((Place)placesArray.get(placeNo)).getCurrentMarking(); }
	 */

	/*
	 * TK - this method is never called Restores Initial Markup Matrix to
	 * current Petri-Net Markup
	 * 
	 * 
	 * public void restoreInitialMarking(){ if (initialMarkupMatrixChanged)
	 * createMatrices(); int placeNo = 0; int placeSize = placesArray.size();
	 * for( ; placeNo < placeSize ; placeNo++) { Place place =
	 * ((Place)placesArray.get(placeNo)); if(place != null) {
	 * place.setCurrentMarking(initialMarkupMatrix[placeNo]); setChanged();
	 * setMatrixChanged(); notifyObservers(place); } } }
	 */

	public Place getTargetPlaceFromTransition (final Transition transition){
		
		for(int i=0;i<this.arcsArray.size();i++){
			
			final Arc arc = ((Arc) arcsArray.get(i));
			if( arc.getSource() instanceof Transition && arc.getSource() == transition )
				return ((Place)arc.getTarget());
			
			
		}
		return null;
	}
	
	
	public void fireTransitionBackwards(final Transition transition)
	{
		
		if (transition != null)
		{
			
			
			
			boolean backFiredTagged = false;
			this.setEnabledTransitionsBackwards();
			if (transition.isEnabled() && this.placesArray != null)
			{
				
				final int transitionNo = this.transitionsArray.indexOf(transition);
				
			
				for (int placeNo = 0; placeNo < this.placesArray.size(); placeNo++)
				{
					final Place current = ((Place) this.placesArray.get(placeNo));
					
					
					System.out.print("\n***");
					if(this.previoulyTaggedPlace.get(firedTagged-1) instanceof Place)
						System.out.print( ( (Place) this.previoulyTaggedPlace.get(firedTagged-1)).getId());
					else System.out.print(this.previoulyTaggedPlace.get(firedTagged-1));
					
					if(current.isTagged())
						System.out.print(" current tagged place+ "+ current.getId());
					
					if(this.getTargetPlaceFromTransition(transition)==current )
						System.out.print(" last expression true");
					
	
					if(this.previoulyTaggedPlace.get(firedTagged-1) instanceof Place &&
							current.isTagged() && 
							this.getTargetPlaceFromTransition(transition)==current )
					{
						
						
						backFiredTagged = true;
						//((Transition) arc.getTarget()).setTaggedTokenEnabled(false);
						transition.setTaggedTokenEnabled(true);
						current.setTagged(false);
						
						System.out.println("\n***back fired tagged");
						
						
						for(int i=0;i<this.arcsArray.size();i++)
						{
							final Arc arc = (Arc)this.arcsArray.get(i);

							if(arc.getSource() == current)
							{	System.out.println("arc got source");
							
								if(arc.getTarget()!=null && arc.getTarget() instanceof Transition)
								{
									if( ((Transition) arc.getTarget()).isTaggedTokenEnabled() && ((Transition) arc.getTarget())!=transition )
									{
										//backFiredTagged = true;
										((Transition) arc.getTarget()).setTaggedTokenEnabled(false);
										//transition.setTaggedTokenEnabled(true);
										//current.setTagged(false);
										
										//System.out.println("\n***back fired tagged");
										//System.out.println("\ntransition was taggedEnabled is " +((Transition) arc.getTarget()).getId());
										//System.out.println("\ntransition is taggedEnabled is " +transition.getId());
										
									}	
								}	
							}	
						}
						
					}
					
					current.setCurrentMarking((this.currentMarkingVector[placeNo] - this.incidenceMatrix.get(	placeNo,
																																				transitionNo)));
				}
			}
			
			if(backFiredTagged && firedTagged>0){
				
				( (Place)this.previoulyTaggedPlace.get(firedTagged-1)).setTagged(true);

			}
			firedTagged--;
	
			
		}
		
		
		
		System.out.println("\n firedTagged = "+ firedTagged+" previoulytaggedplace size" + this.previoulyTaggedPlace.size()+   "\n");
		for(int i=0;i<this.previoulyTaggedPlace.size(); i++){
			if(this.previoulyTaggedPlace.get(i) instanceof Place)System.out.println( ((Place) this.previoulyTaggedPlace.get(i)).getId() );
			else System.out.println(this.previoulyTaggedPlace.get(i));
		}
		
		this.setMatrixChanged();
	}

	/**
	 * Return the Arc called arcName from the Petri-Net
	 * 
	 * @param arcName
	 *            Name of Arc object to return
	 * @return The first Arc object found with a name equal to arcName
	 */
	public Arc getArc(final String arcName)
	{
		Arc returnArc = null;

		if (this.arcsArray != null)
		{
			if (this.arcsArray != null)
			{
				for (int i = 0; i < this.arcsArray.size(); i++)
				{
					if (arcName.equalsIgnoreCase(((Arc) this.arcsArray.get(i)).getId()))
					{
						returnArc = (Arc) this.arcsArray.get(i);
					}
				}
			}
		}

		return returnArc;
	}

	/**
	 * Get an List of all the Arcs objects in the Petri-Net
	 * 
	 * @return An List of all the Arc objects
	 */
	public Arc[] getArcs()
	{
		final Arc[] returnArray = new Arc[this.arcsArray.size()];

		for (int i = 0; i < this.arcsArray.size(); i++)
		{
			returnArray[i] = (Arc) this.arcsArray.get(i);
		}

		return returnArray;
	}

	/*
	 * TK - this method is never called Fire a specified transition, no affect
	 * if transtions not enabled @param transitionNo Position of Transition in
	 * internal ArrayList
	 * 
	 * private void fireTransition(int transitionNo) { setEnabledTransitions();
	 * if(((Transition)transitionsArray.get(transitionNo)).isEnabled() &&
	 * placesArray != null){ for(int placeNo = 0 ; placeNo < placesArray.size() ;
	 * placeNo++) {
	 * ((Place)placesArray.get(placeNo)).setCurrentMarking((currentMarkupMatrix[placeNo] +
	 * incidenceMatrix.get(placeNo, transitionNo))); } } setMatrixChanged(); }
	 */

	/**
	 * Return the Backward Incidence Matrix for the Petri-Net
	 * 
	 * @return The Backward Incidence Matrix for the Petri-Net
	 */
	public int[][] getBackwardsIncidenceMatrix()
	{
		if (this.backwardsIncidenceMatrix == null || this.backwardsIncidenceMatrix.matrixChanged)
		{
			this.createBackwardsIncidenceMatrix();
		}
		return this.backwardsIncidenceMatrix != null ? this.backwardsIncidenceMatrix.getArrayCopy() : null;
	}

	/**
	 * Return the Initial Markup Matrix for the Petri-Net
	 * 
	 * @return The Initial Markup Matrix for the Petri-Net
	 */
	public int[] getCurrentMarkingVector()
	{
		if (DataLayer.currentMarkingVectorChanged)
		{
			this.createCurrentMarkingVector();
		}
		return this.currentMarkingVector;
	}

	/**
	 * Return the Forward Incidence Matrix for the Petri-Net
	 * 
	 * @return The Forward Incidence Matrix for the Petri-Net
	 */
	public int[][] getForwardsIncidenceMatrix()
	{
		if (this.forwardsIncidenceMatrix == null || this.forwardsIncidenceMatrix.matrixChanged)
		{
			this.createForwardIncidenceMatrix();
		}
		return this.forwardsIncidenceMatrix != null ? this.forwardsIncidenceMatrix.getArrayCopy() : null;
	}

	/**
	 * Return the Incidence Matrix for the Petri-Net
	 * 
	 * @return The Incidence Matrix for the Petri-Net
	 */
	public int[][] getIncidenceMatrix()
	{
		if (this.incidenceMatrix == null || this.incidenceMatrix.matrixChanged)
		{
			this.createIncidenceMatrix();
		}
		return this.incidenceMatrix != null ? this.incidenceMatrix.getArrayCopy() : null;
	}

	/**
	 * Return the Initial Markup Matrix for the Petri-Net
	 * 
	 * @return The Initial Markup Matrix for the Petri-Net
	 */
	public int[] getInitialMarkingVector()
	{
		if (DataLayer.initialMarkingVectorChanged)
		{
			this.createInitialMarkingVector();
		}
		return this.initialMarkingVector;
	}

	/**
	 * Get a List of all the net-level NameLabel objects in the Petri-Net
	 * 
	 * @return A List of all the net-level (as opposed to element-specific)
	 *         label objects
	 */
	public AnnotationNote[] getLabels()
	{
		final AnnotationNote[] returnArray = new AnnotationNote[this.labelsArray.size()];

		for (int i = 0; i < this.labelsArray.size(); i++)
		{
			returnArray[i] = (AnnotationNote) this.labelsArray.get(i);
		}

		return returnArray;
	}

	/**
	 * Get position of Petri-Net Object in ArrayList of given Petri-Net Object's
	 * type
	 * 
	 * @param pnObject
	 *            PlaceTransitionObject to get the position of
	 * @return Position (-1 if not present) of Petri-Net Object in ArrayList of
	 *         given Petri-Net Object's type
	 */
	private int getListPosition(final PetriNetObject pnObject)
	{
		if (this.setPetriNetObjectArrayList(pnObject))
		{
			return this.changeArrayList.indexOf(pnObject);
		}
		else
		{
			return -1;
		}
	}

	/* wjk added 03/10/2007 */
	/**
	 * Get the current marking of the Petri net
	 * 
	 * @return The current marking of the Petri net
	 */
	public int[] getMarking()
	{
		final int[] result = new int[this.placesArray.size()];

		for (int i = 0; i < this.placesArray.size(); i++)
		{
			result[i] = ((Place) this.placesArray.get(i)).getCurrentMarking();
		}

		return result;
	}

	/**
	 * Returns an iterator of all PetriNetObjects - the order of these cannot be
	 * guaranteed.
	 * 
	 * @return An iterator of all PetriNetObjects
	 */
	public Iterator getPetriNetObjects()
	{
		final ArrayList all = new ArrayList(this.placesArray);
		all.addAll(this.transitionsArray);
		all.addAll(this.arcsArray);
		all.addAll(this.tokensArray);
		all.addAll(this.labelsArray);
		all.addAll(this.arrowsArray);

		return all.iterator();
	}

	/**
	 * Return the Place called placeName from the Petri-Net
	 * 
	 * @param placeNo
	 *            No of Place object to return
	 * @return The Place object
	 */
	public Place getPlace(final int placeNo)
	{
		Place returnPlace = null;

		if (this.placesArray != null)
		{
			if (placeNo < this.placesArray.size())
			{
				returnPlace = (Place) this.placesArray.get(placeNo);
			}
		}

		return returnPlace;
	}

	/**
	 * Return the Place called placeName from the Petri-Net
	 * 
	 * @param placeId
	 *            ID of Place object to return
	 * @return The first Place object found with an ID equal to placeId
	 */
	public Place getPlaceById(final String placeId)
	{
		Place returnPlace = null;

		if (this.placesArray != null)
		{
			if (placeId != null)
			{
				for (int i = 0; i < this.placesArray.size(); i++)
				{
					if (placeId.equalsIgnoreCase(((Place) this.placesArray.get(i)).getId()))
					{
						returnPlace = (Place) this.placesArray.get(i);
					}
				}
			}
		}

		return returnPlace;
	}

	/**
	 * Return the Place called placeName from the Petri-Net
	 * 
	 * @param placeName
	 *            Name of Place object to return
	 * @return The first Place object found with a name equal to placeName
	 */
	public Place getPlaceByName(final String placeName)
	{
		Place returnPlace = null;

		if (this.placesArray != null)
		{
			if (placeName != null)
			{
				for (int i = 0; i < this.placesArray.size(); i++)
				{
					if (placeName.equalsIgnoreCase(((Place) this.placesArray.get(i)).getName()))
					{
						returnPlace = (Place) this.placesArray.get(i);
					}
				}
			}
		}

		return returnPlace;
	}

	/**
	 * Get an List of all the Place objects in the Petri-Net
	 * 
	 * @return A List of all the Place objects
	 */
	public Place[] getPlaces()
	{
		final Place[] returnArray = new Place[this.placesArray.size()];

		for (int i = 0; i < this.placesArray.size(); i++)
		{
			returnArray[i] = (Place) this.placesArray.get(i);
		}

		return returnArray;
	}

	public int getPlacesCount()
	{

		if (this.placesArray == null)
		{
			return 0;
		}
		else
		{
			return this.placesArray.size();
		}
	}

	/**
	 * Return the PlaceTransitionObject called ptoName from the Petri-Net
	 * 
	 * @param ptoId
	 *            Id of PlaceTransitionObject object to return
	 * @return The first Arc PlaceTransitionObject found with a name equal to
	 *         ptoName
	 */
	public PlaceTransitionObject getPlaceTransitionObject(final String ptoId)
	{

		if (ptoId != null)
		{
			if (this.getPlaceById(ptoId) != null)
			{
				return this.getPlaceById(ptoId);
			}
			else if (this.getTransitionById(ptoId) != null)
			{
				return this.getTransitionById(ptoId);
			}
		}

		return null;
	}

	public StateGroup[] getStateGroups()
	{
		final StateGroup[] returnArray = new StateGroup[this.stateGroups.size()];

		for (int i = 0; i < this.stateGroups.size(); i++)
		{
			returnArray[i] = (StateGroup) this.stateGroups.get(i);
		}

		return returnArray;
	}

	public ArrayList<StateGroup> getStateGroupsArray()
	{
		return this.stateGroups;
	}

	/**
	 * Return the Transition called transitionName from the Petri-Net
	 * 
	 * @param transitionNo
	 *            No of Transition object to return
	 * @return The Transition object
	 */
	public Transition getTransition(final int transitionNo)
	{
		Transition returnTransition = null;

		if (this.transitionsArray != null)
		{
			if (transitionNo < this.transitionsArray.size())
			{
				returnTransition = (Transition) this.transitionsArray.get(transitionNo);
			}
		}

		return returnTransition;
	}

	/**
	 * Return the Transition called transitionName from the Petri-Net
	 * 
	 * @param transitionID
	 *            ID of Transition object to return
	 * @return The first Transition object found with a ID equal to
	 *         transitionName
	 */
	public Transition getTransitionById(final String transitionID)
	{
		Transition returnTransition = null;

		if (this.transitionsArray != null)
		{
			if (transitionID != null)
			{
				for (int i = 0; i < this.transitionsArray.size(); i++)
				{
					if (transitionID.equalsIgnoreCase(((Transition) this.transitionsArray.get(i)).getId()))
					{
						returnTransition = (Transition) this.transitionsArray.get(i);
					}
				}
			}
		}

		return returnTransition;
	}

	/**
	 * Return the Transition called transitionName from the Petri-Net
	 * 
	 * @param transitionName
	 *            Name of Transition object to return
	 * @return The first Transition object found with a name equal to
	 *         transitionName
	 */
	public Transition getTransitionByName(final String transitionName)
	{
		Transition returnTransition = null;

		if (this.transitionsArray != null)
		{
			if (transitionName != null)
			{
				for (int i = 0; i < this.transitionsArray.size(); i++)
				{
					if (transitionName.equalsIgnoreCase(((Transition) this.transitionsArray.get(i)).getName()))
					{
						returnTransition = (Transition) this.transitionsArray.get(i);
					}
				}
			}
		}

		return returnTransition;
	}

	/**
	 * Get an List of all the Transition objects in the Petri-Net
	 * 
	 * @return An List of all the Transition objects
	 */
	public Transition[] getTransitions()
	{
// setEnabledTransitions();
		final Transition[] returnArray = new Transition[this.transitionsArray.size()];

		for (int i = 0; i < this.transitionsArray.size(); i++)
		{
			returnArray[i] = (Transition) this.transitionsArray.get(i);
		}

		return returnArray;
	}

	public int getTransitionsCount()
	{

		if (this.transitionsArray == null)
		{
			return 0;
		}
		else
		{
			return this.transitionsArray.size();
		}
	}

	/**
	 * 
	 * Return a URI for the PNML file for the Petri-Net
	 * 
	 * @return A DOM for the Petri-Net
	 */
	public String getURI()
	{
		return DataLayer.pnmlName;
	}

	/**
	 * Initialize Arrays
	 */
	private void initializeMatrices()
	{
		this.placesArray = new ArrayList();
		this.transitionsArray = new ArrayList();
		this.arcsArray = new ArrayList();
		this.tokensArray = new ArrayList();
		this.arrowsArray = new ArrayList();
		this.labelsArray = new ArrayList();
		this.stateGroups = new ArrayList();
		this.initialMarkingVector = null;
		this.forwardsIncidenceMatrix = null;
		this.backwardsIncidenceMatrix = null;
		this.incidenceMatrix = null;

		// may as well do the hashtable here as well
		this.arcsMap = new Hashtable();
	}

	/*
	 * TK - this method is never called Return the Arc called arcName from the
	 * Petri-Net
	 * 
	 * @param arcName Name of Arc object to return @return The first Arc object
	 * found with a name equal to arcName
	 * 
	 * public Arc getArcWithSource(PetriNetObject arcName) { Arc returnArc =
	 * null;
	 * 
	 * if(arcsArray != null) { for(int i = 0 ; i < arcsArray.size(); i++) {
	 * if(arcsArray.get(i) != null) { Arc arc = (Arc)arcsArray.get(i);
	 * if(arc.getTarget() != null) { if(arcName.equals(arc.getSource())) {
	 * returnArc = arc; } } } } }
	 * 
	 * return returnArc; }
	 */

	/*
	 * TK - this method is never called Return the Arc called arcName from the
	 * Petri-Net
	 * 
	 * @param arcName Name of Arc object to return @return The first Arc object
	 * found with a name equal to arcName
	 * 
	 * public Arc getArcWithTarget(PetriNetObject arcName) { Arc returnArc =
	 * null;
	 * 
	 * if(arcsArray != null) { for(int i = 0 ; i < arcsArray.size(); i++) {
	 * if(arcsArray.get(i) != null) { Arc arc = (Arc)arcsArray.get(i);
	 * if(arc.getTarget() != null) { if(arcName.equals(arc.getTarget())) {
	 * returnArc = arc; } } } } }
	 * 
	 * return returnArc; }
	 */

	/** prints out a brief representation of the dataLayer object */
	public void print()
	{
		System.out.println("No of Places = " + this.placesArray.size() + "\"");
		System.out.println("No of Transitions = " + this.transitionsArray.size() + "\"");
		System.out.println("No of Arcs = " + this.arcsArray.size() + "\"");
		System.out.println("No of Labels = " + this.labelsArray.size() +
							"\" (Model View Controller Design Pattern)");
	}

	// ######################################################################################
	/**
	 * Removes the specified object from the appropriate ArrayList of objects.
	 * All observers are notified of this change.
	 * 
	 * @param pnObject
	 *            The PetriNetObject to be removed.
	 */

	public void removePetriNetObject(final PetriNetObject pnObject)
	{
		boolean didSomething = false;
		ArrayList attachedArcs = null;

		// System.out.println("removing: " + pnObject.getClass().getName());

		if (this.setPetriNetObjectArrayList(pnObject))
		{
			didSomething = this.changeArrayList.remove(pnObject);

			// we want to remove all attached arcs also
			if (pnObject instanceof PlaceTransitionObject)
			{

				if ((ArrayList) this.arcsMap.get(pnObject) != null)
				{

					// get the list of attached arcs for the object we are
					// removing
					attachedArcs = (ArrayList) this.arcsMap.get(pnObject);

					// remove the object we are removing from the arcsMap
					Arc removeArc;

					// iterate over all the attached arcs, removing them all
					while (!attachedArcs.isEmpty())
					{
						removeArc = (Arc) attachedArcs.get(0);
// removePetriNetObject(removeArc);
						removeArc.delete();

					}
					this.arcsMap.remove(pnObject);
				}
			}
			else if (pnObject instanceof Arc)
			{

				// get source and target of the arc
				PlaceTransitionObject attached = ((Arc) pnObject).getSource();

				if (attached != null)
				{
					final ArrayList a = (ArrayList) this.arcsMap.get(attached);
					if (a != null)
					{
						a.remove(pnObject);
					}
					attached.removeFromArc((Arc) pnObject);
					if (attached instanceof Transition)
					{
						((Transition) attached).removeArcCompareObject((Arc) pnObject);
						// attached.updateConnected(); ? causing null pointer
						// exceptions
					}
				}

				attached = ((Arc) pnObject).getTarget();

				if (attached != null)
				{
					((ArrayList) this.arcsMap.get(attached)).remove(pnObject);
					attached.removeToArc((Arc) pnObject);
					if (attached instanceof Transition)
					{
						((Transition) attached).removeArcCompareObject((Arc) pnObject);
					}
					attached.updateConnected();
				}
			}

			if (didSomething)
			{
				this.setChanged();
				this.setMatrixChanged();
				// notifyObservers(pnObject.getBounds());
				this.notifyObservers(pnObject);
			}
		}
		// we reset to null so that the wrong ArrayList can't get added to
		this.changeArrayList = null;
		this.validated = false;
	}

	/**
	 * This method removes a state group from the arrayList
	 * 
	 * @param SGObject
	 *            The State Group objet to be removed
	 */
	public void removeStateGroup(final StateGroup SGObject)
	{
		this.stateGroups.remove(SGObject);
	}

	public void resetEnabledTransitions()
	{
		if (DataLayer.currentMarkingVectorChanged)
		{
			this.createMatrices();
		}
		if (this.transitionsArray != null && this.placesArray != null)
		{
			for (int transitionNo = 0; transitionNo < this.transitionsArray.size(); transitionNo++)
			{
				final Transition transition = (Transition) this.transitionsArray.get(transitionNo);
				if (transition != null)
				{
					int test = this.placesArray.size();
					for (int placeNo = 0; placeNo < this.placesArray.size(); placeNo++)
					{
						if (this.backwardsIncidenceMatrix.get(placeNo, transitionNo) <= this.currentMarkingVector[placeNo])
						{
							test--;
						}
					}
					if (test <= 0)
					{
						transition.setEnabled(false);
					}
					else
					{
						transition.setEnabled(false);
					}
					
					if(transition.isEnabled() && transition.hasTaggedArcAndPlace()==1)
						transition.setEnabled(false);  
					
					
					this.setChanged();
					this.notifyObservers(transition);
				}
			}
		}
	}

	/**
	 * Restores To previous Stored Markup
	 */
	public void restoreState()
	{
		if (this.markingVectorAnimationStorage != null)
		{
			int placeNo = 0;
			final int placeSize = this.placesArray.size();
			for (; placeNo < placeSize; placeNo++)
			{
				final Place place = (Place) this.placesArray.get(placeNo);
				if (place != null)
				{
					final int temp = this.markingVectorAnimationStorage[placeNo];
					place.setCurrentMarking(temp);
					
					place.setTagged(false);
					if(placeNo == originalTaggedPlace) place.setTagged(true);
					this.setChanged();
					this.notifyObservers(place);
					this.setMatrixChanged();
				}
			}
		}
		
		firedTagged=0;		
		this.removeStoredTaggedPlace();
		
	}

	/*
	 * Returns an iterator for the transitions array. Used by Animator.class to
	 * set all enabled transitions to highlighted
	 */
	public Iterator returnTransitions()
	{
// System.out.println("returnTransitionsIterator called");
		return this.transitionsArray.iterator();
	}


	/**
	 * Determines whether all transitions are enabled and sets the correct value
	 * of the enabled boolean
	 */
	public void setEnabledTransitions()
	{

		if (DataLayer.currentMarkingVectorChanged)
		{
			this.createMatrices();
		}

		// wjk 03/10/2007 - try this instead? it should correctly take account
		// of the fact
		// that immediate transitions have priority over timed ones
		final boolean[] enabled = StateSpaceGenerator.getTransitionEnabledStatusArray(	this,
																						this.getMarking(),
																						false);
		for (int i = 0; i < enabled.length; i++)
		{
			final Transition t = this.getTransition(i);
			this.setChanged();
			this.notifyObservers(t);
			t.setEnabled(enabled[i]);		
			t.setTaggedTokenEnabled(false);		
			if(t.hasTaggedArcAndPlace()>0  && t.isEnabled()){
				//System.out.println("\n transition "+t.getId()+"can fire tagged token");
				t.setTaggedTokenEnabled(true);
				if(t.hasTaggedArcAndPlace()==1)t.setEnabled(false);
			}
			
			if(t.isEnabled() && t.hasTaggedPlaceUntaggedArc()==1)t.setEnabled(false);
			
			
		}

		/*
		 * if(transitionsArray != null && placesArray != null) { for(int
		 * transitionNo = 0 ; transitionNo < transitionsArray.size() ;
		 * transitionNo++) { Transition transition =
		 * (Transition)transitionsArray.get(transitionNo); if(transition !=
		 * null) { int test = placesArray.size(); for(int placeNo = 0 ; placeNo <
		 * placesArray.size() ; placeNo++) {
		 * if(backwardsIncidenceMatrix.get(placeNo, transitionNo) <=
		 * currentMarkupMatrix[placeNo]) { test--; } } if(test <= 0) {
		 * transition.setEnabled(true); setChanged();
		 * notifyObservers(transition); } else { transition.setEnabled(false);
		 * setChanged(); notifyObservers(transition); } } } }
		 */
	}

	/**
	 * Determines whether all transitions are enabled and sets the correct value
	 * of the enabled boolean
	 */
	private void setEnabledTransitionsBackwards()
	{
		if (DataLayer.currentMarkingVectorChanged)
		{
			this.createMatrices();
		}
		if (this.transitionsArray != null && this.placesArray != null)
		{
			for (int transitionNo = 0; transitionNo < this.transitionsArray.size(); transitionNo++)
			{
				final Transition transition = (Transition) this.transitionsArray.get(transitionNo);
				if (transition != null)
				{
					int test = this.placesArray.size();
					for (int placeNo = 0; placeNo < this.placesArray.size(); placeNo++)
					{
						if (this.forwardsIncidenceMatrix.get(placeNo, transitionNo) <= this.currentMarkingVector[placeNo])
						{
							test--;
						}
					}
					if (test <= 0)
					{
						
						transition.setEnabled(true);
					}
					else
					{
						transition.setEnabled(false);
					}
					this.setChanged();
					this.notifyObservers(transition);
				}
			}
		}
	}

	private void setMatrixChanged()
	{

		if (this.forwardsIncidenceMatrix != null)
		{
			this.forwardsIncidenceMatrix.matrixChanged = true;
		}
		if (this.backwardsIncidenceMatrix != null)
		{
			this.backwardsIncidenceMatrix.matrixChanged = true;
		}
		if (this.incidenceMatrix != null)
		{
			this.incidenceMatrix.matrixChanged = true;
		}
		DataLayer.initialMarkingVectorChanged = true;
		DataLayer.currentMarkingVectorChanged = true;

	}

	/**
	 * Sets an internal ArrayList according to the class of the object passed
	 * in.
	 * 
	 * @param pnObject
	 *            The pnObject in question.
	 * @return Returns True if the pnObject is of type Place, Transition or Arc
	 */
	private boolean setPetriNetObjectArrayList(final PetriNetObject pnObject)
	{

		// determine appropriate ArrayList
		if (pnObject instanceof Transition)
		{
			this.changeArrayList = this.transitionsArray;
			return true;
		}
		else if (pnObject instanceof Place)
		{
			this.changeArrayList = this.placesArray;
			return true;
		}
		else if (pnObject instanceof Arc)
		{
			this.changeArrayList = this.arcsArray;
			return true;
		}
		else if (pnObject instanceof Token)
		{
			this.changeArrayList = this.tokensArray;
			return true;
		}
		else if (pnObject instanceof AnnotationNote)
		{
			this.changeArrayList = this.labelsArray;
			return true;

		}
		return false;
	}

	/**
	 * Checks whether a state group with the same name exists already as the
	 * argument
	 * 
	 * @param stateName
	 * @return
	 */
	public boolean stateGroupExistsAlready(final String stateName)
	{
		final Iterator<StateGroup> i = this.stateGroups.iterator();
		while (i.hasNext())
		{
			final StateGroup stateGroup = i.next();
			final String stateGroupName = stateGroup.getName();
			if (stateName.equals(stateGroupName))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Stores Current Marking
	 * 
	 */
	public void storeState()
	{
		int placeNo = 0;
		final int placeSize = this.placesArray.size();
		this.markingVectorAnimationStorage = new int[placeSize];
		for (; placeNo < placeSize; placeNo++)
		{
			final Place place = ((Place) this.placesArray.get(placeNo));
			this.markingVectorAnimationStorage[placeNo] = place.getCurrentMarking();
			
			if( place.isTagged() ) originalTaggedPlace = placeNo;
			
		}
	}
	
	public void setValidate(boolean valid){
		validated = valid;
	}
	
	/*use to check if structure contain any tagged token or tagged arc, then the structure
	 * needs to be validated before animation
	 */
	public boolean hasValidatedStructure()
	{
	
		boolean tagged = false;
		
		for (int i = 0; i < this.placesArray.size(); i++)
		{
			if (  ((Place) this.placesArray.get(i)).isTagged()) tagged = true;
		}
		
		for (int i = 0; i < this.arcsArray.size(); i++)
		{
			if (  ((Arc) this.arcsArray.get(i)).isTagged()) tagged = true;
		}
			
		if(tagged && validated)return true;
		else if( !tagged ) return true;
		else return false;
			
				
		
	}
	

	// Function to check the structure of the Petri Net to ensure that if tagged
	// arcs are included then they obey the restrictions on how they can be used
	// (i.e. a transition may only have one input tagged Arc and one output
	// tagged Arc
	// and if it has one it must have the other).
	
	public boolean validTagStructure()
	{

		final ArrayList inputArcsArray = new ArrayList();
		final ArrayList outputArcsArray = new ArrayList();

		Place currentPlace = null;
		Transition currentTrans = null;
		Arc currentArc = null;
		boolean taggedTransition = false;
		boolean taggedInput = false;
		boolean taggedOutput = false;
		boolean validStructure = true;
		String checkResult = null;
		int noTaggedPlaces = 0;
		String taggedPlace = null;
		int noTaggedPlacesUntaggedArc=0;
		int noTaggedInArcs = 0;
		int noTaggedOutArcs = 0;

		String[] placeToCheck;
		placeToCheck = new String[20];
		int toCheck = 0;
		
		checkResult = "Tagged structure validation result:\n";

		/*
		// first check there is only one tagged token
		if (this.placesArray != null && this.placesArray.size() > 0)
		{
			for (int i = 0; i < this.placesArray.size(); i++)
			{
				currentPlace = (Place) this.placesArray.get(i);
				if (currentPlace.isTagged())
				{
					noTaggedPlaces++;
					checkResult = checkResult + "  Place " + currentPlace.getName() + " has a tagged token\n";
				}
				
				if (noTaggedPlaces > 1)
				{
					validStructure = false;
				}
			}
		}

		if (noTaggedPlaces > 1)
		{
			checkResult = checkResult + "  Multiple tagged tokens detected\n";
		}

		if (noTaggedPlaces == 0)
		{
			validStructure = false;
			checkResult = checkResult + "  No tagged tokens detected\n";
		}
		*/
		
		
		if (this.transitionsArray != null && this.transitionsArray.size() > 0)
		{
			// we need to check all the arcs....
			for (int i = 0; i < this.transitionsArray.size(); i++)
			{
				currentTrans = (Transition) this.transitionsArray.get(i);

				taggedTransition = false;
				taggedInput = false;
				taggedOutput = false;
				// invalidStructure = false;
				noTaggedInArcs = 0;
				noTaggedOutArcs = 0;

				inputArcsArray.clear();
				outputArcsArray.clear();

				// we must:
				// i) find the arcs attached to this transition
				// ii) determine whether they are input arcs or output arcs
				// iii) check that if there is one tagged input arc there is
				// also one output arc

				if (this.arcsArray != null && this.arcsArray.size() > 0)
				{
					for (int j = 0; j < this.arcsArray.size(); j++)
					{
						currentArc = (Arc) this.arcsArray.get(j);

						// System.out.println("Size of arcsArray = " +
						// arcsArray.size());
						// System.out.println("Got Arc " + currentArc.getId());

						if (currentArc.getSource() == currentTrans)
						{
							outputArcsArray.add(currentArc);
							if (currentArc.isTagged())
							{
								taggedTransition = true;
								taggedOutput = true;
								noTaggedOutArcs++;
								if (noTaggedOutArcs > 1)
								{
									checkResult = checkResult + "  Transition " + currentTrans.getName() +
													" has more than one tagged output arc\n";
									// System.out.println("Transition " +
									// currentTrans.getName() + " has more than
									// one tagged output arc");
									validStructure = false;
								}
							}
						}
						else if (currentArc.getTarget() == currentTrans)
						{
							inputArcsArray.add(currentArc);
							
							currentPlace = (Place) currentArc.getSource();
			
							if (currentArc.isTagged())
							{
								taggedTransition = true;
								taggedInput = true;
								noTaggedInArcs++;
								
								if(currentPlace!=null){
									
									if(currentPlace.isTagged()){
										if(taggedPlace==null)
										{
											taggedPlace = currentPlace.getId();
											noTaggedPlaces++;
										}
										else if(currentPlace.getId()!=taggedPlace)
										{
											noTaggedPlaces++;
										}
										checkResult = checkResult + "  Place " + currentPlace.getName() + " has a tagged token\n";
									}
									if (noTaggedPlaces > 1)validStructure = false;										
								}
							}
							else
							{
								
								if(currentPlace!=null){
									if(currentPlace.isTagged() && taggedPlace==null){
										placeToCheck[toCheck++] = currentPlace.getId();
									}
									if(currentPlace.isTagged() && currentPlace.getId()!=taggedPlace){
										noTaggedPlacesUntaggedArc++;
										checkResult = checkResult + "  Place " + currentPlace.getName() + " has a tagged token with no tagged arc\n";
										validStructure = false;
									}									
								}
							}
						}
					}
				}

				for(int x=0;x<toCheck;x++){
					if(placeToCheck[x]==taggedPlace)validStructure=true;
				}
				
				// we have now built lists of input arcs and output arcs and
				// verified that there is at most one of each.
				// we must check, however, that if there is a tagged input there
				// is a tagged output and vice-versa
				if (taggedTransition)
				{
					if (taggedInput && !taggedOutput || !taggedInput && taggedOutput)
					{
						// System.out.println("Transition " +
						// currentTrans.getName() + " does not have matching
						// tagged arcs");
						checkResult = checkResult + "  Transition " + currentTrans.getName() +
										" does not have matching tagged arcs\n";
						validStructure = false;
					}
				}
			}
		}

		
		if (noTaggedPlaces > 1)
		{
			checkResult = checkResult + "  Multiple tagged tokens detected\n";
		}

		
		//tagged net doesn't need to have tagged token
		/*
		if (noTaggedPlaces == 0 && taggedTransition)
		{
			validStructure = false;
			checkResult = checkResult + "  No tagged tokens detected on the tagged arc\n";
		}
		
		*/
		
		
		// if we reach the end with validStructure still true then everything
		// must be OK!
		if (validStructure)
		{
			
			validated = true;
			// System.out.println("Tagged arc structure is valid");
			checkResult = "Tagged structure validation result:\n  Tagged arc structure is valid\n";
			JOptionPane.showMessageDialog(	null,
											checkResult,
											"Validation Results",
											JOptionPane.INFORMATION_MESSAGE);
		}
		else
		{
			JOptionPane.showMessageDialog(null, checkResult, "Validation Results", JOptionPane.ERROR_MESSAGE);
		}

		// System.out.println(checkResult);

		return validStructure;
	}
	
	public int getPlaceIndex(String placeName){		
		int index = -1;
		for(int i=0; i<placesArray.size(); i++) {		
			if(((Place)placesArray.get(i)).getId()==placeName)
			{
				index = i;
				break;
			}
		}
//		System.out.println("Returning " + index);
		
		return index;
	}
	
	
}
