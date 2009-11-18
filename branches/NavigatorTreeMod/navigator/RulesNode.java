package org.ascape.runtime.swing.navigator;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.tree.DefaultTreeModel;

import org.ascape.model.Scape;
import org.ascape.model.rule.Rule;

/**
 * The Class RulesNode.
 */
public class RulesNode extends LazyMutableTreeNode {

	/**
     * 
     */
	private static final long serialVersionUID = 5237720048765754585L;

	/**
	 * The current rules.
	 */
	private final Map<Rule, RuleNode> currentRules =
			new HashMap<Rule, RuleNode>();;

	private final Scape scape;

	private final DefaultTreeModel treeModel;

	/**
	 * Instantiates a new rules node.
	 * 
	 * @param scape
	 *            the scape
	 * @param treeModel
	 *            tree model for adding nodes
	 */
	public RulesNode(final Scape scape, DefaultTreeModel treeModel) {
		super("Rules");
		this.scape = scape;
		this.treeModel = treeModel;

		Observer selectionObserver = new Observer() {

			// when a rule is activated/deactivated
			// via Model Settings, update the RulesNode's
			// children
			public void update(Observable o, Object arg) {
				updateChildren();
			}
		};
		scape.getRules().addObserver(selectionObserver);
	}

	/**
	 * Add/remove rules.
	 */
	protected void updateChildren() {
		// todo does not handle updating order
		for (int i = 0; i < scape.getRules().getVector().size(); i++) {
			Rule rule = (Rule) scape.getRules().getVector().elementAt(i);
			if (scape.getRules().isSelected(i)) {

				// if rule doesn't exist as a child, add it
				if (currentRules.get(rule) == null) {
					RuleNode addNode = new RuleNode(rule);
					treeModel.insertNodeInto(addNode, this, Math.min(
							treeModel.getChildCount(this), scape.getRules()
									.getSelectedIndex(rule)));
					currentRules.put(rule, addNode);
				}
			} else {

				// if rule exists as a child, remove it
				RuleNode node = currentRules.get(rule);
				if (node != null) {
					treeModel.removeNodeFromParent(node);
					currentRules.remove(rule);
				}
			}

		}
	}
}
