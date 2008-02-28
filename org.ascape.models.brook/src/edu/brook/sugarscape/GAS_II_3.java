/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.sugarscape;

import java.awt.Color;

import org.ascape.util.data.StatCollector;
import org.ascape.view.vis.ChartView;



public class GAS_II_3 extends GAS_II_2 {

    /**
     * 
     */
    private static final long serialVersionUID = 1854992312550139759L;

    public void createViews() {
        super.createViews();
        agents.addStatCollector(new StatCollector("All"));
        ChartView population = new ChartView();
        agents.addView(population);
        population.addSeries("Count All", Color.red);
    }
}
