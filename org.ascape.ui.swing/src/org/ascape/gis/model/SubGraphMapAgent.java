/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */
package org.ascape.gis.model;

import org.ascape.model.space.Coordinate;
import org.ascape.model.space.Coordinate2DContinuous;
import org.ascape.model.space.CoordinateGraph;
import org.ascape.model.space.ISubGraphAgent;

import com.bbn.openmap.omGraphics.OMRaster;

/**
 * 
 * User: jmiller Date: Nov 14, 2005 Time: 1:30:41 PM To change this template use Options | File Templates.
 */
public class SubGraphMapAgent extends MapAgent implements ISubGraphAgent {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    // org.ascapex.gis.model
    CoordinateGraph coordinateGraph;

    /*
     * (non-Javadoc)
     * 
     * @see org.ascape.gis.model.ISubgraphAgent#setCoordinate(org.ascape.model.space.Coordinate)
     */
    public void setCoordinate(Coordinate coordinate) {
        if (coordinate == null) {
            this.coordinate = coordinate;
        } else {
            double newX = ((Coordinate2DContinuous) coordinate).getXValue();
            double newY = ((Coordinate2DContinuous) coordinate).getYValue();
            graphic = new OMRaster((float) newX, (float) newY, getImage());
            location = new MapLocation((float) newX, (float) newY, "", graphic);
            this.coordinate = new MapCoordinate() {
                /**
                 * 
                 */
                private static final long serialVersionUID = 1L;

                public void setXValue(double x) {
                    super.setXValue(x);
                    location.setLocation((float) getYValue(), (float) getXValue());
                    graphic.setLat((float) getYValue());
                    graphic.setLon((float) getXValue());
                }

                public void setYValue(double y) {
                    super.setYValue(y);
                    location.setLocation((float) getYValue(), (float) getXValue());
                    graphic.setLat((float) getYValue());
                    graphic.setLon((float) getXValue());
                }
            };
            ((MapCoordinate) this.coordinate).setXValue(newX);
            ((MapCoordinate) this.coordinate).setYValue(newY);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ascape.gis.model.ISubgraphAgent#getCoordinateGraph()
     */
    public CoordinateGraph getCoordinateGraph() {
        return coordinateGraph;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ascape.gis.model.ISubgraphAgent#setCoordinateGraph(org.ascape.model.space.CoordinateGraph)
     */
    public void setCoordinateGraph(CoordinateGraph coordinateGraph) {
        this.coordinateGraph = coordinateGraph;
    }

}
