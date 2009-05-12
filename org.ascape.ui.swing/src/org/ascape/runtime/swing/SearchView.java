/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */

package org.ascape.runtime.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.ascape.model.Scape;
import org.ascape.query.Query;
import org.ascape.query.parser.ParseException;
import org.ascape.util.Utility;
import org.ascape.view.vis.PanelView;

/**
 * The Class SearchView.
 */
public class SearchView extends PanelView {

    /**
     * The Class DialogButton.
     */
    private class DialogButton extends JButton {

        /**
         * 
         */
        private static final long serialVersionUID = -3774436696137676290L;

        /**
         * Instantiates a new dialog button.
         * 
         * @param text
         *            the text
         */
        public DialogButton(String text) {
            super(text);
        }

        /* (non-Javadoc)
         * @see javax.swing.JComponent#getPreferredSize()
         */
        public Dimension getPreferredSize() {
            return new Dimension(80, super.getPreferredSize().height);
        }
    }

    /**
     * The search scape.
     */
    Scape searchScape;

    /**
     * The dynamic check box.
     */
    JCheckBox dynamicCheckBox;

    /* (non-Javadoc)
     * @see org.ascape.view.vis.PanelView#build()
     */
    public void build() {
        setPreferredSize(new Dimension(400, 100));
        setName("Search Agents");
        setFont(new Font(getFont().getName(), Font.PLAIN, getFont().getSize()));

        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);
        GridBagConstraints gbc = gbl.getConstraints(this);

        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.ipadx = 6;
        gbc.ipady = 6;
        gbc.gridx = 0;
        gbc.gridy = 0;

        List scapes = getScape().getAllScapes();
        final JComboBox scapeList = new JComboBox(new Vector(scapes));
        ActionListener scapeChangedAction = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchScape = (Scape) scapeList.getSelectedItem();
            }
        };
        scapeList.addActionListener(scapeChangedAction);
        //Select first item and call on scape selection
        scapeChangedAction.actionPerformed(null);
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        final JLabel comp = new JLabel("Scape:");
        add(comp, gbc);
        gbc.gridx++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(scapeList, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        gbc.fill = GridBagConstraints.NONE;
        final JLabel comp2 = new JLabel("Query:");
        add(comp2, gbc);
        gbc.gridx++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        final JTextField queryArea = new JTextField();
        add(queryArea, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        JButton search = new DialogButton("Search");
        search.setAction(new AbstractAction("Search") {
            /**
             * 
             */
            private static final long serialVersionUID = -972914149190896812L;

            public void actionPerformed(ActionEvent e) {
                final Query query;
                try {
                    query = new Query(searchScape, queryArea.getText(), dynamicCheckBox.isSelected());

                    SearchView.this.getViewFrame().dispose();
                    if (dynamicCheckBox.isSelected()) {
                        searchScape.addView(query);
                    }
                    Container frameImp = DesktopEnvironment.getDefaultDesktop().getUserFrame();
                    final ProgressMonitor monitor = new ProgressMonitor(frameImp, "Searching...", "", 0, query.getSearchSize());
                    monitor.setNote("                                                                                                    ");
                    monitor.setMillisToPopup(0);
                    monitor.setProgress(0);
                    final Thread queryThread = new Thread("Query Execution: " + queryArea.getText()) {
                        public void run() {
                            query.execute();
                            if (!monitor.isCanceled()) {
                                searchScape.addView(new AgentSelectionView(query));
                            }
                        }
                    };
                    queryThread.start();
                    new Thread("Progress Monitor: " + queryArea.getText()) {
                        public void run() {
                            while (!query.isEvaluating()) {
                                try {
                                    sleep(100);
                                } catch (InterruptedException e1) {
                                    e1.printStackTrace();  //To change body of catch statement use Options | File Templates.
                                }
                            }
                            monitor.setMaximum(query.getSearchSize());
                            //todo we need to actually stop querying if the thread is cancelled but there is no way to do this
                            //now without rewriting find iteration to allow cancelling of an in process conditon execution
                            while (query.isEvaluating() && !monitor.isCanceled()) {
                                if (query.getQueryString() != null) {
                                    SwingUtilities.invokeLater(new Runnable() {
                                        public void run() {
                                            monitor.setProgress(query.getSearchPosition());
                                            monitor.setNote("Searched " + Utility.padStringLeftWithZeros(Integer.toString(query.getSearchPosition()), (query.getSearchSize() + "").length()) + " of " + query.getSearchSize() + " / Found " + query.getSearchFound());
                                        }
                                    });
                                }
                                try {
                                    sleep(100);
                                } catch (InterruptedException e1) {
                                    e1.printStackTrace();  //To change body of catch statement use Options | File Templates.
                                }
                            }
                            monitor.close();
                        }
                    }.start();
                } catch (ParseException e1) {
                    JOptionPane.showMessageDialog(SearchView.this, "Couldn't understand query: " + e1.toString(), "Query Error", JOptionPane.ERROR_MESSAGE);  //To change body of catch statement use Options | File Templates.
                }
            }
        });
        JButton cancel = new DialogButton("Cancel");
        cancel.setAction(new AbstractAction("Cancel") {
            /**
             * 
             */
            private static final long serialVersionUID = 6027128893612115906L;

            public void actionPerformed(ActionEvent e) {
                SearchView.this.getViewFrame().dispose();
            }
        });
        JButton help = new DialogButton("Help");
        help.setAction(new AbstractAction("Help") {
            /**
             * 
             */
            private static final long serialVersionUID = 7531098784367856449L;

            public void actionPerformed(ActionEvent e) {
                String urlString ="";
                try {
                    final JPanel panel = new JPanel() {
                        /**
                         * 
                         */
                        private static final long serialVersionUID = 8991627308734234096L;

                        public Dimension getPreferredSize() {
                            return new Dimension(700, 600);
                        }
                    };
                    panel.setLayout(new BorderLayout());
                    urlString = "../model/SearchHelp.html";
                    JEditorPane editorPane = new JEditorPane();
                    editorPane.setEditable(false);
                    URL url = getClass().getResource(urlString);
                    editorPane.setPage(url);
                    JScrollPane scrollPane = new JScrollPane(editorPane);
                    Border b = new EmptyBorder(4, 4, 4, 4);
                    scrollPane.setBackground(Color.white);
                    scrollPane.setBorder(b);
                    panel.add(scrollPane, BorderLayout.CENTER);
                    JOptionPane.showMessageDialog(null, panel, "Search Help", JOptionPane.DEFAULT_OPTION);
                } catch (IOException e1) {
                    System.err.println("Tried to read the help file from: " + urlString);
                }
            }
        });

        dynamicCheckBox = new JCheckBox("Dynamic");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(dynamicCheckBox);
        buttonPanel.add(search);
        buttonPanel.add(cancel);
        buttonPanel.add(help);

        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);
    }
}
