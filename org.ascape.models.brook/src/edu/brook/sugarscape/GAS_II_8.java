/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */

package edu.brook.sugarscape;

import java.awt.Color;
import java.awt.GridLayout;

import org.ascape.runtime.applet.AppletEnvironment;
import org.ascape.util.data.UnitIntervalDataPoint;
import org.ascape.util.vis.ColorFeatureFixed;
import org.ascape.util.vis.ColorFeatureGradiated;
import org.ascape.view.vis.ComponentView;
import org.ascape.view.vis.Overhead2DView;


public class GAS_II_8 extends GAS_II_2 {

    /**
     * 
     */
    private static final long serialVersionUID = 5908718881149855011L;
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
            /**
             * 
             */
            private static final long serialVersionUID = -2894146917508743499L;

            public double getValue(Object object) {
                return (double) ((SugarCell) object).getSugarQuantity() / (double) SugarCell.MAX_SUGAR;
            }
        });
        cellColor.setMaximumColor(Color.yellow);
        sugarView.setCellColorFeature(cellColor);
        sugarView.setHostedAgentColorFeature(ColorFeatureFixed.red);
        sugarView.setCellSize(6);
        //sugarscape.addView(sugarView);
        if (getUIEnvironment() instanceof AppletEnvironment) {
            ((AppletEnvironment) getUIEnvironment()).getApplet().add("Center", sugarView);
        }
        pollutionView = new Overhead2DView();
        ColorFeatureGradiated pollutionCellColor = new ColorFeatureGradiated("Pollution Level");
        pollutionCellColor.setDataPoint(new UnitIntervalDataPoint() {
            /**
             * 
             */
            private static final long serialVersionUID = 7536329161993874770L;

            public double getValue(Object object) {
                return (double) ((PollutableSugarCell) object).getPollution() / (double) ((PollutableSugarCell) object).getMaxPollution();
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
        if (getUIEnvironment() instanceof AppletEnvironment) {
            ((AppletEnvironment) getUIEnvironment()).getApplet().setLayout(new GridLayout(2, 2));
            ((AppletEnvironment) getUIEnvironment()).getApplet().removeAll();
            ((AppletEnvironment) getUIEnvironment()).getApplet().add(sugarView);
            ((AppletEnvironment) getUIEnvironment()).getApplet().add(pollutionView);
            ((AppletEnvironment) getUIEnvironment()).getApplet().setSize(600, 600);
        }
    }
}
