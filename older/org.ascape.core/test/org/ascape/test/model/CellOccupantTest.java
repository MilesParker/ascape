/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.test.model;

import junit.framework.TestCase;

import org.ascape.model.CellOccupant;
import org.ascape.model.HostCell;
import org.ascape.model.Scape;
import org.ascape.model.space.Array2D;
import org.ascape.model.space.Array2DMoore;
import org.ascape.model.space.Coordinate2DDiscrete;

public class CellOccupantTest extends TestCase {

    public CellOccupantTest(String name) {
        super(name);
    }

    public void testRandomWalkAvailableNowhereToGo() {
        Scape testScape = new Scape(new Array2DMoore());
        testScape.setPrototypeAgent(new HostCell());
        testScape.setExtent(15, 15);
        testScape.createScape();
        testScape.initialize();
        ((Array2D) testScape.getSpace()).get(9, 9).setOccupant(new CellOccupant());
        ((Array2D) testScape.getSpace()).get(9, 10).setOccupant(new CellOccupant());
        ((Array2D) testScape.getSpace()).get(9, 11).setOccupant(new CellOccupant());
        ((Array2D) testScape.getSpace()).get(10, 9).setOccupant(new CellOccupant());
        ((Array2D) testScape.getSpace()).get(10, 11).setOccupant(new CellOccupant());
        ((Array2D) testScape.getSpace()).get(11, 9).setOccupant(new CellOccupant());
        ((Array2D) testScape.getSpace()).get(11, 10).setOccupant(new CellOccupant());
        ((Array2D) testScape.getSpace()).get(11, 11).setOccupant(new CellOccupant());

        CellOccupant mover = new CellOccupant();
        mover.moveTo((HostCell) ((Array2D) testScape.getSpace()).get(10, 10));
        mover.randomWalkAvailable();

        assertTrue(mover.getHostCell().getCoordinate().equals(new Coordinate2DDiscrete(10, 10)));
    }

    public void testRandomWalkAvailableOnePlaceToGo() {
        Scape testScape = new Scape(new Array2DMoore());
        testScape.setPrototypeAgent(new HostCell());
        testScape.setExtent(15, 15);
        testScape.createScape();
        testScape.initialize();
        //9, 9 is available..
        ((Array2D) testScape.getSpace()).get(9, 10).setOccupant(new CellOccupant());
        ((Array2D) testScape.getSpace()).get(9, 11).setOccupant(new CellOccupant());
        ((Array2D) testScape.getSpace()).get(10, 9).setOccupant(new CellOccupant());
        ((Array2D) testScape.getSpace()).get(10, 11).setOccupant(new CellOccupant());
        ((Array2D) testScape.getSpace()).get(11, 9).setOccupant(new CellOccupant());
        ((Array2D) testScape.getSpace()).get(11, 10).setOccupant(new CellOccupant());
        ((Array2D) testScape.getSpace()).get(11, 11).setOccupant(new CellOccupant());

        CellOccupant mover = new CellOccupant();
        mover.moveTo((HostCell) ((Array2D) testScape.getSpace()).get(10, 10));
        mover.randomWalkAvailable();

        assertTrue(mover.getHostCell().getCoordinate().equals(new Coordinate2DDiscrete(9, 9)));
    }

    public void testRandomWalkAvailableTwoPlacesToGo() {
        Scape testScape = new Scape(new Array2DMoore());
        testScape.setPrototypeAgent(new HostCell());
        testScape.setExtent(15, 15);
        testScape.createScape();
        testScape.initialize();
        //9,  9 is available..
        //9, 10 is available..
        ((Array2D) testScape.getSpace()).get(9, 11).setOccupant(new CellOccupant());
        ((Array2D) testScape.getSpace()).get(10, 9).setOccupant(new CellOccupant());
        ((Array2D) testScape.getSpace()).get(10, 11).setOccupant(new CellOccupant());
        ((Array2D) testScape.getSpace()).get(11, 9).setOccupant(new CellOccupant());
        ((Array2D) testScape.getSpace()).get(11, 10).setOccupant(new CellOccupant());
        ((Array2D) testScape.getSpace()).get(11, 11).setOccupant(new CellOccupant());

        Scape list = new Scape();
        CellOccupant mover = new CellOccupant();
        list.add(mover);
        mover.moveTo((HostCell) ((Array2D) testScape.getSpace()).get(10, 10));
        mover.randomWalkAvailable();

        assertTrue(mover.getHostCell().getCoordinate().equals(new Coordinate2DDiscrete(9, 9)) || mover.getCoordinate().equals(new Coordinate2DDiscrete(9, 10)));
    }
}