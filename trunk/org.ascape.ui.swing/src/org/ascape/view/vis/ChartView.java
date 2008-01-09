/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.view.vis;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Paint;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.TooManyListenersException;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;

import org.ascape.model.event.ScapeEvent;
import org.ascape.runtime.swing.DesktopEnvironment;
import org.ascape.util.data.DataSeries;
import org.ascape.util.vis.DataViewSelection;
import org.ascape.view.custom.BaseCustomizer;
import org.ascape.view.custom.ChartCustomizer;

import com.jrefinery.chart.Axis;
import com.jrefinery.chart.HorizontalCategoryAxis;
import com.jrefinery.chart.HorizontalNumberAxis;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.JFreeChartPanel;
import com.jrefinery.chart.Legend;
import com.jrefinery.chart.NumberTickUnit;
import com.jrefinery.chart.PiePlot;
import com.jrefinery.chart.Plot;
import com.jrefinery.chart.StandardLegend;
import com.jrefinery.chart.StandardXYItemRenderer;
import com.jrefinery.chart.VerticalBarPlot;
import com.jrefinery.chart.VerticalNumberAxis;
import com.jrefinery.chart.XYPlot;
import com.jrefinery.chart.event.PlotChangeEvent;
import com.jrefinery.data.AbstractSeriesDataset;
import com.jrefinery.data.CategoryDataset;
import com.jrefinery.data.PieDataset;
import com.jrefinery.data.XYDataset;

/**
 * A chart view of a scape. Delegates many functions, including scape listener,
 * to subclasses of ChartViewModel. This view uses KL Group's JChart; if you
 * want to use other charting packages, let us know, we'd like to generalize
 * further so that multiple charting packages can be used. Uses JFreeChart.
 * 
 * @author Miles Parker
 * @version 2.9
 * @history 2.9 1/29/02 Replaced JCCHart implementation with JFreeChart
 * @history 1.9.2 2/2/01 fixed to support quicker chart updating
 * @history 1.2.6 10/25/99 added support for named views
 * @history 1.0.2 3/6/1999 made aware of view frame
 * @history 1.0.1 added support for removing scapes
 * @since 1.0
 */
public class ChartView extends PanelView implements Observer, Externalizable {

    //For use in case where no series are specified
    /**
     * The default paint.
     */
    private Paint[] defaultPaint = {Color.white};

    /**
     * The Class ChartViewDataset.
     */
    abstract class ChartViewDataset extends AbstractSeriesDataset {

        /**
         * Returns the name of a series.
         * 
         * @param series
         *            The series (zero-based index).
         * @return the series name
         */
        public String getSeriesName(int series) {
            return dataSelection.getSelectedName(series);
        }

        /**
         * Returns the number of series in the dataset.
         * 
         * @return The number of series in the dataset.
         */
        public int getSeriesCount() {
            return dataSelection.getSelectionSize();
        }

        /**
         * Gets the series paint.
         * 
         * @param series
         *            the series
         * @return the series paint
         */
        public Paint getSeriesPaint(int series) {
            return dataSelection.getSeriesView(series).getColor();
        }

        /**
         * On update.
         */
        public void onUpdate() {
            Paint[] seriesPaints = new Paint[dataSelection.getSelectionSize()];
            for (int i = 0; i < seriesPaints.length; i++) {
                seriesPaints[i] = dataSelection.getSelectedSeriesView(i).getColor();
            }
            //Work around for current JFreeChart bug
            if (seriesPaints.length > 0) {
                plot.setSeriesPaint(seriesPaints);
            } else {
                plot.setSeriesPaint(defaultPaint);
            }
        }
    }

    /**
     * The Class TimeSeriesDataset.
     */
    class TimeSeriesDataset extends ChartViewDataset implements XYDataset {

        /**
         * The horizontal axis.
         */
        protected HorizontalNumberAxis horizontalAxis;

        /**
         * The vertical axis.
         */
        protected VerticalNumberAxis verticalAxis;

