/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
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
