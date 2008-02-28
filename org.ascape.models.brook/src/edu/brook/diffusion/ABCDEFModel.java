/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.diffusion;

import java.awt.Color;

import org.ascape.model.Agent;
import org.ascape.model.Scape;
import org.ascape.model.rule.Rule;
import org.ascape.model.space.Array2DMoore;
import org.ascape.model.space.Coordinate2DDiscrete;
import org.ascape.runtime.swing.DesktopEnvironment;
import org.ascape.util.data.StatCollector;
import org.ascape.util.data.StatCollectorCond;
import org.ascape.view.vis.ChartView;
import org.ascape.view.vis.Overhead2DView;


public class ABCDEFModel extends Scape {

    /**
     * 
     */
    private static final long serialVersionUID = -4539607186730331772L;

    protected int span = 10;

    public Scape cells;

    public void createScape() {
        super.createScape();
        setName("ABCDEF Diffusion Model");
        cells = new Scape(new Array2DMoore());
        cells.setExtent(new Coordinate2DDiscrete(span, span));
        cells.setPrototypeAgent(new ABCDEFCell());
        cells.getRules().clear();
        cells.addRule(new Rule("Decide action") {
            /**
             * 
             */
            private static final long serialVersionUID = 1613828427092776595L;

            public void execute(Agent a) {
                ((ABCDEFCell) a).decideAction();
            }
        });
        cells.setExecutionStyle(REPEATED_DRAW);
        cells.setAgentsPerIteration(25);
        add(cells);
    }

    Overhead2DView overheadView = new Overhead2DView();

    public void createViews() {
        super.createViews();
        //cells.addDrawFeature(ABCDEFCell.DRAW_BandW_Feature);

        overheadView = new Overhead2DView();
        overheadView.setCellSize(19);
        //Set the cell size to be 12
        //overheadView.setDrawEveryNUpdates(100);
        //cells.setCellsRequestUpdates(true);
        cells.addView(overheadView);
        //overheadView.getDrawSelection().clearSelection();
        //overheadView.getDrawSelection().setSelected("Black & White", true);
        final StatCollector[] stats = {
            new StatCollectorCond(".4 Above") {
                /**
                 * 
                 */
                private static final long serialVersionUID = -2517957250177288267L;

                public boolean meetsCondition(Object object) {
                    return ((((ABCDEFCell) object).action == ABCDEFCell.C) && (((ABCDEFCell) object).isAbove()));
                }
            },
            new StatCollectorCond(".5 Above") {
                /**
                 * 
                 */
                private static final long serialVersionUID = -92935995073631806L;

                public boolean meetsCondition(Object object) {
                    return ((((ABCDEFCell) object).action == ABCDEFCell.B) && (((ABCDEFCell) object).isAbove()));
                }
            },
            new StatCollectorCond(".6 Above") {
                /**
                 * 
                 */
                private static final long serialVersionUID = -7533252159672439017L;

                public boolean meetsCondition(Object object) {
                    return ((((ABCDEFCell) object).action == ABCDEFCell.A) && (((ABCDEFCell) object).isAbove()));
                }
            },
            new StatCollectorCond(".7 Above") {
                /**
                 * 
                 */
                private static final long serialVersionUID = 1391130853409262577L;

                public boolean meetsCondition(Object object) {
                    return ((((ABCDEFCell) object).action == ABCDEFCell.F) && (((ABCDEFCell) object).isAbove()));
                }
            },
            new StatCollectorCond(".8 Above") {
                /**
                 * 
                 */
                private static final long serialVersionUID = -872369415223672318L;

                public boolean meetsCondition(Object object) {
                    return ((((ABCDEFCell) object).action == ABCDEFCell.E) && (((ABCDEFCell) object).isAbove()));
                }
            },
            new StatCollectorCond(".9 Above") {
                /**
                 * 
                 */
                private static final long serialVersionUID = 8092322670885996141L;

                public boolean meetsCondition(Object object) {
                    return ((((ABCDEFCell) object).action == ABCDEFCell.D) && (((ABCDEFCell) object).isAbove()));
                }
            },
            new StatCollectorCond(".4 Below") {
                /**
                 * 
                 */
                private static final long serialVersionUID = -2741618534906475573L;

                public boolean meetsCondition(Object object) {
                    return ((((ABCDEFCell) object).action == ABCDEFCell.C) && !(((ABCDEFCell) object).isAbove()));
                }
            },
            new StatCollectorCond(".5 Below") {
                /**
                 * 
                 */
                private static final long serialVersionUID = 1302265613146811245L;

                public boolean meetsCondition(Object object) {
                    return ((((ABCDEFCell) object).action == ABCDEFCell.B) && !(((ABCDEFCell) object).isAbove()));
                }
            },
            new StatCollectorCond(".6 Below") {
                /**
                 * 
                 */
                private static final long serialVersionUID = -5926153306106169760L;

                public boolean meetsCondition(Object object) {
                    return ((((ABCDEFCell) object).action == ABCDEFCell.A) && !(((ABCDEFCell) object).isAbove()));
                }
            },
            new StatCollectorCond(".7 Below") {
                /**
                 * 
                 */
                private static final long serialVersionUID = -7931591189206725628L;

                public boolean meetsCondition(Object object) {
                    return ((((ABCDEFCell) object).action == ABCDEFCell.F) && !(((ABCDEFCell) object).isAbove()));
                }
            },
            new StatCollectorCond(".8 Below") {
                /**
                 * 
                 */
                private static final long serialVersionUID = -568015063352681396L;

                public boolean meetsCondition(Object object) {
                    return ((((ABCDEFCell) object).action == ABCDEFCell.E) && !(((ABCDEFCell) object).isAbove()));
                }
            },
            new StatCollectorCond(".9 Below") {
                /**
                 * 
                 */
                private static final long serialVersionUID = 4196661452132806270L;

                public boolean meetsCondition(Object object) {
                    return ((((ABCDEFCell) object).action == ABCDEFCell.D) && !(((ABCDEFCell) object).isAbove()));
                }
            }
        };
        cells.addStatCollectors(stats);

        if (!(((DesktopEnvironment) getUIEnvironment()).isInApplet())) {
            ChartView aboveChart = new ChartView();
            aboveChart.setName("Action Above");
            cells.addView(aboveChart);
            aboveChart.addSeries("Count .4 Above", Color.blue);
            aboveChart.addSeries("Count .5 Above", Color.green);
            aboveChart.addSeries("Count .6 Above", Color.red);
            aboveChart.addSeries("Count .7 Above", Color.orange);
            aboveChart.addSeries("Count .8 Above", Color.cyan);
            aboveChart.addSeries("Count .9 Above", Color.magenta);
            ChartView belowChart = new ChartView();
            belowChart.setName("Action Below");
            cells.addView(belowChart);
            belowChart.addSeries("Count .4 Below", Color.blue);
            belowChart.addSeries("Count .5 Below", Color.green);
            belowChart.addSeries("Count .6 Below", Color.red);
            belowChart.addSeries("Count .7 Below", Color.orange);
            belowChart.addSeries("Count .8 Below", Color.cyan);
            belowChart.addSeries("Count .9 Below", Color.magenta);
        }
    }

