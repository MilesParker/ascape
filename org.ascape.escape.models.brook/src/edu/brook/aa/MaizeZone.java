/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.aa;

import org.ascape.model.Scape;
import org.ascape.model.space.Singleton;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.swt.graphics.Color;

public class MaizeZone extends Scape implements IColorProvider {

	/**
     * 
     */
	private static final long serialVersionUID = 7378302753497194685L;
	private Color color;

	public MaizeZone(String name, Color color) {
		setSpace(new Singleton());
		this.name = name;
		this.color = color;
	}

	public Color getBackground(Object object) {
		return color;
	}

	public Color getForeground(Object object) {
		return getBackground(object);
	}
}
