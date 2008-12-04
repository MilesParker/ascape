/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */

package org.ascape.model;

import java.io.PrintStream;
import java.io.Serializable;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;

import org.ascape.util.HasName;
import org.ascape.util.RandomFunctions;


/**
 * The cannonical class for most ascape model objects, including rules.
 * Used to manage basic features, such as access to random stream.
 * Every ascape object should 'belong' to some scape, but is not
 * neccesarily a member of that scape.
 *
 * @author Miles Parker
 * @version 2.0
 * @history 2.0 7/26/01 Added support for deep diff and equivalence tests
 * @history 1.1.2 5/14/1999 Added conveneince methods for random numbers directly to ascape object.
 * @history 1.1.1 2/8/1999 Centralized support for naming here; all ascape objects may now have names
 * @since 1.0.1
 */
public class AscapeObject extends Object implements RandomFunctions, HasName, Cloneable, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Symbol for random seed to be arbitrary (current time in milliseconds.)
     */
    public final static int ARBITRARY_SEED = -1;

    /**
     * Note, this color must be set from every target platform, and the setting class must load before the model is
     * displayed.
     */
    public static Object PLATFORM_DEFAULT_COLOR;

    /**
     * The agent that this object belongs to. Note that this object
     * is not neccesarily a <i>member</i> of the scape.
     */
    protected Scape scape;

    /**
     * The name of this object
     */
    protected String name;

    /**
     * The random number stream that will be used by default, and should
     * be used for typical random number draws. Made a static variable for
     * all objects for performance and transperency.
     */
    private static Random random = new Random(System.currentTimeMillis());

    /**
     * The seed that will be used to initialize the random stream.
     * If arbitrary seed, current time in milliseconds will be used.
     * todo this needs to be changed to non-static implementation for repeatability in multi-model / vm usage
     */
    private static long randomSeed = ARBITRARY_SEED;

    /**
     * The last seed set fpor the random stream.
     */
    private static long lastRandomSeed;

    /**
     * A stream to dump equals and diff comparisons too. Usually null.
     */
    private transient static PrintStream comparisonStream;

    /**
     * Constructs an ascape object.
     */
    public AscapeObject() {
    }

    /**
     * Constructs an ascape object.
     * @param name the name of this object
     */
    public AscapeObject(String name) {
        this.name = name;
    }

    /**
     * Sets the scape that this object is primarily related to.
     * @param scape the scape this agent is belongs to
     */
    public void setScape(Scape scape) {
        this.scape = scape;
    }

    /**
     * Returns the scape that this object is primarily related to.
     */
    public Scape getScape() {
        return scape;
    }

    /**
     * A name this object may be referred to by.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets a name this object may be referred to by.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the random number stream to be used by this object. For
     * this base class, sets a static variable <code>random</code> that
     * is available to all objects. May be overriden to set an instance
     * random stream instead.
     * @see #getRandom
     * @param newRandom the new random number stream to use
     */
    public void setRandom(Random newRandom) {
        random = newRandom;
    }

    /**
     * Gets the random number stream used by this object. For this base class,
     * this is a static variable <code>random</code> available to all objects.
     * If you want to provide scapes or agents with their own
     * randoms, override this method and be sure to use it instead of simply
     * accessing the random variable.
     */
    public Random getRandom() {
        return random;
    }

    /**
     * Returns the seed for the default random number seed. Will return actual seed
     * (not the arbitrary seed symbol) if seed is set for arbitrary seed.
     */
    public long getRandomSeed() {
        return lastRandomSeed;
    }

    /**
     * Sets the seed for the default random number seed. Call or override to set
     * the seed for any instance random streams. Set to ARBITRARY_SEED to have the
     * system generate an arbitrary random seed (current time in milliseconds) at start.
     * @param seed the random number seed to use
     */
    public void setRandomSeed(long seed) {
        randomSeed = seed;
        reseed();
    }

    /**
     * Resets the random number generator. If a seed has been provided,
     * returns the number generator to its orginal state, so that the
     * last series of random numbers can be reproduced. If the random
     * seed has not been set, sets the random number generator to some
     * undetermined state (based on current system clock.)
     */
    public void reseed() {
        if (randomSeed != ARBITRARY_SEED) {
            lastRandomSeed = randomSeed;
        } else {
            lastRandomSeed = System.currentTimeMillis();
        }
        random.setSeed(lastRandomSeed);
    }

    /**
     * Generate an integer uniformly distributed across some range.
     * @param low the lowest number (inclusive) that the resulting int might be
     * @param high the hignest number (inclusive) that the resulting int might be
     * @return uniformly distributed pseudorandom int
     */
    public int randomInRange(int low, int high) {
        return random.nextInt(high - low + 1) + low;
    }

    /**
     * Generate a double uniformly distributed across some range.
     * @param low the lowest number (inclusive) that the resulting double might be
     * @param high the hignest number (exclusive) that the resulting double might be
     * @return uniformly distributed pseudorandom double
     */
    public double randomInRange(double low, double high) {
        return random.nextDouble() * (high - low) + low;
    }

    /**
     * Generate an integer uniformly distributed across 0...limit - 1.
     * @param limit the maximum limit (exclusive) of the resulting int
     * @return uniformly distributed pseudorandom int
     */
    public int randomToLimit(int limit) {
        return random.nextInt(limit);
    }

    /**
     * Returns a random boolean value.
     */
    public boolean randomIs() {
        return random.nextBoolean();
    }

    /**
     * Represents a node on the object comparison tree.
     */
    static class SearchNode {

        String pathString;
        Field field;
        private Object o1;
        private Object o2;

        SearchNode(String pathString, Field field, Object o1, Object o2) {
            this.pathString = pathString;
            this.field = field;
            this.o1 = o1;
            this.o2 = o2;
        }

        public String toString() {
            return pathString + ": " + o1 + ", " + o2;
        }

        public boolean equals(Object o) {
            return this.o1 == ((SearchNode) o).o1 && this.o2 == ((SearchNode) o).o2;
        }

        public int hashCode() {
            long hashCandidate = o1.hashCode() + o2.hashCode();
            if (hashCandidate > Integer.MAX_VALUE) {
                hashCandidate -= 2 * Integer.MAX_VALUE;
            }
            return (int) hashCandidate;
        }
    }

    /**
     * Helper method for both diffDeep mothods. Checks for node equivalence. At this point, node is known to contain two primitive objects.
     * Returns null if the nodes are equivalent, and the node's description if not.
     *
     * @param n the node in the object tree that is currently being explored
     * @return an ArrayList enumerating any differences, containing Strings of the form "field.memberField{[index]}: {object 1 value}, {object 2 value}"
     */
    protected static String diffDeepVisit(SearchNode n) {
        if (comparisonStream != null) {
            comparisonStream.print("Visiting " + n.toString() + "\n");
        }
        if (!n.o1.equals(n.o2)) {
            if (comparisonStream != null) {
                comparisonStream.print("*** Difference in " + n.toString() + " ***");
            }
            return n.toString();
        }
        return null;
    }

    /**
     * Helper method for both diffDeep mothods. Checks 'exterior' equivalence of the node pair. Do they share the sane null state, class, and or type?
     * Returns null if objects are validated, a String describing the node (and the object differences) otherwise.
     *
     * @param n the node in the object tree that is currently being explored
     * @return an ArrayList enumerating any differences, containing Strings of the form "field.memberField{[index]}: {object 1 value}, {object 2 value}"
     */
    protected static String diffDeepValidate(SearchNode n) {
        if (comparisonStream != null) {
            comparisonStream.print("Validating " + n.toString() + "\n");
        }
        if (n.o1 != null && n.o2 != null) {
            //Make sure we have an instance of the same object!
            if (n.o1.getClass() != n.o2.getClass()) {
                if (comparisonStream != null) {
                    comparisonStream.print("*** Different Class in " + n.toString() + " ***");
                }
                return n.pathString + ": " + n.o1.getClass().getName() + ", " + n.o2.getClass().getName();
            }
        } else { //Zero or one might be null, both should be
            if (n.o1 != null) {
                if (comparisonStream != null) {
                    comparisonStream.print("*** Different null status in " + n.toString() + " ***");
                }
                return n.pathString + ": " + n.o1.getClass().getName() + ", null";
            } else if (n.o2 != null) {
                if (comparisonStream != null) {
                    comparisonStream.print("*** Different null status in " + n.toString() + " ***");
                }
                return n.pathString + ": null, " + n.o2.getClass().getName();
            }
        }
        return null;
    }

    /**
     * Does a deep comparison of two objects, returning an array list containing a string description of the differences found.
     *
     * @param o1 the first object to compare
     * @param o2 the second object to compare
     * @return an ArrayList enumerating any differences, containing Strings of the form "field.memberField{[index]}: {object 1 value}, {object 2 value}"
     */
    public static ArrayList diffDeepDFS(Object o1, Object o2) {
        return diffDeepDFS(new HashSet(), new SearchNode("", null, o1, o2));
    }

    /**
     * Does a deep comparison of two objects, returning an array list containing a string description of the differences found.
     *
     * @param visitedObjects the set of objects that have allready been tested
     * @param currentNode the node in the object tree that is currently being explored
     * @return an ArrayList enumerating any differences, containing Strings of the form "field.memberField{[index]}: {object 1 value}, {object 2 value}"
     */
    private static ArrayList diffDeepDFS(HashSet visitedObjects, SearchNode currentNode) {
        ArrayList allDiffs = new ArrayList();
        String validateResult = diffDeepValidate(currentNode);
        if (validateResult == null) {
            if (currentNode.o1 != null && currentNode.o2 != null) {
                //Note for String case, we are stopping short because we don't want to inspect each element of the char array individually
                if (currentNode.field != null && (currentNode.field.getType().isPrimitive() || currentNode.field.getType().equals(String.class))) {
                    String nodeVisit = diffDeepVisit(currentNode);
                    if (nodeVisit != null) {
                        allDiffs.add(nodeVisit);
                    }
                } else if (currentNode.o1.getClass().isArray()) {  //We have an array, and need to tour its members
                    int aLength = Array.getLength(currentNode.o1);
                    if (aLength == Array.getLength(currentNode.o2)) {
                        for (int j = 0; j < aLength; j++) {  //recurse on each pair of array elements
                            SearchNode newNode = new SearchNode(currentNode.pathString + "[" + j + "]", currentNode.field, Array.get(currentNode.o1, j), Array.get(currentNode.o2, j));
                            allDiffs.addAll(diffDeepDFS(visitedObjects, newNode));
                        }
                    } else {  //Array lengths don't match
                        allDiffs.add(currentNode.pathString + ": Length " + aLength + ", Length " + Array.getLength(currentNode.o2));
                    }
                } else {  //We have a class, and need to tour the fields
                    //First make sure that the objects haven't allready been compared, thus avoiding circular refeence problem and saving time
                    SearchNode currentPair = new SearchNode("", null, currentNode.o1, currentNode.o2);
                    if (!visitedObjects.contains(currentPair)) {
                        //We need to add now, so that they are not included if discovered in any member objects
                        if (!(currentPair.o1 instanceof String) && !(currentPair.o1 instanceof Number) && !currentPair.o1.getClass().isArray()) {
                            visitedObjects.add(currentPair);
                        }

                        String className = "";
                        for (Class c = currentNode.o1.getClass(); c != null; c = c.getSuperclass(), className = c != null ? c.getName() + "." : "") {
                            Field[] allFields = c.getDeclaredFields();

                            AccessibleObject.setAccessible(allFields, true);
                            if (currentNode.pathString != "") {
                                currentNode.pathString += ".";
                            }
                            for (int i = 0; i < allFields.length; i++) {  //recurse on each pair of field contents
                                try {
                                    if (!Modifier.isFinal(allFields[i].getModifiers())) {  //we don't compare final values, saving us a lot of grief with infinite recurion
                                        SearchNode newNode = new SearchNode(currentNode.pathString + className + allFields[i].getName(), allFields[i], allFields[i].get(currentNode.o1), allFields[i].get(currentNode.o2));
                                        allDiffs.addAll(diffDeepDFS(visitedObjects, newNode));
                                    }
                                } catch (IllegalAccessException e) {
                                    System.out.println(e);
                                }
                            }
                        }
                    }
                    //Else, no point in comparing the two objects again..
                } //else, both objects are null
            }
        } else {
            allDiffs.add(validateResult);
        }
        return allDiffs;
    }

    /**
     * Does a deep comparison of two objects, returning an array list containing a string description of the differences found.
     * This version does a breadth first search, so that objects are discovered at the lowest point in the object tree.
     *
     * @param o1 the first object to compare
     * @param o2 the second object to compare
     * @return an ArrayList enumerating any differences, containing Strings of the form "field.memberField{[index]}: {object 1 value}, {object 2 value}"
     */
    public static ArrayList diffDeep(Object o1, Object o2) {
        SearchNode topNode = new SearchNode("", null, o1, o2);
        String validateResult = diffDeepValidate(topNode);
        if (validateResult == null) {
            LinkedList newList = new LinkedList();
            newList.addLast(topNode);
            return diffDeepBFS(new HashSet(), newList);
        } else {
            ArrayList allDiffs = new ArrayList();
            allDiffs.add(validateResult);
            return allDiffs;
        }
    }

    /**
     * Does a deep comparison of two objects, returning an array list containing a string description of the differences found.
     *
     * @param visitedObjects the set of objects that have allready been tested
     * @param queue the current search node queue
     * @return an ArrayList enumerating any differences, containing Strings of the form "field.memberField{[index]}: {object 1 value}, {object 2 value}"
     */
    public static ArrayList diffDeepBFS(HashSet visitedObjects, LinkedList queue) {
        ArrayList allDiffs = new ArrayList();
        while (!queue.isEmpty()) {
            SearchNode currentNode = (SearchNode) queue.removeFirst();
            if (!visitedObjects.contains(currentNode)) {
                if (currentNode.field == null || !currentNode.field.getType().isPrimitive() && !currentNode.field.getType().equals(String.class)) {
                    //We don't want to treat basic classes as the same object comparison..
                    if (!(currentNode.o1 instanceof String) && !(currentNode.o1 instanceof Number) && !currentNode.o1.getClass().isArray()) {
                        visitedObjects.add(currentNode);
                    }
                } else {
                    String nodeVisit = diffDeepVisit(currentNode);
                    if (nodeVisit != null) {
                        allDiffs.add(nodeVisit);
                    }
                }
                if (currentNode.o1.getClass().isArray()) {  //We have an array, and need to tour its members
                    int aLength = Array.getLength(currentNode.o1);
                    if (aLength == Array.getLength(currentNode.o2)) {
                        for (int j = 0; j < aLength; j++) {  //recurse on each pair of array elements
                            SearchNode candidateNode = new SearchNode(currentNode.pathString + "[" + j + "]", currentNode.field, Array.get(currentNode.o1, j), Array.get(currentNode.o2, j));
                            String validateResult = diffDeepValidate(candidateNode);
                            if (validateResult == null) {
                                if (candidateNode.o1 != null && candidateNode.o2 != null) {
                                    if (!visitedObjects.contains(candidateNode)) {
                                        queue.addLast(candidateNode);
                                    }
                                }
                            } else {
                                allDiffs.add(validateResult);
                            }
                        }
                    } else {  //Array lengths don't match
                        allDiffs.add(currentNode.pathString + ": Length " + aLength + ", Length " + Array.getLength(currentNode.o2));
                    }
                }
                if (currentNode.field == null || !currentNode.field.getType().isPrimitive() && !currentNode.field.getType().equals(String.class)) {

                    String className = "";
                    for (Class c = currentNode.o1.getClass(); c != null; c = c.getSuperclass(), className = c != null ? c.getName() + "." : "") {
                        Field[] allFields = c.getDeclaredFields();

                        AccessibleObject.setAccessible(allFields, true);
                        if (currentNode.pathString != "") {
                            currentNode.pathString += ".";
                        }
                        for (int i = 0; i < allFields.length; i++) {  //recurse on each pair of field contents
                            if (!Modifier.isFinal(allFields[i].getModifiers()) && !Modifier.isTransient(allFields[i].getModifiers())) {  //we don't compare final values, saving us a lot of grief with infinite recurion
                                //and we don't compare transients, so that we can add intrumenetation that is not compared
                                try {
                                    SearchNode candidateNode = new SearchNode(currentNode.pathString + className + allFields[i].getName(), allFields[i], allFields[i].get(currentNode.o1), allFields[i].get(currentNode.o2));
                                    String validateResult = diffDeepValidate(candidateNode);
                                    if (validateResult == null) {
                                        if (candidateNode.o1 != null && candidateNode.o2 != null) {
                                            if (!visitedObjects.contains(candidateNode)) {
                                                queue.addLast(candidateNode);
                                            }
                                        }
                                    } else {
                                        allDiffs.add(validateResult);
                                    }
                                } catch (IllegalAccessException e) {
                                    System.out.println(e);
                                }
                            }
                        }
                    }
                }
            }
        }
        return allDiffs;
    }

    /**
     * Does a deep comparison of this object with another, returning an array list containing a string description of the differences found.
     *
     * @param o the object to compare to
     * @return an ArrayList enumerating any differences, containing Strings of the form "field.memberField{[index]}: {object 1 value}, {object 2 value}"
     */
    public ArrayList diffDeep(Object o) {
        return diffDeep(this, o);
    }

    /**
     * Does a deep comparison of two objects, returning wether the two objects are deeply identical.
     *
     * @param o1 the first object to compare
     * @param o2 the second object to compare
     * @return true if the objects are deeply identical, false otherwise
     */
    public static boolean equalsDeep(Object o1, Object o2) {
        return equalsDeep(new HashSet(), new SearchNode("", null, o1, o2));
    }

    /**
     * Does a deep comparison of two objects, returning wether the two objects are deeply identical.
     *
     * @param visitedObjects a string describing the parent members of the objects (may be empty)
     * @param currentNode the node in the object tree that is currently being explored
     * @return true if the objects are deeply identical, false otherwise
     */
    public static boolean equalsDeep(HashSet visitedObjects, SearchNode currentNode) {
        String validateResult = diffDeepValidate(currentNode);
        if (validateResult == null) {
            if (currentNode.o1 != null && currentNode.o2 != null) {
                //Note for String case, we are stopping short because we don't want to inspect each element of the char array individually
                if (currentNode.field != null && (currentNode.field.getType().isPrimitive() || currentNode.field.getType().equals(String.class))) {
                    String nodeVisit = diffDeepVisit(currentNode);
                    if (nodeVisit != null) {
                        return false;
                    }
                } else if (currentNode.o1.getClass().isArray()) {  //We have an array, and need to tour its members
                    int aLength = Array.getLength(currentNode.o1);
                    if (aLength == Array.getLength(currentNode.o2)) {
                        for (int j = 0; j < aLength; j++) {  //recurse on each pair of array elements
                            boolean equal = equalsDeep(visitedObjects, new SearchNode(currentNode.pathString + "[" + j + "]", currentNode.field, Array.get(currentNode.o1, j), Array.get(currentNode.o2, j)));
                            if (!equal) {
                                return false;
                            }
                        }
                    } else {  //Array lengths don't match
                        return false;
                    }
                } else {  //We have a class, and need to tour the fields
                    //First make sure that the objects haven't allready been compared, thus avoiding circular refeence problem and saving time
                    if (!visitedObjects.contains(currentNode)) {
                        //We need to add now, so that they are not included if discovered in any member objects
                        visitedObjects.add(currentNode);

                        String className = "";
                        for (Class c = currentNode.o1.getClass(); c != null; c = c.getSuperclass(), className = c != null ? c.getName() + "." : "") {
                            Field[] allFields = c.getDeclaredFields();

                            AccessibleObject.setAccessible(allFields, true);
                            if (currentNode.pathString != "") {
                                currentNode.pathString += ".";
                            }
                            for (int i = 0; i < allFields.length; i++) {  //recurse on each pair of field contents
                                try {
                                    if (!Modifier.isFinal(allFields[i].getModifiers()) && !Modifier.isTransient(allFields[i].getModifiers())) {  //we don't compare final values, saving us a lot of grief with infinite recurion, and not transietn values so we don't get swing and reflection stuff
                                        boolean equal = equalsDeep(visitedObjects,
                                                                   new SearchNode(currentNode.pathString + className + allFields[i].getName(),
                                                                                  allFields[i], allFields[i].get(currentNode.o1), allFields[i].get(currentNode.o2)));
                                        if (!equal) {
                                            return false;
                                        }
                                    }
                                } catch (IllegalAccessException e) {
                                    System.out.println(e);
                                }
                            }
                        }
                    }
                    //Else, no point in comparing the two objects again..
                }
            }
            //Else, one or both of the objects is null
            else {
                if (currentNode.o1 == null && currentNode.o2 == null) {
                    return true;
                } else {
                    return false;
                }
            }
        } else { //Check node failed
            return false;
        }
        return true;
    }

    /**
     * Does a deep compare of the two objects. To be equal, the deep internal state of two objects must be identical, but they do not have
     * to share any of the same objects. e.g. for an object composed of other objects, those objects do not have to be the same object.
     * However, they must be of the same class, and any primitive values must be identical.
     * Warning: This equals method uses reflection and does a deep tour of the entire object's makeup; therefore, it can be very
     * time-consuming. This method is primarily designed for testing, verification, and other circumstances where runtime performacne isn't
     * critical. For simple agent comparison's, it is probably better to override this method and prvoide a hardcoded internal state comparison.
     */
    public boolean equalsDeep(Object o) {
        return equalsDeep(this, o);
    }

    /**
     * Return the stream used to capture the results of object deep comparison. If not null, the tests and results
     * of equal and diff will be sent to the stream.
     */
    public static PrintStream getComparisonStream() {
        return comparisonStream;
    }

    /**
     * Sets the stream used to capture the results of object deep comparison. If not null, the tests and results
     * of equal and diff will be sent to the stream.
     */
    public static void setComparisonStream(PrintStream comparisonStream) {
        AscapeObject.comparisonStream = comparisonStream;
    }

    /**
     * Clones this object. Typically should be overridden to create a copy that is deep
     * with respect to its internal state (for mutables), shares parent and 'context' data with the
     * original, and clears any positional or relational state that might create a resulting
     * illegal state in a parent or peer. For example, a cloned cell with a memory vector would
     * copy the memory vector, retain the original's scape, and clear its coordinate data (because
     * it is usually not legal for two cells to exists at the same coordinate.)
     * Does not throw CloneNotSupportedException, since all ascape objects should support it.
     */
    public Object clone() {
        try {
            AscapeObject clone = (AscapeObject) super.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }

    /**
     * A string representing this object.
     */
    public String toString() {
        if (name != null) {
            return name;
        } else {
            return "An ascape object";
        }
    }
}