        /**
         * Instantiates a new time series dataset.
         */
        public TimeSeriesDataset() {
            horizontalAxis = new HorizontalNumberAxis("Period");
            verticalAxis = new VerticalNumberAxis("Value");
            plot = new XYPlot(horizontalAxis, verticalAxis);
            ((XYPlot) plot).setXYItemRenderer(new StandardXYItemRenderer(StandardXYItemRenderer.LINES));
// todo: change and fix all comments marked xxx
//            plot = new XYPlot(this, horizontalAxis, verticalAxis, new StandardXYItemRenderer(StandardXYItemRenderer.LINES));
        }

        /**
         * Returns the x-value for an item within a series.
         * 
         * @param series
         *            The series (zero-based index).
         * @param item
         *            The item (zero-based index).
         * @return The x-value for an item within a series.
         */
        public Number getXValue(int series, int item) {
            Number xValue;
            if (getScape() != null && getScape().getRunner().getData().getPeriods().size() > 0) {
                try {
                    xValue = (Number) getScape().getRunner().getData().getPeriods().get(item);
                } catch (ArrayIndexOutOfBoundsException e) {
                    xValue = new Double(0.0);
                }
            } else {
                xValue = new Double(0.0);
            }
            return xValue;
        }

        /**
         * Returns the y-value for an item within a series.
         * 
         * @param series
         *            The series (zero-based index).
         * @param item
         *            The item (zero-based index).
         * @return The y-value for an item within a series.
         */
        public Number getYValue(int series, int item) {
            Number yValue;
            try {
                yValue = (Number) dataSelection.getSelectedSeriesData(series).get(item);
            } catch (ArrayIndexOutOfBoundsException e) {
                yValue = new Double(0.0);
            }
            return yValue;
        }

        /**
         * Returns the number of items in a series.
         * 
         * @param series
         *            The series (zero-based index).
         * @return The number of items within a series.
         */
        public int getItemCount(int series) {
            Vector seriesData = dataSelection.getSelectedSeriesData(series);
            if (seriesData != null) {
                return seriesData.size();
            } else {
                return 0;
            }
        }

        /* (non-Javadoc)
         * @see org.ascape.view.vis.ChartView.ChartViewDataset#onUpdate()
         */
        public void onUpdate() {
            super.onUpdate();
            if (displayPoints == ALL_POINTS) {
                horizontalAxis.setMinimumAxisValue(getScape().getStartPeriod());
            } else {
                horizontalAxis.setMinimumAxisValue(Math.max(getScape().getPeriod() - displayPoints, getScape().getStartPeriod()));
            }
            horizontalAxis.setMaximumAxisValue(Math.max(getScape().getPeriod(), 1.0));

            double minValue = dataSelection.getMin();
            double maxValue = dataSelection.getMax();
            final double margin = (maxValue - minValue) / 20.0;
            minValue -= margin;
            maxValue += margin;

//workaround for jchart problem..
            if ((minValue == 0.0) && (maxValue > 0.0)) {
//Set min value to a smidgen (technical term) below zero line to make it display properly.
                minValue = -maxValue / getHeight();
            }
//We need to have some kind of range to avoid JFreeChart display errors
            if ((minValue == 0.0) && (maxValue == 0.0)) {
                minValue = -1.0;
                maxValue = 1.0;
            }
            if (forcePosVerticalAxisMinToZero && (minValue > 0.0)) {
//Force min value to floor, better representation for many statistics (ex: population)
                minValue = 0.0;
            }
            
            if (!Double.isNaN(verticalAxisMin)) {
                minValue = verticalAxisMin;
            }
            
            if (!Double.isNaN(verticalAxisMax)) {
                maxValue = verticalAxisMax;
            }
            
            verticalAxis.setMinimumAxisValue(minValue);
            verticalAxis.setMaximumAxisValue(maxValue);

            if (Math.abs(minValue) >= 1.0e10 || Math.abs(maxValue) >= 1.0e10 || (Math.abs(minValue) < 1.0e-6 && Math.abs(maxValue) < 1.0e-6))
            {
// very large or small magnitudes, so use scientific notation on the vertical axis
// and work around JFreeChart's limited range of tick unit sizes
                double tickSize = chooseTickSize(minValue, maxValue);
                verticalAxis.setTickUnit(new NumberTickUnit(new Double(tickSize), new DecimalFormat("0.##########E0")));
// xxx
//                verticalAxis.setTickUnit(new NumberTickUnit(tickSize, new DecimalFormat("0.############E0")));
            } else {
                verticalAxis.setAutoTickUnitSelection(true);
            }
        }
    }

