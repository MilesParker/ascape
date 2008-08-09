/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */
package org.ascape.model.event;

import java.util.EventObject;
import java.util.Iterator;
import java.util.Vector;

import org.ascape.util.VectorSelection;
import org.ascape.util.vis.PlatformDrawFeature;
import org.ascape.util.vis.PlatformDrawFeatureSelection;

/**
 * User: jmiller Date: Jun 12, 2006 Time: 1:21:05 PM To change this template use
 * File | Settings | File Templates.
 */
public class DrawFeatureEvent extends EventObject {

//    transient DrawFeatureSelection drawFeatureSelection;
//    DrawFeatureSelection drawFeatureSelection;

//    ArrayList selectedDrawFeaturesByName;

    /**
     * 
     */
    private static final long serialVersionUID = 1L;


    /**
 * The df name selection.
 */
VectorSelection dfNameSelection;


    /**
     * A flag for if this worker generated this event - see
     * GridWorker.respondDrawFeatureMessage
     */
    boolean originator;

    /**
     * Instantiates a new draw feature event.
     * 
     * @param source
     *            the source
     * @param drawFeatureSelection
     *            the draw feature selection
     */
    public DrawFeatureEvent(Object source, PlatformDrawFeatureSelection drawFeatureSelection) {
        this(source, drawFeatureSelection, false);
    }

    /**
     * Instantiates a new draw feature event.
     * 
     * @param source
     *            the source
     * @param drawFeatureSelection
     *            the draw feature selection
     * @param originator
     *            the originator
     */
    public DrawFeatureEvent(Object source, PlatformDrawFeatureSelection drawFeatureSelection, boolean originator) {
        super(source);
//        selectedDrawFeaturesByName = new ArrayList(drawFeatureSelection.getSelectionSize());
        dfNameSelection = new VectorSelection(new Vector());
        for (Iterator iterator = drawFeatureSelection.getVector().iterator(); iterator.hasNext();) {
            PlatformDrawFeature drawFeature = (PlatformDrawFeature) iterator.next();
            boolean selected = drawFeatureSelection.isSelected(drawFeature);
            dfNameSelection.addElement(drawFeature.getName(), selected);
        }
        this.originator = originator;
    }

    /**
     * Instantiates a new draw feature event.
     * 
     * @param source
     *            the source
     * @param tempSelection
     *            the temp selection
     * @param originator
     *            the originator
     */
    public DrawFeatureEvent(Object source, VectorSelection tempSelection, boolean originator) {
        super(source);
        this.dfNameSelection = tempSelection;
        this.originator = originator;
    }

//    public ArrayList getSelectedDrawFeaturesByName() {
//        return selectedDrawFeaturesByName;
//    }

    /**
 * Gets the df name selection.
 * 
 * @return the df name selection
 */
public VectorSelection getDfNameSelection() {
        return dfNameSelection;
    }

    /**
     * Checks if is originator.
     * 
     * @return true, if is originator
     */
    public boolean isOriginator() {
        return originator;
    }

    /**
     * Sets the originator.
     * 
     * @param originator
     *            the new originator
     */
    public void setOriginator(boolean originator) {
        this.originator = originator;
    }

//    public String toString() {
//        return "DrawFeatureEvent: source: " + source+", Selected Draw Features: " + selectedDrawFeaturesByName;
//    }
}
