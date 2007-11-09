/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.test.model;

import java.util.ArrayList;
import java.util.HashSet;

import junit.framework.TestCase;

import org.ascape.model.AscapeObject;



interface CommonInterface {

    public double getDouble();

    public void setDouble(double value);
}

class NestedClass implements CommonInterface {

    private double ntDouble;
    NestedClass nestedClass;

    public double getDouble() {
        return ntDouble;
    }

    public void setDouble(double ntDouble) {
        this.ntDouble = ntDouble;
    }

    public double getNtDouble() {
        return ntDouble;
    }

    public void setNtDouble(double ntDouble) {
        this.ntDouble = ntDouble;
    }
}

class NestedSubClass extends NestedClass {

}

class NestedSubClass2 extends NestedClass {

}

class DifferentClass implements CommonInterface {

    double ntDouble;

    public double getDouble() {
        return ntDouble;
    }

    public void setDouble(double ntDouble) {
        this.ntDouble = ntDouble;
    }

    public double getNtDouble() {
        return ntDouble;
    }

    public void setNtDouble(double ntDouble) {
        this.ntDouble = ntDouble;
    }
}

class TestClass {

    private double sDouble;
    double dDouble;
    Object sNull;
    Object dNull;
    String sName;
    String dName;
    NestedClass sObj;
    private NestedClass dObj;
    //Really, the same test as sObj above..
    private NestedClass sSubClass;
    private NestedClass dSubClass;
    private CommonInterface sInterface;
    private CommonInterface dInterface;
    int[] sIntArray;
    int[] dIntArray;
    public int[][] s2DIntArray;
    int[][] d2DIntArray;
    int[] dLenIntArray;
    protected Object[] sObjArray;
    Object[] dObjArray;

    public TestClass() {
    }

    public String toString() {
        return dName;
    }

    public int[][] getD2DIntArray() {
        return d2DIntArray;
    }

    public void setD2DIntArray(int[][] d2DIntArray) {
        this.d2DIntArray = d2DIntArray;
    }

    public double getDDouble() {
        return dDouble;
    }

    public void setDDouble(double dDouble) {
        this.dDouble = dDouble;
    }

    public int[] getDIntArray() {
        return dIntArray;
    }

    public void setDIntArray(int[] dIntArray) {
        this.dIntArray = dIntArray;
    }

    public CommonInterface getDInterface() {
        return dInterface;
    }

    public void setDInterface(CommonInterface dInterface) {
        this.dInterface = dInterface;
    }

    public int[] getDLenIntArray() {
        return dLenIntArray;
    }

    public void setDLenIntArray(int[] dLenIntArray) {
        this.dLenIntArray = dLenIntArray;
    }

    public String getDName() {
        return dName;
    }

    public void setDName(String dName) {
        this.dName = dName;
    }

    public Object getDNull() {
        return dNull;
    }

    public void setDNull(Object dNull) {
        this.dNull = dNull;
    }

    public NestedClass getDObj() {
        return dObj;
    }

    public void setDObj(NestedClass dObj) {
        this.dObj = dObj;
    }

    public Object[] getDObjArray() {
        return dObjArray;
    }

    public void setDObjArray(Object[] dObjArray) {
        this.dObjArray = dObjArray;
    }

    public NestedClass getDSubClass() {
        return dSubClass;
    }

    public void setDSubClass(NestedClass dSubClass) {
        this.dSubClass = dSubClass;
    }

    public int[][] getS2DIntArray() {
        return s2DIntArray;
    }

    public void setS2DIntArray(int[][] s2DIntArray) {
        this.s2DIntArray = s2DIntArray;
    }

    public double getSDouble() {
        return sDouble;
    }

    public void setSDouble(double sDouble) {
        this.sDouble = sDouble;
    }

    public int[] getSIntArray() {
        return sIntArray;
    }

    public void setSIntArray(int[] sIntArray) {
        this.sIntArray = sIntArray;
    }

