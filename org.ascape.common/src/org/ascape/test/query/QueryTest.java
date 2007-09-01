package org.ascape.test.query;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.ascape.model.Scape;
import org.ascape.query.Query;
import org.ascape.query.parser.ParseException;

/**

 * User: jmiller
 * Date: Dec 13, 2004
 * Time: 12:17:37 PM
 * To change this template use Options | File Templates.
 */
public class QueryTest extends TestCase {

    public QueryTest(String s) {
        super(s);
    }

//    public void testListener() {
//        setUp();
////        Query q = new Query(collection, "testStateInt", "=", new Integer(15));
//        Query q = null;
//        try {
//            q = new Query(collection, "testStateInt=15");
//        } catch (ParseException e) {
//            fail(e.toString());  //To change body of catch statement use Options | File Templates.
//        }
//        q.execute();
//        q.setDynamicSearch(true);
//        assertTrue(q.getResults().size() == 0);
//
////        collection.execute(new NotifyViews(ScapeEvent.REPORT_ITERATE));
////        assertTrue(q.getResults().size() == 1);
//    }

    Scape collection;

    protected void setUp() {
        collection = new Scape();
        collection.setName("QueryTest collection");
        collection.setExtent(10);
        TestAgent proto = new TestAgent();
        proto.initialize();
        collection.setPrototypeAgent(proto);
        collection.createScape();
        collection.initialize();
    }

    public void testValidate() {
        setUp();

        constructQuery(true, "integerVal=15");

        constructQuery(true, "integerVal=15 OR (stringVal=blue OR (doubleVal>5.0 AND booleanVal=true))");

        constructQuery(true, "(integerVal=15 OR (stringVal=blue OR (doubleVal>5.0 AND booleanVal=true)))");

        constructQuery(true, "(integerObj=15 OR (stringVal=blue OR (doubleObj>5.0 AND booleanObj=true)))");

        //Should fail, unrecognized field
        constructQuery(false, "(genre=classical AND (field2=value2 OR field3=val3))");//"(integerVal=15 OR (stringVal=blue OR (testStateFoo>5.0 AND booleanVal=true)))");

        //Should fail, wrong type for operation (double can't use contains)
        constructQuery(false, "(integerVal=15 OR (stringVal contains blue OR (doubleVal contains 5.0 AND booleanVal=true)))");

        //Should fail, wrong type for operation (boolean can't use greater than)
        constructQuery(false, "(integerVal=15 OR (stringVal contains blue OR (doubleVal=5.0 AND booleanVal>true)))");

        //Should fail, bad number format (15.5 for int)
        constructQuery(false, "(integerVal=15.5 OR (stringVal contains blue OR (doubleVal=5.0 AND booleanVal=true)))");

        //Should fail, bad number format (fifteen for double)
        constructQuery(false, "(integerVal=15 OR (stringVal contains blue OR (doubleVal=fifteen AND booleanVal=true)))");

        //Should fail, bad number format (maybe for boolean)
        constructQuery(false, "(integerVal=15 OR (stringVal contains blue OR (doubleVal=5.0 AND booleanVal=maybe)))");

        //Should fail, bad number format (15.5 for intObj)
        constructQuery(false, "(integerObj=15.5 OR (stringVal=blue OR (doubleObj>5.0 AND booleanObj=true)))");

        //Should fail, bad number format (fifteen for doubleObj)
        constructQuery(false, "(integerObj=15 OR (stringVal=blue OR (doubleObj>fifteen AND booleanObj=true)))");

        //Should fail, bad number format (maybe for booleanObj)
        constructQuery(false, "(integerObj=15 OR (stringVal=blue OR (doubleObj>5.0 AND booleanObj=maybe)))");


        constructQuery(true, "testObj contains test");


        constructQuery(true, "member.member.member.integerObj = 15");

        //no such object
        constructQuery(false, "member.member.member.member.member.integerObj = 15");

// No support for wildcards (yet)
//        constructQuery(true, "* = 15");
//        constructQuery(true, "member.member.* = 15");
//        constructQuery(false, "member.*.* = 15");
    }

    interface TypeTester {
        public void setValue(TestAgent a, double val);
        public String getFieldName();
    }

    TypeTester SET_INTEGER_VAL = new TypeTester() {
        public void setValue(TestAgent a, double val) {
            a.setIntegerVal((int) val);
        }
        public String getFieldName() {
            return "integerVal";
        }
    };

    TypeTester SET_INTEGER_OBJ = new TypeTester() {
        public void setValue(TestAgent a, double val) {
            a.setIntegerObj(new Integer((int) val));
        }
        public String getFieldName() {
            return "integerObj";
        }
    };

