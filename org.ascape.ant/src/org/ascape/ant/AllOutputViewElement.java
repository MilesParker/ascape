/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.ant;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ascape.model.Scape;

/**
 User: jmiller Date: Nov 2, 2005 Time: 2:04:19 PM To
 * change this template use Options | File Templates.
 */

// copied from DataOutputViewElement - not sure if I could just change it to extend DataOutputViewElement instead.
public class AllOutputViewElement extends AllOutputView implements Serializable, ViewElement {

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

    /**
     * The output data elements.
     */
    List outputDataElements;

    /**
     * Instantiates a new all output view element.
     */
    public AllOutputViewElement() {
        outputDataElements = new ArrayList();
    }

    /* (non-Javadoc)
     * @see org.ascape.view.nonvis.AllOutputView#addOutputData(org.ascape.ant.OutputDataElement)
     */
    public void addOutputData(OutputDataElement element) {
        outputDataElements.add(element);
    }

    /* (non-Javadoc)
     * @see org.ascape.ant.ViewElement#addToScape(org.ascape.model.Scape)
     */
    public void addToScape(Scape scape) {
        // putting this code here, since I'm not sure exactly when Ant "sets" the element (ie calls setName).
        // if I call it from addOutputData here, the name isn't yet set.
        for (Iterator iterator = outputDataElements.iterator(); iterator.hasNext();) {
            OutputDataElement element = (OutputDataElement) iterator.next();
            super.addOutputData(element);
        }
        scape.addView(this);
    }

//    public String getRunRelativeName() {
//        return runRelativeName;
//    }
//
//    public void setRunRelativeName(String runRelativeName) {
//        this.runRelativeName = runRelativeName;
//    }
//
//    public String getPeriodRelativeName() {
//        return periodRelativeName;
//    }
//
//    public void setPeriodRelativeName(String periodRelativeName) {
//        this.periodRelativeName = periodRelativeName;
//    }
//
//    public void setRootDirestory(String rootDirestory) {
//        this.rootDirestory = rootDirestory;
//    }
}
