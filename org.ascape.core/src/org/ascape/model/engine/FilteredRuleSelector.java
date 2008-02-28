/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.engine;

import org.ascape.model.rule.Rule;


/**
 * The Class FilteredRuleSelector.
 */
public abstract class FilteredRuleSelector extends DefaultRuleSelector {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new filtered rule selector.
     */
    public FilteredRuleSelector() {
        super();
    }

    /**
     * Instantiates a new filtered rule selector.
     * 
     * @param rules
     *            the rules
     */
    public FilteredRuleSelector(Object[] rules) {
        super(rules);
        reset();
    }

    /**
     * Find next index.
     */
    abstract void findNextIndex();

    /* (non-Javadoc)
     * @see org.ascape.model.engine.DefaultRuleSelector#hasMoreRules()
     */
    public boolean hasMoreRules() {
        return i < rules.length;
    }

    /* (non-Javadoc)
     * @see org.ascape.model.engine.DefaultRuleSelector#nextRule()
     */
    public Rule nextRule() {
        Rule rule = (Rule) rules[i];
        findNextIndex();
        return rule;
    }

    /* (non-Javadoc)
     * @see org.ascape.model.engine.DefaultRuleSelector#reset()
     */
    public void reset() {
        i = -1;
        findNextIndex();
    }
}
