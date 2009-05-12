/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */

package org.ascape.view.custom;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import org.ascape.model.Agent;
import org.ascape.model.Scape;
import org.ascape.runtime.swing.SwingEnvironment;
import org.ascape.util.PropertyAccessor;
import org.ascape.view.vis.AgentView;


/*
 * User: mparker
 * Date: Mar 18, 2003
 * Time: 7:04:53 PM
 * To change this template use Options | File Templates.
 */

/**
 * The Class AgentCustomizerPanel.
 */
public class AgentCustomizerPanel {

    /**
     * The data model for displaying agent property values.
     */
    class PropertiesModel extends AbstractTableModel implements Serializable {

        /**
         * 
         */
        private static final long serialVersionUID = -7556103457969889396L;

        /* (non-Javadoc)
         * @see javax.swing.table.TableModel#getRowCount()
         */
        public int getRowCount() {
            return accessors.length;
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
                Object oldValue = accessors[row].getReadMethod().invoke(object, (Object[]) null);
                PropertyAccessor.setAsText(agent, (String) object, accessors[row]);
                if (propertySupport != null) {
                    propertySupport.firePropertyChange(accessors[row].getName(), oldValue, object);
                }
            } catch (IllegalArgumentException e) {
                // primaryAccessors[row].setAsText(settingsAccessors[i].getAsText());
            } catch (InvocationTargetException e) {
                throw new Error("Exception in called method: " + e.getTargetException());
            } catch (IllegalAccessException e) {
                throw new Error("Exception in called method: " + e);
            }
        }

        /* (non-Javadoc)
         * @see javax.swing.table.TableModel#getValueAt(int, int)
         */
        public Object getValueAt(int row, int col) {
            if (col == 0) {
                return PropertyAccessor.getLongName(accessors[row]);
            } else {
                return PropertyAccessor.getAsText(agent, accessors[row]);
            }
        }

