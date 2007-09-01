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
 *          All rights reserved worldwide.
 *
 * U.S. GOVERNMENT END USERS. The software is a "commercial item," as that term is defined in 48 C.F.R. 2.101,
 * consisting of "commercial computer software" and "commercial computer software documentation," as such terms are
 * used in 48 C.F.R. 12.212. Consistent with 48 C.F.R. 12.212 and 48 C.F.R. 227.7202-1 through 227.7202-4, and other
 * relevant sections of the Code of Federal Regulations, as applicable all U.S. Government End Users acquire the
 * software with only those rights set forth in the license agreement. This software includes trade secrets that
 * are exempt from disclosure under the Freedom of Information Act.
 *
 */


package org.ascape.examples.bionland;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;

import javax.swing.JPanel;

import org.ascape.explorer.UserEnvironment;
import org.ascape.model.Agent;
import org.ascape.model.CellOccupant;
import org.ascape.model.HostCell;
import org.ascape.model.Scape;
import org.ascape.model.event.ScapeEvent;
import org.ascape.model.rule.Diffusable;
import org.ascape.model.rule.ParameterizedDiffusion;
import org.ascape.model.rule.Rule;
import org.ascape.model.space.Array1D;
import org.ascape.util.Conditional;
import org.ascape.util.data.DataPoint;
import org.ascape.util.data.DataPointConcrete;
import org.ascape.util.data.StatCollectorCSAMM;
import org.ascape.util.swing.CustomSliderPanel;
import org.ascape.util.vis.ImageFeatureFixed;
import org.ascape.view.custom.AutoCustomizerSwing;
import org.ascape.view.vis.Scrolling1DView;

public class Model extends Scape {

    private double populationDensity = .28f;

    private double bionEnergyLeakage = 0.02f;

    private double bionEnergyConsumption = 0.023f;

    protected double energyGain = 1.0f;

    protected double fissionEnergy = 2.0f;

    private int minVision = 8;

    private int maxVision = 8;

    public int getMinVision() {
        return minVision;
    }

    public void setMinVision(int vision) {
        this.minVision = vision;
    }

    public int getMaxVision() {
        return maxVision;
    }

    public void setMaxVision(int vision) {
        this.maxVision = vision;
    }

    public double getPopulationDensity() {
        return populationDensity;
    }

    public void setPopulationDensity(double populationDensity) {
        this.populationDensity = populationDensity;
    }

    public double getEnergyGain() {
        return energyGain;
    }

    public void setEnergyGain(double energyGain) {
        this.energyGain = energyGain;
    }

    public double getBionEnergyLeakage() {
        return bionEnergyLeakage;
    }

    public void setBionEnergyLeakage(double bionEnergyLeakage) {
        this.bionEnergyLeakage = bionEnergyLeakage;
    }

    public double getBionEnergyConsumption() {
        return bionEnergyConsumption;
    }

    public void setBionEnergyConsumption(double bionEnergyConsumption) {
        this.bionEnergyConsumption = bionEnergyConsumption;
    }

    public double getFissionEnergy() {
        return fissionEnergy;
    }

    public void setFissionEnergy(double fissionEnergy) {
        this.fissionEnergy = fissionEnergy;
    }

    private class BionTile extends HostCell implements Diffusable {

        protected double redEnergy;
        protected double orangeEnergy;
        protected double blueEnergy;

        public Color getColor() {
            return new Color(Math.min(1.0f, (float) (redEnergy + orangeEnergy)), (float) orangeEnergy * .8f, (float) blueEnergy);
        }

        public double getRedEnergy() {
            return redEnergy;
        }

        public void setRedEnergy(double redEnergy) {
            this.redEnergy = redEnergy;
        }

        public double getOrangeEnergy() {
            return orangeEnergy;
        }

        public void setOrangeEnergy(double orangeEnergy) {
            this.orangeEnergy = orangeEnergy;
        }

        public double getBlueEnergy() {
            return blueEnergy;
        }

        public void setBlueEnergy(double blueEnergy) {
            this.blueEnergy = blueEnergy;
        }

        private double diffusionTemp;

        public double getDiffusionTemp() {
            return diffusionTemp;
        }

        public void setDiffusionTemp(double diffusionTemp) {
            this.diffusionTemp = diffusionTemp;
        }
    }

    private class Bion extends CellOccupant {

        protected int vision;
        protected double storedEnergy;

        public void initialize() {
            vision = randomInRange(minVision, maxVision);
            storedEnergy = 1.5f;
        }

        public int getVision() {
            return vision;
        }

        public void setVision(int vision) {
            this.vision = vision;
        }

