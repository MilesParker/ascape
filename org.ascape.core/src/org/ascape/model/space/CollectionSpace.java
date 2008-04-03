/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.space;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Random;

import org.ascape.util.Conditional;
import org.ascape.util.Conditionals;
import org.ascape.util.RandomIterator;
import org.ascape.util.ResetableIterator;
import org.ascape.util.data.DataPoint;
import org.ascape.util.data.DataPointConcrete;

/**
 * The Class CollectionSpace.
 */
public class CollectionSpace implements Space, Cloneable, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * The estimated maximum size a typical collection will be.
     */
    public static int ESTIMATED_MAXIMUM_SIZE = 100;

    /**
     * The backing collection.
     */
    protected Collection collection;

    /**
     * Have there been remove operations on the collection that require a later
     * deletion sweep?.
     */
    private boolean deleteSweepNeeded;

    /**
     * The context.
     */
    private SpaceContext context;

    /**
     * The random.
     */
    private Random random;

    /**
     * The basic geometric structure of this collection.
     */
    protected Geometry geometry;

    /**
     * The extent (furthest valid point) of the context. This parameter will
     * make sense for some scapes, but not for others.
     */
    protected Coordinate extent;

    // protected class CSMutableIterator extends CSIterator implements
    // Serializable {
    /**
     * The Class CSMutableIterator.
     */
    protected class CSMutableIterator extends CSIterator {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;
        /**
         * The i.
         */
        int i;

        /*
         * (non-Javadoc)
         * 
         * @see org.ascape.model.space.CollectionSpace.CSIterator#hasNext()
         */
        public final boolean hasNext() {
            while (copyIterator.hasNext()) {
                if (!((Location) copy.get(copyIterator.nextIndex())).isDelete()) {
                    return true;
                } else {
                    copyIterator.next();
                }
            }
            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.ascape.model.space.CollectionSpace.CSIterator#remove()
         */
        public final void remove() {
            Object removeObject = this.copy.get(i);
            // The backing collection may not be deleted immeadiatly..
            CollectionSpace.this.collection.remove(removeObject);
            // However, we do want to remove the item from the clone collection
            // immediatly
            copy.remove(removeObject);
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.ascape.model.space.CollectionSpace.CSIterator#next()
         */
        public final Object next() {
            i++;
            Object candidate = copyIterator.next();
            while (((Location) candidate).isDelete()) {
                candidate = copyIterator.next();
            }
            return candidate;
        }
    }

    /**
     * The Class CSIterator.
     */
    protected class CSIterator implements ResetableIterator, Serializable {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        /**
         * The copy.
         */
        protected List copy;

        /**
         * The copy iterator.
         */
        protected transient ListIterator copyIterator;

        /**
         * Instantiates a new CS iterator.
         */
        public CSIterator() {
            copy = new ArrayList(collection);
            copyIterator = copy.listIterator();
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.ascape.util.ResetableIterator#first()
         */
        public void first() {
            // Just create a new iterator..
            copyIterator = copy.listIterator();
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.util.Iterator#hasNext()
         */
        public boolean hasNext() {
            return copyIterator.hasNext();
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.util.Iterator#remove()
         */
        public void remove() {
            throw new UnsupportedOperationException("Can't remove an agent from an immutable context!");
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.util.Iterator#next()
         */
        public Object next() {
            return copyIterator.next();
        }
    }

    /**
     * The Class SubIterator.
     */
    protected class SubIterator extends CSIterator {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        /**
         * Instantiates a new sub iterator.
         * 
         * @param start
         *            the start
         * @param limit
         *            the limit
         */
        public SubIterator(int start, int limit) {
            copy = new ArrayList(((AbstractList) collection).subList(start, limit));
            copyIterator = copy.listIterator();
        }
    }

    /**
     * The Class MutableSubIterator.
     */
    protected class MutableSubIterator extends CSMutableIterator {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        /**
         * Instantiates a new mutable sub iterator.
         * 
         * @param start
         *            the start
         * @param limit
         *            the limit
         */
        public MutableSubIterator(int start, int limit) {
            deleteSweep();
            copy = new ArrayList(((AbstractList) collection).subList(start, limit));
            copyIterator = copy.listIterator();
        }
    }

    /**
     * The Class ListMutableRandomIterator.
     */
    protected class ListMutableRandomIterator extends CSMutableIterator implements RandomIterator {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        /**
         * Instantiates a new list mutable random iterator.
         */
        public ListMutableRandomIterator() {
            super();
            randomize();
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.ascape.util.Randomizable#randomize()
         */
        public void randomize() {
            // The Java API shuffle appears to be correct (follows Knuth's
            // algorithm), so we will use it here
            Collections.shuffle(copy, getRandom());
        }
    }

    /**
     * The Class ListRandomIterator.
     */
    protected class ListRandomIterator extends CSIterator implements RandomIterator {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        /**
         * Instantiates a new list random iterator.
         */
        public ListRandomIterator() {
            super();
            randomize();
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.ascape.util.Randomizable#randomize()
         */
        public void randomize() {
            // The Java API shuffle appears to be correct (follows Knuth's
            // algorithm), so we will use it here
            Collections.shuffle(copy, getRandom());
        }
    }

    /**
     * Instantiates a new collection space.
     */
    public CollectionSpace() {
        collection = new ArrayList();
        extent = new Coordinate1DDiscrete(0);
        geometry = new Geometry(1, false, false, true, Geometry.NOT_APPLICABLE);
    }

    /**
     * Walks through each agent, deleting it if it has been marked for deletion.
     */
    public void deleteSweep() {
        if (deleteSweepNeeded) {
            // Generally quicker to construct a new array collection than to
            // delete from the existing one!
            ArrayList newList = new ArrayList();
            // We want the internal iterator, that is the iterator before a
            // delete sweep has occurred
            Iterator i = collection.iterator();
            while (i.hasNext()) {
                Location candidate = (Location) i.next();
                if (!candidate.isDelete() || !getContext().isHome(candidate)) {
                    newList.add(candidate);
                }
            }
            collection = newList;
            deleteSweepNeeded = false;
        }
    }

    /**
     * Creates a new agent in this collection by cloning the prototype agent,
     * adding it to the end of the vector, and initializing it.
     * 
     * @return the location
     */
    public synchronized Location newLocation() {
        return newLocation(false);
    }

    /**
     * Creates a new agent in this collection by cloning the prototype agent,
     * adding it to a random or arbitrary (last in this case) place in the
     * collection, and initializing it.
     * 
     * @param randomLocation
     *            should the agent be placed in a random location, or in an
     *            arbitrary location?
     * @return the location
     */
    public synchronized Location newLocation(boolean randomLocation) {
        Location newLocation = (Location) getContext().getPrototype().clone();
        // Rather than override entire new Location method
        if (!randomLocation || !(this instanceof ListBase)) {
            add(newLocation);
        } else {
            ((ListBase) this).add(randomToLimit(size()), newLocation);
        }
        newLocation.initialize();
        return newLocation;
    }

    /**
     * Are there no agents in this context?.
     * 
     * @return true if the context is empty
     */
    public boolean isEmpty() {
        deleteSweep();
        return collection.isEmpty();
    }

    /**
     * Returns true if the context collection contains the object (agent.)
     * 
     * @param o
     *            the agent to search for
     * @return true if the context contains the agent
     */
    public boolean contains(Object o) {
        // Make sure the agent (if it is an agent) has not been deleted from
        // this collection..
        if (!(o instanceof Location) || !((Location) o).isDelete() || !getContext().isHome((Location) o)) {
            return collection.contains(o);
        } else {
            return false;
        }
    }

    /**
     * Returns an array containing all of the elements in this collection in
     * proper sequence. Obeys the general contract of the
     * <tt>Collection.toArray</tt> method.
     * 
     * @return an array containing all of the elements in this collection in
     *         proper sequence.
     * @see java.util.Arrays#asList(java.lang.Object[])
     */
    public Object[] toArray() {
        deleteSweep();
        return collection.toArray();
    }

    /**
     * Returns an array containing the current agents in this context; the
     * runtime type is specified by the passed array.
     * 
     * @param a
     *            the array to copy the agents to
     * @return an array containing the agents
     * @throws java.lang.ArrayStoreException
     *             if the runtime type of the specified array doesn't match all
     *             agents
     */
    public Object[] toArray(Object a[]) {
        deleteSweep();
        return collection.toArray(a);
    }

    /**
     * Returns true if this collection contains all of agents in the specified
     * collection.
     * 
     * @param c
     *            collection of agents to be found in the context
     * @return true if this context contains all of the agents in the collection
     */
    public boolean containsAll(java.util.Collection c) {
        deleteSweep();
        return containsAll(c);
    }

    /**
     * Adds all of the agent in the specified collection to the end of the
     * context. Assumes (but does not check) that all of the elements are
     * instances of agent.
     * 
     * @param c
     *            collection whose agents are to be added to the context
     * @return true if the context had new agents added
     */
    public boolean addAll(java.util.Collection c) {
        boolean addedAll = collection.addAll(c);
        setSize(getSize() + c.size());
        return addedAll;
    }

    /**
     * Helper method to clear collections agent's coordinates. Used when
     * deleting agents from the context.
     * 
     * @param collection
     *            the collection to clear coordinates in
     */
    private void clearCoordinates(java.util.Collection collection) {
        Iterator iter = collection.iterator();
        while (iter.hasNext()) {
            Location a = (Location) iter.next();
            if (getContext().isHome(a) && (a instanceof Node)) {
                ((Node) a).setCoordinate(null);
            }
        }
    }

    /**
     * Removes all of the agents contained in the collection. No attempt is made
     * to cache the removal; the agents are all removed at once.
     * 
     * @param c
     *            collection whose agents are to be added to the context
     * @return true if the context had agents (but not neccessarily all?)
     *         removed
     */
    public boolean removeAll(java.util.Collection c) {
        clearCoordinates(c);
        // We don't know how many agents were in either collections, so we have
        // to do a sweep to make sure we have the right size
        deleteSweep();
        setSize(collection.size());
        return collection.removeAll(c);
    }

    /**
     * Retains only the elements in the context that are in the specified
     * collection.
     * 
     * @param c
     *            collection whose agents are to be retained in the context
     * @return true if this context had agents removed
     */
    public boolean retainAll(java.util.Collection c) {
        Iterator iter = iterator();
        while (iter.hasNext()) {
            Location a = (Location) iter.next();
            // Probably not terribly efficient; potential reengineer candidate
            // if this method gets a lot of use
            if (getContext().isHome(a) && (a instanceof Node)) {
                if (!c.contains(a)) {
                    ((Node) a).setCoordinate(null);
                }
            }
        }
        boolean retainedAll = collection.retainAll(c);
        // We don't know how many agents were in both collections, so we have to
        // do a sweep to make sure we have the right size
        deleteSweep();
        setSize(collection.size());
        return retainedAll;
    }

    /**
     * Removes all agents from the context.
     */
    public void clear() {
        clearCoordinates(collection);
        collection = new ArrayList();
        setSize(0);
    }

    /**
     * Adds the supplied object (agent) to this collection.
     * 
     * @param a
     *            the a
     * @return true, if add
     */
    public boolean add(Object a) {
        return add(a, true);
    }

    /**
     * Adds the supplied object to this collection. The object is assumed to be
     * an agent, though that behavior may be loosened at some point.
     * 
     * @param o
     *            the agent to add
     * @param isParent
     *            should this context be made the parent context of the agent?
     * @return true, if add
     * @throws java.lang.ClassCastException
     *             if the object is not an instance of agent
     */
    public boolean add(Object o, boolean isParent) {
        boolean added = false;
        if (isParent && (o instanceof Location) && ((Location) o).isDelete() && getContext().isHome((Location) o)) {
            ((Location) o).clearDeleteMarker();
        } else {
            added = collection.add(o);
        }
        // setSize(getSize() + 1);
        setSize(getSize() + 1);
        return added;
    }

    /**
     * Removes the supplied object (agent) from this collection.
     * 
     * @param o
     *            the agent to be removed
     * @return true if the agent was deleted, false otherwise
     */
    public boolean remove(Object o) {
        if ((o instanceof Location) && getContext().isHome((Location) o)) {
            deleteSweepNeeded = true;
            if (!((Location) o).isDelete()) {
                ((Location) o).markForDeletion();
                if (o instanceof Node) {
                    ((Node) o).setCoordinate(null);
                }
                if (collection.contains(o)) {
                    setSize(getSize() - 1);
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            boolean success = collection.remove(o);
            if (success) {
                setSize(getSize() - 1);
            }
            return success;
        }
    }

    /**
     * Returns an iterator across all agents in this context. Note that this is
     * simply an iterator of the backing collections members. It will have
     * different behavior than is typically desried when iterating behavior
     * across a context*; so for instance, this method is not used by the
     * internal rule mechanism. It should be perfectly adequete for tight
     * iterations across agents when there are no additions or deletions during
     * the iteration; for instance, when calcualting some value across a number
     * of agents. *The iterator will not be aware of an agents deletion from the
     * context after its creation; this is because the context caches these
     * removals to improve performance. It may include agents that are added to
     * the context after its creation, and this is typically not desirable
     * behavior when touring a collection of current agents.
     * 
     * @return an iterator over the agents in context order
     */
    public Iterator iterator() {
        deleteSweep();
        return collection.iterator();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ascape.model.space.Space#safeIterator(int, int)
     */
    public ResetableIterator safeIterator(int start, int limit) {
        if (collection instanceof List) {
            if (isMutable()) {
                return new CollectionSpace.MutableSubIterator(start, limit);
            } else {
                return new CollectionSpace.SubIterator(start, limit);
            }
        } else {
            throw new UnsupportedOperationException(
                    "The collection must implement list, or the space must provide its own implementation of context iterator.");
        }
    }

    /**
     * Is the context mutable, that is, can it change its structure at runtime?
     * Returns true for this collection.
     * 
     * @return true, if is mutable
     */
    public boolean isMutable() {
        return true;
    }

    /**
     * Returns multiple independently thread safe context iterators across all
     * agents in this context.
     * 
     * @param count
     *            the count
     * @return an iterator over the agents in context order
     */
    public ResetableIterator[] safeIterators(int count) {
        deleteSweep();
        int start = 0;
        int increment = size() / count;
        ResetableIterator[] iterators = new ResetableIterator[count];
        int i = 0;
        for (; i < iterators.length - 1; i++) {
            iterators[i] = safeIterator(start, start + increment);
            start += increment;
        }
        iterators[i] = safeIterator(start, size());
        return iterators;
    }

    /**
     * Moves an agent toward the specified agent.
     * 
     * @param origin
     *            the agent moving
     * @param target
     *            the agent's target
     * @param distance
     *            the distance to move
     */
    public void moveAway(Location origin, Coordinate target, double distance) {
        throw new UnsupportedOperationException("Tried to use move toward within a non-supporting context.");
    }

    /**
     * Moves an agent toward the specified agent. It is an error to call this
     * method on collections (and discrete discrete scapes not composed of
     * HostCells.
     * 
     * @param origin
     *            the agent moving
     * @param target
     *            the agent's target
     * @param distance
     *            the distance to move
     */
    public void moveToward(Location origin, Coordinate target, double distance) {
        throw new UnsupportedOperationException("Tried to use move toward within a non-supporting context.");
    }

    /**
     * Returns the shortest distance between one agent and another.
     * 
     * @param origin
     *            the starting agent
     * @param target
     *            the ending agent
     * @return the double
     */
    public double calculateDistance(Location origin, Location target) {
        return calculateDistance(origin.getCoordinate(), target.getCoordinate());
    }

    /**
     * Returns the shortest distance between one Location and another. Warning:
     * this default method only returns a coordinate specific distance. It uses
     * no information about the context context; for example wether it is a
     * periodic (wrapping) space or not. Therefore, if you implement your own
     * versions of CollectionSpace, ensure that you have properly implemented a
     * version of this method. (All Ascape spaces properly overide this method.)
     * 
     * @param origin
     *            one Location
     * @param target
     *            another Location
     * @return the double
     */
    public double calculateDistance(Coordinate origin, Coordinate target) {
        return origin.getDistance(target);
    }

    /**
     * The Class ConditionalIterator.
     */
    public static class ConditionalIterator implements Iterator {

        /**
         * The iter.
         */
        Iterator iter;

        /**
         * The condition.
         */
        Conditional condition;

        /**
         * The next.
         */
        Object next;

        /**
         * Instantiates a new conditional iterator.
         * 
         * @param iter
         *            the iter
         * @param condition
         *            the condition
         */
        public ConditionalIterator(Iterator iter, Conditional condition) {
            CollectionSpace.ConditionalIterator.this.iter = iter;
            CollectionSpace.ConditionalIterator.this.condition = condition;
            loadNext();
        }

        /**
         * Load next.
         */
        private void loadNext() {
            next = null;
            while (iter.hasNext() && (next == null)) {
                Object o = iter.next();
                if (condition.meetsCondition(o)) {
                    next = o;
                }
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.util.Iterator#hasNext()
         */
        public boolean hasNext() {
            return next != null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.util.Iterator#next()
         */
        public Object next() {
            if (next != null) {
                Object currentNext = next;
                loadNext();
                return currentNext;
            } else {
                throw new NoSuchElementException();
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.util.Iterator#remove()
         */
        public void remove() {
            throw new UnsupportedOperationException("Can't remove from a conditional iterator.");
        }
    }

    /**
     * Finds all locations with specified condition.
     * 
     * @param condition
     *            the condition to search locations for
     * @return a list of found locations
     */
    public List find(Conditional condition) {
        return filter(this, condition);
    }

    /**
     * Find the maximum cell of some data point. If multiple points have the
     * same value, returns a random instance at that value.
     * 
     * @param iter
     *            the iter
     * @param dataPoint
     *            the data point
     * @return the location
     */
    public Location findMaximum(final Iterator iter, DataPoint dataPoint) {
        // The code is written in such a way that there will not be the cost of
        // Array creation if only one maximum exists
        ArrayList multipleMaxObjects = null;
        double maxValue = -Double.MAX_VALUE;
        Location maxObject = null;
        while (iter.hasNext()) {
            Object next = iter.next();
            if (dataPoint.getValue(next) > maxValue) {
                maxValue = dataPoint.getValue(next);
                maxObject = (Location) next;
                multipleMaxObjects = null;
            }
            // Awaiting decision to become depndent on 1.4
            // else if (Double.compare(dataPoint.getValue(next), maxValue) == 0)
            // {
            else if (DataPointConcrete.equals(dataPoint.getValue(next), maxValue)) {
                if (multipleMaxObjects == null) {
                    multipleMaxObjects = new ArrayList();
                    multipleMaxObjects.add(maxObject);
                }
                multipleMaxObjects.add(next);
            }
        }
        if (multipleMaxObjects == null) {
            return maxObject;
        } else {
            return (Location) multipleMaxObjects.get(randomToLimit(multipleMaxObjects.size()));
        }
    }

    /**
     * The Class ClosestDataPoint.
     */
    public class ClosestDataPoint extends DataPointConcrete {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;
        /**
         * The origin.
         */
        Coordinate origin;

        /**
         * Instantiates a new closest data point.
         * 
         * @param origin
         *            the origin
         */
        public ClosestDataPoint(Coordinate origin) {
            super("Closest Point");
            this.origin = origin;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.ascape.util.data.DataPointConcrete#getValue(java.lang.Object)
         */
        public double getValue(Object o) {
            return calculateDistance(origin, ((Location) o).getCoordinate());
        }
    }

    /**
     * Finds the nearest agent that meets some condition. Spaces without
     * coordinate meaing should override this method.
     * 
     * @param origin
     *            the coordinate to find agents near
     * @param condition
     *            the condition that found agent must meet
     * @param includeOrigin
     *            if the origin should be included
     * @param distance
     *            the maximum distance around the origin to look
     * @return the location
     */
    public Location findNearest(final Coordinate origin, Conditional condition, boolean includeOrigin, double distance) {
        return findMinimum(withinIterator(origin, condition, includeOrigin, distance), new ClosestDataPoint(origin));
    }
    
    /**
     * Finds the nearest agent that meets some condition. Spaces without
     * coordinate meaing should override this method.
     * 
     * @param origin
     *            the lcoation to find agents near
     * @param condition
     *            the condition that found agent must meet
     * @param includeOrigin
     *            if the origin should be included
     * @param distance
     *            the maximum distance around the origin to look
     * @return the location
     */
    public Location findNearest(Location origin, Conditional condition, boolean includeOrigin, double distance) {
        return findNearest(origin.getCoordinate(), condition, includeOrigin, distance);
    }

    /**
     * Returns a coordinate randomly selected from the collection's space.
     * 
     * @return the coordinate
     */
    public Coordinate findRandomCoordinate() {
        // todo we'll need to replace with shuffle if we don't have a collection
        return new Coordinate1DDiscrete(randomToLimit(collection.size()));
    }

    /**
     * Iterator to list.
     * 
     * @param iter
     *            the iter
     * @return the list
     */
    public static List iteratorToList(Iterator iter) {
        ArrayList l = new ArrayList();
        while (iter.hasNext()) {
            l.add(iter.next());
        }
        return l;
    }

    /**
     * Iterator count.
     * 
     * @param iter
     *            the iter
     * @return the int
     */
    public static int iteratorCount(Iterator iter) {
        int count = 0;
        while (iter.hasNext()) {
            count++;
            iter.next();
        }
        return count;
    }

    /**
     * Returns all agents within the specified distance of the agent.
     * 
     * @param origin
     *            the coordinate at the center of the search
     * @param includeSelf
     *            whether or not the starting agent should be included in the
     *            search
     * @param distance
     *            the distance agents must be within to be included
     * @param condition
     *            the condition
     * @return the list
     */
    public List findWithin(Coordinate origin, Conditional condition, boolean includeSelf, double distance) {
        return iteratorToList(withinIterator(origin, condition, includeSelf, distance));
    }

    /**
     * Returns the number of agents within the specified distance of the agent
     * that meet some condition.
     * 
     * @param origin
     *            the coordinate at the center of the search
     * @param condition
     *            the condition the agent must meet to be included
     * @param distance
     *            the distance agents must be within to be included
     * @param includeSelf
     *            the include self
     * @return the int
     */
    public int countWithin(Coordinate origin, Conditional condition, boolean includeSelf, double distance) {
        return iteratorCount(withinIterator(origin, condition, includeSelf, distance));
    }

    /**
     * Returns if there are agents within the specified distance of the origin
     * that meet some Condition.
     * 
     * @param origin
     *            the coordinate at the center of the search
     * @param condition
     *            the condition the agent must meet to be included
     * @param distance
     *            the distance agents must be within to be included
     * @param includeSelf
     *            the include self
     * @return true, if has within
     */
    public boolean hasWithin(final Coordinate origin, Conditional condition, boolean includeSelf, double distance) {
        return withinIterator(origin, condition, includeSelf, distance).hasNext();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ascape.model.space.Space#findMinimumWithin(org.ascape.model.space.Coordinate,
     *      org.ascape.util.data.DataPoint, org.ascape.util.Conditional,
     *      boolean, double)
     */
    public Location findMinimumWithin(Coordinate coordinate, DataPoint dataPoint, Conditional condition,
            boolean includeSelf, double distance) {
        return findMinimum(withinIterator(coordinate, condition, includeSelf, distance), dataPoint);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ascape.model.space.Space#findMaximumWithin(org.ascape.model.space.Coordinate,
     *      org.ascape.util.data.DataPoint, org.ascape.util.Conditional,
     *      boolean, double)
     */
    public Location findMaximumWithin(Coordinate coordinate, DataPoint dataPoint, Conditional condition,
            boolean includeSelf, double distance) {
        return findMaximum(withinIterator(coordinate, condition, includeSelf, distance), dataPoint);
    }

    /**
     * Find minimum.
     * 
     * @param iter
     *            the iter
     * @param dataPoint
     *            the data point
     * @return the location
     */
    public Location findMinimum(final Iterator iter, DataPoint dataPoint) {
        // The code is written in such a way that there will not be the cost of
        // Array creation if only one minimum exists
        ArrayList multipleMinObjects = null;
        double minValue = Double.MAX_VALUE;
        Location minObject = null;
        while (iter.hasNext()) {
            Object next = iter.next();
            if (dataPoint.getValue(next) < minValue) {
                minValue = dataPoint.getValue(next);
                minObject = (Location) next;
                multipleMinObjects = null;
            }
            // Awaiting decision to become depndent on 1.4
            // else if (Double.compare(dataPoint.getValue(next), minValue) == 0)
            // {
            else if (DataPointConcrete.equals(dataPoint.getValue(next), minValue)) {
                if (multipleMinObjects == null) {
                    multipleMinObjects = new ArrayList();
                    multipleMinObjects.add(minObject);
                }
                multipleMinObjects.add(next);
            }
        }
        if (multipleMinObjects == null) {
            return minObject;
        } else {
            return (Location) multipleMinObjects.get(randomToLimit(multipleMinObjects.size()));
        }
    }

    /**
     * Returns a context iterator across all objects in this context. This
     * iterator differs from a collection iterator in two fundamental ways.
     * First, it handles deletions and adds differently. It does not include any
     * objects that are added after the iterator has been created, and it
     * correctly deals with the case where agents are removed from the context
     * after the creation of the iterator. (As such deletions are marked for a
     * later delete sweep, but not actually removed the iterator has to be aware
     * of them.) Second, it allows these deletions to occur after an iteration
     * has been created, without throwing a ConcurrentModificationExcpetion on a
     * <tt>next</tt> (or similar) call. Such modifications (say during the
     * execution of fission or death behaviors) during a particular behavior are
     * expected. The important thing is that objects that have been removed from
     * a context do not have rules executed upon them, and that new agents added
     * to a context do not have behavior executed on them in the current
     * iteration. This iterator ensures that.
     * 
     * @return an iterator over the agents in context order
     */
    public ResetableIterator safeIterator() {
        if (collection instanceof List) {
            if (isMutable()) {
                return new CSMutableIterator();
            } else {
                return new CSIterator();
            }
        } else {
            throw new UnsupportedOperationException(
                    "The collection must implement list, or the space must provide its own implementation of context iterator.");
        }
    }

    /**
     * Returns a context iterator across all agents in random order. Other than
     * its random order, it has identical behavior to context iterator; see the
     * important notes for that method.
     * 
     * @return an iterator over the agents in context order
     */
    public RandomIterator safeRandomIterator() {
        if (collection instanceof List) {
            if (isMutable()) {
                return new ListMutableRandomIterator();
            } else {
                return new ListRandomIterator();
            }
        } else {
            throw new UnsupportedOperationException(
                    "The collection must implement list, or the space must provide its own implementation of context iterator.");
        }
    }

    /**
     * Conditional iterator.
     * 
     * @param iter
     *            the iter
     * @param condition
     *            the condition
     * @return the iterator
     */
    public static Iterator conditionalIterator(final Iterator iter, final Conditional condition) {
        return new CollectionSpace.ConditionalIterator(iter, condition);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ascape.model.space.Space#conditionalIterator(org.ascape.util.Conditional)
     */
    public Iterator conditionalIterator(final Conditional condition) {
        return new CollectionSpace.ConditionalIterator(safeIterator(), condition);
    }

    /**
     * Filter.
     * 
     * @param list
     *            the list
     * @param condition
     *            the condition
     * @return the list
     */
    public static List filter(Collection list, Conditional condition) {
        if (condition != null) {
            List result = new ArrayList();
            for (Iterator iterator = list.iterator(); iterator.hasNext();) {
                Object o = iterator.next();
                if (condition.meetsCondition(o)) {
                    result.add(o);
                }
            }
            return result;
        } else {
            return new ArrayList(list);
        }
    }

    /**
     * Returns an iteration across all agents the specified distance from the
     * origin.
     * 
     * @param origin
     *            the starting cell
     * @param includeSelf
     *            should the origin be included
     * @param distance
     *            the distance agents must be within to be included
     * @param condition
     *            the condition
     * @return the iterator
     */
    public Iterator withinIterator(final Coordinate origin, Conditional condition, boolean includeSelf,
            final double distance) {
        Conditional allConditions = createSpatialConditional(origin, condition, includeSelf, distance);
        if (allConditions != null) {
            return conditionalIterator(iterator(), allConditions);
        } else {
            return iterator();
        }
    }

    /**
     * Creates the spatial conditional.
     * 
     * @param origin
     *            the origin
     * @param condition
     *            the condition
     * @param includeSelf
     *            the include self
     * @param distance
     *            the distance
     * @return the conditional
     */
    protected Conditional createSpatialConditional(final Coordinate origin, Conditional condition, boolean includeSelf,
            final double distance) {
        // Wrap all of the conditionals together to save execution time.
        Conditional allConditions = null;
        if (distance < Double.MAX_VALUE) {
            allConditions = new Conditional() {
                /**
                 * 
                 */
                private static final long serialVersionUID = 1L;

                public boolean meetsCondition(Object o) {
                    return calculateDistance(origin, ((Location) o).getCoordinate()) <= distance;
                }
            };
        }
        // Save the supplied condtional till now as we do not know its cost
        allConditions = Conditionals.and(allConditions, condition);
        // Least common case
        if (!includeSelf) {
            allConditions = Conditionals.and(allConditions, new Conditional() {
                /**
                 * 
                 */
                private static final long serialVersionUID = 1L;

                public boolean meetsCondition(Object object) {
                    return !((Location) object).getCoordinate().equals(origin);
                }
            });
        }
        return allConditions;
    }

    /**
     * Returns an agent randomly selected from the collection. If no agents
     * exist, returns null.
     * 
     * @return the location
     */
    public Location findRandom() {
        if (collection instanceof List) {
            if (getSize() > 0) {
                Location candidate;
                do {
                    candidate = (Location) ((List) collection).get(randomToLimit(collection.size()));
                } while (candidate.isDelete());
                return candidate;
            } else {
                return null;
            }
        } else {
            throw new UnsupportedOperationException(
                    "Can't find random within a bare collection. You must use a list or provide a find random method.");
        }
    }

    /**
     * Returns a random unoccupied discrete location in the space given with the
     * lattice.
     * 
     * @param excludeLocation
     *            a cell to exclude from get (typically origin)
     * @return the location
     */
    public Location findRandom(Location excludeLocation) {
        Location randomLocation;
        do {
            randomLocation = findRandom();
        } while (randomLocation == excludeLocation);
        return randomLocation;
    }

    /**
     * Returns an agent randomly that matches a condition. Note: If there are no
     * agents in the collection that meet the condition, the method returns
     * null.
     * 
     * @param condition
     *            the condition that must be matched
     * @return the location
     */
    public Location findRandom(Conditional condition) {
        Iterator it = safeRandomIterator();
        while (it.hasNext()) {
            Object agent = it.next();
            if (condition.meetsCondition(agent)) {
                return (Location) agent;
            }
        }
        return null;
    }

    /**
     * Returns an agent randomly that matches a condition, excluding the
     * coordinate. Note: If there are no agents in the collection that meet the
     * condition, the method returns null.
     * 
     * @param condition
     *            the condition that must be matched
     * @return the location
     */
    public Location findRandom(Location exclude, Conditional condition) {
        Iterator it = safeRandomIterator();
        while (it.hasNext()) {
            Object agent = it.next();
            if (condition.meetsCondition(agent) && ((Location) agent).getCoordinate() != exclude.getCoordinate()) {
                return (Location) agent;
            }
        }
        return null;
    }
    
    public Location findRandomWithin(Location origin, Conditional condition, boolean includeSelf, double distance) {
        List locations = findWithin(origin.getCoordinate(), condition, includeSelf, distance);
        if (!locations.isEmpty()) {
            return (Location) locations.get(randomToLimit(locations.size()));
        }
        return null;
    }

    /**
     * Returns the agent with the minimum value.
     * 
     * @param point
     *            the data point to use to make the comparison for minimum
     * @return the location
     */
    public Location findMinimum(DataPoint point) {
        return findMinimum(iterator(), point);
    }

    /**
     * Returns the agent with the maximum value.
     * 
     * @param point
     *            the data point to use to make the comparison for maximum
     * @return the location
     */
    public Location findMaximum(DataPoint point) {
        return findMaximum(iterator(), point);
    }

    /**
     * Returns the cell existing at the specified coordinate.
     * 
     * @param coordinate
     *            the coordinate
     * @return the location
     */
    public Location get(Coordinate coordinate) {
        try {
            return (Location) ((List) collection).get(((Coordinate1DDiscrete) coordinate).getXValue());
        } catch (ClassCastException e) {
            throw new UnsupportedOperationException(
                    "The underlying space is not a list and cannot be accessed randomly.");
        }
    }

    /**
     * Sets the agent at the specified coordinate to the supplied agent.
     * 
     * @param coordinate
     *            the coordinate to add the agent at
     * @param agent
     *            the agent to add
     */
    public void set(Coordinate coordinate, Location agent) {
        try {
            ((List) collection).set(((Coordinate1DDiscrete) coordinate).getXValue(), agent);
        } catch (ClassCastException e) {
            throw new UnsupportedOperationException(
                    "The underlying space is not a list and cannot be accessed randomly.");
        }
    }

    /**
     * Sets the size of the collection. It is an error to set extent while a
     * context is running.
     * 
     * @param size
     *            the size of this context
     */
    public void setExtent(int size) {
        setSize(size);
    }

    /**
     * Returns the number of agents in the context.
     * 
     * @return the number of agents in the context
     */
    public int size() {
        return getSize();
    }

    /**
     * Returns the size, or number of agents, of this space.
     * 
     * @return the size
     */
    public int getSize() {
        return ((Coordinate1DDiscrete) extent).getXValue();
        // return vector.size();
    }

    /**
     * Returns the extent of the context. The extent can be thought of as the
     * most extreme point in the context. For discrete context's this will
     * simply be the furthest cell, so that for a 20x20 grid, the extent would
     * be {20, 20}. For continuous spaces it will be the maximum boundary of the
     * space. For lists, it will be the size of lists. Therefore, this method
     * should net be confused with the context's "size". Note that context
     * graphs will not have useful extents, but all other scapes do.
     * 
     * @return the extent
     */
    public Coordinate getExtent() {
        return extent;
    }

    /**
     * Sets the size of the context. Note that context graphs will not have
     * useful extents, but all other scapes do. It is an error to set extent
     * while a context is running.
     * 
     * @param extent
     *            a coordinate at the maximum extent
     */
    public void setExtent(Coordinate extent) {
        this.extent = extent;
    }

    /**
     * Is a delete sweep needed for this context? Intended for internal
     * purposes.
     * 
     * @return true, if is delete sweep needed
     */
    public boolean isDeleteSweepNeeded() {
        return deleteSweepNeeded;
    }

    /**
     * Sets the size of the collection. This method should be used to add or
     * remove agents from the collection, caused by fissioning or the death of
     * an agent.
     * 
     * @param internalSize
     *            the new size of the collection
     */
    public void setSize(int internalSize) {
        ((Coordinate1DDiscrete) getExtent()).setXValue(internalSize);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ascape.model.space.Space#construct()
     */
    public void construct() {
        collection = new ArrayList(ESTIMATED_MAXIMUM_SIZE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ascape.model.space.Space#populate()
     */
    public void populate() {
        if (extent != null) {
            // Use an array to and then dump all at once to collectioon for
            // better performance.
            Location[] newPopulation = new Location[getSize()];
            for (int i = 0; i < newPopulation.length; i++) {
                newPopulation[i] = (Location) getContext().getPrototype().clone();
            }
            collection.clear();
            collection.addAll(Arrays.asList(newPopulation));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ascape.model.space.Space#initialize()
     */
    public void initialize() {
    }

    /**
     * Generate an integer uniformly distributed across some range.
     * 
     * @param low
     *            the lowest number (inclusive) that the resulting int might be
     * @param high
     *            the hignest number (inclusive) that the resulting int might be
     * @return uniformly distributed pseudorandom int
     */
    public int randomInRange(int low, int high) {
        return random.nextInt(high - low + 1) + low;
    }

    /**
     * Generate a double uniformly distributed across some range.
     * 
     * @param low
     *            the lowest number (inclusive) that the resulting double might
     *            be
     * @param high
     *            the hignest number (exclusive) that the resulting double might
     *            be
     * @return uniformly distributed pseudorandom double
     */
    public double randomInRange(double low, double high) {
        return (random.nextDouble() * (high - low)) + low;
    }

    /**
     * Generate an integer uniformly distributed across 0...limit - 1.
     * 
     * @param limit
     *            the maximum limit (exclusive) of the resulting int
     * @return uniformly distributed pseudorandom int
     */
    public int randomToLimit(int limit) {
        return random.nextInt(limit);
    }

    /**
     * Returns a random boolean value.
     * 
     * @return true, if random is
     */
    public boolean randomIs() {
        return random.nextBoolean();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ascape.model.space.Space#getContext()
     */
    public SpaceContext getContext() {
        return context;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ascape.model.space.Space#setContext(org.ascape.model.space.SpaceContext)
     */
    public void setContext(SpaceContext context) {
        this.context = context;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ascape.util.RandomFunctions#getRandom()
     */
    public Random getRandom() {
        return random;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ascape.util.RandomFunctions#setRandom(java.util.Random)
     */
    public void setRandom(Random random) {
        this.random = random;
    }

    /**
     * Return the geometry of this context.
     * 
     * @return the geometry
     */
    public Geometry getGeometry() {
        return geometry;
    }

    /**
     * Sets the geometry or basic structure of this context. This method should
     * never be called directly, but is assigned by an implementing class or
     * used with a factory method in Geometry.
     * 
     * @param geometry
     *            the structure of this context
     */
    protected void setGeometry(Geometry geometry) {
        this.geometry = geometry;
        // setInitialized(false);
    }

    /**
     * Is the geometry periodic or aperiodic? (Do edges wrap to opposite side or
     * not?).
     * 
     * @return true, if is periodic
     */
    public boolean isPeriodic() {
        return geometry.isPeriodic();
    }

    /**
     * Sets the geometry to periodic or aperiodic.
     * 
     * @param periodic
     *            true if periodic (wraps around at each edge), false is
     *            aperiodic
     */
    public void setPeriodic(boolean periodic) {
        geometry.setPeriodic(periodic);
    }

    /**
     * Overides the clone method to do a deep clone of member state so that such
     * state will not be shared between scapes.
     * 
     * @return the object
     */
    public Object clone() {
        try {
            CollectionSpace clone = (CollectionSpace) super.clone();
            clone.collection = new ArrayList();
            clone.extent = new Coordinate1DDiscrete(0);
            if (geometry != null) {
                clone.geometry = (Geometry) geometry.clone();
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException();
        }
    }

    /**
     * Creates a new array of ints for use as indexes for an ordered iteration.
     * Initially sequential.
     * 
     * @param length
     *            the length
     * @return the int[]
     */
    public static int[] createOrder(int length) {
        int[] order = new int[length];
        // Set initial order (sequential)
        for (int i = 0; i < order.length; i++) {
            order[i] = i;
        }
        return order;
    }

    /**
     * Randomizes order of the supplied int.
     * 
     * @param order
     *            the order
     * @param random
     *            the random
     * @return the int[]
     */
    public static int[] randomizeOrder(int[] order, Random random) {
        // Random Shuffle
        // See Knuth Volume 2, 3.4.2, Algorithm P
        // Count with i from highest index down to one greater than lowest
        // index..
        for (int j = order.length - 1; j > 0; j--) {
            int k = (random.nextInt() & Integer.MAX_VALUE) % (j + 1);
            // No reason to swap if index is the same
            if (k != j) {
                int swap = order[j];
                order[j] = order[k];
                order[k] = swap;
            }
        }
        return order;
    }

    public List toList() {
        return new ArrayList(this);
    }
}
