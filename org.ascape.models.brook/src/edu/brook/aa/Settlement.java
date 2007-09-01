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
 * mparker@brook.edu
 * http://www.brook.edu/es/dynamics/models/ascape/
 * The Brookings Institution
 * Washington, D.C.
 */

package edu.brook.aa;

import org.ascape.model.Agent;
import org.ascape.model.Scape;
import org.ascape.util.data.StatCollector;
import org.ascape.util.data.StatCollectorCond;


public class Settlement extends Scape {

    public Location location;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void moveTo(Location location) {
        location.setSettlement(this);
        this.location = location;
    }

    public String toString() {
        return "Settlement " + coordinate;
    }

    public void leave() {
        if (location != null) {
            getScape().remove(this);
            location.setSettlement(null);
            location = null;
        }
    }

    /*public void add(Agent agent) {
    	vector.addElement(agent);
    	extent = new Coordinate1DDiscrete(vector.size());
    }*/

    public boolean remove(Agent agent) {
        boolean superRemoveSuccess = super.remove(agent);
        if (superRemoveSuccess) {
            if (this.getSize() == 0) {
                leave();
            }
        }
        return superRemoveSuccess;
    }

    public void scapeCreated() {
        scape.getRules().clear();

        StatCollector[] stats = new StatCollector[7];
        stats[0] = new StatCollectorCond("Size 1") {
            public double getValue(Object o) {
                return ((Settlement) o).getSize();
            }

            public boolean meetsCondition(Object o) {
                return ((Settlement) o).getSize() == 1;
            }
        };
        stats[1] = new StatCollectorCond("Size 2 to 3") {
            public double getValue(Object o) {
                return ((Settlement) o).getSize();
            }

            public boolean meetsCondition(Object o) {
                return ((((Settlement) o).getSize() >= 2) && (((Settlement) o).getSize() <= 3));
            }
        };
        stats[2] = new StatCollectorCond("Size 4 to 9") {
            public double getValue(Object o) {
                return ((Settlement) o).getSize();
            }

            public boolean meetsCondition(Object o) {
                return ((((Settlement) o).getSize() >= 4) && (((Settlement) o).getSize() <= 9));
            }
        };
        stats[3] = new StatCollectorCond("Size 10 to 19") {
            public double getValue(Object o) {
                return ((Settlement) o).getSize();
            }

            public boolean meetsCondition(Object o) {
                return ((((Settlement) o).getSize() >= 10) && (((Settlement) o).getSize() <= 19));
            }
        };
        stats[4] = new StatCollectorCond("Size 20 to 39") {
            public double getValue(Object o) {
                return ((Settlement) o).getSize();
            }

            public boolean meetsCondition(Object o) {
                return ((((Settlement) o).getSize() >= 20) && (((Settlement) o).getSize() <= 39));
            }
        };
        stats[5] = new StatCollectorCond("Size 40 to 79") {
            public double getValue(Object o) {
                return ((Settlement) o).getSize();
            }

            public boolean meetsCondition(Object o) {
                return ((((Settlement) o).getSize() >= 40) && (((Settlement) o).getSize() <= 79));
            }
        };
        stats[6] = new StatCollectorCond("Size 80+") {
            public double getValue(Object o) {
                return ((Settlement) o).getSize();
            }

            public boolean meetsCondition(Object o) {
                return (((Settlement) o).getSize() >= 80);
            }
        };
        scape.addStatCollectors(stats);
    }

    public void occupy(Location location) {
        location.setSettlement(this);
        this.location = location;
    }
}