        public double getStoredEnergy() {
            return storedEnergy;
        }

        public void setStoredEnergy(double storedEnergy) {
            this.storedEnergy = storedEnergy;
        }

        public void leaveEnergy() {
            storedEnergy -= bionEnergyLeakage;
        }

        public void addEnergy(double energy) {
            storedEnergy += energy;
        }

        public boolean deathCondition() {
            return storedEnergy <= 0.0f;
        }

        public boolean fissionCondition() {
            return storedEnergy >= fissionEnergy;
        }

        public void fission() {
            if (getHostCell().isNeighborAvailable()) {
                Bion child = (Bion) this.clone();
                child.initialize();
                getScape().add(child);
                child.moveTo(getHostCell().findRandomAvailableNeighbor());
                child.storedEnergy = storedEnergy / 2.0f;
                storedEnergy = storedEnergy / 2.0f;
            }
        }
    }

    public final ParameterizedDiffusion DIFFUSE_RED_ENERGY_RULE = new ParameterizedDiffusion("Diffuse Red") {
        public double getDiffusionValue(Agent a) {
            return ((BionTile) a).getRedEnergy();
        }

        public void setDiffusionValue(Agent a, double value) {
            ((BionTile) a).setRedEnergy(value);
        }
    };

    public final ParameterizedDiffusion DIFFUSE_ORANGE_ENERGY_RULE = new ParameterizedDiffusion("Diffuse Orange") {
        public double getDiffusionValue(Agent a) {
            return ((BionTile) a).getOrangeEnergy();
        }

        public void setDiffusionValue(Agent a, double value) {
            ((BionTile) a).setOrangeEnergy(value);
        }
    };

    public final ParameterizedDiffusion DIFFUSE_BLUE_ENERGY_RULE = new ParameterizedDiffusion("Diffuse Blue") {
        public double getDiffusionValue(Agent a) {
            return ((BionTile) a).getBlueEnergy();
        }

        public void setDiffusionValue(Agent a, double value) {
            ((BionTile) a).setBlueEnergy(value);
        }
    };

    public double getDiffusionConstant() {
        return DIFFUSE_RED_ENERGY_RULE.getDiffusionConstant();
    }

    public void setDiffusionConstant(double value) {
        DIFFUSE_RED_ENERGY_RULE.setDiffusionConstant(value);
        DIFFUSE_ORANGE_ENERGY_RULE.setDiffusionConstant(value);
        DIFFUSE_BLUE_ENERGY_RULE.setDiffusionConstant(value);
    }

    public double getEvaporationRate() {
        return DIFFUSE_RED_ENERGY_RULE.getEvaporationRate();
    }

    public void setEvaporationRate(double value) {
        DIFFUSE_RED_ENERGY_RULE.setEvaporationRate(value);
        DIFFUSE_ORANGE_ENERGY_RULE.setEvaporationRate(value);
        DIFFUSE_BLUE_ENERGY_RULE.setEvaporationRate(value);
    }

    public static final Conditional CONTAINS_RED = new Conditional() {
        public boolean meetsCondition(Object o) {
            return (((HostCell) o).getOccupant() instanceof RedBion);
        }
    };

    public static final Conditional CONTAINS_ORANGE = new Conditional() {
        public boolean meetsCondition(Object o) {
            return (((HostCell) o).getOccupant() instanceof OrangeBion);
        }
    };

    public static final Conditional CONTAINS_BLUE = new Conditional() {
        public boolean meetsCondition(Object o) {
            return (((HostCell) o).getOccupant() instanceof BlueBion);
        }
    };

    public static final DataPoint REDNESS = new DataPointConcrete() {
        public double getValue(Object o) {
            return ((BionTile) o).getRedEnergy();
        }
    };

    public static final DataPoint ORANGENESS = new DataPointConcrete() {
        public double getValue(Object o) {
            return ((BionTile) o).getOrangeEnergy();
        }
    };

    public static final DataPoint BLUENESS = new DataPointConcrete() {
        public double getValue(Object o) {
            return ((BionTile) o).getBlueEnergy();
        }
    };

    class MoveTowardCondition extends Rule {

        Conditional conditional;

        MoveTowardCondition(String name, Conditional conditional) {
            super(name);
            this.conditional = conditional;
        }

        public void execute(Agent a) {
            HostCell cell = (HostCell) ((CellOccupant) a).getHostCell()
                .findNearest(conditional, false, ((Bion) a).getVision());
            if (cell != null) {
                ((CellOccupant) a).moveToward(cell);
            }
        }
    }

    class MoveAwayCondition extends Rule {

