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
     * The portion of cells that will be alive when we start the model.
     */
    private static float initialAliveDensity = 0.1f;

    public final static Rule NEXT_STATE_CALC = new Rule("Determine Next State") {
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
