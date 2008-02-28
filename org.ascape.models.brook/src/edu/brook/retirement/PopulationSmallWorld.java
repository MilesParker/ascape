/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.retirement;

//Temporary for JDK 1.1 compatibility
//import com.sun.java.util.collections.*;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

import org.ascape.model.Agent;
import org.ascape.model.Cell;
import org.ascape.model.Scape;
import org.ascape.model.event.ScapeEvent;
import org.ascape.model.space.Array2DSmallWorld;
import org.ascape.model.space.Coordinate2DDiscrete;
import org.ascape.util.data.UnitIntervalDataPoint;
import org.ascape.util.vis.ColorFeatureGradiated;
import org.ascape.util.vis.DrawColorFeature;
import org.ascape.view.vis.OverheadRelative2DView;

public class PopulationSmallWorld extends Scape {

    /**
     * 
     */
    private static final long serialVersionUID = -7438025166018172606L;

    private int youngestRow;

    private int cohortSize = 100;

    private int youngestAge = 50;

    private int oldestAge = 100;

    private int retirementEligibility = 65;

    protected double fractionImitator;

    private double fractionRational;

    private double fractionRandom;

    private OverheadRelative2DView view;

    public PopulationSmallWorld() {
        super();
        setSpace(new Array2DSmallWorld());
        setAutoCreate(true);
        setPrototypeAgent(new PersonSmallWorld());
        setFractionRational(.10);
        setFractionRandom(.05);
        ((Array2DSmallWorld) getSpace()).setRadius(2);
        //Note that there is a very interesting dependency on by rule vs. by agent execution
        //When execution is by agent (that is, agents age asynchronously) retirement often occurs
        //from the top, the youngest cohorts begin retiring first. When execution is by rule,
        //the expected (reported) behavior is obtained.
        setExecutionOrder(RULE_ORDER);
        //setExecutionOrder(AGENT_ORDER);
    }

    public void createScape() {
        setExtent(new Coordinate2DDiscrete(cohortSize, getAgeSpan()));
        super.createScape();
    }

    public List calculateNeighbors(Cell cell) {
        //For this version, selection of neighbors is bound to the radius of the neighborhood.
        //This way, ages of network members will always be within n of the cohort, regardless of size.
        //Node[] n = getCellsNearMoore(cell, false, radius);
        List n = findWithin(cell.getCoordinate(), null, false, ((Array2DSmallWorld) getSpace()).getRadius());
        final Array2DSmallWorld sw = ((Array2DSmallWorld) getSpace());
        if (sw.getRandomEdgeRatio() > 0.0) {
            for (int i = 0; i < n.size(); i++) {
                if (getRandom().nextDouble() < ((Array2DSmallWorld) getSpace()).getRandomEdgeRatio()) {
                    //Warning..O(n^2)..need to replace with a hash table
                    //The following code just nesures that we don't link to the same cell twice
                    boolean tryAgain;
                    do {
                        tryAgain = false;
                        int row = ((Coordinate2DDiscrete) cell.getCoordinate()).getYValue();
                        n.add(i, sw.findRandom(cell, 0, row - sw.getRadius(), sw.getXSize(), sw.getRadius() * 2 + 1));
                        for (int j = 0; j < n.size(); j++) {
                            if ((j != i) && (n.get(j) == n.get(i))) {
                                tryAgain = true;
                            }
                        }
                    } while (tryAgain);
                }
            }
        }
        return n;
    }

    public void initialize() {
        youngestRow = 0;
        if (view != null) {
            view.getOrigin().setYValue(youngestRow);
        }
        super.initialize();
    }

    public int getAgeForRow(int row) {
        //Do it yourself %, Java's is useless for common operations.
        return row >= youngestRow ? youngestAge + (row - youngestRow) : row + (oldestAge - youngestRow) + 1;
    }

    public int getRowForAge(int age) {
        return ((youngestRow + age - youngestAge) % (oldestAge - youngestAge + 1));
    }

    public void scapeIterated(ScapeEvent event) {
        if (youngestRow > 0) {
            youngestRow = youngestRow - 1;
        } else {
            youngestRow = ((Array2DSmallWorld) getSpace()).getYSize() - 1;
        }
        if (view != null) {
            view.getOrigin().setYValue(youngestRow);
        }
        super.scapeIterated(event);
    }

    public void createViews() {
        super.createViews();
        view = new OverheadRelative2DView();
        view.setCellSize(4);
        view.setBorderSize(1);
        ColorFeatureGradiated cellColor =
            new ColorFeatureGradiated("Age", Color.blue, new UnitIntervalDataPoint() {
                /**
                 * 
                 */
                private static final long serialVersionUID = 7509539289931311600L;

                public double getValue(Object object) {
                    return ((double) (((PersonSmallWorld) object).getAge() - youngestAge) / ((PopulationSmallWorld) ((Agent) object).getScape()).getAgeSpan());
                }
            });
        DrawColorFeature drawAge = new DrawColorFeature("Draw Age", cellColor) {
            /**
             * 
             */
            private static final long serialVersionUID = 4547781325683979953L;

            public void draw(Graphics g, Object object, int width, int height) {
                g.setColor(getColor(object));
                g.fillRect(0, 0, width, height);
            }
        };
        addDrawFeature(drawAge);
        addView(view);
        view.setDrawNetwork(true);
        view.getDrawSelection().clearSelection();
        view.getDrawSelection().setSelected(view.cells_fill_draw_feature, true);
    }

    public int getYoungestRow() {
        return youngestRow;
    }

    public int getYoungestAge() {
        return youngestAge;
    }

    public int getOldestAge() {
        return oldestAge;
    }

    public int getAgeSpan() {
        return ((oldestAge - youngestAge) + 1);
    }

    public int getCohortSize() {
        return cohortSize;
    }

    public int getRetirementEligibilityAge() {
        return retirementEligibility;
    }

    public void setRetirementEligibilityAge(int retirementEligibility) {
        this.retirementEligibility = retirementEligibility;
    }

    public double getFractionRational() {
        return fractionRational;
    }

    public void setFractionRational(double fractionRational) {
        this.fractionRational = fractionRational;
        if ((fractionRational + fractionRandom) > 1.0) {
            throw new RuntimeException("Warning: fraction random and fraction rational greater than 1.0.");
        }
    }

    public double getFractionRandom() {
        return fractionRandom;
    }

    public void setFractionRandom(double fractionRandom) {
        this.fractionRandom = fractionRandom;
        if ((fractionRational + fractionRandom) > 1.0) {
            throw new RuntimeException("Warning: fraction random and fraction rational greater than 1.0.");
        }
    }

    public double getFractionImitator() {
        return 1.0 - (fractionRational + fractionRandom);
    }
}