    /**
     * Choose tick size.
     * 
     * @param min
     *            the min
     * @param max
     *            the max
     * @return the double
     */
    private static double chooseTickSize(double min, double max) {
        if (min == max) {
            return 1.0;
        }

        // make sure that min < max
        if (min > max) {
            double temp = min;
            min = max;
            max = temp;
        }

        double range = (max - min) / 4.0;
        double log10 = Math.log(range) / Math.log(10.0);
        double orderOfMagnitude = Math.floor(log10);
        double fractionalPart = log10 - orderOfMagnitude;
        double multiplier;

        if (fractionalPart < 0.251029995663981) {
            multiplier = 1.;
        } else if (fractionalPart < 0.34794000867203745) {
            multiplier = 2.;
        } else if (fractionalPart < 0.6489700043360187) {
            multiplier = 2.5;
        } else if (fractionalPart < 0.95) {
            multiplier = 5.;
        } else {
            multiplier = 10.;
        }

        return Math.pow(10.0, orderOfMagnitude) * multiplier;
    }

    /**
     * The Class HistogramDataset.
     */
    class HistogramDataset extends ChartViewDataset implements CategoryDataset {

        /**
         * The horizontal axis.
         */
        protected HorizontalCategoryAxis horizontalAxis;

        /**
         * The vertical axis.
         */
        protected VerticalNumberAxis verticalAxis;

        /**
         * Instantiates a new histogram dataset.
         */
        public HistogramDataset() {
            horizontalAxis = new HorizontalCategoryAxis("Series");
            verticalAxis = new VerticalNumberAxis("Value");
            plot = new VerticalBarPlot(horizontalAxis, verticalAxis);
// xxx
//            plot = new XYPlot(this, horizontalAxis, verticalAxis);
        }

        /**
         * The cat string.
         */
        private final String[] catString = {""};

        /**
         * The categories.
         */
        private final List categories = Arrays.asList(catString);

        /* (non-Javadoc)
         * @see com.jrefinery.data.CategoryDataset#getCategories()
         */
        public List getCategories() {
            return categories;
        }

        /* (non-Javadoc)
         * @see com.jrefinery.data.CategoryDataset#getCategoryCount()
         */
        public int getCategoryCount() {
            return 1;
        }

        /* (non-Javadoc)
         * @see com.jrefinery.data.CategoryDataset#getValue(int, java.lang.Object)
         */
        public Number getValue(int series, Object category) {
            return new Double(dataSelection.getSelectedSeries(series).getValue());
        }

        /* (non-Javadoc)
         * @see org.ascape.view.vis.ChartView.ChartViewDataset#onUpdate()
         */
        public void onUpdate() {
            super.onUpdate();
            verticalAxis.setMinimumAxisValue(Math.min(dataSelection.getMin() * 1.1, 0.0));
            verticalAxis.setMaximumAxisValue(dataSelection.getMax() * 1.1);
        }
    }

    /*
     * There appears to be something not quite working as expected w/ the JFreeChart Pie Chart implementation --
     * that's why I built such a funky dataset with both a set (what the pie plot wants) and list (so we can index into it).
     * We already know that the list has unique elements. The first attempt had JFreechart dropping elements from the wrong end;
     * i.e., if, in a 3 series chart, if series 3 had a value of zero, series 1 would be dropped from the list and the mapping would go haywire.
     * So I just hacked together a soultion that seems to work with the current implementation of JFreeChart Pie Chart. Not going to put a lot
     * of effort into cleaning it up since a) it works, and b) JFreeChart will probably evolve Pie Chart implementation. MTP
     */
    /**
     * The Class PieChartDataset.
     */
    public class PieChartDataset extends ChartViewDataset implements PieDataset {

