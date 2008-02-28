/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.diffusion;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

import org.ascape.model.Cell;
import org.ascape.model.space.Coordinate2DDiscrete;
import org.ascape.util.vis.DrawFeature;
import org.ascape.util.vis.DrawSymbol;


public class ABCDEFCell extends Cell {

    /**
     * 
     */
    private static final long serialVersionUID = -3770207188502231021L;
    public static float beta = .1f;
    public static float gamma = 40.0f;

    public final static int L = 1;
    public final static int M = 2;
    public final static int H = 3;
    protected int characteristic;
    public final static int A = 1;
    public final static int B = 2;
    public final static int C = 3;
    public final static int D = 4;
    public final static int E = 5;
    public final static int F = 6;
    protected int action = A;
    public static float matrixHA = 34;
    public static float matrixHB = 42.5f;
    public static float matrixHC = 51;
    public static float matrixMA = 28;
    public static float matrixMB = 35;
    public static float matrixMC = 0;
    public static float matrixLA = 24;
    public static float matrixLB = 0;
    public static float matrixLC = 0;
    public static float matrixHD = 8.5f;
    public static float matrixHE = 17f;
    public static float matrixHF = 25.5f;
    public static float matrixMD = 7;
    public static float matrixME = 14;
    public static float matrixMF = 21;
    public static float matrixLD = 6;
    public static float matrixLE = 12;
    public static float matrixLF = 18;

    public void initialize() {
        Coordinate2DDiscrete c = (Coordinate2DDiscrete) getCoordinate();
        if ((((float) c.getXValue() + c.getYValue()) % 2) == 0) {
            characteristic = M;
        } else {
            if (isAbove()) {
                characteristic = H;
            } else {
                characteristic = L;
            }
        }
        action = B;
    }

    public boolean isAbove() {
        Coordinate2DDiscrete c = (Coordinate2DDiscrete) getCoordinate();
        return c.getYValue() < ((Coordinate2DDiscrete) getScape().getExtent()).getYValue() / 2;
    }

    public void decideAction() {
        List neighbors = findNeighbors();
        int aCount = 0;
        int bCount = 0;
        int cCount = 0;
        int dCount = 0;
        int eCount = 0;
        int fCount = 0;
        for (int i = 0; i < neighbors.size(); i++) {
            switch (((ABCDEFCell) neighbors.get(i)).action) {
                case A:
                    aCount++;
                    break;
                case B:
                    bCount++;
                    break;
                case C:
                    cCount++;
                    break;
                case D:
                    dCount++;
                    break;
                case E:
                    eCount++;
                    break;
                case F:
                    fCount++;
                    break;
            }
        }
        float aTerm = (float) Math.pow(Math.E, beta * (charAction(A) + gamma * (aCount / 8.0)));
        if ((int) charAction(A) == 0) {
            aTerm = 0.0f;
        }
        float bTerm = (float) Math.pow(Math.E, beta * (charAction(B) + gamma * (bCount / 8.0)));
        if ((int) charAction(B) == 0) {
            bTerm = 0.0f;
        }
        float cTerm = (float) Math.pow(Math.E, beta * (charAction(C) + gamma * (cCount / 8.0)));
        if ((int) charAction(C) == 0) {
            cTerm = 0.0f;
        }
        float dTerm = (float) Math.pow(Math.E, beta * (charAction(D) + gamma * (dCount / 8.0)));
        if ((int) charAction(D) == 0) {
            dTerm = 0.0f;
        }
        float eTerm = (float) Math.pow(Math.E, beta * (charAction(E) + gamma * (eCount / 8.0)));
        if ((int) charAction(E) == 0) {
            eTerm = 0.0f;
        }
        float fTerm = (float) Math.pow(Math.E, beta * (charAction(F) + gamma * (fCount / 8.0)));
        if ((int) charAction(F) == 0) {
            fTerm = 0.0f;
        }

        float termSum = aTerm + bTerm + cTerm + dTerm + eTerm + fTerm;

        float probOverTerm = getRandom().nextFloat() * termSum;
        if (probOverTerm < aTerm) {
            action = A;
        } else if (probOverTerm < aTerm + bTerm) {
            action = B;
        } else if (probOverTerm < aTerm + bTerm + cTerm) {
            action = C;
        } else if (probOverTerm < aTerm + bTerm + cTerm + dTerm) {
            action = D;
        } else if (probOverTerm < aTerm + bTerm + cTerm + dTerm + eTerm) {
            action = E;
        } else {
            action = F;
        }
        requestUpdate();
    }