    public float getBeta() {
        return ABCDEFCell.beta;
    }

    public void setBeta(float beta) {
        ABCDEFCell.beta = beta;
    }

    public float getGamma() {
        return ABCDEFCell.gamma;
    }

    public void setGamma(float gamma) {
        ABCDEFCell.gamma = gamma;
    }

    public float getMatrix_H_A() {
        return ABCDEFCell.matrixHA;
    }

    public void setMatrix_H_A(float matrixHA) {
        ABCDEFCell.matrixHA = matrixHA;
    }

    public float getMatrix_H_B() {
        return ABCDEFCell.matrixHB;
    }

    public void setMatrix_H_B(float matrixHB) {
        ABCDEFCell.matrixHB = matrixHB;
    }

    public float getMatrix_H_C() {
        return ABCDEFCell.matrixHC;
    }

    public void setMatrix_H_C(float matrixHC) {
        ABCDEFCell.matrixHC = matrixHC;
    }

    public float getMatrix_M_A() {
        return ABCDEFCell.matrixMA;
    }

    public void setMatrix_M_A(float matrixMA) {
        ABCDEFCell.matrixMA = matrixMA;
    }

    public float getMatrix_M_B() {
        return ABCDEFCell.matrixMB;
    }

    public void setMatrix_M_B(float matrixMB) {
        ABCDEFCell.matrixMB = matrixMB;
    }

    public float getMatrix_M_C() {
        return ABCDEFCell.matrixMC;
    }

    public void setMatrix_M_C(float matrixMC) {
        ABCDEFCell.matrixMC = matrixMC;
    }

    public float getMatrix_L_A() {
        return ABCDEFCell.matrixLA;
    }

    public void setMatrix_L_A(float matrixLA) {
        ABCDEFCell.matrixLA = matrixLA;
    }

    public float getMatrix_L_B() {
        return ABCDEFCell.matrixLB;
    }

    public void setMatrix_L_B(float matrixLB) {
        ABCDEFCell.matrixLB = matrixLB;
    }

    public float getMatrix_L_C() {
        return ABCDEFCell.matrixLC;
    }

    public void setMatrix_L_C(float matrixLC) {
        ABCDEFCell.matrixLC = matrixLC;
    }


    public float getMatrix_H_D() {
        return ABCDEFCell.matrixHD;
    }

    public void setMatrix_H_D(float matrixHD) {
        ABCDEFCell.matrixHD = matrixHD;
    }

    public float getMatrix_H_E() {
        return ABCDEFCell.matrixHE;
    }

    public void setMatrix_H_E(float matrixHE) {
        ABCDEFCell.matrixHE = matrixHE;
    }

    public float getMatrix_H_F() {
        return ABCDEFCell.matrixHF;
    }

    public void setMatrix_H_F(float matrixHF) {
        ABCDEFCell.matrixHF = matrixHF;
    }

    public float getMatrix_M_D() {
        return ABCDEFCell.matrixMD;
    }

    public void setMatrix_M_D(float matrixMD) {
        ABCDEFCell.matrixMD = matrixMD;
    }

    public float getMatrix_M_E() {
        return ABCDEFCell.matrixME;
    }

    public void setMatrix_M_E(float matrixME) {
        ABCDEFCell.matrixME = matrixME;
    }

    public float getMatrix_M_F() {
        return ABCDEFCell.matrixMF;
    }

    public void setMatrix_M_F(float matrixMF) {
        ABCDEFCell.matrixMF = matrixMF;
    }

    public float getMatrix_L_D() {
        return ABCDEFCell.matrixLD;
    }

    public void setMatrix_L_D(float matrixLD) {
        ABCDEFCell.matrixLD = matrixLD;
    }

    public float getMatrix_L_E() {
        return ABCDEFCell.matrixLE;
    }

    public void setMatrix_L_E(float matrixLE) {
        ABCDEFCell.matrixLE = matrixLE;
    }

    public float getMatrix_L_F() {
        return ABCDEFCell.matrixLF;
    }

    public void setMatrix_L_F(float matrixLF) {
        ABCDEFCell.matrixLF = matrixLF;
    }

    public int getSpan() {
        return span;
    }

    public void setSpan(int span) {
        this.span = span;
    }
}
