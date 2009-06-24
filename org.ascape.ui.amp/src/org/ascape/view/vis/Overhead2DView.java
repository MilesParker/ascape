/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */

package org.ascape.view.vis;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.ascape.model.Agent;
import org.ascape.model.Cell;
import org.ascape.model.CellOccupant;
import org.ascape.model.LocatedAgent;
import org.ascape.model.space.Array2DBase;
import org.ascape.model.space.Coordinate2DDiscrete;
import org.ascape.util.vis.ColorFeature;
import org.ascape.util.vis.DrawFeature;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;



/**
 * A scape view that draws the classic bird's eye view of a 2-dimensional lattice. Draws each cell the cell feature
 * color, then draws an oval in each cell in the agent's feature color. Future versions will provide more drawing
 * options and support multiple agent populations within one lattice.
 * 
 * @author Miles Parker
 * @version 1.9
 * @history 1.9 5/1/00 added support for cell customizer
 * @history 1.1.2 5/17/99 added border support
 * @history 1.0 First in version 1.0
 * @since 1.0
 */
public class Overhead2DView extends HostedAgentView {

    private static final long serialVersionUID = 4333847136610603354L;

    /**
     * The draw selected neighbors.
     */
    protected boolean drawSelectedNeighbors = false;

    /**
     * The draw far neighbors.
     */
    private boolean drawFarNeighbors = false;

    /**
     * The draw network.
     */
    private boolean drawNetwork = false;

    /**
     * The draw by feature.
     */
    private boolean drawByFeature = false;

    /**
     * The draw features.
     */
    Object[] drawFeatures;

    /**
     * Constructs an overhead two-dimensional view.
     */
    public Overhead2DView() {
        this("Overhead 2D View");
    }

    /**
     * Constructs an overhead two-dimensional view.
     * 
     * @param name
     *        a user relevant name for this view
     */
    public Overhead2DView(String name) {
        super(name);
    }

    // @Override
    // protected void setInput(IEditorInput input) {
    // // TODO Auto-generated method stub
    // super.setInput(input);
    // if (!(getScape().getSpace() instanceof Array2D)) {
    // throw new RuntimeException("Tried to use an overhead 2D view with an incompatible space.");
    // }
    // }

    /**
     * Draw cell at if update.
     * 
     * @param x
     *        the x
     * @param y
     *        the y
     */
    protected void drawCellAtIfUpdate(Graphics graphics, int x, int y) {
        Cell cell = (Cell) ((Array2DBase) getScape().getSpace()).get(x, y);
        if (cell.isUpdateNeeded(getScape().getIterationsPerRedraw())) {
            for (Object drawFeature : drawFeatures) {
                ((DrawFeature) drawFeature).draw(graphics, cell, agentSize, agentSize);
            }
        }
    }

    /**
     * Draw cell at.
     * 
     * @param x
     *        the x
     * @param y
     *        the y
     */
    protected void drawCellAt(Graphics graphics, int x, int y) {
        for (Object drawFeature : drawFeatures) {
            ((DrawFeature) drawFeature).draw(graphics, ((Array2DBase) getScape().getSpace()).get(x, y), agentSize,
                                             agentSize);
        }
    }

    //
    // /**
    // * Draw cell at if update.
    // *
    // * @param x
    // * the x
    // * @param y
    // * the y
    // * @param feature
    // * the feature
    // */
    protected void drawCellAtIfUpdate(Graphics graphics, int x, int y, DrawFeature feature) {
        Cell cell = (Cell) ((Array2DBase) getScape().getSpace()).get(x, y);
        if (cell.isUpdateNeeded(getScape().getIterationsPerRedraw())) {
            feature.draw(graphics, cell, agentSize, agentSize);
        }
    }

