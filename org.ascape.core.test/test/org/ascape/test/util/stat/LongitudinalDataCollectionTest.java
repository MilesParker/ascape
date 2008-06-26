/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.test.util.stat;

import junit.framework.TestCase;

import org.ascape.model.Scape;
import org.ascape.model.space.Singleton;
import org.ascape.util.data.DataSeries;
import org.ascape.util.data.StatCollector;
import org.ascape.util.data.StatCollectorCSAMM;

public class LongitudinalDataCollectionTest extends TestCase {

    public LongitudinalDataCollectionTest(String name) {
        super(name);
    }

    public void testStatCollectorLongitudinalDataMode() {
        Scape testScape = new Scape(new Singleton());
        StatCollectorCSAMM stat = new StatCollectorCSAMM();
        testScape.createScape();
        testScape.addStatCollector(stat);

        stat.setCollectingLongitudinalDataMode(StatCollector.NOT_COLLECTING);
        assertTrue(stat.isCollectingLongitudinalData() == false);

        stat.setCollectingLongitudinalDataMode(StatCollector.COLLECTING);
        assertTrue(stat.isCollectingLongitudinalData() == true);

        stat.setCollectingLongitudinalDataMode(StatCollector.SET_BY_DATAGROUP);
        assertTrue(stat.isCollectingLongitudinalData() == true);
    }


    public void testCOLLECTING() {
        Scape testScape = new Scape(new Singleton());
        StatCollectorCSAMM stat = new StatCollectorCSAMM();
        stat.setCollectingLongitudinalDataMode(StatCollector.COLLECTING);
        testScape.createScape();
        testScape.addStatCollector(stat);

        for (int i = 0; i < testScape.getRunner().getData().getSize(); i++) {
            DataSeries ds = testScape.getRunner().getData().getSeries(i);
            assertTrue(ds.isCollecting() == true);
        }
    }

    public void testNOT_COLLECTING() {
        Scape testScape = new Scape(new Singleton());
        StatCollectorCSAMM stat = new StatCollectorCSAMM();
        stat.setCollectingLongitudinalDataMode(StatCollector.NOT_COLLECTING);
        testScape.createScape();
        testScape.addStatCollector(stat);

        for (int i = 0; i < testScape.getRunner().getData().getSize(); i++) {
            DataSeries ds = testScape.getRunner().getData().getSeries(i);
            assertTrue(ds.isCollecting() == false);
        }
    }

    public void testSET_BY_DATAGROUP() {
        Scape testScape = new Scape(new Singleton());
        StatCollectorCSAMM stat = new StatCollectorCSAMM();
        stat.setCollectingLongitudinalDataMode(StatCollector.SET_BY_DATAGROUP);
        testScape.createScape();
        testScape.addStatCollector(stat);

        for (int i = 0; i < testScape.getRunner().getData().getSize(); i++) {
            DataSeries ds = testScape.getRunner().getData().getSeries(i);
            assertTrue(ds.isCollecting() == true);
        }
    }
}
