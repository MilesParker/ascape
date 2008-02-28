/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */



package org.ascape.examples.traffic;

import java.awt.Color;
import java.awt.Graphics;

import org.ascape.model.HostCell;
import org.ascape.model.space.Array2D;
import org.ascape.model.space.Coordinate2DDiscrete;
import org.ascape.util.vis.DrawFeature;


public class RoadTile extends HostCell {

    /**
     * 
     */
    private static final long serialVersionUID = 2547119597302364712L;

    public final static DrawFeature DRAW_LANE_MARKINGS = new DrawFeature("Draw Lane Markings") {
        /**
         * 
         */
        private static final long serialVersionUID = -425768204643441623L;

        public final void draw(Graphics g, Object object, int width, int height) {
            if (((Coordinate2DDiscrete) ((RoadTile) object).getCoordinate()).getYValue() != 0) {
                g.setColor(Color.white);
                g.drawLine(0, 0, width / 3, 0);
            } else {
                g.setColor(Color.yellow);
                g.drawLine(0, 0, width, 0);
            }
        }
    };

    public Color getColor() {
        return Color.gray;
    }

    public RoadTile cellLeft() {
        int y = ((Coordinate2DDiscrete) getCoordinate()).getYValue();
        y--;
        if (y >= 0) {
            RoadTile leftCell = (RoadTile) ((Array2D) getScape().getSpace()).get(((Coordinate2DDiscrete) getCoordinate()).getXValue(), y);
            return leftCell;
        }
        return null;
    }

    public RoadTile cellRight() {
        int y = ((Coordinate2DDiscrete) getCoordinate()).getYValue();
        y++;
        if (y < ((Coordinate2DDiscrete) getScape().getExtent()).getYValue()) {
            RoadTile rightCell = (RoadTile) ((Array2D) getScape().getSpace()).get(((Coordinate2DDiscrete) getCoordinate()).getXValue(), y);
            return rightCell;
        }
        return null;
    }

    public RoadTile cellAhead() {
        int x = ((Coordinate2DDiscrete) getCoordinate()).getXValue();
        x++;
        if (x >= ((Coordinate2DDiscrete) getScape().getExtent()).getXValue()) {
            x = 0;
        }
        return (RoadTile) ((Array2D) getScape().getSpace()).get(x, ((Coordinate2DDiscrete) getCoordinate()).getYValue());
    }

    public RoadTile cellAhead(int distance) {
        int x = ((Coordinate2DDiscrete) getCoordinate()).getXValue();
        x += distance;
        int xSize = ((Coordinate2DDiscrete) getScape().getExtent()).getXValue();
        if (x >= xSize) {
            x -= xSize;
        }
        return (RoadTile) ((Array2D) getScape().getSpace()).get(x, ((Coordinate2DDiscrete) getCoordinate()).getYValue());
    }

    public RoadTile cellBehind(int distance) {
        int x = ((Coordinate2DDiscrete) getCoordinate()).getXValue();
        x -= distance;
        if (x < 0) {
            int xSize = ((Coordinate2DDiscrete) getScape().getExtent()).getXValue();
            x += xSize;
        }
        return (RoadTile) ((Array2D) getScape().getSpace()).get(x, ((Coordinate2DDiscrete) getCoordinate()).getYValue());
    }
}
