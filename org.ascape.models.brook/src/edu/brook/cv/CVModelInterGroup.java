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

    /**
     * 
     */
    private static final long serialVersionUID = -4613947171716443781L;

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
            /**
             * 
             */
            private static final long serialVersionUID = 7888374739179927241L;

            public boolean meetsCondition(Object object) {
                return (((CitizenBlueGreen) object).group == CitizenBlueGreen.BLUE);
            }
        };
        final StatCollectorCond blueQuiescentStat = new StatCollectorCond("Blue Quiescent") {
            /**
             * 
             */
            private static final long serialVersionUID = 3364674504003591650L;

            public boolean meetsCondition(Object object) {
                return ((((CitizenBlueGreen) object).group == CitizenBlueGreen.BLUE) && ((CitizenBlueGreen) object).getState() == CitizenBlueGreen.QUIESCENT);
            }
        };
        final StatCollectorCond blueActiveStat = new StatCollectorCond("Blue Active") {
            /**
             * 
             */
            private static final long serialVersionUID = -7845639503004273737L;

            public boolean meetsCondition(Object object) {
                return ((((CitizenBlueGreen) object).group == CitizenBlueGreen.BLUE) && ((Citizen) object).getState() == Citizen.ACTIVE);
            }
        };
//        final StatCollectorCond blueJailedStat = new StatCollectorCond("Blue Jailed") {
//            /**
//             * 
//             */
//            private static final long serialVersionUID = -667286813613366730L;
//
//            public boolean meetsCondition(Object object) {
//                return ((((CitizenBlueGreen) object).group == CitizenBlueGreen.BLUE) && ((Citizen) object).getState() == Citizen.IN_JAIL);
//            }
//        };
        final StatCollectorCond blueNonJailedStat = new StatCollectorCond("Blue Non-Jailed") {
            /**
             * 
             */
            private static final long serialVersionUID = 3781427193621644681L;

            public boolean meetsCondition(Object object) {
                return ((((CitizenBlueGreen) object).group == CitizenBlueGreen.BLUE) && ((Citizen) object).getState() != Citizen.IN_JAIL);
            }
        };
        final StatCollectorCondCSA blueRiskAversionStat = new StatCollectorCondCSA("Blue Non-Jailed Risk Aversion") {
            /**
             * 
             */
            private static final long serialVersionUID = 3103971942527087391L;

            public double getValue(Object object) {
                return ((Citizen) object).getRiskAversion();
            }

            public boolean meetsCondition(Object object) {
                return ((((CitizenBlueGreen) object).group == CitizenBlueGreen.BLUE) && ((Citizen) object).getState() != Citizen.IN_JAIL);
            }
        };
        final StatCollectorCondCSA blueGrievanceStat = new StatCollectorCondCSA("Blue Non-Jailed Grievance") {
            /**
             * 
             */
            private static final long serialVersionUID = 8521355647391359517L;

            public double getValue(Object object) {
                return ((Citizen) object).getGrievance();
            }

            public boolean meetsCondition(Object object) {
                return ((((CitizenBlueGreen) object).group == CitizenBlueGreen.BLUE) && ((Citizen) object).getState() != Citizen.IN_JAIL);
            }
        };
        final StatCollectorCalculated blueActiveRatioStat = new StatCollectorCalculated("Blue Active Ratio") {
            /**
             * 
             */
            private static final long serialVersionUID = -5375130605901629843L;

            public double calculateValue() {
                if (blueNonJailedStat.getCount() != 0) {
                    return (float) blueActiveStat.getCount() / (float) blueNonJailedStat.getCount();
                } else {
                    return 0.0;
                }
            }
        };
        final StatCollectorCalculated blueQuiescentRatioStat = new StatCollectorCalculated("Blue Quiescent Ratio") {
            /**
             * 
             */
            private static final long serialVersionUID = 9082276914534966534L;

            public double calculateValue() {
                if (blueNonJailedStat.getCount() != 0) {
                    return (float) blueQuiescentStat.getCount() / (float) blueNonJailedStat.getCount();
                } else {
                    return 0.0;
                }
            }
        };
        final StatCollectorCalculated blueTensionStat = new StatCollectorCalculated("Blue Tension") {
            /**
             * 
             */
            private static final long serialVersionUID = -3766099475025282839L;

            public double calculateValue() {
                return (float) blueGrievanceStat.getAvg() / (1 - blueQuiescentRatioStat.getSum() * blueRiskAversionStat.getAvg());
            }
        };
        final StatCollectorCond greenStat = new StatCollectorCond("Green") {
            /**
             * 
             */
            private static final long serialVersionUID = -7988130264310952707L;

            public boolean meetsCondition(Object object) {
                return (((CitizenBlueGreen) object).group == CitizenBlueGreen.GREEN);
            }
        };
        final StatCollectorCond greenQuiescentStat = new StatCollectorCond("Green Quiescent") {
            /**
             * 
             */
            private static final long serialVersionUID = 7892509905444120172L;

            public boolean meetsCondition(Object object) {
                return ((((CitizenBlueGreen) object).group == CitizenBlueGreen.GREEN) && ((CitizenBlueGreen) object).getState() == CitizenBlueGreen.QUIESCENT);
            }
        };
        final StatCollectorCond greenActiveStat = new StatCollectorCond("Green Active") {
            /**
             * 
             */
            private static final long serialVersionUID = 7683303350094163983L;

            public boolean meetsCondition(Object object) {
                return ((((CitizenBlueGreen) object).group == CitizenBlueGreen.GREEN) && ((Citizen) object).getState() == Citizen.ACTIVE);
            }
        };
