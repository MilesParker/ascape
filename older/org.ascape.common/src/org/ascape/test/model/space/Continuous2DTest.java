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
import org.ascape.util.Conditional;

public class Continuous2DTest extends TestCase {

    Conditional trueCondition = new Conditional() {
        public boolean meetsCondition(Object o) {
            return true;
        }
    };

    Conditional falseCondition = new Conditional() {
        public boolean meetsCondition(Object o) {
            return false;
        }
    };

    public Continuous2DTest(String name) {
        super(name);
    }

    public void testDistance() {
        Scape ts = new Scape(new Continuous2D());
        ts.setPrototypeAgent(new LocatedAgent());
        ts.setSize(2);
        ts.setExtent(new Coordinate2DContinuous(50.0, 50.0));
// this works here, but the extent (Coordinate2DContinuous) will still be zero..
//        ts.setExtent(new Coordinate2DContinuous(2.0, 0.0));
//        ts.setSize(2);
        ts.createScape();
        ts.execute(Scape.INITIALIZE_RULE);

        Iterator tsIter = ts.iterator();
        LocatedAgent a0 = (LocatedAgent) tsIter.next();
        LocatedAgent a1 = (LocatedAgent) tsIter.next();

        a0.setCoordinate(new Coordinate2DContinuous(1.0, 2.0));
        a1.setCoordinate(new Coordinate2DContinuous(3.5, -1.6));

        assertTrue(ts.calculateDistance(a0, a0) == 0.);
        assertTrue(ts.calculateDistance(a1, a1) == 0.);
        assertTrue(ts.calculateDistance(a0, a1) == Math.sqrt(Math.pow(3.5 - 1., 2) + Math.pow(-1.6 - 2., 2)));
        assertTrue(ts.calculateDistance(a1, a0) == Math.sqrt(Math.pow(3.5 - 1., 2) + Math.pow(-1.6 - 2., 2)));
    }

    public void testDistancePeriodic() {
        Scape ts = new Scape(new Continuous2D());
        ts.setPrototypeAgent(new LocatedAgent());
        ts.setSize(2);
        ts.setExtent(new Coordinate2DContinuous(50.0, 50.0));
        ts.getSpace().setPeriodic(true);
        ts.createScape();
        ts.execute(Scape.INITIALIZE_RULE);

        Iterator tsIter = ts.iterator();
        LocatedAgent a0 = (LocatedAgent) tsIter.next();
        LocatedAgent a1 = (LocatedAgent) tsIter.next();

        a0.setCoordinate(new Coordinate2DContinuous(1.0, 2.0));
        a1.setCoordinate(new Coordinate2DContinuous(48.0, 48.0));
        assertTrue(ts.calculateDistance(a0, a0) == 0.);
        assertTrue(ts.calculateDistance(a1, a1) == 0.);
        assertTrue(ts.calculateDistance(a0, a1) == 5.0);
        assertTrue(ts.calculateDistance(a1, a0) == 5.0);

        a0.setCoordinate(new Coordinate2DContinuous(48.0, 2.0));
        a1.setCoordinate(new Coordinate2DContinuous(1.0, 48.0));
        assertTrue(ts.calculateDistance(a0, a0) == 0.);
        assertTrue(ts.calculateDistance(a1, a1) == 0.);
        assertTrue(ts.calculateDistance(a0, a1) == 5.0);
        assertTrue(ts.calculateDistance(a1, a0) == 5.0);

        a0.setCoordinate(new Coordinate2DContinuous(1.0, 48.0));
        a1.setCoordinate(new Coordinate2DContinuous(48.0, 2.0));
        assertTrue(ts.calculateDistance(a0, a0) == 0.);
        assertTrue(ts.calculateDistance(a1, a1) == 0.);
        assertTrue(ts.calculateDistance(a0, a1) == 5.0);
        assertTrue(ts.calculateDistance(a1, a0) == 5.0);

        a0.setCoordinate(new Coordinate2DContinuous(48.0, 48.0));
        a1.setCoordinate(new Coordinate2DContinuous(2.0, 1.0));
        assertTrue(ts.calculateDistance(a0, a0) == 0.);
        assertTrue(ts.calculateDistance(a1, a1) == 0.);
        assertTrue(ts.calculateDistance(a0, a1) == 5.0);
        assertTrue(ts.calculateDistance(a1, a0) == 5.0);
    }

