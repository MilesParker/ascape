package org.ascape.test.query;

import junit.framework.TestSuite;
import junit.framework.Test;

/**

 * User: jmiller
 * Date: Dec 8, 2004
 * Time: 2:24:35 PM
 * To change this template use Options | File Templates.
 */
public class AllQuery extends TestSuite {

    public AllQuery(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Ascape Query");
        suite.addTest(new TestSuite(org.ascape.test.query.QueryTest.class));

        return suite;
    }
}
