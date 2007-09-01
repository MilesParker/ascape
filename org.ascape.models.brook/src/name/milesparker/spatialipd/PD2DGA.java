/*
 * This software and documentation is subject to, and may be only used in accordance with, the terms and conditions of
 * the license agreement described or set forth in the file titled License.txt.  This software and documentation
 * contains information that is confidential and proprietary to NuTech Solutions, Inc.
 *
 *             Copyright (c) 2000-2004
 *             NuTech Solutions, Inc.
 * 
 * Portions of the software are licensed from The Brookings Institution
 *             Copyright (c) 1998-2000
 *            The Brookings Institution
 *           All rights reserved worldwide.
 *
 *
 * U.S. GOVERNMENT END USERS. The software is a "commercial item," as that term is defined in 48 C.F.R. 2.101,
 * consisting of "commercial computer software" and "commercial computer software documentation," as such terms are
 * used in 48 C.F.R. 12.212. Consistent with 48 C.F.R. 12.212 and 48 C.F.R. 227.7202-1 through 227.7202-4, and other
 * relevant sections of the Code of Federal Regulations, as applicable all U.S. Government End Users acquire the
 * software with only those rights set forth in the license agreement. This software includes trade secrets that
 * are exempt from disclosure under the Freedom of Information Act.  
 *
 * 
 * _Demographic Prisoners Dilemma_
 * See _Zones of Cooperation in Demographic Prisoner's Dilemma_
 * Santa Fe Institute Economics Research Program Working Paper
 * Joshua Epstein
 * jepstein@brook.edu
 *
 * Miles Parker
 * mparker@brook.edu
 * The Brookings Institution
 * Washington, D.C.
 */

package name.milesparker.spatialipd;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Map;

import org.ascape.model.HostCell;
import org.ascape.model.Scape;
import org.ascape.model.space.Array2D;
import org.ascape.util.data.StatCollector;
import org.ascape.util.data.StatCollectorCSA;
import org.ascape.util.data.StatCollectorCond;
import org.ascape.util.data.UnitIntervalDataPoint;
import org.ascape.util.vis.ColorFeatureGradiated;
import org.ascape.util.vis.DrawFeature;

import edu.brook.pd.PD2D;
import edu.brook.pd.Player;

public class PD2DGA extends PD2D {

    private int memorySize;

    private double mutationProbability;

    private double crossoverProbability;

    protected int trackAll = 0;

    Map colorForGenome;

    public int getTrackAll() {
        return trackAll;
    }

    public void setTrackAll(int trackAll) {
        this.trackAll = trackAll;
    }

