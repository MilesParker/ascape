/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.engine;

import org.ascape.model.rule.Rule;


/**
 * The Class NoIterateAllRuleSelector.
 */
public class NoIterateAllRuleSelector extends FilteredRuleSelector {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new no iterate all rule selector.
     * 
     * @param rules
     *            the rules
     */
    public NoIterateAllRuleSelector(Object[] rules) {
        super(rules);
    }

    /* (non-Javadoc)
     * @see org.ascape.model.engine.FilteredRuleSelector#findNextIndex()
     */
    void findNextIndex() {
        do {
            i++;
        } while ((i < rules.length) && (((Rule) rules[i]).isIterateAll()));
    }
}
