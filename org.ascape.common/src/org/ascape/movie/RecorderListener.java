/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.movie;

/**
 * A marker interface for a recording target that Quicktime can use; e.g. that is paintable.
 * To setup a quicktime target, simply extend this interface, implementing the Paintable classes.
 *
 * @author Miles Parker
 * @version 2.9 (Ascape)
 * @history 5/9/2002 first in
 */
public interface RecorderListener {

    /**
     * Called by the recorder when it has been properly initialized
     */
    void recordingCreated();

    /**
     * Called by the recorder when recording has begun, and we are waiting for the next frame.
     */
    void recordingStarted();

    /**
     * Called by the recorder when all recording has stopped.
     */
    void recordingStopped();
}