        /**
         * The set.
         */
        Set set;
        
        /**
         * The list.
         */
        List list;

        /**
         * The default paint.
         */
        private final Paint[] defaultPaint = {Color.red};

        /**
         * Instantiates a new pie chart dataset.
         */
        public PieChartDataset() {
            plot = new PiePlot();
// xxx
//            plot = new PiePlot(this);
        }

        /* (non-Javadoc)
         * @see com.jrefinery.data.PieDataset#getCategories()
         */
        public Set getCategories() {
            return set;
        }
// xxx
//        public List getCategories() {
//            return list;
//        }

        /**
 * Gets the category count.
 * 
 * @return the category count
 */
public int getCategoryCount() {
            return set.size();
        }

        /* (non-Javadoc)
         * @see com.jrefinery.data.PieDataset#getValue(java.lang.Object)
         */
        public Number getValue(Object category) {
            return new Double(((DataSeries) category).getValue());
        }

        /* (non-Javadoc)
         * @see org.ascape.view.vis.ChartView.ChartViewDataset#onUpdate()
         */
        public void onUpdate() {
            set = new TreeSet(dataSelection.getSelectedSeries());
            Iterator setIter = set.iterator();
            while (setIter.hasNext()) {
                DataSeries series = (DataSeries) setIter.next();
                if (series.getValue() <= 0.0) {
                    setIter.remove();
                }
            }

            list = new ArrayList(set);
            Paint[] seriesPaints = new Paint[list.size()];
            for (int i = 0; i < seriesPaints.length; i++) {
                //Nasty, but again works, and not worth figuring something more permenant out, esp. as we will prob. be refactoring dataselection stuff down the road.
                seriesPaints[i] = dataSelection.getSelectedSeriesView(dataSelection.getSelectedIndexOf(((DataSeries) list.get(i)).getName())).getColor();
            }
            //Work around for current JFreeChart bug
            if (seriesPaints.length > 0) {
                plot.setSeriesPaint(seriesPaints);
            } else {
                plot.setSeriesPaint(defaultPaint);
            }
        }

        /**
         * Returns the name of a series.
         * 
         * @param series
         *            The series (zero-based index).
         * @return the series name
         */
        public String getSeriesName(int series) {
            return ((DataSeries) list.get(series)).getName();
        }

        /**
         * Returns the number of series in the dataset.
         * 
         * @return The number of series in the dataset.
         */
        public int getSeriesCount() {
            return set.size();
        }

        /* (non-Javadoc)
         * @see org.ascape.view.vis.ChartView.ChartViewDataset#getSeriesPaint(int)
         */
        public Paint getSeriesPaint(int series) {
            return Color.yellow;//dataSelection.getSeriesView(series).getColor();
        }
    }

    /**
     * Time series (line graph) chart type. Same as JCChart.PLOT. <i>Note:</i>
     * Chart types have the same ints as cooresponding JCChart chart types. Only
     * the names have been changed to match general usage. Note that this is
     * just for convenience and clarity; these symbols may also be translated
     * for other charting packages. So this usage should not be relied on;
     * whenever possible use these ChartView constants, and not their JCChart
     * counterparts.
     */
    public final static int TIME_SERIES = 1;

    /**
     * Histogram (bar graph) chart type.
     */
    public final static int HISTOGRAM = 2;

    /**
     * Pie chart type.
     */
    public final static int PIE = 3;

    /**
     * The panel.
     */
    private JFreeChartPanel panel;
// xxx
//    ChartPanel panel;

    /**
 * The chart.
 */
private JFreeChart chart;

    /**
     * The plot.
     */
    private Plot plot;

    /**
     * The plot event.
     */
    private PlotChangeEvent plotEvent;

    /**
     * The legend showing.
     */
    private boolean legendShowing = true;

    /**
     * The legend.
     */
    private Legend legend;

    /**
     * The chart type this view is using. (Time series is default.)
     */
    private int chartType;

