package com.manoelcampos.collectionsadvisor;

import java.util.*;

import static java.util.Objects.requireNonNull;

/**
 * Class to store metrics about calls to methods on JDK {@link Collection} classes.
 * @author Manoel Campos da Silva Filho
 */
public class Metrics {
    /** @see #getTracedPackageName() */
    private static String tracedPackageName = "com.sample";

    /** @see #getMetricMap() */
    private static final Map<CollectionReference, CollectionMetric> metricMap = new HashMap<>();

    /**
     * A map where keys are a reference for methods called from a {@link Collection}
     * and values are the number of calls for each method.
     */
    public static Map<CollectionReference, CollectionMetric> getMetricMap() {
        return Collections.unmodifiableMap(metricMap);
    }

    public static void print() {
        if(metricMap.isEmpty()){
            System.out.println();
            System.out.println("# Collections Advisor Agent");
            System.out.println("## No Intercepted calls to java.util.Collection methods. ");
            System.out.println("## This may be because:");
            System.out.println("  - the agent is not working;");
            System.out.println("  - you don't have code using Collection classes;");
            System.out.println("  - you haven't correctly specified the package to track Collection calls when starting up the agent for you app.");
            return;
        }

        System.out.printf("%n# Collections Advisor Agent%n");
        System.out.printf(
            "## Intercepted java.util.Collection methods calls from %s package%n%n",
            tracedPackageName);
        metricMap.forEach((method, metric) -> System.out.printf("%s:%n  %s%n", method, metric));
    }

    /**
     * Gets the name of the package to trace calls to {@link Collection} methods.
     * @see #setTracedPackageName(String)
     */
    public static String getTracedPackageName() {
        return tracedPackageName;
    }

    /**
     * Sets the name of the package to trace calls to {@link Collection} methods.
     * Only {@link Collection} objects declared in classes
     * inside this package will be traced.
     * That can be changed by passing a new value as a command line parameter to the Collections Advisor Agent.
     * Check CollectionAgent class documentation in the agent project dir.
     * @param tracedPackageName the package to inspect
     */
    public static void setTracedPackageName(final String tracedPackageName) {
        if(requireNonNull(tracedPackageName).isBlank()){
            throw new IllegalArgumentException("Inspect package name cannot be blank");
        }

        Metrics.tracedPackageName = tracedPackageName;
    }

    /**
     * Increments the number of calls for methods on a given {@link Collection}
     *
     * @param call data about the Collection method call
     * @param attrs Collection attribute values when the called method was started and ended
     * @return true if the Collection object {@link CollectionReference#isNotInsideTracedPackage() is inside a tracked package};
     *              false otherwise
     */
    public static boolean add(final CollectionCall call, final CollectionAttrs attrs){
        requireNonNull(call);
        final var reference = call.getCollectionRef();
        if (reference.isNotInsideTracedPackage()) {
            return false;
        }

        final var metric = metricMap.getOrDefault(reference, new CollectionMetric());
        metric.getSize().setValue(attrs.getPreviousSize(), attrs.getCurrentSize());
        metric.getCapacity().setValue(attrs.getPreviousCapacity(), attrs.getCurrentCapacity());
        metric.track(call);
        metricMap.put(reference, metric);
        return true;
    }
}
