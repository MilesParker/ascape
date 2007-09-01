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
import org.ascape.model.space.Array2DMoore;
import org.ascape.model.space.Coordinate2DDiscrete;
import org.ascape.util.data.StatCollector;
import org.ascape.util.data.StatCollectorCond;
import org.ascape.view.vis.ChartView;
import org.ascape.view.vis.Overhead2DView;


class ABCCell extends Cell {

    public final static int L = 1;
    public final static int M = 2;
    public final static int H = 3;
    protected int characteristic;
    public final static int A = 1;
    public final static int B = 2;
    public final static int C = 3;
    protected int action;
    public static float matrixHA = 34;
    public static float matrixHB = 42.5f;
    public static float matrixHC = 51;
    public static float matrixMA = 28;
    public static float matrixMB = 35;
    public static float matrixMC = 0;
    public static float matrixLA = 24;
    public static float matrixLB = 0;
    public static float matrixLC = 0;
    public static float beta = .1f;
    public static float gamma = 1.0f;

    public void initialize() {
        Coordinate2DDiscrete c = (Coordinate2DDiscrete) getCoordinate();
        if ((((float) c.getXValue() + c.getYValue()) % 2) == 0) {
            characteristic = M;
        } else {
            if (isAbove()) {
                characteristic = H;
            } else {
                characteristic = L;
            }
        }
        action = B;
    }

    public boolean isAbove() {
        Coordinate2DDiscrete c = (Coordinate2DDiscrete) getCoordinate();
        return c.getYValue() < ((Coordinate2DDiscrete) getScape().getExtent()).getYValue() / 2;
    }

    public void decideAction() {
        List neighbors = findNeighbors();
        int aCount = 0;
        int bCount = 0;
        int cCount = 0;
        for (int i = 0; i < neighbors.size(); i++) {
            switch (((ABCCell) neighbors.get(i)).action) {
                case A:
                    aCount++;
                    break;
                case B:
                    bCount++;
                    break;
                case C:
                    cCount++;
                    break;
            }
        }
        float aTerm = (float) Math.pow(Math.E, beta * (charAction(A) + gamma * (aCount / 8.0)));
        if ((int) charAction(A) == 0) {
            aTerm = 0.0f;
        }
        float bTerm = (float) Math.pow(Math.E, beta * (charAction(B) + gamma * (bCount / 8.0)));
        if ((int) charAction(B) == 0) {
            bTerm = 0.0f;
        }
        float cTerm = (float) Math.pow(Math.E, beta * (charAction(C) + gamma * (cCount / 8.0)));
        if ((int) charAction(C) == 0) {
            cTerm = 0.0f;
        }

        float termSum = aTerm + bTerm + cTerm;

        float probOverTerm = getRandom().nextFloat() * termSum;
        if (probOverTerm < aTerm) {
            action = A;
        } else if (probOverTerm < aTerm + bTerm) {
            action = B;
        } else {
            action = C;
        }
        requestUpdate();
    }

    public float charAction(int action) {
        switch (action) {
            case A:
                {
                    switch (characteristic) {
                        case L:
                            {
                                return matrixLA;
                            }
                        case M:
                            {
                                return matrixMA;
                            }
                        case H:
                            {
                                return matrixHA;
                            }
                    }
                }
            case B:
                {
                    switch (characteristic) {
                        case L:
                            {
                                return matrixLB;
                            }
                        case M:
                            {
                                return matrixMB;
                            }
                        case H:
                            {
                                return matrixHB;
                            }
                    }
                }
            case C:
                {
                    switch (characteristic) {
                        case L:
                            {
                                return matrixLC;
                            }
                        case M:
                            {
                                return matrixMC;
                            }
                        case H:
                            {
                                return matrixHC;
                            }
                    }
                }
        }
        throw new RuntimeException("Bad characteristic or action");
    }

    public int getAction() {
        return action;
    }

    public final Color getColor() {
        switch (action) {
            case A:
                return Color.red;
            case B:
                return Color.green;
            case C:
                return Color.blue;
            default:
                throw new RuntimeException("Bad action.");
        }
        /*switch (characteristic) {
            case L: return Color.white;
            case M: return Color.gray;
            case H: return Color.black;
            default: throw new RuntimeException("Bad action.");
        }*/
    }
}

public class ABCModel extends Scape {

    protected int span = 10;

    public Scape cells;

