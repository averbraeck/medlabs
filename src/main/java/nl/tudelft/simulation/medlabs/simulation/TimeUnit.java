package nl.tudelft.simulation.medlabs.simulation;

/**
 * The TimeUnitInterface defines the simulator time units.
 * <p>
 * Copyright (c) 2014-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. The
 * MEDLABS project (Modeling Epidemic Disease with Large-scale Agent-Based Simulation) is aimed at providing policy analysis
 * tools to predict and help contain the spread of epidemics. It makes use of the DSOL simulation engine and the agent-based
 * modeling formalism. See for project information <a href="http://www.simulation.tudelft.nl/"> www.simulation.tudelft.nl</a>.
 * The original MEDLABS Java library was developed as part of the PhD research of Mingxin Zhang at TU Delft and is described in
 * the PhD thesis "Large-Scale Agent-Based Social Simulation" (2016). This software is licensed under the BSD license. See
 * license.txt in the main project.
 * </p>
 * @author Mingxin Zhang
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public enum TimeUnit
{
    /** UNIT reflects the non actual time related unit. */
    UNIT(1.0, "units", "u"),

    /** MILLISECOND reflects the MILLISECONDS. */
    MILLISECOND(1.0, "milliseconds", "ms"),

    /** SECOND reflects the SECOND. */
    SECOND(1000.0, "seconds", "s"),

    /** MINUTE reflects the MINUTE. */
    MINUTE(60000.0, "minutes", "m"),

    /** HOUR reflects the HOUR. */
    HOUR(60000.0 * 60.0, "hours", "h"),

    /** DAY reflects the DAY. */
    DAY(24.0 * 60000.0 * 60.0, "days", "d"),

    /** WEEK reflects the WEEK. */
    WEEK(7.0 * 24.0 * 60000.0 * 60.0, "weeks", "w"),

    /** YEAR reflects the YEAR. */
    YEAR(365.0 * 24.0 * 60000.0 * 60.0, "years", "y");

    /** conversion factor to msec. */
    private final double factor;

    /** name for reporting. */
    private final String name;

    /** abbreviation for reporting. */
    private final String abbreviation;

    /**
     * @param factor
     * @param name
     * @param abbreviation
     */
    TimeUnit(final double factor, final String name, final String abbreviation)
    {
        this.factor = factor;
        this.name = name;
        this.abbreviation = abbreviation;
    }

    /**
     * @return the factor
     */
    public double getFactor()
    {
        return this.factor;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * @return the abbreviation
     */
    public String getAbbreviation()
    {
        return this.abbreviation;
    }

    /**
     * Convert amount of time in given units to the target unit.
     * @param amount double; the amount to convert
     * @param unit TimeUnit; the unit of the amount
     * @param targetUnit TimeUnit; the unit to convert to
     * @return double; the amount in the target unit
     */
    public static double convert(final double amount, final TimeUnit unit, final TimeUnit targetUnit)
    {
        return unit.getFactor() * amount / targetUnit.getFactor();
    }

    /**
     * Convert amount of time in given units to hours.
     * @param amount double; the the amount of time units to convert to hours
     * @param unit TimeUnit; the unit of the amount 
     * @return double the amount in hours
     */
    public static double convert(final double amount, final TimeUnit unit)
    {
        return unit.getFactor() * amount / HOUR.factor;
    }

    // TODO: simulation time printing versus time-of-day printing
    
    /**
     * Output a time in hours as HH:MM.
     * @param hours double; the number of hours to format
     * @return String; output as HH:MM
     */
    public static String formatHHMM(final double hours)
    {
        int days = (int) Math.floor(hours / 24.0);
        double currentHour = hours - 24.0 * days;
        int h = (int) Math.floor(currentHour);
        int m = (int) (((long) Math.floor(hours * 60.0)) % 60);
        int s = (int) (((long) Math.floor(hours * 3600.0)) % 60);
        return String.format("%02d:%02d:%02d", h, m, s);
    }

    /**
     * Output a time in hours as HH:MM:SS.
     * @param hours double; the number of hours to format
     * @return String; output as HH:MM:SS
     */
    public static String formatHHHMSS(final double hours)
    {
        int h = (int) Math.floor(hours);
        int m = (int) (((long) Math.floor(hours * 60.0)) % 60);
        int s = (int) (((long) Math.floor(hours * 3600.0)) % 60);
        return String.format("%02d:%02d:%02d", h, m, s);
    }

}
