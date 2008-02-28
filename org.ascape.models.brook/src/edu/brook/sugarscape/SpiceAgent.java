/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.sugarscape;

import java.util.List;

import org.ascape.model.Agent;
import org.ascape.model.Cell;
import org.ascape.model.rule.Rule;
import org.ascape.util.Utility;
import org.ascape.util.data.StatCollector;
import org.ascape.util.data.StatCollectorCSAMMVar;

/**
 * A basic sugarscape citizen. Provides basic funtionality for sugarscape agents
 * as well as all desired functionality that could be included in base class without
 * compromising good design or supporting unnecessary member variables.
 * Despite the relativly large size of this class it is actually
 * quite simple; much of the code is simply getters and setters for various
 * initialization paramaters.
 *
 * @author Alan Lockard
 * alockard@gmu.edu
 * @version 1.0
 */
public class SpiceAgent extends SugarAgent {

    /**
     * 
     */
    private static final long serialVersionUID = -3547597461497394296L;

    public final static Rule DEATH_STARVATION_SPICE_RULE = new Rule("Death From Starvation") {
        /**
         * 
         */
        private static final long serialVersionUID = 5946321502720910867L;

        public void execute(Agent agent) {
            //System.out.println("sugar = " + ((SpiceAgent) agent).getSugar().getStock() + " spice = " + ((SpiceAgent) agent).getSpice().getStock());
            if ((((SpiceAgent) agent).getSugar().getStock() <= 0)
                || (((SpiceAgent) agent).getSpice().getStock() <= 0)) {
                agent.die();
            }
        }
    };

    public final static Rule DEATH_STARVATION_OLD_AGE_SPICE_RULE = new Rule("Death From Starvation and/or Old Age") {
        /**
         * 
         */
        private static final long serialVersionUID = 5212614029869784940L;

        public void execute(Agent agent) {
            //if (((SpiceAgent) agent).getSugar().getStock() <= 0) System.out.println ("Died for Lack of SUGAR");
            //if (((SpiceAgent) agent).getSpice().getStock() <= 0) System.out.println ("Died for Lack of Spice");
            //if (((SugarAgent) agent).getAge() >= ((SugarAgent) agent).getDeathAge()) System.out.println ("Died of old aGe");
            if ((((SpiceAgent) agent).getSugar().getStock() <= 0)
                || (((SpiceAgent) agent).getSpice().getStock() <= 0)
                || (((SugarAgent) agent).getAge() >= ((SugarAgent) agent).getDeathAge())) {
                agent.die();
            }
        }
    };

    //Agent attributes
    private int spiceMetabolism;
    public CommodityStock spice;
    private float aBigNumber = 100000f; // not quite + infinity
    /**
     * Factor by which one would like to inflate (deflate) price
     * when buying spice (sugar)
     */
    private float negotiatingFactor = .25f;
    protected float firstOfferFactor = 2.0f;
    public int maxNumOffers = 6;

    public void initialize() {
        super.initialize();
        spice = new CommodityStock();
        spice.setName("spice");
        spice.setOwner(this);
        setSpiceMetabolism(randomInRange(((GAS_SpiceBase) getRoot()).getMinSpiceMetabolism(), ((GAS_SpiceBase) getRoot()).getMaxSpiceMetabolism()));
        setSpice(randomInRange(((GAS_SpiceBase) getRoot()).getMinInitialSpice(), ((GAS_SpiceBase) getRoot()).getMaxInitialSpice()));
    }

