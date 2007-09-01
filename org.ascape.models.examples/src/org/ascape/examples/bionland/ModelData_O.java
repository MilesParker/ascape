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
 *          All rights reserved worldwide.
 *
 * U.S. GOVERNMENT END USERS. The software is a "commercial item," as that term is defined in 48 C.F.R. 2.101,
 * consisting of "commercial computer software" and "commercial computer software documentation," as such terms are
 * used in 48 C.F.R. 12.212. Consistent with 48 C.F.R. 12.212 and 48 C.F.R. 227.7202-1 through 227.7202-4, and other
 * relevant sections of the Code of Federal Regulations, as applicable all U.S. Government End Users acquire the
 * software with only those rights set forth in the license agreement. This software includes trade secrets that
 * are exempt from disclosure under the Freedom of Information Act.
 *
 */


package org.ascape.examples.bionland;

import java.io.File;
import java.io.IOException;

import org.ascape.model.event.ScapeEvent;
import org.ascape.model.space.Array2D;
import org.ascape.util.sweep.SweepDimension;
import org.ascape.view.nonvis.DataOutputView;
import org.ascape.view.nonvis.StandardOutView;
import org.ascape.view.nonvis.SweepControlView;



public class ModelData_O extends Model_M {

    public void createScape() {
        super.createScape();
        ((Array2D) territory.getSpace()).setExtent(10, 10);
    }

    public void createViews() {
        addView(new StandardOutView() {
            public void scapeIterated(ScapeEvent scapeEvent) {
                super.scapeIterated(scapeEvent);
                if ((getPeriod() / 10) * 10 == getPeriod())
                    System.out.println(getPeriod());
            }
        });
        createSelfView();
        setAutoRestart(false);
        DataOutputView outView = new DataOutputView();
        addView(outView);
        try {
            outView.setRunFile(new File("RunFileTest.txt"));
            outView.setPeriodFile(new File("DataFileTest.txt"));
        } catch (IOException e) {
            System.out.println("Exception while creating output File: " + e);
        }
        try {
            setStopPeriod(500);
        } catch (org.ascape.model.space.SpatialTemporalException e) {
            System.out.println(e);
        }
        SweepControlView sweep = new SweepControlView("Model Parameter Sweep");
        addView(sweep);
        sweep.getSweepGroup().addMember(new SweepDimension(this, "DiffusionConstant", 0.0f, 1.0f, 0.05f));
    }
}