        Conditional conditional;

        MoveAwayCondition(String name, Conditional conditional) {
            super(name);
            this.conditional = conditional;
        }

        public void execute(Agent a) {
            HostCell cell = (HostCell) ((CellOccupant) a).getHostCell()
                .findNearest(conditional, false, ((Bion) a).getVision());
            if (cell != null) {
                ((CellOccupant) a).moveAway(cell);
            }
        }
    }

    class MoveTowardMaximumRule extends Rule {

        String name;
        DataPoint point;

        public MoveTowardMaximumRule(String name, DataPoint point) {
            super(name);
            this.name = name;
            this.point = point;
        }

        public void execute(Agent a) {
            HostCell target = (HostCell) ((CellOccupant) a).getHostCell().findMaximumWithin(point, true, ((Bion) a).getVision());
            if (target != null) {
                ((Bion) a).moveToward(target);
            }
        }
    }

    class MoveAwayMaximumRule extends MoveTowardMaximumRule {

        public MoveAwayMaximumRule(String name, DataPoint point) {
            super(name, point);
        }

        public void execute(Agent a) {
            HostCell target = (HostCell) ((CellOccupant) a).getHostCell().findMaximumWithin(point, true, ((Bion) a).getVision());
            if (target != null) {
                ((Bion) a).moveAway(target);
            }
        }
    }

    public final Rule MOVE_TOWARD_RED_ENERGY_RULE = new MoveTowardMaximumRule("Move Toward Red Energy", REDNESS);

    public final Rule MOVE_TOWARD_ORANGE_ENERGY_RULE = new MoveTowardMaximumRule("Move Toward Orange Energy", ORANGENESS);

    public final Rule MOVE_TOWARD_BLUE_ENERGY_RULE = new MoveTowardMaximumRule("Move Toward Blue Energy", BLUENESS);

    public final Rule MOVE_AWAY_RED_ENERGY_RULE = new MoveAwayMaximumRule("Move Away Red Energy", REDNESS);

    public final Rule MOVE_AWAY_ORANGE_ENERGY_RULE = new MoveAwayMaximumRule("Move Away Orange Energy", ORANGENESS);

    public final Rule MOVE_AWAY_BLUE_ENERGY_RULE = new MoveAwayMaximumRule("Move Away Blue Energy", BLUENESS);

    public final static Rule LEAVE_ENERGY_RULE = new Rule("Leave Energy Rule") {
        public void execute(Agent a) {
            ((Bion) a).leaveEnergy();
        }
    };

    public final Rule MOVE_TOWARD_RED_RULE = new MoveTowardCondition("Move Toward Red", CONTAINS_RED);
    public final Rule MOVE_TOWARD_ORANGE_RULE = new MoveTowardCondition("Move Toward Orange", CONTAINS_ORANGE);
    public final Rule MOVE_TOWARD_BLUE_RULE = new MoveTowardCondition("Move Toward Blue", CONTAINS_BLUE);

    public final Rule MOVE_AWAY_RED_RULE = new MoveAwayCondition("Move Away Red", CONTAINS_RED);
    public final Rule MOVE_AWAY_ORANGE_RULE = new MoveAwayCondition("Move Away Orange", CONTAINS_ORANGE);
    public final Rule MOVE_AWAY_BLUE_RULE = new MoveAwayCondition("Move Away Blue", CONTAINS_BLUE);

    public final Rule CONSUME_RED_ENERGY_RULE = new Rule("Consume Red Energy") {
        public void execute(Agent a) {
            double energy =
                Math.min(((BionTile) ((Bion) a).getHostCell()).getRedEnergy(), bionEnergyConsumption);
            ((BionTile) ((Bion) a).getHostCell()).setRedEnergy(
                ((BionTile) ((Bion) a).getHostCell()).getRedEnergy() - energy);
            ((Bion) a).addEnergy(energy);
        }
    };

    public final Rule CONSUME_ORANGE_ENERGY_RULE = new Rule("Consume Orange Energy") {
        public void execute(Agent a) {
            double energy =
                Math.min(((BionTile) ((Bion) a).getHostCell()).getOrangeEnergy(), bionEnergyConsumption);
            ((BionTile) ((Bion) a).getHostCell()).setOrangeEnergy(
                ((BionTile) ((Bion) a).getHostCell()).getOrangeEnergy() - energy);
            ((Bion) a).addEnergy(energy);
        }
    };

