/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.cv;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Dictionary;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.ascape.model.Cell;
import org.ascape.model.CellOccupant;
import org.ascape.model.HostCell;
import org.ascape.model.Scape;
import org.ascape.model.event.ScapeEvent;
import org.ascape.model.space.Array2DMoore;
import org.ascape.model.space.Coordinate1DDiscrete;
import org.ascape.model.space.Coordinate2DDiscrete;
import org.ascape.model.space.Mutable;
import org.ascape.runtime.swing.DesktopEnvironment;
import org.ascape.util.data.StatCollector;
import org.ascape.util.data.StatCollectorCSA;
import org.ascape.util.data.StatCollectorCalculated;
import org.ascape.util.data.StatCollectorCond;
import org.ascape.util.data.StatCollectorCondCSA;
import org.ascape.util.data.UnitIntervalDataPoint;
import org.ascape.util.swing.CustomSliderPanel;
import org.ascape.util.vis.ColorFeature;
import org.ascape.util.vis.ColorFeatureConcrete;
import org.ascape.util.vis.ColorFeatureFixed;
import org.ascape.util.vis.ColorFeatureGradiated;
import org.ascape.util.vis.DrawFeature;
import org.ascape.view.custom.AutoCustomizerSwing;
import org.ascape.view.vis.ChartView;
import org.ascape.view.vis.ComponentView;
import org.ascape.view.vis.Overhead2DView;

public class CVModel extends Scape {

    /**
     * 
     */
    private static final long serialVersionUID = 6136885380116175919L;

    protected double initialPopulationDensity = 0.70;

    protected double initialCopDensity = 0.040;

    protected double targetCopDensity = initialCopDensity;

    protected double copVision = 1.7;//7.0;

    protected double personVision = 1.7;//7.0;

    protected double fissionProbability = .05;

    protected int deathAge = 200;

    protected double legitimacy = .80;

    private double activeThreshold = .1;

    protected int jailTerm = 15;

    private int latticeWidth = 40;

    private int latticeHeight = 40;

    protected Scape lattice;

    protected Scape people;

    //Scape prey;

    private Scape cops;

    protected ChartView chart;

    public void createScape() {
        super.createScape();

        //Create a 2D lattice (we'll be using Moore Geometry.)
        lattice = new Scape(new Array2DMoore());
        //Use the generic host cell as the agent that will fill the lattice.
        lattice.setPrototypeAgent(new HostCell());
        lattice.getRules().clear();
        lattice.setExtent(new Coordinate2DDiscrete(latticeWidth, latticeHeight));
        add(lattice);

        Citizen person = new Citizen();
        person.setHostScape(lattice);
        people = new Scape();
        people.setPrototypeAgent(person);
        add(people);
        people.addInitialRule(MOVE_RANDOM_LOCATION_RULE);
        people.addRule(RANDOM_WALK_RULE);
        people.addRule(Citizen.CHECK_JAIL);
        people.addRule(Citizen.DECIDE_STATE);

        Cop cop = new Cop();
        cop.setHostScape(lattice);
        cops = new Scape();
        cops.setPrototypeAgent(cop);
        add(cops);
        /*
         * All runs
         */
        initialPopulationDensity = 0.70;

        targetCopDensity = initialCopDensity;
    }

    private Overhead2DView agentView;
    protected ChartView predChart;

    AutoCustomizerSwing customizer;
    GridBagConstraints cgbc;
    JPanel customPanel;

