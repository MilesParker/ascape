/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */



package org.ascape.examples.bionland;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.util.Iterator;

import org.ascape.model.Agent;
import org.ascape.model.Cell;
import org.ascape.model.CellOccupant;
import org.ascape.model.HostCell;
import org.ascape.model.LocatedAgent;
import org.ascape.model.Scape;
import org.ascape.model.rule.Rule;
import org.ascape.model.space.Graph;
import org.ascape.util.Conditional;
import org.ascape.util.vis.DefaultRelationFeature;
import org.ascape.util.vis.ImageFeatureFixed;
import org.ascape.view.vis.erv.DefaultEntityFeature;
import org.ascape.view.vis.erv.EntityRelationView;

public class Model_R extends Scape {

    /**
     * 
     */
    private static final long serialVersionUID = -4983941083287303944L;

    private Scape graph;

    private Scape redBions;

    private Scape orangeBions;

    private Scape blueBions;

    private int initialScapeSize = 5;

    private int initialAgentSize = 15;

    private int minVision = 5;

    private int maxVision = 10;

    private EntityRelationView view;

    private int graphSize = 50;

    private boolean directedGraph = true;

    private double probBuildLink = 0.1;

    private double probChangeNeighbors = 0.01;

    private double edgeThickness = 0.10;

    private boolean showEdges = true;

    class MoveTowardConditionRule extends Rule {

        /**
         * 
         */
        private static final long serialVersionUID = -1247397306235344839L;
        String name;
        Conditional condition;

        public MoveTowardConditionRule(String name, Conditional condition) {
            super(name);
            this.name = name;
            this.condition = condition;
        }

        public void execute(Agent a) {
            Bion b = (Bion) a;
            HostCell target = (HostCell) b.getHostCell().findNearest(condition, false, b.getVision());
            if (target != null) {
                b.moveToward(target);
            }
        }
    };

    class MoveAwayConditionRule extends MoveTowardConditionRule {

        /**
         * 
         */
        private static final long serialVersionUID = 4020131205086266317L;

        public MoveAwayConditionRule(String name, Conditional condition) {
            super(name, condition);
        }

        public void execute(Agent a) {
            Bion b = (Bion) a;
            HostCell target = (HostCell) b.getHostCell().findNearest(condition, false, b.getVision());
            if (target != null) {
                b.moveAway(target);
            }
        }
    };

    public static Conditional CONTAINS_RED = new Conditional() {
        /**
         * 
         */
        private static final long serialVersionUID = -8647013045934251232L;

        public boolean meetsCondition(Object o) {
            return (((HostCell) o).getOccupant() instanceof RedBion);
        }
    };

    public static Conditional CONTAINS_ORANGE = new Conditional() {
        /**
         * 
         */
        private static final long serialVersionUID = -6442279018980024253L;

        public boolean meetsCondition(Object o) {
            return (((HostCell) o).getOccupant() instanceof OrangeBion);
        }
    };

    public static Conditional CONTAINS_BLUE = new Conditional() {
        /**
         * 
         */
        private static final long serialVersionUID = 711142646734494016L;

        public boolean meetsCondition(Object o) {
            return (((HostCell) o).getOccupant() instanceof BlueBion);
        }
    };

    public final Rule MOVE_TOWARD_RED_RULE = new MoveTowardConditionRule("Move Toward Red", CONTAINS_RED);

    public final Rule MOVE_TOWARD_ORANGE_RULE = new MoveTowardConditionRule("Move Toward Orange", CONTAINS_ORANGE);

    public final Rule MOVE_TOWARD_BLUE_RULE = new MoveTowardConditionRule("Move Toward Blue", CONTAINS_BLUE);

    public final Rule MOVE_AWAY_RED_RULE = new MoveAwayConditionRule("Move Away Red", CONTAINS_RED);

    public final Rule MOVE_AWAY_ORANGE_RULE = new MoveAwayConditionRule("Move Away Orange", CONTAINS_ORANGE);

    public final Rule MOVE_AWAY_BLUE_RULE = new MoveAwayConditionRule("Move Away Blue", CONTAINS_BLUE);

