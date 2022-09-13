package nl.tudelft.simulation.medlabs.model;

import java.util.List;
import java.util.Map;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.set.TIntSet;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterException;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterMap;
import nl.tudelft.simulation.jstats.distributions.DistUniform;
import nl.tudelft.simulation.jstats.streams.StreamInterface;
import nl.tudelft.simulation.medlabs.activity.pattern.WeekPattern;
import nl.tudelft.simulation.medlabs.common.MedlabsRuntimeException;
import nl.tudelft.simulation.medlabs.common.ReproducibleRandomGenerator;
import nl.tudelft.simulation.medlabs.disease.DiseaseMonitor;
import nl.tudelft.simulation.medlabs.disease.DiseaseProgression;
import nl.tudelft.simulation.medlabs.disease.DiseaseTransmission;
import nl.tudelft.simulation.medlabs.location.Location;
import nl.tudelft.simulation.medlabs.location.LocationType;
import nl.tudelft.simulation.medlabs.person.Person;
import nl.tudelft.simulation.medlabs.person.PersonMonitor;
import nl.tudelft.simulation.medlabs.person.PersonType;
import nl.tudelft.simulation.medlabs.policy.Policy;
import nl.tudelft.simulation.medlabs.properties.Properties;
import nl.tudelft.simulation.medlabs.simulation.SimpleDEVSSimulatorInterface;
import nl.tudelft.simulation.medlabs.simulation.SimpleModelInterface;

