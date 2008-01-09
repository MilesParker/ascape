/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.cv;

import java.awt.Color;
import java.awt.Dimension;

import org.ascape.model.event.ScapeEvent;
import org.ascape.util.data.StatCollector;
import org.ascape.util.data.StatCollectorCalculated;
import org.ascape.util.data.StatCollectorCond;
import org.ascape.util.data.StatCollectorCondCSA;
import org.ascape.util.swing.CustomSliderPanel;


public class CVModelInterGroup extends CVModel {

    private float greenLegitimacy;

    private float blueLegitimacy;

    private float greenDistrust;

    private float blueDistrust;

    private float initialBlueLegitimacy;

    private float initialGreenLegitimacy;

    private float evaporation = 1.0f;

    public void createScape() {
        super.createScape();
        Citizen person = new CitizenBlueGreen();
        person.setHostScape(lattice);
        people.setPrototypeAgent(person);
        //people.addRule(CitizenBlueGreen.DECIDE_STATE_RETRIBUTION, false);
        //people.addRule(CitizenBlueGreen.DECIDE_STATE_RETRIBUTION_2, false);
        //people.addRule(CitizenBlueGreen.DECIDE_STATE_RETRIBUTION_3, false);
        people.addRule(FISSIONING_RULE);
        people.addRule(METABOLISM_RULE);
        people.addRule(DEATH_RULE);
        people.addRule(UPDATE_RULE);
        people.addRule(CitizenBlueGreen.ATTACK_RETRIBUTION_RULE);
        //people.addRule(CitizenBlueGreen.ATTACK_RETALIATION_RULE);
        /*
         * Model II, runs
         */
        fissionProbability = .05;
        deathAge = 200;
        jailTerm = 15;
        copVision = 1.7;
        personVision = 1.7;
        /*
         * Model II, run 1
         */
        //initialCopDensity = .0;
        //legitimacy = .90;
        /*
         * Model II, run 2, 3
         */
        initialCopDensity = 0.0f;
        initialBlueLegitimacy = 0.2f;
        initialGreenLegitimacy = 0.2f;

        evaporation = .9f;
        //legitimacy = .80;
        /*
         * Model II, run 4a, 4b
         */
        //initialCopDensity = .04;
        //setRandomSeed(941562289135L);
        //Visitor's suggestion..
        final StatCollectorCond blueStat = new StatCollectorCond("Blue") {
            public boolean meetsCondition(Object object) {
                return (((CitizenBlueGreen) object).group == CitizenBlueGreen.BLUE);
            }
        };
        final StatCollectorCond blueQuiescentStat = new StatCollectorCond("Blue Quiescent") {
            public boolean meetsCondition(Object object) {
                return ((((CitizenBlueGreen) object).group == CitizenBlueGreen.BLUE) && ((CitizenBlueGreen) object).getState() == CitizenBlueGreen.QUIESCENT);
            }
        };
        final StatCollectorCond blueActiveStat = new StatCollectorCond("Blue Active") {
            public boolean meetsCondition(Object object) {
                return ((((CitizenBlueGreen) object).group == CitizenBlueGreen.BLUE) && ((Citizen) object).getState() == Citizen.ACTIVE);
            }
        };
        final StatCollectorCond blueJailedStat = new StatCollectorCond("Blue Jailed") {
            public boolean meetsCondition(Object object) {
                return ((((CitizenBlueGreen) object).group == CitizenBlueGreen.BLUE) && ((Citizen) object).getState() == Citizen.IN_JAIL);
            }
        };
        final StatCollectorCond blueNonJailedStat = new StatCollectorCond("Blue Non-Jailed") {
            public boolean meetsCondition(Object object) {
                return ((((CitizenBlueGreen) object).group == CitizenBlueGreen.BLUE) && ((Citizen) object).getState() != Citizen.IN_JAIL);
            }
        };
        final StatCollectorCondCSA blueRiskAversionStat = new StatCollectorCondCSA("Blue Non-Jailed Risk Aversion") {
            public double getValue(Object object) {
                return ((Citizen) object).getRiskAversion();
            }

            public boolean meetsCondition(Object object) {
                return ((((CitizenBlueGreen) object).group == CitizenBlueGreen.BLUE) && ((Citizen) object).getState() != Citizen.IN_JAIL);
            }
        };
        final StatCollectorCondCSA blueGrievanceStat = new StatCollectorCondCSA("Blue Non-Jailed Grievance") {
            public double getValue(Object object) {
                return ((Citizen) object).getGrievance();
            }

            public boolean meetsCondition(Object object) {
                return ((((CitizenBlueGreen) object).group == CitizenBlueGreen.BLUE) && ((Citizen) object).getState() != Citizen.IN_JAIL);
            }
        };
        final StatCollectorCalculated blueActiveRatioStat = new StatCollectorCalculated("Blue Active Ratio") {
            public double calculateValue() {
                if (blueNonJailedStat.getCount() != 0) {
                    return (float) blueActiveStat.getCount() / (float) blueNonJailedStat.getCount();
                } else {
                    return 0.0;
                }
            }
        };
        final StatCollectorCalculated blueQuiescentRatioStat = new StatCollectorCalculated("Blue Quiescent Ratio") {
            public double calculateValue() {
                if (blueNonJailedStat.getCount() != 0) {
                    return (float) blueQuiescentStat.getCount() / (float) blueNonJailedStat.getCount();
                } else {
                    return 0.0;
                }
            }
        };
        final StatCollectorCalculated blueTensionStat = new StatCollectorCalculated("Blue Tension") {
            public double calculateValue() {
                return (float) blueGrievanceStat.getAvg() / (1 - blueQuiescentRatioStat.getSum() * blueRiskAversionStat.getAvg());
            }
        };
        final StatCollectorCond greenStat = new StatCollectorCond("Green") {
            public boolean meetsCondition(Object object) {
                return (((CitizenBlueGreen) object).group == CitizenBlueGreen.GREEN);
            }
        };
        final StatCollectorCond greenQuiescentStat = new StatCollectorCond("Green Quiescent") {
            public boolean meetsCondition(Object object) {
                return ((((CitizenBlueGreen) object).group == CitizenBlueGreen.GREEN) && ((CitizenBlueGreen) object).getState() == CitizenBlueGreen.QUIESCENT);
            }
        };
        final StatCollectorCond greenActiveStat = new StatCollectorCond("Green Active") {
            public boolean meetsCondition(Object object) {
                return ((((CitizenBlueGreen) object).group == CitizenBlueGreen.GREEN) && ((Citizen) object).getState() == Citizen.ACTIVE);
            }
        };
        final StatCollectorCond greenJailedStat = new StatCollectorCond("Green Jailed") {
            public boolean meetsCondition(Object object) {
                return ((((CitizenBlueGreen) object).group == CitizenBlueGreen.GREEN) && ((Citizen) object).getState() == Citizen.IN_JAIL);
            }
        };
        final StatCollectorCond greenNonJailedStat = new StatCollectorCond("Green Non-Jailed") {
            public boolean meetsCondition(Object object) {
                return ((((CitizenBlueGreen) object).group == CitizenBlueGreen.GREEN) && ((Citizen) object).getState() != Citizen.IN_JAIL);
            }
        };
        final StatCollectorCondCSA greenRiskAversionStat = new StatCollectorCondCSA("Green Non-Jailed Risk Aversion") {
            public double getValue(Object object) {
                return ((Citizen) object).getRiskAversion();
            }

            public boolean meetsCondition(Object object) {
                return ((((CitizenBlueGreen) object).group == CitizenBlueGreen.GREEN) && ((Citizen) object).getState() != Citizen.IN_JAIL);
            }
        };
        final StatCollectorCondCSA greenGrievanceStat = new StatCollectorCondCSA("Green Non-Jailed Grievance") {
            public double getValue(Object object) {
                return ((Citizen) object).getGrievance();
            }

            public boolean meetsCondition(Object object) {
                return ((((CitizenBlueGreen) object).group == CitizenBlueGreen.GREEN) && ((Citizen) object).getState() != Citizen.IN_JAIL);
            }
        };
        final StatCollectorCalculated greenActiveRatioStat = new StatCollectorCalculated("Green Active Ratio") {
            public double calculateValue() {
                if (greenNonJailedStat.getCount() != 0) {
                    return (float) greenActiveStat.getCount() / (float) greenNonJailedStat.getCount();
                } else {
                    return 0.0;
                }
            }
        };
        final StatCollectorCalculated greenQuiescentRatioStat = new StatCollectorCalculated("Green Quiescent Ratio") {
            public double calculateValue() {
                if (greenNonJailedStat.getCount() != 0) {
                    return (float) greenQuiescentStat.getCount() / (float) greenNonJailedStat.getCount();
                } else {
                    return 0.0;
                }
            }
        };
        final StatCollectorCalculated greenTensionStat = new StatCollectorCalculated("Green Tension") {
            public double calculateValue() {
                return (float) greenGrievanceStat.getAvg() / (1 - greenQuiescentRatioStat.getSum() * greenRiskAversionStat.getAvg());
            }
        };
        final StatCollector greensKillBluesStat = new StatCollector("Greens Killed By Blue", false);
        final StatCollector bluesKillGreensStat = new StatCollector("Blues Killed By Green", false);
        final StatCollectorCalculated calculateLegitimacy = new StatCollectorCalculated("[Calculate Legitimacy]") {
            public double calculateValue() {
                if (getPeriod() > 1) {
                    if (greenStat.getCount() != 0) {
                        greenDistrust = ((float) bluesKillGreensStat.getCount() / (float) blueStat.getCount()) + evaporation * greenDistrust;
                    } else {
                        //avoid divide by zero
                        greenDistrust = evaporation * greenDistrust;
                    }
                    greenDistrust = Math.min(1.0f, greenDistrust);
                    greenLegitimacy = 1.0f - greenDistrust;
                    if (blueStat.getCount() != 0) {
                        blueDistrust = ((float) greensKillBluesStat.getCount() / (float) greenStat.getCount()) + evaporation * blueDistrust;
                    } else {
                        //avoid divide by zero
                        blueDistrust = evaporation * blueDistrust;
                    }
                    blueDistrust = Math.min(1.0f, blueDistrust);
                    blueLegitimacy = 1.0f - blueDistrust;
                }
                return 0.0;
            }
        };
        final StatCollectorCalculated blueLegitimacyStat = new StatCollectorCalculated("Blue Legitimacy") {
            public double calculateValue() {
                return blueLegitimacy;
            }
        };
        final StatCollectorCalculated greenLegitimacyStat = new StatCollectorCalculated("Green Legitimacy") {
            public double calculateValue() {
                return greenLegitimacy;
            }
        };
        final StatCollector[] stats = {
            blueStat,
            blueQuiescentStat,
            blueRiskAversionStat,
            blueGrievanceStat,
            blueRiskAversionStat,
            blueGrievanceStat,
            blueActiveRatioStat,
            blueQuiescentRatioStat,
            blueTensionStat,
            greenStat,
            greenQuiescentStat,
            greenRiskAversionStat,
            greenGrievanceStat,
            greenRiskAversionStat,
            greenGrievanceStat,
            greenActiveRatioStat,
            greenQuiescentRatioStat,
            greenTensionStat,
            greensKillBluesStat,
            bluesKillGreensStat,
            calculateLegitimacy,
            greenLegitimacyStat,
            blueLegitimacyStat
        };
        //Add the new values stats to agents
        people.addStatCollectors(stats);
    }

