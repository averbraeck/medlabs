package nl.tudelft.simulation.medlabs.simulation.gui.chart;

/**
 * HistogramInt is a histogram that uses integer values for the bins.
 * <p>
 * Copyright (c) 2022-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class HistogramInt extends AbstractHistogram<Integer>
{
    /** */
    private static final long serialVersionUID = 20220918L;

    /** the lowest value for the first bin. */
    private final int lo;

    /** the highest value for the last bin. */
    private final int hi;

    /** the size of each bin. */
    private final int binSize;

    /** the number of bins; note that there is always one extra bin for wrong x-values. */
    private final int nrBins;
    
    /** whether to show a bin with other values. */
    private final boolean showOther;

    /**
     * Create a new histogram that uses integers for the bins.
     * @param title String; the title of the histogram
     * @param xLabel String; the label for the x-axis
     * @param yLabel String; the label for the y-axis
     * @param lo int; the lowest value for the first bin
     * @param hi int; the highest value for the last bin
     * @param binSize int; the size of each bin
     * @param showOther boolean; whether to show a bin with other values
     */
    public HistogramInt(final String title, final String xLabel, final String yLabel, final int lo, final int hi,
            final int binSize, final boolean showOther)
    {
        super(title, xLabel, yLabel);
        this.lo = lo;
        this.hi = hi;
        this.binSize = binSize;
        this.nrBins = (hi - lo + 1) / binSize;

        // make the labels and fill the values with 0
        for (int i = 0; i < this.nrBins; i++)
        {
            this.binValues.add(0);
            if (this.binSize == 1)
                this.binLabels.add(String.valueOf(lo + i));
            else
                this.binLabels.add(String.valueOf(lo + i * binSize) + "-" + String.valueOf(lo + (i + 1) * binSize - 1));
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
    protected int resolveBin(final Integer x)
    {
        if (x < this.lo || x > this.hi)
            return this.nrBins;
        return (int) Math.floor(((1.0 * x - this.lo) / this.binSize));
    }

}