    private CustomSliderPanel legitimacySlide = new CustomSliderPanel("Legitimacy", 0.0, 1.0, 2) {
        /**
         * 
         */
        private static final long serialVersionUID = -5856083478333148823L;

        public double getActualValue() {
            return getLegitimacy();
        }

        public void setActualValue(double value) {
            setLegitimacy(value);
            //We need to request an update here, so that the updates will happen live
            lattice.requestUpdate();
        }
    };
    private CustomSliderPanel copVisionSlide = new CustomSliderPanel("Cop Vision", 0.0, 16.0, 1) {
        /**
         * 
         */
        private static final long serialVersionUID = 7262707779307331658L;

        public double getActualValue() {
            return getCopVision();
        }

        public void setActualValue(double value) {
            setCopVision(value);
            lattice.requestUpdate();
        }
    };
    private CustomSliderPanel personVisionSlide = new CustomSliderPanel("Citizen Vision", 0.0, 16.0, 1) {
        /**
         * 
         */
        private static final long serialVersionUID = 4503135655636886212L;

        public double getActualValue() {
            return getCitizenVision();
        }

        public void setActualValue(double value) {
            setCitizenVision(value);
            lattice.requestUpdate();
        }
    };
    private CustomSliderPanel targetCopDensitySlide = new CustomSliderPanel("Target Cop Density", 0.0, 0.1, 3) {
        /**
         * 
         */
        private static final long serialVersionUID = -3563315742789563055L;

        public double getActualValue() {
            return getTargetCopDensity();
        }

        public void setActualValue(double value) {
            setTargetCopDensity(value);
            lattice.requestUpdate();
        }
    };
    private CustomSliderPanel jailTermSlide = new org.ascape.util.swing.CustomSliderPanelInf("Jail Term", 0.0, 1000.0, 0) {
        /**
         * 
         */
        private static final long serialVersionUID = 4472209572158945135L;

        public double getActualValue() {
            return getJailTerm();
        }

        public void setActualValue(double value) {
            setJailTerm((int) value);
            lattice.requestUpdate();
        }
    };
    private CustomSliderPanel initialPopulationSlide = new CustomSliderPanel("Initial Population Density", 0.0, 1.0, 2) {
        /**
         * 
         */
        private static final long serialVersionUID = -2246939563861488097L;

        public double getActualValue() {
            return getInitialPopulationDensity();
        }

        public void setActualValue(double value) {
            setInitialPopulationDensity(value);
            lattice.requestUpdate();
        }
    };
    private CustomSliderPanel initialCopDensitySlide = new CustomSliderPanel("Initial Cop Density", 0.0, 0.1, 3) {
        /**
         * 
         */
        private static final long serialVersionUID = -4471014243905395542L;

        public double getActualValue() {
            return getInitialCopDensity();
        }

        public void setActualValue(double value) {
            setInitialCopDensity(value);
            lattice.requestUpdate();
        }
    };

