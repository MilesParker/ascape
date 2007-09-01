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
 * Artificial Anasazi - Long House Valley
 * see _Understanding Anasazi Culture Change Through Agent-Based Modeling_
 * Jeffrey S. Dean, George J. Gumerman, Joshua M. Epstein, Robert Axtell,
 * Alan C. Swedlund, Miles Parker, and Steven McCarroll
 *
 * Java Implementation:
 * Miles Parker
 * mparker@brook.edu
 * Original C++ Implementation:
 * Steven McCarroll
 * The Brookings Institution
 * Washington, D.C.
 */

package edu.brook.aa;

import java.awt.Color;
import java.awt.Graphics;
import java.io.DataInputStream;
import java.io.IOException;

import org.ascape.model.Cell;
import org.ascape.util.data.StatCollector;
import org.ascape.util.data.StatCollectorCondCSA;
import org.ascape.util.data.StatCollectorCondCSAMM;
import org.ascape.util.data.UnitIntervalDataPoint;
import org.ascape.util.vis.ColorFeatureGradiated;
import org.ascape.util.vis.Drawable;



public class HistoricSettlement extends Cell implements Drawable {

    private int SARGnumber;

    private int metersNorth;

    private int metersEast;

    private int startDate;

    private int endDate;

    private int medianDate;

    private int type;

    @SuppressWarnings("unused")
    private int size;

    @SuppressWarnings("unused")
    private int description;

    @SuppressWarnings("unused")
    private int roomCount;

    @SuppressWarnings("unused")
    private int elevation;

    private int baselineHouseholds;

    public HistoricSettlement() {
    }

    public void streamToState(DataInputStream s) throws IOException {
        SARGnumber = s.readShort();
        metersNorth = s.readShort();
        metersEast = s.readShort();
        startDate = s.readShort();
        endDate = s.readShort();
        medianDate = 1950 - s.readShort();
        //System.out.println(startDate + " - " + medianDate + " - " + endDate);
        type = s.readShort();
        size = s.readShort();
        description = s.readShort();
        roomCount = s.readShort();
        elevation = s.readShort();
        baselineHouseholds = s.readShort();
        //Per C++ code, correct typo in SARG data set
        if (SARGnumber == 2307) {
            medianDate = 1075;
        }
    }

    public boolean isExtant() {
        //if ((startDate <= getScape().getPeriod()) && (endDate > getScape().getPeriod()))
        //System.out.println(getScape().getPeriod());
        return ((startDate <= getScape().getPeriod()) && (endDate > getScape().getPeriod()));
    }

    public int getType() {
        return type;
    }

    public int getStartYear() {
        return startDate;
    }

    public int getMedianYear() {
        return medianDate;
    }

    public int getEndYear() {
        return endDate;
    }

    public int getMetersNorth() {
        return metersNorth;
    }

    public int getMetersEast() {
        return metersEast;
    }

    public int getBaselineHouseholds() {
        return baselineHouseholds;
    }

    public int getSize() {
        int period = getScape().getPeriod();
        if (period > medianDate) {
            return (int) Math.max(Math.ceil((baselineHouseholds * (float) (endDate - period)) / (float) (endDate - medianDate)), 1);
        } else if (period < medianDate) {
            return (int) Math.max(Math.ceil((baselineHouseholds * (float) (period - startDate)) / (float) (medianDate - startDate)), 1);
        } else {
            return baselineHouseholds;
        }
    }

    public int getHouseholdCount() {
        int period = getScape().getPeriod();
        if (period > medianDate) {
            return (int) Math.max(Math.ceil((baselineHouseholds * (float) (endDate - period)) / (float) (endDate - medianDate)), 1);
        } else if (period < medianDate) {
            return (int) Math.max(Math.ceil((baselineHouseholds * (float) (period - startDate)) / (float) (medianDate - startDate)), 1);
        } else {
            return baselineHouseholds;
        }
    }

    final ColorFeatureGradiated settlementColorFeature =
        new ColorFeatureGradiated("Settlement Size", Color.black, (new UnitIntervalDataPoint() {
            public double getValue(Object object) {
                return ((double) (((HistoricSettlement) object).getHouseholdCount()) / 10.0);
            }
        }));

    public void scapeCreated() {
        StatCollector[] stats = new StatCollector[2];
        stats[0] = new StatCollectorCondCSA("Historic Households") {
            public boolean meetsCondition(Object object) {
                return ((HistoricSettlement) object).isExtant();
            }

            public double getValue(Object object) {
                return ((HistoricSettlement) object).getHouseholdCount();
            }
        };
        stats[1] = new StatCollectorCondCSAMM("Historic Household Size") {
            public boolean meetsCondition(Object object) {
                return ((HistoricSettlement) object).isExtant();
            }

            public final double getValue(Object object) {
                return 5 * ((HistoricSettlement) object).getHouseholdCount();
            }
        };
        getScape().addStatCollectors(stats);
    }

    public void draw(Graphics g, int width, int height) {
        g.setColor(Color.black);
        g.drawOval(0, 0, width - 1, height - 1);
        //g.setColor(settlementColorFeature.getColor(this));
        //g.fillOval(1, 1, width - 2, height - 2);
    }

    public String toString() {
        return "Historic Settlement " + coordinate;
    }
}