    /**
     * The dataset.
     */
    private ChartViewDataset dataset;

    /**
     * Symbol for 'display all data points.'
     */
    public static final int ALL_POINTS = -1;

    /**
     * Number of display points to display. (Default all.)
     */
    private int displayPoints = ALL_POINTS;

    /**
     * The selected series for this data model.
     */
    private DataViewSelection dataSelection;

    /**
     * The panel responsible for customizing this chart.
     */
    private BaseCustomizer customizer;

    /**
     * The force pos vertical axis min to zero.
     */
    private boolean forcePosVerticalAxisMinToZero = true;
    
    /**
     * The vertical axis min.
     */
    private double verticalAxisMin = Double.NaN;
    
    /**
     * The vertical axis max.
     */
    private double verticalAxisMax = Double.NaN;
    
    /**
     * The frame, if any, that this view is displayed within.
     */
    // MEI - we don't want this here--it shadows PanelView's frame field
    //protected ViewFrameBridge frame;

    /**
     * Construct a chart view of default time series type.
     */
    public ChartView() {
        this(TIME_SERIES);
    }

    /**
     * Construct a chart view with a specicifed chart type.
     * 
     * @param chartType
     *            the charttype symbol
     */
    public ChartView(int chartType) {
        setForeground(Color.black);
        setBackground(Color.lightGray);
        setPlotBackground(Color.white);
//        getChartArea().setBackground(new Color(230, 220, 215));
//        getChartArea().setForeground(Color.black);
//        getChartArea().getPlotArea().setBackground(new Color(255, 245, 220));
        setPreferredSize(new Dimension(500, 380));
        setChartType(chartType);
        name = "Chart View";
    }

    /**
     * Construct a chart view of default time series type.
     * 
     * @param name
     *            a name for the chart series
     */
    public ChartView(String name) {
        this();
        this.name = name;
    }

    /**
     * Construct a chart view of default time series type.
     * 
     * @param chartType
     *            the charttype symbol
     * @param name
     *            a name for the chart series
     */
    public ChartView(int chartType, String name) {
        this(chartType);
        this.name = name;
    }

    /**
     * Displays a window for altering the setting for this chart.
     */
    public void displayCustomizer() {
        if (customizer == null) {
            customizer = new ChartCustomizer(this);
            getScape().addView(customizer);
        } else {
            if (customizer.getViewFrame().getFrameImp() instanceof JInternalFrame) {
                ((JInternalFrame) customizer.getViewFrame().getFrameImp()).toFront();
            } else if (customizer.getViewFrame().getFrameImp() instanceof JFrame) {
                ((JFrame) customizer.getViewFrame().getFrameImp()).toFront();
            }
        }
    }

    /**
     * Removes the customizer for this chart.
     */
    public void removeCustomizer() {
        if (customizer != null) {
            //
            if (customizer.getViewFrame() != null) {
                customizer.getViewFrame().dispose();
            }
            customizer = null;
        }
    }

    /**
     * Sets the name of this view.
     * 
     * @param name
     *            a user relevant name for this view
     */
    public final void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the customizer for this view.
     * 
     * @return the customizer
     */
    public BaseCustomizer getCustomizer() {
        return customizer;
    }

    /**
     * Sets the customizer for this view. Could be used to provde custom chart
     * editors.
     * 
     * @param customizer
     *            the customizer to use
     */
    public void setCustomizer(ChartCustomizer customizer) {
        this.customizer = customizer;
    }

    /**
     * Gets the chartType.
     * 
     * @return Returns a int
     */
    public int getChartType() {
        return chartType;
    }