    public float charAction(int action) {
        switch (action) {
            case A:
                {
                    switch (characteristic) {
                        case L:
                            {
                                return matrixLA;
                            }
                        case M:
                            {
                                return matrixMA;
                            }
                        case H:
                            {
                                return matrixHA;
                            }
                    }
                }
            case B:
                {
                    switch (characteristic) {
                        case L:
                            {
                                return matrixLB;
                            }
                        case M:
                            {
                                return matrixMB;
                            }
                        case H:
                            {
                                return matrixHB;
                            }
                    }
                }
            case C:
                {
                    switch (characteristic) {
                        case L:
                            {
                                return matrixLC;
                            }
                        case M:
                            {
                                return matrixMC;
                            }
                        case H:
                            {
                                return matrixHC;
                            }
                    }
                }
            case D:
                {
                    switch (characteristic) {
                        case L:
                            {
                                return matrixLD;
                            }
                        case M:
                            {
                                return matrixMD;
                            }
                        case H:
                            {
                                return matrixHD;
                            }
                    }
                }
            case E:
                {
                    switch (characteristic) {
                        case L:
                            {
                                return matrixLE;
                            }
                        case M:
                            {
                                return matrixME;
                            }
                        case H:
                            {
                                return matrixHE;
                            }
                    }
                }
            case F:
                {
                    switch (characteristic) {
                        case L:
                            {
                                return matrixLF;
                            }
                        case M:
                            {
                                return matrixMF;
                            }
                        case H:
                            {
                                return matrixHF;
                            }
                    }
                }
        }
        throw new RuntimeException("Bad characteristic or action");
    }

    public int getAction() {
        return action;
    }

    public final static DrawFeature DRAW_BandW_Feature = new DrawFeature("Black & White") {
        /**
         * 
         */
        private static final long serialVersionUID = 2298379242651178745L;

        public void draw(Graphics g, Object object, int width, int height) {
            switch (((ABCDEFCell) object).action) {
                case A:
                    g.setColor(Color.white);
                    DrawSymbol.FILL_RECT.draw(g, width, height);
                    break;
                case B:
                    g.setColor(Color.gray);
                    DrawSymbol.FILL_RECT.draw(g, width, height);
                    break;
                case C:
                    g.setColor(Color.black);
                    DrawSymbol.FILL_RECT.draw(g, width, height);
                    break;
                case D:
                    g.setColor(Color.white);
                    DrawSymbol.FILL_RECT.draw(g, width, height);
                    g.setColor(Color.black);
                    DrawSymbol.DRAW_HATCH.draw(g, width, height);
                    break;
                case E:
                    g.setColor(Color.black);
                    DrawSymbol.FILL_RECT.draw(g, width, height);
                    g.setColor(Color.white);
                    DrawSymbol.DRAW_HATCH.draw(g, width, height);
                    break;
                case F:
                    g.setColor(Color.white);
                    DrawSymbol.FILL_RECT.draw(g, width, height);
                    g.setColor(Color.black);
                    DrawSymbol.DRAW_HATCH_G2_W1.draw(g, width, height);
                    break;
                default:
                    throw new RuntimeException("Bad action.");
            }
        }
    };

    public final Color getColor() {
        switch (action) {
            case A:
                return Color.red;
            case B:
                return Color.green;
            case C:
                return Color.blue;
            case D:
                return Color.magenta;
            case E:
                return Color.cyan;
            case F:
                return Color.orange;
            default:
                throw new RuntimeException("Bad action.");
        }
        /*switch (characteristic) {
            case L: return Color.white;
            case M: return Color.gray;
            case H: return Color.black;
            default: throw new RuntimeException("Bad action.");
        }*/
    }
}
