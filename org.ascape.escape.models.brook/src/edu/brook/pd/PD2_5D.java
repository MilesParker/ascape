package edu.brook.pd;

import org.eclipse.amp.agf3d.CubeEdit3DPart;
import org.eclipse.amp.escape.ascape.adapt.Ascape3DPartFactory;
import org.eclipse.amp.escape.ascape.agf3d.GEF3DView;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

public class PD2_5D extends PD2D {

    public void createGraphicViews() {
        GEF3DView overheadView = new GEF3DView() {

            GEF3DView gef3DView = new GEF3DView() {
                public EditPartFactory createFactory() {
                    return new Ascape3DPartFactory() {
                        public EditPart getPartForElement(Object model) {
                            if (model instanceof Player) {
                                return new CubeEdit3DPart() {
                                    protected void updateGraphics() {
                                        getCube().setColor(((Player) getModel()).getPlatformColor(), 255);
                                    }
                                };
                            }
                            return super.getPartForElement(model);
                        }
                    };
                }
            };
        };
        lattice.addView(overheadView);
    }
}