    /**
     * On scape and chart type set.
     */
    private void onScapeAndChartTypeSet() {
        if (dataSelection != null) {
            switch (chartType) {
                case TIME_SERIES:
                    dataset = new TimeSeriesDataset();
                    break;
                case HISTOGRAM:
                    dataset = new HistogramDataset();
                    break;
                case PIE:
                    dataset = new PieChartDataset();
                    break;
            }
            plotEvent = new PlotChangeEvent(plot);
            chart = new JFreeChart(dataset, plot);
            dataset.onUpdate();
            chart.setAntiAlias(true);
            //To make sure we set all of the colors properly
            setLegendShowing(legendShowing);
            refreshColors();
            panel = new JFreeChartPanel(chart);
            panel.addMouseListener(new MouseListener() {
                public void mouseClicked(MouseEvent e) {
                    //Synchronize so that the model can't continue while we're opening the window
                    synchronized (ChartView.this) {
                        if (e.getClickCount() == 2) {
                            displayCustomizer();
                        }
                    }
                }

                public void mouseEntered(MouseEvent e) {
                    //getHeader().setIsShowing(true);
                }

                public void mouseExited(MouseEvent e) {
                }

                public void mousePressed(MouseEvent e) {
                }

                public void mouseReleased(MouseEvent e) {
                }
            });
            setLayout(new BorderLayout());
            removeAll();
            add(panel);
            validate();
            iconUpdated();
        }
    }

    /**
     * Sets the chartType.
     * 
     * @param chartType
     *            The chartType to set
     */
    public void setChartType(int chartType) {
        this.chartType = chartType;
        if (getScape() != null) {
            onScapeAndChartTypeSet();
        }
    }

    /**
     * Checks if is legend showing.
     * 
     * @return true, if is legend showing
     */
    public boolean isLegendShowing() {
        return legendShowing;
    }

    /**
     * Sets the legend showing.
     * 
     * @param legendShowing
     *            the new legend showing
     */
    public void setLegendShowing(boolean legendShowing) {
        this.legendShowing = legendShowing;
        if ((legendShowing) && !(dataset instanceof PieChartDataset)) {
            if (chart != null) {
                if (legend == null) {
                    legend = new StandardLegend(chart);
                    legend.setAnchor(Legend.EAST);
                }
                chart.setLegend(legend);
                //To ensure legend colors are updated properly
                refreshColors();
            }
        } else {
            if (chart != null) {
                chart.setLegend(null);
            }
        }
    }

    /**
     * Adds a series to the chart being viewed. Convenience method (could also
     * get view model and set select group.)
     * 
     * @param valueName
     *            the name of the series to add
     */
    public void addSeries(String valueName) {
        getDataSelection().setSelected(valueName, true);
    }

    /**
     * Adds a series to the chart being viewed. Convenience method.
     * 
     * @param valueName
     *            the name of the series to add
     * @param color
     *            the color to use for the series in the chart
     */
    public void addSeries(String valueName, Color color) {
        this.addSeries(valueName);
        getDataSelection().getSeriesView(valueName).setColor(color);
    }

    /**
     * Adds a series to the chart being viewed. Convenience method.
     * 
     * @param valueName
     *            the name of the series to add
     * @param color
     *            the color to use for the series in the chart
     * @param continuous
     *            boolean should the series be drawn as a line or series of
     *            symbols?
     */
    public void addSeries(String valueName, Color color, boolean continuous) {
        this.addSeries(valueName, color);
        getDataSelection().getSeriesView(valueName).setContinuous(continuous);
    }

    /**
     * Removes a series from the chart being viewed. Convenience method.
     * 
     * @param valueName
     *            the name of the series to remove
     */
    public void removeSeries(String valueName) {
        getDataSelection().setSelected(valueName, false);
        updateScapeGraphics();
    }

    /**
     * Clears all series selections from the chart being viewed. Convenience
     * method.
     */
    public void clearSeries() {
        getDataSelection().clearSelection();
        updateScapeGraphics();
    }

    /**
     * Notifies the view that the scape has added it. Note that it doesn't
     * really matter what scape you add the listener to, as the chart view model
     * will have access to every statistic available for the entire model, but
     * all views still need to be controlled by one particular scape. One and
     * only one scape may be added.
     * 
     * @param scapeEvent
     *            the scape added notification event
     * @throws java.util.TooManyListenersException
     *             on attempt to add this listener to another scape when one has
     *             already been assigned
     * @throws TooManyListenersException
     *             the too many listeners exception
     */
    public synchronized void scapeAdded(ScapeEvent scapeEvent) throws TooManyListenersException {
        super.scapeAdded(scapeEvent);
        dataSelection = new DataViewSelection();
        dataSelection.addObserver(this);
        dataSelection.setData(getScape().getRunner().getData());
        onScapeAndChartTypeSet();
    }