    public void createViews() {
        super.createViews();
        int viewCellSize = 9;
        /*Overhead2DView riskView = new Overhead2DView();
        riskView.setCellSize(viewCellSize);
		final ColorFeatureGradiated colorAgentForRisk =
		  new ColorFeatureGradiated("Risk", Color.yellow, new UnitIntervalDataPoint() {
            public double getValue(Object object) {
                return 1 - ((Citizen) object).getRiskAversion();
            }
		});
		final ColorFeature colorRiskIfNotCop = new ColorFeatureConcrete("Grievance") {
            public Color getColor(Object object) {
                if (object instanceof Citizen) {
                    return colorAgentForRisk.getColor(object);
                }
                else {
                    return Color.black;
                }
            }
		};
		riskView.setAgentColorFeature(colorRiskIfNotCop);
		riskView.setCellColorFeature(background);*/

        final ColorFeatureGradiated colorAgentForGrievance =
            new ColorFeatureGradiated("Grievance", Color.red, new UnitIntervalDataPoint() {
                /**
                 * 
                 */
                private static final long serialVersionUID = -5516447619379417428L;

                public double getValue(Object object) {
                    return ((Citizen) object).getGrievance();
                }
            });
        final ColorFeatureGradiated colorAgentForRiskAversion =
            new ColorFeatureGradiated("Risk Aversion", Color.yellow, new UnitIntervalDataPoint() {
                /**
                 * 
                 */
                private static final long serialVersionUID = -5957840065532306454L;

                public double getValue(Object object) {
                    return ((Citizen) object).getRiskAversion();
                }
            });
        final ColorFeature colorIfNotCop = new ColorFeatureConcrete("Grievance") {
            /**
             * 
             */
            private static final long serialVersionUID = 3131676225052217287L;

            public Color getColor(Object object) {
                if (object instanceof Citizen) {
                    return colorAgentForGrievance.getColor(object);
                } else {
                    return Color.black;
                }
            }
        };
        ColorFeatureFixed background = new ColorFeatureFixed("Brown", new Color(237, 230, 214));
        final Overhead2DView grievanceView = new Overhead2DView("Grievance View");
        final DrawFeature riskAversionFeature = new DrawFeature("Agent Risk Aversion") {
            /**
             * 
             */
            private static final long serialVersionUID = 1416871935140791570L;

            public void draw(Graphics g, Object object, int width, int height) {
                CellOccupant occupant = (CellOccupant) ((Cell) object).getOccupant();
                if (occupant instanceof Citizen) {
                    g.setColor(colorAgentForRiskAversion.getColor(occupant));
                    g.fillRect(0, 0, width, height);
                } else { // cop
                    g.setColor(Color.black);
                    g.fillRect(0, 0, width, height);
                }
            }
        };

        lattice.addDrawFeature(riskAversionFeature);

        agentView = new Overhead2DView("Agent View");
        agentView.setCellSize(viewCellSize);
        agentView.setPrimaryAgentColorFeature(background);
        grievanceView.setHostedAgentColorFeature(colorIfNotCop);
        grievanceView.setPrimaryAgentColorFeature(background);
        //riskView.getDrawSelection().setSelected(riskView.agents_fill_cells_draw_feature, true);
        ComponentView[] views = new ComponentView[2];
        views[0] = agentView;
        views[1] = grievanceView;
        //views[2] = riskView;
        lattice.addViews(views);
        //agentView.getDrawSelection().clear();
        //agentView.getDrawSelection().setSelected(agentView.agents_image_cells_draw_feature, true);
        grievanceView.getDrawSelection().clearSelection();
        grievanceView.getDrawSelection().setSelected(grievanceView.agents_fill_cells_draw_feature, true);

/*agentView.getDrawSelection().addElement(
        	new DrawFeature("Draw Target Agent") {
		        public void draw(Graphics g, Object object, int width, int height) {
		    		g.setColor(agentView.getPrimaryAgentColorFeature().getColor((Cell) object));
		    		g.fillRect(0, 0, width, height);
		            CellOccupant occupant = ((Cell) object).getOccupant();
		            if (occupant != null) {
		            	if (occupant instanceof Citizen) {
			            	if (((Citizen) occupant).getState() == Citizen.QUIESCENT) {
				            	g.setColor(((Citizen) occupant).getGroupColor());
				            	g.fillOval(1, 1, width - 2, height - 2);
				           	}
				           	else {
				            	g.setColor(Color.red);
				            	g.fillOval(0, 0, width, height);
				            	g.setColor(((Citizen) occupant).getGroupColor());
				            	g.fillOval(2, 2, width - 4, height - 4);
				           	}
			           	}
			           	else {
			           		//Should be cop
			            	g.setColor(Color.black);
			            	g.fillOval(1, 1, width - 2, height - 2);
			          	}
		            }
		        }
		  	}
	    );
	    //We decided this looked kind of ugly, though it is more explanatory
		//agentView.getDrawSelection().clearSelection();
		//agentView.getDrawSelection().setSelected("Draw Target Agent", true);*/

        final StatCollectorCond activeStat = new StatCollectorCond("Active") {
            /**
             * 
             */
            private static final long serialVersionUID = -1799133551683376351L;

            public boolean meetsCondition(Object object) {
                return (((Citizen) object).getState() == Citizen.ACTIVE);
            }
        };
        final StatCollectorCond quiescentStat = new StatCollectorCond("Quiescent") {
            /**
             * 
             */
            private static final long serialVersionUID = -1832475342086515016L;

            public boolean meetsCondition(Object object) {
                return (((Citizen) object).getState() == Citizen.QUIESCENT);
            }
        };
        final StatCollectorCond jailedStat = new StatCollectorCond("Jailed") {
            /**
             * 
             */
            private static final long serialVersionUID = -5172569367804591671L;

            public boolean meetsCondition(Object object) {
                return (((Citizen) object).getState() == Citizen.IN_JAIL);
            }
        };
        final StatCollectorCond nonJailedStat = new StatCollectorCond("Non-Jailed") {
            /**
             * 
             */
            private static final long serialVersionUID = -8649533987820545827L;

            public boolean meetsCondition(Object object) {
                return (((Citizen) object).getState() != Citizen.IN_JAIL);
            }
        };
        final StatCollectorCSA riskAversionStat = new StatCollectorCSA("Risk Aversion") {
            /**
             * 
             */
            private static final long serialVersionUID = -4383137972043954727L;

            public double getValue(Object object) {
                return ((Citizen) object).getRiskAversion();
            }
        };
        final StatCollectorCSA grievanceStat = new StatCollectorCSA("Grievance") {
            /**
             * 
             */
            private static final long serialVersionUID = 1577310168633977346L;

            public double getValue(Object object) {
                return ((Citizen) object).getGrievance();
            }
        };
        final StatCollectorCondCSA riskAversionNonJailStat = new StatCollectorCondCSA("Non-Jailed Risk Aversion") {
            /**
             * 
             */
            private static final long serialVersionUID = -3915022638682637313L;

            public double getValue(Object object) {
                return ((Citizen) object).getRiskAversion();
            }

            public boolean meetsCondition(Object object) {
                return (((Citizen) object).getState() != Citizen.IN_JAIL);
            }
        };
        final StatCollectorCondCSA grievanceNonJailStat = new StatCollectorCondCSA("Non-Jailed Grievance") {
            /**
             * 
             */
            private static final long serialVersionUID = 6981876763210293161L;

            public double getValue(Object object) {
                return ((Citizen) object).getGrievance();
            }

            public boolean meetsCondition(Object object) {
                return (((Citizen) object).getState() != Citizen.IN_JAIL);
            }
        };
        final StatCollectorCalculated activeRatioStat = new StatCollectorCalculated("Active Ratio") {
            /**
             * 
             */
            private static final long serialVersionUID = 819462462218968871L;

            public double calculateValue() {
                if (nonJailedStat.getCount() != 0) {
                    return (double) activeStat.getCount() / (double) nonJailedStat.getCount();
                } else {
                    return 0.0;
                }
            }
        };
        final StatCollectorCalculated quiescentRatioStat = new StatCollectorCalculated("Quiescent Ratio") {
            /**
             * 
             */
            private static final long serialVersionUID = -6704785089883262054L;

            public double calculateValue() {
                if (nonJailedStat.getCount() != 0) {
                    return (double) quiescentStat.getCount() / (double) nonJailedStat.getCount();
                } else {
                    return 0.0;
                }
            }
        };
        final StatCollectorCalculated tensionStat = new StatCollectorCalculated("Tension") {
            /**
             * 
             */
            private static final long serialVersionUID = 7408024115056438717L;

            public double calculateValue() {
                return (grievanceNonJailStat.getAvg() * quiescentRatioStat.getSum()) / riskAversionNonJailStat.getAvg();
            }
        };
        final StatCollectorCalculated legitimacyStat = new StatCollectorCalculated("Legitimacy") {
            /**
             * 
             */
            private static final long serialVersionUID = -3878670181590365627L;

            public double calculateValue() {
                return (legitimacy * 1000);
            }
        };
        final StatCollectorCalculated copDensityStat = new StatCollectorCalculated("Cop Density") {
            /**
             * 
             */
            private static final long serialVersionUID = 6273927700877313170L;

            public double calculateValue() {
                return (targetCopDensity * 10000);
            }
        };
        final StatCollector[] stats = {
            activeStat,
            quiescentStat,
            jailedStat,
            nonJailedStat,
            riskAversionStat,
            grievanceStat,
            riskAversionNonJailStat,
            grievanceNonJailStat,
            activeRatioStat,
            quiescentRatioStat,
            tensionStat,
            legitimacyStat,
            copDensityStat
        };
        //Add the new values stats to agents
        people.addStatCollectors(stats);

        //Create a new chart
        chart = new ChartView("Population Chart");
        //Add the chart view
        addView(chart);
        //And add some of the stat series we've just created to it
        //chart.addSeries("Count All", Color.black);
        chart.addSeries("Count Active", Color.red);
        chart.addSeries("Count Quiescent", Color.blue);
        //chart.addSeries("Count Green Quiescent", Color.green);
        //chart.addSeries("Count Blue Quiescent", Color.blue);
        chart.addSeries("Count Jailed", Color.black);
        //Set starting data point we're interested in
        //((TimeSeriesViewModel) chart.getViewModel()).setDisplayPoints(100);

        predChart = new ChartView("Measures Chart");
        //Add the chart view
        addView(predChart);
        //And add some of the stat series we've just created to it
        //chart.addSeries("Count All", Color.black);
        predChart.addSeries("Average Non-Jailed Grievance", Color.red);
        predChart.addSeries("Average Grievance", Color.orange);
        predChart.addSeries("Average Non-Jailed Risk Aversion", Color.green);
        predChart.addSeries("Average Risk Aversion", Color.cyan);
        predChart.addSeries("Sum Quiescent Ratio", Color.blue);
        predChart.addSeries("Sum Tension", Color.magenta);

        customizer = new AutoCustomizerSwing() {
            public void build() {
                super.build();
                CVModel.this.buildCustomizer();
            }
        };
        getUIEnvironment().setCustomizer(customizer);
    }

