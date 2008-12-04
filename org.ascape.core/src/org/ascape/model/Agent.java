/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */

package org.ascape.model;

import java.awt.Color;
import java.awt.Image;

import org.ascape.model.rule.ExecuteThenUpdate;
import org.ascape.model.rule.Propogate;
import org.ascape.model.rule.Rule;
import org.ascape.model.space.Mutable;

/**
 * The base class for all modeled objects in ascape.
 * Note that an ascape agent is actually a superset of the general
 * understanding of a software 'agent'. Ascape agents need not be
 * mobile or autonomous, but are always capable of being so.
 * For instance, a lattice cell is a subclass of this class and within its
 * own lattice has no opportunity to move, obviously. The same cell, however,
 * may move upon some other lattice or collection of agents.
 * <BR>To represent agent state simply provide member variables along with
 * getters and setters in subclasses, and create StatCollectors to make aggregate values available.
 * To provide behavior add rules to the parent scape. A simple way to do this is to
 * override the <code>scapeCreated</code> method. There are a number of rules that
 * act simply to call a cooresponding method or methods in Agent. For instance,
 * <code>METABOLISM_RULE</code> will call the <code>metabolism</code> method on
 * the appropriate agents<BR>.
 * <code>ITERATE_RULE</code> is added to the parent scape by default, and calls the
 * <code>iterate</code> method, but it is typically preferrable to use more granular
 * and flexible rules. To get rid of this iterate rule, overide <code>scapeCreated</code>, or
 * call <code>clearRules</code> on the parent scape. You should do this whenever
 * agent level processing is not required for a given scape; it can take time to iterate
 * through some scapes, and there is no way for the engine to determine at runtime that
 * an iterate method is a non-op.
 *
 * @author Miles Parker
 * @version 1.2.5
 * @history 1.2.5 9/1/1999 added support for drawing images by implementing ImageFeature
 * @history 1.2 7/1/99 Made default rules memebers of Agent, documentation update
 * @history 1.0.2 2/1/99 numerous changes and fixes
 * @history 1.0.1 11/9/98 changed addRule method to scapeCreated
 * @since 1.0
 */
