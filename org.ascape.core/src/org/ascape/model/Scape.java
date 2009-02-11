/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc.,
 * Metascape LLC, and contributors. All rights reserved. This program and the
 * accompanying materials are made available solely under the BSD license
 * "ascape-license.txt". Any referenced or included libraries carry licenses of
 * their respective copyright holders.
 */

package org.ascape.model;

import java.beans.IntrospectionException;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Observable;
import java.util.TooManyListenersException;
import java.util.Vector;
import java.util.zip.GZIPOutputStream;

import org.ascape.model.engine.ExecutionStrategy;
import org.ascape.model.engine.StrategyFactory;
import org.ascape.model.event.ControlEvent;
import org.ascape.model.event.ControlListener;
import org.ascape.model.event.DrawFeatureEvent;
import org.ascape.model.event.ScapeEvent;
import org.ascape.model.event.ScapeListener;
import org.ascape.model.event.ScapeListenerDelegate;
import org.ascape.model.rule.CollectStats;
import org.ascape.model.rule.ExecuteThenUpdate;
import org.ascape.model.rule.NotifyViews;
import org.ascape.model.rule.PropogateScapeOnly;
import org.ascape.model.rule.Rule;
import org.ascape.model.rule.SearchRule;
import org.ascape.model.space.Array2DBase;
import org.ascape.model.space.CollectionSpace;
import org.ascape.model.space.Continuous;
import org.ascape.model.space.Coordinate;
import org.ascape.model.space.Discrete;
import org.ascape.model.space.ListBase;
import org.ascape.model.space.ListSpace;
import org.ascape.model.space.Location;
import org.ascape.model.space.Singleton;
import org.ascape.model.space.Space;
import org.ascape.model.space.SpaceContext;
import org.ascape.model.space.SpatialTemporalException;
import org.ascape.model.space.SubSpace;
import org.ascape.runtime.AbstractUIEnvironment;
import org.ascape.runtime.NonGraphicRunner;
import org.ascape.runtime.Runner;
import org.ascape.runtime.RuntimeEnvironment;
import org.ascape.util.Conditional;
import org.ascape.util.PropertyAccessor;
import org.ascape.util.RandomIterator;
import org.ascape.util.ResetableIterator;
import org.ascape.util.Utility;
import org.ascape.util.VectorSelection;
import org.ascape.util.data.DataGroup;
import org.ascape.util.data.DataPoint;
import org.ascape.util.data.DataPointConcrete;
import org.ascape.util.data.StatCollector;
import org.ascape.util.vis.PlatformDrawFeature;

/**
 * The base class for all collections of agents within ascape. Provides services
 * to identify other scape members, execute rules on members, support scape
 * views, and other features. Also provides methods for model creation and use;
 * a model is simply a special use of a scape.<BR>
 * <BR>
 * While scapes are essentially collections of agents, there is no assumption
 * that these collections must be discrete. Scapes are the basic building block
 * of ascape models. Pick a scape appropriate for your model. For example, you
 * might want to create a model that uses cells in a 2-dimensional array. Simply
 * create an instance of the scape you want:
 * 
 * <pre>
 * Scape lattice = new Scape(new Array2DVonNeumann());
 * lattice.setExtent(new Coordinate2DDiscrete(x, y));
 * lattice.setPrototypeAgent(new MyPrototypeCell());
 * </pre>
 * 
 * In this example, extent defines the size of the lattice, and prototype agent
 * is the agent that wil be cloned to populate the lattice.<BR>
 * All ascape models are made up of at least one scape. All scapes are agents
 * and so can belong to other scapes. Models are built as a hierarchy of scapes.
 * Typically, this hierarchy is quite simple; a 'root' scape, usually a
 * <code>ScapeList</code>, is created, and other scapes are added to it:
 * 
 * <pre>
 * root = new Scape();
 * lattice = newScape();
 * lattice.setSpace(new Array2D());
 * root.add(lattice);
 * </pre>
 * 
 * Or simply subclass <code>Scape</code> as a model, and use it as the root:
 * 
 * <pre>
 * public class MyModel extends Scape {
 *     ...
 *     public MyModel() {
 *         add(lattice);
 *         ...
 *     }
 * </pre>
 * 
 * To provide behavior for your agents, you add rules to be executed upon them.
 * (If members are scapes, the rules can be executed on their members as well.)
 * 
 * <pre>
 * lattice.addRule(new MyRule());
 * </pre>
 * 
 * Rules are executed once for every iteration, on every agent. Some rules are
 * added by default, or are used by the scape internally. (For example,
 * initialization and rule iteration itself are both managed by rules.) For more
 * information, see the documentation for Rule.<BR>
 * <BR>
 * To observe a scape, you attach views to it:
 * 
 * <pre>
 * lattice.addView(new Overhead2DView());
 * </pre>
 * 
 * This registers the view as a listener of the scape and automatically provides
 * a window for it if appropriate. The scape uses an event based Model View
 * Controller design. After each iteration, each scape sends an update event to
 * each of its views, and then waits for the views to update. It is every view's
 * responsibility to inform its scape when it has updated, which it does by
 * sending a control event. More general control events are used to control
 * scape execution. Normally you don't need to worry about controls as they are
 * managed automatically by the framework.
 * 
 * Scapes can automatically
 * collect statistics on their members. (See StatCollectorCSA documentation for
 * a description of how stats are created.) Here is an example of how this might
 * be done:
 * 
 * <pre>
 * agents.addStatCollectors({new StatCollectorCSA() {
 *     public double getValue(Object object) {
 *         return ((MyAgent) object).getMyInterestingValue();
 *     }
 *     public String getName() {
 *         return &quot;Interesting Value&quot;;
 *     }
 *  },setPro
 *  ...
 * }
 * </pre>
 * 
 * <BR>
 * Once statistics have been added to a view, data on each member is gathered
 * for every agent; once before the scape is iterated, and then following the
 * execution of each iteration. (See StatCollector documentation for more
 * information on how to collect statistics.) To view your data, you can add a
 * chart view to your scape:
 * 
 * <pre>
 * ChartView myChart = new ChartView();
 * lattice.addView(myChart);
 * </pre>
 * 
 * You can double-click on a chart to select statistics to view, or add them in
 * code.
 * 
 * <pre>
 * chart.addSeries(&quot;Average Interesting Value&quot;, Color.blue);
 * </pre>
 * 
 * Finally, if you haven't added a control view, or you want the model to begin
 * running upon execution, tell it to start. (Control events can be sent to any
 * scape in the model, unless the event is scape specific. Model scapes start
 * automatically; you can easily override this behavior.)
 * 
 * <pre>
 * lattice.start();
 * </pre>
 * 
 * or send it
 * 
 * <pre>
 * lattice.respondControl(ControlEvent.REQUEST_START);
 * </pre>
 * 
 * Scapes are initialized and iterated hierarchically. The initialization and
 * iteration process are rules, and their behavior is well defined. For example,
 * if you use the default list space for your root scape, scapes will be initialized and
 * iterated in the order in which they were added to the root. Of course, this
 * is an imporant consideration whenever there are dependencies between scapes.
 * On initialization, each scape first instantiates all of its members, and then
 * initializes them. After initialization, statistics are gathered, and views
 * are requested to update. Then, each rule is executed on its scape, statistics
 * are gathered, and views are requested to update. This continues until a
 * control event stops or pauses the model, or the iteration limit provided with
 * <code>setAutoStopAt</code> is reached.<BR>
 * <BR>
 * As stated, all scapes classes may act as a complete ascape model application.
 * Using the construct methods, basic views and services are automatically added
 * to a model. Scape can be used as a common common class for both applets and
 * full applications, simplifing maintenance and guaranteeing that web page
 * models will behave consistently with desktop applications. <BR>
 * <BR>
 * Examples:<BR>
 * <BR>
 * 
 * <pre>
 * &lt;code&gt;
 *     public class MyModel extends Scape {
 *        ...
 *        createScape() {
 *            [Instantiate and add scapes to model, add rules to the model]
 *        }
 *        createViews() {
 *            [and views to the model.]
 *        }
 *        createGraphicViews() {
 *            [and graphic views to the model. Ignored when displayGraphics=false]
 *        }
 *        ...
 *     }
 * &lt;/code&gt;
 * </pre>
 * 
 * Application:<BR>
 * 
 * <pre>
 * java org.ascape.model.Scape mypath.MyModel
 * &lt;BR&gt;
 * </pre>
 * 
 * Applet:<BR>
 * 
 * <pre>
 * &lt;APPLET name=AppletName codebase=[path] &lt;param name=&quot;Scape&quot; value=&quot;mypath.MyModel&quot;&gt;&gt;&lt;/APPLET&gt;&lt;BR&gt;
 * </pre>
 * 
 * <i>Note that it is neccesary to call Model with your model's fully qualified
 * class name as the parameter. To allow your model to be invoked directly,
 * override main.</i>
 * 
 * @see org.ascape.model.rule.Rule
 * @see org.ascape.model.event.ScapeListener
 * @see org.ascape.util.data.StatCollector
 * @see org.ascape.model.event.ControlEvent
 * @author Miles Parker
 * @version 5.0
 * @history 3.0 2008 factored out runtime support
 * @history 3.0 9/10/02 significant refactoring of scape model hierarchy
 * @history 2.9 6-7/02 refactorings to relationships to user environments and
 *          scape listeners
 * @history 2.1 3/13/02 new graphics/nongraphics mechanism
 * @history 2.1 10/29/01 many changes to support new continuous space
 *          functionality, including new iteration pattern for searching space
 * @history 2.1 7/11/2001 many changes to create scape functionality, breaking
 *          into construct scape and populate scape, among other things
 * @history 2.0 6/6/2001 added basic linear search capability
 * @history 1.9 8/1/2000 many improvements to run model, including better view
 *          updating, new interupt mechanism, etc..
 * @history 1.9 6/30/2000 enhanced view support, added startOnOpen property,
 *          etc..
 * @history 1.5 1/15/00 changed to support iteration style of rule execution,
 *          added suport for opening and closing of named scapes, etc..
 * @history 1.2.5 10/6/99 changed scape constructors to include name and not
 *          include geometry where appropriate
 * @history 1.2 7/13/99 various minor changes
 * @history 1.0.9 3/23/99 moved scape control rules into Scape, making
 *          singletons
 * @history 1.0.8 3/09/99 numerous renaming related to data
 * @history 1.0.7 3/06/99 moved Model into Scape so that any scape could easily
 *          be used as the basis for a model
 * @history 1.0.6 3/03/99 added manual updating control
 * @history 1.0.5 2/19/99 numerous changes to views and data group code
 * @history 1.0.5 1/13/99 made default add view auto create window, added new
 *          form to not create window if desired
 * @history 1.0.4 12/~/98 changed to extend CellOccupant (not Cell)
 * @history 1.0.3 12/~/98 changed auto start methods, see pd.Batch2DApplication
 *          for example of new usage
 * @history 1.0.3 12/~/98 added support for periods which are distinguishable
 *          from iteration and contorl for period boundaries
 * @history 1.0.3 12/~/98 refineenvid pause and run mechanism to support propery
 *          inspectors correctly
 * @history 1.0.2 11/~/98 added support for removing views from scapes, changed
 *          scape setup mecahnism, etc..., etc..
 * @history 1.0.2 11/~/98 created usage documentation
 * @history 1.0.1 10/~/98 numerous changes to execution mechanism, etc...
 * @since 1.0
 */
public class Scape extends CellOccupant implements SpaceContext, Collection, ControlListener, ScapeListener {

    /**
     * Increment this constant if you modify this class in a way that makes it
     * incompatible with the previous version, from a serialization standpoint.
     * Note that adding or removing serialized fields does not necessarily cause
     * incompatibility.
     */
    static final long serialVersionUID = 7686992038072599524L;

    /**
     * The current version of the Ascape framework as a whole. Returns "3.0" We
     * keep this in Scape since it is typically the invoked class.
     */
    public final static String version = "3.1";

    /**
     * Copyright and credits information for ascape w/ HTML style tags. Again,
     * we keep this in Scape since it is typically the invoked class.
     */
    public final static String copyrightAndCredits =
        "<B>Ascape version "
        + version
        + "</B><BR></BR>"
        + "Portions copyright 2000-2007, NuTech Solutions, Inc.<BR></BR>"
        +
        // "http://ascape.nutechsolutions.com<BR></BR>"
        // +
        "Portions copyright 1998-2000, The Brookings Institution<BR></BR>"
        + "JFreeChart Copyright (c) 2000, 2001, Simba Management Limited and others. (See licence-LGPL.txt)<BR></BR>"
        + "Icons (C)1998 Dean S. Jones<BR></BR>"
        + "Use subject to license agreement - see License.txt.<BR></BR>"
        + "Government Users -- Commercial Software subject to 48 C.F.R. ß12.212 or 48 C.F.R. ßß227.7202-1 through 227.7202-4.";

    /**
     * A rule causing the target scape and all its children scapes to be
     * populated if auto create is set to true.
     */
    public static final Rule CREATE_RULE = new PropogateScapeOnly("Create Scape") {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        /**
         * populates the scape and all its children.
         * 
         * @param agent the playing agent
         */
        public void execute(Agent agent) {
            if (((Scape) agent).isAutoCreate()) {
                ((Scape) agent).createScape();
            }
            /*
             * if (((Scape) agent).getCustomizer() != null) { ((Scape)
             * agent).getCustomizer().build(); }
             */
            super.execute(agent);
        }

        public boolean isIterateAll() {
            return true;
        }
    };

    /**
     * A rule causing viwews to be created for scape and all subscapes. Creates
     * views for the resulting scapes.
     */
    public static final Rule CREATE_VIEW_RULE = new PropogateScapeOnly("Create Views") {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        /**
         * Creates views for all of the scapes.
         * 
         * @param agent the playing agent
         */
        public void execute(Agent agent) {
            ((Scape) agent).createViews();
            super.execute(agent);
        }

        public boolean isIterateAll() {
            return true;
        }
    };

