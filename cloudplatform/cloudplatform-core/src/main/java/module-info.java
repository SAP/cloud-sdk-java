module com.sap.cloud.sdk.cloudplatform.core {
    requires com.google.common;
    requires io.vavr;
    requires jsr305;
    requires static lombok;

    exports com.sap.cloud.sdk.cloudplatform;
    exports com.sap.cloud.sdk.cloudplatform.servlet;
    exports com.sap.cloud.sdk.cloudplatform.requestheader;
    exports com.sap.cloud.sdk.cloudplatform.thread;
    exports com.sap.cloud.sdk.cloudplatform.thread.exception;
    exports com.sap.cloud.sdk.cloudplatform.exception;
    exports com.sap.cloud.sdk.cloudplatform.util;
    uses com.sap.cloud.sdk.cloudplatform.thread.ThreadContextFacade;
    provides com.sap.cloud.sdk.cloudplatform.thread.ThreadContextFacade
        with com.sap.cloud.sdk.cloudplatform.thread.ThreadLocalThreadContextFacade;
}