    public void createScape() {
        super.createScape();
        setName("ABC Diffusion Model");
        cells = new Scape(new Array2DMoore());
        cells.setExtent(new Coordinate2DDiscrete(span, span));
        cells.setPrototypeAgent(new ABCCell());
        cells.getRules().clear();
        cells.addRule(new Rule("Decide action") {
            public void execute(Agent a) {
                ((ABCCell) a).decideAction();
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
        overheadView.setCellSize(20);
        //overheadView.setDrawEveryNUpdates(100);
        cells.setCellsRequestUpdates(true);
        cells.addView(overheadView);
        final StatCollector[] stats = {
            new StatCollectorCond("A Above") {
                public boolean meetsCondition(Object object) {
                    return ((((ABCCell) object).action == ABCCell.A) && (((ABCCell) object).isAbove()));
                }
            },
            new StatCollectorCond("B Above") {
                public boolean meetsCondition(Object object) {
                    return ((((ABCCell) object).action == ABCCell.B) && (((ABCCell) object).isAbove()));
                }
            },
            new StatCollectorCond("C Above") {
                public boolean meetsCondition(Object object) {
                    return ((((ABCCell) object).action == ABCCell.C) && (((ABCCell) object).isAbove()));
                }
            },
            new StatCollectorCond("A Below") {
                public boolean meetsCondition(Object object) {
                    return ((((ABCCell) object).action == ABCCell.A) && !(((ABCCell) object).isAbove()));
                }
            },
            new StatCollectorCond("B Below") {
                public boolean meetsCondition(Object object) {
                    return ((((ABCCell) object).action == ABCCell.B) && !(((ABCCell) object).isAbove()));
                }
            },
            new StatCollectorCond("C Below") {
                public boolean meetsCondition(Object object) {
                    return ((((ABCCell) object).action == ABCCell.C) && !(((ABCCell) object).isAbove()));
                }
            }
        };
        cells.addStatCollectors(stats);

        if (!(getUserEnvironment().isInApplet())) {
            ChartView aboveChart = new ChartView();
            aboveChart.setName("Action Above");
            cells.addView(aboveChart);
            aboveChart.addSeries("Count A Above", Color.red);
            aboveChart.addSeries("Count B Above", Color.green);
            aboveChart.addSeries("Count C Above", Color.blue);
            ChartView belowChart = new ChartView();
            belowChart.setName("Action Below");
            cells.addView(belowChart);
            belowChart.addSeries("Count A Below", Color.red);
            belowChart.addSeries("Count B Below", Color.green);
            belowChart.addSeries("Count C Below", Color.blue);
        }
    }

    public int getSpan() {
        return span;
    }

    public void setSpan(int span) {
        this.span = span;
    }

    public float getBeta() {
        return ABCCell.beta;
    }

    public void setBeta(float beta) {
        ABCCell.beta = beta;
    }

    public float getGamma() {
        return ABCCell.gamma;
    }

    public void setGamma(float gamma) {
        ABCCell.gamma = gamma;
    }

    public float getMatrix_H_A() {
        return ABCCell.matrixHA;
    }

    public void setMatrix_H_A(float matrixHA) {
        ABCCell.matrixHA = matrixHA;
    }

    public float getMatrix_H_B() {
        return ABCCell.matrixHB;
    }

    public void setMatrix_H_B(float matrixHB) {
        ABCCell.matrixHB = matrixHB;
    }

    public float getMatrix_H_C() {
        return ABCCell.matrixHC;
    }

    public void setMatrix_H_C(float matrixHC) {
        ABCCell.matrixHC = matrixHC;
    }

    public float getMatrix_M_A() {
        return ABCCell.matrixMA;
    }

    public void setMatrix_M_A(float matrixMA) {
        ABCCell.matrixMA = matrixMA;
    }

    public float getMatrix_M_B() {
        return ABCCell.matrixMB;
    }

    public void setMatrix_M_B(float matrixMB) {
        ABCCell.matrixMB = matrixMB;
    }

    public float getMatrix_M_C() {
        return ABCCell.matrixMC;
    }

    public void setMatrix_M_C(float matrixMC) {
        ABCCell.matrixMC = matrixMC;
    }

    public float getMatrix_L_A() {
        return ABCCell.matrixLA;
    }

    public void setMatrix_L_A(float matrixLA) {
        ABCCell.matrixLA = matrixLA;
    }

    public float getMatrix_L_B() {
        return ABCCell.matrixLB;
    }

    public void setMatrix_L_B(float matrixLB) {
        ABCCell.matrixLB = matrixLB;
    }

    public float getMatrix_L_C() {
        return ABCCell.matrixLC;
    }

    public void setMatrix_L_C(float matrixLC) {
        ABCCell.matrixLC = matrixLC;
    }
}
