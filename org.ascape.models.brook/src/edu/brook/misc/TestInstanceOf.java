
package edu.brook.misc;

import java.util.Random;

public class TestInstanceOf {

    interface Fooable {

        public void fooMe();
    }

    interface MightFoo {

        public boolean isFooable();
    }

    static int t;

    class ConcreteFoo implements MightFoo, Fooable {

        public final boolean isFooable() {
            return true;
        }

        public void fooMe() {
            //just want to make sure the call isn't optimized away
            t++;
        }
    }

    class ConcreteNotFoo implements MightFoo {

        public final boolean isFooable() {
            return false;
        }
    }

    public void test() {
        Random r = new Random();
        //initialize
        MightFoo[] mightFoos = new MightFoo[500000];
        long totalInterface = 0;
        long totalMethod = 0;
        long totalString = 0;
        for (int trial = 0; trial < 100; trial++) {
            for (int i = 0; i < mightFoos.length; i++) {
                if (r.nextFloat() < .5) {
                    mightFoos[i] = new ConcreteFoo();
                } else {
                    mightFoos[i] = new ConcreteNotFoo();
                }
            }
            t = 0;
            long startInterface = System.currentTimeMillis();
            for (int i = 0; i < mightFoos.length; i++) {
                if (mightFoos[i] instanceof Fooable) {
                    ((Fooable) mightFoos[i]).fooMe();
                }
            }
            long endInterface = System.currentTimeMillis();
            totalInterface += (endInterface - startInterface);
            t = 0;
            long startMethod = System.currentTimeMillis();
            for (int i = 0; i < mightFoos.length; i++) {
                if (mightFoos[i].isFooable()) {
                    ((Fooable) mightFoos[i]).fooMe();
                }
            }
            long endMethod = System.currentTimeMillis();
            totalMethod += (endMethod - startMethod);
            t = 0;
            long startString = System.currentTimeMillis();
            for (int i = 0; i < mightFoos.length; i++) {
                if (mightFoos[i].getClass().getName().equals("edu.brook.misc.TestInstanceOf")) {
                    ((Fooable) mightFoos[i]).fooMe();
                }
            }
            long endString = System.currentTimeMillis();
            totalString += (endString - startString);
            System.out.println("Trial " + trial);
            System.out.println("Interface: " + (endInterface - startInterface));
            System.out.println("Method: " + (endMethod - startMethod));
            System.out.println("String: " + (endString - startString));
        }
        System.out.println();
        System.out.println("Sum");
        System.out.println("Interface: " + totalInterface);
        System.out.println("Method: " + totalMethod);
        System.out.println("String: " + totalString);
    }

    public static void main(String[] args) {
        (new TestInstanceOf()).test();
    }
}
