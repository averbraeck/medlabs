package nl.tudelft.simulation.medlabs.simulation.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import nl.tudelft.simulation.dsol.animation.Locatable;
import nl.tudelft.simulation.dsol.swing.gui.animation.DsolAnimationTab;
import nl.tudelft.simulation.medlabs.location.Location;
import nl.tudelft.simulation.medlabs.location.LocationType;
import nl.tudelft.simulation.medlabs.model.MedlabsModelInterface;
import nl.tudelft.simulation.medlabs.person.Person;
import nl.tudelft.simulation.medlabs.person.Student;
import nl.tudelft.simulation.medlabs.person.Worker;

/**
 * Set the default animation toggles for the animation tab. Code based on OpenTrafficSim project component with the same
 * purpose.
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
 * @author <a href="http://www.transport.citg.tudelft.nl">Wouter Schakel</a>
 */
public class AnimationToggles
{
    /**
     * Do not instantiate this class.
     */
    private AnimationToggles()
    {
        // static class.
    }

    /**
     * Set the most common animation on, and create the toggles on the left hand side.
     * @param model MedlabsModelInterface; the model
     * @param panel MedlabsAnimationPanel; the Animation tab.
     */
    public static void setTextAnimationTogglesStandard(final MedlabsModelInterface model, final DsolAnimationTab panel)
    {
        panel.addToggleText("INFRASTRUCTURE");
        for (LocationType type : model.getLocationTypeIndexMap().values())
        {
            if (type.getAnimationClass() != null && type.getLocationClass() != null)
            {
                panel.addToggleAnimationButtonText(type.getName(), type.getLocationClass(), "Show/hide " + type.getName(),
                        true);
            }
        }
        panel.addToggleText("  ");
        panel.addToggleText("MOVABLES");
        panel.addToggleAnimationButtonText("Person", Person.class, "Show/hide persons", false);
        panel.addToggleAnimationButtonText("Worker", Worker.class, "Show/hide workers", false);
        panel.addToggleAnimationButtonText("Student", Student.class, "Show/hide students", false);
        // TODO: car?
    }

    /**
     * Set the most common animation on, and create the toggles on the left hand side.
     * @param model MedlabsModelInterface; the model
     * @param panel MedlabsAnimationPanel; the Animation tab.
     */
    public static void setTextAnimationTogglesBasedOnName(final MedlabsModelInterface model, final MedlabsAnimationTab panel)
    {
        panel.addToggleText("INFRASTRUCTURE");
        List<String> names = new ArrayList<>();
        List<String> toolTips = new ArrayList<>();
        for (LocationType type : model.getLocationTypeIndexMap().values()) // XXX: for now
        {
            if (type.getAnimationClass() != null && type.getLocationClass() != null)
            {
                names.add(type.getName());
                toolTips.add("Show/hide " + type.getName());
            }
        }
        VisibilityEvaluator<Location> localtionVE = new VisibilityEvaluator<>()
        {
            @Override
            public boolean evaluate(final Set<String> visibleToggles, final Location location)
            {
                return visibleToggles.contains(location.getLocationType().getName());
            }
        };
        boolean[] visible = new boolean[names.size()];
        for (int i = 0; i < visible.length; i++)
        {
            visible[i] = true;
        }
        panel.addToggleAnimationStringText(names.toArray(new String[0]), Location.class, localtionVE,
                toolTips.toArray(new String[0]), visible);

        panel.addToggleText("  ");
        panel.addToggleText("MOVABLES");
        panel.addToggleAnimationButtonText("Person", Person.class, "Show/hide persons", false);
        panel.addToggleAnimationButtonText("Worker", Worker.class, "Show/hide workers", false);
        panel.addToggleAnimationButtonText("Student", Student.class, "Show/hide students", false);
        // TODO: car?
    }

    /**
     * Set a class to be shown in the animation to true.
     * @param model MedlabsModelInterface; the model
     * @param panel MedlabsAnimationPanel; the Animation tab.
     * @param locatableClass Class&lt;? extends Locatable&gt;; the class for which the animation has to be shown.
     */
    public static void showAnimationClass(final MedlabsModelInterface model, final DsolAnimationTab panel,
            final Class<? extends Locatable> locatableClass)
    {
        panel.getAnimationPanel().showClass(locatableClass);
        panel.updateAnimationClassCheckBox(locatableClass);
    }

    /**
     * Set a class to be shown in the animation to false.
     * @param model MedlabsModelInterface; the model
     * @param panel MedlabsAnimationPanel; the Animation tab.
     * @param locatableClass Class&lt;? extends Locatable&gt;; the class for which the animation has to be shown.
     */
    public static void hideAnimationClass(final MedlabsModelInterface model, final DsolAnimationTab panel,
            final Class<? extends Locatable> locatableClass)
    {
        panel.getAnimationPanel().hideClass(locatableClass);
        panel.updateAnimationClassCheckBox(locatableClass);
    }

    /**
     * Set the most common animation on, and create the toggles on the left hand side.
     * @param model MedlabsModelInterface; the model
     * @param panel MedlabsAnimationPanel; the Animation tab.
     */
    public static void showAnimationStandard(final MedlabsModelInterface model, final DsolAnimationTab panel)
    {
        for (LocationType type : model.getLocationTypeIndexMap().values())
        {
            showAnimationClass(model, panel, type.getLocationClass());
        }
    }

}
