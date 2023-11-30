package pipe.common.dataLayer;

//Collections
import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Create DataLayerWriter object
 * 
 * @param DataLayer
 *            object containing net to save
 * @author Ben Kirby
 * 
 */

public class DataLayerWriter
{
	/** DataLayer object passed in to save */
	private final DataLayer	netModel;

	/** Create a writer with the DataLayer object to save */
	public DataLayerWriter(final DataLayer currentNet) {
		this.netModel = currentNet;
	}

	/**
	 * Creates a label Element for a PNML Petri-Net DOM
	 * 
	 * @param inputLabel
	 *            input label
	 * @param document
	 *            Any DOM to enable creation of Elements and Attributes
	 * @return label Element for a PNML Petri-Net DOM
	 */
	private Element createAnnotationNoteElement(final AnnotationNote inputLabel, final Document document)
	{

		Element labelElement = null;
		if (document != null)
		{
			labelElement = document.createElement("labels");
		}

		if (inputLabel != null)
		{
			final int positionXInput = inputLabel.getOriginalX();
			final int positionYInput = inputLabel.getOriginalY();
			final int widthInput = inputLabel.getNoteWidth();
			final int heightInput = inputLabel.getNoteHeight();
			final String nameInput = inputLabel.getNoteText();
			final boolean borderInput = inputLabel.isShowingBorder();

			labelElement.setAttribute("positionX", (positionXInput >= 0.0	? String.valueOf(positionXInput)
																			: ""));
			labelElement.setAttribute("positionY", (positionYInput >= 0.0	? String.valueOf(positionYInput)
																			: ""));
			labelElement.setAttribute("width", (widthInput >= 0.0 ? String.valueOf(widthInput) : ""));
			labelElement.setAttribute("height", (heightInput >= 0.0 ? String.valueOf(heightInput) : ""));
			labelElement.setAttribute("border", String.valueOf(borderInput));
			labelElement.setAttribute("text", (nameInput != null ? nameInput : ""));
		}

		return labelElement;
	}

	/**
	 * Creates a Arc Element for a PNML Petri-Net DOM
	 * 
	 * @param inputArc
	 *            Input Arc
	 * @param document
	 *            Any DOM to enable creation of Elements and Attributes
	 * @return Arc Element for a PNML Petri-Net DOM
	 */
	private Element createArcElement(final Arc inputArc, final Document document)
	{
		Element arcElement = null;

		if (document != null)
		{
			arcElement = document.createElement("arc");
		}

		if (inputArc != null)
		{
			final String idInput = inputArc.getId();
			final String sourceInput = inputArc.getSource().getId();
			final String targetInput = inputArc.getTarget().getId();
			final int inscriptionInput = inputArc.getWeight();
			final boolean tagged = inputArc.isTagged();
// Double inscriptionPositionXInput = inputArc.getInscriptionOffsetXObject();
// Double inscriptionPositionYInput = inputArc.getInscriptionOffsetYObject();
			arcElement.setAttribute("id", (idInput != null ? idInput : "error"));
			arcElement.setAttribute("source", (sourceInput != null ? sourceInput : ""));
			arcElement.setAttribute("target", (targetInput != null ? targetInput : ""));
			arcElement.setAttribute("inscription", Integer.toString(inscriptionInput));
// arcElement.setAttribute("inscriptionOffsetX", (inscriptionPositionXInput !=
// null ? String.valueOf(inscriptionPositionXInput) : ""));
// arcElement.setAttribute("inscriptionOffsetY", (inscriptionPositionYInput !=
// null ? String.valueOf(inscriptionPositionYInput) : ""));
			arcElement.setAttribute("tagged", String.valueOf(tagged));

		}
		return arcElement;
	}

	private Element createArcPoint(	final String x,
									final String y,
									final String type,
									final Document document,
									final int id)
	{
		Element arcPoint = null;
		if (document != null)
		{
			arcPoint = document.createElement("arcpath");
		}
		String pointId = String.valueOf(id);
		if (pointId.length() < 3)
		{
			pointId = "0" + pointId;
		}
		if (pointId.length() < 3)
		{
			pointId = "0" + pointId;
		}
		arcPoint.setAttribute("id", pointId);
		arcPoint.setAttribute("xCoord", x);
		arcPoint.setAttribute("yCoord", y);
		arcPoint.setAttribute("arcPointType", type);

		return arcPoint;
	}

