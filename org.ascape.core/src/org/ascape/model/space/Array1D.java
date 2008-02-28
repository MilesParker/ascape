/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.space;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.ascape.util.RandomIterator;
import org.ascape.util.ResetableIterator;



/**
 * A one-dimensional, fixed-size, collection of agents providing services
 * described for space.
 * 
 * @author Miles Parker
 * @version 3.0
 * @history 3.0 9/10/02 significant refactoring of space model hierarchy
 * @history 1.9.3 3-4/2001 Changed to subclass Scape1DBase, Many QA fixes and
 *          functional improvements, changed getCells ordering
 * @history 1.9.2 2/26/01 added test code, fixed periodicity, distance and
 *          arraycopy usage issues in getCellsNear
 * @history 1.5 12/99 added support for iterations, and so was able to move
 *          executeMembers functionality to ScapeDiscrete
 * @history 1.2.5 10/6/99 changed space constructors to include name and not
 *          include geometry where appropriate
 * @history 1.0.1 ~12/##/98 no longer base class of ScapeArray2D
 * @history 1.0.2 renamed from ScapeArray for clarity
 * @since 1.0
 */
public class Array1D extends ListBase {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * The cells.
     */
    private Node[] cells = new Node[10];

    /**
     * The order.
     */
    private int[] order;

    /**
     * Constructs a one-dimensional immutable array.
     */
    public Array1D() {
        super();
        setGeometry(new Geometry(1, true));
    }

    /**
     * Constructs a one-dimensional immutable array.
     * 
     * @param extent
     *            a coordinate describing the size of this space
     */
    public Array1D(CoordinateDiscrete extent) {
        this();
        setExtent(extent);
    }

    /**
     * Sets the geometry of this space. Must of course be a one-dimensional
     * geometry.
     * 
     * @param geometry
     *            the basic geometry of this space
     */
    public void setGeometry(Geometry geometry) {
        super.setGeometry(geometry);
        if (geometry.getDimensionCount() != 1) {
            throw new RuntimeException("Tried to assign an inappropriate geometry.");
        }
    }

    /**
     * Sets the size of the array.
     * 
     * @param size
     *            the size of this space
     */
    public void setExtent(int size) {
        this.setExtent(new Coordinate1DDiscrete(size));
    }

    /**
     * Contructs the basic structure. Instantiates the cells, but does not
     * populate them. Geometry and extent should be set before calling this
     * method.
     */
    public void construct() {
        cells = new Node[((Coordinate1DDiscrete) extent).getValueAtDimension(1)];
        order = createOrder(cells.length);
    }

    /**
     * Populates the space with clones of the prototype agent. Prototype agent
     * should be set before calling this method. (By default, the prototpye
     * agent is a Node.)
     */
    public void populate() {
        for (int i = 0; i < cells.length; i++) {
            cells[i] = (Node) getContext().getPrototype().clone();
            cells[i].setCoordinate(new Coordinate1DDiscrete(i));
        }
        //todo, ensure that unsupportedoperation exceptions are properly thrown
        collection = Arrays.asList(cells);
    }

    /**
     * Initializes the space, ensuring that the ordering used for random draws
     * starts consistently.
     */
    public void initialize() {
        super.initialize();
        //We need to be certain that a new ordering is created at the start of any new run
        order = createOrder(cells.length);
    }

    /**
     * Randomizes the lookup used to determine calling order for random order
     * execution of rules.
     */
    public void randomizeCallingOrder() {
        order = randomizeOrder(order, getRandom());
    }

    /**
     * The Class Array1DIterator.
     */
    private class Array1DIterator implements ResetableIterator {

        /**
         * The i.
         */
        protected int i = 0;