    TypeTester SET_DOUBLE_VAL = new TypeTester() {
        public void setValue(TestAgent a, double val) {
            a.setDoubleVal(val);
        }

        public String getFieldName() {
            return "doubleVal";
        }
    };

    TypeTester SET_DOUBLE_OBJ = new TypeTester() {
        public void setValue(TestAgent a, double val) {
            a.setDoubleObj(new Double((int) val));
        }

        public String getFieldName() {
            return "doubleObj";
        }
    };

    TypeTester SET_FLOAT_VAL = new TypeTester() {
        public void setValue(TestAgent a, double val) {
            a.setFloatVal((float) val);
        }

        public String getFieldName() {
            return "floatVal";
        }
    };

    TypeTester SET_FLOAT_OBJ = new TypeTester() {
        public void setValue(TestAgent a, double val) {
            a.setFloatObj(new Float((float) val));
        }

        public String getFieldName() {
            return "floatObj";
        }
    };

    TypeTester SET_LONG_VAL = new TypeTester() {
        public void setValue(TestAgent a, double val) {
            a.setLongVal((long) val);
        }

        public String getFieldName() {
            return "longVal";
        }
    };

    TypeTester SET_LONG_OBJ = new TypeTester() {
        public void setValue(TestAgent a, double val) {
            a.setLongObj(new Long((long) val));
        }

        public String getFieldName() {
            return "longObj";
        }
    };

    TypeTester[] numberTesters = {SET_INTEGER_VAL, SET_INTEGER_OBJ, SET_DOUBLE_VAL, SET_DOUBLE_OBJ, SET_FLOAT_VAL, SET_FLOAT_OBJ, SET_LONG_VAL, SET_LONG_OBJ};

    public void testNumberOps() {
        setUp();
        for (int i = 0; i < numberTesters.length; i++) {
            TypeTester numberTester = numberTesters[i];
            executeTestForNumberType(numberTester);
        }
    }

    public void testDotSyntax() {
        setUp();
        TestAgent a3 = (TestAgent) collection.get(3);
        for (int i = 0; i < numberTesters.length; i++) {
            TypeTester numberTester = numberTesters[i];
            numberTester.setValue(a3, 10);
        }
        a3.getMember().getMember().setStringVal("a bunch of characters");

        List res = executeQuery("member.member.stringVal=\"a bunch of characters\"");
        assertTrue(res.size() == 1);
        assertTrue(res.get(0) == a3);
    }

    public void testObjectOps() {
        setUp();
        TestAgent a3 = (TestAgent) collection.get(3);
        for (int i = 0; i < numberTesters.length; i++) {
            TypeTester numberTester = numberTesters[i];
            numberTester.setValue(a3, 10);
        }
        a3.setTestObj(new Object() {
            public String toString() {
                return "My test object";
            }
        });

        List res = executeQuery("testObj=\"My test object\"");
        assertTrue(res.size() == 1);
        assertTrue(res.get(0) == a3);

        res = executeQuery("testObj contains test");
        assertTrue(res.size() == 1);
        assertTrue(res.get(0) == a3);
    }

    public void testStringOps() {
        setUp();
        TestAgent a3 = (TestAgent) collection.get(3);
        for (int i = 0; i < numberTesters.length; i++) {
            TypeTester numberTester = numberTesters[i];
            numberTester.setValue(a3, 10);
        }
        a3.setStringVal("a bunch of characters");

        List res = executeQuery("stringVal=\"a bunch of characters\"");
        assertTrue(res.size() == 1);
        assertTrue(res.get(0) == a3);

        res = executeQuery("stringVal=\"a bunches of characters\"");
        assertTrue(res.size() == 0);
        assertTrue(!res.contains(a3));

        res = executeQuery("stringVal startsWith \"a bunch\"");
        assertTrue(res.size() == 1);
        assertTrue(res.contains(a3));

        res = executeQuery("stringVal contains \"a bunch\"");
        assertTrue(res.size() == 1);
        assertTrue(res.contains(a3));

        res = executeQuery("stringVal contains \"bunch\"");
        assertTrue(res.size() == 1);
        assertTrue(res.contains(a3));

        res = executeQuery("stringVal contains \"ters\"");
        assertTrue(res.size() == 1);
        assertTrue(res.contains(a3));

        res = executeQuery("(stringVal contains \"ters\" AND (integerVal=10))");
        assertTrue(res.size() == 1);
        assertTrue(res.contains(a3));
    }

