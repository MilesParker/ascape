/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.engine;

import org.ascape.model.rule.Rule;


/**
 * The Class PartialRuleSelector.
 */
public class PartialRuleSelector extends FilteredRuleSelector {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * The agent selector.
     */
    private FlaggedTourAgentSelector agentSelector;

    /**
     * Instantiates a new partial rule selector.
     * 
     * @param rules
     *            the rules
     * @param agentSelector
     *            the agent selector
     */
    public PartialRuleSelector(Object[] rules, FlaggedTourAgentSelector agentSelector) {
        this.rules = rules;
        this.agentSelector = agentSelector;
        reset();
    }

    /* (non-Javadoc)
     * @see org.ascape.model.engine.FilteredRuleSelector#findNextIndex()
     */
    void findNextIndex() {
        do {
            i++;
        } while ((i < rules.length) && !agentSelector.hasMorePartialAgents() && !(((Rule) rules[i]).isIterateAll()));
    }

}
