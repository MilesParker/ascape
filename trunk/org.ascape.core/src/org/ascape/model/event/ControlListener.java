/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.event;

import java.util.EventListener;

/**
 * An interface describing an obect which can receive control events.
 * 
 * @author Miles Parker
 * @version 2.9.1
 * @history 2.9.1 7/10/02 Refacotred to .event, changed names to better conform
 *          to standard usage
 * @since 1.0
 */
public interface ControlListener extends EventListener {

    /**
     * Respond control.
     * 
     * @param event
     *            the event
     */
    public void respondControl(ControlEvent event);
}
