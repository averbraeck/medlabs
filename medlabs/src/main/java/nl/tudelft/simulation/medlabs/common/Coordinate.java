package nl.tudelft.simulation.medlabs.common;

/**
 * Coordinates in WGS84.
 * <p>
 * Copyright (c) 2014-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. The
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
public class Coordinate
{
    /** lat. */
    private final float latitude;

    /** lon. */
    private final float longitude;

    /** the radius of the earth in meters. ok for northern latitudes */
    private static final float EARTH_RADIUS_M = 6378137.0f;

    /**
     * @param latitude
     * @param longitude
     */
    public Coordinate(final float latitude, final float longitude)
    {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * @param d
     * @return radians of degree d
     */
    private static float rad(final float d)
    {
        return (float) (d * Math.PI / 180.0f);
    }

    /**
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return distance between 2 lat/lon coordinates
     */
    public static float distanceMprecise(final float lat1, final float lng1, final float lat2, final float lng2)
    {
        float radLat1 = rad(lat1);
        float radLat2 = rad(lat2);
        float a = radLat1 - radLat2;
        float b = rad(lng1) - rad(lng2);
        float s = 2.0f * (float) Math.asin(Math.sqrt(
                Math.pow(Math.sin(a / 2.0f), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2.0f), 2)));
        s = s * EARTH_RADIUS_M;
        return s;
    }

    /**
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return approximate distance between 2 lat/lon coordinates
     */
    public static float distanceM(final float lat1, final float lng1, final float lat2, final float lng2)
    {
        
        float dx = (lat2 - lat1);
        float dy = (lng2 - lng1) * (float) Math.cos(Math.toDegrees(lat1));
        float d =  (float) Math.sqrt(dx * dx + dy * dy) * 111319.24f;
        if (d > 50000)
        {
            System.out.println(d);
        }
        return d;
    }

    /**
     * @param coordinate other coordinate
     * @return distance in meters between this coordinate and the other coordinate
     */
    public float distanceM(final Coordinate coordinate)
    {
        return distanceM(this.latitude, this.longitude, coordinate.getLatitude(), coordinate.getLongitude());
    }

    /**
     * @return the latitude
     */
    public float getLatitude()
    {
        return this.latitude;
    }

    /**
     * @return the longitude
     */
    public float getLongitude()
    {
        return this.longitude;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return String.format("(%-1.4f,%-1.4f)", this.latitude, this.longitude);
    }

    /**
     * @param args
     */
    public static void main(final String[] args)
    {
        System.out.println(distanceMprecise(39.5f, 116f, 39.5f, 117f));
        System.out.println(distanceMprecise(39.5f, 116f, 40.5f, 116f));
        System.out.println();
        System.out.println(distanceM(39f, 116f, 39.1f, 115.8f));
        System.out.println(distanceMprecise(39f, 116f, 39.1f, 115.8f));
        System.out.println();
        System.out.println(distanceM(39f, 116f, 40.1f, 116.8f));
        System.out.println(distanceMprecise(39f, 116f, 40.1f, 116.8f));
        System.out.println();
        System.out.println(distanceM(39f, 116f, 39.01f, 116.02f));
        System.out.println(distanceMprecise(39f, 116f, 39.01f, 116.02f));
    }
}
