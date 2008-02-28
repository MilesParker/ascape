/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */
package org.ascape.view.custom;

import java.awt.Color;
import java.awt.Component;
import java.io.Serializable;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

/**
 * The Class ColorRenderer.
 */
public class ColorRenderer extends JLabel
    implements Serializable, TableCellRenderer {

    /**
     * 
     */
    private static final long serialVersionUID = -8762840075101590635L;

    /**
     * The unselected border.
     */
    Border unselectedBorder = null;
    
    /**
     * The selected border.
     */
    Border selectedBorder = null;
    
    /**
     * The is bordered.
     */
    boolean isBordered = true;

    /**
     * Instantiates a new color renderer.
     * 
     * @param isBordered
     *            the is bordered
     */
    public ColorRenderer(boolean isBordered) {
        super();
        this.isBordered = isBordered;
        setOpaque(true); //MUST do this for background to show up.
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
     */
    public Component getTableCellRendererComponent(
        JTable table, Object color,
        boolean isSelected, boolean hasFocus,
        int row, int column) {
        setBackground((Color) color);
        if (isBordered) {
            if (isSelected) {
                if (selectedBorder == null) {
                    selectedBorder = BorderFactory.createMatteBorder(2, 5, 2, 5,
                        table.getSelectionBackground());
                }
                setBorder(selectedBorder);
            } else {
                if (unselectedBorder == null) {
                    unselectedBorder = BorderFactory.createMatteBorder(2, 5, 2, 5,
                        table.getBackground());
                }
                setBorder(unselectedBorder);
            }
        }
        this.setBackground(this.getBackground());
        return this;
    }
}
