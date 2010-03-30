/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */

package org.ascape.test.model.space;

import java.util.List;

import org.ascape.gis.model.Globe;
import org.ascape.gis.model.MapAgent;
import org.ascape.gis.model.MapCoordinate;
import org.ascape.model.Scape;
import org.ascape.util.Conditional;

import junit.framework.TestCase;


public class GlobeTest extends TestCase {

    public GlobeTest(String name) {
        super(name);
    }

    public void testFindWithin() {
        Scape globeScape = new Scape();
        Globe map = new Globe();
        globeScape.setSpace(map);
        final MapAgent agent1 = new MapAgent();
        MapAgent agent2 = new MapAgent();
        MapAgent agent3 = new MapAgent();

        globeScape.add(agent1);
        globeScape.add(agent2);
        globeScape.add(agent3);

        agent1.setCoordinate(new MapCoordinate(10f, 10f));
        agent2.setCoordinate(new MapCoordinate(20f, 20f));
        agent3.setCoordinate(new MapCoordinate(50f, 50f));

        // found by map.calculateDistance(agent, agent);
        // dist b/w 1 and 2 is 834.2806
        // dist b/w 1 and 3 is 3112.7869
        // dist b/w 2 and 3 is 2298.2344

        Conditional dontIncludeSelf = new Conditional() {
            public boolean meetsCondition(Object o) {
                return agent1 != o;
            }
        };

        List found = agent1.findWithin(dontIncludeSelf, 1000);
        assertTrue(found.size() == 1); // including agent1.
        assertTrue((MapAgent) found.get(0) == agent2);
        assertTrue(map.calculateDistance(agent1, agent2) < 1000);

        found = agent1.findWithin(dontIncludeSelf, 3500);
        assertTrue(found.size() == 2); // including agent1.
        assertTrue(map.calculateDistance(agent1, agent2) < 3500);
        assertTrue(map.calculateDistance(agent1, agent3) < 3500);
    }

}