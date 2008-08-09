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
public class PlatformDrawFeatureSelection extends VectorSelection {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Scape scape;

    /**
     * Construct a new DrawFeatureSelection.
     * @param scape the scape holding the draw feature inforamtion
     */
    public PlatformDrawFeatureSelection(Scape scape) {
        super(scape.getDrawFeatures());
        scape.getDrawFeaturesObservable().addObserver(this);
        this.scape = scape;
    }

    /**
     * Moves the Draw Feature selected to the top of the list.
     * Note: This code is from ViewCustomizer.update()
     * @param drawFeature the Draw Feature to be moved to the top
     */
    public void moveToTop(PlatformDrawFeature drawFeature) {
        int selectedRow = vector.indexOf(drawFeature);
        if (selectedRow > 0) {
            for (int i = selectedRow - 1; i >= 0; i--) {
                Object swapFeature = vector.elementAt(i);
                vector.removeElementAt(i);
                vector.insertElementAt(swapFeature, i + 1);
            }
            scape.getDrawFeaturesObservable().notifyObservers();
            update();
        }
    }

    public void moveToBottom(PlatformDrawFeature drawFeature) {
        int selectedRow = vector.indexOf(drawFeature);
        if (selectedRow < vector.size()) {
            for (int i = selectedRow + 1; i < vector.size(); i++) {
                Object swapFeature = vector.elementAt(i);
                vector.removeElementAt(i);
                vector.insertElementAt(swapFeature, i - 1);
            }
            scape.getDrawFeaturesObservable().notifyObservers();
            update();
        }
    }

    public void moveUp(PlatformDrawFeature drawFeature) {
        int selectedRow = vector.indexOf(drawFeature);
        if (selectedRow > 0) {
            Object swapFeature = vector.elementAt(selectedRow - 1);
            vector.removeElementAt(selectedRow - 1);
            vector.insertElementAt(swapFeature, selectedRow);
            scape.getDrawFeaturesObservable().notifyObservers();
            update();
        }
    }

    public void moveDown(PlatformDrawFeature drawFeature) {
        int selectedRow = vector.indexOf(drawFeature);
        if (selectedRow < vector.size()) {
            Object swapFeature = vector.elementAt(selectedRow + 1);
            vector.removeElementAt(selectedRow + 1);
            vector.insertElementAt(swapFeature, selectedRow);
            scape.getDrawFeaturesObservable().notifyObservers();
            update();
        }
    }

    public PlatformDrawFeature findByName(String name) {
        PlatformDrawFeature found = null;
        for (Iterator iterator = vector.iterator(); iterator.hasNext();) {
            PlatformDrawFeature feature = (PlatformDrawFeature) iterator.next();
            if (feature.getName().equals(name)) {
                found = feature;
                break;
            }
        }
        return found;
    }

}
