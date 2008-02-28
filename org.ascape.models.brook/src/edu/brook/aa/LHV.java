/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.aa;

//Temporary for JDK 1.1 compatibility
//import com.sun.java.util.collections.*;

import java.awt.Color;
import java.awt.Graphics;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ascape.model.Agent;
import org.ascape.model.HistoryValueSetter;
import org.ascape.model.Scape;
import org.ascape.model.event.ScapeEvent;
import org.ascape.model.rule.CollectStats;
import org.ascape.model.rule.Rule;
import org.ascape.model.rule.SetValues;
import org.ascape.model.space.Array2D;
import org.ascape.model.space.Array2DMoore;
import org.ascape.model.space.Coordinate1DDiscrete;
import org.ascape.model.space.Coordinate2DDiscrete;
import org.ascape.model.space.SpatialTemporalException;
import org.ascape.util.data.StatCollectorCondCSA;
import org.ascape.util.data.UnitIntervalDataPoint;
import org.ascape.util.vis.ColorFeature;
import org.ascape.util.vis.ColorFeatureConcrete;
import org.ascape.util.vis.ColorFeatureGradiated;
import org.ascape.util.vis.DrawFeature;
import org.ascape.util.vis.DrawSymbol;
import org.ascape.util.vis.FillCellFeature;
import org.ascape.view.vis.ChartView;
import org.ascape.view.vis.Overhead2DView;

class GeneralValleyStreamSource extends WaterSource {

    /**
     * 
     */
    private static final long serialVersionUID = -3153973451588490393L;

    public boolean isExtant() {
        if (LHV.isStreamsExist(getScape().getPeriod())) {
            return true;
        } else {
            return false;
        }
    }
}

public class LHV extends Scape {

    /**
     * 
     */
    private static final long serialVersionUID = -1892876266881188560L;

    public static EnvironmentZone ENVIRON_EMPTY = new EnvironmentZone("Empty", Color.white);

    public static EnvironmentZone ENVIRON_GENERAL_VALLEY = new EnvironmentZone("General Valley Floor", Color.black);

    public static EnvironmentZone ENVIRON_NORTH_VALLEY = new EnvironmentZone("North Valley Floor", Color.red);

    public static EnvironmentZone ENVIRON_MID_VALLEY = new EnvironmentZone("Mid-Valley Floor", Color.green);

    public static EnvironmentZone ENVIRON_UPLANDS_NATURAL = new EnvironmentZone("Uplands Natural", Color.yellow);

    public static EnvironmentZone ENVIRON_UPLANDS_ARABLE = new EnvironmentZone("Uplands Arable", Color.blue);

    public static EnvironmentZone ENVIRON_KINBIKO_CANYON = new EnvironmentZone("Kinbiko Canyon", Color.pink);

    public static MaizeZone MAIZE_EMPTY = new MaizeZone("Empty", Color.white);

    public static MaizeZone MAIZE_NO_YIELD = new MaizeZone("No Yield", Color.white);

    public static MaizeZone MAIZE_YIELD_1 = new MaizeZone("Yield 1", Color.green);

    public static MaizeZone MAIZE_YIELD_2 = new MaizeZone("Yield 2", Color.blue);

    public static MaizeZone MAIZE_YIELD_3 = new MaizeZone("Yield 3", Color.gray);

    public static MaizeZone MAIZE_SAND_DUNE = new MaizeZone("Sand Dune", Color.yellow);

    public static YieldZone YIELD_EMPTY = new YieldZone("Empty", Color.white, ENVIRON_EMPTY, MAIZE_EMPTY);

    public static YieldZone YIELD_GENERAL_VALLEY = new YieldZone("General Valley Floor", Color.black, ENVIRON_GENERAL_VALLEY, MAIZE_YIELD_2);

    public static YieldZone YIELD_NORTH_SAND_DUNE = new YieldZone("North Valley Dunes", Color.white, ENVIRON_NORTH_VALLEY, MAIZE_SAND_DUNE);

    public static YieldZone YIELD_NORTH_VALLEY = new YieldZone("North Valley Floor", Color.red, ENVIRON_NORTH_VALLEY, MAIZE_YIELD_1);

    public static YieldZone YIELD_MID_SAND_DUNE = new YieldZone("Mid Valley Dunes", Color.white, ENVIRON_MID_VALLEY, MAIZE_SAND_DUNE);

    public static YieldZone YIELD_MID_VALLEY_WEST = new YieldZone("West Mid-Valley Floor", Color.gray, ENVIRON_MID_VALLEY, MAIZE_YIELD_1);

    public static YieldZone YIELD_MID_VALLEY_EAST = new YieldZone("East Mid-Valley Floor", Color.green, ENVIRON_MID_VALLEY, MAIZE_YIELD_2);

    public static YieldZone YIELD_UPLANDS_NATURAL = new YieldZone("Uplands Natural", Color.yellow, ENVIRON_UPLANDS_NATURAL, MAIZE_NO_YIELD);

    public static YieldZone YIELD_UPLANDS_ARABLE = new YieldZone("Uplands Arable", Color.blue, ENVIRON_UPLANDS_ARABLE, MAIZE_YIELD_3);

    public static YieldZone YIELD_KINBIKO_CANYON = new YieldZone("Kinbiko Canyon", Color.pink, ENVIRON_KINBIKO_CANYON, MAIZE_YIELD_1);

    protected Scape valley;

    protected Scape households;

    protected Scape settlements;

    protected Scape farms;

