/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */
package org.ascape.view.nonvis;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.TooManyListenersException;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.ascape.model.Agent;
import org.ascape.model.CellOccupant;
import org.ascape.model.HostCell;
import org.ascape.model.LocatedAgent;
import org.ascape.model.event.ScapeEvent;
import org.ascape.model.space.Coordinate;
import org.ascape.model.space.Coordinate1DContinuous;
import org.ascape.model.space.Coordinate1DDiscrete;
import org.ascape.model.space.Coordinate2DContinuous;
import org.ascape.model.space.Coordinate2DDiscrete;
import org.ascape.util.PropertyAccessor;
import org.ascape.util.VectorSelection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * A view that instantiates a scape with the contents of an XML file. Agents
 * will be assigned properties based on the values as defined in this example.
 * <agent> <parameter name="foodState" value="0.66542" /> <parameter name="mode"
 * value="1" /> <parameter name="timeWaitingForFood" value="14" /> <parameter
 * name="vision" value="40" /> </agent> This would create an agent of with a
 * foodState of 0.66542, a mode of 1, a timeWaitingForFood of 14, and a vision
 * of 40. All values specified in the header must be Java properties; that is,
 * there must be getters and setters defined in the agent for each value. How
 * these agents are created is specified by the attributes sizeMode, readOrder,
 * and assignmentOrder. SizeMode determines whether the scape's size will be set
 * by the number of agents defined in the XML file or by the default size of the
 * scape. AssignmentOrder determines whether agents will be selected to receive
 * XML data randomly or sequentially. ReadOrder determines whether XML data will
 * be read randomly or sequentially. These flags are set in the input file
 * (where the data is), and not in the Control file (typically "run.xml"). If
 * values for any or all of these attributes are not specified in the XML file,
 * the defaults are: size by file, and sequentially for both orders. This was
 * written by drawing extensively from ScapeFromFileView. User: jmiller Date:
 * Jan 10, 2006 Time: 10:22:32 AM To change this template use Options | File
 * Templates.
 */
public class ScapeFromXMLView extends NonGraphicView {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * The root element.
     */
    private Element rootElement;

    /**
     * The Constant UNDEFINED_MODE.
     */
    public final static int UNDEFINED_MODE = 0;

    /**
     * Set scape size by the number of entries in the xml file.
     */
    public final static int SIZE_BY_FILE = 1;

    /**
     * Set scape size by the existing size of the scape.
     */
    public final static int SIZE_BY_SCAPE = 2;


    /**
     * The Constant UNDEFINED_ORDER.
     */
    public final static int UNDEFINED_ORDER = 0;

    /**
     * The Constant RANDOM_ORDER.
     */
    public final static int RANDOM_ORDER = 1;

    /**
     * The Constant SEQUENTIAL_ORDER.
     */
    public final static int SEQUENTIAL_ORDER = 2;

    /**
     * The candidate descriptors.
     */
    private PropertyDescriptor[] candidateDescriptors;

    /**
     * Set the size fo the scape by the data in the xml file, or leave it as is
     * (and potentially ignore data in the file).
     */
    private int mode = UNDEFINED_MODE;

    /**
     * Assign the xml data to the agents in sequential order or in random order
     * (for the agents).
     */
    private int assignmentOrder = UNDEFINED_ORDER;

    /**
     * Read the file in either random or sequential order from the XML file.
     */
    private int readOrder = UNDEFINED_ORDER;

    /**
     * The element list.
     */
    private ArrayList elementList; // list of the data from file to choose from, at random, without repeating

