package nl.tudelft.simulation.medlabs.simulation.gui.chart;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.djutils.event.Event;
import org.djutils.event.EventListenerMap;
import org.djutils.event.EventProducer;
import org.djutils.event.EventType;
import org.djutils.exceptions.Throw;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.Styler.ToolTipType;

/**
 * MultiLineChart draws multiple X-Y plots in a panel where the lines share a
 * common X and Y axis. Currently we use XCharts for the display, but it could
 * easily be changed for another charting package.
 * <p>
 * Copyright (c) 2022-2024 Delft University of Technology, Jaffalaan 5, 2628 BX
 * Delft, the Netherlands. All rights reserved.
 * </p>
 * 
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class MultiLineChart extends DynamicChart<XYChart> {
	/** */
	private static final long serialVersionUID = 20220918L;

	/**
	 * x-data as a series map of Lists to easily add new points. The map key is the
	 * series name.
	 */
	@SuppressWarnings("checkstyle:visibilitymodifier")
	protected Map<String, List<Double>> xData = new LinkedHashMap<>();

	/**
	 * y-data as a series map of Lists to easily add new points. The map key is the
	 * series name.
	 */
	@SuppressWarnings("checkstyle:visibilitymodifier")
	protected Map<String, List<Double>> yData = new LinkedHashMap<>();

	/**
	 * the event to listen to for each series. The Map maps eventName#sourceId onto
	 * the series name.
	 */
	@SuppressWarnings("checkstyle:visibilitymodifier")
	protected Map<String, String> listenerEventTypes = new LinkedHashMap<>();

	/**
	 * Create a new line chart.
	 * 
	 * @param title  String; the title of the chart
	 * @param xLabel String; the label for the x-axis
	 * @param yLabel String; the label for the y-axis
	 */
	public MultiLineChart(final String title, final String xLabel, final String yLabel) {
		this.chart = new XYChartBuilder().title(title).xAxisTitle(xLabel).yAxisTitle(yLabel).width(1920).height(1280)
				.build();
		this.chart.getStyler().setLegendVisible(false);
		this.chart.getStyler().setToolTipsEnabled(true);
		this.chart.getStyler().setToolTipType(ToolTipType.xAndYLabels);
		this.chartPanel = new MenuXChartPanel<XYChart>(this);
	}

	/**
	 * Fix the minimum for the x-axis.
	 * 
	 * @param xMin double; minimum for the x-axis
	 * @return the LineChart for method chaining
	 */
	public MultiLineChart setXMin(final double xMin) {
		this.chart.getStyler().setXAxisMin(xMin);
		return this;
	}

	/**
	 * Fix the maximum for the x-axis.
	 * 
	 * @param xMax double; maximum for the x-axis
	 * @return the LineChart for method chaining
	 */
	public MultiLineChart setXMax(final double xMax) {
		this.chart.getStyler().setXAxisMax(xMax);
		return this;
	}

	/**
	 * Fix the minimum for the y-axis.
	 * 
	 * @param yMin double; minimum for the y-axis
	 * @return the LineChart for method chaining
	 */
	public MultiLineChart setYMin(final double yMin) {
		this.chart.getStyler().setYAxisMin(yMin);
		return this;
	}

	/**
	 * Fix the maximum for the y-axis.
	 * 
	 * @param yMax double; maximum for the y-axis
	 * @return the LineChart for method chaining
	 */
	public MultiLineChart setYMax(final double yMax) {
		this.chart.getStyler().setYAxisMax(yMax);
		return this;
	}

	/**
	 * Add an empty x-y series to the chart.
	 * 
	 * @param seriesName String; the name of the series
	 */
	public void addSeries(final String seriesName) {
		List<Double> xp = new ArrayList<>();
		this.xData.put(seriesName, xp);
		List<Double> yp = new ArrayList<>();
		this.yData.put(seriesName, yp);
		this.chart.addSeries(seriesName, xp, yp);
	}

	/**
	 * Add a point for a series to the chart.
	 * 
	 * @param seriesName String; the name of the series to add the pint to
	 * @param x          double; x-value of the point
	 * @param y          double; y-value of the point
	 */
	public void addPoint(final String seriesName, final double x, final double y) {
		this.xData.get(seriesName).add(x);
		this.yData.get(seriesName).add(y);
	}

	/**
	 * Update the (dynamic) chart on the screen.
	 */
	@Override
	protected void updateChart() {
		for (String seriesName : this.xData.keySet()) {
			this.chart.updateXYSeries(seriesName, this.xData.get(seriesName), this.yData.get(seriesName), null);
		}
		this.chartPanel.revalidate();
	}

	/**
	 * Start listening to a certain event from an eventProducer for one of the
	 * plotted series.
	 * 
	 * @param seriesName    String; the name of the series to update via events
	 * @param eventProducer EventProducerInterface;
	 * @param eventType     EventType; the event to listen to
	 * @throws RemoteException on networking error for remote events
	 */
	public void listenTo(final String seriesName, final EventProducer eventProducer, final EventType eventType)
			throws RemoteException {
		String key = eventType.getName();
		Throw.when(this.listenerEventTypes.containsKey(key), RuntimeException.class,
				"eventType " + this.listenerEventTypes + " already registered");
		this.listenerEventTypes.put(key, seriesName);
		eventProducer.addListener(this, eventType);
	}

	/** {@inheritDoc} */
	@Override
	public void notify(final Event event) throws RemoteException {
		String seriesName = this.listenerEventTypes.get(event.getType().getName());
		if (seriesName != null) {
			if (event.getContent() instanceof double[]) {
				double[] arr = (double[]) event.getContent();
				addPoint(seriesName, arr[0], arr[1]);
			}
		}
	}

	EventListenerMap eventListenerMap = new EventListenerMap();

	@Override
	public EventListenerMap getEventListenerMap() throws RemoteException {
		return eventListenerMap;
	}
}
