/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */
package org.ascape.ant;

import org.ascape.model.Scape;


/**
 * The Interface ViewElement.
 */
public interface ViewElement {
    
    /**
     * Adds the to scape.
     * 
     * @param scape
     *            the scape
     */
    public void addToScape(Scape scape);
}
