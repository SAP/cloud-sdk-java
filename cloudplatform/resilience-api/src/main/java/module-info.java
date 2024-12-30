module com.sap.cloud.sdk.cloudplatform.resilience.api {
    exports com.sap.cloud.sdk.cloudplatform.resilience;
    
    requires com.sap.cloud.sdk.cloudplatform.core;
    requires com.sap.cloud.sdk.cloudplatform.security;
    requires com.sap.cloud.sdk.cloudplatform.tenant;
    requires com.sap.cloud.sdk.cloudplatform.cache;
    requires com.google.common;
    requires io.vavr;
    requires static javax.annotation;
    requires static lombok;
    requires org.slf4j;
}
