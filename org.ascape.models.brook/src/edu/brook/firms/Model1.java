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

    /*
     * (non-Javadoc)
     * 
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

    /*
     * (non-Javadoc)
     * 
     * @see org.ascape.model.Scape#scapeSetup(org.ascape.model.event.ScapeEvent)
     */
    public void scapeSetup(ScapeEvent scapeEvent) {
        firms.setExtent(new Coordinate1DDiscrete(0));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ascape.model.Scape#createViews()
     */
    public void createGraphicViews() {
        super.createGraphicViews();
        final StatCollectorCSAMM firmUnitOutput = new StatCollectorCSAMM("Firm Unit Output") {
            /**
             * 
             */
            private static final long serialVersionUID = 4318959934381762180L;

            public double getValue(Object object) {
                return ((Firm) object).getAvgOutput();
            }
        };
        firms.addStatCollector(firmUnitOutput);

        final ColorFeatureGradiated colorForAvgOutput = new ColorFeatureGradiated("Avg Output");
        colorForAvgOutput.setDataPoint(new UnitIntervalDataPoint() {
            /**
             * 
             */
            private static final long serialVersionUID = -6033261844239803432L;

            public double getValue(Object object) {
                double max = firmUnitOutput.getMax();
                if (max > 0.0) {
                    return ((Firm) object).getAvgOutput() / max;
                }
                return 0.0;
            }
        });
        colorForAvgOutput.setMaximumColor(Color.GREEN);
        colorForAvgOutput.setMinimumColor(Color.RED);

        final ColorFeatureGradiated colorForAvgEffort = new ColorFeatureGradiated("Avg Effort");
        colorForAvgEffort.setDataPoint(new UnitIntervalDataPoint() {
            public double getValue(Object object) {
                return ((Firm) object).getAvgEffort();
            }
        });
        colorForAvgEffort.setMaximumColor(Color.BLUE);
        colorForAvgEffort.setMinimumColor(Color.WHITE);

        FixedStretchyView firmEffortView = createView("Firm Effort", colorForAvgEffort, false);
        FixedStretchyView firmEffortAgeView = createView("Firm Effort by Age", colorForAvgEffort, true);
        FixedStretchyView firmOutputView = createView("Firm Output", colorForAvgOutput, false);
        FixedStretchyView firmOutputAgeView = createView("Firms Output by Age", colorForAvgOutput, true);
        firms.addView(firmEffortView);
        firms.addView(firmOutputView);
        firms.addView(firmEffortAgeView);
        firms.addView(firmOutputAgeView);
    }

    private FixedStretchyView createView(String name, final ColorFeatureGradiated colorFeature, boolean gaps) {
        FixedStretchyView firmView = new FixedStretchyView(name, nAgents, nAgents / 2);
        firmView.setCellColorFeature(colorFeature);
        firmView.setRemoveGaps(gaps);
        firmView.setCellSize(4);
        return firmView;
    }
}
