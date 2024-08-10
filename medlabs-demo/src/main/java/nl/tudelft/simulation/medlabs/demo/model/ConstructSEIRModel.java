package nl.tudelft.simulation.medlabs.demo.model;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.djutils.io.URLResource;

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import nl.tudelft.simulation.medlabs.activity.Activity;
import nl.tudelft.simulation.medlabs.activity.FixedDurationActivity;
import nl.tudelft.simulation.medlabs.activity.TravelActivity;
import nl.tudelft.simulation.medlabs.activity.locator.CarLocator;
import nl.tudelft.simulation.medlabs.activity.locator.CurrentLocator;
import nl.tudelft.simulation.medlabs.activity.locator.HomeLocator;
import nl.tudelft.simulation.medlabs.activity.locator.LocatorInterface;
import nl.tudelft.simulation.medlabs.activity.locator.WorkLocator;
import nl.tudelft.simulation.medlabs.activity.pattern.DayPattern;
import nl.tudelft.simulation.medlabs.activity.pattern.WeekDayPattern;
import nl.tudelft.simulation.medlabs.demo.disease.SEIRProgression;
import nl.tudelft.simulation.medlabs.demo.disease.SEIRTransmission;
import nl.tudelft.simulation.medlabs.demo.person.Worker;
import nl.tudelft.simulation.medlabs.disease.DiseaseMonitor;
import nl.tudelft.simulation.medlabs.disease.DiseaseProgression;
import nl.tudelft.simulation.medlabs.disease.DiseaseTransmission;
import nl.tudelft.simulation.medlabs.location.Location;
import nl.tudelft.simulation.medlabs.location.LocationType;
import nl.tudelft.simulation.medlabs.location.animation.defaults.HouseAnimation;
import nl.tudelft.simulation.medlabs.location.animation.defaults.WorkplaceAnimation;
import nl.tudelft.simulation.medlabs.model.MedlabsModelInterface;
import nl.tudelft.simulation.medlabs.output.ResultWriter;
import nl.tudelft.simulation.medlabs.person.Person;
import nl.tudelft.simulation.medlabs.person.PersonMonitor;
import nl.tudelft.simulation.medlabs.person.PersonType;
import nl.tudelft.simulation.medlabs.person.index.IdxPerson;

