/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.sugarscape;

import java.awt.Color;

import org.ascape.model.Scape;
import org.ascape.model.space.Array2DVonNeumann;
import org.ascape.model.space.Coordinate1DDiscrete;
import org.ascape.model.space.Coordinate2DDiscrete;
import org.ascape.util.data.UnitIntervalDataPoint;
import org.ascape.util.vis.ColorFeature;
import org.ascape.util.vis.ColorFeatureConcrete;
import org.ascape.util.vis.ColorFeatureGradiated;

/**
 * Extends GAS_Base so that the scape can include spice as well as sugar
 */
public class GAS_SpiceBase extends GAS_Base {

    /**
     * 
     */
    private static final long serialVersionUID = 5886348096049907358L;
    private float spiceMoundness = .008f; // was .004
    private int minSpiceMetabolism = 1;  // was 1
    private int maxSpiceMetabolism = 3; // was 4
    private int minInitialSpice = 0;
    private int maxInitialSpice = 30;

    public void createScape() {
        setPrototypeAgent(new Scape());
        sugarscape = new Scape(new Array2DVonNeumann());
        sugarscape.setName("SpiceScape");
        sugarscape.setPrototypeAgent(new SpiceCell());
        sugarscape.setExtent(new Coordinate2DDiscrete(50, 50));
        SpiceAgent agent = new SpiceAgent();
        agent.setHostScape(sugarscape);
        agents = new Scape();
        agents.setPrototypeAgent(agent);
        agents.setExtent(new Coordinate1DDiscrete(400));
        sugarscape.setExecutionOrder(Scape.RULE_ORDER);
        sugarscape.setCellsRequestUpdates(true);
        add(sugarscape);
        add(agents);
    }

    public void initialize() {
        super.initialize();
        setSugarMoundness(.008f);
        setMinSugarMetabolism(1); // was 1
        setMaxSugarMetabolism(3); // was 4
        setMaxVision(10);
        setMinVision(10);  // eliminates variation in vision
    }

    /**
     * Returns the topology parameter for spice peaks
     */
    public float getSpiceMoundness() {
        return spiceMoundness;
    }

    /**
     * Sets the topology parameter for spice peaks
     */
    public void setSpiceMoundness(float moundness) {
        spiceMoundness = moundness;
    }

    /**
     * Returns the minimum spice metabolism that an agent may be given on initialization.
     * Model parameter.
     */
    public int getMinSpiceMetabolism() {
        return minSpiceMetabolism;
    }

    /**
     * Sets the minimum spice metabolism that an agent may be given on initialization.
     * Model parameter.
     */
    public void setMinSpiceMetabolism(int _minMetabolism) {
        minSpiceMetabolism = _minMetabolism;
    }

    /**
     * Returns the maximum spice metabolism that an agent may be given on initialization.
     * Model parameter.
     */
    public int getMaxSpiceMetabolism() {
        return maxSpiceMetabolism;
    }

    /**
     * Returns the minimum spice that an agent may be given on initialization.
     * Model parameter.
     */
    public int getMinInitialSpice() {
        return minInitialSpice;
    }

    /**
     * Sets the minimum spice that an agent may be given on initialization.
     * Model parameter.
     */
    public void setMinInitialSpice(int _minInitialSpice) {
        minInitialSpice = _minInitialSpice;
    }

    /**
     * Returns the maximum spice that an agent may be given on initialization.
     * Model parameter.
     */
    public int getMaxInitialSpice() {
        return maxInitialSpice;
    }

    /**
     * Sets the maximum spice that an agent may be given on initialization.
     * Model parameter.
     */
    public void setMaxInitialSugar(int _maxInitialSpice) {
        maxInitialSpice = _maxInitialSpice;
    }

    /**
     * Sets the maximum Spice metabolism that an agent may be given on initialization.
     * Model parameter.
     */
    public void setMaxSpiceMetabolism(int _maxMetabolism) {
        maxSpiceMetabolism = _maxMetabolism;
    }

    public void createViews() {
        super.createViews();
        final ColorFeatureGradiated colorCellForSugar = new ColorFeatureGradiated("Sugar");
        colorCellForSugar.setDataPoint(new UnitIntervalDataPoint() {
            /**
             * 
             */
            private static final long serialVersionUID = 934997267282438193L;

            public double getValue(Object object) {
                return ((double) ((SpiceCell) object).getSugar().getQuantity() / (double) SpiceCell.MAX_SUGAR);
            }
        });
        colorCellForSugar.setMaximumColor(Color.yellow);

        final ColorFeatureGradiated colorCellForSpice = new ColorFeatureGradiated("Spice");
        colorCellForSpice.setDataPoint(new UnitIntervalDataPoint() {
            /**
             * 
             */
            private static final long serialVersionUID = -7722867828090284365L;

            public double getValue(Object object) {
                return ((double) ((SpiceCell) object).getSpice().getQuantity() / (double) SpiceCell.MAX_SPICE);
            }
        });
        colorCellForSpice.setMaximumColor(Color.pink);
        ColorFeature colorSugarOrSpice = new ColorFeatureConcrete("colorSugarOrSpice") {
            /**
             * 
             */
            private static final long serialVersionUID = 6668081586209688060L;

            public Color getColor(Object object) {
                if (((SpiceCell) object).getSpice().getQuantity() > ((SpiceCell) object).getSugar().getQuantity()) {
                    return colorCellForSpice.getColor(object);
                } else {
                    return colorCellForSugar.getColor(object);
                }
            }
        };
        sugarView.setCellColorFeature(colorSugarOrSpice);
    }
}
