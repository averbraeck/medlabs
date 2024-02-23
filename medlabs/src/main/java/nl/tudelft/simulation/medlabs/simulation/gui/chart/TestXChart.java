package nl.tudelft.simulation.medlabs.simulation.gui.chart;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;

import nl.tudelft.simulation.dsol.swing.gui.TablePanel;

/**
 * TestXChart.java.
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
public class TestXChart
{

    public TestXChart()
    {

    }

    /**
     * @param args
     * @throws RemoteException
     * @throws InterruptedException
     */
    public static void main(final String[] args) throws RemoteException, InterruptedException
    {
        TablePanel charts = new TablePanel(2, 2);

        List<String> binLabels = new ArrayList<>();
        binLabels.addAll(Arrays.asList("A", "B", "C", "D", "E"));
        HistogramString hs = new HistogramString("Histogram for Strings", "category", "#", binLabels, false);
        charts.setCell(hs.getSwingPanel(), 0, 0);

        javax.swing.SwingUtilities.invokeLater(new Runnable()
        {

            @Override
            public void run()
            {
                JFrame frame = new JFrame("TestXChart");
                frame.setPreferredSize(new Dimension(1920, 1080));
                frame.setLayout(new BorderLayout());
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(charts, BorderLayout.CENTER);
                frame.pack();
                frame.setVisible(true);
            }
        });

        hs.setCount("A", 10);
        hs.setCount("B", 15);
        hs.setCount("C", 17);
        hs.setCount("D", 11);
        hs.setCount("E", 3);
        Random random = new Random();
        while (true)
        {
            Thread.sleep(100);
            String cat = "" + (char) ('A' + random.nextInt(5));
            if (random.nextDouble() < 0.5)
                hs.addCount(cat, 1);
            else
                hs.addCount(cat, -1);
        }
    }

}
