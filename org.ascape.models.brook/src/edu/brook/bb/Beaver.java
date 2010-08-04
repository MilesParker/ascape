/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */
//--------------------------------------------------------------------

package edu.brook.bb;

import org.ascape.model.Agent;
import org.ascape.model.CellOccupant;
import org.ascape.model.rule.Rule;

//--------------------------------------------------------------------

public class Beaver extends CellOccupant {

    /**
     * 
     */
    private static final long serialVersionUID = 4179121434655882917L;

    static int counter,
    tapeLength,
    maxStati,
    maxTrans,
    focusLength,
    mode,
    subStepPerStep;

    //Characters of the beaver
    int number;
    char type;

    //status variable
    int status,
    headPosition,
    trans;

    //performance measurement
    int maxOnes,
    writtenOnes;
    double averageOnes,
    steps;

    //Tape, table to store rules and variables to control transition
    int s,p,pt,w;
    char v;
    char[] tape;
    char[] action;
    char[] move;
    int[] newStatus;
    char[] focusTape;
    int[] subRuleOnes;
    boolean[] subRuleUsed;
    boolean[] statusUsed;
    boolean firstTime;
    String aStringRule;
    char[] aVectorRule;

    //Variables to store address of service objects
    Bsr bsr;
    Brr brr;
    Bga bga;
    Bga[] mga;

    //-----------------------------------------------------------------
    Beaver(int tL, int mS, int mT, int fL, int sS) {
        tapeLength = tL;
        maxStati = mS;
        maxTrans = mT;
        focusLength = fL;
        subStepPerStep = sS;

        if (maxStati < 4)
            mode = 2;
        else
            mode = 3;
    }

    public void initialize() {
        long aRandomSeed;
        int aRuleLength;
        int i;

        super.initialize();
        maxOnes = 0;
        averageOnes = 0;
        steps = 0;

        number = counter;
        counter++;

        if (number == 0)
            type = 's';
        else {
            if (number % 3 == 2) type = 'r';
            if (number % 3 == 1) type = 'g';
            if (number % 3 == 0) type = 'm';
        }

        //Creating the tape array, it will be used as tape by the simulated TM
        //focusTape array is needed in reporting results
        tape = new char[tapeLength];
        focusTape = new char[focusLength];
        subRuleOnes = new int[maxStati * 2];
        subRuleUsed = new boolean[maxStati * 2];
        statusUsed = new boolean[maxStati * 2];

        //Creating array where store the current strategy to apply, i.e. rules
        //to handle the TM work
        action = new char[maxStati * 2];
        move = new char[maxStati * 2];
        newStatus = new int[maxStati * 2];
        pt = tapeLength / 2 - focusLength / 2;

        if (type == 's') bsr = new Bsr(maxStati);

        if (type == 'r') {
            aRandomSeed = randomInRange(0, 999999);
            aRuleLength = (mode + 2) * 2 * maxStati;
            brr = new Brr(aRuleLength, aRandomSeed);
        }

        if (type == 'g') {
            aRandomSeed = randomInRange(0, 999999);
            aRuleLength = (mode + 2) * 2 * maxStati;
            bga = new Bga(100, aRuleLength, aRandomSeed);
        }

        if (type == 'm') {
            mga = new Bga[maxStati];
            for (i = 0; i < maxStati; i++) {
                aRandomSeed = randomInRange(0, 999999);
                aRuleLength = (mode + 2) * 2;
                bga = new Bga(100, aRuleLength, aRandomSeed);
                mga[i] = bga;
            }
        }

        firstTime = true;
    }

    //-----------------------------------------------------------------
    public void reset() {
        int i;

        status = 0;
        writtenOnes = 0;
        trans = 0;
        headPosition = tapeLength / 2;
        for (i = 0; i < tapeLength; i++) tape[i] = '0';
        for (i = 0; i < maxStati * 2; i++) {
            subRuleOnes[i] = 0;
            subRuleUsed[i] = false;
        }
        for (i = 0; i < maxStati * 2; i++) statusUsed[i] = false;
    }

    //-----------------------------------------------------------------
    public void setStaticRules() {
        aStringRule = bsr.getCurrentRule();
        aVectorRule = aStringRule.toCharArray();
        this.decode();
    }

    //-----------------------------------------------------------------
    public void setRandomRules() {
        aStringRule = brr.getCurrentRule();
        aVectorRule = aStringRule.toCharArray();
        this.decode();
    }

    //-----------------------------------------------------------------
    public void setGeneticRules() {
        bga.step();
        aStringRule = bga.getCurrentRule();
        aVectorRule = aStringRule.toCharArray();
        this.decode();
    }

    //----------------------------------------------------------------
    public void decode() {
        int i,s;

        for (i = 0; i < maxStati * 2; i++) {
            if (aVectorRule[i * (mode + 2)] == '1')
                action[i] = '1';
            else
                action[i] = '0';

            if (aVectorRule[i * (mode + 2) + 1] == '1')
                move[i] = 'R';
            else
                move[i] = 'L';

            s = 0;
            if (mode == 2) {
                if (aVectorRule[i * (mode + 2) + 2] == '1') s = s + 2;
                if (aVectorRule[i * (mode + 2) + 3] == '1') s = s + 1;
            }

            if (mode == 3) {
                if (aVectorRule[i * (mode + 2) + 2] == '1') s = s + 4;
                if (aVectorRule[i * (mode + 2) + 3] == '1') s = s + 2;
                if (aVectorRule[i * (mode + 2) + 4] == '1') s = s + 1;
            }
            newStatus[i] = s;
        }
    }

