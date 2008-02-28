/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.engine;

import org.ascape.model.rule.ExecuteThenUpdate;


/**
 * The Class RuleOrderUpdateMutableStrategy.
 */
public class RuleOrderUpdateMutableStrategy extends RuleOrderMutableStrategy {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new rule order update mutable strategy.
     * 
     * @param factory
     *            the factory
     */
    public RuleOrderUpdateMutableStrategy(StrategyFactory factory) {
        super(factory);
    }

    /* (non-Javadoc)
     * @see org.ascape.model.engine.IncrementalExecutionStrategy#fire()
     */
    public void fire() {
        ((ExecuteThenUpdate) currentRule).update(currentAgent);
    }

    /* (non-Javadoc)
     * @see org.ascape.model.engine.RuleOrderMutableStrategy#isSupportsParallel()
     */
    public final boolean isSupportsParallel() {
        return true;
    }
}