    protected Scape historicSettlements;

    protected Scape waterSources;

    protected LocationRanking locationRankings;

    protected Scape environmentZones;

    protected Scape maizeZones;

    protected Scape yieldZones;

    protected Overhead2DView view;

    protected static int EARLIEST_YEAR = 200;

    protected static int LATEST_YEAR = 1499;

    protected static int PDSI_EARLIEST_YEAR = 382;

    protected static int MAXIMUM_YEARS = LHV.LATEST_YEAR - LHV.EARLIEST_YEAR;

    protected double harvestAdjustment = 1.0;

    protected double harvestVarianceYear = 0.1;

    protected double harvestVarianceLocation = 0.1;

    /**
     * The size an aggregate (household not individual level) household is assumed to be.
     */
    protected int typicalHouseholdSize = 5;

    /**
     * The amount of food assumed to be needed for one average adult.
     */
    protected int baseNutritionNeed = 160;

    protected int householdMinNutritionNeed = baseNutritionNeed * typicalHouseholdSize;

    protected int householdMaxNutritionNeed = baseNutritionNeed * typicalHouseholdSize;

    protected int minFertilityAge = 16;//16

    protected int maxFertilityAge = 16;//16

    protected int minDeathAge = 30;//30

    protected int maxDeathAge = 30;//30

    //For Rob's experiment
    protected int minFertilityEndsAge = 30;

    protected int maxFertilityEndsAge = 30;

    protected double minFertility = 0.125;//0.125

    protected double maxFertility = 0.125;//0.125

    protected double maizeGiftToChild = .33;

    protected double waterSourceDistance = 16.0;

    protected int yearsOfStock = 2;

    protected int householdMinInitialAge = 0;

    protected int householdMaxInitialAge = 29;

    protected int householdMinInitialCorn = 2000;

    protected int householdMaxInitialCorn = 2400;

    class FillValleyCellFeature extends FillCellFeature {

        /**
         * 
         */
        private static final long serialVersionUID = 3823585979723128174L;

        public FillValleyCellFeature(String name, ColorFeature colorFeature) {
            super(name, colorFeature);
        }

        public void draw(Graphics g, Object object, int width, int height) {
            if (((Location) object).getEnvironmentZone() != ENVIRON_EMPTY) {
                g.setColor(getColor(object));
                g.fillRect(0, 0, width, height);
            } else {
                g.setColor(Color.lightGray);
                g.fillRect(0, 0, width, height);
            }
        }
    }

