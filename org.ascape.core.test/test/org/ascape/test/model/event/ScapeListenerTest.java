/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.test.model.event;

import java.util.ArrayList;
import java.util.Iterator;

import junit.framework.TestCase;

import org.ascape.model.Agent;
import org.ascape.model.Cell;
import org.ascape.model.Scape;
import org.ascape.model.event.ScapeEvent;
import org.ascape.model.rule.Rule;
import org.ascape.model.space.SpatialTemporalException;
import org.ascape.runtime.Runner;
import org.ascape.view.nonvis.NonGraphicView;

public class ScapeListenerTest extends TestCase {

    public ScapeListenerTest(String name) {
        super(name);
    }

    class TestView extends NonGraphicView {

        public int accessed;

        public void scapeIterated(ScapeEvent scapeEvent) {
            accessed++;
            assertTrue(accessed == ((Scape) scapeEvent.getSource()).getPeriod());
        }

        public void scapeNotification(ScapeEvent scapeEvent) {
            super.scapeNotification(scapeEvent);
            (new Thread() {
                public void run() {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                    }
                    notifyScapeUpdated();
                }
            }).start();
        }
    }

    /**
     * Tests wether the scape exceution and view updating mechanism works properly.
     * Viws should be able to update asynchronously, and the scape should wait for all of those updates before proceeding.
     * This test measures that.
     */
    public void testScapeWaitsForListenerUpdate() {
        final Scape testScape = new Scape();
        Scape root = new Scape() {
            public void createViews() {
                //No views for clean test output....
                super.createViews();
            }

            public void createScape() {
                super.createScape();
                testScape.setPrototypeAgent(new Cell());
                testScape.setExtent(5);
                add(testScape);
            }
        };
        Runner.setDisplayGraphics(false);
        final TestView[] views = new TestView[5];
        for (int i = 0; i < views.length; i++) {
            views[i] = new TestView();
            views[i].setNotifyScapeAutomatically(false);
        }
        root.addViews(views);
        root.addRule(new Rule("Test Views") {
            public void execute(Agent a) {
                ArrayList listeners = ((Scape) a).getScapeListeners();
                for (Iterator iterator = listeners.iterator(); iterator.hasNext();) {
                    TestView v = (TestView) iterator.next();
                    if (((Scape) a).getPeriod() > 0) {
                        assertTrue(v.accessed == ((Scape) a).getPeriod() - 1);
                    }
                }
            }
        });
        root.setStartOnOpen(false);
        root.createScape();
        root.getRunner().open();
        try {
            root.setStopPeriod(10);
        } catch (SpatialTemporalException e) {
            throw new RuntimeException("Bad stop period: " + e);
        }
        root.setAutoRestart(false);
        root.getRunner().run();
        assertTrue(testScape.getPeriod() == 10);
        for (int i = 0; i < views.length; i++) {
            assertTrue(views[i].accessed == 10);
        }
    }
}