/**
 * ConstructSEIRModel.java.
 * <p>
 * Copyright (c) 2020-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. The
 * code is a demo for the MEDLABS project. The simulation tools are aimed at providing policy analysis tools to predict and help
 * contain the spread of epidemics. They make use of the DSOL simulation engine and the agent-based modeling formalism. This
 * software is licensed under the BSD license. See license.txt in the main project.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class ConstructSEIRModel
{
    /** the model. */
    private final SEIRModel model;

    /** Location type house. */
    LocationType houseType;

    /** Location type work. */
    LocationType workType;

    /**
     * Constructor of the model reader.
     * @param model the model
     */
    public ConstructSEIRModel(final SEIRModel model)
    {
        this.model = model;
        URL baseURL = URLResource.getResource(this.model.getParameterValue("generic.InputPath"));
        File file = null;
        if (baseURL != null)
        {
            file = new File(baseURL.getPath());
        }
        if (file == null || !file.exists())
        {
            file = new File(this.model.getParameterValue("generic.InputPath"));
        }
        if (file == null || !file.exists())
        {
            file = new File(URLResource.getResource("/").getPath() + this.model.getParameterValue("generic.InputPath"));
        }
        if (file == null || !file.exists() || !file.isDirectory())
        {
            System.err.println("could not find base path as specified in generic.InputPath parameter with value: "
                    + this.model.getParameterValue("generic.InputPath"));
            System.exit(-1);
        }
        model.setBasePath(file.getAbsolutePath());
        try
        {
            DiseaseProgression seirProgression = new SEIRProgression(this.model);
            DiseaseTransmission seirTransmission = new SEIRTransmission(this.model);
            makeLocationTypes();
            makePersonTypes();
            this.model.setDiseaseProgression(seirProgression);
            this.model.setDiseaseTransmission(seirTransmission);
            this.model.setDiseaseMonitor(new DiseaseMonitor(this.model, seirProgression, 0.5));
            this.model.setPersonMonitor(new PersonMonitor(this.model));
            makeLocations();
            makeWeekpatternData();
            makePersonTypes();
            makePersons();
            makeFamilies();
            infectPersons();
            makeResultWriter();
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
            System.exit(0);
        }
    }

    private void makeLocationTypes() throws Exception
    {
        this.model.getLocationTypeIndexMap().remove((byte) 0);
        this.model.getLocationTypeNameMap().remove("house");
        this.houseType = new LocationType(this.model, (byte) 0, "house", Location.class, HouseAnimation.class, true, true, 1.0,
                false, 0.25, 1.0);
        this.workType = new LocationType(this.model, (byte) 1, "work", Location.class, WorkplaceAnimation.class, true, true,
                1.0, false, 0.25, 1.0);
    }

    private void makeLocations() throws Exception
    {
        int numberPersons = this.model.getParameterValueInt("settings.NumberPersons");
        // make n houses numbered 0 to n-1, 100 m2 without sublocations.
        for (int i = 0; i < numberPersons; i++)
        {
            new Location(this.model, i, this.houseType, 4.0f + (1.0f * i) / 1000.0f, 52.0f, (short) 1, 100.0f);
        }

        // make m workplaces numbered 100_000 to 100_000 + m - 1, x m2 with y sublocations.
        int numberWorkplaces = this.model.getParameterValueInt("settings.NumberWorkplaces");
        short numberSublocations = (short) this.model.getParameterValueInt("settings.NumberSublocations");
        float workplaceSize = (float) this.model.getParameterValueDouble("settings.WorkplaceSize");
        for (int i = 0; i < numberWorkplaces; i++)
        {
            new Location(this.model, 100_000 + i, this.workType, 4.0f + (1.0f * i) / 1000.0f, 52.1f, numberSublocations,
                    workplaceSize);
        }

    }

    private void makeWeekpatternData() throws Exception
    {
        // work 10 hours, sleep 8 hours.
        Activity sleep1 = new FixedDurationActivity(this.model, "sleep1", new HomeLocator(), 8.0);
        Activity travel1 =
                new TravelActivityFly(this.model, "travel1", new CarLocator(), new CurrentLocator(), new WorkLocator());
        Activity work = new FixedDurationActivity(this.model, "work", new WorkLocator(), 10.0);
        Activity travel2 =
                new TravelActivityFly(this.model, "travel2", new CarLocator(), new CurrentLocator(), new HomeLocator());
        Activity sleep2 = new FixedDurationActivity(this.model, "sleep2", new HomeLocator(), 6.0);

        List<Activity> activityList = new ArrayList<>();
        activityList.add(sleep1);
        activityList.add(travel1);
        activityList.add(work);
        activityList.add(travel2);
        activityList.add(sleep2);
        DayPattern[] dayPatterns = new DayPattern[7];
        for (int dayOfWeek = 0; dayOfWeek < 7; dayOfWeek++)
        {
            dayPatterns[dayOfWeek] = new DayPattern(this.model, activityList);
        }
        new WeekDayPattern(this.model, "worker_work", dayPatterns);
        // patterns are stored automatically in the maps of the model }
    }

    /**
     * make the PersonTypes and register in the Model.
     */
    @SuppressWarnings("unchecked")
    private void makePersonTypes()
    {
        int nr = 1;
        for (Class<? extends Person> pc : new Class[] {Worker.class})
        {
            PersonType pt = new PersonType(this.model, nr, pc);
            this.model.getPersonTypeList().add(pt);
            this.model.getPersonTypeClassMap().put(pc, pt);
            nr++;
        }
    }

    private void makePersons() throws Exception
    {
        int numberPersons = this.model.getParameterValueInt("settings.NumberPersons");
        int numberWorkplaces = this.model.getParameterValueInt("settings.NumberWorkplaces");
        int personsPerWorkplace = (int) Math.ceil(numberPersons / numberWorkplaces);
        // make n workers.
        for (int id = 0; id < numberPersons; id++)
        {
            boolean genderFemale = this.model.getU01().draw() < 0.5;
            byte age = (byte) Math.round(21 + 45 * this.model.getU01().draw());
            int workplaceid = 100_000 + (int) Math.floor(id / personsPerWorkplace);
            int homeid = id;
            short weekPatternIndex = (short) this.model.getWeekPatternMap().get("worker_work").getId();
            IdxPerson person = new Worker(this.model, id, genderFemale, age, homeid, weekPatternIndex, workplaceid);

            person.setHomeSubLocationIndex((short) 0);
            person.setExposureTime(0.0f);
            person.setDiseasePhase(SEIRProgression.susceptible);
            SEIRProgression.susceptible.addPerson();
        }
    }

    private void makeFamilies()
    {
        TIntObjectMap<TIntSet> families = this.model.getFamilyMembersByHomeLocation();
        for (TIntObjectIterator<Person> it = this.model.getPersonMap().iterator(); it.hasNext();)
        {
            it.advance();
            Person person = it.value();
            TIntSet family = families.get(person.getHomeLocation().getId());
            if (family == null)
            {
                family = new TIntHashSet();
                families.put(person.getHomeLocation().getId(), family);
            }
            family.add(person.getId());
        }
    }

    /**
     * Infections based on the inputParameterMap.
     */
    private void infectPersons()
    {
        int numberToInfect = this.model.getParameterValueInt("settings.NumberInfected");
        int ageMin = 0;
        int ageMax = 100;
        List<Person> persons = new ArrayList<>(this.model.getPersonMap().valueCollection());
        int nrPersons = persons.size() - 1;
        while (numberToInfect > 0)
        {
            Person person = persons.get(this.model.getRandomStream().nextInt(0, nrPersons));
            if (person.getDiseasePhase().isSusceptible())
            {
                if (person.getAge() >= ageMin && person.getAge() <= ageMax)
                {
                    this.model.getDiseaseProgression().expose(person, SEIRProgression.exposed);
                    numberToInfect--;
                }
            }
        }
    }

    /**
     * Create the ResultWriter to write the output files.
     */
    private void makeResultWriter()
    {
        if (this.model.getParameterValueBoolean("generic.WriteOutput"))
        {
            String outputPath = this.model.getParameterValue("generic.OutputPath");
            new ResultWriter(this.model, outputPath);
        }
    }

    static class TravelActivityFly extends TravelActivity
    {
        private static final long serialVersionUID = 1L;

        public TravelActivityFly(final MedlabsModelInterface model, final String name, final LocatorInterface travelLocator,
                final LocatorInterface startLocator, final LocatorInterface endLocator)
        {
            super(model, name, travelLocator, startLocator, endLocator);
        }

        @Override
        protected double getDuration(final Person person, final Location startLocation, final Location endLocation)
        {
            return 1E-6;
        }

        @Override
        public double getDuration(final Person person)
        {
            return 1E-6;
        }

    }
}
