/*
 * (c) 1998-2000 The Brookings Institution, All Rights Reserved
 *
 * Permission to use this software and its documentation for non-commercial
 * purposes and without fee is hereby granted, provided this copyright statement
 * is included. Please contact us for permission for redistribution and other uses.
 *
 * BROOKINGS MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * OR NON-INFRINGEMENT. BROOKINGS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
 * LICENSEE AS A RESULT OF USING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * Conway's Life
 *
 * Miles Parker
 * mparker@brook.edu
 * The Brookings Institution
 * Washington, D.C.
 */

package edu.brook.life;

//import java.awt.*;

import org.ascape.model.Agent;
import org.ascape.model.Cell;
import org.ascape.model.Scape;
import org.ascape.model.rule.ExecuteThenUpdate;
import org.ascape.model.rule.Rule;
import org.ascape.model.space.Array2DSmallWorld;
import org.ascape.model.space.Coordinate2DDiscrete;
import org.ascape.util.Conditional;
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
public class ConwayLifeSmallWorld extends Scape {

    /**
     * The portion of cells that will be alive when we start the core.
     */
    private float initialAliveDensity = 0.1f;

    /**
     * A conditional is a class that is used to determine whether some object passed
     * to it meets a set condition. In this case, it simply determines wheteher a life
     * cell is alive or not. In Java, classes like this serve a similar role as function
     * pointers in C++; they allow us to use a common method (in this case, countNeighbors)
     * on any kind of object.
     */
    private final static Conditional IS_ALIVE = new Conditional() {
        public boolean meetsCondition(Object object) {
            return ((LifeCell) object).alive;
        }
    };

    /**
     * This is the class that defines the state of a cell within the automata.
     */
    class LifeCell extends Cell {

        //Is the cell currently alive?
        public boolean alive;
        //Stores the cells 'next' alive state; that is, the state that the cell should be in
        //when all cells have finsihed calculating their next state.
        boolean nextAlive;

        public void initialize() {
            super.initialize();
            if (getRandom().nextFloat() < initialAliveDensity) {
                alive = true;
            } else {
                alive = false;
            }
        }

        public boolean calculateNextAlive() {
            int neighborsAlive = countNeighbors(IS_ALIVE);
            if (alive) {
                if ((neighborsAlive < 2) || (neighborsAlive > 3)) {
                    return false;
                }
            }
//Dead
            else {
                if (neighborsAlive == 3) {
                    return true;
                }
            }
            return alive;
        }

        public java.awt.Color getColor() {
            if (!alive) {
                return java.awt.Color.black;
            } else {
                return java.awt.Color.white;
            }
        }
    }

    /**
     * A rule that provides the behavior for the cell. Like conditionals, rules provide a way
     * to use general methods (in this case, to execute behavior across a collection of agents)
     * with object specific functionality.
     * This rule provides the basic functionality
     */
    public final static Rule NEXT_STATE_SYNCHRONOUS = new ExecuteThenUpdate("Determine Next State") {
        public void execute(Agent agent) {
            ((LifeCell) agent).nextAlive = ((LifeCell) agent).calculateNextAlive();
        }

        public void update(Agent agent) {
            ((LifeCell) agent).alive = ((LifeCell) agent).nextAlive;
        }
    };

    /**
     * This rule provides an example of what not to do, at least if we want to properly implement
     * Conway's life. Since we are calculating the next state of blahh...
     */
    public final static Rule NEXT_STATE_ASYNCHRONOUS = new Rule("Determine Next State Asynchronsously") {
        public void execute(Agent agent) {
            ((LifeCell) agent).alive = ((LifeCell) agent).calculateNextAlive();
        }
    };

    public void createScape() {
        setSpace(new Array2DSmallWorld());
        LifeCell cell = new LifeCell();
        setPrototypeAgent(cell);
        //setCellsRequestUpdates(true);
        setExecutionOrder(RULE_ORDER);
        addRule(NEXT_STATE_SYNCHRONOUS);
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