	private Element createCondition(final String condition, final Document document)
	{
		Element stateCondition = null;

		if (document != null)
		{
			stateCondition = document.createElement("statecondition");
		}

		stateCondition.setAttribute("condition", condition);

		return stateCondition;
	}

	/**
	 * Creates a Place Element for a PNML Petri-Net DOM
	 * 
	 * @param inputPlace
	 *            Input Place
	 * @param document
	 *            Any DOM to enable creation of Elements and Attributes
	 * @return Place Element for a PNML Petri-Net DOM
	 */
	private Element createPlaceElement(final Place inputPlace, final Document document)
	{

		Element placeElement = null;

		if (document != null)
		{
			placeElement = document.createElement("place");
		}

		if (inputPlace != null)
		{
			final Double positionXInput = inputPlace.getPositionXObject();
			final Double positionYInput = inputPlace.getPositionYObject();
			final String idInput = inputPlace.getId();
			final String nameInput = inputPlace.getName();
// Double nameOffsetYInput = inputPlace.getNameOffsetXObject();
// Double nameOffsetXInput = inputPlace.getNameOffsetXObject();
			final Integer initialMarkingInput = inputPlace.getCurrentMarkingObject();
			final Double markingOffsetXInput = inputPlace.getMarkingOffsetXObject();
			final Double markingOffsetYInput = inputPlace.getMarkingOffsetYObject();
			final boolean tagged = inputPlace.isTagged();

			placeElement.setAttribute("positionX", (positionXInput != null	? String.valueOf(positionXInput)
																			: ""));
			placeElement.setAttribute("positionY", (positionYInput != null	? String.valueOf(positionYInput)
																			: ""));
			placeElement.setAttribute(	"name",
										(nameInput != null ? nameInput : idInput != null &&
																			idInput.length() > 0 ? idInput
																								: ""));
			placeElement.setAttribute("id", (idInput != null ? idInput : "error"));
// placeElement.setAttribute("nameOffsetX", (nameOffsetXInput != null ?
// String.valueOf(nameOffsetXInput) : ""));
// placeElement.setAttribute("nameOffsetY", (nameOffsetYInput != null ?
// String.valueOf(nameOffsetYInput) : ""));
			placeElement.setAttribute(	"initialMarking",
										(initialMarkingInput != null ? String.valueOf(initialMarkingInput)
																	: "0"));
			placeElement.setAttribute(	"markingOffsetX",
										(markingOffsetXInput != null ? String.valueOf(markingOffsetXInput)
																	: ""));
			placeElement.setAttribute(	"markingOffsetY",
										(markingOffsetYInput != null ? String.valueOf(markingOffsetYInput)
																	: ""));
			placeElement.setAttribute("tagged", String.valueOf(tagged));
		}
		return placeElement;
	}

	/**
	 * Creates a State Group Element for a PNML Petri-Net DOM
	 * 
	 * @param inputStateGroup
	 *            Input State Group
	 * @param document
	 *            Any DOM to enable creation of Elements and Attributes
	 * @return State Group Element for a PNML Petri-Net DOM
	 * @author Barry Kearns, August 2007
	 */
	private Element createStateGroupElement(final StateGroup inputStateGroup, final Document document)
	{

		Element stateGroupElement = null;

		if (document != null)
		{
			stateGroupElement = document.createElement("stategroup");
		}

		if (inputStateGroup != null)
		{
			final String idInput = inputStateGroup.getId();
			final String nameInput = inputStateGroup.getName();

			stateGroupElement.setAttribute("name", (nameInput != null	? nameInput
																		: idInput != null &&
																			idInput.length() > 0 ? idInput
																								: ""));
			stateGroupElement.setAttribute("id", (idInput != null ? idInput : "error"));
		}

		return stateGroupElement;
	}

