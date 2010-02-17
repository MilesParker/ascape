/**
 * <copyright>
 *
 * Copyright (c) 2009 Metascape, LLC.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Metascape - Initial API and Implementation
 *
 * </copyright>
 *
 */

package edu.brooksantafe.heatbugs;

import org.ascape.util.vis.ColorFeature;
import org.eclipse.amp.agf3d.I3DModelFactoryProvider;
import org.eclipse.amp.escape.ascape.adapt.Ascape3DPartFactory;
import org.eclipse.amp.escape.ascape.agf3d.GEF3DView;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.swt.graphics.Color;

/**
 * 
 * @author mparker
 *
 */
public class HeatbugModel3D extends HeatbugModel implements I3DModelFactoryProvider {

    private static final long serialVersionUID = 1L;

    /**
     * 
     * @see edu.brooksantafe.heatbugs.HeatbugModel#createGraphicViews()
     */
    public void createGraphicViews() {
        addView(new GEF3DView());
    }

    IColorProvider cellColorProvider = new IColorProvider() {
        public Color getForeground(Object object) {
            System.err.println(object);
            return ColorFeature.RED;// getCellColorFeature().getColor(object);
        }

        public Color getBackground(Object object) {
            return ColorFeature.RED;// getCellColorFeature().getColor(object);
        }
    };

    IColorProvider agentColorProvider = new IColorProvider() {
        public Color getForeground(Object object) {
            return ColorFeature.BLUE;
        }

        public Color getBackground(Object object) {
            return ColorFeature.GREEN;
        }
    };

    public EditPartFactory getEditPart3DFactory() {
        return new Ascape3DPartFactory() {
            public IColorProvider getColorProvider(Object object) {
                if (object instanceof HeatCell) {
                    return super.getColorProvider(object);
                    // return cellColorProvider;
                } else if (object instanceof Heatbug) {
                    return agentColorProvider;
                }
                return super.getColorProvider(object);
            }
        };
    }
}