    public void testBooleanOps() {
        setUp();
        TestAgent a3 = (TestAgent) collection.get(3);
        for (int i = 0; i < numberTesters.length; i++) {
            TypeTester numberTester = numberTesters[i];
            numberTester.setValue(a3, 10);
        }
        a3.setBooleanVal(true);

        List res = executeQuery("booleanVal equals true");
        assertTrue(res.size() == 1);
        assertTrue(res.contains(a3));

        res = executeQuery("booleanVal equals false");
        assertTrue(res.size() == 9);
        assertTrue(!res.contains(a3));

    }

    private void executeTestForNumberType(TypeTester tester) {
        TestAgent a3 = (TestAgent) collection.get(3);
        tester.setValue(a3, 10);
        List res = executeQuery(tester, "testObj=10");
        assertTrue(res.size() == 1);
        assertTrue(res.get(0) == a3);

        res = executeQuery(tester, "testObj=0");
        assertTrue(res.size() == 9);
        assertTrue(!res.contains(a3));

        res = executeQuery(tester, "testObj=0 OR testObj=10");
        assertTrue(res.size() == 10);
        assertTrue(res.contains(a3));

        res = executeQuery(tester, "testObj=0 AND testObj=10");
        assertTrue(res.size() == 0);
        assertTrue(!res.contains(a3));

        //somewhat menaingless construct..
        res = executeQuery(tester, "testObj=0 AND testObj=10 OR testObj=10");
        assertTrue(res.size() == 1);
        assertTrue(res.contains(a3));

        //somewhat menaingless construct..
        res = executeQuery(tester, "testObj=0 OR testObj=6 AND testObj=10");
        assertTrue(res.size() == 0);
        assertTrue(!res.contains(a3));

        res = executeQuery(tester, "testObj=0 OR (testObj=6 AND testObj=10)");
        assertTrue(res.size() == 9);
        assertTrue(!res.contains(a3));

        res = executeQuery(tester, "testObj=0 AND (testObj=0 OR testObj=10)");
        assertTrue(res.size() == 9);
        assertTrue(!res.contains(a3));

        res = executeQuery(tester, "testObj!=0");
        assertTrue(res.size() == 1);
        assertTrue(res.contains(a3));

        res = executeQuery(tester, "testObj!=0 OR (testObj!=10 AND testObj=0)");
        assertTrue(res.size() == 10);
        assertTrue(res.contains(a3));
        int i = 0;
        for (Iterator iterator = collection.iterator(); iterator.hasNext();) {
            TestAgent testAgent = (TestAgent) iterator.next();
            tester.setValue(testAgent, i++);
        }

        res = executeQuery(tester, "testObj>3");
        assertTrue(res.size() == 6);
        assertTrue(!res.contains(a3));

        res = executeQuery(tester, "testObj>2");
        assertTrue(res.size() == 7);
        assertTrue(res.contains(a3));

        res = executeQuery(tester, "testObj<7");
        assertTrue(res.size() == 7);
        assertTrue(res.contains(a3));

        res = executeQuery(tester, "testObj<3");
        assertTrue(res.size() == 3);
        assertTrue(!res.contains(a3));

        res = executeQuery(tester, "testObj!=3 OR (testObj>2 AND testObj<4)");
        assertTrue(res.size() == 10);
        assertTrue(res.contains(a3));

        res = executeQuery(tester, "testObj!=3 AND (testObj>2 AND testObj<4)");
        assertTrue(res.size() == 0);
        assertTrue(!res.contains(a3));

        res = executeQuery(tester, "testObj=3 AND (testObj>2 OR testObj<4)");
        assertTrue(res.size() == 1);
        assertTrue(res.contains(a3));
    }

    void constructQuery(boolean parseShouldPass, String queryString) {
        if (parseShouldPass) {
            try {
                Query q = new Query(collection, queryString);
            } catch (ParseException e) {
                fail(e.toString());  //To change body of catch statement use Options | File Templates.
            }
        } else {
            try {
                Query q = new Query(collection, queryString);
                fail("Should have recognized parse failure in: " + queryString);  //To change body of catch statement use Options | File Templates.
            } catch (ParseException e) {
            }
        }
    }

    List executeQuery(TypeTester tester, String queryString) {
        queryString = queryString.replaceAll("testObj", tester.getFieldName());
        return executeQuery(queryString);
    }

    List executeQuery(String queryString) {
        try {
            Query q = new Query(collection, queryString);
            q.execute();
            return q.getResults();
        } catch (ParseException e) {
            fail(e.toString());  //To change body of catch statement use Options | File Templates.
            return new ArrayList();
        }
    }
}
