/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.sugarscape;

import org.ascape.model.rule.MoveRandomWithin;

public class GAS_II_6 extends GAS_II_2 {

    /**
     * 
     */
    private static final long serialVersionUID = -6891001884991349614L;

    public void createScape() {
        super.createScape();
        setSugarMoundness(.0037f);

        SugarAgent agent = new SugarAgent();
        setMinVision(6);
        setMaxVision(10);
        agent.setHostScape(sugarscape);
        agents.setPrototypeAgent(agent);
        agents.setExtent(new org.ascape.model.space.Coordinate1DDiscrete(350));
        MoveRandomWithin place = new MoveRandomWithin();
        place.setArea(0, 30, 20, 20);
        agents.addInitialRule(place);
        try {
            setStopPeriod(50);
        } catch (org.ascape.model.space.SpatialTemporalException e) {
            System.out.println("Can't set stop: " + e);
        }
    }
}
