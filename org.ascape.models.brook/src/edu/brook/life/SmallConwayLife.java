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

    private float initialAliveDensity = 0.1f;

    class LifeCell extends Cell {

        public boolean alive;
        boolean nextAlive;

        public void initialize() {
            super.initialize();
            alive = (getRandom().nextFloat() < initialAliveDensity);
        }

        public void iterate() {
            int neighborsAlive = countNeighbors(new Conditional() {
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
