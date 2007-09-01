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
 *
 * Boids Model
 *
 * June - July 2002
 *
 * An implementation of Craig Reynold's Boids model in Ascape.
 *             Copyright (c) 2002-2004-2004
 *                NuTech Solutions,Inc.
 *
 */


package org.ascape.examples.boids;

import java.awt.Color;

import org.ascape.model.MomentumAgent;
import org.ascape.util.Conditional;


public class Obstacle extends MomentumAgent {

    private Conditional boid;

    public void initialize() {
        super.initialize();
        boid = new Conditional() {
            public boolean meetsCondition(Object o) {
                return o instanceof Boid;
            }
        };
    }

    public Color getColor() {
        return Color.black;
    }

    public int getNumBoidsAround() {
        return findWithin(boid, 20f).size();
    }

    public String getName() {
        return "Obstacle at " + getCoordinate();
    }
}
