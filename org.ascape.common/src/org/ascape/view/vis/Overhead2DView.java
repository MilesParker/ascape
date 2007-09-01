/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.view.vis;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TooManyListenersException;

import org.ascape.model.Agent;
import org.ascape.model.Cell;
import org.ascape.model.CellOccupant;
import org.ascape.model.LocatedAgent;
import org.ascape.model.Scape;
import org.ascape.model.event.ScapeEvent;
import org.ascape.model.space.Array2DBase;
import org.ascape.model.space.Array2DSmallWorld;
import org.ascape.model.space.Coordinate2DDiscrete;
import org.ascape.util.vis.DrawFeature;
import org.ascape.util.vis.DrawSymbol;
import org.ascape.view.custom.AgentCustomizer;

/**
 * A scape view that draws the classic bird's eye view of a 2-dimensional
 * lattice. Draws each cell the cell feature color, then draws an oval in each
 * cell in the agent's feature color. Future versions will provide more drawing
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
        this("Overhead Agent View");
    }

    /**
     * Constructs an overhead two-dimensional view.
     * 
     * @param name
     *            a user relevant name for this view
     */
    public Overhead2DView(String name) {
        super();
        this.name = name;
        setCellSize(10);
    }

    /**
     * Override addNotify to build buffer.
     */
    public void addNotify() {
        super.addNotify();
        while (bufferedGraphics == null) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
        }
        bufferedGraphics.translate(borderSize, borderSize);
    }

    /**
     * Draw cell at if update.
     * 
     * @param x
     *            the x
     * @param y
     *            the y
     */
    protected void drawCellAtIfUpdate(int x, int y) {
        Cell cell = (Cell) ((Array2DBase) getScape().getSpace()).get(x, y);
        if (cell.isUpdateNeeded(iterationsPerRedraw)) {
            for (int i = 0; i < drawFeatures.length; i++) {
                ((DrawFeature) drawFeatures[i]).draw(bufferedGraphics, cell, agentSize, agentSize);
            }
        }
    }

    /**
     * Draw cell at.
     * 
     * @param x
     *            the x
     * @param y
     *            the y
     */
    protected void drawCellAt(int x, int y) {
        for (int i = 0; i < drawFeatures.length; i++) {
            ((DrawFeature) drawFeatures[i]).draw(bufferedGraphics, ((Array2DBase) getScape().getSpace()).get(x, y), agentSize, agentSize);
        }
    }

    /**
     * Draw cell at if update.
     * 
     * @param x
     *            the x
     * @param y
     *            the y
     * @param feature
     *            the feature
     */
    protected void drawCellAtIfUpdate(int x, int y, DrawFeature feature) {
        Cell cell = (Cell) ((Array2DBase) getScape().getSpace()).get(x, y);
        if (cell.isUpdateNeeded(iterationsPerRedraw)) {
            feature.draw(bufferedGraphics, cell, agentSize, agentSize);
        }
    }

    /**
     * Draw cell at.
     * 
     * @param x
     *            the x
     * @param y
     *            the y
     * @param feature
     *            the feature
     */
    protected void drawCellAt(int x, int y, DrawFeature feature) {
        feature.draw(bufferedGraphics, ((Array2DBase) getScape().getSpace()).get(x, y), agentSize, agentSize);
    }

    /* (non-Javadoc)
     * @see org.ascape.view.vis.AgentView#getAgentAtPixel(int, int)
     */
    public Agent getAgentAtPixel(int x, int y) {
        int td = agentSize + borderSize;
        // handles case where mouse is outside lattice, but inside view
        if (x == (getPreferredSize().getHeight() - 1)) {
            getAgentAtPixel(--x, y);
        }
        if (y == (getPreferredSize().getWidth() - 1)) {
            getAgentAtPixel(x, --y);
        }
        return (Cell) ((Array2DBase) getScape().getSpace()).get(x / td, y / td);
    }

    /**
     * Draw neighbors for.
     * 
     * @param g
     *            the g
     * @param agent
     *            the agent
     */
    protected void drawNeighborsFor(Graphics g, LocatedAgent agent) {
        //bufferedGraphics.drawString(Integer.toString(((SugarAgent) occupant).getVision()), x * agentSize, y * agentSize);
        int td = agentSize + borderSize;
        g.setColor(Color.black);
        List neighbors = new LinkedList(((Cell) agent).findNeighbors());
        neighbors.removeAll(agent.findWithin(1.0));
        for (int i = 0; i < neighbors.size(); i++) {
            if (((Cell) neighbors.get(i)).getCoordinate() != null) {
                int dx = ((Coordinate2DDiscrete) ((Cell) neighbors.get(i)).getCoordinate()).getXValue() - ((Coordinate2DDiscrete) agent.getCoordinate()).getXValue();
                int dy = ((Coordinate2DDiscrete) ((Cell) neighbors.get(i)).getCoordinate()).getYValue() - ((Coordinate2DDiscrete) agent.getCoordinate()).getYValue();
                //int x2 = ((Coordinate2DDiscrete) neighbors[i].getCoordinate()).getXValue();
                //int y2 = ((Coordinate2DDiscrete) neighbors[i].getCoordinate()).getYValue();
                g.drawLine(td / 2 - 1, td / 2 - 1, dx * td + td / 2 - 1, dy * td + td / 2 - 1);
            }
        }
    }

    /**
     * Draw selected agent at.
     * 
     * @param g
     *            the g
     * @param a
     *            the a
     */
    protected synchronized void drawSelectedAgentAt(Graphics g, LocatedAgent a) {
        g.translate(-2, -2);
        g.setColor(Color.black);
        if (getAgentCustomizer().getFocus() == AgentCustomizer.FOCUS_PRIMARY) {
            DrawSymbol.DRAW_RECT_2.draw(g, null, agentSize + 4, agentSize + 4);
        } else {
            DrawSymbol.DRAW_OVAL_2.draw(g, null, agentSize + 4, agentSize + 4);
        }
        g.translate(2, 2);
        if (agentSize > 4) {
            g.setColor(Color.yellow);
            if (getAgentCustomizer().getFocus() == AgentCustomizer.FOCUS_PRIMARY) {
                DrawSymbol.DRAW_RECT_2.draw(g, null, agentSize, agentSize);
            } else {
                DrawSymbol.DRAW_OVAL_2.draw(g, null, agentSize, agentSize);
            }
        } else if (agentSize == 4) {
            g.setColor(Color.yellow);
            if (getAgentCustomizer().getFocus() == AgentCustomizer.FOCUS_PRIMARY) {
                DrawSymbol.DRAW_RECT.draw(g, null, agentSize, agentSize);
            } else {
                DrawSymbol.DRAW_OVAL.draw(g, null, agentSize, agentSize);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.ascape.view.vis.AgentView#drawSelectedAgent(java.awt.Graphics, org.ascape.model.LocatedAgent)
     */
    public synchronized void drawSelectedAgent(Graphics g, LocatedAgent a) {
        Coordinate2DDiscrete coor = (Coordinate2DDiscrete) a.getCoordinate();
        int x = coor.getXValue();
        int y = coor.getYValue();
        int td = agentSize + borderSize;
        g.translate(x * td, y * td);
        drawSelectedAgentAt(g, a);
        if ((drawSelectedNeighbors) && ((getAgentCustomizer() != null) && ((Cell) getAgentCustomizer().getAgent() != null))) {
            drawNeighborsFor(g, a);
        }
        g.translate(-x * td, -y * td);
        if (scape.isCellsRequestUpdates()) {
            //check x bounds;
            int xmin = (x - 1 < 0 ? x = 0 : x - 1);
            int xmax = (x + 1 < ((Array2DBase) getScape().getSpace()).getXSize() ? x + 1 : x);
            //check y bounds;
            int ymin = (y - 1 < 0 ? y = 0 : y - 1);
            int ymax = (y + 1 < ((Array2DBase) getScape().getSpace()).getXSize() ? y + 1 : y);
            for (int i = xmin; i <= xmax; i++) {
                for (int j = ymin; j <= ymax; j++) {
                    ((Cell) ((Array2DBase) getScape().getSpace()).get(i, j)).requestUpdateNext();
                }
            }
        }
    }

    /**
     * Notifies the listener that the scape has added it. Override to set draw
     * network on if the scape is a small world.
     * 
     * @param scapeEvent
     *            the scape added notification event
     * @throws TooManyListenersException
     *             the too many listeners exception
     * @exception java.util.TooManyListenersException
     *                on attempt to add this listener to another scape when one
     *                has already been assigned
     */
    public void scapeAdded(ScapeEvent scapeEvent) throws TooManyListenersException {
        super.scapeAdded(scapeEvent);
        if (((Scape) scapeEvent.getSource()).getSpace() instanceof Array2DSmallWorld) {
            setDrawSelectedNeighbors(true);
        }
    }

    /**
     * On notification of a scape update, draws the actual overhead view.
     */
    public synchronized void updateScapeGraphics() {
        super.updateScapeGraphics();
        Coordinate2DDiscrete extent = ((Coordinate2DDiscrete) scape.getExtent());
        drawFeatures = getDrawSelection().getSelection();
        int td = agentSize + borderSize;

        if (!drawByFeature) {
            if (scape.isCellsRequestUpdates() && !scape.isUpdateNeeded(iterationsPerRedraw) && !drawNetwork && !drawFarNeighbors) {
                for (int x = 0; x < extent.getXValue(); x++) {
                    for (int y = 0; y < extent.getYValue(); y++) {
                        drawCellAtIfUpdate(x, y);
                        bufferedGraphics.translate(0, td);
                    }
                    bufferedGraphics.translate(td, (-extent.getYValue()) * td);
                }
            } else {
                for (int x = 0; x < extent.getXValue(); x++) {
                    for (int y = 0; y < extent.getYValue(); y++) {
                        drawCellAt(x, y);
                        bufferedGraphics.translate(0, td);
                    }
                    bufferedGraphics.translate(td, (-extent.getYValue()) * td);
                }
                updateAllRequested = false;
            }
            bufferedGraphics.translate((-extent.getXValue()) * td, 0);
        } else {
            for (int i = 0; i < drawFeatures.length; i++) {
                if (scape.isCellsRequestUpdates() && !scape.isUpdateNeeded(iterationsPerRedraw) && !drawNetwork) {
                    for (int x = 0; x < extent.getXValue(); x++) {
                        for (int y = 0; y < extent.getYValue(); y++) {
                            drawCellAtIfUpdate(x, y, (DrawFeature) drawFeatures[i]);
                            bufferedGraphics.translate(0, td);
                        }
                        bufferedGraphics.translate(td, (-extent.getYValue()) * td);
                    }
                } else {
                    for (int x = 0; x < extent.getXValue(); x++) {
                        for (int y = 0; y < extent.getYValue(); y++) {
                            drawCellAt(x, y, (DrawFeature) drawFeatures[i]);
                            bufferedGraphics.translate(0, td);
                        }
                        bufferedGraphics.translate(td, (-extent.getYValue()) * td);
                    }
                    updateAllRequested = false;
                }
                bufferedGraphics.translate((-extent.getXValue()) * td, 0);
            }
        }
        if (drawNetwork) {
            for (int x = 0; x < extent.getXValue(); x++) {
                for (int y = 0; y < extent.getYValue(); y++) {
                    Cell cell = (Cell) ((Array2DBase) getScape().getSpace()).get(x, y);
                    CellOccupant occupant = (CellOccupant) cell.getOccupant();
                    if (occupant != null) {
                        List network = occupant.getNetwork();
                        bufferedGraphics.setColor(Color.darkGray);
                        for (Iterator iterator = network.iterator(); iterator.hasNext();) {
                            Cell target = (Cell) iterator.next();
                            if (target.getCoordinate() != null) {
                                int x2 = ((Coordinate2DDiscrete) target.getCoordinate()).getXValue();
                                int y2 = ((Coordinate2DDiscrete) target.getCoordinate()).getYValue();
                                bufferedGraphics.drawLine(x * agentSize + agentSize / 2 - 1, y * agentSize + agentSize / 2 - 1, x2 * agentSize + agentSize / 2 - 1, y2 * agentSize + agentSize / 2 - 1);
                            }
                        }
                    }
                }
            }
        }
        if (drawFarNeighbors) {
            bufferedGraphics.setColor(Color.yellow.darker());
            for (int x = 0; x < extent.getXValue(); x++) {
                for (int y = 0; y < extent.getYValue(); y++) {
                    drawNeighborsFor(bufferedGraphics, (Cell) ((Array2DBase) getScape().getSpace()).get(x, y));
                    bufferedGraphics.translate(0, td);
                }
                bufferedGraphics.translate(td, (-extent.getYValue()) * td);
            }
            bufferedGraphics.translate((-extent.getXValue()) * td, 0);
        }
        drawSelectedAgents();
    }

    /**
     * Draw selected agents.
     */
    private void drawSelectedAgents() {
    }

    /* (non-Javadoc)
     * @see org.ascape.view.vis.AgentSizedView#calculateViewSizeForAgentSize(int)
     */
    public Dimension calculateViewSizeForAgentSize(int cellSize) {
        return new Dimension(((Coordinate2DDiscrete) scape.getExtent()).getXValue() * (cellSize + borderSize) + borderSize,
            ((Coordinate2DDiscrete) scape.getExtent()).getYValue() * (cellSize + borderSize) + borderSize);
    }

    /* (non-Javadoc)
     * @see org.ascape.view.vis.AgentSizedView#calculateAgentSizeForViewSize(java.awt.Dimension)
     */
    public int calculateAgentSizeForViewSize(Dimension d) {
        int width = (int) (d.getWidth() - borderSize) / ((Array2DBase) getScape().getSpace()).getXSize() - borderSize;
        int height = (int) (d.getHeight() - borderSize) / ((Array2DBase) getScape().getSpace()).getYSize() - borderSize;
        return Math.max(1, Math.min(width, height));
    }

    /**
     * Method called once a model is deserialized.
     * 
     * @return true, if is draw by feature
     */
/*
    // MEI - I have been having trouble getting an Overhead2DView to respond to
    // mouse clicks once it has been deserialized, so for now we will just leave
    // it hidden by not giving it a new ViewFrameBridge. We can put our call
    // to make a new Overhead2DView in createGraphicsViews instead of createViews,
    // and that way a new Overhead2DView will be made each time you deserialize.
    public void onDeserialized() {
        onChangeIterationsPerRedraw();
        new ViewFrameBridge(this);
    }
*/

    public boolean isDrawByFeature() {
        return drawByFeature;
    }

    /**
     * Sets the draw by feature.
     * 
     * @param drawByFeature
     *            the new draw by feature
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
     *            the draw network
     */
    public void setDrawNetwork(boolean drawNetwork) {
        this.drawNetwork = drawNetwork;
    }

    /**
     * Should this view draw neighbor connections between agents?.
     * 
     * @param drawSelectedNeighbors
     *            the draw selected neighbors
     */
    public void setDrawSelectedNeighbors(boolean drawSelectedNeighbors) {
        this.drawSelectedNeighbors = drawSelectedNeighbors;
    }

    /**
     * Sets the draw far neighbors.
     * 
     * @param drawFarNeighbors
     *            the new draw far neighbors
     */
    public void setDrawFarNeighbors(boolean drawFarNeighbors) {
        this.drawFarNeighbors = drawFarNeighbors;
    }
}
