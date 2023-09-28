package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;

import javax.net.ssl.SSLContext;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.sap.cloud.sdk.cloudplatform.exception.CloudPlatformException;

import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CfPlatformSslContextProviderTest
{
    private static final SSLContext mockContext = mock(SSLContext.class, "Initial mock");
    private static final SSLContext updatedMockContext = mock(SSLContext.class, "Updated mock");
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private CfPlatformSslContextProvider providerToTest;

    @Before
    public void setUp()
    {
        providerToTest = spy(new CfPlatformSslContextProvider());
    }

    @Test
    public void testUnstubbedAccessUsesDefaultContext()
        throws NoSuchAlgorithmException
    {
        assertThat(providerToTest.tryGetContext().get()).isSameAs(SSLContext.getDefault());
    }

    @Test
    public void testCacheAccess()
    {
        doReturn(Try.success(mockContext)).when(providerToTest).tryLoadInstanceIdentity();

        assertThat(providerToTest.getCache().get().isEmpty()).isTrue();
        final SSLContext computed = providerToTest.tryGetContext().get();

        assertThat(providerToTest.getCache().get().isDefined()).isTrue();
        final SSLContext fromCache = providerToTest.tryGetContext().get();

        assertThat(fromCache).isSameAs(computed).isSameAs(mockContext);

        verify(providerToTest, times(1)).tryLoadInstanceIdentity();
    }

    @Test
    public void testCacheDuration()
    {
        doReturn(Try.success(mockContext)).when(providerToTest).tryLoadInstanceIdentity();
        providerToTest.setCacheDuration(Duration.ZERO);

        assertThat(providerToTest.getCache().get().isEmpty()).isTrue();
        providerToTest.tryGetContext();
        providerToTest.tryGetContext();
        providerToTest.tryGetContext();
        assertThat(providerToTest.getCache().get().isEmpty()).isTrue();

        verify(providerToTest, times(3)).tryLoadInstanceIdentity();
    }

    @Test
    public void testBuildpackAccess()
        throws NoSuchAlgorithmException
    {
        providerToTest.setSecurityProviderAvailable(true);

        final Try<SSLContext> context = providerToTest.tryGetContext();

        assertThat(context).contains(SSLContext.getDefault());
        verify(providerToTest, times(0)).tryLoadInstanceIdentity();
    }

    @Test
    public void testFileSystemAccess()
        throws IOException
    {
        final String cert = tempFolder.newFile("cert").getAbsolutePath();
        final String key = tempFolder.newFile("key").getAbsolutePath();

        mockEnvVars(cert, key);
        doReturn(Try.success(mockContext)).when(providerToTest).tryGetContext(any(Reader.class), any());

        final Try<SSLContext> actual = providerToTest.tryGetContext();

        assertThat(actual.get()).isSameAs(mockContext);
        verify(providerToTest, times(1)).tryGetContext(any(Reader.class), any());
    }

    @Test
    public void testLastModifiedCheck()
        throws IOException,
            InterruptedException
    {
        providerToTest.setCacheDuration(Duration.ZERO);

        File certFile = tempFolder.newFile("cert");
        String cert = certFile.getAbsolutePath();
        final String key = tempFolder.newFile("key").getAbsolutePath();
        mockEnvVars(cert, key);

        long certFileModified = certFile.lastModified();
        assertThat(providerToTest.getCache().getLastModified()).isNull();

        doReturn(Try.success(mockContext), Try.success(updatedMockContext))
            .when(providerToTest)
            .tryGetContext(any(Reader.class), any());
        final Try<SSLContext> first = providerToTest.tryGetContext();
        assertThat(providerToTest.getCache().getLastModified()).isEqualTo(certFileModified);

        final Try<SSLContext> second = providerToTest.tryGetContext();
        assertThat(second.get()).isSameAs(first.get());
        assertThat(providerToTest.getCache().getLastModified()).isEqualTo(certFileModified);

        // the file system time stamp does not recognise milliseconds
        // we need to wait at least one full second to get a new timestamp on the new file
        Thread.sleep(Duration.ofSeconds(3).toMillis());
        certFile = tempFolder.newFile("certFileUpdated");
        certFileModified = certFile.lastModified();
        log.warn("updated file is: {}", certFileModified);

        cert = certFile.getAbsolutePath();
        mockEnvVars(cert, key);
        final Try<SSLContext> third = providerToTest.tryGetContext();

        verify(providerToTest, times(2)).tryGetContext(any(Reader.class), any());

        assertThat(third.get()).isSameAs(updatedMockContext);
        assertThat(providerToTest.getCache().getLastModified()).isEqualTo(certFileModified);
    }

    @Test
    public void testFileNotFound()
    {
        mockEnvVars("/foo/file/that/does/not/exist", "/bar/file/that/does/not/exist");

        final Try<SSLContext> shouldBeFailure = providerToTest.tryGetContext();

        assertThat(shouldBeFailure.isFailure()).isTrue();
        assertThat(shouldBeFailure.getCause()).isInstanceOf(CloudPlatformException.class);
    }

    @Test
    public void testOnlyKeyDefined()
    {
        mockEnvVars(null, "/bar/file/that/does/not/exist");

        assertThat(providerToTest.tryGetContext().isSuccess()).isTrue();
    }

    private void mockEnvVars( String cert, String key )
    {
        CfPlatformSslContextProvider.setEnvironmentVariableReader(name -> {
            if( CfPlatformSslContextProvider.CERT_ENVIRONMENT_VARIABLE.equals(name) ) {
                return cert;
            }
            if( CfPlatformSslContextProvider.KEY_ENVIRONMENT_VARIABLE.equals(name) ) {
                return key;
            }
            fail("Test queried for unrecognized environment variable" + name);
            return null;
        });
    }

    @After
    public void tearDown()
    {
        CfPlatformSslContextProvider.setEnvironmentVariableReader(System::getenv);
    }
}
