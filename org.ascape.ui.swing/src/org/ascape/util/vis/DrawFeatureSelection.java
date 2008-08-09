/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.util.vis;

import java.util.Iterator;

import org.ascape.model.Scape;
import org.ascape.util.VectorSelection;

/**
 * A (probably temporary) class for handling DrawFeature Observation.
 *
 * @author Miles Parker
 * @version 1.2.6 10/26/99
 * @since 1.2.6
 */
public class DrawFeatureSelection extends PlatformDrawFeatureSelection {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Scape scape;

    /**
     * Construct a new DrawFeatureSelection.
     * @param scape the scape holding the draw feature inforamtion
     */
    public DrawFeatureSelection(Scape scape) {
        super(scape);
    }

}
