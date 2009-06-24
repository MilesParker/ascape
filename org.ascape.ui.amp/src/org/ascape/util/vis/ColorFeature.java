/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */

package org.ascape.util.vis;

import org.ascape.AscapeRuntimePlugin;
import org.ascape.util.HasName;
import org.eclipse.swt.graphics.Color;


/**
 * A Color feature returns a color based on the state of an object of known type provided to it. Used to provide an
 * appropriate color for some feature or aspect of an object.
 * 
 * @author Miles Parker
 * @version 1.0.1
 * @history 1.0.1 3/10/99 renamed from ColorSource
 * @since 1.0
 */
public interface ColorFeature extends HasName {

    public final static Color BLACK = AscapeRuntimePlugin.getDefault().createColor(org.eclipse.swt.SWT.COLOR_BLACK);

    public final static Color BLUE = AscapeRuntimePlugin.getDefault().createColor(org.eclipse.swt.SWT.COLOR_BLUE);

    public final static Color CYAN = AscapeRuntimePlugin.getDefault().createColor(org.eclipse.swt.SWT.COLOR_CYAN);

    public final static Color DARK_BLUE = AscapeRuntimePlugin.getDefault()
    .createColor(org.eclipse.swt.SWT.COLOR_DARK_BLUE);

    public final static Color DARK_GREEN = AscapeRuntimePlugin.getDefault()
    .createColor(org.eclipse.swt.SWT.COLOR_DARK_GREEN);

    public final static Color DARK_GRAY = AscapeRuntimePlugin.getDefault()
    .createColor(org.eclipse.swt.SWT.COLOR_DARK_GRAY);

    public final static Color DARK_MAGENTA = AscapeRuntimePlugin.getDefault()
    .createColor(org.eclipse.swt.SWT.COLOR_DARK_MAGENTA);

    public final static Color DARK_RED = AscapeRuntimePlugin.getDefault()
    .createColor(org.eclipse.swt.SWT.COLOR_DARK_RED);

    public final static Color DARK_YELLOW = AscapeRuntimePlugin.getDefault()
    .createColor(org.eclipse.swt.SWT.COLOR_DARK_YELLOW);

    public final static Color DEFAULT_BG = AscapeRuntimePlugin.getDefault()
            .createColor(org.eclipse.swt.SWT.COLOR_WIDGET_BACKGROUND);

    public final static Color GRAY = AscapeRuntimePlugin.getDefault().createColor(org.eclipse.swt.SWT.COLOR_GRAY);

    public final static Color LIGHT_GRAY = ColorFeatureConcrete.create(192, 192, 192);

    public final static Color GREEN = AscapeRuntimePlugin.getDefault().createColor(org.eclipse.swt.SWT.COLOR_GREEN);

    public final static Color MAGENTA = AscapeRuntimePlugin.getDefault().createColor(org.eclipse.swt.SWT.COLOR_MAGENTA);

    public final static Color RED = AscapeRuntimePlugin.getDefault().createColor(org.eclipse.swt.SWT.COLOR_RED);

    public final static Color WHITE = AscapeRuntimePlugin.getDefault().createColor(org.eclipse.swt.SWT.COLOR_WHITE);

    public final static Color YELLOW = AscapeRuntimePlugin.getDefault().createColor(org.eclipse.swt.SWT.COLOR_YELLOW);

    public final static Color ORANGE = AscapeRuntimePlugin.getDefault().createColor(255, 200, 0);

    public final static Color HIGHLIGHT_AGENT_COLOR = YELLOW;

    /**
     * Returns a color for the object as defined in implementions of this class.
     * 
     * @param object
     *        the object to get a color from.
     */
    public Color getColor(Object object);
}
