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

import org.eclipse.amp.escape.ascape.agf3d.GEF3DView;

/**
 * 
 * @author mparker
 *
 */
public class HeatbugModel3D extends HeatbugModel {

    private static final long serialVersionUID = 1L;

    /**
     * 
     * @see edu.brooksantafe.heatbugs.HeatbugModel#createGraphicViews()
     */
    public void createGraphicViews() {
        super.createGraphicViews();
        addView(new GEF3DView());
    }

}
