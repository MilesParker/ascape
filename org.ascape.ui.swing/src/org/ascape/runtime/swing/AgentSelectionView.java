/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */
package org.ascape.runtime.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.ascape.model.Agent;
import org.ascape.model.Cell;
import org.ascape.model.CellOccupant;
import org.ascape.model.HostCell;
import org.ascape.model.event.ScapeEvent;
import org.ascape.query.Query;
import org.ascape.view.custom.ColorRenderer;
import org.ascape.view.vis.PanelView;


/**
 * The Class AgentSelectionView.
 */
public class AgentSelectionView extends PanelView {

    /**
     * The query.
     */
    Query query;

    /**
     * The agent selection.
     */
    AbstractTableModel agentSelection = new AbstractTableModel() {
        /**
         * 
         */
        private static final long serialVersionUID = -1892988083028382121L;

        public int getColumnCount() {
            return agentDescriptors.length;
        }

        public int getRowCount() {
            return query.getResults().size();
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            try {
                Method readMethod = agentDescriptors[columnIndex].getReadMethod();
                if (readMethod != null && agentDescriptors[columnIndex].getPropertyType() != Image.class) {
                    return readMethod.invoke(query.getResults().get(rowIndex), new Object[0]);
                } else {
                    return "";
                }
            } catch (Exception e) {
                throw new InternalError("Unexpected error: " + e);
            }
        }

        public String getColumnName(int column) {
            return agentDescriptors[column].getName();
        }

        public Class<?> getColumnClass(int columnIndex) {
            Class type = agentDescriptors[columnIndex].getPropertyType();
            if (type == Color.class) {
                return type;
            } else {
                return String.class;
            }
        }
    };

    /**
     * The agent descriptors.
     */
    private PropertyDescriptor[] agentDescriptors;

    /**
     * Instantiates a new agent selection view.
     * 
     * @param query
     *            the query
     */
    public AgentSelectionView(Query query) {
        this.query = query;
    }

    /**
     * Instantiates a new agent selection view.
     */
    public AgentSelectionView() {
    }

    /* (non-Javadoc)
     * @see org.ascape.view.vis.PanelView#build()
     */
    public void build() {
        setPreferredSize(new Dimension(500, 600));
        setLayout(new BorderLayout(6, 6));
        Class stopClass = Agent.class;
        if (getScape().getPrototypeAgent() instanceof Cell) {
            stopClass = Cell.class;
        }
        if (getScape().getPrototypeAgent() instanceof CellOccupant) {
            stopClass = CellOccupant.class;
        }
        if (getScape().getPrototypeAgent() instanceof HostCell) {
            stopClass = HostCell.class;
        }
        try {
            agentDescriptors = Introspector.getBeanInfo(getScape().getPrototypeAgent().getClass(), stopClass).getPropertyDescriptors();
        } catch (IntrospectionException e) {
            throw new InternalError("Unexpected error: " + e);
        }
        JTable agentTable = new JTable(agentSelection);
        agentTable.setColumnSelectionAllowed(true);
        agentTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        add(agentTable.getTableHeader(), BorderLayout.PAGE_START);
        agentTable.setDefaultRenderer(Color.class, new ColorRenderer(true));
        JScrollPane scroll = new JScrollPane();
        scroll.setViewportView(agentTable);
        add(scroll, BorderLayout.CENTER);
        invalidate();
    }

    /**
     * Gets the query.
     * 
     * @return the query
     */
    public Query getQuery() {
        return query;
    }

    /**
     * Sets the query.
     * 
     * @param query
     *            the new query
     */
    public void setQuery(Query query) {
        this.query = query;
    }

    /* (non-Javadoc)
     * @see org.ascape.view.vis.PanelView#toString()
     */
    public String toString() {
        return super.toString();
    }

    /* (non-Javadoc)
     * @see org.ascape.view.vis.PanelView#scapeIterated(org.ascape.model.event.ScapeEvent)
     */
    public void scapeIterated(ScapeEvent scapeEvent) {
        super.scapeIterated(scapeEvent);
        agentSelection.fireTableDataChanged();
        notifyScapeUpdated();
    }

    /* (non-Javadoc)
     * @see org.ascape.view.vis.PanelView#getName()
     */
    public String getName() {
        return query.getSearchFound() + " " + getScape().getName() + " agents where " + query.getQueryString();
    }
}
