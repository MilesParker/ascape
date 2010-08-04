package org.ascape.model.space;

public class SubGraphAgent implements ISubGraphAgent {
    CoordinateGraph coordinateGraph;

    /*
     * (non-Javadoc)
     * 
     * @see org.ascape.gis.model.ISubgraphAgent#setCoordinate(org.ascape.model.space.Coordinate)
     */
    public void setCoordinate(Coordinate coordinate) {
        this.coordinateGraph = (CoordinateGraph) coordinate;

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
