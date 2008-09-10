/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc.,
 * Metascape LLC, and contributors. All rights reserved. This program and the
 * accompanying materials are made available solely under of the BSD license
 * "ascape-license.txt". Any referenced or included libraries carry licenses of
 * their respective copyright holders.
 */

package org.ascape.view.custom;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serializable;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import org.ascape.runtime.swing.DesktopEnvironment;
import org.ascape.util.data.DataSelection;
import org.ascape.util.data.DataSeries;
import org.ascape.util.data.StatCollector;
import org.ascape.util.swing.NumberOnlyField;
import org.ascape.view.vis.ChartView;

/**
 * A frame (modeless dialog) for making live changes to a chart. Requires Swing.
 * This class is quite complicated, and can safely be left alone unless you're
 * curious about Swing, or want to add additional chart editing capabilites.
 * 
 * @author Miles Parker
 * @version 1.9.2
 * @history 1.9.2 2/2/01 fixed to support quicker chart updating
 * @history 1.2 7/9/99 redesigned all customizers, renamed from
 *          ChartSettingsFrame, with new base class of BaseCustomizer, updated
 *          to support new base
 * @history 1.0.1 added many settings features
 * @history 1.0.2 changed name from ChartSettingsWindow
 * @since 1.0
 */
public class ChartCustomizer extends BaseCustomizer implements Serializable {

    /**
     * The Constant WINDOW_PREFERRED_WIDTH.
     */
    private static final int WINDOW_PREFERRED_WIDTH = 460;

    /**
     * The Constant EDITABLE_COLUMN_PREFERRED_WIDTH.
     */
    private static final int EDITABLE_COLUMN_PREFERRED_WIDTH = 40;

    /**
     * The chart view being edited. (This reference simply mirrors target, but
     * we keep it in for clarity.)
     */
    private ChartView chartView;

    /**
     * Number of data points to display when "last" check box is selected
     * (default is 100.)
     */
    private int lastDataPoints = 100;

    /**
     * The instance of our selected data model.
     */
    private JTable selectedTable;

    /**
     * Constructs the dialog.
     */
    public ChartCustomizer() {
        super();
        selectedTable = new JTable(new SelectedDataModel());
    }

    /**
     * Constructs the frame.
     * 
     * @param chartView the chart view being edited.
     */
    public ChartCustomizer(ChartView chartView) {
        super();
        setObject(chartView);
    }

    /**
     * Sets the chart view being edited.
     * 
     * @param chartView the chart view
     */
    public void setObject(ChartView chartView) {
        super.setObject(chartView);
        this.chartView = chartView;
        selectedTable = new JTable(new SelectedDataModel());
    }

