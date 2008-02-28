/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.ant;

import java.io.Serializable;

import org.ascape.model.Scape;
import org.ascape.view.nonvis.DataOutputView;



/**
 * The Class DataOutputViewElement.
 */
public class DataOutputViewElement extends DataOutputView implements Serializable, ViewElement {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * The root direstory.
     */
    String rootDirestory;

    /**
     * The run relative name.
     */
    String runRelativeName;

    /**
     * The period relative name.
     */
    String periodRelativeName;

    /* (non-Javadoc)
     * @see org.ascape.ant.ViewElement#addToScape(org.ascape.model.Scape)
     */
    public void addToScape(Scape scape) {
        scape.addView(this);
    }

    /**
     * Gets the run relative name.
     * 
     * @return the run relative name
     */
    public String getRunRelativeName() {
        return runRelativeName;
    }

    /**
     * Sets the run relative name.
     * 
     * @param runRelativeName
     *            the new run relative name
     */
    public void setRunRelativeName(String runRelativeName) {
        this.runRelativeName = runRelativeName;
    }

    /**
     * Gets the period relative name.
     * 
     * @return the period relative name
     */
    public String getPeriodRelativeName() {
        return periodRelativeName;
    }

    /**
     * Sets the period relative name.
     * 
     * @param periodRelativeName
     *            the new period relative name
     */
    public void setPeriodRelativeName(String periodRelativeName) {
        this.periodRelativeName = periodRelativeName;
    }

    /**
     * Sets the root direstory.
     * 
     * @param rootDirestory
     *            the new root direstory
     */
    public void setRootDirestory(String rootDirestory) {
        this.rootDirestory = rootDirestory;
    }
}
