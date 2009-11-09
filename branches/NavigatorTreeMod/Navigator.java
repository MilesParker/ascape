/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */

package org.ascape.runtime.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.beans.IntrospectionException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.TooManyListenersException;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.ascape.model.Agent;
import org.ascape.model.Scape;
import org.ascape.model.event.DefaultScapeListener;
import org.ascape.model.event.ScapeEvent;
import org.ascape.model.event.ScapeListener;
import org.ascape.model.rule.Rule;
import org.ascape.util.PropertyAccessor;
import org.ascape.util.ResetableIterator;
import org.ascape.view.vis.ComponentViewDelegate;
import org.ascape.view.vis.PanelView;


/**
 * The Class Navigator.
 */
public class Navigator extends PanelView {

    /**
     * The tree.
     */
    JTree tree;

    /**
     * The listeners.
     */
    List listeners;

    /**
     * The scape model.
     */
    private DefaultTreeModel scapeModel;

    /**
     * Instantiates a new navigator.
     */
    public Navigator() {
        super("Scape Navigator");
        delegate = new ComponentViewDelegate(this) {
            /**
             * 
             */
            private static final long serialVersionUID = 6905412538045347381L;

            public void scapeAdded(ScapeEvent scapeEvent) throws TooManyListenersException {
                scape = (Scape) scapeEvent.getSource();
            }
        };
        listeners = new ArrayList(0);
    }

    /**
     * The lock.
     */
    Object lock = new Object();

    /**
     * The Class ScapeIcon.
     */
    class ScapeIcon implements Icon {

        /**
         * The scape.
         */
        Scape scape;

        /**
         * Instantiates a new scape icon.
         * 
         * @param agent
         *            the agent
         */
        public ScapeIcon(Scape agent) {
            this.scape = agent;
        }

        /* (non-Javadoc)
         * @see javax.swing.Icon#getIconHeight()
         */
        public final int getIconHeight() {
            return 16;
        }

        /* (non-Javadoc)
         * @see javax.swing.Icon#getIconWidth()
         */
        public final int getIconWidth() {
            return 16;
        }

