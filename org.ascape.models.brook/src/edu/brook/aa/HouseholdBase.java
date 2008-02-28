/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.aa;

import java.util.Enumeration;
import java.util.Vector;

import org.ascape.model.Scape;
import org.ascape.util.Conditional;
import org.ascape.util.data.DataPoint;
import org.ascape.util.data.StatCollector;
import org.ascape.util.data.StatCollectorCSAMM;
import org.ascape.util.data.StatCollectorCond;

public abstract class HouseholdBase extends Scape {

    /**
     * 
     */
    private static final long serialVersionUID = 4016766647753095651L;

    public Conditional find_settlement_rule = new Conditional() {
        /**
         * 
         */
        private static final long serialVersionUID = -1952585657699460589L;

        public boolean meetsCondition(Object o) {
            return ((((Location) o).isOnMap()) && (((Location) o).getFarm() == null) && ((((Location) o).getClan() == null) || (((Location) o).getClan() == clan)) && (((Location) o).hasWithin(Location.LOW_EROSION, true, 1.0)));
        }
    };

    static int maximumFarmlandsSearched = 100;

    Vector farms;

    Settlement settlement;

    int lastHarvest;

    int fertilityAge;

    int deathAge;

    int fertilityEndsAge;

    double fertility;

    //The last item in cornstocks is unusable, except as a holder for
    //use when aging a years cornstocks.
    int[] agedCornStocks;

    private Clan clan;

    public void initialize() {
        super.initialize();
        setMembersActive(false);
        agedCornStocks = new int[((LHV) getRoot()).getYearsOfStock() + 1];
        for (int i = 0; i < agedCornStocks.length - 1; i++) {
            agedCornStocks[i] = randomInRange(((LHV) getRoot()).getHouseholdMinInitialCorn(), ((LHV) getRoot()).getHouseholdMaxInitialCorn());
        }
        lastHarvest = 0;
        //Change mtp 8/18/00
        //farm = new Farm();
        farms = new Vector();
        settlement = null;
        clan = null;

        //Support for hetergeneous fertility, etc..
        //added 6/7/2000 mtp
        fertilityAge = randomInRange(((LHV) getRoot()).getMinFertilityAge(), ((LHV) getRoot()).getMaxFertilityAge());
        deathAge = randomInRange(((LHV) getRoot()).getMinDeathAge(), ((LHV) getRoot()).getMaxDeathAge());
        fertilityEndsAge = randomInRange(((LHV) getRoot()).getMinFertilityEndsAge(), ((LHV) getRoot()).getMaxFertilityEndsAge());
        fertility = randomInRange(((LHV) getRoot()).getMinFertility(), ((LHV) getRoot()).getMaxFertility());
    }

    protected Farm addFarm() {
        Farm farm = new Farm();
        farm.setHousehold(this);
        farms.addElement(farm);
        return farm;
    }

    public int getNutritionNeed() {
        return 0;
    }

    public int getTotalCornStocks() {
        int total = 0;
        for (int i = 0; i < ((LHV) getRoot()).getYearsOfStock(); i++) {
            total += agedCornStocks[i];
        }
        return total;
    }

    public int getEstimateNextYearCorn() {
        return getTotalCornStocks() + lastHarvest;
    }

    public void occupy(Location location) {
        if (location.getSettlement() == null) {
            location.createSettlement();
        }
        settlement = location.getSettlement();
        settlement.add(this, false);
    }

    public void leave() {
        leaveAllFarms();
        if (settlement != null) {
            settlement.remove(this);
            settlement = null;
        }
    }

