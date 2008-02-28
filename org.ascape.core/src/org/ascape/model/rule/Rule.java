/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.rule;

import org.ascape.model.Agent;
import org.ascape.model.AscapeObject;
import org.ascape.model.Scape;

/**
 * An abstract base class for behaviors that can be be iterated across agent
 * scapes or a single agent. You can subclass rule to provide any kind of
 * behavior you want for an agent. Often, rules will simply call agent member
 * functions. But, because rules aren't themselves member functions of agents,
 * they don't need to follow class inheiritance rules, and they can be added,
 * removed and executed dynamically. In effect, they provide a kind of dynamic
 * method dispatch capability to the framework, and allow us to flexibly execute
 * methods upon collections of agents without needing to know anything about the
 * underlying structure of the collection or the method of execution. The system
 * is designed to be powerful, without imposing large performance or conceptual
 * costs. For many common tasks, you can use built-in rules. For example, if you
 * want to allow movement for your agents, you can add a standard movement rule
 * to the scape containing them. These rules are memebers of the Agent and Cell
 * classes.
 * 
 * <pre>
 * scape.addRule(MOVEMENT_RULE);
 * </pre>
 * 
 * Then, simply override the built-in agent movement rule.
 * 
 * <pre>
 * public class MyAgent extends CellOccupant {
 * ...
 * public void movement() {
 * [Your movement code]
 * }
 * ...
 * }
 * 
 * There are a number of rules that have behavior allready defined. (And more planned.)
 * For these ruels, you simply need to add them to  a scape. So to have you agents take a
 * random walk in any direction, simply add the random walk rule.
 * &lt;pre&gt;
 * scape.addRule(RANDOM_WALK_RULE);
 * &lt;pre&gt;
 * To create your own rules you can implement a rule as a straight-forward class. You may
 * want to do this if there is significant state that is kept as part of the rule, as we do
 * for our stat collector and value setter rules, or if you want to extend rules in different
 * ways. But typically, rules are implemented as inner classes, static member classes or
 * anoymous inner classes.
 * &lt;pre&gt;
 * scape.addRule(new Rule(&quot;Update Radius &amp; ExecutionStrategy&quot;) {
 * public void execute(Agent agent) {
 * ((NormCell) agent).updateRadius();
 * ((NormCell) agent).updateStrategy();
 * }
 * });
 * &lt;/pre&gt;
 * Information about the rule provided by the isRandomExecution and isCauseDelete is used by
 * the scape execution methods to optimize rule execution. The default behavior is conservative,
 * that is, rules are assumed to need random execution and potentially cause deletion in their
 * parent scapes. For better performance, if your rules do not need to be execute randomly (typically
 * because their outcomes do not affect otehr agents and/or are not affected by execution order),
 * or cannot cause an agent to be deleted, you should override these methods and return false.
 * 
 * @author Miles Parker
 * @version 1.0
 * @history 1.5 moved isScapeOnly method from Rule
 * @since 1.0
 * @see org.ascape.model.Scape
 * @see org.ascape.model.Agent
 * @see org.ascape.model.Cell
 * @see org.ascape.util.data.StatCollector
 * @see org.ascape.util.ValueSetter
 * @see org.ascape.model.rule.Propogate
 * 
 */
public abstract class Rule extends AscapeObject {

    /**
     * Constructs a Rule.
     * 
     * @param name
     *            the name
     */
    /*public Rule() {
        super("Unnamed");
    }*/

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a rule with the providied name.
     * It is strongly encouraged to provide a name for all rules.
     * This name will be used for run time rule selection and provides important information
     * fopr analyzing and debugging models.
     *
     * @param name the name of this object
     */
    public Rule(String name) {
        super(name);
    }

    /**
     * Perform the rule for the specified agent.
     * 
     * @param agent
     *            the target agent.
     */
    public abstract void execute(Agent agent);

    /**
     * Sets the scape for the agent to act within.
     * 
     * @param scape
     *            the scape that this rule 'belongs' to
     */
    public void setScape(Scape scape) {
        this.scape = scape;
    }

    /**
     * Returns the scape the agent will act within.
     * 
     * @return the scape
     */
    public Scape getScape() {
        return scape;
    }

    /**
     * Does this action affect the state of any other agent in such a way that
     * that another agent's execution of <i>this</i> rule would be affected?
     * Used to determine safe optimization of iterations.
     * 
     * @return true, if is random execution
     */
    public boolean isRandomExecution() {
        return true;
    }

    /**
     * Could this rule cause the removal of any agents from within an this
     * rule's scape or any agent's scape? Used to determine safe optimization of
     * iterations.
     * 
     * @return true, if is cause removal
     */
    public boolean isCauseRemoval() {
        return true;
    }

    /**
     * Should this rule be iterated across all even if iterations per cycle is
     * set? Typically false. Used for rules like INITIALIZE that must be
     * executed on all agents.
     * 
     * @return true, if is iterate all
     */
    public boolean isIterateAll() {
        return false;
    }
}
