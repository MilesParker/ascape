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

//Temporary for JDK 1.1 compatibility
//import com.sun.java.util.collections.*;

import java.awt.Color;

import org.ascape.view.vis.ChartView;


/**
 * This class involves very preliminary exploration.
 */
public class LHVClan extends LHVDisaggregate {

    public void createScape() {
        super.createScape();
        people.setPrototypeAgent(new PersonClan());
        minFertility = .33;
        //harvestAdjustment = .75;
        //harvestAdjustment = .80;

        //setRandomSeed(936050508840L);
        try {
            setStopPeriod(1400);
        } catch (org.ascape.model.space.SpatialTemporalException e) {
        }
    }

    public void createViews() {
        ChartView chart = new ChartView();
        valley.addView(chart);
        //And add some of the stat series we've just created to it
        chart.addSeries("Count Orange Clan", Color.orange);
        chart.addSeries("Count Red Clan", Color.red);
        chart.addSeries("Count Green Clan", Color.green);
        chart.addSeries("Count Yellow Clan", Color.yellow);
        chart.addSeries("Count Pink Clan", Color.pink);
        chart.addSeries("Count Cyan Clan", Color.cyan);

        super.createViews();
        view.getDrawSelection().setSelected("Clan", true);
    }
}
