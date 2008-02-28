/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.util;

import java.awt.Image;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;

/**
 * An class encapsulating various common utility functions.
 * For now, just has some random number helper functions and a set of series for 2D arrays.
 *
 * @author Miles Parker
 * @version 1.0
 * @since 1.0
 */
public class Utility implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * A quick set of unique series up to 4 nodes.
     * Used to randomly traverse a list using a non-repeating path.
     */
    public final static int[][][] uniqueSeries = {{{}},
                                                  {{1}},
                                                  {{1, 2},
                                                   {2, 1}},
                                                  {{1, 2, 3},
                                                   {1, 3, 2},
                                                   {2, 1, 3},
                                                   {2, 3, 1},
                                                   {3, 1, 2},
                                                   {3, 2, 1}},
                                                  {{1, 2, 3, 4},
                                                   {1, 2, 4, 3},
                                                   {1, 3, 2, 4},
                                                   {1, 3, 4, 2},
                                                   {1, 4, 2, 3},
                                                   {1, 4, 3, 2},
                                                   {2, 1, 3, 4},
                                                   {2, 1, 4, 3},
                                                   {2, 3, 1, 4},
                                                   {2, 3, 4, 1},
                                                   {2, 4, 1, 3},
                                                   {2, 4, 3, 1},
                                                   {3, 1, 2, 4},
                                                   {3, 1, 4, 2},
                                                   {3, 2, 1, 4},
                                                   {3, 2, 4, 1},
                                                   {3, 4, 1, 2},
                                                   {3, 4, 2, 1},
                                                   {4, 1, 2, 3},
                                                   {4, 1, 3, 2},
                                                   {4, 2, 1, 3},
                                                   {4, 2, 3, 1},
                                                   {4, 3, 1, 2},
                                                   {4, 3, 2, 1}}};

    /**
     * Generate an integer uniformly distributed across some range.
     * @param random the random number stream to use
     * @param low the lowest number (inclusive) that the resulting int might be
     * @param high the hignest number (inclusive) that the resulting int might be
     * @return uniformly distributed pseudorandom int
     */
    public final static int randomInRange(Random random, int low, int high) {
        // return (random.nextInt() & Integer.MAX_VALUE) % (high - low + 1) + low;
        return random.nextInt(high - low + 1) + low;
    }

    /**
     * Generate a double uniformly distributed across some range.
     * @param random the random number stream to use
     * @param low the lowest number (inclusive) that the resulting double might be
     * @param high the highest number (exclusive) that the resulting double might be
     * @return uniformly distributed pseudo-random double
     */
    public final static double randomInRange(Random random, double low, double high) {
        return (random.nextDouble() * (high - low)) + low;
    }

    /**
     * Generate an integer uniformly distributed across 0...limit - 1.
     * @param random the random number stream to use
     * @param limit the maximum limit (exclusive) of the resulting int
     * @return uniformly distributed pseudo-random int
     */
    public final static int randomToLimit(Random random, int limit) {
        // Roedy Green's gotcha page has a mistake in it:
        // return (random.nextInt() & Integer.MAX_VALUE) % (limit); // Roedy Green's broken algorithm
        // see http://java.sun.com/docs/books/effective/excursion-random.html
        return random.nextInt(limit);
    }

    /**
     * Returns a random boolean value.
     * @param random the random number stream to use
     */
    public final static boolean randomIs(Random random) {
        // return (random.nextInt() > 0); // <-- approx. 1 in 10^9 bias towards false
        return random.nextBoolean();
    }

    /**
     * Reports standard error and any other desired behavior when the user attempts to use an unimplemented method.
     */
    public static void notImplemented() {
        throw new RuntimeException("Method not yet implemented, you should really complain to the developer.");
    }

    /**
     * Formats a time in milleseconds (typically elapsed time) into a string with format "[d'd'] hh:mm:ss.mmmm".
     * For example,  00:00:02.3328, or 1d 02:12:03.3521.
     * Note that time will be downcast to an int, but this should not really be a problem!
     */
    public static String formatElapsedMillis(long time) {
        int timeInt = (int) time;
        int mils = timeInt % 1000;
        timeInt /= 1000;
        int secs = timeInt % 60;
        timeInt /= 60;
        int mins = timeInt % 60;
        timeInt /= 60;
        int hours = timeInt % 24;
        timeInt /= 24;
        //timeInt is now remaining days.
        if (timeInt == 0) {
            return Integer.toString(hours) + ":" + Integer.toString(mins) + ":" + Integer.toString(secs) + "." + padStringLeftWithZeros(Integer.toString(mils), 3);
        } else {
            return Integer.toString(timeInt) + "d " + Integer.toString(hours) + ":" + Integer.toString(mins) + ":" + Integer.toString(secs) + "." + padStringLeftWithZeros(Integer.toString(mils), 3);
        }
    }

    /**
     * Helper method for removeQualifiers for now.
     */
    private static String removeStartsWith(String source, String startingText) {
        if (source.startsWith(startingText)) {
            return source.substring(startingText.length());
        } else {
            return source;
        }
    }

    /**
     * Removes qualifiers such as min and max from string.
     * @param string the string to remove qualifiers from
     */
    public static String removeQualifiers(String string) {
        //Not very concerned with performance here..
        string = removeStartsWith(string, "Minimum ");
        string = removeStartsWith(string, "Maximum ");
        string = removeStartsWith(string, "Min ");
        string = removeStartsWith(string, "Max ");
        return string;
    }

    /**
     * Helper method for removeQualifiers for now.
     */
    private static String orderWith(String source, String orderText, String endToken) {
        int occursAt = source.indexOf(orderText);
        if (occursAt >= 0) {
            return source.substring(0, occursAt) + source.substring(occursAt + orderText.length()) + endToken;
        } else {
            return source;
        }
    }

    /**
     * Removes qualifiers such as min and max from string, placing an appropriate order token at the end of the string.
     * Useful for sorting items into user readable order.
     * @param string the string to remove qualifiers from
     */
    public static String orderedQualifiers(String string) {
        //Not very concerned with performance here..
        string = orderWith(string, "Minimum ", "1");
        string = orderWith(string, "Maximum ", "2");
        string = orderWith(string, "Min ", "1");
        string = orderWith(string, "Max ", "2");
        string = orderWith(string, "Minimum", "1");
        string = orderWith(string, "Maximum", "2");
        string = orderWith(string, "Min", "1");
        string = orderWith(string, "Max", "2");
        return string;
    }

    /**
     * Pads the string with spaces on the right to the supplied size.
     * If the string is larger than the supplied number of spaces it is truncated.
     * @param string the string to pad
     * @param size the size to pad (or truncate) the string to
     */
    public static String padStringRight(String string, int size) {
        while (string.length() < size) {
            string += ' ';
        }
        if (string.length() > size) {
            string = string.substring(0, size);
        }
        return string;
    }

    /**
     * Pads the string with spaces on the left to the supplied size.
     * If the string is larger than the supplied number of spaces it is truncated.
     * @param string the string to pad
     * @param size the size to pad (or truncate) the string to
     */
    public static String padStringLeft(String string, int size) {
        while (string.length() < size) {
            string = ' ' + string;
        }
        if (string.length() > size) {
            string = string.substring(0, size);
        }
        return string;
    }

    /**
     * Pads the string with spaces on the left to the supplied size.
     * If the string is larger than the supplied number of spaces it is truncated.
     * @param string the string to pad
     * @param size the size to pad (or truncate) the string to
     */
    public static String padStringLeftWithZeros(String string, int size) {
        while (string.length() < size) {
            string = '0' + string;
        }
        if (string.length() > size) {
            string = string.substring(0, size);
        }
        return string;
    }

    /**
     * Returns the supplied number formatted as a string with the given number of decimal places fixed
     * and padded with zeroes if needed.
     * For example, <code>formatToString(23.436765, 2) would return "23.44".
     * @param num the number to format
     * @param decPlaces the number of places to display
     */
    public static String formatToString(double num, int decPlaces) {
        NumberFormat nf = NumberFormat.getNumberInstance();

        if (Math.abs(num) >= 1.0E9) {
// generate scientific notation
            try { // we use "try" because the number format of some locales may not support scientific notation
                ((DecimalFormat) nf).applyPattern("0.###E0");
            } catch (Exception e) {
            }
        }

        nf.setGroupingUsed(false);
        nf.setMinimumFractionDigits(decPlaces);
        nf.setMaximumFractionDigits(decPlaces);

        return nf.format(num);
    }

    /**
     * Given a class, returns the name of that class without qualifiers. e.g. "Color", not "java.awt.Color".
     */
    public static String getClassNameOnly(Class c) {
        String className = c.getName();
        return className.substring(className.lastIndexOf('.') + 1);
    }

