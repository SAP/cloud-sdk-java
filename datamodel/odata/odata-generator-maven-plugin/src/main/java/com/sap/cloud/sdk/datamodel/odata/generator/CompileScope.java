package com.sap.cloud.sdk.datamodel.odata.generator;

import org.apache.maven.project.MavenProject;

/**
 * Enum representing the different compile scopes the generated OData VDM might be compiled in (or not).
 */
enum CompileScope
{
    /**
     * Configures the maven plugin to add the output directory as a source root for the default compile phase.
     */
    COMPILE {
        @Override
        void addSourceRoot( final MavenProject project, final String outputDirectory )
        {
            project.addCompileSourceRoot(outputDirectory);
        }
    },

    /**
     * Configures the maven plugin to add the output directory as a source root for the test compile phase.
     */
    TEST_COMPILE {
        @Override
        void addSourceRoot( final MavenProject project, final String outputDirectory )
        {
            project.addTestCompileSourceRoot(outputDirectory);
        }
    },

    /**
     * Configures the maven plugin to not add the output directory as a source root for the any compile phase.
     */
    NONE {
        @Override
        void addSourceRoot( final MavenProject project, final String outputDirectory )
        {
            // intended noop
        }
    };

    /**
     * Depending on the implementation this method may add the given {@code outputDirectory} to some compile source root
     * set of the given project.
     *
     * @param project
     *            The project that might get the output directory added as a compile source root.
     * @param outputDirectory
     *            The output directory to add.
     */
    abstract void addSourceRoot( final MavenProject project, final String outputDirectory );
}