    public void scapeSetup(ScapeEvent scapeEvent) {
        super.scapeSetup(scapeEvent);
        blueDistrust = 0.0f;
        greenDistrust = 0.0f;
        blueLegitimacy = initialBlueLegitimacy;
        greenLegitimacy = initialGreenLegitimacy;
    }

    public void createViews() {
/*people.addView(new NonGraphicView() {
            public void updateScapeGraphics() {
                System.out.println(scape.getPeriod());
            }
        });*/
        super.createViews();

        //agentView.getDrawSelection().clearSelection();
        //agentView.getDrawSelection().select("Draw Target Agent");

        chart.removeSeries("Count Quiescent");
        chart.addSeries("Count Blue", Color.blue);
        chart.addSeries("Count Green", Color.green);
        chart.addSeries("Count Jailed", Color.black);

        predChart.clearSeries();
        //predChart.addSeries("Sum Blue Tension", Color.blue.brighter());
        //predChart.addSeries("Sum Green Tension", Color.green.brighter());
        predChart.addSeries("Sum Blue Legitimacy", Color.blue.brighter());
        predChart.addSeries("Sum Green Legitimacy", Color.green.brighter());
    }

    private CustomSliderPanel initialBlueLegitimacySlide = new CustomSliderPanel("Initial Blue Legitimacy", 0.0, 1.0, 2) {
        public double getActualValue() {
            return getInitialBlueLegitimacy();
        }

        public void setActualValue(double value) {
            setInitialBlueLegitimacy((float) value);
            //We need to request an update here, so that the updates will happen live
            lattice.requestUpdate();
        }
    };

