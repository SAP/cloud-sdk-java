module com.sap.cloud.sdk.cloudplatform.tenant {
    exports com.sap.cloud.sdk.cloudplatform.tenant;
    exports com.sap.cloud.sdk.cloudplatform.tenant.exception;
    
    requires com.sap.cloud.sdk.cloudplatform.core;
    requires com.sap.cloud.sdk.cloudplatform.security;
    requires com.auth0.jwt;
    requires com.sap.cloud.environment.servicebinding;
    requires io.vavr;
    requires static javax.annotation;
    requires static lombok;
    requires org.slf4j;
}
