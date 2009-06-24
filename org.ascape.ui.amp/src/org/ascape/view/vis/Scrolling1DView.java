/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */

package org.ascape.view.vis;

import org.ascape.model.Agent;
import org.ascape.model.LocatedAgent;
import org.ascape.util.vis.ColorFeatureConcrete;
import org.ascape.util.vis.DrawFeature;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;



/**
 * A view that provides a vertically scrolling view appropriate for dispalying a simple one-dimensional collection of
 * agents; i.e. a 1DCA. To Do: Support color features ala Overhead2DView.
 * 
 * @author Miles Parker
 * @version 1.9.2
 * @history 1.9.2 3/1/01 Fixed off-by-one display issue in updateScapeGraphics
 * @history 1.5 Changed so that instead of displaying wraped around agents a gray border is creted if the view is
 *          resized larger than the normal view size
 * @history 1.5 various small changes since 1.0
 * @since 1.0
 */
public class Scrolling1DView extends CellView {

    private static final long serialVersionUID = 3644306272230219573L;

    /**
     * The last iteration.
     */
    private int lastIteration = -1;

    /**
     * The max agent width.
     */
    protected int maxAgentWidth;

    /**
     * Constructs a new scrolling view, setting its initial cell size to 2.
     */
    public Scrolling1DView() {
        this("Scrolling 1D View");
    }