    public void createScape() {
        setAutoRestart(true);
        setPrototypeAgent(new Scape());
        //setAutoCreate(false);

        getRules().clear();
        //setRandomSeed(129);

        /*
         * Create valley
         */
        valley = new Scape(new Array2DMoore());
        valley.setPrototypeAgent(new Location());
        valley.setExtent(new Coordinate2DDiscrete(80, 120));
        valley.setAutoCreate(false);
        valley.getRules().clear();
        /*StatCollector[] stats = new StatCollector[1];
        stats[0] = new StatCollectorCondCSAMM("Potential Yield") {
        	public boolean meetsCondition(Object o) {
        		return (((Location) o).getBaseYield() > 0.0);
        	}
        	public double getValue(Object o) {
        		return ((Location) o).getBaseYield();
        	}
        };
        valley.addStatCollectors(stats);*/

        setEarliestPeriod(EARLIEST_YEAR);
        setLatestPeriod(LATEST_YEAR);
        setPeriodName("Year");

        /*
         * Create Environment Zones
         */
        environmentZones = new Scape();
        environmentZones.setName("Environment Zones");
        add(environmentZones);
        environmentZones.setPrototypeAgent(ENVIRON_EMPTY);
        environmentZones.add(ENVIRON_GENERAL_VALLEY);
        environmentZones.add(ENVIRON_NORTH_VALLEY);
        environmentZones.add(ENVIRON_MID_VALLEY);
        environmentZones.add(ENVIRON_UPLANDS_NATURAL);
        environmentZones.add(ENVIRON_UPLANDS_ARABLE);
        environmentZones.add(ENVIRON_KINBIKO_CANYON);
        environmentZones.add(ENVIRON_EMPTY);
        environmentZones.setAutoCreate(false);

        importEnvironmentalHistory();

        /*
         * Create Maize Zones
         */
        maizeZones = new Scape();
        maizeZones.setName("Maize Zones");
        //No need to add maize environmentZones to root, as no calculations needed
        maizeZones.setPrototypeAgent(MAIZE_EMPTY);
        maizeZones.add(MAIZE_YIELD_1);
        maizeZones.add(MAIZE_YIELD_2);
        maizeZones.add(MAIZE_YIELD_3);
        maizeZones.add(MAIZE_SAND_DUNE);
        maizeZones.add(MAIZE_NO_YIELD);
        maizeZones.add(MAIZE_EMPTY);
        maizeZones.setAutoCreate(false);

        /*
         * Create Yield Zones
         */
        yieldZones = new Scape();
        yieldZones.setName("Yield Zones");
        add(yieldZones);
        yieldZones.setPrototypeAgent(YIELD_EMPTY);
        yieldZones.add(YIELD_EMPTY);
        yieldZones.add(YIELD_GENERAL_VALLEY);
        yieldZones.add(YIELD_NORTH_SAND_DUNE);
        yieldZones.add(YIELD_NORTH_VALLEY);
        yieldZones.add(YIELD_MID_SAND_DUNE);
        yieldZones.add(YIELD_MID_VALLEY_WEST);
        yieldZones.add(YIELD_MID_VALLEY_EAST);
        yieldZones.add(YIELD_UPLANDS_NATURAL);
        yieldZones.add(YIELD_UPLANDS_ARABLE);
        yieldZones.add(YIELD_KINBIKO_CANYON);
        yieldZones.setAutoCreate(false);
        //We sort all at once to avoid sorting penalites per addition
        yieldZones.addInitialRule(new Rule("Sort Available Locations") {
            /**
             * 
             */
            private static final long serialVersionUID = 8923085455603538447L;

            public void execute(Agent agent) {
                Collections.sort(((YieldZone) agent).getAvailableLocations());
            }
        });

        valley.createScape();

        importMap();

        /*
         * Create Historic Settlements
         */
        historicSettlements = new Scape() {
            /**
             * 
             */
            private static final long serialVersionUID = 8710583027169504915L;

            public void initialize() {
                super.initialize();
                CollectStats collector = new CollectStats();
                StatCollectorCondCSA countHouseholds = new StatCollectorCondCSA() {
                    /**
                     * 
                     */
                    private static final long serialVersionUID = -8509781010737527704L;

                    public boolean meetsCondition(Object object) {
                        return ((HistoricSettlement) object).isExtant();
                    }

                    public double getValue(Object object) {
                        return ((HistoricSettlement) object).getHouseholdCount();
                    }
                };
                collector.addStatCollector(countHouseholds);
                historicSettlements.executeOnMembers(collector);
                LHV.this.households.setExtent(new Coordinate1DDiscrete((int) countHouseholds.getSum()));
            }
        };
        add(historicSettlements);
        historicSettlements.setPrototypeAgent(new HistoricSettlement());
        historicSettlements.setAutoCreate(false);
        historicSettlements.setName("Historic Settlements");
        historicSettlements.getRules().clear();
        importSettlementHistory();

        /*
         * Add valley to root
         */
        valley.setName("Long House Valley");
        add(valley);

        setAutoRestart(false);
        try {
            setStartPeriod(800);
            setStopPeriod(1350);
        } catch (SpatialTemporalException e) {
            System.out.println("Bad start/stop periods: " + e);
        }

        /*
         * Create Households
         */
        households = new Scape();
        households.setName("Households");
        add(households);
        HouseholdAggregate protoHousehold = new HouseholdAggregate();
        protoHousehold.setMembersActive(false);
        households.setPrototypeAgent(protoHousehold);

        /*
         * Create Simulation Settlements
         */
        settlements = new Scape() {
            /**
             * 
             */
            private static final long serialVersionUID = 2980206173636571476L;

            public void initialize() {
                //setExtent(new Coordinate1DDiscrete(0));
                super.initialize();
            }
        };
        settlements.setName("Settlements");
        Settlement protoSettlement = new Settlement();
        protoSettlement.setMembersActive(false);
        add(settlements);
        settlements.setPrototypeAgent(protoSettlement);

        /*
         * Create Farms
         */
        farms = new Scape();
        settlements.setName("Farms");
        farms.setPrototypeAgent(new Farm());

        createDrawFeatures();
    }

    /**
     * Import map and water sources from binary data files.
     */
    protected void importMap() {
        try {
            InputStream fs = this.getClass().getResourceAsStream("MapData/map.bin");
            DataInputStream ds = new DataInputStream(fs);

            for (int x = 0; x < 80; x++) {
                for (int y = 0; y < 120; y++) {
                    Location location = (Location) ((Array2D) valley.getSpace()).get(x, y);
                    location.streamToState(ds);
                }
            }

            ds.close();
            fs.close();

            waterSources = new Scape();
            waterSources.setPrototypeAgent(new WaterSource());
            waterSources.setAutoCreate(false);
            waterSources.setName("Water Sources");
            add(waterSources);
            waterSources.getRules().clear();
            InputStream fws = this.getClass().getResourceAsStream("MapData/water.bin");
            DataInputStream dws = new DataInputStream(fws);
            for (int i = 0; i < 108; i++) {
                //Not used
                WaterSource importedSource = new WaterSource();
                importedSource.streamToState(dws);
                Coordinate2DDiscrete locCoordinate = Location.getCoordinateFromMeters(importedSource.getMetersNorth(), importedSource.getMetersEast());
                if (((Array2D) valley.getSpace()).isValid(locCoordinate)) {
                    Location location = (Location) valley.get(locCoordinate);
                    location.setWaterSource(importedSource);
                    waterSources.add(importedSource);
                } else {
                    //System.out.println("Bad data; ignored.");
                }
            }
            int[][] streamLocations = {{72, 5}, {70, 6}, {69, 7}, {68, 8}, {67, 9}, {66, 10}, {65, 11}, {65, 12}};
            for (int i = 0; i < streamLocations.length; i++) {
                WaterSource streamSource = new GeneralValleyStreamSource();
                waterSources.add(streamSource);
                Location location = (Location) ((Array2D) valley.getSpace()).get(streamLocations[i][0], streamLocations[i][1]);
                location.setWaterSource(streamSource);
            }

            dws.close();
            fws.close();
        } catch (IOException e) {
            throw new RuntimeException("IO exception while importing data " + e);
        }
    }