    public CommonInterface getSInterface() {
        return sInterface;
    }

    public void setSInterface(CommonInterface sInterface) {
        this.sInterface = sInterface;
    }

    public String getSName() {
        return sName;
    }

    public void setSName(String sName) {
        this.sName = sName;
    }

    public Object getSNull() {
        return sNull;
    }

    public void setSNull(Object sNull) {
        this.sNull = sNull;
    }

    public NestedClass getSObj() {
        return sObj;
    }

    public void setSObj(NestedClass sObj) {
        this.sObj = sObj;
    }

    public Object[] getSObjArray() {
        return sObjArray;
    }

    public void setSObjArray(Object[] sObjArray) {
        this.sObjArray = sObjArray;
    }

    public NestedClass getSSubClass() {
        return sSubClass;
    }

    public void setSSubClass(NestedClass sSubClass) {
        this.sSubClass = sSubClass;
    }
}

class SuperClass {

    public void superClassSet(int value) {
    }

    public void superClassSetIValue(SubClass iValue) {
    }
}

class SubClass extends SuperClass {

    public void subClassSet(int value) {
    }
}

class SubSubClass extends SubClass {

    public void subSubClassSet(int value) {
    }
}

/**
 * A class containg a static member for comparison
 **/
class StaticClass {

    public static TestClass staticMember;

    public void build() {
        staticMember = new TestClass();
        staticMember.dDouble = 1.0;
    }
}

/**
 * A class containg a static member for comparison
 **/
class NonStaticClass {

    public TestClass nonStaticMember;

    public void build() {
        nonStaticMember = new TestClass();
        nonStaticMember.dDouble = 1.0;
    }
}

public class AscapeObjectTest extends TestCase {

    public AscapeObjectTest(String name) {
        super(name);
    }

    NestedClass sObj1 = new NestedClass();
    NestedClass sObj2 = new NestedClass();
    NestedClass dObj1 = new NestedClass();
    NestedClass dObj2 = new NestedClass();

    int[] sIntArrayInit = {0, 1, 2, 3};
    int[] dIntArrayInit = {0, 1, 3, 2};
    int[] dLenIntArrayInit = {0, 1, 2};
    Object[] sObjArrayInit = {null, sObj1, dObj1, null};
    Object[] dObjArrayInit = {null, sObj2, dObj2, dObj1};
    int[][] s2DIntArrayInit = {sIntArrayInit, sIntArrayInit, sIntArrayInit, sIntArrayInit};
    int[][] d2DIntArrayInit = {sIntArrayInit, sIntArrayInit, dIntArrayInit, sIntArrayInit};


    /**
     * Lifted from Ascape Object.
     * Represent a pair of objects that have allready been compared, so that we do not have to compare them again.
     */
    static class ComparisonPair {

        Object o1;
        Object o2;

        ComparisonPair(Object o1, Object o2) {
            this.o1 = o1;
            this.o2 = o2;
        }

        public boolean equals(Object o) {
            return ((this.o1 == ((ComparisonPair) o).o1) && (((ComparisonPair) o).o2 == this.o2));
        }

        public int hashCode() {
            long hashCandidate = o1.hashCode() + o2.hashCode();
            if (hashCandidate > Integer.MAX_VALUE) {
                hashCandidate -= 2 * Integer.MAX_VALUE;
            }
            return (int) hashCandidate;
        }
    }

    public void testComparisonPair() {
        Object obj1 = new Object();
        Object obj2 = new Object();
        Object obj3 = new Object();
        Object obj4 = new Object();
        assertTrue((new ComparisonPair(obj1, obj2)).equals(new ComparisonPair(obj1, obj2)));
        HashSet set = new HashSet();
        set.add(new ComparisonPair(obj1, obj2));
        set.add(new ComparisonPair(obj3, obj4));
        assertTrue(!set.contains(new ComparisonPair(obj1, obj3)));
        assertTrue(set.contains(new ComparisonPair(obj1, obj2)));
    }

