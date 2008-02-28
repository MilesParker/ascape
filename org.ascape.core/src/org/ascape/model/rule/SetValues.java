/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.rule;

import org.ascape.model.Agent;
import org.ascape.model.Scape;
import org.ascape.util.ValueSetter;

/**
 * A rule for setting values for a population of agents. Typically this rule is
 * iused for assigning a set of exogenous values to a collection of agents. For
 * instance, this ruel might be used to set historical values each year for a
 * group of agents in a simulation.
 * 
 * @author Miles Parker
 * @version 1.0.3
 * @history 1.0.3 12/1/98 first in ascape 1.0.3
 * @since 1.0.3
 */
public class SetValues extends Rule {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * The setters for setting data points.
     */
    private ValueSetter[] setters;

    /**
     * Construct a new setter collection rule.
     */
    public SetValues() {
        super("Set Stored Values");
        setters = new ValueSetter[0];
    }

    /**
     * Sets the scape for this rule, adding any setters from the scapes
     * prototype agent.
     * 
     * @param scape
     *            the scape
     */
    public void setScape(Scape scape) {
        super.setScape(scape);
    }

    /**
     * Returns the setters used to collect setters from the scape.
     * 
     * @return the setters
     */
    public ValueSetter[] getSetters() {
        return setters;
    }

    /**
     * Adds setters to the collection rule. Typically not called directly.
     * 
     * @param addDataPoints
     *            the add data points
     */
    public void addSetters(ValueSetter[] addDataPoints) {
        ValueSetter[] newDataPoints = new ValueSetter[setters.length + addDataPoints.length];
        int i = 0;
        for (; i < setters.length; i++) {
            newDataPoints[i] = setters[i];
        }
        for (; i < newDataPoints.length; i++) {
            newDataPoints[i] = addDataPoints[i - setters.length];
        }
        setters = newDataPoints;
    }

    /**
     * Sets all values for the agent.
     * 
     * @param agent
     *            the target agent.
     */
    public void execute(Agent agent) {
        for (int i = 0; i < setters.length; i++) {
            setters[i].setValue(agent);
        }
    }

    /**
     * Returns false; it doesn't matter what order we set setters in.
     * 
     * @return true, if is random execution
     */
    public boolean isRandomExecution() {
        return false;
    }
}