    /**
     * Notifies the listener that the scape has removed it.
     * 
     * @param scapeEvent
     *            the scape removed notification event
     */
    public synchronized void scapeRemoved(ScapeEvent scapeEvent) {
        dataSelection.deleteObserver(this);
        dataSelection.setData(null);
        dataSelection = null;
        removeCustomizer();
        scape = null;
        super.scapeRemoved(scapeEvent);
    }

    /**
     * Notifies this view model that a change (in the selected group, typically)
     * has occured.
     * 
     * @param observed
     *            the observed
     * @param arg
     *            the arg
     */
    public void update(Observable observed, Object arg) {
        if (observed == dataSelection) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    if (getScape().getRunner().getData() != null) {
                        updateScapeGraphics();
                    }
                }
            });
        }
    }

    /**
     * Gets the dataSelection.
     * 
     * @return Returns a DataViewSelection
     */
    public DataViewSelection getDataSelection() {
        return dataSelection;
    }

    /**
     * Sets the dataSelection.
     * 
     * @param dataSelection
     *            The dataSelection to set
     */
    public void setDataSelection(DataViewSelection dataSelection) {
        this.dataSelection = dataSelection;
    }

    /**
     * Gets the displayPoints.
     * 
     * @return Returns a int
     */
    public int getDisplayPoints() {
        return displayPoints;
    }

    /**
     * Sets the displayPoints.
     * 
     * @param displayPoints
     *            The displayPoints to set
     */
    public void setDisplayPoints(int displayPoints) {
        this.displayPoints = displayPoints;
    }

    /**
     * Gets the panel.
     * 
     * @return the panel
     */
    public JFreeChartPanel getPanel() {
        return panel;
    }
// xxx
//    public ChartPanel getPanel() {
//        return panel;
//    }

    /**
 * Gets the chart.
 * 
 * @return the chart
 */
