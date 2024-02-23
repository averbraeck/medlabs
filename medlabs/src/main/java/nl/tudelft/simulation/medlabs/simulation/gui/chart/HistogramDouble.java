package nl.tudelft.simulation.medlabs.simulation.gui.chart;

/**
 * HistogramInt is a histogram that uses double values for the bins.
 * <p>
 * Copyright (c) 2022-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class HistogramDouble extends AbstractHistogram<Double>
{
    /** */
    private static final long serialVersionUID = 20220918L;

    /** the lowest value for the first bin. */
    private final double lo;

    /** the highest value for the last bin. */
    private final double hi;

    /** the size of each bin. */
    private final double binSize;

    /** the number of bins; note that there is always one extra bin for wrong x-values. */
    private final int nrBins;
    
    /** whether to show a bin with other values. */
    private final boolean showOther;

    /**
     * Create a new histogram that uses doubles for the bins.
     * @param title String; the title of the histogram
     * @param xLabel String; the label for the x-axis
     * @param yLabel String; the label for the y-axis
     * @param lo double; the lowest value for the first bin
     * @param hi double; the highest value for the last bin
     * @param binSize double; the size of each bin
     * @param formatXLabels String; the format string for the x-labels
     * @param showOther boolean; whether to show a bin with other values
     */
    public HistogramDouble(final String title, final String xLabel, final String yLabel, final double lo, final double hi,
            final double binSize, final String formatXLabels, final boolean showOther)
    {
        super(title, xLabel, yLabel);
        this.lo = lo;
        this.hi = hi;
        this.binSize = binSize;
        this.nrBins = (int) Math.round((hi - lo) / binSize);

        // make the labels and fill the values with 0
        for (int i = 0; i < this.nrBins; i++)
        {
            this.binValues.add(0);
            this.binLabels.add(String.format(formatXLabels, lo + (0.5 + i) * binSize));
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
    protected int resolveBin(final Double x)
    {
        if (x < this.lo || x > this.hi)
            return this.nrBins;
        return (int) Math.floor(((1.0 * x - this.lo) / this.binSize));
    }

}
