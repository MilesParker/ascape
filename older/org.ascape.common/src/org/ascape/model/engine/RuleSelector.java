/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.engine;

import org.ascape.model.rule.Rule;

/*
 * User: Miles Parker  
 * Date: Sep 23, 2003
 * Time: 1:11:26 PM
 * To change this template use Options | File Templates.
 */

/**
 * The Interface RuleSelector.
 */
public interface RuleSelector extends Selector {

    /**
     * Checks for more rules.
     * 
     * @return true, if successful
     */
    public abstract boolean hasMoreRules();

    /**
     * Next rule.
     * 
     * @return the rule
     */
    public abstract Rule nextRule();
//        public Rule[] getRules();
//        public void setRules(Rule[] rules);
}

