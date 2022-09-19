package nl.tudelft.simulation.medlabs.simulation.gui.chart;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * HistogramInt is a histogram that uses string values for the bins.
 * <p>
 * Copyright (c) 2022-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class HistogramString extends AbstractHistogram<String>
{
    /** */
    private static final long serialVersionUID = 20220918L;

    /** the mapping of string values on bin numbers. */
    private final Map<String, Integer> binMapping = new LinkedHashMap<>();

    /** whether to show a bin with other values. */
    private final boolean showOther;

    /**
     * Create a new histogram that uses string values for the bins.
     * @param title String; the title of the histogram
     * @param xLabel String; the label for the x-axis
     * @param yLabel String; the label for the y-axis
     * @param binLabels List&lt;String&gt;; the list of bin labels
     * @param showOther boolean; whether to show a bin with other values
     */
    public HistogramString(final String title, final String xLabel, final String yLabel, final List<String> binLabels,
            final boolean showOther)
    {
        super(title, xLabel, yLabel);

        // make the labels and fill the values with 0
        int index = 0;
        for (String label : binLabels)
        {
            this.binValues.add(0);
            this.binLabels.add(label);
            this.binMapping.put(label, index);
            index++;
        }

        // potentially add the label and value for wrong x-values
        this.showOther = showOther;
        if (this.showOther)
        {
            this.binValues.add(0);
            this.binLabels.add("other");
        }

        // always end with making the series
        this.chart.addSeries("hist", this.binLabels, this.binValues);
    }

    /** {@inheritDoc} */
    @Override
    protected int resolveBin(final String x)
    {
        int bin = this.binLabels.indexOf(x);
        return (bin >= 0) ? bin : this.binLabels.size() + 1;
    }

}
