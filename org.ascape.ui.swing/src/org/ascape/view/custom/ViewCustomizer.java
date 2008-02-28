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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serializable;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.ascape.gis.view.MapView;
import org.ascape.model.Scape;
import org.ascape.model.event.ScapeEvent;
import org.ascape.runtime.Runner;
import org.ascape.runtime.swing.DesktopEnvironment;
import org.ascape.util.vis.DrawFeature;
import org.ascape.view.vis.AgentView;

/**
 * A panel for making live changes to a view. Requires Swing. This class is
 * quite complicated, and can safely be left alone unless you're curious about
 * Swing, or want to add new capabilites.
 * 
 * @author Miles Parker
 * @version 1.2
 * @history 1.2 7/9/99 redesigned all customizers, renamed from
 *          ViewSettingsFrame, Changed base class from Frame, various updates to
 *          support new base
 * @history 1.0 copied initial implementation from ChartSettingsFrame 1.0.2
 * @since 1.0
 */
public class ViewCustomizer extends BaseCustomizer {

    /**
     * The button for dismissing this dialog.
     */
    protected JButton okButton;

    /**
     * The view being edited. (This reference simply mirrors target, but we keep
     * it in for clarity.)
     */
    private AgentView view;

    /**
     * The data model for displaying all features that may be viewed.
     */
    class AvailableFeatureModel extends AbstractTableModel implements Serializable {

        /**
         * 
         */
        private static final long serialVersionUID = 4387350730111166920L;

        /* (non-Javadoc)
         * @see javax.swing.table.TableModel#getRowCount()
         */
        public int getRowCount() {
            if (view != null) {
                return view.getDrawSelection().getVector().size();
            } else {
                return 0;
            }
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
            boolean deselected = false;
            boolean b = ((Boolean) object).booleanValue();
            if (b == false) {
                deselected = true;
            }
            view.getDrawSelection().setSelected(row, b);
            view.getScape().requestUpdate();
            //todo: find a better way of doing this
            if (view instanceof MapView && deselected) { // HUGE HACK - needs to be fixed. kind of hacky - notifies observers (ie, MapView) that this draw feature was just deselected
                DrawFeature df = (DrawFeature) view.getDrawSelection().getVector().get(row);
                ((Scape.DrawFeatureObservable) view.getScape().getDrawFeaturesObservable()).setChanged();
                view.getScape().getDrawFeaturesObservable().notifyObservers(df);
            }
            ((Scape.DrawFeatureObservable) view.getScape().getRoot().getDrawFeaturesObservable()).setChanged();
            view.getScape().getRoot().getDrawFeaturesObservable().notifyObservers(view.getDrawSelection());
            view.requestUpdateAll();
            view.updateScapeGraphics();
            view.repaint();
            this.fireTableDataChanged();
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
                return "Show";
            } else {
                return "Feature";
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
    class NullCheckBoxRenderer extends DefaultTableCellRenderer implements Serializable {

        //public NullCheckBoxRenderer() {
        //    super(); //Unfortunately, the constructor
        //    component = new JCheckBox();
        //}

        /**
         * 
         */
        private static final long serialVersionUID = 7637386380155118668L;

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

    /**
     * Constructs the customizer.
     */
    public ViewCustomizer() {
        super();
    }

    /**
     * Constructs the frame.
     * 
     * @param view
     *            the chart view being edited.
     */
    public ViewCustomizer(final AgentView view) {
        this();
        setObject(view);
    }

    /**
     * Sets the chart view being edited.
     * 
     * @param cutomizedView
     *            the cutomized view
     */
    public void setObject(final Object cutomizedView) {
        super.setObject(cutomizedView);
        this.view = (AgentView) cutomizedView;
    }

    /**
     * Consturcts the customizer interface.
     */
    public void build() {
        super.build();
        setPreferredSize(new Dimension(280, 300));

        getViewFrame().setTitle(view.getName() + " Settings");
        if (Runner.isMultiWinEnvironment()) {
            getViewFrame().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        } else {
            getViewFrame().setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
        }
        update();

        if (getViewFrame().getFrameImp() instanceof JInternalFrame) {
            ((JInternalFrame) getViewFrame().getFrameImp()).addInternalFrameListener(new InternalFrameAdapter() {
                public void internalFrameClosing(InternalFrameEvent e) {
                    super.internalFrameClosing(e);
                    if (view != null) {
	                    view.removeCustomizer();
                    }
                }
            });
        } else {
            ((Window) getViewFrame().getFrameImp()).addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    super.windowClosing(e);
                    view.removeCustomizer();
                }
            });
        }
    }