    public void testDiffCircularReference() {
        ArrayList diffResults;

        NestedClass parent1 = new NestedClass();
        NestedClass child1 = new NestedClass();
        parent1.nestedClass = child1;
        child1.nestedClass = parent1;

        NestedClass parent2 = new NestedClass();
        NestedClass child2 = new NestedClass();
        parent2.nestedClass = child2;
        child2.nestedClass = parent2;

        //Will run out of memory if problem exists, not ideal, but..
        try {
            diffResults = AscapeObject.diffDeep(parent1, parent2);
            assertTrue(diffResults.size() == 0);
        } catch (OutOfMemoryError e) {
            fail();
        }
    }

    //causing Opteron JDK to fail
    public void donttestDiffDeep() {
        ArrayList diffResults;

        diffResults = AscapeObject.diffDeep(new Double(1.0), new Double(1.0));
        assertTrue(diffResults.size() == 0);

        diffResults = AscapeObject.diffDeep(new Double(1.0), new Double(1.2));
        assertTrue(diffResults.size() == 1);
        assertTrue(diffResults.contains("value: 1.0, 1.2"));

        diffResults = AscapeObject.diffDeep(sIntArrayInit, dIntArrayInit);
        assertTrue(diffResults.size() == 2);
        assertTrue(diffResults.contains("[2].value: 2, 3"));

        diffResults = AscapeObject.diffDeep(new NestedSubClass(), new NestedClass());
        assertTrue(diffResults.size() == 1);
        assertTrue(diffResults.contains(": org.ascape.test.model.NestedSubClass, org.ascape.test.model.NestedClass"));

        TestClass test1 = new TestClass();
        TestClass test2 = new TestClass();
        diffResults = AscapeObject.diffDeep(test1, test2);
        assertTrue(diffResults.size() == 0);

        sObj1.setDouble(1.0);
        sObj2.setDouble(1.0);
        dObj1.setDouble(1.0);
        dObj2.setDouble(1.1);

        NestedClass sSubClass1 = new NestedSubClass();
        sSubClass1.setDouble(1.0);
        NestedClass sSubClass2 = new NestedSubClass();
        sSubClass2.setDouble(1.0);
        NestedClass dSubClass1 = new NestedSubClass();
        sSubClass1.setDouble(1.0);
        NestedClass dSubClass2 = new NestedSubClass2();
        sSubClass2.setDouble(1.0);

        CommonInterface sInterface1 = new NestedClass();
        sInterface1.setDouble(1.0);
        CommonInterface sInterface2 = new NestedClass();
        sInterface2.setDouble(1.0);
        CommonInterface dInterface1 = new NestedClass();
        dInterface1.setDouble(1.0);
        CommonInterface dInterface2 = new DifferentClass();
        dInterface2.setDouble(1.0);

        test1.setSDouble(1.0);
        test1.dDouble = 1.0;
        test1.sNull = null;
        test1.dNull = null;
        test1.sName = "Test";
        test1.dName = "TestObject 1";
        test1.sObj = sObj1;
        test1.setDObj(dObj1);
        test1.dName = "TestObject 1";
        test1.sIntArray = sIntArrayInit;
        test1.dIntArray = sIntArrayInit;
        test1.s2DIntArray = s2DIntArrayInit;
        test1.d2DIntArray = s2DIntArrayInit;
        test1.dLenIntArray = sIntArrayInit;
        test1.sObjArray = sObjArrayInit;
        test1.dObjArray = sObjArrayInit;
        test1.setSSubClass(sSubClass1);
        test1.setDSubClass(dSubClass1);
        test1.setSInterface(sInterface1);
        test1.setDInterface(dInterface1);

        test2.setSDouble(1.0);
        test2.dDouble = 1.2;
        test2.sNull = null;
        test2.dNull = new Object();
        test2.sName = "Test";
        test2.dName = "TestObject 2";
        test2.sObj = sObj2;
        test2.setDObj(dObj2);
        test2.sIntArray = sIntArrayInit;
        test2.dIntArray = dIntArrayInit;
        test2.s2DIntArray = s2DIntArrayInit;
        test2.d2DIntArray = d2DIntArrayInit;
        test2.dLenIntArray = dLenIntArrayInit;
        test2.sObjArray = sObjArrayInit;
        test2.dObjArray = dObjArrayInit;
        test2.setSSubClass(sSubClass2);
        test2.setDSubClass(dSubClass2);
        test2.setSInterface(sInterface2);
        test2.setDInterface(dInterface2);

        diffResults = AscapeObject.diffDeep(test1, test2);
        /*Iterator diffIter = diffResults.iterator();
        while(diffIter.hasNext()) {
            System.out.println(diffIter.next());
        }*/

        assertTrue(diffResults.size() == 12);
        assertTrue(diffResults.contains("dDouble: 1.0, 1.2"));
        assertTrue(diffResults.contains("dName: TestObject 1, TestObject 2"));
        assertTrue(diffResults.contains("dObj.ntDouble: 1.0, 1.1"));
        assertTrue(diffResults.contains("dIntArray[2].value: 2, 3"));
        assertTrue(diffResults.contains("dIntArray[3].value: 3, 2"));
        assertTrue(diffResults.contains("dLenIntArray: Length 4, Length 3"));
        assertTrue(diffResults.contains("d2DIntArray[2][2].value: 2, 3"));
        assertTrue(diffResults.contains("d2DIntArray[2][3].value: 3, 2"));
        assertTrue(diffResults.contains("dLenIntArray: Length 4, Length 3"));
        //Allready discovered before
        //assertTrue(diffResults.contains("dObjArray[2].ntDouble: 1.0, 1.1"));
        assertTrue(diffResults.contains("dObjArray[3]: null, org.ascape.test.model.NestedClass"));
        assertTrue(diffResults.contains("dSubClass: org.ascape.test.model.NestedSubClass, org.ascape.test.model.NestedSubClass2"));
        assertTrue(diffResults.contains("dInterface: org.ascape.test.model.NestedClass, org.ascape.test.model.DifferentClass"));


        // make sure that inherited fields are checked
        sSubClass1.setDouble(1.0);
        sSubClass2.setDouble(1.0);
        diffResults = AscapeObject.diffDeep(sSubClass1, sSubClass2);
        assertTrue(diffResults.size() == 0);

        sSubClass2.setDouble(2.0);
        diffResults = AscapeObject.diffDeep(sSubClass1, sSubClass2);
        assertTrue(diffResults.size() == 1);
        assertTrue(diffResults.contains("org.ascape.test.model.NestedClass.ntDouble: 1.0, 2.0"));

        SubSubClass ssc1 = new SubSubClass();
        ssc1.superClassSet(1);
        ssc1.subClassSet(2);
        ssc1.subSubClassSet(3);
        SubClass sc1a = new SubClass();
        sc1a.superClassSet(4);
        ssc1.superClassSetIValue(sc1a);
        SubSubClass ssc2 = new SubSubClass();
        ssc2.superClassSet(11);
        ssc2.subClassSet(12);
        ssc2.subSubClassSet(13);
        SubClass sc2a = new SubClass();
        sc2a.superClassSet(14);
        ssc2.superClassSetIValue(sc2a);
        diffResults = AscapeObject.diffDeep(ssc1, ssc2);

        assertTrue(diffResults.size() == 4);
        assertTrue(diffResults.contains("value: 3, 13"));
        assertTrue(diffResults.contains("org.ascape.test.model.SubClass.value: 2, 12"));
        assertTrue(diffResults.contains("org.ascape.test.model.SuperClass.value: 1, 11"));
        assertTrue(diffResults.contains("org.ascape.test.model.SuperClass.iValue..org.ascape.test.model.SuperClass.value: 4, 14"));
    }

