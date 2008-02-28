/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.sugarscape;

import org.ascape.view.nonvis.DataOutputView;

// Implements rule TradeT3
// solicits offers from neighbors, gets best offer, will trade
// with all neighbors at that price
// Requires the directory C:\ResultData to exist

public class MultipleGAS_IV_c extends GAS_IV_c {

    /**
     * 
     */
    private static final long serialVersionUID = -3370656485202228991L;

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
