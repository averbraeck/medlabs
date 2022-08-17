package nl.tudelft.simulation.medlabs.simulation.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.rmi.RemoteException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import org.djutils.draw.bounds.Bounds2d;
import org.djutils.exceptions.Throw;
import org.djutils.logger.CategoryLogger;

import nl.tudelft.simulation.dsol.animation.Locatable;
import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;
import nl.tudelft.simulation.dsol.swing.gui.animation.DSOLAnimationGisTab;
import nl.tudelft.simulation.language.DSOLException;

/**
 * MedlabsAnimationTab.java.
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
public class MedlabsAnimationTab extends DSOLAnimationGisTab
{
    /** */
    private static final long serialVersionUID = 20211231L;

    /** Map of visibility layer names (simpleClassName) + "_" + displayName) to toggle buttons. */
    private Map<Class<? extends Locatable>, Map<String, JToggleButton>> toggleNamedButtons = new LinkedHashMap<>();

    /** Map of class name to all display strings for that class. */
    private Map<Class<? extends Locatable>, Set<String>> namedVisibilityMap = new LinkedHashMap<>();

    /**
     * Construct a tab with an AnimationPane for the animation of a DSOLModel, including GIS layers.
     * @param homeExtent Bounds2d; initial extent of the animation
     * @param simulator SimulatorInterface; the simulator
     * @throws RemoteException when notification of the animation panel fails
     * @throws DSOLException when simulator does not implement the AnimatorInterface
     */
    public MedlabsAnimationTab(final Bounds2d homeExtent, final SimulatorInterface<?, ?, ?> simulator)
            throws RemoteException, DSOLException
    {
        super(simulator, new MedlabsAnimationPanel(homeExtent, simulator));
    }

    /**
     * Add a series of buttons for a class for toggling animation based on a String evaluation.
     * @param nameArray String[]; the names of the buttons
     * @param locatableClass Class&lt;? extends Locatable&gt;; the class for which the button holds (e.g., Person.class)
     * @param visibilityEvaluator VisibilityEvaluator&lt;?&gt;; the evaluator
     * @param toolTipTextArray String[]; the tool tip texts to show when hovering over the button
     * @param initiallyVisibleArray boolean[]; whether the classes are initially shown or not
     */
    public <T extends Locatable> void addToggleAnimationStringText(final String[] nameArray, final Class<T> locatableClass,
            final VisibilityEvaluator<T> visibilityEvaluator, final String[] toolTipTextArray,
            final boolean[] initiallyVisibleArray)
    {
        Throw.when(nameArray.length != toolTipTextArray.length, IllegalArgumentException.class,
                "nameArray.length != toolTipArray.length");
        Throw.when(nameArray.length != initiallyVisibleArray.length, IllegalArgumentException.class,
                "nameArray.length != initiallyVisibleArray.length");
        this.namedVisibilityMap.put(locatableClass, new LinkedHashSet<>());
        this.toggleNamedButtons.put(locatableClass, new LinkedHashMap<>());
        getMedlabsAnimationPanel().addVisibilityEvaluator(locatableClass, visibilityEvaluator);
        for (int i = 0; i < nameArray.length; i++)
        {
            String name = nameArray[i];
            String toolTipText = toolTipTextArray[i];
            boolean initiallyVisible = initiallyVisibleArray[i];
            JToggleButton button;
            button = new JCheckBox(name);
            button.setName(name);
            button.setEnabled(true);
            button.setSelected(initiallyVisible);
            button.setActionCommand(locatableClass.getSimpleName() + "_" + name);
            button.setToolTipText(toolTipText);
            button.addActionListener(this);

            JPanel toggleBox = new JPanel();
            toggleBox.setLayout(new BoxLayout(toggleBox, BoxLayout.X_AXIS));
            toggleBox.add(button);
            getTogglePanel().add(toggleBox);
            toggleBox.setAlignmentX(Component.LEFT_ALIGNMENT);

            if (initiallyVisible)
            {
                getMedlabsAnimationPanel().addVisibilityString(locatableClass, name);
            }
            else
            {
                getMedlabsAnimationPanel().removeVisibilityString(locatableClass, name);
            }
            this.namedVisibilityMap.get(locatableClass).add(name);
            this.toggleNamedButtons.get(locatableClass).put(name, button);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(final ActionEvent actionEvent)
    {
        String actionCommand = actionEvent.getActionCommand();
        try
        {
            if (actionCommand.contains("_"))
            {
                String[] classButtonName = actionCommand.split("_", 2);
                for (Class<? extends Locatable> locatableClass : this.toggleNamedButtons.keySet())
                {
                    if (locatableClass.getSimpleName().equals(classButtonName[0]))
                    {
                        String name = classButtonName[1];
                        if (getMedlabsAnimationPanel().isVisible(locatableClass, name))
                        {
                            getMedlabsAnimationPanel().removeVisibilityString(locatableClass, name);
                        }
                        else
                        {
                            getMedlabsAnimationPanel().addVisibilityString(locatableClass, name);
                        }
                        getTogglePanel().repaint();
                        this.repaint();
                        return;
                    }
                }
            }
            super.actionPerformed(actionEvent); // handle other commands for GIS and Class toggles
        }
        catch (Exception exception)
        {
            CategoryLogger.always().warn(exception);
        }
    }

    /**
     * Show the objects of a named class, and update the toggle on the screen.
     * @param locatableClass Class&lt;? extends Locatable&gt;; the class for the visibility string
     * @param displayName String; the string that identifies the element to be shown
     */
    public void showNamedAnimationClass(final Class<? extends Locatable> locatableClass, final String displayName)
    {
        this.toggleNamedButtons.get(locatableClass).get(displayName).setSelected(true);
        getMedlabsAnimationPanel().addVisibilityString(locatableClass, displayName);
        getTogglePanel().repaint();
        this.repaint();
    }

    /**
     * Hide the objects of a named class, and update the toggle on the screen.
     * @param locatableClass Class&lt;? extends Locatable&gt;; the class for the visibility string
     * @param displayName String; the string that identifies the element to be hidden
     */
    public void hideNamedAnimationClass(final Class<? extends Locatable> locatableClass, final String displayName)
    {
        this.toggleNamedButtons.get(locatableClass).get(displayName).setSelected(false);
        getMedlabsAnimationPanel().removeVisibilityString(locatableClass, displayName);
        getTogglePanel().repaint();
        this.repaint();
    }

    /**
     * @return MedlabsAnimationPanel
     */
    public MedlabsAnimationPanel getMedlabsAnimationPanel()
    {
        return (MedlabsAnimationPanel) getAnimationPanel();
    }
}
