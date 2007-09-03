/*
 * Copyright 2000 Miles T. Parker. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package name.milesparker.spatialipd;

import edu.brook.pd.PD2D;


/**
 * A Chromosome.
 *
 * @author Miles Parker
 * @version 1.0
 **/
public class Chromosome {

    private PlayerGA player;

    public byte[] encoding;

    private static byte[] swapSpace = new byte[0];

    public void setPlayer(PlayerGA player) {
        this.player = player;
    }

    public void initialize() {
        for (int i = 0; i < encoding.length; i++) {
            encoding[i] = (byte) (player.randomIs() ? 0 : 1);
        }
    }

    public void setSize(int size) {
        encoding = new byte[size];
        if (size > swapSpace.length) {
            swapSpace = new byte[size];
        }
    }

    public void setEncoding(int coding) {
        for (int i = 0; i < encoding.length; i++) {
            int oldEncoding = coding;
            coding = coding / 2;
            if ((coding * 2) != oldEncoding) {
                encoding[i] = 1;
            } else {
                encoding[i] = 0;
            }
        }
    }

    /*public String getEncoding() {
        for (int i = 0; i < strategySize(); i++) {
            strat += strategies.encoding[i] == PD2D.COOPERATE ? "C" : "D";
        }
    }*/

    public void mutate() {
        /*System.out.println(p.isDelete() + " " + p.getCoordinate());
        for (int i = 0; i < encoding.length; i++) {
            System.out.print(encoding[i]);
        }*/
        //        System.out.println("1 "+this);
        //System.out.println(((PD2DGA) getRoot()).getMutationProbability());
        //        System.out.println(" "+this);
        for (int i = 0; i < encoding.length; i++) {
            if (player.getRandom().nextDouble() < ((PD2DGA) player.getRoot()).getMutationProbability()) {
                //        System.out.println(i+" "+this);
                encoding[i] = (byte) (encoding[i] == 1 ? 0 : 1);
                //        System.out.println(i+"-"+this);
            }
        }
        //        System.out.println("2 "+this+" "+PlayerGA.this.getCoordinate()+" "+PlayerGA.this.isDelete());
        //int l = randomInRange(0, encoding.length - 1);
        //encoding[l] = (byte) (encoding[l] == 1 ? 0 : 1);
        /*for (int i = 0; i < encoding.length; i++) {
            System.out.print(encoding[i]);
        }
        System.out.println();*/
    }

    public double hammingDistance(Chromosome other) {
        double distance = 0;
        for (int i = 0; i < this.encoding.length; i++) {
            if (this.encoding[i] != other.encoding[i]) {
                distance++;
            }
        }
//System.out.println(this);
//System.out.println(other+"  " + (distance / (double) other.encoding.length));
        return distance / (double) other.encoding.length;
    }

    //Not thread safe
    public void crossover(Chromosome other, int locus) {
        //        System.out.println("1 "+other+" "+this+" "+locus);
        System.arraycopy(this.encoding, 0, swapSpace, 0, locus);
        System.arraycopy(other.encoding, 0, this.encoding, 0, locus);
        System.arraycopy(swapSpace, 0, other.encoding, 0, locus);
        //        System.out.println("2 "+other+" "+this+" "+" ");
    }

    public final void crossover(Chromosome other) {
        crossover(other, player.randomInRange(0, encoding.length - 1));
    }

    public String toString() {
        String strat = new String();
        for (int i = 0; i < encoding.length; i++) {
            strat += encoding[i] == PD2D.COOPERATE ? "C" : "D";
        }
        return strat;
    }
}
