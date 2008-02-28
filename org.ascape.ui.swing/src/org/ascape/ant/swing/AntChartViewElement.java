/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.ant.swing;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TooManyListenersException;

import org.ascape.ant.ChartSeriesElement;
import org.ascape.ant.RectangleElement;
import org.ascape.ant.ViewElement;
import org.ascape.model.Scape;
import org.ascape.model.event.ScapeEvent;
import org.ascape.view.vis.ChartView;

/**
 * The Class AntChartView.
 */
public class AntChartViewElement extends ChartView implements ViewElement, Serializable {

    /**
     * The series elements.
     */
    List seriesElements = new ArrayList();

    /**
     * The new bounds.
     */
    Rectangle newBounds;

    /**
     * Adds the configured series.
     * 
     * @param series
     *            the series
     */
    public void addConfiguredSeries(ChartSeriesElement series) {
        //Have to differ adding to chart as it hasn't been created yet.
        seriesElements.add(series);
    }

    /* (non-Javadoc)
     * @see org.ascape.view.vis.ChartView#scapeAdded(org.ascape.model.event.ScapeEvent)
     */
    public void scapeAdded(ScapeEvent scapeEvent) throws TooManyListenersException {
        super.scapeAdded(scapeEvent);
        for (Iterator iterator = seriesElements.iterator(); iterator.hasNext();) {
            ChartSeriesElement series = (ChartSeriesElement) iterator.next();
            Color color = series.getColor();
            if (color == null) {
                color = new Color((float) Math.random(), (float) Math.random(), (float) Math.random());
            }
            addSeries(series.getValueName(), color);
        }
    }

    /* (non-Javadoc)
     * @see org.ascape.view.vis.PanelView#build()
     */
//    public void build() {
//        super.build();
//        if (newBounds != null) {
//            this.getViewFrame().getFrameImp().setBounds(newBounds);
//        }
//    }

    /**
     * Adds the window bounds.
     * 
     * @param r
     *            the r
     */
    public void addWindowBounds(RectangleElement r) {
        newBounds = r;
    }

    /* (non-Javadoc)
     * @see org.ascape.ant.ViewElement#addToScape(org.ascape.model.Scape)
     */
    public void addToScape(Scape scape) {
        scape.addView(this);
    }
}