/**
 * MedlabsModelInterface contains the calls to generic objects that belong to a disease model. Notable examples are:
 * <ul>
 * <li>Simulator</li>
 * <li>Experiment</li>
 * <li>InputParameters</li>
 * <li>Disease(s) in the model</li>
 * <li>WeekPatterns</li>
 * <li>Persons</li>
 * <li>Families</li>
 * <li>LocationTypes</li>
 * <li>Animation on/off</li>
 * <li>Lat/Lon of the location for quick x/y scaling</li>
 * </ul>
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
public interface MedlabsModelInterface extends SimpleModelInterface
{
    /** @return the simulator for the model */
    @Override
    SimpleDEVSSimulatorInterface getSimulator();

    /** @return whether the simulation is interactive or a batch run */
    boolean isInteractive();

    /** @param interactive boolean; whether the simulation is interactive or a batch run. */
    void setInteractive(boolean interactive);

    /** @return the input parameter map to be accessible by all model components. */
    @Override
    InputParameterMap getInputParameterMap();

    /** @return the standard random stream of the model. */
    StreamInterface getRandomStream();

    /** @return the standard uniform distribution based on the standard random stream of the model. */
    DistUniform getU01();

    /** @return the standard reproducible Java random stream in the model. */
    ReproducibleRandomGenerator getReproducibleJava2Random();

    /** @return the persons in the model, based on their original id. */
    TIntObjectMap<Person> getPersonMap();

    /** @return the person types by id. */
    List<PersonType> getPersonTypeList();

    /** @return the map of the person class name to the person type. */
    Map<Class<? extends Person>, PersonType> getPersonTypeClassMap();

    /** @return the family compositions in the model (array of person ids), indexed by home location. */
    TIntObjectMap<TIntSet> getFamilyMembersByHomeLocation();

    /** @return the map of person properties. */
    Properties getPersonProperties();

    /** @return the map of all location types by name. */
    Map<String, LocationType> getLocationTypeNameMap();

    /** @return the List of all location types by index. */
    Map<Byte, LocationType> getLocationTypeIndexMap();

    /** @return a map of all locations in the model, based on their original id. */
    TIntObjectMap<Location> getLocationMap();

    /** @return the map of week patterns to use elsewhere in the model. */
    Map<String, WeekPattern> getWeekPatternMap();

    /** @return the list of week patterns. */
    List<WeekPattern> getWeekPatternList();

    /**
     * Method to be implemented in the model to check whether the (week) activity pattern needs to be changed for a person that
     * changed his/her disease phase. This method is always called at midnight.
     * @param person Person; the person whose disease phase has changed
     */
    void checkChangeActivityPattern(Person person);

    // /** @return the graph for this network of gridstops and public transport stops. */
    // DefaultDirectedWeightedGraph<Stop, PublicTransportEdge> getDirectedGraph();
    // initialize as: new DefaultDirectedWeightedGraph<Stop, PublicTransportEdge>(PublicTransportEdge.class);

    // /** @return the map of ActivityGroups. */
    // TIntObjectMap<ActivityGroup> getActivityGroupMap();

    /**
     * Se the person monitor to report changes for statistics.
     * @param personMonitor PersonMonitor; the person monitor to report changes for statistics
     */
    void setPersonMonitor(PersonMonitor personMonitor);

    /** @return the person monitor to report changes for statistics */
    PersonMonitor getPersonMonitor();

    /**
     * Return the day of the week where 0 is Monday and 6 is Sunday.
     * @return int; the day of the week; 0-6 = Mon-Sun
     */
    default int getWeekday()
    {
        return ((int) Math.floor(getSimulator().getSimulatorTime() / 24)) % 7;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // Disease
    ///////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Set the disease progression model.
     * @param diseaseProgression DiseaseProgression; the disease progression model.
     */
    void setDiseaseProgression(DiseaseProgression diseaseProgression);

    /** @return the disease progression in this model. */
    DiseaseProgression getDiseaseProgression();

    /**
     * Set the disease transmission model.
     * @param diseaseTransmission DiseaseTransmission; the disease transmission model.
     */
    void setDiseaseTransmission(DiseaseTransmission diseaseTransmission);

    /** @return the disease transmission in this model. */
    DiseaseTransmission getDiseaseTransmission();

    /**
     * Set the disease monitor to report changes for statistics.
     * @param diseaseMonitor DiseaseMonitor; the disease monitor to report changes for statistics
     */
    void setDiseaseMonitor(DiseaseMonitor diseaseMonitor);

    /** @return the disease monitor to report changes for statistics */
    DiseaseMonitor getDiseaseMonitor();

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // Policies
    ///////////////////////////////////////////////////////////////////////////////////////////////////

    /** @return the map of all policies in the model. */
    Map<String, Policy> getAllPolicies();

    /** @return the map of the active policies in the model. */
    Map<String, Policy> getActivePolicies();

    /** @param policy Policy; the policy to add. */
    default void addPolicy(final Policy policy)
    {
        getAllPolicies().put(policy.getName(), policy);
    }

    /** @param policy Policy; the policy to remove. */
    default void removePolicy(final Policy policy)
    {
        getAllPolicies().remove(policy.getName());
    }

    /** @param policy Policy; the policy to activate. */
    default void activatePolicy(final Policy policy)
    {
        getActivePolicies().put(policy.getName(), policy);
    }

    /** @param policy Policy; the policy to deactivate. */
    default void deactivatePolicy(final Policy policy)
    {
        getActivePolicies().remove(policy.getName());
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // Some locations and location types that have to be defined in each model (even if not used)
    // and that should be cached for quick lookup
    ///////////////////////////////////////////////////////////////////////////////////////////////////

    /** @return the location type of a house or residence. */
    LocationType getLocationTypeHouse();

    // /** @return the location type of the a grid stop in the public transport network. */
    // LocationType getLocationTypeGridStop();

    /** @return the location of the infinitely large walk area. */
    Location getLocationWalk();

    /** @return the location of the infinitely large bike area. */
    Location getLocationBike();

    /** @return the location of the infinitely large car area. */
    Location getLocationCar();

    /* @return the properties file to use. */
    String getPropertyFilename();

    /**
     * Return a random number between 0 and maxMinus1.
     * @param maxMinus1 zero-based for length
     * @return random number between 0 and maxMinus1
     */
    default int randomUniform(final int maxMinus1)
    {
        return (int) Math.floor(maxMinus1 * getU01().draw());
    }

    /**
     * Return a double value for a parameter.
     * @param key String the key to get the value for
     * @return double; the double value for the "key" parameter
     */
    default double getParameterValueDouble(final String key)
    {
        try
        {
            return ((double) getInputParameterMap().get(key).getValue());
        }
        catch (InputParameterException pe)
        {
            throw new MedlabsRuntimeException(pe);
        }
    }

    /**
     * Return a long value for a parameter.
     * @param key String the key to get the value for
     * @return long; the long value for the "key" parameter
     */
    default long getParameterValueLong(final String key)
    {
        try
        {
            return ((long) getInputParameterMap().get(key).getValue());
        }
        catch (InputParameterException pe)
        {
            throw new MedlabsRuntimeException(pe);
        }
    }

    /**
     * Return a int value for a parameter.
     * @param key String the key to get the value for
     * @return int; the int value for the "key" parameter
     */
    default int getParameterValueInt(final String key)
    {
        try
        {
            return ((int) getInputParameterMap().get(key).getValue());
        }
        catch (InputParameterException pe)
        {
            throw new MedlabsRuntimeException(pe);
        }
    }

    /**
     * Return a boolean value for a parameter.
     * @param key String the key to get the value for
     * @return boolean; the boolean value for the "key" parameter
     */
    default boolean getParameterValueBoolean(final String key)
    {
        try
        {
            return ((boolean) getInputParameterMap().get(key).getValue());
        }
        catch (InputParameterException pe)
        {
            throw new MedlabsRuntimeException(pe);
        }
    }

    /**
     * Return a String value for a parameter.
     * @param key String the key to get the value for
     * @return String; the String value for the "key" parameter
     */
    default String getParameterValue(final String key)
    {
        try
        {
            return getInputParameterMap().get(key).getValue().toString();
        }
        catch (InputParameterException pe)
        {
            throw new MedlabsRuntimeException(pe);
        }
    }

}
