/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.aa;

import java.awt.Color;
import java.awt.Graphics;
import java.io.DataInputStream;
import java.io.IOException;

import org.ascape.model.Cell;
import org.ascape.util.vis.DrawSymbol;
import org.ascape.util.vis.Drawable;



public class WaterSource extends Cell implements Drawable {

    /**
     * 
     */
    private static final long serialVersionUID = -847405276616547520L;

    public final static int PERMANENT = 2;

    public final static int RESERVOIR = 3;

    @SuppressWarnings("unused")
    private int number;

    private int metersNorth;

    private int metersEast;

    private int type;

    private int startDate;

    private int endDate;

    public void streamToState(DataInputStream s) throws IOException {
        number = s.readShort();
        metersNorth = s.readShort();
        metersEast = s.readShort();
        type = s.readShort();
        startDate = s.readShort();
        endDate = s.readShort();
    }

    public boolean isExtant() {
        return ((type == PERMANENT) || ((type == RESERVOIR) && (startDate <= getScape().getPeriod()) && (endDate >= getScape().getPeriod())));
    }

    public int getType() {
        return type;
    }

    public int getMetersNorth() {
        return metersNorth;
    }

    public int getMetersEast() {
        return metersEast;
    }

    public int getStartDate() {
        return startDate;
    }

    public int getEndDate() {
        return endDate;
    }

    public void draw(Graphics g, int width, int height) {
        g.setColor(Color.red);
        DrawSymbol.DRAW_X.draw(g, width, height);
    }

    public String toString() {
        return "Water Source " + coordinate;
    }
}
