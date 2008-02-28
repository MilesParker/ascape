/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */



package org.ascape.examples.traffic;

import java.awt.Color;

import org.ascape.model.Agent;
import org.ascape.model.Scape;
import org.ascape.model.rule.Rule;
import org.ascape.model.space.Array2DMoore;
import org.ascape.util.data.StatCollectorCSAMM;
import org.ascape.view.vis.ChartView;
import org.ascape.view.vis.Overhead2DView;

public class TrafficModel extends Scape {

    /**
     * 
     */
    private static final long serialVersionUID = -2074626017813799729L;

    private Scape highway;

    private Scape cars;

    private double maxSpeed = 90;

    private double minSpeed = 60;

    private double minSafeDistance = 1.0;

    private double maxSafeDistance = 3.0;

    private double minAcceleration = 1.5;

    private double maxAcceleration = 3.0;

    private double minDeceleration = 2.0;

    private double maxDeceleration = 4.0;

    private double portionCooperator;

    private double trafficDensity = .50;

    public void createScape() {
        super.createScape();
        setName("Traffic Model");
        highway = new Scape(new Array2DMoore());
        highway.setExtent(80, 6);
        RoadTile roadTile = new RoadTile();
        highway.setPrototypeAgent(roadTile);
        highway.setName("Highway");
        add(highway);
        cars = new Scape();
        Car protoCar = new Car();
        protoCar.setHostScape(highway);
        cars.setPrototypeAgent(protoCar);
        cars.addInitialRule(MOVE_RANDOM_LOCATION_RULE);
        cars.addRule(MOVEMENT_RULE);
        cars.addRule(new Rule("Lane Changing") {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            public void execute(Agent a) {
                ((Car) a).changeLanes();
            }
        });
        cars.setName("Cars");
        add(cars);
        cars.addStatCollector(new StatCollectorCSAMM("Speed") {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            public double getValue(Object a) {
                return ((Car) a).getActualSpeed();
            }
        });
//        cars.addStatCollector(new StatCollectorCondCSAMM("Speed Left Lane") {
//            public boolean meetsCondition(Object a) {
//                return (((Coordinate2DDiscrete) ((Car) a).getCoordinate()).getYValue() == 0);
//            }
//
//            public double getValue(Object a) {
//                return ((Car) a).getActualSpeed();
//            }
//        });
//        cars.addStatCollector(new StatCollectorCondCSAMM("Speed Middle Lanes") {
//            public boolean meetsCondition(Object a) {
//                return (((Coordinate2DDiscrete) ((Car) a).getCoordinate()).getYValue() == 1);
//            }
//
//            public double getValue(Object a) {
//                return ((Car) a).getActualSpeed();
//            }
//        });
//        cars.addStatCollector(new StatCollectorCondCSAMM("Speed Right Lane") {
//            public boolean meetsCondition(Object a) {
//                return (((Coordinate2DDiscrete) ((Car) a).getCoordinate()).getYValue() == 2);
//            }
//
//            public double getValue(Object a) {
//                return ((Car) a).getActualSpeed();
//            }
//        });
        cars.addStatCollector(new StatCollectorCSAMM("Frustration") {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            public double getValue(Object a) {
                return ((Car) a).getDesiredSpeed() - ((Car) a).getActualSpeed();
            }
        });
    }

    public void initialize() {
        super.initialize();
        cars.setExtent((int) (highway.getSize() * trafficDensity));
    }

    public void createGraphicViews() {
        super.createGraphicViews();

        Overhead2DView helicopterView = new Overhead2DView("Helicopter View");
        highway.addView(helicopterView);
        helicopterView.setDrawByFeature(true);
        highway.addDrawFeature(RoadTile.DRAW_LANE_MARKINGS);
        highway.addDrawFeature(Car.DRAW_SIMPLE_CAR);
        helicopterView.getDrawSelection().clearSelection();
        helicopterView.getDrawSelection().setSelected(Car.DRAW_SIMPLE_CAR, true);
        helicopterView.getDrawSelection().setSelected(RoadTile.DRAW_LANE_MARKINGS, true);
        helicopterView.getDrawSelection().setSelected(helicopterView.cells_fill_draw_feature, true);
        helicopterView.getDrawSelection().moveToFront(helicopterView.cells_fill_draw_feature);

        ChartView chart = new ChartView("Frustration");
        addView(chart);
        chart.addSeries("Average Speed", Color.green);
        chart.addSeries("Average Frustration", Color.red);

//        ChartView chart2 = new ChartView("Lane Speed");
//        addView(chart2);
//        chart2.addSeries("Average Speed Left Lane", Color.green);
//        chart2.addSeries("Average Speed Middle Lanes", Color.yellow);
//        chart2.addSeries("Average Speed Right Lane", Color.red);

//        ChartView chart3 = new ChartView("Lane Density");
//        addView(chart3);
//        chart3.addSeries("Count Speed Left Lane", Color.green);
//        chart3.addSeries("Count Speed Middle Lanes", Color.yellow);
//        chart3.addSeries("Count Speed Right Lane", Color.red);
    }


    public Scape getCars() {
        return cars;
    }

    public Scape getHighway() {
        return highway;
    }

    public double getTrafficDensity() {
        return trafficDensity;
    }

    public void setTrafficDensity(double trafficDensity) {
        this.trafficDensity = trafficDensity;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public double getMinSpeed() {
        return minSpeed;
    }

    public void setMinSpeed(double minSpeed) {
        this.minSpeed = minSpeed;
    }

    public double getPortionCooperator() {
        return portionCooperator;
    }

    public void setPortionCooperator(double portionCooperator) {
        this.portionCooperator = portionCooperator;
    }

    public double getMaxSafeDistance() {
        return maxSafeDistance;
    }

    public void setMaxSafeDistance(double maxSafeDistance) {
        this.maxSafeDistance = maxSafeDistance;
    }

    public double getMinSafeDistance() {
        return minSafeDistance;
    }

    public void setMinSafeDistance(double minSafeDistance) {
        this.minSafeDistance = minSafeDistance;
    }

    public double getMaxAcceleration() {
        return maxAcceleration;
    }

    public void setMaxAcceleration(double maxAcceleration) {
        this.maxAcceleration = maxAcceleration;
    }

    public double getMaxDeceleration() {
        return maxDeceleration;
    }

    public void setMaxDeceleration(double maxDeceleration) {
        this.maxDeceleration = maxDeceleration;
    }

    public double getMinAcceleration() {
        return minAcceleration;
    }

    public void setMinAcceleration(double minAcceleration) {
        this.minAcceleration = minAcceleration;
    }

    public double getMinDeceleration() {
        return minDeceleration;
    }

    public void setMinDeceleration(double minDeceleration) {
        this.minDeceleration = minDeceleration;
    }

}


