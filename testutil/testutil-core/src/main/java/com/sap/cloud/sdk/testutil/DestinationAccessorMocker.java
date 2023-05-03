package com.sap.cloud.sdk.testutil;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationLoader;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationLoaderChain;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationOptions;

import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

/**
 * Destination Accessor Mocker
 */
@Slf4j
public class DestinationAccessorMocker implements TestRule
{
    private final Map<String, Destination> destinations = new HashMap<>();

    /**
     * Add a destination with "name" property.
     *
     * @param destination
     *            The destination to add.
     * @return The DestinationAccessorMocker reference.
     */
    @Nonnull
    public DestinationAccessorMocker addNamedDestination( @Nonnull final Destination destination )
    {
        final Option<String> name = destination.get("name", String.class::cast);
        if( name.isDefined() ) {
            destinations.put(name.get(), destination);
        } else {
            log.warn("Could find name for mocked destination: " + destination);
        }
        return this;
    }

    @Nonnull
    @Override
    public Statement apply( @Nonnull final Statement base, @Nullable final Description description )
    {
        final DestinationLoader destinationLoader = this::getDestination;

        return new Statement()
        {
            @Override
            public void evaluate()
                throws Throwable
            {
                final DestinationLoader previousLoader = DestinationAccessor.getLoader();

                final DestinationLoaderChain extendedLoader =
                    DestinationLoaderChain.builder(destinationLoader).append(previousLoader).build();

                DestinationAccessor.setLoader(extendedLoader);

                base.evaluate();

                DestinationAccessor.setLoader(previousLoader);
            }
        };
    }

    @Nonnull
    private
        Try<Destination>
        getDestination( @Nonnull final String destinationName, @Nonnull final DestinationOptions options )
    {
        return Option
            .of(destinations.get(destinationName))
            .toTry()
            .onSuccess(d -> log.info("Successfully loaded provided destination for name '" + destinationName + "'."))
            .onFailure(e -> log.warn("Could not find any provided destination for name '" + destinationName + "'."));
    }
}
