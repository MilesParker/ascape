/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */
package org.ascape.view.vis;

import java.util.HashMap;
import java.util.Map;

import org.ascape.model.space.Array2DMoore;
import org.ascape.model.space.Array2DSmallWorld;
import org.ascape.model.space.Array2DVonNeumann;


/**
 * The Class ViewVisUtil.
 */
public class ViewVisUtil {

	/**
     * The view for scape.
     */
	static Map viewForScape = new HashMap();
	
	static {
		ViewVisUtil.registerView(Array2DMoore.class, Overhead2DView.class);
		ViewVisUtil.registerView(Array2DVonNeumann.class, Overhead2DView.class);
		ViewVisUtil.registerView(Array2DSmallWorld.class, Overhead2DView.class);
	}
	
	/**
     * Register view.
     * 
     * @param space
     *            the space
     * @param view
     *            the view
     */
	public static void registerView(Class space, Class view) {
		if (!ComponentView.class.isAssignableFrom(view)) {
			throw new IllegalArgumentException("View must be a ComponentView");
		}
		Object check = viewForScape.get(space);
		if (check != null) {
			throw new IllegalArgumentException("A view has already been registered for " + space);
		}
		viewForScape.put(space, view);
	}
	
	/**
     * Gets the default view.
     * 
     * @param space
     *            the space
     * @return the default view
     */
	public static Class getDefaultView(Class space) {
		return (Class) viewForScape.get(space);
	}

}
