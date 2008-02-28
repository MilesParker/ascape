/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.view.nonvis;

import org.ascape.model.event.DefaultScapeListener;

/**
 * A base class for most non-gui observer of scapes. Provides notification of
 * scape updates.
 * 
 * @author Miles Parker
 * @version 2.9
 * @history 2.9 3/30/02 added support for delegate
 * @history 1.2.6 10/25/99 added support for named listeners
 * @history 1.1 2/19/1999 first in
 * @since 1.1
 */
public class NonGraphicView extends DefaultScapeListener {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a non-graphic view.
     */
    public NonGraphicView() {
        this("Non-graphic view");
    }

    /**
     * Constructs a non-grahpic view.
     * 
     * @param name
     *            a user relevant name for this view
     */
    public NonGraphicView(String name) {
        this.name = name;
        setNotifyScapeAutomatically(true);
    }

    /**
     * Returns false, by definition non graphic views have no gui.
     * 
     * @return true, if is graphic
     */
    public final boolean isGraphic() {
        return false;
    }
}
