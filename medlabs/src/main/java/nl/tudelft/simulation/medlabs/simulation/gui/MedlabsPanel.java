package nl.tudelft.simulation.medlabs.simulation.gui;

import java.awt.Font;
import java.rmi.RemoteException;
import java.util.function.Function;

import org.knowm.xchart.style.AxesChartStyler.TextAlignment;
import org.knowm.xchart.style.Styler.ToolTipType;
import org.knowm.xchart.style.markers.SeriesMarkers;
import org.pmw.tinylog.Level;

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.set.TIntSet;
import nl.tudelft.simulation.dsol.swing.gui.DsolPanel;
import nl.tudelft.simulation.dsol.swing.gui.TablePanel;
import nl.tudelft.simulation.dsol.swing.gui.control.AbstractControlPanel;
import nl.tudelft.simulation.medlabs.disease.DiseasePhase;
import nl.tudelft.simulation.medlabs.disease.DiseaseProgression;
import nl.tudelft.simulation.medlabs.location.Location;
import nl.tudelft.simulation.medlabs.location.LocationType;
import nl.tudelft.simulation.medlabs.location.animation.LocationAnimation;
import nl.tudelft.simulation.medlabs.model.MedlabsModelInterface;
import nl.tudelft.simulation.medlabs.person.Person;
import nl.tudelft.simulation.medlabs.person.PersonMonitor;
import nl.tudelft.simulation.medlabs.simulation.SimpleDevsSimulatorInterface;
import nl.tudelft.simulation.medlabs.simulation.gui.chart.HistogramDouble;
import nl.tudelft.simulation.medlabs.simulation.gui.chart.HistogramInt;
import nl.tudelft.simulation.medlabs.simulation.gui.chart.LineChart;