    public final Rule CONSUME_BLUE_ENERGY_RULE = new Rule("Consume Blue Energy") {
        public void execute(Agent a) {
            double energy =
                Math.min(((BionTile) ((Bion) a).getHostCell()).getBlueEnergy(), bionEnergyConsumption);
            ((BionTile) ((Bion) a).getHostCell()).setBlueEnergy(
                ((BionTile) ((Bion) a).getHostCell()).getBlueEnergy() - energy);
            ((Bion) a).addEnergy(energy);
        }
    };

    public class RedBion extends Bion {

        public Color getColor() {
            return Color.red;
        }

        public Image getImage() {
            return ImageFeatureFixed.redBall;
        }

        public void leaveEnergy() {
            super.leaveEnergy();
            double energy = Math.min(((BionTile) getHostCell()).getRedEnergy()
                + bionEnergyLeakage * energyGain, 1.0f);
            ((BionTile) getHostCell()).setRedEnergy(energy);
        }
    }

    public class OrangeBion extends Bion {

        public Color getColor() {
            return Color.orange;
        }

        public Image getImage() {
            return ImageFeatureFixed.orangeBall;
        }

        public void leaveEnergy() {
            super.leaveEnergy();
            double energy = Math.min(((BionTile) getHostCell()).getOrangeEnergy()
                + bionEnergyLeakage * energyGain, 1.0f);
            ((BionTile) getHostCell()).setOrangeEnergy(energy);
        }
    }

    public class BlueBion extends Bion {

        public Color getColor() {
            return Color.blue;
        }

        public Image getImage() {
            return ImageFeatureFixed.blueBall;
        }

        public void leaveEnergy() {
            super.leaveEnergy();
            double energy = Math.min(((BionTile) getHostCell()).getBlueEnergy()
                + bionEnergyLeakage * energyGain, 1.0f);
            ((BionTile) getHostCell()).setBlueEnergy(energy);
        }
    }

    private Scape territory;

    private Scape redBions;

    private Scape orangeBions;

    private Scape blueBions;

    public void createScape() {
        super.createScape();
        territory = new Scape(new Array1D());
        territory.setName("Map");
        territory.setPrototypeAgent(new BionTile());
        territory.setExtent(190);
        territory.setExecutionOrder(RULE_ORDER);
        territory.addRule(DIFFUSE_RED_ENERGY_RULE);
        territory.addRule(DIFFUSE_ORANGE_ENERGY_RULE);
        territory.addRule(DIFFUSE_BLUE_ENERGY_RULE);
        add(territory);
        redBions = createBions("Red", new RedBion());
        redBions.getRules().setSelected(MOVE_TOWARD_ORANGE_ENERGY_RULE, true);
        redBions.getRules().setSelected(MOVE_AWAY_BLUE_ENERGY_RULE, true);
        redBions.getRules().setSelected(CONSUME_ORANGE_ENERGY_RULE, true);
        //redBions.getRules().setSelected(CONSUME_BLUE_ENERGY_RULE, true);
        orangeBions = createBions("Orange", new OrangeBion());
        orangeBions.getRules().setSelected(MOVE_TOWARD_BLUE_ENERGY_RULE, true);
        orangeBions.getRules().setSelected(MOVE_AWAY_RED_ENERGY_RULE, true);
        //orangeBions.getRules().setSelected(CONSUME_RED_ENERGY_RULE, true);
        orangeBions.getRules().setSelected(CONSUME_BLUE_ENERGY_RULE, true);
        blueBions = createBions("Blue", new BlueBion());
        blueBions.getRules().setSelected(MOVE_TOWARD_RED_ENERGY_RULE, true);
        blueBions.getRules().setSelected(MOVE_AWAY_ORANGE_ENERGY_RULE, true);
        blueBions.getRules().setSelected(CONSUME_RED_ENERGY_RULE, true);
        //blueBions.getRules().setSelected(CONSUME_ORANGE_ENERGY_RULE, true);
    }

