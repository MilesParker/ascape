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

package edu.brook.misc;

import java.awt.Color;

import org.ascape.explorer.ViewFrameBridge;
import org.ascape.model.Agent;
import org.ascape.model.Cell;
import org.ascape.model.Scape;
import org.ascape.model.rule.Rule;
import org.ascape.model.space.Array2DSmallWorld;
import org.ascape.view.vis.Overhead2DView;

class TestSmallLattice extends Scape {

    static int allUpdate;
    static Color nextRandomColor;

    class GenCell extends Cell {

        int lastUpdate;
        Color color;

        public void initialize() {
            lastUpdate = 0;
            color = Color.black;
        }

        public void update() {
            lastUpdate = getScape().getPeriod();
            color = nextRandomColor;
            if (allUpdate != lastUpdate) {
                allUpdate = lastUpdate;
                nextRandomColor = new Color((float) Math.random(), (float) Math.random(), (float) Math.random());
            }
        }

        public Color getColor() {
            return color;
        }
    }

    public void createScape() {
        setSpace(new Array2DSmallWorld());
        setPrototypeAgent(new GenCell());
        setExtent(25, 25);
        ((Array2DSmallWorld) getSpace()).setRandomEdgeRatio(randomness);
        randomness += 0.1;
        super.createScape();
    }

    static double randomness = 0.0;
    static int index = 0;
    static Overhead2DView[] views = new Overhead2DView[11];

    public void createViews() {
        super.createViews();
        views[index] = new Overhead2DView("Test") {
            public void updateScapeGraphics() {
                super.updateScapeGraphics();
            }
        };
        views[index].setCellSize(3);
        addView(views[index], false);
        index++;
        if (index == 11) {
            ViewFrameBridge frame = new ViewFrameBridge(views);
        }
        //views[index].getViewFrame().setTitle(Utility.formatToString(randomness - .1, 2));
        /*ColorFeatureGradiatedIndex genFeature = new ColorFeatureGradiatedIndex("Generation");
        genFeature.setDataPoint(new UnitIntervalDataPoint() {
            public double getValue(Object object) {
                return (double) Math.min((((GenCell) object).getScape().getPeriod() - ((GenCell) object).lastUpdate), 10) / 10.0;
            }
        });
        genFeature.setMinimumColor(Color.black);
        genFeature.setMaximumColor(Color.green);
        ov.setCellColorFeature(genFeature);*/
    }
}

public class TestSmallWorld extends Scape {

    public void createScape() {
        super.createScape();
        Scape lattices = new Scape();
        lattices.setAutoCreate(false);
        TestSmallLattice sw = new TestSmallLattice();
        ((Array2DSmallWorld) sw.getSpace()).setRadius(2);
        sw.setAutoCreate(false);
        lattices.setPrototypeAgent(sw);
        lattices.setExtent(11);
        lattices.addRule(new Rule("Update One") {
            public void execute(Agent a) {
                ((Scape) a).execute(UPDATE_RULE, ((Cell) ((Scape) a).findRandom()).findNeighbors());
            }
        });
        add(lattices);
    }
}
