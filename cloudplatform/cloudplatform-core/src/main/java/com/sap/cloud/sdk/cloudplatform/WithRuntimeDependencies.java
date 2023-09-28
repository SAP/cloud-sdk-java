package com.sap.cloud.sdk.cloudplatform;

import com.sap.cloud.sdk.cloudplatform.exception.DependencyNotFoundException;

/**
 * Interface for classes that require certain runtime dependencies.
 */
public interface WithRuntimeDependencies
{
    /**
     * Asserts that required runtime dependencies are available on the class path.
     *
     * @throws DependencyNotFoundException
     *             If a required runtime dependency is not available.
     */
    void assertRuntimeDependenciesExist()
        throws DependencyNotFoundException;
}
