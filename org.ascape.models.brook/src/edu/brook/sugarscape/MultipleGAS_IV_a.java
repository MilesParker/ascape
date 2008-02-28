/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.sugarscape;

import org.ascape.view.nonvis.DataOutputView;

// Trade rule as in Growing Artificual Societies
// Requires the directory C:\ResultData to exist

public class MultipleGAS_IV_a extends GAS_IV_a {

    /**
     * 
     */
    private static final long serialVersionUID = -5518795130564975048L;

    public void createViews() {
        DataOutputView dataView = new DataOutputView();
        sugarscape.addView(dataView);
        try {
            setStopPeriod(1000);
        } catch (org.ascape.model.space.SpatialTemporalException e) {
            throw new RuntimeException("Internal Logic Error");
        }
        //dataView.getSweep().setRunsPer(2);
        setViewSelf(true);
    }
}
