/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */

package edu.brook.sugarscape;

import org.ascape.view.vis.GEFView;
import org.eclipse.amp.agf3d.IShape3DProvider;
import org.eclipse.amp.agf3d.Shapes3D;
import org.eclipse.amp.escape.ascape.agf3d.GEF3DView;

public class GAS_II_3_3D extends GAS_II_3 {

    private static final long serialVersionUID = 1L;

    @Override
    public void createGraphicViews() {
        addView(new GEF3DView());
        addView(new GEFView());
    }

    public Object getAdapter(Class adapter) {
        if (adapter == IShape3DProvider.class) {
            return Shapes3D.HUMAN_SHAPE_PROVIDER;
        }
        return null;
    }

}
