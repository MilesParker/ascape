/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.space;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.ascape.util.Conditional;
import org.ascape.util.RandomIterator;
import org.ascape.util.ResetableIterator;



/**
 * The Class Array2DBase.
 */
public abstract class Array2DBase extends Array implements Relative {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * The Class Array2DIterator.
     */
    private class Array2DIterator implements ResetableIterator, Serializable {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        /**
         * The x.
         */
        int x = 0;
        
        /**
         * The y.
         */
        int y = 0;

        /* (non-Javadoc)
         * @see org.ascape.util.ResetableIterator#first()
         */
        public void first() {
            x = 0;
            y = 0;
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#hasNext()
         */
        public boolean hasNext() {
            return ((x < cells.length - 1) || (y < cells[0].length));
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#next()
         */
        public Object next() {
            if (y >= cells[0].length) {
                x++;
                y = 0;
            }
            return cells[x][y++];
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#remove()
         */
        public void remove() {
            throw new UnsupportedOperationException("Can't remove an object from a immutable space.");
        }
    }

    /**
     * The Class Array2DSubIterator.
     */
    private class Array2DSubIterator extends Array2DIterator {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        /**
         * The start.
         */
        int start;
        
        /**
         * The limit.
         */
        int limit;
        
        /**
         * The xlimit.
         */
        int xlimit;
        
        /**
         * The ylimit.
         */
        int ylimit;

        /**
         * Instantiates a new array2 D sub iterator.
         * 
         * @param start
         *            the start
         * @param limit
         *            the limit
         */
        public Array2DSubIterator(int start, int limit) {
            this.start = start;
            this.limit = limit;
            first();
            xlimit = (limit / cells[0].length);
            ylimit = (limit % cells[0].length);
            if (ylimit == 0) {
                ylimit = cells[0].length;
            } else {
                xlimit++;
            }
        }

        /* (non-Javadoc)
         * @see org.ascape.model.space.Array2DBase.Array2DIterator#first()
         */
        public void first() {
            x = start / cells[0].length;
            y = start % cells[0].length;
        }

        /* (non-Javadoc)
         * @see org.ascape.model.space.Array2DBase.Array2DIterator#hasNext()
         */
        public boolean hasNext() {
            return ((x < xlimit - 1) || (y < ylimit));
        }

        /* (non-Javadoc)
         * @see org.ascape.model.space.Array2DBase.Array2DIterator#next()
         */
        public Object next() {
            if (y >= cells[0].length) {
                x++;
                if (x < xlimit) {
                    y = 0;
                }
            }
            return cells[x][y++];
        }

        /* (non-Javadoc)
         * @see org.ascape.model.space.Array2DBase.Array2DIterator#remove()
         */
        public void remove() {
            throw new UnsupportedOperationException("Can't remove an object from a immutable space.");
        }
    }

    /**
     * The Class Array2DRandomIterator.
     */
    private class Array2DRandomIterator implements RandomIterator {

        /**
         * The i.
         */
        private int i = -1;
        
        /**
         * The iter order.
         */
        private int[] iterOrder;
        
        /**
         * The size.
         */
        private final int size = getSize();

        /**
         * Instantiates a new array2 D random iterator.
         */
        public Array2DRandomIterator() {
            iterOrder = createOrder(getSize());
            randomize();
        }

        /* (non-Javadoc)
         * @see org.ascape.util.ResetableIterator#first()
         */
        public void first() {
            i = -1;
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#hasNext()
         */
        public boolean hasNext() {
            return i < size - 1;
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#next()
         */
        public Object next() {
            i++;
            return cells[iterOrder[i] / cells[0].length][iterOrder[i] % cells[0].length];
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#remove()
         */
        public void remove() {
            throw new UnsupportedOperationException("Can't remove an object from a immutable space.");
        }

        /* (non-Javadoc)
         * @see org.ascape.util.Randomizable#randomize()
         */
        public void randomize() {
            first();
            iterOrder = randomizeOrder(order, getRandom());
        }
    }

    /**
     * The actual 2D array of agents.
     */
    protected Node[][] cells = new Node[0][0];
    
    /**
     * A one dimensional selection of agents. Only created if getLocations is
     * called.
     */
    private Node[] agents = new Node[0];
    
    /**
     * The order.
     */
    protected int[] order;
    
    /**
     * Should movement or line of sight be used for interpreting 'nearness'?
     * Default is line of sight for the moment, but will change as soon as code
     * is completed.
     */
    protected boolean nearnessLineOfSight = true;

    /**
     * The found cells coordinates.
     */
    private int[][] foundCellsCoordinates = new int[1000][2];
    
    /**
     * The Constant relativeCoordinatesTemplate.
     */
    protected final static int[][][] relativeCoordinatesTemplate =
        {{{0, 0}},
         {{1, 0}, {0, 1}, {0, -1}, {-1, 0}},
         {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}},
         {{2, 0}, {0, 2}, {0, -2}, {-2, 0}},
         {{2, 1}, {2, -1}, {1, 2}, {1, -2}, {-1, 2}, {-1, -2}, {-2, 1}, {-2, -1}},
         {{2, 2}, {2, -2}, {-2, 2}, {-2, -2}},
         {{3, 0}, {0, 3}, {0, -3}, {-3, 0}},
         {{3, 1}, {3, -1}, {1, 3}, {1, -3}, {-1, 3}, {-1, -3}, {-3, 1}, {-3, -1}},
         {{3, 2}, {3, -2}, {2, 3}, {2, -3}, {-2, 3}, {-2, -3}, {-3, 2}, {-3, -2}},
         {{4, 0}, {0, 4}, {0, -4}, {-4, 0}},
         {{4, 1}, {4, -1}, {1, 4}, {1, -4}, {-1, 4}, {-1, -4}, {-4, 1}, {-4, -1}},
         {{3, 3}, {3, -3}, {-3, 3}, {-3, -3}},
         {{4, 2}, {4, -2}, {2, 4}, {2, -4}, {-2, 4}, {-2, -4}, {-4, 2}, {-4, -2}},
         {{5, 0}, {4, 3}, {4, -3}, {3, 4}, {3, -4}, {0, 5}, {0, -5}, {-3, 4}, {-3, -4}, {-4, 3}, {-4, -3}, {-5, 0}},
         {{5, 1}, {5, -1}, {1, 5}, {1, -5}, {-1, 5}, {-1, -5}, {-5, 1}, {-5, -1}},
         {{5, 2}, {5, -2}, {2, 5}, {2, -5}, {-2, 5}, {-2, -5}, {-5, 2}, {-5, -2}},
         {{4, 4}, {4, -4}, {-4, 4}, {-4, -4}},
         {{5, 3}, {5, -3}, {3, 5}, {3, -5}, {-3, 5}, {-3, -5}, {-5, 3}, {-5, -3}},
         {{6, 0}, {0, 6}, {0, -6}, {-6, 0}},
         {{6, 1}, {6, -1}, {1, 6}, {1, -6}, {-1, 6}, {-1, -6}, {-6, 1}, {-6, -1}},
         {{6, 2}, {6, -2}, {2, 6}, {2, -6}, {-2, 6}, {-2, -6}, {-6, 2}, {-6, -2}},
         {{5, 4}, {5, -4}, {4, 5}, {4, -5}, {-4, 5}, {-4, -5}, {-5, 4}, {-5, -4}},
         {{6, 3}, {6, -3}, {3, 6}, {3, -6}, {-3, 6}, {-3, -6}, {-6, 3}, {-6, -3}},
         {{7, 0}, {0, 7}, {0, -7}, {-7, 0}},
         {{7, 1}, {7, -1}, {5, 5}, {5, -5}, {1, 7}, {1, -7}, {-1, 7}, {-1, -7}, {-5, 5}, {-5, -5}, {-7, 1}, {-7, -1}},
         {{6, 4}, {6, -4}, {4, 6}, {4, -6}, {-4, 6}, {-4, -6}, {-6, 4}, {-6, -4}},
         {{7, 2}, {7, -2}, {2, 7}, {2, -7}, {-2, 7}, {-2, -7}, {-7, 2}, {-7, -2}},
         {{7, 3}, {7, -3}, {3, 7}, {3, -7}, {-3, 7}, {-3, -7}, {-7, 3}, {-7, -3}},
         {{6, 5}, {6, -5}, {5, 6}, {5, -6}, {-5, 6}, {-5, -6}, {-6, 5}, {-6, -5}},
         {{8, 0}, {0, 8}, {0, -8}, {-8, 0}},
         {{8, 1}, {8, -1}, {7, 4}, {7, -4}, {4, 7}, {4, -7}, {1, 8}, {1, -8}, {-1, 8}, {-1, -8}, {-4, 7}, {-4, -7}, {-7, 4}, {-7, -4}, {-8, 1}, {-8, -1}},
         {{8, 2}, {8, -2}, {2, 8}, {2, -8}, {-2, 8}, {-2, -8}, {-8, 2}, {-8, -2}},
         {{6, 6}, {6, -6}, {-6, 6}, {-6, -6}},
         {{8, 3}, {8, -3}, {3, 8}, {3, -8}, {-3, 8}, {-3, -8}, {-8, 3}, {-8, -3}},
         {{7, 5}, {7, -5}, {5, 7}, {5, -7}, {-5, 7}, {-5, -7}, {-7, 5}, {-7, -5}},
         {{8, 4}, {8, -4}, {4, 8}, {4, -8}, {-4, 8}, {-4, -8}, {-8, 4}, {-8, -4}},
         {{9, 0}, {0, 9}, {0, -9}, {-9, 0}},
         {{9, 1}, {9, -1}, {1, 9}, {1, -9}, {-1, 9}, {-1, -9}, {-9, 1}, {-9, -1}},
         {{9, 2}, {9, -2}, {7, 6}, {7, -6}, {6, 7}, {6, -7}, {2, 9}, {2, -9}, {-2, 9}, {-2, -9}, {-6, 7}, {-6, -7}, {-7, 6}, {-7, -6}, {-9, 2}, {-9, -2}},
         {{8, 5}, {8, -5}, {5, 8}, {5, -8}, {-5, 8}, {-5, -8}, {-8, 5}, {-8, -5}},
         {{9, 3}, {9, -3}, {3, 9}, {3, -9}, {-3, 9}, {-3, -9}, {-9, 3}, {-9, -3}},
         {{9, 4}, {9, -4}, {4, 9}, {4, -9}, {-4, 9}, {-4, -9}, {-9, 4}, {-9, -4}},
         {{7, 7}, {7, -7}, {-7, 7}, {-7, -7}},
         {{10, 0}, {8, 6}, {8, -6}, {6, 8}, {6, -8}, {0, 10}, {0, -10}, {-6, 8}, {-6, -8}, {-8, 6}, {-8, -6}, {-10, 0}},
         {{10, 1}, {10, -1}, {1, 10}, {1, -10}, {-1, 10}, {-1, -10}, {-10, 1}, {-10, -1}},
         {{10, 2}, {10, -2}, {2, 10}, {2, -10}, {-2, 10}, {-2, -10}, {-10, 2}, {-10, -2}},
         {{9, 5}, {9, -5}, {5, 9}, {5, -9}, {-5, 9}, {-5, -9}, {-9, 5}, {-9, -5}},
         {{10, 3}, {10, -3}, {3, 10}, {3, -10}, {-3, 10}, {-3, -10}, {-10, 3}, {-10, -3}},
         {{8, 7}, {8, -7}, {7, 8}, {7, -8}, {-7, 8}, {-7, -8}, {-8, 7}, {-8, -7}},
         {{10, 4}, {10, -4}, {4, 10}, {4, -10}, {-4, 10}, {-4, -10}, {-10, 4}, {-10, -4}},
         {{9, 6}, {9, -6}, {6, 9}, {6, -9}, {-6, 9}, {-6, -9}, {-9, 6}, {-9, -6}},
         {{11, 0}, {0, 11}, {0, -11}, {-11, 0}},
         {{11, 1}, {11, -1}, {1, 11}, {1, -11}, {-1, 11}, {-1, -11}, {-11, 1}, {-11, -1}},
         {{11, 2}, {11, -2}, {10, 5}, {10, -5}, {5, 10}, {5, -10}, {2, 11}, {2, -11}, {-2, 11}, {-2, -11}, {-5, 10}, {-5, -10}, {-10, 5}, {-10, -5}, {-11, 2}, {-11, -2}},
         {{8, 8}, {8, -8}, {-8, 8}, {-8, -8}},
         {{11, 3}, {11, -3}, {9, 7}, {9, -7}, {7, 9}, {7, -9}, {3, 11}, {3, -11}, {-3, 11}, {-3, -11}, {-7, 9}, {-7, -9}, {-9, 7}, {-9, -7}, {-11, 3}, {-11, -3}},
         {{10, 6}, {10, -6}, {6, 10}, {6, -10}, {-6, 10}, {-6, -10}, {-10, 6}, {-10, -6}},
         {{11, 4}, {11, -4}, {4, 11}, {4, -11}, {-4, 11}, {-4, -11}, {-11, 4}, {-11, -4}},
         {{12, 0}, {0, 12}, {0, -12}, {-12, 0}},
         {{12, 1}, {12, -1}, {9, 8}, {9, -8}, {8, 9}, {8, -9}, {1, 12}, {1, -12}, {-1, 12}, {-1, -12}, {-8, 9}, {-8, -9}, {-9, 8}, {-9, -8}, {-12, 1}, {-12, -1}},
         {{11, 5}, {11, -5}, {5, 11}, {5, -11}, {-5, 11}, {-5, -11}, {-11, 5}, {-11, -5}},
         {{12, 2}, {12, -2}, {2, 12}, {2, -12}, {-2, 12}, {-2, -12}, {-12, 2}, {-12, -2}},
         {{10, 7}, {10, -7}, {7, 10}, {7, -10}, {-7, 10}, {-7, -10}, {-10, 7}, {-10, -7}},
         {{12, 3}, {12, -3}, {3, 12}, {3, -12}, {-3, 12}, {-3, -12}, {-12, 3}, {-12, -3}},
         {{11, 6}, {11, -6}, {6, 11}, {6, -11}, {-6, 11}, {-6, -11}, {-11, 6}, {-11, -6}},
         {{12, 4}, {12, -4}, {4, 12}, {4, -12}, {-4, 12}, {-4, -12}, {-12, 4}, {-12, -4}},
         {{9, 9}, {9, -9}, {-9, 9}, {-9, -9}},
         {{10, 8}, {10, -8}, {8, 10}, {8, -10}, {-8, 10}, {-8, -10}, {-10, 8}, {-10, -8}},
         {{13, 0}, {12, 5}, {12, -5}, {5, 12}, {5, -12}, {0, 13}, {0, -13}, {-5, 12}, {-5, -12}, {-12, 5}, {-12, -5}, {-13, 0}},
         {{13, 1}, {13, -1}, {11, 7}, {11, -7}, {7, 11}, {7, -11}, {1, 13}, {1, -13}, {-1, 13}, {-1, -13}, {-7, 11}, {-7, -11}, {-11, 7}, {-11, -7}, {-13, 1}, {-13, -1}},
         {{13, 2}, {13, -2}, {2, 13}, {2, -13}, {-2, 13}, {-2, -13}, {-13, 2}, {-13, -2}},
         {{13, 3}, {13, -3}, {3, 13}, {3, -13}, {-3, 13}, {-3, -13}, {-13, 3}, {-13, -3}},
         {{12, 6}, {12, -6}, {6, 12}, {6, -12}, {-6, 12}, {-6, -12}, {-12, 6}, {-12, -6}},
         {{10, 9}, {10, -9}, {9, 10}, {9, -10}, {-9, 10}, {-9, -10}, {-10, 9}, {-10, -9}},
         {{13, 4}, {13, -4}, {11, 8}, {11, -8}, {8, 11}, {8, -11}, {4, 13}, {4, -13}, {-4, 13}, {-4, -13}, {-8, 11}, {-8, -11}, {-11, 8}, {-11, -8}, {-13, 4}, {-13, -4}},
         {{12, 7}, {12, -7}, {7, 12}, {7, -12}, {-7, 12}, {-7, -12}, {-12, 7}, {-12, -7}},
         {{13, 5}, {13, -5}, {5, 13}, {5, -13}, {-5, 13}, {-5, -13}, {-13, 5}, {-13, -5}},
         {{14, 0}, {0, 14}, {0, -14}, {-14, 0}},
         {{14, 1}, {14, -1}, {1, 14}, {1, -14}, {-1, 14}, {-1, -14}, {-14, 1}, {-14, -1}},
         {{14, 2}, {14, -2}, {10, 10}, {10, -10}, {2, 14}, {2, -14}, {-2, 14}, {-2, -14}, {-10, 10}, {-10, -10}, {-14, 2}, {-14, -2}},
         {{11, 9}, {11, -9}, {9, 11}, {9, -11}, {-9, 11}, {-9, -11}, {-11, 9}, {-11, -9}},
         {{14, 3}, {14, -3}, {13, 6}, {13, -6}, {6, 13}, {6, -13}, {3, 14}, {3, -14}, {-3, 14}, {-3, -14}, {-6, 13}, {-6, -13}, {-13, 6}, {-13, -6}, {-14, 3}, {-14, -3}},
         {{12, 8}, {12, -8}, {8, 12}, {8, -12}, {-8, 12}, {-8, -12}, {-12, 8}, {-12, -8}},
         {{14, 4}, {14, -4}, {4, 14}, {4, -14}, {-4, 14}, {-4, -14}, {-14, 4}, {-14, -4}},
         {{13, 7}, {13, -7}, {7, 13}, {7, -13}, {-7, 13}, {-7, -13}, {-13, 7}, {-13, -7}},
         {{14, 5}, {14, -5}, {11, 10}, {11, -10}, {10, 11}, {10, -11}, {5, 14}, {5, -14}, {-5, 14}, {-5, -14}, {-10, 11}, {-10, -11}, {-11, 10}, {-11, -10}, {-14, 5}, {-14, -5}},
         {{15, 0}, {12, 9}, {12, -9}, {9, 12}, {9, -12}, {0, 15}, {0, -15}, {-9, 12}, {-9, -12}, {-12, 9}, {-12, -9}, {-15, 0}},
         {{15, 1}, {15, -1}, {1, 15}, {1, -15}, {-1, 15}, {-1, -15}, {-15, 1}, {-15, -1}},
         {{15, 2}, {15, -2}, {2, 15}, {2, -15}, {-2, 15}, {-2, -15}, {-15, 2}, {-15, -2}},
         {{14, 6}, {14, -6}, {6, 14}, {6, -14}, {-6, 14}, {-6, -14}, {-14, 6}, {-14, -6}},
         {{13, 8}, {13, -8}, {8, 13}, {8, -13}, {-8, 13}, {-8, -13}, {-13, 8}, {-13, -8}},
         {{15, 3}, {15, -3}, {3, 15}, {3, -15}, {-3, 15}, {-3, -15}, {-15, 3}, {-15, -3}},
         {{15, 4}, {15, -4}, {4, 15}, {4, -15}, {-4, 15}, {-4, -15}, {-15, 4}, {-15, -4}},
         {{11, 11}, {11, -11}, {-11, 11}, {-11, -11}},
         {{12, 10}, {12, -10}, {10, 12}, {10, -12}, {-10, 12}, {-10, -12}, {-12, 10}, {-12, -10}},
         {{14, 7}, {14, -7}, {7, 14}, {7, -14}, {-7, 14}, {-7, -14}, {-14, 7}, {-14, -7}},
         {{15, 5}, {15, -5}, {13, 9}, {13, -9}, {9, 13}, {9, -13}, {5, 15}, {5, -15}, {-5, 15}, {-5, -15}, {-9, 13}, {-9, -13}, {-13, 9}, {-13, -9}, {-15, 5}, {-15, -5}},
         {{16, 0}, {0, 16}, {0, -16}, {-16, 0}},
         {{16, 1}, {16, -1}, {1, 16}, {1, -16}, {-1, 16}, {-1, -16}, {-16, 1}, {-16, -1}},
         {{16, 2}, {16, -2}, {14, 8}, {14, -8}, {8, 14}, {8, -14}, {2, 16}, {2, -16}, {-2, 16}, {-2, -16}, {-8, 14}, {-8, -14}, {-14, 8}, {-14, -8}, {-16, 2}, {-16, -2}},
         {{15, 6}, {15, -6}, {6, 15}, {6, -15}, {-6, 15}, {-6, -15}, {-15, 6}, {-15, -6}},
         {{16, 3}, {16, -3}, {12, 11}, {12, -11}, {11, 12}, {11, -12}, {3, 16}, {3, -16}, {-3, 16}, {-3, -16}, {-11, 12}, {-11, -12}, {-12, 11}, {-12, -11}, {-16, 3}, {-16, -3}},
         {{13, 10}, {13, -10}, {10, 13}, {10, -13}, {-10, 13}, {-10, -13}, {-13, 10}, {-13, -10}},
         {{16, 4}, {16, -4}, {4, 16}, {4, -16}, {-4, 16}, {-4, -16}, {-16, 4}, {-16, -4}},
         {{15, 7}, {15, -7}, {7, 15}, {7, -15}, {-7, 15}, {-7, -15}, {-15, 7}, {-15, -7}},
         {{14, 9}, {14, -9}, {9, 14}, {9, -14}, {-9, 14}, {-9, -14}, {-14, 9}, {-14, -9}},
         {{16, 5}, {16, -5}, {5, 16}, {5, -16}, {-5, 16}, {-5, -16}, {-16, 5}, {-16, -5}},
         {{12, 12}, {12, -12}, {-12, 12}, {-12, -12}},
         {{15, 8}, {15, -8}, {8, 15}, {8, -15}, {-8, 15}, {-8, -15}, {-15, 8}, {-15, -8}},
         {{13, 11}, {13, -11}, {11, 13}, {11, -13}, {-11, 13}, {-11, -13}, {-13, 11}, {-13, -11}},
         {{16, 6}, {16, -6}, {6, 16}, {6, -16}, {-6, 16}, {-6, -16}, {-16, 6}, {-16, -6}},
         {{14, 10}, {14, -10}, {10, 14}, {10, -14}, {-10, 14}, {-10, -14}, {-14, 10}, {-14, -10}},
         {{16, 7}, {16, -7}, {7, 16}, {7, -16}, {-7, 16}, {-7, -16}, {-16, 7}, {-16, -7}},
         {{15, 9}, {15, -9}, {9, 15}, {9, -15}, {-9, 15}, {-9, -15}, {-15, 9}, {-15, -9}},
         {{13, 12}, {13, -12}, {12, 13}, {12, -13}, {-12, 13}, {-12, -13}, {-13, 12}, {-13, -12}},
         {{14, 11}, {14, -11}, {11, 14}, {11, -14}, {-11, 14}, {-11, -14}, {-14, 11}, {-14, -11}},
         {{16, 8}, {16, -8}, {8, 16}, {8, -16}, {-8, 16}, {-8, -16}, {-16, 8}, {-16, -8}},
         {{15, 10}, {15, -10}, {10, 15}, {10, -15}, {-10, 15}, {-10, -15}, {-15, 10}, {-15, -10}},
         {{16, 9}, {16, -9}, {9, 16}, {9, -16}, {-9, 16}, {-9, -16}, {-16, 9}, {-16, -9}},
         {{13, 13}, {13, -13}, {-13, 13}, {-13, -13}},
         {{14, 12}, {14, -12}, {12, 14}, {12, -14}, {-12, 14}, {-12, -14}, {-14, 12}, {-14, -12}},
         {{15, 11}, {15, -11}, {11, 15}, {11, -15}, {-11, 15}, {-11, -15}, {-15, 11}, {-15, -11}},
         {{16, 10}, {16, -10}, {10, 16}, {10, -16}, {-10, 16}, {-10, -16}, {-16, 10}, {-16, -10}},
         {{14, 13}, {14, -13}, {13, 14}, {13, -14}, {-13, 14}, {-13, -14}, {-14, 13}, {-14, -13}},
         {{15, 12}, {15, -12}, {12, 15}, {12, -15}, {-12, 15}, {-12, -15}, {-15, 12}, {-15, -12}},
         {{16, 11}, {16, -11}, {11, 16}, {11, -16}, {-11, 16}, {-11, -16}, {-16, 11}, {-16, -11}},
         {{14, 14}, {14, -14}, {-14, 14}, {-14, -14}},
         {{15, 13}, {15, -13}, {13, 15}, {13, -15}, {-13, 15}, {-13, -15}, {-15, 13}, {-15, -13}},
         {{16, 12}, {16, -12}, {12, 16}, {12, -16}, {-12, 16}, {-12, -16}, {-16, 12}, {-16, -12}},
         {{15, 14}, {15, -14}, {14, 15}, {14, -15}, {-14, 15}, {-14, -15}, {-15, 14}, {-15, -14}},
         {{16, 13}, {16, -13}, {13, 16}, {13, -16}, {-13, 16}, {-13, -16}, {-16, 13}, {-16, -13}},
         {{15, 15}, {15, -15}, {-15, 15}, {-15, -15}},
         {{16, 14}, {16, -14}, {14, 16}, {14, -16}, {-14, 16}, {-14, -16}, {-16, 14}, {-16, -14}},
         {{16, 15}, {16, -15}, {15, 16}, {15, -16}, {-15, 16}, {-15, -16}, {-16, 15}, {-16, -15}},
         {{16, 16}, {16, -16}, {-16, 16}, {-16, -16}}};
    
    /**
     * The relative coordinates.
     */
    protected int[][][] relativeCoordinates = new int[relativeCoordinatesTemplate.length][16][2];
    
    /**
     * The relative coordinates rank lengths.
     */
    protected static int[] relativeCoordinatesRankLengths =
        {1, 4, 4, 4, 8, 4, 4, 8, 8, 4,
         8, 4, 8, 12, 8, 8, 4, 8, 4, 8,
         8, 8, 8, 4, 12, 8, 8, 8, 8, 4,
         16, 8, 4, 8, 8, 8, 4, 8, 16, 8,
         8, 8, 4, 12, 8, 8, 8, 8, 8, 8,
         8, 4, 8, 16, 4, 16, 8, 8, 4, 16,
         8, 8, 8, 8, 8, 8, 4, 8, 12, 16,
         8, 8, 8, 8, 16, 8, 8, 4, 8, 12,
         8, 16, 8, 8, 8, 16, 12, 8, 8, 8,
         8, 8, 8, 4, 8, 8, 16, 4, 8, 16,
         8, 16, 8, 8, 8, 8, 8, 4, 8, 8,
         8, 8, 8, 8, 8, 8, 8, 8, 8, 4,
         8, 8, 8, 8, 8, 8, 4, 8, 8, 8,
         8, 4, 8, 8, 4};
    
    /**
     * The relative coordinates rank distance.
     */
    protected static double[] relativeCoordinatesRankDistance;
    
    /**
     * The sum of coordinates within rank.
     */
    protected static int[] sumOfCoordinatesWithinRank =
        {1, 5, 9, 13, 21, 25, 29, 37, 45, 49,
         57, 61, 69, 81, 89, 97, 101, 109, 113, 121,
         129, 137, 145, 149, 161, 169, 177, 185, 193, 197,
         213, 221, 225, 233, 241, 249, 253, 261, 277, 285,
         293, 301, 305, 317, 325, 333, 341, 349, 357, 365,
         373, 377, 385, 401, 405, 421, 429, 437, 441, 457,
         465, 473, 481, 489, 497, 505, 509, 517, 529, 545,
         553, 561, 569, 577, 593, 601, 609, 613, 621, 633,
         641, 657, 665, 673, 681, 697, 709, 717, 725, 733,
         741, 749, 757, 761, 769, 777, 793, 797, 805, 821,
         829, 845, 853, 861, 869, 877, 885, 889, 897, 905,
         913, 921, 929, 937, 945, 953, 961, 969, 977, 981,
         989, 997, 1005, 1013, 1021, 1029, 1033, 1041, 1049, 1057,
         1065, 1069, 1077, 1085, 1089};
    
    /**
     * The rank limit.
     */
    public static int rankLimit;

    /**
     * The current distance from the origin for this enumeration.
     */
    protected int currentRank = 0;

    /**
     * The current position within the current rank for this enumeration.
     */
    protected int currentPositionInRank = 0;

    /**
     * Constructs a base 2-dimensional array space of provided extent.
     */
    public Array2DBase() {
        super();
        relativeCoordinatesRankDistance = new double[relativeCoordinatesTemplate.length];
    }

    /**
     * Constructs a base 2-dimensional array space of provided extent.
     * 
     * @param extent
     *            a coordinate describing the size of this space
     */
    public Array2DBase(CoordinateDiscrete extent) {
        this();
        setExtent(extent);
    }

    /**
     * Overides the clone method to do a deep clone of member state so that such
     * state will not be shared between scapes. The key here is that
     * CollectionSpace ignores the prototype agent's extent, and since
     * CollectionSpace is by default a 1D space, we need to be sure the 2D
     * characteristic of the prototype agent isn't lost.
     * 
     * @return the object
     */
    public Object clone() {
        CollectionSpace clone = (CollectionSpace) super.clone();
        Coordinate2DDiscrete originalExtent = (Coordinate2DDiscrete) extent;
        clone.extent = new Coordinate2DDiscrete(originalExtent.getXValue(), originalExtent.getYValue());
//        clone.extent = new Coordinate2DDiscrete(0, 0);
        return clone;
    }

    /**
     * Contructs the basic space structure. Instantiates the cells, but does not
     * populate them. Geometry and extent should be set before calling this
     * method.
     */
    public void construct() {
        cells = new Node[((Coordinate2DDiscrete) extent).getXValue()][((Coordinate2DDiscrete) extent).getYValue()];
        order = createOrder(getSize());
    }

    /**
     * Populates the space with clones of the prototype agent. Prototype agent
     * should be set before calling this method. (By default, the prototpye
     * agent is a Node.)
     */
    public void populate() {
        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells[0].length; y++) {
                cells[x][y] = (Node) getContext().getPrototype().clone();
                cells[x][y].setCoordinate(new Coordinate2DDiscrete(x, y));
            }
        }

        agents = null;
    }

    /**
     * Initializes the space, copying a set of relative coordinates for use, and
     * ensuring that the ordering used for random draws starts consistently.
     */
    public void initialize() {
        super.initialize();
        //We need to be certain that a new ordering is created at the start of any new run
        order = createOrder(((Coordinate2DDiscrete) extent).getXValue() * ((Coordinate2DDiscrete) extent).getYValue());
    }

    /**
     * Sets the geometry of this space. Must of course be a one-dimensional
     * geometry.
     * 
     * @param geometry
     *            the basic geometry of this space
     */
    public void setGeometry(Geometry geometry) {
        super.setGeometry(geometry);
        if (geometry.getDimensionCount() != 2) {
            throw new RuntimeException("Tried to assign an inappropriate geometry.");
        }
    }

    /**
     * Sets the size of the space. The extent defines the limit of the lattice
     * (exclusive) from the origin (inclusive), so that the extent also
     * represents the size of the lattice. It is an error to set extent while a
     * space is running.
     * 
     * @param extent
     *            a coordinate at the maximum extent
     */
    public void setExtent(Coordinate extent) {
        super.setExtent(extent);
    }

    /**
     * Sets the size of the space. The extent defines the limit of the lattice
     * (exclusive) from the origin (inclusive), so that the extent also
     * represents the size of the lattice. It is an error to set extent while a
     * space is running.
     * 
     * @param xSize
     *            the horizontal size (width) of the space
     * @param ySize
     *            the vertical size (height) of the space
     */
    public void setExtent(int xSize, int ySize) {
        this.setExtent(new Coordinate2DDiscrete(xSize, ySize));
    }

    /**
     * Randomizes the lookup used to determine calling order for random order
     * execution of rules.
     */
    public void randomizeCallingOrder() {
        order = randomizeOrder(order, getRandom());
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.CollectionSpace#iterator()
     */
    public Iterator iterator() {
        return new Array2DIterator();
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.CollectionSpace#safeIterator()
     */
    public ResetableIterator safeIterator() {
        return new Array2DIterator();
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.CollectionSpace#safeIterator(int, int)
     */
    public ResetableIterator safeIterator(int start, int limit) {
        return new Array2DSubIterator(start, limit);
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.CollectionSpace#safeRandomIterator()
     */
    public RandomIterator safeRandomIterator() {
        return new Array2DRandomIterator();
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.CollectionSpace#isMutable()
     */
    public boolean isMutable() {
        return false;
    }

    /**
     * Returns the size, or number of cells, (the product of all extents) of
     * this two-dimensional array.
     * 
     * @return the size
     */
    public int getSize() {
        return ((Coordinate2DDiscrete) extent).getXValue() * ((Coordinate2DDiscrete) extent).getYValue();
        /*if (cells != null) {
            if (cells.length != 0) {
	            return cells.length * cells[0].length;
            }
            else {
	            return 0;
            }
	   	}
	   	else {
	   	    return 0;
	   	}*/
    }

    /**
     * Returns the horizontal span of the array.
     * 
     * @return the x size
     */
    public int getXSize() {
        if (cells != null) {
            return cells.length;
        } else {
            return 0;
        }
    }

    /**
     * Returns the vertical span of the array.
     * 
     * @return the y size
     */
    public int getYSize() {
        if (cells != null) {
            if (cells.length != 0) {
                return cells[0].length;
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    /**
     * Finds cells within the space that meet some condition.
     * 
     * @param condition
     *            the condition that found cell must meet
     * @return the node[]
     */
    public synchronized Node[] findCells(Conditional condition) {
        return findCells(condition, 0, 0, cells.length, cells[0].length);
    }

    /**
     * Finds cells within the specified space that meet some condition.
     * 
     * @param condition
     *            the condition that found cell must meet
     * @param _x
     *            the leftmost cell location
     * @param _y
     *            the topmost cell location
     * @param width
     *            the number of vertical cells
     * @param height
     *            the number of horizontal cells
     * @return the node[]
     */
    public synchronized Node[] findCells(Conditional condition, int _x, int _y, int width, int height) {
        int count = 0;
        if (getSize() > foundCellsCoordinates.length) {
            foundCellsCoordinates = new int[getSize()][2];
        }
        for (int x = _x; x < width; x++) {
            for (int y = _y; y < height; y++) {
                if (condition.meetsCondition(cells[x][y])) {
                    foundCellsCoordinates[count][0] = x;
                    foundCellsCoordinates[count][1] = y;
                    count++;
                }
            }
        }
        Node[] foundCells = new Node[count];
        for (int i = 0; i < count; i++) {
            foundCells[i] = cells[foundCellsCoordinates[i][0]][foundCellsCoordinates[i][1]];
        }
        return foundCells;
    }

    /**
     * Returns a cell randomly selected from the lattice.
     * 
     * @return the location
     */
    public Location findRandom() {
        return cells[randomToLimit(cells.length)][randomToLimit(cells[0].length)];
    }

    /**
     * Returns a random unoccupied discrete location in the space given with the
     * lattice.
     * 
     * @param x
     *            the leftmost cell location
     * @param y
     *            the top cell location
     * @param width
     *            the number of vertical cells
     * @param height
     *            the number of horizontal cells
     * @return the location
     */
    public Location findRandom(int x, int y, int width, int height) {
        if (geometry.isPeriodic()) {
            int xCoor = randomInRange(x, x + width - 1);
            if (xCoor < 0) {
                xCoor += getXSize();
            } else if (xCoor >= getXSize()) {
                xCoor -= getXSize();
            }
            int yCoor = randomInRange(y, y + height - 1);
            if (yCoor < 0) {
                yCoor += getYSize();
            } else if (yCoor >= getYSize()) {
                yCoor -= getYSize();
            }
            return cells[xCoor][yCoor];
        } else {
            if (x < 0) {
                x = 0;
            }
            width = x + width - 1;
            if (width >= getXSize()) {
                width = getXSize() - 1;
            }
            if (y < 0) {
                y = 0;
            }
            height = y + height - 1;
            if (height >= getYSize()) {
                height = getYSize() - 1;
            }
            return cells[randomInRange(x, width)][randomInRange(y, height)];
        }
    }

    /**
     * Returns a random unoccupied discrete location in the space given with the
     * lattice.
     * 
     * @param cell
     *            a cell to exclude from search (typically origin)
     * @param x
     *            the leftmost cell location
     * @param y
     *            the top cell location
     * @param width
     *            the number of vertical cells
     * @param height
     *            the number of horizontal cells
     * @return the location
     */
    public final Location findRandomRelative(Node cell, int x, int y, int width, int height) {
        Coordinate2DDiscrete coor = (Coordinate2DDiscrete) cell.getCoordinate();
        return findRandom(cell, coor.getXValue() + x, coor.getYValue() + y, coor.getXValue() + width, coor.getXValue() + height);
    }

    /**
     * Returns a random unoccupied discrete location in the space given with the
     * lattice.
     * 
     * @param cell
     *            a cell to exclude from search (typically origin)
     * @param x
     *            the leftmost cell location
     * @param y
     *            the top cell location
     * @param width
     *            the number of vertical cells
     * @param height
     *            the number of horizontal cells
     * @return the location
     */
    public final Location findRandom(Node cell, int x, int y, int width, int height) {
        Location randomCell;
        do {
            randomCell = findRandom(x, y, width, height);
        } while (cell == randomCell);
        return randomCell;
    }

    /**
     * Returns a random unoccupied discrete location in the space given with the
     * lattice. Returns null if no random locations are available, but an
     * unoccupied location, even if only one exists. This method first tries
     * testing random locations within the grid, and the first n (10) are found
     * to be unnoccupied, the number of random locations is assumed to be
     * sparse, and a search of all random locations is done.
     * 
     * @param x
     *            the leftmost cell location
     * @param y
     *            the top cell location
     * @param width
     *            the number of vertical cells
     * @param height
     *            the number of horizontal cells
     * @return the node
     */
    public Node findRandomUnoccupied(int x, int y, int width, int height) {
        for (int i = 0; i < 10; i++) {
            Node cell = (Node) findRandom(x, y, width, height);
            if (cell.isAvailable()) {
                return cell;
            }
        }
        List available = findAvailable();
        if (available.size() > 0) {
            return (Node) available.get(randomToLimit(available.size()));
        } else {
            return null;
        }
    }

    /**
     * Returns a coordinate randomly selected from the lattice's space.
     * 
     * @return the coordinate
     */
    public Coordinate findRandomCoordinate() {
        return new Coordinate2DDiscrete(randomToLimit(cells.length), randomToLimit(cells[0].length));
    }

    /**
     * Determines whether the supplied coordinate is valid in the space.
     * 
     * @param coordinate
     *            the position to check
     * @return true, if is valid
     */
    public boolean isValid(CoordinateDiscrete coordinate) {
        return isValid(coordinate.getValueAtDimension(1), coordinate.getValueAtDimension(2));
    }

    /**
     * Locates a cell based on a relative reference. e.g. given a Location with
     * coordiante [10,10] and a relative coordinate [-1, 1], method will return
     * location at [9, 11]. Does not fail if relative coordinate not in space,
     * instead returns null.
     * 
     * @param location
     *            the origin location
     * @param coordinate
     *            the delta from location
     * @return relative location if in bounds, otherwise null
     */
    public Location findRelative(Location location, Coordinate coordinate) {
        Coordinate2DDiscrete origin = ((Coordinate2DDiscrete) location.getCoordinate());
        Coordinate2DDiscrete delta = ((Coordinate2DDiscrete) coordinate);
        int rx = origin.getXValue() + delta.getXValue();
        int ry = origin.getYValue() + delta.getYValue();
        if (isValid(rx, ry)) {
            return get(rx, ry);
        } else {
            return null;
        }
    }

    /**
     * Returns the cell existing at the specified coordinate. It is the
     * programmers responisibility to determine whether the coordinate is valid.
     * If in doubt, check first, otherwise a RuntimeException wil result.
     * 
     * @param coordinate
     *            a coordinate asserted to be within the space
     * @return the location
     * @see #isValid
     */
    public Location get(Coordinate coordinate) {
        return cells[((Coordinate2DDiscrete) coordinate).getValueAtDimension(1)][((Coordinate2DDiscrete) coordinate).getValueAtDimension(2)];
    }

    /**
     * Sets the cell existing at the specified coordinate. It is the programmers
     * responisibility to determine whether the coordinate is valid. If in
     * doubt, check first to prevent a RuntimeException.
     * 
     * @param coordinate
     *            the coordinate
     * @param cell
     *            the cell
     */
    public void set(Coordinate coordinate, Location cell) {
        cells[((Coordinate2DDiscrete) coordinate).getXValue()][((Coordinate2DDiscrete) coordinate).getYValue()] = (Node) cell;
    }

    /**
     * Sets the cell existing at the specified coordinate. It is the programmers
     * responisibility to determine whether the coordinate is valid. If in
     * doubt, check first to prevent a RuntimeException.
     * 
     * @param xPosition
     *            the x position
     * @param yPosition
     *            the y position
     * @param cell
     *            the cell
     */
    public void set(int xPosition, int yPosition, Location cell) {
        cells[xPosition][yPosition] = (Node) cell;
    }

    /**
     * Determines whether the supplied position is valid in the space. It is the
     * programmers responisibility to determine whether the coordinate is valid.
     * If in doubt, check first to prevent a RuntimeException.
     * 
     * @param xPosition
     *            the x position to check
     * @param yPosition
     *            the y position to check
     * @return true, if is valid
     */
    public boolean isValid(int xPosition, int yPosition) {
        if ((xPosition >= 0) && (xPosition < cells.length) && (yPosition >= 0) && (cells.length > 0) && (yPosition < cells[0].length)) {
            return true;
        }
        return false;
    }

    /**
     * Returns the cell existing at the specified integer coordinates.
     * 
     * @param xPosition
     *            the x position
     * @param yPosition
     *            the y position
     * @return the node
     */
    public Node get(int xPosition, int yPosition) {
        return cells[xPosition][yPosition];
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.CollectionSpace#add(java.lang.Object)
     */
    public boolean add(Object o) {
        throw new UnsupportedOperationException("Arrays are immutable.");
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.CollectionSpace#addAll(java.util.Collection)
     */
    public boolean addAll(Collection c) {
        throw new UnsupportedOperationException("Arrays are immutable.");
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.CollectionSpace#clear()
     */
    public void clear() {
        throw new UnsupportedOperationException("Arrays are immutable.");
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.CollectionSpace#contains(java.lang.Object)
     */
    public boolean contains(Object o) {
        for (Iterator iterator = collection.iterator(); iterator.hasNext();) {
            if (iterator.next().equals(o)) {
                return true;
            }
        }
        return false;
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.CollectionSpace#containsAll(java.util.Collection)
     */
    public boolean containsAll(Collection c) {
        Iterator e = c.iterator();
        while (e.hasNext()) {
            if (!contains(e.next())) {
                return false;
            }
        }
        return true;
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.CollectionSpace#isEmpty()
     */
    public boolean isEmpty() {
        return getSize() != 0;
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.CollectionSpace#remove(java.lang.Object)
     */
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("Arrays are immutable.");
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.CollectionSpace#removeAll(java.util.Collection)
     */
    public boolean removeAll(Collection c) {
        throw new UnsupportedOperationException("Arrays are immutable.");
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.CollectionSpace#retainAll(java.util.Collection)
     */
    public boolean retainAll(Collection c) {
        throw new UnsupportedOperationException("Arrays are immutable.");
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.CollectionSpace#size()
     */
    public int size() {
        return getSize();
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.CollectionSpace#toArray()
     */
    public Object[] toArray() {
        if (agents == null) {
            agents = new Node[getSize()];
            int indexLocations = 0;
            for (int x = 0; x < cells.length; x++) {
                System.arraycopy(cells[x], 0, agents, indexLocations, cells[0].length);
                indexLocations += cells[0].length;
            }
        }
        return agents;
    }
    
    public List toList() {
    	return Arrays.asList(toArray());
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.CollectionSpace#toArray(java.lang.Object[])
     */
    public Object[] toArray(Object a[]) {
        a = (Object[]) java.lang.reflect.Array.newInstance(
            a.getClass().getComponentType(), getSize());
        int indexLocations = 0;
        for (int x = 0; x < cells.length; x++) {
            System.arraycopy(cells[x], 0, a, indexLocations, cells[0].length);
            indexLocations += cells[0].length;
        }
        return a;
    }

    /**
     * Should 'nearness' be interpreted as where an agent can move, or where the
     * agent has line of sight? This is important for von Neumann geometry, but
     * irrelevant otherwise. If we are using von Neuman geometry, we can
     * intrpret getDistance, getCellsNear, findMaximum, etc., as searching
     * within 'n' cells, where n is the distance to search. The question is
     * wether this search only looks within the presumed 'line of sight' of the
     * agent, or where the agent can move one cell at a time. That is, when use
     * line of sight interpretation is false (default), we use: N NNN NNTNN NNN
     * N Whereas is use line of sight intrpretation is true, we would use: N N
     * NNTNN N N Note that this menas that if this interpreation is used for von
     * Neumann distance, many cells will have infinite distance from the source,
     * since there is no line of site way to reach them! This interpretation is
     * neccessary to support the Sugarscae models.
     * 
     * @return true, if is nearness line of sight
     */
    public boolean isNearnessLineOfSight() {
        return nearnessLineOfSight;
    }

    /**
     * Returns wether line of sight is being used as the interpretation for von
     * Neumann geometry nearness. Default is true for the moment, but will
     * change as soon as code is completed.
     * 
     * @param nearnessLineOfSight
     *            should line of sight be used as the interpreation for
     *            'nearness'
     * @see #isNearnessLineOfSight
     */
    public void setNearnessLineOfSight(boolean nearnessLineOfSight) {
        this.nearnessLineOfSight = nearnessLineOfSight;
//        clearCache();
    }

    /**
     * Randomizes the coordinates within the specified rank.
     * 
     * @param rank
     *            the rank to shuffle
     */
    protected void randomizeRank(int rank) {
        //Random Shuffle
        //See Knuth Volume 2, 3.4.2, Algorithm P
        //Count with i from highest index down to one greater than lowest index...
        for (int j = Array2D.relativeCoordinatesRankLengths[rank] - 1; j > 0; j--) {
            int k = randomInRange(0, j);
            //No reason to swap if index is the same
            if (k != j) {
                int[] swapCoordinate = relativeCoordinates[rank][j];
                relativeCoordinates[rank][j] = relativeCoordinates[rank][k];
                relativeCoordinates[rank][k] = swapCoordinate;
            }
        }
    }

    /**
     * Find within moore.
     * 
     * @param origin
     *            the origin
     * @param includeSelf
     *            the include self
     * @param distanceDouble
     *            the distance double
     * @return the list
     */
    public List findWithinMoore(Coordinate origin, boolean includeSelf, double distanceDouble) {
        int distance = (int) distanceDouble;
        if (getGeometry().isPeriodic()) {
            int xMid = ((Coordinate2DDiscrete) origin).getXValue();
            int yMid = ((Coordinate2DDiscrete) origin).getYValue();
            int xExtent = ((Coordinate2DDiscrete) this.getExtent()).getXValue();
            int yExtent = ((Coordinate2DDiscrete) this.getExtent()).getYValue();
            int xMin = xMid - distance;
            int yMin = yMid - distance;
            int xMax = xMid + distance;
            int yMax = yMid + distance;

//Special cases where distance is larger than actual lattice size..
            if (distance * 2 + 1 > xExtent) {
                xMin = xMid - (xExtent / 2);
                xMax = xMin + xExtent - 1;
            }
            if (distance * 2 + 1 > yExtent) {
                yMin = yMid - (yExtent / 2);
                yMax = yMin + yExtent - 1;
            }

            int xSpan = xMax - xMin + 1;
            int ySpan = yMax - yMin + 1;
            Node[] cellsNear = new Node[xSpan * ySpan - (includeSelf ? 0 : 1)];
            int xSourcePos = xMin;
            int xOffset = 0;
            if (xMin < 0) {
                xSourcePos += xExtent;
            }
            for (int xPos = xMin; xPos <= xMax; xPos++) {
                if (xSourcePos >= xExtent) {
                    xSourcePos -= xExtent;
                }
//small additional cost of doing includeself check inside for loop seems better than cost of eytra code-compleyitx from moving it inside
                if ((includeSelf) || (xPos != xMid)) {
                    if (yMin < 0) {
                        System.arraycopy(cells[xSourcePos], yExtent + yMin, cellsNear, xOffset, -yMin);
                        System.arraycopy(cells[xSourcePos], 0, cellsNear, xOffset - yMin, yMax + 1);
                    } else if (yMax >= yExtent) { //both (yMin < 0) && (yMax >= yExtent) can't be true
                        System.arraycopy(cells[xSourcePos], yMin, cellsNear, xOffset, yExtent - yMin);
                        System.arraycopy(cells[xSourcePos], 0, cellsNear, xOffset + yExtent - yMin, yMax - yExtent + 1);
                    } else { //No boundary overlap
                        System.arraycopy(cells[xSourcePos], yMin, cellsNear, xOffset, ySpan);
                    }
                } else {
                    //Case where we are _not_ including origin cell, and we are at the row that contains origin cell
                    if (yMin < 0) {
                        System.arraycopy(cells[xSourcePos], yExtent + yMin, cellsNear, xOffset, -yMin);
                        System.arraycopy(cells[xSourcePos], 0, cellsNear, xOffset - yMin, yMid);
                        //Offset Moves down one, because we have passed the row with no included origin
                        xOffset -= 1;
                        System.arraycopy(cells[xSourcePos], yMid + 1, cellsNear, xOffset + yMid - yMin + 1, yMax - yMid);
                    } else if (yMax >= yExtent) { //both (yMin < 0) && (yMax >= yExtent) can't be true
                        System.arraycopy(cells[xSourcePos], yMin, cellsNear, xOffset, yMid - yMin);
                        //Offset Moves down one, because we have passed the row with no included origin
                        xOffset -= 1;
                        System.arraycopy(cells[xSourcePos], yMid + 1, cellsNear, xOffset + yMid - yMin + 1, yExtent - yMid - 1);
                        System.arraycopy(cells[xSourcePos], 0, cellsNear, xOffset + ySpan - (yMax - yExtent + 1), yMax - yExtent + 1);
                    } else { //No boundary overlap
                        if (yMin < yMax) {
                            System.arraycopy(cells[xSourcePos], yMin, cellsNear, xOffset, yMid - yMin);
                            //Offset Moves down one, because we have passed the row with no included origin
                            xOffset -= 1;
                            System.arraycopy(cells[xSourcePos], yMid + 1, cellsNear, xOffset + (yMid - yMin) + 1, yMax - yMid);
                        } else {
                            xOffset -= 1;
                        }
                    }
                }
                xOffset += ySpan;
                xSourcePos++;
            }
            return Arrays.asList(cellsNear);
        } else { //Non-periodic space
            int xMid = ((Coordinate2DDiscrete) origin).getXValue();
            int yMid = ((Coordinate2DDiscrete) origin).getYValue();
            int xExtent = ((Coordinate2DDiscrete) this.getExtent()).getXValue();
            int yExtent = ((Coordinate2DDiscrete) this.getExtent()).getYValue();
            int xMin = Math.max(xMid - distance, 0);
            int yMin = Math.max(yMid - distance, 0);
            int xMax = Math.min(xMid + distance, xExtent - 1);
            int yMax = Math.min(yMid + distance, yExtent - 1);

            int xSpan = xMax - xMin + 1;
            int ySpan = yMax - yMin + 1;

            Node[] cellsNear = new Node[xSpan * ySpan - (includeSelf ? 0 : 1)];
            int xOffset = 0;
            for (int xPos = xMin; xPos <= xMax; xPos++) {
//small additional cost of doing includeself check inside for loop seems better than cost of eytra code-compleyitx from moving it inside
                if ((includeSelf) || (xPos != xMid)) {
                    System.arraycopy(cells[xPos], yMin, cellsNear, xOffset, ySpan);
                } else {
                    if (yMin != yMax) {
                        System.arraycopy(cells[xPos], yMin, cellsNear, xOffset, yMid - yMin);
                        System.arraycopy(cells[xPos], yMid + 1, cellsNear, xOffset + yMid - yMin, yMax - yMid);
                    }
                    //Offset Moves down one, because we have passed the row with no included origin
                    xOffset -= 1;
                }
                xOffset += ySpan;
            }
            return Arrays.asList(cellsNear);
        }
    }

    /**
     * Find within von neumann.
     * 
     * @param origin
     *            the origin
     * @param includeSelf
     *            the include self
     * @param dist
     *            the dist
     * @return the list
     */
    public List findWithinVonNeumann(Coordinate origin, boolean includeSelf, double dist) {
        int distance = (int) dist;
        //if (distance != 1) || (neighbors == null)) {
        if (!nearnessLineOfSight) {
            return findWithinVonNeumannMovement(origin, includeSelf, distance);
        } else {
            return findWithinVonNeumannLineOfSight(origin, includeSelf, distance);
        }
        /*}
        else {
            //Special case, just return the previously calculated neighbors
            return origin.getNeighbors();
        }*/

    }

    /**
     * This method returns cells that are near the provided cell in von Neumann
     * space using the movement interpretation. Typically called from
     * getCellsNearVonNeumann based on value of nearnessLineOfSight. Ex: for
     * distance = 2, the neighhborhood would look like: N NNN NNTNN NNN N Not: N
     * N NNTNN N N
     * 
     * @param origin
     *            the agent to find cells near
     * @param includeSelf
     *            should supplied agent be included in the return set
     * @param distance
     *            the distance to form centralCells to return cells
     * @return the cells near von neumann movement
     * @deprecated
     */
    public final Node[] getCellsNearVonNeumannMovement(Node origin, boolean includeSelf, int distance) {
        List list = findWithinVonNeumannMovement(origin.getCoordinate(), includeSelf, distance);
        Node[] cells = new Node[list.size()];
        return (Node[]) list.toArray(cells);
    }

    /**
     * Find within von neumann movement.
     * 
     * @param origin
     *            the origin
     * @param includeSelf
     *            the include self
     * @param distance
     *            the distance
     * @return the list
     */
    public List findWithinVonNeumannMovement(Coordinate origin, boolean includeSelf, double distance) {
        return findWithinDefault(origin, includeSelf, distance);
    }

    /**
     * This method returns cells that are near the provided cell in von Neumann
     * space using the Line of Sight Interpretation. Typically called from
     * getCellsNearVonNeumann based on value of nearnessLineOfSight. Ex: for
     * distance = 2, the neighhborhood would look like: N N NNTNN N N Not: N NNN
     * NNTNN NNN N
     * 
     * @param origin
     *            the agent to find cells near
     * @param distance
     *            the distance to form centralCells to return cells
     * @param includeSelf
     *            should supplied agent be included in the return set
     * @return the cells near von neumann line of sight
     * @deprecated
     */
    public final Node[] getCellsNearVonNeumannLineOfSight(Node origin, boolean includeSelf, int distance) {
        List list = findWithinVonNeumannLineOfSight(origin.getCoordinate(), includeSelf, distance);
        Node[] cells = new Node[list.size()];
        return (Node[]) list.toArray(cells);
    }

    /**
     * Find within von neumann line of sight.
     * 
     * @param origin
     *            the origin
     * @param includeSelf
     *            the include self
     * @param dist
     *            the dist
     * @return the list
     */
    public List findWithinVonNeumannLineOfSight(Coordinate origin, boolean includeSelf, double dist) {
        int distance = (int) dist;
        int maxDistance = (distance * 2 + 1);
        if (getGeometry().isPeriodic()) {
            if ((maxDistance <= cells.length) && (maxDistance <= cells[0].length)) {
                if (includeSelf) {
                    Node[] nearCells = new Node[4 * distance + 1];
                    int x = ((Coordinate2DDiscrete) origin).getXValue();
                    int y = ((Coordinate2DDiscrete) origin).getYValue();
                    nearCells[0] = cells[x][y];
                    for (int i = 1; i <= distance; i++) {
                        if (x + i < cells.length) {
                            nearCells[i * 4 - 3] = cells[x + i][y];
                        } else {
                            nearCells[i * 4 - 3] = cells[x + i - cells.length][y];
                        }
                        if (x - i >= 0) {
                            nearCells[i * 4 - 2] = cells[x - i][y];
                        } else {
                            nearCells[i * 4 - 2] = cells[cells.length + (x - i)][y];
                        }
                        if (y + i < cells[0].length) {
                            nearCells[i * 4 - 1] = cells[x][y + i];
                        } else {
                            //fix
                            nearCells[i * 4 - 1] = cells[x][y + i - cells[0].length];
                        }
                        if (y - i >= 0) {
                            nearCells[i * 4] = cells[x][y - i];
                        } else {
                            nearCells[i * 4] = cells[x][cells[0].length + (y - i)];
                        }
                    }
                    return Arrays.asList(nearCells);
                } else {
                    Node[] nearCells = new Node[4 * distance];
                    int x = ((Coordinate2DDiscrete) origin).getXValue();
                    int y = ((Coordinate2DDiscrete) origin).getYValue();
                    for (int i = 1; i <= distance; i++) {
                        //Tim's fix:
                        if (x + i < cells.length) {
                            nearCells[i * 4 - 4] = cells[x + i][y];
                        } else {
                            nearCells[i * 4 - 4] = cells[x + i - cells.length][y];
                        }
                        if (x - i >= 0) {
                            nearCells[i * 4 - 3] = cells[x - i][y];
                        } else {
                            nearCells[i * 4 - 3] = cells[cells.length + (x - i)][y];
                        }
                        if (y + i < cells[0].length) {
                            nearCells[i * 4 - 2] = cells[x][y + i];
                        } else {
                            nearCells[i * 4 - 2] = cells[x][y + i - cells[0].length];
                        }
                        if (y - i >= 0) {
                            nearCells[i * 4 - 1] = cells[x][y - i];
                        } else {
                            nearCells[i * 4 - 1] = cells[x][cells[0].length + (y - i)];
                        }
                    }
                    //return nearCells;
                    return Arrays.asList(nearCells);
                }
            } else {
                int xExtent = ((Coordinate2DDiscrete) getExtent()).getXValue();
                int yExtent = ((Coordinate2DDiscrete) getExtent()).getYValue();
                int xMid = ((Coordinate2DDiscrete) origin).getXValue();
                int yMid = ((Coordinate2DDiscrete) origin).getYValue();
                int xMin = xMid - distance;
                if (xMin < 0) {
                    xMin += xExtent;
                }
                int yMin = yMid - distance;
                if (yMin < 0) {
                    yMin += yExtent;
                }
                int xMax = Math.min(xMid + distance, xExtent - 1);
                if (xMax >= xExtent) {
                    xMax -= xExtent;
                }
                int yMax = Math.min(yMid + distance, yExtent - 1);
                if (yMax >= yExtent) {
                    yMax -= yExtent;
                }
                int xSpan = xMax - xMin + 1;
                if (xSpan < 0) {
                    xSpan = xMax + (xExtent - xMin);
                }
                int ySpan = yMax - yMin + 1;
                if (ySpan < 0) {
                    ySpan = yMax + (yExtent - yMin);
                }
                int size = 0;
                if (xSpan > maxDistance) {
                    size += xSpan;
                } else {
                    size += Math.min(maxDistance, cells.length);
                }
                if (ySpan > maxDistance) {
                    size += ySpan - 1;
                } else {
                    size += Math.min(maxDistance, cells[0].length) - 1;
                }
                if (!includeSelf) {
                    size -= 1;
                }
                Node[] nearCells = new Node[size];
                int index = 0;
                try {
                    if (maxDistance > cells.length) {
                        for (int xPos = 0; xPos < xMid; xPos++) {
                            nearCells[index] = cells[xPos][yMid];
                            index++;
                        }
                        if (includeSelf) {
                            nearCells[index] = cells[xMid][yMid];
                            index++;
                        }
                        for (int xPos = xMid + 1; xPos < xExtent; xPos++) {
                            nearCells[index] = cells[xPos][yMid];
                            index++;
                        }
                    } else {
                        if (xMin < xMax) {
                            for (int xPos = xMin; xPos < xMid; xPos++) {
                                nearCells[index] = cells[xPos][yMid];
                                index++;
                            }
                            if (includeSelf) {
                                nearCells[index] = cells[xMid][yMid];
                                index++;
                            }
                            for (int xPos = xMid + 1; xPos <= xMax; xPos++) {
                                nearCells[index] = cells[xPos][yMid];
                                index++;
                            }
                        } else {
                            if (xMid < xMin) {
                                for (int xPos = xMin; xPos < xExtent; xPos++) {
                                    nearCells[index] = cells[xPos][yMid];
                                    index++;
                                }
                                for (int xPos = 0; xPos < xMid; xPos++) {
                                    nearCells[index] = cells[xPos][yMid];
                                    index++;
                                }
                                if (includeSelf) {
                                    nearCells[index] = cells[xMid][yMid];
                                    index++;
                                }
                                for (int xPos = xMid + 1; xPos <= xMax; xPos++) {
                                    nearCells[index] = cells[xPos][yMid];
                                    index++;
                                }
                            } else { //xMid > xMin
                                for (int xPos = xMin; xPos < xMid; xPos++) {
                                    nearCells[index] = cells[xPos][yMid];
                                    index++;
                                }
                                if (includeSelf) {
                                    nearCells[index] = cells[xMid][yMid];
                                    index++;
                                }
                                for (int xPos = xMid + 1; xPos < xExtent; xPos++) {
                                    nearCells[index] = cells[xPos][yMid];
                                    index++;
                                }
                                for (int xPos = 0; xPos <= xMax; xPos++) {
                                    nearCells[index] = cells[xPos][yMid];
                                    index++;
                                }
                            }
                        }
                    }
                    if (maxDistance > cells[0].length) {
                        for (int yPos = 0; yPos < yMid; yPos++) {
                            nearCells[index] = cells[xMid][yPos];
                            index++;
                        }
                        for (int yPos = yMid + 1; yPos < yExtent; yPos++) {
                            nearCells[index] = cells[xMid][yPos];
                            index++;
                        }
                    } else {
                        if (yMin < yMax) {
                            for (int yPos = yMin; yPos < yMid; yPos++) {
                                nearCells[index] = cells[xMid][yPos];
                                index++;
                            }
                            for (int yPos = yMid + 1; yPos <= yMax; yPos++) {
                                nearCells[index] = cells[xMid][yPos];
                                index++;
                            }
                        } else {
                            if (yMid < yMin) {
                                for (int yPos = yMin; yPos < yExtent; yPos++) {
                                    nearCells[index] = cells[xMid][yPos];
                                    index++;
                                }
                                for (int yPos = 0; yPos < yMid; yPos++) {
                                    nearCells[index] = cells[xMid][yPos];
                                    index++;
                                }
                                for (int yPos = yMid + 1; yPos <= yMax; yPos++) {
                                    nearCells[index] = cells[xMid][yPos];
                                    index++;
                                }
                            } else { //yMid > yMin
                                for (int yPos = yMin; yPos < yMid; yPos++) {
                                    nearCells[index] = cells[xMid][yPos];
                                    index++;
                                }
                                for (int yPos = yMid + 1; yPos < yExtent; yPos++) {
                                    nearCells[index] = cells[xMid][yPos];
                                    index++;
                                }
                                for (int yPos = 0; yPos <= yMax; yPos++) {
                                    nearCells[index] = cells[xMid][yPos];
                                    index++;
                                }
                            }
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("xMin = " + xMin);
                    System.out.println("xMid = " + xMid);
                    System.out.println("xMax = " + xMax);
                    System.out.println("xSpan = " + xSpan);
                    System.out.println("xExtent = " + xExtent);
                    System.out.println("cells = " + cells.length);
                    System.out.println("yMin = " + yMin);
                    System.out.println("yMid = " + yMid);
                    System.out.println("yMax = " + yMax);
                    System.out.println("ySpan = " + ySpan);
                    System.out.println("yEytent = " + yExtent);
                    System.out.println("cells[0] = " + cells[0].length);
                    System.out.println("nearCells = " + nearCells.length);
                    throw e;
                }
                return Arrays.asList(nearCells);
            }
        } else { //non-periodic space
            int xExtent = ((Coordinate2DDiscrete) getExtent()).getXValue();
            int yExtent = ((Coordinate2DDiscrete) getExtent()).getYValue();
            int xMid = ((Coordinate2DDiscrete) origin).getXValue();
            int yMid = ((Coordinate2DDiscrete) origin).getYValue();
            int xMin = Math.max(xMid - distance, 0);
            int yMin = Math.max(yMid - distance, 0);
            int xMax = Math.min(xMid + distance, xExtent - 1);
            int yMax = Math.min(yMid + distance, yExtent - 1);
            int xSpan = xMax - xMin + 1;
            int ySpan = yMax - yMin + 1;

            //Smaller by two if !includeSelf since we've counted the same cell twice
            Node[] nearCells = new Node[xSpan + ySpan - (includeSelf ? 1 : 2)];
            if (includeSelf) {
                System.arraycopy(cells[xMid], yMin, nearCells, 0, yMax - yMin);
                for (int xPos = xMin; xPos < xMid; xPos++) {
                    nearCells[xSpan + xPos - xMin] = cells[xPos][yMid];
                }
                for (int xPos = xMid + 1; xPos <= xMax; xPos++) {
                    nearCells[xSpan + xPos - xMin - 1] = cells[xPos][yMid];
                }
            } else {
                System.arraycopy(cells[xMid], yMin, nearCells, 0, yMid - yMin);
                System.arraycopy(cells[xMid], yMid + 1, nearCells, yMid - yMin, yMax - yMid);
                for (int xPos = xMin; xPos < xMid; xPos++) {
                    nearCells[ySpan + xPos - xMin - 1] = cells[xPos][yMid];
                }
                for (int xPos = xMid + 1; xPos <= xMax; xPos++) {
                    nearCells[ySpan + xPos - xMin - 2] = cells[xPos][yMid];
                }
            }
            return Arrays.asList(nearCells);
        }
    }

    /**
     * Find within euclidian.
     * 
     * @param origin
     *            the origin
     * @param includeSelf
     *            the include self
     * @param dist
     *            the dist
     * @return the list
     */
    public List findWithinEuclidian(Coordinate origin, boolean includeSelf, double dist) {
        int distance = (int) dist;
        if (getGeometry().isPeriodic()) {
            if (distance <= Array2D.relativeCoordinatesRankDistance[relativeCoordinates.length - 1]) {
                int maxRank = 0;
                boolean lastRankReached = false;
                while (!lastRankReached) {
                    if (Array2D.relativeCoordinatesRankDistance[maxRank + 1] > distance) {
                        lastRankReached = true;
                    } else {
                        maxRank++;
                    }
                }
                int index = 0;
                int xO = ((Coordinate2DDiscrete) origin).getXValue();
                int yO = ((Coordinate2DDiscrete) origin).getYValue();
                Node[] nearCells = new Node[Array2D.sumOfCoordinatesWithinRank[maxRank + (includeSelf ? 1 : 0)]];
                for (int rank = (includeSelf ? 0 : 1); rank < maxRank; rank++) {
                    for (int place = 0; place < Array2D.relativeCoordinatesRankLengths[rank]; place++) {
                        int x = xO + relativeCoordinates[rank][place][0];
                        if (x < 0) {
                            x += cells.length;
                        } else if (x >= cells.length) {
                            x -= cells.length;
                        }
                        int y = yO + relativeCoordinates[rank][place][1];
                        if (y < 0) {
                            y += cells[0].length;
                        } else if (y >= cells[0].length) {
                            y -= cells[0].length;
                        }
                        nearCells[index] = cells[x][y];
                        index++;
                    }
                }
                ArrayList found = new ArrayList();
                for (int i = 0; i < nearCells.length; i++) {
                    found.add(nearCells[i]);
                }
                return found;
            } else { //distance is larger than relative cooredinates acocunt for
                return findWithinDefault(origin, includeSelf, distance);
            }
        } else {
            return findWithinDefault(origin, includeSelf, distance);
        }
    }

    /**
     * Returns the next cell within immediate neighborhood toward the requested
     * cell. This method is used for both the Moore and Euclidian case, and
     * overridden for the von Neumann and smallworld cases.
     * 
     * @param originCell
     *            the current cell
     * @param targetCell
     *            the cell that we are moving toward
     * @return the node
     */
    public Node findCellToward(Node originCell, Node targetCell) {
        if (originCell == targetCell) {
            return originCell;
        }
        int ox = ((Coordinate2DDiscrete) originCell.getCoordinate()).getXValue();
        int oy = ((Coordinate2DDiscrete) originCell.getCoordinate()).getYValue();
        int tx = ((Coordinate2DDiscrete) targetCell.getCoordinate()).getXValue();
        int ty = ((Coordinate2DDiscrete) targetCell.getCoordinate()).getYValue();
        if (!getGeometry().isPeriodic()) {
            if (ox > tx) {
                ox--;
            } else if (ox < tx) {
                ox++;
            }
            if (oy > ty) {
                oy--;
            } else if (oy < ty) {
                oy++;
            }
        } else {
            int ex = getXSize();
            int ey = getYSize();
            if (ox > tx) {
                //is inside distance less than outside distance?
                if ((ox - tx) < (tx + (ex - ox))) {
                    ox--;
                } else {
                    ox++;
                }
            } else if (ox < tx) {
                if ((tx - ox) < (ox + (ex - tx))) {
                    ox++;
                } else {
                    ox--;
                }
            }
            if (oy > ty) {
                if ((oy - ty) < (ty + (ey - oy))) {
                    oy--;
                } else {
                    oy++;
                }
            } else if (oy < ty) {
                if ((ty - oy) < (oy + (ey - ty))) {
                    oy++;
                } else {
                    oy--;
                }
            }
            if (ox >= ex) {
                ox = 0;
            } else if (ox < 0) {
                ox = ex - 1;
            }
            if (oy >= ey) {
                oy = 0;
            } else if (oy < 0) {
                oy = ey - 1;
            }
        }
        return get(ox, oy);
    }

    /**
     * Returns the cell within immediate neighborhood furthest away from the
     * requestd cell. This method is used for both the Moore and Euclidian case,
     * and overridden for the von Neumann and smallworld cases.
     * 
     * @param originCell
     *            the current cell
     * @param targetCell
     *            the cell that we are moving toward
     * @return the node
     */
    public Node findCellAway(Node originCell, Node targetCell) {
        if (originCell == targetCell) {
            // todo: the following does not do a check to see if the random neighbor "isAvailable"
            return originCell.findRandomNeighbor();
        }
        int ox = ((Coordinate2DDiscrete) originCell.getCoordinate()).getXValue();
        int oy = ((Coordinate2DDiscrete) originCell.getCoordinate()).getYValue();
        int tx = ((Coordinate2DDiscrete) targetCell.getCoordinate()).getXValue();
        int ty = ((Coordinate2DDiscrete) targetCell.getCoordinate()).getYValue();
        int ex = getXSize();
        int ey = getYSize();
        int dx = tx - ox;
        int dy = ty - oy;
        if (getGeometry().isPeriodic()) {
            int rx = ex / 2;
            int ry = ey / 2;
            if (dx > 0) {
                if (dx < rx - 1) {
                    if (ox > 0) {
                        ox--;
                    } else {
                        ox = ex - 1;
                    }
                } else {
                    if (ox < ex - 1) {
                        ox++;
                    } else {
                        ox = 0;
                    }
                }
            } else if (dx < 0) {
                if (dx > -rx - 1) {
                    if (ox < ex - 1) {
                        ox++;
                    } else {
                        ox = 0;
                    }
                } else {
                    if (ox > 0) {
                        ox--;
                    } else {
                        ox = ex - 1;
                    }
                }
            }
            if (dy > 0) {
                if (dy < ry - 1) {
                    if (oy > 0) {
                        oy--;
                    } else {
                        oy = ey - 1;
                    }
                } else {
                    if (oy < ey - 1) {
                        oy++;
                    } else {
                        oy = 0;
                    }
                }
            } else if (dy < 0) {
                if (dy > -ry - 1) {
                    if (oy < ey - 1) {
                        oy++;
                    } else {
                        oy = 0;
                    }
                } else {
                    if (oy > 0) {
                        oy--;
                    } else {
                        oy = ey - 1;
                    }
                }
            }
        } else { // non-periodic
            if (dx > 0) {
                if (ox > 0) {
                    ox--;
                }
            } else if (dx < 0) {
                if (ox < ex - 1) {
                    ox++;
                }
            }
            if (dy > 0) {
                if (oy > 0) {
                    oy--;
                }
            } else if (dy < 0) {
                if (oy < ey - 1) {
                    oy++;
                }
            }
        }
        Node dCell = get(ox, oy);
        if (dCell.isAvailable()) {
            return dCell;
        } else {
            if (randomIs()) {
                dCell = get(((Coordinate2DDiscrete) originCell.getCoordinate()).getXValue(), oy);
                if (dCell.isAvailable()) {
                    return dCell;
                } else {
                    dCell = get(ox, ((Coordinate2DDiscrete) originCell.getCoordinate()).getYValue());
                    if (dCell.isAvailable()) {
                        return dCell;
                    }
                }
            } else {
                dCell = get(ox, ((Coordinate2DDiscrete) originCell.getCoordinate()).getYValue());
                if (dCell.isAvailable()) {
                    return dCell;
                } else {
                    dCell = get(((Coordinate2DDiscrete) originCell.getCoordinate()).getXValue(), oy);
                    if (dCell.isAvailable()) {
                        return dCell;
                    }
                }
            }
        }
        return originCell;
    }
}
