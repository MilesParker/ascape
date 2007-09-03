/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.aa;

import java.io.File;
import java.io.IOException;

import org.ascape.model.ModelRoot;
import org.ascape.model.event.ScapeEvent;
import org.ascape.util.data.DataSeries;
import org.ascape.util.data.StatCollector;
import org.ascape.util.data.StatCollectorCSA;
import org.ascape.util.data.StatCollectorCalculated;
import org.ascape.util.sweep.SweepDimension;
import org.ascape.util.sweep.SweepLink;
import org.ascape.view.nonvis.DataOutputView;
import org.ascape.view.nonvis.SweepControlView;

public class LHVRMS extends LHV {

    public void createScape() {
        ModelRoot.setDisplayGraphics(false);
        super.createScape();
    }

    public void createNonGraphicViews() {
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
        sweeper.getSweepGroup().setRunsPer(3);

        final StatCollectorCSA historicSum = (StatCollectorCSA) getData().getStatCollector("Historic Households");
        final StatCollector simulationCount = getData().getStatCollector("Households");
        final StatCollectorCalculated difference = new StatCollectorCalculated("Difference Squared") {
            public double calculateValue() {
                return (simulationCount.getCount() - historicSum.getSum());
            }
        };
        addStatCollector(difference);
        final StatCollectorCalculated differenceSquared = new StatCollectorCalculated("Difference Squared") {
            public double calculateValue() {
                return Math.pow((simulationCount.getCount() - historicSum.getSum()), 2.0);
            }
        };
        addStatCollector(differenceSquared);

        final DataSeries differenceSeries = difference.getDataSeries("Sum");
        final DataSeries differenceSquaredSeries = differenceSquared.getDataSeries("Sum");

        //DataOutputView dataView = batchView.getDataView();
        DataOutputView dataView = new DataOutputView() {
            public void writeRunHeader() throws IOException {
                super.writeRunHeader();
                runDataStream.writeBytes("Minimum Difference");
                runDataStream.writeBytes("\t");
                runDataStream.writeBytes("Maximum Difference");
                runDataStream.writeBytes("\t");
                runDataStream.writeBytes("Average Difference");
                runDataStream.writeBytes("\t");
                runDataStream.writeBytes("RMS");
                runDataStream.writeBytes("\t");
            }

            public void writeRunData() throws IOException {
                super.writeRunData();
                runDataStream.writeBytes(Double.toString(differenceSeries.getMin()));
                runDataStream.writeBytes("\t");
                runDataStream.writeBytes(Double.toString(differenceSeries.getMax()));
                runDataStream.writeBytes("\t");
                runDataStream.writeBytes(Double.toString(differenceSeries.getAvg()));
                runDataStream.writeBytes("\t");
                runDataStream.writeBytes(Double.toString(Math.sqrt(differenceSquaredSeries.getAvg())));
                runDataStream.writeBytes("\t");
            }

            public void scapeIterated(ScapeEvent scapeEvent) {
                super.scapeIterated(scapeEvent);
                if ((getPeriod() < 1000) && (difference.getSum() > 300)) {
                    getScape().getModel().stop();
                }
            }
        };
        addView(dataView);
        try {
            setStartPeriod(800);
            setStopPeriod(1350);
        } catch (org.ascape.model.space.SpatialTemporalException e) {
            System.out.println("Bad start/stop periods: " + e);
        }
        try {
            dataView.setRunFile(new File("TestRMSFinal.txt"));
        } catch (IOException e) {
            throw new RuntimeException(e.toString());
        }
    }
}