    /**
     * Import environmental history from binary data files.
     */
    protected void importEnvironmentalHistory() {
        int zoneCount = 6;
        HistoryValueSetter[][] historyValues = new HistoryValueSetter[zoneCount][5];
        for (int zoneIndex = 0; zoneIndex < zoneCount; zoneIndex++) {
            SetValues valueSetter = new SetValues();
            historyValues[zoneIndex][0] = new HistoryValueSetter() {
                public String getName() {
                    return "Set Zone History Values";
                }

                public void setValue(Object object) {
                    ((EnvironmentZone) object).setPDSI(getValue(object));
                }
            };
            historyValues[zoneIndex][1] = new HistoryValueSetter() {
                public String getName() {
                    return "Hydrology";
                }

                public void setValue(Object object) {
                    ((EnvironmentZone) object).setHydrology(getValue(object));
                }
            };
            historyValues[zoneIndex][2] = new HistoryValueSetter() {
                public String getName() {
                    return "Soil Agredation";
                }

                public void setValue(Object object) {
                    ((EnvironmentZone) object).setAggredation(getValue(object));
                }
            };
            historyValues[zoneIndex][3] = new HistoryValueSetter() {
                public String getName() {
                    return "Adjusted PDSI";
                }

                public void setValue(Object object) {
                    ((EnvironmentZone) object).setAPDSI(getValue(object));
                }
            };
            historyValues[zoneIndex][4] = new HistoryValueSetter() {
                public String getName() {
                    return "Water Source";
                }

                public void setValue(Object object) {
                    ((EnvironmentZone) object).setIsWaterSource(getValue(object) == 1.0);
                }
            };
            valueSetter.addSetters(historyValues[zoneIndex]);
            ((Scape) environmentZones.get(zoneIndex)).addRule(valueSetter);
            ((Scape) environmentZones.get(zoneIndex)).addInitialRule(valueSetter);
        }
        try {
            DataInputStream envStream = new DataInputStream(this.getClass().getResourceAsStream("MapData/environment.bin"));
            DataInputStream apdsiStream = new DataInputStream(this.getClass().getResourceAsStream("MapData/adjustedPDSI.bin"));
            for (int zoneIndex = 0; zoneIndex < historyValues.length; zoneIndex++) {
                for (int k = 0; k < historyValues[0].length; k++) {
                    historyValues[zoneIndex][k].setPeriodRange(LHV.EARLIEST_YEAR, LHV.LATEST_YEAR);
                }
            }
            for (int period = LHV.EARLIEST_YEAR; period <= LHV.LATEST_YEAR; period++) {
                for (int zoneIndex = 0; zoneIndex < 5; zoneIndex++) {
                    if (period >= LHV.PDSI_EARLIEST_YEAR) {
                        historyValues[zoneIndex][0].setValueFor(period, (double) envStream.readFloat());
                    } else {
                        historyValues[zoneIndex][0].setValueFor(period, 0.0);
                    }
                    historyValues[zoneIndex][1].setValueFor(period, (double) envStream.readFloat());
                    historyValues[zoneIndex][2].setValueFor(period, (double) envStream.readFloat());
                    //Since water source data is expressed in years not iterations.
                    boolean isSource = isWaterSource((EnvironmentZone) environmentZones.get(zoneIndex), period);
                    historyValues[zoneIndex][4].setValueFor(period, isSource ? 1.0 : 0.0);
                }
                historyValues[5][0].setValueFor(period, historyValues[1][0].getValueFor(period));
                historyValues[5][1].setValueFor(period, historyValues[1][1].getValueFor(period));
                historyValues[5][2].setValueFor(period, historyValues[1][2].getValueFor(period));
                boolean isSource = isWaterSource((EnvironmentZone) environmentZones.get(5), period);
                historyValues[5][4].setValueFor(period, isSource ? 1.0 : 0.0);
                //testStream.writeBytes(historyValues[0][3].getValueFor(period) + "\t" + historyValues[1][3].getValueFor(period) + "\t" + historyValues[2][3].getValueFor(period) + "\t" + historyValues[3][3].getValueFor(period) + "\t" + historyValues[4][3].getValueFor(period) + "\t" + historyValues[5][3].getValueFor(period) + "\r\n");
            }
            //Don't know why the original data was encoded this way!
            for (int zoneIndex = 0; zoneIndex < 4; zoneIndex++) {
                for (int period = LHV.EARLIEST_YEAR; period <= LHV.LATEST_YEAR; period++) {
                    historyValues[zoneIndex][3].setValueFor(period, (double) apdsiStream.readFloat());
                }
            }
            for (int period = LHV.EARLIEST_YEAR; period <= LHV.LATEST_YEAR; period++) {
                historyValues[4][3].setValueFor(period, historyValues[3][3].getValueFor(period));
                historyValues[5][3].setValueFor(period, historyValues[1][3].getValueFor(period));
            }
        } catch (IOException e) {
            throw new RuntimeException("IO exception while importing environment " + e);
        }
    }

