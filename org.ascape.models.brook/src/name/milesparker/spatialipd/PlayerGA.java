/*
 * Copyright 2000 Miles T. Parker. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package name.milesparker.spatialipd;

import java.awt.Color;
import java.util.Hashtable;

import org.ascape.model.Agent;
import org.ascape.model.Cell;
import org.ascape.model.HostCell;
import org.ascape.model.rule.Rule;
import org.ascape.util.data.DataPoint;

import edu.brook.pd.PD2D;
import edu.brook.pd.Player;

/**
 * A player in the prisoner's dilemma game.
 *
 * @author Miles Parker
 * @version 1.0
 **/
public class PlayerGA extends Player {

    /**
     * 
     */
    private static final long serialVersionUID = 2542404872757513114L;

    Chromosome strategies;

    //Chromosome identity;

    public Chromosome getStrategies() {
        return strategies;
    }

    protected final int memSize() {
        return ((PD2DGA) getRoot()).getMemorySize();
    }

    public String getChromosomeAsString() {
        String strat = new String();
        for (int i = 0; i < strategies.encoding.length; i++) {
            strat += strategies.encoding[i] == PD2D.COOPERATE ? "C" : "D";
        }
        return strat;
    }

    public String getChromosomeStrategyAsString() {
        String strat = new String();
        for (int i = 0; i < strategySize(); i++) {
            strat += strategies.encoding[i] == PD2D.COOPERATE ? "C" : "D";
        }
        return strat;
    }

    public String getChromosomePreferenceAsString() {
        String strat = new String();
        strat += strategies.encoding[strategySize() + defaultHistorySize()] == 0 ? "+" : "-";
        return strat;
    }

    public String getChromosomeDefaultHistoryAsString() {
        String hist = new String();
        for (int i = strategySize() - 1; i < strategySize() + defaultHistorySize(); i++) {
            hist += strategies.encoding[i] == PD2D.COOPERATE ? "C" : "D";
        }
        return hist;
    }

    protected final int strategySize() {
        return (int) Math.pow(2, (2 * memSize()));
    }

    protected final int defaultHistorySize() {
        return memSize() * 2;
    }

    public void initialize() {
        super.initialize();
        strategies = new Chromosome();
        strategies.setPlayer(this);
        strategies.setSize(strategySize() + defaultHistorySize() + 1);
        strategies.initialize();
        //
        /*identity = new Chromosome();
        identity.setPlayer(this);
        identity.setSize(strategySize() + defaultHistorySize() + 1);
        identity.initialize();*/
    }

    class PartnerPlayRecord {

        int played;
    }

    private PartnerPlayRecord playRecord;

    Hashtable partnerRecords = new Hashtable();

    /*public boolean deathCondition() {
        return (getWealth() < 0);
    }*/

    private int totalCount;

    private int cooperationCount;

    public void fission() {
        if (getHostCell().isNeighborAvailable()) {
            PlayerGA child = (PlayerGA) this.clone();
            getScape().add(child);
            child.initialize();
            child.moveTo(getHostCell().findRandomAvailableNeighbor());
            child.wealth = ((PD2D) scape.getRoot()).getInheiritedWealth();
            child.age = 0;
            child.age = randomInRange(0, ((PD2D) scape.getRoot()).getDeathAge());
            wealth -= ((PD2D) scape.getRoot()).getInheiritedWealth();
            if (getRandom().nextFloat() < ((PD2D) scape.getRoot()).getMutationRate()) {
                if (randomIs()) {
                    child.strategy = PD2D.COOPERATE;
                } else {
                    child.strategy = PD2D.DEFECT;
                }
            }
            System.arraycopy(this.strategies.encoding, 0, child.strategies.encoding, 0, this.strategies.encoding.length);
            //System.arraycopy(this.identity.encoding, 0, child.identity.encoding, 0, this.identity.encoding.length);
        }
    }

    private static byte[] swapSpace = new byte[255];