    /**
     * A rule causing graphic views to be created for scape and all subscapes.
     */
    public static final Rule CREATE_GRAPHIC_VIEW_RULE = new PropogateScapeOnly("Create Graphic Views") {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        /**
         * Creates views for all of the scapes.
         * 
         * @param agent the playing agent
         */
        public void execute(Agent agent) {
            ((Scape) agent).createGraphicViews();
            super.execute(agent);
        }

        public boolean isIterateAll() {
            return true;
        }
    };

    /**
     * A rule causing the target scape to be populated.
     */
    public static final Rule CREATE_SCAPE_RULE = new Rule("Create Scape") {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        /**
         * populates the scape and all its children.
         * 
         * @param agent the playing agent
         */
        public void execute(Agent agent) {
            ((Scape) agent).createScape();
        }

        public boolean isIterateAll() {
            return true;
        }
    };

    /**
     * A rule causing the targets initial rules to be executed on its members.
     */
    public static final Rule INITIAL_RULES_RULE = new PropogateScapeOnly("Initial Rules") {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        /**
         * Initializes the scape and all its children.
         * 
         * @param agent the playing agent
         * @see Scape#getInitialRules()
         */
        public void execute(Agent agent) {
            super.execute(agent);
            int iterations = 0;
            int style = 0;
            if (((Scape) agent).getSpace() instanceof Discrete) {
                iterations = ((Scape) agent).getAgentsPerIteration();
                style = ((Scape) agent).getExecutionStyle();
                ((Scape) agent).setExecutionStyle(Scape.COMPLETE_TOUR);
                ((Scape) agent).setAgentsPerIteration(Scape.ALL_AGENTS);
            }
            ((Scape) agent).executeOnMembers(((Scape) agent).getInitialRules());
            if (((Scape) agent).getSpace() instanceof Discrete) {
                ((Scape) agent).setAgentsPerIteration(iterations);
                ((Scape) agent).setExecutionStyle(style);
            }
        }

        public boolean isIterateAll() {
            return true;
        }
    };

    /**
     * A rule causing all children and members that are scapes to iterate.
     * Executes all rules that have been added to each scape, increments the
     * scape counter, and collectes and stores statistics for any scapes with a
     * statistics collection rule.
     */
    public final static Rule EXECUTE_RULES_RULE = new PropogateScapeOnly("Iterate Scape") {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        /**
         * Iterate over every member and child.
         * 
         * @param agent the target agent
         */
        public void execute(Agent agent) {
            ((Scape) agent).executeOnMembers();
            super.execute(agent);
        }
    };

    /**
     * A rule causing all children and members that are scapes to iterate.
     * Executes all rules that have been added to each scape, increments the
     * scape counter, and collectes and stores statistics for any scapes with a
     * statistics collection rule.
     */
    public final static Rule CLEAR_STATS_RULE = new PropogateScapeOnly("Iterate Scape") {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        /**
         * Iterate over every member and child.
         * 
         * @param agent the target agent
         */
        public void execute(Agent agent) {
            CollectStats collectStats = ((Scape) agent).getCollectStats();
            if (collectStats != null) {
                collectStats.clear();
            }
            super.execute(agent);
        }
    };

    /**
     * A rule causing all children and members that are scapes to iterate.
     * Executes all rules that have been added to each scape, increments the
     * scape counter, and collectes and stores statistics for any scapes with a
     * statistics collection rule.
     */
    public final static Rule COLLECT_STATS_RULE = new PropogateScapeOnly("Iterate Scape") {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        /**
         * Iterate over every member and child.
         * 
         * @param agent the target agent
         */
        public void execute(Agent agent) {
            CollectStats collectStats = ((Scape) agent).getCollectStats();
            // Special case for value collection rule
            if (collectStats != null) {
                /*
                 * int iterations = ScapeDiscrete.ALL_AGENTS; if (agent
                 * instanceof ScapeDiscrete) { iterations = ((ScapeDiscrete)
                 * agent).getAgentsPerIteration(); ((ScapeDiscrete)
                 * agent).setAgentsPerIteration(ScapeDiscrete.ALL_AGENTS); }
                 */
                collectStats.setPhase(1);
                ((Scape) agent).executeOnMembers(collectStats);
                collectStats.setPhase(2);
                ((Scape) agent).executeOnMembers(collectStats);
                /*
                 * if (agent instanceof ScapeDiscrete) { ((ScapeDiscrete)
                 * agent).setAgentsPerIteration(iterations); }
                 */
            }
            super.execute(agent);
            if (collectStats != null) {
                collectStats.calculateValues();
            }
            if (((Scape) agent).isRoot() && ((Scape) agent).getRunner().getData() != null) {
                ((Scape) agent).getRunner().getData().update();
                // System.gc();
            }
        }
    };

    /**
     * The symbol to execute rules against all agents in each iteration.
     */
    public final static int ALL_AGENTS = -1;

    /**
     * Symbol for by agent execution order.
     */
    public final static int AGENT_ORDER = -2;

    /**
     * Symbol for by rule execution order.
     */
    public final static int RULE_ORDER = -1;

    /**
     * Symbol for complete tour excution style.
     */
    public final static int COMPLETE_TOUR = 1;

    /**
     * Symbol for repeated random draw execution style.
     */
    public final static int REPEATED_DRAW = 2;

    /**
     * Manages time, model-wide views and other features that are shared between
     * scapes. There is one an only one for each model instance.
     */
    private Runner runner;

    private Space space;

    /**
     * An agent which which may be cloned to produce members of this collection.
     * By default, all scapes which have a known number of members are
     * initialized with clones of this agent.
     */
    protected Agent prototypeAgent;

    /**
     * The rules that this scape will execute on its memebers.
     */
    private VectorSelection rules = new VectorSelection(new Vector());

    /**
     * The rules that this scape will execute on its members upon initializtion.
     */
    protected VectorSelection initialRules = new VectorSelection(new Vector());

    /**
     * The observers of this scape. All listeners are notified when the scape is
     * updated and given a chance to update themselves.
     */
    private ArrayList scapeListeners = new ArrayList();

    /**
     * The number of agents to execute each rule across for each iteration.
     */
    protected int agentsPerIteration = ALL_AGENTS;

    /**
     * Order in which rules should be executed.
     */
    private int executionOrder = AGENT_ORDER;

    /**
     * 'Stlye' of rule execution.
     */
    private int executionStyle = COMPLETE_TOUR;

    /**
     * Should members of the scape be iterated against?
     */
    private boolean membersActive = true;

    /**
     * Should members of the scape be automatically created at startup?
     */
    private boolean autoCreate = true;

    /**
     * Should the scape be populated on creation?
     */
    private boolean populateOnCreate = true;

    /**
     * Should cells indicate that they need to be updated manually (imroving
     * performance significantly) or should all cells be updated every
     * iteration.
     */
    private boolean cellsRequestUpdates = false;

    /**
     * The value collection rule for this scape. Null if no values should be
     * collected.
     */
    private CollectStats collectStats = null;

    /**
     * A view of the scape that delegates back to the scape, often null.
     * Automatically created for root when standard model is used.
     */
    private ScapeListener selfView;

    /**
     * A vector of features available to draw memebers of this scape.
     */
    private Vector drawFeatures = new Vector();

    /**
     * Are all the listeners and members (which may have listeners) of this
     * scape current? (If so, we can continue updating, otherwise, we must wait.
     */
    private boolean listenersAndMembersCurrent = false;

    /**
     * Determines how many time steps pass between updating displayed charts and
     * graphs. Higher numbers result in faster performance but less frequent
     * updates. This setting does not affect model results.
     */
    private int iterationsPerRedraw = 1;

    /**
     * Count of the number of listeners that have been updated. Used to
     * determine when all listeners have been updated.
     */
    private int updatedListeners = 0;

    /**
     * Count of the number of members that have been updated. Used to determine
     * when all members have been updated.
     */
    private int updatedMembers = 0;

    private boolean serializable = true;

    /**
     * Constructs a scape with default list topology.
     */
    public Scape() {
        this(new ListSpace());
    }

    /**
     * Constructs a scape.
     * 
     * @param space the topology for this scape
     */
    public Scape(CollectionSpace space) {
        this(space, null, null);
    }

    /**
     * Constructs a scape of provided geometry, to be populated with clones of
     * provided agent.
     * 
     * @param name a descriptive name for the scape
     * @param prototypeAgent the agent whose clones will be used to populate
     *            this scape
     */
    public Scape(String name, Agent prototypeAgent) {
        this(null, name, prototypeAgent);
    }

    /**
     * Constructs a scape of provided geometry, to be populated with clones of
     * provided agent.
     * 
     * @param name a descriptive name for the scape
     * @param prototypeAgent the agent whose clones will be used to populate
     *            this scape
     * @param space the topology for this scape
     */
    public Scape(CollectionSpace space, String name, Agent prototypeAgent) {
        super();
        setSpace(space);
        setName(name);
        setPrototypeAgent(prototypeAgent);
        scapeListeners = new ArrayList();
        listenersAndMembersCurrent = true;
    }

    /**
     * Returns the size, or number of agents, of this Scape.
     */
    public int getSize() {
        return space.getSize();
        // return vector.size();
    }

    /**
     * Sets the prototype agent, the agent that, in default implementations,
     * will be cloned to populate this scape. It is an error to call while the
     * scape is running.
     * 
     * @param prototypeAgent the agent whose clones will populate this scape
     */
    public void setPrototypeAgent(Agent prototypeAgent) {
        this.prototypeAgent = prototypeAgent;
        if (prototypeAgent != null && prototypeAgent.getScape() == null) {
            prototypeAgent.setScape(this);
            prototypeAgent.scapeCreated();
        }
        setInitialized(false);
    }

    /**
     * Returns the agent that is cloned to populate this scape.
     */
    public Agent getPrototypeAgent() {
        return prototypeAgent;
    }

    /**
     * Returns the number of agents to iterate through each iteration cycle.
     * *@param returns the number of iterations per cycle
     */
    public int getAgentsPerIteration() {
        return agentsPerIteration;
    }

    /**
     * Sets the number of agents to iterate through each iteration cycle. By
     * default, set to iterate through all agents. *@param agentsPerIteration
     * the number of agents to iterate against per cycle, ALL_AGENTS for all
     * agents
     */
    public void setAgentsPerIteration(int agentsPerIteration) {
        this.agentsPerIteration = agentsPerIteration;
    }

    /**
     * Returns the number of iterations to perform before updating views.
     */
    public int getIterationsPerRedraw() {
        return iterationsPerRedraw;
    }

    /**
     * Sets the number of iterations to perform before updating views, and
     * propagates this setting to all scapes and views in the model.
     */
    public void setIterationsPerRedraw(int iterationsPerRedraw) {
        setIterationsPerRedraw(iterationsPerRedraw, true);
    }

    /**
     * Sets the number of iterations to perform before updating views, and
     * optionally propagates this setting to all scapes and views in the model.
     */
    public void setIterationsPerRedraw(int iterationsPerRedraw, boolean propagate) {
        this.iterationsPerRedraw = iterationsPerRedraw;
        if (propagate) {
            executeOnRoot(new NotifyViews(ScapeEvent.REQUEST_CHANGE_ITERATIONS_PER_REDRAW) {
                /**
                 * 
                 */
                private static final long serialVersionUID = 1L;

                public void execute(Agent agent) {
                    ((Scape) agent).setIterationsPerRedraw(agent.getRoot().getIterationsPerRedraw(), false);
                    super.execute(agent);
                }
            });
        }
    }

    /**
     * Returns the execution order that has been set for this scape.
     */
    public int getExecutionOrder() {
        return executionOrder;
    }

    /**
     * Sets the order of rule execution for this scape. If 'rule order', each
     * rule is executed on every agent in turn. If 'agent order', every rule is
     * executed on each agent in turn. Execution order can be profoundly
     * significant to a model's dynamics. For 'synchrounous' style rules that
     * subclass ExecuteAndUpdate, 'by rule' execution is the only order that
     * makes sense.
     * 
     * @param symbol RULE_ORDER for by rule execution, AGENT_ORDER for by agent
     *            execution
     */
    public void setExecutionOrder(int symbol) {
        this.executionOrder = symbol;
    }

    /**
     * Returns the execution style that has been set for this scape.
     */
    public int getExecutionStyle() {
        return executionStyle;
    }

    /**
     * Sets the style that rules will be executed upon this scape. If complete
     * tour, every agent is visited once and only once (assuming agents per
     * iteration is set to 'all agents'.) If repeated draw, a random agent is
     * picked n times for execution. (Actually, if execution order is by agent,
     * each rule is then executed upon the picked agent, so that there are n
     * total draws. But if execution order is by rule, then for each rule, a
     * random agent is picked, which means that there are actually (rules X n)
     * draws. In practice, this combination does not seem to make much sense in
     * any case.) A complete tour style of execution seems generally more
     * plausible, but a repeated draw approach can produce different and
     * interesting results.
     * 
     * @param symbol one of COMPLETE_TOUR or REPEATED_DRAW
     */
    public void setExecutionStyle(int symbol) {
        this.executionStyle = symbol;
    }

    /**
     * Returns the extent of the scape. The extent can be thought of as the most
     * extreme point in the scape. For discrete scape's this will simply be the
     * furthest cell, so that for a 20x20 grid, the extent would be {20, 20}.
     * For continuous spaces it will be the maximum boundary of the space. For
     * lists, it will be the size of lists. Therefore, this method should net be
     * confused with the scape's "size". Note that scape graphs will not have
     * useful extents, but all other scapes do.
     */
    public Coordinate getExtent() {
        return space.getExtent();
    }

