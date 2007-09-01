/*
 * This software and documentation is subject to, and may be only used in accordance with, the terms and conditions of
 * the license agreement described or set forth in the file titled License.txt.  This software and documentation
 * contains information that is confidential and proprietary to NuTech Solutions, Inc.
 *
 *             Copyright (c) 2000-2004
 *             NuTech Solutions, Inc.
 * 
 * Portions of the software are licensed from The Brookings Institution
 *             Copyright (c) 1998-2000
 *            The Brookings Institution
 *           All rights reserved worldwide.
 *
 *
 * U.S. GOVERNMENT END USERS. The software is a "commercial item," as that term is defined in 48 C.F.R. 2.101,
 * consisting of "commercial computer software" and "commercial computer software documentation," as such terms are
 * used in 48 C.F.R. 12.212. Consistent with 48 C.F.R. 12.212 and 48 C.F.R. 227.7202-1 through 227.7202-4, and other
 * relevant sections of the Code of Federal Regulations, as applicable all U.S. Government End Users acquire the
 * software with only those rights set forth in the license agreement. This software includes trade secrets that
 * are exempt from disclosure under the Freedom of Information Act.  
 *
 * 
 * Artificial Anasazi - Long House Valley
 * see _Understanding Anasazi Culture Change Through Agent-Based Modeling_
 * Jeffrey S. Dean, George J. Gumerman, Joshua M. Epstein, Robert Axtell,
 * Alan C. Swedlund, Miles Parker, and Steven McCarroll
 *
 * Java Implementation:
 * Miles Parker
 * mparker@brook.edu
 * Original C++ Implementation:
 * Steven McCarroll
 * The Brookings Institution
 * Washington, D.C.
 */

package edu.brook.aa;

import java.awt.Color;

import org.ascape.model.AscapeObject;


public class Clan extends AscapeObject {

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
