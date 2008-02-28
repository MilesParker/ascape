/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.aa;

import java.awt.Color;

import org.ascape.model.AscapeObject;


public class Clan extends AscapeObject {

    /**
     * 
     */
    private static final long serialVersionUID = 2760292253117658739L;

    public final static Clan ORANGE_CLAN = new Clan("Orange Clan", Color.orange);

    public final static Clan RED_CLAN = new Clan("Red Clan", Color.red);

    public final static Clan GREEN_CLAN = new Clan("Green Clan", Color.green);

    public final static Clan PINK_CLAN = new Clan("Pink Clan", Color.pink);

    public final static Clan YELLOW_CLAN = new Clan("Yellow Clan", Color.yellow);

    public final static Clan WHITE_CLAN = new Clan("White Clan", Color.white);

    public final static Clan CYAN_CLAN = new Clan("Cyan Clan", Color.cyan);

    public final static Clan BLACK_CLAN = new Clan("Black Clan", Color.black);

    public static final Clan[] clans = {ORANGE_CLAN, RED_CLAN, GREEN_CLAN, PINK_CLAN, YELLOW_CLAN, CYAN_CLAN};

    //public static final Clan[] clans = {ORANGE_CLAN, RED_CLAN};//, GREEN_CLAN};//, PINK_CLAN};

    private Color color = new Color((float) Math.random(), (float) Math.random(), (float) Math.random());

    public Clan() {
        super();
    }

    public Clan(String name, Color color) {
        super();
        this.name = name;
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public static Clan randomClan(HouseholdBase household) {
        return clans[household.randomToLimit(clans.length)];
    }

    public String toString() {
        return getName();
    }
}
