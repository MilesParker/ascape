/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */
package org.ascape.model.space;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.ascape.util.Conditional;


/**
 * User: milesparker Date: Aug 11, 2005 Time: 4:10:17 PM To change this template
 * use File | Settings | File Templates.
 */
public class BFSWithinIterator implements Iterator {

    /**
     * The origin.
     */
    Location origin;
    
    /**
     * The condition.
     */
    Conditional condition;
    
    /**
     * The include self.
     */
    boolean includeSelf;
    
    /**
     * The distance.
     */
    int distance;
    
    /**
     * The visited.
     */
    Collection visited;
    
    /**
     * The current search.
     */
    private Collection currentSearch;
    
    /**
     * The next search.
     */
    Collection nextSearch;
    
    /**
     * The depth.
     */
    private int depth;
    
    /**
     * The next location.
     */
    Location nextLocation;
    
    /**
     * The search iterator.
     */
    private Iterator searchIterator;
    
    /**
     * The last depth.
     */
    private int lastDepth;
    
    /**
     * The space.
     */
    private Discrete space;

    /**
     * Instantiates a new BFS within iterator.
     * 
     * @param space
     *            the space
     * @param origin
     *            the origin
     * @param condition
     *            the condition
     * @param includeSelf
     *            the include self
     * @param distance
     *            the distance
     */
    public BFSWithinIterator(Discrete space, Location origin, Conditional condition, boolean includeSelf, double distance) {
        this.space = space;
        this.origin = origin;
        this.condition = condition;
        this.includeSelf = includeSelf;
        this.distance = (int) distance;
        visited = new HashSet();
        depth = 0;
        lastDepth = 0;
        initialize();
//        loadNext();
    }

    /**
     * Initialize.
     */
    protected void initialize() {
        currentSearch = new HashSet();
        nextSearch = new HashSet();
        if (includeSelf) {
            List start = new ArrayList();
            start.add(origin);
            visited.add(origin);
            searchIterator = start.iterator();
        } else {
            depth++;
            visited.add(origin);
            searchIterator = ((Node) origin).findNeighbors().iterator();
        }
    }

    /**
     * Load next.
     */
    public void loadNext() {
        nextLocation = null;
        while ((nextLocation == null) && searchIterator.hasNext() && (depth <= distance)) {
            Node candidate = (Node) searchIterator.next();
            visit(candidate);
            //todo special case condition and includeself for better performance
            if ((condition == null) || condition.meetsCondition(candidate)) {
                lastDepth = depth;
                nextLocation = candidate;
            }
            if (!searchIterator.hasNext()) {
                depth++;
                searchIterator = nextDepth();
            }
        }
    }

    /**
     * Visit.
     * 
     * @param candidate
     *            the candidate
     */
    public void visit(Node candidate) {
        nextSearch.addAll(candidate.findNeighbors());
    }

    /**
     * Next depth.
     * 
     * @return the iterator
     */
    public Iterator nextDepth() {
        nextSearch.removeAll(visited);
        visited.addAll(nextSearch);
        currentSearch.clear();
        currentSearch.addAll(nextSearch);
        nextSearch.clear();
        return currentSearch.iterator();
    }

    /**
     * The loaded next.
     */
    private boolean loadedNext;

    /* (non-Javadoc)
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext() {
        if (!loadedNext) {
            loadNext();
            loadedNext = true;
        }
        return nextLocation != null;
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#next()
     */
    public Object next() {
        if (!loadedNext) {
            loadNext();
        }
        loadedNext = false;
        Object result = nextLocation;
        return result;
    }

    /**
     * Gets the space.
     * 
     * @return the space
     */
    public Discrete getSpace() {
        return space;
    }

    /**
     * Gets the depth.
     * 
     * @return the depth
     */
    public int getDepth() {
        return lastDepth;
    }

    /**
     * Gets the internal depth.
     * 
     * @return the internal depth
     */
    protected int getInternalDepth() {
        return depth;
    }

    /**
     * Sets the depth.
     * 
     * @param depth
     *            the new depth
     */
    protected void setDepth(int depth) {
        this.depth = depth;
    }

    /**
     * Sets the last depth.
     * 
     * @param lastDepth
     *            the new last depth
     */
    protected void setLastDepth(int lastDepth) {
        this.lastDepth = lastDepth;
    }

    /**
     * Gets the origin.
     * 
     * @return the origin
     */
    public Location getOrigin() {
        return origin;
    }

    /**
     * Gets the visited.
     * 
     * @return the visited
     */
    protected Collection getVisited() {
        return visited;
    }

    /**
     * Gets the search iterator.
     * 
     * @return the search iterator
     */
    public Iterator getSearchIterator() {
        return searchIterator;
    }

    /**
     * Sets the search iterator.
     * 
     * @param searchIterator
     *            the new search iterator
     */
    public void setSearchIterator(Iterator searchIterator) {
        this.searchIterator = searchIterator;
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#remove()
     */
    public void remove() {
        throw new UnsupportedOperationException("Can't remove from within currentSearchIterator.");
    }
}