    private CustomSliderPanel initialGreenLegitimacySlide = new CustomSliderPanel("Intiial Green Legitimacy", 0.0, 1.0, 2) {
        public double getActualValue() {
            return getInitialGreenLegitimacy();
        }

        public void setActualValue(double value) {
            setInitialGreenLegitimacy((float) value);
            //We need to request an update here, so that the updates will happen live
            lattice.requestUpdate();
        }
    };

    private CustomSliderPanel evaporationSlide = new CustomSliderPanel("Evaporation of History", 0.0, 1.0, 2) {
        public double getActualValue() {
            return (double) evaporation;
        }

        public void setActualValue(double value) {
            evaporation = (float) value;
            //We need to request an update here, so that the updates will happen live
            lattice.requestUpdate();
        }
    };

    public void buildCustomizer() {
        super.buildCustomizer();
        customizer.setPreferredSize(new Dimension(290, 600));
        cgbc.gridy++;
        customPanel.add(initialBlueLegitimacySlide, cgbc);
        initialBlueLegitimacySlide.build();
        cgbc.gridy++;
        customPanel.add(initialGreenLegitimacySlide, cgbc);
        initialGreenLegitimacySlide.build();
        cgbc.gridy++;
        customPanel.add(evaporationSlide, cgbc);
        evaporationSlide.build();
    }

    public float getGreenLegitimacy() {
        return greenLegitimacy;
    }

    public void setGreenLegitimacy(float greenLegitimacy) {
        this.greenLegitimacy = greenLegitimacy;
    }

    public float getBlueLegitimacy() {
        return blueLegitimacy;
    }

    public void setBlueLegitimacy(float blueLegitimacy) {
        this.blueLegitimacy = blueLegitimacy;
    }

    public float getInitialBlueLegitimacy() {
        return initialBlueLegitimacy;
    }

    public void setInitialBlueLegitimacy(float initialBlueLegitimacy) {
        this.initialBlueLegitimacy = initialBlueLegitimacy;
    }

    public float getInitialGreenLegitimacy() {
        return initialGreenLegitimacy;
    }

    public void setInitialGreenLegitimacy(float initialGreenLegitimacy) {
        this.initialGreenLegitimacy = initialGreenLegitimacy;
    }
}
