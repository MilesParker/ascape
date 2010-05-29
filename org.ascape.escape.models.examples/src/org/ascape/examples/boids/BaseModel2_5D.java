/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */

package org.ascape.examples.boids;

import org.eclipse.amp.escape.ascape.agf3d.GEF3DView;

public class BaseModel2_5D extends BaseModel {
    public void createGraphicViews() {
        GEF3DView view = new GEF3DView();
        addView(view);
    }

}
