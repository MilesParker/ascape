/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.view.nonvis;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.ascape.model.event.ScapeEvent;
import org.ascape.util.PropertyAccessor;


/**
 * Sets up each run based on parameters drawn from a file.
 * 
 * @author Jim Girard, Miles Parker
 * @version 2.0
 * @history 2.0 9/1/02 first in
 * @since 2.0
 */
public class ParameterControlView extends NonGraphicView {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * The input.
     */
    private FileReader input;
    
    /**
     * The buf input.
     */
    private BufferedReader bufInput;
    
    /**
     * The params.
     */
    private Vector params;
    
    /**
     * The first line.
     */
    private String firstLine;
    
    /**
     * The next line.
     */
    private String nextLine;
    
    /**
     * The filename.
     */
    private String filename;

    // Open data file of runs to do and read in first line
    //  first line must match up with possible parameters in scape
    /**
     * Sets the file.
     * 
     * @param filename
     *            the new file
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public void setFile(String filename) throws IOException {
        this.filename = filename;
        params = new Vector();
        try {
            input = new FileReader(filename);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e.toString());
        }
        bufInput = new BufferedReader(input);
        firstLine = bufInput.readLine();
        nextLine = null;
    }

    /**
     * Inits the params.
     */
    public void initParams() {
        List accessors = getScape().retrieveAllAccessors();
        StringTokenizer st = new StringTokenizer(firstLine);
        while (st.hasMoreTokens()) {
            boolean paramFound = false;
            String parameterName = st.nextToken();
            // Check to see if this parameter exists in the scape
            for (Iterator iterator = accessors.iterator(); iterator.hasNext();) {
                PropertyAccessor accessor = (PropertyAccessor) iterator.next();
                if (accessor.getName().equalsIgnoreCase(parameterName)) {
                    // save the parameter name
                    params.add(accessor);
                    paramFound = true;
                }
            }
            if (!paramFound) {
                if (parameterName.equalsIgnoreCase("RandomSeed")) {
                    params.add("RandomSeed");
                } else if (parameterName.equalsIgnoreCase("StartPeriod")) {
                    params.add("StartPeriod");
                } else if (parameterName.equalsIgnoreCase("StopPeriod")) {
                    params.add("StopPeriod");
                } else {
                    throw new RuntimeException("***Warning*** Paramater not found: " +
                        parameterName);
                }
            } else {
                //System.out.println("Parameter Found: "+parameterName);
            }
        }

        // read in the next line, there should be at least one
        try {
            nextLine = bufInput.readLine();
        } catch (IOException e) {
            throw new RuntimeException("No data in file " + filename);
        }
        if (nextLine == null) {
            throw new RuntimeException("No data in file " + filename);
        }
    }

    /**
     * Method called once a model is deserialized.
     * 
     * @param scapeEvent
     *            the scape event
     */
    public void scapeDeserialized(ScapeEvent scapeEvent) {
        super.scapeDeserialized(scapeEvent);
        readParams();
    }

    /* (non-Javadoc)
     * @see org.ascape.model.event.DefaultScapeListener#scapeSetup(org.ascape.model.event.ScapeEvent)
     */
    public void scapeSetup(ScapeEvent scapeEvent) {
        readParams();
    }

    /**
     * Read params.
     */
    private void readParams() {
        if (nextLine == null) {
            initParams();
        }

        // read the next line from the file and set parameters
        String token;
        StringTokenizer st = new StringTokenizer(nextLine);
        for (int i = 0; i < params.size(); i++) {
            token = st.nextToken();
            //System.out.println("param value is "+token);
            Object nextParam = params.elementAt(i);
            if (nextParam instanceof PropertyAccessor) {
                try {
                    ((PropertyAccessor) nextParam).setAsText(token);
                } catch (InvocationTargetException e) {
                    throw new Error("Exception in invoked method: " + e.getTargetException());
                }
            } else if (nextParam instanceof String) {
                //This is kind of cheesy, but really just a stop gap until we auto provide accessors for these things...
                if (nextParam.equals("RandomSeed")) {
                    getScape().setRandomSeed(Long.valueOf(token).longValue());
                } else if (nextParam.equals("StartPeriod")) {
                    try {
                        getScape().setStartPeriod(Integer.valueOf(token).intValue());
                    } catch (org.ascape.model.space.SpatialTemporalException e) {
                    	getScape().getEnvironment().getConsole().println("Bad Start Period Specified: " + Integer.valueOf(token).intValue());
                    }
                } else if (nextParam.equals("StopPeriod")) {
                    try {
                        getScape().setStopPeriod(Integer.valueOf(token).intValue());
                    } catch (org.ascape.model.space.SpatialTemporalException e) {
                    	getScape().getEnvironment().getConsole().println("Bad Start Period Specified: " + Integer.valueOf(token).intValue());
                    }
                }
            }
            // use introspection to set the value
            //System.out.println("Paramter Set: "+ currentAccessor.getLongName() + " to "+token);
        }
    }

    /* (non-Javadoc)
     * @see org.ascape.model.event.DefaultScapeListener#scapeStopped(org.ascape.model.event.ScapeEvent)
     */
    public void scapeStopped(ScapeEvent scapeEvent) {
        // read a line from file; if doesn't exist, stop, if it does, request a restart..
        try {
            nextLine = bufInput.readLine();
            if (nextLine != null) {
                getScape().getRunner().requestRestart();
                return;
            }
        } catch (IOException e) {
            //Read failed, so we will just move on to quit..
        }
        getScape().getRunner().quit();
    }

}