    /**
     * Parse in the XML file, and set the root element and set flags (mode,
     * assignmentOrder, readOrder).
     * 
     * @param inputFileName
     *            the input file name
     */
    public void parseFile(String inputFileName) {
    	InputStream fis;
		try {
			fis = new FileInputStream(new File(inputFileName));
	    	parseStream(fis);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
    }

    /**
     * Parse in the XML file, and set the root element and set flags (mode,
     * assignmentOrder, readOrder).
     * 
     * @param is
     *            the is
     */
    public void parseStream(InputStream is) {
        rootElement = streamToElelement(is); // <Nima>
        mode = determineMode(rootElement.getAttribute("sizeMode"));
        assignmentOrder = determineAssignmentOrder(rootElement.getAttribute("assignmentOrder"));
        readOrder = determineReadOrder(rootElement.getAttribute("readOrder"));
    }

    /**
     * Add the view to the scape, registering it as a listener, and ensuring
     * that it hasn't been added to any other scapes.
     * 
     * @param scapeEvent
     *            the event for this scape to make this view the observer of
     * @throws TooManyListenersException
     *             the too many listeners exception
     * @exception TooManyListenersException
     *                on attempt to add a scape when one is allready added
     */
    public void scapeAdded(ScapeEvent scapeEvent) throws TooManyListenersException {
        super.scapeAdded(scapeEvent);
        if (rootElement == null) {
            throw new RuntimeException("Root element is null. Be sure a file name was set, via 'inputFileName'.");
        }
        if (mode == UNDEFINED_MODE) {
            getScape().getEnvironment().getConsole().println("No mode has been set -- setting mode to size by scape.");
            mode = SIZE_BY_SCAPE;
        }
        if (assignmentOrder == UNDEFINED_ORDER) {
            getScape().getEnvironment().getConsole().println("No assignment order has been set -- setting assignment order to random order.");
            assignmentOrder = RANDOM_ORDER;
        }
        if (readOrder == UNDEFINED_ORDER) {
            getScape().getEnvironment().getConsole().println("No read order has been set -- setting read order to random order.");
            readOrder = RANDOM_ORDER;
        }
        if (mode == SIZE_BY_FILE) {
            if ((getScape() != null) && !(getScape().isMutable())) {
                throw new RuntimeException("Population size can not be defined by file for immutable (fixed size) scapes. Set PopulationSizeDefinedByFile to size by scape or use a mutable scape.");
            }
            scape.setPopulateOnCreate(false);
        }
    }

    /**
     * Called immediatly after the scape is initialized.
     * 
     * @param scapeEvent
     *            the scape event
     */
    public void scapeInitialized(ScapeEvent scapeEvent) {
        super.scapeInitialized(scapeEvent);
        if (mode == SIZE_BY_FILE) {
            getScape().clear();
        }

        // populate array list
        NodeList agentsList = rootElement.getElementsByTagName("agent");
        elementList = new ArrayList(agentsList.getLength());
        for (int i = 0; i < agentsList.getLength(); i++) {
            Element agentEl = (Element) agentsList.item(i);
            elementList.add(agentEl);
        }

        // if reading in random order, shuffle the elements
        if (readOrder == RANDOM_ORDER) {
            Collections.shuffle(elementList, getScape().getRandom());
        }

        try {
            candidateDescriptors = Introspector.getBeanInfo(getScape().getPrototypeAgent().getClass(), Agent.class).getPropertyDescriptors();
        } catch (IntrospectionException e) {
            throw new RuntimeException("While reading prototype agent descriptors: " + e);
        }
        Iterator scapeIterator = null;
        if (mode == SIZE_BY_SCAPE) {
            if (assignmentOrder == SEQUENTIAL_ORDER) {
                scapeIterator = getScape().iterator();
            } else if (assignmentOrder  == RANDOM_ORDER) {
                scapeIterator = getScape().getSpace().safeRandomIterator();
            } else {
                throw new RuntimeException("Undefined or unknown assignment order value.");
            }
        }

        Agent currentAgent;
        if (scapeIterator != null) { // size by scape
            while (scapeIterator.hasNext()) {
                currentAgent = (Agent) scapeIterator.next();
            // todo: what happens if the scape is bigger than the XML file?
                if (elementList.isEmpty()) {
                    getScape().getEnvironment().getConsole().println("Warning: ScapeFile contains less data than there are agents in the target scape: " + getScape());
                } else {
                    Element agentElement = (Element) elementList.remove(0);
//                    System.err.println("setParametersFor.. called in size by scape loop");
                    setParametersfor(currentAgent, agentElement);
                }
            }
            if (elementList.isEmpty() == false) {
                getScape().getEnvironment().getConsole().println("Warning: ScapeFile contains more data than there are agents in the target scape: " + getScape());
            }
        } else { // size by file
            for (Iterator elementIt = elementList.iterator(); elementIt.hasNext();) {
                Element agentElement = (Element) elementIt.next();
//                VectorSelection initRules = getScape().getInitialRules();
                // scape.newAgent executes initial rules. Some models may depend on a specific timing of rule executing,
                // so this temporarily clears the scapes' initial rules, so none are fired, until the proper time.
                if (assignmentOrder == RANDOM_ORDER) {
                    VectorSelection initRulesHolder = getScape().getInitialRules();
                    getScape().setInitialRules(new VectorSelection(new Vector()));
                    currentAgent = getScape().newAgent(true);
                    getScape().setInitialRules(initRulesHolder);
                } else {
                    VectorSelection initRulesHolder = getScape().getInitialRules();
                    getScape().setInitialRules(new VectorSelection(new Vector()));
                    currentAgent = getScape().newAgent(false);
                    getScape().setInitialRules(initRulesHolder);
                }
//                System.err.println("setParametersFor.. called in size by file loop");
                setParametersfor(currentAgent, agentElement);
            }
        }
    }

    /**
     * Sets the parametersfor.
     * 
     * @param currentAgent
     *            the current agent
     * @param agentElement
     *            the agent element
     */
    private void setParametersfor(Agent currentAgent, Element agentElement) {
        // check for coordinate

        // this (commented) code will handle coordinates. But majority of models don't check to see if an agent
        // already has a coordinate. So for now, leaving it out.

        String xCoord = agentElement.getAttribute("x");
        
        String yCoord = agentElement.getAttribute("y");
        
        Object xObj = null;
        Object yObj = null;
        String decimalRegex = "/[[0-9].[0-9]]/";
        if (xCoord.length() > 0) { // not an empty string
            if (xCoord.matches(decimalRegex)) { // there's (exactly) one decimal, so it's a double
                xObj = new Double(xCoord);
            } else {  // no decimal, and int
                xObj = new Integer(xCoord);
            }
        }

        if (yCoord.length() > 0) {
            if (xObj == null) {
                throw new RuntimeException("No matching x coordinate in: " + currentAgent + ",  " + agentElement);
            } else {
                if (yCoord.matches(decimalRegex)) { // there's (exactly) one decimal, so it's a double
                    if (xObj instanceof Double) {
                        yObj = new Double(yCoord);
                    } else {
                        // even though y-coord has a decimal, x is an Integer, so make y an integer too
                        yObj = new Integer(yCoord);
                    }
                } else {  // no decimal, and int
                    if (xObj instanceof Double) {
                        // even though y-coord has no decimal, x is a double, so make y a double too
                        yObj = new Double(yCoord);
                    } else {
                        yObj = new Integer(yCoord);
                    }
                }
            }
        }

        if (xObj != null) {
            if (currentAgent instanceof LocatedAgent == false) {
                getScape().getEnvironment().getConsole().println(currentAgent + " is not an instance of LocatedAgent, so it can't have a coordinate.");
            } else {
                Coordinate coor = null;
                if (yObj == null) { // 1D coordinate
                    if (xObj instanceof Double) {  // continuous
                        coor = new Coordinate1DContinuous(((Double) xObj).doubleValue());
                        ((LocatedAgent) currentAgent).moveTo(coor);
                    } else { // discrete
                        coor = new Coordinate1DDiscrete(((Integer) xObj).intValue());
                        CellOccupant cell = (CellOccupant) currentAgent;
                        cell.moveTo((HostCell) cell.getHostScape().get(coor));
                    }
                } else { // 2D coordinate
                    if (xObj instanceof Double) { // continuous
                        coor = new Coordinate2DContinuous(((Double) xObj).doubleValue(), ((Double) yObj).doubleValue());
                        ((LocatedAgent) currentAgent).moveTo(coor);
                    } else { // discrete
                        if (currentAgent instanceof CellOccupant) {
                            coor = new Coordinate2DDiscrete(((Integer) xObj).intValue(), ((Integer) yObj).intValue());
                            CellOccupant cell = (CellOccupant) currentAgent;
                            cell.moveTo((HostCell) cell.getHostScape().get(coor));
                        } else {
                            ((LocatedAgent) currentAgent).moveTo(coor);
                        }
                    }
                }
            }
        }

        // now handle rest of parameters
        NodeList parametersList = agentElement.getElementsByTagName("parameter");
        for (int i = 0; i < parametersList.getLength(); i++) {
            Element parameter = (Element) parametersList.item(i);
            String name = parameter.getAttribute("name");
            String value = parameter.getAttribute("value");
            boolean paramFound = false;
            for (int j = 0; j < candidateDescriptors.length; j++) {
                PropertyDescriptor desc = candidateDescriptors[j];
                if (desc.getName().equalsIgnoreCase(name)) {
                    paramFound = true;
                    Object[] args = new Object[1];
                    try {
                        args[0] = PropertyAccessor.stringAsClass(desc.getPropertyType(), value);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Error - " + value + " cannot be converted to (" + desc.getPropertyType() + ") " + desc.getDisplayName());
                    }
                    try {
                        desc.getWriteMethod().invoke(currentAgent, args);
                    } catch (IllegalAccessException e) {
                        getScape().getEnvironment().getConsole().println("Error in dynamic method write: " + e);
                    } catch (IllegalArgumentException e) {
                        getScape().getEnvironment().getConsole().println("Error in dynamic method write: " + e);
                    } catch (InvocationTargetException e) {
                        getScape().getEnvironment().getConsole().println("Error in dynamic method write: " + e);
                    } catch (NullPointerException e) {
                        throw new RuntimeException("Bad Property Descriptor: " + desc.getName());
                    }
                }
            }
            if (paramFound == false) {
                throw new RuntimeException("Property " + name+ " not found in prototype agent class: " + getScape().getPrototypeAgent().getClass().getName());
            }
        }
    }

    /**
     * Stream to elelement.
     * 
     * @param is
     *            the is
     * @return the element
     */
    private static Element streamToElelement(InputStream is) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document document = null;
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(is);
        } catch (SAXException sxe) {
            // Error generated during parsing
            Exception x = sxe;
            if (sxe.getException() != null) {
                x = sxe.getException();
            }
            x.printStackTrace();

        } catch (ParserConfigurationException pce) {
            // Parser with specified options can't be built
            pce.printStackTrace();

        } catch (IOException ioe) {
            // I/O error
            ioe.printStackTrace();
        }
        return document.getDocumentElement(); // <Nima>
    }

    /**
     * Determine mode.
     * 
     * @param modeAttribute
     *            the mode attribute
     * @return the int
     */
    private int determineMode(String modeAttribute) {
        if (modeAttribute.equalsIgnoreCase("size by file")) {
            return SIZE_BY_FILE;
        } else if (modeAttribute.equalsIgnoreCase("size by scape")) {
            return SIZE_BY_SCAPE;
        } else {
            getScape().getEnvironment().getConsole().println("Mode attribute not recognized: " + modeAttribute+". Setting mode to size by file.");
             return SIZE_BY_FILE;
        }
    }

    /**
     * Determine assignment order.
     * 
     * @param orderAttribute
     *            the order attribute
     * @return the int
     */
    private int determineAssignmentOrder(String orderAttribute) {
        if (orderAttribute.equalsIgnoreCase("random order")) {
            return RANDOM_ORDER;
        } else if (orderAttribute.equalsIgnoreCase("sequential order")) {
            return SEQUENTIAL_ORDER;
        } else {
            getScape().getEnvironment().getConsole().println("Assignment order attribute not recognized: " + orderAttribute+". Setting assignment order to sequential order.");
            return SEQUENTIAL_ORDER;
        }
    }

    /**
     * Determine read order.
     * 
     * @param orderAttribute
     *            the order attribute
     * @return the int
     */
    private int determineReadOrder(String orderAttribute) {
        if (orderAttribute.equalsIgnoreCase("random order")) {
            return RANDOM_ORDER;
        } else if (orderAttribute.equalsIgnoreCase("sequential order")) {
            return SEQUENTIAL_ORDER;
        } else {
            getScape().getEnvironment().getConsole().println("Read order attribute not recognized: " + orderAttribute+". Setting read order to sequential order.");
            return SEQUENTIAL_ORDER;
        }
    }

    /**
     * Gets the assignment order.
     * 
     * @return the assignment order
     */
    public int getAssignmentOrder() {
        return assignmentOrder;
    }

    /**
     * Sets the assignment order.
     * 
     * @param assignmentOrder
     *            the new assignment order
     */
    public void setAssignmentOrder(int assignmentOrder) {
        this.assignmentOrder = assignmentOrder;
    }

    /**
     * Gets the read order.
     * 
     * @return the read order
     */
    public int getReadOrder() {
        return readOrder;
    }

    /**
     * Sets the read order.
     * 
     * @param readOrder
     *            the new read order
     */
    public void setReadOrder(int readOrder) {
        this.readOrder = readOrder;
    }

    /**
     * Gets the mode.
     * 
     * @return the mode
     */
    public int getMode() {
        return mode;
    }

    /**
     * Sets the mode.
     * 
     * @param mode
     *            the new mode
     */
    public void setMode(int mode) {
        this.mode = mode;
    }
}
