/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.test.model;

import org.ascape.model.AscapeObject;

/**

 * User: minchios
 * Date: Apr 5, 2003
 * Time: 10:55:26 PM
 * To change this template use Options | File Templates.
 */
public class RandomTest {

    private static final int NUM_ITERATIONS = 1000000;

    public static void main(String[] args) {
        System.out.println("Integer.MAX_VALUE = " + Integer.MAX_VALUE);
        System.out.println("Integer.MIN_VALUE = " + Integer.MIN_VALUE);

        int limit = 2 * (Integer.MAX_VALUE / 3);

        {
            System.out.println("");
            System.out.println("Old method:");
            AscapeObject ascapeObject = new AscapeObject() {
                public int randomToLimit(int limit) {
                    return (getRandom().nextInt() & Integer.MAX_VALUE) % (limit);
                }
            };
            runTest(ascapeObject, limit);
        }

        {
            System.out.println("");
            System.out.println("New method:");
            runTest(new AscapeObject(), limit);
        }

        {
            System.out.println("");
            System.out.println("Test of randomInRange(-10, 10):");
            AscapeObject ascapeObject = new AscapeObject();
            long sum = 0;
            int min = Integer.MAX_VALUE;
            int max = Integer.MIN_VALUE;
            for (int i = 0; i < NUM_ITERATIONS; i++) {
                int r = ascapeObject.randomInRange(-10, 10);

                sum += r;

                min = Math.min(r, min);
                max = Math.max(r, max);
            }
            System.out.println("min: " + min);
            System.out.println("max: " + max);
            System.out.println("average: " + (double) sum / NUM_ITERATIONS);
        }

        {
            System.out.println("");
            System.out.println("Test of randomInRange(-10.0, 10.0):");
            AscapeObject ascapeObject = new AscapeObject();
            double sum = 0;
            double min = Double.MAX_VALUE;
            double max = Double.MIN_VALUE;
            for (int i = 0; i < NUM_ITERATIONS; i++) {
                double r = ascapeObject.randomInRange(-10.0, 10.0);

                sum += r;

                min = Math.min(r, min);
                max = Math.max(r, max);
            }
            System.out.println("min: " + min);
            System.out.println("max: " + max);
            System.out.println("average: " + sum / NUM_ITERATIONS);
        }
    }

    private static void runTest(AscapeObject ascapeObject, int limit) {
        long sum = 0;
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        int count = 0;

        for (int i = 0; i < NUM_ITERATIONS; i++) {
            int r = ascapeObject.randomToLimit(limit);

            sum += r;

            min = Math.min(r, min);
            max = Math.max(r, max);

            if (r < limit / 2) {
                count++;
            }
        }

        double average = (double) sum / NUM_ITERATIONS;
        int inclusiveLimit = limit - 1;

        System.out.println("inclusiveLimit: " + inclusiveLimit);
        System.out.println("max: " + max);
        System.out.println("min: " + min);
        System.out.println("average / inclusiveLimit (expecting 0.5): " + average / inclusiveLimit);
        System.out.println("Fraction below half of limit: " + (double) count / NUM_ITERATIONS);
    }
}