    public void scapeCreated() {
        //scape.addRule(new DefaultUpdate());
        StatCollector[] stats = {new StatCollectorCSAMMVar() {
            /**
             * 
             */
            private static final long serialVersionUID = 5508178474880538451L;

            public double getValue(Object object) {
                return ((SpiceAgent) object).getSugar().getStock();
            }

            public String getName() {
                return "Sugar";
            }
        },
                                 new StatCollectorCSAMMVar() {
                                     /**
                                     * 
                                     */
                                    private static final long serialVersionUID = -9013191235719926425L;

                                    public double getValue(Object object) {
                                         return ((SpiceAgent) object).getSpice().getStock();
                                     }

                                     public String getName() {
                                         return "Spice";
                                     }
                                 },
                                 new StatCollectorCSAMMVar() {
                                     /**
                                     * 
                                     */
                                    private static final long serialVersionUID = 6503639767992244491L;

                                    public double getValue(Object object) {
                                         return ((SpiceAgent) object).calculateMRS();
                                     }

                                     public String getName() {
                                         return "MRS";
                                     }
                                 },
/*
        new StatCollectorCSAMMVar() {
            public double getValue(Object object) {
                return ((SpiceAgent) object).getAge();
//                return -((SugarAgent) object).getAge();
            }
            public String getName() {
                return "Age";
            }
        },
        new StatCollectorCSAMMVar() {
            public double getValue(Object object) {
                return ((SpiceAgent) object).getVision();
            }
            public String getName() {
                return "Vision";
            }
        },
*/
                                 new StatCollectorCSAMMVar() {
                                     /**
                                     * 
                                     */
                                    private static final long serialVersionUID = -5996122912404354652L;

                                    public double getValue(Object object) {
                                         return ((SpiceAgent) object).getSugarMetabolism();
                                     }

                                     public String getName() {
                                         return "Sugar Metabolism";
                                     }
                                 },
                                 new StatCollectorCSAMMVar() {
                                     /**
                                     * 
                                     */
                                    private static final long serialVersionUID = 2927789983195469842L;

                                    public double getValue(Object object) {
                                         return ((SpiceAgent) object).getSpiceMetabolism();
                                     }

                                     public String getName() {
                                         return "Spice Metabolism";
                                     }
                                 },

                                 new StatCollectorCSAMMVar() {
                                     /**
                                     * 
                                     */
                                    private static final long serialVersionUID = 3151035238739702246L;

                                    // returning double here no longer required in most recent code
                                     // Because isAutoCollect() returns false, this value of 0.0
                                     // is never actually collected
                                     public double getValue(Object object) {
                                         return 0.0;
                                     }

                                     public String getName() {
                                         return "Trades";
                                     }

                                     public boolean isAutoCollect() {
                                         return false;
                                     }
                                 },
                                 new StatCollectorCSAMMVar() {
                                     /**
                                     * 
                                     */
                                    private static final long serialVersionUID = 2399755976323230608L;

                                    // returning double here no longer required in most recent code
                                     // Because isAutoCollect() returns false, this value of 0.0
                                     // is never actually collected
                                     public double getValue(Object object) {
                                         return 0.0;
                                     }

                                     public String getName() {
                                         return "Price";
                                     }

                                     public boolean isAutoCollect() {
                                         return false;
                                     }
                                 },
                                 new StatCollectorCSAMMVar() {
                                     /**
                                     * 
                                     */
                                    private static final long serialVersionUID = -8971684188270855031L;

                                    // returning double here no longer required in most recent code
                                     // Because isAutoCollect() returns false, this value of 0.0
                                     // is never actually collected
                                     public double getValue(Object object) {
                                         return 0.0;
                                     }

                                     public String getName() {
                                         return "Log Price";
                                     }

                                     public boolean isAutoCollect() {
                                         return false;
                                     }
                                 },
                                 new StatCollectorCSAMMVar() {
                                     /**
                                     * 
                                     */
                                    private static final long serialVersionUID = -5287570512729696231L;

                                    // returning double here no longer required in most recent code
                                     // Because isAutoCollect() returns false, this value of 0.0
                                     // is never actually collected
                                     public double getValue(Object object) {
                                         return 0.0;
                                     }

                                     public String getName() {
                                         return "Self Gains from Trade";
                                     }

                                     public boolean isAutoCollect() {
                                         return false;
                                     }
                                 },
                                 new StatCollectorCSAMMVar() {
                                     /**
                                     * 
                                     */
                                    private static final long serialVersionUID = -4562137762033496503L;

                                    // returning double here no longer required in most recent code
                                     // Because isAutoCollect() returns false, this value of 0.0
                                     // is never actually collected
                                     public double getValue(Object object) {
                                         return 0.0;
                                     }

                                     public String getName() {
                                         return "Partner Gains from Trade";
                                     }

                                     public boolean isAutoCollect() {
                                         return false;
                                     }
                                 }
        };
        scape.addStatCollectors(stats);
    }

    public CommodityStock getSpice() {
        return spice;
    }

    public void harvest() {
        putSugar(((SpiceCell) getHostCell()).takeSugar());
        putSpice(((SpiceCell) getHostCell()).takeSpice());
    }

    public void metabolism() {
        super.metabolism();
        spice.reduceStock(spiceMetabolism);
        age++;
    }

    public void movement() {
        float sugarStock = getSugar().getStock();
        float spiceStock = getSpice().getStock();
        int sugarMet = getSugarMetabolism();
        int spiceMet = getSpiceMetabolism();
        // Note: this was changed from getCellsNear(..)
        // If the model breaks and we don't catch it, this is probably the problem.
        // jm, 9/18/02
        final List within = getHostCell().findWithin(getVision(), true);
        Cell[] visibleCells = (Cell[]) within.toArray(new Cell[within.size()]);
        float bestValue = ((SpiceCell) visibleCells[0]).getPotentialValue(sugarStock, spiceStock, sugarMet, spiceMet);
        for (int i = 1; i < visibleCells.length; i++) {
            if (((SpiceCell) visibleCells[i]).getPotentialValue(sugarStock, spiceStock, sugarMet, spiceMet) > bestValue && visibleCells[i].isAvailable()) {
                bestValue = ((SpiceCell) visibleCells[i]).getPotentialValue(sugarStock, spiceStock, sugarMet, spiceMet);
            }
        }
        if (((SpiceCell) visibleCells[0]).getPotentialValue(sugarStock, spiceStock, sugarMet, spiceMet) != bestValue) {
            for (int i = 1; i <= getVision(); i++) {
                int series = randomToLimit(24);
                for (int j = 0; j < 4; j++) {
                    SpiceCell current = (SpiceCell) visibleCells[(i * 4 - Utility.uniqueSeries[4][series][j]) + 1];
                    if ((current.getPotentialValue(sugarStock, spiceStock, sugarMet, spiceMet) == bestValue) && current.isAvailable()) {
                        moveTo(current);
                        return;
                    }
                }
            }
        }
    }

