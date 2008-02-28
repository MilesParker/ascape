/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.util.data;

/**
 * An exception thrown if the scape state would become inconsistent if
 * requested change is made.
 *
 * @author Miles Parker
 * @version 1.0
 * @since 1.0
 */
public class ValueNotInRangeException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public ValueNotInRangeException() {
        super();
    }

    public ValueNotInRangeException(String s) {
        super(s);
    }
}
