package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;

import com.google.common.annotations.Beta;
import com.sap.cloud.sdk.cloudplatform.servlet.LocaleAccessor;

import lombok.extern.slf4j.Slf4j;

/**
 * Adds the headers "sap-client" and "sap-language", if the respective properties are present on a destination.
 *
 * @see DestinationProperty#SAP_CLIENT
 * @see DestinationProperty#SAP_LANGUAGE
 * @see DestinationProperty#DYNAMIC_SAP_LANGUAGE
 * @since 4.16.0
 */
@Beta
@Slf4j
public class ErpDestinationHeaderProvider implements DestinationHeaderProvider
{
    @Nonnull
    @Override
    public List<Header> getHeaders( @Nonnull final DestinationRequestContext requestContext )
    {
        final List<Header> result = new ArrayList<>();
        final DestinationProperties destination = requestContext.getDestination();

        destination
            .get(DestinationProperty.SAP_CLIENT)
            .map(client -> new Header(DestinationProperty.SAP_CLIENT.getKeyName(), client))
            .onEmpty(() -> warnIfDestinationIsOnPremise(destination))
            .peek(result::add);

        destination
            .get(DestinationProperty.DYNAMIC_SAP_LANGUAGE)
            .filter(Boolean::booleanValue)
            .map(b -> LocaleAccessor.getCurrentLocale())
            .orElse(() -> destination.get(DestinationProperty.SAP_LANGUAGE).map(Locale::forLanguageTag))
            .map(l -> new Header(DestinationProperty.SAP_LANGUAGE.getKeyName(), l.getLanguage()))
            .peek(result::add);

        return result;
    }

    private static void warnIfDestinationIsOnPremise( final DestinationProperties destination )
    {
        final ProxyType maybeProxyType = destination.get(DestinationProperty.PROXY_TYPE).getOrNull();

        if( maybeProxyType == ProxyType.ON_PREMISE ) {
            log
                .info(
                    "No {} property defined on HTTP destination pointing to on-premise ERP system with URI {}. It is recommended to specify the {} property to prevent authentication issues.",
                    DestinationProperty.SAP_CLIENT.getKeyName(),
                    destination.get(DestinationProperty.URI),
                    DestinationProperty.SAP_CLIENT.getKeyName());
        }
    }
}