        /* (non-Javadoc)
         * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
         */
        public boolean isCellEditable(int row, int col) {
            if (col > 0) {
                return PropertyAccessor.isWriteable(accessors[row]);
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
    }

    /**
     * The Class JGrayNonEditTable.
     */
    static class JGrayNonEditTable extends JTable {

        /**
         * 
         */
        private static final long serialVersionUID = -174752549754256913L;

        /**
         * Instantiates a new j gray non edit table.
         * 
         * @param a
         *            the a
         */
        public JGrayNonEditTable(AbstractTableModel a) {
            super(a);
        }

        /* (non-Javadoc)
         * @see javax.swing.JTable#getCellRenderer(int, int)
         */
        public TableCellRenderer getCellRenderer(int row, int column) {
            TableCellRenderer p = super.getCellRenderer(row, column);
            if (column == 0 || isCellEditable(row, column)) {
                ((Component) p).setBackground(Color.white);
            } else {
                ((Component) p).setBackground(Color.lightGray);
            }
            return p;
        }
    }

    /**
     * The agent.
     */
    private Agent agent;

    /**
     * The last agent.
     */
    private Agent lastAgent;

    /**
     * The view.
     */
    private AgentView view;

    /**
     * The accessors.
     */
    private PropertyDescriptor[] accessors;

    /**
     * The properties.
     */
    private PropertiesModel properties;

    /**
     * The label.
     */
    private JLabel label;

    /**
     * The color.
     */
    private Color color;

    /**
     * The main panel.
     */
    private JPanel mainPanel;

    /**
     * The detail panel.
     */
    private JPanel detailPanel;

    /**
     * The properties panel.
     */
    private JPanel propertiesPanel;

    /**
     * The properties table.
     */
    private JTable propertiesTable;

    /**
     * The properties scroll pane.
     */
    private JScrollPane propertiesScrollPane;

    /**
     * The no properties message.
     */
    private JTextField noPropertiesMessage;

    /**
     * The no selected message.
     */
    private JTextField noSelectedMessage;

    /**
     * The no agent message.
     */
    private JTextField noAgentMessage;

    /**
     * The property support.
     */
    PropertyChangeSupport propertySupport;

    /**
     * Build.
     */
    public void build() {
        setMainPanel(new JPanel());
        mainPanel.setLayout(new BorderLayout());
        setLabel(new JLabel("Agent"));
        mainPanel.add(getLabel(), BorderLayout.NORTH);

        setDetailPanel(new JPanel());
        setPropertiesPanel(new JPanel());
        getPropertiesPanel().setLayout(new BorderLayout());
        getDetailPanel().setLayout(new BorderLayout());
        getDetailPanel().add(getPropertiesPanel(), BorderLayout.CENTER);
        getMainPanel().add(getDetailPanel(), BorderLayout.CENTER);
        accessors = new PropertyDescriptor[0];
        properties = new PropertiesModel();
        propertiesTable = new JGrayNonEditTable(properties);
        propertiesTable.setRowSelectionAllowed(true);
        propertiesTable.setShowGrid(false);
        propertiesTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        final DefaultCellEditor ed = (DefaultCellEditor) propertiesTable.getDefaultEditor(Object.class);
        ed.setClickCountToStart(0);
        propertiesScrollPane = new JScrollPane(propertiesTable);

        noPropertiesMessage = new JTextField("[No Accessible Properties]");
        noPropertiesMessage.setEditable(false);
        noPropertiesMessage.setHorizontalAlignment(JTextField.CENTER);
        noPropertiesMessage.setBackground(Color.white);
        noSelectedMessage = new JTextField("[No Agent Selected]");
        noSelectedMessage.setEditable(false);
        noSelectedMessage.setHorizontalAlignment(JTextField.CENTER);
        noSelectedMessage.setBackground(Color.white);
        noAgentMessage = new JTextField("[No Agent at Agent]");
        noAgentMessage.setEditable(false);
        noAgentMessage.setHorizontalAlignment(JTextField.CENTER);
        noAgentMessage.setBackground(Color.white);
    }

    /**
     * On agent change.
     */
    public void onAgentChange() {
        if (propertiesTable != null) {
            DefaultCellEditor primaryEd = (DefaultCellEditor) propertiesTable.getCellEditor();
            if (primaryEd != null) {
                primaryEd.stopCellEditing();
            }
        }
        propertiesPanel.removeAll();
        if (agent != null) {
            label.setText(agent.toString());

            if (SwingEnvironment.DEFAULT_ENVIRONMENT != null) {
                propertySupport = SwingEnvironment.DEFAULT_ENVIRONMENT.getPropertySupportForObject(agent);
            }

            try {
                if (!(agent instanceof Scape)) {
                    //                    List accessorsList = PropertyAccessor.determineReadWriteAccessors(agent, Agent.class, true);
                    accessors = Introspector.getBeanInfo(agent.getClass(), Agent.class).getPropertyDescriptors();
                } else {
                    accessors = Introspector.getBeanInfo(agent.getClass(), Scape.class).getPropertyDescriptors();
                }
                List accessorsWithReadMethods = new ArrayList();
                for (int i = 0; i < accessors.length; i++) {
                    PropertyDescriptor accessor = accessors[i];
                    if (accessor.getReadMethod() != null) {
                        accessorsWithReadMethods.add(accessors[i]);
                    }
                }
                accessors = new PropertyDescriptor[accessorsWithReadMethods.size()];
                accessors = (PropertyDescriptor[]) accessorsWithReadMethods.toArray(accessors);
                Arrays.sort(accessors, new Comparator() {
                    public int compare(Object o1, Object o2) {
                        PropertyDescriptor p1 = (PropertyDescriptor) o1;
                        PropertyDescriptor p2 = (PropertyDescriptor) o2;
                        if (PropertyAccessor.isWriteable(p1) && !PropertyAccessor.isWriteable(p2)) {
                            return -1;
                        } else if (PropertyAccessor.isWriteable(p2) && !PropertyAccessor.isWriteable(p1)) {
                            return 1;
                        }
                        return p1.getName().compareTo(p2.getName());
                    }
                });
                if (accessors.length > 0) {
                    propertiesPanel.add(propertiesScrollPane, BorderLayout.CENTER);
                } else {
                    propertiesPanel.add(noPropertiesMessage, BorderLayout.CENTER);
                }
            } catch (IntrospectionException e) {
                propertiesPanel.add(new JLabel("Error"), BorderLayout.CENTER);
                throw new RuntimeException("An introspection error occured while trying to determine agent properties.");
            }
        } else {
            label.setText("Agent");
            propertiesPanel.add(new JLabel("(No Agent Selected)"), BorderLayout.CENTER);
        }
        //propertiesPanel.setBackground(color);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    /**
     * Gets the preferred size.
     * 
     * @return the preferred size
     */
    public Dimension getPreferredSize() {
        return new Dimension(350, 180);
    }

    /**
     * Update color.
     */
    public void updateColor() {
        if (agent != null) {
            if (view != null) {
                color = view.getAgentColorFeature().getColor(agent);
            } else {
                color = agent.getColor();
            }
        } else {
            color = Color.lightGray;
        }
        mainPanel.setBackground(color);
        detailPanel.setBackground(color);
        propertiesPanel.setBackground(color);
        label.setBackground(color);
    }

    /**
     * Gets the main panel.
     * 
     * @return the main panel
     */
    public JPanel getMainPanel() {
        return mainPanel;
    }

    /**
     * Sets the main panel.
     * 
     * @param mainPanel
     *            the new main panel
     */
    public void setMainPanel(JPanel mainPanel) {
        this.mainPanel = mainPanel;
    }

    /**
     * Gets the detail panel.
     * 
     * @return the detail panel
     */
    public JPanel getDetailPanel() {
        return detailPanel;
    }

    /**
     * Sets the detail panel.
     * 
     * @param detailPanel
     *            the new detail panel
     */
    public void setDetailPanel(JPanel detailPanel) {
        this.detailPanel = detailPanel;
    }

    /**
     * Gets the properties.
     * 
     * @return the properties
     */
    public PropertiesModel getProperties() {
        return properties;
    }

    /**
     * Sets the properties.
     * 
     * @param properties
     *            the new properties
     */
    public void setProperties(PropertiesModel properties) {
        this.properties = properties;
    }

    /**
     * Gets the properties table.
     * 
     * @return the properties table
     */
    public JTable getPropertiesTable() {
        return propertiesTable;
    }

    /**
     * Sets the properties table.
     * 
     * @param propertiesTable
     *            the new properties table
     */
    public void setPropertiesTable(JTable propertiesTable) {
        this.propertiesTable = propertiesTable;
    }

    /**
     * Gets the properties panel.
     * 
     * @return the properties panel
     */
    public JPanel getPropertiesPanel() {
        return propertiesPanel;
    }

    /**
     * Sets the properties panel.
     * 
     * @param propertiesPanel
     *            the new properties panel
     */
    public void setPropertiesPanel(JPanel propertiesPanel) {
        this.propertiesPanel = propertiesPanel;
    }

    /**
     * Gets the properties scroll pane.
     * 
     * @return the properties scroll pane
     */
    public JScrollPane getPropertiesScrollPane() {
        return propertiesScrollPane;
    }

    /**
     * Sets the properties scroll pane.
     * 
     * @param propertiesScrollPane
     *            the new properties scroll pane
     */
    public void setPropertiesScrollPane(JScrollPane propertiesScrollPane) {
        this.propertiesScrollPane = propertiesScrollPane;
    }

    /**
     * Gets the label.
     * 
     * @return the label
     */
    public JLabel getLabel() {
        return label;
    }

    /**
     * Sets the label.
     * 
     * @param label
     *            the new label
     */
    public void setLabel(JLabel label) {
        this.label = label;
    }

    /**
     * Gets the color.
     * 
     * @return the color
     */
    public Color getColor() {
        return color;
    }

    /**
     * Sets the color.
     * 
     * @param color
     *            the new color
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Gets the agent.
     * 
     * @return the agent
     */
    public Agent getAgent() {
        return agent;
    }

    /**
     * Sets the agent.
     * 
     * @param agent
     *            the new agent
     */
    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    /**
     * Gets the last agent.
     * 
     * @return the last agent
     */
    public Agent getLastAgent() {
        return lastAgent;
    }

    /**
     * Sets the last agent.
     * 
     * @param lastAgent
     *            the new last agent
     */
    public void setLastAgent(Agent lastAgent) {
        this.lastAgent = lastAgent;
    }

    /**
     * Gets the view.
     * 
     * @return the view
     */
    public AgentView getView() {
        return view;
    }

    /**
     * Sets the view.
     * 
     * @param view
     *            the new view
     */
    public void setView(AgentView view) {
        this.view = view;
    }
}
