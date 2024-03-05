package nl.tudelft.simulation.medlabs.simulation.gui.chart;

import java.awt.Container;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.djutils.event.EventListener;
import org.djutils.event.EventProducer;
import org.djutils.event.EventType;
import org.djutils.metadata.MetaData;
import org.knowm.xchart.internal.chartpart.Chart;

import nl.tudelft.simulation.dsol.swing.Swingable;

/**
 * DynamicChart contains a few helper methods and fields for all charts.
 * <p>
 * Copyright (c) 2022-2024 Delft University of Technology, Jaffalaan 5, 2628 BX
 * Delft, the Netherlands. All rights reserved.
 * </p>
 * 
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param <T> The chart type
 */
public abstract class DynamicChart<T extends Chart<?, ?>> implements EventProducer, EventListener, Swingable {
	/** */
	private static final long serialVersionUID = 20220918L;

	/** chart contains the rendering and styling of the histogram. */
	@SuppressWarnings("checkstyle:visibilitymodifier")
	protected T chart;

	/** the panel in which the chart is displayed. */
	@SuppressWarnings("checkstyle:visibilitymodifier")
	protected Container chartPanel = null;

	/** EventType to indicate the panel needs to be updated. */
	public static final EventType UPDATE_EVENT = new EventType("UPDATE_EVENT", new MetaData("update", "graph update"));

	/** dirty bit to indicate the chart has been updated. */
	@SuppressWarnings("checkstyle:visibilitymodifier")
	protected AtomicBoolean dirty = new AtomicBoolean(false);

	/** update frequency in ms. */
	@SuppressWarnings("checkstyle:visibilitymodifier")
	protected long chartUpdateFrequency = 1000L;

	/**
	 * Instantiate a new chart.
	 */
	public DynamicChart() {
		new Thread() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(DynamicChart.this.chartUpdateFrequency);
						if (DynamicChart.this.dirty.getAndSet(false)) {
							updateChart();
						}
					} catch (Exception e) {
						System.err.println("Error in update of chart: " + e.getMessage());
					}
				}
			}
		}.start();
	}

	/** {@inheritDoc} */
	public Serializable getSourceId() {
		return "DynamicChart";
	}

	/**
	 * Return the chart that contains the rendering and styling of the histogram.
	 * 
	 * @return chart CategoryChart; the chart that is displayed on the screen
	 */
	public T getChart() {
		return this.chart;
	}

	/** {@inheritDoc} */
	@Override
	public Container getSwingPanel() throws RemoteException {
		return this.chartPanel;
	}

	/**
	 * Update the (dynamic) chart on the screen. Make sure the UPDATE_EVENT is fired
	 * in the method.
	 */
	protected abstract void updateChart();

	/**
	 * Set the dirty bit to indicate the chart has been updated.
	 */
	protected void setDirty() {
		this.dirty.set(true);
	}

	/**
	 * Set the update frequency of the chart.
	 * 
	 * @param chartUpdateFrequency long; the chart update frequency in ms
	 */
	public void setChartUpdateFrequency(final long chartUpdateFrequency) {
		this.chartUpdateFrequency = chartUpdateFrequency;
	}
}
