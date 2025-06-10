package com.sap.cloud.sdk.cloudplatform.util;

import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingInt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.TreeMap;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.exception.ObjectLookupFailedException;

import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

/**
 * This class is for internal use only. It determines facades that are used to abstract certain logic, e.g., to adjust
 * to different platform services based on the current Cloud platform.
 */
@Slf4j
public class FacadeLocator
{
    @Nonnull
    private static MockableInstance mockableInstance = new MockableInstance();

    /**
     * Performs the facade lookup within the {@link FacadeLocator}. Allows to mock the behavior of the
     * {@link FacadeLocator}.
     * <p>
     * For internal use only.
     */
    public static class MockableInstance
    {
        /**
         * Holds the class loader of this class to ensure proper facade lookups, even when being lazily loaded in
         * non-container managed threads.
         */
        private final ClassLoader classLoader = MockableInstance.class.getClassLoader();

        /**
         * Retrieves the facades for a given facade interface.
         * <p>
         * For internal use only.
         *
         * @param facadeInterface
         *            The facade interface for which implementations should be located.
         *
         * @return A collection of all instances of the class that implement the facade interface.
         * @param <FacadeT>
         *            The generic facade type.
         */
        @Nonnull
        public <FacadeT> List<FacadeT> getFacades( @Nonnull final Class<FacadeT> facadeInterface )
        {
            final TreeMap<Class<? extends FacadeT>, FacadeT> sortedByName = new TreeMap<>(comparing(Class::getName));
            final TreeMap<Class<? extends FacadeT>, FacadeT> sortedByPriority =
                new TreeMap<>(comparingInt(f -> f.getAnnotation(Priority.class).value()));

            ServiceLoader.load(facadeInterface, classLoader).stream().forEach(facade -> {
                if( facade.type().isAnnotationPresent(Priority.class) ) {
                    sortedByPriority.put(facade.type(), facade.get());
                } else {
                    sortedByName.put(facade.type(), facade.get());
                }
            });

            final List<FacadeT> result = new ArrayList<>(sortedByPriority.values());
            result.addAll(sortedByName.values());

            log.debug("Located the following extensions of {}: {}", facadeInterface, result);
            return result;
        }

        /**
         * Retrieves the facade for a given interface, allowing at most one implementation to exist. Considered to fail
         * if there is more than one facade.
         * <p>
         * For internal use only.
         *
         * @param facadeInterface
         *            The facade interface for which an implementation should be located.
         *
         * @return A {@link Try} of the instance of the class that implements the facade interface.
         * @param <FacadeT>
         *            The generic facade type.
         */
        @Nonnull
        public <FacadeT> Try<FacadeT> getFacade( @Nonnull final Class<FacadeT> facadeInterface )
        {
            final ServiceLoader<FacadeT> serviceLoader = ServiceLoader.load(facadeInterface, classLoader);
            final Iterator<FacadeT> serviceIt = serviceLoader.iterator();

            final FacadeT facade;

            if( serviceIt.hasNext() ) {
                facade = serviceIt.next();

                if( serviceIt.hasNext() ) {
                    return Try
                        .failure(
                            new ObjectLookupFailedException(
                                "Found multiple implementations of "
                                    + facadeInterface.getSimpleName()
                                    + ": "
                                    + getFacades(facadeInterface)
                                    + ". Make sure to only specify one implementation in META-INF/services/"
                                    + facadeInterface.getName()
                                    + "."));
                }

                return Try.success(facade);
            }

            return Try
                .failure(
                    new ObjectLookupFailedException(
                        "Failed to find implementation of " + facadeInterface.getSimpleName() + "."));
        }
    }

    /**
     * Returns the current {@link MockableInstance}.
     * <p>
     * For internal use only.
     *
     * @return The current {@link MockableInstance}.
     */
    @Nonnull
    public static MockableInstance getMockableInstance()
    {
        return mockableInstance;
    }

    /**
     * Replaces the default {@link MockableInstance}.
     * <p>
     * For internal use only.
     *
     * @param mockableInstance
     *            The mockable instance.
     */
    public static void setMockableInstance( @Nonnull final MockableInstance mockableInstance )
    {
        FacadeLocator.mockableInstance = mockableInstance;
    }

    /**
     * Retrieves the facades for a given facade interface.
     * <p>
     * For internal use only.
     *
     * @param facadeInterface
     *            The facade interface for which implementations should be located.
     *
     * @return A collection of all instances of the class that implement the facade interface.
     * @param <FacadeT>
     *            The generic facade type.
     */
    @Nonnull
    public static <FacadeT> List<FacadeT> getFacades( @Nonnull final Class<FacadeT> facadeInterface )
    {
        return mockableInstance.getFacades(facadeInterface);
    }

    /**
     * Retrieves the facade for a given interface, allowing at most one implementation to exist. Considered to fail if
     * there is more than one facade.
     * <p>
     * For internal use only.
     *
     * @param facadeInterface
     *            The facade interface for which an implementation should be located.
     *
     * @return A {@link Try} of the instance of the class that implements the facade interface.
     * @param <FacadeT>
     *            The generic facade type.
     */
    @Nonnull
    public static <FacadeT> Try<FacadeT> getFacade( @Nonnull final Class<FacadeT> facadeInterface )
    {
        return mockableInstance.getFacade(facadeInterface);
    }
}