public JFreeChart getChart() {
        return chart;
    }

    /**
     * Refresh colors.
     */
    private void refreshColors() {
        setBackground(getBackground());
        setForeground(getForeground());
        setPlotBackground(getPlotBackground());
    }

    /**
     * Gets the plot background.
     * 
     * @return the plot background
     */
    public Color getPlotBackground() {
        if (plot.getBackgroundPaint() instanceof Color) {
            return (Color) plot.getBackgroundPaint();
        } else {
            return getBackground();
        }
    }

    /**
     * Sets the plot background.
     * 
     * @param bg
     *            the new plot background
     */
    public void setPlotBackground(Color bg) {
        if (getChart() != null) {
            plot.setBackgroundPaint(bg);
            if (legend instanceof StandardLegend) {
                ((StandardLegend) legend).setBackgroundPaint(bg);
            }
        }
    }

    /* (non-Javadoc)
     * @see javax.swing.JComponent#setBackground(java.awt.Color)
     */
    public void setBackground(Color fg) {
        super.setBackground(fg);
        if (getChart() != null) {
            getChart().setBackgroundPaint(fg);
        }
    }

    /* (non-Javadoc)
     * @see javax.swing.JComponent#setForeground(java.awt.Color)
     */
    public void setForeground(Color fg) {
        super.setForeground(fg);
        if (getChart() != null) {
            getChart().getPlot().setOutlinePaint(fg);

            Axis horizontalAxis = (Axis) plot.getHorizontalAxis();
            if (horizontalAxis != null) {
                horizontalAxis.setLabelPaint(fg);
                horizontalAxis.setTickLabelPaint(fg);
            }
            Axis verticalAxis = (Axis) getChart().getPlot().getVerticalAxis();
            if (verticalAxis != null) {
                verticalAxis.setLabelPaint(fg);
                verticalAxis.setTickLabelPaint(fg);
            }

            if (legend instanceof StandardLegend) {
                ((StandardLegend) legend).setOutlinePaint(getForeground());
                ((StandardLegend) legend).setItemPaint(getForeground());
            }
        }
    }

    /**
     * Checks if is force pos vertical axis min to zero.
     * 
     * @return true, if is force pos vertical axis min to zero
     */
    public boolean isForcePosVerticalAxisMinToZero() {
        return forcePosVerticalAxisMinToZero;
    }

    /**
     * Sets the force pos vertical axis min to zero.
     * 
     * @param forcePosVerticalAxisMinToZero
     *            the new force pos vertical axis min to zero
     */
    public void setForcePosVerticalAxisMinToZero(boolean forcePosVerticalAxisMinToZero) {
        this.forcePosVerticalAxisMinToZero = forcePosVerticalAxisMinToZero;
    }

    /**
     * Gets the vertical axis min.
     * 
     * @return the vertical axis min
     */
    public double getVerticalAxisMin() {
        return verticalAxisMin;
    }

    /**
     * Sets the vertical axis min.
     * 
     * @param verticalAxisMin
     *            the new vertical axis min
     */
    public void setVerticalAxisMin(double verticalAxisMin) {
        this.verticalAxisMin = verticalAxisMin;
    }

    /**
     * Gets the vertical axis max.
     * 
     * @return the vertical axis max
     */
    public double getVerticalAxisMax() {
        return verticalAxisMax;
    }

    /**
     * Sets the vertical axis max.
     * 
     * @param verticalAxisMax
     *            the new vertical axis max
     */
    public void setVerticalAxisMax(double verticalAxisMax) {
        this.verticalAxisMax = verticalAxisMax;
    }

    /* (non-Javadoc)
     * @see org.ascape.view.vis.PanelView#updateScapeGraphics()
     */
    public void updateScapeGraphics() {
        super.updateScapeGraphics();
        dataset.onUpdate();
        delegate.viewPainted();
        chart.plotChanged(plotEvent);
    }

    /* (non-Javadoc)
     * @see javax.swing.JComponent#paintChildren(java.awt.Graphics)
     */
    public void paintChildren(Graphics g) {
        if (getScape().getRunner().getData() != null) {
            super.paintChildren(g);
        }
    }

    /**
     * Method called once a model is deserialized.
     * 
     * @param scapeEvent
     *            the scape event
     */
    public void scapeDeserialized(ScapeEvent scapeEvent) {
        super.scapeDeserialized(scapeEvent);
        onScapeAndChartTypeSet();
    }

    /* (non-Javadoc)
     * @see org.ascape.view.vis.PanelView#writeExternal(java.io.ObjectOutput)
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeInt(chartType);
        out.writeObject(dataSelection);
        out.writeInt(displayPoints);
        out.writeBoolean(legendShowing);
        out.writeBoolean(forcePosVerticalAxisMinToZero);
        out.writeDouble(verticalAxisMin);
        out.writeDouble(verticalAxisMax);
    }

    /* (non-Javadoc)
     * @see org.ascape.view.vis.PanelView#readExternal(java.io.ObjectInput)
     */
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        chartType = in.readInt();
        dataSelection = (DataViewSelection) in.readObject();
        displayPoints = in.readInt();
        legendShowing = in.readBoolean();
        forcePosVerticalAxisMinToZero = in.readBoolean();
        verticalAxisMin = in.readDouble();
        verticalAxisMax = in.readDouble();
    }

    /**
     * Return an icon that can be used to represent this frame. Returns null in
     * this case, use default. Implementors should specify an icon that makes
     * sense for the view.
     * 
     * @return the icon
     */
    public ImageIcon getIcon() {
        switch (chartType) {
            case TIME_SERIES:
                return DesktopEnvironment.getIcon("LineGraph");
            case HISTOGRAM:
                return DesktopEnvironment.getIcon("BarGraph");
            case PIE:
                return DesktopEnvironment.getIcon("PieGraph");
            default:
                throw new RuntimeException("Unknown chart type.");
        }
    }
}
