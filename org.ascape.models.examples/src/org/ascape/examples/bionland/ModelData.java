/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */



package org.ascape.examples.bionland;

import java.io.File;
import java.io.IOException;

import org.ascape.model.event.ScapeEvent;
import org.ascape.util.sweep.SweepDimension;
import org.ascape.view.nonvis.ConsoleOutView;
import org.ascape.view.nonvis.DataOutputView;
import org.ascape.view.nonvis.SweepControlView;


public class ModelData extends Model {

    /**
     * 
     */
    private static final long serialVersionUID = 6851259106078061070L;

    public void createViews() {
        createSelfView();
        addView(new ConsoleOutView() {
            /**
             * 
             */
            private static final long serialVersionUID = -2133098069069661527L;

            public void scapeIterated(ScapeEvent scapeEvent) {
                super.scapeIterated(scapeEvent);
                if ((getPeriod() / 10) * 10 == getPeriod())
                    System.out.println(getPeriod());
            }
        });
        DataOutputView data = new DataOutputView();
        addView(data);
        try {
            setStopPeriod(50);
        } catch (org.ascape.model.space.SpatialTemporalException e) {
            System.out.println(e);
        }
        try {
            data.setRunFile(new File("TestRunFile.txt"));
            data.setPeriodFile(new File("TestPeriodFile.txt"));
        } catch (IOException e) {
            System.out.println(e);
        }
        SweepControlView sweep = new SweepControlView("Sweep View");
        sweep.getSweepGroup().addMember(new SweepDimension(this, "DiffusionConstant", 0.0f, 1.0f, 0.05f));
        addView(sweep);
    }
}