	/**
	 * Creates a Transition Element for a PNML Petri-Net DOM
	 * 
	 * @param inputTransition
	 *            Input Transition
	 * @param document
	 *            Any DOM to enable creation of Elements and Attributes
	 * @return Transition Element for a PNML Petri-Net DOM
	 */
	private Element createTransitionElement(final Transition inputTransition, final Document document)
	{
		Element transitionElement = null;

		if (document != null)
		{
			transitionElement = document.createElement("transition");
		}

		if (inputTransition != null)
		{
			final Double positionXInput = inputTransition.getPositionXObject();
			final Double positionYInput = inputTransition.getPositionYObject();
			final String idInput = inputTransition.getId();
			final String nameInput = inputTransition.getName();
			final double aRate = inputTransition.getRate();
			final boolean infiniteServer = inputTransition.isInfiniteServer();
			final boolean timedTrans = inputTransition.isTimed();
			final int orientation = inputTransition.getAngle();

			transitionElement.setAttribute(	"positionX",
											(positionXInput != null ? String.valueOf(positionXInput) : ""));
			transitionElement.setAttribute(	"positionY",
											(positionYInput != null ? String.valueOf(positionYInput) : ""));
			transitionElement.setAttribute("name", (nameInput != null	? nameInput
																		: idInput != null &&
																			idInput.length() > 0 ? idInput
																								: ""));
			transitionElement.setAttribute("id", (idInput != null ? idInput : "error"));
			transitionElement.setAttribute("rate", (aRate != 1 ? String.valueOf(aRate) : "1.0"));
			transitionElement.setAttribute("timed", String.valueOf(timedTrans));
			/* ### */transitionElement.setAttribute("infiniteServer", String.valueOf(infiniteServer));
			transitionElement.setAttribute("angle", String.valueOf(orientation));
		}

		return transitionElement;
	}

	/**
	 * Save the Petri-Net
	 * 
	 * @param filename
	 *            URI location to save file
	 * @throws ParserConfigurationException
	 * @throws DOMException
	 * @throws TransformerConfigurationException
	 * @throws TransformerException
	 */