    /*
     * (non-Javadoc)
     * @see org.ascape.view.custom.BaseCustomizer#build()
     */
    public void build() {
        super.build();
        if (getViewFrame().getFrameImp() instanceof JFrame) {
            ((JFrame) getViewFrame().getFrameImp()).addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    super.windowClosing(e);
                    chartView.removeCustomizer();
                }
            });
        } else {
            ((JInternalFrame) getViewFrame().getFrameImp()).addInternalFrameListener(new InternalFrameAdapter() {
                public void internalFrameClosing(InternalFrameEvent e) {
                    super.internalFrameClosing(e);
                    chartView.removeCustomizer();
                }
            });
        }
        setPreferredSize(new Dimension(WINDOW_PREFERRED_WIDTH, 600));
        getViewFrame().setTitle(chartView.getName() + " Settings");
        setupForChartType();
    }

    /**
     * Sets up the frame for the options specific to the chart type. Override to
     * add any additional options.
     */
    private void setupForChartType() {
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
        // (We use JPanel, not JPanel: availablePanel must use Swing)
        contentPanel.add(new JLabel("Pick Data Series"), gbc);
        JPanel availablePanel = new JPanel();
        availablePanel.setLayout(new BorderLayout());
        // availablePanel.setBorder(BorderFactory.createTitledBorder("Pick Data")
        // );
        JTable dataTable = new JTable(new AvailableDataModel());
        dataTable.setDefaultRenderer(Boolean.class, new NullCheckBoxRenderer());
        dataTable.setRowSelectionAllowed(false);
        dataTable.setShowGrid(false);
        // dataTable.getColumn(0).setMinimumSize(dataTable.getColumn(0).
        // getMaximumSize());
        // dataTable.setDefaultEditor(Boolean.class, new NullCheckBoxEditor(new
        // JCheckBox()));
        JScrollPane scrollPane = new JScrollPane(dataTable);
        availablePanel.add(scrollPane, "Center");
        dataTable.getColumnModel().getColumn(0).setPreferredWidth(
                WINDOW_PREFERRED_WIDTH - 35 - 7 * EDITABLE_COLUMN_PREFERRED_WIDTH);
        for (int i = 1; i < StatCollector.getAllMeasureNamesShort().length + 1; i++) {
            TableColumn col = dataTable.getColumnModel().getColumn(i);
            // col.setMinWidth(40);
            // col.setMaxWidth(40);
            col.setPreferredWidth(EDITABLE_COLUMN_PREFERRED_WIDTH);
            // col.sizeWidthToFit();
        }
        // gbc.insets = new Insets(6, 6, 0, 6);
        // gbc.gridwidth = GridBagConstraints.REMAINDER;
        // gbc.gridy++;
        // contentPanel.add(new JLabel("Pick Data Series"), gbc);
        gbc.weighty = 1.0;
        // gbc.insets = new Insets(2, 6, 0, 6);
        gbc.gridy++;
        contentPanel.add(availablePanel, gbc);
        /*
         * contentPanel.add(new JLabel("Chart Name:"), gbc); gbc.gridy = 1;
         * JTextField nameField = new JTextField("New Chart");
         * nameField.addActionListener(new ActionListener() { public void
         * actionPerformed(ActionEvent e) {
         * chartView.getViewModel().setName(e.getActionCommand()); } });
         * contentPanel.add(nameField, gbc);
         */

        gbc.weighty = 0.0;
        gbc.gridy++;
        contentPanel.add(new JLabel("Chart Type"), gbc);
        /*
         * Chart Type Selection
         */
        JPanel chartTypePanel = new JPanel();
        chartTypePanel.setLayout(new GridLayout());
        // chartTypePanel.setBorder(BorderFactory.createTitledBorder("Type"));

        JPanel chartTSHolder = new JPanel(new BorderLayout(4, 0));
        chartTSHolder.add(new JLabel(DesktopEnvironment.getIcon("LineGraph")), BorderLayout.WEST);
        JRadioButton chartTSRadioButton = new JRadioButton("Time Series");
        chartTSRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (((JRadioButton) e.getSource()).isSelected()) {
                    chartView.setChartType(ChartView.TIME_SERIES);
                    setupForChartType();
                }
            }
        });
        chartTSHolder.add(chartTSRadioButton, BorderLayout.CENTER);
        chartTypePanel.add(chartTSHolder);

        JPanel chartHistHolder = new JPanel(new BorderLayout(4, 0));
        chartHistHolder.add(new JLabel(DesktopEnvironment.getIcon("BarGraph")), BorderLayout.WEST);
        JRadioButton chartHistRadioButton = new JRadioButton("Histogram");
        chartHistRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (((JRadioButton) e.getSource()).isSelected()) {
                    chartView.setChartType(ChartView.HISTOGRAM);
                    setupForChartType();
                }
            }
        });
        chartHistHolder.add(chartHistRadioButton, BorderLayout.CENTER);
        chartTypePanel.add(chartHistHolder);

        JPanel chartPieHolder = new JPanel(new BorderLayout(4, 0));
        chartPieHolder.add(new JLabel(DesktopEnvironment.getIcon("PieGraph")), BorderLayout.WEST);
        JRadioButton chartPieRadioButton = new JRadioButton("Pie Chart");
        chartPieRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (((JRadioButton) e.getSource()).isSelected()) {
                    chartView.setChartType(ChartView.PIE);
                    setupForChartType();
                }
            }
        });
        chartPieHolder.add(chartPieRadioButton, BorderLayout.CENTER);
        chartTypePanel.add(chartPieHolder);

        ButtonGroup chartTypeGroup = new ButtonGroup();
        chartTypeGroup.add(chartTSRadioButton);
        chartTypeGroup.add(chartHistRadioButton);
        chartTypeGroup.add(chartPieRadioButton);
        if (chartView.getChartType() == ChartView.TIME_SERIES) {
            chartTSRadioButton.setSelected(true);
        } else if (chartView.getChartType() == ChartView.HISTOGRAM) {
            chartHistRadioButton.setSelected(true);
        } else if (chartView.getChartType() == ChartView.PIE) {
            chartPieRadioButton.setSelected(true);
        }
        // gbc.weighty = 0.0;
        gbc.gridy++;
        contentPanel.add(chartTypePanel, gbc);

        /*
         * Chart Color Selection
         */
        gbc.gridy++;
        contentPanel.add(new JLabel("Chart Colors"), gbc);
        JPanel chartColorsPanel = new JPanel();
        GridBagLayout cgbl = new GridBagLayout();
        GridBagConstraints cgbc = cgbl.getConstraints(chartColorsPanel);
        cgbc.gridx = 0;
        cgbc.gridy = 0;
        cgbc.gridwidth = GridBagConstraints.REMAINDER;
        cgbc.fill = GridBagConstraints.HORIZONTAL;
        cgbc.weightx = 1.0;
        cgbc.weighty = 0.0;
        chartColorsPanel.setLayout(cgbl);
        // chartColorsPanel.setBorder(BorderFactory.createTitledBorder("Colors"))
        // ;
        JPanel elementColorPanel = new JPanel();
        elementColorPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(3, 2, 3, 2)));
        GridLayout gl = new GridLayout(3, 2, 4, 4);
        elementColorPanel.setLayout(gl);
        JLabel fl = new JLabel("Foreground");
        fl.setForeground(Color.black);
        elementColorPanel.add(fl);
        final JButton fb = new JButton("");
        fb.setBorderPainted(false);
        fb.setBackground((Color) chartView.getChart().getPlot().getOutlinePaint());
        final JColorChooser fcolorChooser = new JColorChooser();
        final JDialog fdialog = JColorChooser.createDialog(fb, "Pick a Color", false, fcolorChooser, null, null); // XXXDoublecheck
        // this
        // is
        // OK

        fcolorChooser.getSelectionModel().addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                Color ec = ((ColorSelectionModel) e.getSource()).getSelectedColor();
                chartView.setForeground(ec);
                fb.setBackground(ec);
            }
        });
        fb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fcolorChooser.setColor(fb.getBackground());
                fdialog.setVisible(true);
            }
        });
        elementColorPanel.add(fb);
        JLabel bl = new JLabel("Background");
        bl.setForeground(Color.black);
        elementColorPanel.add(bl);
        final JButton bb = new JButton("");
        bb.setBackground(chartView.getBackground());
        bb.setBorderPainted(false);
        bb.setMargin(new Insets(0, 0, 0, 0));
        final JColorChooser bcolorChooser = new JColorChooser();
        final JDialog bdialog = JColorChooser.createDialog(bb, "Pick a Color", false, bcolorChooser, null, null); 

        bcolorChooser.getSelectionModel().addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                Color ec = ((ColorSelectionModel) e.getSource()).getSelectedColor();
                chartView.setBackground(ec);
                bb.setBackground(ec);
            }
        });
        bb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                bcolorChooser.setColor(bb.getBackground());
                bdialog.setVisible(true);
            }
        });
        elementColorPanel.add(bb);
        JLabel pbl = new JLabel("Plot Background");
        pbl.setForeground(Color.black);
        elementColorPanel.add(pbl);
        final JButton pbb = new JButton("");
        pbb.setBackground(chartView.getPlotBackground());
        pbb.setBorderPainted(false);
        pbb.setMargin(new Insets(0, 0, 0, 0));
        final JColorChooser pbcolorChooser = new JColorChooser();
        final JDialog pbdialog = JColorChooser.createDialog(pbb, "Pick a Color", false, pbcolorChooser, null, null); 

        pbcolorChooser.getSelectionModel().addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                Color ec = ((ColorSelectionModel) e.getSource()).getSelectedColor();
                chartView.setPlotBackground(ec);
                pbb.setBackground(ec);
            }
        });
        pbb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pbcolorChooser.setColor(pbb.getBackground());
                pbdialog.setVisible(true);
            }
        });
        elementColorPanel.add(pbb);
        chartColorsPanel.add(elementColorPanel, cgbc);

        JScrollPane selectPane = new JScrollPane(selectedTable);
        selectPane.setBorder(null);
        selectedTable.setBackground(this.getBackground());
        selectedTable.setShowGrid(false);
        selectedTable.setShowHorizontalLines(false);
        selectedTable.setShowVerticalLines(false);
        selectedTable.setTableHeader(null);
        setUpColorRenderer(selectedTable);
        setUpColorEditor(selectedTable);
        cgbc.gridy++;
        cgbc.weighty = 1.0;
        cgbc.fill = GridBagConstraints.BOTH;
        chartColorsPanel.add(selectPane, cgbc);

        // gbc.insets = new Insets(2, 6, 0, 6);
        gbc.gridy++;
        gbc.weighty = 0.3;
        cgbc.fill = GridBagConstraints.HORIZONTAL;
        contentPanel.add(chartColorsPanel, gbc);
        gbc.weighty = 0.0;

        /*
         * Show labels?
         */
        if (chartView.getChartType() != ChartView.PIE) {
            // Legens aren't needed for pie charts and currently they are the
            // only options
            gbc.gridy++;
            contentPanel.add(new JLabel("Options"), gbc);
            JPanel optionsPanel = new JPanel();
            optionsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            // optionsPanel.setBorder(BorderFactory.createTitledBorder("Options")
            // );
            JCheckBox showLabelCheckBox = new JCheckBox("Show Legend");
            showLabelCheckBox.setSelected(chartView.isLegendShowing());
            showLabelCheckBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    chartView.setLegendShowing(((JCheckBox) e.getSource()).isSelected());
                }
            });
            optionsPanel.add(showLabelCheckBox);
            gbc.gridy++;
            contentPanel.add(optionsPanel, gbc);
        }

        if (chartView.getChartType() == ChartView.TIME_SERIES) {
            /*
             * Select data points shown (All or last n)
             */
            gbc.gridy++;
            contentPanel.add(new JLabel("Data Points"), gbc);
            JPanel showPointsPanel = new JPanel();
            showPointsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            // showPointsPanel.setBorder(BorderFactory.createTitledBorder(
            // "Data Points"));
            final JTextField pointsField = new NumberOnlyField(Integer.toString(lastDataPoints), 6);
            JRadioButton showAllPointsButton = new JRadioButton("All");
            showAllPointsButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (((JRadioButton) e.getSource()).isSelected()) {
                        chartView.setDisplayPoints(ChartView.ALL_POINTS);
                        pointsField.setEnabled(false);
                    } else {
                        chartView.setDisplayPoints(lastDataPoints);
                        pointsField.setText(Integer.toString(lastDataPoints));
                        pointsField.setEnabled(true);
                    }
                }
            });
            showPointsPanel.add(showAllPointsButton);
            JRadioButton showLastPointsButton = new JRadioButton("Last");
            showLastPointsButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (((JRadioButton) e.getSource()).isSelected()) {
                        chartView.setDisplayPoints(lastDataPoints);
                        pointsField.setEnabled(true);
                    } else {
                        chartView.setDisplayPoints(ChartView.ALL_POINTS);
                        pointsField.setEnabled(false);
                    }
                }
            });
            showPointsPanel.add(showLastPointsButton);
            pointsField.getDocument().addDocumentListener(new DocumentListener() {
                public void updated() {
                    try {
                        lastDataPoints = Integer.parseInt(pointsField.getText());
                    } catch (NumberFormatException e) {
                        lastDataPoints = 100;
                        pointsField.setText(Integer.toString(lastDataPoints));
                    }
                    if (lastDataPoints == 0) {
                        lastDataPoints = 100;
                        pointsField.setText(Integer.toString(lastDataPoints));
                    }
                    if (lastDataPoints < 0) {
                        lastDataPoints = -lastDataPoints;
                        pointsField.setText(Integer.toString(lastDataPoints));
                    }
                    chartView.setDisplayPoints(lastDataPoints);
                }

                public void changedUpdate(DocumentEvent e) {
                    updated();
                    pointsField.setText(Integer.toString(lastDataPoints));
                };

                public void insertUpdate(DocumentEvent e) {
                    updated();
                };

                public void removeUpdate(DocumentEvent e) {
                    updated();
                };
            });
            ButtonGroup pointsGroup = new ButtonGroup();
            pointsGroup.add(showAllPointsButton);
            pointsGroup.add(showLastPointsButton);
            if (chartView.getDisplayPoints() == ChartView.ALL_POINTS) {
                showAllPointsButton.setSelected(true);
                pointsField.setEnabled(false);
            } else {
                showLastPointsButton.setSelected(true);
                pointsField.setEnabled(true);
            }
            showPointsPanel.add(pointsField);
            gbc.gridy++;
            contentPanel.add(showPointsPanel, gbc);
        }
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    /**
     * Sets the up color renderer.
     * 
     * @param table the new up color renderer
     */
    private void setUpColorRenderer(JTable table) {
        table.setDefaultRenderer(Color.class, new ColorRenderer(true));
    }

    // Set up the editor for the Color cells.
    /**
     * Sets the up color editor.
     * 
     * @param table the new up color editor
     */
    private void setUpColorEditor(JTable table) {
        // First, set up the button that brings up the dialog.
        final JButton button = new JRowButton("") {
            /**
             * 
             */
            private static final long serialVersionUID = 5842237790364872497L;

            public void setText(String s) {
                // Button never shows text -- only color.
            }
        };
        // button.setBackground(Color.white);
        button.setBorderPainted(false);
        button.setMargin(new Insets(0, 0, 0, 0));

        // Now create an editor to encapsulate the button, and
        // set it up as the editor for all Color cells.
        final ColorEditor colorEditor = new ColorEditor(button);
        table.setDefaultEditor(Color.class, colorEditor);

        // Set up the dialog that the button brings up.
        final JColorChooser colorChooser = new JColorChooser();
        // XXX: PENDING: add the following when setPreviewPanel
        // XXX: starts working.
        // JComponent preview = new ColorRenderer(false);
        // preview.setPreferredSize(new Dimension(50, 10));
        // colorChooser.setPreviewPanel(preview);
        ActionListener okListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                colorEditor.currentColor = colorChooser.getColor();
            }
        };
        final JDialog dialog =
                JColorChooser.createDialog(button, "Pick a Color", false, colorChooser, okListener, null); // XXXDoublecheck
        // this
        // is
        // OK

        colorChooser.getSelectionModel().addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                chartView.getDataSelection().getSelectedSeriesView(((JRowButton) button).row).setColor(
                        ((ColorSelectionModel) e.getSource()).getSelectedColor());
                chartView.updateScapeGraphics();
                // button.setBackground(((ColorSelectionModel)
                // e.getStatCollector()).getSelectedColor());
                selectedTable.tableChanged(new TableModelEvent(selectedTable.getModel()));
            }
        });

        // Here's the code that brings up the dialog.
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                button.setBackground(colorEditor.currentColor);
                colorChooser.setColor(colorEditor.currentColor);
                // Without the following line, the dialog comes up
                // in the middle of the screen.
                // dialog.setLocationRelativeTo(button);
                dialog.setVisible(true);
            }
        });
    }

    /**
     * The data model for displaying all series that may be added to the chart.
     */
    class AvailableDataModel extends AbstractTableModel implements Serializable {

        /**
         * 
         */
        private static final long serialVersionUID = 4759032635778249844L;

        /*
         * (non-Javadoc)
         * @see javax.swing.table.TableModel#getRowCount()
         */
        public int getRowCount() {
            return chartView.getDataSelection().getData().getStatCollectors().length;
        }

        /*
         * (non-Javadoc)
         * @see javax.swing.table.TableModel#getColumnCount()
         */
        public int getColumnCount() {
            return StatCollector.getAllMeasureNamesShort().length + 1;
        }

        /*
         * (non-Javadoc)
         * @see
         * javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object,
         * int, int)
         */
        public synchronized void setValueAt(Object object, int row, int col) {
            DataSelection group = chartView.getDataSelection();
            DataSeries series = (group.getData().getStatCollectors()[row]).getAllDataSeries()[col - 1];
            if (series != null) {
                group.setSelected(series, ((Boolean) object).booleanValue());
                chartView.updateScapeGraphics();
                // NewChart chartView.updateChart();
                selectedTable.tableChanged(new TableModelEvent(this));
            }
        }

        /*
         * (non-Javadoc)
         * @see javax.swing.table.TableModel#getValueAt(int, int)
         */
        public Object getValueAt(int row, int col) {
            DataSelection group = chartView.getDataSelection();
            if (col == 0) {
                return (group.getData().getStatCollectors()[row]).getName();
            } else {
                DataSeries dataSeries = (group.getData().getStatCollectors()[row]).getAllDataSeries()[col - 1];
                if (dataSeries != null) {
                    // return new Boolean(true);
                    return new Boolean(chartView.getDataSelection().isSelected(dataSeries));
                } else {
                    return null;
                }
                // return viewedSeries.isSelected();
            }
        }

        /*
         * (non-Javadoc)
         * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
         */
        public boolean isCellEditable(int row, int col) {
            return col > 0
                    && ((chartView.getDataSelection().getData().getStatCollectors()[row]).getAllDataSeries()[col - 1]) != null;
        }

        /*
         * (non-Javadoc)
         * @see javax.swing.table.AbstractTableModel#getColumnName(int)
         */
        public String getColumnName(int col) {
            if (col > 0) {
                return StatCollector.getAllMeasureNamesShort()[col - 1];
            } else {
                return "";
            }
        }

        /*
         * (non-Javadoc)
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
     * The data model for editing and viewing series that are being included in
     * the chart.
     */
    class SelectedDataModel extends AbstractTableModel implements Serializable {

        /**
         * 
         */
        private static final long serialVersionUID = -4531534031089806395L;

        /*
         * (non-Javadoc)
         * @see javax.swing.table.TableModel#getRowCount()
         */
        public int getRowCount() {
            return chartView.getDataSelection().getSelectionSize();
        }

        /*
         * (non-Javadoc)
         * @see javax.swing.table.TableModel#getColumnCount()
         */
        public int getColumnCount() {
            return 2;
        }

        /*
         * (non-Javadoc)
         * @see
         * javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object,
         * int, int)
         */
        public void setValueAt(Object value, int row, int col) {
            if (col == 1) {
                (chartView.getDataSelection()).getSelectedSeriesView(row).setColor((Color) value);
            }
        }

        /*
         * (non-Javadoc)
         * @see javax.swing.table.TableModel#getValueAt(int, int)
         */
        public Object getValueAt(int row, int col) {
            if (col == 0) {
                return chartView.getDataSelection().getSelectedSeries(row).getName();
            } else if (col == 1) {
                return (chartView.getDataSelection()).getSelectedSeriesView(row).getColor();
            } else {
                throw new RuntimeException("Internal Error");
            }
        }

        /*
         * (non-Javadoc)
         * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
         */
        public boolean isCellEditable(int row, int col) {
            if (col == 1) {
                return true;
            } else {
                return false;
            }
        }

        /*
         * (non-Javadoc)
         * @see javax.swing.table.AbstractTableModel#getColumnName(int)
         */
        public String getColumnName(int col) {
            if (col == 0) {
                return "Name";
            } else if (col == 1) {
                return "Color";
            } else {
                throw new RuntimeException("Internal Error");
            }
        }

        /*
         * (non-Javadoc)
         * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
         */
        public Class getColumnClass(int col) {
            if (col > 0) {
                return (Color.class);
            } else {
                try {
                    return Class.forName("java.lang.Object");
                } catch (ClassNotFoundException e) {
                    System.out.println("Internal error: " + e);
                }
            }
            return null;
        }
    }
}

