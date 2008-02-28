/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.space;

import java.io.Serializable;

/**
 * An encapsulation of all of the potential space definitions for any space of
 * agents, and a factory for creating realizations of these scapes. At the
 * moment, only one-dimensional and two-dimensional spaces are represented, but
 * this class should eventually suport n-dimensional spaces, non-discrete
 * spaces, and complex graphs. Users of this class are encouraged to use the
 * static definitions whenever possible.
 * 
 * @author Miles Parker
 * @version 2.9
 * @version 2.9 4/12/02 fixed issue with static Geometry
 * @since 1.0
 */
public class Geometry extends Object implements Cloneable, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * If appropriate. If the geometry is periodic, the edges of the space are
     * connected; for example, a 1D periodic lattice is a ring, and a periodic
     * 2D lattice is a torus, wheras a 1D aperiodic lattice is a line segment,
     * and an aperiodic 2D lattice is a plane.
     */
    private boolean periodic;

    /**
     * Is this geometry of fixed extent, as in an array or a closed space, or
     * can it shrink and grow like a vector or open space?.
     */
    private boolean fixedSize;

    /**
     * Does this geometry represent space as a set of discrete locations, or do
     * all objects within it have a some aproximate location within continous
     * space? For example, raster graphics would represent a discrete space,
     * while vector graphics would represent a non-discrete space.
     */
    private boolean discrete;

    /**
     * If appropriate. How many dimensions does this space have?
     */
    private int dimensionCount;

    /**
     * In a Moore neighborhood, cells are considered neighbors if they meet the
     * target at any point:
     * 
     * <pre>
     * OOO
     * OXO
     * OOO
     * </pre>.
     */
    public static final int MOORE = -1;

    /**
     * In a von Neumann neighborhood, cells are considered neighbors if they
     * share an edge with the target:
     * 
     * <pre>
     * O
     * OXO
     * O
     * </pre>.
     */
    public static final int VON_NEUMANN = -2;

    /**
     * In a Euclidian neighborhood, cells are considered neighbors if they meet
     * the target at any point:
     * 
     * <pre>
     * OOO
     * OXO
     * OOO
     * </pre>
     * 
     * This is just an Ascape convention, but you probably shouldn't be
     * concerned with neighbors in a Euclidian model anyway. Instead, you will
     * want to use distances, so that distance 1 = immeadiate vN distance, and >=
     * Sqrt(2) = immeadiate Moore distance.
     */
    public static final int EUCLIDIAN = -2;

    /**
     * Neighborhood not applicable.
     */
    public static final int NOT_APPLICABLE = -3;

    /**
     * The neighborhood (Moore or von Neumann) assumed for this lattice. This is
     * generally only an appropriate distinction in 2D space. (Check this!)
     */
    private int neighborhood = NOT_APPLICABLE;

    /**
     * Constructs a new geometry with the appropriate specifications.
     * 
     * @param dimensionCount
     *            the number of dimensions of the space
     * @param periodic
     *            if the space is connected, or if it has an edge
     * @param fixedSize
     *            if the space has a fixed extent
     * @param discrete
     *            if the geometry is made up of dicrete locations (nodes)
     * @param neighborhood
     *            whether the geometry' assumed neighborhood is Moore, von
     *            Neumann, or not applicable for this geometry
     */
    public Geometry(int dimensionCount, boolean periodic, boolean fixedSize, boolean discrete, int neighborhood) {
        this.dimensionCount = dimensionCount;
        this.periodic = periodic;
        this.fixedSize = fixedSize;
        this.discrete = discrete;
        this.neighborhood = neighborhood;
    }

    /**
     * Constructs a new fixed size, discrete geometry with the appropriate
     * specifications.
     * 
     * @param dimensionCount
     *            the number of dimensions of the space
     * @param periodic
     *            if the space is connected, or if it has an edge
     * @param neighborhood
     *            whether the geometry' assumed neighborhood is Moore, von
     *            Neumann, or not applicable for this geometry
     */
    public Geometry(int dimensionCount, boolean periodic, int neighborhood) {
        this(dimensionCount, periodic, true, true, neighborhood);
    }

    /**
     * Constructs a new fixed size, discrete geometry with the appropriate
     * specifications and no neighborhood definition.
     * 
     * @param dimensionCount
     *            the number of dimensions of the space
     * @param periodic
     *            if the space is connected, or if it has an edge
     */
    public Geometry(int dimensionCount, boolean periodic) {
        this(dimensionCount, periodic, true, true, NOT_APPLICABLE);
    }

    /**
     * Creates a non-periodic, discrete, geometry with no neighborhood
     * definition.
     * 
     * @param dimensionCount
     *            the dimension count
     */
    public Geometry(int dimensionCount) {
        this(dimensionCount, true, true, true, NOT_APPLICABLE);
    }

    /**
     * Creates an empty geometry. (No paramter constructor for Java Beans
     * usage.)
     */
    public Geometry() {
        this(0, true, true, true, NOT_APPLICABLE);
    }

    /**
     * Returns the number of dimensions for this lattice. Returns null if
     * structure is not dimensional.
     * 
     * @return the dimension count
     */
    public int getDimensionCount() {
        return dimensionCount;
    }

    /**
     * Sets the number of dimensions for this lattice. Set to null for
     * non-dimensional Geometry, otherwise, makes Geometry dimensional.
     * 
     * @param dimensionCount
     *            the number of dimensions
     */
    public void setDimensionCount(int dimensionCount) {
        this.dimensionCount = dimensionCount;
    }

    /**
     * Is the geometry periodic or aperiodic? (Do edges wrap to opposite side or
     * not?).
     * 
     * @return true, if is periodic
     */
    public boolean isPeriodic() {
        return periodic;
    }

    /**
     * Sets the geometry to periodic or aperiodic.
     * 
     * @param periodic
     *            true if periodic (wraps around at each edge), false is
     *            aperiodic
     */
    public void setPeriodic(boolean periodic) {
        this.periodic = periodic;
    }

    /**
     * Is the geometry dicrete or continous?.
     * 
     * @return true, if is discrete
     */
    public boolean isDiscrete() {
        return discrete;
    }

    /**
     * Sets the geometry to discrete or continous.
     * 
     * @param discrete
     *            true if discrete, false is continous
     */
    public void setDiscrete(boolean discrete) {
        this.discrete = discrete;
    }

    /**
     * Returns the geometry's presumed neighborhood.
     * 
     * @return the symbol MOORE, VON_NEUMANN, or NOT_APPLICABLE
     */
    public int getNeighborhood() {
        return this.neighborhood;
    }

    /**
     * Set the neighborhood to use for calculations within this geometry.
     * 
     * @param symbol
     *            the neighborhood, one of: MOORE, VON_NEUMANN, or
     *            NOT_APPLICABLE
     */
    public void setNeighborhood(int symbol) {
        this.neighborhood = symbol;
    }

    /**
     * Returns wether this geometry has a fixed size.
     * 
     * @return true if the space is always the same size, false otherwise
     */
    public boolean isFixedSize() {
        return fixedSize;
    }

    /**
     * Sets wether this geometry has a fixed size.
     * 
     * @param fixedSize
     *            should the space always have the same size?
     */
    public void setFixedSize(boolean fixedSize) {
        this.fixedSize = fixedSize;
    }

    /**
     * A string representation of this geometry.
     * 
     * @return the string
     */
    public String toString() {
        String desc = "";
        if (periodic) {
            desc = desc + "A periodic";
        } else {
            desc = desc + "An aperiodic";
        }
        if (discrete) {
            desc = desc + ", discrete";
        }
        if (fixedSize) {
            desc = desc + ", fixed-size";
        }
        desc = desc + Integer.toString(dimensionCount) + "-dimensional ";
        if (neighborhood == MOORE) {
            desc = desc + " structure using the Moore neighborhood.";
        } else if (neighborhood == VON_NEUMANN) {
            desc = desc + " structure using the von Neumnann neighborhood.";
        } else {
            desc = desc + " structure.";
        }
        return desc;
    }

    /**
     * Clones the geometry. Catches clone not supported, as all geometries
     * support cloning.
     * 
     * @return the object
     */
    public Object clone() {
        try {
            Geometry clone = (Geometry) super.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }
}
