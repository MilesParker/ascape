/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc.,
 * Metascape LLC, and contributors. All rights reserved. This program and the
 * accompanying materials are made available solely under of the BSD license
 * "ascape-license.txt". Any referenced or included libraries carry licenses of
 * their respective copyright holders.
 */

package org.ascape.model.engine;

import org.ascape.model.space.Mutable;

/**
 * The Class AgentOrderMutableStrategy.
 */
public class AgentOrderMutableStrategy extends AgentOrderStrategy {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new agent order mutable strategy.
     * 
     * @param factory the factory
     */
    public AgentOrderMutableStrategy(StrategyFactory factory) {
        super(factory);
    }

    /*
     * (non-Javadoc)
     * @see org.ascape.model.engine.AgentOrderStrategy#hasNext()
     */
    public boolean hasNext() {
        while ((currentAgent != null) && currentAgent.isDelete()) {
            // Only the first call to reset is neccessary, but it should be rare
            // for this loop to be called more than once,
            // and there are no harmful effects except performance cost for
            // calling mutliple time
            ruleSelector.reset();
            if (agentSelector.hasMoreAgents()) {
                currentAgent = agentSelector.nextAgent();
            } else {
                return false;
            }
        }
        return super.hasNext();
    }

    /*
     * (non-Javadoc)
     * @see org.ascape.model.engine.AgentOrderStrategy#reset()
     */
    public void reset() {
        super.reset();
        if (getScape().getSpace() instanceof Mutable) {
            ((Mutable) getScape().getSpace()).deleteSweep();
        }
    }

    /*
     * (non-Javadoc)
     * @see org.ascape.model.engine.AgentOrderStrategy#isSupportsParallel()
     */
    public boolean isSupportsParallel() {
        return !factory.isAnyRandom();// && !factory.isAnyDelete();
    }
}