    protected void buildCustomizer() {
        customizer.setPreferredSize(new Dimension(290, 480));

        customPanel = new JPanel();
        customPanel.removeAll();
        GridBagLayout cgbl = new GridBagLayout();
        customPanel.setLayout(cgbl);
        cgbc = cgbl.getConstraints(customPanel);
        cgbc.gridwidth = GridBagConstraints.REMAINDER;
        cgbc.fill = GridBagConstraints.HORIZONTAL;
//cgbc.anchor = GridBagConstraints.WEST;
        cgbc.weightx = 1.0;
        cgbc.weighty = 1.0;
        cgbc.gridy = 0;
        cgbc.gridx = 0;
        customPanel.add(legitimacySlide, cgbc);
        legitimacySlide.build();
        cgbc.gridy++;
        customPanel.add(copVisionSlide, cgbc);
        copVisionSlide.build();
        Dictionary visionLabels = copVisionSlide.getSlider().getLabelTable();
        visionLabels.put(new Integer(10), new JLabel("v"));
        visionLabels.put(new Integer(18), new JLabel("m"));
        copVisionSlide.getSlider().setLabelTable(visionLabels);
        cgbc.gridy++;
        customPanel.add(personVisionSlide, cgbc);
        personVisionSlide.build();
        personVisionSlide.getSlider().setLabelTable(visionLabels);
        cgbc.gridy++;
        customPanel.add(targetCopDensitySlide, cgbc);
        targetCopDensitySlide.build();
        cgbc.gridy++;
        customPanel.add(jailTermSlide, cgbc);
        jailTermSlide.build();
        cgbc.gridy++;
        customPanel.add(initialPopulationSlide, cgbc);
        initialPopulationSlide.build();
        cgbc.gridy++;
        customPanel.add(initialCopDensitySlide, cgbc);
        initialCopDensitySlide.build();
        customizer.getTabPane().addTab("Sliders", DesktopEnvironment.getIcon("FingerUp"), customPanel, "Change Values Using Sliders");
        customizer.getTabPane().setSelectedComponent(customPanel);
    }

