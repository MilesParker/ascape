/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.view.custom;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.CellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.ascape.explorer.UserEnvironment;
import org.ascape.model.Scape;
import org.ascape.model.rule.Rule;
import org.ascape.util.PropertyAccessor;

/**
 * A panel for making changes to model settings. Automatically creates field
 * names and text fields for viewing and changing model settings based on bean
 * info.
 * 
 * @author Miles Parker
 * @version 1.1.2
 * @history 1.1.2 Improved Netscape error reporting
 * @history first in 1.0
 * @since 1.0
 */
public class AutoCustomizerSwing extends ModelCustomizerSwing {

    /**
     * The panel that all settings are displayed within.
     */
    private JPanel settingsPanel;

    /**
     * The panel that all settings are displayed within.
     */
    private JPanel rulePanel;

    /**
     * The tab pane.
     */
    private JTabbedPane tabPane = new JTabbedPane();

    /**
     * The current scape.
     */
    private Scape currentScape;

    //protected JComboBox selectableRules;

    /**
     * The last agents per iteration.
     */
    private int lastAgentsPerIteration;

    /**
     * The iterations field.
     */
    private JTextField iterationsField = new JTextField();

    /**
     * The property support.
     */
    PropertyChangeSupport propertySupport;

    /**
     * Create and place the customizer's components. Introspects the model to
     * find setting's accessors, and adds descriptions and text fields to the
     * customizer for them.
     */
    public void build() {
        super.build();

        propertySupport = UserEnvironment.getDefaultEnvironment().getPropertySupportForObject(getScape());

        buildSettingsPanel();
        buildRulePanel();
        addPanels();
//        setPreferredSize(new Dimension(280, getPreferredSize().height));
        setPreferredSize(new Dimension(320, getPreferredSize().height)); // changed 8/4/03 to fit 3 tab panes for NIMA
        validate();
    }

