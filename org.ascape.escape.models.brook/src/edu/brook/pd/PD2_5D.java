package edu.brook.pd;

import org.eclipse.amp.escape.ascape.agf3d.GEF3DView;

public class PD2_5D extends PD2D {

    public void createGraphicViews() {
        addView(new GEF3DView());
    }

}