    /**
     * Import historical settlment data from binary data files.
     */
    protected void importSettlementHistory() {
        try {
            InputStream fss = this.getClass().getResourceAsStream("MapData/settlements.bin");
            DataInputStream dss = new DataInputStream(fss);
            //FileOutputStream fos = new FileOutputStream("D:/LHVData/SetTest.txt");
            //DataOutputStream dos = new DataOutputStream(fos);
            for (int i = 0; i < 488; i++) {
                //Not used
                HistoricSettlement settlement = new HistoricSettlement();
                settlement.streamToState(dss);
                Coordinate2DDiscrete locCoordinate = Location.getCoordinateFromMeters(settlement.getMetersNorth(), settlement.getMetersEast());
                //System.out.println(locCoordinate);
                //dos.writeBytes(locCoordinate.getXValue() + "\t" + locCoordinate.getYValue() + "\t" + settlement.getType() + "\t" + settlement.getStartYear() + "\t" + settlement.getMedianYear() + "\t" + settlement.getEndYear() + "\t" + settlement.getBaselineHouseholds() + "\r\n");
                if (settlement.getType() == 1) {
                    if (((Array2D) valley.getSpace()).isValid(locCoordinate)) {
                        Location location = (Location) valley.get(locCoordinate);
                        location.addHistoricSettlement(settlement);
                        historicSettlements.add(settlement);
                    } else {
                        //System.out.println("Bad data; ignored.");
                    }
                }
            }
            dss.close();
            fss.close();
        } catch (IOException e) {
            throw new RuntimeException("IO exception while importing data " + e);
        }
    }

    public void initialize() {
        farmSitesAvailable = true;
        //farmsSearchedThisYear = new Vector();
        super.initialize();
    }

    public double getHarvestAdjustment() {
        return harvestAdjustment;
    }

    public void setHarvestAdjustment(double harvestAdjustment) {
        this.harvestAdjustment = harvestAdjustment;
    }

    public double getHarvestVarianceYear() {
        return harvestVarianceYear;
    }

    public void setHarvestVarianceYear(double harvestVarianceYear) {
        this.harvestVarianceYear = harvestVarianceYear;
    }

    public double getHarvestVarianceLocation() {
        return harvestVarianceLocation;
    }

    public void setHarvestVarianceLocation(double harvestVarianceLocation) {
        this.harvestVarianceLocation = harvestVarianceLocation;
    }

    public int getBaseNutritionNeed() {
        return baseNutritionNeed;
    }

    public void setBaseNutritionNeed(int baseNutritionNeed) {
        this.baseNutritionNeed = baseNutritionNeed;
    }

    public int getHouseholdMinNutritionNeed() {
        return householdMinNutritionNeed;
    }

    public void setHouseholdMinNutritionNeed(int householdMinNutritionNeed) {
        this.householdMinNutritionNeed = householdMinNutritionNeed;
    }

    public int getHouseholdMaxNutritionNeed() {
        return householdMaxNutritionNeed;
    }

    public void setHouseholdMaxNutritionNeed(int householdMaxNutritionNeed) {
        this.householdMaxNutritionNeed = householdMaxNutritionNeed;
    }

    public int getMinFertilityAge() {
        return minFertilityAge;
    }

    public void setMinFertilityAge(int minFertilityAge) {
        this.minFertilityAge = minFertilityAge;
    }

    public int getMaxFertilityAge() {
        return maxFertilityAge;
    }

    public void setMaxFertilityAge(int maxFertilityAge) {
        this.maxFertilityAge = maxFertilityAge;
    }

    public int getMinFertilityEndsAge() {
        return minFertilityEndsAge;
    }

    public void setMinFertilityEndsAge(int minFertilityEndsAge) {
        this.minFertilityEndsAge = minFertilityEndsAge;
    }

    public int getMaxFertilityEndsAge() {
        return maxFertilityEndsAge;
    }

    public void setMaxFertilityEndsAge(int maxFertilityEndsAge) {
        this.maxFertilityEndsAge = maxFertilityEndsAge;
    }

    public int getMinDeathAge() {
        return minDeathAge;
    }

    public void setMinDeathAge(int minDeathAge) {
        this.minDeathAge = minDeathAge;
    }

    public int getMaxDeathAge() {
        return maxDeathAge;
    }

    public void setMaxDeathAge(int maxDeathAge) {
        this.maxDeathAge = maxDeathAge;
    }

    public double getMinFertility() {
        return minFertility;
    }

    public void setMinFertility(double minFertility) {
        this.minFertility = minFertility;
    }

    public double getMaxFertility() {
        return maxFertility;
    }

    public void setMaxFertility(double maxFertility) {
        this.maxFertility = maxFertility;
    }

    public double getMaizeGiftToChild() {
        return maizeGiftToChild;
    }

    public void setMaizeGiftToChild(double maizeGiftToChild) {
        this.maizeGiftToChild = maizeGiftToChild;
    }

    public double getWaterSourceDistance() {
        return waterSourceDistance;
    }

    public void setWaterSourceDistance(double waterSourceDistance) {
        this.waterSourceDistance = waterSourceDistance;
    }

    public int getYearsOfStock() {
        return yearsOfStock;
    }

    public void setYearsOfStock(int yearsOfStock) {
        this.yearsOfStock = yearsOfStock;
    }

    public int getHouseholdMinInitialAge() {
        return householdMinInitialAge;
    }

    public void setHouseholdMinInitialAge(int householdMinInitialAge) {
        this.householdMinInitialAge = householdMinInitialAge;
    }

    public int getHouseholdMaxInitialAge() {
        return householdMaxInitialAge;
    }

    public void setHouseholdMaxInitialAge(int householdMaxInitialAge) {
        this.householdMaxInitialAge = householdMaxInitialAge;
    }

    public int getHouseholdMinInitialCorn() {
        return householdMinInitialCorn;
    }

    public void setHouseholdMinInitialCorn(int householdMinInitialCorn) {
        this.householdMinInitialCorn = householdMinInitialCorn;
    }

    public int getHouseholdMaxInitialCorn() {
        return householdMaxInitialCorn;
    }