    /**
     * Adds the panels.
     */
    protected void addPanels() {
        tabPane = new JTabbedPane();
        tabPane.addTab("Parameters", UserEnvironment.getIcon("World"), settingsPanel, "Set Model Paramaters");
        tabPane.addTab("Rules", UserEnvironment.getIcon("Document"), rulePanel, "Set Model Behavior");
        tabPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                // user changed tabs
                assignSettings();
                /*if (rulePanel==((JTabbedPane) e.getSource()).getSelectedComponent()) {
                    // user has switched to the Rules tab
                    assignSettings();
                };*/
            }
        });
        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(tabPane);
    }

    /**
     * The all accessors.
     */
    private PropertyAccessor[] allAccessors;
    
    /**
     * The settings table.
     */
    private JTable settingsTable;

    /**
     * Builds the settings panel.
     */
    protected void buildSettingsPanel() {
        GridBagLayout gbl = new GridBagLayout();
        settingsPanel = new JPanel();
        settingsPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        settingsPanel.setLayout(gbl);
        GridBagConstraints gbc = gbl.getConstraints(settingsPanel);
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.ipadx = 6;
        gbc.ipady = 6;
        gbc.gridy = 1;
        settingsPanel.add(new JLabel("All Scapes"), gbc);
        //try {
        java.util.List accessors = scape.retrieveModelAccessorsOrdered();
        allAccessors = (PropertyAccessor[]) accessors.toArray(new PropertyAccessor[accessors.size()]);
        final SettingsModel settings = new SettingsModel();
        //final JTable
        settingsTable = new JTable(settings);
        settingsTable.setRowSelectionAllowed(false);
        settingsTable.setShowGrid(false);
        //settingsTable.getColumnModel().getColumn(0).setMaxWidth(80);
        //Fix table behavior so that clicks edit immeadiatly and edits are live
        final DefaultCellEditor ed = (DefaultCellEditor) settingsTable.getDefaultEditor(Object.class);
        ed.setClickCountToStart(0);
        final JTextField f = (JTextField) (ed).getComponent();
        DocumentListener myListener = new DocumentListener() {
            public void anyUpdate() {
                int r = settingsTable.getEditingRow();
                int c = settingsTable.getEditingColumn();
                if ((r != -1) && (c != -1)) {
                    settingsTable.setValueAt(ed.getCellEditorValue(), r, c);
                }
            }

            public void changedUpdate(DocumentEvent e) {
                anyUpdate();
            }

            public void insertUpdate(DocumentEvent e) {
                anyUpdate();
            }

            public void removeUpdate(DocumentEvent e) {
                anyUpdate();
            }
        };
        f.getDocument().addDocumentListener(myListener);

        //settingsTable.getColumnModel().getColumn(1).setMinWidth(60);
        settingsTable.getColumnModel().getColumn(1).setResizable(true);
        JScrollPane scrollPane = new JScrollPane(settingsTable);
        gbc.gridy++;
        gbc.weighty = 1.0;
        settingsPanel.add(scrollPane, gbc);
        /*}
        catch (RuntimeException e) {
            netscapeFailure = true;
            System.out.println(netscapeMsg);
            TextArea msg = new TextArea(netscapeMsg, 16, 38, TextArea.SCROLLBARS_NONE);
            msg.setEditable(false);
            setLayout(new BorderLayout());
            removeAll();
            settingsPanel.add("Center", msg);
        }*/
    }

    /**
     * Builds the rule panel.
     */
    protected void buildRulePanel() {
        GridBagLayout gbl = new GridBagLayout();
        rulePanel = new JPanel();
        rulePanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        rulePanel.setLayout(gbl);
        GridBagConstraints gbc = gbl.getConstraints(rulePanel);
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.ipadx = 6;
        gbc.ipady = 6;
        gbc.gridy = 1;
        List scapes = scape.getAllScapes();
        for (ListIterator scapeIt = scapes.listIterator(); scapeIt.hasNext();) {
            Scape s = (Scape) scapeIt.next();
            if (s.getRules().getVector().size() == 0) {
                scapeIt.remove();
            }
        }
        if (scapes.size() == 0) {
            rulePanel.add(new JLabel("No Rules"), gbc);
            return;
        }
        rulePanel.add(new JLabel("Select Scape"), gbc);
        final JComboBox scapeList = new JComboBox(new Vector(scapes));
//        final JComboBox Scape = new JComboBox(scape.getAllScapes());
        scapeList.setEditable(false);
        currentScape = (Scape) scapeList.getSelectedItem();
        final RuleModel availableRules = new RuleModel();
        final JTable ruleTable = new JTable(availableRules);
        ruleTable.setDefaultRenderer(Boolean.class, new NullCheckBoxRenderer());
        ruleTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        //ruleTable.setRowSelectionAllowed(false);
        ruleTable.setShowGrid(false);
        ruleTable.getColumnModel().getColumn(1).setMaxWidth(60);
        ruleTable.getColumnModel().getColumn(1).setMinWidth(60);
        ruleTable.getColumnModel().getColumn(1).setResizable(false);
        rulePanel.setPreferredSize(new Dimension(rulePanel.getPreferredSize().width, 360));
        gbc.gridy++;
        rulePanel.add(scapeList, gbc);
        gbc.gridy++;
        rulePanel.add(new JLabel("Select and Order Rules"), gbc);
        JPanel selRulePanel = new JPanel();
        selRulePanel.setBorder(BorderFactory.createTitledBorder(""));
        selRulePanel.setLayout(new BorderLayout());
        JPanel availablePanel = new JPanel();
        availablePanel.setLayout(new BorderLayout());
        //availablePanel.setBorder(BorderFactory.createTitledBorder("Rules"));
        JScrollPane scrollPane = new JScrollPane(ruleTable);
        availablePanel.add(scrollPane, "Center");
        gbc.weighty = 1.0;
        selRulePanel.add(availablePanel, BorderLayout.CENTER);
        //JPanel rulesButtonPanel = new JPanel(new FlowLayout());



        //JToolBar ruleToolBar = new JToolBar();
        JToolBar ruleToolBar = new JToolBar() {
            protected JButton createActionComponent(Action a) {
                String text = (String) a.getValue(Action.NAME);
                Icon icon = (Icon) a.getValue(Action.SMALL_ICON);

                JButton b = new JButton(text, icon) {
                    // I override JButton's createActionPropertyChangeListener
                    // to avoid the creation of a AbstractButton$ButtonActionPropertyChangeListener,
                    // which is not serializable!
                    protected PropertyChangeListener createActionPropertyChangeListener(Action a) {
                        return null;
                    }
                };
                if (icon != null) {
                    b.putClientProperty("hideActionText", Boolean.TRUE);
                }
                b.setHorizontalTextPosition(JButton.CENTER);
                b.setVerticalTextPosition(JButton.BOTTOM);
                b.setEnabled(a.isEnabled());
                b.setToolTipText((String) a.getValue(Action.SHORT_DESCRIPTION));
                return b;
            }
        };


        ruleToolBar.setFloatable(false);
        //JButton upButton = new JButton("Up", UserEnvironment.getIcon("Up"));
        //rulesButtonPanel.add(upButton);
        JButton upButton = ruleToolBar.add(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                int[] userSelRules = ruleTable.getSelectedRows();
                if (userSelRules.length > 0) {
                    int min = userSelRules[0];
                    int max = userSelRules[0];
                    //Even though seleciton model is single interval, we can't be sure that they will
                    //be in order since this isn't specified in the api...
                    for (int i = 1; i < userSelRules.length; i++) {
                        if (userSelRules[i] < min) min = userSelRules[i];
                        if (userSelRules[i] > max) max = userSelRules[i];
                    }
                    if (min > 0) {
                        //If min = 0, can't move up, so do nothing
                        Vector allRules = currentScape.getRules().getVector();
                        Object swapRule = allRules.elementAt(min - 1);
                        allRules.removeElementAt(min - 1);
                        //Java 1.2 replace above with this:
                        //Object swapRule = allRules.remove(min - 1);
                        //Not max + 1, because we have removed one item
                        allRules.insertElementAt(swapRule, max);
                        currentScape.getRules().update();
                        availableRules.fireTableDataChanged();
                        ruleTable.getSelectionModel().setSelectionInterval(min - 1, max - 1);
                    }
                }
            }
        });
        upButton.setToolTipText("Move Selected Rules Up (Execute Before)");
        upButton.setIcon(UserEnvironment.getIcon("Up"));
        //JButton downButton = new JButton("Down", UserEnvironment.getIcon("Down"));
        //rulesButtonPanel.add(downButton);
        JButton downButton = ruleToolBar.add(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                int[] userSelRules = ruleTable.getSelectedRows();
                if (userSelRules.length > 0) {
                    int min = userSelRules[0];
                    int max = userSelRules[0];
                    //Even though seleciton model is single interval, we can't be sure that they will
                    //be in order since this isn't specified in the api...
                    for (int i = 1; i < userSelRules.length; i++) {
                        if (userSelRules[i] < min) min = userSelRules[i];
                        if (userSelRules[i] > max) max = userSelRules[i];
                    }
                    Vector allRules = currentScape.getRules().getVector();
                    if (max < allRules.size() - 1) {
                        //If max >= size, can't move down, so do nothing
                        Object swapRule = allRules.elementAt(max + 1);
                        allRules.removeElementAt(max + 1);
                        //Java 1.2 replace above with this:
                        //Object swapRule = allRules.remove(max + 1);
                        allRules.insertElementAt(swapRule, min);
                        currentScape.getRules().update();
                        availableRules.fireTableDataChanged();
                        ruleTable.getSelectionModel().setSelectionInterval(min + 1, max + 1);
                    }
                }
            }
        });
        downButton.setToolTipText("Move Selected Rules Down (Execute After)");
        downButton.setIcon(UserEnvironment.getIcon("Down"));
        selRulePanel.add(ruleToolBar, BorderLayout.SOUTH);
        gbc.gridy++;
        rulePanel.add(selRulePanel, gbc);
        /*
         * Execution Order
         */
        gbc.gridy++;
        gbc.weighty = 0.0;
        rulePanel.add(new JLabel("Execution Order"), gbc);
        JPanel orderPanel = new JPanel();
        orderPanel.setLayout(new GridLayout());
        JPanel orderHolder = new JPanel(new BorderLayout(4, 0));
        orderHolder.add(new JLabel(UserEnvironment.getIcon("Forward")), BorderLayout.WEST);
        final JRadioButton orderByAgent = new JRadioButton("By Agent");
        orderByAgent.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (((JRadioButton) e.getSource()).isSelected()) {
                    currentScape.setExecutionOrder(Scape.AGENT_ORDER);
                }
            }
        });
        orderHolder.add(orderByAgent, BorderLayout.CENTER);
        orderPanel.add(orderHolder);
        JPanel orderHolder2 = new JPanel(new BorderLayout(4, 0));
        orderHolder2.add(new JLabel(UserEnvironment.getIcon("DocumentIn")), BorderLayout.WEST);
        final JRadioButton orderByRule = new JRadioButton("By Rule");
        orderByRule.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (((JRadioButton) e.getSource()).isSelected()) {
                    currentScape.setExecutionOrder(Scape.RULE_ORDER);
                }
            }
        });
        orderHolder2.add(orderByRule, BorderLayout.CENTER);
        orderPanel.add(orderHolder2);
        ButtonGroup orderGroup = new ButtonGroup();
        orderGroup.add(orderByAgent);
        orderGroup.add(orderByRule);
        gbc.gridy++;
        rulePanel.add(orderPanel, gbc);
        /*
         * Execution Style
         */
        gbc.gridy++;
        rulePanel.add(new JLabel("Execution Style"), gbc);
        JPanel stylePanel = new JPanel();
        stylePanel.setLayout(new GridLayout());
        JPanel styleHolder = new JPanel(new BorderLayout(4, 0));
        styleHolder.add(new JLabel(UserEnvironment.getIcon("Thread")), BorderLayout.WEST);
        final JRadioButton styleCompleteTour = new JRadioButton("Complete Tour");
        styleCompleteTour.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (((JRadioButton) e.getSource()).isSelected()) {
                    currentScape.setExecutionStyle(Scape.COMPLETE_TOUR);
                }
            }
        });
        styleHolder.add(styleCompleteTour, BorderLayout.CENTER);
        stylePanel.add(styleHolder);
        JPanel styleHolder2 = new JPanel(new BorderLayout(4, 0));
        styleHolder2.add(new JLabel(UserEnvironment.getIcon("Box")), BorderLayout.WEST);
        final JRadioButton styleRepeatedDraw = new JRadioButton("Repeated Draw");
        styleRepeatedDraw.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (((JRadioButton) e.getSource()).isSelected()) {
                    currentScape.setExecutionStyle(Scape.REPEATED_DRAW);
                }
            }
        });
        styleHolder2.add(styleRepeatedDraw, BorderLayout.CENTER);
        stylePanel.add(styleHolder2);
        ButtonGroup styleGroup = new ButtonGroup();
        styleGroup.add(styleCompleteTour);
        styleGroup.add(styleRepeatedDraw);
        gbc.gridy++;
        rulePanel.add(stylePanel, gbc);

        gbc.gridy++;
        rulePanel.add(new JLabel("Agents Per Iteration"), gbc);
        JPanel agentIterationsPanel = new JPanel();
        agentIterationsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        //agentIterationsPanel.setBorder(BorderFactory.createTitledBorder("Data Points"));
        iterationsField.setText(Integer.toString(lastAgentsPerIteration));
        iterationsField = new JTextField(Integer.toString(lastAgentsPerIteration));
        iterationsField.setPreferredSize(new Dimension(40, iterationsField.getPreferredSize().height));
        final JRadioButton iterateAllAgentsButton = new JRadioButton("All");
        iterateAllAgentsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (((JRadioButton) e.getSource()).isSelected()) {
                    currentScape.setAgentsPerIteration(Scape.ALL_AGENTS);
                    iterationsField.setEnabled(false);
                } else {
                    currentScape.setAgentsPerIteration(lastAgentsPerIteration);
                    iterationsField.setEnabled(true);
                }
            }
        });
        agentIterationsPanel.add(iterateAllAgentsButton);
        final JRadioButton iterateNAgentsButton = new JRadioButton("n:");
        iterateNAgentsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (((JRadioButton) e.getSource()).isSelected()) {
                    currentScape.setAgentsPerIteration(lastAgentsPerIteration);
                    iterationsField.setEnabled(true);
                } else {
                    currentScape.setAgentsPerIteration(Scape.ALL_AGENTS);
                    iterationsField.setEnabled(false);
                }
            }
        });
        agentIterationsPanel.add(iterateNAgentsButton);
        iterationsField.getDocument().addDocumentListener(new DocumentListener() {
            public void updated() {
                try {
                    lastAgentsPerIteration = Integer.parseInt(iterationsField.getText());
                } catch (NumberFormatException e) {
                    lastAgentsPerIteration = currentScape.getSize();
                    //iterationsField.setText(Integer.toString(lastAgentsPerIteration));
                }
                if (lastAgentsPerIteration == 0) {
                    lastAgentsPerIteration = currentScape.getSize();
                    //iterationsField.setText(Integer.toString(lastAgentsPerIteration));
                }
                if (lastAgentsPerIteration < 0) {
                    lastAgentsPerIteration = -lastAgentsPerIteration;
                    //iterationsField.setText(Integer.toString(lastAgentsPerIteration));
                }
                if (iterationsField.isEnabled()) {
                    currentScape.setAgentsPerIteration(lastAgentsPerIteration);
                }
            }

            public void changedUpdate(DocumentEvent e) {
                updated();
                iterationsField.setText(Integer.toString(lastAgentsPerIteration));
            };
            public void insertUpdate(DocumentEvent e) {
                updated();
            };
            public void removeUpdate(DocumentEvent e) {
                updated();
            };
        });
        ButtonGroup iterationsGroup = new ButtonGroup();
        iterationsGroup.add(iterateAllAgentsButton);
        iterationsGroup.add(iterateNAgentsButton);
        agentIterationsPanel.add(iterationsField);
        gbc.gridy++;
        rulePanel.add(agentIterationsPanel, gbc);
        ActionListener scapeChangedAction = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                currentScape = (Scape) scapeList.getSelectedItem();
                availableRules.fireTableDataChanged();
                if (currentScape.getExecutionOrder() == Scape.AGENT_ORDER) {
                    orderByAgent.setSelected(true);
                } else {
                    orderByRule.setSelected(true);
                }
                if (currentScape.getExecutionStyle() == Scape.COMPLETE_TOUR) {
                    styleCompleteTour.setSelected(true);
                } else {
                    styleRepeatedDraw.setSelected(true);
                }
                if (currentScape.getAgentsPerIteration() == Scape.ALL_AGENTS) {
                    iterateAllAgentsButton.setSelected(true);
                    iterationsField.setEnabled(false);
                    lastAgentsPerIteration = currentScape.getSize();
                    iterationsField.setText(Integer.toString(lastAgentsPerIteration));
                } else {
                    iterateNAgentsButton.setSelected(true);
                    iterationsField.setEnabled(true);
                    lastAgentsPerIteration = currentScape.getAgentsPerIteration();
                    iterationsField.setText(Integer.toString(lastAgentsPerIteration));
                }
            }
        };
        scapeList.addActionListener(scapeChangedAction);
        //Select first item and call on scape selection
        scapeChangedAction.actionPerformed(null);
    }

    /**
     * Gets the tab pane.
     * 
     * @return the tab pane
     */
    public JTabbedPane getTabPane() {
        return tabPane;
    }

    /**
     * Retrieve the settings from the model, and update the panel's components
     * to reflect them. Takes all the accessor values and assigns them to the
     * fields.
     */
    /*public void retrieveSettings() {
        if (!netscapeFailure) {
            for (int i = 0; i < settingsAccessors.length; i++) {
                //To do: fix to handle custom editors
                if (settingsComponents[i] instanceof JTextField) {
                    ((JTextField) settingsComponents[i]).setText(settingsAccessors[i].getAsText());
                }
            }
        }
    }*/

    /**
     * Assign the changes made in the panel's components back to the model.
     */
    public void assignSettings() {
        int r = settingsTable.getEditingRow();
        int c = settingsTable.getEditingColumn();

        if ((r != -1) && (c != -1)) {
            // some cell is being edited
            CellEditor ed = settingsTable.getCellEditor(r, c);
            // try to stop cell editing and cause Java to
            // accept the value that has been entered
            if (ed.stopCellEditing()) {
                // cell editing has been stopped successfully, so get value
                settingsTable.setValueAt(ed.getCellEditorValue(), r, c);
            }
        }
    }

    /**
     * The data model for displaying all parameters that may be set.
     */
    class SettingsModel extends AbstractTableModel implements Serializable {

        /* (non-Javadoc)
         * @see javax.swing.table.TableModel#getRowCount()
         */
        public int getRowCount() {
            return allAccessors.length;
        }

        /* (non-Javadoc)
         * @see javax.swing.table.TableModel#getColumnCount()
         */
        public int getColumnCount() {
            return 2;
        }

        /* (non-Javadoc)
         * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
         */
        public synchronized void setValueAt(Object object, int row, int col) {
            try {
                Object oldValue = allAccessors[row].getValue();
                allAccessors[row].setAsText((String) object);
                propertySupport.firePropertyChange(allAccessors[row].getName(), oldValue, object);
            } catch (InvocationTargetException e) {
                throw new Error("Exception in called method: " + e.getTargetException());
            } catch (IllegalArgumentException e) {
                //Ignore
            }
        }

        /* (non-Javadoc)
         * @see javax.swing.table.TableModel#getValueAt(int, int)
         */
        public Object getValueAt(int row, int col) {
            if (col == 0) {
                return allAccessors[row].getLongName();
            } else {
                return allAccessors[row].getAsText();
            }
        }

        /* (non-Javadoc)
         * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
         */
        public boolean isCellEditable(int row, int col) {
            if (col > 0) {
                return true;
            } else {
                return false;
            }
        }

        /* (non-Javadoc)
         * @see javax.swing.table.AbstractTableModel#getColumnName(int)
         */
        public String getColumnName(int col) {
            if (col == 0) {
                return "Parameter";
            } else {
                return "Value";
            }
        }
        /*public Class getColumnClass(int col) {
            if (col >= 0) {
                return Boolean.class;
            }
            else {
                return String.class;
            }
        }*/
    }

    /**
     * The data model for displaying all rules that may be used.
     */
    class RuleModel extends AbstractTableModel implements Serializable {

        /* (non-Javadoc)
         * @see javax.swing.table.TableModel#getRowCount()
         */
        public int getRowCount() {
            return currentScape.getRules().getVector().size();
        }

        /* (non-Javadoc)
         * @see javax.swing.table.TableModel#getColumnCount()
         */
        public int getColumnCount() {
            return 2;
        }

        /* (non-Javadoc)
         * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
         */
        public synchronized void setValueAt(Object object, int row, int col) {
            currentScape.getRules().setSelected(row, ((Boolean) object).booleanValue());
        }

        /* (non-Javadoc)
         * @see javax.swing.table.TableModel#getValueAt(int, int)
         */
        public Object getValueAt(int row, int col) {
            if (col == 0) {
                return ((Rule) currentScape.getRules().getVector().elementAt(row)).getName();
            } else {
                return new Boolean(currentScape.getRules().isSelected(row));
            }
        }

        /* (non-Javadoc)
         * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
         */
        public boolean isCellEditable(int row, int col) {
            if (col > 0) {
                return true;
            } else {
                return false;
            }
        }

        /* (non-Javadoc)
         * @see javax.swing.table.AbstractTableModel#getColumnName(int)
         */
        public String getColumnName(int col) {
            if (col > 0) {
                return "Active";
            } else {
                return "Rule";
            }
        }

        /* (non-Javadoc)
         * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
         */
        public Class getColumnClass(int col) {
            if (col >= 0) {
                return Boolean.class;
            } else {
                return String.class;
            }
        }
    }

    /**
     * The Class NullCheckBoxRenderer.
     */
    static class NullCheckBoxRenderer extends DefaultTableCellRenderer implements Serializable {

        //public NullCheckBoxRenderer() {
        //    super(); //Unfortunately, the constructor
        //    component = new JCheckBox();
        //}

        /* (non-Javadoc)
         * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
         */
        public Component getTableCellRendererComponent(
            JTable table, Object value,
            boolean isSelected, boolean hasFocus,
            int row, int column) {
            if (value instanceof Boolean) {
                //return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                JCheckBox cb = new JCheckBox();
                cb.setSelected(((Boolean) value).booleanValue());
                cb.setBackground(this.getBackground());
                cb.setHorizontalAlignment(AbstractButton.CENTER);
                return cb;
            } else if (value instanceof String) {
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                /*JTextField label = new JTextField((String) value);
                label.setOpaque(true);
                label.setBackground(this.getBackground());
                label.setForeground(Color.black);
                //label.setBorder(BorderFactory.createEtchedBorder());
                return label;*/
            } else {
                return new JLabel();
            }
        }
    }
}