    public final static Rule ASSIGN_RANDOM_NEIGHBORS_RULE = new Rule("Assign Neighbors Randomly") {
        /**
         * 
         */
        private static final long serialVersionUID = -8557509988712583473L;

        public void execute(Agent a) {
            final Cell node = (Cell) a;
            Graph graph = (Graph) ((Model_R) a.getRoot()).getGraph().getSpace();
            for (Iterator agentsIt = graph.iterator(); agentsIt.hasNext();) {
                Cell neighbor = (Cell) agentsIt.next();
                if (a.getRandom().nextDouble() < ((Model_R) a.getRoot()).getProbBuildLink()
                    && neighbor != node) {
                    if (((Model_R) a.getRoot()).isDirectedGraph()) {
                        graph.addNeighbor(node, neighbor);
                    } else {
                        graph.addNeighbor(node, neighbor, false);
                    }
                }
            }
        }
    };

    public final static Rule CHANGE_NEIGHBORS_RULE = new Rule("Change Neighbors") {
        /**
         * 
         */
        private static final long serialVersionUID = 3651969577083618074L;

        public void execute(Agent a) {
            final Cell node = (Cell) a;
            Model_R model = (Model_R) a.getRoot();
            if (a.getRandom().nextDouble() < model.getProbChangeNeighbors()) {
                Graph graph = (Graph) model.getGraph().getSpace();
                graph.getNeighborsFor(node).clear();
                for (Iterator agentsIt = graph.iterator(); agentsIt.hasNext();) {
                    Cell neighbor = (Cell) agentsIt.next();
                    if (a.getRandom().nextDouble() <= model.getProbBuildLink()
                        && neighbor != node) {
                        if (model.isDirectedGraph()) {
                            graph.addNeighbor(node, neighbor);
                        } else {
                            graph.addNeighbor(node, neighbor, false);
                        }
                    }
                }
            }
        }
    };

    public void createScape() {
        super.createScape();

        graph = new Scape(new Graph());
        graph.setName("Graph");
        graph.setPrototypeAgent(new HostCell());
        graph.addInitialRule(ASSIGN_RANDOM_NEIGHBORS_RULE);
        graph.addRule(CHANGE_NEIGHBORS_RULE);
        graph.setExecutionOrder(RULE_ORDER);
        add(graph);

        redBions = new Scape();
        createBions(redBions, new RedBion(), "Red");
        redBions.getRules().setSelected(MOVE_TOWARD_ORANGE_RULE, true);
        redBions.getRules().setSelected(MOVE_AWAY_BLUE_RULE, true);

        orangeBions = new Scape();
        createBions(orangeBions, new OrangeBion(), "Orange");
        orangeBions.getRules().setSelected(MOVE_TOWARD_BLUE_RULE, true);
        orangeBions.getRules().setSelected(MOVE_AWAY_RED_RULE, true);

        blueBions = new Scape();
        createBions(blueBions, new BlueBion(), "Blue");
        blueBions.getRules().setSelected(MOVE_TOWARD_RED_RULE, true);
        blueBions.getRules().setSelected(MOVE_AWAY_ORANGE_RULE, true);
    }

    public void initialize() {
        super.initialize();
        graph.setSize(graphSize);
        redBions.setSize(initialScapeSize);
        orangeBions.setSize(initialScapeSize);
        blueBions.setSize(initialScapeSize);
    }

    protected void createBions(Scape bions, CellOccupant protoAgent, String name) {
        bions.setName(name);
        protoAgent.setHostScape(graph);
        bions.setPrototypeAgent(protoAgent);
        bions.addInitialRule(MOVE_RANDOM_LOCATION_RULE);
        bions.addRule(MOVE_TOWARD_RED_RULE, false);
        bions.addRule(MOVE_TOWARD_ORANGE_RULE, false);
        bions.addRule(MOVE_TOWARD_BLUE_RULE, false);
        bions.addRule(MOVE_AWAY_RED_RULE, false);
        bions.addRule(MOVE_AWAY_ORANGE_RULE, false);
        bions.addRule(MOVE_AWAY_BLUE_RULE, false);
        add(bions);
    }