    protected Scape createBions(String name, Bion protoBion) {
        Scape bions = new Scape();
        bions.setName(name);
        protoBion.setHostScape(territory);
        bions.setPrototypeAgent(protoBion);
        add(bions);
        bions.addInitialRule(MOVE_RANDOM_LOCATION_RULE);
        bions.addRule(RANDOM_WALK_RULE, false);
        bions.addRule(MOVE_TOWARD_RED_RULE, false);
        bions.addRule(MOVE_TOWARD_ORANGE_RULE, false);
        bions.addRule(MOVE_TOWARD_BLUE_RULE, false);
        bions.addRule(MOVE_AWAY_RED_RULE, false);
        bions.addRule(MOVE_AWAY_ORANGE_RULE, false);
        bions.addRule(MOVE_AWAY_BLUE_RULE, false);
        bions.addRule(MOVE_TOWARD_RED_ENERGY_RULE, false);
        bions.addRule(MOVE_TOWARD_ORANGE_ENERGY_RULE, false);
        bions.addRule(MOVE_TOWARD_BLUE_ENERGY_RULE, false);
        bions.addRule(MOVE_AWAY_RED_ENERGY_RULE, false);
        bions.addRule(MOVE_AWAY_ORANGE_ENERGY_RULE, false);
        bions.addRule(MOVE_AWAY_BLUE_ENERGY_RULE, false);
        bions.addRule(CONSUME_RED_ENERGY_RULE, false);
        bions.addRule(CONSUME_ORANGE_ENERGY_RULE, false);
        bions.addRule(CONSUME_BLUE_ENERGY_RULE, false);
        bions.addRule(DEATH_RULE);
        bions.addRule(FISSIONING_RULE);
        bions.addRule(LEAVE_ENERGY_RULE);
        bions.addStatCollector(new StatCollectorCSAMM(name + " Energy") {
            public double getValue(Object o) {
                return ((Bion) o).getStoredEnergy();
            }
        });
        return bions;
    }

    public void scapeSetup(ScapeEvent scapeEvent) {
        double mapSize = territory.getSize();
        int popSize = (int) ((mapSize * getPopulationDensity()) / 3.0f);
        redBions.setExtent(popSize);
        orangeBions.setExtent(popSize);
        blueBions.setExtent(popSize);
    }

    public void createViews() {
        super.createViews();
        Scrolling1DView mapView = new Scrolling1DView();
        mapView.setName("Map View");
        //mapView.setCellSize(15);
        territory.addView(mapView);
        //mapView.getDrawSelection().clearSelection();
        //mapView.getDrawSelection().setSelected(mapView.agents_image_cells_draw_feature, true);

        /*ChartView chart = new ChartView();
        chart.setName("Population Chart");
        addView(chart);
        chart.addSeries("Count Red Energy", Color.red);
        chart.addSeries("Count Orange Energy", Color.orange);
        chart.addSeries("Count Blue Energy", Color.blue);*/

        AutoCustomizerSwing customizer;
        final JPanel customPanel = new JPanel();
        GridBagConstraints cgbc;

        customizer = new AutoCustomizerSwing() {
            public void build() {
                super.build();
                getTabPane().addTab("Sliders", UserEnvironment.getIcon("FingerUp"), customPanel, "Change Values Using Sliders");
                getTabPane().setSelectedComponent(customPanel);
            }
        };
        setCustomizer(customizer);
        customizer.setPreferredSize(new Dimension(900, 480));
        GridBagLayout cgbl = new GridBagLayout();
        customPanel.setLayout(cgbl);
        cgbc = cgbl.getConstraints(customPanel);
        customPanel.removeAll();
        cgbc.gridwidth = 1;
        cgbc.fill = GridBagConstraints.HORIZONTAL;
        cgbc.insets = new Insets(3, 6, 3, 6);
        cgbc.anchor = GridBagConstraints.NORTH;
        cgbc.weightx = 1.0;
        cgbc.weighty = 1.0;
        cgbc.gridy = 0;
        cgbc.gridx = 0;

        CustomSliderPanel diffusionConstantSlider = new CustomSliderPanel("Diffusion Constant", 0.0, 1.0, 2) {
            public double getActualValue() {
                //usage of specific model version is a bit awkward, but neccessitated by tutorial format
                return Model.this.getDiffusionConstant();
            }

            public void setActualValue(double value) {
                Model.this.setDiffusionConstant(value);
            }
        };
        customPanel.add(diffusionConstantSlider, cgbc);
        diffusionConstantSlider.build();
        cgbc.gridy++;

        CustomSliderPanel evaporationRateSlider = new CustomSliderPanel("Evaporation Rate", 0.0, 0.2, 3) {
            public double getActualValue() {
                return Model.this.getEvaporationRate();
            }

            public void setActualValue(double value) {
                Model.this.setEvaporationRate(value);
            }
        };
        customPanel.add(evaporationRateSlider, cgbc);
        evaporationRateSlider.build();
        cgbc.gridy++;

        CustomSliderPanel energyGainSlider = new CustomSliderPanel("Bion Energy gain", 0.8f, 3.0f, 3) {
            public double getActualValue() {
                return Model.this.getEnergyGain();
            }

            public void setActualValue(double value) {
                Model.this.setEnergyGain(value);
            }
        };
        customPanel.add(energyGainSlider, cgbc);
        energyGainSlider.build();
        cgbc.gridy++;
    }
}


