package nl.tudelft.simulation.medlabs.register;

import java.io.Serializable;

import nl.tudelft.simulation.medlabs.person.Person;

/**
 * A register is a file in which important records are kept. In this case records about past illness, vaccination and testing
 * (see https://www.quora.com/What-is-the-difference-between-record-registry-and-register for the naming of a 'register').
 * Several types of Registers are available in the software (and these can be extended further):
 * <ul>
 * <li>Register interface, containing the 'register(person)' method and the isRegistered(person) method.</li>
 * <li>PersonRegister class, persistently containing that a person has been registered. Registry is once. An example is a model
 * with a single illness and lifelong immunity, where registration of having had the disease is sufficient.</li>
 * <li>LastDateRegister class, persistently containing the date that a person has been registered. The last date is kept. An
 * example is an official test where the last day of the test is kept.</li>
 * <li>MultipleDateRegister class, persistently containing the dates that a person has been registered. Multiple dates can be
 * registered for the same person. An example is the registration of multiple vaccinations and booster shots.</li>
 * <li>TimeoutDateRegister class, containing the last date that a person has been registered, with a timeout on how long the
 * record is kept. An example is a self test that is valid for 2 days. After 2 days, the record is erased automatically.</li>
 * <li>LastDateTypeRegister class, persistently containing the last date that a person has been registered, together with the
 * type of registry. An example is storing the type of self test with the last test date.</li>
 * <li>MultipleDateTypeRegister class, persistently containing the dates that a person has been registered, together with the
 * type of registry. Multiple dates can be registered for the same person. An example is the registration of multiple
 * vaccinations and booster shots, where vaccinations can be of different types; or variants of a disease, where each variant
 * should be stored together with the date.</li>
 * </ul>
 * Note that different registers can be maintained. E.g., a separate register per vaccine, a separate register per type of
 * illness, and a separate register for each type of test that can be done. Or three combined registers for testing, for
 * illness, and for vaccinations.
 * <p>
 * Copyright (c) 2022-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. The
 * MEDLABS project (Modeling Epidemic Disease with Large-scale Agent-Based Simulation) is aimed at providing policy analysis
 * tools to predict and help contain the spread of epidemics. It makes use of the DSOL simulation engine and the agent-based
 * modeling formalism. See for project information <a href="http://www.simulation.tudelft.nl/"> www.simulation.tudelft.nl</a>.
 * The original MEDLABS Java library was developed as part of the PhD research of Mingxin Zhang at TU Delft and is described in
 * the PhD thesis "Large-Scale Agent-Based Social Simulation" (2016). This software is licensed under the BSD license. See
 * license.txt in the main project.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface RegisterInterface extends Serializable, Iterable<Person>
{
    /**
     * Register a person in the registry.
     * @param person Person; the person to register in the registry
     */
    default void register(final Person person)
    {
        register(person.getId());
    }

    /**
     * Register a person in the registry using the person id.
     * @param personId int; the id of the person to register in the registry
     */
    void register(int personId);

    /**
     * Check whether a person is registered in the registry.
     * @param person Person; the person to look up in the registry
     * @return boolean; whether the person is registered in the registry
     */
    default boolean isRegistered(final Person person)
    {
        return isRegistered(person.getId());
    }

    /**
     * Check whether a person is registered in the registry.
     * @param personId int; the id of the person to look up in the registry
     * @return boolean; whether the person is registered in the registry
     */
    boolean isRegistered(int personId);

    /**
     * Return the size of the register in terms of the number of registered persons.
     * @return int; the size of the register in terms of the number of registered persons
     */
    int numberOfRegisteredPersons();
}