    /**
     * Sets the size of the scape. Note that scape graphs will not have useful
     * extents, but all other scapes do. It is an error to set extent while a
     * scape is running.
     * 
     * @param extent a coordinate at the maximum extent
     */
    public void setExtent(Coordinate extent) {
        if (getRunner() != null && getRunner().isRunning()) {
            throw new RuntimeException("Tried to modfiy extent while scape was running");
        }
        space.setExtent(extent);
    }

    /**
     * Sets the size of the scape. Note that scape graphs will not have useful
     * extents, but all other scapes do. It is an error to set extent while a
     * scape is running.
     * 
     * @throws RuntimeException if the scape is currently running
     * @param xval coordinate 1 of the extent
     */
    public void setExtent(int xval) {
        if (runner != null && runner.isRunning()) {
            throw new RuntimeException("Tried to modfiy extent while scape was running");
        }
        if (getSpace().getGeometry().getDimensionCount() != 1) {
            throw new RuntimeException("Tried to set extent as 1-dimension for a scape that isn't 1-dimensional.");
        }
        ((CollectionSpace) getSpace()).setExtent(xval);
    }

    /**
     * Sets the size of the scape. Note that scape graphs will not have useful
     * extents, but all other scapes do. It is an error to set extent while a
     * scape is running.
     * 
     * @param xval coordinate 1 of the extent
     * @param yval coordinate 2 of the extent
     * @throws RuntimeException if the scape is currently running
     * @throws UnsupportedOperationException if the underlying space isn't
     *             appropriate
     */
    public void setExtent(int xval, int yval) {
        if (runner != null && runner.isRunning()) {
            throw new RuntimeException("Tried to modfiy extent while scape was running");
        }
        try {
            ((Array2DBase) getSpace()).setExtent(xval, yval);
        } catch (ClassCastException e) {
            throw new UnsupportedOperationException("Can't set extent as x, y; underlying scape doesn't support it.");
        }
    }

    /**
     * Returns the name of this scape, the model name if this is root and there
     * is no name set.
     */
    public String getName() {
        if (name != null) {
            return name;
        } else {
            return getClass().getName();
        }
    }

    private void loadDescriptions() {
        if (getRunner().getDescription() == null || getRunner().getHTMLDescription() == null) {
            String fileName = "About" + Utility.getClassNameOnly(this.getClass()) + ".html";
            URL aboutFile = this.getClass().getResource(fileName);
            if (aboutFile != null) {
                getRunner().setDescription("");
                try {
                    BufferedInputStream is = (BufferedInputStream) aboutFile.getContent();
                    BufferedReader ir = new BufferedReader(new InputStreamReader(is));
                    String nextLine = ir.readLine();
                    // Clean out html tags
                    StringBuffer htmlFragBuffer = new StringBuffer();
                    while (nextLine != null) {
                        htmlFragBuffer.append(nextLine);
                        nextLine = ir.readLine();
                    }
                    StringBuffer plainTextBuffer = new StringBuffer();
                    // Neccessary to support 1.3
                    String htmlString = htmlFragBuffer.toString();
                    boolean done = false;
                    int pos = 0;
                    while (!done) {
                        int anglePos = htmlString.indexOf("<", pos);
                        if (anglePos >= 0) {
                            plainTextBuffer.append(htmlFragBuffer.substring(pos, anglePos));
                            if (htmlFragBuffer.substring(anglePos, anglePos + 4).equalsIgnoreCase("<BR>")) {
                                plainTextBuffer.append("\n");
                            }
                            pos = htmlString.indexOf(">", anglePos) + 1;
                        } else {
                            plainTextBuffer.append(htmlFragBuffer.substring(pos, htmlFragBuffer.length()));
                            done = true;
                        }
                    }
                    setHTMLDescription(htmlFragBuffer.toString());
                    setDescription(plainTextBuffer.toString());
                } catch (java.io.IOException e) {
                    // Probably file has not been defined; in any case, just use
                    // toString value..
                    getEnvironment().getConsole().println("Non-critical exception: couldn't read \"About\" file: " + e);
                    getRunner().setDescription(toString());
                }
            } else {
                // No about file defined
                getRunner().setDescription(toString());
            }
        }
    }

    /**
     * Returns a long (paragraph length suggested) description of the scape. The
     * root scape should describe the model as a whole; subscapes should
     * describe themselves. Plantext. If no description is provided, return the
     * standar toString() description. This description is automatically loaded
     * from a file called "About[ModelClassName].html" located in the the same
     * directory as the .class file for the model, if such a file exists. To use
     * this feature simply create such a file and place it in the appropriate
     * directory. You can include any normal html style tags in this file, they
     * will be stripped from the non-html description.
     */
    public String getDescription() {
        try {
            loadDescriptions();
            return getRunner().getDescription();
        } catch (ClassCastException cce) {
            return "";
        }
    }

    /**
     * Returns a long (paragraph length suggested) description of the scape. The
     * root scape should describe the model as a whole; subscapes should
     * describe themselves. If no description is provided, return the standar
     * toString() description.
     */
    public void setDescription(String description) {
        getRunner().setDescription(description);
    }

    /**
     * Returns a long (paragraph length suggested) description of the scape. The
     * root scape should describe the model as a whole; subscapes should
     * describe themselves. Includes html tags as appropriate. This description
     * is automatically loaded from a file called "About[ModelClassName].html"
     * located in the the same directory as the .class file for the model, if
     * such a file exists. To use this feature simply create such a file and
     * place it in the appropriate directory. You can include any normal html
     * style tags in this file, they will be stripped from the non-html
     * description. If no description is provided, return the standar toString()
     * description.
     */
    public String getHTMLDescription() {
        loadDescriptions();
        return getRunner().getHTMLDescription();
    }

    /**
     * Returns a long (paragraph length suggested) description of the scape. The
     * root scape should describe the model as a whole; subscapes should
     * describe themselves. Should include html tags as appropriate. If no
     * description is provided, return the standar toString() description.
     */
    public void setHTMLDescription(String description) {
        getRunner().setHTMLDescription(description);
    }

    /**
     * Returns the root of this scape, which may be this scape.
     */
    public final Scape getRoot() {
        if (scape == null) {
            return this;
        } else {
            return scape.getRoot();
        }
    }

    /**
     * Is this scape the root within its entire simulation context? That is,
     * does this root not have any parent scapes?
     */
    public boolean isRoot() {
        return scape == null;
    }

    /**
     * Has a view update been requested for this cell?
     */
    public boolean isUpdateNeeded() {
        return isUpdateNeeded(getIterationsPerRedraw());
    }

    /**
     * Contructs the basic scape structure. Instantiates the agents, but does
     * not populate them. It is not neccesary to set extent before initializing
     * a collection, unless you want to have a populated vecotr to begin with.
     */
    public void construct() {
        space.construct();
        if (getSpace() instanceof Discrete && getPrototypeAgent() == null) {
            setPrototypeAgent(new Cell());
        }
    }

    /**
     * Populates the scape with clones of the prototype agent. Prototype agent
     * should be set before calling this method.
     */
    public void populate() {
        space.populate();
    }

    /**
     * Create this scape; contruct it, populate it, add rules, create statistic
     * collectors, etc. Called automatically at model construction, unless
     * isAutoCreate is set to false. By default, automatically populates all
     * members with clones of the prototype agent. Of course, this behavior can
     * be overridden or embellished. To turn off the populate behavior, set
     * populate on create to false.
     * 
     * @see #setPopulateOnCreate
     */
    public void createScape() {
        // We use Scape as a prototype simply because Scape is abstract and so
        // can't be
        // instantiated. Any scape will do, since we won't be cloning it to
        // populate.
        if (isRoot()) {
            if (getPrototypeAgent() == null) {
                setPrototypeAgent(new Scape());
            }
            if (getRunner() == null) {
                new NonGraphicRunner().setRootScape(this);
            }
        }
        construct();
        if (isPopulateOnCreate()) {
            populate();
        }
    }

    /**
     * Initializes the state of the scape. This is the appropriate place to put
     * any initialization that is dependent on the state of other ascape
     * objects. If autoCreate is true, calls createScape() on the scape. Note
     * that for root scapes, autoCreate is always false. Objects are initialized
     * in the order they are added to parent scapes.
     */
    public void initialize() {
        if (isRoot()) {
            setAutoCreate(false);
        }
        if (isAutoCreate()) {
            createScape();
        }
        super.initialize();
        getSpace().initialize();
        if (!(getSpace() instanceof Continuous) && getPrototypeAgent() != null
                && getPrototypeAgent().getScape() == this && !getSpace().isMutable()) {
            executeOnMembers(Cell.CALCULATE_NEIGHBORS_RULE);
        }
    }

    /**
     * Returns current count of iterations.
     */
    public final int getIteration() {
        return getRunner().getIteration();
    }

    /**
     * Returns the current period, which is just the iteration plus the period
     * begin.
     */
    public final int getPeriod() {
        return getRunner().getPeriod();
    }

    /**
     * Returns the name that periods are referred to by.
     */
    public String getPeriodName() {
        return getRunner().getPeriodName();
    }

    /**
     * Returns a string description of the current period, i.e. "Iteration 1",
     * "Year 1900", "StarDate 3465.29."
     */
    public String getPeriodDescription() {
        return getPeriodName() + " " + Integer.toString(getPeriod());
    }

    /**
     * Sets the name that periods are referred to by.
     */
    public void setPeriodName(String name) {
        getRunner().setPeriodName(name);
    }

    /**
     * Adds a rule to this scape, automatically selecting it.
     */
    public synchronized void addRule(Rule rule) {
        addRule(rule, true);
    }

    /**
     * Adds a rule to this scape. Allows setting whether rule be should be
     * selected (run) automatically? If the selection is not changed, rules are
     * executed in the order they are added.
     * 
     * @param rule the rule to add
     * @param select if rule should be run false if rule should just be made
     *            available to be run
     */
    public synchronized void addRule(Rule rule, boolean select) {
        if (rule instanceof ExecuteThenUpdate && getExecutionOrder() != RULE_ORDER) {
            // bug mtp: need to add similar check when setting rule order.
            throw new RuntimeException(
            "Tried to add execute and update rule to AGENT_ORDER Scape. Set Scape to RULE_ORDER execution first.");
        }
        if (rule.getScape() == null) {
            rule.setScape(this);
        }
        rules.addElement(rule, select);
    }

    /**
     * Returns all rules that this scape might execute.
     */
    public VectorSelection getRules() {
        return rules;
    }

    /**
     * Adds a rule to be executed once following initialization. Rule is
     * automatically selected for running.
     * 
     * @param rule to be executed at simulation start
     */
    public synchronized void addInitialRule(Rule rule) {
        addInitialRule(rule, true);
    }

    /**
     * Adds a rule to be executed once following initialization. If the
     * selection is not chagned, rules are executed in the order they are added.
     * 
     * @param rule to be executed at simulation start
     * @param select if rule should be run false if rule should just be made
     *            available to be run
     */
    public synchronized void addInitialRule(Rule rule, boolean select) {
        if (rule.getScape() == null) {
            rule.setScape(this);
        }
        initialRules.addElement(rule, select);
    }

    /**
     * Returns all the rules executed following scape initialization.
     */
    public VectorSelection getInitialRules() {
        return initialRules;
    }

    public void setInitialRules(VectorSelection initialRules) {
        this.initialRules = initialRules;
    }

    /**
     * Adds a view to this scape. Takes care of basic housekeeping, including
     * registering view as listener, and creating window for view to be
     * displayed within.
     * 
     * @param view ComponentView to display in window
     */
    public synchronized void addView(ScapeListener view) {
        this.addView(view, true);
    }

    /**
     * Adds a view to this scape. Takes care of basic housekeeping, including
     * registering view as listener, and creating window for view to be
     * displayed within. This version of the method allow the adding of a view
     * without regard to the GUI setting. This method might be useful for
     * instace when temporaily instrumenting a non-gui run with diagnostics.
     * Normally addScapeListener should be used.
     * 
     * @param view ComponentView to display in window
     * @param createFrame should the view be placed within a new window frame?
     * @param forceGUI add a GUI view witout regard to the display GUI setting
     */
    public synchronized void addView(final ScapeListener view, boolean createFrame, boolean forceGUI) {
        if (forceGUI || Runner.isDisplayGraphics() || !view.isGraphic() || Runner.isServeGraphics()) {
            try {
                this.addScapeListener(view);
                // if (view instanceof Component) {
                // // try {
                // SwingUtilities.invokeLater(new Runnable() {
                // public void run() {
                // view.scapeNotification(new ScapeEvent(Scape.this,
                // ScapeEvent.REPORT_ADDED));
                // }
                // });
                // // } catch (InterruptedException e) {
                // // e.printStackTrace();
                // // } catch (InvocationTargetException e) {
                // // e.printStackTrace();
                // // }
                // } else {
                view.scapeAdded(new ScapeEvent(this, ScapeEvent.REPORT_ADDED));
                // }
            } catch (TooManyListenersException e) {
                throw new RuntimeException("Tried to add a view to more than one scape:\n" + e);
            }
            if (createFrame && view.isGraphic()) {
                if (!Runner.isServeGraphics()) {
                    getEnvironment().addView(view);
                }
                // topdo remove view dependency
            }
        } else {
        }
    }

    /**
     * Adds a view to this scape. Takes care of basic housekeeping, including
     * registering view as listener, and creating window for view to be
     * displayed within. An important exception occurs when a GUI view is added
     * and display GUI is set to false. In this case, the view will _not_ be
     * added. This makes it easy to add views in many model components without
     * worrying about checking for GUI display state. Views can be added
     * regardless of the value of display GUI by using addViewForce. Note: Even
     * with the conveneince of this method, it is often nec
     * 
     * @param view ComponentView to display in window
     * @param createFrame should the view be placed within a new window frame?
     */
    public synchronized void addView(ScapeListener view, boolean createFrame) {
        addView(view, createFrame, false);
    }