class JRowButton extends JButton implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -4806113943445633794L;

    public JRowButton(String s) {
        super(s);
    }

    // nasty hack!!
    public int row;
}

class NullCheckBoxRenderer extends DefaultTableCellRenderer implements Serializable {

    // public NullCheckBoxRenderer() {
    // super(); //Unfortunately, the constructor
    // component = new JCheckBox();
    // }

    /**
     * 
     */
    private static final long serialVersionUID = -1533657479611690117L;

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
        if (value instanceof Boolean) {
            // return super.getTableCellRendererComponent(table, value,
            // isSelected, hasFocus, row, column);
            JCheckBox cb = new JCheckBox();
            cb.setSelected(((Boolean) value).booleanValue());
            cb.setBackground(this.getBackground());
            cb.setHorizontalAlignment(AbstractButton.CENTER);
            return cb;
        } else if (value instanceof String) {
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            /*
             * JTextField label = new JTextField((String) value);
             * label.setOpaque(true); label.setBackground(this.getBackground());
             * label.setForeground(Color.black);
             * //label.setBorder(BorderFactory.createEtchedBorder()); return
             * label;/ //label.setBorder(BorderFactory.createEtchedBorder());
             * return label;
             */
        } else {
            return new JLabel();
        }
    }
}

/*
 * Copied w/ changes (for live upating) from Swing examples. The editor button
 * that brings up the dialog. We extend DefaultCellEditor for convenience, even
 * though it mean we have to create a dummy check box. Another approach would be
 * to copy the implementation of TableCellEditor methods from the source code
 * for DefaultCellEditor.
 */

class ColorEditor extends DefaultCellEditor implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -462567358233937941L;
    Color currentColor = null;

    public ColorEditor(JButton b) {
        super(new JCheckBox()); // Unfortunately, the constructor
        // expects a check box, combo box,
        // or text field.
        editorComponent = b;
        setClickCountToStart(1); // This is usually 1 or 2.

        // Must do this so that editing stops when appropriate.
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped();
            }
        });
    }

    protected void fireEditingStopped() {
        super.fireEditingStopped();
    }

    public Object getCellEditorValue() {
        return currentColor;
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        ((JButton) editorComponent).setText(value.toString());
        ((JRowButton) editorComponent).row = row;
        currentColor = (Color) value;
        return editorComponent;
    }
}
