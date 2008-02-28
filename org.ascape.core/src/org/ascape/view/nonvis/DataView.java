/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.view.nonvis;

import java.util.TooManyListenersException;

import org.ascape.model.Scape;
import org.ascape.model.event.ScapeEvent;
import org.ascape.util.data.DataSelection;

/**
 * A non-graphic view providing output of model data to a file. To use,
 * (assuming you are collecting the statistics you are interested in, see Scape)
 * just add this view to any scape, and set a file or data strem for it. Every
 * period, statistic measurements will be written to the file or data strem. By
 * default, all statistics are selected; get data selection to make different
 * selections. DataOutputView provides an implementation of this class that
 * handles multiple files and runs.
 * 
 * @see org.ascape.view.nonvis.DataOutputView
 * @see org.ascape.model.Scape
 * @author Miles Parker
 * @version 1.2 8/4/99
 * @since 1.2
 */
public class DataView extends NonGraphicView {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * The selection of data that is be written.
     */
    protected DataSelection dataSelection;

    /**
     * Returns the data group selection for this data output view. Selected data
     * series will be exported to the file. By default, all series are selected.
     * 
     * @return the data selection
     */
    public DataSelection getDataSelection() {
        return dataSelection;
    }

    /**
     * Notifies the listener that the scape has added it. Creates a new data
     * selection for data output, backed by the Scape's data group.
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
        dataSelection = new DataSelection();
        dataSelection.setData(((Scape) scapeEvent.getSource()).getRunner().getData());
    }
}