    /**
     * Constructs a new scrolling view, setting its initial cell size to 2.
     * 
     * @param name
     *        a user relevant name for this view
     */
    public Scrolling1DView(String name) {
        super(name);
        // setCellSize(2);
        // setDelegate(new ComponentViewDelegate(this) {
        // /**
        // *
        // */
        // private static final long serialVersionUID = 7075869804727213331L;
        //
        // public synchronized void scapeNotification(ScapeEvent scapeEvent) {
        // super.scapeNotification(scapeEvent);
        // //Updatge graphics even when we are _not_ going to be painting to the screen this period
        // if ((((ComponentView) getScapeListener()).getViewFrame() != null) && (scapeEvent.getID() != ScapeEvent.TICK)
        // && ((Container) getScapeListener()).isVisible() && ((Container) getScapeListener()).isShowing() &&
        // ((currentUpdate != ((ComponentView) getScapeListener()).getgetIterPerRedraw()()) &&
        // (getScape().isInitialized()))) {
        // ((ComponentView) getScapeListener()).updateScapeGraphics(Graphics);
        // }
        // }
        // });
        // setSize(200, 300);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ascape.view.vis.BufferView#buildGraphicsBuffer()
     */
    // public void buildGraphicsBuffer() {
    // //For some reason. size is changing between the first call and the second!
    // if (graphics != null) {
    // graphics.dispose();
    // }
    // int width = (int) getSize().getWidth();
    // int height = (int) getSize().getHeight();
    // if (width * height <= 0) {
    // width = (int) getPreferredSize().getWidth();
    // height = (int) getPreferredSize().getHeight();
    // }
    // VolatileImage newImage = createVolatileImage(width, height);
    // Graphics newGraphics = newImage.getGraphics();
    // if (bufferedImage != null) {
    // newGraphics.drawImage(bufferedImage, 0, height - getSize().width, width, getSize().width, this);
    // }
    // bufferedImage = newImage;
    // graphics = newGraphics;
    // }
    /**
     * Sets the size of the view, rescaling and moving the buffer as neccesary.
     * 
     * @param x
     *        the x
     * @param y
     *        the y
     * @param width
     *        the width
     * @param height
     *        the height
     */

    // public synchronized void setBounds(int x, int y, int width, int height) {
    // if (height > 0) {
    // height = (height / agentSize) * agentSize;
    // }
    // super.setBounds(x, y, width, height);
    // }
    /*
     * (non-Javadoc)
     * 
     * @see org.ascape.view.vis.AgentSizedView#getPreferredSizeWithin(java.awt.Dimension)
     */
    public Dimension getPreferredSizeWithin(Dimension d) {
        int tempCellSize = calculateAgentSizeForViewSize(d);
        return new Dimension(Math.max(getScape().getSize(), 1) * tempCellSize, Math.max(1, d.height));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ascape.view.vis.AgentSizedView#calculateViewSizeForAgentSize(int)
     */
    public Dimension calculateViewSizeForAgentSize(Dimension availableSize, int cellSize) {
        return new Dimension(Math.max(getScape().getSize(), 1) * cellSize, Math.max(1, availableSize.width));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ascape.view.vis.AgentSizedView#calculateAgentSizeForViewSize(java.awt.Dimension)
     */
    public int calculateAgentSizeForViewSize(Dimension d) {
        int width = d.width / getScape().getSize();
        return Math.max(1, width);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ascape.view.vis.AgentView#getAgentAtPixel(int, int)
     */
    public Agent getAgentAtPixel(int x, int y) {
        int p = x / agentSize;
        if (p < 0) {
            // return (Agent) ((ScapeArray1D) getScape()).get(0);
            return (Agent) getScape().get(0);
        } else if (p >= getScape().getSize()) {
            // return (Agent) ((ScapeArray1D) getScape()).get(scape.getSize() - 1);
            return (Agent) getScape().get(getScape().getSize() - 1);
        } else {
            // return (Agent) ((ScapeArray1D) getScape()).get(p);
            return (Agent) getScape().get(p);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ascape.view.vis.AgentView#drawSelectedAgent(java.awt.Graphics, org.ascape.model.LocatedAgent)
     */
    public void drawSelectedAgent(Graphics g, LocatedAgent a) {
        // noop, we want to draw from paint, not here
    }

    /**
     * On notification of a scape update, draws the next line of the view, and copies the buffer upwards to scroll the
     * view.
     */
    public void updateScapeGraphics(Graphics g) {
        if (getScape().getIteration() != lastIteration) {
            Object[] drawFeatures = getDrawSelection().getSelection();
            if (drawFeatures.length > 0) {
                // graphics.copyArea(0, agentSize, getSize().width, getSize().width, 0, -agentSize);
                int lastRow = getSize().width - agentSize;
                // Transform tr = createTR(graphics);
                g.translate(0, lastRow);
                if (drawFeatures.length != 0) {
                    for (int p = 0; p < getScape().getSize(); p++) {
                        g.setBackgroundColor(ColorFeatureConcrete.CYAN);
                        g.fillOval(0, 0, 3, 3);
                        // Cell cell = (Agent) ((ScapeArray1D) getScape()).get(p);
                        Agent agent = (Agent) getScape().get(p);
                        for (Object drawFeature : drawFeatures) {
                            ((DrawFeature) drawFeature).draw(g, agent, agentSize, agentSize);
                        }
                        g.translate(agentSize, 0);
                    }
                } else {
                    // Special common case where there is only one draw feature
                    DrawFeature f = (DrawFeature) drawFeatures[0];
                    for (int p = 0; p < getScape().getSize(); p++) {
                        // Cell cell = ((ScapeArray1D) getScape()).get(p);
                        Agent agent = (Agent) getScape().get(p);
                        f.draw(g, agent, agentSize, agentSize);
                        g.translate(agentSize, 0);
                    }
                }
                g.translate(-getScape().getSize() * agentSize, -lastRow);
                lastIteration = getScape().getIteration();
            }
        }
    }

    /**
     * Repaints the canvas, drawing the buffer into it. If the view is wider than the buffer (size of agents), copies
     * the buffer alongside it, to provide a continuous view of a periodic scape. To do: override this behavior for
     * aperiod scapes, and provide some means for scrolling the horizontal origin.
     * 
     * @param g
     *        the graphics context.
     */
    // public void paint(Graphics g) {
    // super.paint(g);
    // /*int curPosition = 0;
    // while (curPosition < this.getSize().width) {
    // if (bufferedImage != null) {
    // g.drawImage(bufferedImage, curPosition, -1, null);
    // }
    // curPosition = curPosition + maxAgentWidth;
    // }*/
    // if (bufferedImage != null) {
    // g.drawImage(bufferedImage, 0, -1, null);
    // }
    // if (getAgentCustomizer() != null) {
    // Cell cell = (Cell) getAgentCustomizer().getAgent();
    // if (cell != null) {
    // int p = ((Coordinate1DDiscrete) cell.getCoordinate()).getValue();
    // g.setForegroundColor(Color.yellow);
    // g.drawRectangle(p * agentSize - 1, 0, agentSize, this.getSize().height);
    // }
    // }
    // }
}
