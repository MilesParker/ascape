package org.ascape.examples.bions;

import org.eclipse.amp.agf3d.IGraphics3DAdapted;
import org.eclipse.amp.agf3d.IGraphics3DAdapter;
import org.eclipse.amp.agf3d.IShape3DProvider;
import org.eclipse.amp.agf3d.Shapes3D;
import org.eclipse.amp.escape.ascape.adapt.AscapeDefaultGraphics3DAdapter;
import org.eclipse.amp.escape.ascape.agf3d.GEF3DView;

public class Model_Q3D extends Model_Q_ContinuousSpace implements IGraphics3DAdapted {

    public void createViews() {
        GEF3DView mapView = new GEF3DView();
        addView(mapView);
    }

    public IGraphics3DAdapter getGraphics3DAdapter() {
        return new AscapeDefaultGraphics3DAdapter() {
            public IShape3DProvider[] getShapes(Object object) {
                return new IShape3DProvider[] { Shapes3D.SMALL_DISC_SHAPE_PROVIDER };
            }
        };
    }

}
