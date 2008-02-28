/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.test.model.space;

import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.ascape.model.LocatedAgent;
import org.ascape.model.Scape;
import org.ascape.model.space.Continuous2D;
import org.ascape.model.space.Coordinate2DContinuous;
import org.ascape.model.space.SubContinuous2D;
import org.ascape.util.Conditional;

/*
 * These tests have arguable merit...in a sense they are just recapitualiting sublists tests..
 */

public class SubContinuous2DTest extends TestCase {

    public SubContinuous2DTest(String name) {
        super(name);
    }

    public void testDistance() {
        Scape ts = new Scape(new Continuous2D());
        ts.setPrototypeAgent(new LocatedAgent());
        ts.setExtent(1);
        ts.createScape();
        ts.execute(Scape.INITIALIZE_RULE);

        Scape tsSub = new Scape(new SubContinuous2D());
        tsSub.setPrototypeAgent(new LocatedAgent());
        tsSub.setSuperScape(ts);
        tsSub.setExtent(1);
        tsSub.createScape();
        tsSub.execute(Scape.INITIALIZE_RULE);

        LocatedAgent a0 = (LocatedAgent) ts.iterator().next();
        LocatedAgent a1 = (LocatedAgent) tsSub.iterator().next();

        a0.setCoordinate(new Coordinate2DContinuous(1.0, 2.0));
        a1.setCoordinate(new Coordinate2DContinuous(3.5, -1.6));

        assertTrue(ts.calculateDistance(a0, a0) == 0.0f);
        assertTrue(ts.calculateDistance(a1, a1) == 0.0f);
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(ts.calculateDistance(a0, a1), Math.sqrt(Math.pow(3.5 - 1.0, 2) + Math.pow(-1.6 - 2.0, 2))));
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(ts.calculateDistance(a1, a0), Math.sqrt(Math.pow(3.5 - 1.0, 2) + Math.pow(-1.6 - 2.0, 2))));
    }

    public void testFindWithin() {
        Scape ts = new Scape(new Continuous2D());
        ts.setPrototypeAgent(new LocatedAgent());
        ts.setExtent(2);
        ts.createScape();
        ts.execute(Scape.INITIALIZE_RULE);

        Scape tsSub1 = new Scape(new SubContinuous2D());
        tsSub1.setPrototypeAgent(new LocatedAgent());
        tsSub1.setSuperScape(ts);
        tsSub1.setExtent(2);
        tsSub1.createScape();
        tsSub1.execute(Scape.INITIALIZE_RULE);

        Scape tsSub2 = new Scape(new SubContinuous2D());
        tsSub2.setPrototypeAgent(new LocatedAgent());
        tsSub2.setSuperScape(ts);
        tsSub2.setExtent(1);
        tsSub2.createScape();
        tsSub2.execute(Scape.INITIALIZE_RULE);

        Iterator tsIter = ts.iterator();
        LocatedAgent a0 = (LocatedAgent) tsIter.next();
        LocatedAgent a1 = (LocatedAgent) tsIter.next();
        Iterator tsSubIter = tsSub1.iterator();
        final LocatedAgent a2 = (LocatedAgent) tsSubIter.next();
        LocatedAgent a3 = (LocatedAgent) tsSubIter.next();
        LocatedAgent a4 = (LocatedAgent) tsSub2.iterator().next();

        a0.setCoordinate(new Coordinate2DContinuous(1.0f, 2.0f));
        a1.setCoordinate(new Coordinate2DContinuous(3.6f, 3.0f));
        a2.setCoordinate(new Coordinate2DContinuous(3.1f, 3.0f));
        a3.setCoordinate(new Coordinate2DContinuous(2.6f, 3.0f));
        a4.setCoordinate(new Coordinate2DContinuous(2.6f, 2.9f));

        Iterator iter = ts.withinIterator(a2.getCoordinate(), null, true, 0.0f);
        assertTrue(iter.hasNext());
        assertTrue(iter.next() == a2);
        assertTrue(!iter.hasNext());

        List t0 = a2.findWithin(0.0f, true);
        assertTrue(t0.size() == 1);
        assertTrue(t0.get(0) == a2);

        List t1 = ts.findWithin(a2.getCoordinate(), null, true, 0.501f);
        assertTrue(t1.size() == 3);
        assertTrue(!t1.contains(a0));
        assertTrue(t1.contains(a1));
        assertTrue(t1.contains(a2));
        assertTrue(t1.contains(a3));
        assertTrue(!t1.contains(a4));

        List t2 = ts.findWithin(a2.getCoordinate(), null, true, 0.53f);
        assertTrue(t2.size() == 4);
        assertTrue(!t2.contains(a0));
        assertTrue(t2.contains(a1));
        assertTrue(t2.contains(a2));
        assertTrue(t2.contains(a3));
        assertTrue(t2.contains(a4));

        List t3 = ts.findWithin(a2.getCoordinate(), null, true, 10.0f);
        assertTrue(t3.size() == 5);
        assertTrue(t3.contains(a0));
        assertTrue(t3.contains(a1));
        assertTrue(t3.contains(a2));
        assertTrue(t3.contains(a3));
        assertTrue(t3.contains(a4));

        Conditional testConditional = new Conditional() {
            public boolean meetsCondition(Object o) {
                return (((LocatedAgent) o).calculateDistance(a2) > 0.501f);
            }
        };

        List t4 = ts.findWithin(a2.getCoordinate(), testConditional, true, 10.0f);
        assertTrue(t4.size() == 2);
        assertTrue(t4.contains(a0));
        assertTrue(!t4.contains(a1));
        assertTrue(!t4.contains(a2));
        assertTrue(!t4.contains(a3));
        assertTrue(t4.contains(a4));
    }

    public void testHasWithin() {
        Scape ts = new Scape(new Continuous2D());
        ts.setPrototypeAgent(new LocatedAgent());
        ts.setExtent(2);
        ts.createScape();
        ts.execute(Scape.INITIALIZE_RULE);

        Scape tsSub1 = new Scape(new SubContinuous2D());
        tsSub1.setPrototypeAgent(new LocatedAgent());
        tsSub1.setSuperScape(ts);
        tsSub1.setExtent(2);
        tsSub1.createScape();
        tsSub1.execute(Scape.INITIALIZE_RULE);

        Scape tsSub2 = new Scape(new SubContinuous2D());
        tsSub2.setPrototypeAgent(new LocatedAgent());
        tsSub2.setSuperScape(ts);
        tsSub2.setExtent(1);
        tsSub2.createScape();
        tsSub2.execute(Scape.INITIALIZE_RULE);

        Iterator tsIter = ts.iterator();
        LocatedAgent a0 = (LocatedAgent) tsIter.next();
        LocatedAgent a1 = (LocatedAgent) tsIter.next();
        Iterator tsSubIter = tsSub1.iterator();
        final LocatedAgent a2 = (LocatedAgent) tsSubIter.next();
        LocatedAgent a3 = (LocatedAgent) tsSubIter.next();
        LocatedAgent a4 = (LocatedAgent) tsSub2.iterator().next();

        a0.setCoordinate(new Coordinate2DContinuous(1.0f, 2.0f));
        a1.setCoordinate(new Coordinate2DContinuous(3.6f, 3.0f));
        a2.setCoordinate(new Coordinate2DContinuous(3.1f, 3.0f));
        a3.setCoordinate(new Coordinate2DContinuous(2.6f, 3.0f));
        a4.setCoordinate(new Coordinate2DContinuous(2.6f, 2.9f));

        assertTrue(ts.hasWithin(new Coordinate2DContinuous(3.6f, 2.96f), null, false, 0.05f));
        assertTrue(!ts.hasWithin(new Coordinate2DContinuous(3.6f, 2.96f), null, false, 0.02f));
    }

    public void testCountWithin() {
        Scape ts = new Scape(new Continuous2D());
        ts.setPrototypeAgent(new LocatedAgent());
        ts.setExtent(2);
        ts.createScape();
        ts.execute(Scape.INITIALIZE_RULE);

        Scape tsSub1 = new Scape(new SubContinuous2D());
        tsSub1.setPrototypeAgent(new LocatedAgent());
        tsSub1.setSuperScape(ts);
        tsSub1.setExtent(2);
        tsSub1.createScape();
        tsSub1.execute(Scape.INITIALIZE_RULE);

        Scape tsSub2 = new Scape(new SubContinuous2D());
        tsSub2.setPrototypeAgent(new LocatedAgent());
        tsSub2.setSuperScape(ts);
        tsSub2.setExtent(1);
        tsSub2.createScape();
        tsSub2.execute(Scape.INITIALIZE_RULE);

        Iterator tsIter = ts.iterator();
        LocatedAgent a0 = (LocatedAgent) tsIter.next();
        LocatedAgent a1 = (LocatedAgent) tsIter.next();
        Iterator tsSubIter = tsSub1.iterator();
        final LocatedAgent a2 = (LocatedAgent) tsSubIter.next();
        LocatedAgent a3 = (LocatedAgent) tsSubIter.next();
        LocatedAgent a4 = (LocatedAgent) tsSub2.iterator().next();

        a0.setCoordinate(new Coordinate2DContinuous(1.0f, 2.0f));
        a1.setCoordinate(new Coordinate2DContinuous(3.6f, 3.0f));
        a2.setCoordinate(new Coordinate2DContinuous(3.1f, 3.0f));
        a3.setCoordinate(new Coordinate2DContinuous(2.6f, 3.0f));
        a4.setCoordinate(new Coordinate2DContinuous(2.6f, 2.9f));

        assertTrue(ts.countWithin(a2.getCoordinate(), null, true, 0.0f) == 1);
        assertTrue(ts.countWithin(a2.getCoordinate(), null, true, 0.501f) == 3);
        assertTrue(ts.countWithin(a2.getCoordinate(), null, true, 0.53f) == 4);
        assertTrue(ts.countWithin(a2.getCoordinate(), null, true, 10.0f) == 5);
    }
}