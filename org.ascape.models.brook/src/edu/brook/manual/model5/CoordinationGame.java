/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */
package edu.brook.manual.model5;

import java.awt.Color;

import org.ascape.model.HostCell;
import org.ascape.model.Scape;
import org.ascape.model.event.ScapeEvent;
import org.ascape.model.space.Array2DVonNeumann;
import org.ascape.util.data.StatCollector;
import org.ascape.util.data.StatCollectorCSAMM;
import org.ascape.util.data.StatCollectorCond;
import org.ascape.view.vis.ChartView;
import org.ascape.view.vis.Overhead2DView;

public class CoordinationGame extends Scape {

    /**
     * 
     */
    private static final long serialVersionUID = -144832214147304010L;

    // model-scope variables
    // model-scope variables
    protected int nPlayers = 100;

    protected int latticeWidth = 30;

    protected int latticeHeight = 30;

    public int coordinateOnBlue = 1;

    public int coordinateOnRed = 1;

    public int error = 0;

    public int turns = 5;

    public int repScore = 5;

    public int minScore = 3;

    public int friends_size = 4;

    Scape lattice;

    Scape players;

    Overhead2DView overheadView;

    // creates scapes and agents
    public void createScape() {
        super.createScape();
        lattice = new Scape(new Array2DVonNeumann());
        lattice.setPrototypeAgent(new HostCell());
        lattice.setExtent(latticeWidth, latticeHeight);

        CoordinationGamePlayer cgplayer = new CoordinationGamePlayer();
        cgplayer.setHostScape(lattice);
        players = new Scape();
        players.setName("Players");
        players.setPrototypeAgent(cgplayer);
        players.setExecutionOrder(Scape.RULE_ORDER);

        add(lattice);
        add(players);

        StatCollector CountReds = new StatCollectorCond("Reds") {
            /**
             * 
             */
            private static final long serialVersionUID = 7621367283677572920L;

            public boolean meetsCondition(Object object) {
                return (((CoordinationGamePlayer) object).getColor() == Color.red);
            }
        };
        StatCollector CountBlues = new StatCollectorCond("Blues") {
            /**
             * 
             */
            private static final long serialVersionUID = 7195414564121187498L;

            public boolean meetsCondition(Object object) {
                return (((CoordinationGamePlayer) object).getColor() == Color.blue);
            }
        };
        StatCollector AvgPayoff = new StatCollectorCSAMM("Payoff") {
            /**
             * 
             */
            private static final long serialVersionUID = -1218449785355833052L;

            public double getValue(Object object) {
                return ((CoordinationGamePlayer) object).getRunningTotal();
            }
        };

        players.addStatCollector(CountReds);
        players.addStatCollector(CountBlues);
        players.addStatCollector(AvgPayoff);
    }

    public void scapeSetup(ScapeEvent scapeEvent) {
        ((Scape) players).setExtent(nPlayers);
    }

    // create views and charts
    public void createGraphicViews() {
        super.createGraphicViews();
        ChartView chart = new ChartView();
        players.addView(chart);
        chart.addSeries("Count Reds", Color.red);
        chart.addSeries("Count Blues", Color.blue);
        chart.addSeries("Average Payoff", Color.black);
        chart.setDisplayPoints(100);

        overheadView = new Overhead2DView();
        overheadView.setCellSize(15);
        overheadView.setDrawNetwork(true);
        lattice.addView(overheadView);
    }

    // get() and set() methods for the model variables
    public int getRedScore() {
        return coordinateOnRed;
    }

    public void setRedScore(int NewcoordinateOnRed) {
        coordinateOnRed = NewcoordinateOnRed;

    }

    public int getBlueScore() {
        return coordinateOnBlue;
    }

    public void setBlueScore(int NewcoordinateOnBlue) {
        coordinateOnBlue = NewcoordinateOnBlue;

    }

    public int getError() {
        return error;
    }

    public void setError(int NewError) {
        error = NewError;
    }

    public int getnPlayers() {
        return nPlayers;
    }

    public void setnPlayers(int NewnPlayers) {
        nPlayers = NewnPlayers;
    }

    public int getTurnstoDie() {
        return turns;
    }

    public void setTurnstoDie(int Newnum) {
        turns = Newnum;

    }

    public int getrepScore() {
        return repScore;
    }

    public void setrepScore(int Newscore) {
        repScore = Newscore;

    }

    public int getMinDieScore() {
        return minScore;
    }

    public void setMinDieScore(int Newscore) {
        minScore = Newscore;

    }

    public Scape getPlayers() {
        return players;
    }
}