    public void putSpice(float saffron) {
        spice.putStock(saffron);
    }

    public float getSpiceStock() {
        return spice.getStock();
    }

    public void setSpice(float saffron) {
        spice.setStock(saffron);
    }

    public float takeSpice() {
        return spice.takeStock();
    }

    public float takeSpice(float saffron) {
        return spice.takeStock(saffron);
    }

    public void addSpice(float saffron) {
        spice.addStock(saffron);
    }

    public int getSpiceMetabolism() {
        return spiceMetabolism;
    }

    public void setSpiceMetabolism(int metabolism) {
        this.spiceMetabolism = metabolism;
    }

    public void setNegotiatingFactor(float factor) {
        this.negotiatingFactor = factor;
    }

    public float getNegotiatingFactor() {
        return negotiatingFactor;
    }

    public float calculateMRS(float sugarStock, float spiceStock) {
        float mrs;
        if (sugarStock <= 0) { // point of starvation
            mrs = aBigNumber;  // arbitrary, pretty big number (price)
        } else {
            if (spiceStock <= 0) {
                mrs = 1 / aBigNumber;
            } else {
                mrs = (this.sugarMetabolism * spiceStock) /
                    (this.spiceMetabolism * sugarStock);
            }
        }
        if (mrs < 0) System.out.println("mrs = " + mrs + " = (" + this.sugarMetabolism + " * " + spiceStock + ") / (" + this.spiceMetabolism + " * " + sugarStock + ")");
//if (Float.isNaN(mrs)) System.out.println ("mrs = " + mrs + ", sugarmet = " + this.sugarMetabolism + ", spicemet = " + this.spiceMetabolism + ", sugarstock = " + sugarStock + ", spiceStock = " + spiceStock);
        return mrs;
    }

    public float calculateMRS() {
        float mrs;
        if (sugar.getStock() <= 0) { // point of starvation
            mrs = aBigNumber;  // arbitrary, pretty big number (price)
        } else {
            if (spice.getStock() < 0) {
                mrs = 1 / aBigNumber;
            } else {
                mrs = (this.sugarMetabolism * spice.getStock()) /
                    (this.spiceMetabolism * sugar.getStock());
            }
        }
        if (mrs < 0) System.out.println("mrs = " + mrs + " = (" + this.sugarMetabolism + " * " + spice.getStock() + ") / (" + this.spiceMetabolism + " * " + sugar.getStock() + ")");
//if (Float.isNaN(mrs)) System.out.println ("mrs = " + mrs + ", sugarmet = " + this.sugarMetabolism + ", spicemet = " + this.spiceMetabolism + ", sugarstock = " + sugar.getStock() + ", spiceStock = " + spice.getStock());
        return mrs;
    }

    public float calculateUtility(float sugarStock, float spiceStock) {
        int mt = this.sugarMetabolism + this.spiceMetabolism;
        return (float) (Math.pow(sugarStock, (float) this.sugarMetabolism / mt)
            * Math.pow(spiceStock, (float) this.spiceMetabolism / mt));
    }

    public float calculateUtility() {
        int mt = this.sugarMetabolism + this.spiceMetabolism;
        return (float) (Math.pow(sugar.getStock(), (float) this.sugarMetabolism / mt)
            * Math.pow(spice.getStock(), (float) this.spiceMetabolism / mt));
    }

    public float makeOffer(SpiceAgent partner) {
        float offer;
        offer = (float) Math.sqrt(calculateMRS() * partner.calculateMRS());
        if (Float.isNaN(offer)) {
            System.out.println("Offer = " + offer + " = (Math.sqrt(" + calculateMRS() + " * " + partner.calculateMRS() + ")");
        }
        return offer;
    }

    public boolean acceptOffer(Exchange exchange) {
        if ((exchange.tradeIsValid())
            && (exchange.selfGainsFromTrade > 0) // good for me
            && (exchange.partnerGainsFromTrade > 0) // good for you
            && (exchange.selfExAnteMRS != exchange.partnerExAnteMRS) // goods not valued equally
            && !((exchange.selfExAnteMRS > exchange.partnerExAnteMRS) ^ (exchange.selfExPostMRS > exchange.partnerExPostMRS))) { // MRSs don't flip. Otherwise could lead to cycling.
            return true;
        } else {
            return false;
        }
    }
}
