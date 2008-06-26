/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */
package org.ascape.query;


/**

 * User: jmiller
 * Date: Dec 6, 2004
 * Time: 4:17:24 PM
 * To change this template use Options | File Templates.
 *
 *
 * **RULES FOR SEARCHING**
 * To search for a single characteristic (age),
 * age = 15; age=15 --> there can be separating spaces, there doesn't need to be
 *
 * To search within a list for a parameter without a name (eg a list of strings)
 * list.* = goldfish ---> same rule applies about spacing
 *
 * To search within a list for a named parameter
 * list.value1 = goldfish  ---> same rule applies about spacing
 *
 * To do a boolean search,
 * 1. there cannot be separating spaces between parameters
 * 2. each sub string, as well as the entire query, must be enclosed in parentheses.
 * 3. As of now, boolean searches cannot search on parameters enclosed in quotes (eg, "Holland Queen").
 * ((list.value1=goldfish) AND (list.value2=mouse))
 */
public class SearchString {

//    public static final String EQUALS = "=";
//    public static final String NOT_EQUAL = "!=";
//    public static final String LESS_THAN = "<";
//    public static final String GREATER_THAN = ">";
//    public static final String STARTS_WITH = "startsWith";
//    public static final String CONTAINS = "contains";
//    public static final String STARTS_WITH_ALL_CAPS = "STARTSWITH";
//    public static final String STARTS_WITH_ALL_SMALL = "startswith";
//    public static final String CONTAINS_CAPS = "CONTAINS";
//    public static final String ASTERISK = "*";
//
//    // String, Double or Int.. (typically)
//    private Object comparisonValue;
//
//    /**
//     * For those searches that are looking for things inside objects (eg. tupleList.age),
//     * subvalue is "age".
//     */
//    private String subvalue = "";
//
//    // place holder for searching within a list, (eg a list of strings), vs within a list of objects, like a tuple manager
//    private final String SIMPLE_SEARCH = "SIMPLE_SEARCH";
//
//    // <, >, =, !=
//    private String comparisonOperator;
//
//    PropertyDescriptor propertyDescriptor;
//
//    /**
//     * this is being used for testing only
//     */
//    public SearchString(String comparisonOperator, Object comparisonValue, PropertyAccessor accessor) {
//        this(comparisonOperator, comparisonValue, accessor.getDescriptor());
//    }
//
//    public SearchString() {
//    }
//
//    /**
//     * I made this one so the tests wouldnt break, but we could use the descriptor. -- still, for tests only
//     * @param comparisonOperator
//     * @param comparisonValue
//     * @param descriptor
//     */
//    public SearchString(String comparisonOperator, Object comparisonValue, PropertyDescriptor descriptor) {
//        this.comparisonOperator = comparisonOperator;
//        this.comparisonValue = comparisonValue;
//        this.propertyDescriptor = descriptor;
//
//        if (comparisonValue instanceof Number) {
//            this.comparisonValue = new Double(comparisonValue.toString());
//        }
//        subvalue = SIMPLE_SEARCH;
//        searchPosition = 0;
//    }
//
//    /**
//     * This is the main constructor, and (as of now), the only one that should be used for operational use.
//     * Assume string is of format parameter comparisonOp comparisonValue, with or without spaces.
//     * @param collection
//     * @param string
//     */
//    public SearchString(Scape collection, String string) {
//        String[] tokens = tokenize(string);
//        this.comparisonOperator = determineComparisonOp(string);
//
//        String param = tokens[0];
//        String val = tokens[1];
//
//        String[] parensTokens = param.split("[.]");
//        subvalue = "";
//        if (param.endsWith(ASTERISK)) {
//            subvalue = SIMPLE_SEARCH;
//        } else if (parensTokens.length == 2) {
//            // complex internal search
//            subvalue = parensTokens[1];
//        } else if (parensTokens.length > 2) {
//            throw new IllegalArgumentException("Parser Error - too many tokens: " + parensTokens.length);
//        } else if (param.endsWith(".")) {
//            throw new IllegalArgumentException("Illegal Search String - cannot end with a '.'");
//        } else {
//            // catch-all.. this may be a mistake
//            subvalue = SIMPLE_SEARCH;
//        }
//        param = parensTokens[0];
//
//        LocatedAgent randomAgent = collection.findRandom();
//        List descriptorsList = getDescriptors(randomAgent);
//        PropertyDescriptor descriptor = null;
//        for (Iterator iterator = descriptorsList.iterator(); iterator.hasNext();) {
//            PropertyDescriptor propertyDescriptor = (PropertyDescriptor) iterator.next();
////            System.out.println(propertyDescriptor.getName());
//            if (propertyDescriptor.getName().equalsIgnoreCase(param)) {
//                descriptor = propertyDescriptor;
////                System.out.println("Found match: " + propertyDescriptor.getName());
//                break;
//            }
//        }
//        this.propertyDescriptor = descriptor;
//        System.out.println("param = " + param);
//        System.out.println("descriptor = " + descriptor.getName());
//        System.out.println("comparisonOperator = " + comparisonOperator);
//        if (descriptor == null) { // ie there was no matching accessor
//            throw new IllegalStateException("Descriptor is null!");
//        } else if (subvalue.equals(ASTERISK)){
//            this.comparisonValue = determineClass(val, descriptor, randomAgent);
//        } else {
//            this.comparisonValue = val;
//        }
//        System.out.println("comparisonValue = " + comparisonValue);
////        System.out.println("leaving constructor...subvalue = " + subvalue);
//    }
//
//    Conditional condition = new Conditional() {
//        public boolean meetsCondition(Object object) {
//            searchPosition++;
//            boolean res = false;
//            Agent agent = (Agent) object;
////            List descriptorsList = getDescriptors(agent);
////            for (Iterator accessorIterator = descriptorsList.iterator(); accessorIterator.hasNext();) {
////                PropertyDescriptor descriptor = (PropertyDescriptor) accessorIterator.next();
////                if (descriptor.getName().equalsIgnoreCase(propertyDescriptor.getName())) {
////                    System.out.println("descriptor name: " + descriptor.getName()  + ", getDescriptorValue) = " + getDescriptorValue(agent, descriptor)+", subvalue: " + subvalue);
//                    if (getDescriptorValue(agent, propertyDescriptor) != null && subvalue.equalsIgnoreCase(SIMPLE_SEARCH)) {
////                        System.out.println("1.. simple search");
////                    if (getDescriptorValue(agent, descriptor) != null && subvalue.equalsIgnoreCase(ASTERISK)) {
//                        res = compareObject(PropertyAccessor.getValue(object, propertyDescriptor));
////                        break;
//                    } else if (getDescriptorValue(agent, propertyDescriptor) != null && subvalue.equalsIgnoreCase(ASTERISK)) {
////                        System.out.println("2. simple internal search");
////                    } else if (getDescriptorValue(agent, descriptor) != null && subvalue.equalsIgnoreCase(SIMPLE_SEARCH)) {
////                         //find in the list
//                        PropertyAccessor pa = new PropertyAccessor(agent, propertyDescriptor);
//                        List value = (List) pa.getValue();
//                        for (Iterator iterator = value.iterator(); iterator.hasNext();) {
//                            Object o = iterator.next();
//                            if (compareComplex(o)) {
//                                res = true;
//                                break;
//                            }
//                        }
////                        System.out.println("Returning FALSE");
////                        break;
//                    } else if (getDescriptorValue(agent, propertyDescriptor) != null) {
////                        System.out.println("3.. complex internal search");
//                        // complex search case
////                        System.out.println("Conditional.. 2");
//                        PropertyAccessor pa = new PropertyAccessor(agent, propertyDescriptor);
//                        List descriptors = getComplexSearchDescriptors(pa.getValue());
////                        System.out.println("Descriptors");
//                        for (Iterator complexDescripIt = descriptors.iterator(); complexDescripIt.hasNext();) {
//                            PropertyDescriptor pd = (PropertyDescriptor) complexDescripIt.next();
//                            if (pd.getName().equalsIgnoreCase(subvalue)) {
//                                PropertyAccessor pa2 = new PropertyAccessor(pa.getValue(), pd);
////                                return complexCompare(pa2.getValue());
//                                if (compareComplex(pa2.getValue())) {
//                                    res = true;
//                                    break;
//                                }
//                            }
//                        }
////                        System.out.println("Returning FALSE");
////                        break;
//                    } else {
//                        throw new IllegalStateException("PropertyDescriptor " + propertyDescriptor.getName()+"'s value is null.");
//                    }
////                }
////            }
//            if (res) {
//                searchFound++;
//            }
//            return res;
//        }
//    };
//
//    private String determineComparisonOp(String string) {
//        // need to have not equal check before equals check..
//        if (contains(string, NOT_EQUAL)) {
//            return NOT_EQUAL;
//        } else if (contains(string, EQUALS)) {
//            return EQUALS;
//        } else if (contains(string, LESS_THAN)) {
//            return LESS_THAN;
//        } else if (contains(string, GREATER_THAN)) {
//            return GREATER_THAN;
//        } else if (contains(string, CONTAINS) || contains(string, CONTAINS_CAPS)) {
//            return CONTAINS;
//        } else if (contains(string, STARTS_WITH) || contains(string, STARTS_WITH_ALL_CAPS) ||
//                contains(string, STARTS_WITH_ALL_SMALL)) {
//            return STARTS_WITH;
//        } else {
//            throw new IllegalArgumentException("Comparison Operators may be of the following types only: " +
//                    EQUALS + ", " + NOT_EQUAL + ", " + LESS_THAN + ", " + GREATER_THAN + ", " + CONTAINS + ", " +
//                            CONTAINS_CAPS + ", " + STARTS_WITH + ", " + STARTS_WITH_ALL_CAPS + ", " + STARTS_WITH_ALL_SMALL);
//        }
//    }
//
//    private String[] tokenize(String string) {
//        return string.split(EQUALS +"|" + NOT_EQUAL +"|" + GREATER_THAN + "|" + LESS_THAN + "|" + CONTAINS
//            + "|" + CONTAINS_CAPS + "|" + STARTS_WITH + "|" + STARTS_WITH_ALL_CAPS + "|" + STARTS_WITH_ALL_SMALL);
//    }
//    /**
//     * Util method, checks to see if a string (pattern) is contained within another string (original).
//     * @param original
//     * @param pattern
//     * @return
//     */
//    public boolean contains(String original, String pattern) {
//        return original.indexOf(pattern) != -1;
//    }
//
//    private Object determineClass(String val, PropertyDescriptor descriptor, Agent agent) {
//        Object descriptorValue = getDescriptorValue(agent, descriptor);
//        if (descriptorValue instanceof String) {
//            // remove the "'s on either end of the String
//            if (val.startsWith("\"")) {
//                val = val.substring(1);
//            }
//            if (val.endsWith("\"")) {
//                val = val.substring(0, val.length()-1);
//            }
//            return val;
//        } else if (descriptorValue instanceof Number) {
//            return new Double(val);
//        } else if (descriptorValue instanceof Boolean) {
//            return new Boolean(val);
//        } else {
//            return val.toString();
////            throw new IllegalArgumentException("Descriptor should be either a String, Number or Boolean, not a: "
////                    + descriptor.getPropertyType());
//        }
//    }
//
//    private List getDescriptors(Agent agent) {
//        PropertyDescriptor[] infoProperties = new PropertyDescriptor[0];
//        try {
//            infoProperties = Introspector.getBeanInfo(agent.getClass(), Agent.class).getPropertyDescriptors();
////            if (!(agent instanceof Scape)) {
////                infoProperties = Introspector.getBeanInfo(agent.getClass(), Agent.class).getPropertyDescriptors();
////            } else {
////                infoProperties = Introspector.getBeanInfo(agent.getClass(), Scape.class).getPropertyDescriptors();
////            }
//        } catch (IntrospectionException e) {
//            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
//        }
//        return Arrays.asList(infoProperties);
//    }
//
//    private List getComplexSearchDescriptors(Object o) {
//        PropertyDescriptor[] infoProperties = new PropertyDescriptor[0];
//        try {
//            infoProperties = Introspector.getBeanInfo(o.getClass()).getPropertyDescriptors();
//        } catch (IntrospectionException e){
//            e.printStackTrace();
//        }
//        List list = new ArrayList(infoProperties.length);
//        for (int i = 0; i < infoProperties.length; i++) {
//            list.add(infoProperties[i]);
//        }
//        return list;
//    }
//
//    /**
//     * It's easier to do it this way than rewrite all the test code, etc.
//     * @param pa
//     * @return
//     */
//    public boolean compare(PropertyAccessor pa) {
//        return compareObject(pa.getValue());
//    }
//
//    // made public for testing
//    public boolean compareObject(Object object) {
////        System.out.println("SearchString.compare.. comparing " + pa.getValue() + " to " + comparisonValue);
//        if (object instanceof Number) {
//            Number tempComparisonValue = null;
//            if (comparisonValue instanceof String) {
//                tempComparisonValue = new Double((String) comparisonValue);
//            } else {
//                tempComparisonValue = (Number) comparisonValue;
//            }
//            return numberCompare(object, tempComparisonValue);
//        } else if (object instanceof Boolean) {
//            Boolean tempComparisonValue = null;
//            if (comparisonValue instanceof String) {
//                tempComparisonValue = new Boolean((String) comparisonValue);
//            } else {
//                tempComparisonValue = (Boolean) comparisonValue;
//            }
//            return booleanCompare(object, tempComparisonValue);
//        } else {
//            String cv = comparisonValue.toString();
//            if (cv.startsWith("\"")) {
//                cv = cv.substring(1);
//            }
//            if (cv.endsWith("\"")) {
//                cv = cv.substring(0, cv.length() - 1);
//            }
//            comparisonValue = cv;
//            if (object instanceof String) {
//                return stringCompare(object);
//            } else {
//                return stringCompare(object.toString());
//            }
//        }
////            throw new IllegalArgumentException("Accessor should be either a String, Number or Boolean, not a: "
////                + pa.getDescriptor().getPropertyEditorClass());
//    }
//
//    private boolean stringCompare(Object o) {
//        if (comparisonOperator.equalsIgnoreCase(EQUALS)) {
//            return STRING_EQUALS.compare(o, comparisonValue);
//        } else if (comparisonOperator.equalsIgnoreCase(NOT_EQUAL)) {
//            return STRING_NOT_EQUALS.compare(o, comparisonValue);
//        } else if (comparisonOperator.equalsIgnoreCase(STARTS_WITH)) {
//            return STRING_STARTS_WITH.compare(o, comparisonValue);
//        } else if (comparisonOperator.equalsIgnoreCase(CONTAINS)) {
//            return STRING_CONTAINS.compare(o, comparisonValue);
//        } else {
//            throw new IllegalArgumentException("Illegal comparison operator: " + comparisonOperator);
//        }
//    }
//
//    private boolean numberCompare(Object o, Object tempComparisonValue) {
//        if (comparisonOperator.equalsIgnoreCase(EQUALS)) {
//            return NUMEBR_EQUALS.compare(o, tempComparisonValue);
//        } else if (comparisonOperator.equalsIgnoreCase(NOT_EQUAL)) {
//            return NUMBER_NOT_EQUALS.compare(o, tempComparisonValue);
//        } else if (comparisonOperator.equalsIgnoreCase(LESS_THAN)) {
//            return NUMBER_LESS_THAT.compare(o, tempComparisonValue);
//        } else if (comparisonOperator.equalsIgnoreCase(GREATER_THAN)) {
//            return NUMBER_GREATER_THAN.compare(o, tempComparisonValue);
//        } else {
////            throw new IllegalArgumentException("Illegal comparison operator: " + comparisonOperator);
//            return false;
//        }
//    }
//    private boolean booleanCompare(Object o, Object tempComparisonValue) {
//        if (comparisonOperator.equalsIgnoreCase(EQUALS)) {
//            return BOOLEAN_EQUALS.compare(o, tempComparisonValue);
//        } else if (comparisonOperator.equalsIgnoreCase(NOT_EQUAL)) {
//            return BOOLEAN_NOT_EQUALS.compare(o, tempComparisonValue);
//        } else {
//            return false;
////            throw new IllegalArgumentException("Illegal comparison operator for a Boolean: " + comparisonOperator);
//        }
//    }
//
//    private boolean compareComplex(Object o) {
////        System.out.println("SearchString.complexCompare.. comparing o: " + o + " to compVal: " + comparisonValue+", o's class: " + o.getClass());
////        System.out.println("SearchString.complexCompare.. comparing o: " + o + " to compVal: " + comparisonValue);
//        // for complex searches, comparisonValue is only set to subvalue, as a String.
//        try {
//            return compareObject(o);
//        } catch (NumberFormatException e) {
//            // the way the simple search works, it compares the comparisonValue to the agent's list. It hits trouble
//            // when it tries to convert a comparisonoperator string to a number or boolean
//            return false;
//        } catch (ClassCastException e) { // this may be too general an exception.. (ie it may catch something it shouldn't)
//            return false;
//        }
//    }
//
//    private static abstract class Comparator {
//        public abstract boolean compare(Object o1, Object o2);
//    }
//
//    public static Comparator STRING_EQUALS = new Comparator() {
//        public boolean compare(Object o1, Object o2) {
//            return ((String) o1).equalsIgnoreCase((String) o2);
//        }
//    };
//
//    public static Comparator STRING_NOT_EQUALS = new Comparator() {
//        public boolean compare(Object o1, Object o2) {
//            return !((String) o1).equalsIgnoreCase((String) o2);
//        }
//    };
//
//    public static Comparator STRING_STARTS_WITH = new Comparator() {
//        public boolean compare(Object o1, Object o2) {
//            String s1 = (String) o1;
//            String s2 = (String) o2;
//            if (s1.length() < s2.length()) {
//                return false;
//            } else {
//                String substring = s1.substring(0, s2.length());
//                return substring.equalsIgnoreCase(s2);
//            }
//        }
//    };
//
//    public static Comparator STRING_CONTAINS = new Comparator() {
//        public boolean compare(Object o1, Object o2) {
//            boolean match = false;
//            String s1 = (String) o1;
//            String s2 = (String) o2;
//            for (int i = 0; i < s1.length(); i++) {
//                if (s1.length() - i >= s2.length()) {
//                    if (s1.substring(i, i + s2.length()).equalsIgnoreCase(s2)) {
//                        match = true;
//                        break;
//                    }
//                } else {
//                    break;
//                }
//            }
//            return match;
//        }
//    };
//
//    public static Comparator NUMBER_LESS_THAT = new Comparator() {
//        public boolean compare(Object o1, Object o2) {
//            return ((Number) o1).doubleValue() < ((Number) o2).doubleValue();
//        }
//    };
//
//    public static Comparator NUMBER_GREATER_THAN = new Comparator() {
//        public boolean compare(Object o1, Object o2) {
////            System.out.println("o1.getClass: " + o1.getClass() + ", o2.getClass():  "+ o2.getClass());
//            return ((Number) o1).doubleValue() > ((Number) o2).doubleValue();
//        }
//    };
//
//    public static Comparator NUMEBR_EQUALS = new Comparator() {
//        public boolean compare(Object o1, Object o2) {
//            return ((Number) o1).doubleValue() == ((Number) o2).doubleValue();
//        }
//    };
//
//    public static Comparator NUMBER_NOT_EQUALS = new Comparator() {
//        public boolean compare(Object o1, Object o2) {
//            return ((Number) o1).doubleValue() != ((Number) o2).doubleValue();
//        }
//    };
//
//    public static Comparator BOOLEAN_EQUALS = new Comparator() {
//        public boolean compare(Object o1, Object o2) {
////            System.out.println("o1: " + o1+", o1.getClass: " + o1.getClass()+", o2: " + o2+", o2.class: " + o2.getClass());
//            return (o1).equals(o2);
//        }
//    };
//
//    public static Comparator BOOLEAN_NOT_EQUALS = new Comparator() {
//        public boolean compare(Object o1, Object o2) {
//            return !(o1).equals(o2);
//        }
//    };
//
//    public String getComparisonOperator() {
//        return comparisonOperator;
//    }
//
//    public Object getComparisonValue() {
//        return comparisonValue;
//    }
//
//    public PropertyDescriptor getPropertyDescriptor() {
//        return propertyDescriptor;
//    }
//
//    /**
//     * A hack to get around the fact that the code here was all written to use a property accessor. It was changed
//     * to search on non-primitive variables, that were being filtered by PropertyAccessor.determineAccessors()
//     * @param object
//     * @param descriptor
//     * @return
//     */
//    private Object getDescriptorValue(Object object, PropertyDescriptor descriptor) {
//        Object o = null;
//        try {
//            o = descriptor.getReadMethod().invoke(object, null);
//        } catch (IllegalAccessException e) {
//            System.out.println("Error in dynamic method read: " + e);
//        } catch (IllegalArgumentException e) {
//            System.out.println("Error in dynamic method read: " + e);
//        } catch (InvocationTargetException e) {
//            System.out.println("Error in dynamic method read: " + e);
//        }
//        return o;
//    }
//
//    protected int getSearchPosition() {
//        return searchPosition;
//    }
//
//    public int getSearchFound() {
//        return searchFound;
//    }
}

