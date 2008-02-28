/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.rule;


/**
 * A rule that executes on the target scape, and any member scapes of the target
 * scape. Typically used for scape control.
 * 
 * @author Miles Parker
 * @version 1.0 3/22/99
 * @since 1.0
 */
public class PropogateScapeOnly extends Propogate {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a Propogate rule.
     */
    public PropogateScapeOnly() {
        super("Unnamed");
    }

    /**
     * Constructs a propogate rule with the provided name. It is strongly
     * encouraged to provide a name for all rules.
     * 
     * @param name
     *            the name of this object
     */
    public PropogateScapeOnly(String name) {
        super(name);
    }

    /**
     * Returns true, of course.
     * 
     * @return true, if is scape only
     */
    public boolean isScapeOnly() {
        return true;
    }
}

