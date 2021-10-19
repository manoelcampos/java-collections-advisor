package com.manoelcampos.collectionsadvisor;

import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription.ForLoadedType;
import net.bytebuddy.dynamic.loading.ClassInjector.UsingInstrumentation;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import static net.bytebuddy.dynamic.ClassFileLocator.ForClassLoader.read;
import static net.bytebuddy.dynamic.loading.ClassInjector.UsingInstrumentation.Target.BOOTSTRAP;
import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * A Java Agent to trace calls to methods on {@link Collection} classes.
 * @author Manoel Campos da Silva Filho
 */
public class CollectionAgent {
    public static void main(String[] args) throws UnmodifiableClassException {
        System.out.printf("%nStarting %s%n", CollectionAgent.class.getName());
        final var instrumentation = ByteBuddyAgent.install();
        premain("", instrumentation);
        instrumentation.retransformClasses(LinkedList.class);
        new Test();
    }

    /**
     * Creates the Java Agent to instrument code.
     * @param agentArgs command line parameters for the agent
     * @param inst the ByteBuddy instrumentation instante
     */
    public static void premain(final String agentArgs, final Instrumentation inst) {
        final var interceptorClass = CollectionAdvice.class;
        final var typeMap = Map.of(new ForLoadedType(interceptorClass), read(interceptorClass));
        UsingInstrumentation.of(getTempDir(), BOOTSTRAP, inst).inject(typeMap);

        new AgentBuilder.Default()
                // by default, JVM classes are not instrumented
                .ignore(none())
                // Ignore Byte Buddy and JDK classes we are not interested in
                .ignore(
                    nameStartsWith("net.bytebuddy.")
                        .or(nameStartsWith("jdk.internal.reflect."))
                        .or(nameStartsWith("java.lang.invoke."))
                        .or(nameStartsWith("com.sun.proxy.")))
                .disableClassFormatChanges()
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                // Make sure we see helpful logs
                .with(AgentBuilder.RedefinitionStrategy.Listener.StreamWriting.toSystemError())
                .with(AgentBuilder.Listener.StreamWriting.toSystemError().withTransformationsOnly())
                .with(AgentBuilder.InstallationListener.StreamWriting.toSystemError())
                .type(nameEndsWith("LinkedList"))
                .transform((builder, type, loader, module) ->
                    builder.visit(Advice.to(CollectionAdvice.class).on(isMethod()))
                )
                .installOn(inst);
    }

    private static File getTempDir() {
        try {
            return Files.createTempDirectory("tmp").toFile();
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}