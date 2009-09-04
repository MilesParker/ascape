/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */

package edu.brook.pd;

import org.ascape.view.vis.GEFView;

public class PD2DGEF extends PD2D {
    public void createGraphicViews() {
        GEFView overheadView = new GEFView();
        lattice.addView(overheadView);
    }
}