    public void testRelativePositionNonPeriodic() {
        Scape ts = new Scape(new Continuous2D());
        ts.setPrototypeAgent(new LocatedAgent());
        ts.setSize(2);
        ts.getSpace().setPeriodic(false);
        ts.setExtent(new Coordinate2DContinuous(50.0, 50.0));
        // this works here, but the extent (Coordinate2DContinuous) will still be zero..
//        ts.setExtent(new Coordinate2DContinuous(2.0, 0.0));
//        ts.setSize(2);
        ts.createScape();
        ts.execute(Scape.INITIALIZE_RULE);

        Iterator tsIter = ts.iterator();
        LocatedAgent origin = (LocatedAgent) tsIter.next();
        LocatedAgent target = (LocatedAgent) tsIter.next();

        origin.setCoordinate(new Coordinate2DContinuous(1.0, 1.0));
        target.setCoordinate(new Coordinate2DContinuous(2.0, 2.0));
        Coordinate2DContinuous rel = (Coordinate2DContinuous) ((Continuous2D) ts.getSpace()).calculateRelativePosition(origin, target);
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(rel.getXValue(), 1.0));
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(rel.getYValue(), 1.0));

        origin.setCoordinate(new Coordinate2DContinuous(2.0, 2.0));
        target.setCoordinate(new Coordinate2DContinuous(1.0, 1.0));
        rel = (Coordinate2DContinuous) ((Continuous2D) ts.getSpace()).calculateRelativePosition(origin, target);
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(rel.getXValue(), -1.0));
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(rel.getYValue(), -1.0));

        origin.setCoordinate(new Coordinate2DContinuous(49.0, 49.0));
        target.setCoordinate(new Coordinate2DContinuous(1.0, 1.0));
        rel = (Coordinate2DContinuous) ((Continuous2D) ts.getSpace()).calculateRelativePosition(origin, target);
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(rel.getXValue(), -48.0));
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(rel.getYValue(), -48.0));

        origin.setCoordinate(new Coordinate2DContinuous(1.0, 1.0));
        target.setCoordinate(new Coordinate2DContinuous(49.0, 49.0));
        rel = (Coordinate2DContinuous) ((Continuous2D) ts.getSpace()).calculateRelativePosition(origin, target);
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(rel.getXValue(), 48.0));
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(rel.getYValue(), 48.0));
    }

    public void testRelativePositionPeriodic() {
        Scape ts = new Scape(new Continuous2D());
        ts.setPrototypeAgent(new LocatedAgent());
        ts.setSize(2);
        ts.getSpace().setPeriodic(true);
        ts.setExtent(new Coordinate2DContinuous(50.0, 50.0));
        // this works here, but the extent (Coordinate2DContinuous) will still be zero..
//        ts.setExtent(new Coordinate2DContinuous(2.0, 0.0));
//        ts.setSize(2);
        ts.createScape();
        ts.execute(Scape.INITIALIZE_RULE);

        Iterator tsIter = ts.iterator();
        LocatedAgent origin = (LocatedAgent) tsIter.next();
        LocatedAgent target = (LocatedAgent) tsIter.next();

        origin.setCoordinate(new Coordinate2DContinuous(1.0, 1.0));
        target.setCoordinate(new Coordinate2DContinuous(2.0, 2.0));
        Coordinate2DContinuous rel = (Coordinate2DContinuous) ((Continuous2D) ts.getSpace()).calculateRelativePosition(origin, target);
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(rel.getXValue(), 1.0));
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(rel.getYValue(), 1.0));

        origin.setCoordinate(new Coordinate2DContinuous(2.0, 2.0));
        target.setCoordinate(new Coordinate2DContinuous(1.0, 1.0));
        rel = (Coordinate2DContinuous) ((Continuous2D) ts.getSpace()).calculateRelativePosition(origin, target);
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(rel.getXValue(), -1.0));
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(rel.getYValue(), -1.0));

        origin.setCoordinate(new Coordinate2DContinuous(49.0, 49.0));
        target.setCoordinate(new Coordinate2DContinuous(1.0, 1.0));
        rel = (Coordinate2DContinuous) ((Continuous2D) ts.getSpace()).calculateRelativePosition(origin, target);
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(rel.getXValue(), 2.0));
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(rel.getYValue(), 2.0));

        origin.setCoordinate(new Coordinate2DContinuous(1.0, 1.0));
        target.setCoordinate(new Coordinate2DContinuous(49.0, 49.0));
        rel = (Coordinate2DContinuous) ((Continuous2D) ts.getSpace()).calculateRelativePosition(origin, target);
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(rel.getXValue(), -2.0));
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(rel.getYValue(), -2.0));
    }

    public void testDistanceNonPeriodic() {
        Scape ts = new Scape(new Continuous2D());
        ts.setPrototypeAgent(new LocatedAgent());
        ts.setSize(2);
        ts.setExtent(new Coordinate2DContinuous(50.0, 50.0));
        ts.getSpace().setPeriodic(false);
        ts.createScape();
        ts.execute(Scape.INITIALIZE_RULE);

        Iterator tsIter = ts.iterator();
        LocatedAgent a0 = (LocatedAgent) tsIter.next();
        LocatedAgent a1 = (LocatedAgent) tsIter.next();

        a0.setCoordinate(new Coordinate2DContinuous(1.0, 1.0));
        a1.setCoordinate(new Coordinate2DContinuous(41.0, 31.0));
        assertTrue(ts.calculateDistance(a0, a0) == 0.);
        assertTrue(ts.calculateDistance(a1, a1) == 0.);
        assertTrue(ts.calculateDistance(a0, a1) == 50.0);
        assertTrue(ts.calculateDistance(a1, a0) == 50.0);

        a0.setCoordinate(new Coordinate2DContinuous(41.0, 1.0));
        a1.setCoordinate(new Coordinate2DContinuous(1.0, 31.0));
        assertTrue(ts.calculateDistance(a0, a0) == 0.);
        assertTrue(ts.calculateDistance(a1, a1) == 0.);
        assertTrue(ts.calculateDistance(a0, a1) == 50.0);
        assertTrue(ts.calculateDistance(a1, a0) == 50.0);

        a0.setCoordinate(new Coordinate2DContinuous(1.0, 31.0));
        a1.setCoordinate(new Coordinate2DContinuous(41.0, 1.0));
        assertTrue(ts.calculateDistance(a0, a0) == 0.);
        assertTrue(ts.calculateDistance(a1, a1) == 0.);
        assertTrue(ts.calculateDistance(a0, a1) == 50.0);
        assertTrue(ts.calculateDistance(a1, a0) == 50.0);

        a0.setCoordinate(new Coordinate2DContinuous(41.0, 31.0));
        a1.setCoordinate(new Coordinate2DContinuous(1.0, 1.0));
        assertTrue(ts.calculateDistance(a0, a0) == 0.);
        assertTrue(ts.calculateDistance(a1, a1) == 0.);
        assertTrue(ts.calculateDistance(a0, a1) == 50.0);
        assertTrue(ts.calculateDistance(a1, a0) == 50.0);
    }

    public void testFindWithin() {
        Scape ts = new Scape(new Continuous2D());
        ts.setPrototypeAgent(new LocatedAgent());
        ts.setExtent(5);
        ts.createScape();
        ts.execute(Scape.INITIALIZE_RULE);

        Iterator tsIter = ts.iterator();
        LocatedAgent a0 = (LocatedAgent) tsIter.next();
        LocatedAgent a1 = (LocatedAgent) tsIter.next();
        final LocatedAgent a2 = (LocatedAgent) tsIter.next();
        LocatedAgent a3 = (LocatedAgent) tsIter.next();
        LocatedAgent a4 = (LocatedAgent) tsIter.next();

        a0.setCoordinate(new Coordinate2DContinuous(1.0f, 2.0));
        a1.setCoordinate(new Coordinate2DContinuous(3.6f, 3.0));
        a2.setCoordinate(new Coordinate2DContinuous(3.1f, 3.0));
        a3.setCoordinate(new Coordinate2DContinuous(2.6f, 3.0));
        a4.setCoordinate(new Coordinate2DContinuous(2.6f, 2.9));

        Iterator iter = ts.withinIterator(a2.getCoordinate(), null, true, 0.0);
        assertTrue(iter.hasNext());
        assertTrue(iter.next() == a2);
        assertTrue(!iter.hasNext());

        List t0 = ts.findWithin(a2.getCoordinate(), null, true, 0.0);
        assertTrue(t0.size() == 1);
        assertTrue(t0.get(0) == a2);

        List t1 = ts.findWithin(a2.getCoordinate(), null, true, 0.501);
        assertTrue(t1.size() == 3);
        assertTrue(!t1.contains(a0));
        assertTrue(t1.contains(a1));
        assertTrue(t1.contains(a2));
        assertTrue(t1.contains(a3));
        assertTrue(!t1.contains(a4));

        List t2 = ts.findWithin(a2.getCoordinate(), null, true, 0.53);
        assertTrue(t2.size() == 4);
        assertTrue(!t2.contains(a0));
        assertTrue(t2.contains(a1));
        assertTrue(t2.contains(a2));
        assertTrue(t2.contains(a3));
        assertTrue(t2.contains(a4));

        List t3 = ts.findWithin(a2.getCoordinate(), null, true, 10.0);
        assertTrue(t3.size() == 5);
        assertTrue(t3.contains(a0));
        assertTrue(t3.contains(a1));
        assertTrue(t3.contains(a2));
        assertTrue(t3.contains(a3));
        assertTrue(t3.contains(a4));

        Conditional testConditional = new Conditional() {
            public boolean meetsCondition(Object o) {
                return (((LocatedAgent) o).calculateDistance(a2) > 0.501);
            }
        };

        List t4 = ts.findWithin(a2.getCoordinate(), testConditional, true, 10.0);
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
        ts.setExtent(5);
        ts.createScape();
        ts.execute(Scape.INITIALIZE_RULE);

        Iterator tsIter = ts.iterator();
        LocatedAgent a0 = (LocatedAgent) tsIter.next();
        LocatedAgent a1 = (LocatedAgent) tsIter.next();
        final LocatedAgent a2 = (LocatedAgent) tsIter.next();
        LocatedAgent a3 = (LocatedAgent) tsIter.next();
        LocatedAgent a4 = (LocatedAgent) tsIter.next();

        a0.setCoordinate(new Coordinate2DContinuous(1.0f, 2.0));
        a1.setCoordinate(new Coordinate2DContinuous(3.6f, 3.0));
        a2.setCoordinate(new Coordinate2DContinuous(3.1f, 3.0));
        a3.setCoordinate(new Coordinate2DContinuous(2.6f, 3.0));
        a4.setCoordinate(new Coordinate2DContinuous(2.6f, 2.9));

        Coordinate2DContinuous testCoord1 = new Coordinate2DContinuous(3.6f, 2.96);
        Coordinate2DContinuous testCoord2 = new Coordinate2DContinuous(3.6f, 2.96);

        assertTrue(ts.hasWithin(testCoord1, null, false, 0.05));
        assertTrue(!ts.hasWithin(testCoord2, null, false, 0.02));

        assertTrue(ts.hasWithin(testCoord1, trueCondition, false, 0.05));
        assertTrue(!ts.hasWithin(testCoord1, falseCondition, false, 0.05));

        assertTrue(ts.hasWithin(a0.getCoordinate(), trueCondition, true, 0.05));
        assertTrue(!ts.hasWithin(a0.getCoordinate(), trueCondition, false, 0.05));
        assertTrue(!ts.hasWithin(a0.getCoordinate(), falseCondition, true, 0.05));
        assertTrue(!ts.hasWithin(a0.getCoordinate(), falseCondition, false, 0.05));
    }

    public void testCountWithin() {
        Scape ts = new Scape(new Continuous2D());
        ts.setPrototypeAgent(new LocatedAgent());
        ts.setExtent(5);
        ts.createScape();
        ts.execute(Scape.INITIALIZE_RULE);

        Iterator tsIter = ts.iterator();
        LocatedAgent a0 = (LocatedAgent) tsIter.next();
        LocatedAgent a1 = (LocatedAgent) tsIter.next();
        final LocatedAgent a2 = (LocatedAgent) tsIter.next();
        LocatedAgent a3 = (LocatedAgent) tsIter.next();
        LocatedAgent a4 = (LocatedAgent) tsIter.next();

        a0.setCoordinate(new Coordinate2DContinuous(1.0f, 2.0));
        a1.setCoordinate(new Coordinate2DContinuous(3.6f, 3.0));
        a2.setCoordinate(new Coordinate2DContinuous(3.1f, 3.0));
        a3.setCoordinate(new Coordinate2DContinuous(2.6f, 3.0));
        a4.setCoordinate(new Coordinate2DContinuous(2.6f, 2.9));

        assertTrue(a2.countWithin(null, true, 0.0) == 1);
        assertTrue(a2.countWithin(null, true, 0.501) == 3);
        assertTrue(a2.countWithin(null, true, 0.53) == 4);
        assertTrue(a2.countWithin(null, true, 10.0) == 5);

        assertTrue(a2.countWithin(falseCondition, false, 0.0) == 0);
        assertTrue(a2.countWithin(falseCondition, true, 0.0) == 0);
        assertTrue(a2.countWithin(trueCondition, false, 0.0) == 0);
        assertTrue(a2.countWithin(trueCondition, true, 0.0) == 1);

        assertTrue(a2.countWithin(falseCondition, false, 0.0) == 0);
        assertTrue(a2.countWithin(falseCondition, true, 0.0) == 0);
        assertTrue(a2.countWithin(trueCondition, false, 0.0) == 0);
        assertTrue(a2.countWithin(trueCondition, true, 0.0) == 1);

        assertTrue(a2.countWithin(falseCondition, false, 0.501) == 0);
        assertTrue(a2.countWithin(falseCondition, true, 0.501) == 0);
        assertTrue(a2.countWithin(trueCondition, false, 0.501) == 2);
        assertTrue(a2.countWithin(trueCondition, true, 0.501) == 3);

        assertTrue(a2.countWithin(falseCondition, false, 0.501) == 0);
        assertTrue(a2.countWithin(falseCondition, true, 0.501) == 0);
        assertTrue(a2.countWithin(trueCondition, false, 0.501) == 2);
        assertTrue(a2.countWithin(trueCondition, true, 0.501) == 3);
    }

    public void testFindNearest() {
        Scape ts = new Scape(new Continuous2D());
        ts.setPrototypeAgent(new LocatedAgent());
        ts.setExtent(5);
        ts.createScape();
        ts.execute(Scape.INITIALIZE_RULE);

        Iterator tsIter = ts.iterator();
        LocatedAgent a0 = (LocatedAgent) tsIter.next();
        LocatedAgent a1 = (LocatedAgent) tsIter.next();
        final LocatedAgent a2 = (LocatedAgent) tsIter.next();
        LocatedAgent a3 = (LocatedAgent) tsIter.next();
        LocatedAgent a4 = (LocatedAgent) tsIter.next();

        a0.setCoordinate(new Coordinate2DContinuous(1.0f, 2.0));
        a1.setCoordinate(new Coordinate2DContinuous(3.7f, 3.0));
        a2.setCoordinate(new Coordinate2DContinuous(3.1f, 3.0));
        a3.setCoordinate(new Coordinate2DContinuous(2.6f, 3.0));
        a4.setCoordinate(new Coordinate2DContinuous(2.6f, 2.9));

        assertTrue(a0.findNearest(null) == a4);
        assertTrue(a1.findNearest(null) == a2);
        assertTrue(a2.findNearest(null) == a3);
        assertTrue(a3.findNearest(null) == a4);
        assertTrue(a4.findNearest(null) == a3);
    }

    public void testMoveToward() {
        //Just test up and down left and right
        LocatedAgent origin = new LocatedAgent();

        Coordinate2DContinuous target = new Coordinate2DContinuous(10.0, 10.0);
        origin.setCoordinate(new Coordinate2DContinuous(20.0, 10.0));
        Scape testScape = new Scape(new Continuous2D());
        testScape.setExtent(new Coordinate2DContinuous(50.0, 50.0));
        testScape.createScape();
        testScape.add(origin);
        testScape.moveToward(origin, target, 5.0);
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getXValue(), 15.0));
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getYValue(), 10.0));

        target = new Coordinate2DContinuous(10.0, 10.0);
        origin.setCoordinate(new Coordinate2DContinuous(10.0, 20.0));
        testScape = new Scape(new Continuous2D());
        testScape.setExtent(new Coordinate2DContinuous(50.0, 50.0));
        testScape.createScape();
        testScape.moveToward(origin, target, 5.0);
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getXValue(), 10.0));
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getYValue(), 15.0));

        target = new Coordinate2DContinuous(0.0, 10.0);
        origin.setCoordinate(new Coordinate2DContinuous(10.0, 10.0));
        testScape = new Scape(new Continuous2D());
        testScape.setExtent(new Coordinate2DContinuous(50.0, 50.0));
        testScape.createScape();
        testScape.moveToward(origin, target, 5.0);
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getXValue(), 5.0));
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getYValue(), 10.0));

        target = new Coordinate2DContinuous(10.0, 0.0);
        origin.setCoordinate(new Coordinate2DContinuous(10.0, 10.0));
        testScape = new Scape(new Continuous2D());
        testScape.setExtent(new Coordinate2DContinuous(50.0, 50.0));
        testScape.createScape();
        testScape.moveToward(origin, target, 5.0);
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getXValue(), 10.0));
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getYValue(), 5.0));

        //Four diaganol directions
        origin.setCoordinate(new Coordinate2DContinuous(11.0, 11.0));
        target = new Coordinate2DContinuous(14.0, 15.0);
        testScape = new Scape(new Continuous2D());
        testScape.setExtent(new Coordinate2DContinuous(50.0, 50.0));
        testScape.createScape();
        testScape.moveToward(origin, target, 5.0);
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getXValue(), 14.0));
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getYValue(), 15.0));

        origin.setCoordinate(new Coordinate2DContinuous(11.0, 11.0));
        target = new Coordinate2DContinuous(15.0, 14.0);
        testScape = new Scape(new Continuous2D());
        testScape.setExtent(new Coordinate2DContinuous(50.0, 50.0));
        testScape.createScape();
        testScape.moveToward(origin, target, 5.0);
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getXValue(), 15.0));
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getYValue(), 14.0));

        origin.setCoordinate(new Coordinate2DContinuous(11.0, 11.0));
        target = new Coordinate2DContinuous(7.0, 8.0);
        testScape = new Scape(new Continuous2D());
        testScape.setExtent(new Coordinate2DContinuous(50.0, 50.0));
        testScape.createScape();
        testScape.moveToward(origin, target, 5.0);
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getXValue(), 7.0));
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getYValue(), 8.0));

        origin.setCoordinate(new Coordinate2DContinuous(11.0, 11.0));
        target = new Coordinate2DContinuous(8.0, 7.0);
        testScape = new Scape(new Continuous2D());
        testScape.setExtent(new Coordinate2DContinuous(50.0, 50.0));
        testScape.createScape();
        testScape.moveToward(origin, target, 5.0);
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getXValue(), 8.0));
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getYValue(), 7.0));

        //Test Overshooting
        origin.setCoordinate(new Coordinate2DContinuous(11.0, 11.0));
        target = new Coordinate2DContinuous(14.0, 15.0);
        testScape = new Scape(new Continuous2D());
        testScape.setExtent(new Coordinate2DContinuous(50.0, 50.0));
        testScape.createScape();
        testScape.moveToward(origin, target, 15.0);
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getXValue(), 14.0));
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getYValue(), 15.0));

        origin.setCoordinate(new Coordinate2DContinuous(11.0, 11.0));
        target = new Coordinate2DContinuous(15.0, 14.0);
        testScape = new Scape(new Continuous2D());
        testScape.setExtent(new Coordinate2DContinuous(50.0, 50.0));
        testScape.createScape();
        testScape.moveToward(origin, target, 15.0);
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getXValue(), 15.0));
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getYValue(), 14.0));

        origin.setCoordinate(new Coordinate2DContinuous(11.0, 11.0));
        target = new Coordinate2DContinuous(7.0, 8.0);
        testScape = new Scape(new Continuous2D());
        testScape.setExtent(new Coordinate2DContinuous(50.0, 50.0));
        testScape.createScape();
        testScape.moveToward(origin, target, 15.0);
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getXValue(), 7.0));
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getYValue(), 8.0));

        origin.setCoordinate(new Coordinate2DContinuous(11.0, 11.0));
        target = new Coordinate2DContinuous(8.0, 7.0);
        testScape = new Scape(new Continuous2D());
        testScape.setExtent(new Coordinate2DContinuous(50.0, 50.0));
        testScape.createScape();
        testScape.moveToward(origin, target, 15.0);
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getXValue(), 8.0));
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getYValue(), 7.0));
    }

    public void testMoveTowardPeriodic() {

        //Four diaganol directions
        LocatedAgent origin = new LocatedAgent();
        origin.setCoordinate(new Coordinate2DContinuous(1.0, 1.0));
        Coordinate2DContinuous target = new Coordinate2DContinuous(47.0, 48.0);
        Scape testScape = new Scape(new Continuous2D());
        testScape.setExtent(new Coordinate2DContinuous(50.0, 50.0));
        testScape.getSpace().setPeriodic(true);
        testScape.createScape();
        testScape.add(origin);
        testScape.moveToward(origin, target, 5.0);
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getXValue(), 47.0));
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getYValue(), 48.0));

        origin.setCoordinate(new Coordinate2DContinuous(47.0, 48.0));
        target = new Coordinate2DContinuous(1.0, 1.0);
        testScape = new Scape(new Continuous2D());
        testScape.setExtent(new Coordinate2DContinuous(50.0, 50.0));
        testScape.getSpace().setPeriodic(true);
        testScape.createScape();
        testScape.moveToward(origin, target, 5.0);
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getXValue(), 1.0));
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getYValue(), 1.0));

        origin.setCoordinate(new Coordinate2DContinuous(1.0, 48.0));
        target = new Coordinate2DContinuous(47.0, 1.0);
        testScape = new Scape(new Continuous2D());
        testScape.setExtent(new Coordinate2DContinuous(50.0, 50.0));
        testScape.getSpace().setPeriodic(true);
        testScape.createScape();
        testScape.moveToward(origin, target, 5.0);
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getXValue(), 47.0));
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getYValue(), 1.0));

        origin.setCoordinate(new Coordinate2DContinuous(47.0, 1.0));
        target = new Coordinate2DContinuous(1.0, 48.0);
        testScape = new Scape(new Continuous2D());
        testScape.setExtent(new Coordinate2DContinuous(50.0, 50.0));
        testScape.getSpace().setPeriodic(true);
        testScape.createScape();
        testScape.moveToward(origin, target, 5.0);
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getXValue(), 1.0));
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getYValue(), 48.0));

        //Check overrun
        origin.setCoordinate(new Coordinate2DContinuous(1.0, 1.0));
        target = new Coordinate2DContinuous(47.0, 48.0);
        testScape = new Scape(new Continuous2D());
        testScape.setExtent(new Coordinate2DContinuous(50.0, 50.0));
        testScape.getSpace().setPeriodic(true);
        testScape.createScape();
        testScape.moveToward(origin, target, 15.0);
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getXValue(), 47.0));
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getYValue(), 48.0));

        origin.setCoordinate(new Coordinate2DContinuous(47.0, 48.0));
        target = new Coordinate2DContinuous(1.0, 1.0);
        testScape = new Scape(new Continuous2D());
        testScape.setExtent(new Coordinate2DContinuous(50.0, 50.0));
        testScape.getSpace().setPeriodic(true);
        testScape.createScape();
        testScape.moveToward(origin, target, 15.0);
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getXValue(), 1.0));
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getYValue(), 1.0));

        origin.setCoordinate(new Coordinate2DContinuous(1.0, 48.0));
        target = new Coordinate2DContinuous(47.0, 1.0);
        testScape = new Scape(new Continuous2D());
        testScape.setExtent(new Coordinate2DContinuous(50.0, 50.0));
        testScape.getSpace().setPeriodic(true);
        testScape.createScape();
        testScape.moveToward(origin, target, 15.0);
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getXValue(), 47.0));
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getYValue(), 1.0));

        origin.setCoordinate(new Coordinate2DContinuous(47.0, 1.0));
        target = new Coordinate2DContinuous(1.0, 48.0);
        testScape = new Scape(new Continuous2D());
        testScape.setExtent(new Coordinate2DContinuous(50.0, 50.0));
        testScape.getSpace().setPeriodic(true);
        testScape.createScape();
        testScape.moveToward(origin, target, 15.0);
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getXValue(), 1.0));
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getYValue(), 48.0));
    }

    public void testMoveTowardNonPeriodic() {

        //Four diaganol directions
        LocatedAgent origin = new LocatedAgent();
        origin.setCoordinate(new Coordinate2DContinuous(1.0, 1.0));
        Coordinate2DContinuous target = new Coordinate2DContinuous(31.0, 41.0);
        Scape testScape = new Scape(new Continuous2D());
        testScape.setExtent(new Coordinate2DContinuous(50.0, 50.0));
        testScape.getSpace().setPeriodic(false);
        testScape.createScape();
        testScape.add(origin);
        testScape.moveToward(origin, target, 5.0);
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getXValue(), 4.0));
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getYValue(), 5.0));

        origin.setCoordinate(new Coordinate2DContinuous(1.0, 41.0));
        target = new Coordinate2DContinuous(31.0, 1.0);
        testScape = new Scape(new Continuous2D());
        testScape.setExtent(new Coordinate2DContinuous(50.0, 50.0));
        testScape.getSpace().setPeriodic(false);
        testScape.createScape();
        testScape.moveToward(origin, target, 5.0);
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getXValue(), 4.0));
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getYValue(), 37.0));

        origin.setCoordinate(new Coordinate2DContinuous(31.0, 41.0));
        target = new Coordinate2DContinuous(1.0, 1.0);
        testScape = new Scape(new Continuous2D());
        testScape.setExtent(new Coordinate2DContinuous(50.0, 50.0));
        testScape.getSpace().setPeriodic(false);
        testScape.createScape();
        testScape.moveToward(origin, target, 5.0);
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getXValue(), 28.0));
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getYValue(), 37.0));

        origin.setCoordinate(new Coordinate2DContinuous(31.0, 1.0));
        target = new Coordinate2DContinuous(1.0, 41.0);
        testScape = new Scape(new Continuous2D());
        testScape.setExtent(new Coordinate2DContinuous(50.0, 50.0));
        testScape.getSpace().setPeriodic(false);
        testScape.createScape();
        testScape.moveToward(origin, target, 5.0);
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getXValue(), 28.0));
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getYValue(), 5.0));
    }

    public void testMoveAway() {

        //Four diaganol directions
        LocatedAgent origin = new LocatedAgent();
        origin.setCoordinate(new Coordinate2DContinuous(11.0, 11.0));
        Coordinate2DContinuous target = new Coordinate2DContinuous(14.0, 15.0);
        Scape testScape = new Scape(new Continuous2D());
        testScape.setExtent(new Coordinate2DContinuous(50.0, 50.0));
        testScape.createScape();
        testScape.add(origin);
        testScape.moveAway(origin, target, 5.0);
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getXValue(), 8.0));
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getYValue(), 7.0));

        origin.setCoordinate(new Coordinate2DContinuous(11.0, 11.0));
        target = new Coordinate2DContinuous(15.0, 14.0);
        testScape = new Scape(new Continuous2D());
        testScape.setExtent(new Coordinate2DContinuous(50.0, 50.0));
        testScape.createScape();
        testScape.moveAway(origin, target, 5.0);
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getXValue(), 7.0));
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getYValue(), 8.0));

        origin.setCoordinate(new Coordinate2DContinuous(11.0, 11.0));
        target = new Coordinate2DContinuous(7.0, 8.0);
        testScape = new Scape(new Continuous2D());
        testScape.setExtent(new Coordinate2DContinuous(50.0, 50.0));
        testScape.createScape();
        testScape.moveAway(origin, target, 5.0);
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getXValue(), 15.0));
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getYValue(), 14.0));

        origin.setCoordinate(new Coordinate2DContinuous(11.0, 11.0));
        target = new Coordinate2DContinuous(8.0, 7.0);
        testScape = new Scape(new Continuous2D());
        testScape.setExtent(new Coordinate2DContinuous(50.0, 50.0));
        testScape.createScape();
        testScape.moveAway(origin, target, 5.0);
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getXValue(), 14.0));
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getYValue(), 15.0));
    }

    public void testMoveAwayPeriodic() {

        //Four diaganol directions
        LocatedAgent origin = new LocatedAgent();
        origin.setCoordinate(new Coordinate2DContinuous(1.0, 1.0));
        Coordinate2DContinuous target = new Coordinate2DContinuous(4.0, 5.0);
        Scape testScape = new Scape(new Continuous2D());
        testScape.setExtent(new Coordinate2DContinuous(50.0, 50.0));
        testScape.getSpace().setPeriodic(true);
        testScape.createScape();
        testScape.add(origin);
        testScape.moveAway(origin, target, 5.0);
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getXValue(), 48.0));
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getYValue(), 47.0));

        origin.setCoordinate(new Coordinate2DContinuous(4.0, 1.0));
        target = new Coordinate2DContinuous(1.0, 5.0);
        testScape = new Scape(new Continuous2D());
        testScape.setExtent(new Coordinate2DContinuous(50.0, 50.0));
        testScape.getSpace().setPeriodic(true);
        testScape.createScape();
        testScape.moveAway(origin, target, 5.0);
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getXValue(), 7.0));
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getYValue(), 47.0));

        origin.setCoordinate(new Coordinate2DContinuous(4.0, 5.0));
        target = new Coordinate2DContinuous(1.0, 1.0);
        testScape = new Scape(new Continuous2D());
        testScape.setExtent(new Coordinate2DContinuous(50.0, 50.0));
        testScape.getSpace().setPeriodic(true);
        testScape.createScape();
        testScape.moveAway(origin, target, 5.0);
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getXValue(), 7.0));
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getYValue(), 9.0));

        origin.setCoordinate(new Coordinate2DContinuous(1.0, 5.0));
        target = new Coordinate2DContinuous(4.0, 1.0);
        testScape = new Scape(new Continuous2D());
        testScape.setExtent(new Coordinate2DContinuous(50.0, 50.0));
        testScape.getSpace().setPeriodic(true);
        testScape.createScape();
        testScape.moveAway(origin, target, 5.0);
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getXValue(), 48.0));
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getYValue(), 9.0));
    }

    public void testMoveAwayNonPeriodic() {

        //Four diaganol directions
        LocatedAgent origin = new LocatedAgent();
        origin.setCoordinate(new Coordinate2DContinuous(1.0, 1.0));
        Coordinate2DContinuous target = new Coordinate2DContinuous(4.0, 5.0);
        Scape testScape = new Scape(new Continuous2D());
        testScape.setExtent(new Coordinate2DContinuous(50.0, 50.0));
        testScape.getSpace().setPeriodic(false);
        testScape.createScape();
        testScape.add(origin);
        testScape.moveAway(origin, target, 5.0);
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getXValue(), 0.0));
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getYValue(), 0.0));

        origin.setCoordinate(new Coordinate2DContinuous(4.0, 1.0));
        target = new Coordinate2DContinuous(1.0, 5.0);
        testScape = new Scape(new Continuous2D());
        testScape.setExtent(new Coordinate2DContinuous(50.0, 50.0));
        testScape.getSpace().setPeriodic(false);
        testScape.createScape();
        testScape.moveAway(origin, target, 5.0);
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getXValue(), 7.0));
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getYValue(), 0.0));

        origin.setCoordinate(new Coordinate2DContinuous(4.0, 5.0));
        target = new Coordinate2DContinuous(1.0, 1.0);
        testScape = new Scape(new Continuous2D());
        testScape.setExtent(new Coordinate2DContinuous(50.0, 50.0));
        testScape.getSpace().setPeriodic(false);
        testScape.createScape();
        testScape.moveAway(origin, target, 5.0);
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getXValue(), 7.0));
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getYValue(), 9.0));

        origin.setCoordinate(new Coordinate2DContinuous(1.0, 5.0));
        target = new Coordinate2DContinuous(4.0, 1.0);
        testScape = new Scape(new Continuous2D());
        testScape.setExtent(new Coordinate2DContinuous(50.0, 50.0));
        testScape.getSpace().setPeriodic(false);
        testScape.createScape();
        testScape.moveAway(origin, target, 5.0);
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getXValue(), 0.0));
        assertTrue(org.ascape.util.data.DataPointConcrete.equals(((Coordinate2DContinuous) origin.getCoordinate()).getYValue(), 9.0));
    }
}