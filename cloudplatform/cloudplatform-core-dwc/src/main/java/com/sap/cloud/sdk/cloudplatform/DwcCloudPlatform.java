package com.sap.cloud.sdk.cloudplatform;

import java.util.Map;

import javax.annotation.Nonnull;

import com.google.common.annotations.Beta;
import com.sap.cloud.sdk.cloudplatform.connectivity.MegacliteServiceBindingAccessor;

import io.vavr.control.Option;

/**
 * Interface for a specific {@link CloudPlatform} that is used when running on the SAP Deploy with Confidence stack.
 */
@Beta
public interface DwcCloudPlatform extends CloudPlatform
{
    /**
     * Returns the {@link DwcOutboundProxyBinding} where the service binding name is equal to
     * {@code DwcOutboundProxyBinding.DEFAULT_SERVICE_BINDING_NAME}. This call is equivalent to
     * {@code getOutboundProxyBinding(DwcOutboundProxyBinding.DEFAULT_SERVICE_BINDING_NAME)}.
     *
     * @return A {@link Option} that contains the default {@link DwcOutboundProxyBinding} if it exists, otherwise a
     *         {@link io.vavr.control.Option.None}.
     */
    @Nonnull
    Option<DwcOutboundProxyBinding> getOutboundProxyBinding();

    /**
     * Returns the default {@link DwcOutboundProxyBinding} where the service binding name is equal to
     * {@link DwcOutboundProxyBinding#DEFAULT_SERVICE_BINDING_NAME}. If no such binding exists, an
     * {@link IllegalStateException} is thrown. This call is equivalent to
     * {@code getOutboundProxyBindingOrThrow(DwcOutboundProxyBinding.DEFAULT_SERVICE_BINDING_NAME)}.
     *
     * @return The default {@link DwcOutboundProxyBinding}.
     */
    @Nonnull
    DwcOutboundProxyBinding getOutboundProxyBindingOrThrow();

    /**
     * Returns the {@link DwcOutboundProxyBinding} associated with a given name.
     *
     * @param bindingName
     *            The name of the user defined service binding.
     * @return A {@link Option} that contains the {@link DwcOutboundProxyBinding} associated with the given
     *         {@code bindingName} if one exists, otherwise a {@link io.vavr.control.Option.None}.
     */
    @Nonnull
    Option<DwcOutboundProxyBinding> getOutboundProxyBinding( @Nonnull final String bindingName );

    /**
     * Return the {@link DwcOutboundProxyBinding} associated with the given {@code name} or throws an
     * {@link IllegalStateException}.
     *
     * @param bindingName
     *            The name of the user defined service binding.
     * @return The {@link DwcOutboundProxyBinding}.
     */
    @Nonnull
    DwcOutboundProxyBinding getOutboundProxyBindingOrThrow( @Nonnull final String bindingName );

    /**
     * Returns the mapping of outbound-service-bindings.
     *
     * @return A mapping of outbound-service-bindings and service-meta information.
     * @deprecated Deprecated in favor of the {@link MegacliteServiceBindingAccessor#getServiceBindings()} API.
     */
    @Deprecated
    @Nonnull
    Map<DwcOutboundServiceBinding, DwcOutboundServiceMeta> getOutboundServices();
}
