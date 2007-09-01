/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.view.vis;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.VolatileImage;
import java.util.TooManyListenersException;

import org.ascape.model.Agent;
import org.ascape.model.Cell;
import org.ascape.model.LocatedAgent;
import org.ascape.model.event.ScapeEvent;
import org.ascape.model.space.Coordinate1DDiscrete;
import org.ascape.util.vis.DrawFeature;

/**
 * A view that provides a vertically scrolling view appropriate for dispalying a
 * simple one-dimensional collection of agents; i.e. a 1DCA. To Do: Support
 * color features ala Overhead2DView.
 * 
 * @author Miles Parker
 * @version 1.9.2
 * @history 1.9.2 3/1/01 Fixed off-by-one display issue in updateScapeGraphics
 * @history 1.5 Changed so that instead of displaying wraped around agents a
 *          gray border is creted if the view is resized larger than the normal
 *          view size
 * @history 1.5 various small changes since 1.0
 * @since 1.0
 */
public class Scrolling1DView extends CellView {

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
     *            a user relevant name for this view
     */
    public Scrolling1DView(String name) {
        setName(name);
        setCellSize(2);
        setDelegate(new ComponentViewDelegate(this) {
            public synchronized void scapeNotification(ScapeEvent scapeEvent) {
                super.scapeNotification(scapeEvent);
                //Updatge graphics even when we are _not_ going to be painting to the screen this period
                if ((((ComponentView) getScapeListener()).getViewFrame() != null) && (scapeEvent.getID() != ScapeEvent.TICK) && ((Container) getScapeListener()).isVisible() && ((Container) getScapeListener()).isShowing() && ((currentUpdate != ((ComponentView) getScapeListener()).getIterationsPerRedraw()) && (scape.isInitialized()))) {
                    ((ComponentView) getScapeListener()).updateScapeGraphics();
                }
            }
        });
        setSize(200, 300);
    }

    /**
     * Notifies the listener that the scape has added it. Sets the maximum agent
     * width.
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
        setSize(new Dimension(scape.getSize() * getAgentSize(), 300));
    }


    /* (non-Javadoc)
     * @see org.ascape.view.vis.BufferView#buildGraphicsBuffer()
     */
    public void buildGraphicsBuffer() {
        //For some reason. size is changing between the first call and the second!
        if (bufferedGraphics != null) {
            bufferedGraphics.dispose();
        }
        int width = (int) getSize().getWidth();
        int height = (int) getSize().getHeight();
        if (width * height <= 0) {
            width = (int) getPreferredSize().getWidth();
            height = (int) getPreferredSize().getHeight();
        }
        VolatileImage newImage = createVolatileImage(width, height);
        Graphics newGraphics = newImage.getGraphics();
        if (bufferedImage != null) {
            newGraphics.drawImage(bufferedImage, 0, height - bufferedImage.getHeight(null), width, bufferedImage.getHeight(null), this);
        }
        bufferedImage = newImage;
        bufferedGraphics = newGraphics;
    }

    /**
     * Sets the size of the view, rescaling and moving the buffer as neccesary.
     * 
     * @param x
     *            the x
     * @param y
     *            the y
     * @param width
     *            the width
     * @param height
     *            the height
     */
    public synchronized void setBounds(int x, int y, int width, int height) {
        if (height > 0) {
            height = (height / agentSize) * agentSize;
        }
        super.setBounds(x, y, width, height);
    }

    /* (non-Javadoc)
     * @see org.ascape.view.vis.AgentSizedView#getPreferredSizeWithin(java.awt.Dimension)
     */
    public Dimension getPreferredSizeWithin(Dimension d) {
        int tempCellSize = calculateAgentSizeForViewSize(d);
        return new Dimension(scape.getSize() * tempCellSize,
            (int) d.getHeight());
    }

    /* (non-Javadoc)
     * @see org.ascape.view.vis.AgentSizedView#calculateViewSizeForAgentSize(int)
     */
    public Dimension calculateViewSizeForAgentSize(int cellSize) {
        return new Dimension(scape.getSize() * cellSize,
            (int) getSize().getHeight());
    }

