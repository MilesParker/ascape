/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */
package org.ascape.query;

/**

 * User: jmiller
 * Date: Jan 20, 2005
 * Time: 11:42:32 AM
 * To change this template use Options | File Templates.
 */
public class ParserFailureException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public ParserFailureException() {
        super();
    }

    public ParserFailureException(String message) {
        super(message);
    }
}
