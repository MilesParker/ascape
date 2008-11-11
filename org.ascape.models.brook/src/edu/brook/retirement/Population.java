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
import java.util.TooManyListenersException;

import org.ascape.model.Agent;
import org.ascape.model.Scape;
import org.ascape.model.event.ScapeEvent;
import org.ascape.model.space.Array2D;
import org.ascape.model.space.Array2DMoore;
import org.ascape.model.space.Coordinate2DDiscrete;
import org.ascape.util.data.UnitIntervalDataPoint;
import org.ascape.util.vis.ColorFeatureGradiated;
import org.ascape.util.vis.DrawColorFeature;
import org.ascape.view.vis.OverheadRelative2DView;

public class Population extends Scape {

    /**
     * 
     */
    private static final long serialVersionUID = -826827440399030214L;

    private int youngestRow;

    private int cohortSize = 100;

    private int youngestAge = 50;

    private int oldestAge = 100;

    private int minNetworkSize = 10;

    private int maxNetworkSize = 25;

    private int maxAgeDifference = 5;

    private int retirementEligibility = 65;

    protected double fractionImitator;

    private double fractionRational;

    private double fractionRandom;

    private OverheadRelative2DView view;

    public Population() {
        super();
    }

    public void createScape() {
        setSpace(new Array2DMoore());
        setExtent(new Coordinate2DDiscrete(cohortSize, getAgeSpan()));
//        setAutoCreate(true);
        setPrototypeAgent(new Person());
        setFractionRational(.10);
        setFractionRandom(.05);
        //Note that there is a very interesting dependency on by rule vs. by agent execution
        //When execution is by agent (that is, agents age asynchronously) retirement often occurs
        //from the top, the youngest cohorts begin retiring first. When execution is by rule,
        //the expected (reported) behavior is obtained.
        setExecutionOrder(RULE_ORDER);
        //setExecutionOrder(AGENT_ORDER);
        addRule(ITERATE_RULE);
        super.createScape();
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
            youngestRow = ((Array2D) getSpace()).getYSize() - 1;
        }
        if (view != null) {
            view.getOrigin().setYValue(youngestRow);
        }
        super.scapeIterated(event);
    }

    public void createGraphicViews() {
        super.createGraphicViews();
        view = new OverheadRelative2DView();
        view.setCellSize(4);
        view.setBorderSize(1);
        ColorFeatureGradiated cellColor =
            new ColorFeatureGradiated("Age", Color.blue, new UnitIntervalDataPoint() {
                /**
                 * 
                 */
                private static final long serialVersionUID = 2563022449995502359L;

                public double getValue(Object object) {
                    return ((double) (((Person) object).getAge() - youngestAge) / ((Population) ((Agent) object).getScape()).getAgeSpan());
                }
            });
        DrawColorFeature drawAge = new DrawColorFeature("Draw Age", cellColor) {
            /**
             * 
             */
            private static final long serialVersionUID = -1777757536635594803L;

            public void draw(Graphics g, Object object, int width, int height) {
                g.setColor(getColor(object));
                g.fillRect(0, 0, width, height);
            }
        };
        addDrawFeature(drawAge);
        //This is just a simple way to verify that calls are cocuring in specified (random) order;
        /*ColorFeatureGradiated callColor = new ColorFeatureGradiated();
        callColor.setDataPoint(new UnitIntervalDataPoint() {
public double getValue(Object object) {
return ((double) (double) (((Person) object).callOrder) / ((Population) ((Agent) object).getScape()).getSize());
}
public String getName() {
return "Call";
}
        });
        callColor.setMaximumColor(Color.black);
        DrawColorFeature drawCall = new DrawColorFeature() {
public void draw(Graphics g, Object object, int width, int height) {
g.setColor(getColor(object));
g.fillRect(0, 0, width, height);
}
        };
        drawCall.setColorFeature(callColor);
        drawCall.setName("Call Order");
        addDrwFeature(drawCall);*/
        //view.setDrawNetwork(true);
        addView(view);
        view.getDrawSelection().clearSelection();
        view.getDrawSelection().setSelected(view.cells_fill_draw_feature, true);
        //view.getDrawSelection().setSelected(drawAge, true);
        //view.getDrawSelection().setSelected(drawCall, true);
    }

/*public void updateScapeGraphics() {
	    System.out.println(getPeriod());
	}*/

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

    public int getMaxNetworkAgeDifference() {
        return maxAgeDifference;
    }

    public int getMinNetworkSize() {
        return minNetworkSize;
    }

    public void setMinNetworkSize(int minNetworkSize) {
        this.minNetworkSize = minNetworkSize;
    }

    public int getMaxNetworkSize() {
        return maxNetworkSize;
    }

    public void setMaxNetworkSize(int maxNetworkSize) {
        this.maxNetworkSize = maxNetworkSize;
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