    public void createScape() {
        super.createScape();

        colorForGenome = new HashMap();

        //Create an instance of Player that we can set state for...
        Player player = new PlayerGA();
        player.setHostScape(getLattice());
        getAgents().setPrototypeAgent(player);
        getAgents().setExecutionOrder(Scape.AGENT_ORDER);
        setMutationRate(0.00);
        setMutationProbability(0.01);
        setCrossoverProbability(0.01);
        setPayoff_C_C(3);
        setPayoff_C_D(-5);
        setPayoff_D_C(6);
        setPayoff_D_D(-2);
        setFissionWealth(20);
        //setDeathAge(40);
        setMemorySize(1);
        setTrackAll(0);
        ((Array2D) getLattice().getSpace()).setExtent(20, 20);
        getLattice().setCellsRequestUpdates(false);

        class ChromosomeCounter extends StatCollectorCond {

            Chromosome toMatch;

            public ChromosomeCounter(int size, int encoding) {
                toMatch = new Chromosome();
                toMatch.setSize(size);
                toMatch.setEncoding(encoding);
            }

            public String getName() {
                return toMatch.toString();
            }

            public boolean meetsCondition(Object o) {
                for (int i = 0; i < toMatch.encoding.length; i++) {
                    if (toMatch.encoding[i] != ((PlayerGA) o).getStrategies().encoding[i]) {
                        return false;
                    }
                }
                return true;
            }
        }

        //ChartView chromosomeChart = new ChartView();
        //getAgents().addView(chromosomeChart);
        for (int i = 0; i < Math.pow(2, (2 * 2 * getMemorySize())); i++) {
            ChromosomeCounter c = new ChromosomeCounter((int) Math.pow(2, (2 * getMemorySize())), i);
            getAgents().addStatCollector(c);
            //chromosomeChart.addSeries("Count " + c.getName());
        }
        final StatCollector[] stats = {
            new StatCollectorCSA("Cooperate Ratio") {
                public double getValue(Object object) {
                    return (((PlayerGA) object).getCooperateRatio());
                }
            },
            new StatCollectorCSA("Defect Ratio") {
                public double getValue(Object object) {
                    return (((PlayerGA) object).getDefectRatio());
                }
            },
            new StatCollectorCSA("Cooperate Closest") {
                public double getValue(Object object) {
                    if (((PlayerGA) object).getPreference() == PlayerGA.PREFERENCE_CLOSEST) {
                        return ((PlayerGA) object).getCooperateRatio();
                    } else {
                        return 0.0;
                    }
                }
            },
            new StatCollectorCSA("Cooperate Farthest") {
                public double getValue(Object object) {
                    if (((PlayerGA) object).getPreference() == PlayerGA.PREFERENCE_FARTHEST) {
                        return ((PlayerGA) object).getCooperateRatio();
                    } else {
                        return 0.0;
                    }
                }
            },
            new StatCollectorCSA("Defect Closest") {
                public double getValue(Object object) {
                    if (((PlayerGA) object).getPreference() == PlayerGA.PREFERENCE_CLOSEST) {
                        return 1.0 - ((PlayerGA) object).getCooperateRatio();
                    } else {
                        return 0.0;
                    }
                }
            },
            new StatCollectorCSA("Defect Farthest") {
                public double getValue(Object object) {
                    if (((PlayerGA) object).getPreference() == PlayerGA.PREFERENCE_FARTHEST) {
                        return 1.0 - ((PlayerGA) object).getCooperateRatio();
                    } else {
                        return 0.0;
                    }
                }
            }
        };
        //Add the new values stats to getAgents()
        getAgents().addStatCollectors(stats);
    }

    public void createGraphicViews() {
        //getOverheadView().setCellSize(6);
        super.createGraphicViews();

        final ColorFeatureGradiated colorAgentForCooperation =
            new ColorFeatureGradiated("Grievance", Color.blue, Color.red, new UnitIntervalDataPoint() {
                public double getValue(Object object) {
                    return ((PlayerGA) object).getCooperateRatio();
                }
            });
        getOverheadView().setHostedAgentColorFeature(colorAgentForCooperation);

        final DrawFeature drawGenome = new DrawFeature("Chromosome") {
            public void draw(Graphics g, Object o, int width, int height) {
                g.setColor(Color.LIGHT_GRAY);
                g.fillRect(0, 0, width, height);
                PlayerGA player = (PlayerGA) ((HostCell) o).getOccupant();
                if (player != null) {
                    Color genomeColor  = (Color) colorForGenome.get(player.getChromosomeStrategyAsString());
                    if (genomeColor == null) {
                        genomeColor = new Color((float) Math.random(), (float) Math.random(), (float) Math.random());
                        colorForGenome.put(player.getChromosomeStrategyAsString(), genomeColor);
                    }
                    g.setColor(genomeColor);
                    g.fillOval(0, 0, width, height);
                    g.setColor(colorAgentForCooperation.getColor(player));
                    g.drawOval(0, 0, width, height);
                }
            }
        };
        getLattice().addDrawFeature(drawGenome);

        getOverheadView().getDrawSelection().update();
        getOverheadView().getDrawSelection().clearSelection();
        getOverheadView().getDrawSelection().setSelected(drawGenome, true);

        if (!(getUserEnvironment().isInApplet())) {
            //Create a new getChart()
            getChart().clearSeries();
            //And add some of the stat series we've just created to it
            getChart().addSeries("Sum Cooperate Ratio", Color.blue);
            getChart().addSeries("Sum Defect Ratio", Color.red);
            //Set starting data point we're interested in
            getChart().setDisplayPoints(100);
        }
    }

    public int getMemorySize() {
        return memorySize;
    }

    public void setMemorySize(int memorySize) {
        this.memorySize = memorySize;
    }

    public double getMutationProbability() {
        return mutationProbability;
    }

    public void setMutationProbability(double mutationProbability) {
        this.mutationProbability = mutationProbability;
    }

    public double getCrossoverProbability() {
        return crossoverProbability;
    }

    public void setCrossoverProbability(double crossoverProbability) {
        this.crossoverProbability = crossoverProbability;
    }
}