    private int calculateTargetCopCount() {
        return (int) (targetCopDensity * lattice.getSize());
    }

    private void matchTargetCopDensity() {
        //We implement so that the algortihm responds correctly if user changes target while we are trying to match it
        while (cops.getSize() != calculateTargetCopCount()) {
            if (cops.getSize() < calculateTargetCopCount()) {
                cops.newAgent();
            }
            if (cops.getSize() > calculateTargetCopCount()) {
                cops.findRandom().die();
            }
        }
        ((Mutable) cops.getSpace()).deleteSweep();
    }

    public void scapeIterated(ScapeEvent event) {
        matchTargetCopDensity();
        super.scapeIterated(event);
    }

    public void scapeSetup(ScapeEvent scapeEvent) {
        people.setExtent(new Coordinate1DDiscrete((int) (lattice.getSize() * initialPopulationDensity)));
        cops.setExtent(new Coordinate1DDiscrete((int) (lattice.getSize() * initialCopDensity)));
        targetCopDensity = initialCopDensity;
        //lattice.setExtent(new Coordinate2DDiscrete(latticeWidth, latticeHeight));
    }

    public double getFissionProbability() {
        return fissionProbability;
    }

    public void setFissionProbability(double fissionProbability) {
        this.fissionProbability = fissionProbability;
    }