        /* (non-Javadoc)
         * @see org.ascape.util.ResetableIterator#first()
         */
        public void first() {
            i = 0;
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#hasNext()
         */
        public boolean hasNext() {
            return i < cells.length;
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#next()
         */
        public Object next() {
            return cells[i++];
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#remove()
         */
        public void remove() {
            throw new UnsupportedOperationException("Can't remove an object from a immutable space.");
        }
    }

    /**
     * The Class Array1DSubIterator.
     */
    private class Array1DSubIterator extends Array1DIterator {

        /**
         * The start.
         */
        int start;
        
        /**
         * The limit.
         */
        int limit;

        /**
         * Instantiates a new array1 D sub iterator.
         * 
         * @param start
         *            the start
         * @param limit
         *            the limit
         */
        public Array1DSubIterator(int start, int limit) {
            this.start = start;
            this.limit = limit;
            first();
        }

        /* (non-Javadoc)
         * @see org.ascape.model.space.Array1D.Array1DIterator#first()
         */
        public void first() {
            i = start;
        }

        /* (non-Javadoc)
         * @see org.ascape.model.space.Array1D.Array1DIterator#hasNext()
         */
        public boolean hasNext() {
            return i < limit;
        }
    }

    /**
     * The Class Array1DRandomIterator.
     */
    private class Array1DRandomIterator extends Array1DIterator implements RandomIterator {

        /**
         * The iter order.
         */
        private int[] iterOrder;

        /**
         * Instantiates a new array1 D random iterator.
         */
        public Array1DRandomIterator() {
            iterOrder = createOrder(getSize());
            randomize();
        }

        /* (non-Javadoc)
         * @see org.ascape.model.space.Array1D.Array1DIterator#next()
         */
        public Object next() {
            return cells[iterOrder[i++]];
        }

        /* (non-Javadoc)
         * @see org.ascape.util.Randomizable#randomize()
         */
        public void randomize() {
            first();
            iterOrder = randomizeOrder(iterOrder, getRandom());
        }
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.CollectionSpace#iterator()
     */
    public Iterator iterator() {
        return new Array1DIterator();
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.CollectionSpace#safeIterator()
     */
    public ResetableIterator safeIterator() {
        return new Array1DIterator();
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.CollectionSpace#safeIterator(int, int)
     */
    public ResetableIterator safeIterator(int start, int limit) {
        return new Array1DSubIterator(start, limit);
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.CollectionSpace#safeRandomIterator()
     */
    public RandomIterator safeRandomIterator() {
        return new Array1DRandomIterator();
    }

    /**
     * Returns the size, or number of cells, (the product of all extents) of
     * this FixedList.
     * 
     * @return the size
     */
    public int getSize() {
        return ((Coordinate1DDiscrete) extent).getValueAtDimension(1);
        /*if (cells != null) {
            return cells.length;
        }
        else {
            return 0;
        }*/
    }

    /**
     * Returns the cell existing at the specified coordinate position.
     * 
     * @param xPosition
     *            the x position
     * @return the object
     */
    public Object get(int xPosition) {
        return cells[xPosition];
    }

    /**
     * Returns the object (agent) existing at the specified coordinate.
     * 
     * @param coordinate
     *            the coordinate
     * @return the location
     */
    public Location get(Coordinate coordinate) {
        return cells[((Coordinate1DDiscrete) coordinate).getValue()];
    }

    /**
     * Returns a cell randomly selected from the lattice.
     * 
     * @return the location
     */
    public Location findRandom() {
        return cells[randomToLimit(cells.length)];
    }

    /**
     * Returns a coordinate randomly selected from the lattice's space.
     * 
     * @return the coordinate
     */
    public Coordinate findRandomCoordinate() {
        return new Coordinate1DDiscrete(findRandomIndex());
    }

    /**
     * Returns a coordinate randomly selected from the lattice's space.
     * 
     * @return the int
     */
    public int findRandomIndex() {
        return randomToLimit(cells.length);
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.ListBase#findWithinImpl(org.ascape.model.space.Coordinate, boolean, double)
     */
    public List findWithinImpl(Coordinate origin, boolean includeSelf, double dist) {
        int distance = (int) dist;
        int xMid = ((Coordinate1DDiscrete) origin).getXValue();
        if (getGeometry().isPeriodic()) {
            int xMin = xMid - distance;
            int xMax = xMid + distance;
            if (distance * 2 + 1 > getSize()) {
                //Special case where distance is larger than actual lattice size..
                //Rounding down so that odd sizes get 1 less radius
                xMin = xMid - (getSize() / 2);
                if (xMin < 1) {
                    xMax = xMin + getSize() - 1;
                } else {
                    xMax = xMin - 1;
                }
            }
            Node[] cellsNear = new Node[(xMax - xMin) + (includeSelf ? 1 : 0)];
            if (includeSelf) {
                if (xMin < 0) {
                    System.arraycopy(cells, getSize() + xMin, cellsNear, 0, -xMin);
                    System.arraycopy(cells, 0, cellsNear, -xMin, xMax + 1);
                } else if (xMax >= getSize()) { //both (xMin < 0) && (xMax >= getSize()) can't be true
                    System.arraycopy(cells, xMin, cellsNear, 0, getSize() - xMin);
                    System.arraycopy(cells, 0, cellsNear, getSize() - xMin, xMax - getSize() + 1);
                } else { //No boundary overlap
                    System.arraycopy(cells, xMin, cellsNear, 0, cellsNear.length);
                }
            } else {  //!include self
                if (xMin < 0) {
                    System.arraycopy(cells, getSize() + xMin, cellsNear, 0, -xMin);
                    System.arraycopy(cells, 0, cellsNear, -xMin, xMid);
                    System.arraycopy(cells, xMid + 1, cellsNear, xMid - xMin, xMax - xMid);
                } else if (xMax >= getSize()) { //both (xMin < 0) && (xMax >= getSize()) can't be true
                    System.arraycopy(cells, xMin, cellsNear, 0, xMid - xMin);
                    System.arraycopy(cells, xMid + 1, cellsNear, xMid - xMin, getSize() - xMid - 1);
                    System.arraycopy(cells, 0, cellsNear, cellsNear.length - (xMax - getSize() + 1), xMax - getSize() + 1);
                } else { //No boundary overlap
                    if (xMin != xMax) {
                        System.arraycopy(cells, xMin, cellsNear, 0, xMid - xMin);
                        System.arraycopy(cells, xMid + 1, cellsNear, xMid - xMin, xMax - xMid);
                    }
                }
            }
            ArrayList found = new ArrayList();
            for (int i = 0; i < cellsNear.length; i++) {
                found.add(cellsNear[i]);
            }
            return found;

        } else {
            int xMin = Math.max(0, (xMid - distance));
            int xMax = Math.min((xMid + distance), this.getSize() - 1);
            Node[] cellsNear = new Node[xMax - xMin + (includeSelf ? 1 : 0)];
            if (includeSelf) {
                System.arraycopy(cells, xMin, cellsNear, 0, cellsNear.length);
            } else {
                System.arraycopy(cells, xMin, cellsNear, 0, xMid - xMin);
                System.arraycopy(cells, xMid + 1, cellsNear, xMid - xMin, xMax - xMid);
            }
            ArrayList found = new ArrayList();
            for (int i = 0; i < cellsNear.length; i++) {
                found.add(cellsNear[i]);
            }
            return found;
        }

    }

    /**
     * Is the space mutable, that is, can it change its structure at runtime?
     * Returns false for array 1D.
     * 
     * @return true, if is mutable
     */
    public boolean isMutable() {
        return false;
    }

    /**
     * Is a delete sweep needed for this space? Intended for internal purposes.
     * 
     * @return true, if is delete sweep needed
     */
    public boolean isDeleteSweepNeeded() {
        return false;
    }

    /**
     * Is a coordinate location sweep needed for this space? Intended for
     * internal purposes.
     * 
     * @return true, if is coordinate sweep needed
     */
    public boolean isCoordinateSweepNeeded() {
        return false;
    }

    /**
     * Returns all agents in the space as an array of cells (use this method to
     * avoid coercion of memebers to Node.)
     * 
     * @return the cells
     */
    public Node[] getCells() {
        return cells;
    }

    /**
     * Returns all agents in the space as an array.
     * 
     * @return the locations
     */
    public Location[] getLocations() {
        return cells;
    }
}
