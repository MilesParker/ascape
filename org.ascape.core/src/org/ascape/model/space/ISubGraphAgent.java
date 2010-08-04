package org.ascape.model.space;


public interface ISubGraphAgent {

    public abstract void setCoordinate(Coordinate coordinate);

    public abstract CoordinateGraph getCoordinateGraph();

    public abstract void setCoordinateGraph(CoordinateGraph coordinateGraph);

}