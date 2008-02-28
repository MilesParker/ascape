/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.view.vis.erv;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.ascape.runtime.swing.DesktopEnvironment;
import org.ascape.util.vis.DrawFeature;
import org.ascape.view.custom.BaseCustomizer;


/**
 * The Class ERVViewCustomizer.
 * 
 * @author Roger Critchlow
 * @version 2.9
 * @history 2.9 Moved into main Ascape.
 * @history 1.0 (Class version) 06/05/01 initial definition
 * @since 1.0
 */
public class ERVViewCustomizer extends BaseCustomizer {

    /**
     * The button for dismissing this dialog.
     */
    private JButton okButton;

    /**
     * The view being edited. (This reference simply mirrors target, but we keep
     * it in for clarity.)
     */
    private EntityRelationView view;

    /**
     * The data model for displaying all features that may be viewed.
     * 
     * @author Miles Parker, Matthew Hendrey, and others
     */
    class AvailableFeatureModel extends AbstractTableModel implements Serializable {

        /**
         * 
         */
        private static final long serialVersionUID = 965104711773717071L;

        /* (non-Javadoc)
         * @see javax.swing.table.TableModel#getRowCount()
         */
        public int getRowCount() {
            return view != null ? view.getDrawSelection().getVector().size() : 0;
        }

        /* (non-Javadoc)
         * @see javax.swing.table.TableModel#getColumnCount()
         */
        public int getColumnCount() {
            return 2;
        }

        /* (non-Javadoc)
         * @see javax.swing.table.TableModel#getValueAt(int, int)
         */
        public Object getValueAt(int row, int col) {
            if (col == 0) {
                return ((DrawFeature) view.getDrawSelection().getVector().elementAt(row)).getName();
            } else {
                return new Boolean(view.getDrawSelection().isSelected(row));
            }
        }

        /* (non-Javadoc)
         * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
         */
        public boolean isCellEditable(int row, int col) {
            return col > 0;
        }

        /* (non-Javadoc)
         * @see javax.swing.table.AbstractTableModel#getColumnName(int)
         */
        public String getColumnName(int col) {
            return col > 0 ? "Show" : "Feature";
        }

        /* (non-Javadoc)
         * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
         */
        public Class getColumnClass(int col) {
            return col > 0 ? Boolean.class : String.class;
        }

        /* (non-Javadoc)
         * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
         */
        public synchronized void setValueAt(Object object, int row, int col) {
            view.getDrawSelection().setSelected(row, ((Boolean) object).booleanValue());
            view.getScape().requestUpdate();
        }
    }

    /**
     * Describe class <code>NullCheckBoxRenderer</code> here.
     */
    class NullCheckBoxRenderer extends DefaultTableCellRenderer implements Serializable {

        /**
         * 
         */
        private static final long serialVersionUID = -5450463752732774757L;

        /* (non-Javadoc)
         * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
         */
        public Component getTableCellRendererComponent(
            JTable table, Object value,
            boolean isSelected, boolean hasFocus,
            int row, int column) {
            if (value instanceof Boolean) {
                JCheckBox cb = new JCheckBox();
                cb.setSelected(((Boolean) value).booleanValue());
                cb.setBackground(this.getBackground());
                cb.setHorizontalAlignment(AbstractButton.CENTER);
                return cb;
            } else if (value instanceof String) {
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            } else {
                return new JLabel();
            }
        }
    }

    /**
     * Constructs the customizer.
     */
    public ERVViewCustomizer() {
        super();
    }

    /**
     * Constructs the frame.
     * 
     * @param view
     *            the view being edited.
     */
    public ERVViewCustomizer(final EntityRelationView view) {
        this();
        setObject(view);
    }

