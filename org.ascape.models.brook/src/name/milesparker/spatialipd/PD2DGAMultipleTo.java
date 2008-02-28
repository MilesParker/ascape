/*
 * Copyright 2000 Miles T. Parker. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package name.milesparker.spatialipd;

import java.io.File;
import java.io.IOException;

import org.ascape.model.event.ScapeEvent;
import org.ascape.util.sweep.SweepDimension;
import org.ascape.util.sweep.SweepLink;
import org.ascape.view.nonvis.ConsoleOutView;
import org.ascape.view.nonvis.DataOutputView;
import org.ascape.view.nonvis.SweepControlView;


public class PD2DGAMultipleTo extends PD2DGA {

    /**
     * 
     */
    private static final long serialVersionUID = 5819146031963269842L;

    public void createViews() {
        //super.createViews();
        DataOutputView dataView = new DataOutputView() {

            /**
             * 
             */
            private static final long serialVersionUID = 1052809134828172805L;
            /*public void writeRunHeader() throws IOException {
                super.writeRunHeader();
                runDataStream.writeBytes("\t");
                runDataStream.writeBytes("Last Period");
            }
            public void writeRunData() throws IOException {
                super.writeRunData();
                runDataStream.writeBytes("\t");
                runDataStream.writeBytes(Integer.toString(getPeriod()));
            }*/
        };
        addView(dataView);
        getAgents().getRules().setSelected("Play Closest", true);
        try {
            dataView.setRunFile(new File("PlayClosestEndPeriod.txt"));
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
        //We don't need to export historic data, and we want to match current C++ output
        /*dataView.getDataSelection().setSelected("Sum Cooperate Ratio", false);
        dataView.getDataSelection().setSelected("Sum Defect Ratio", false);
        dataView.getDataSelection().setSelected("Count Cooperate Closest", false);
        dataView.getDataSelection().setSelected("Count Cooperate Farthest", false);
        dataView.getDataSelection().setSelected("Count Defect Closest", false);
        dataView.getDataSelection().setSelected("Count Defect Farthest", false);*/
        setViewSelf(true);
    }

    public void scapeIterated(ScapeEvent event) {
        super.scapeIterated(event);
        System.out.println(getPeriod() + "   " + getAgents().getSize());
        if ((getAgents().getSize() == 0) && (getPeriod() != 0)) {
            getRunner().stop();
        }
    }
}
