/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */
package org.ascape.ant;

import java.beans.IntrospectionException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ascape.model.Agent;
import org.ascape.model.LocatedAgent;
import org.ascape.model.Scape;
import org.ascape.model.event.ScapeEvent;
import org.ascape.util.PropertyAccessor;
import org.ascape.util.Utility;
import org.ascape.view.nonvis.DataOutputView;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * User: jmiller Date: Nov 2, 2005 Time: 1:02:02 PM To change this template use
 * Options | File Templates.
 */

/**
 * Very similar to DataOutputView, this view outputs all model and member-agent scape data.
 * The other differnce is that data here is written to an XML file.
 * Note: If the RunDataSet is being used, it must be added in the Ant file before the AllOutputView.
 */
public class AllOutputView extends DataOutputView {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * The output data strings.
     */
    private List outputDataStrings;

    /**
     * The root element.
     */
    protected Element rootElement;

    /**
     * On scape start, record the start time and wirte the file headers. If any
     * headers need to be written, they are written now. Write first period
     * data.
     * 
     * @param scapeEvent
     *            the scape event
     */
    public void scapeStarted(ScapeEvent scapeEvent) {
        rootElement = new Element("Model");
        startTime = System.currentTimeMillis();
        if (runFile == null) { // ie it's not set in the Ant run.xml file
            String modelName = getScape().getRoot().getName();
            runFile = new File(modelName+"_"+startTime+".xml"); // unusual that a file name wasn't set at view creation, but..
            try {
                setRunFile(runFile);
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            }
        }
        //Just a firendly warning message..no point adding this view if you're not going to use it!
        if (runDataStream == null) {
            System.out.println("Warning: AllOutput view added, but no output stream specified!");
        }
        scapeChanged();


    }

    /**
     * Writes the headers for the run parameters to the run data stream or file.
     * Data is output as tab delimited ASCII text, with the selected
     * "EOLSeparator" separating each period.
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public void writeRunHeader() throws IOException {
        throw new RuntimeException("WriteRunHeader was called... it shouldn't be.");
    }

    /**
     * Writes the current run parameters to the data stream or file. Data is
     * output as tab delimited ASCII text, with the selected "EOLSeparator"
     * separating each period.
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public void writeRunData() throws IOException {
        // add to the run file in proper xml format, <model name="Food Distribution" systemTime="0514865115"> with closing </model>
        //        runDataStream.writeBytes("<?xml version='1.0' encoding='utf-8'?>" + EOLString);

        rootElement.setAttribute("name", getScape().getRoot().getName());
        rootElement.setAttribute("systemTime", new Long(startTime).toString());

        // write model parameters
        Element modelParametersElement = buildAndAddElement("modelParameters", rootElement);
        buildAndAddParameterElement("runCount", new Integer(runCount), modelParametersElement);
        buildAndAddParameterElement("startPeriod", new Integer(scape.getStartPeriod()), modelParametersElement);
        buildAndAddParameterElement("stopPeriod", new Integer(scape.getPeriod()), modelParametersElement);
        buildAndAddParameterElement("randomSeed", new Long(scape.getRandomSeed()), modelParametersElement);

        List accessors = scape.retrieveAllAccessorsOrdered();
        for (Iterator propertyAccessorIt = accessors.iterator(); propertyAccessorIt.hasNext();) {
            PropertyAccessor accessor = (PropertyAccessor) propertyAccessorIt.next();
            if (outputDataStrings != null) {
                // if dataoutputset is being used, check it.
                if (matches(outputDataStrings, accessor.getName())) {
                    buildAndAddParameterElement(accessor.getName(), getAccessorString(accessor), modelParametersElement);
                }
            } else {
                buildAndAddParameterElement(accessor.getName(), getAccessorString(accessor), modelParametersElement);
            }
        }

        // todo: note - right now, not including stats; not sure if they're necessary, since it'll be only 1 value
        // if want to do it, since DataOutputView.writePeriodData - that includes that stats

        // done with model params
        // write agent data
        Element agentDataElement = buildAndAddElement("agentData", rootElement);
        //        runDataStream.writeBytes(tab+"<agentData>" + EOLString);
        if (scape.getPrototypeAgent() instanceof Scape) {
            for (Iterator scapeIt = scape.iterator(); scapeIt.hasNext();) {
                // todo: do this recursively, in case there are multiple layers of members
                Scape member = (Scape) scapeIt.next();
                Element membersElement = buildAndAddElement("members", agentDataElement);
                membersElement.setAttribute("name", member.getName());
                //                runDataStream.writeBytes(tab+tab+"<members name=\""+member.getName()+"\" />" + EOLString);
                for (Iterator memberIt = member.iterator(); memberIt.hasNext();) {
                    LocatedAgent agent = (LocatedAgent) memberIt.next();
                    Element memElement = buildAndAddElement("member", membersElement);
                    //                    Coordinate coordinate = agent.getCoordinate();
                    //                    if (agent instanceof CellOccupant) {
                    //                        coordinate = ((CellOccupant) agent).getHostCell().getCoordinate();
                    // memElement.setAttribute("name", "coordinate");
                    // memElement.setAttribute("value", coordinate.toString());
                    //                    }
                    //                    runDataStream.writeBytes(tab+tab+tab+"<member coordinate=\""+coordinate+"\" >"+ EOLString);
                    List agentAccessors = retrieveAgentAccessors(agent);
                    for (Iterator agentAccessorsIt = agentAccessors.iterator(); agentAccessorsIt.hasNext();) {
                        PropertyAccessor accessor = (PropertyAccessor) agentAccessorsIt.next();
                        buildAndAddParameterElement(accessor.getName(), getAccessorString(accessor), memElement);
                    }
                }
            }
        } else {
            for (Iterator memberIt = scape.iterator(); memberIt.hasNext();) {
                Element membersElement = buildAndAddElement("members", agentDataElement);
                membersElement.setAttribute("name", scape.getName());
                LocatedAgent agent = (LocatedAgent) memberIt.next();
                Element memElement = buildAndAddElement("member", membersElement);
                memElement.setAttribute("name", "coordinate");
                memElement.setAttribute("value", agent.getCoordinate().toString());
                List agentAccessors = retrieveAgentAccessors(agent);
                for (Iterator agentAccessorsIt = agentAccessors.iterator(); agentAccessorsIt.hasNext();) {
                    PropertyAccessor accessor = (PropertyAccessor) agentAccessorsIt.next();
                    buildAndAddParameterElement(accessor.getName(), getAccessorString(accessor), memElement);
                }
            }
        }
    }

    // done this way since contains is case sensitive
    /**
     * Matches.
     * 
     * @param outputDataStrings
     *            the output data strings
     * @param accessorName
     *            the accessor name
     * @return true, if successful
     */
    private boolean matches(List outputDataStrings, String accessorName) {
        for (Iterator iterator = outputDataStrings.iterator(); iterator.hasNext();) {
            String data = (String) iterator.next();
            if (data.equalsIgnoreCase(accessorName)) {
                return true;
            }
        }
        return false;

    }