    /* (non-Javadoc)
     * @see org.ascape.view.vis.AgentSizedView#calculateAgentSizeForViewSize(java.awt.Dimension)
     */
    public int calculateAgentSizeForViewSize(Dimension d) {
        int width = (int) d.getWidth() / getScape().getSize();
        return Math.max(1, width);
    }

    /* (non-Javadoc)
     * @see org.ascape.view.vis.AgentView#getAgentAtPixel(int, int)
     */
    public Agent getAgentAtPixel(int x, int y) {
        int p = x / agentSize;
        if (p < 0) {
//	        return (Agent) ((ScapeArray1D) scape).get(0);
            return (Agent) getScape().get(0);
        } else if (p >= scape.getSize()) {
//	        return (Agent) ((ScapeArray1D) scape).get(scape.getSize() - 1);
            return (Agent) getScape().get(scape.getSize() - 1);
        } else {
//	        return (Agent) ((ScapeArray1D) scape).get(p);
            return (Agent) getScape().get(p);
        }
    }

    /* (non-Javadoc)
     * @see org.ascape.view.vis.AgentView#drawSelectedAgent(java.awt.Graphics, org.ascape.model.LocatedAgent)
     */
    public void drawSelectedAgent(Graphics g, LocatedAgent a) {
        //noop, we want to draw from paint, not here
    }

    /**
     * On notification of a scape update, draws the next line of the view, and
     * copies the buffer upwards to scroll the view.
     */
    public synchronized void updateScapeGraphics() {
        if ((scape != null) && (scape.isInitialized()) && (drawSelection != null)) {
            if (scape.getIteration() != lastIteration) {
                Object[] drawFeatures = getDrawSelection().getSelection();
                if (drawFeatures.length > 0) {
                    bufferedGraphics.copyArea(0, agentSize, this.getSize().width, this.getSize().height, 0, -agentSize);
                    int lastRow = (this.getSize().height - agentSize);
                    bufferedGraphics.translate(0, lastRow);
                    if (drawFeatures.length != 0) {
                        for (int p = 0; p < scape.getSize(); p++) {
//                            Cell cell = (Agent) ((ScapeArray1D) scape).get(p);
                            Agent agent = (Agent) getScape().get(p);
                            for (int i = 0; i < drawFeatures.length; i++) {
                                ((DrawFeature) drawFeatures[i]).draw(bufferedGraphics, agent, agentSize, agentSize);
                            }
                            bufferedGraphics.translate(agentSize, 0);
                        }
                    } else {
//Special common case where there is only one draw feature
                        DrawFeature f = ((DrawFeature) drawFeatures[0]);
                        for (int p = 0; p < scape.getSize(); p++) {
//                            Cell cell = ((ScapeArray1D) scape).get(p);
                            Agent agent = (Agent) getScape().get(p);
                            f.draw(bufferedGraphics, agent, agentSize, agentSize);
                            bufferedGraphics.translate(agentSize, 0);
                        }
                    }
                    bufferedGraphics.translate(-scape.getSize() * agentSize, -lastRow);
                    lastIteration = scape.getIteration();
                }
            }
        }
        super.updateScapeGraphics();
    }

    /**
     * Repaints the canvas, drawing the buffer into it. If the view is wider
     * than the buffer (size of agents), copies the buffer alongside it, to
     * provide a continuous view of a periodic scape. To do: override this
     * behavior for aperiod scapes, and provide some means for scrolling the
     * horizontal origin.
     * 
     * @param g
     *            the graphics context.
     */
    public void paint(Graphics g) {
        super.paint(g);
        /*int curPosition = 0;
        while (curPosition < this.getSize().width) {
               if (bufferedImage != null) {
                g.drawImage(bufferedImage, curPosition, -1, null);
            }
            curPosition = curPosition + maxAgentWidth;
        }*/
        if (bufferedImage != null) {
            g.drawImage(bufferedImage, 0, -1, null);
        }
        if (getAgentCustomizer() != null) {
            Cell cell = (Cell) getAgentCustomizer().getAgent();
            if (cell != null) {
                int p = ((Coordinate1DDiscrete) cell.getCoordinate()).getValue();
                g.setColor(Color.yellow);
                g.drawRect(p * agentSize - 1, 0, agentSize, this.getSize().height);
            }
        }
    }
}
