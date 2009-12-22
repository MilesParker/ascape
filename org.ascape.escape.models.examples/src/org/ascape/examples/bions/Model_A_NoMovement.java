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
import org.ascape.model.space.Array2D;
import org.ascape.model.space.Array2DMoore;
import org.ascape.util.vis.ColorFeature;
import org.escape.view.vis.Overhead2DView;

/**
 * Bion model.
 * 
 * @author mparker
 * 
 */
public class Model_A_NoMovement extends Scape {

    /**
     * 
     */
    private static final long serialVersionUID = -2512386128295267885L;

    private Scape territory;

    private Scape bions;

    private class BionTile extends HostCell {

        /**
         * 
         */
        private static final long serialVersionUID = -7187320826408194012L;

        public Object getPlatformColor() {
            return ColorFeature.LIGHT_GRAY;
        }
    }

    private class Bion extends CellOccupant {

        /**
         * 
         */
        private static final long serialVersionUID = 3659897580113436647L;

    }

    public void createScape() {
        super.createScape();
        territory = new Scape(new Array2DMoore());
        territory.setName("Bionland");
        territory.setPrototypeAgent(new BionTile());
        ((Array2D) territory.getSpace()).setExtent(25, 25);
        add(territory);
        bions = new Scape();
        bions.setExtent(100);
        bions.setName(name);
        bions.addInitialRule(MOVE_RANDOM_LOCATION_RULE);
        CellOccupant protoAgent = new Bion();
        protoAgent.setHostScape(territory);
        bions.setPrototypeAgent(protoAgent);
        add(bions);
    }

    public void createViews() {
        super.createViews();
        Overhead2DView mapView = new Overhead2DView();
        mapView.setName("Map");
        territory.addView(mapView);
    }
}
