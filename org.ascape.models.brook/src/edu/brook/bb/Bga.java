/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.bb;

import java.util.Random;

class Bga {

    //Arrays to store rules and children
    String rules[];
    double fitness[];
    int copies[];
    boolean willReproduce[];
    boolean willDie[];
    String newRules[];

    //Rule Master variables
    int currentRule,bestRule,worstRule,mostDiffusedRule;
    int maxCopies;

    //Rule Maker variables
    int evolutions,reproductions,crossovers,mutations;
    int parents,children;

    //general variable, parms, randomizer
    int numberOfRules,ruleLength;
    boolean normalizeFitness;
    double turnoverRate,crossoverRate,mutationRate;
    double sumFitness,meanFitness,maxFitness,minFitness;

    Random rnd;

//---------------------------------------------------------------------------
//Constructor
//
    Bga(int nR, int rL, long rS) {
        char aRule[];
        int i,j;
        long randomSeed = 123456;

        //here we keep mandatory parameters and set default values for the others
        numberOfRules = nR;
        ruleLength = rL;
        randomSeed = rS;
        normalizeFitness = true;
        turnoverRate = 0.5;
        crossoverRate = 0.5;
        mutationRate = 0.001;

        //here we create a random distribution
        rnd = new Random(randomSeed);

        aRule = new char[ruleLength];

        //here we create the arrays to store rules and correlated values
        rules = new String[numberOfRules];
        fitness = new double[numberOfRules];
        copies = new int[numberOfRules];
        willReproduce = new boolean[numberOfRules];
        willDie = new boolean[numberOfRules];
        newRules = new String[numberOfRules];

        //here we set statistics to zero and currentRule to -1
        currentRule = -1;
        evolutions = 0;
        reproductions = 0;
        crossovers = 0;
        mutations = 0;

        //here we create a new population at random
        for (i = 0; i < numberOfRules; i++) {
            for (j = 0; j < ruleLength; j++) {
                if (rnd.nextDouble() < 0.5)
                    aRule[j] = '0';
                else
                    aRule[j] = '1';
            }

            rules[i] = new String(aRule);
            newRules[i] = new String(aRule);
        }
        parents = children = numberOfRules;
    }

//---------------------------------------------------------------------------
//Setters and getters
//parameters
    void setTurnoverRate(double tR) {
        turnoverRate = tR;
    }

    void setCrossoverRate(double cR) {
        crossoverRate = cR;
    }

    void setMutationRate(double mR) {
        mutationRate = mR;
    }

    void setNormalizeFitness(boolean nF) {
        normalizeFitness = nF;
    }

    double getTurnoverRate() {
        return turnoverRate;
    }

    double getCrossoverRate() {
        return crossoverRate;
    }

    double getMutationRate() {
        return mutationRate;
    }

    boolean getNormalizeFitness() {
        return normalizeFitness;
    }

//statistics
    int getEvolutions() {
        return evolutions;
    }

    int getCrossovers() {
        return crossovers;
    }

    int getMutations() {
        return mutations;
    }

//rules health
    double getSumFitness() {
        return sumFitness;
    }

    double getMaxFitness() {
        return maxFitness;
    }

    double getMinFitness() {
        return minFitness;
    }

    double getMeanFitness() {
        return meanFitness;
    }

    double getCurrentFitness() {
        return fitness[currentRule];
    }

//rules
    String getCurrentRule() {
        return rules[currentRule];
    }

    String getBestRule() {
        return rules[bestRule];
    }

    String getWorstRule() {
        return rules[worstRule];
    }

    String getMostDiffusedRule() {
        return rules[mostDiffusedRule];
    }

//GA convergency
    double getConv() {
        double d,n;

        d = maxCopies;
        n = numberOfRules;

        return d / n;
    }

//---------------------------------------------------------------------------
//print

    void print() {
        int i;

        for (i = 0; i < numberOfRules; i++)
            System.out.println(i + " " + rules[i] + " " + fitness[i] +
                " " + copies[i] + " " + willReproduce[i] +
                " " + willDie[i]);

        for (i = 0; i < children; i++)
            System.out.println(i + " " + newRules[i] + " " + fitness[i] +
                " " + copies[i] + " " + willReproduce[i] +
                " " + willDie[i]);


//      System.out.println("currentRule=     "+currentRule+" "
//                                            +rules[currentRule]);
        System.out.println("bestRule=        " + bestRule + " " + rules[bestRule]);
        System.out.println("worstRule=       " + worstRule + " " + rules[worstRule]);
        System.out.println("mostDiffusedRule=" + mostDiffusedRule + " "
            + rules[mostDiffusedRule]);

        System.out.println("minFitness       =" + minFitness);
        System.out.println("maxFitness       =" + maxFitness);
        System.out.println("meanFitness      =" + meanFitness);

        System.out.println("maxCopies =" + maxCopies
            + " conv=" + this.getConv());

        System.out.println("E=" + evolutions + " C=" + crossovers + " M=" + mutations);

    }



//---------------------------------------------------------------------------
//Rule master

    //------------------------------------------------------------------------
    void step() {
        currentRule++;
        if (currentRule == numberOfRules) {
            this.evolve();
            currentRule = 0;
        }
    }

    //------------------------------------------------------------------------
    void setReward(double fV) {
        fitness[currentRule] = fV;
        if (currentRule == 0) {
            sumFitness = minFitness = maxFitness = fV;
            worstRule = bestRule = currentRule;
            meanFitness = fV;
        } else {
            sumFitness = sumFitness + fV;
            meanFitness = sumFitness / (currentRule + 1);
            if (maxFitness < fV) {
                maxFitness = fV;
                bestRule = currentRule;
            }
            if (minFitness > fV) {
                minFitness = fV;
                worstRule = currentRule;
            }
        }
    }