/*
    // starts becoming less accurate than our new tanh around 10^-7,
    // and has a 100% error below 10^-17
    public static double tanh(double x) {
        double absX = Math.abs(x);

        if (absX > 22.0) {
            // tanh(22) equals 0.9999999999999999998 which equals 1.0 to within double precision
            return (x > 0.0) ? 1.0 : -1.0;
        } else {
            double expX = Math.exp(x);
            double expMX = Math.exp(-x);
            return (expX - expMX) / (expX + expMX);
        }
    }
*/

/*
    // starts becoming less accurate than our new tanh around 10^-7,
    // and has a 100% error below 10^-17
    public static double tanh(double x) {
        double absX = Math.abs(x);

        if (absX > 22.0) {
            // tanh(22) equals 0.9999999999999999998 which equals 1.0 to within double precision
            return (x > 0.0) ? 1.0 : -1.0;
        } else {
            return (Math.pow(Math.E, x) - Math.pow(Math.E, -x)) / (Math.pow(Math.E, x) + Math.pow(Math.E, -x));
        }
    }
*/

    /**
     * A function that returns the hyperbolic tangent of its argument.
     * Note that this method has its worst precision around |x| = 1.0e-6,
     * but it always provides at least 11 significant digits
     * @param x  the argument
     */
    public static double tanh(double x) {
        double absX = Math.abs(x);

        if (absX > 22.0) {
            // tanh(22) equals 0.9999999999999999998 which equals 1.0 to within double precision
            return (x > 0.0) ? 1.0 : -1.0;
        } else if (absX > 1.0e-6) {
            double expX = Math.exp(x);
            double expMX = Math.exp(-x);
            return (expX - expMX) / (expX + expMX);
        } else {
            // absX <= 1.0e-6
            return x;
        }
    }

    /**
     * Negatively scale an image.
     * @param image The image to be scaled
     * @param reductionFactorHeight Scale amount for height.
     * @param reductionFactorWidth Scale amount for width.
     * @return  The scaled image.
     */
    public static Image reduceImage(Image image, double reductionFactorHeight, double reductionFactorWidth) {
       int bWidth  = (int)(image.getWidth(null)/reductionFactorHeight);
       int bHeight = (int)(image.getHeight(null)/reductionFactorWidth);
       return image.getScaledInstance(bWidth,bHeight,Image.SCALE_SMOOTH);
     }

    /**
     * Positively scale an image.
     * @param image The image to be scaled
     * @param magnificationFactorHeight Scale amount for height.
     * @param magnificationFactorWidth Scale amount for width.
     * @return  The scaled image.
     */
    public static Image magnifyImage(Image image, double magnificationFactorHeight, double magnificationFactorWidth) {
       int bWidth  = (int)(image.getWidth(null)*magnificationFactorHeight);
       int bHeight = (int)(image.getHeight(null)*magnificationFactorWidth);
       return image.getScaledInstance(bWidth,bHeight,Image.SCALE_SMOOTH);
     }

}
