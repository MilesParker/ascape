/*
 * This software and documentation is subject to, and may be only used in accordance with, the terms and conditions of
 * the license agreement described or set forth in the file titled License.txt.  This software and documentation
 * contains information that is confidential and proprietary to NuTech Solutions, Inc.
 *
 *             Copyright (c) 2000-2004
 *             NuTech Solutions, Inc.
 *
 * Portions of the software are licensed from The Brookings Institution
 *             Copyright (c) 1998-2000
 *            The Brookings Institution
 *           All rights reserved worldwide.
 *
 *
 * U.S. GOVERNMENT END USERS. The software is a "commercial item," as that term is defined in 48 C.F.R. 2.101,
 * consisting of "commercial computer software" and "commercial computer software documentation," as such terms are
 * used in 48 C.F.R. 12.212. Consistent with 48 C.F.R. 12.212 and 48 C.F.R. 227.7202-1 through 227.7202-4, and other
 * relevant sections of the Code of Federal Regulations, as applicable all U.S. Government End Users acquire the
 * software with only those rights set forth in the license agreement. This software includes trade secrets that
 * are exempt from disclosure under the Freedom of Information Act.
 *
 * An incomplete implemention of Rob Axtell's firms model.
 *
 * Firms Model
 * Robert Axtell
 * The Brookings Institution
 * Washington, D.C.
 * raxtell@brook.edu
 *
 * Miles Parker
 * The Brookings Institution
 * Washington, D.C.
 * mparker@brook.edu
 * (202) 797-6136
 *
 * Please contact us for permission to use, copy, modify, or distribute this software.
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

public class Model1 extends Scape {

    private static int nAgents = 500;

    private Scape firms;

    private Scape employees;

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

    public void scapeSetup(ScapeEvent scapeEvent) {
        firms.setExtent(new Coordinate1DDiscrete(0));
    }

    public void createViews() {
        super.createViews();
        final StatCollectorCSAMM firmUnitOutput = new StatCollectorCSAMM("Firm Unit Output") {
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
