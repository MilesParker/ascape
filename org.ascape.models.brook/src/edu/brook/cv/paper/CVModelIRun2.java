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


public class CVModelIRun2 extends CVModelI {

    public void createScape() {
        super.createScape();
        //Rnadom seed used for working paper...
        //setRandomSeed(955396277049L);
        copVision = 7.0;
        personVision = 7.0;
        legitimacy = .82;
        jailTerm = 30;
        initialCopDensity = .04;
        targetCopDensity = initialCopDensity;
    }
}