    //Exact same test (excpet for ordering), ensuring that DFS and BFS behave in same way.
    public void donttestDiffDeepDFS() {
        ArrayList diffResults;

        diffResults = AscapeObject.diffDeepDFS(new Double(1.0), new Double(1.0));
        assertTrue(diffResults.size() == 0);

        diffResults = AscapeObject.diffDeepDFS(new Double(1.0), new Double(1.2));
        assertTrue(diffResults.size() == 1);
        /*Iterator diffIter = diffResults.iterator();
        while(diffIter.hasNext()) {
            System.out.println(diffIter.next());
        }*/
        assertTrue(diffResults.contains("value: 1.0, 1.2"));

        diffResults = AscapeObject.diffDeepDFS(sIntArrayInit, dIntArrayInit);
        /*Iterator diffIter = diffResults.iterator();
        while(diffIter.hasNext()) {
            System.out.println(diffIter.next());
        }*/
        assertTrue(diffResults.size() == 2);
        assertTrue(diffResults.contains("[2].value: 2, 3"));

        diffResults = AscapeObject.diffDeepDFS(new NestedSubClass(), new NestedClass());
        assertTrue(diffResults.size() == 1);
        assertTrue(diffResults.contains(": org.ascape.test.model.NestedSubClass, org.ascape.test.model.NestedClass"));

        TestClass test1 = new TestClass();
        TestClass test2 = new TestClass();
        diffResults = AscapeObject.diffDeepDFS(test1, test2);
        assertTrue(diffResults.size() == 0);

        sObj1.setNtDouble(1.0);
        sObj2.setNtDouble(1.0);
        dObj1.setNtDouble(1.0);
        dObj2.setNtDouble(1.1);

        NestedClass sSubClass1 = new NestedSubClass();
        sSubClass1.setDouble(1.0);
        NestedClass sSubClass2 = new NestedSubClass();
        sSubClass2.setDouble(1.0);
        NestedClass dSubClass1 = new NestedSubClass();
        sSubClass1.setDouble(1.0);
        NestedClass dSubClass2 = new NestedSubClass2();
        sSubClass2.setDouble(1.0);

        CommonInterface sInterface1 = new NestedClass();
        sInterface1.setDouble(1.0);
        CommonInterface sInterface2 = new NestedClass();
        sInterface2.setDouble(1.0);
        CommonInterface dInterface1 = new NestedClass();
        dInterface1.setDouble(1.0);
        CommonInterface dInterface2 = new DifferentClass();
        dInterface2.setDouble(1.0);

        test1.setSDouble(1.0);
        test1.dDouble = 1.0;
        test1.sNull = null;
        test1.dNull = null;
        test1.sName = "Test";
        test1.dName = "TestObject 1";
        test1.sObj = sObj1;
        test1.setDObj(dObj1);
        test1.dName = "TestObject 1";
        test1.sIntArray = sIntArrayInit;
        test1.dIntArray = sIntArrayInit;
        test1.s2DIntArray = s2DIntArrayInit;
        test1.d2DIntArray = s2DIntArrayInit;
        test1.dLenIntArray = sIntArrayInit;
        test1.sObjArray = sObjArrayInit;
        test1.dObjArray = sObjArrayInit;
        test1.setSSubClass(sSubClass1);
        test1.setDSubClass(dSubClass1);
        test1.setSInterface(sInterface1);
        test1.setDInterface(dInterface1);

        test2.setSDouble(1.0);
        test2.dDouble = 1.2;
        test2.sNull = null;
        test2.dNull = new Object();
        test2.sName = "Test";
        test2.dName = "TestObject 2";
        test2.sObj = sObj2;
        test2.setDObj(dObj2);
        test2.sIntArray = sIntArrayInit;
        test2.dIntArray = dIntArrayInit;
        test2.s2DIntArray = s2DIntArrayInit;
        test2.d2DIntArray = d2DIntArrayInit;
        test2.dLenIntArray = dLenIntArrayInit;
        test2.sObjArray = sObjArrayInit;
        test2.dObjArray = dObjArrayInit;
        test2.setSSubClass(sSubClass2);
        test2.setDSubClass(dSubClass2);
        test2.setSInterface(sInterface2);
        test2.setDInterface(dInterface2);

        diffResults = AscapeObject.diffDeepDFS(test1, test2);
        /*Iterator diffIter = diffResults.iterator();
        while(diffIter.hasNext()) {
            System.out.println(diffIter.next());
        }*/

        assertTrue(diffResults.size() == 12);
        assertTrue(diffResults.contains("dDouble: 1.0, 1.2"));
        assertTrue(diffResults.contains("dName: TestObject 1, TestObject 2"));
        assertTrue(diffResults.contains("dObj.ntDouble: 1.0, 1.1"));
        assertTrue(diffResults.contains("dIntArray[2].value: 2, 3"));
        assertTrue(diffResults.contains("dIntArray[3].value: 3, 2"));
        assertTrue(diffResults.contains("dLenIntArray: Length 4, Length 3"));
        assertTrue(diffResults.contains("d2DIntArray[2][2].value: 2, 3"));
        assertTrue(diffResults.contains("d2DIntArray[2][3].value: 3, 2"));
        assertTrue(diffResults.contains("dLenIntArray: Length 4, Length 3"));
        //Allready discovered before
        //assertTrue(diffResults.contains("dObjArray[2].ntDouble: 1.0, 1.1"));
        assertTrue(diffResults.contains("dObjArray[3]: null, org.ascape.test.model.NestedClass"));
        assertTrue(diffResults.contains("dSubClass: org.ascape.test.model.NestedSubClass, org.ascape.test.model.NestedSubClass2"));
        assertTrue(diffResults.contains("dInterface: org.ascape.test.model.NestedClass, org.ascape.test.model.DifferentClass"));

        // make sure that inherited fields are checked
        sSubClass1.setDouble(1.0);
        sSubClass2.setDouble(1.0);
        diffResults = AscapeObject.diffDeepDFS(sSubClass1, sSubClass2);
        assertTrue(diffResults.size() == 0);

        sSubClass2.setDouble(2.0);
        diffResults = AscapeObject.diffDeepDFS(sSubClass1, sSubClass2);
        assertTrue(diffResults.size() == 1);
        assertTrue(diffResults.contains("org.ascape.test.model.NestedClass.ntDouble: 1.0, 2.0"));

        SubSubClass ssc1 = new SubSubClass();
        ssc1.superClassSet(1);
        ssc1.subClassSet(2);
        ssc1.subSubClassSet(3);
        SubClass sc1a = new SubClass();
        sc1a.superClassSet(4);
        ssc1.superClassSetIValue(sc1a);
        SubSubClass ssc2 = new SubSubClass();
        ssc2.superClassSet(11);
        ssc2.subClassSet(12);
        ssc2.subSubClassSet(13);
        SubClass sc2a = new SubClass();
        sc2a.superClassSet(14);
        ssc2.superClassSetIValue(sc2a);
        diffResults = AscapeObject.diffDeepDFS(ssc1, ssc2);

        assertTrue(diffResults.size() == 4);
        assertTrue(diffResults.contains("value: 3, 13"));
        assertTrue(diffResults.contains("org.ascape.test.model.SubClass.value: 2, 12"));
        assertTrue(diffResults.contains("org.ascape.test.model.SuperClass.value: 1, 11"));
        assertTrue(diffResults.contains("org.ascape.test.model.SuperClass.iValue..org.ascape.test.model.SuperClass.value: 4, 14"));
    }

