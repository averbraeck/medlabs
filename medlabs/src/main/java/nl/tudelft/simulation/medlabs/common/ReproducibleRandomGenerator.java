package nl.tudelft.simulation.medlabs.common;

import nl.tudelft.simulation.jstats.streams.MersenneTwister;
import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * The ReproducibleRandomGenerator class implements a RNG implementation where we can <b>reproduce</b> a random draw based on a
 * value that we provide, such as a personId. Still, between replications or runs with a different seed for the RNG, the draws
 * for that same person would be different, yet reproducible.
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
public class ReproducibleRandomGenerator extends MersenneTwister implements StreamInterface
{
    /** */
    private static final long serialVersionUID = 20140831L;

    /** the seed value of the reproducible RNG. */
    private final long reproducibleSeed;

    /** the reproducible RNG. */
    private final StreamInterface reproducibleRNG;

    /**
     * creates a new ReproducibleRandomGenerator and in initializes with a given seed.
     * @param seed long; the seed to use.
     */
    public ReproducibleRandomGenerator(final long seed)
    {
        super(seed);
        this.reproducibleRNG = new MersenneTwister(seed + 1L);
        // a totally different seed to use as the basis.
        this.reproducibleSeed = this.reproducibleRNG.nextLong();
    }

    /**
     * Return the reproducibleSeed of the reproducible RNG.
     * @return long; the reproducibleSeed of the reproducible RNG
     */
    public long getReproducibleSeed()
    {
        return reproducibleSeed;
    }

    /**
     * Method returns (pseudo)random number from the stream over the integers i and j .
     * @param i int; the minimal value
     * @param j int; the maximum value
     * @param reproducibleValue long; the value to use to make the result reproducible
     * @return next int between i and j
     */
    public int nextInt(final int i, final int j, final long reproducibleValue)
    {
        this.reproducibleRNG.setSeed(this.reproducibleSeed + reproducibleValue);
        return i + (int) Math.floor((j - i + 1) * this.reproducibleRNG.nextDouble());
    }

    /**
     * Returns the next pseudorandom, uniformly distributed int value from this random number generator's sequence. The general
     * contract of nextInt is that one int value is pseudorandomly generated and returned. All 2<sup>32</sup> possible int
     * values are produced with (approximately) equal probability.
     * @param reproducibleValue long; the value to use to make the result reproducible
     * @return the next pseudorandom, uniformly distributed int value from this random number generator's sequence
     */
    public int nextInt(final long reproducibleValue)
    {
        this.reproducibleRNG.setSeed(this.reproducibleSeed + reproducibleValue);
        return this.reproducibleRNG.nextInt();
    }

    /**
     * Returns the next pseudorandom, uniformly distributed long value from this random number generator's sequence. The general
     * contract of nextLong is that one long value is pseudorandomly generated and returned.
     * @param reproducibleValue long; the value to use to make the result reproducible
     * @return the next pseudorandom, uniformly distributed long value from this random number generator's sequence
     */
    public long nextLong(final long reproducibleValue)
    {
        this.reproducibleRNG.setSeed(this.reproducibleSeed + reproducibleValue);
        return this.reproducibleRNG.nextLong();
    }

    /**
     * Returns the next pseudorandom, uniformly distributed boolean value from this random number generator's sequence.
     * @param reproducibleValue long; the value to use to make the result reproducible
     * @return the next pseudorandom, uniformly distributed boolean value from this random number generator's sequence
     */
    public boolean nextBoolean(final long reproducibleValue)
    {
        this.reproducibleRNG.setSeed(this.reproducibleSeed + reproducibleValue);
        return this.reproducibleRNG.nextBoolean();
    }

    /**
     * Returns the next pseudorandom, uniformly distributed float value between 0.0 and 1.0 from this random number generator's
     * sequence.
     * @param reproducibleValue long; the value to use to make the result reproducible
     * @return the next pseudorandom, uniformly distributed float value between 0.0 and 1.0 from this random number generator's
     *         sequence
     */
    public float nextFloat(final long reproducibleValue)
    {
        this.reproducibleRNG.setSeed(this.reproducibleSeed + reproducibleValue);
        return this.reproducibleRNG.nextFloat();
    }

    /**
     * Returns the next pseudorandom, uniformly distributed double value between 0.0 and 1.0 from this random number generator's
     * sequence.
     * @param reproducibleValue long; the value to use to make the result reproducible
     * @return the next pseudorandom, uniformly distributed double value between 0.0 and 1.0 from this random number generator's
     *         sequence
     */
    public double nextDouble(final long reproducibleValue)
    {
        this.reproducibleRNG.setSeed(this.reproducibleSeed + reproducibleValue);
        return this.reproducibleRNG.nextDouble();
    }

