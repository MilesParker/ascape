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
 *
 * _Sugarscape_
 * See _Growing Artificial Societies: Social Science from the Ground Up_
 * Brookings Institution Press, The MIT Press
 * Joshua Epstein
 * jepstein@brook.edu
 * Robert Axtell
 * raxtell@brook.edu
 *
 * Miles Parker
 * mparker@brook.edu

 * http://www.brook.edu/es/dynamics/models/ascape
 * The Brookings Institution
 * Washington, D.C.
 */

package edu.brook.sugarscape;

import java.awt.Color;
import java.awt.GridLayout;

import org.ascape.util.data.UnitIntervalDataPoint;
import org.ascape.util.vis.ColorFeatureFixed;
import org.ascape.util.vis.ColorFeatureGradiated;
import org.ascape.view.vis.ComponentView;
import org.ascape.view.vis.Overhead2DView;


public class GAS_II_8 extends GAS_II_2 {

    private Overhead2DView pollutionView;

    public void createScape() {
        super.createScape();
        sugarscape.setPrototypeAgent(new PollutableSugarCell());
        PollutingSugarAgent agent = new PollutingSugarAgent();
        agent.setHostScape(sugarscape);
        agents.setPrototypeAgent(agent);
        agents.setExtent(new org.ascape.model.space.Coordinate1DDiscrete(300));
    }

    public void createViews() {
        super.createViews();
        sugarView = new Overhead2DView();
        ColorFeatureGradiated cellColor = new ColorFeatureGradiated("Sugar");
        cellColor.setDataPoint(new UnitIntervalDataPoint() {
            public double getValue(Object object) {
                return ((double) ((SugarCell) object).getSugarQuantity() / (double) SugarCell.MAX_SUGAR);
            }
        });
        cellColor.setMaximumColor(Color.yellow);
        sugarView.setCellColorFeature(cellColor);
        sugarView.setHostedAgentColorFeature(ColorFeatureFixed.red);
        sugarView.setCellSize(6);
        //sugarscape.addView(sugarView);
        if (getUserEnvironment().isInApplet()) {
            getUserEnvironment().getAppletModelPanel().add("Center", sugarView);
        }
        pollutionView = new Overhead2DView();
        ColorFeatureGradiated pollutionCellColor = new ColorFeatureGradiated("Pollution Level");
        pollutionCellColor.setDataPoint(new UnitIntervalDataPoint() {
            public double getValue(Object object) {
                return ((double) ((PollutableSugarCell) object).getPollution() / (double) ((PollutableSugarCell) object).getMaxPollution());
            }
        });
        pollutionCellColor.setMaximumColor(Color.green);
        pollutionView.setCellColorFeature(pollutionCellColor);
        pollutionView.setCellSize(6);
        pollutionView.setHostedAgentColorFeature(ColorFeatureFixed.red);
        ComponentView[] views = new ComponentView[2];
        views[0] = sugarView;
        views[1] = pollutionView;
        sugarscape.addViews(views);
        if (getUserEnvironment().isInApplet()) {
            getUserEnvironment().getAppletModelPanel().setLayout(new GridLayout(2, 2));
            getUserEnvironment().getAppletModelPanel().removeAll();
            getUserEnvironment().getAppletModelPanel().add(sugarView);
            getUserEnvironment().getAppletModelPanel().add(pollutionView);
            getUserEnvironment().getAppletModelPanel().setSize(600, 600);
        }
    }
}