/**
 * MedlabsPanel.java.
 * <p>
 * Copyright (c) 2020-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. The
 * MEDLABS project (Modeling Epidemic Disease with Large-scale Agent-Based Simulation) is aimed at providing policy analysis
 * tools to predict and help contain the spread of epidemics. It makes use of the DSOL simulation engine and the agent-based
 * modeling formalism. See for project information <a href="http://www.simulation.tudelft.nl/"> www.simulation.tudelft.nl</a>.
 * The original MEDLABS Java library was developed as part of the PhD research of Mingxin Zhang at TU Delft and is described in
 * the PhD thesis "Large-Scale Agent-Based Social Simulation" (2016). This software is licensed under the BSD license. See
 * license.txt in the main project.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class MedlabsPanel extends DsolPanel
{
    /** */
    private static final long serialVersionUID = 20210128L;

    /** the model. */
    private final MedlabsModelInterface model;

    /**
     * @param controlPanel the control panel
     * @param model the model
     * @throws RemoteException on network error
     */
    public MedlabsPanel(final AbstractControlPanel<?, ?> controlPanel, final MedlabsModelInterface model) throws RemoteException
    {
        super(controlPanel);
        this.model = model;
        createAnimation();
        addTabs();
    }

    /**
     * Create the animation in case the simulation is interactive.
     */
    protected void createAnimation()
    {
        if (getModel().isInteractive())
        {
            try
            {
                // add the locations to the animation
                for (LocationType locationType : getModel().getLocationTypeIndexMap().values())
                {
                    if (locationType.getAnimationClass() != null)
                    {
                        Class<? extends LocationAnimation> animationClass = locationType.getAnimationClass();
                        for (Location location : locationType.getLocationMap().values(new Location[0]))
                        {
                            animationClass.getDeclaredConstructor(new Class<?>[] {Location.class}).newInstance(location);
                        }
                    }
                    System.out.println(locationType.getName() + " length: " + locationType.getLocationMap().size());
                }
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
            }
        }
    }

    /** Add the tabs. */
    protected void addTabs()
    {
        addConsoleOutput();
        addConsoleLogger(Level.INFO);
        addLocationStatistics();
        addDiseaseStatistics(this.model.getDiseaseProgression());
        addInfectionStatisticsPerLocationType();
        addInfectionStatisticsPerAge();
        addPersonStatistics();
    }

    /**
     * Make two tabs with location statistics: one for the number of people per location, the other for the duration per
     * location.
     */
    protected void addLocationStatistics()
    {
        SimpleDevsSimulatorInterface simulator = getModel().getSimulator();

        // create the location statistics
        int rows = (int) Math.ceil(1.0 * getModel().getLocationTypeIndexMap().size() / 6.0);
        TablePanel locationCharts = new TablePanel(6, rows);
        getTabbedPane().addTab(2, "number in location", locationCharts);

        // create
        TablePanel actDurCharts = new TablePanel(6, rows);
        getTabbedPane().addTab(3, "duration in location", actDurCharts);

        int row = 0;
        int col = 0;
        for (LocationType locationType : getModel().getLocationTypeIndexMap().values())
        {
            try
            {
                LineChart chart = new LineChart(locationType.getName(), "time (h)", "");
                chart.listenTo(locationType, LocationType.STATISTICS_EVENT);
                chart.setXMin(0).setYMin(0).setXMax(simulator.getReplication().getEndTime());
                chart.getChart().getStyler().setPlotMargin(0).setXAxisDecimalPattern("###0").setYAxisDecimalPattern("#####0")
                        .setYAxisLabelAlignment(TextAlignment.Right)
                        .setAxisTickLabelsFont(new Font(Font.SANS_SERIF, Font.PLAIN, 8));
                chart.getChart().getStyler().setxAxisTickLabelsFormattingFunction(new Function<Double, String>()
                {
                    @Override
                    public String apply(final Double t)
                    {
                        if (t.doubleValue() == 0.0)
                            return "\u030D   0     \u030D";
                        return String.format("%d", t.intValue());
                    }

                });
                chart.getSeries().setLineWidth(0.75f);
                chart.getSeries().setMarker(SeriesMarkers.NONE);
                locationCharts.setCell(chart.getSwingPanel(), col, row);

                // make sure every LocationType reports about its status every 5 minutes
                simulator.scheduleEventRel(0.0, locationType, "reportStatistics", null);

                HistogramDouble histogram = null;
                if (locationType.getName() == "metrotrain" || locationType.getName() == "bus"
                        || locationType.getName() == "metrostop" || locationType.getName() == "busstop"
                        || locationType.getLocationTypeId() < 0)
                {
                    histogram = new HistogramDouble(locationType.getName(), "hours", "", 0, 2, 0.1, "%2.1f", true);
                    histogram.getChart().getStyler().setXAxisLabelRotation(90)
                            .setAxisTickLabelsFont(new Font(Font.SANS_SERIF, Font.PLAIN, 8));
                    histogram.getChart().getStyler().setYAxisTitleVisible(false).setYAxisTicksVisible(false);
                }
                else
                {
                    histogram = new HistogramDouble(locationType.getName(), "hours", "", 0, 12, 0.5, "%2.1f", true);
                    histogram.getChart().getStyler().setXAxisLabelRotation(90)
                            .setAxisTickLabelsFont(new Font(Font.SANS_SERIF, Font.PLAIN, 8));
                    histogram.getChart().getStyler().setYAxisTitleVisible(false).setYAxisTicksVisible(false);
                }

                histogram.listenTo(locationType, LocationType.DURATION_EVENT);
                actDurCharts.setCell(histogram.getSwingPanel(), col, row);
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
            }

            col++;
            if (col > 5)
            {
                row++;
                col = 0;
            }
        }
    }

    private void addDiseaseStatistics(final DiseaseProgression disease)
    {
        SimpleDevsSimulatorInterface simulator = getModel().getSimulator();

        int rows = (int) Math.ceil(1.0 * disease.getDiseasePhases().size() / 4.0);
        TablePanel diseaseCharts = new TablePanel(4, rows);
        getTabbedPane().addTab(4, "disease number", diseaseCharts);

        int row = 0;
        int col = 0;

        // create the disease statistics
        for (DiseasePhase diseasePhase : disease.getDiseasePhases())
        {
            try
            {
                LineChart chart = new LineChart(diseasePhase.getName(), "time (h)", "");
                chart.listenTo(getModel().getDiseaseMonitor(), diseasePhase.DISEASE_STATISTICS_EVENT);
                chart.setXMin(0).setYMin(0).setXMax(simulator.getReplication().getEndTime());
                chart.getChart().getStyler().setPlotMargin(0).setXAxisDecimalPattern("###0").setYAxisDecimalPattern("#####0")
                        .setYAxisLabelAlignment(TextAlignment.Right);
                chart.getSeries().setMarker(SeriesMarkers.NONE);
                diseaseCharts.setCell(chart.getSwingPanel(), col, row);

                col++;
                if (col > 3)
                {
                    row++;
                    col = 0;
                }
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
            }
        }
    }

    /**
     * Make a tab with disease transmission statistics per location type per day.
     */
    protected void addInfectionStatisticsPerLocationType()
    {
        SimpleDevsSimulatorInterface simulator = getModel().getSimulator();

        // create the location statistics
        int rows = (int) Math.ceil(1.0 * getModel().getLocationTypeIndexMap().size() / 6.0);
        TablePanel infectionLocationCharts = new TablePanel(6, rows);
        getTabbedPane().addTab(5, "infection location", infectionLocationCharts);

        int row = 0;
        int col = 0;
        for (LocationType locationType : getModel().getLocationTypeIndexMap().values())
        {
            try
            {
                LineChart chart = new LineChart("Infection " + locationType.getName(), "time (h)", "");
                chart.listenTo(getModel().getPersonMonitor(),
                        getModel().getPersonMonitor().INFECT_LOCATIONTYPE_PER_DAY_EVENT.get(locationType));
                chart.setXMin(0).setYMin(0).setXMax(simulator.getReplication().getEndTime());
                chart.getChart().getStyler().setPlotMargin(0).setXAxisDecimalPattern("###0").setYAxisDecimalPattern("#####0")
                        .setYAxisLabelAlignment(TextAlignment.Right)
                        .setAxisTickLabelsFont(new Font(Font.SANS_SERIF, Font.PLAIN, 8));
                chart.getSeries().setLineWidth(0.75f);
                chart.getSeries().setMarker(SeriesMarkers.NONE);
                infectionLocationCharts.setCell(chart.getSwingPanel(), col, row);
            }
            catch (Exception exception)
            {
                System.err.println(locationType);
                System.err.println(getModel().getPersonMonitor().INFECT_LOCATIONTYPE_PER_DAY_EVENT);
                exception.printStackTrace();
            }

            col++;
            if (col > 5)
            {
                row++;
                col = 0;
            }
        }
    }

    /**
     * Make a tab with disease transmission statistics per age bracket per day.
     */
    protected void addInfectionStatisticsPerAge()
    {
        SimpleDevsSimulatorInterface simulator = getModel().getSimulator();

        // create the age statistics
        TablePanel infectionAgeCharts = new TablePanel(4, 3);
        getTabbedPane().addTab(6, "infection age", infectionAgeCharts);

        int row = 0;
        int col = 0;
        for (int ageBracket = 0; ageBracket < 11; ageBracket++)
        {
            String bracket = (10 * ageBracket) + "-" + (10 * (ageBracket + 1) - 1);
            try
            {
                LineChart chart = new LineChart("Infection " + bracket, "time (h)", "");
                chart.listenTo(getModel().getPersonMonitor(),
                        getModel().getPersonMonitor().INFECT_AGE_PER_DAY_EVENT[ageBracket]);
                chart.setXMin(0).setYMin(0).setXMax(simulator.getReplication().getEndTime());
                chart.getChart().getStyler().setPlotMargin(0).setXAxisDecimalPattern("###0").setYAxisDecimalPattern("#####0")
                        .setYAxisLabelAlignment(TextAlignment.Right)
                        .setAxisTickLabelsFont(new Font(Font.SANS_SERIF, Font.PLAIN, 8));
                chart.getSeries().setLineWidth(0.75f);
                chart.getSeries().setMarker(SeriesMarkers.NONE);
                infectionAgeCharts.setCell(chart.getSwingPanel(), col, row);
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
            }

            col++;
            if (col > 3)
            {
                row++;
                col = 0;
            }
        }
    }

    private void addPersonStatistics()
    {
        try
        {
            // create the initial person statistics
            TablePanel initialCharts = new TablePanel(2, 2);
            getTabbedPane().addTab(7, "other statistics", initialCharts);

            HistogramInt agehistogram = new HistogramInt("age distribution", "age", "#", 0, 109, 10, false);
            agehistogram.listenTo(getModel().getPersonMonitor(), PersonMonitor.AGE_EVENT);
            agehistogram.getChart().getStyler().setToolTipsEnabled(true).setToolTipType(ToolTipType.yLabels);
            for (TIntObjectIterator<Person> it = getModel().getPersonMap().iterator(); it.hasNext();)
            {
                it.advance();
                Person person = it.value();
                getModel().getPersonMonitor().reportAge(person.getAge());
            }
            initialCharts.setCell(agehistogram.getSwingPanel(), 0, 0);

            HistogramInt familySizeHistogram = new HistogramInt("Family size distribution", "family size", "#", 0, 9, 1, false);
            familySizeHistogram.getChart().getStyler().setToolTipsEnabled(true).setToolTipType(ToolTipType.yLabels);
            TIntObjectMap<TIntSet> families = getModel().getFamilyMembersByHomeLocation();
            int[] fs = new int[10];
            for (int homeLocationId : families.keys())
            {
                Location homeLocation = this.model.getLocationMap().get(homeLocationId);
                int sub = homeLocation.getNumberOfSubLocations();
                TIntSet family = families.get(homeLocationId);
                int famSize = family.size() / sub;
                for (int i = 0; i < sub; i++)
                {
                    getModel().getPersonMonitor().reportFamilySize(famSize);
                    fs[Math.min(9, famSize)] += 1;
                }
            }
            for (int i = 0; i < 10; i++)
                familySizeHistogram.setCount(i, fs[i]);
            initialCharts.setCell(familySizeHistogram.getSwingPanel(), 1, 0);

            HistogramInt deathsHistogram = new HistogramInt("deaths by age", "age", "#", 0, 109, 10, false);
            deathsHistogram.listenTo(getModel().getPersonMonitor(), PersonMonitor.DEATH_EVENT);
            deathsHistogram.getChart().getStyler().setToolTipsEnabled(true).setToolTipType(ToolTipType.yLabels);
            initialCharts.setCell(deathsHistogram.getSwingPanel(), 0, 1);
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
    }

    /**
     * @return the model
     */
    @Override
    public MedlabsModelInterface getModel()
    {
        return this.model;
    }

}