    public void decidePlay(PlayerGA partner) {
        if (((PD2DGA) getRoot()).getTrackAll() == 1) {
            playRecord = (PartnerPlayRecord) partnerRecords.get(partner);
        }
        if (playRecord == null) {
            //No record exists yet
            playRecord = new PartnerPlayRecord();
            //Copy default history from encoding
            //The default encoding is the individuals GA assumption about play info without other info.
            //Not thread safe
            System.arraycopy(strategies.encoding, strategySize(), swapSpace, 0, defaultHistorySize());
            //We can simplify this aftter we make sure it works..
            playRecord.played = 0;
            for (int i = 0; i < defaultHistorySize(); i++) {
                playRecord.played += swapSpace[i] * Math.pow(2, i);
            }
            //System.out.println("new");
            //}
            //else {
            //System.out.println("exist");
            if (((PD2DGA) getRoot()).getTrackAll() == 1) {
                partnerRecords.put(partner, playRecord);
            }
        }
        strategy = strategies.encoding[playRecord.played];
    }

    public void recordPlay(PlayerGA partner) {
        //Move old history back
        playRecord.played = playRecord.played / 4;
        //record last strategy played
        playRecord.played = playRecord.played + this.strategy * (int) Math.pow(2, ((memSize() - 1) * 2));
        //record last partner played
        playRecord.played = playRecord.played + partner.strategy * (int) Math.pow(2, ((memSize() - 1) * 2 + 1));
        tempTotalCount++;
        if (strategy == PD2D.COOPERATE) {
            tempCooperationCount++;
        }
    }

    /**
     * The color to paint this agent; blue if cooperate, red if defect.
     */
    public Color getColor() {
        if (strategy == PD2D.COOPERATE) {
            return Color.blue;
        } else {
            return Color.red;
        }
    }

    private int tempTotalCount = 0;
    private int tempCooperationCount = 0;

    public void playBegin() {
        tempTotalCount = 0;
        tempCooperationCount = 0;
    }

    public void playEnd() {
        if (tempTotalCount > 0) {
            totalCount = tempTotalCount;
            cooperationCount = tempCooperationCount;
        }
    }

    /**
     * Interact with each neighbor as specified by the Agent.play() method.
     */
    public void playNeighbors() {
        playBegin();
        super.playNeighbors();
        playEnd();
    }

    public void playRandomNeighbor() {
        playBegin();
        super.playRandomNeighbor();
        playEnd();
    }

    Rule play_all = new Rule("Play") {
        /**
         * 
         */
        private static final long serialVersionUID = 2310662184769678048L;

        public void execute(Agent a) {
            PlayerGA.this.play(a);
        }
    };

    public void playAll() {
        playBegin();
        getScape().executeOnMembers(new Rule("Play") {
            /**
             * 
             */
            private static final long serialVersionUID = 2999967533894163283L;

            public void execute(Agent a) {
                PlayerGA.this.play(a);
            }
        });
        playEnd();
    }

    public final static int PREFERENCE_CLOSEST = 0;
    public final static int PREFERENCE_FARTHEST = 1;

    public int getPreference() {
        return strategies.encoding[strategies.encoding.length - 1];
    }

    public void playPreference() {
        playBegin();
        central = this;
        if (getPreference() == 0) {
            Cell closest = (Cell) ((HostCell) getHostCell().findMaximumWithin(closestDistance, false, 1)).getOccupant();
            if (closest != null) {
                play(closest);
            }
        } else {
            Cell furthest = (Cell) ((HostCell) getHostCell().findMaximumWithin(furthestDistance, false, 1)).getOccupant();
            if (furthest != null) {
                play(furthest);
            }
        }
        playEnd();
    }

    public void playClosest() {
        playBegin();
        central = this;
        Cell closest = (Cell) ((HostCell) getHostCell().findMaximumWithin(closestDistance, false, 1)).getOccupant();
        if (closest != null) {
            play(closest);
        }
        playEnd();
    }

    public void playFurthest() {
        playBegin();
        central = this;
        Cell furthest = (Cell) ((HostCell) getHostCell().findMaximumWithin(furthestDistance, false, 1)).getOccupant();
        if (furthest != null) {
            play(furthest);
        }
        playEnd();
    }