public class Agent extends AscapeObject implements Cloneable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * A rule causing the target and all its children scapes to be initialized.
     */
    public static final Rule INITIALIZE_RULE = new Propogate("Initialize") {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        /**
         * Initialize the scape or agent and all its children.
         * @param agent the playing agent
         * @see Agent#initialize()
         */
        public void execute(Agent agent) {
            agent.initialize();
            super.execute(agent);
        }

        /**
         * Ensure that all agents are initialized even if an iterations per cycle is set.
         */
        public boolean isIterateAll() {
            return true;
        }
    };

    /**
     * A rule calling the update method of the target agent.
     * The update method is intended to provide a means for performing basic
     * agent housekeeping, and <i>not</i> behavior that could change the state
     * of other agents or the lattice.
     */
    public static final Rule UPDATE_RULE = new Rule("Update") {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        /**
         * Call update on each agent.
         * @param agent the playing agent
         * @see Agent#update()
         */
        public void execute(Agent agent) {
            agent.update();
        }

        /**
         * Returns false.
         */
        public boolean isRandomExecution() {
            return false;
        }

        /**
         * Returns false.
         */
        public boolean isCauseRemoval() {
            return false;
        }
    };
    /**
     * A rule calling the iterate method on each agent in a scape and then the
     * update method on each agent in a seperate, subsequent step.
     * This rule is designed to facilitate simple synchronous updating. By
     * calculating a temporary value in the iterate method for each agent,
     * and then assiging that value to the agent permanentaly in a subsequent
     * step, synchronous (effectivly simultaneous) processes can be modelled.
     * For this to work properly, the scape's execution order must be by rule, not by agent.
     */
    public static final Rule ITERATE_AND_UPDATE_RULE = new ExecuteThenUpdate("Iterate and Update") {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        /**
         * Call iterate on each agent.
         * @param agent the playing agent
         * @see Agent#update()
         */
        public void execute(Agent agent) {
            agent.iterate();
        }

        /**
         * Call update on each agent.
         * @param agent the playing agent
         * @see Agent#update()
         */
        public void update(Agent agent) {
            agent.update();
        }
    };

    /**
     * A rule calling the death method of the target agent.
     */
    public static final Rule DEATH_RULE = new Rule("Death") {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        /**
         * Call the death method for each agent in the scape.
         * @param agent the playing agent
         * @see Agent#death
         */
        public void execute(Agent agent) {
            agent.death();
        }

        /**
         * Returns false, death does not typically need to occur randomly.
         */
        public boolean isRandomExecution() {
            return false;
        }

        /**
         * How true!
         */
        public boolean isCauseRemoval() {
            return true;
        }
    };

    /**
     * A rule calling the die method of the target agent.
     * Kills the agent regardless of deathCondition.
     */
    public static final Rule FORCE_DIE_RULE = new Rule("Force Death") {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        /**
         * Call the death method for each agent in the scape.
         * @param agent the playing agent
         * @see Agent#death
         */
        public void execute(Agent agent) {
            agent.die();
        }

        /**
         * Returns false, death does not typically need to occur randomly.
         */
        public boolean isRandomExecution() {
            return false;
        }

        /**
         * How true!
         */
        public boolean isCauseRemoval() {
            return true;
        }
    };

    /**
     * A rule calling the fissioning method of the target agent.
     * By default, this calls the fissioning rule, which calls move if the agent's
     * fissionCondition method returns true.
     */
    public static final Rule FISSIONING_RULE = new Rule("Fissioning") {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        /**
         * Execute fission rule for all agents in scape.
         */
        public void execute(Agent agent) {
            agent.fissioning();
        }

        /**
         * Returns true. Typically fission must occur in random order,
         * since it may effect the availability of resources or occupation of new sites.
         */
        public boolean isRandomExecution() {
            return true;
        }

        /**
         * Returns false. Typically fission does not cause removal.
         */
        public boolean isCauseRemoval() {
            return false;
        }
    };

    /**
     * A rule calling the fission method of the target agent.
     * Forces the agent to fission, regardless of fission condition.
     */
    public static final Rule FORCE_FISSION_RULE = new Rule("Force Fission") {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        /**
         * Execute fission rule for all agents in scape.
         */
        public void execute(Agent agent) {
            agent.fission();
        }

        /**
         * Returns true. Typically fission must occur in random order,
         * since it may effect the availability of resources or occupation of new sites.
         */
        public boolean isRandomExecution() {
            return true;
        }

        /**
         * Returns false. Typically fission does not cause removal.
         */
        public boolean isCauseRemoval() {
            return false;
        }
    };

    /**
     * An rule calling the metabolism method of the target agent.
     */
    public static final Rule METABOLISM_RULE = new Rule("Metabolism") {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        /**
         * Execute metabolism rule for all agents in scape.
         */
        public void execute(Agent agent) {
            agent.metabolism();
        }

        /**
         * Returns false. Typically metabolism does not have to be performed in random order.
         */
        public boolean isRandomExecution() {
            return false;
        }

        /**
         * Returns false. Typically metabolism does not cause removal directly.
         */
        public boolean isCauseRemoval() {
            return false;
        }
    };

    /**
     * An rule calling the default movement method of the target agent.
     * By default, this calls the movement condition rule, which calls move if the agent's
     * movementCondition method returns true.
     */
    public static final Rule MOVEMENT_RULE = new Rule("Movement") {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        /**
         * Call move for each agent in the scape.
         * @param agent the moving agent
         */
        public void execute(Agent agent) {
            agent.movement();
        }

        /**
         * Returns true. Movement rules almost always must be random, as occupation
         * of space within the lattice affects other agents.
         */
        public boolean isRandomExecution() {
            return true;
        }

        /**
         * Returns false. Movement does not typically cause removal.
         */
        public boolean isCauseRemoval() {
            return false;
        }
    };

    /**
     * An rule calling the move method of the target agent, causing the
     * agent to move regradless of what the movement condition method returns.
     */
    public static final Rule FORCE_MOVE_RULE = new Rule("Force Move") {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        /**
         * Call move for each agent in the scape.
         * @param agent the moving agent
         */
        public void execute(Agent agent) {
            agent.move();
        }

        /**
         * Returns true. Movement rules almost always must be random, as occupation
         * of space within the lattice affects other agents.
         */
        public boolean isRandomExecution() {
            return true;
        }

        /**
         * Returns false. Movement does not typically cause removal.
         */
        public boolean isCauseRemoval() {
            return false;
        }
    };

    /**
     * A rule causing the target agent to interact with another agent in its scape using
     * the Agent.play() method.
     */
    public static final Rule PLAY_OTHER = new Rule("Play Other") {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        /**
         * Play each neighbor within one unit of the agent.
         * @param agent the playing agent
         * @see Agent#play
         */
        public void execute(Agent agent) {
            Agent other = agent.getScape().findRandom();
            //Check to make sure we didn't find agent; we need an opponent that is not this agent
            while (agent == other) {
                if (agent.getScape().getSize() == 1) {
                    return;
                }
                other = agent.getScape().findRandom();
            }
            agent.play(other);
        }

        /**
         * Returns true. Random execution is required, since other agent's state might be affected.
         */
        public boolean isRandomExecution() {
            return true;
        }

        /**
         * Returns false. Play does not by default cause any agent removal, though it
         * might in some models.
         */
        public boolean isCauseRemoval() {
            return false;
        }
    };

    /**
     * An rule calling the iterate method of the target agent.
     * This rule is different than the iterate scape rule.
     * This provides a simple way to provide a general iterate method for
     * each agent, while still using the general rule mechanism. This is not the
     * 'recommended' way to do things; it is more flexible, powerful, and maintainable
     * to add specialized rules to an existing scape.
     */
    public static final Rule ITERATE_RULE = new Rule("Iterate") {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        /**
         * Call iterate on each agent.
         * @param agent the playing agent
         * @see Agent#iterate()
         */
        public void execute(Agent agent) {
            agent.iterate();
        }

        /**
         * Returns true.
         */
        public boolean isRandomExecution() {
            return true;
        }

        /**
         * Returns true.
         */
        public boolean isCauseRemoval() {
            return true;
        }
    };

    /**
     * A marker for deleting this agent during a later sweep.
     */
    private boolean deleteMarker = false;

    /**
     * Has the agent had the initialization method performed on it since new model state?
     */
    private boolean initialized = false;

    /**
     * Notifies the prototype agent that a new scape has been created, allowing
     * rules to be added directly from an agent class.
     * This is especially useful if you don't want a lot of public rules classes around,
     * since you can add the rules directly from within an agent java file.
     * Guaranteed to be called only once for a new model; will only be called if the
     * parent scape is assigned a prototype agent.
     * Override to add rules to the agent's scape, to override subclass default rules,
     * etc... for instance, you might use this method to create special purpose value collectors,
     * or anything else that is related to the agent and needs to be created once per model.
     * (See norms.NormsCell for an example.)
     * By default, adds a default iterate rule, which calls iterate on each agent for
     * every iteration. <i>You should always override this rule for custom agents
     * (or call scape.clearRules()) so that the overhead of the iterate rule isn't incurred.</i>
     *
     * Edited - Iterate rule is no longer added by default.
     */
    public void scapeCreated() {
        //        scape.addRule(ITERATE_RULE);
    }

    /*
     * Returns data points for instance data that users might be interested in viewing or summarizing.
     * Typically implemented by adding an array of anonymous subclasses implementations of data points,
     * usually StatCollectors.
     * Scape is provided for those rare instances in which different scapes may be interested in different values.
     * Note that the value points are not always value points, but this is the typical use.
     *
     * For a good intro to inner and anonymous classes, see the JavaWorld article <A HREF=http://www.javaworld.com/javaworld/jw-10-1997/jw-10-indepth.html>A look at inner classes</A>.
     * @see edu.brook.sugarscape.SugarAgent#getDataPoints
     */
    /*public DataPoint[] getDataPoints(Scape scape) {
        return new DataPoint[0];
    }*/

    /*
     * Returns the data series points for this agent that have been defined as value points.
     */
    /*public final StatCollector[] getStatCollectors(Scape scape) {
       DataPoint[] points = getDataPoints(scape);
       //This implementation doesn't need to be particularly quick as its called rarely...
       int count = 0;
       for (int i = 0; i < points.length; i++) {
           if (points[i] instanceof StatCollector) {
               count++;
           }
       }
       int index = 0;
       StatCollector[] values = new StatCollector[count];
       for (int i = 0; i < count; i++) {
           if (points[i] instanceof StatCollector) {
               values[index] = (StatCollector) points[i];
               index++;
           }
       }
       return values;
    }*/

    /**
     * Conditions under which this agent should die.
     * Returns false by default.
     * @return true if the agent should die
     * @see #death
     */
    public boolean deathCondition() {
        return false;
    }

    /**
     * Perform the death rule; if the death condition is met, kill the agent.
     * @see #deathCondition
     * @see #DEATH_RULE
     */
    public void death() {
        if (deathCondition()) {
            die();
        }
    }

    /**
     * "Kill" the agent. If the agent is a member of a mutable scape, remove it from the scape.
     */
    public void die() {
        if (scape != null && scape.isMutable()) {
            if (!scape.remove(this)) {
                throw new RuntimeException("Agent couldn't be deleted");
            }
        }
    }

    /**
     * Conditions under which this agent should fission.
     * Returns false by default.
     * @return true if the agent should fission
     * @see #fission
     */
    public boolean fissionCondition() {
        return false;
    }

    /**
     * Perform the fissioning rule; if the fission condition is met, fission.
     * @see #fissionCondition
     * @see #FISSIONING_RULE
     */
    public void fissioning() {
        if (fissionCondition()) {
            fission();
        }
    }

    /**
     * Override to reproduce agent, creating a new agent.
     */
    public void fission() {
    }

    /**
     * Conditions under which this agent should move.
     * Returns false by default.
     * @see #movement
     */
    public boolean movementCondition() {
        return false;
    }

    /**
     * Perform the movement rule; by default, if the movement condition is met, move.
     * @see #movementCondition
     * @see #MOVEMENT_RULE
     */
    public void movement() {
        if (movementCondition()) {
            move();
        }
    }

    /**
     * Override to move this agent based on movement condition.
     * To provide a general purpose movement function, overide movement instead
     * @see #movement
     */
    public void move() {
    }

    /**
     * Performs default metabolism for this agent.
     * @see #METABOLISM_RULE
     */
    public void metabolism() {
    }

    /**
     * Interact in some way with the supplied agent.
     * To use, simply add a Play rule to the agent's scape
     * and overide this method. (Other play methods may be supplied later.)
     * @see CellOccupant#PLAY_NEIGHBORS_RULE
     * @see CellOccupant#PLAY_RANDOM_NEIGHBOR_RULE
     * @see CellOccupant#PLAY_OTHER
     */
    public void play(Agent agent) {
    }

    /**
     * Iterate this agent.
     * By default, called automatically on each agent each iteration.
     * Use clearRules or overide scapeCreated to prevent this.
     * @see #scapeCreated
     * @see #ITERATE_RULE
     */
    public void iterate() {
    }

    /**
     * Update this agent.
     * @see #UPDATE_RULE
     */
    public void update() {
    }

    /**
     * Initialize any values. This method will be called automatically
     * when the parent scape is initialized or started, so override
     * this to provide initial values, location, etc... Any parent
     * models of this agent's scape can be assumed to be initialized.
     * Of course, other agents within the scape should be assumed
     * not to have been initialized.
     */
    public void initialize() {
        initialized = true;
    }

    /**
     * Causes the provided rules to be executed upon this agent.
     * @param rules an array of rules to be executed
     */
    public void execute(Rule[] rules) {
        for (int i = 0; i < rules.length; i++) {
            rules[i].execute(this);
        }
    }

    /**
     * Causes the provided rule to be executed upon this agent.
     * @param rule a rule to be executed
     */
    public void execute(Rule rule) {
        Rule[] rules = new Rule[1];
        rules[0] = rule;
        execute(rules);
    }

    /**
     * Gets the rootmost parent scape for this agent.
     */
    public Scape getRoot() {
        if (this.getScape() != null) {
            return this.getScape().getRoot();
        } else {
            //instanceof is expensive, so we'll just catch the bad case and rethrow it..
            try {
                return (Scape) this;
            } catch (ClassCastException e) {
                throw new RuntimeException("Tried to get root of a non-member agent");
            }
        }
    }

    /**
     * Returns the current count of iteration.
     * For non-scape agents, the parent's scape iteration will be returned.
     * For scapes, this method is overriden to return it's own iteration.
     */
    public int getIteration() {
        return scape.getIteration();
    }

    /**
     * The agent color as platform specific (i.e. non-AWT).
     */
    public Object getPlatformColor() {
        return PLATFORM_DEFAULT_COLOR;
    }

    /**
     * This agent's default color, used by many simple views.
     * Black is default; override to provide an appropriate color.
     */
    public Color getColor() {
        return Color.black;
    }

    /**
     * Provides the default color for an agent.
     * Simply calls the getColor method for the object.
     */
    public Color getColor(Object object) {
        return ((Agent) object).getColor();
    }

    /**
     * This agent's default image, used by many simple views.
     * No default; override to provide an appropriate image.
     */
    public Image getImage() {
        return null;
    }

    /**
     * Provides the default image for an agent.
     * Simply calls the getImage method for the object.
     */
    public Image getImage(Object object) {
        return ((Agent) object).getImage();
    }

    /**
     * Has this agent been initialized?
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Sets the scape for this agent.
     * (If the agent is marked for deletion, we need to force the deletion in the agent's parent scape.
     *
     * @param scape the scape this agent is belongs to
     */
    public void setScape(Scape scape) {
        if (isDelete() && scape != null && this.scape != null && this.scape != scape) {
            //We need to force a deletion in the parent scape, because we are about to lose the context
            try {
                //Assume that we were member of mutable scape before
                ((Mutable) getScape().getSpace()).deleteSweep();

            } catch (ClassCastException e) {
                //Otherwise, something is very wrong...
                throw new RuntimeException("Internal Ascape Exception in Agent.setScape().");
            }
        }
        //We set the delete marker to false, because we have deleted from original scape and are now in a new context.
        clearDeleteMarker();
        super.setScape(scape);
    }

    /**
     * Sets the initialization state of the agent.
     */
    protected void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    /**
     * Is this agent marked for deletion?.
     */
    public boolean isDelete() {
        return deleteMarker;
    }

    /**
     * Sets the agent to be deleted when the next deletion sweep occurs.
     * Used when agent deletion can be safely postponed, that is, when
     * the state of other agents is not a factor in the deletion rules
     * for agents.
     */
    public void markForDeletion() {
        deleteMarker = true;
    }

    /**
     * Resets the marker for deletion. The agtent will no longer be deleted on
     * the next deletion pass.
     */
    public void clearDeleteMarker() {
        deleteMarker = false;
    }

    /**
     * A string representation of this agent.
     */
    public String toString() {
        if (getName() != null) {
            return getName();
        } else {
            return "Agent";
        }
    }

    /**
     * Clones the agent. Can be overriden so that a deep copy is made of any member objects that you do
     * not want to share between agents. Typically, this applies to all agent member objects.
     * <strong>Warning: The default behavior of this method is to perform a shallow copy. This is not appropriate
     * for most agent state! For any member objects in the model you must either a) construct a new object
     * at <code>initialize()</code> or b) preform a deep clone of that object by overriding this method. If you do
     * not do one of these things, you will end up sharing members across agents. This can be a very
     * diffuclt problem to debug!</strong>
     */
    public Object clone() {
        Agent clone = (Agent) super.clone();
        return clone;
    }
}

