package com.sample;

import com.manoelcampos.collectionsadvisor.CollectionAgent;
import com.manoelcampos.collectionsadvisor.Metrics;

import java.util.LinkedList;
import java.util.Random;

/**
 * A sample to check that method calls on JDK {@link java.util.Collection} classes
 * are being intercepted by the implemented {@link CollectionAgent}.
 * @author Manoel Campos da Silva Filho
 */
public class Sample {
    /**
     * Runs sample code that uses instrumented {@link java.util.Collections}
     * to check if the agent is working.
     */
    public Sample() {
        System.out.printf("%nStarting %s%n", getClass().getName());

        final var list = new LinkedList<Integer>();
        for (int i = 0; i < 10; i++) {
            list.add(i);
        }

        final var rand = new Random();
        final int size = list.size();
        for (int i = 0; i < 10; i++) {
            list.get(rand.nextInt(size));
        }

        for (final Integer val : list) {
            System.out.printf("%d ", val);
        }

        System.out.println();
        Metrics.print();
    }
}