    /**
     * Constructs the customizer interface.
     */
    public void build() {
        super.build();
        setPreferredSize(new Dimension(280, 300));

        /*
         * Button Panel
         */
        okButton = new JButton("Done", DesktopEnvironment.getIcon("Check"));
        buttonPanel.add(okButton);
        okButton.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    getViewFrame().dispose();
                }
            });
        getRootPane().setDefaultButton(okButton);
        getViewFrame().setTitle(view.getName() + " Settings");
        update();
    }

    /**
     * Sets up the frame for the view. Override to add any additional options.
     */
    private void update() {
        /*
         * Basic setup
         */
        contentPanel.removeAll();
        GridBagLayout gbl = new GridBagLayout();
        contentPanel.setLayout(gbl);
        GridBagConstraints gbc = gbl.getConstraints(contentPanel);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(6, 6, 0, 6);

        /*
         * Available Data Table
         */
        gbc.gridy++;
        contentPanel.add(new JLabel("Select Draw Features"), gbc);
        JPanel availablePanel = new JPanel();

        availablePanel.setLayout(new BorderLayout());
        final AvailableFeatureModel availableFeatures = new AvailableFeatureModel();
        final JTable featureTable = new JTable(availableFeatures);
        featureTable.setDefaultRenderer(Boolean.class, new NullCheckBoxRenderer());
        featureTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        featureTable.setShowGrid(false);
        featureTable.getColumnModel().getColumn(1).setMaxWidth(60);
        featureTable.getColumnModel().getColumn(1).setMinWidth(60);
        featureTable.getColumnModel().getColumn(1).setResizable(false);
        featureTable.setShowGrid(false);
        JScrollPane scrollPane = new JScrollPane(featureTable);
        availablePanel.add(scrollPane, "Center");
        JToolBar featureToolBar = new JToolBar();
        featureToolBar.setFloatable(false);
        JButton upButton = featureToolBar.add(
            new AbstractAction() {
                /**
                 * 
                 */
                private static final long serialVersionUID = 9085229251345355909L;

                public void actionPerformed(ActionEvent e) {
                    int[] userSelFeatures = featureTable.getSelectedRows();
                    if (userSelFeatures.length > 0) {
                        int min = userSelFeatures[0];
                        int max = userSelFeatures[0];
                        //Even though selection model is single interval, we can't be sure that they will
                        //be in order since this isn't specified in the api...
                        for (int i = 1; i < userSelFeatures.length; i++) {
                            if (userSelFeatures[i] < min) {
                                min = userSelFeatures[i];
                            }
                            if (userSelFeatures[i] > max) {
                                max = userSelFeatures[i];
                            }
                        }
                        if (min > 0) {
                            //If min = 0, can't move up, so do nothing
                            Vector allFeatures = view.getDrawSelection().getVector();
                            Object swapFeature = allFeatures.elementAt(min - 1);
                            allFeatures.removeElementAt(min - 1);
                            //Java 1.2 replace above with this:
                            //Object swapFeature = allFeatures.remove(min - 1);
                            //Not max + 1, because we have removed one item
                            allFeatures.insertElementAt(swapFeature, max);
                            view.getScape().getDrawFeaturesObservable().notifyObservers();
                            view.getDrawSelection().update();
                            availableFeatures.fireTableDataChanged();
                            featureTable.getSelectionModel().setSelectionInterval(min - 1, max - 1);
                        }
                    }
                }
            });
        upButton.setToolTipText("Move Selected Feature Up (Draw Before)");
        upButton.setIcon(DesktopEnvironment.getIcon("Up"));
        JButton downButton = featureToolBar.add(
            new AbstractAction() {
                /**
                 * 
                 */
                private static final long serialVersionUID = 8981085140803144384L;

                public void actionPerformed(ActionEvent e) {
                    int[] userSelFeatures = featureTable.getSelectedRows();
                    if (userSelFeatures.length > 0) {
                        int min = userSelFeatures[0];
                        int max = userSelFeatures[0];
                        //Even though selection model is single interval, we can't be sure that they will
                        //be in order since this isn't specified in the api...
                        for (int i = 1; i < userSelFeatures.length; i++) {
                            if (userSelFeatures[i] < min) {
                                min = userSelFeatures[i];
                            }
                            if (userSelFeatures[i] > max) {
                                max = userSelFeatures[i];
                            }
                        }
                        Vector allFeatures = view.getDrawSelection().getVector();
                        if (max < allFeatures.size() - 1) {
                            //If max >= size, can't move down, so do nothing
                            Object swapFeature = allFeatures.elementAt(max + 1);
                            allFeatures.removeElementAt(max + 1);
                            //Java 1.2 replace above with this:
                            //Object swapFeature = allFeatures.remove(max + 1);
                            allFeatures.insertElementAt(swapFeature, min);
                            view.getScape().getDrawFeaturesObservable().notifyObservers();
                            view.getDrawSelection().update();
                            availableFeatures.fireTableDataChanged();
                            featureTable.getSelectionModel().setSelectionInterval(min + 1, max + 1);
                        }
                    }
                }
            });
        downButton.setToolTipText("Move Selected Feature Down (Draw After)");
        downButton.setIcon(DesktopEnvironment.getIcon("Down"));
        availablePanel.add(featureToolBar, BorderLayout.SOUTH);
        //gbc.insets = new Insets(6, 6, 0, 6);
        //gbc.gridwidth = GridBagConstraints.REMAINDER;
        //gbc.gridy++;
        //contentPanel.add(new JLabel("Pick Data Series"), gbc);
        gbc.gridy++;
        gbc.weighty = 3.0;
        //gbc.insets = new Insets(2, 6, 0, 6);
        contentPanel.add(availablePanel, gbc);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    /**
     * Sets the chart view being edited.
     * 
     * @param view
     *            an <code>EntityRelationView</code> value
     */
    public void setObject(final EntityRelationView view) {
        super.setObject(view);
        this.view = view;
    }
}