//        final StatCollectorCond greenJailedStat = new StatCollectorCond("Green Jailed") {
//            /**
//             * 
//             */
//            private static final long serialVersionUID = -1120820648396968268L;
//
//            public boolean meetsCondition(Object object) {
//                return ((((CitizenBlueGreen) object).group == CitizenBlueGreen.GREEN) && ((Citizen) object).getState() == Citizen.IN_JAIL);
//            }
//        };
        final StatCollectorCond greenNonJailedStat = new StatCollectorCond("Green Non-Jailed") {
            /**
             * 
             */
            private static final long serialVersionUID = 7142663382880228863L;

            public boolean meetsCondition(Object object) {
                return ((((CitizenBlueGreen) object).group == CitizenBlueGreen.GREEN) && ((Citizen) object).getState() != Citizen.IN_JAIL);
            }
        };
        final StatCollectorCondCSA greenRiskAversionStat = new StatCollectorCondCSA("Green Non-Jailed Risk Aversion") {
            /**
             * 
             */
            private static final long serialVersionUID = -4970671627151480208L;

            public double getValue(Object object) {
                return ((Citizen) object).getRiskAversion();
            }

            public boolean meetsCondition(Object object) {
                return ((((CitizenBlueGreen) object).group == CitizenBlueGreen.GREEN) && ((Citizen) object).getState() != Citizen.IN_JAIL);
            }
        };
        final StatCollectorCondCSA greenGrievanceStat = new StatCollectorCondCSA("Green Non-Jailed Grievance") {
            /**
             * 
             */
            private static final long serialVersionUID = -8923200124907479565L;

            public double getValue(Object object) {
                return ((Citizen) object).getGrievance();
            }

            public boolean meetsCondition(Object object) {
                return ((((CitizenBlueGreen) object).group == CitizenBlueGreen.GREEN) && ((Citizen) object).getState() != Citizen.IN_JAIL);
            }
        };
        final StatCollectorCalculated greenActiveRatioStat = new StatCollectorCalculated("Green Active Ratio") {
            /**
             * 
             */
            private static final long serialVersionUID = 6036068897288518223L;

            public double calculateValue() {
                if (greenNonJailedStat.getCount() != 0) {
                    return (float) greenActiveStat.getCount() / (float) greenNonJailedStat.getCount();
                } else {
                    return 0.0;
                }
            }
        };
        final StatCollectorCalculated greenQuiescentRatioStat = new StatCollectorCalculated("Green Quiescent Ratio") {
            /**
             * 
             */
            private static final long serialVersionUID = -6290355912284306442L;

            public double calculateValue() {
                if (greenNonJailedStat.getCount() != 0) {
                    return (float) greenQuiescentStat.getCount() / (float) greenNonJailedStat.getCount();
                } else {
                    return 0.0;
                }
            }
        };
        final StatCollectorCalculated greenTensionStat = new StatCollectorCalculated("Green Tension") {
            /**
             * 
             */
            private static final long serialVersionUID = -5419065143084068964L;

            public double calculateValue() {
                return (float) greenGrievanceStat.getAvg() / (1 - greenQuiescentRatioStat.getSum() * greenRiskAversionStat.getAvg());
            }
        };
        final StatCollector greensKillBluesStat = new StatCollector("Greens Killed By Blue", false);
        final StatCollector bluesKillGreensStat = new StatCollector("Blues Killed By Green", false);
        final StatCollectorCalculated calculateLegitimacy = new StatCollectorCalculated("[Calculate Legitimacy]") {
            /**
             * 
             */
            private static final long serialVersionUID = -4044718344856318486L;

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
            /**
             * 
             */
            private static final long serialVersionUID = 7308611012432757221L;

            public double calculateValue() {
                return blueLegitimacy;
            }
        };
        final StatCollectorCalculated greenLegitimacyStat = new StatCollectorCalculated("Green Legitimacy") {
            /**
             * 
             */
            private static final long serialVersionUID = -6291388430081544624L;

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
        /**
         * 
         */
        private static final long serialVersionUID = -6859661418091949959L;

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
        /**
         * 
         */
        private static final long serialVersionUID = -556103970425376428L;

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
        /**
         * 
         */
        private static final long serialVersionUID = 8535825148500574319L;

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
