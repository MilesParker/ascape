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
 * _Demographic Prisoners Dilemma_
 * See _Zones of Cooperation in Demographic Prisoner's Dilemma_
 * Santa Fe Institute Economics Research Program Working Paper
 * Joshua Epstein
 * jepstein@brook.edu
 *
 * Miles Parker
 * mparker@brook.edu
 * The Brookings Institution
 * Washington, D.C.
 */

package name.milesparker.spatialipd;

import java.io.File;
import java.io.IOException;

import org.ascape.util.sweep.SweepDimension;
import org.ascape.util.sweep.SweepLink;
import org.ascape.view.nonvis.DataOutputView;
import org.ascape.view.nonvis.StandardOutView;
import org.ascape.view.nonvis.SweepControlView;


public class PD2DGAMultiple extends PD2DGA {

    public void createViews() {
        //super.createViews();
        DataOutputView dataView = new DataOutputView() {
            public void writeRunHeader() throws IOException {
                super.writeRunHeader();
                runDataStream.writeBytes("\t");
                runDataStream.writeBytes("Cooperate Ratio");
                runDataStream.writeBytes("\t");
                runDataStream.writeBytes("Defect Ratio");
                runDataStream.writeBytes("\t");
                runDataStream.writeBytes("Cooperate Closest");
                runDataStream.writeBytes("\t");
                runDataStream.writeBytes("Cooperate Farthest");
                runDataStream.writeBytes("\t");
                runDataStream.writeBytes("Defect Closest");
                runDataStream.writeBytes("\t");
                runDataStream.writeBytes("Defect Farthest");
            }

            public void writeRunData() throws IOException {
                super.writeRunData();
                runDataStream.writeBytes("\t");
                runDataStream.writeBytes(Double.toString(getDataSelection().getData().getSeries("Sum Cooperate Ratio").getValue()));
                runDataStream.writeBytes("\t");
                runDataStream.writeBytes(Double.toString(getDataSelection().getData().getSeries("Sum Defect Ratio").getValue()));
                runDataStream.writeBytes("\t");
                runDataStream.writeBytes(Double.toString(getDataSelection().getData().getSeries("Count Cooperate Closest").getValue()));
                runDataStream.writeBytes("\t");
                runDataStream.writeBytes(Double.toString(getDataSelection().getData().getSeries("Count Cooperate Farthest").getValue()));
                runDataStream.writeBytes("\t");
                runDataStream.writeBytes(Double.toString(getDataSelection().getData().getSeries("Count Defect Closest").getValue()));
                runDataStream.writeBytes("\t");
                runDataStream.writeBytes(Double.toString(getDataSelection().getData().getSeries("Count Defect Farthest").getValue()));
            }
        };
        addView(dataView);
        getAgents().getRules().setSelected("Play Preference", true);
        try {
            dataView.setRunFile(new File("PlayPreference.txt"));
        } catch (IOException e) {
            throw new RuntimeException(e.toString());
        }
        addView(new StandardOutView());
        SweepControlView sweep = new SweepControlView();
        SweepLink doNothing = new SweepLink();
        doNothing.addMember(new SweepDimension(this, "MemorySize", 1, 1));
        sweep.getSweepGroup().setRunsPer(35);
        sweep.getSweepGroup().addMember(doNothing);
        addView(sweep);
        try {
            setStopPeriod(1000);
        } catch (org.ascape.model.space.SpatialTemporalException e) {
        }
        //We don't need to export historic data, and we want to match current C++ output
        dataView.getDataSelection().setSelected("Sum Cooperate Ratio", false);
        dataView.getDataSelection().setSelected("Sum Defect Ratio", false);
        dataView.getDataSelection().setSelected("Count Cooperate Closest", false);
        dataView.getDataSelection().setSelected("Count Cooperate Farthest", false);
        dataView.getDataSelection().setSelected("Count Defect Closest", false);
        dataView.getDataSelection().setSelected("Count Defect Farthest", false);
        setViewSelf(true);
    }
}
