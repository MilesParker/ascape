/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.util.data;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Observable;
import java.util.Vector;

import org.ascape.model.Scape;


/**
 * A group of data points and their corresponding series (pl) of measurements.
 *
 * @author Miles Parker
 * @version 1.0.3
 * @history 1.0.1 11/6/98 documentation, minor changes
 * @history 1.0.2 3/9/99 renamed from SeriesGroup
 * @history 1.0.3 10/02/10 fixed ArrayIndexOutOfBoundsException threading issue
 * @since 1.0
 */
public class DataGroup extends Observable implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * The scape that this data group belongs to. May be null; only used to
     * track collection periods, so this class can be used outside of ascape engine.
     */
    private Scape scape;

    /**
     * The stats that make up the data group.
     */
    private StatCollector[] stats = new StatCollector[0];

    /**
     * A vector of Doubles for each iteration; typically a series {s, s + 1, s + 2 ... s + n},
     * where s is the start period, and s + n is the current period.
     */
    private Vector periods = new Vector();

    /**
     * The data series (pl) that record a measure for a statistic for every iteration.
     */
    private DataSeries[] dataSeries = new DataSeries[0];

    /**
     * Flag to check if the data group's stat collectors
     * should be collecting data over time.
     */
    private boolean collectingLongitudinalData = true;
    
	/** Used to lock {@link #dataSeries} while it is being updated and read. */
	private transient Object lock = new Object();

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		
		// because the lock is transient it will be null
		// create a new Object() during deserialization
		lock = new Object();
	}
	
    /**
     * Adds the supplied stats to this group, creating all appropriate
     * data series for them. The order and index of existing value stats and
     * data series will be retained.
     * @param addedStatCollectors the value stat to add to this group
     *
     * Note to Miles: Right now, each StatCollector knows whether or not it is collecting
     * longitudinal data.
     */
    public void add(StatCollector[] addedStatCollectors) {
        //Create new series for value stats
        Vector vectorStatCollectorsSeries = new Vector();
        for (int j = 0; j < addedStatCollectors.length; j++) {
            DataSeries[] tempDataSeries = addedStatCollectors[j].getAllDataSeries();
            for (int k = 0; k < tempDataSeries.length; k++) {
                // check to see if the DataSeries should be a DataSeriesStore
                //if (tempDataSeries[k].isCollecting()) {
                //}
                if (tempDataSeries[k] != null) {
                    vectorStatCollectorsSeries.addElement(tempDataSeries[k]);
                }
            }
        }
        DataSeries[] newStatCollectorsSeries = new DataSeries[dataSeries.length + vectorStatCollectorsSeries.size()];
        int j = 0;
        for (; j < dataSeries.length; j++) {
            newStatCollectorsSeries[j] = dataSeries[j];
        }
        for (; j < newStatCollectorsSeries.length; j++) {
            newStatCollectorsSeries[j] = (DataSeries) vectorStatCollectorsSeries.elementAt(j - dataSeries.length);
        }
        
		// synchronize access when updating dataSeries
		synchronized (lock) {
			dataSeries = newStatCollectorsSeries;
		}
        
        if (periods == null) {
            periods = new Vector();
        }

        StatCollector[] newStatCollectors = new StatCollector[stats.length + addedStatCollectors.length];
        int i = 0;
        for (; i < stats.length; i++) {
            newStatCollectors[i] = stats[i];
        }
        for (; i < newStatCollectors.length; i++) {
            newStatCollectors[i] = addedStatCollectors[i - stats.length];
        }
        stats = newStatCollectors;
        setChanged();
        notifyObservers();
    }

    public boolean removeStatCollector(StatCollector toBeRemoved) {
        StatCollector[] temp = new StatCollector[stats.length-1];
        boolean found = false;
        // remove from stats array
        for (int i = 0, j = 0; i < stats.length; i++) {
            StatCollector statCollector = stats[i];
            if (statCollector.getName().equalsIgnoreCase(toBeRemoved.getName())) {
                // don't increment j for this one
                found = true;
            } else {
                temp[j++] = stats[i];
            }
        }
        stats = temp;
        return found;
    }

    /**
     * Return the stats that comprise this group of data.
     */
    public StatCollector[] getStatCollectors() {
        return stats;
    }

    /**
     * Called when all data has been collected for a data group for a given period.
     * If scape not null, adds an element to periods for current scape period.
     * Updates data series statistics.
     */
    public void update() {
        if (scape != null) {
            periods.addElement(new Integer(scape.getPeriod()));
        }
        for (int i = 0; i < dataSeries.length; i++) {
            dataSeries[i].addValue();
        }
    }

    /**
     * Clears all data for the group. Called when it is time to collect a new set of data.
     * Creates a new blank periods, and clears all data series data and statistics.
     */
    public void clear() {
        //super.clear();
        periods.clear();
        //period = 0;
        for (int i = 0; i < dataSeries.length; i++) {
            dataSeries[i].clear();
        }
        setChanged();
        notifyObservers();
    }


    /**
     * Sets the scape that this data group is primarily related to.
     * @param scape the scape this agent is belongs to
     */
    public void setScape(Scape scape) {
        this.scape = scape;
    }

    /**
     * Returns the scape that this data group is primarily related to.
     */
    public Scape getScape() {
        return scape;
    }

    /**
     * Returns a vector of periods for which statistics have been collected.
     */
    public Vector getPeriods() {
        return periods;
    }

    /**
     * Returns the total number of data series that comprise this group.
     */
    public int getSize() {
        return dataSeries.length;
    }

    /**
     * Returns the internal index of the series from the series name.
     * @param name the name of the series to get the index for
     * @return the index in this group, -1 if not in this group
     */
    public int getIndexOfSeries(String name) {
        //Allow use of "total" for temporary backward compatibility
        if (name.startsWith("Total")) {
            getScape().getEnvironment().getConsole().println("***Warning, using \"" + name + "\" to reference statistic series.");
            name = "Sum " + name.substring(6, Integer.MAX_VALUE);
            getScape().getEnvironment().getConsole().println("Usage is deprecated, use \"" + name + "\" instead.***");
        }
        for (int i = 0; i < dataSeries.length; i++) {
            if (dataSeries[i].getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the internal index of the series.
     * @param series the series to get the index for, -1 if not in this group
     * @return the index in this group, -1 if not in this group
     */
    public int getIndexOfSeries(DataSeries series) {
        for (int i = 0; i < dataSeries.length; i++) {
            if (dataSeries[i] == series) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the series at the provided index.
     */
    public DataSeries getSeries(int index) {
        if (dataSeries[index].isCollecting()) {
            return (DataSeriesStore) dataSeries[index];
        }
        return dataSeries[index];
    }

    /**
     * Returns the series with the provided name.
     */
    public DataSeries getSeries(String name) {
        if (dataSeries[getIndexOfSeries(name)].isCollecting()) {
            return (DataSeriesStore) dataSeries[getIndexOfSeries(name)];
        }
        return dataSeries[getIndexOfSeries(name)];
    }

    /**
     * Returns the names of every data series in this group.
     */
    public String[] getSeriesNames() {
		String[] names;

		/**
		 * Called by the AWT thread. We need to synchronise access in case its
		 * being updated at the same time by another thread, otherwise the
		 * dataSeries array will change under us and we'll get an
		 * ArrayIndexOutOfBoundsException.
		 */
		synchronized (lock) {
			names = new String[dataSeries.length];
			for (int i = 0; i < dataSeries.length; i++) {
				names[i] = dataSeries[i].getName();
			}
		}
		return names;
    }

    /**
     * Returns the internal index of the stat from the stat name.
     */
    protected int getIndexOfStatCollector(String name) {
        for (int i = 0; i < stats.length; i++) {
            if (stats[i].getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the stat at the provided index.
     */
    public StatCollector getStatCollector(int index) {
        return stats[index];
    }

    /**
     * Returns the stat with the provided name. If no such sta exists, returns null.
     */
    public StatCollector getStatCollector(String name) {
        int index = getIndexOfStatCollector(name);
        if (index >= 0) {
            return stats[getIndexOfStatCollector(name)];
        } else {
            return null;
        }
    }

    /**
     * Returns the names of every statistic in this group.
     */
    public String[] getStatCollectorNames() {
        String[] names = new String[stats.length];
        for (int i = 0; i < stats.length; i++) {
            names[i] = stats[i].getName();
        }
        return names;
    }

    /**
     * Returns if this data group's series should be collecting longitudinal data
     */
    public boolean isCollectingLongitudinalData() {
        return collectingLongitudinalData;
    }

    public void setCollectingLongitudinalData(boolean collectingLongitudinalData) {
        this.collectingLongitudinalData = collectingLongitudinalData;
    }

}
