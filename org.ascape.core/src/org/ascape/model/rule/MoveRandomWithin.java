/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.rule;

import org.ascape.model.Agent;
import org.ascape.model.CellOccupant;
import org.ascape.model.HostCell;
import org.ascape.model.space.Array2D;

/**
 * A rule causing the taget agent to move to a random location within some
 * bounded area.
 * 
 * @author Miles Parker
 * @version 1.0
 * @since 1.0
 */
public class MoveRandomWithin extends Rule {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * The x.
     */
    private int x;

    /**
     * The y.
     */
    private int y;

    /**
     * The width.
     */
    private int width;

    /**
     * The height.
     */
    private int height;

    /**
     * Constructs a new move random within rule, with no area defined.
     */
    public MoveRandomWithin() {
        super("Move Random Within");
    }

    /**
     * Constructs a new move random within rule, causing the agenst to move to a
     * random location within the area defined.
     * 
     * @param x
     *            top boundary of the area
     * @param y
     *            left boundary of the area
     * @param width
     *            horizontal extent of area
     * @param height
     *            vertical extent of area
     */
    public MoveRandomWithin(int x, int y, int width, int height) {
        //Name will really be set in setArea.
        super("Temp");
        setArea(x, y, width, height);
    }

    /**
     * The agent must select its new location from within this area.
     * 
     * @param x
     *            top boundary of the area
     * @param y
     *            left boundary of the area
     * @param width
     *            horizontal extent of area
     * @param height
     *            vertical extent of area
     */
    public void setArea(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        name = "Move Random Within " + x + ", " + y + " -> " + width + ", " + height;
    }

    /**
     * Move to a random location in the lattice.
     * 
     * @param agent
     *            the playing agent
     * @see CellOccupant#moveTo
     */
    public void execute(Agent agent) {
        ((CellOccupant) agent).moveTo((HostCell) ((Array2D) ((CellOccupant) agent).getHostScape().getSpace()).findRandomUnoccupied(x, y, width, height));
    }

    /**
     * Returns false. Movement should not usually cause agent removal.
     * 
     * @return true, if is cause removal
     */
    public boolean isCauseRemoval() {
        return false;
    }
}