    //-----------------------------------------------------------------
    public void setGeneticSubRules(int st) {
        int i,s;

        bga = mga[st];
        bga.step();
        aStringRule = bga.getCurrentRule();
        aVectorRule = aStringRule.toCharArray();

        for (i = 0; i < 2; i++) {
            if (aVectorRule[i * (mode + 2)] == '1')
                action[i + st * 2] = '1';
            else
                action[i + st * 2] = '0';

            if (aVectorRule[i * (mode + 2) + 1] == '1')
                move[i + st * 2] = 'R';
            else
                move[i + st * 2] = 'L';

            s = 0;
            if (mode == 2) {
                if (aVectorRule[i * (mode + 2) + 2] == '1') s = s + 2;
                if (aVectorRule[i * (mode + 2) + 3] == '1') s = s + 1;
            }

            if (mode == 3) {
                if (aVectorRule[i * (mode + 2) + 2] == '1') s = s + 4;
                if (aVectorRule[i * (mode + 2) + 3] == '1') s = s + 2;
                if (aVectorRule[i * (mode + 2) + 4] == '1') s = s + 1;
            }
            newStatus[i + st * 2] = s;
        }
    }

    //-----------------------------------------------------------------
    public static final Rule STEP = new Rule("Step") {
        /**
         * 
         */
        private static final long serialVersionUID = -3308874521315266173L;

        public void execute(Agent beaver) {
            ((Beaver) beaver).step();
        }
    };

    //-----------------------------------------------------------------
    public void scapeCreated() {
        scape.addInitialRule(STEP);
        scape.addRule(STEP);
    }

    //-----------------------------------------------------------------
    public void step() {
        int i;

        if (type == 's') {
            if (firstTime) {
                this.subStep();
                firstTime = false;
            }
        }

        for (i = 0; i < subStepPerStep; i++) this.subStep();
    }

    //-----------------------------------------------------------------
    public void subStep() {
        int i,w;

        this.reset();

        steps++;
        if (type == 's') this.setStaticRules();
        if (type == 'r') this.setRandomRules();
        if (type == 'g') this.setGeneticRules();

        while ((status < maxStati) && (trans < maxTrans)) {
            v = tape[headPosition];
            s = status;
            if (!statusUsed[s]) {
                statusUsed[s] = true;
                for (i = 0; i < maxStati * 2; i++)
                    if (subRuleUsed[i]) subRuleOnes[i]++;
                if (type == 'm') this.setGeneticSubRules(s);
            }

            p = s * 2;
            if (v == '1') p = p + 1;
            subRuleUsed[p] = true;

            //Possible actions are: 0)write 0, 1)write 1.
            w = 0;
            if ((v == '0') && (action[p] == '1')) w = 1;
            if ((v == '1') && (action[p] == '0')) w = -1;

            if (w != 0)
                for (i = 0; i < maxStati * 2; i++)
                    if (subRuleUsed[i]) subRuleOnes[i] += w;

            tape[headPosition] = action[p];

            //Possible moves are: R)right, L)left.
            if (move[p] == 'R') headPosition++;
            if (move[p] == 'L') headPosition--;

            //setting the new status
            status = newStatus[p];

            trans++;

        }
        if (trans < maxTrans) {
            for (i = 0; i < tapeLength; i++) if (tape[i] == '1') writtenOnes++;
            subRuleOnes[p] += writtenOnes;
        } else {
            writtenOnes = -1;
            w = subRuleOnes[p];
            for (i = 0; i < maxStati * 2; i++)
                if (subRuleUsed[i]) subRuleOnes[i] -= w;
        }

        averageOnes = averageOnes + writtenOnes;

        w = 0;
        if (type == 'g') {
            for (i = 0; i < maxStati * 2; i++) w = w + subRuleOnes[i];
            bga.setReward((double) w);
        }

        if (type == 'm') {
            for (i = 0; i < maxStati; i++) {
                w = 0;
                w = subRuleOnes[i * 2] + subRuleOnes[i * 2 + 1];
                if (statusUsed[i]) {
                    bga = mga[i];
                    bga.setReward((double) w);
                }
            }
        }

        if (maxOnes < writtenOnes) {
            maxOnes = writtenOnes;
            if (maxOnes > 0) this.write();
        }
    }

    //-----------------------------------------------------------------
    public double getAverageOnes() {
        return averageOnes / steps;
    }


    //-----------------------------------------------------------------
    public int getMaxOnes() {
        return maxOnes;
    }

    //-----------------------------------------------------------------
    public void write() {
        int i;
        this.reset();

        while (status < maxStati) {
            v = tape[headPosition];
            s = status;
            p = s * 2;
            if (v == '1') p = p + 1;

            for (i = 0; i < focusLength; i++) focusTape[i] = tape[i + pt];
            System.out.println(String.valueOf(focusTape) + " " + headPosition + "  " +
                s + v + " -> " + action[p] + move[p] + newStatus[p]);
            tape[headPosition] = action[p];
            if (move[p] == 'R') headPosition++;
            if (move[p] == 'L') headPosition--;
            status = newStatus[p];
        }
        for (i = 0; i < focusLength; i++) focusTape[i] = tape[i + pt];
        System.out.println(String.valueOf(focusTape) + " " + headPosition);
        System.out.println("Beaver " + number + " type " + type +
            " has written " + maxOnes);
    }
//-------------------------------------------------------------------
}
