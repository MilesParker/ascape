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
 * Miles Parker
 * mparker@brook.edu
 * http://www.brook.edu/es/dynamics/models/ascape/
 * The Brookings Institution
 * Washington, D.C.
 */

package edu.brook.aa;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;

import org.ascape.model.Agent;
import org.ascape.model.Scape;
import org.ascape.model.rule.Rule;
import org.ascape.model.space.Singleton;

class Locations extends ArrayList {

    private boolean needsSort = true;

    public boolean add(Object o) {
        needsSort = true;
        return super.add(o);
    }

    /*public boolean add(Object o) {
        CSIterator i = listIterator(size());
        while (i.hasPrevious()) {
            if (((Comparable) o).compareTo((Comparable) i.previous()) >= 0) {
                if (i.hasNext()) {
                    i.next();
                }
                i.add(o);
                return true;
            }
        }
        i.add(o);
        /*CSIterator j = listIterator();
        while (j.hasNext()) {
            System.out.print(((Location) j.next()).getQuality() + " ");
        }
        System.out.println();*/
    //return true;
    //}*/

    public boolean remove(Object o) {
        needsSort = true;
        return super.remove(o);
    }

    /*public Object removeFirst() {
        checkSort();
	    return super.removeFirst();
    }

    public Object getFirst() {
        checkSort();
	    return super.getFirst();
    }

    public Object removeLast() {
        checkSort();
	    return super.removeLast();
    }

    public Object getLast()  {
        checkSort();
	    return super.getLast();
    }*/

    public final Object remove(int index) {
        checkSort();
        return super.remove(index);
    }

    public final Object get(int index) {
        checkSort();
        return super.get(index);
    }

    public void checkSort() {
        if (needsSort) {
            needsSort = false;
            Collections.sort(this);
        }
    }
}

public class YieldZone extends Scape {

    private EnvironmentZone environmentZone;

    private MaizeZone maizeZone;

    private Locations locations;

    private int yield;

    protected Color color;

    public YieldZone(String name, Color color, EnvironmentZone environmentZone, MaizeZone maizeZone) {
        super(new Singleton());
        this.environmentZone = environmentZone;
        this.maizeZone = maizeZone;
        setName(name);
        this.color = color;
        locations = new Locations();
    }

    public void initialize() {
        locations.clear();
    }

    public int getYield() {
        return yield;
    }

    public MaizeZone getMaizeZone() {
        return maizeZone;
    }

    public void calculateYield() {
        double apdsi = environmentZone.getAPDSI();
        if (maizeZone == LHV.MAIZE_NO_YIELD) {
            yield = 0;
        } else if (maizeZone == LHV.MAIZE_YIELD_1) {
            if (apdsi >= 3.0) {
                yield = 1153;
            } else if (apdsi >= 1.0) {
                yield = 988;
            } else if (apdsi > -1.0) {
                yield = 821;
            } else if (apdsi > -3.0) {
                yield = 719;
            } else {
                yield = 617;
            }
        } else if (maizeZone == LHV.MAIZE_YIELD_2) {
            if (apdsi >= 3.0) {
                yield = 961;
            } else if (apdsi >= 1.0) {
                yield = 824;
            } else if (apdsi > -1.0) {
                yield = 684;
            } else if (apdsi > -3.0) {
                yield = 599;
            } else {
                yield = 514;
            }
        } else if (maizeZone == LHV.MAIZE_YIELD_3) {
            if (apdsi >= 3.0) {
                yield = 769;
            } else if (apdsi >= 1.0) {
                yield = 659;
            } else if (apdsi > -1.0) {
                yield = 547;
            } else if (apdsi > -3.0) {
                yield = 479;
            } else {
                yield = 411;
            }
        } else if (maizeZone == LHV.MAIZE_SAND_DUNE) {
            if (apdsi >= 3.0) {
                yield = 1201;
            } else if (apdsi >= 1.0) {
                yield = 1030;
            } else if (apdsi > -1.0) {
                yield = 855;
            } else if (apdsi > -3.0) {
                yield = 749;
            } else {
                yield = 642;
            }
        } else if ((maizeZone == LHV.MAIZE_NO_YIELD) || (maizeZone == LHV.MAIZE_EMPTY)) {
            yield = 0;
        } else {
            throw new RuntimeException("Bad data or logic in Location#getYield");
        }
        //yield = (int) (yield * getFrostYieldFactor());
    }

    public void scapeCreated() {
        scape.getRules().clear();
        Rule calculateYield = new Rule("Calculate Yield") {
            public void execute(Agent agent) {
                ((YieldZone) agent).calculateYield();
            }
            //public boolean isRandomExecution() {
            //return false;
            //}
        };
        scape.addInitialRule(calculateYield);
        scape.addRule(calculateYield);
    }

    public Color getColor() {
        return color;
    }

    public Locations getAvailableLocations() {
        return locations;
    }
}