    public double getDefectRatio() {
        if (totalCount > 0) {
            return ((double) (totalCount - cooperationCount)) / ((double) totalCount);
        } else {
            return 0;
        }
    }

    public double getCooperateRatio() {
        if (totalCount > 0) {
            return ((double) cooperationCount) / ((double) totalCount);
        } else {
            return 0;
        }
    }

    private static PlayerGA central;

    private static DataPoint closestDistance = new DataPoint() {
        public double getValue(Object object) {
            PlayerGA other = (PlayerGA) ((HostCell) object).getOccupant();
            if (other != null) {
                return 1.0 - central.strategies.hammingDistance(other.strategies);
                //return 1.0 - central.identity.hammingDistance(other.identity);
            } else {
                return -1.0;
            }
        }

        public String getName() {
            return "Closest Distance";
        }
    };

    private static DataPoint furthestDistance = new DataPoint() {
        public double getValue(Object object) {
            PlayerGA other = (PlayerGA) ((HostCell) object).getOccupant();
            if (other != null) {
                return central.strategies.hammingDistance(other.strategies);
                //return central.identity.hammingDistance(other.identity);
            } else {
                return -1.0;
            }
        }

        public String getName() {
            return "Furthest Distance";
        }
    };

    public void scapeCreated() {
        /*agents.addRule(new Rule("Test") {
            public void execute(Agent a) {
                ((PlayerGA) a).test1();
            }
        });*/
        getScape().addRule(new Rule("Play All") {
            /**
             * 
             */
            private static final long serialVersionUID = 2624996512918858385L;

            public void execute(Agent a) {
                ((PlayerGA) a).playAll();
            }
        }, false);
        getScape().addRule(new Rule("Play Closest") {
            /**
             * 
             */
            private static final long serialVersionUID = 2282755988782060805L;

            public void execute(Agent a) {
                ((PlayerGA) a).playClosest();
            }
        }, false);
        getScape().addRule(new Rule("Play Furthest") {
            /**
             * 
             */
            private static final long serialVersionUID = 5252852787075969595L;

            public void execute(Agent a) {
                ((PlayerGA) a).playFurthest();
            }
        }, false);
        getScape().addRule(new Rule("Crossover") {
            /**
             * 
             */
            private static final long serialVersionUID = 582547545111225412L;

            public void execute(Agent a) {
                ((PlayerGA) a).crossover();
            }
        });
        getScape().addRule(new Rule("Mutation") {
            /**
             * 
             */
            private static final long serialVersionUID = 5233061733786103253L;

            public void execute(Agent a) {
                ((PlayerGA) a).mutation();
            }
        });
        /*agents.addRule(new Rule("Test") {
            public void execute(Agent a) {
                ((PlayerGA) a).test2();
            }
        });*/
    }

    /**
     * Play one round of the game with another agent.
     */
    public void play(Agent partner) {
        this.decidePlay((PlayerGA) partner);
        ((PlayerGA) partner).decidePlay(this);
        super.play(partner);
        this.recordPlay((PlayerGA) partner);
        ((PlayerGA) partner).recordPlay(this);
    }

    public void test1() {
        System.out.println("1 " + getChromosomeAsString() + "   ");
    }

    public void test2() {
        System.out.println("2 " + getChromosomeAsString() + "   " + getCoordinate());
    }

    public void mutation() {
        strategies.mutate();
        //identity.mutate();
    }

    public void crossover() {
        if (getRandom().nextDouble() < ((PD2DGA) getRoot()).getCrossoverProbability()) {
            Cell n = findRandomNeighborOnHost();
            if (n != null) {
                strategies.crossover(((PlayerGA) n).strategies);
            }
        }
        /*if (getRandom().nextDouble() < ((PD2DGA) getRoot()).getCrossoverProbability()) {
            Cell n = findRandomNeighborOnHost();
            if (n != null) {
                identity.crossover(((PlayerGA) n).identity);
            }
        }*/
    }

    public String toString() {
        return getChromosomeStrategyAsString();
    }
}
