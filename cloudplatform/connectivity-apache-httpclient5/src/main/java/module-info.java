module com.sap.cloud.sdk.cloudplatform.connectivity.httpclient5 {
    exports com.sap.cloud.sdk.cloudplatform.connectivity;
    
    requires com.sap.cloud.sdk.cloudplatform.core;
    requires com.sap.cloud.sdk.cloudplatform.connectivity;
    requires com.sap.cloud.sdk.cloudplatform.security;
    requires com.sap.cloud.sdk.cloudplatform.tenant;
    requires com.sap.cloud.sdk.cloudplatform.cache;
    requires org.apache.httpcomponents.client5.httpclient5;
    requires org.apache.httpcomponents.core5.httpcore5;
    requires org.apache.commons.lang3;
    requires com.github.benmanes.caffeine;
    requires io.vavr;
    requires static javax.annotation;
    requires static lombok;
    requires org.slf4j;
}