    public void findFarmAndSettlement() {
        Vector farmsSearched = new Vector();
        int searchCount = 0;
        while (farms.size() == 0) {
            Location farmLocation = ((LHV) getRoot()).removeBestLocation();
            if (farmLocation != null) {
                //System.out.println(farmLocation.getClan() + " " + clan);
                //if (farmLocation.getBaseYield() >= ((LHV) getRoot()).getHouseholdMinNutritionNeed()) {
                if (farmLocation.getBaseYield() > 0) {
                    //if (true) {
                    /*if (farmLocation.countNeighbors(new Conditional() {
                    	public boolean meetsCondition(Object o) {
                    		//return ((((Location) o).getFarm() != null) && (((Location) o).getClan() != HouseholdBase.this.getClan()));
                    		return (((Location) o).getFarm() != null);
                    	}
                    }) == 0) {*/
                    if ((farmLocation.getClan() == null) || (farmLocation.getClan() == clan)) {
                        Location nearestWater = (Location) farmLocation.findNearest(Location.HAS_WATER, true, ((LHV) getRoot()).getWaterSourceDistance());
                        if (nearestWater != null) {
                            //System.out.println(farmLocation.getYieldZone().getName());
                            if (settlement != null) {
                                settlement.remove(this);
                                settlement = null;
                            }
                            /*if ((farmLocation.getClan() != null) && (farmLocation.getClan() == clan)) {
                                System.out.println("Occupy old clan land");
                            }*/
                            addFarm().occupy(farmLocation);
                            Location nearestSettlementSite = (Location) nearestWater.findNearest(find_settlement_rule, true, Double.MAX_VALUE);
                            //Location nearestSettlementSite = (Location) nearestWater.findNearest(Location.KAYENTA_1_SETTLEMENT, true);
                            if (nearestSettlementSite == null) {
                                throw new RuntimeException("Unexpected model condition in household");
                            }
                            occupy(nearestSettlementSite);
                            if (!findFarmsForNutritionalNeed()) {
                                leave();
                                farmsSearched.addElement(farmLocation);
                                break;
                            }
                        } else {
                            //No nearby water location found...
                            farmsSearched.addElement(farmLocation);
                            //((LHV) getRoot()).farmsSearchedThisYear.addElement(farmLocation);
                        }
                        /*}
                        else {
                            //No nearby water location found...
                            farmsSearched.addElement(farmLocation);
                            //((LHV) getRoot()).farmsSearchedThisYear.addElement(farmLocation);
                        }*/
                    } else {
                        //No nearby water location found...
                        farmsSearched.addElement(farmLocation);
                        //((LHV) getRoot()).farmsSearchedThisYear.addElement(farmLocation);
                    }
                } else {
                    //Yield < need, so give up search...
                    farmsSearched.addElement(farmLocation);
                    //((LHV) getRoot()).farmsSearchedThisYear.addElement(farmLocation);
                    //((LHV) getRoot()).farmSitesAvailable = false;
                    break;
                }
            } else {
                //Null farmLocation, so no valley locations with any yield are left...
                //((LHV) getRoot()).farmSitesAvailable = false;
                return;
            }
            searchCount++;
        }
        Enumeration e = farmsSearched.elements();
        while (e.hasMoreElements()) {
            Location unusedLocation = (Location) e.nextElement();
            unusedLocation.makeAvailable();
        }
    }

    public final static DataPoint BEST_FARM = new DataPoint() {
        public String getName() {
            return "Best Farm";
        }

        public double getValue(Object object) {
            if (((Location) object).isAvailable()) {
                return ((Location) object).getBaseYield();
            } else {
                return 0.0;
            }
        }
    };

    public void leaveAllFarms() {
        Enumeration e = farms.elements();
        while (e.hasMoreElements()) {
            ((Farm) e.nextElement()).leave();
        }
        farms.removeAllElements();
    }

    public boolean findFarmsForNutritionalNeed() {
        int need = getNutritionNeed();
        int adultCount = 0;
        int adults = getNumAdults();
        int currentEstimate = 0;
        Enumeration e = farms.elements();
        while (e.hasMoreElements()) {
            currentEstimate += ((Farm) e.nextElement()).getLocation().getBaseYield();
            adultCount++;
        }
        while (true) {
            //System.out.println(adults+" "+adultCount+", "+getSize());
            if (currentEstimate >= need) {
                return true;
            }
            adultCount++;
            if (adultCount > adults) {
                return false;
            }
            Location best = (Location) settlement.getLocation().findMaximumWithin(BEST_FARM, false, (int) ((LHV) getRoot()).getWaterSourceDistance());
            if ((best != null) && (best.isAvailable()) && (best.getBaseYield() > 0.0)) {
                addFarm().occupy(best);
                currentEstimate += best.getBaseYield();
                //System.out.println(adults+" "+adultCount+", "+getSize()+"-"+currentEstimate);
            } else {
                return false;
            }
        }
        //if (farmLocation.getBaseYield() >= ((LHV) getRoot()).getHouseholdMinNutritionNeed()) {
    }

    public void giveMaizeGift(HouseholdBase recipient) {
        for (int i = 0; i < agedCornStocks.length - 1; i++) {
            recipient.agedCornStocks[i] = (int) (agedCornStocks[i] * ((LHV) getRoot()).getMaizeGiftToChild());
            agedCornStocks[i] = (int) (agedCornStocks[i] * (1.0 - ((LHV) getRoot()).getMaizeGiftToChild()));
        }
    }

    /**
     * Consume the given quantity of corn.
     * If the quantity available is greater than or equal to the quantity needed, removes from stores the quantity requested, and returns 0.
     * If the quantity avialable is less than the quantity needed, removes from stores all reminaing food, and returns the amount of need not met.
     * @param amount the quantity of food needed
     */
    public int consumeCorn(int amount) {
        //Loop down from 1 greater than the size of current year's stock
        for (int i = ((LHV) getRoot()).getYearsOfStock(); i >= 0; i--) {
            if (agedCornStocks[i] >= amount) {
                agedCornStocks[i] -= amount;
                return 0;
            } else {
                amount -= agedCornStocks[i];
                agedCornStocks[i] = 0;
            }
        }
        //Not all need was meet
        return amount;
    }

