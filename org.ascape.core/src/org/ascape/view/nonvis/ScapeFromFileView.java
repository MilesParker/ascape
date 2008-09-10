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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.TooManyListenersException;

import org.ascape.model.Agent;
import org.ascape.model.LocatedAgent;
import org.ascape.model.Scape;
import org.ascape.model.event.ScapeEvent;
import org.ascape.util.PropertyAccessor;

/**
 * A view that instantiates a scape with the contents of a file or input stream.
 * Agents will be assigned properties based on the values for a given record as
 * defined in the file header (first record.) The file is expected to be record
 * (agent) delimited in native format and field (property) delimited with a tab
 * character. For example a file with the contencts: <code>
 * Age  Wealth  Size
 * 12     150   200
 * 20    1200   150
 * 30    1300   110
 * </code>
 * Might create an agent of age 20, wealth 1200, and size 150. All values
 * specified in the header must be Java properties; that is, there must be
 * getters and setters defined in the agent for each value. How these agents are
 * created is specified by this control view's mode and order.
 * 
 * @author Miles Parker
 * @version 2.0
 * @history 2.0 9/1/02 first in
 * @since 2.0
 */
public class ScapeFromFileView extends NonGraphicView {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * The Constant UNDEFINED_MODE.
     */
    public final static int UNDEFINED_MODE = 0;

    /**
     * The Constant SIZE_BY_FILE_SEQUENTIAL_MODE.
     */
    public final static int SIZE_BY_FILE_SEQUENTIAL_MODE = 1;

    /**
     * The Constant SIZE_BY_SCAPE_SEQUENTIAL_MODE.
     */
    public final static int SIZE_BY_SCAPE_SEQUENTIAL_MODE = 2;

    /**
     * The Constant SIZE_BY_SCAPE_RANDOM_SAMPLE_MODE.
     */
    public final static int SIZE_BY_SCAPE_RANDOM_SAMPLE_MODE = 3;

    /**
     * The Constant UNDEFINED_ORDER.
     */
    public final static int UNDEFINED_ORDER = 0;

    /**
     * The Constant ASSIGN_RANDOM_ORDER.
     */
    public final static int ASSIGN_RANDOM_ORDER = 1;

    /**
     * The Constant ASSIGN_SEQUENTIAL_ORDER.
     */
    public final static int ASSIGN_SEQUENTIAL_ORDER = 2;

    /**
     * The reader.
     */
    transient private BufferedReader reader;

    /**
     * The mode.
     */
    private int mode = UNDEFINED_MODE;

    /**
     * The order.
     */
    private int order = UNDEFINED_ORDER;

    /**
     * The random lines.
     */
    private ArrayList randomLines;

    /**
     * The current line.
     */
    private int currentLine;

    /**
     * The last line read.
     */
    private String lastLineRead;

    /**
     * The file.
     */
    private File file;

    /**
     * Sets the file.
     * 
     * @param filename
     *            the new file
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public void setFile(String filename) throws IOException {
        file = new File(filename);
        reader = new BufferedReader(new FileReader(file));
        //Is this dangerous?
        reader.mark(4000000);
    }

    /**
     * Sets the input stream.
     * 
     * @param inputStream
     *            the new input stream
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public void setInputStream(InputStream inputStream) throws IOException {
        reader = new BufferedReader(new InputStreamReader(inputStream));
        //Is this dangerous?
        reader.mark(400000);
    }

    /**
     * Retrieve next line.
     * 
     * @return the string
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private String retrieveNextLine() throws IOException {
        if ((mode == SIZE_BY_SCAPE_SEQUENTIAL_MODE) || (mode == SIZE_BY_FILE_SEQUENTIAL_MODE)) {
            currentLine++;
            return reader.readLine();
        } else if (mode == SIZE_BY_SCAPE_RANDOM_SAMPLE_MODE) {
            if (randomLines.size() > 0) {
                int targetLine = ((Integer) randomLines.remove(randomLines.size() - 1)).intValue();
                while (currentLine <= targetLine) {
                    lastLineRead = reader.readLine();
                    currentLine++;
                }
                return lastLineRead;
            } else {
                return null;
            }
        } else {
            throw new RuntimeException("Bad Mode in file control view: " + mode);
        }
    }

    /**
     * The Constant BACKWARD_COMPARE.
     */
    public static final Comparator BACKWARD_COMPARE = new Comparator() {
        public int compare(Object o1, Object o2) {
            return -((Comparable) o1).compareTo(o2);
        }
    };

