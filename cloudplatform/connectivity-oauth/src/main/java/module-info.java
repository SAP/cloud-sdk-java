module com.sap.cloud.sdk.cloudplatform.connectivity.oauth {
    exports com.sap.cloud.sdk.cloudplatform.connectivity;
    
    requires com.sap.cloud.sdk.cloudplatform.core;
    requires com.sap.cloud.sdk.cloudplatform.connectivity;
    requires com.sap.cloud.sdk.cloudplatform.tenant;
    requires com.sap.cloud.sdk.cloudplatform.security;
    requires com.sap.cloud.environment.servicebinding;
    requires com.sap.cloud.security.xsuaa;
    requires org.apache.httpcomponents.httpcore;
    requires io.vavr;
    requires static javax.annotation;
    requires static lombok;
    requires org.slf4j;
}
