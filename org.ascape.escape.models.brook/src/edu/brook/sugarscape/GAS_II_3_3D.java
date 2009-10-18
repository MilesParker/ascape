/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */

package edu.brook.sugarscape;

import org.ascape.util.vis.ColorFeatureConcrete;
import org.ascape.view.vis.GEFView;
import org.eclipse.amp.agf3d.I3DModelFactoryProvider;
import org.eclipse.amp.agf3d.ShapeType3D;
import org.eclipse.amp.escape.ascape.adapt.Ascape3DPartFactory;
import org.eclipse.amp.escape.ascape.agf3d.AscapeAgentEdit3DPart;
import org.eclipse.amp.escape.ascape.agf3d.CellOccupantEdit3DPart;
import org.eclipse.amp.escape.ascape.agf3d.GEF3DView;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

public class GAS_II_3_3D extends GAS_II_3 implements I3DModelFactoryProvider {
    private final class SugarCellPart extends AscapeAgentEdit3DPart {
        public SugarCellPart() {
            super(ShapeType3D.TILE, true);
        }

        // @Override
        // protected IFigure buildFigure(IFigure figure) {
        // IFigure buildFigure = super.buildFigure(figure);
        // buildFigure.setBackgroundColor(getColorCellForSugar().getColor(getModel()));
        // return buildFigure;
        // }
    }

    private static final long serialVersionUID = 1L;

    @Override
    public void createGraphicViews() {
        addView(new GEF3DView());
        addView(new GEFView());
    }

    public EditPartFactory getEditPart3DFactory() {
        return new Ascape3DPartFactory() {
            public EditPart getPartForElement(final Object model) {
                if (model instanceof SugarCell) {
                    return new SugarCellPart();
                } else if (model instanceof SugarAgent) {
                    return new CellOccupantEdit3DPart() {
                        @Override
                        protected IFigure buildFigure(IFigure figure) {
                            IFigure buildFigure = super.buildFigure(figure);
                            buildFigure.setBackgroundColor(ColorFeatureConcrete.RED);
                            return buildFigure;
                        }
                    };
                }
                // if (model instanceof NeighborhoodCell) {
                // return new Tile3DEditPart();
                // }
                return super.getPartForElement(model);
            }
        };
    }

}
