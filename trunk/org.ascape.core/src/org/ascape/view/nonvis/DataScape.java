/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */
package org.ascape.view.nonvis;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * The Interface DataScape.
 */
public interface DataScape {
    
    /**
     * Write period data.
     * 
     * @param os
     *            the os
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public void writePeriodData(DataOutputStream os) throws IOException;

    /**
     * Write period header.
     * 
     * @param os
     *            the os
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public void writePeriodHeader(DataOutputStream os) throws IOException;

    /**
     * Write run data.
     * 
     * @param os
     *            the os
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public void writeRunData(DataOutputStream os) throws IOException;

    /**
     * Write run header.
     * 
     * @param os
     *            the os
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public void writeRunHeader(DataOutputStream os) throws IOException;
}
