package nl.tudelft.simulation.medlabs.simulation.gui;

import java.rmi.RemoteException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.djutils.draw.bounds.Bounds2d;

import nl.tudelft.simulation.dsol.animation.Locatable;
import nl.tudelft.simulation.dsol.animation.d2.Renderable2dInterface;
import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;
import nl.tudelft.simulation.dsol.swing.animation.d2.AnimationPanel;
import nl.tudelft.simulation.language.DsolException;

/**
 * MedlabsAnimationPanel is an animation panel that reacts on 'String' toggles.
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
public class MedlabsAnimationPanel extends AnimationPanel
{
    /** */
    private static final long serialVersionUID = 1L;

    /** the set of visible labels per class, in addition to the visible classes. */
    private Map<Class<? extends Locatable>, Set<String>> visibleLabelMap = new LinkedHashMap<>();

    /** the set of visibility evaluators. */
    private Map<Class<? extends Locatable>, VisibilityEvaluator> visibilityEvaluatorMap = new LinkedHashMap<>();

    /** a cache of named classes to consider, mapping the display class to the vivibility class. */
    private Map<Class<? extends Locatable>, Class<? extends Locatable>> namedClassesMap = new LinkedHashMap<>();

    /** a cache of named classes to not consider for named visibility evaluation. */
    private Set<Class<? extends Locatable>> noNamedClassesSet = new LinkedHashSet<>();

    /**
     * Construct a new AnimationPanel that reacts on "string" toggles.
     * @param homeExtent Bounds2d; the home (initial) extent of the panel
     * @param simulator SimulatorInterface&lt;?, ?, ?&gt;; the simulator of which we want to know the events for animation
     * @throws RemoteException on network error for one of the listeners
     * @throws DsolException when the simulator is not implementing the AnimatorInterface
     */
    public MedlabsAnimationPanel(final Bounds2d homeExtent, final SimulatorInterface<?> simulator)
            throws RemoteException, DsolException
    {
        super(homeExtent, simulator);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isShowElement(final Renderable2dInterface<? extends Locatable> element)
    {
        Locatable locatable = element.getSource();
        if (locatable == null)
        {
            return false;
        }
        Class<? extends Locatable> locatableClass = locatable.getClass();
        if (!isShowClass(locatableClass))
        {
            return false;
        }
        if (!isShowNamedClass(locatableClass))
        {
            return true; // it is not a class that needs to be further evaluated
        }
        Class<? extends Locatable> baseClass = this.namedClassesMap.get(locatableClass);
        if (!this.visibleLabelMap.containsKey(baseClass))
        {
            return true;
        }
        return this.visibilityEvaluatorMap.get(baseClass).evaluate(this.visibleLabelMap.get(baseClass), locatable);
    }

    /**
     * Test whether a certain named class needs to be shown on the screen or not. The class needs to implement Locatable,
     * otherwise it cannot be shown at all. Data is stored in two fast-access maps to help
     * @param locatableClass Class&lt;? extends Locatable&gt;; the class to test
     * @return whether the class of this object needs to be shown or not
     */
    public boolean isShowNamedClass(final Class<? extends Locatable> locatableClass)
    {
        if (this.noNamedClassesSet.contains(locatableClass))
        {
            return false;
        }
        if (this.namedClassesMap.containsKey(locatableClass))
        {
            return true;
        }
        synchronized (this.namedClassesMap)
        {
            Class<? extends Locatable> baseClass = locatableClass;
            boolean show = false;
            for (Class<? extends Locatable> lc : this.visibilityEvaluatorMap.keySet())
            {
                if (lc.isAssignableFrom(locatableClass))
                {
                    show = true;
                    baseClass = lc;
                    break;
                }
            }
            // add to the right cache
            if (show)
            {
                this.namedClassesMap.put(locatableClass, baseClass);
                return true;
            }
            this.noNamedClassesSet.add(locatableClass);
            return false;
        }
    }

    /**
     * Add a visible element identified by a String (so we can have multiple show/hide buttons for the same class).
     * @param locatableClass Class&lt;? extends Locatable&gt;; the class for the visibility string
     * @param displayName String; the string that identifies the element to be shown
     */
    public void addVisibilityString(final Class<? extends Locatable> locatableClass, final String displayName)
    {
        this.visibleLabelMap.get(locatableClass).add(displayName);
    }

    /**
     * Remove a visible element identified by a String (so we can have multiple show/hide buttons for the same class).
     * @param locatableClass Class&lt;? extends Locatable&gt;; the class for the visibility string
     * @param displayName String; the string that identifies the element to be hidden
     */
    public void removeVisibilityString(final Class<? extends Locatable> locatableClass, final String displayName)
    {
        this.visibleLabelMap.get(locatableClass).remove(displayName);
    }

    /**
     * Return whether elements identified by the String are visible at the moment.
     * @param locatableClass Class&lt;? extends Locatable&gt;; the class for the visibility string
     * @param displayName String; the string that identifies the element to be shown
     * @return boolean; whether elements identified by the String are visible at the moment.
     */
    public boolean isVisible(final Class<? extends Locatable> locatableClass, final String displayName)
    {
        return this.visibleLabelMap.get(locatableClass).contains(displayName);
    }

    /**
     * Add an evaluator for a class, which determines if members of the class will be drawn on the screen or not.
     * @param locatableClass locatableClass Class&lt;? extends Locatable&gt;; the class for the evaluation
     * @param visibilityEvaluator VisibilityEvaluator&lt;Locatable&gt;; the evaluator for the class
     * @param <T> Locatable type to keep arguments consistent
     */
    public <T extends Locatable> void addVisibilityEvaluator(final Class<T> locatableClass,
            final VisibilityEvaluator<T> visibilityEvaluator)
    {
        this.visibilityEvaluatorMap.put(locatableClass, visibilityEvaluator);
        this.visibleLabelMap.put(locatableClass, new LinkedHashSet<>());
    }
}
