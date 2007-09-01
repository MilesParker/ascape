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
 * Diffusion of Behaviors in Social Networks
 * See _Diffusion in Social Networks_
 * Center on Social and Economic Dynamics Working Paper No. 2, May 1999
 * H. Peyton Young
 * pyoung@brook.edu
 * The Brookings Institution
 * The Johns Hopkins University
 *
 * Implementated by
 * Miles Parker
 * mparker@brook.edu
 * The Brookings Institution
 * Washington, D.C.
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

    protected int span = 50;

    public Scape cells;

    public void createScape() {
        super.createScape();
        setName("AB Diffusion Model");
        cells = new Scape(new Array2DVonNeumann());
        cells.setExtent(new Coordinate2DDiscrete(span, span));
        cells.setPrototypeAgent(new ABCell());
        cells.addRule(new Rule("Decide State") {
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
