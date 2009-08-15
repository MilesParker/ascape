/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */
package org.ascape.ant;

import java.io.Serializable;

import org.ascape.model.Scape;
import org.ascape.view.nonvis.ScapeOutputView;


/*
 * User: Miles Parker
 * Date: Feb 10, 2005
 * Time: 10:22:58 AM
 */

/**
 * The Class ScapeOutputViewElement.
 */
public class ScapeOutputViewElement extends ScapeOutputView implements Serializable, ViewElement {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /* (non-Javadoc)
     * @see org.ascape.ant.ViewElement#addToScape(org.ascape.model.Scape)
     */
    public void addToScape(Scape scape) {
        scape.addView(this);
    }
}