    /**
     * Adds a view to this scape. Takes care of basic housekeeping, including
     * registering view as listener, and creating window for view to be
     * displayed within.
     * 
     * @param views ComponentView to display in window
     */
    public synchronized void addViews(ScapeListener[] views) {
        this.addViews(views, true);
    }

    /**
     * Adds an array of views to this scape. Takes care of basic housekeeping,
     * including registering the views as listeners, and creating window for
     * view to be displayed within. This version of the method allow the adding
     * of a view without regard to the GUI setting. This method might be useful
     * for instace when temporaily instrumenting a non-gui run with diagnostics.
     * Normally addScapeListener should be used.
     * 
     * @param views ComponentViews array to display in window
     * @param createFrame should the view be placed within a new window frame?
     * @param forceGUI add a GUI view witout regard to the dispaly GUI setting
     */
    public synchronized void addViews(ScapeListener[] views, boolean createFrame, boolean forceGUI) {
        if (forceGUI || Runner.isDisplayGraphics() || !views[0].isGraphic()) {
            try {
                for (int i = 0; i < views.length; i++) {
                    this.addScapeListener(views[i]);
                    views[i].scapeAdded(new ScapeEvent(this, ScapeEvent.REPORT_ADDED));
                }
            } catch (TooManyListenersException e) {
                throw new RuntimeException("Tried to add a view to more than one scape");
            }
            boolean allGraphic = false;
            for (int i = 0; i < views.length; i++) {
                if (views[i].isGraphic()) {
                    allGraphic = true;
                } else {
                    allGraphic = false;
                    break;
                }
            }
            // For now, we're going to assume that if one of the views is
            // graphic, they all are
            if (createFrame && allGraphic) {
                getEnvironment().addViews(views);
            }
        }
    }

    /**
     * Adds an array of views to this scape. Takes care of basic housekeeping,
     * including registering the views as listeners, and creating window for
     * view to be displayed within. An important exception occurs when GUI views
     * are added and display GUI is set to false. In this case, the views will
     * _not_ be added. This makes it easy to add views in many model components
     * without worrying about checking for GUI display state. Views can be added
     * regardless of the value of dispaly GUI by using addViewForce.
     * 
     * @param views ComponentViews to display in window
     * @param createFrame should the view be placed within a new window frame?
     */
    public synchronized void addViews(ScapeListener[] views, boolean createFrame) {
        // To do, test and throw error for case where user tries to add mixed
        // gui and non gui views, or the view array is empty
        addViews(views, createFrame, false);
    }

    /**
     * Adds an observer to this scape. This observer will be notified when the
     * scape has finished iterating, and is expected to notify this scape when
     * it has updated itself. This method also adds the scape to the listener as
     * a control listener.
     * 
     * @param listener the listern to add
     */
    public synchronized void addScapeListener(ScapeListener listener) {
        if (listener == null) {
            throw new RuntimeException("Tried to add a null listener to Scape.");
        }
        // Not sure if we want to make this poilicy or not..
        /*
         * if (listener.getScape() == null) { throw new
         * RuntimeException("Listener must have this scape assigned before
         * calling addScapeListener."); }
         */
        // change to array copy
        scapeListeners.add(listener);
        updatedListeners++;
        listenerOrMemberUpdated();
    }

    /**
     * Adds an observer to this scape. This version simple adds the new listener
     * to the beginning of the list. This can be useful if there are listeners
     * that need to be called first.
     * 
     * @param listener the listern to add
     */
    public synchronized void addScapeListenerFirst(ScapeListener listener) {
        if (listener == null) {
            throw new RuntimeException("Tried to add a null listener to Scape.");
        }
        scapeListeners.add(0, listener);
        updatedListeners++;
        listenerOrMemberUpdated();
    }

    /**
     * Returns true if and only if the argument is an observer of this scape.
     */
    public boolean isScapeListener(ScapeListener listener) {
        return scapeListeners.contains(listener);
    }

    /**
     * Removes the observer from this scape. This observer will be notified when
     * the scape has finished iterating, and is expected to notify this scape
     * when it has updated itself.
     */
    public synchronized void removeScapeListener(ScapeListener listener) {
        boolean success = scapeListeners.remove(listener);
        if (!success) {
            getEnvironment().getConsole().println(
                                                  "WARNING: Tried to remove unregistered scape listener " + listener + " from scape " + this + ".");
        }

        listener.scapeRemoved(new ScapeEvent(this, ScapeEvent.REPORT_REMOVED));
        // we may have just removed the last non-updated listener,
        // so that everything needing an update has been updated; we need to
        // check.
        listenerOrMemberUpdated();
    }

    /**
     * Returns all listeners for this scape.
     */
    public ArrayList getScapeListeners() {
        return scapeListeners;
    }

    // todo (Tried using thread notification, but too slow...perhpas w/
    // pooling??
    // class NotificationThread extends Thread {
    // private ScapeListener listener;
    // private int id;
    //
    // public NotificationThread(ScapeListener listener, int id) {
    // super(Scape.this + " Scape Notify " + listener);
    // this.listener = listener;
    // this.id = id;
    // }
    //
    // public void run() {
    // listener.scapeNotification(new ScapeEvent(Scape.this, id));
    // }
    // }

    /**
     * Notifies all scape listeners that this scapes state has changed. The root
     * scape thread then waits until all listeners have been updated.
     */
    public void notifyViews(final int id) {
        notifyViews(new ScapeEvent(Scape.this, id));
    }

    /**
     * Notifies all scape listeners that this scapes state has changed. The root
     * scape thread then waits until all listeners have been updated.
     */
    public void notifyViews(final ScapeEvent event) {
        listenersAndMembersCurrent = false;
        updatedListeners = 0;
        updatedMembers = 0;
        if (scapeListeners.size() > 0) {
            ArrayList currentListeners = (ArrayList) scapeListeners.clone();
            for (Object listener : currentListeners) {
                getRunner().notify(event, (ScapeListener) listener);
            }
        } else {
            listenerOrMemberUpdated();
        }
    }

    /**
     * Have all views and views of memebers of this scape been updated? [The
     * grammer is terrible, but it fits the text pattern!]
     * 
     * @return boolean true if no views are still updating, false if not
     */
    public final boolean isAllViewsUpdated() {
        return listenersAndMembersCurrent;
    }

    /**
     * Called whenever a listener or member scape of this scape has been
     * updated. If all listeners and members have been updated, informs parent
     * scape.
     */
    protected synchronized void listenerOrMemberUpdated() {
        // For testing updating..
        // if ((updatedListeners >= scapeListeners.length) &&
        // ((!(getPrototypeAgent() instanceof Scape)) || (!(((Scape)
        // getPrototypeAgent()).isMembersActive())) || (!(getPrototypeAgent()
        // instanceof AgentScape)) || (updatedMembers >= getSize()))) {
        if (updatedListeners >= scapeListeners.size()
                && (updatedMembers >= getSize() || !(getPrototypeAgent() instanceof Scape)
                        || getSpace() instanceof Singleton || !((Scape) getPrototypeAgent()).isMembersActive())) {
            listenersAndMembersCurrent = true;
            updatedListeners = 0;
            updatedMembers = 0;
            if (scape != null) {
                scape.memberUpdated(this);
                // if (isInitialized() ) {
                // getRuntimeEnvironment().getConsole().println(scape.
                // updatedListeners
                // + "/" +
                // scape.scapeListeners.length + ", " + scape.updatedMembers +
                // "/"+scape.getSize() + " for " + scape + " from " + this);
                // }
            }
        }
    }

    /**
     * Called whenever a listener has been updated.
     * 
     * @param listener the listener tha has been updated
     */
    public synchronized void listenerUpdated(ScapeListener listener) {
        updatedListeners++;
        // Useful for testing updating problems.
        // getRuntimeEnvironment().getConsole().println("L " + listener + ": " +
        // updatedListeners +" of "+ scapeListeners.size()+" ---
        // "+updatedMembers + " of
        // " + getSize());
        listenerOrMemberUpdated();
    }

    /**
     * Called whenever a member has been updated.
     * 
     * @param member the member that has been updated
     */
    public synchronized void memberUpdated(Scape member) {
        updatedMembers++;
        // Useful for testing updating problems.
        // getRuntimeEnvironment().getConsole().println("M " + member + ": " +
        // updatedListeners +" of "+ scapeListeners.size()+" ---
        // "+updatedMembers + " of
        // " + getSize());
        listenerOrMemberUpdated();
    }

    /**
     * Responds to any control events fired at this scape. Currently reacts to
     * start, stop, pause, resume, step, quit, and restart events, as well as
     * listener update report events. All control events except listener updates
     * are passed up to the root. Any other events trigger an untrapped
     * exception.
     */
    public void respondControl(ControlEvent control) {
        if (control.getID() == ControlEvent.REPORT_LISTENER_UPDATED) {
            listenerUpdated((ScapeListener) control.getSource());
        } else {
            getRunner().respondControl(control);
        }
    }

    /**
     * This is for grid communication of changes in draw feature.
     * 
     * @param event
     */
    public void respondDrawFeature(DrawFeatureEvent event) {
        throw new RuntimeException("The client or worker scape should define their own version of this!");
    }

    /**
     * Sets the running state for all scapes. Safe to call on any scape in the
     * model; the request is propogated to the parent scape. If true, starts the
     * parent scape's thread, which causes scape to iterate. If set false, the
     * scape will be stopped when the current iteration is complete.
     * 
     * @param running if true, starts the thread, if false, stops it.
     */
    public void setRunning(boolean running) {
        getRunner().setRunning(running);
    }

    /**
     * Has the scape been requested to run? <i>Note:</i> if false, indicates
     * that a stop has been requested, not neccesarily that it has occured, as
     * the simulation continues the current iteration. If you need to know when
     * a scape has actually stopped, listen for the scapeStopped event.
     * 
     * @return the current requested running state
     */
    public boolean isRunning() {
        return getRunner() != null && getRunner().isRunning();
    }

    /**
     * Sets the paused state for all parent and member scapes. Safe to call on
     * any scape in the model; the request is propogated up to the parent scape.
     * If set true, a pause will occur when the current iteration is complete.
     * 
     * @param pause if true, pauses, otherwise resumes iterations
     */
    public void setPaused(boolean pause) {
        if (pause) {
            getRunner().pause();
        } else {
            getRunner().resume();
        }
    }

    /**
     * Has the scape been requested to pause? <i>Note:</i> indicates that a
     * pause has been requested, not neccesarily that the simulation is paused;
     * it may be completing its current iteration.
     * 
     * @return true if pause requested, false if resume requested or running
     *         normally
     */
    public boolean isPaused() {
        return getRunner().isPaused();
    }

    /**
     * Sets the earliest period this scape is expected to be run at. 0 by
     * default.
     * 
     * @param earliestPeriod the lowest period value this scape can have
     */
    public void setEarliestPeriod(int earliestPeriod) {
        getRunner().setEarliestPeriod(earliestPeriod);
    }

    /**
     * Sets the latest period this scape is expected to be run at. Max of
     * integer (effectively unlimited) by default.
     * 
     * @param latestPeriod the highest period value this scape can have
     */
    public void setLatestPeriod(int latestPeriod) {
        getRunner().setLatestPeriod(latestPeriod);
    }

    /**
     * Is the supplied period a valid period for this scape?
     * 
     * @param period the period to test
     * @return true if within earliest and latest periods, false otherwise
     */
    public boolean isValidPeriod(int period) {
        return getRunner().isValidPeriod(period);
    }

    /**
     * Returns the period this scape begins running at. By default, the greater
     * of earliest period and 0.
     */
    public int getStartPeriod() {
        return getRunner().getStartPeriod();
    }

    /**
     * Sets the start period for this scape. The start period is the period this
     * scape is given when a model run is started.
     * 
     * @param startPeriod the period to begin runs at
     */
    public void setStartPeriod(int startPeriod) throws SpatialTemporalException {
        getRunner().setStartPeriod(startPeriod);
    }

    /**
     * Returns the period this scape stops running at. By default, the lesser of
     * latest period and integer maximum value (effectively unlimited.)
     */
    public int getStopPeriod() {
        return getRunner().getStopPeriod();
    }

    /**
     * Sets the stop period for this scape. The stop period is the period that
     * the scape is automatically stopped at. The scape may be automatically set
     * to start agina at start value is the scape is set to restart.
     * 
     * @param stopPeriod the period the scape will stop at upon reaching
     * @see #setAutoRestart
     */
    public void setStopPeriod(int stopPeriod) throws SpatialTemporalException {
        getRunner().setStopPeriod(stopPeriod);
    }

    /**
     * Returns the period to pause on.
     */
    public int getPausePeriod() {
        return getRunner().getPausePeriod();
    }

    /**
     * Causes the model to pause at the specified period.
     * 
     * @param pausePeriod when to pause
     */
    public void setPausePeriod(int pausePeriod) {
        getRunner().setPausePeriod(pausePeriod);
    }

    /**
     * Does the scape automatically start upon opening? True by default.
     */
    public boolean isStartOnOpen() {
        return Runner.isStartOnOpen();
    }

    /**
     * Should the scape be automatically started upon opening? True by default.
     * 
     * @param startOnOpen true to start the scape upon opening a model
     */
    public void setStartOnOpen(boolean startOnOpen) {
        Runner.setStartOnOpen(startOnOpen);
    }

    /**
     * Should the scape be automatically restarted upon stopping at its stop
     * period? Setting this value to true allows easy cycling of models for
     * demonstrations, model explorations, etc. See DataOutputView for an
     * example of more sophisticated handling of multiple runs.
     * 
     * @param autoRestart true to restart the scape upon reaching stop period,
     *            false to simple stop
     * @see #setStopPeriod
     */
    public void setAutoRestart(boolean autoRestart) {
        getRunner().setAutoRestart(autoRestart);
    }