    public void createGraphicViews() {
        super.createGraphicViews();
        view = new EntityRelationView();
        graph.addView(view);
        view.setPreferredSize(new Dimension(600, 600));
        view.setBackground(Color.white);
        view.addEntityFeature(
            new DefaultEntityFeature(graph, "Graph Nodes") {
                /**
                 * 
                 */
                private static final long serialVersionUID = -6747972304065555498L;

                public double getHeight(Object o) {
                    return ((LocatedAgent) o).getAgentSize();
                }

                public Color getColor(Object o) {
                    HostCell hc = (HostCell) o;
                    if (hc.isAvailable()) {
                        return Color.white;
                    } else {
                        return ((Cell) hc.getOccupant()).getColor();
                    }
                }

                public Color getBorderColor(Object o) {
                    return Color.black;
                }
            }
        );
        view.addRelationFeature(
            new DefaultRelationFeature() {
                /**
                 * 
                 */
                private static final long serialVersionUID = -4242555042680052693L;

                public boolean includesRelation(Object source, Object target) {
                    return ((Graph) graph.getSpace()).isNeighbor((Cell) source, (Cell) target);
                }

                // name for identification
                public String getName() {
                    return "Neighbors View";
                }

                public double getLineWidth(Object source, Object target) {
                    return getEdgeThickness();
                }

                public Color getLineColor(Object source, Object target) {
                    if (isShowEdges()) {
                        return Color.black;
                    } else {
                        return Color.white;
                    }
                }

                public Color getGlyphColor(Object source, Object target) {
                    return Color.green;
                }
            }, true);
    }

    private class Bion extends CellOccupant {

        /**
         * 
         */
        private static final long serialVersionUID = 7888773110860599816L;
        protected double vision;

        public void initialize() {
            super.initialize();
            vision = randomInRange(minVision, maxVision);
            setAgentSize(getInitialAgentSize());
        }

        public double getVision() {
            return vision;
        }

        public void setVision(double vision) {
            this.vision = vision;
        }
    }

    class RedBion extends Bion {

        /**
         * 
         */
        private static final long serialVersionUID = -1643892823450738543L;

        public Color getColor() {
            return Color.red;
        }

        public Image getImage() {
            return ImageFeatureFixed.redBall;
        }

        public String getName() {
            return "Red Bion at " + getCoordinate();
        }
    }

    class OrangeBion extends Bion {

        /**
         * 
         */
        private static final long serialVersionUID = 8477338556896950917L;

        public Color getColor() {
            return Color.orange;
        }

        public Image getImage() {
            return ImageFeatureFixed.orangeBall;
        }

        public String getName() {
            return "Orange Bion at " + getCoordinate();
        }
    }

    class BlueBion extends Bion {

        /**
         * 
         */
        private static final long serialVersionUID = 1031034108379262939L;

        public Color getColor() {
            return Color.blue;
        }

        public Image getImage() {
            return ImageFeatureFixed.blueBall;
        }

        public String getName() {
            return "Blue Bion at " + getCoordinate();
        }
    }

    public int getGraphSize() {
        return graphSize;
    }

    public void setGraphSize(int graphSize) {
        this.graphSize = graphSize;
    }

    public Scape getGraph() {
        return graph;
    }

    public void setGraph(Scape graph) {
        this.graph = graph;
    }

    public boolean isDirectedGraph() {
        return directedGraph;
    }

    public void setDirectedGraph(boolean directedGraph) {
        this.directedGraph = directedGraph;
    }

    public double getProbBuildLink() {
        return probBuildLink;
    }

    public void setProbBuildLink(double probBuildLink) {
        this.probBuildLink = probBuildLink;
    }

    public int getInitialScapeSize() {
        return initialScapeSize;
    }

    public void setInitialScapeSize(int initialSize) {
        this.initialScapeSize = initialSize;
    }

    public int getMinVision() {
        return minVision;
    }

    public void setMinVision(int minVision) {
        this.minVision = minVision;
    }

    public int getMaxVision() {
        return maxVision;
    }

    public void setMaxVision(int maxVision) {
        this.maxVision = maxVision;
    }

    public int getInitialAgentSize() {
        return initialAgentSize;
    }

    public void setInitialAgentSize(int initialAgentSize) {
        this.initialAgentSize = initialAgentSize;
    }

    public double getEdgeThickness() {
        return edgeThickness;
    }

    public void setEdgeThickness(double edgeThickness) {
        this.edgeThickness = edgeThickness;
    }

    public boolean isShowEdges() {
        return showEdges;
    }

    public void setShowEdges(boolean showEdges) {
        this.showEdges = showEdges;
    }

    public double getProbChangeNeighbors() {
        return probChangeNeighbors;
    }

    public void setProbChangeNeighbors(double probChangeNeighbors) {
        this.probChangeNeighbors = probChangeNeighbors;
    }
}