    /**
     * Reset reader.
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private void resetReader() throws IOException {
        if (reader != null) {
            if (file != null) {
                reader.close();
                reader = new BufferedReader(new FileReader(file));
            } else {
                reader.reset();
            }
        } else {
            reader = new BufferedReader(new FileReader(file));
        }
    }

    /**
     * When scape is initialized, load appropriate values into scape.
     * 
     * @param scapeEvent
     *            the scape event
     */
    public void scapeInitialized(ScapeEvent scapeEvent) {
        if (mode == SIZE_BY_FILE_SEQUENTIAL_MODE) {
            getScape().clear();
        }
        try {
            if (mode == SIZE_BY_SCAPE_RANDOM_SAMPLE_MODE) {
                resetReader();
                //We need to get the total number of lines in the file; it seems that we have to parse the entire file to do this,
                //since the lines are of variable length.
                //Don't count header, or alst line read
                int numLines = 0;
                reader.readLine();
                while (reader.readLine() != null) {
                    numLines++;
                }
                randomLines = new ArrayList();
                for (int i = 0; i < getScape().getSize(); i++) {
                    randomLines.add(new Integer(getScape().randomToLimit(numLines) + 1));
                }
                Collections.sort(randomLines, BACKWARD_COMPARE);
            }
            currentLine = 1;

            resetReader();

            String nextLine = reader.readLine();

            ArrayList descriptors = new ArrayList();

            PropertyDescriptor[] candidateDescriptors;
            try {
                candidateDescriptors = Introspector.getBeanInfo(getScape().getPrototypeAgent().getClass(), Agent.class).getPropertyDescriptors();
            } catch (IntrospectionException e) {
                throw new RuntimeException("While reading prototype agent descriptors: " + e);
            }

            StringTokenizer st = new StringTokenizer(nextLine, "\t");
            while (st.hasMoreTokens()) {
                boolean paramFound = false;
                String parameterName = st.nextToken();
                // Check to see if this parameter exists in the scape
                for (int i = 0; i < candidateDescriptors.length; i++) {
                    if (candidateDescriptors[i].getName().equalsIgnoreCase(parameterName)) {
                        // save the parameter name
                        descriptors.add(candidateDescriptors[i]);
                        paramFound = true;
                    }
                }
                if (!paramFound) {
                    //Perhaps should throw this instead of do runtime exception
                    throw new RuntimeException("Property defined in ScapeFile file " + reader + " not found in prototype agent class: " +
                        parameterName);
                }
            }

            Iterator scapeIterator = null;
            if (getMode() != SIZE_BY_FILE_SEQUENTIAL_MODE) {
                if (order == ASSIGN_SEQUENTIAL_ORDER) {
                    scapeIterator = getScape().iterator();
                } else if (order == ASSIGN_RANDOM_ORDER) {
                    scapeIterator = getScape().getSpace().safeRandomIterator();
                } else {
                    throw new RuntimeException("Undefined or unknown order value.");
                }
            }

            nextLine = retrieveNextLine();
            while (nextLine != null) {
                String token;
                st = new StringTokenizer(nextLine, "\t");
                Agent currentAgent;
                if (scapeIterator != null) {
                    if (scapeIterator.hasNext()) {
                        currentAgent = (Agent) scapeIterator.next();
                    } else {
                        break;
                    }
                } else {
                    if (order == ASSIGN_SEQUENTIAL_ORDER) {
                        currentAgent = (Agent) getScape().getSpace().newLocation(false);
                    } else if (order == ASSIGN_RANDOM_ORDER) {
                        currentAgent = (Agent) getScape().getSpace().newLocation(true);
                    } else {
                        throw new RuntimeException("Undefined or unknown order value.");
                    }
                }
                Iterator descIter = descriptors.iterator();
                while (descIter.hasNext()) {
                    token = st.nextToken();
                    PropertyDescriptor desc = (PropertyDescriptor) descIter.next();
                    Object[] args = new Object[1];
                    try {
                        args[0] = PropertyAccessor.stringAsClass(desc.getPropertyType(), token);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Error at data line " + currentLine + ": " + token + " can not be converted to (" + desc.getPropertyType() + ") " + desc.getDisplayName());
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
                nextLine = retrieveNextLine();
            }
            if ((scapeIterator != null) && scapeIterator.hasNext()) {
                getScape().getEnvironment().getConsole().println("Warning: ScapeFile contains less data than there are agents in the target scape: " + getScape());
            }
            if ((nextLine != null) && ((scapeIterator != null) && !scapeIterator.hasNext())) {
                getScape().getEnvironment().getConsole().println("Warning: ScapeFile contains more data than there are agents in the target scape: " + getScape());
            }
        } catch (IOException e) {
            throw new RuntimeException("An IO Exception happend while reading ScapeFile file data: " + e);
        }
    }

    /**
     * On mode or scape selection.
     */
    private void onModeOrScapeSelection() {
        if (mode == SIZE_BY_FILE_SEQUENTIAL_MODE) {
            if ((getScape() != null) && !(getScape().isMutable())) {
                throw new RuntimeException("Population size can not be defined by file for immutable (fixed size) scapes. Set PopulationSizeDefinedByFile to false or use a mutable scape.");
            }
            scape.setPopulateOnCreate(false);
        }
    }

    /**
     * Notifies the listener that the scape has added it.
     * 
     * @param scapeEvent
     *            the scape added notification event
     * @throws TooManyListenersException
     *             the too many listeners exception
     * @exception java.util.TooManyListenersException
     *                on attempt to add this listener to another scape when one
     *                has already been assigned
     */
    public void scapeAdded(ScapeEvent scapeEvent) throws TooManyListenersException {
        super.scapeAdded(scapeEvent);
        if (mode == UNDEFINED_MODE) {
            if (((Scape) scapeEvent.getSource()).isMutable()) {
                setMode(SIZE_BY_FILE_SEQUENTIAL_MODE);
            } else {
                setMode(SIZE_BY_SCAPE_SEQUENTIAL_MODE);
            }
        } else {
            //We don't need to do this in the default case, because setMode will call it..
            onModeOrScapeSelection();
        }
    }

    /**
     * Gets the order.
     * 
     * @return the order
     */
    public int getOrder() {
        return order;
    }

    /**
     * Sets the order.
     * 
     * @param order
     *            the new order
     */
    public void setOrder(int order) {
        this.order = order;
        if ((order != ASSIGN_SEQUENTIAL_ORDER) && (order != ASSIGN_RANDOM_ORDER)) {
            throw new RuntimeException("Undefined or unknown order value.");
        }
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
        if ((mode != SIZE_BY_SCAPE_SEQUENTIAL_MODE) && (mode != SIZE_BY_FILE_SEQUENTIAL_MODE) && (mode != SIZE_BY_SCAPE_RANDOM_SAMPLE_MODE)) {
            throw new RuntimeException("Bad Mode in file control view: " + mode);
        }
        if (order == UNDEFINED_ORDER) {
            if (mode == SIZE_BY_SCAPE_RANDOM_SAMPLE_MODE) {
                setOrder(ASSIGN_RANDOM_ORDER);
            } else {
                setOrder(ASSIGN_SEQUENTIAL_ORDER);
            }
        }
        onModeOrScapeSelection();
    }

    /* (non-Javadoc)
     * @see org.ascape.model.event.DefaultScapeListener#scapeDeserialized(org.ascape.model.event.ScapeEvent)
     */
    public void scapeDeserialized(ScapeEvent event) {
        super.scapeDeserialized(event);
        try {
            resetReader();
        } catch (IOException e) {
            getScape().getEnvironment().getConsole().println(e.toString());
        }
    }
}