        /* (non-Javadoc)
         * @see javax.swing.Icon#paintIcon(java.awt.Component, java.awt.Graphics, int, int)
         */
        public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(scape.getColor());
            g.drawRect(x, y, 14, 14);
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (scape.isInitialized() && scape.getSize() > 0) {
                Object[] agents = new Object[Math.min(scape.getSize(), 4)];
                //To prevent comodification errors..
                for (int i = 0; i < agents.length; i++) {
                    agents[i] = scape.findRandom();
                }
            }
        }
    }

    ;

    /**
     * The Class AgentIcon.
     */
    class AgentIcon implements Icon {

        /**
         * The agent.
         */
        Agent agent;

        /**
         * Instantiates a new agent icon.
         * 
         * @param agent
         *            the agent
         */
        public AgentIcon(Agent agent) {
            this.agent = agent;
        }

        /* (non-Javadoc)
         * @see javax.swing.Icon#getIconHeight()
         */
        public final int getIconHeight() {
            return 16;
        }

        /* (non-Javadoc)
         * @see javax.swing.Icon#getIconWidth()
         */
        public final int getIconWidth() {
            return 16;
        }

        /* (non-Javadoc)
         * @see javax.swing.Icon#paintIcon(java.awt.Component, java.awt.Graphics, int, int)
         */
        public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(agent.getColor());
            g.fillOval(x, y, 14, 14);
        }
    }

    ;

    /* (non-Javadoc)
     * @see javax.swing.JComponent#addNotify()
     */
    public void addNotify() {
        super.addNotify();
        createTree();
    }

    protected void createTree() {
        tree = new JTree();
        tree.setFont(getFont().deriveFont(10.0f));
        //Just so we don't see that silly built-in table
        tree.setModel(new DefaultTreeModel(null));
        setLayout(new BorderLayout());
        add(tree, BorderLayout.CENTER);
        tree.getSelectionModel().setSelectionMode
        (TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setLargeModel(true);
        tree.setToggleClickCount(1);
        tree.setShowsRootHandles(true);

        //        tree.putClientProperty("JTree.lineStyle", "Angled");
        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer() {
            /**
             * 
             */
            private static final long serialVersionUID = 1371869534479837978L;

            public Component getTreeCellRendererComponent(JTree tree, Object value,
                    boolean sel,
                    boolean expanded,
                    boolean leaf, int row,
                    boolean hasFocus) {
                Component c = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
                if (value instanceof ScapeMembersNode) {
                    setIcon(DesktopEnvironment.getIcon("Members"));
                } else if (value instanceof AgentNode) {
                    setIcon(new AgentIcon((Agent) ((AgentNode) value).getUserObject()));
                } else if (value instanceof ScapeNode) {
                    setIcon(new ScapeIcon((Scape) ((ScapeNode) value).getUserObject()));
                    //Order matters for line below, as agent node subclasses property node
                } else if (value instanceof PropertiesNode) {
                    setIcon(DesktopEnvironment.getIcon("Properties"));
                    setText("Properties");
                } else if (value instanceof RulesNode) {
                    setIcon(DesktopEnvironment.getIcon("Rules"));
                } else if (value instanceof RuleNode) {
                    setIcon(DesktopEnvironment.getIcon("Rule"));
                } else if (value instanceof AccessorNode) {
                    setIcon(DesktopEnvironment.getIcon("Property"));
                }
                return c;
            }
        };
        tree.setCellRenderer(renderer);
        //        tree.setCellEditor(new DefaultTreeCellEditor(tree, renderer) {
        //            public Component getTreeCellEditorComponent(JTree tree, Object value,
        //                                                        boolean isSelected,
        //                                                        boolean expanded,
        //                                                        boolean leaf, int row) {
        //                System.out.println(": "+value.getClass());
        //                return super.getTreeCellEditorComponent(tree, value, isSelected, expanded, leaf, row);
        //            }
        //        });
    }

    /**
     * The waiting for update.
     */
    Set<BaseNode> waitingForUpdate = new HashSet();

    /**
     * The Class BaseNode.
     */
    class BaseNode extends DefaultMutableTreeNode {

        /**
         * 
         */
        private static final long serialVersionUID = -1339361794486509129L;

        /**
         * The scape.
         */
        Scape scape;

        /**
         * The listener.
         */
        ScapeListener listener;

        /**
         * Instantiates a new base node.
         * 
         * @param scape
         *            the scape
         * @param userObject
         *            the user object
         */
        public BaseNode(Scape scape, Object userObject) {
            super(userObject);
            this.scape = scape;

            listener = new DefaultScapeListener("Tree Members Listener") {
                /**
                 * 
                 */
                private static final long serialVersionUID = 6739656535899709964L;

                private void update() {
                    TreePath nodePath = new TreePath(BaseNode.this.getPath());
                    if (scape.isMutable() && tree.isVisible(nodePath)) {
                        requestUpdateChildren();
                    }
                }

                public void scapeStarted(ScapeEvent scapeEvent) {
                    update();
                }

                public void scapeIterated(ScapeEvent scapeEvent) {
                    update();
                }
            };
            scape.addView(listener);

            tree.addTreeExpansionListener(new TreeExpansionListener() {
                public void treeCollapsed(TreeExpansionEvent event) {
                }

                public void treeExpanded(TreeExpansionEvent event) {
                    TreePath nodePath = new TreePath(BaseNode.this.getPath());
                    if (event.getPath().equals(nodePath.getParentPath())) {
                        updateChildren();
                    }
                }
            });
        }


        /**
         * Request update children.
         */
        protected void requestUpdateChildren() {
            if (!scape.isRunning()) {
                invokeUpdateChildren();
            } else {
                synchronized (waitingForUpdate) {
                    waitingForUpdate.add(BaseNode.this);
                }
            }
        }

        /**
         * Invoke update children.
         */
        protected void invokeUpdateChildren() {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    public void run() {
                        updateChildren();
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                System.out.println(e);
                e.printStackTrace();
            }
        }

        /**
         * Update children.
         */
        protected void updateChildren() {
        }

    }

    /**
     * The Class AccessorNode.
     */
    class AccessorNode extends BaseNode {

        /**
         * 
         */
        private static final long serialVersionUID = 6327681491877012320L;
        /**
         * The accessor.
         */
        private PropertyAccessor accessor;

        /**
         * Instantiates a new accessor node.
         * 
         * @param scape
         *            the scape
         * @param accessor
         *            the accessor
         */
        public AccessorNode(Scape scape, PropertyAccessor accessor) {
            super(scape, accessor);
            this.accessor = accessor;
        }

        /* (non-Javadoc)
         * @see javax.swing.tree.DefaultMutableTreeNode#toString()
         */
        public String toString() {
            return accessor.getDescriptor().getShortDescription() + "=" + accessor.getValue();
        }
    }


    /**
     * The Class RuleNode.
     */
    class RuleNode extends BaseNode {

        /**
         * 
         */
        private static final long serialVersionUID = -6204254579116806384L;

        /**
         * Instantiates a new rule node.
         * 
         * @param scape
         *            the scape
         * @param userObject
         *            the user object
         */
        public RuleNode(Scape scape, Object userObject) {
            super(scape, userObject);
        }
    }

    /**
     * The Class RulesNode.
     */
    class RulesNode extends BaseNode {

        /**
         * 
         */
        private static final long serialVersionUID = 5237720048765754585L;

        /**
         * The selection observer.
         */
        Observer selectionObserver;

        /**
         * The current rules.
         */
        private Map<Rule, RuleNode> currentRules;

        /**
         * Instantiates a new rules node.
         * 
         * @param scape
         *            the scape
         */
        public RulesNode(final Scape scape) {
            super(scape, new String("Rules"));
            this.scape = scape;
            currentRules = new HashMap<Rule, RuleNode>();
            listener = new DefaultScapeListener("Tree Members Listener") {
                /**
                 * 
                 */
                private static final long serialVersionUID = 5414250624546793220L;

                public void scapeStarted(ScapeEvent scapeEvent) {
                    requestUpdateChildren();
                }
            };
            scape.addView(listener);
            selectionObserver = new Observer() {
                public void update(Observable o, Object arg) {
                    updateChildren();
                }
            };
            scape.getRules().addObserver(selectionObserver);
        }

        /* (non-Javadoc)
         * @see org.ascape.explorer.Navigator.BaseNode#updateChildren()
         */
        protected void updateChildren() {
            //todo does not handle updating order
            for (int i = 0; i < scape.getRules().getVector().size(); i++) {
                Rule rule = (Rule) scape.getRules().getVector().elementAt(i);
                if (scape.getRules().isSelected(i)) {
                    if (currentRules.get(rule) == null) {
                        RuleNode addNode = new RuleNode(scape, rule);
                        scapeModel.insertNodeInto(addNode, this, Math.min(scapeModel.getChildCount(this), scape.getRules().getSelectedIndex(rule)));
                        currentRules.put(rule, addNode);
                    }
                } else {
                    RuleNode node = currentRules.get(rule);
                    if (node != null) {
                        scapeModel.removeNodeFromParent(node);
                        currentRules.remove(rule);
                    }
                }

            }
            tree.repaint();
        }
    }

    /**
     * The Class PropertiesNode.
     */
    class PropertiesNode extends BaseNode {

        /**
         * 
         */
        private static final long serialVersionUID = 8638479208005464388L;

        /**
         * The accessors.
         */
        private PropertyAccessor[] accessors;

        /**
         * The accessor ind.
         */
        int[] accessorInd;

        /**
         * The property support.
         */
        PropertyChangeSupport propertySupport;

        /**
         * The property listener.
         */
        PropertyChangeListener propertyListener;

        /**
         * Instantiates a new properties node.
         * 
         * @param scape
         *            the scape
         * @param agent
         *            the agent
         */
        public PropertiesNode(Scape scape, Agent agent) {
            super(scape, agent);
            try {
                if (!(agent instanceof Scape)) {
                    List<?> accessorsList = PropertyAccessor.determineReadWriteAccessors(agent, Agent.class, true);
                    accessors = accessorsList.toArray(new PropertyAccessor[accessorsList.size()]);
                } else {
                    List<?> accessorsList = PropertyAccessor.determineReadWriteAccessors(agent, Scape.class, true);
                    accessors = accessorsList.toArray(new PropertyAccessor[accessorsList.size()]);
                }
                Arrays.sort(accessors);
                accessorInd = new int[accessors.length];
                for (int i = 0; i < accessors.length; i++) {
                    add(new AccessorNode(scape, accessors[i]));
                    accessorInd[i] = i;
                }

                tree.addTreeExpansionListener(new TreeExpansionListener() {
                    public void treeCollapsed(TreeExpansionEvent event) {
                        if (propertySupport != null) {
                            propertySupport.removePropertyChangeListener(propertyListener);
                        }
                    }

                    public void treeExpanded(TreeExpansionEvent event) {
                        TreePath nodePath = new TreePath(PropertiesNode.this.getPath());
                        if (event.getPath().equals(nodePath)) {
                            propertySupport = SwingEnvironment.DEFAULT_ENVIRONMENT.getPropertySupportForObject(getUserObject());
                            if (propertyListener == null) {
                                propertyListener = new PropertyChangeListener() {
                                    public void propertyChange(PropertyChangeEvent evt) {
                                        //linear search
                                        for (int i = 0; i < accessors.length; i++) {
                                            PropertyAccessor accessor = accessors[i];
                                            if (accessor.getName().equals(evt.getPropertyName())) {
                                                int[] ind = new int[1];
                                                ind[0] = i;
                                                scapeModel.nodesChanged(PropertiesNode.this, ind);
                                                tree.repaint();
                                            }
                                        }
                                    }
                                };
                            }
                            if (!Arrays.asList(propertySupport.getPropertyChangeListeners()).contains(propertyListener)) {
                                propertySupport.addPropertyChangeListener(propertyListener);
                            }
                        }
                    }
                });
            } catch (IntrospectionException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * The Class AgentNode.
     */
    class AgentNode extends PropertiesNode {

        /**
         * 
         */
        private static final long serialVersionUID = -5046377326408958875L;

        /**
         * Instantiates a new agent node.
         * 
         * @param scape
         *            the scape
         * @param agent
         *            the agent
         */
        public AgentNode(Scape scape, Agent agent) {
            super(scape, agent);
        }
    }

    /**
     * The Class ScapeNode.
     */
    class ScapeNode extends BaseNode {

        /**
         * 
         */
        private static final long serialVersionUID = -7469682418324424534L;

        /**
         * Instantiates a new scape node.
         * 
         * @param scape
         *            the scape
         */
        public ScapeNode(Scape scape) {
            super(scape, scape);
            add(new PropertiesNode(scape, scape));
            add(new RulesNode(scape));
            if (scape instanceof Scape) {
                ScapeMembersNode membersNode = new ScapeMembersNode(scape);
                add(membersNode);
                
				//if scape can modify the tree, call the modifyTree method 
                //of the scape on the AWT Event thread
				if (this.scape instanceof TreeModifier) {
					Runnable doWorkRunnable = new Runnable() {
					    public void run() {  
					    	((TreeModifier) ScapeNode.this.scape).
								modifyTree(tree, (DefaultMutableTreeNode) 
										ScapeNode.this);
					    }
					};
					SwingUtilities.invokeLater(doWorkRunnable);
				}

            }
        }
    }

    /**
     * Desc.
     * 
     * @param startIndex
     *            the start index
     * @param endIndex
     *            the end index
     * @return the string
     */
    public static String desc(int startIndex, int endIndex) {
        return "Members " + startIndex + "-" + (endIndex - 1);
    }

    /**
     * The Class ScapeGroupNode.
     */
    class ScapeGroupNode extends ScapeMembersNode {

        /**
         * 
         */
        private static final long serialVersionUID = 3489960978154121203L;

        /**
         * The start index.
         */
        int startIndex;

        /**
         * The end index.
         */
        int endIndex;

        /**
         * The iterator.
         */
        ResetableIterator iterator;

        /**
         * Instantiates a new scape group node.
         * 
         * @param scape
         *            the scape
         * @param startIndex
         *            the start index
         * @param endIndex
         *            the end index
         */
        public ScapeGroupNode(Scape scape, int startIndex, int endIndex) {
            super(scape, desc(startIndex, endIndex));
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }

        /* (non-Javadoc)
         * @see org.ascape.explorer.Navigator.ScapeMembersNode#updateChildren()
         */
        protected void updateChildren() {
            updateChildren(startIndex, endIndex);
        }
    }

    /**
     * The group size.
     */
    private int groupSize = 20;

    /**
     * The Class ScapeMembersNode.
     */
    class ScapeMembersNode extends BaseNode {

        /**
         * 
         */
        private static final long serialVersionUID = 3696674806064654573L;

        /**
         * Instantiates a new scape members node.
         * 
         * @param scape
         *            the scape
         * @param desc
         *            the desc
         */
        public ScapeMembersNode(Scape scape, String desc) {
            super(scape, desc);
        }

        /**
         * Instantiates a new scape members node.
         * 
         * @param scape
         *            the scape
         */
        public ScapeMembersNode(Scape scape) {
            this(scape, "Members");
        }

        /* (non-Javadoc)
         * @see org.ascape.explorer.Navigator.BaseNode#updateChildren()
         */
        protected void updateChildren() {
            if (getChildCount() > 0 && getFirstChild().toString().equals("Dummy")) {
                scapeModel.removeNodeFromParent((MutableTreeNode) getFirstChild());
            }
            updateChildren(0, scape.getSize());
        }

        /**
         * Update children.
         * 
         * @param startIndex
         *            the start index
         * @param endIndex
         *            the end index
         */
        void updateChildren(int startIndex, int endIndex) {
            int size = endIndex - startIndex;
            if (size <= groupSize) {
                if (scape.isMutable()) {
                    insertMutableMembers(startIndex, endIndex);
                } else {
                    insertImmutableMembers(startIndex, endIndex);
                }
            } else {
                if (scape.isMutable()) {
                    insertMutableGroups(startIndex, endIndex);
                } else {
                    insertImmutableGroups(startIndex, endIndex);
                }
            }
        }

        /**
         * Insert mutable members.
         * 
         * @param startIndex
         *            the start index
         * @param endIndex
         *            the end index
         */
        void insertMutableMembers(int startIndex, int endIndex) {
            List scapeMembers = new ArrayList();
            //A little cheat here..to fix an issue w (I think) node getting updted out of order.
            Iterator iterator = scape.getSpace().safeIterator(Math.min(startIndex, scape.getSize()), Math.min(endIndex, scape.getSize()));
            for (; iterator.hasNext();) {
                scapeMembers.add(iterator.next());
            }
            Set remain = new HashSet();
            List delete = new ArrayList();
            Enumeration e = children();
            while (e.hasMoreElements()) {
                BaseNode node = (BaseNode) e.nextElement();

                if (!scapeMembers.contains(node.getUserObject())) {
                    delete.add(node);
                } else {
                    remain.add(node.getUserObject());
                }
                scapeModel.nodeChanged(node);
            }
            scapeMembers.removeAll(remain);

            for (Iterator addIterator = scapeMembers.iterator(); addIterator.hasNext();) {
                addNodes((Agent) addIterator.next());
            }

            for (Iterator delIterator = delete.iterator(); delIterator.hasNext();) {
                MutableTreeNode node = (MutableTreeNode) delIterator.next();
                scapeModel.removeNodeFromParent(node);
            }
        }

        /**
         * Insert immutable members.
         * 
         * @param startIndex
         *            the start index
         * @param endIndex
         *            the end index
         */
        void insertImmutableMembers(int startIndex, int endIndex) {
            Iterator iterator = scape.getSpace().safeIterator(startIndex, endIndex);
            if (getChildCount() == 0) {
                for (; iterator.hasNext();) {
                    addNodes((Agent) iterator.next());
                }
            }
        }

        /**
         * Insert mutable groups.
         * 
         * @param start
         *            the start
         * @param end
         *            the end
         */
        void insertMutableGroups(int start, int end) {
            int size = end - start;
            int numChildren = groupSize;
            if (size / groupSize >= groupSize) {
                numChildren = size / groupSize;
            }
            Enumeration e = children();
            int i = start;
            int index = 0;
            while (e.hasMoreElements()) {
                BaseNode node = (BaseNode) e.nextElement();
                //If we were formally displaying actual memebers here (e.g. the size of scape has grown past group size) we need to delete them.
                if (node instanceof ScapeNode || node instanceof AgentNode) {
                    scapeModel.removeNodeFromParent(node);
                    scapeModel.insertNodeInto(node, this, index);
                } else {
                    ((ScapeGroupNode) node).startIndex = i;
                    ((ScapeGroupNode) node).endIndex = Math.min(end, i + numChildren);
                    ((ScapeGroupNode) node).setUserObject(desc(i, ((ScapeGroupNode) node).endIndex));
                }
                if (i >= end) {
                    scapeModel.removeNodeFromParent(node);
                }
                index++;
                i = Math.min(end, i + numChildren);
            }
            for (; i < end; i = Math.min(end, i + numChildren)) {
                ScapeGroupNode node = new ScapeGroupNode(scape, i, Math.min(end, i + numChildren));
                scapeModel.insertNodeInto(node, this, getChildCount());
            }
        }

        /**
         * Insert immutable groups.
         * 
         * @param start
         *            the start
         * @param end
         *            the end
         */
        void insertImmutableGroups(int start, int end) {
            int size = end - start;
            int numChildren = groupSize;
            if (size / groupSize >= groupSize) {
                numChildren = size / groupSize;
            }
            int index = 0;
            for (int i = start; i < end; i = Math.min(end, i + numChildren)) {
                ScapeGroupNode node = new ScapeGroupNode(scape, i, Math.min(end, i + numChildren));
                scapeModel.insertNodeInto(node, this, index);
                index++;
            }
        }

        /**
         * Adds the nodes.
         * 
         * @param agent
         *            the agent
         */
        void addNodes(Agent agent) {
            MutableTreeNode treeNode;
            if (agent instanceof Scape) {
                treeNode = new ScapeNode((Scape) agent);
            } else { //Agent
                treeNode = new AgentNode(scape, agent);
            }
            scapeModel.insertNodeInto(treeNode, ScapeMembersNode.this, getChildCount());
        }
    }

    /**
     * The root.
     */
    private ScapeNode root;

    /* (non-Javadoc)
     * @see org.ascape.view.vis.PanelView#scapeAdded(org.ascape.model.event.ScapeEvent)
     */
    public void scapeAdded(ScapeEvent scapeEvent) throws TooManyListenersException {
        super.scapeAdded(scapeEvent);
        if (tree == null) {
            createTree();
        }
        root = new ScapeNode(scape);
        scapeModel = new DefaultTreeModel(root);
        tree.setModel(scapeModel);
    }

    /* (non-Javadoc)
     * @see org.ascape.view.vis.PanelView#scapeRemoved(org.ascape.model.event.ScapeEvent)
     */
    public void scapeRemoved(ScapeEvent scapeEvent) {
        super.scapeRemoved(scapeEvent);
        scape = null;
    }

    /* (non-Javadoc)
     * @see org.ascape.view.vis.PanelView#isLifeOfScape()
     */
    public boolean isLifeOfScape() {
        return false;
    }

    /* (non-Javadoc)
     * @see org.ascape.view.vis.PanelView#scapeNotification(org.ascape.model.event.ScapeEvent)
     */
    public void scapeNotification(ScapeEvent scapeEvent) {
        if (scapeEvent.getID() == ScapeEvent.REPORT_ITERATE) {
            synchronized (waitingForUpdate) {
                for (Iterator iterator = waitingForUpdate.iterator(); iterator.hasNext();) {
                    BaseNode node = (BaseNode) iterator.next();
                    TreePath nodePath = new TreePath(node);
                    if (tree.isVisible(nodePath)) {
                        node.updateChildren();
                    }
                }
                waitingForUpdate = new HashSet();
            }
        }
        tree.repaint();
        notifyScapeUpdated();
    }
}



