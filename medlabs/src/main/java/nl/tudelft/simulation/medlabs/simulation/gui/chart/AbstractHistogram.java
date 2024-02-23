package nl.tudelft.simulation.medlabs.simulation.gui.chart;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.djutils.event.EventInterface;
import org.djutils.event.EventProducerInterface;
import org.djutils.event.EventTypeInterface;
import org.djutils.exceptions.Throw;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.style.Styler.ToolTipType;

/**
 * AbstractHistogram is the parent class for a histogram that can display counted values per bin. It embeds a chart that can
 * display the histogram on the screen. Currently we use XCharts for the display, but it could easily be changed for another
 * charting package.
 * <p>
 * Copyright (c) 2022-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param <T> the type of variable that defines the bins; typically String, Integer or Double
 */
public abstract class AbstractHistogram<T> extends DynamicChart<CategoryChart>
{
    /** */
    private static final long serialVersionUID = 20220918L;

    /** the bin counts; use the last bin for 'wrong' x-values. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected final List<Integer> binValues = new ArrayList<>();

    /** the bin labels; use the last bin for 'wrong' x-values. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected final List<String> binLabels = new ArrayList<>();

    /** the event with the x-value to listen to. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected EventTypeInterface listenerEventType = null;

    /**
     * Create a new histogram.
     * @param title String; the title of the histogram
     * @param xLabel String; the label for the x-axis
     * @param yLabel String; the label for the y-axis
     */
    public AbstractHistogram(final String title, final String xLabel, final String yLabel)
    {
        this.chart =
                new CategoryChartBuilder().title(title).xAxisTitle(xLabel).yAxisTitle(yLabel).width(1920).height(1280).build();
        this.chart.getStyler().setLegendVisible(false);
        this.chartPanel = new MenuXChartPanel<CategoryChart>(this);
    }

    /**
     * Resolve the bin number for value x. Use the bin with index nrBins is used for 'wrong' x-values.
     * @param x T; the value to resolve the bin for
     * @return the bin number, or nrBins in case the bin for value x could not be resolved
     */
    protected abstract int resolveBin(T x);

    /**
     * Return the value of the count of a bin belonging to the x-value.
     * @param x T; the value of the observation to retrieve the count for
     * @return int; the value of the count of the bin, or -1 if the value does not translate to a bin
     */
    public int getCount(final T x)
    {
        int bin = resolveBin(x);
        if (bin >= 0 && bin < this.binValues.size())
            return this.binValues.get(resolveBin(x));
        return -1;
    }

    /**
     * Set the value of the count of a bin belonging to the x-value.
     * @param x T; the value of the observation to set the count for
     * @param value int; the new value of the count of the bin
     */
    public void setCount(final T x, final int value)
    {
        int bin = resolveBin(x);
        if (bin >= 0 && bin < this.binValues.size())
        {
            this.binValues.set(bin, value);
            setDirty();
        }
    }

    /**
     * Add a count to the existing count of a bin belonging to the x-value.
     * @param x T; the value of the observation to increase the count for
     * @param increase int; the increase of the bin
     */
    public void addCount(final T x, final int increase)
    {
        int bin = resolveBin(x);
        if (bin >= 0 && bin < this.binValues.size())
        {
            this.binValues.set(bin, this.binValues.get(bin) + increase);
            setDirty();
        }
    }

    /**
     * Add 1 to the existing count of a bin belonging to the x-value.
     * @param x T; the value of the observation to increase the count for
     */
    public void addCount(final T x)
    {
        addCount(x, 1);
    }

    /** {@inheritDoc} */
    @Override
    protected void updateChart()
    {
        this.chart.updateCategorySeries("hist", this.binLabels, this.binValues, null);
        fireEvent(DynamicChart.UPDATE_EVENT);
    }

    /**
     * Start listening to a certain event from an eventProducer.
     * @param eventProducer EventProducerInterface;
     * @param eventType EventType; the event to listen to
     * @throws RemoteException on networking error for remote events
     */
    public void listenTo(final EventProducerInterface eventProducer, final EventTypeInterface eventType) throws RemoteException
    {
        Throw.when(this.listenerEventType != null, RuntimeException.class,
                "eventType " + this.listenerEventType + " already registered");
        this.listenerEventType = eventType;
        eventProducer.addListener(this, eventType);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public void notify(final EventInterface event) throws RemoteException
    {
        if (event.getType().equals(this.listenerEventType))
        {
            addCount((T) event.getContent());
        }
    }

}
