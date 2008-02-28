/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.sugarscape;

import org.ascape.view.nonvis.DataOutputView;

// Implements rule TradeT4
// neighbors ordered randomly
// Agent and eighbor make offers and counter offers
// based on their own Marginal rates of substitution
// a negotiating factor (to scale offers), and a
// personal max number of offer iterations they will go through
// Requires the directory C:\ResultData to exist

public class MultipleGAS_IV_d extends GAS_IV_d {

    /**
     * 
     */
    private static final long serialVersionUID = -4768391685811507753L;

    public void createViews() {
        DataOutputView dataView = new DataOutputView();
        sugarscape.addView(dataView);
        try {
            setStopPeriod(100);
        } catch (org.ascape.model.space.SpatialTemporalException e) {
            throw new RuntimeException("Internal Logic Error");
        }
        //dataView.getSweep().setRunsPer(2);
        setViewSelf(true);
    }
}
