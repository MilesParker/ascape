/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.aa;


public class PersonClan extends Person {

    /**
     * 
     */
    private static final long serialVersionUID = -2839096845066088358L;
    public ClanFindMateRule findMate = new ClanFindMateRule(this);

    public void formHousehold() {
        findMate.clear();
        scape.executeOnMembers(findMate);
        if (findMate.getMate() != null) {
            HouseholdDisaggregate oldHousehold = household;
            mate = findMate.getMate();
            mate.setMate(this);
            //System.out.println("A " + this.toInnerString() + " marries a " + mate.toInnerString());
            scape.getData().getStatCollector("Households Formed").addValue(0.0);
            HouseholdDisaggregate newHousehold = new HouseholdDisaggregate();
            ((LHVDisaggregate) getRoot()).getHouseholds().add(newHousehold);
            newHousehold.initialize();
            //System.out.println(oldHousehold.getClan());
            newHousehold.setClan(oldHousehold.getClan());
            mate.setHousehold(newHousehold);
            this.setHousehold(newHousehold);
            oldHousehold.giveMaizeGift(newHousehold);
            newHousehold.move();
        }
    }
}
