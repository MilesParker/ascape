/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.sugarscape;

import java.awt.Color;

import org.ascape.model.Agent;
import org.ascape.model.Cell;
import org.ascape.model.CellOccupant;
import org.ascape.model.HostCell;
import org.ascape.model.rule.Rule;
import org.ascape.model.space.Discrete;
import org.ascape.util.data.DataPoint;
import org.ascape.util.data.DataPointConcrete;
import org.ascape.util.data.StatCollector;
import org.ascape.util.data.StatCollectorCSAMM;

/**
 * A basic sugarscape citizen. Provides basic funtionality for sugarscape agents as well as all desired functionality
 * that could be included in base class without compromising good design or supporting unnecessary member variables.
 * Despite the relativly large size of this class it is actually quite simple; much of the code is simply getters and
 * setters for various initialization paramaters.
 * 
 * @author Miles T. Parker
 * @version 1.0
 */
public class SugarAgent extends CellOccupant {

    /**
     * 
     */
    private static final long serialVersionUID = 7249004118591072497L;

    public final static Rule DEATH_STARVATION_RULE = new Rule("Death Starvation") {
        /**
         * 
         */
        private static final long serialVersionUID = -74109594775448280L;

        public void execute(Agent agent) {
            if ((((SugarAgent) agent).getSugar().getStock() <= 0)) {
                agent.die();
            }
        }
    };

    public final static Rule DEATH_STARVATION_OLD_AGE_RULE = new Rule("Death Starvation or Old Age") {
        /**
         * 
         */
        private static final long serialVersionUID = -6064131481294308735L;

        public void execute(Agent agent) {
            if ((((SugarAgent) agent).getSugar().getStock() <= 0)
                    || (((SugarAgent) agent).getAge() >= ((SugarAgent) agent).getDeathAge())) {
                agent.die();
            }
        }
    };

    public final static Rule DEATH_REPLACEMENT_RULE = new Rule("Death Replacement") {
        /**
         * 
         */
        private static final long serialVersionUID = -3794366718314163108L;

        public void execute(Agent agent) {
            if ((((SugarAgent) agent).getSugarStock() <= 0)
                    || (((SugarAgent) agent).getAge() >= ((SugarAgent) agent).getDeathAge())) {
                agent.die();
                agent.getScape().newAgent();
            }
        }
    };

    public final static Rule HARVEST_RULE = new Rule("Harvest") {
        /**
         * 
         */
        private static final long serialVersionUID = 6733179840843438968L;

        public void execute(Agent agent) {
            ((SugarAgent) agent).harvest();
        }
    };

    // Agent attributes
    protected int age;
    private int vision;
    protected int sugarMetabolism;
    private int deathAge;

    private boolean initialColorRandom;
    private Color color;

    public CommodityStock sugar;

    /**
     * Begining population values. Vision is random draw, coordinate placement is random in scape.
     */
    public void initialize() {
        super.initialize();
        sugar = new CommodityStock();
        sugar.setName("sugar");
        sugar.setOwner(this);
        Cell cell = (Cell) ((Discrete) getHostScape().getSpace()).findRandomUnoccupiedCell();
        if (cell != null) {
            moveTo((HostCell) cell);
        }
        setDeathAge(randomInRange(((GAS_Base) getRoot()).getMinDeathAge(), ((GAS_Base) getRoot()).getMaxDeathAge()));
        setAge(randomInRange(0, getDeathAge()));
        setVision(randomInRange(((GAS_Base) getRoot()).getMinVision(), ((GAS_Base) getRoot()).getMaxVision()));
        setSugarMetabolism(randomInRange(((GAS_Base) getRoot()).getMinSugarMetabolism(), ((GAS_Base) getRoot())
                .getMaxSugarMetabolism()));
        setSugar(randomInRange(((GAS_Base) getRoot()).getMinInitialSugar(), ((GAS_Base) getRoot()).getMaxInitialSugar()));
        if (!initialColorRandom) {
            if (getColor() == null) {
                setColor(Color.red);
            }
        } else { // Random initial color requested
            color = new Color((float) Math.random(), (float) Math.random(), (float) Math.random());
        }
    }

    /**
     * Directs agents to assign themselves a random color at initialization.
     */
    public void setInitialColorRandom(boolean initialColorRandom) {
        this.initialColorRandom = initialColorRandom;
    }

    public void harvest() {
        putSugar(((SugarCell) getHostCell()).takeSugar());
    }

    final static DataPoint SUGAR = new DataPointConcrete("Sugar Value") {
        /**
         * 
         */
        private static final long serialVersionUID = 5198939801775563573L;

        public double getValue(Object o) {
            return ((SugarCell) o).getPerceivedValue();
        }
    };

    public void movement() {
        // Note: this was changed from getCellsNear(..)
        // If the model breaks and we , this is probably the problem.
        // jm, 9/18/02
        HostCell bestCell = (HostCell) getHostCell().findMaximumWithin(SUGAR, true, (double) getVision());
        if ((bestCell != null) && bestCell.isAvailable()) {
            moveTo(bestCell);
        }
    }

    public void metabolism() {
        sugar.reduceStock(sugarMetabolism);
        age++;
    }

    public void putSugar(float sucrose) {
        sugar.putStock(sucrose);
    }

    public float getSugarStock() {
        return sugar.getStock();
    }

    public CommodityStock getSugar() {
        return sugar;
    }

    public void setSugar(float amount) {
        sugar.setStock(amount);
    }

    public float takeSugar() {
        return sugar.takeStock();
    }

    public float takeSugar(float amount) {
        return sugar.takeStock(amount);
    }

    public void addSugar(float amount) {
        this.sugar.addStock(amount);
    }

    public int getVision() {
        return vision;
    }

    public void setVision(int vision) {
        this.vision = vision;
    }

    public int getSugarMetabolism() {
        return sugarMetabolism;
    }

    public void setSugarMetabolism(int metabolism) {
        this.sugarMetabolism = metabolism;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getDeathAge() {
        return deathAge;
    }

    public void setDeathAge(int deathAge) {
        this.deathAge = deathAge;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