    public void setHouseholdMaxInitialCorn(int householdMaxInitialCorn) {
        this.householdMaxInitialCorn = householdMaxInitialCorn;
    }

    public Scape getHouseholds() {
        return households;
    }

    public Scape getSettlements() {
        return settlements;
    }

    public Scape getFarms() {
        return farms;
    }

    public Scape getEnvironmentZones() {
        return environmentZones;
    }

    public Scape getYieldZones() {
        return yieldZones;
    }

    public Scape getMaizeZones() {
        return maizeZones;
    }

    //Replace w/ comparison
    class FindBestLocation extends Rule {

        /**
         * 
         */
        private static final long serialVersionUID = 4269530128883979605L;

        public FindBestLocation() {
            super("Find Best Location");
        }

        private Location bestLocation;
        private List bestList;

        public void execute(Agent agent) {
            try {
                //Location currentLocation = (Location) ((YieldZone) agent).getAvailableLocations().getLast();
                Location currentLocation = null;
                ArrayList l = ((YieldZone) agent).getAvailableLocations();
                if (l.size() > 0) {
                    currentLocation = (Location) (l.get(l.size() - 1));
                }
                if ((currentLocation != null) && ((bestLocation == null) || (currentLocation.getBaseYield() > bestLocation.getBaseYield()))) {
                    bestLocation = currentLocation;
                    bestList = l;
                }
            } catch (java.util.NoSuchElementException e) {
            }
        }

        public Location getBestLocation() {
            return bestLocation;
        }

        public List getBestList() {
            return bestList;
        }

        public boolean isRandomExecution() {
            return false;
        }
    }

    public Location getBestLocation() {
        FindBestLocation finder = new FindBestLocation();
        yieldZones.executeOnMembers(finder);
        return finder.getBestLocation();
    }

    public Location removeBestLocation() {
        FindBestLocation finder = new FindBestLocation();
        yieldZones.executeOnMembers(finder);
        if ((finder.getBestLocation() != null) && (finder.getBestLocation().getBaseYield() > 0)) {
            //return (Location) finder.getBestList().removeLast();
            ArrayList l = (ArrayList) finder.getBestList();
            return (Location) l.remove(l.size() - 1);
        } else {
            return null;
        }
    }

    public static boolean isStreamsExist(int date) {
        return (((date >= 280) && (date < 360)) ||
            ((date >= 800) && (date < 930)) ||
            ((date >= 1300) && (date < 1450)));
    }

    public static boolean isAlluviumExists(int date) {
        return (((date >= 420) && (date < 560)) ||
            ((date >= 630) && (date < 680)) ||
            ((date >= 980) && (date < 1120)) ||
            ((date >= 1180) && (date < 1230)));
    }

    public static boolean isWaterSource(EnvironmentZone zone, int date) {
        if ((isAlluviumExists(date)) && ((zone == ENVIRON_GENERAL_VALLEY) || (zone == ENVIRON_NORTH_VALLEY) || (zone == ENVIRON_MID_VALLEY) || (zone == ENVIRON_KINBIKO_CANYON))) {
            return true;
        }
        if ((isStreamsExist(date)) && (zone == ENVIRON_KINBIKO_CANYON)) {
            return true;
        }
        return false;
    }

    //public Vector farmsSearchedThisYear = new Vector();

    public boolean farmSitesAvailable = true;

    public void scapeIterated(ScapeEvent event) {
        /*Enumeration e = farmsSearchedThisYear.elements();
        while(e.hasMoreElements()) {
            Location unusedLocation = (Location) e.nextElement();
            unusedLocation.getYieldZone().getAvailableLocations().add(unusedLocation);
        }
        farmsSearchedThisYear = new Vector();*/
        farmSitesAvailable = true;
        super.scapeIterated(event);
    }