    public void metabolism() {
        lastHarvest = 0;
        int adultCount = 0;
        int adults = getNumAdults();
        Enumeration e = farms.elements();
        while (e.hasMoreElements()) {
            lastHarvest += ((Farm) e.nextElement()).getLocation().findRandomYield();
            adultCount++;
            if (adultCount >= adults) {
                break;
            }
        }
        //lastHarvest = (int) ((Location) farm.getLocation()).findRandomYield();
        for (int i = ((LHV) getRoot()).getYearsOfStock() - 1; i >= 0; i--) {
            agedCornStocks[i + 1] = agedCornStocks[i];
        }
        agedCornStocks[0] = lastHarvest;
    }

    public boolean movementCondition() {
        //System.out.println(getSize()+" "+getNutritionNeed()+", "+" f "+farms.size()+" "+getEstimateNextYearCorn());
        if (getEstimateNextYearCorn() < getNutritionNeed()) {
            if (!findFarmsForNutritionalNeed()) {
                scape.getData().getStatCollector("Movements").addValue(0.0);
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public void move() {
        leave();
        //leaveAllFarms();
        findFarmAndSettlement();
        if ((farms.size() == 0) || (settlement == null)) {
            //System.out.println("No settlements available");
            die();
            scape.getData().getStatCollector("Departures").addValue(0.0);
        }
    }

    public boolean alreadyDead = false;

    public void die() {
        if (!scape.remove(this)) {
            throw new RuntimeException("Tried to kill an agent not a memeber of its Scape.");
        }
        leave();
    }

    class SettlementZoneStatCollector extends StatCollectorCond {

        /**
         * 
         */
        private static final long serialVersionUID = 5283713940057557749L;
        EnvironmentZone zone;

        public boolean meetsCondition(Object o) {
            Settlement s = ((HouseholdBase) o).getSettlement();
            if (s != null) {
                return (s.getLocation().getEnvironmentZone() == zone);
            } else {
                return false;
            }
        }

        public String getName() {
            return "Households in " + zone.getName();
        }
    }

    class FarmZoneStatCollector extends StatCollectorCond {

        /**
         * 
         */
        private static final long serialVersionUID = 6863625481169667385L;
        EnvironmentZone zone;

        public boolean meetsCondition(Object o) {
            /*Location f = ((HouseholdBase) o).getFarm().getLocation();
            if (f != null) {
                return (f.getEnvironmentZone() == zone);
            }
            else {
                return false;
            }*/
            //Fix
            return false;
        }

        public String getName() {
            return "Farms in " + zone.getName();
        }
    }

    public void scapeCreated() {
        scape.addInitialRule(FORCE_MOVE_RULE);
        StatCollector[] stats = new StatCollector[5];
        stats[0] = new StatCollector("Households");
        stats[1] = new StatCollector("Movements", false);
        stats[2] = new StatCollector("Fissions", false);
        stats[3] = new StatCollector("Departures", false);
        stats[4] = new StatCollectorCSAMM("Farms Per Household") {
            /**
             * 
             */
            private static final long serialVersionUID = -3060585396202230071L;

            public double getValue(Object o) {
                return ((HouseholdBase) o).farms.size();
            }
        };
        scape.addStatCollectors(stats);
        for (int i = 0; i < ((LHV) scape.getRoot()).getEnvironmentZones().getSize(); i++) {
            SettlementZoneStatCollector settlementStatCollector = new SettlementZoneStatCollector();
            settlementStatCollector.zone = (EnvironmentZone) ((LHV) scape.getRoot()).getEnvironmentZones().get(i);
            scape.addStatCollector(settlementStatCollector);
            FarmZoneStatCollector farmStatCollector = new FarmZoneStatCollector();
            farmStatCollector.zone = (EnvironmentZone) ((LHV) scape.getRoot()).getEnvironmentZones().get(i);
            scape.addStatCollector(farmStatCollector);
        }
    }

    public Settlement getSettlement() {
        return settlement;
    }

    public Clan getClan() {
        return clan;
    }

    public void setClan(Clan clan) {
        this.clan = clan;
    }

    /*public Farm getFarm() {
        return farm;
    }*/

    public abstract int getNumAdults();

    /**
     * Clones the host cell, making occupant and neighbors null.
     */
    public Object clone() {
        HouseholdBase clone = (HouseholdBase) super.clone();
        //clone.valley = this.valley;
        clone.farms = new Vector();
        clone.settlement = null;
        //System.out.println(scape);
        return clone;
    }

    public String toInnerString() {
        return "household size " + getSize() + (getClan() != null ? getClan().toString() : "");
    }

    /**
     * Returns a description of the person including age, sex and household.
     */
    public String toString() {
        return "A " + toInnerString();
    }
}
