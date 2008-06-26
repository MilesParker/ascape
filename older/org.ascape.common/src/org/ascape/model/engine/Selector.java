/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.engine;

/*
 * User: Miles Parker  
 * Date: Sep 23, 2003
 * Time: 1:09:53 PM
 * To change this template use Options | File Templates.
 */

/**
 * The Interface Selector.
 */
public interface Selector extends Cloneable {

    /**
     * Reset.
     */
    public abstract void reset();

    /**
     * Clone.
     * 
     * @return the object
     */
    public Object clone();
}