	public void savePNML(final File file) throws NullPointerException, IOException, DOMException
	{

		// Error checking
		if (file == null)
		{
			throw new NullPointerException("Null file in savePNML");
		}
		/*
		 * System.out.println("=======================================");
		 * System.out.println("dataLayer SAVING FILE=\"" +
		 * file.getCanonicalPath() + "\"");
		 * System.out.println("=======================================");
		 */
		Document pnDOM = null;
		int i;
		StreamSource xsltSource = null;
		Transformer transformer = null;
		try
		{
			// Build a Petri Net XML Document
			final DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder builder = builderFactory.newDocumentBuilder();
			pnDOM = builder.newDocument();

			final Element PNML = pnDOM.createElement("pnml"); // PNML Top
																// Level
			// Element
			pnDOM.appendChild(PNML);

			final Attr pnmlAttr = pnDOM.createAttribute("xmlns"); // PNML
																	// "xmlns"
			// Attribute
			pnmlAttr.setValue("http://www.informatik.hu-berlin.de/top/pnml/ptNetb");
			PNML.setAttributeNode(pnmlAttr);

			final Element NET = pnDOM.createElement("net"); // Net Element
			PNML.appendChild(NET);
			final Attr netAttrId = pnDOM.createAttribute("id"); // Net "id"
			// Attribute
			netAttrId.setValue("Net-One");
			NET.setAttributeNode(netAttrId);

			final Attr netAttrType = pnDOM.createAttribute("type"); // Net
																	// "type"
			// Attribute
			netAttrType.setValue("P/T net");
			NET.setAttributeNode(netAttrType);
			Place[] places = this.netModel.getPlaces();
			for (i = 0; i < places.length; i++)
			{
				NET.appendChild(this.createPlaceElement(places[i], pnDOM));
			}
			places = null;
			AnnotationNote[] labels = this.netModel.getLabels();
			if (labels.length > 0)
			{
				for (i = 0; i < labels.length; i++)
				{
					NET.appendChild(this.createAnnotationNoteElement(labels[i], pnDOM));
				}
				labels = null;
			}

			Transition[] transitions = this.netModel.getTransitions();
			for (i = 0; i < transitions.length; i++)
			{
				NET.appendChild(this.createTransitionElement(transitions[i], pnDOM));
			}
			transitions = null;
			Arc[] arcs = this.netModel.getArcs();
			for (i = 0; i < arcs.length; i++)
			{
				final Element newArc = this.createArcElement(arcs[i], pnDOM);

				final int arcPoints = arcs[i].getArcPath().getArcPathDetails().length;
				final String[][] point = arcs[i].getArcPath().getArcPathDetails();
				for (int j = 0; j < arcPoints; j++)
				{
					newArc.appendChild(this.createArcPoint(point[j][0], point[j][1], point[j][2], pnDOM, j));
				}
				NET.appendChild(newArc);
				// newArc = null;
			}
			arcs = null;

			StateGroup[] stateGroups = this.netModel.getStateGroups();
			for (i = 0; i < stateGroups.length; i++)
			{
				final Element newStateGroup = this.createStateGroupElement(stateGroups[i], pnDOM);

				final int numConditions = stateGroups[i].numElements();
				final String[] conditions = stateGroups[i].getConditions();
				for (int j = 0; j < numConditions; j++)
				{
					newStateGroup.appendChild(this.createCondition(conditions[j], pnDOM));
				}

				NET.appendChild(newStateGroup);
			}
			stateGroups = null;

			pnDOM.normalize();
			// Create Transformer with XSL Source File
			xsltSource = new StreamSource(Thread.currentThread()
												.getContextClassLoader()
												.getResourceAsStream("xslt" +
																		System.getProperty("file.separator") +
																		"GeneratePNML.xsl"));

			transformer = TransformerFactory.newInstance().newTransformer(xsltSource);
			// Write file and do XSLT transformation to generate correct PNML
			final File outputObjectArrayList = file;// new File(filename); //
													// Output
			// for XSLT Transformation
			final DOMSource source = new DOMSource(pnDOM);
			final StreamResult result = new StreamResult(outputObjectArrayList);
			transformer.transform(source, result);

		}
		catch (final ParserConfigurationException e)
		{
// System.out.println("=====================================================================================");
			System.out.println("ParserConfigurationException thrown in savePNML() : dataLayerWriter Class : dataLayer Package: filename=\"" +
								file.getCanonicalPath() + "\" xslt=\"" + "\" transformer=\"" + "\"");
// System.out.println("=====================================================================================");
// e.printStackTrace(System.err);
		}
		catch (final DOMException e)
		{
// System.out.println("=====================================================================");
			System.out.println("DOMException thrown in savePNML() : dataLayerWriter Class : dataLayer Package: filename=\"" +
								file.getCanonicalPath() +
								"\" xslt=\"" +
								xsltSource.getSystemId() +
								"\" transformer=\"" + transformer.getURIResolver() + "\"");
// System.out.println("=====================================================================");
// e.printStackTrace(System.err);
		}
		catch (final TransformerConfigurationException e)
		{
// System.out.println("==========================================================================================");
			System.out.println("TransformerConfigurationException thrown in savePNML() : dataLayerWriter Class : dataLayer Package: filename=\"" +
								file.getCanonicalPath() +
								"\" xslt=\"" +
								xsltSource.getSystemId() +
								"\" transformer=\"" + transformer.getURIResolver() + "\"");
// System.out.println("==========================================================================================");
// e.printStackTrace(System.err);
		}
		catch (final TransformerException e)
		{
// System.out.println("=============================================================================");
			System.out.println("TransformerException thrown in savePNML() : dataLayerWriter Class : dataLayer Package: filename=\"" +
								file.getCanonicalPath() +
								"\" xslt=\"" +
								xsltSource.getSystemId() +
								"\" transformer=\"" + transformer.getURIResolver() + "\"" + e);
// System.out.println("=============================================================================");
// e.printStackTrace(System.err);
		}
	}

}