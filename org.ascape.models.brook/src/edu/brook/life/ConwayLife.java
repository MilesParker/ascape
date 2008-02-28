/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.life;

//import java.awt.*;

import org.ascape.model.Agent;
import org.ascape.model.Scape;
import org.ascape.model.rule.ExecuteThenUpdate;
import org.ascape.model.rule.Rule;
import org.ascape.model.space.Array2DMoore;
import org.ascape.model.space.Coordinate2DDiscrete;
import org.ascape.view.vis.Overhead2DView;

/**
 * An implementation of John Conway's 'Life.'
 * In this classic cellular automata, very simple life and death rules create
 * interesting and dynamic patterns that seem quite life-like.
 * This class is intended to demonstrate a few important aspects of Ascape.
 * First it show how a simple CA can be easily constructed with a minimum
 * amount of specification and programming.
 * It also shows how rules allow agent behaviors (mehtods) to be decoupled from agent class
 * definition, and made selectable and dispatchable across collections at runtime.
 * Understanding this concept is crucial to an understanding of how Ascape works.
 * Finally, it demonstrates how synchronous updating of rules can be achieved through
 * the use of Ascape's "execute, then update" rule.
 * The importance of the distinction between synchronous and asynchronous updating
 * should not be overlooked. To see this, try modifying the source code so that it uses
 * the asynchronous rule instead.
 */
public class ConwayLife extends Scape {

    /**
     * 
     */
    private static final long serialVersionUID = -6825695188923587693L;

    /**
     * The portion of cells that will be alive when we start the model.
     */
    private static float initialAliveDensity = 0.1f;

    public final static Rule NEXT_STATE_CALC = new Rule("Determine Next State") {
        /**
         * 
         */
        private static final long serialVersionUID = -188714168999737619L;
        public void execute(Agent agent) {
            ((LifeCell) agent).nextAlive = ((LifeCell) agent).calculateNextAlive();
        }
        public boolean isCauseRemoval() {
            return false;
        }
        public boolean isRandomExecution() {
            return false;
        }
    };

    public final static Rule NEXT_STATE_UPDATE = new Rule("Determine Next State") {
        /**
         * 
         */
        private static final long serialVersionUID = -3196280105259514656L;
        public void execute(Agent agent) {
            ((LifeCell) agent).alive = ((LifeCell) agent).nextAlive;
        }
        public boolean isCauseRemoval() {
            return false;
        }
        public boolean isRandomExecution() {
            return false;
        }
    };

    /**
     * A rule that provides the behavior for the cell. Like conditionals, rules provide a way
     * to use general methods (in this case, to execute behavior across a collection of agents)
     * with object specific functionality.
     * This rule provides the basic functionality
     */
    public final static Rule NEXT_STATE_SYNCHRONOUS = new ExecuteThenUpdate("Determine Next State") {
        /**
         * 
         */
        private static final long serialVersionUID = 4775093433702008839L;
        public void execute(Agent agent) {
            ((LifeCell) agent).nextAlive = ((LifeCell) agent).calculateNextAlive();
        }

        public void update(Agent agent) {
            ((LifeCell) agent).alive = ((LifeCell) agent).nextAlive;
        }
        public boolean isCauseRemoval() {
            return false;
        }
        public boolean isRandomExecution() {
            return false;
        }
    };

    /**
     * This rule provides an example of what not to do, at least if we want to properly implement
     * Conway's life. Since we are calculating the next state of blahh...
     */
    public final static Rule NEXT_STATE_ASYNCHRONOUS = new Rule("Determine Next State Asynchronsously") {
        /**
         * 
         */
        private static final long serialVersionUID = 5674573245383950666L;

        public void execute(Agent agent) {
            ((LifeCell) agent).alive = ((LifeCell) agent).calculateNextAlive();
        }

        public boolean isRandomExecution() {
            return true;
        }
    };

    private Overhead2DView overheadView;
    private int width = 51;
    private int height = 51;

    public void createScape() {
        setSpace(new Array2DMoore());
        setName("Lattice");
        //to allow overiding of LifeCell
        if (!(getPrototypeAgent() instanceof LifeCell)) {
            LifeCell cell = new LifeCell();
            setPrototypeAgent(cell);
        }
        //setCellsRequestUpdates(true);
        setExecutionOrder(RULE_ORDER);
        getRules().clear();
        addRule(NEXT_STATE_SYNCHRONOUS);
        setExtent(new Coordinate2DDiscrete(width, height));
//        addStatCollector(new StatCollectorCondCSA("Alive") {
//            public boolean meetsCondition(Object object) {
//                return LifeCell.IS_ALIVE.meetsCondition(object);
//            }
//        });
        super.createScape();
    }

    public void createViews() {
        super.createViews();
        overheadView = new Overhead2DView("Overhead View");
        overheadView.setCellSize(4);
        addView(overheadView);
    }

    public static float getInitialAliveDensity() {
        return initialAliveDensity;
    }

    public static void setInitialAliveDensity(float _initialAliveDensity) {
        initialAliveDensity = _initialAliveDensity;
    }

    public Overhead2DView getOverheadView() {
        return overheadView;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