    /**
     * Returns the root of this scape, which may be this scape. This was an
     * older usage and is retained for backward compatability only. It is now
     * not a all good name as it confuses model for a view / control component,
     * and may be removed in the future.
     * 
     * @deprecated please use #getRunner().
     */
    public Runner getModel() {
        return getRunner();
    }

    /**
     * Returns the runtime model environment, which manages model-wide state
     * such as run status.
     * 
     * @return the model environment for entire model
     */
    public Runner getRunner() {
        return getRoot().runner;
    }

    public void setRunner(Runner _runner) {
        runner = _runner;
    }

    /**
     * Returns the path in which all files should by default be stored to and
     * retrieved from. Nonstatic, so that parameter can automatically be set
     * from command line, but backing variable is static. Default is "./", can
     * be modified by calling setHome or providing an ascape.home java property.
     * (This may change now since it is no longer neccesary.)
     */
    public String getHome() {
        return getRunner().getHome();
    }

    /**
     * Sets the path in which to store all scape related files. Nonstatic, so
     * that parameter can automatically be set from command line, but backing
     * variable is static.
     * 
     * @param home the fully qualified path name for this scape
     */
    public void setHome(String home) {
        getRunner().setHome(home);
    }

    /**
     * Are members of this active scape model participants, that is, do they
     * have rules executed upon them? Default is true.
     * 
     * @return true if members actively execute rules, false otherwise
     */
    public boolean isMembersActive() {
        return membersActive;
    }

    /**
     * Sets whether members of this scape actively execute rules upon members.
     * 
     * @param membersActive true if members actively execute rules, false
     *            otherwise
     */
    public void setMembersActive(boolean membersActive) {
        this.membersActive = membersActive;
    }

    /**
     * Do cells request view updates manually or are all cells automatically
     * updated every view cycle? While requiring cells to request updates
     * manually adds a little to complication to model design and maintenance,
     * manual requests allow a significant boost in view performance, as all
     * cells do not have to be drawn every cycle. False by default.
     * 
     * @return true if cells must request updates, false if cell updates handled
     *         automatically
     */
    public boolean isCellsRequestUpdates() {
        return cellsRequestUpdates;
    }

    /**
     * Should cells request view updates manually or are all cells automatically
     * updated every view cycle? See above. <i>Important:</i> If you set this
     * value to be true, you are responsible for ensuring that the
     * <code>requestUpdate</code> method is called anytime a cell's state
     * changes such that a view may be affected. Some of these calls will be
     * handled for you automatically, for instance, it is not neccesary to call
     * requestUpdate when a cell moves, since the HostCell calls requestUpdates
     * for you. Typically, you will need to request updates when the internal
     * state of a cell changes and that is reflected in how a cell is
     * represeneted in a view, for example, if you color an agent for wealth,
     * you will need to call <code>requestUpdate</code> anytime the agent wealth
     * changes.
     * 
     * @param cellsRequestUpdates if cells should request updates, false if cell
     *            updates should be handled automatically
     * @see Cell#requestUpdate
     */
    public void setCellsRequestUpdates(boolean cellsRequestUpdates) {
        this.cellsRequestUpdates = cellsRequestUpdates;
    }

    /**
     * Executes the provided rules on the supplied agentArray.
     */
    public void execute(List rules, List agents) {
        Agent[] agentArray = new Agent[agents.size()];
        agents.toArray(agentArray);
        int[] order = CollectionSpace.createOrder(agentArray.length);
        if (executionOrder == AGENT_ORDER) {
            order = CollectionSpace.randomizeOrder(order, getRandom());
            for (int i = 0; i < agentArray.length; i++) {
                for (Iterator iterator = rules.iterator(); iterator.hasNext();) {
                    Rule rule = (Rule) iterator.next();
                    rule.execute(agentArray[order[i]]);
                }
            }
        } else { // executionOrder == RULE_ORDER
            // Add call so that cells can respond even if this is not the
            // primary agent
            for (Iterator iterator = rules.iterator(); iterator.hasNext();) {
                Rule rule = (Rule) iterator.next();

                if (rule.isRandomExecution()) {
                    order = CollectionSpace.randomizeOrder(order, getRandom());
                }
                for (int i = 0; i < agentArray.length; i++) {
                    rule.execute(agentArray[order[i]]);
                }
                if (rule instanceof ExecuteThenUpdate) {
                    if (rule.isRandomExecution()) {
                        order = CollectionSpace.randomizeOrder(order, getRandom());
                    }
                    for (int i = 0; i < agentArray.length; i++) {
                        ((ExecuteThenUpdate) rule).update(agentArray[order[i]]);
                    }
                }
            }
        }
    }

    /**
     * Executes the provided rule on every member of the lattice, according to
     * the rule settings and the execution order of this scape.
     */
    public void execute(Rule rule, List agents) {
        List rules = new ArrayList(1);
        rules.add(rule);
        execute(rules, agents);
    }

    /**
     * Executes all of this scapes selected rules on its members.
     */
    public void executeOnMembers() {
        executeOnMembers(rules);
    }

    /**
     * Executes the provided rules on every member of the lattice, according to
     * the rule settings and the execution order of this scape.
     */
    public void executeOnMembers(VectorSelection ruleSelection) {
        executeOnMembers(ruleSelection.getSelection());
    }

    /**
     * Executes the provided rule on every member of the lattice, according to
     * the rule settings and the execution order of this scape.
     */
    public void executeOnMembers(Rule rule) {
        Rule[] rules = new Rule[1];
        rules[0] = rule;
        executeOnMembers(rules);
    }

    /**
     * Executes the provided rules on every member of the collection, according
     * to the rule settings and the execution order of the scape.
     */
    public void executeOnMembers(Object[] rules) {
        if (rules.length > 0) {
            strategy = new StrategyFactory(this, rules, Scape.threadCount).getStrategy();
            strategy.execute();
        }
    }

    /**
     * Returns an iterator across all agents in this scape. Note that this is
     * simply an iterator of the backing collections members. It will have
     * different behavior than is typically desried when iterating behavior
     * across a scape*; so for instance, this method is not used by the internal
     * rule mechanism. It should be perfectly adequete for tight iterations
     * across agents when there are no additions or deletions during the
     * iteration; for instance, when calcualting some value across a number of
     * agents. *The iterator will not be aware of an agents deletion from the
     * scape after its creation; this is because the scape caches these removals
     * to improve performance. It may include agents that are added to the scape
     * after its creation, and this is typically not desirable behavior when
     * touring a collection of current agents.
     * 
     * @return an iterator over the agents in scape order
     */
    public Iterator iterator() {
        return space.iterator();
    }

    /**
     * Propogates the rule for execution up to the root of the scape tree, then
     * propogates down to all nodes.
     */
    public void executeOnRoot(Rule[] rules) {
        if (scape == null) {
            // Top level, so execute for all nodes
            execute(rules);
        } else {
            // Still have parent scapes, so propogate up
            scape.executeOnRoot(rules);
        }
    }

    /**
     * Propogates the rule for execution up to the root of the scape tree, then
     * propogates down to all nodes.
     */
    public void executeOnRoot(Rule rule) {
        Rule[] rules = new Rule[1];
        rules[0] = rule;
        executeOnRoot(rules);
    }

    /**
     * Holds the current search rule. Make non-static to make searching
     * threadsafe.
     */
    private static SearchRule defaultSearch;

    /**
     * Searches through the scape for an object (agent) that matches the
     * supplied key and comparator. Typically will return the first agent found,
     * but this behavior is not guranteed. For now, this is implemented as a
     * simple linear search, so search time is O(n). Future versions may allow
     * use of a binary search where the scape is allready appropriatly sorted,
     * or may allow a cached comparator map to be used. For now if the scape is
     * sorted in an order matching the comparators order, you can use
     * "getAsList" and Collections.binarySearch to get an O(log(n)) search. This
     * code is not thread safe, but can easily be made so.
     * 
     * @param comparator the Comparator to use to perfrom the search
     * @param key the key that an agent must match in order to be returned.
     */
    public Agent search(Comparator comparator, Object key) {
        if (defaultSearch == null) {
            defaultSearch = new SearchRule("Default Scape Search Rule");
        }
        defaultSearch.setComparator(comparator);
        defaultSearch.setKey(key);
        defaultSearch.setSearchType(SearchRule.SEARCH_EQUAL);
        defaultSearch.clear();
        executeOnMembers(defaultSearch);
        return defaultSearch.getFoundAgent();
    }

    /**
     * Searches through the scape for an object (agent) that has the minimum
     * value as defined by the comparator.
     * 
     * @param comparator the Comparator to use to determin the minimum
     */
    public Agent searchMin(Comparator comparator) {
        if (defaultSearch == null) {
            defaultSearch = new SearchRule("Default Scape Search Rule");
        }
        defaultSearch.setComparator(comparator);
        defaultSearch.setSearchType(SearchRule.SEARCH_MIN);
        defaultSearch.clear();
        executeOnMembers(defaultSearch);
        return defaultSearch.getFoundAgent();
    }

    /**
     * Searches through the scape for an object (agent) that has the minimum
     * value as defined by the comparator.
     * 
     * @param comparator the Comparator to use to determin the minimum
     */
    public Agent searchMax(Comparator comparator) {
        if (defaultSearch == null) {
            defaultSearch = new SearchRule("Default Scape Search Rule");
        }
        defaultSearch.setComparator(comparator);
        defaultSearch.setSearchType(SearchRule.SEARCH_MAX);
        defaultSearch.clear();
        executeOnMembers(defaultSearch);
        return defaultSearch.getFoundAgent();
    }

    /**
     * If true, turns on value (typically for statistics) collection, else turns
     * off stat collection.
     */
    public void setCollectStats(boolean collect) {
        if (collect) {
            collectStats = new CollectStats();
            collectStats.setScape(this);
        } else {
            collectStats = null;
        }
    }

    /**
     * Returns the value collection rule in effect; null if no value collection.
     */
    public CollectStats getCollectStats() {
        return collectStats;
    }

    /**
     * Sets the value collection rule to the one supplied. Allows use of custom
     * value collection rules. Please let me know if you use this..considering
     * removal.
     */
    public void setCollectStats(CollectStats collectStats) {
        this.collectStats = collectStats;
        collectStats.setScape(this);
    }

    /**
     * Is the scape responsible for creating itself and its members, or are
     * other classes responsible for creating the scape? If true (default) calls
     * the createScape method on model construction, typically causing the scape
     * to be populated with clones of prototype agent. If false, scape must be
     * populated manually.
     */
    public boolean isAutoCreate() {
        return autoCreate;
    }

    /**
     * Sets wether the scape is responsible for creating itself and its members,
     * or other model components handle this.
     * 
     * @param autoCreate if true calls createScape at construction, otherwise
     *            model is built manually
     */
    public void setAutoCreate(boolean autoCreate) {
        this.autoCreate = autoCreate;
    }

    /**
     * Is the scape populated when the scape is created? That is, is the
     * populate scape method called when create scape is executed? (Typically,
     * the populate scape method will fill each cell with clones of the
     * prototype cell, but of course this behavior can be overidden.) True by
     * default.
     * 
     * @return true if the scape will be populated when scape create is called,
     *         false otherwise
     */
    public boolean isPopulateOnCreate() {
        return populateOnCreate;
    }

    /**
     * Sets wether the scape is responsible for populating itself.
     * 
     * @param populateOnCreate if true calls createScape at construction,
     *            otherwise model is built manually
     */
    public void setPopulateOnCreate(boolean populateOnCreate) {
        this.populateOnCreate = populateOnCreate;
    }

    /**
     * Adds the specified stat collectors to this scape for automatic collection
     * by the scape. If this scape is not allready collecting stats, implicitly
     * sets collect stats to true. Adds the stats to the stat collection rule.
     * 
     * @param stats the stat collectors to add to this scape.
     */
    public void addStatCollectors(StatCollector[] stats) {
        // stats added in collect values
        // getData().addStatCollectors(StatCollectors);

        // loop through and give the stats references for their datagroup.
        // must be done prior to getData().add(stats), because
        // DataGroup.add(Stats)
        // calls getAllDataSeries, which calls isCollectingLongitudinal, which
        // uses the StatCollector's
        // reference to its data group.
        for (int i = 0; i < stats.length; i++) {
            stats[i].setDataGroup(getRunner().getData());
        }
        getRunner().getData().add(stats);
        if (collectStats == null) {
            setCollectStats(true);
        }
        collectStats.addStatCollectors(stats);
    }

    /**
     * Adds the specified stat collector iff and only if it hasn't allready been
     * added.
     * 
     * @param stat the stat collector to add to this scape. todo allow
     *            replacement (cuurent version only adds if a stat does not
     *            already exist.) possibly get rid of this once issue with
     *            multiple stat collectors is resolved.
     */
    public StatCollector addStatCollectorIfNew(StatCollector stat) {
        StatCollector foundStat = getRunner().getData().getStatCollector(stat.getName());
        if (foundStat == null) {
            addStatCollector(stat);
            foundStat = stat;
        }
        return foundStat;
    }

    /**
     * Adds the specified stat collector to this scape for automatic collection
     * by the scape. If this scape is not allready collecting stats, implicitly
     * sets collect stats to true. Adds the stat to the stat collection rule.
     * 
     * @param stat the stat collector to add to this scape.
     */
    public void addStatCollector(StatCollector stat) {
        StatCollector[] stats = new StatCollector[1];
        stats[0] = stat;
        addStatCollectors(stats);
    }

    /**
     * Returns the stat collectors currently calcualting stats for this scape.
     */
    public StatCollector[] getStatCollectors() {
        if (collectStats != null) {
            return collectStats.getStatCollectors();
        } else {
            throw new RuntimeException("Tried to get stats but they are not being collected");
        }
    }

    /**
     * Just a class for a delegated proxy for draw features.
     */
    public class DrawFeatureObservable extends Observable implements Serializable {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        /**
         * Have to provide this silly method because set changed is protected
         * for some reason.
         */
        public void setChanged() {
            super.setChanged();
        }
    };

