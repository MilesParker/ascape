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
 * Work in progress at the Center on Economic and Social Dynamics
 * John D. Steinbruner
 * Joshua M. Epstein
 * Carol Graham
 * Miles Parker
 * Robert Axtell
 * Peyton Young
 * The Johns Hopkins University
 * The Brookings Institution
 * Washington, D.C.
 *
 * Implementation:
 * Miles Parker
 * mparker@brook.edu
 * The Brookings Institution
 * Washington, D.C.
 */

package edu.brook.cv.paper;

import java.awt.Color;

import org.ascape.model.event.ScapeEvent;


public class CVModelIRun5 extends CVModelIRun3and4and5 {

    public void scapeSetup(ScapeEvent scapeEvent) {
        super.scapeSetup(scapeEvent);
        setLegitimacy(0.80);
        setTargetCopDensity(.06);
    }

    public void scapeIterated(ScapeEvent event) {
        super.scapeIterated(event);
        if (getTargetCopDensity() - 0.001 > 0.0) {
            setTargetCopDensity(getTargetCopDensity() - 0.001);
        } else {
            setTargetCopDensity(0.0);
        }
        chart.removeSeries("Sum Legitimacy");
        chart.addSeries("Sum Cop Density", Color.gray);
    }
}
