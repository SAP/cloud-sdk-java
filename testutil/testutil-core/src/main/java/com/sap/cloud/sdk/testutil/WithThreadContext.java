package com.sap.cloud.sdk.testutil;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.extension.ExtendWith;

@Target( ElementType.METHOD )
@Retention( RetentionPolicy.RUNTIME )
@ExtendWith( ThreadContextInvocationInterceptor.class )
public @interface WithThreadContext {
}
