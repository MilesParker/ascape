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

package org.ascape.ide;

import org.eclipse.jdt.core.JavaCore;

/**
 * 
 * @author mparker
 *
 */
public class AscapeJavaPluginProjectWizard extends PluginTemplateProjectWizard {

    public String getProjectTypeName() {
        return "Escape Java";
    }

    @Override
    public String[] getNatures() {
        return new String[] { JavaCore.NATURE_ID, "org.eclipse.pde.PluginNature" };
    }

    public String getPluginID() {
        return AscapeIDEPlugin.PLUGIN_ID;
    }

    public String getSourceDirName() {
        return "src";
    }
}