    /**
     * A delegate keeping track of observers of draw features.
     */
    private DrawFeatureObservable drawFeatureObservable = new DrawFeatureObservable();

    /**
     * Adds the provided draw feature to this scape.
     * 
     * @see org.ascape.util.vis.awt.DrawFeature
     */
    public void addDrawFeature(PlatformDrawFeature feature) {
        // Simple linear search...
        // todo, replace with hashmap mechanism
        for (Iterator iterator = drawFeatures.iterator(); iterator.hasNext();) {
            PlatformDrawFeature drawFeature = (PlatformDrawFeature) iterator.next();
            if (drawFeature.getName().equals(feature.getName())) {
                // ignore, don't add feature with same name twice.
                return;
            }
        }
        drawFeatures.addElement(feature);
        drawFeatureObservable.setChanged();
        drawFeatureObservable.notifyObservers();
    }

    /**
     * Removes the provided draw feature.
     * 
     * @param feature the draw feature to be removed
     * @return returns true if successful. False, otherwise.
     */
    public boolean removeDrawFeature(PlatformDrawFeature feature) {
        PlatformDrawFeature found = null;
        // todo, replace with hashmap mechanism
        for (Iterator iterator = drawFeatures.iterator(); iterator.hasNext();) {
            PlatformDrawFeature drawFeature = (PlatformDrawFeature) iterator.next();
            if (drawFeature.getName().equals(feature.getName())) {
                found = feature;
            }
        }
        if (found != null) {
            drawFeatures.removeElement(found);
            drawFeatureObservable.setChanged();
            drawFeatureObservable.notifyObservers();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns an observable delegate that notifies users of draw features that
     * a change has occurred. If you need to know when a change in draw features
     * occur, implement observer in the appropriate class and add it to the
     * Observerable this method returns.
     */
    public Observable getDrawFeaturesObservable() {
        return drawFeatureObservable;
    }

    /**
     * Returns, as a vector, the draw features available for interpretation of
     * members of this scape.
     * 
     * @see org.ascape.util.vis.awt.DrawFeature
     */
    public Vector getDrawFeatures() {
        return drawFeatures;
    }

    /**
     * Returns the user environment for this scape. Returns null if no user
     * environmnet exists; that is if we are running in a non graphic context.
     * Otherwise, returns the same environmnet as getRuntimeEnvironment, except
     * cast apprpriatly.
     */
    public AbstractUIEnvironment getUIEnvironment() {
        if (getRunner() != null && getRunner().getEnvironment() instanceof AbstractUIEnvironment) {
            return (AbstractUIEnvironment) getRunner().getEnvironment();
        } else {
            return null;
        }
    }

    /**
     * Returns the runtime environment, if any, for this scape.
     */
    public RuntimeEnvironment getEnvironment() {
        return getRunner() != null ? getRunner().getEnvironment() : null;
    }

    /**
     * A class defining a rule causing the target scape to return all accessors.
     */
    class RetrieveAccessorsRule extends Rule {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        List accessors;

        public RetrieveAccessorsRule() {
            super("Retrieve Accessors Rule");
        }

        /**
         * @param agent the target scape
         */
        public void execute(Agent agent) {
            try {
                accessors.addAll(PropertyAccessor.determineReadWriteAccessors(this, Scape.class, false));
            } catch (IntrospectionException e) {
                getEnvironment().getConsole().println(
                                                      "An introspection exception occured while trying to determine model properties: "
                                                      + e.getMessage());
            }
        }
    }

    /**
     * A class defining a rule causing the target scape to search for all scapes
     * and members scapes accessors.
     */
    // class RetrieveAllAccessorsRule extends PropogateScapeOnly {
    class RetrieveAllAccessorsRule extends Rule {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        List accessors;

        public RetrieveAllAccessorsRule() {
            super("Retrieve Accessors Rule");
            accessors = new ArrayList();
        }

        /**
         * @param agent the target scape
         */
        public void execute(Agent agent) {
            if (((Scape) agent).isMembersActive()) {
                try {
                    accessors.addAll(PropertyAccessor.determineReadWriteAccessors(agent, Scape.class, false));
                } catch (IntrospectionException e) {
                    getEnvironment().getConsole().println(
                                                          "An introspection exception occured while trying to determine model properties: "
                                                          + e.getMessage());
                }
                // super.execute(agent);
            }
        }
    }

    /**
     * A class defining a rule causing the target scape to search for all scapes
     * and members scapes accessors.
     */
    class RetrieveAllScapesRule extends PropogateScapeOnly {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        Vector scapes;

        public RetrieveAllScapesRule() {
            super("Retrieve Scapes Rule");
            scapes = new Vector();
        }

        /**
         * @param agent the target scape
         */
        public void execute(Agent agent) {
            scapes.addElement(agent);
            super.execute(agent);
        }
    }

    /**
     * Returns all property accessors for this scape and recursivly for all
     * member scapes of this scape.
     */
    private List retrieveAllAccessorsBase() {
        RetrieveAllAccessorsRule retrieveRule = new RetrieveAllAccessorsRule();
        executeOnRoot(retrieveRule);
        retrieveRule.accessors.add(new PropertyAccessor(this, "RandomSeed"));
        retrieveRule.accessors.add(new PropertyAccessor(this, "StartPeriod"));
        retrieveRule.accessors.add(new PropertyAccessor(this, "StopPeriod"));
        retrieveRule.accessors.add(new PropertyAccessor(this, "PausePeriod"));
        retrieveRule.accessors.add(new PropertyAccessor(this, "ThreadCount"));
        return retrieveRule.accessors;
    }

    /**
     * Returns all property accessors for this scape and recursivly for all
     * member scapes of this scape.
     */
    public List retrieveAllAccessors() {
        List accessors = retrieveAllAccessorsBase();
        return accessors;
    }

    public final static Comparator COMPARE_ORDERED_QUALIFIERS = new Comparator() {
        public int compare(Object o1, Object o2) {
            return Utility.orderedQualifiers(((PropertyAccessor) o1).getLongName()).compareTo(
                                                                                              Utility.orderedQualifiers(((PropertyAccessor) o2).getLongName()));
        }
    };

    /**
     * Returns all property accessors for this scape and recursivly for all
     * member scapes of this scape.
     */
    public List retrieveAllAccessorsOrdered() {
        List accessors = retrieveAllAccessorsBase();
        Collections.sort(accessors, COMPARE_ORDERED_QUALIFIERS);
        return accessors;
    }

    /**
     * Returns all property accessors for this scape (excluding
     * inappropriate/disabled accessors such as size) and recursivly for all
     * member scapes of this scape. Can be overriden to only include those
     * accessors that should be included in model definitions.
     */
    public List retrieveModelAccessorsOrdered() {
        List accessors = retrieveAllAccessorsOrdered();
        for (Iterator iterator = accessors.iterator(); iterator.hasNext();) {
            PropertyAccessor accessor = (PropertyAccessor) iterator.next();
            if (accessor.getName().equalsIgnoreCase("size")) {
                iterator.remove();
            }
        }
        return accessors;
    }

    /**
     * Returns all scapes that are composed with this scape. All subscapes,
     * parent scapes, and subscapes of parent scapes (more simply, the root
     * scape and all of its subscapes) are returned.
     */
    public List getAllScapes() {
        RetrieveAllScapesRule retrieveRule = new RetrieveAllScapesRule();
        executeOnRoot(retrieveRule);
        return retrieveRule.scapes;
    }

    /**
     * Does the scape view itself? True by default for root scape when
     * createViews is used, false otherwise.
     */
    public boolean isViewSelf() {
        return selfView != null;
    }

    /**
     * Sets wether the scape is a view of itself. True by default for root scape
     * whn createViews is used, false otherwise. Not extensively tested yet.
     * 
     * @param viewSelf should the scape view itself.
     */
    public void setViewSelf(boolean viewSelf) {
        if (viewSelf && !isViewSelf()) {
            createSelfView();
        } else if (!viewSelf && isViewSelf()) {
            removeScapeListener(selfView);
        }
    }

    /**
     * Makes the scape a view of itself.
     */
    public void createSelfView() {
        selfView = new ScapeListenerDelegate(this) {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            public String getName() {
                return this + " Self-View";
            }
        };
        addView(selfView);
    }

    /**
     * Constructs the views for this scape. If display graphics is set to true,
     * calls create graphic views. Calls create nongraphic views in either case.
     * Override to create views for the scape. Alternativly, override the
     * createGraphicsViews and createNonGraphicViews methods to create views
     * appropriate for the current operating mode. This method does NOT get
     * called when a model is deserialized, but createGraphicViews does.
     */
    public void createViews() {
        if (isRoot() && getRunner().getEnvironment() == null) {
            getRunner().createEnvironment();
            addView(getRunner().getEnvironment());
        }
        createNonGraphicViews();
        if (Runner.isDisplayGraphics() || Runner.isServeGraphics()) {
            // SwingUtilities.invokeLater(new Runnable() {
            // public void run() {
            createGraphicViews();
            // }
            // });
        }
    }

    /**
     * Override to create any graphical views for the scape. This method will
     * not be called when display graphics is set to false, and so is a good
     * place to put any user interface only views. If root, will setup the user
     * interface environment and add an auto customizer.
     */
    public void createGraphicViews() {
    }

    /**
     * Overide to create and non-graphical views for the scape. If root, will
     * automatically create control and counter views, create a self view, add a
     * standand output view.
     */
    public void createNonGraphicViews() {
        if (isRoot()) {
            createSelfView();
            getEnvironment().getConsole().println("Ascape Model: " + getName());
            getEnvironment().getConsole().println(getDescription());
        }
    }

    /*
     * public PropertyAccessor[] retrieveAccessors(PropertyAccessor[] accessors)
     * throws IntrospectionException { return
     * retrieveAccessors(PropertyAccessor.determineAccessors(this,
     * Model.class)); }
     */

    /**
     * If the scape has delegated a view to itself, called each time a scape
     * sends a "initialize" event, indicating it has been initialized. Normally
     * wouldn't use in this context.
     */
    public void scapeInitialized(ScapeEvent scapeEvent) {
    }

    /**
     * If the scape has delegated a view to itself, called each time a scape
     * sends a "setup" method, indicating it needs to be setup for a run.
     * Possible uses include setting initial vector extents, responding to
     * changes in user settings, and changing parameters systematically. (A view
     * delegate to the scape is automatically created for root scapes when the
     * standard model implementation is used.)
     * 
     * @param scapeEvent the associated scape event
     */
    public void scapeSetup(ScapeEvent scapeEvent) {
    }

    /**
     * If the scape has delegated a view to itself, called each time the scape
     * is iterated. (A view delegate to the scape is automatically created for
     * root scapes when the standard model implementation is used.)
     * 
     * @param scapeEvent the associated scape event
     */
    public void scapeIterated(ScapeEvent scapeEvent) {
    }

    /**
     * If the scape has delegated a view to itself, called each time the scape
     * is started. (A view delegate to the scape is automatically created for
     * root scapes when the standard model implementation is used.)
     * 
     * @param scapeEvent the associated scape event
     */
    public void scapeStarted(ScapeEvent scapeEvent) {
    }

    /**
     * If the scape has delegated a view to itself, called each time the scape
     * is stopped. (A view delegate to the scape is automatically created for
     * root scapes when the standard model implementation is used.)
     * 
     * @param scapeEvent the associated scape event
     */
    public void scapeStopped(ScapeEvent scapeEvent) {
    }

    /**
     * If the scape has delegated a view to itself, called each time the scape
     * is updated. (A view delegate to the scape is automatically created for
     * root scapes when the standard model implementation is used.)
     * 
     * @param scapeEvent the associated scape event
     */
    public void scapeNotification(ScapeEvent scapeEvent) {
    }

    /**
     * If the scape has delegated a view to itself, called each time a scape
     * sends a "closing" event. Normally wouldn't use in this context.
     * 
     * @param scapeEvent the associated scape event
     */
    public void scapeClosing(ScapeEvent scapeEvent) {
    }

    /**
     * Method called as the entire envornmnet is about to be exited.
     * 
     * @param scapeEvent the associated scape event
     */
    public void environmentQuiting(ScapeEvent scapeEvent) {
    }

    /**
     * If the scape has delegated a view to itself, called each time a scape
     * sends a "deserialized" event.
     * 
     * @param scapeEvent the associated scape event
     */
    public void scapeDeserialized(ScapeEvent scapeEvent) {
        if (Runner.isDisplayGraphics()) {
            if (isRoot()) {
                getRunner().createEnvironment();
                addView(getRunner().getEnvironment());
            }
        }
    }

    /**
     * Add a scape to this listener. Just here to fulfill the scape listener
     * contract.
     * 
     * @param scapeEvent the associated scape event
     */
    public void scapeAdded(ScapeEvent scapeEvent) throws TooManyListenersException {
    }

    /**
     * Notifies the listener that the scape has removed it. Just here to fulfill
     * the scape listener contract.
     * 
     * @param scapeEvent the associated scape event
     */
    public void scapeRemoved(ScapeEvent scapeEvent) {
    }

    /**
     * Returns false the scape is not a graphical user interface component.
     */
    public boolean isGraphic() {
        return false;
    }

    /**
     * Returns true (default) if the listener is intended to be used only for
     * the current scape; certainly true in this case.
     */
    public boolean isLifeOfScape() {
        return true;
    }

    /*
     * Returns the ascape home directory. public static String getAscapeHome() {
     * //return System.getProperty("ascape.home", "D:/"); return ""; }
     */

    /**
     * Save the state of the scape to a file.
     */
    public void save(File file) throws IOException {
        OutputStream os = new FileOutputStream(file);
        save(os);
    }

    /**
     * Save the state of the scape to an output stream.
     */
    public void save(OutputStream os) throws IOException {
        if (!isSerializable()) {
            throw new RuntimeException("Tried to save a model that is not serializable.");
        }

        getRunner().setInternalRunning(false);

        GZIPOutputStream gzos = new GZIPOutputStream(os);
        ObjectOutputStream oos = new ObjectOutputStream(gzos);

        // remove the customizer and any environment (non-scape specific) views
        boolean needToAddCustomizer = false;
        if (getUIEnvironment() != null && getUIEnvironment().getCustomizer() != null
                && isScapeListener(getUIEnvironment().getCustomizer())) {
            removeScapeListener(getUIEnvironment().getCustomizer());
            needToAddCustomizer = true;
        }
        for (int i = 0; i < getEnvironment().getEnvironmentViews().size(); i++) {
            ScapeListener l = (ScapeListener) getEnvironment().getEnvironmentViews().get(i);
            l.getScape().removeScapeListener(l);
        }

        try {
            oos.writeObject(this);
        } catch (StackOverflowError e) {
            e.printStackTrace();
            System.err.println("");
            System.err.println("************************************");
            throw new RuntimeException("PLEASE INCREASE STACK SIZE, e.g. by using java's -Xss command line paramter.");
        }
        oos.close();

        // reconnect the customizer and any environment (non-scape specific)
        // views
        if (needToAddCustomizer) {
            addScapeListener(getUIEnvironment().getCustomizer());
        }
        for (int i = 0; i < getEnvironment().getEnvironmentViews().size(); i++) {
            ScapeListener l = (ScapeListener) getEnvironment().getEnvironmentViews().get(i);
            addView(l, false);
        }

        // we set startPeriod to scape.period + 1 so that there is not a blank
        // first point in the charts
        try {
            setStartPeriod(getPeriod() + 1);
        } catch (SpatialTemporalException e) {
            try {
                setStartPeriod(getPeriod());
            } catch (SpatialTemporalException e1) {
                try {
                    setStartPeriod(getPeriod());
                } catch (SpatialTemporalException e2) {
                    throw new RuntimeException("Internal Error");
                }
            }
        }
        getRunner().setInternalRunning(true);
    }

    private void writeObject(final java.io.ObjectOutputStream out) throws IOException {

        if (getRunner().getData() != null) {
            getRunner().getData().getPeriods().clear();
        }

        getRunner().write(out);
    }

    /**
     * Sets values for the models paramters based on supplied array of key value
     * pairs. Paramters and values are expected to be seperated with an "=", for
     * example: "MyParameter=12".
     * 
     * @param args an array of strings with paramter-value paris in the form
     *            "{paramter-name}={paramter-value}"
     * @param reportNotFound if paramters not found should result in a console
     *            notification and if errors in invocation should be reported,
     *            false otherwise
     */
    public void assignParameters(String[] args, boolean reportNotFound) {
        // List allAccessors = retrieveAllAccessors();
        List allAccessors = null;
        try {
            allAccessors = PropertyAccessor.determineReadWriteAccessors(this, Scape.class, false);
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }

        for (String arg : args) {
            String paramName = PropertyAccessor.paramName(arg);
            if (paramName != null) {
                boolean found = false;
                String paramValue = PropertyAccessor.paramValue(arg);
                for (Iterator iterator = allAccessors.iterator(); iterator.hasNext();) {
                    PropertyAccessor accessor = (PropertyAccessor) iterator.next();
                    if (accessor.getName().equalsIgnoreCase(paramName)) {
                        try {
                            accessor.setAsText(paramValue);
                        } catch (InvocationTargetException e) {
                            if (reportNotFound) {
                                throw new RuntimeException("Exception in called method: " + e.getTargetException());
                            }
                            // Else ignore, its ok if there is a problem calling
                            // the method at this point
                        }
                        found = true;
                    }
                }

                found = found || Runner.assignEnvironmentParameter(paramName, paramValue);
                if (!found) {
                    if (paramName.equalsIgnoreCase("RandomSeed")) {
                        try {
                            setRandomSeed(PropertyAccessor.paramValueLong(arg));
                        } catch (NumberFormatException e) {
                            getEnvironment().getConsole().println("Couldn't decode random seed value: " + paramValue);
                        }
                        found = true;
                    } else if (paramName.equalsIgnoreCase("StopPeriod")) {
                        try {
                            setStopPeriod(PropertyAccessor.paramValueInt(arg));
                            found = true;
                        } catch (SpatialTemporalException e) {
                            e.printStackTrace(); // To change body of catch
                            // statement use File | Settings
                            // | File Templates.
                        }
                    } else if (paramName.equalsIgnoreCase("PausePeriod")) {
                        setPausePeriod(PropertyAccessor.paramValueInt(arg));
                        found = true;
                    } else if (paramName.equalsIgnoreCase("AutoRestart")) {
                        setAutoRestart(PropertyAccessor.paramValueBoolean(arg));
                        found = true;
                    }
                }
                if (!found && reportNotFound) {
                    getEnvironment().getConsole().println(
                                                          "***WARNING: Parameter not found: " + paramName + " in " + getName());
                }
            }
        }
    }

    public void createViews(String[] args) {
        if (args != null) {
            for (String arg : args) {
                if (PropertyAccessor.paramName(arg).equals("view")) {
                    ScapeListener newView =
                        (ScapeListener) getRunner().instanceFromName(PropertyAccessor.paramValue(arg));
                    if (newView != null) {
                        addView(newView);
                    }
                }
            }
        }
    }

    /**
     * Sets values for the models paramters based on supplied array of key value
     * pairs, reporting if any of the keys (parameter names) are not found.
     * Paramters and values are expected to be seperated with an "=", for
     * example: "MyParameter=12".
     * 
     * @param args an array of strings with paramter-value paris in the form
     *            "{paramter-name}={paramter-value}"
     */
    public void assignParameters(String[] args) {
        assignParameters(args, true);
    }

    /**
     * Moves an agent toward the specified agent.
     * 
     * @param origin the agent moving
     * @param target the agent's target
     * @param distance the distance to move
     */
    public final void moveAway(LocatedAgent origin, Coordinate target, double distance) {
        getSpace().moveAway(origin, target, distance);
    }

    /**
     * Moves an agent toward the specified agent. It is an error to call this
     * method on collections (and discrete discrete scapes not composed of
     * HostCells.
     * 
     * @param origin the agent moving
     * @param target the agent's target
     * @param distance the distance to move
     */
    public final void moveToward(LocatedAgent origin, Coordinate target, double distance) {
        getSpace().moveToward(origin, target, distance);
    }

    /**
     * Returns the shortest distance between one agent and another.
     * 
     * @param origin the starting agent
     * @param target the ending agent
     */
    public double calculateDistance(LocatedAgent origin, LocatedAgent target) {
        return calculateDistance(origin.getCoordinate(), target.getCoordinate());
    }

    /**
     * Returns the shortest distance between one LocatedAgent and another.
     * Warning: this default method only returns a coordinate specific distance.
     * It uses no information about the scape context; for example wether it is
     * a periodic (wrapping) space or not. Therefore, if you implement your own
     * versions of Scape, ensure that you have properly implemented a version of
     * this method. (All Ascape Scape collections properly overide this method.)
     * 
     * @param origin one LocatedAgent
     * @param target another LocatedAgent
     */
    public final double calculateDistance(Coordinate origin, Coordinate target) {
        return getSpace().calculateDistance(origin, target);
    }

    public class ConditionalIterator implements Iterator {

        Iterator iter;

        Conditional condition;

        Object next;

        public ConditionalIterator(Iterator iter, Conditional condition) {
            ConditionalIterator.this.iter = iter;
            ConditionalIterator.this.condition = condition;
            loadNext();
        }

        private void loadNext() {
            next = null;
            while (iter.hasNext() && next == null) {
                Object o = iter.next();
                if (condition.meetsCondition(o)) {
                    next = o;
                }
            }
        }

        public boolean hasNext() {
            return next != null;
        }

        public Object next() {
            if (next != null) {
                Object currentNext = next;
                loadNext();
                return currentNext;
            } else {
                throw new NoSuchElementException();
            }
        }

        public void remove() {
            throw new UnsupportedOperationException("Can't remove from a conditional iterator.");
        }
    }

    /**
     * Find the maximum cell of some data point. If multiple points have the
     * same value, returns a random instance at that value.
     * 
     * @param condition
     * @return
     */
    public final List find(Conditional condition) {
        return space.find(condition);
    }

    /**
     * Find the maximum cell of some data point. If multiple points have the
     * same value, returns a random instance at that value.
     * 
     * @param iter
     * @param dataPoint
     * @return
     */
    public final LocatedAgent findMaximum(final Iterator iter, DataPoint dataPoint) {
        // The code is written in such a way that there will not be the cost of
        // Array creation if only one maximum exists
        ArrayList multipleMaxObjects = null;
        double maxValue = -Double.MAX_VALUE;
        LocatedAgent maxObject = null;
        while (iter.hasNext()) {
            Object next = iter.next();
            if (dataPoint.getValue(next) > maxValue) {
                maxValue = dataPoint.getValue(next);
                maxObject = (LocatedAgent) next;
                multipleMaxObjects = null;
            }
            // Awaiting decision to become depndent on 1.4
            // else if (Double.compare(dataPoint.getValue(next), maxValue) == 0)
            // {
            else if (DataPointConcrete.equals(dataPoint.getValue(next), maxValue)) {
                if (multipleMaxObjects == null) {
                    multipleMaxObjects = new ArrayList();
                    multipleMaxObjects.add(maxObject);
                }
                multipleMaxObjects.add(next);
            }
        }
        if (multipleMaxObjects == null) {
            return maxObject;
        } else {
            return (LocatedAgent) multipleMaxObjects.get(randomToLimit(multipleMaxObjects.size()));
        }
    }

    public final LocatedAgent findMinimumWithin(Coordinate coordinate, DataPoint dataPoint, Conditional condition,
            boolean includeSelf, double distance) {
        return (LocatedAgent) getSpace().findMinimumWithin(coordinate, dataPoint, condition, includeSelf, distance);
    }

    public final LocatedAgent findMaximumWithin(Coordinate coordinate, DataPoint dataPoint, Conditional condition,
            boolean includeSelf, double distance) {
        return (LocatedAgent) getSpace().findMaximumWithin(coordinate, dataPoint, condition, includeSelf, distance);
    }

    public final LocatedAgent findMinimum(final Iterator iter, DataPoint dataPoint) {
        // The code is written in such a way that there will not be the cost of
        // Array creation if only one minimum exists
        ArrayList multipleMinObjects = null;
        double minValue = Double.MAX_VALUE;
        LocatedAgent minObject = null;
        while (iter.hasNext()) {
            Object next = iter.next();
            if (dataPoint.getValue(next) < minValue) {
                minValue = dataPoint.getValue(next);
                minObject = (LocatedAgent) next;
                multipleMinObjects = null;
            }
            // Awaiting decision to become depndent on 1.4
            // else if (Double.compare(dataPoint.getValue(next), minValue) == 0)
            // {
            else if (DataPointConcrete.equals(dataPoint.getValue(next), minValue)) {
                if (multipleMinObjects == null) {
                    multipleMinObjects = new ArrayList();
                    multipleMinObjects.add(minObject);
                }
                multipleMinObjects.add(next);
            }
        }
        if (multipleMinObjects == null) {
            return minObject;
        } else {
            return (LocatedAgent) multipleMinObjects.get(randomToLimit(multipleMinObjects.size()));
        }
    }

    /**
     * Returns an iteration across all agents the specified distance from the
     * origin.
     * 
     * @param origin the starting cell
     * @param includeSelf should the origin be included
     * @param distance the distance agents must be within to be included
     */
    public final Iterator withinIterator(final Coordinate origin, Conditional condition, boolean includeSelf,
            final double distance) {
        return getSpace().withinIterator(origin, condition, includeSelf, distance);
    }

    /**
     * Returns the agent with the minimum value.
     * 
     * @param point the data point to use to make the comparison for minimum
     */
    public LocatedAgent findMinimum(DataPoint point) {
        return findMinimum(iterator(), point);
    }

    /**
     * Returns the agent with the maximum value.
     * 
     * @param point the data point to use to make the comparison for maximum
     */
    public LocatedAgent findMaximum(DataPoint point) {
        return findMaximum(iterator(), point);
    }

    /**
     * The strategy that will be used to execute rules across this scape.
     */
    private ExecutionStrategy strategy;

    private static int threadCount = 1;

    /**
     * Finds the nearest agent that meets some condition. Scapes without
     * coordinate meaing should override this method.
     * 
     * @param origin the coordinate to find agents near
     * @param condition the condition that found agent must meet
     * @param includeOrigin if the origin should be included
     * @param distance the maximum distance around the origin to look
     */
    public final LocatedAgent findNearest(final Coordinate origin, Conditional condition, boolean includeOrigin,
            double distance) {
        return (LocatedAgent) getSpace().findNearest(origin, condition, includeOrigin, distance);
    }

    /**
     * Returns a coordinate randomly selected from the collection's space.
     */
    public final Coordinate findRandomCoordinate() {
        return getSpace().findRandomCoordinate();
    }

    /**
     * Returns all agents within the specified distance of the agent.
     * 
     * @param origin the coordinate at the center of the search
     * @param includeSelf whether or not the starting agent should be included
     *            in the search
     * @param distance the distance agents must be within to be included
     */
    public final List findWithin(Coordinate origin, Conditional condition, boolean includeSelf, double distance) {
        return getSpace().findWithin(origin, condition, includeSelf, distance);
    }

    /**
     * Returns the number of agents within the specified distance of the agent
     * that meet some condition.
     * 
     * @param origin the coordinate at the center of the search
     * @param condition the condition the agent must meet to be included
     * @param distance the distance agents must be within to be included
     */
    public final int countWithin(Coordinate origin, Conditional condition, boolean includeSelf, double distance) {
        return getSpace().countWithin(origin, condition, includeSelf, distance);
    }

    /**
     * Returns if there are agents within the specified distance of the origin
     * that meet some Condition.
     * 
     * @param origin the coordinate at the center of the search
     * @param condition the condition the agent must meet to be included
     * @param distance the distance agents must be within to be included
     */
    public final boolean hasWithin(final Coordinate origin, Conditional condition, boolean includeSelf, double distance) {
        return getSpace().hasWithin(origin, condition, includeSelf, distance);
    }

    public final boolean isMutable() {
        return getSpace().isMutable();
    }

    /**
     * Returns a string composed of descriptions of the contents.
     */
    public String contentsToString() {
        String contents = "";
        for (Iterator iterator = space.iterator(); iterator.hasNext();) {
            Agent agent = (Agent) iterator.next();
            contents = contents + agent.toString();
            if (iterator.hasNext()) {
                contents = contents + ", ";
            }
        }
        return contents;
    }

    /**
     * Returns a string representation of this scape.
     */
    public String toString() {
        if (name != null) {
            return name;
        } else {
            if (isRoot()) {
                return "Root Scape";
            } else if (prototypeAgent != null) {
                return "Scape of " + prototypeAgent.toString() + "(s)";
            } else {
                return "Scape of agents of unspecified type";
            }
        }
    }

    public boolean isSerializable() {
        return serializable;
    }

    public void setSerializable(boolean serializable) {
        this.serializable = serializable;
    }

    /**
     * Overides the clone method to do a deep clone of member state so that such
     * state will not be shared between scapes.
     */
    public Object clone() {
        Scape clone = (Scape) super.clone();
        clone.scapeListeners = new ArrayList();
        for (Iterator iter = scapeListeners.iterator(); iter.hasNext();) {
            ScapeListener thisListener = (ScapeListener) iter.next();
            try {
                ScapeListener newListener = (ScapeListener) thisListener.clone();
                removeScapeListener(newListener);
                newListener.scapeRemoved(new ScapeEvent(this, ScapeEvent.REPORT_REMOVED));
                newListener.scapeAdded(new ScapeEvent(clone, ScapeEvent.REPORT_ADDED));
                clone.addScapeListener(newListener);
            } catch (TooManyListenersException e) {
                throw new RuntimeException("Internal error in Scape.clone " + e);
            }
        }
        if (prototypeAgent != null) {
            clone.prototypeAgent = (Agent) prototypeAgent.clone();
        }
        if (rules != null) {
            clone.rules = (VectorSelection) rules.clone();
        }
        if (initialRules != null) {
            clone.initialRules = (VectorSelection) initialRules.clone();
        }
        clone.drawFeatures = (Vector) drawFeatures.clone();
        clone.space = (CollectionSpace) space.clone();
        return clone;
    }

    /**
     * Returns an agent randomly selected from the collection. If no agents
     * exist, returns null.
     */
    public final LocatedAgent findRandom() {
        return (LocatedAgent) getSpace().findRandom();
    }

    /**
     * Returns a random unoccupied discrete location in the space given with the
     * lattice.
     * 
     * @param excludeAgent a cell to exclude from get (typically origin)
     */
    public Agent findRandom(Location excludeAgent) {
        return (LocatedAgent) getSpace().findRandom(excludeAgent);
    }

    /**
     * Returns an agent randomly that matches a condition. Note: If there are no
     * agents in the collection that meet the condition, the method returns
     * null.
     * 
     * @param condition the condition that must be matched
     */
    public final Agent findRandom(Conditional condition) {
        return (LocatedAgent) getSpace().findRandom(condition);
    }

    /**
     * Creates a new agent in this collection by cloning the prototype agent,
     * adding it in an arbitrary place (typically at the end of a list), and
     * initializing it.
     */
    public synchronized Agent newAgent() {
        return newAgent(false);
    }

    /**
     * Creates a new agent in this collection by cloning the prototype agent,
     * adding it to a random or arbitrary (last in most cases) place in the
     * collection, and initializing it.
     * 
     * @param randomLocation should the agent be placed in a random location, or
     *            in an arbitrary location?
     */
    public synchronized Agent newAgent(boolean randomLocation) {
        Agent newAgent = (LocatedAgent) getSpace().newLocation(randomLocation);
        List agents = new LinkedList();
        agents.add(newAgent);
        execute(getInitialRules().getVector(), agents);
        return newAgent;
    }

    /**
     * Returns the number of agents in the scape.
     * 
     * @return the number of agents in the scape
     */
    public int size() {
        return getSize();
    }

    /**
     * Are there no agents in this scape?
     * 
     * @return true if the scape is empty
     */
    public final boolean isEmpty() {
        return getSpace().isEmpty();
    }

    /**
     * Returns true if the scape collection contains the object (agent.)
     * 
     * @param o the agent to search for
     * @return true if the scape contains the agent
     */
    public final boolean contains(Object o) {
        return getSpace().contains(o);
    }

    /**
     * Returns an array containing all of the elements in this collection in
     * proper sequence. Obeys the general contract of the
     * <tt>Collection.toArray</tt> method.
     * 
     * @return an array containing all of the elements in this collection in
     *         proper sequence.
     * @see java.util.Arrays#asList(java.lang.Object[])
     */
    public final Object[] toArray() {
        return getSpace().toArray();
    }

    /**
     * Returns an array containing the current agents in this scape; the runtime
     * type is specified by the passed array.
     * 
     * @param a the array to copy the agents to
     * @return an array containing the agents
     * @throws java.lang.ArrayStoreException if the runtime type of the
     *             specified array doesn't match all agents
     */
    public final Object[] toArray(Object a[]) {
        return getSpace().toArray(a);
    }

    /**
     * Returns true if this collection contains all of agents in the specified
     * collection.
     * 
     * @param c collection of agents to be found in the scape
     * @return true if this scape contains all of the agents in the collection
     */
    public final boolean containsAll(Collection c) {
        return getSpace().containsAll(c);
    }

    /**
     * Adds all of the agent in the specified collection to the end of the
     * scape. Assumes (but does not check) that all of the elements are
     * instances of agent.
     * 
     * @param c collection whose agents are to be added to the scape
     * @return true if the scape had new agents added
     */
    public final boolean addAll(Collection c) {
        return getSpace().addAll(c);
    }

    /**
     * Removes all of the agnets contained in the collection. No attempt is made
     * to cache the removal; the agents are all removed at once.
     * 
     * @param c collection whose agents are to be added to the scape
     * @return true if the scape had agents (but not neccessarily all?) removed
     */
    public final boolean removeAll(Collection c) {
        return getSpace().removeAll(c);
    }

    /**
     * Retains only the elements in the scape that are in the specified
     * collection.
     * 
     * @param c collection whose agents are to be retained in the scape
     * @return true if this scape had agents removed
     */
    public final boolean retainAll(Collection c) {
        return getSpace().retainAll(c);
    }

    /**
     * Removes all agents from the scape.
     */
    public final void clear() {
        getSpace().clear();
    }

    /**
     * Adds the supplied object (agent) to this collection.
     */
    public boolean add(Object a) {
        return add(a, true);
    }

    /**
     * Adds the supplied object (assumed to be an agent) to this collection. The
     * object is assumed to be an agent, though that behavior may be loosened at
     * some point.
     * 
     * @param agent the agent to add
     * @param isParent should this scape be made the parent scape of the agent?
     * @throws UnsupportedOperationException if this scape's space is not a
     *             list.
     */
    public final boolean add(Object agent, boolean isParent) {
        if (!(agent instanceof Agent)) {
            // This may change at some point..
            throw new ClassCastException("Scape collections expect Agents only.");
        }
        boolean success = getSpace().add(agent, isParent);
        if (isParent) {
            ((Agent) agent).setScape(this);
        }
        return success;
    }

    /**
     * Adds the supplied object (agent) to this collection.
     * 
     * @throws UnsupportedOperationException if this scape's space is not a
     *             list.
     */
    public final void add(int index, Object a) {
        add(index, a, true);
    }

    /**
     * Adds the supplied object (assumed to be an agent) to this collection. The
     * object is assumed to be an agent, though that behavior may be loosened at
     * some point.
     * 
     * @param o the agent to add
     * @param isParent should this scape be made the parent scape of the agent?
     * @throws UnsupportedOperationException if this scape's space is not a
     *             list.
     */
    public final void add(int index, Object o, boolean isParent) {
        try {
            ((ListSpace) getSpace()).add(index, o, isParent);
            if (isParent) {
                ((Agent) o).setScape(this);
            }
        } catch (ClassCastException e) {
            throw new UnsupportedOperationException("Underlying space is not a list and cannot be accessed randomly.");
        }
    }

    /**
     * Removes the supplied object (agent) from this collection.
     * 
     * @param o the agent to be removed
     * @return true if the agent was deleted, false otherwise
     */
    public boolean remove(Object o) {
        return space.remove(o);
    }

    /**
     * Removes the object at the index from this collection.
     * 
     * @param index the index for the agent to remove
     * @throws UnsupportedOperationException if this scape's space is not a
     *             list.
     */
    public final Object remove(int index) {
        try {
            return ((List) getSpace()).remove(index);
        } catch (ClassCastException e) {
            throw new UnsupportedOperationException("Underlying space is not a list and cannot be accessed randomly.");
        }
    }

    /**
     * Returns the cell existing at the specified coordinate.
     */
    public final LocatedAgent get(Coordinate coordinate) {
        return (LocatedAgent) getSpace().get(coordinate);
    }

    /**
     * Sets the agent at the specified coordinate to the supplied agent.
     * 
     * @param coordinate the coordinate to add the agent at
     * @param agent the agent to add
     */
    public final void set(Coordinate coordinate, LocatedAgent agent, boolean isParent) {
        getSpace().set(coordinate, agent);
        if (isParent) {
            agent.setScape(getScape());
            agent.setCoordinate(coordinate);
        }
    }

    /**
     * Sets the agent at the specified coordinate to the supplied agent.
     * 
     * @param coordinate the coordinate to add the agent at
     * @param agent the agent to add
     */
    public void set(Coordinate coordinate, LocatedAgent agent) {
        set(coordinate, agent, true);
    }

    /**
     * Returns the cell existing at the specified location. Convenience method.
     * 
     * @throws UnsupportedOperationException if this scape's space is not a
     *             list.
     */
    public final Object get(int index) {
        try {
            return ((ListBase) getSpace()).get(index);
        } catch (ClassCastException e) {
            throw new UnsupportedOperationException("Underlying space is not a list and cannot be accessed randomly.");
        }
    }

    /**
     * Sets the specified location to the provided agent. Convenience method.
     * 
     * @throws UnsupportedOperationException if this scape's space is not a
     *             list.
     */
    public void set(int index, Object agent) {
        set(index, agent, true);
    }

    /**
     * Sets the specified location to the provided agent. Convenience method.
     * 
     * @throws UnsupportedOperationException if this scape's space is not a
     *             list.
     */
    public void set(int index, Object agent, boolean isParent) {
        try {
            ((ListBase) getSpace()).set(index, (LocatedAgent) agent, isParent);
            if (isParent) {
                ((Agent) agent).setScape(this);
            }
        } catch (ClassCastException e) {
            throw new UnsupportedOperationException("Underlying space is not a list and cannot be accessed randomly.");
        }
    }

    public final ResetableIterator scapeIterator() {
        return getSpace().safeIterator();
    }

    public final RandomIterator scapeRandomIterator() {
        return getSpace().safeRandomIterator();
    }

    protected final ResetableIterator scapeIterator(int start, int limit) {
        return getSpace().safeIterator(start, limit);
    }

    public boolean isPeriodic() {
        return getSpace().isPeriodic();
    }

    public void setPeriodic(boolean periodic) {
        getSpace().setPeriodic(periodic);
    }

    public Scape getSuperScape() {
        return getSpace() instanceof SubSpace ? (Scape) ((SubSpace) getSpace()).getSuperSpace().getContext() : null;
    }

    public void setSuperScape(Scape superScape) {
        try {
            ((SubSpace) getSpace()).setSuperSpace(superScape.getSpace());
        } catch (ClassCastException e) {
            throw new UnsupportedOperationException("Underlying scape is no a SubSpace.");
        }
    }

    /**
     * Returns multiple independently thread safe scape iterators across all
     * agents in this scape.
     * 
     * @return an iterator over the agents in scape order
     */
    public final ResetableIterator[] scapeIterators(int count) {
        return getSpace().safeIterators(count);
    }

    public boolean isListenersAndMembersCurrent() {
        return listenersAndMembersCurrent;
    }

    public final Space getSpace() {
        return space;
    }

    public void setSpace(Space space) {
        this.space = space;
        // todo remove circular dependency
        space.setContext(this);
        space.setRandom(getRandom());
    }

    /**
     * Sets the size of the collection, filling with clones of prototype agent.
     * It is an error to set size while a scape is running.
     * 
     * @param size a coordinate describing the size of this scape
     */
    public void setSize(int size) {
        if (runner != null && runner.isRunning()) {
            throw new RuntimeException("Tried to set size while scape was running");
        }
        space.setSize(size);
    }

    public int getThreadCount() {
        return Scape.threadCount;
    }

    public void setThreadCount(int threadCount) {
        Scape.threadCount = threadCount;
    }

    public Location getPrototype() {
        return (Location) getPrototypeAgent();
    }

    public boolean isHome(Location a) {
        return ((Agent) a).getScape() == this;
    }

    /**
     * Convenience method for obtaining sata for current run.
     * 
     * @return the Runner's Data Group.
     */
    public DataGroup getData() {
        return getRunner().getData();
    }

    /**
     * Returns the UI Environment.
     * 
     * @return a UI environment appropriate for given UI.
     * @deprecated retained for backward compatability, please use
     *             #getUIEnvironment instead.
     */
    public AbstractUIEnvironment getUserEnvironment() {
        return getUIEnvironment();
    }
}
