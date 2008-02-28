/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */


package org.ascape.examples.boids;

import java.awt.Color;

import org.ascape.model.MomentumAgent;
import org.ascape.util.Conditional;


public class Obstacle extends MomentumAgent {

    /**
     * 
     */
    private static final long serialVersionUID = 4682987456440494084L;
    private Conditional boid;

    public void initialize() {
        super.initialize();
        boid = new Conditional() {
            /**
             * 
             */
            private static final long serialVersionUID = -3339428292053346642L;

            public boolean meetsCondition(Object o) {
                return o instanceof Boid;
            }
        };
    }

    public Color getColor() {
        return Color.black;
    }

    public int getNumBoidsAround() {
        return findWithin(boid, 20f).size();
    }

    public String getName() {
        return "Obstacle at " + getCoordinate();
    }
}
