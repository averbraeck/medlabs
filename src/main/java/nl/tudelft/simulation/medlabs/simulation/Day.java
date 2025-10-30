package nl.tudelft.simulation.medlabs.simulation;

/**
 * Day prints the day of the week and has methods isWeekday() and isWeekend(). This class can be adapted for countries where the
 * weekend does not fall on a Saturday and Sunday with a setter method that indicates the day names, abbreviations, and weekend
 * days as flexible arguments. It can also be used to start the week e.g. on a Sunday. In line with the Global class, this class
 * is static, which means that if two simulations run in one JVM, the need to have the same week composition.
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
public class Day
{
    /** The abbreviations of the weekdays. Default: 0 = Monday. */
    private static String[] abbreviations = new String[] { "Mo", "Tu", "We", "Th", "Fr", "Sa", "Su" };

    /** The names of the weekdays. Default: 0 = Monday. */
    private static String[] names =
            new String[] { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" };

    /** The first weekend day. */
    private static byte weekendDay1 = 5;

    /** The second weekend day. */
    private static byte weekendDay2 = 6;

    /** Utility class. */
    private Day()
    {
        // Utility class
    }

    /**
     * Change the composition of the week.
     * @param newAbbreviations String[7]; the abbreviations of the weekdays
     * @param newNames String[7]; the names of the weekdays
     * @param newWeekendDay1 byte; the first weekend day
     * @param newWeekendDay2 byte; the second weekend day
     */
    public static void setWeekComposition(final String[] newAbbreviations, final String[] newNames, final byte newWeekendDay1,
            final byte newWeekendDay2)
    {
        abbreviations = newAbbreviations;
        names = newNames;
        weekendDay1 = newWeekendDay1;
        weekendDay2 = newWeekendDay2;
    }

    /**
     * Return the day abbreviation.
     * @param day byte; the day number
     * @return String; the abbreviation
     */
    public static String abbreviation(final byte day)
    {
        return abbreviations[day];
    }

    /**
     * Return the day name.
     * @param day byte; the day number
     * @return String; the name
     */
    public static String name(final byte day)
    {
        return names[day];
    }

    /**
     * Return whether the day number indicates a weekday.
     * @param day byte; the day number
     * @return boolean; whether the day number indicates a weekday
     */
    public static boolean isWeekday(final byte day)
    {
        return day != weekendDay1 && day != weekendDay2;
    }

    /**
     * Return whether the day number indicates a weekend.
     * @param day byte; the day number
     * @return boolean; whether the day number indicates a weekend
     */
    public static boolean isWeekend(final byte day)
    {
        return day == weekendDay1 || day == weekendDay2;
    }

}
