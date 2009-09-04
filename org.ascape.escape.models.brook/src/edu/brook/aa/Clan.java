/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.aa;

import org.ascape.model.AscapeObject;
import org.ascape.util.vis.ColorFeature;
import org.ascape.util.vis.ColorFeatureConcrete;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.swt.graphics.Color;


public class Clan extends AscapeObject implements IColorProvider {

	/**
     * 
     */
	private static final long serialVersionUID = 2760292253117658739L;

	public final static Clan DARK_RED_CLAN = new Clan("Orange Clan", ColorFeature.DARK_RED);

	public final static Clan RED_CLAN = new Clan("Red Clan", ColorFeature.RED);

	public final static Clan GREEN_CLAN = new Clan("Green Clan", ColorFeature.GREEN);

	public final static Clan DARK_BLUE_CLAN = new Clan("Pink Clan", ColorFeature.DARK_BLUE);

	public final static Clan YELLOW_CLAN = new Clan("Yellow Clan", ColorFeature.YELLOW);

	public final static Clan WHITE_CLAN = new Clan("White Clan", ColorFeature.WHITE);

	public final static Clan CYAN_CLAN = new Clan("Cyan Clan", ColorFeature.CYAN);

	public final static Clan BLACK_CLAN = new Clan("Black Clan", ColorFeature.BLACK);

	public static final Clan[] clans = { DARK_RED_CLAN, RED_CLAN, GREEN_CLAN,
			DARK_BLUE_CLAN, YELLOW_CLAN, CYAN_CLAN };

	private Color color = ColorFeatureConcrete.create(
			(int) (Math.random() * 256), (int) (Math.random() * 256),
			(int) (Math.random() * 256));

	public Clan() {
		super();
	}

	public Clan(String name, Color color) {
		super();
		this.name = name;
		this.color = color;
	}

	public Color getBackground(Object object) {
		return color;
	}

	public Color getForeground(Object object) {
		return color;
	}

	public static Clan randomClan(HouseholdBase household) {
		return clans[household.randomToLimit(clans.length)];
	}

	public String toString() {
		return getName();
	}
}
