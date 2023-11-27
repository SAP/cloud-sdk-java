#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

// Spring Boot asynchronous configuration
import java.util.concurrent.Executor;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextExecutors;

/**
 * Implementation that customizes the {@link Executor} instance used when processing @{@link Async} method invocations.
 */
@EnableAsync
@Configuration
public class AsynchronousConfiguration implements AsyncConfigurer
{
    @Override
    public Executor getAsyncExecutor()
    {
        return ThreadContextExecutors.getExecutor();
    }
}
