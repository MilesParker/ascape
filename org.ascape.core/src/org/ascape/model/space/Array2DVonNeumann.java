/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.space;

import java.util.List;


/**
 * A two-dimensional space providing effecient implementations for von Neumann
 * neighbors. In a von Neumann neighborhood, cells are considered neighbors if
 * they share a side with the target:
 * 
 * <pre>
 * O
 * OXO
 * O
 * </pre>
 * 
 * @author Miles Parker
 * @version 2.0
 * @history 2.0 10/29/01 changes to support new continuous space functionality,
 *          (int) getDistance is now (double) calcualteDistance
 * @history 1.9.3 3-4/2001 Many QA fixes and functional improvements
 * @history 1.9 9/20/2000 distamce methods
 * @history 1.2.5 10/6/99 changed space constructors to include name and not
 *          include geometry where appropriate
 * @since 1.0
 */
public class Array2DVonNeumann extends Array2D {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a two-dimensional von Neumann array.
     */
    public Array2DVonNeumann() {
        setGeometry(new Geometry(2, true, Geometry.VON_NEUMANN));
    }

    /**
     * Constructs a two-dimensional von Neumann array.
     * 
     * @param extent
     *            a coordinate describing the size of this space
     */
    public Array2DVonNeumann(CoordinateDiscrete extent) {
        this();
        setExtent(extent);
    }

    /**
     * Returns the next cell within immediate neighborhood toward the requested
     * cell.
     * 
     * @param originCell
     *            the current cell
     * @param targetCell
     *            the cell that we are moving toward
     * @return the node
     */
    public Node findCellToward(Node originCell, Node targetCell) {
        if (originCell == targetCell) {
            return originCell;
        }
        int ox = ((Coordinate2DDiscrete) originCell.getCoordinate()).getXValue();
        int oy = ((Coordinate2DDiscrete) originCell.getCoordinate()).getYValue();
        int tx = ((Coordinate2DDiscrete) targetCell.getCoordinate()).getXValue();
        int ty = ((Coordinate2DDiscrete) targetCell.getCoordinate()).getYValue();
        if (!getGeometry().isPeriodic()) {
            if (Math.abs(ox - tx) == Math.abs(tx - ox)) {
                //Both same distance (but known to be greater than 0), so force one to be greater
                if (randomIs()) {
                    tx = ox;
                } else {
                    ty = oy;
                }
            }
            if (Math.abs(ox - tx) > Math.abs(tx - ox)) {
                if (ox > tx) {
                    ox--;
                } else if (ox < tx) {
                    ox++;
                }
            } else {
                if (oy > ty) {
                    oy--;
                } else if (oy < ty) {
                    oy++;
                }
            }
        } else {
            int ex = getXSize();
            int ey = getYSize();
            if (ox > tx) {
                //is inside distance less than outside distance?
                if ((ox - tx) < (tx + (ex - ox))) {
                    ox--;
                } else {
                    ox++;
                }
            } else if (ox < tx) {
                if ((tx - ox) < (ox + (ex - tx))) {
                    ox++;
                } else {
                    ox--;
                }
            }
            if (oy > ty) {
                if ((oy - ty) < (ty + (ey - oy))) {
                    oy--;
                } else {
                    oy++;
                }
            } else if (oy < ty) {
                if ((ty - oy) < (oy + (ey - ty))) {
                    oy++;
                } else {
                    oy--;
                }
            }
            if (ox >= ex) {
                ox = 0;
            } else if (ox < 0) {
                ox = ex - 1;
            }
            if (oy >= ey) {
                oy = 0;
            } else if (oy < 0) {
                oy = ey - 1;
            }
        }
        return get(ox, oy);
    }

