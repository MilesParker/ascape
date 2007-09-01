/*
 * This software and documentation is subject to, and may be only used in accordance with, the terms and conditions of
 * the license agreement described or set forth in the file titled License.txt.  This software and documentation
 * contains information that is confidential and proprietary to NuTech Solutions, Inc.
 *
 *             Copyright (c) 2000-2004
 *             NuTech Solutions, Inc.
 *
 * Portions of the software are licensed from The Brookings Institution
 *             Copyright (c) 1998-2000
 *            The Brookings Institution
 *          All rights reserved worldwide.
 *
 * U.S. GOVERNMENT END USERS. The software is a "commercial item," as that term is defined in 48 C.F.R. 2.101,
 * consisting of "commercial computer software" and "commercial computer software documentation," as such terms are
 * used in 48 C.F.R. 12.212. Consistent with 48 C.F.R. 12.212 and 48 C.F.R. 227.7202-1 through 227.7202-4, and other
 * relevant sections of the Code of Federal Regulations, as applicable all U.S. Government End Users acquire the
 * software with only those rights set forth in the license agreement. This software includes trade secrets that
 * are exempt from disclosure under the Freedom of Information Act.
 *
 */



package org.ascape.examples.traffic;

import java.awt.Color;
import java.awt.Graphics;

import org.ascape.model.HostCell;
import org.ascape.model.space.Array2D;
import org.ascape.model.space.Coordinate2DDiscrete;
import org.ascape.util.vis.DrawFeature;


public class RoadTile extends HostCell {

    public final static DrawFeature DRAW_LANE_MARKINGS = new DrawFeature("Draw Lane Markings") {
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
