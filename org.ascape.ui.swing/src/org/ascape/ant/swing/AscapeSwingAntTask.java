/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.ant.swing;

import java.io.Serializable;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * The Class AscapeAntTask.
 */
public class AscapeSwingAntTask extends Task implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 30929709154741243L;

    /**
     * The scape elem.
     */
    private ScapeSwingElement scapeElem;

    /**
     * The root directory.
     */
    String rootDirectory;

    /**
     * Adds the scape.
     * 
     * @param scape
     *            the scape
     */
    public void addScape(ScapeSwingElement scape) {
        this.scapeElem = scape;
    }

    /* (non-Javadoc)
     * @see org.apache.tools.ant.Task#execute()
     */
    public void execute() throws BuildException {
        scapeElem.run();
    }

    /**
     * Gets the scape element.
     * 
     * @return the scape element
     */
    public ScapeSwingElement getScapeElement() {
        return scapeElem;
    }

    /**
     * Gets the root directory.
     * 
     * @return the root directory
     */
    public String getRootDirectory() {
        return rootDirectory;
    }

    /**
     * Sets the root directory.
     * 
     * @param rootDirectory
     *            the new root directory
     */
    public void setRootDirectory(String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }
}