    /**
     * select item using Zipf's law.
     * @param size of ranked array
     * @return index in [0, array size - 1]
     */
    public int nextPowerlaw(final int size)
    {
        // make array of numbers
        double[] nums = new double[size];
        for (int i = 0; i < nums.length; i++)
        {
            nums[i] = i + 1;
        }

        // make array of probabilities
        double[] probs = new double[nums.length];
        for (int i = 0; i < probs.length; i++)
        {
            probs[i] = (nums[i] == 0) ? 0 : Math.pow(nums[i], -1.2);
        }

        // sum probabilities
        double sum = 0;
        for (int i = 0; i < probs.length; i++)
        {
            sum += probs[i];
        }

        // obtain random number in range [0, sum]
        double r = sum * this.nextDouble();

        // subtract probs until result negative
        // no of iterations gives required index
        int i;
        for (i = 0; i < probs.length; i++)
        {
            r -= probs[i];
            if (r < 0)
            {
                break;
            }
        }
        return i;
    }

    /**
     * select item using Zipf's law.
     * @param size of ranked array
     * @param reproducibleValue long; the value to use to make the result reproducible
     * @return index in [0, array size - 1]
     */
    public int nextPowerlaw(final int size, final long reproducibleValue)
    {
        // make array of numbers
        double[] nums = new double[size];
        for (int i = 0; i < nums.length; i++)
        {
            nums[i] = i + 1;
        }

        // make array of probabilities
        double[] probs = new double[nums.length];
        for (int i = 0; i < probs.length; i++)
        {
            probs[i] = (nums[i] == 0) ? 0 : Math.pow(nums[i], -1.2);
        }

        // sum probabilities
        double sum = 0;
        for (int i = 0; i < probs.length; i++)
        {
            sum += probs[i];
        }

        // obtain random number in range [0, sum]
        this.reproducibleRNG.setSeed(this.reproducibleSeed + reproducibleValue);
        double r = sum * this.reproducibleRNG.nextDouble();

        // subtract probs until result negative
        // no of iterations gives required index
        int i;
        for (i = 0; i < probs.length; i++)
        {
            r -= probs[i];
            if (r < 0)
            {
                break;
            }
        }
        return i;
    }

    /**
     * @param args
     */
    public static void main(final String[] args)
    {
        for (long seed = 1L; seed <= 5L; seed++)
        {
            ReproducibleRandomGenerator r = new ReproducibleRandomGenerator(seed);
            System.out.println("\nSEED = : " + seed);

            System.out.print("Random power law between 0 and 100: ");
            for (int i = 0; i < 10; i++)
            {
                System.out.print(r.nextPowerlaw(100) + "  ");
            }
            System.out.print("\nRandom power law between 0 and 100 with hashcode of 100: ");
            for (int i = 0; i < 10; i++)
            {
                System.out.print(r.nextPowerlaw(100, 500) + "  ");
            }
            System.out.print("\nRandom power law between 0 and 100 with hashcode of 100-120: ");
            for (int i = 0; i < 20; i++)
            {
                System.out.print(r.nextPowerlaw(100, 100 + i) + "  ");
            }

            System.out.print("\nRandom numbers between 1 and 10: ");
            for (int i = 0; i < 10; i++)
            {
                System.out.print(r.nextInt(1, 10) + "  ");
            }
            System.out.print("\nRandom numbers between 1 and 10 with hashcode of 200: ");
            for (int i = 0; i < 10; i++)
            {
                System.out.print(r.nextInt(1, 10, 200) + "  ");
            }
            System.out.print("\nRandom numbers between 1 and 10 with hashcode of 200-220: ");
            for (int i = 0; i < 20; i++)
            {
                System.out.print(r.nextInt(1, 10, 200 + i) + "  ");
            }
            System.out.print("\nAgain 10 random numbers between 1 and 10: ");
            for (int i = 0; i < 10; i++)
            {
                System.out.print(r.nextInt(1, 10) + "  ");
            }
            System.out.print("\nRandom numbers between 1 and 10 with hashcode of String('abc'): ");
            for (int i = 0; i < 10; i++)
            {
                System.out.print(r.nextInt(1, 10, new String("abc").hashCode()) + "  ");
            }
            System.out.println();
        }
    }

}
