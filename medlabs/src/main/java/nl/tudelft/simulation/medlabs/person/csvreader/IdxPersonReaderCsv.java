package nl.tudelft.simulation.medlabs.person.csvreader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.siegmar.fastcsv.reader.NamedCsvReader;
import de.siegmar.fastcsv.reader.NamedCsvRow;
import nl.tudelft.simulation.medlabs.activity.pattern.WeekPattern;
import nl.tudelft.simulation.medlabs.common.MedlabsException;
import nl.tudelft.simulation.medlabs.model.AbstractMedlabsModel;
import nl.tudelft.simulation.medlabs.person.Person;
import nl.tudelft.simulation.medlabs.person.index.IdxPerson;
import nl.tudelft.simulation.medlabs.person.index.IdxStudent;
import nl.tudelft.simulation.medlabs.person.index.IdxWorkStudent;
import nl.tudelft.simulation.medlabs.person.index.IdxWorker;

/**
 * IdxPersonReaderCsv is a default CSV reader for persons. The reader makes persons using indexes. This reader can be extended.
 * The default field headings that are read are:
 * <ul>
 * <li><b>person_id</b>. The number by which the person can be retrieved. Integer value. Not necessarily sequential.</li>
 * <li><b>household_id</b>. The household number to place family members in the same sublocation of the home_id.</li>
 * <li><b>age</b>. Coded as an integer value.</li>
 * <li><b>gender</b>. M or F.</li>
 * <li><b>home_id</b>. The id of the home location in the Locations map.</li>
 * <li><b>workplace_id</b>. The id of the work location in the Locations map. If this value is filled, the person is a
 * Worker.</li>
 * <li><b>school_id</b>. The id of the school location in the Locations map. If this value is filled, the person is a Student.
 * Note that when both workplace_id and school_id are filled, the person becomes a WorkStudent.</li>
 * <li><b>activity_pattern_name</b>. The default and current activity pattern of the person.</li>
 * </ul>
 * <p>
 * Copyright (c) 2022-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. The
 * MEDLABS project (Modeling Epidemic Disease with Large-scale Agent-Based Simulation) is aimed at providing policy analysis
 * tools to predict and help contain the spread of epidemics. It makes use of the DSOL simulation engine and the agent-based
 * modeling formalism. See for project information <a href="http://www.simulation.tudelft.nl/"> www.simulation.tudelft.nl</a>.
 * The original MEDLABS Java library was developed as part of the PhD research of Mingxin Zhang at TU Delft and is described in
 * the PhD thesis "Large-Scale Agent-Based Social Simulation" (2016). This software is licensed under the BSD license. See
 * license.txt in the main project.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class IdxPersonReaderCsv
{
    /** the model into which the persons need to be read. */
    private final AbstractMedlabsModel model;

    /** the field separator character, default a comma. */
    private char fieldSeparator = ',';

    /** the quote character, default a double quote. */
    private char quoteCharacter = '"';

    /** map to allocate households to the right sublocation. The map maps homeId via householdId to sublocationIndex. */
    private Map<Integer, Map<Integer, Short>> householdMap = new HashMap<>();

    /**
     * Construct a default CSV reader for persons.
     * @param model AbstractMedlabsModel; the model into which the persons need to be read
     */
    public IdxPersonReaderCsv(final AbstractMedlabsModel model)
    {
        super();
        this.model = model;
    }

    /**
     * Read the persons from an input stream that contains the data. This can be, for instance:
     * 
     * <pre>
     *   new IdxPersonReader(model).read(new FileInputStream(path)); or
     *   IdxPersonReader reader = new IdxPersonReader(model).setFieldSeparator("\t");
     *   reader.read(new GZIPInputStream(new FileInputStream(path)));
     * </pre>
     * 
     * The instantiation of the person is, together with the other data in the row of the csv-file, delegated to a separate
     * method called instantiatePerson(...);
     * @param stream InputStream; the data stream from which to read the data
     * @throws MedlabsException when the headers are not correct
     */
    public void read(final InputStream stream) throws MedlabsException
    {
        Reader reader = new InputStreamReader(stream);
        NamedCsvReader csvReader =
                NamedCsvReader.builder().fieldSeparator(this.fieldSeparator).quoteCharacter(this.quoteCharacter).build(reader);

        Set<String> header = csvReader.getHeader();
        if (!header.contains("person_id") || !header.contains("household_id") || !header.contains("age")
                || !header.contains("gender") || !header.contains("home_id") || !header.contains("workplace_id")
                || !header.contains("school_id") || !header.contains("activity_pattern_name"))
        {
            throw new MedlabsException("Person csv-file header row did not contain all column headers\n" + header.toString());
        }

        Iterator<NamedCsvRow> it = csvReader.iterator();
        while (it.hasNext())
        {
            NamedCsvRow row = it.next();

            int personId = parseInt(row, "person_id", false);
            if (personId == -1)
            {
                continue;
            }

            int householdId = parseInt(row, "household_id", false);
            if (householdId == -1)
            {
                continue;
            }

            byte age = (byte) parseInt(row, "age", false);
            if (age < 0 || age > 120)
            {
                System.err.println("Person " + personId + " has age " + age + " on row " + row.getOriginalLineNumber() + "\n"
                        + row.toString());
                continue;
            }

            boolean genderFemale = row.getField("gender").toUpperCase().startsWith("F");

            int homeLocationId = parseInt(row, "home_id", false);
            if (!this.model.getLocationMap().containsKey(homeLocationId))
            {
                System.err.println("homeId " + homeLocationId + " not found in the location map on row "
                        + row.getOriginalLineNumber() + "\n" + row.toString());
                continue;
            }

            int workplaceId = parseInt(row, "workplace_id", true);
            if (workplaceId >= 0)
            {
                if (!this.model.getLocationMap().containsKey(workplaceId))
                {
                    System.err.println("workplaceId " + workplaceId + " not found in the location map on row "
                            + row.getOriginalLineNumber() + "\n" + row.toString());
                    continue;
                }
            }

            int schoolId = parseInt(row, "school_id", true);
            if (schoolId >= 0)
            {
                if (!this.model.getLocationMap().containsKey(schoolId))
                {
                    System.err.println("schoolId " + schoolId + " not found in the location map on row "
                            + row.getOriginalLineNumber() + "\n" + row.toString());
                    continue;
                }
            }

            String activityPatternName = row.getField("activity_pattern_name");
            if (!this.model.getWeekPatternMap().containsKey(activityPatternName))
            {
                System.err.println("activityPatternName " + activityPatternName + " not found in the week pattern map on row "
                        + row.getOriginalLineNumber() + "\n" + row.toString());
                continue;
            }
            WeekPattern weekPattern = this.model.getWeekPatternMap().get(activityPatternName);

            // create sublocationIndex for the home
            short homeSubLocationIndex;
            Map<Integer, Short> householdSublocationMap = this.householdMap.get(homeLocationId);
            if (householdSublocationMap == null)
            {
                householdSublocationMap = new HashMap<>();
                this.householdMap.put(homeLocationId, householdSublocationMap);
            }
            if (householdSublocationMap.containsKey(householdId))
            {
                homeSubLocationIndex = householdSublocationMap.get(householdId);
            }
            else
            {
                homeSubLocationIndex = (short) householdSublocationMap.size();
                if (homeSubLocationIndex + 1 > this.model.getLocationMap().get(homeLocationId).getNumberOfSubLocations())
                {
                    System.err.println("Person " + personId + ". The homeId " + homeLocationId + " with householdId "
                            + householdId + " has more sublocations (" + (homeSubLocationIndex + 1) + ") than defined. Record"
                            + " on row " + row.getOriginalLineNumber() + "\n" + row.toString());
                    continue;
                }
                householdSublocationMap.put(householdId, homeSubLocationIndex);
            }

            // delegate the actual instantiation to a separate method that can easily be overridden
            instantiatePerson(row, personId, genderFemale, age, homeLocationId, homeSubLocationIndex,
                    (short) weekPattern.getId(), workplaceId, schoolId);
        }
    }

    /**
     * Instantiate a person using the parsed fields. This method can be overridden to parse other fields in addition to the 
     * default fields. In that case, override this method.
     * @param row NamedCsvRow; the row with potential extra fields
     * @param personId int; unique id number of the person in the Model.getPersons() array
     * @param genderFemale boolean; whether gender is female or not.
     * @param age byte; the age of the person
     * @param homeLocationId int; the location of the home in the list of house locations
     * @param homeSubLocationIndex short; the family sublocation in the home location
     * @param weekPatternIndex short; the index of the standard week pattern for the person; this is also the initial week
     *            pattern that the person will use
     * @param workplaceId int; the location index of the work location, relative to the workTypeIndex
     * @param schoolId int; the location index of the school, relative to the schoolTypeIndex
     */
    @SuppressWarnings("checkstyle:parameternumber")
    protected void instantiatePerson(final NamedCsvRow row, final int personId, final boolean genderFemale, final byte age,
            final int homeLocationId, final short homeSubLocationIndex, final short weekPatternIndex, final int workplaceId,
            final int schoolId)
    {
        // create the Person; the Person constructor will register the person in the model maps.
        Person person = null;
        if (schoolId < 0 && workplaceId < 0)
        {
            person = new IdxPerson(this.model, personId, genderFemale, age, homeLocationId, weekPatternIndex);
        }
        else if (schoolId < 0 && workplaceId >= 0)
        {
            person = new IdxWorker(this.model, personId, genderFemale, age, homeLocationId, weekPatternIndex, workplaceId);
        }
        else if (schoolId >= 0 && workplaceId < 0)
        {
            person = new IdxStudent(this.model, personId, genderFemale, age, homeLocationId, weekPatternIndex, schoolId);
        }
        else
        {
            person = new IdxWorkStudent(this.model, personId, genderFemale, age, homeLocationId, weekPatternIndex, workplaceId,
                    schoolId);
        }
        person.setHomeSubLocationIndex(homeSubLocationIndex);
        person.setCurrentSubLocationIndex(homeSubLocationIndex);
    }

    /**
     * Parse a field of the row from the csv-file. When optional == false, give an error when the field is empty.
     * @param row NamedCsvRow; the row with values
     * @param fieldName String; the header name
     * @param optional boolean; whether to give an error if the field is missing
     * @return the parsed integer value or -1 on error or when the field is empty
     */
    protected int parseInt(final NamedCsvRow row, final String fieldName, final boolean optional)
    {
        String s = row.getField(fieldName).trim();
        if (s.length() == 0)
        {
            if (!optional)
            {
                System.err.println(
                        "Field " + fieldName + " has no value on row " + row.getOriginalLineNumber() + "\n" + row.toString());
            }
            return -1;
        }
        try
        {
            return Integer.parseInt(s);
        }
        catch (NumberFormatException nfe)
        {
            System.err.println("Field " + fieldName + " did not contain a proper numeric value on row "
                    + row.getOriginalLineNumber() + "\n" + row.toString());
            return -1;
        }
    }

    /**
     * Set the field separator (default it is a comma).
     * @param newFieldSeparator char; the field separator to use
     * @return IdxPersonReaderCsv for method chaining
     */
    public IdxPersonReaderCsv setFieldSeparator(final char newFieldSeparator)
    {
        this.fieldSeparator = newFieldSeparator;
        return this;
    }

    /**
     * Set the quote character (default it is a double quote).
     * @param newQuoteCharacter char; the quote character to use
     * @return IdxPersonReaderCsv for method chaining
     */
    public IdxPersonReaderCsv setQuoteCharacter(final char newQuoteCharacter)
    {
        this.quoteCharacter = newQuoteCharacter;
        return this;
    }

}
