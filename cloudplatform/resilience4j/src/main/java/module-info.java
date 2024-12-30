module com.sap.cloud.sdk.cloudplatform.resilience4j {
    exports com.sap.cloud.sdk.cloudplatform.resilience;
    
    requires com.sap.cloud.sdk.cloudplatform.core;
    requires com.sap.cloud.sdk.cloudplatform.resilience.api;
    requires io.github.resilience4j.circuitbreaker;
    requires io.github.resilience4j.bulkhead;
    requires io.github.resilience4j.timelimiter;
    requires io.github.resilience4j.retry;
    requires io.github.resilience4j.ratelimiter;
    requires static javax.annotation;
    requires static lombok;
    requires org.slf4j;
}
