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
 * Miles Parker
 * mparker@brook.edu
 * The Brookings Institution
 * Washington, D.C.
 */

package edu.brook.retirement;

//Temporary for JDK 1.1 compatibility
//import com.sun.java.util.collections.*;

import java.awt.Color;
import java.awt.Graphics;

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
        setSpace(new Array2DMoore());
        setAutoCreate(true);
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
    }

    public void createScape() {
        setExtent(new Coordinate2DDiscrete(cohortSize, getAgeSpan()));
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

    public void createViews() {
        super.createViews();
        view = new OverheadRelative2DView();
        view.setCellSize(4);
        view.setBorderSize(1);
        ColorFeatureGradiated cellColor =
            new ColorFeatureGradiated("Age", Color.blue, new UnitIntervalDataPoint() {
                public double getValue(Object object) {
                    return ((double) (((Person) object).getAge() - youngestAge) / ((Population) ((Agent) object).getScape()).getAgeSpan());
                }
            });
        DrawColorFeature drawAge = new DrawColorFeature("Draw Age", cellColor) {
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