    public void donttestEqualsDeep() {
        assertTrue(AscapeObject.equalsDeep(new Double(1.0), new Double(1.0)));

        assertTrue(!AscapeObject.equalsDeep(new Double(1.0), new Double(1.1)));

        assertTrue(!AscapeObject.equalsDeep(new NestedSubClass(), new NestedClass()));

        TestClass test1 = new TestClass();
        TestClass test2 = new TestClass();
        assertTrue(AscapeObject.equalsDeep(test1, test2));
        test1.dDouble = 0.0;
        test2.dDouble = 1.2;
        assertTrue(!AscapeObject.equalsDeep(test1, test2));
        test2.dDouble = 0.0;
        assertTrue(AscapeObject.equalsDeep(test1, test2));

        test1.sObj = new NestedClass();
        test1.sObj.setNtDouble(0.0);
        test2.sObj = new NestedClass();
        test2.sObj.setNtDouble(0.4);
        assertTrue(!AscapeObject.equalsDeep(test1, test2));
        test1.sObj.setNtDouble(0.4);
        assertTrue(AscapeObject.equalsDeep(test1, test2));

        { //try a test using a NestedClass object
            NestedClass sSubClass1 = new NestedClass();
            sSubClass1.setDouble(1.0);
            NestedClass sSubClass2 = new NestedClass();
            sSubClass2.setDouble(1.0);
            assertTrue(AscapeObject.equalsDeep(sSubClass1, sSubClass2));
            sSubClass2.setDouble(2.0);
            assertTrue(!AscapeObject.equalsDeep(sSubClass1, sSubClass2));
        }

        { //try the same test using a NestedSubClass object to make sure that inherited fields are checked
            NestedClass sSubClass1 = new NestedSubClass();
            sSubClass1.setDouble(1.0);
            NestedClass sSubClass2 = new NestedSubClass();
            sSubClass2.setDouble(1.0);
            assertTrue(AscapeObject.equalsDeep(sSubClass1, sSubClass2));
            sSubClass2.setDouble(2.0);
            assertTrue(!AscapeObject.equalsDeep(sSubClass1, sSubClass2));
        }

        //test2.dIntArray = dIntArrayInit;
        //assertTrue(!AscapeObject.equalsDeep(test1, test2));
    }