    /* (non-Javadoc)
     * @see org.ascape.view.vis.PanelView#scapeRemoved(org.ascape.model.event.ScapeEvent)
     */
    public void scapeRemoved(ScapeEvent e) {
        super.scapeRemoved(e);
        scape = null;
        view = null;
    }

    /**
     * Sets up the frame for the view. Override to add any additional options.
     */
    public void update() {
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
        //availablePanel.setBorder(BorderFactory.createTitledBorder("Draw"));
        final AvailableFeatureModel availableFeatures = new AvailableFeatureModel();
        final JTable featureTable = new JTable(availableFeatures);
        featureTable.setDefaultRenderer(Boolean.class, new NullCheckBoxRenderer());
        featureTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        featureTable.setShowGrid(false);
        featureTable.getColumnModel().getColumn(1).setMaxWidth(60);
        featureTable.getColumnModel().getColumn(1).setMinWidth(60);
        featureTable.getColumnModel().getColumn(1).setResizable(false);
        featureTable.setShowGrid(false);
        //featureTable.getColumn(0).setMinimumSize(featureTable.getColumn(0).getMaximumSize());
        //featureTable.setDefaultEditor(Boolean.class, new NullCheckBoxEditor(new JCheckBox()));
        JScrollPane scrollPane = new JScrollPane(featureTable);
        availablePanel.add(scrollPane, "Center");
        JToolBar featureToolBar = new JToolBar();
        featureToolBar.setFloatable(false);
        JButton upButton = featureToolBar.add(new AbstractAction() {
            /**
             * 
             */
            private static final long serialVersionUID = -7566393552336232557L;

            public void actionPerformed(ActionEvent e) {
                int[] userSelFeatures = featureTable.getSelectedRows();
                if (userSelFeatures.length > 0) {
                    int min = userSelFeatures[0];
                    int max = userSelFeatures[0];
                    //Even though seleciton model is single interval, we can't be sure that they will
                    //be in order since this isn't specified in the api...
                    for (int i = 1; i < userSelFeatures.length; i++) {
                        if (userSelFeatures[i] < min) min = userSelFeatures[i];
                        if (userSelFeatures[i] > max) max = userSelFeatures[i];
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
                    ((Scape.DrawFeatureObservable) view.getScape().getRoot().getDrawFeaturesObservable()).setChanged();
                    view.getScape().getRoot().getDrawFeaturesObservable().notifyObservers(view.getDrawSelection());
                }
            }
        });
        upButton.setToolTipText("Move Selected Feature Up (Draw Before)");
        upButton.setIcon(DesktopEnvironment.getIcon("Up"));
        JButton downButton = featureToolBar.add(new AbstractAction() {
            /**
             * 
             */
            private static final long serialVersionUID = -4839257381827214350L;

            public void actionPerformed(ActionEvent e) {
                int[] userSelFeatures = featureTable.getSelectedRows();
                if (userSelFeatures.length > 0) {
                    int min = userSelFeatures[0];
                    int max = userSelFeatures[0];
                    //Even though seleciton model is single interval, we can't be sure that they will
                    //be in order since this isn't specified in the api...
                    for (int i = 1; i < userSelFeatures.length; i++) {
                        if (userSelFeatures[i] < min) min = userSelFeatures[i];
                        if (userSelFeatures[i] > max) max = userSelFeatures[i];
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
                    ((Scape.DrawFeatureObservable) view.getScape().getRoot().getDrawFeaturesObservable()).setChanged();
                    view.getScape().getRoot().getDrawFeaturesObservable().notifyObservers(view.getDrawSelection());
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
}