    /**
     * Builds the and add element.
     * 
     * @param name
     *            the name
     * @param superElement
     *            the super element
     * @return the element
     */
    private Element buildAndAddElement(String name, Element superElement) {
        Element e = new Element(name);
        superElement.addContent(e);
        return e;
    }

    /**
     * Builds the and add parameter element.
     * 
     * @param paramName
     *            the param name
     * @param paramValue
     *            the param value
     * @param parent
     *            the parent
     */
    private void buildAndAddParameterElement(String paramName, Object paramValue, Element parent) {
        Element parameterEl = new Element("parameter");
        parameterEl.setAttribute("name", paramName);
        parameterEl.setAttribute("value", paramValue.toString());
        parent.addContent(parameterEl);
    }

    /**
     * Gets the accessor string.
     * 
     * @param accessor
     *            the accessor
     * @return the accessor string
     */
    private String getAccessorString(PropertyAccessor accessor) {
        String paramValue = "";
        if (accessor.getValue() instanceof Number) {
            paramValue = Utility.formatToString(((Number) accessor.getValue()).doubleValue(), 4);
        } else if (accessor.getValue() instanceof Boolean) {
            if (((Boolean) accessor.getValue()).booleanValue() == false) {
                paramValue = "False";
            } else {
                paramValue = "True";
            }
        } else {
            //For the moment, we don't want to print out string setting names..
            //System.out.println(Utility.padStringRight(accessors[i].getLongName() + ":", longestNameLength + 3) + ((String) accessors[i].getValue()));
        }
        return paramValue;
    }

    /**
     * Retrieve agent accessors.
     * 
     * @param agent
     *            the agent
     * @return the list
     */
    private List retrieveAgentAccessors(LocatedAgent agent) {
        List agentAccessors = null;
        try {
            agentAccessors = PropertyAccessor.determineReadWriteAccessors(agent, Agent.class, true);
        } catch (IntrospectionException e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
        return agentAccessors;
    }

    /**
     * On scape stop, writes the run parameters to the file.
     * 
     * @param scapeEvent
     *            the scape event
     * @see DataOutputView#scapeStarted
     * @see DataOutputView#scapeStopped
     */
    public void scapeStopped(ScapeEvent scapeEvent) {
        try {
            writeRunData();
            FileOutputStream stream = new FileOutputStream(runFile);
            XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
            out.output(new Document(rootElement), stream);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
    }

    /**
     * For now, not writing Period data.
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public void writePeriodHeader() throws IOException {
        // no op
    }

    /**
     * For now, not writing Period data.
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public void writePeriodData() throws IOException {
        // no op
    }

    /**
     * Adds the output data.
     * 
     * @param element
     *            the element
     */
    public void addOutputData(OutputDataElement element) {
        // not the most elegant approach, but since the output data elements are not mandatory..
        if (outputDataStrings == null) {
            outputDataStrings = new ArrayList();
        }
        outputDataStrings.add(element.getName());
    }
}
