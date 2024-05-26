package nl.tudelft.simulation.medlabs.model;

/**
 * TestGrid.java.
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
public class TestGrid
{
    private float getGridSizeM()
    {
        return 100f;
    }

    private float getLonCenter()
    {
        return 4.3f;
    }

    private float getLatCenter()
    {
        return 52.06f;
    }

    private float lonToM(final float lon)
    {
        return (float) ((lon - getLonCenter()) * 40075.0 * Math.cos(Math.toRadians(getLatCenter())) / 0.36);
    }

    private float latToM(final float lat)
    {
        return 111320.0f * (lat - getLatCenter());
    }

    private int lonToGridX(final float lon)
    {
        return (int) Math.round(lonToM(lon) / getGridSizeM());
    }

    private int latToGridY(final float lat)
    {
        return (int) Math.round(latToM(lat) / getGridSizeM());
    }

    /**
     * 
     */
    public TestGrid()
    {
        System.out.println("lonToM(4.35210 should be about +3700: " + lonToM(4.35210f));
        System.out.println("latToM(52.0321 should be about -3000: " + latToM(52.0321f));
        System.out.println("gridX(4.35210 should be about +37: " + lonToGridX(4.35210f));
        System.out.println("gridY(52.0321 should be about -30: " + latToGridY(52.0321f));
    }

    public static void main(final String[] args)
    {
        new TestGrid();
    }
}
