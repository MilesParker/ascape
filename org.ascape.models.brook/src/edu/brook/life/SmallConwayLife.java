/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.life;

//import java.awt.*;

import org.ascape.model.Cell;
import org.ascape.model.Scape;
import org.ascape.model.space.Array2DMoore;
import org.ascape.model.space.Coordinate2DDiscrete;
import org.ascape.util.Conditional;
import org.ascape.view.vis.Overhead2DView;

/**
 * Just a 'condensed' version of Conway's life to show how small a working model can be.
 */
public class SmallConwayLife extends Scape {

    /**
     * 
     */
    private static final long serialVersionUID = 4450153420664386009L;
    private float initialAliveDensity = 0.1f;

    class LifeCell extends Cell {

        /**
         * 
         */
        private static final long serialVersionUID = 2923116958642120145L;
        public boolean alive;
        boolean nextAlive;

        public void initialize() {
            super.initialize();
            alive = (getRandom().nextFloat() < initialAliveDensity);
        }

        public void iterate() {
            int neighborsAlive = countNeighbors(new Conditional() {
                /**
                 * 
                 */
                private static final long serialVersionUID = 7923827910139566568L;

                public boolean meetsCondition(Object object) {
                    return ((LifeCell) object).alive;
                }
            });
            if (alive) {
                if ((neighborsAlive < 2) || (neighborsAlive > 3)) {
                    nextAlive = false;
                }
            } else {
                if (neighborsAlive == 3) {
                    nextAlive = true;
                }
            }
        }

        public void update() {
            alive = nextAlive;
        }

        public java.awt.Color getColor() {
            if (!alive) {
                return java.awt.Color.black;
            } else {
                return java.awt.Color.white;
            }
        }
    }

    public void createScape() {
        setSpace(new Array2DMoore());
        setPrototypeAgent(new LifeCell());
        setExecutionOrder(RULE_ORDER);
        addRule(ITERATE_AND_UPDATE_RULE);
        setExtent(new Coordinate2DDiscrete(60, 60));
        super.createScape();
    }

    public void createViews() {
        super.createViews();
        Overhead2DView view = new Overhead2DView();
        view.setCellSize(4);
        addView(view);
    }

    public float getInitialAliveDensity() {
        return initialAliveDensity;
    }

    public void setInitialAliveDensity(float initialAliveDensity) {
        this.initialAliveDensity = initialAliveDensity;
    }
}
