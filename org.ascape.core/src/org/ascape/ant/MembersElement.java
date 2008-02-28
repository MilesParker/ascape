/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.ant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * The Class MembersElement.
 */
public class MembersElement implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * The sub scapes.
     */
    private List subScapes;

    /**
     * The parent.
     */
    ScapeElement parent;
    
    /**
     * The scape element.
     */
    ScapeElement scapeElement;

    /**
     * Instantiates a new members element.
     */
    public MembersElement() {
        subScapes = new ArrayList();
    }

    /**
     * Adds the scape.
     * 
     * @param scapeElement
     *            the scape element
     */
    public void addScape(ScapeElement scapeElement) {
        this.scapeElement = scapeElement;
        scapeElement.parent = parent;
        subScapes.add(scapeElement);
    }

    /**
     * Gets the sub scapes.
     * 
     * @return the sub scapes
     */
    public List getSubScapes() {
        return subScapes;
    }
}
