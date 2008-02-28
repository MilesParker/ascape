package org.ascape.test.query;

import org.ascape.model.Cell;

/**

 * User: jmiller
 * Date: Dec 8, 2004
 * Time: 3:18:24 PM
 * To change this template use Options | File Templates.
 */
public class TestAgent extends Cell {

    private String stringVal;

    private boolean booleanVal;
    private int integerVal;
    private double doubleVal;
    private float floatVal;
    private long longVal;

    private Boolean booleanObj;
    private Integer integerObj;
    private Double doubleObj;
    private Long longObj;
    private Float floatObj;

    private TestAgent member;
    static int depthCount;

    private Object testObj;

    public static final String initString =  "XXX";

    public void initialize() {
        booleanVal = false;
        integerVal = 0;
        doubleVal = 0.0;
        floatVal = 0.0f;
        longVal = 0L;
        stringVal = initString;
        booleanObj = Boolean.valueOf(false);
        integerObj = new Integer(0);
        doubleObj = new Double(0);
        floatObj = new Float(0.0f);
        longObj = new Long(0L);
        depthCount++;
        if (depthCount < 5) {
            member = new TestAgent();
            member.initialize();
        }
        depthCount--;
    }

    public boolean isBooleanVal() {

        return booleanVal;
    }

    public int getIntegerVal() {
        return integerVal;
    }

    public String getStringVal() {
        return stringVal;
    }

    public void setBooleanVal(boolean booleanVal) {
        this.booleanVal = booleanVal;
    }

    public void setIntegerVal(int integerVal) {
        this.integerVal = integerVal;
    }

    public void setStringVal(String stringVal) {
        this.stringVal = stringVal;
    }

    public double getDoubleVal() {
        return doubleVal;
    }

    public void setDoubleVal(double doubleVal) {
        this.doubleVal = doubleVal;
    }

    public Boolean getBooleanObj() {
        return booleanObj;
    }

    public Double getDoubleObj() {
        return doubleObj;
    }

    public Integer getIntegerObj() {
        return integerObj;
    }

    public void setBooleanObj(Boolean booleanObj) {
        this.booleanObj = booleanObj;
    }

    public void setDoubleObj(Double doubleObj) {
        this.doubleObj = doubleObj;
    }

    public void setIntegerObj(Integer integerObj) {
        this.integerObj = integerObj;
    }

    public Float getFloatObj() {
        return floatObj;
    }

    public void setFloatObj(Float floatObj) {
        this.floatObj = floatObj;
    }

    public float getFloatVal() {
        return floatVal;
    }

    public void setFloatVal(float floatVal) {
        this.floatVal = floatVal;
    }

    public Long getLongObj() {
        return longObj;
    }

    public void setLongObj(Long longObj) {
        this.longObj = longObj;
    }

    public long getLongVal() {
        return longVal;
    }

    public void setLongVal(long longVal) {
        this.longVal = longVal;
    }

    public TestAgent getMember() {
        return member;
    }

    public void setMember(TestAgent member) {
        this.member = member;
    }

    public Object getTestObj() {
        return testObj;
    }

    public void setTestObj(Object testObj) {
        this.testObj = testObj;
    }

    public String toString() {
        return name+". TestStateBool: " + booleanVal + ", doubleVal: " + doubleVal + ", integerVal: "
                + integerVal + ", stringVal: " + stringVal;
    }

    public Object clone() {
        TestAgent clone = (TestAgent) super.clone();
        if (member != null) {
            clone.member = (TestAgent) member.clone();
        } else {
            clone.member = null;
        }
        return clone;
    }

}
