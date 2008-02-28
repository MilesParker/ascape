/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.diffusion;

import java.awt.Color;
import java.util.List;

import org.ascape.model.Agent;
import org.ascape.model.Cell;
import org.ascape.model.Scape;
import org.ascape.model.rule.Rule;
import org.ascape.model.space.Array2DVonNeumann;
import org.ascape.model.space.Coordinate2DDiscrete;
import org.ascape.view.vis.Overhead2DView;


class ABCell extends Cell {

    /**
     * 
     */
    private static final long serialVersionUID = 2392320215965879432L;
    public final static int A = 1;
    public final static int B = 2;
    protected int state = B;
    public static float beta = .1f;

    public void decideState() {
        List neighbors = findNeighbors();
        int aPayoff = 0;
        int bPayoff = 0;
        for (int i = 0; i < neighbors.size(); i++) {
            aPayoff += calculatePayoff(A, ((ABCell) neighbors.get(i)).getState());
            bPayoff += calculatePayoff(B, ((ABCell) neighbors.get(i)).getState());
        }
        float aTerm = (float) Math.pow(Math.E, beta * aPayoff);
        float bTerm = (float) Math.pow(Math.E, beta * bPayoff);
        float probA = aTerm / (aTerm + bTerm);
        if (getRandom().nextFloat() < probA) {
            state = A;
        } else {
            state = B;
        }
        requestUpdate();
    }

    public static int calculatePayoff(int state1, int state2) {
        int sumState = state1 + state2;
        if (sumState == 2) {
            return 3;
        } else if (sumState == 4) {
            return 2;
        } else {
            return 0;
        }
    }

    public int getState() {
        return state;
    }

    public final Color getColor() {
        if (state == A) {
            return Color.yellow;
        } else {
            return Color.black;
        }
    }
}

public class ABModel extends Scape {

    /**
     * 
     */
    private static final long serialVersionUID = -2306726586870156818L;

    protected int span = 50;

    public Scape cells;

    public void createScape() {
        super.createScape();
        setName("AB Diffusion Model");
        cells = new Scape(new Array2DVonNeumann());
        cells.setExtent(new Coordinate2DDiscrete(span, span));
        cells.setPrototypeAgent(new ABCell());
        cells.addRule(new Rule("Decide State") {
            /**
             * 
             */
            private static final long serialVersionUID = -750095707537534296L;

            public void execute(Agent a) {
                ((ABCell) a).decideState();
            }
        });
        cells.setExecutionStyle(REPEATED_DRAW);
        cells.setAgentsPerIteration(25);
        add(cells);
    }

    public void createViews() {
        super.createViews();
        Overhead2DView overheadView = new Overhead2DView();
        //Set the cell size to be 12
        overheadView.setCellSize(8);
        //overheadView.setDrawEveryNUpdates(100);
        cells.setCellsRequestUpdates(true);
        cells.addView(overheadView);
    }

    public int getSpan() {
        return span;
    }

    public void setSpan(int span) {
        this.span = span;
    }

    public float getBeta() {
        return ABCell.beta;
    }

    public void setBeta(float beta) {
        ABCell.beta = beta;
    }
}
