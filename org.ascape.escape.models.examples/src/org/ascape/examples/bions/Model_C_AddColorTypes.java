/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */

package org.ascape.examples.bions;

import org.ascape.model.CellOccupant;
import org.ascape.model.HostCell;
import org.ascape.model.Scape;
import org.ascape.model.event.ScapeEvent;
import org.ascape.model.space.Array2D;
import org.ascape.model.space.Array2DMoore;
import org.ascape.util.vis.ColorFeature;
import org.ascape.view.vis.Overhead2DView;


/**
 * Bion model.
 * 
 * @author mparker
 * 
 */
public class Model_C_AddColorTypes extends Scape {

    /**
     * 
     */
    private static final long serialVersionUID = 6693075711032433071L;

    private Scape territory;

    private Scape redBions;

    private Scape orangeBions;

    private Scape blueBions;

    private double initialDensity = 0.28f;

    private class BionTile extends HostCell {

        /**
         * 
         */
        private static final long serialVersionUID = 7226538144767880661L;

        public Object getPlatformColor() {
            return ColorFeature.BLACK;
        }
    }

    private class Bion extends CellOccupant {

        /**
         * 
         */
        private static final long serialVersionUID = 7734950764710735084L;

    }

    class RedBion extends Bion {

        /**
         * 
         */
        private static final long serialVersionUID = 4139478235755451956L;

        public Object getPlatformColor() {
            return ColorFeature.RED;
        }
    }

    class OrangeBion extends Bion {

        /**
         * 
         */
        private static final long serialVersionUID = 3359811262263342000L;

        public Object getPlatformColor() {
            return ColorFeature.ORANGE;
        }
    }

    class BlueBion extends Bion {

        /**
         * 
         */
        private static final long serialVersionUID = 3008190589816546252L;

        public Object getPlatformColor() {
            return ColorFeature.BLUE;
        }
    }

    public void createScape() {
        super.createScape();
        territory = new Scape(new Array2DMoore());
        territory.setName("Bionland");
        territory.setPrototypeAgent(new BionTile());
        ((Array2D) territory.getSpace()).setExtent(25, 25);
        add(territory);
        redBions = new Scape();
        createBions(redBions, new RedBion(), "Red");
        orangeBions = new Scape();
        createBions(orangeBions, new OrangeBion(), "Orange");
        blueBions = new Scape();
        createBions(blueBions, new BlueBion(), "Blue");
    }

    public void scapeSetup(ScapeEvent scapeEvent) {
        int popSize = (int) (territory.getSize() * initialDensity / 3.0f);
        redBions.setExtent(popSize);
        orangeBions.setExtent(popSize);
        blueBions.setExtent(popSize);
    }

    protected void createBions(Scape bions, CellOccupant protoCell, String name) {
        bions.setName(name);
        protoCell.setHostScape(territory);
        bions.setPrototypeAgent(protoCell);
        bions.addInitialRule(MOVE_RANDOM_LOCATION_RULE);
        bions.addRule(RANDOM_WALK_RULE);
        add(bions);
    }

    public void createViews() {
        super.createViews();
        Overhead2DView mapView = new Overhead2DView("Map");
        territory.addView(mapView);
    }

    public double getInitialPopulationDensity() {
        return initialDensity;
    }

    public void setInitialPopulationDensity(double initialDensity) {
        this.initialDensity = initialDensity;
    }
}
