/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.aa;

import org.ascape.model.Cell;

public class Farm extends Cell {

    /**
     * 
     */
    private static final long serialVersionUID = 7663675971484207117L;

    public HouseholdBase household;

    public Location location;

    public HouseholdBase getHousehold() {
        return household;
    }

    public void setHousehold(HouseholdBase household) {
        this.household = household;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void leave() {
        if (location != null) {
            //System.out.println(">   ");
            location.setFarm(null);
            location = null;
        }
    }

    public void occupy(Location location) {
        if (this.location == null) {
            location.setFarm(this);
            this.location = location;
        } else {
            throw new RuntimeException("Farm must leave previous location before occuping new one.");
        }
    }
}
