module com.sap.cloud.sdk.cloudplatform.security {
    exports com.sap.cloud.sdk.cloudplatform.security;
    exports com.sap.cloud.sdk.cloudplatform.security.exception;
    
    requires com.sap.cloud.sdk.cloudplatform.core;
    requires com.sap.cloud.sdk.cloudplatform.tenant;
    requires com.google.common;
    requires org.apache.commons.lang3;
    requires io.vavr;
    requires static javax.annotation;
    requires static lombok;
    requires org.slf4j;
}