    public void createDrawFeatures() {
        FillValleyCellFeature zoneFill =
            new FillValleyCellFeature("Environment Zone", new ColorFeatureConcrete() {
                /**
                 * 
                 */
                private static final long serialVersionUID = 8060365487620083420L;

                public Color getColor(Object o) {
                    return (((Location) o).getEnvironmentZone().getColor());
                }
            });
        valley.addDrawFeature(zoneFill);

        FillValleyCellFeature maizeZoneFill =
            new FillValleyCellFeature("Maize Zone", new ColorFeatureConcrete() {
                /**
                 * 
                 */
                private static final long serialVersionUID = -7825780654109831237L;

                public Color getColor(Object o) {
                    return (((Location) o).getMaizeZone().getColor());
                }
            });
        valley.addDrawFeature(maizeZoneFill);

        FillValleyCellFeature yieldZoneFill =
            new FillValleyCellFeature("Yield Zone", new ColorFeatureConcrete() {
                /**
                 * 
                 */
                private static final long serialVersionUID = -8182575802685007681L;

                public Color getColor(Object o) {
                    return (((Location) o).getYieldZone().getColor());
                }
            });
        valley.addDrawFeature(yieldZoneFill);

        FillValleyCellFeature hydroFill =
            new FillValleyCellFeature("Hydrology", new ColorFeatureGradiated(Color.blue, new UnitIntervalDataPoint() {
                /**
                 * 
                 */
                private static final long serialVersionUID = -2268057702246783384L;

                public double getValue(Object object) {
                    return ((((Location) object).getEnvironmentZone().getHydrology()) / 10.0);
                }
            }));
        valley.addDrawFeature(hydroFill);

        FillValleyCellFeature apdsiFill =
            new FillValleyCellFeature("APDSI", new ColorFeatureGradiated(Color.red, new UnitIntervalDataPoint() {
                /**
                 * 
                 */
                private static final long serialVersionUID = 7338600146527039554L;

                public double getValue(Object object) {
                    return ((((Location) object).getEnvironmentZone().getAPDSI()) / 10.0);
                }
            }));
        valley.addDrawFeature(apdsiFill);

        FillValleyCellFeature yieldFill =
            new FillValleyCellFeature("Plot Yield", new ColorFeatureGradiated(Color.orange, new UnitIntervalDataPoint() {
                /**
                 * 
                 */
                private static final long serialVersionUID = -5294758012864949871L;

                public double getValue(Object object) {
                    return ((((Location) object).getBaseYield()) / 1200.0);
                }
            }));
        valley.addDrawFeature(yieldFill);

        FillValleyCellFeature zoneYieldFill =
            new FillValleyCellFeature("Zone Yield", new ColorFeatureGradiated(Color.orange, new UnitIntervalDataPoint() {
                /**
                 * 
                 */
                private static final long serialVersionUID = 322526470426236151L;

                public double getValue(Object object) {
                    return ((double) (((Location) object).getYieldZone().getYield()) / 1200.0);
                }
            }));
        valley.addDrawFeature(zoneYieldFill);

        DrawFeature drawWaterFeature = new DrawFeature("Water Sources") {
            /**
             * 
             */
            private static final long serialVersionUID = 8533411178579775478L;

            public void draw(Graphics g, Object object, int width, int height) {
                if (((Location) object).isCurrentWaterSource()) {
                    g.setColor(Color.blue);
                    g.fillOval(0, 0, width - 2, height - 2);
                }
            }
        };
        valley.addDrawFeature(drawWaterFeature);

        DrawFeature drawFarmFeature = new DrawFeature("Farms") {
            /**
             * 
             */
            private static final long serialVersionUID = -1940011486883417752L;

            public void draw(Graphics g, Object object, int width, int height) {
                if (((Location) object).getFarm() != null) {
                    g.setColor(Color.yellow);
                    DrawSymbol.DRAW_HATCH.draw(g, width - 2, height - 2);
                }
            }
        };
        valley.addDrawFeature(drawFarmFeature);

        /*DrawFeature drawMaxFeature = new DrawFeature("Best Yield") {
            public void draw(Graphics g, Object object, int width, int height) {
                if (object == (((Location) object).getYieldZone().getAvailableLocations().getLast())) {
                    g.setColor(Color.red);
                    DrawSymbol.DRAW_RECT.draw(g, width, height);
                }
            }
        };
        valley.addDrawFeature(drawMaxFeature);*/

        DrawFeature sandDuneFeature = new DrawFeature("Sand Dunes") {
            /**
             * 
             */
            private static final long serialVersionUID = -2391074808277172861L;

            public void draw(Graphics g, Object object, int width, int height) {
                if (((Location) object).isSandDune()) {
                    g.setColor(Color.green);
                    g.fillOval(0 + 2, 0 + 2, width - 2, height - 2);
                }
            }
        };
        valley.addDrawFeature(sandDuneFeature);

        final ColorFeatureGradiated historicSettlementSizeColor = new ColorFeatureGradiated("Households");
        historicSettlementSizeColor.setDataPoint(new UnitIntervalDataPoint() {
            /**
             * 
             */
            private static final long serialVersionUID = 6295840997659754327L;

            public double getValue(Object object) {
                return ((double) (((Location) object).getHistoricSettlementHouseholdCount() - 1) / 10.0);
            }
        });
        historicSettlementSizeColor.setMaximumColor(Color.red);
        DrawFeature historicSettlementFeature = new DrawFeature("Historic Settlements") {
            /**
             * 
             */
            private static final long serialVersionUID = -3243407849851172816L;

            public void draw(Graphics g, Object object, int width, int height) {
                if (((Location) object).isCurrentHistoricSettlement()) {
                    g.setColor(historicSettlementSizeColor.getColor(object));
                    DrawSymbol.FILL_OVOID.draw(g, width, height);
                    g.setColor(Color.red);
                    DrawSymbol.DRAW_OVOID.draw(g, width, height);
                }
            }
        };
        valley.addDrawFeature(historicSettlementFeature);

        final ColorFeatureGradiated settlementSizeColor = new ColorFeatureGradiated("Settlements");
        settlementSizeColor.setDataPoint(new UnitIntervalDataPoint() {
            /**
             * 
             */
            private static final long serialVersionUID = 1044376827552903900L;

            public double getValue(Object object) {
                return ((double) (((Settlement) object).getSize() - 1) / 10.0);
            }
        });
        DrawFeature settlementFeature = new DrawFeature("Simulation Settlements") {
            /**
             * 
             */
            private static final long serialVersionUID = 886210092045835742L;

            public void draw(Graphics g, Object object, int width, int height) {
                if (((Location) object).getSettlement() != null) {
                    g.setColor(settlementSizeColor.getColor(((Location) object).getSettlement()));
                    DrawSymbol.FILL_OVOID.draw(g, width - 1, height - 1);
                    g.setColor(Color.black);
                    DrawSymbol.DRAW_OVOID.draw(g, width - 1, height - 1);
                }
            }
        };
        valley.addDrawFeature(settlementFeature);
        DrawFeature simSettlementTierFeature = new DrawFeature("Simulation Settlement Tier") {
            /**
             * 
             */
            private static final long serialVersionUID = 2663578481949934207L;

            public void draw(Graphics g, Object object, int width, int height) {
                if (((Location) object).isCurrentHistoricSettlement()) {
                    if (((Location) object).getHistoricSettlementHouseholdCount() < 5) {
                        g.setColor(Color.black);
                    } else if (((Location) object).getHistoricSettlementHouseholdCount() < 20) {
                        g.setColor(Color.green);
                    } else {
                        g.setColor(Color.red);
                    }
                    DrawSymbol.FILL_OVOID.draw(g, width, height);
                }
            }
        };
        valley.addDrawFeature(simSettlementTierFeature);
        DrawFeature histSettlementTierFeature = new DrawFeature("Historical Settlement Tier") {
            /**
             * 
             */
            private static final long serialVersionUID = 8151081684304662162L;

            public void draw(Graphics g, Object object, int width, int height) {
                if (((Location) object).getSettlement() != null) {
                    if (((Location) object).getSettlement().getSize() < 5) {
                        g.setColor(Color.black);
                    } else if (((Location) object).getSettlement().getSize() < 20) {
                        g.setColor(Color.green);
                    } else {
                        g.setColor(Color.red);
                    }
                    DrawSymbol.FILL_OVOID.draw(g, width, height);
                }
            }
        };
        valley.addDrawFeature(histSettlementTierFeature);
        DrawFeature drawClanFeature = new DrawFeature("Clan") {
            /**
             * 
             */
            private static final long serialVersionUID = 4722116535511174556L;

            public void draw(Graphics g, Object object, int width, int height) {
                if (((Location) object).getClan() != null) {
                    g.setColor(((Location) object).getClan().getColor());
                    g.drawRect(0, 0, width - 1, height - 1);
                }
            }
        };
        valley.addDrawFeature(drawClanFeature);
    }

