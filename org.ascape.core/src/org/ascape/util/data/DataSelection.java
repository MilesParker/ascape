/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */

package org.ascape.util.data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * A selection of a group of data series, useful for providing some subset of available data.
 * Methods are provided for selecting data and notifying observers of changes in the selection.
 * Shares much the same interface with VectorSelection, and they will probably be integrated once
 * collections comes into use.
 *
 * @author Miles Parker
 * @version 1.0.1
 * @history 1.0.1 3/9/99 renamed from SeriesGroupSelection
 * @since 1.0
 * @see org.ascape.util.VectorSelection
 */
public class DataSelection extends Observable implements Observer, Cloneable, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * The backing data group for this selection.
     */
    protected DataGroup dataGroup;

    /**
     * An array indicating selection status for series of the data group.
     */
    private boolean[] seriesSelected = new boolean[0];

    /**
     * The number of series currently selected.
     */
    private int selectedCount = 0;

    /**
     * Returns the backing data group.
     */
    public DataGroup getData() {
        return dataGroup;
    }

    /**
     * Sets the backing data group for this selection.
     * Adds this selection as a n observer of its data group.
     * Initializes the selection.
     * @param dataGroup the data group this object is a selection of
     */
    public void setData(DataGroup dataGroup) {
        if (this.dataGroup != null) {
            this.dataGroup.deleteObserver(this);
        }
        this.dataGroup = dataGroup;
        if (this.dataGroup != null) {
            this.dataGroup.addObserver(this);
            update();
        }
    }

    /**
     * Updates the selection in response to a change in the data group.
     */
    public void update() {
        //For now, the backing group is expected to only support adding.
        //This code will have to be updated if data groups are given support for
        //removing and/or reordering series.
        boolean[] newSeriesSelected = new boolean[dataGroup.getSize()];
        System.arraycopy(seriesSelected, 0, newSeriesSelected, 0, seriesSelected.length);
        seriesSelected = newSeriesSelected;
    }

    /**
     * Is the supplied series selected in this selection?
     * @param series the series to determine selection status for
     * @return true if the item is selected, false if the item is not selected or is not in the list
     */
    public boolean isSelected(DataSeries series) {
        for (int i = 0; i < seriesSelected.length; i++) {
            if (seriesSelected[i] && dataGroup.getSeries(i) == series) {
                return true;
            }
        }
        return false;
    }

    /**
     * Is the series at the supplied index selected in this selection?
     * @param index the data group index of the series to determine selection status for
     * @return true if the item is selected
     */
    public boolean isSelected(int index) {
        return seriesSelected[index];
    }

    /**
     * Selects every series in the data group.
     */
    public synchronized void selectAll() {
        selectedCount = dataGroup.getSize();
        for (int i = 0; i < seriesSelected.length; i++) {
            seriesSelected[i] = true;
        }
        setChanged();
        notifyObservers();
    }

    /**
     * Sets selection to none, unselecting every series in the data group.
     */
    public synchronized void clearSelection() {
        selectedCount = 0;
        for (int i = 0; i < seriesSelected.length; i++) {
            seriesSelected[i] = false;
        }
        setChanged();
        notifyObservers();
    }

    /**
     * Sets the series at the supplied index to the selection status indicated.
     * @param index the data group index of the series to set select status for
     * @param select true to select the item, false to unselect it
     */
    public synchronized void setSelected(int index, boolean select) {
        if (select && !seriesSelected[index]) {
            selectedCount++;
        } else if (!select && seriesSelected[index]) {
            selectedCount--;
        }
        seriesSelected[index] = select;
        setChanged();
        notifyObservers();
    }

    /**
     * Sets the series at the supplied index to the selection status indicated.
     * @param name the name of the series to set select status for
     * @param select true to select the item, false to unselect it
     */
    public synchronized void setSelected(String name, boolean select) {
        int index = dataGroup.getIndexOfSeries(name);
        if (index != -1) {
            setSelected(index, select);
        } else {
            throw new RuntimeException("Series \"" + name + "\" does not exist.");
        }
    }

    /**
     * Sets the supplied series to the selection status indicated.
     * @param series the series to set selection status for
     * @param select true to select the item, false to unselect it
     */
    public synchronized void setSelected(DataSeries series, boolean select) {

        int index = dataGroup.getIndexOfSeries(series);
        if (index != -1) {
            setSelected(index, select);
        } else {
            throw new RuntimeException("Series does not exist.");
        }
    }

    /**
     * Returns the number of selected series.
     */
    public int getSelectionSize() {
        return selectedCount;
    }

    /**
     * Returns the index in the backing data group of the series at the selection index.
     * @param index the selected series to get the backing group index for
     */
    public int getIndexOfSelectedIndex(int index) {
        int count = 0;
        for (int i = 0; i < dataGroup.getSize(); i++) {
            if (seriesSelected[i]) {
                if (count == index) {
                    return i;
                }
                count++;
            }
        }
        throw new RuntimeException("Selection index out of range in DataSelection.");
    }

    /**
     * Returns the index in the backing data group of the series at the selection index.
     * @param name the selected series to get the backing group index for
     */
    public int getSelectedIndexOf(String name) {
        //Allow use of "total" for temporary backward compatibility
        if (name.startsWith("Total")) {
            System.out.println("***Warning, using \"" + name + "\" to reference statistic series.");
            name = "Sum " + name.substring(6, Integer.MAX_VALUE);
            System.out.println("Usage is deprecated, use \"" + name + "\" instead.***");
        }
        int count = 0;
        for (int i = 0; i < dataGroup.getSize(); i++) {
            if (seriesSelected[i]) {
                if (dataGroup.getSeries(i).getName().equals(name)) {
                    return count;
                }
                count++;
            }
        }
        return -1;
    }

    /**
     * Returns the name of the series at the selection index.
     * @param index the index in this selection of the series to get the name of
     */
    public String getSelectedName(int index) {
        return dataGroup.getSeriesNames()[getIndexOfSelectedIndex(index)];
    }

    /**
     * Returns the series at the selection index.
     * @param index the index in this selection of the series
     */
    public DataSeries getSelectedSeries(int index) {
        return dataGroup.getSeries(getIndexOfSelectedIndex(index));
    }

    /**
     * Returns a list of all selected series.
     */
    public List getSelectedSeries() {
        DataSeries[] allSelected = new DataSeries[selectedCount];
        int index = 0;
        for (int i = 0; i < dataGroup.getSize(); i++) {
            if (seriesSelected[i]) {
                allSelected[index] = dataGroup.getSeries(i);
                index++;
            }
        }
        return Arrays.asList(allSelected);
    }

    /**
     * Returns the data, of the series at the selection index. (Convenience method.)
     * 
     * @throws ClassCastException
     *         if the series is not a DataSeriesStore
     * @param index
     *        the index in this selection of the series
     */
    public List getSelectedSeriesData(int index) {
        try {
            return ((DataSeriesStore) getSelectedSeries(index)).toList();
        } catch (RuntimeException e) {
            return null;
        }
    }

    /**
     * Returns the maximum value across all series in the selection.
     */
    public double getMax() {
        update();
        double max = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < seriesSelected.length; i++) {
            if (seriesSelected[i]) {
                if (dataGroup.getSeries(i).getMax() > max) {
                    max = dataGroup.getSeries(i).getMax();
                }
            }
        }
        return max;
    }

    /**
     * Returns the minimum value across all series in the selection.
     */
    public double getMin() {
        update();
        double min = Double.POSITIVE_INFINITY;
        for (int i = 0; i < seriesSelected.length; i++) {
            if (seriesSelected[i]) {
                if (dataGroup.getSeries(i).getMin() < min) {
                    min = dataGroup.getSeries(i).getMin();
                }
            }
        }
        return min;
    }

    /**
     * Notifies this selection that a change in the backing data group has occured.
     */
    public void update(Observable observed, Object arg) {
        if (observed == dataGroup) {
            update();
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Clones this selectable data group, retaining the original's data group,
     * and copying selection.
     */
    public Object clone() {
        try {
            DataSelection clone = (DataSelection) super.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }
}
