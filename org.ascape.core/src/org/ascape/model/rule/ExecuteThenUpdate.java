/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.rule;

import org.ascape.model.Agent;

/**
 * A rule that should be executed in two phases; executing (calculating), and
 * then updating. Assumes by rule execution, allowing for a form of by rule
 * synchronous operation. (Please let me know if you can think of any reason why
 * this might be useful in by agent execution.) See subclass Diffusion for the
 * <i>raison d'être</i> of this class.
 * 
 * @author Miles Parker
 * @version 1.0
 * @since 1.0
 */
public abstract class ExecuteThenUpdate extends Rule {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs an execute and update rule.
     */
    public ExecuteThenUpdate() {
        super("Unnamed");
    }

    /**
     * Constructs an execute and update rule with the provided name. It is
     * strongly encouraged to provide a name for all rules.
     * 
     * @param name
     *            the name of this object
     */
    public ExecuteThenUpdate(String name) {
        super(name);
    }

    /**
     * Execute phase of the rule. Here you perform a calculation based on
     * dependent values in other cells.
     * 
     * @param agent
     *            the playing agent
     */
    public abstract void execute(Agent agent);

    /**
     * Execute update phase. Here you update the cell's values, after all cells
     * have had there next step values calculated.
     * 
     * @param agent
     *            the playing agent
     */
    public abstract void update(Agent agent);
}
