/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */

package org.ascape.test.model;

import java.awt.Color;

import org.ascape.model.Cell;
import org.ascape.model.Scape;
import org.ascape.model.space.Array2DSmallWorld;
import org.ascape.view.nonvis.DataOutputView;

// I had to make this a regular class rather than an inner class of SerializationTest,
// because if it were an inner class of SerializationTest it would contain an implicit
// reference to SerializationTest that would get serialized, and upon deserialization this
// would cause a java.io.InvalidClassException because SerializationTest extends junit.framework.TestCase
// and junit.framework.TestCase is missing a no-arg constructor

public class SerializationTestModel extends Scape {

    Scape countries;
    DataOutputView dataOutputView;

    public void createScape() {
        super.createScape();
        setName("Test Model Root Scape");
        setSerializable(true);
        countries = new Scape(new Array2DSmallWorld());
        //countries.setAutoCreate(false);
        ((Array2DSmallWorld) countries.getSpace()).setRadius(2);
        ((Array2DSmallWorld) countries.getSpace()).setRandomEdgeRatio(0.0);
        countries.setName("Countries");
        countries.setExtent(2, 4);
        Cell protoCountry = new Cell() {
            public Color getColor() {
                return Color.green;
            }
        };
        countries.setPrototypeAgent(protoCountry);
        add(countries);
        //countries.createScape();

        // countries.addStatCollector(
        // new org.ascape.util.data.StatCollectorCSAMMVar("Random Value") {
        // public double getValue(Object o) {
        // return randomInRange(0.0, 1.0);
        // }
        // }
        // );
    }

    public void createViews() {
        super.createViews();

        //        org.ascape.view.vis.ChartView chart = new org.ascape.view.vis.ChartView("Random number");
        //        addView(chart);
        //        chart.addSeries("Average Random Value");
        //        chart.addSeries("Standard Deviation Random Value");

        // dataOutputView = new DataOutputView();
    }

    public void createGraphicViews() {
        super.createGraphicViews();
        //        org.ascape.view.vis.Overhead2DView view = new org.ascape.view.vis.Overhead2DView("Countries");
        //        view.setBorderSize(1);
        //        view.setCellSize(12);
        //        countries.addView(view);
        //        //mtp..commneted out temporaily because of anotehr strange issue
        //        //view.displayAgentCustomizer(countries.getCell(0, 0));
        //        // I'm commenting out the next one because for some reason when you close,
        //        // the displayViewCustomizer does not get closed.
        //        //view.displayViewCustomizer();
        //
        //        addView(getCustomizer());
    }
}
