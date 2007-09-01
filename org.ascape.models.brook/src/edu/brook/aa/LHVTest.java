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

import java.io.File;
import java.io.IOException;

import org.ascape.model.ModelRoot;
import org.ascape.util.data.StatCollector;
import org.ascape.util.data.StatCollectorCalculated;
import org.ascape.util.sweep.SweepDimension;
import org.ascape.util.sweep.SweepLink;
import org.ascape.view.nonvis.DataOutputView;
import org.ascape.view.nonvis.SweepControlView;


public class LHVTest extends LHV {

    public void createScape() {
        ModelRoot.setDisplayGraphics(false);
        super.createScape();
    }

    public void createNonGraphicViews() {
        super.createNonGraphicViews();
        //BatchView batchView = new BatchView();
        //addView(batchView);
        //SweepControlView sweeper = batchView.getSweepView();
        SweepControlView sweeper = new SweepControlView();
        addView(sweeper);
        SweepLink fertilityAgeDim = new SweepLink();
        fertilityAgeDim.addMember(new SweepDimension(this, "MinFertilityAge", 13, 16));
        fertilityAgeDim.addMember(new SweepDimension(this, "MaxFertilityAge", 19, 16, -1));
        SweepLink fertilityEndsAgeDim = new SweepLink();
        fertilityEndsAgeDim.addMember(new SweepDimension(this, "MinFertilityEndsAge", 26, 30));
        fertilityEndsAgeDim.addMember(new SweepDimension(this, "MaxFertilityEndsAge", 34, 30, -1));
        SweepLink fertilityDim = new SweepLink();
        fertilityDim.addMember(new SweepDimension(this, "MinFertility", 0.09, 0.1251, .0025));
        fertilityDim.addMember(new SweepDimension(this, "MaxFertility", 0.16, 0.1249, -.0025));
        SweepLink deathDim = new SweepLink();
        deathDim.addMember(new SweepDimension(this, "MinDeathAge", 26, 30));
        deathDim.addMember(new SweepDimension(this, "MaxDeathAge", 34, 30, -1));
        sweeper.getSweepGroup().addMember(fertilityAgeDim);
        sweeper.getSweepGroup().addMember(fertilityEndsAgeDim);
        sweeper.getSweepGroup().addMember(fertilityDim);
        sweeper.getSweepGroup().addMember(deathDim);
        sweeper.getSweepGroup().setRunsPer(5);

        final StatCollector householdCount = getData().getStatCollector("Historic Households");
        addStatCollector(new StatCollectorCalculated("Difference Squared") {
            public double calculateValue() {
                return (householdCount.getCount());
            }
        });


        //DataOutputView dataView = batchView.getDataView();
        DataOutputView dataView = new DataOutputView();
        addView(dataView);
        try {
            setStartPeriod(800);
            setStopPeriod(1350);
        } catch (org.ascape.model.space.SpatialTemporalException e) {
            System.out.println("Bad start/stop periods: " + e);
        }
        try {
            dataView.setRunFile(new File("TestRMS.txt"));
        } catch (IOException e) {
            throw new RuntimeException(e.toString());
        }
    }
}
