/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.test.rule;

import junit.framework.TestCase;

import org.ascape.model.Agent;
import org.ascape.model.Cell;
import org.ascape.model.Scape;
import org.ascape.model.rule.Propogate;
import org.ascape.model.rule.PropogateScapeOnly;
import org.ascape.model.rule.Rule;
import org.ascape.model.space.Singleton;

public class PropogateTest extends TestCase {

    public PropogateTest(String name) {
        super(name);
    }

    interface Touchable {

        public void touch();

        public int touchCount();
    }

    class TestScape extends Scape implements Touchable {

        int touched;

        public int touchCount() {
            return touched;
        }

        public void touch() {
            touched++;
        }
    }

    class TestAgent extends Cell implements Touchable {

        int touched;

        public int touchCount() {
            return touched;
        }

        public void touch() {
            touched++;
        }
    }

    class TestAgentScape extends Scape implements Touchable {

        int touched;

        public TestAgentScape() {
            super(new Singleton());
        }

        public int touchCount() {
            return touched;
        }

        public void touch() {
            touched++;
        }
    }

    Rule PROPOGATE_RULE = new Propogate("Propogate Test") {
        public void execute(Agent a) {
            ((Touchable) a).touch();
            super.execute(a);
        }
    };

    Rule PROPOGATE_SCAPE_ONLY_RULE = new PropogateScapeOnly("Propogate Scape-Only Test") {
        public void execute(Agent a) {
            ((Touchable) a).touch();
            super.execute(a);
        }
    };

    public void testPropogate1() {
        TestScape myScape = new TestScape();
        myScape.add(new TestScape());
        myScape.add(new TestScape());
        myScape.add(new TestScape());

        myScape.execute(PROPOGATE_RULE);

        assertTrue(myScape.touchCount() == 1);
        assertTrue(((Touchable) myScape.get(0)).touchCount() == 1);
        assertTrue(((Touchable) myScape.get(1)).touchCount() == 1);
        assertTrue(((Touchable) myScape.get(2)).touchCount() == 1);
    }

    public void testPropogate2() {
        TestScape myScape = new TestScape();
        myScape.add(new TestAgent());
        myScape.add(new TestAgent());
        myScape.add(new TestAgent());

        myScape.execute(PROPOGATE_RULE);

        assertTrue(myScape.touchCount() == 1);
        assertTrue(((Touchable) myScape.get(0)).touchCount() == 1);
        assertTrue(((Touchable) myScape.get(1)).touchCount() == 1);
        assertTrue(((Touchable) myScape.get(2)).touchCount() == 1);
    }

    public void testPropogate3() {
        TestScape myScape = new TestScape();
        TestScape subScape = new TestScape();

        myScape.add(subScape);
        subScape.add(new TestAgent());
        subScape.add(new TestAgent());

        myScape.add(new TestAgent());
        myScape.add(new TestAgent());

        myScape.execute(PROPOGATE_RULE);

        assertTrue(myScape.touchCount() == 1);
        assertTrue(((Touchable) myScape.get(0)).touchCount() == 1);
        assertTrue(((Touchable) subScape.get(0)).touchCount() == 1);
        assertTrue(((Touchable) subScape.get(1)).touchCount() == 1);
        assertTrue(((Touchable) myScape.get(1)).touchCount() == 1);
        assertTrue(((Touchable) myScape.get(2)).touchCount() == 1);
    }


    public void testPropogateScapeOnly1() {
        TestScape myScape = new TestScape();
        myScape.createScape();
        myScape.add(new TestScape());
        myScape.add(new TestScape());
        myScape.add(new TestScape());

        myScape.execute(PROPOGATE_SCAPE_ONLY_RULE);

        assertTrue(myScape.touchCount() == 1);
        assertTrue(((Touchable) myScape.get(0)).touchCount() == 1);
        assertTrue(((Touchable) myScape.get(1)).touchCount() == 1);
        assertTrue(((Touchable) myScape.get(2)).touchCount() == 1);
    }

    public void testPropogateScapeOnly2() {
        TestScape myScape = new TestScape();
        myScape.add(new TestAgent());
        myScape.add(new TestAgent());
        myScape.add(new TestAgent());

        myScape.execute(PROPOGATE_SCAPE_ONLY_RULE);

        assertTrue(myScape.touchCount() == 1);
        assertTrue(((Touchable) myScape.get(0)).touchCount() == 0);
        assertTrue(((Touchable) myScape.get(1)).touchCount() == 0);
        assertTrue(((Touchable) myScape.get(2)).touchCount() == 0);
    }

    public void testPropogateScapeOnly3() {
        TestScape myScape = new TestScape();
        myScape.setPrototypeAgent(new Scape());
        TestScape subScape = new TestScape();
        subScape.setPrototypeAgent(new Scape());

        myScape.add(subScape);
        subScape.add(new TestScape());
        subScape.add(new TestScape());

        myScape.add(new TestScape());
        myScape.add(new TestScape());

        myScape.execute(PROPOGATE_SCAPE_ONLY_RULE);

        assertTrue(myScape.touchCount() == 1);
        assertTrue(((Touchable) myScape.get(0)).touchCount() == 1);
        assertTrue(((Touchable) subScape.get(0)).touchCount() == 1);
        assertTrue(((Touchable) subScape.get(1)).touchCount() == 1);
        assertTrue(((Touchable) myScape.get(1)).touchCount() == 1);
        assertTrue(((Touchable) myScape.get(2)).touchCount() == 1);
    }

    public void testAgentScape() {
        TestScape myScape = new TestScape();
        myScape.createScape();

        myScape.add(new TestScape());
        myScape.add(new TestScape());
        myScape.add(new TestAgentScape());

        myScape.execute(PROPOGATE_SCAPE_ONLY_RULE);

        assertTrue(myScape.touchCount() == 1);
        assertTrue(((Touchable) myScape.get(0)).touchCount() == 1);
        assertTrue(((Touchable) myScape.get(1)).touchCount() == 1);
        assertTrue(((Touchable) myScape.get(2)).touchCount() == 1);
    }
}
