module com.sap.cloud.sdk.cloudplatform.core {
    exports com.sap.cloud.sdk.cloudplatform;
    exports com.sap.cloud.sdk.cloudplatform.exception;
    exports com.sap.cloud.sdk.cloudplatform.thread;
    exports com.sap.cloud.sdk.cloudplatform.thread.exception;
    exports com.sap.cloud.sdk.cloudplatform.util;
    
    requires java.base;
    requires com.google.common;
    requires io.vavr;
    requires static javax.annotation;
    requires static lombok;
    requires org.slf4j;
}