    public void createViews() {
        super.createViews();
        //valley.setCellsRequestUpdates(true);
        //Create a new chart
        ChartView chart = new ChartView();
        //Add it to the agents view, just like any other view
        valley.addView(chart);
        //And add some of the stat series we've just created to it
        chart.addSeries("Sum Historic Households", Color.red);
        chart.addSeries("Count Households", Color.black);
        //chart.addSeries("Sum Historic Household Size", Color.red);
        //chart.addSeries("Sum Household Size", Color.black);
        //chart.addSeries("Count Potential Yield", Color.blue);
        //chart.addSeries("Sum Historic Population", Color.red);
        //chart.addSeries("Count People", Color.black);
        /*chart.addSeries("Count Deaths Starvation", Color.black);
        chart.addSeries("Count Deaths Old Age", Color.gray);
        chart.addSeries("Count Movements", Color.red);
        chart.addSeries("Count Fissions", Color.green);
        chart.addSeries("Count Departures", Color.white);*/
        /*chart.addSeries("Count Households in General Valley Floor", Color.black);
chart.addSeries("Count Households in North Valley Floor", Color.red);
chart.addSeries("Count Households in Mid-Valley Floor", Color.green);
chart.addSeries("Count Households in Uplands Natural", Color.yellow);
chart.addSeries("Count Households in Uplands Arable", Color.blue);
chart.addSeries("Count Households in Kinbiko Canyon", Color.pink);*/
        /*chart.addSeries("Count Farms in General Valley Floor", Color.black);
        chart.addSeries("Count Farms in North Valley Floor", Color.red);
        chart.addSeries("Count Farms in Mid-Valley Floor", Color.green);
        chart.addSeries("Count Farms in Uplands Natural", Color.yellow);
        chart.addSeries("Count Farms in Uplands Arable", Color.blue);
        chart.addSeries("Count Farms in Kinbiko Canyon", Color.pink);*/
        /*chart.addSeries("Sum Size 1", Color.black);
        chart.addSeries("Sum Size 2 to 3", Color.gray);
        chart.addSeries("Sum Size 4 to 9", Color.blue);
        chart.addSeries("Sum Size 10 to 19", Color.green);
        chart.addSeries("Sum Size 20 to 39", Color.orange);
        chart.addSeries("Sum Size 40 to 79", Color.yellow);
        chart.addSeries("Sum Size 80+", Color.red);*/

        Overhead2DView[] views = new Overhead2DView[2];
        views[0] = new Overhead2DView();
        views[0].setCellSize(5);
        views[0].setName("Simulation");
        views[1] = new Overhead2DView();
        views[1].setCellSize(5);
        views[1].setName("History");
        valley.addViews(views);

        views[0].getDrawSelection().clearSelection();
        views[0].getDrawSelection().setSelected("Hydrology", true);
        views[0].getDrawSelection().setSelected("Farms", true);
        views[0].getDrawSelection().setSelected("Simulation Settlement Tier", true);
        views[0].getDrawSelection().setSelected("Water Sources", true);

        views[1].getDrawSelection().clearSelection();
        views[1].getDrawSelection().setSelected("Hydrology", true);
        views[1].getDrawSelection().setSelected("Farms", true);
        views[1].getDrawSelection().setSelected("Historical Settlement Tier", true);
        views[1].getDrawSelection().setSelected("Water Sources", true);

    }

    /**
     * @return
     */
    public int getTypicalHouseholdSize() {
        return (typicalHouseholdSize);
    }

    /**
     * @param typicalHouseholdSize
     */
    public void setTypicalHouseholdSize(int typicalHouseholdSize) {
        this.typicalHouseholdSize = typicalHouseholdSize;
    }
}
