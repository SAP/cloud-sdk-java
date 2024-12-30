module com.sap.cloud.sdk.cloudplatform.cache {
    exports com.sap.cloud.sdk.cloudplatform.cache;
    
    requires com.sap.cloud.sdk.cloudplatform.security;
    requires com.sap.cloud.sdk.cloudplatform.tenant;
    requires com.github.benmanes.caffeine;
    requires com.google.common;
    requires io.vavr;
    requires static javax.annotation;
    requires static lombok;
}