    public int getDeathAge() {
        return deathAge;
    }

    public void setDeathAge(int _deathAge) {
        deathAge = _deathAge;
    }

    public double getLegitimacy() {
        return legitimacy;
    }

    public void setLegitimacy(double legitimacy) {
        this.legitimacy = legitimacy;
        if (legitimacySlide != null) {
            legitimacySlide.valueUpdated();
        }
    }

    public double getActiveThreshold() {
        return activeThreshold;
    }

    public void setActiveThreshold(double activeThreshold) {
        this.activeThreshold = activeThreshold;
    }

    public double getInitialPopulationDensity() {
        return initialPopulationDensity;
    }

    public void setInitialPopulationDensity(double initialPopulationDensity) {
        if (getInitialCopDensity() + initialPopulationDensity > 1.0) {
            setInitialCopDensity(1.0 - initialPopulationDensity);
        }
        this.initialPopulationDensity = initialPopulationDensity;
        if (initialPopulationSlide != null) {
            initialPopulationSlide.valueUpdated();
        }
    }

    public double getInitialCopDensity() {
        return initialCopDensity;
    }

    public void setInitialCopDensity(double initialCopDensity) {
        if (getInitialPopulationDensity() + initialCopDensity > 1.0) {
            setInitialPopulationDensity(1.0 - initialCopDensity);
        }
        this.initialCopDensity = initialCopDensity;
        if (initialCopDensitySlide != null) {
            initialCopDensitySlide.valueUpdated();
        }
    }

    public double getTargetCopDensity() {
        return targetCopDensity;
    }

    public void setTargetCopDensity(double targetCopDensity) {
        this.targetCopDensity = targetCopDensity;
        this.initialCopDensity = targetCopDensity;
        if (initialCopDensitySlide != null) {
            initialCopDensitySlide.valueUpdated();
            targetCopDensitySlide.valueUpdated();
        }
    }

    public double getCopVision() {
        return copVision;
    }

    public void setCopVision(double copVision) {
        this.copVision = copVision;
        if (copVisionSlide != null) {
            copVisionSlide.valueUpdated();
        }
    }

    public double getCitizenVision() {
        return personVision;
    }

    public void setCitizenVision(double personVision) {
        this.personVision = personVision;
        if (personVisionSlide != null) {
            personVisionSlide.valueUpdated();
        }
    }

    public int getJailTerm() {
        return jailTerm;
    }

    public void setJailTerm(int jailTerm) {
        this.jailTerm = jailTerm;
        if (jailTermSlide != null) {
            jailTermSlide.valueUpdated();
        }
    }
}
