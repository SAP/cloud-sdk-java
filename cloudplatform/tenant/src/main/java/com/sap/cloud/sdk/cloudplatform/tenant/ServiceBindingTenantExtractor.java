package com.sap.cloud.sdk.cloudplatform.tenant;

import java.net.URI;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nonnull;

import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@ToString
enum ServiceBindingTenantExtractor
{
    XSUAA(
        "xsuaa",
        new Extraction()
            .subdomain("identityzone")
            .tenantId("identityzoneid") // equal to tenant id
            .tenantId("zoneid") // available when there is a zone for tenant
            .tenantId("tenantid")),

    IAS(
        "identity",
        new Extraction()
            .subdomain(
                "url",
                s -> Try.of(() -> URI.create(s).getHost()).map(h -> h.substring(0, h.indexOf('.'))).getOrElse(""))
            .tenantId("app_tid")
            .tenantId("zone_uuid")); // outdated

    @Nonnull
    @Getter
    private final String service;

    @Nonnull
    @Getter
    @ToString.Exclude
    private final Function<Map<String, Object>, Option<DefaultTenant>> extractor;

    private static class Extraction implements Function<Map<String, Object>, Option<DefaultTenant>>
    {
        private Function<Map<String, Object>, Option<String>> tenantIdLogic = obj -> Option.none();
        private Function<Map<String, Object>, Option<String>> subdomainLogic = obj -> Option.none();

        public Extraction tenantId( @Nonnull final String key )
        {
            return tenantId(key, Function.identity());
        }

        public Extraction tenantId( @Nonnull final String key, @Nonnull final Function<String, String> refiner )
        {
            tenantIdLogic = concatLogic(tenantIdLogic, key, refiner);
            return this;
        }

        public Extraction subdomain( @Nonnull final String key )
        {
            return subdomain(key, Function.identity());
        }

        public Extraction subdomain( @Nonnull final String key, @Nonnull final Function<String, String> refiner )
        {
            subdomainLogic = concatLogic(subdomainLogic, key, refiner);
            return this;
        }

        @Nonnull
        private static Function<Map<String, Object>, Option<String>> concatLogic(
            @Nonnull final Function<Map<String, Object>, Option<String>> logic,
            @Nonnull final String key,
            @Nonnull final Function<String, String> f )
        {
            return o -> logic
                .apply(o)
                .orElse(
                    () -> Option
                        .of(o.get(key))
                        .filter(value -> value instanceof String)
                        .map(value -> (String) value)
                        .map(f));
        }

        @Override
        public Option<DefaultTenant> apply( final Map<String, Object> obj )
        {
            final Option<String> subdomain = subdomainLogic.apply(obj);
            final Option<String> tenantId = tenantIdLogic.apply(obj);
            return tenantId.flatMap(id -> subdomain.map(domain -> new DefaultTenant(id, domain)));
        }
    }
}
