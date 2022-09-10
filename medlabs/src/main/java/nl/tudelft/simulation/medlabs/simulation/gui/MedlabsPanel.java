package nl.tudelft.simulation.medlabs.simulation.gui;

import java.rmi.RemoteException;

import org.pmw.tinylog.Level;

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.set.TIntSet;
import nl.tudelft.simulation.dsol.swing.charts.histogram.Histogram;
import nl.tudelft.simulation.dsol.swing.charts.xy.XYChart;
import nl.tudelft.simulation.dsol.swing.gui.DSOLPanel;
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
import nl.tudelft.simulation.medlabs.simulation.SimpleDEVSSimulatorInterface;

/**
 * MedlabsPanel.java.
 * <p>
 * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. The
 * MEDLABS project (Modeling Epidemic Disease with Large-scale Agent-Based Simulation) is aimed at providing policy analysis
 * tools to predict and help contain the spread of epidemics. It makes use of the DSOL simulation engine and the agent-based
 * modeling formalism. See for project information <a href="http://www.simulation.tudelft.nl/"> www.simulation.tudelft.nl</a>.
 * The original MEDLABS Java library was developed as part of the PhD research of Mingxin Zhang at TU Delft and is described in
 * the PhD thesis "Large-Scale Agent-Based Social Simulation" (2016). This software is licensed under the BSD license. See
 * license.txt in the main project.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class MedlabsPanel extends DSOLPanel
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
    public MedlabsPanel(final AbstractControlPanel<?, ?, ?, ?> controlPanel, final MedlabsModelInterface model)
            throws RemoteException
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
                            animationClass.getDeclaredConstructor(new Class<?>[] { Location.class }).newInstance(location);
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
        SimpleDEVSSimulatorInterface simulator = getModel().getSimulator();

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
                XYChart chart = new XYChart(simulator, "# " + locationType.getName());
                chart.add(locationType.getName(), locationType, LocationType.STATISTICS_EVENT);
                chart.getChart().getXYPlot().getDomainAxis().setAutoRange(true);
                locationCharts.setCell(chart.getSwingPanel(), col, row);

                // make sure every LocationType reports about its status every 5 minutes
                simulator.scheduleEventRel(0.0, locationType, locationType, "reportStatistics", null);

                Histogram histogram = null;
                if (locationType.getName() == "metrotrain" || locationType.getName() == "bus"
                        || locationType.getName() == "metrostop" || locationType.getName() == "busstop"
                        || locationType.getLocationTypeId() < 0)
                {
                    histogram = new Histogram(simulator, locationType.getName() + " (t)", new double[] { 0, 2 }, 24);
                }
                else
                {
                    histogram = new Histogram(simulator, locationType.getName() + " (t)", new double[] { 0, 12 }, 24);
                }

                histogram.add(locationType.getName(), locationType, LocationType.DURATION_EVENT);
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
        SimpleDEVSSimulatorInterface simulator = getModel().getSimulator();

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
                XYChart chart = new XYChart(simulator, diseasePhase.toString());
                chart.add(diseasePhase.getName(), getModel().getDiseaseMonitor(), diseasePhase.DISEASE_STATISTICS_EVENT);
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
        SimpleDEVSSimulatorInterface simulator = getModel().getSimulator();

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
                XYChart chart = new XYChart(simulator, "Infection " + locationType.getName());
                chart.add(locationType.getName(), getModel().getPersonMonitor(),
                        getModel().getPersonMonitor().INFECT_LOCATIONTYPE_PER_DAY_EVENT.get(locationType));
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
        SimpleDEVSSimulatorInterface simulator = getModel().getSimulator();

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
                XYChart chart = new XYChart(simulator, "Infection " + bracket);
                chart.add(bracket, getModel().getPersonMonitor(),
                        getModel().getPersonMonitor().INFECT_AGE_PER_DAY_EVENT[ageBracket]);
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
        SimpleDEVSSimulatorInterface simulator = getModel().getSimulator();

        try
        {
            // create the initial person statistics
            TablePanel initialCharts = new TablePanel(3, 3);
            getTabbedPane().addTab(7, "other statistics", initialCharts);

            Histogram agehistogram = new Histogram(simulator, "age distribution", new double[] { 0, 100 }, 20);
            agehistogram.add("age distribution", getModel().getPersonMonitor(), PersonMonitor.AGE_EVENT);
            for (TIntObjectIterator<Person> it = getModel().getPersonMap().iterator(); it.hasNext();)
            {
                it.advance();
                Person person = it.value();
                getModel().getPersonMonitor().reportAge(person.getAge());
            }
            initialCharts.setCell(agehistogram.getSwingPanel(), 0, 0);

            Histogram familySizeHistogram = new Histogram(simulator, "family size distribution", new double[] { 0, 10 }, 10);
            familySizeHistogram.add("family size distribution", getModel().getPersonMonitor(), PersonMonitor.FAMILY_EVENT);
            for (TIntSet family : getModel().getFamilyMembersByHomeLocation().values(new TIntSet[0]))
            {
                getModel().getPersonMonitor().reportFamilySize(family.size());
            }
            initialCharts.setCell(familySizeHistogram.getSwingPanel(), 1, 0);

            Histogram deathsHistogram = new Histogram(simulator, "deaths by age", new double[] { 0, 100 }, 10);
            deathsHistogram.add("deaths by age", getModel().getPersonMonitor(), PersonMonitor.DEATH_EVENT);
            initialCharts.setCell(deathsHistogram.getSwingPanel(), 2, 0);
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