    //------------------------------------------------------------------------
    void verify() {
        int i,j;

        for (i = 0; i < numberOfRules; i++) copies[i] = 0;
        for (i = 0; i < numberOfRules; i++)
            for (j = 0; j < numberOfRules; j++)
                if (rules[j].compareTo(rules[i]) == 0) copies[i]++;
        maxCopies = copies[0];
        mostDiffusedRule = 0;
        for (i = 0; i < numberOfRules; i++) {
            if (copies[i] > maxCopies) {
                maxCopies = copies[i];
                mostDiffusedRule = i;
            }
        }
    }

//------------------------------------------------------------------------
//Rule Maker

    //------------------------------------------------------------------------
    void evolve() {
        this.prepare();
        this.selectParents();
        this.selectSurvivers();
        this.copy();
//this.print();
        this.cross();
        this.replace();
    }

    //------------------------------------------------------------------------
    void prepare() {
        int i;
        double w = 0,s = 0;

        evolutions++;
        parents = (int) (numberOfRules * turnoverRate);
        parents = parents - (parents % 2);
        if (parents < 2) parents = 2;
        if (numberOfRules < 2) parents = 0;

        if (normalizeFitness == true) {
            w = maxFitness;
            for (i = 0; i < numberOfRules; i++) {
                if ((fitness[i] < w) && (fitness[i] > minFitness))
                    w = fitness[i];
            }
            w = minFitness - (w - minFitness) / 1000;
//          sumFitness = sumFitness - (w * numberOfRules);
//          minFitness = minFitness - w;
//          maxFitness = maxFitness - w;
        }

        if (w == 0) s = minFitness / 1000;
        if (s == 0) s = 0.001;
        sumFitness = 0;
        for (i = 0; i < numberOfRules; i++) {
            fitness[i] = fitness[i] - w; //+ rnd.nextDouble()*s;
            sumFitness = sumFitness + fitness[i];
            willReproduce[i] = false;
            willDie[i] = true;
        }
    }

    //------------------------------------------------------------------------
    void selectParents() {
        int i,j;
        double s,r,w;

        w = sumFitness;
        for (i = 0; i < parents; i++) {
            r = rnd.nextDouble() * w;
            s = 0;
            j = 0;
            while ((s < r) && (j < numberOfRules)) {
                if (willReproduce[j] == false) s = s + fitness[j];
                j++;
            }
            if (j < 1) j = 1;
            willReproduce[j - 1] = true;
            w = w - fitness[j - 1];
        }
    }

    //------------------------------------------------------------------------
    void selectSurvivers() {
        int i,j = 0,x;
        double s,r,w;

        x = numberOfRules - parents;
        w = sumFitness;
        for (i = 0; i < x; i++) {
            r = rnd.nextDouble() * w;
            s = 0;
            j = 0;
            while ((s < r) && (j < numberOfRules)) {
                if (willDie[j] == true) s = s + fitness[j];
                j++;
            }
            if (j < 1) j = 1;
            willDie[j - 1] = false;
            w = w - fitness[j - 1];
        }
    }

    //------------------------------------------------------------------------
    void copy() {
        int i,j = 0;

        for (i = 0; i < numberOfRules; i++) {
            if (willReproduce[i] == true) {
                newRules[j] = rules[i];
                j++;
            }
        }
        children = j;
    }

    //------------------------------------------------------------------------
    void cross() {
        int i,j,xp;
        char work;
        char genoma0[],genoma1[];

        genoma0 = new char[ruleLength];
        genoma1 = new char[ruleLength];

        for (i = 0; i < children / 2; i++) {
            genoma0 = newRules[i * 2].toCharArray();
            genoma1 = newRules[i * 2 + 1].toCharArray();

//System.out.println("Cross n. "+i+" ----------------");
//System.out.println("S0="+newRules[i*2]+" S1="+newRules[i*2+1]);
            xp = 0;
            if (rnd.nextDouble() < crossoverRate) {
                xp = (int) (rnd.nextDouble() * (ruleLength - 1)) + 1;
                crossovers++;
//System.out.println("crossover at: "+xp);
            }

            for (j = 0; j < ruleLength; j++) {
                if (rnd.nextDouble() < mutationRate) {
                    mutations++;
//System.out.println("mutation in S0 at: "+j);
                    if (genoma0[j] == '0')
                        genoma0[j] = '1';
                    else
                        genoma0[j] = '0';
                }

                if (rnd.nextDouble() < mutationRate) {
                    mutations++;
//System.out.println("mutation in S1 at: "+j);
                    if (genoma1[j] == '0')
                        genoma1[j] = '1';
                    else
                        genoma1[j] = '0';
                }

                if (j < xp) {
                    work = genoma0[j];
                    genoma0[j] = genoma1[j];
                    genoma1[j] = work;
                }
            }
            newRules[i * 2] = String.valueOf(genoma0);
            newRules[i * 2 + 1] = String.valueOf(genoma1);
//System.out.println("S0="+newRules[i*2]+" S1="+newRules[i*2+1]);
        }
    }

    //------------------------------------------------------------------------
    void replace() {
        int i,j = 0;
        for (i = 0; i < numberOfRules; i++) {
            if (willDie[i] == true) {
                rules[i] = newRules[j];
                j++;
            }
        }
    }
//---------------------------------------------------------------------------
} //end of class definition
