/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.aa;



public class HouseholdGreedy extends HouseholdAggregate {

    /**
     * 
     */
    private static final long serialVersionUID = 8865761711867701509L;
    public Farm[] bufferFarms = new Farm[0];

    public void fission() {
        HouseholdAggregate child = new HouseholdGreedy();//(Household) this.clone();
        scape.add(child);
        child.initialize();
        child.setAge(0);
        giveMaizeGift(child);
        child.move();
        //if ((child.farm.getLocation() != null) && (child.settlement != null)) {
        //For now, record fissions regardless of successful move to match C++ code
        scape.getData().getStatCollector("Fissions").addValue(0.0);
        //}
        //System.out.println(child.age);
    }

    public void leave() {
        super.leave();
        for (int i = 0; i < bufferFarms.length; i++) {
            bufferFarms[i].leave();
        }
    }

    public void move() {
        super.move();
        /*if ((farm.getLocation() != null) && (settlement != null)) {
        	Vector locations = farm.getLocation().getNeighbors(new Conditional() {
			    public boolean meetsCondition(Object object) {
			    	return ((((Location) object).getFarm() == null) && (((Location) object).getSettlement() == null));
			    }
        	});
        	bufferFarms = new Farm[locations.size()];
        	bufferFarms = new Farm[locations.size()];
        	for (int i = 0; i < bufferFarms.length; i++) {
        		bufferFarms[i] = new Farm();
                bufferFarms[i].occupy(((Location) locations.elementAt(i)));
        	}
        }*/
    }
}
