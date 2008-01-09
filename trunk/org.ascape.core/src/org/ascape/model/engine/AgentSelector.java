/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.engine;

import org.ascape.model.Agent;

/*
 * User: Miles Parker
 * Date: Sep 23, 2003
 * Time: 1:10:49 PM
 * To change this template use Options | File Templates.
 */

/**
 * The Interface AgentSelector.
 */
public interface AgentSelector extends Selector {

    /**
     * Checks for more agents.
     * 
     * @return true, if successful
     */
    public boolean hasMoreAgents();

    /**
     * Next agent.
     * 
     * @return the agent
     */
    public Agent nextAgent();

    /**
     * Gets the strategy.
     * 
     * @return the strategy
     */
    public IncrementalExecutionStrategy getStrategy();

    /**
     * Sets the strategy.
     * 
     * @param strategy
     *            the new strategy
     */
    public void setStrategy(IncrementalExecutionStrategy strategy);
}






