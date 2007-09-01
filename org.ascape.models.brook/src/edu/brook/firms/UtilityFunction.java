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
 * The Brookings Institution
 * Washington, D.C.
 * mparker@brook.edu
 * (202) 797-6136
 *
 * Please contact us for permission to use, copy, modify, or distribute this software.
 */

package edu.brook.firms;

import org.ascape.util.Function;

public class UtilityFunction extends Function {

    /**
     * Preference for income (inversely, for leisure)
     **/
    protected double theta;

    //Economy of scale factor. (Firm productivity per employee improvement as a function of firm size.)
    private static double sigma = 1;

    //Exponent for economy of scale factor in firm output equation.
    private static int beta = 2;

    protected double baseSize;

    protected double baseEffort;

    /**
     * Output as a function of input (effort.)
     **/
    public static double output(double e) {
        return e + (sigma * Math.pow(e, beta));
    }

    public double solveFor(double e) {
        return Math.pow((output(baseEffort + e) / (baseSize + 1)), theta)
            * Math.pow(1 - e, 1 - theta);
    }
}
