/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.test.model.space;

import junit.framework.TestCase;

import org.ascape.model.Cell;
import org.ascape.model.CellOccupant;
import org.ascape.model.HostCell;
import org.ascape.model.Scape;
import org.ascape.model.space.Array2DMoore;
import org.ascape.model.space.Coordinate2DDiscrete;

public class DiscreteTest extends TestCase {

    public DiscreteTest(String name) {
        super(name);
    }

    Scape mn;
    Scape pop = new Scape();

    protected void setUp() {
        mn = new Scape(new Array2DMoore());
        mn.setExtent(100, 100);
        mn.setPrototypeAgent(new HostCell());
        mn.createScape();
        mn.execute(Scape.INITIALIZE_RULE);
        CellOccupant proto = new CellOccupant();
        proto.setHostScape(mn);
        pop.setPrototypeAgent(proto);
        pop.setExtent(4);
        pop.createScape();
        mn.execute(Scape.INITIALIZE_RULE);
    }

    public void testFindNearestMember() {
        ((CellOccupant) pop.get(0)).moveTo((HostCell) mn.get(new Coordinate2DDiscrete(1, 1)));
        ((CellOccupant) pop.get(1)).moveTo((HostCell) mn.get(new Coordinate2DDiscrete(12, 15)));
        ((CellOccupant) pop.get(2)).moveTo((HostCell) mn.get(new Coordinate2DDiscrete(50, 50)));
        ((CellOccupant) pop.get(3)).moveTo((HostCell) mn.get(new Coordinate2DDiscrete(95, 95)));
        assertTrue(((HostCell) mn.get(new Coordinate2DDiscrete(2, 3))).findNearestOccupants() == ((Cell) mn.get(new Coordinate2DDiscrete(1, 1))).getOccupant());
        assertTrue(((HostCell) mn.get(new Coordinate2DDiscrete(11, 13))).findNearestOccupants() == ((Cell) mn.get(new Coordinate2DDiscrete(12, 15))).getOccupant());
        assertTrue(((HostCell) mn.get(new Coordinate2DDiscrete(98, 99))).findNearestOccupants() == ((Cell) mn.get(new Coordinate2DDiscrete(1, 1))).getOccupant());
        assertTrue(((HostCell) mn.get(new Coordinate2DDiscrete(82, 1))).findNearestOccupants() == ((Cell) mn.get(new Coordinate2DDiscrete(95, 95))).getOccupant());
        mn.getSpace().setPeriodic(false);
        assertTrue(((HostCell) mn.get(new Coordinate2DDiscrete(98, 99))).findNearestOccupants() == ((Cell) mn.get(new Coordinate2DDiscrete(95, 95))).getOccupant());
        assertTrue(((HostCell) mn.get(new Coordinate2DDiscrete(82, 1))).findNearestOccupants() == ((Cell) mn.get(new Coordinate2DDiscrete(50, 50))).getOccupant());

        assertTrue(((Cell) pop.get(0)).findNearest() == pop.get(1));
        assertTrue(((Cell) pop.get(2)).findNearest() == pop.get(1));

        mn.getSpace().setPeriodic(true);
        ((Cell) pop.get(1)).die();
        assertTrue(((Cell) pop.get(0)).findNearest() == pop.get(2));
        mn.getSpace().setPeriodic(false);
        assertTrue(((Cell) pop.get(0)).findNearest() == pop.get(1));

    }
}