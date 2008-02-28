/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.engine;


import java.io.Serializable;

import org.ascape.model.rule.Rule;


/**
 * The Class DefaultRuleSelector.
 */
public class DefaultRuleSelector implements RuleSelector, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * The rules.
     */
    Object[] rules;
    
    /**
     * The i.
     */
    int i;

    /**
     * Instantiates a new default rule selector.
     */
    public DefaultRuleSelector() {
    }

    /**
     * Instantiates a new default rule selector.
     * 
     * @param rules
     *            the rules
     */
    public DefaultRuleSelector(Object[] rules) {
        this.rules = rules;
    }

    /* (non-Javadoc)
     * @see org.ascape.model.engine.RuleSelector#hasMoreRules()
     */
    public boolean hasMoreRules() {
        return i < rules.length;
    }

    /* (non-Javadoc)
     * @see org.ascape.model.engine.RuleSelector#nextRule()
     */
    public Rule nextRule() {
        return (Rule) rules[i++];
    }

    /**
     * Gets the current rule.
     * 
     * @return the current rule
     */
    public Rule getCurrentRule() {
        return (Rule) rules[0];
    }

    /* (non-Javadoc)
     * @see org.ascape.model.engine.Selector#reset()
     */
    public void reset() {
        i = 0;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    public Object clone() {
        try {
            RuleSelector clone = (RuleSelector) super.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e.toString());  //To change body of catch statement use Options | File Templates.
        }
    }
}
