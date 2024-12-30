module com.sap.cloud.sdk.cloudplatform.connectivity.destination {
    exports com.sap.cloud.sdk.cloudplatform.connectivity;
    exports com.sap.cloud.sdk.cloudplatform.exception;
    
    requires com.sap.cloud.sdk.cloudplatform.core;
    requires com.sap.cloud.sdk.cloudplatform.connectivity;
    requires com.sap.cloud.sdk.cloudplatform.tenant;
    requires com.sap.cloud.sdk.cloudplatform.security;
    requires com.sap.cloud.environment.servicebinding;
    requires com.google.common;
    requires com.google.gson;
    requires io.vavr;
    requires static javax.annotation;
    requires static lombok;
    requires org.slf4j;
}
