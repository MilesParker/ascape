/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */
package org.ascape.view.nonvis;

import java.io.IOException;
import java.util.TooManyListenersException;

import org.ascape.model.event.ScapeEvent;


/*
 * User: Miles Parker
 * Date: Feb 10, 2005
 * Time: 10:13:45 AM
 */

/**
 * The Class ScapeOutputView.
 */
public class ScapeOutputView extends DataOutputView {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /* (non-Javadoc)
     * @see org.ascape.view.nonvis.DataOutputView#writePeriodData()
     */
    public void writePeriodData() throws IOException {
        super.writePeriodData();
        ((DataScape) getScape()).writePeriodData(getPeriodDataStream());
    }

    /* (non-Javadoc)
     * @see org.ascape.view.nonvis.DataOutputView#writePeriodHeader()
     */
    public void writePeriodHeader() throws IOException {
        super.writePeriodHeader();
        ((DataScape) getScape()).writePeriodHeader(getPeriodDataStream());
    }

    /* (non-Javadoc)
     * @see org.ascape.view.nonvis.DataOutputView#writeRunData()
     */
    public void writeRunData() throws IOException {
        super.writeRunData();
        ((DataScape) getScape()).writeRunData(getRunDataStream());
    }

    /* (non-Javadoc)
     * @see org.ascape.view.nonvis.DataOutputView#writeRunHeader()
     */
    public void writeRunHeader() throws IOException {
        super.writeRunHeader();
        ((DataScape) getScape()).writeRunHeader(getRunDataStream());
    }

    /* (non-Javadoc)
     * @see org.ascape.view.nonvis.DataOutputView#scapeAdded(org.ascape.model.event.ScapeEvent)
     */
    public void scapeAdded(ScapeEvent scapeEvent) throws TooManyListenersException {
        if (scapeEvent.getSource() instanceof DataScape) {
            super.scapeAdded(scapeEvent);
        } else {
            throw new RuntimeException("Scapes using ScapeOutputView must implement DataScape.");
        }
    }
}
