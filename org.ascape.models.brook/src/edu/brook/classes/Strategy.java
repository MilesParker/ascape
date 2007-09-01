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
 * _Emergence of classes_
 * See _The Emergence of Economic Classes in an Agent-Based Bargaining Model_
 * Joshua Epstein
 * jepstein@brook.edu
 * Robert Axtell
 * raxtell@brook.edu
 * Peyton Young
 * pyoung@brook.edu
 *
 * Implementation:
 * Miles Parker
 * mparker@brook.edu
 * The Brookings Institution
 * Washington, D.C.
 */

package edu.brook.classes;

import java.util.Random;

import org.ascape.util.Utility;


public abstract class Strategy {

    public abstract float getDemand();

    public final static Strategy LOW_STRATEGY = new Strategy() {
        public final float getDemand() {
            return BargainingModelBase.getLowPayoffStatic();
        }
    };

    public final static Strategy MEDIUM_STRATEGY = new Strategy() {
        public final float getDemand() {
            return BargainingModelBase.getMediumPayoffStatic();
        }
    };

    public final static Strategy HIGH_STRATEGY = new Strategy() {
        public final float getDemand() {
            return BargainingModelBase.getHighPayoffStatic();
        }
    };

    public static Strategy randomStrategy(Random random) {
        int strategy = Utility.randomToLimit(random, 3);
        switch (strategy) {
            case 0:
                return LOW_STRATEGY;
            case 1:
                return MEDIUM_STRATEGY;
            case 2:
                return HIGH_STRATEGY;
            default:
                //Should never get here, but to satisfy compiler..
                throw new RuntimeException("Unexpected internal error in Bargainer.");
        }
    }
}