    /**
     * Returns the cell within immediate neighborhood furthest away from the
     * requestd cell.
     * 
     * @param originCell
     *            the current cell
     * @param targetCell
     *            the cell that we are moving toward
     * @return the node
     */
    public Node findCellAway(Node originCell, Node targetCell) {
        if (originCell == targetCell) {
            return originCell.findRandomNeighbor();
        }
        int ox = ((Coordinate2DDiscrete) originCell.getCoordinate()).getXValue();
        int oy = ((Coordinate2DDiscrete) originCell.getCoordinate()).getYValue();
        int tx = ((Coordinate2DDiscrete) targetCell.getCoordinate()).getXValue();
        int ty = ((Coordinate2DDiscrete) targetCell.getCoordinate()).getYValue();
        int ex = getXSize();
        int ey = getYSize();
        int rx = ex / 2;
        int ry = ey / 2;
        //Fix for periodic and von neumann
        int dx = tx - ox;
        if (dx > 0) {
            if (dx < rx - 1) {
                if (ox > 0) {
                    ox--;
                } else {
                    ox = ex - 1;
                }
            } else {
                if (ox < ex - 1) {
                    ox++;
                } else {
                    ox = 0;
                }
            }
        } else if (dx < 0) {
            if (dx > -rx - 1) {
                if (ox < ex - 1) {
                    ox++;
                } else {
                    ox = 0;
                }
            } else {
                if (ox > 0) {
                    ox--;
                } else {
                    ox = ex - 1;
                }
            }
        }
        int dy = ty - oy;
        if (dy > 0) {
            if (dy < ry - 1) {
                if (oy > 0) {
                    oy--;
                } else {
                    oy = ey - 1;
                }
            } else {
                if (oy < ey - 1) {
                    oy++;
                } else {
                    oy = 0;
                }
            }
        } else if (dy < 0) {
            if (dy > -ry - 1) {
                if (oy < ey - 1) {
                    oy++;
                } else {
                    oy = 0;
                }
            } else {
                if (oy > 0) {
                    oy--;
                } else {
                    oy = ey - 1;
                }
            }
        }
        Node dCell = get(ox, oy);
        if (dCell.isAvailable()) {
            return dCell;
        } else {
            if (randomIs()) {
                dCell = get(((Coordinate2DDiscrete) originCell.getCoordinate()).getXValue(), oy);
                if (dCell.isAvailable()) {
                    return dCell;
                } else {
                    dCell = get(ox, ((Coordinate2DDiscrete) originCell.getCoordinate()).getYValue());
                    if (dCell.isAvailable()) {
                        return dCell;
                    }
                }
            } else {
                dCell = get(ox, ((Coordinate2DDiscrete) originCell.getCoordinate()).getYValue());
                if (dCell.isAvailable()) {
                    return dCell;
                } else {
                    dCell = get(((Coordinate2DDiscrete) originCell.getCoordinate()).getXValue(), oy);
                    if (dCell.isAvailable()) {
                        return dCell;
                    }
                }
            }
        }
        return originCell;
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.Array#findWithinImpl(org.ascape.model.space.Coordinate, boolean, double)
     */
    public List findWithinImpl(Coordinate origin, boolean includeSelf, double distance) {
        return findWithinVonNeumann(origin, includeSelf, distance);
    }

    /**
     * Returns the shortest distance between one cell and the other. In a von
     * Neumann neighborhood, this distance is equal to the sum of each dimension
     * distance, unless nearnessIsLineOfSight is true, in which case, the
     * distance is equal to the distance along an axis if the origin and target
     * share an axis, and infinite (max value) otherwise. That is,
     * nearnessIsLineOfSight interpets distance as the 'perceived' distance,
     * while nearnessIsLineOfSight false interprest distance as the actual
     * walking distance between two points.
     * 
     * @param origin
     *            the origin
     * @param target
     *            the target
     * @return the double
     */
    public double calculateDistance(Coordinate origin, Coordinate target) {
        if (!nearnessLineOfSight) {
            return getXSpan(origin, target) + getYSpan(origin, target);
        } else {
            if (getXSpan(origin, target) == 0) {
                return getYSpan(origin, target);
            } else if (getYSpan(origin, target) == 0) {
                return getXSpan(origin, target);
            } else {
                return Integer.MAX_VALUE;
            }
        }
    }
}
