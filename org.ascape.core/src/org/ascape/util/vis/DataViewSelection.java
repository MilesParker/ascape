/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.util.vis;

import org.ascape.util.data.DataSelection;

/**
 * A group of data series which may be selected, and which provides a settable
 * SeriesRepresentation (and other view realted features, as needed) for each element in the backing data
 * group. Items are reachable through selection in index or directly.
 *
 * @author Miles Parker
 * @version 1.2
 * @history 1.2 7/22/99 updated to support series view instead of a single color
 * @history 1.0.1 11/2/98 moved to observe from util
 * @history 1.0.2 3/9/99 renamed from SeriesGroupViewSelection
 * @history 1.0.3 3/17/99 documented, etc..
 * @since 1.0
 */
public class DataViewSelection extends DataSelection {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * An array mirroring selection that tracks selected seriesView
     */
    private SeriesRepresentation[] seriesViews = new SeriesRepresentation[0];

    /**
     * Updates the selection in response to a change in the data group.
     */
    public void update() {
        super.update();
        SeriesRepresentation[] newSeriesViews = new SeriesRepresentation[dataGroup.getSize()];
        int i = 0;
        for (; i < Math.min(seriesViews.length, newSeriesViews.length); i++) {
            newSeriesViews[i] = seriesViews[i];
        }
        for (; i < newSeriesViews.length; i++) {
            newSeriesViews[i] = new SeriesRepresentation();
        }
        seriesViews = newSeriesViews;
    }

    /**
     * Return the seriesView for the series with the given name in the data group.
     * @param name the name of the series within the data group
     */
    public SeriesRepresentation getSeriesView(String name) {
        int index = dataGroup.getIndexOfSeries(name);
        if (index != -1) {
            return seriesViews[index];
        } else {
            throw new RuntimeException("Series \"" + name + "\" does not exist.");
        }
    }

    /**
     * Sets the seriesView for the named series to the supplied seriesView.
     * @param name the name of the series within the data group
     * @param seriesView the seriesView to assign to the series
     */
    public void setSeriesView(String name, SeriesRepresentation seriesView) {
        int index = dataGroup.getIndexOfSeries(name);
        if (index != -1) {
            seriesViews[index] = seriesView;
        } else {
            throw new RuntimeException("Series \"" + name + "\" does not exist.");
        }
    }

    /**
     * Return the seriesView for the series at the supplied index.
     * @param index the index of the series within the data group
     */
    public SeriesRepresentation getSeriesView(int index) {
        return seriesViews[index];
    }

    /**
     * Return the seriesView for the selected series at the supplied index.
     * @param index the index of the series within the selection
     */
    public SeriesRepresentation getSelectedSeriesView(int index) {
        return getSeriesView(getIndexOfSelectedIndex(index));
    }

    /**
     * Sets the seriesView for the selected series at the supplied index to the supplied seriesView.
     * @param index the index of the series within the selection
     * @param seriesView the seriesView to assign to the series
     */
    public void setSelectedSeriesView(int index, SeriesRepresentation seriesView) {
        seriesViews[getIndexOfSelectedIndex(index)] = seriesView;
    }
}
