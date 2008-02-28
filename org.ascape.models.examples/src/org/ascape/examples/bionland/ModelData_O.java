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
import org.ascape.model.space.Array2D;
import org.ascape.util.sweep.SweepDimension;
import org.ascape.view.nonvis.ConsoleOutView;
import org.ascape.view.nonvis.DataOutputView;
import org.ascape.view.nonvis.SweepControlView;



public class ModelData_O extends Model_M {

    /**
     * 
     */
    private static final long serialVersionUID = -8344288107930733062L;

    public void createScape() {
        super.createScape();
        ((Array2D) territory.getSpace()).setExtent(10, 10);
    }

    public void createViews() {
        addView(new ConsoleOutView() {
            /**
             * 
             */
            private static final long serialVersionUID = -7460871229923903844L;

            public void scapeIterated(ScapeEvent scapeEvent) {
                super.scapeIterated(scapeEvent);
                if ((getPeriod() / 10) * 10 == getPeriod())
                    System.out.println(getPeriod());
            }
        });
        createSelfView();
        setAutoRestart(false);
        DataOutputView outView = new DataOutputView();
        addView(outView);
        try {
            outView.setRunFile(new File("RunFileTest.txt"));
            outView.setPeriodFile(new File("DataFileTest.txt"));
        } catch (IOException e) {
            System.out.println("Exception while creating output File: " + e);
        }
        try {
            setStopPeriod(500);
        } catch (org.ascape.model.space.SpatialTemporalException e) {
            System.out.println(e);
        }
        SweepControlView sweep = new SweepControlView("Model Parameter Sweep");
        addView(sweep);
        sweep.getSweepGroup().addMember(new SweepDimension(this, "DiffusionConstant", 0.0f, 1.0f, 0.05f));
    }
}
