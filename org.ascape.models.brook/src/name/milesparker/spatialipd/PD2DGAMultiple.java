/*
 * Copyright 2000 Miles T. Parker. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package name.milesparker.spatialipd;

import java.io.File;
import java.io.IOException;

import org.ascape.util.sweep.SweepDimension;
import org.ascape.util.sweep.SweepLink;
import org.ascape.view.nonvis.ConsoleOutView;
import org.ascape.view.nonvis.DataOutputView;
import org.ascape.view.nonvis.SweepControlView;


public class PD2DGAMultiple extends PD2DGA {

    /**
     * 
     */
    private static final long serialVersionUID = -6376060604942893005L;

    public void createViews() {
        //super.createViews();
        DataOutputView dataView = new DataOutputView() {
            /**
             * 
             */
            private static final long serialVersionUID = -8079291156256729800L;

            public void writeRunHeader() throws IOException {
                super.writeRunHeader();
                runDataStream.writeBytes("\t");
                runDataStream.writeBytes("Cooperate Ratio");
                runDataStream.writeBytes("\t");
                runDataStream.writeBytes("Defect Ratio");
                runDataStream.writeBytes("\t");
                runDataStream.writeBytes("Cooperate Closest");
                runDataStream.writeBytes("\t");
                runDataStream.writeBytes("Cooperate Farthest");
                runDataStream.writeBytes("\t");
                runDataStream.writeBytes("Defect Closest");
                runDataStream.writeBytes("\t");
                runDataStream.writeBytes("Defect Farthest");
            }

            public void writeRunData() throws IOException {
                super.writeRunData();
                runDataStream.writeBytes("\t");
                runDataStream.writeBytes(Double.toString(getDataSelection().getData().getSeries("Sum Cooperate Ratio").getValue()));
                runDataStream.writeBytes("\t");
                runDataStream.writeBytes(Double.toString(getDataSelection().getData().getSeries("Sum Defect Ratio").getValue()));
                runDataStream.writeBytes("\t");
                runDataStream.writeBytes(Double.toString(getDataSelection().getData().getSeries("Count Cooperate Closest").getValue()));
                runDataStream.writeBytes("\t");
                runDataStream.writeBytes(Double.toString(getDataSelection().getData().getSeries("Count Cooperate Farthest").getValue()));
                runDataStream.writeBytes("\t");
                runDataStream.writeBytes(Double.toString(getDataSelection().getData().getSeries("Count Defect Closest").getValue()));
                runDataStream.writeBytes("\t");
                runDataStream.writeBytes(Double.toString(getDataSelection().getData().getSeries("Count Defect Farthest").getValue()));
            }
        };
        addView(dataView);
        getAgents().getRules().setSelected("Play Preference", true);
        try {
            dataView.setRunFile(new File("PlayPreference.txt"));
        } catch (IOException e) {
            throw new RuntimeException(e.toString());
        }
        addView(new ConsoleOutView());
        SweepControlView sweep = new SweepControlView();
        SweepLink doNothing = new SweepLink();
        doNothing.addMember(new SweepDimension(this, "MemorySize", 1, 1));
        sweep.getSweepGroup().setRunsPer(35);
        sweep.getSweepGroup().addMember(doNothing);
        addView(sweep);
        try {
            setStopPeriod(1000);
        } catch (org.ascape.model.space.SpatialTemporalException e) {
        }
        //We don't need to export historic data, and we want to match current C++ output
        dataView.getDataSelection().setSelected("Sum Cooperate Ratio", false);
        dataView.getDataSelection().setSelected("Sum Defect Ratio", false);
        dataView.getDataSelection().setSelected("Count Cooperate Closest", false);
        dataView.getDataSelection().setSelected("Count Cooperate Farthest", false);
        dataView.getDataSelection().setSelected("Count Defect Closest", false);
        dataView.getDataSelection().setSelected("Count Defect Farthest", false);
        setViewSelf(true);
    }
}
