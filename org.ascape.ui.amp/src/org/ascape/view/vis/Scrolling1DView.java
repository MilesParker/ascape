/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */

package org.ascape.view.vis;

import org.ascape.model.Agent;
import org.ascape.model.LocatedAgent;
import org.ascape.util.vis.DrawFeature;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;



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
    }

    public synchronized void createImage() {
        Dimension size = getPreferredSizeWithin(imageFigure.getSize());
        if (size.width <= 0 || size.height <= 0) {
            size = new Dimension(1, 1);
        }
        Image oldImage = image;
        GC oldGC = imageGC;
        Graphics oldGraphics = imageGraphics;
        image = new Image(Display.getCurrent(), new org.eclipse.swt.graphics.Rectangle(0, 0, size.width, size.height));
        imageGC = new GC(image);
        imageGraphics = new SWTGraphics(imageGC);
        if (oldImage != null) {
            imageGC.drawImage(oldImage, 0, agentSize);
        }
        if (oldImage != null && !oldImage.isDisposed()) {
            oldGraphics.dispose();
            oldImage.dispose();
            oldGC.dispose();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ascape.view.vis.AgentSizedView#calculateViewSizeForAgentSize(int)
     */
    @Override
    public Dimension calculateViewSizeForAgentSize(Dimension availableSize, int cellSize) {
        return new Dimension(agentSize * getScape().getSize(), availableSize.height);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ascape.view.vis.AgentSizedView#calculateAgentSizeForViewSize(java.awt.Dimension)
     */
    @Override
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
        if (getSize().height > 0) {
            Object[] drawFeatures = getDrawSelection().getSelection();
            if (drawFeatures.length > 0) {
                // graphics.copyArea(0, agentSize, getSize().width, getSize().width, 0, -agentSize);
                // Transform tr = createTR(graphics);
                // g.translate(0, getAgentSize());
                for (Object object : getScape()) {
                    Agent agent = (Agent) object;
                    for (Object drawFeature : drawFeatures) {
                        ((DrawFeature) drawFeature).draw(g, agent, agentSize, agentSize);
                    }
                    g.translate(agentSize, 0);
                }
            }
        }
    }
}