    /**
     * Draw cell at.
     * 
     * @param x
     *        the x
     * @param y
     *        the y
     * @param feature
     *        the feature
     */
    protected void drawCellAt(Graphics graphics, int x, int y, DrawFeature feature) {
        feature.draw(graphics, ((Array2DBase) getScape().getSpace()).get(x, y), agentSize, agentSize);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ascape.view.vis.AgentView#getAgentAtPixel(int, int)
     */
    public Agent getAgentAtPixel(int x, int y) {
        int td = agentSize + borderSize;
        // handles case where mouse is outside lattice, but inside view
        if (x == getSize().width - 1) {
            getAgentAtPixel(--x, y);
        }
        if (y == getSize().width - 1) {
            getAgentAtPixel(x, --y);
        }
        return (Cell) ((Array2DBase) getScape().getSpace()).get(x / td, y / td);
    }

    /**
     * Draw neighbors for.
     * 
     * @param g
     *        the g
     * @param agent
     *        the agent
     */
    @SuppressWarnings("unchecked")
    protected void drawNeighborsFor(Graphics g, LocatedAgent agent) {
        // graphics.drawString(Integer.toString(((SugarAgent) occupant).getVision()), x * agentSize, y * agentSize);
        int td = agentSize + borderSize;
        g.setForegroundColor(ColorFeature.BLACK);
        List neighbors = new LinkedList(((Cell) agent).findNeighbors());
        neighbors.removeAll(agent.findWithin(1.0));
        for (int i = 0; i < neighbors.size(); i++) {
            if (((Cell) neighbors.get(i)).getCoordinate() != null) {
                int dx = ((Coordinate2DDiscrete) ((Cell) neighbors.get(i)).getCoordinate()).getXValue()
                - ((Coordinate2DDiscrete) agent.getCoordinate()).getXValue();
                int dy = ((Coordinate2DDiscrete) ((Cell) neighbors.get(i)).getCoordinate()).getYValue()
                - ((Coordinate2DDiscrete) agent.getCoordinate()).getYValue();
                // int x2 = ((Coordinate2DDiscrete) neighbors[i].getCoordinate()).getXValue();
                // int y2 = ((Coordinate2DDiscrete) neighbors[i].getCoordinate()).getYValue();
                g.drawLine(td / 2 - 1, td / 2 - 1, dx * td + td / 2 - 1, dy * td + td / 2 - 1);
            }
        }
    }

    /**
     * Draw selected agent at.
     * 
     * @param g
     *        the g
     * @param a
     *        the a
     */
    protected synchronized void drawSelectedAgentAt(Graphics g, LocatedAgent a) {
        // g.translate(-2, -2);
        // g.setForegroundColor(DEFAULT_AGENT_COLOR);
        // if (getAgentCustomizer().getFocus() == AgentCustomizer.FOCUS_PRIMARY) {
        // DrawSymbol.DRAW_RECT_2.draw(g, null, agentSize + 4, agentSize + 4);
        // } else {
        // DrawSymbol.DRAW_OVAL_2.draw(g, null, agentSize + 4, agentSize + 4);
        // }
        // graphics.translate(2, 2);
        // if (agentSize > 4) {
        // graphics.setForegroundColor(Color.yellow);
        // if (getAgentCustomizer().getFocus() == AgentCustomizer.FOCUS_PRIMARY) {
        // DrawSymbol.DRAW_RECT_2.draw(g, null, agentSize, agentSize);
        // } else {
        // DrawSymbol.DRAW_OVAL_2.draw(g, null, agentSize, agentSize);
        // }
        // } else if (agentSize == 4) {
        // graphics.setForegroundColor(Color.yellow);
        // if (getAgentCustomizer().getFocus() == AgentCustomizer.FOCUS_PRIMARY) {
        // DrawSymbol.DRAW_RECT.draw(g, null, agentSize, agentSize);
        // } else {
        // DrawSymbol.DRAW_OVAL.draw(g, null, agentSize, agentSize);
        // }
        // }
    }

    /*
     * (non-Javadoc)
     * 
     * @see orgraphics.ascape.view.vis.AgentView#drawSelectedAgent(java.awt.Graphics,
     * orgraphics.ascape.model.LocatedAgent)
     */
    public synchronized void drawSelectedAgent(Graphics graphics, LocatedAgent a) {
        Coordinate2DDiscrete coor = (Coordinate2DDiscrete) a.getCoordinate();
        int x = coor.getXValue();
        int y = coor.getYValue();
        int td = agentSize + borderSize;
        // Transform tr = createTR(graphics);
        graphics.translate(x * td, y * td);
        drawSelectedAgentAt(graphics, a);
        // if ((drawSelectedNeighbors) && ((getAgentCustomizer() != null) && ((Cell) getAgentCustomizer().getAgent() !=
        // null))) {
        // drawNeighborsFor(g, a);
        // }
        graphics.translate(-x * td, -y * td);
        if (getScape().isCellsRequestUpdates()) {
            // check x bounds;
            int xmin = x - 1 < 0 ? x = 0 : x - 1;
            int xmax = x + 1 < ((Array2DBase) getScape().getSpace()).getXSize() ? x + 1 : x;
            // check y bounds;
            int ymin = y - 1 < 0 ? y = 0 : y - 1;
            int ymax = y + 1 < ((Array2DBase) getScape().getSpace()).getXSize() ? y + 1 : y;
            for (int i = xmin; i <= xmax; i++) {
                for (int j = ymin; j <= ymax; j++) {
                    ((Cell) ((Array2DBase) getScape().getSpace()).get(i, j)).requestUpdateNext();
                }
            }
        }
        graphics.dispose();
    }

    /**
     * On notification of a scape update, draws the actual overhead view.
     */
    public void updateScapeGraphics(Graphics graphics) {
        super.updateScapeGraphics(graphics);
        Coordinate2DDiscrete extent = (Coordinate2DDiscrete) getScape().getExtent();
        drawFeatures = getDrawSelection().getSelection();
        int td = agentSize + borderSize;
        // Transform tr = createTR(graphics);
        if (!drawByFeature) {
            if (getScape().isCellsRequestUpdates() && !getScape().isUpdateNeeded(getScape().getIterationsPerRedraw())
                    && !drawNetwork && !drawFarNeighbors) {
                for (int x = 0; x < extent.getXValue(); x++) {
                    for (int y = 0; y < extent.getYValue(); y++) {
                        drawCellAtIfUpdate(graphics, x, y);
                        graphics.translate(0, td);
                        // graphics.setTransform(tr);
                    }
                    graphics.translate(td, -extent.getYValue() * td);
                    // graphics.setTransform(tr);
                }
            } else {
                for (int x = 0; x < extent.getXValue(); x++) {
                    for (int y = 0; y < extent.getYValue(); y++) {
                        drawCellAt(graphics, x, y);
                        graphics.translate(0, td);
                        // graphics.setTransform(tr);
                    }
                    graphics.translate(td, -extent.getYValue() * td);
                    // graphics.setTransform(tr);
                }
            }
            graphics.translate(-extent.getXValue() * td, 0);
        } else {
            for (Object drawFeature : drawFeatures) {
                if (getScape().isCellsRequestUpdates()
                        && !getScape().isUpdateNeeded(getScape().getIterationsPerRedraw()) && !drawNetwork) {
                    for (int x = 0; x < extent.getXValue(); x++) {
                        for (int y = 0; y < extent.getYValue(); y++) {
                            drawCellAtIfUpdate(graphics, x, y, (DrawFeature) drawFeature);
                            graphics.translate(0, td);
                            // graphics.setTransform(tr);
                        }
                        graphics.translate(td, -extent.getYValue() * td);
                        // graphics.setTransform(tr);
                    }
                } else {
                    for (int x = 0; x < extent.getXValue(); x++) {
                        for (int y = 0; y < extent.getYValue(); y++) {
                            drawCellAt(graphics, x, y, (DrawFeature) drawFeature);
                            graphics.translate(0, td);
                            // graphics.setTransform(tr);
                        }
                        graphics.translate(td, -extent.getYValue() * td);
                        // graphics.setTransform(tr);
                    }
                }
                graphics.translate(-extent.getXValue() * td, 0);
                // graphics.setTransform(tr);
            }
        }
        if (drawNetwork) {
            for (int x = 0; x < extent.getXValue(); x++) {
                for (int y = 0; y < extent.getYValue(); y++) {
                    Cell cell = (Cell) ((Array2DBase) getScape().getSpace()).get(x, y);
                    CellOccupant occupant = (CellOccupant) cell.getOccupant();
                    if (occupant != null) {
                        List network = occupant.getNetwork();
                        graphics.setForegroundColor(ColorFeature.DARK_GRAY);
                        for (Iterator iterator = network.iterator(); iterator.hasNext();) {
                            Cell target = (Cell) iterator.next();
                            int x2 = ((Coordinate2DDiscrete) target.getCoordinate()).getXValue();
                            int y2 = ((Coordinate2DDiscrete) target.getCoordinate()).getYValue();
                            graphics.drawLine(x * agentSize + agentSize / 2 - 1, y * agentSize + agentSize / 2 - 1, x2
                                              * agentSize + agentSize / 2 - 1, y2 * agentSize + agentSize / 2 - 1);
                        }
                    }
                }
            }
        }
        if (drawFarNeighbors) {
            graphics.setForegroundColor(ColorFeature.DARK_YELLOW);
            for (int x = 0; x < extent.getXValue(); x++) {
                for (int y = 0; y < extent.getYValue(); y++) {
                    drawNeighborsFor(graphics, (Cell) ((Array2DBase) getScape().getSpace()).get(x, y));
                    graphics.translate(0, td);
                }
                graphics.translate(td, -extent.getYValue() * td);
            }
            graphics.translate(-extent.getXValue() * td, 0);
        }
        drawSelectedAgents();
    }

    /**
     * Draw selected agents.
     */
    private void drawSelectedAgents() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see orgraphics.ascape.view.vis.AgentSizedView#calculateViewSizeForAgentSize(int)
     */
    @Override
    public Dimension calculateViewSizeForAgentSize(Dimension availableSize, int cellSize) {
        return new Dimension(((Coordinate2DDiscrete) getScape().getExtent()).getXValue() * (cellSize + borderSize)
                             + borderSize, ((Coordinate2DDiscrete) getScape().getExtent()).getYValue() * (cellSize + borderSize)
                             + borderSize);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ascape.view.vis.AgentSizedView#calculateAgentSizeForViewSize(java.awt.Dimension)
     */
    @Override
    public int calculateAgentSizeForViewSize(Dimension d) {
        int width = (d.width - borderSize) / ((Array2DBase) getScape().getSpace()).getXSize() - borderSize;
        int height = (d.height - borderSize) / ((Array2DBase) getScape().getSpace()).getYSize() - borderSize;
        return Math.max(1, Math.min(width, height));
    }

    /**
     * Method called once a model is deserialized.
     * 
     * @return true, if is draw by feature
     */
    /*
     * // MEI - I have been having trouble getting an Overhead2DView to respond to // mouse clicks once it has been
     * deserialized, so for now we will just leave // it hidden by not giving it a new ViewFrameBridge. We can put our
     * call // to make a new Overhead2DView in createGraphicsViews instead of createViews, // and that way a new
     * Overhead2DView will be made each time you deserialize. public void onDeserialized() {
     * onChangegetIterPerRedraw()(); new ViewFrameBridge(this); }
     */

    public boolean isDrawByFeature() {
        return drawByFeature;
    }

    /**
     * Sets the draw by feature.
     * 
     * @param drawByFeature
     *        the new draw by feature
     */
    public void setDrawByFeature(boolean drawByFeature) {
        this.drawByFeature = drawByFeature;
    }

    /**
     * Checks if is draw network.
     * 
     * @return true, if is draw network
     */
    public boolean isDrawNetwork() {
        return drawNetwork;
    }

    /**
     * Checks if is draw selected neighbors.
     * 
     * @return true, if is draw selected neighbors
     */
    public boolean isDrawSelectedNeighbors() {
        return drawSelectedNeighbors;
    }

    /**
     * Checks if is draw far neighbors.
     * 
     * @return true, if is draw far neighbors
     */
    public boolean isDrawFarNeighbors() {
        return drawFarNeighbors;
    }

    /**
     * Should this view draw network connections between agents?.
     * 
     * @param drawNetwork
     *        the draw network
     */
    public void setDrawNetwork(boolean drawNetwork) {
        this.drawNetwork = drawNetwork;
    }

    /**
     * Should this view draw neighbor connections between agents?.
     * 
     * @param drawSelectedNeighbors
     *        the draw selected neighbors
     */
    public void setDrawSelectedNeighbors(boolean drawSelectedNeighbors) {
        this.drawSelectedNeighbors = drawSelectedNeighbors;
    }

    /**
     * Sets the draw far neighbors.
     * 
     * @param drawFarNeighbors
     *        the new draw far neighbors
     */
    public void setDrawFarNeighbors(boolean drawFarNeighbors) {
        this.drawFarNeighbors = drawFarNeighbors;
    }
}
