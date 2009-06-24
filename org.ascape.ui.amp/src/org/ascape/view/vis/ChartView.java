package org.ascape.view.vis;

import java.util.Collection;
import java.util.HashSet;

import org.ascape.util.data.DataSelection;
import org.eclipse.amp.agf.chart.CachedColorProvider;
import org.eclipse.amp.agf.chart.ChartEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.IViewPart;

public class ChartView extends EditPartScapeView {

    private static final long serialVersionUID = 1L;
    private ChartEditPart chartPart;
    private Collection<String> seriesCache = new HashSet<String>();
    CachedColorProvider seriesColorProvider = new CachedColorProvider();

    public ChartView() {
        super("org.eclipse.amp.view.ChartView", null, "Chart View");
    }

    public EditPart createEditPart(EditPart context, Object model) {
        chartPart = new ChartEditPart() {
            @Override
            public Object getAdapter(Class key) {
                if (key == IViewPart.class) {
                    return getViewPart();
                }
                return ChartView.super.getAdapter(key);
            }
        };
        chartPart.setSeriesColorProvider(seriesColorProvider);

        chartPart.setModel(model);
        synchCache(model);
        return chartPart;
    }

    private void synchCache(Object model) {
        DataSelection dataSelection = (DataSelection) chartPart.getDataProvider().getDataSource(model);
        for (String series : seriesCache) {
            dataSelection.setSelected(series, true);
        }
    }

    /**
     * Adds a series to the chart being viewed. Convenience method (could also get view model and set select group.)
     * 
     * @param valueName
     *        the name of the series to add
     */
    public void addSeries(String valueName) {
        seriesCache.add(valueName);
        if (chartPart != null) {
            synchCache(chartPart.getModel());
        }
    }

    /**
     * Adds a series to the chart being viewed. Convenience method.
     * 
     * @param valueName
     *        the name of the series to add
     * @param color
     *        the color to use for the series in the chart
     */
    public void addSeries(String valueName, Color color) {
        seriesColorProvider.putForegroundColor(valueName, color);
        addSeries(valueName);
    }
}