    /*
     * Following test are desgined to ensure that static members aren't compared more than once
     */

    class StaticHolderClass {

        StaticClass value1 = new StaticClass();
        StaticClass value2 = new StaticClass();
        StaticClass[] values;

        public void build() {
            value1.build();
            value2.build();
            values = new StaticClass[10];
            for (int i = 0; i < values.length; i++) {
                values[i] = new StaticClass();
                values[i].build();
            }
        }
    }

    class NonStaticHolderClass {

        NonStaticClass value1 = new NonStaticClass();
        NonStaticClass value2 = new NonStaticClass();
        NonStaticClass[] values;

        public void build() {
            value1.build();
            value2.build();
            values = new NonStaticClass[10];
            for (int i = 0; i < values.length; i++) {
                values[i] = new NonStaticClass();
                values[i].build();
            }
        }
    }

    //Exact same test (excpet for ordering), ensuring that DFS and BFS behave in same way.
    public void testDiffDeepNonStatic() {
        ArrayList diffResults;
        //AscapeObject.setComparisonStream(System.out);
        NonStaticHolderClass holder1 = new NonStaticHolderClass();
        holder1.build();
        NonStaticHolderClass holder2 = new NonStaticHolderClass();
        holder2.build();
        diffResults = AscapeObject.diffDeep(holder1, holder2);
        assertTrue(diffResults.size() == 0);
    }

    //Exact same test (excpet for ordering), ensuring that DFS and BFS behave in same way.
    public void testDiffDeepStatic() {
        ArrayList diffResults;
        //AscapeObject.setComparisonStream(System.out);
        StaticHolderClass holder1 = new StaticHolderClass();
        holder1.build();
        StaticHolderClass holder2 = new StaticHolderClass();
        holder2.build();
        diffResults = AscapeObject.diffDeep(holder1, holder2);
        assertTrue(diffResults.size() == 0);
    }
}