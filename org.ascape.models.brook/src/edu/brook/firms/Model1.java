/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.firms;

import java.awt.Color;

import org.ascape.model.Scape;
import org.ascape.model.event.ScapeEvent;
import org.ascape.model.space.Array1D;
import org.ascape.model.space.Coordinate1DDiscrete;
import org.ascape.util.data.StatCollectorCSAMM;
import org.ascape.util.data.UnitIntervalDataPoint;
import org.ascape.util.vis.ColorFeatureGradiated;
import org.ascape.view.vis.FixedStretchyView;
import org.ascape.view.vis.StretchyView;

/**
 * The Class Model1.
 */
public class Model1 extends Scape {

    /**
     * 
     */
    private static final long serialVersionUID = -6879053433696894545L;

    /**
     * The n agents.
     */
    private static int nAgents = 500;

    /**
     * The firms.
     */
    private Scape firms;

    /**
     * The employees.
     */
    private Scape employees;

    /* (non-Javadoc)
     * @see org.ascape.model.Scape#createScape()
     */
    public void createScape() {
        super.createScape();
        firms = new Scape();
        Firm protoFirm = new Firm();
        protoFirm.setAutoCreate(false);
        protoFirm.setMembersActive(false);
        firms.setPrototypeAgent(protoFirm);
        add(firms);

        Employee protoEmployee = new Employee();
        protoEmployee.setFirms(firms);
        employees = new Scape(new Array1D());
        employees.setPrototypeAgent(protoEmployee);
        employees.setExtent(new Coordinate1DDiscrete(nAgents));
        employees.setAgentsPerIteration(nAgents / 20);
        add(employees);
        employees.addRule(ITERATE_RULE);
    }

    /* (non-Javadoc)
     * @see org.ascape.model.Scape#scapeSetup(org.ascape.model.event.ScapeEvent)
     */
    public void scapeSetup(ScapeEvent scapeEvent) {
        firms.setExtent(new Coordinate1DDiscrete(0));
    }

    /* (non-Javadoc)
     * @see org.ascape.model.Scape#createViews()
     */
    public void createViews() {
        super.createViews();
        final StatCollectorCSAMM firmUnitOutput = new StatCollectorCSAMM("Firm Unit Output") {
            /**
             * 
             */
            private static final long serialVersionUID = 4318959934381762180L;

            public double getValue(Object object) {
                return (((Firm) object).getAvgOutput());
            }
        };
        firms.addStatCollector(firmUnitOutput);
        /*if (!(ViewFrameBridge.isInApplet())) {
            //Create a new chart
            ChartView chart = new ChartView();
            //Add the chart view
            firms.addView(chart);
            //And add some of the stat series we've just created to it
            chart.addSeries("Average Firm Unit Output", Color.green);
        }*/
        //Add the new values stats to agents
        StretchyView firmView = new FixedStretchyView(nAgents, nAgents / 2);
        firmView.setName("Firms");
        /*final ColorFeatureGradiated colorForAvgEffort = new ColorFeatureGradiatedIndex("Avg Effort");
        colorForAvgEffort.setDataPoint(new UnitIntervalDataPoint() {
public double getValue(Object object) {
return ((Firm) object).getAvgEffort();
}
        });
        colorForAvgEffort.setMaximumColor(Color.green);
        colorForAvgEffort.setMinimumColor(Color.red);
        firmView.setAgentColorFeature(colorForAvgEffort);*/
        final ColorFeatureGradiated colorForAvgOutput = new ColorFeatureGradiated("Avg Output");
        colorForAvgOutput.setDataPoint(new UnitIntervalDataPoint() {
            /**
             * 
             */
            private static final long serialVersionUID = -6033261844239803432L;

            public double getValue(Object object) {
                return ((Firm) object).getAvgOutput() / firmUnitOutput.getMax();
            }
        });
        colorForAvgOutput.setMaximumColor(Color.green);
        colorForAvgOutput.setMinimumColor(Color.red);
        firmView.setCellColorFeature(colorForAvgOutput);
        firmView.setCellSize(4);
        firms.addView(firmView);
    }
}
