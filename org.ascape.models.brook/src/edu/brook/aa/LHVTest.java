/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.aa;

import java.io.File;
import java.io.IOException;

import org.ascape.runtime.Runner;
import org.ascape.util.data.StatCollector;
import org.ascape.util.data.StatCollectorCalculated;
import org.ascape.util.sweep.SweepDimension;
import org.ascape.util.sweep.SweepLink;
import org.ascape.view.nonvis.DataOutputView;
import org.ascape.view.nonvis.SweepControlView;


public class LHVTest extends LHV {

    /**
     * 
     */
    private static final long serialVersionUID = 8839050509811991896L;

    public void createScape() {
        Runner.setDisplayGraphics(false);
        super.createScape();
    }

    public void createNonGraphicViews() {
        super.createNonGraphicViews();
        //BatchView batchView = new BatchView();
        //addView(batchView);
        //SweepControlView sweeper = batchView.getSweepView();
        SweepControlView sweeper = new SweepControlView();
        addView(sweeper);
        SweepLink fertilityAgeDim = new SweepLink();
        fertilityAgeDim.addMember(new SweepDimension(this, "MinFertilityAge", 13, 16));
        fertilityAgeDim.addMember(new SweepDimension(this, "MaxFertilityAge", 19, 16, -1));
        SweepLink fertilityEndsAgeDim = new SweepLink();
        fertilityEndsAgeDim.addMember(new SweepDimension(this, "MinFertilityEndsAge", 26, 30));
        fertilityEndsAgeDim.addMember(new SweepDimension(this, "MaxFertilityEndsAge", 34, 30, -1));
        SweepLink fertilityDim = new SweepLink();
        fertilityDim.addMember(new SweepDimension(this, "MinFertility", 0.09, 0.1251, .0025));
        fertilityDim.addMember(new SweepDimension(this, "MaxFertility", 0.16, 0.1249, -.0025));
        SweepLink deathDim = new SweepLink();
        deathDim.addMember(new SweepDimension(this, "MinDeathAge", 26, 30));
        deathDim.addMember(new SweepDimension(this, "MaxDeathAge", 34, 30, -1));
        sweeper.getSweepGroup().addMember(fertilityAgeDim);
        sweeper.getSweepGroup().addMember(fertilityEndsAgeDim);
        sweeper.getSweepGroup().addMember(fertilityDim);
        sweeper.getSweepGroup().addMember(deathDim);
        sweeper.getSweepGroup().setRunsPer(5);

        final StatCollector householdCount = getData().getStatCollector("Historic Households");
        addStatCollector(new StatCollectorCalculated("Difference Squared") {
            /**
             * 
             */
            private static final long serialVersionUID = -7962434131328292911L;

            public double calculateValue() {
                return (householdCount.getCount());
            }
        });


        //DataOutputView dataView = batchView.getDataView();
        DataOutputView dataView = new DataOutputView();
        addView(dataView);
        try {
            setStartPeriod(800);
            setStopPeriod(1350);
        } catch (org.ascape.model.space.SpatialTemporalException e) {
            System.out.println("Bad start/stop periods: " + e);
        }
        try {
            dataView.setRunFile(new File("TestRMS.txt"));
        } catch (IOException e) {
            throw new RuntimeException(e.toString());
        }
    }
}
