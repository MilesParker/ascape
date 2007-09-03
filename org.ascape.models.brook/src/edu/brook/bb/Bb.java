/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */
//-------------------------------------------------------------------

package edu.brook.bb;

import java.awt.Color;

import org.ascape.model.HostCell;
import org.ascape.model.Scape;
import org.ascape.model.event.ScapeEvent;
import org.ascape.model.space.Array2DVonNeumann;
import org.ascape.util.data.StatCollector;
import org.ascape.util.data.StatCollectorCondCSAMM;
import org.ascape.view.vis.ChartView;
import org.ascape.view.vis.Overhead2DView;

//-------------------------------------------------------------------

public class Bb extends Scape {

    public int nAgents = 16;
    private int latticeWidth = 30;
    private int latticeHeight = 30;

    Scape lattice;
    Scape agents;

    public Overhead2DView overheadView;

    //-------------------------------------------------------------------
    public void createScape() {
        super.createScape();

        lattice = new Scape(new Array2DVonNeumann());
        lattice.setPrototypeAgent(new HostCell());
        lattice.getRules().clear();
        lattice.setExtent(latticeWidth, latticeHeight);

        Beaver beaver = new edu.brook.bb.Beaver(512, 3, 50, 32, 100);
        beaver.setHostScape(lattice);

        agents = new Scape();
        agents.setName("Beavers");
        agents.setPrototypeAgent(beaver);
        agents.setExecutionOrder(Scape.RULE_ORDER);

        add(lattice);
        add(agents);
    }

    //-------------------------------------------------------------------
    public void createViews() {
        super.createViews();
        final StatCollector[] stats =
            {
                new StatCollectorCondCSAMM("RAOnes") {
                    public boolean meetsCondition(Object object) {
                        return (((Beaver) object).type == 'r');
                    }

                    public double getValue(Object object) {
                        return ((Beaver) object).getAverageOnes();
                    }
                },

                new StatCollectorCondCSAMM("GAOnes") {
                    public boolean meetsCondition(Object object) {
                        return (((Beaver) object).type == 'g');
                    }

                    public double getValue(Object object) {
                        return ((Beaver) object).getAverageOnes();
                    }
                },

                new StatCollectorCondCSAMM("MAOnes") {
                    public boolean meetsCondition(Object object) {
                        return (((Beaver) object).type == 'm');
                    }

                    public double getValue(Object object) {
                        return ((Beaver) object).getAverageOnes();
                    }
                },
                new StatCollectorCondCSAMM("SMOnes") {
                    public boolean meetsCondition(Object object) {
                        return (((Beaver) object).type == 's');
                    }

                    public double getValue(Object object) {
                        return ((Beaver) object).getMaxOnes();
                    }
                },

                new StatCollectorCondCSAMM("RMOnes") {
                    public boolean meetsCondition(Object object) {
                        return (((Beaver) object).type == 'r');
                    }

                    public double getValue(Object object) {
                        return ((Beaver) object).getMaxOnes();
                    }
                },

                new StatCollectorCondCSAMM("GMOnes") {
                    public boolean meetsCondition(Object object) {
                        return (((Beaver) object).type == 'g');
                    }

                    public double getValue(Object object) {
                        return ((Beaver) object).getMaxOnes();
                    }
                },

                new StatCollectorCondCSAMM("MMOnes") {
                    public boolean meetsCondition(Object object) {
                        return (((Beaver) object).type == 'm');
                    }

                    public double getValue(Object object) {
                        return ((Beaver) object).getMaxOnes();
                    }
                }

            };

        agents.addStatCollectors(stats);

        ChartView chart = new ChartView();
        agents.addView(chart);
        chart.addSeries("Maximum RAOnes", Color.yellow);
        chart.addSeries("Maximum GAOnes", Color.green);
        chart.addSeries("Maximum MAOnes", Color.pink);
        chart.addSeries("Maximum SMOnes", Color.black);
        chart.addSeries("Maximum RMOnes", Color.red);
        chart.addSeries("Maximum GMOnes", Color.blue);
        chart.addSeries("Maximum MMOnes", Color.magenta);

        chart.setDisplayPoints(100);
    }

    //-------------------------------------------------------------------
    public void scapeSetup(ScapeEvent e) {
        agents.setSize(nAgents);
    }

    //-------------------------------------------------------------------
    public int getNumberOfAgents() {
        return nAgents;
    }
}
