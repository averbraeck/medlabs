package nl.tudelft.simulation.medlabs.simulation.gui.chart;

import java.rmi.RemoteException;

import org.djutils.event.Event;
import org.djutils.event.EventListenerMap;
import org.djutils.event.EventProducer;
import org.djutils.event.EventType;
import org.djutils.event.TimedEvent;
import org.djutils.exceptions.Throw;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;

import gnu.trove.list.TDoubleList;
import gnu.trove.list.array.TDoubleArrayList;

/**
 * LineChart draws a single X-Y plot in a panel. Currently we use XCharts for
 * the display, but it could easily be changed for another charting package.
 * <p>
 * Copyright (c) 2022-2024 Delft University of Technology, Jaffalaan 5, 2628 BX
 * Delft, the Netherlands. All rights reserved.
 * </p>
 * 
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class LineChart extends DynamicChart<XYChart> {
	/** */
	private static final long serialVersionUID = 20220918L;

	/** x-data as a List to easily add new points. */
	@SuppressWarnings("checkstyle:visibilitymodifier")
	protected TDoubleList xData = new TDoubleArrayList();

	/** y-data as a List to easily add new points. */
	@SuppressWarnings("checkstyle:visibilitymodifier")
	protected TDoubleList yData = new TDoubleArrayList();

	/** the event to listen to update the graph with new points. */
	@SuppressWarnings("checkstyle:visibilitymodifier")
	protected EventType listenerEventType = null;

	/** the series. */
	@SuppressWarnings("checkstyle:visibilitymodifier")
	protected XYSeries series;

	/**
	 * Create a new line chart.
	 * 
	 * @param title  String; the title of the chart
	 * @param xLabel String; the label for the x-axis
	 * @param yLabel String; the label for the y-axis
	 */
	public LineChart(final String title, final String xLabel, final String yLabel) {
		this.chart = new XYChartBuilder().title(title).xAxisTitle(xLabel).yAxisTitle(yLabel).width(1920).height(1280)
				.build();
		this.xData.add(0.0);
		this.yData.add(0.0);
		this.series = this.chart.addSeries(title, this.xData.toArray(), this.yData.toArray());
		this.chart.getStyler().setLegendVisible(false);
		this.chart.getStyler().setToolTipsEnabled(false);
		this.chartPanel = new MenuXChartPanel<XYChart>(this);
	}

	/**
	 * Fix the minimum for the x-axis.
	 * 
	 * @param xMin double; minimum for the x-axis
	 * @return the LineChart for method chaining
	 */
	public LineChart setXMin(final double xMin) {
		this.chart.getStyler().setXAxisMin(xMin);
		return this;
	}

	/**
	 * Fix the maximum for the x-axis.
	 * 
	 * @param xMax double; maximum for the x-axis
	 * @return the LineChart for method chaining
	 */
	public LineChart setXMax(final double xMax) {
		this.chart.getStyler().setXAxisMax(xMax);
		return this;
	}

	/**
	 * Fix the minimum for the y-axis.
	 * 
	 * @param yMin double; minimum for the y-axis
	 * @return the LineChart for method chaining
	 */
	public LineChart setYMin(final double yMin) {
		this.chart.getStyler().setYAxisMin(yMin);
		return this;
	}

	/**
	 * Fix the maximum for the y-axis.
	 * 
	 * @param yMax double; maximum for the y-axis
	 * @return the LineChart for method chaining
	 */
	public LineChart setYMax(final double yMax) {
		this.chart.getStyler().setYAxisMax(yMax);
		return this;
	}

	/**
	 * Add a point to the chart.
	 * 
	 * @param x double; x-value of the point
	 * @param y double; y-value of the point
	 */
	public synchronized void addPoint(final double x, final double y) {
		this.xData.add(x);
		this.yData.add(y);
		setDirty();
	}

	/**
	 * Update the (dynamic) chart on the screen.
	 */
	@Override
	protected synchronized void updateChart() {
		try {
			this.chart.updateXYSeries(getChart().getTitle(), this.xData.toArray(), this.yData.toArray(), null);
			fireEvent(DynamicChart.UPDATE_EVENT);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Return the series of this chart.
	 * 
	 * @return XYSeries; the series of this chart
	 */
	public XYSeries getSeries() {
		return this.series;
	}

	/**
	 * Start listening to a certain event from an eventProducer.
	 * 
	 * @param eventProducer EventProducerInterface; the event producer to listen to
	 * @param eventType     EventType; the event to listen to
	 * @throws RemoteException on networking error for remote events
	 */
	public void listenTo(final EventProducer eventProducer, final EventType eventType) throws RemoteException {
		Throw.when(this.listenerEventType != null, RuntimeException.class,
				"eventType " + this.listenerEventType + " already registered");
		this.listenerEventType = eventType;
		eventProducer.addListener(this, eventType);
	}

	/** {@inheritDoc} */
	@Override
	public void notify(final Event event) throws RemoteException {
		if (event.getType().equals(this.listenerEventType)) {
			if (event instanceof TimedEvent) {
				// assume time + Number
				TimedEvent<?> timedEvent = (TimedEvent<?>) event;
				if (timedEvent.getTimeStamp() instanceof Number && timedEvent.getContent() instanceof Number) {
					double x = ((Number) timedEvent.getTimeStamp()).doubleValue();
					double y = ((Number) timedEvent.getContent()).doubleValue();
					addPoint(x, y);
				}
			} else if (event.getContent() instanceof double[]) {
				// assume double[2] with x and y
				double[] arr = (double[]) event.getContent();
				addPoint(arr[0], arr[1]);
			}
		}
	}

	@Override
	public EventListenerMap getEventListenerMap() throws RemoteException {
		return null;
	}

}
