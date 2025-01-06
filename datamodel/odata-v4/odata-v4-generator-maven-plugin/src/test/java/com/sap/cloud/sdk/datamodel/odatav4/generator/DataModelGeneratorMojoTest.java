package com.sap.cloud.sdk.datamodel.odatav4.generator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.io.File;
import java.net.URL;

import org.apache.maven.plugin.testing.MojoRule;
import org.junit.jupiter.api.Test;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.sap.cloud.sdk.datamodel.odata.utility.NameSource;
import com.sap.cloud.sdk.datamodel.odata.utility.S4HanaNamingStrategy;
import com.sap.cloud.sdk.datamodel.odatav4.generator.annotation.DefaultAnnotationStrategy;

class DataModelGeneratorMojoTest
{
    @Test
    void test()
        throws Throwable
    {
        final DataModelGeneratorMojo mojo = loadTestProject();
        final DataModelGenerator generator = mojo.getDataModelGenerator();

        assertSoftly(softly -> {
            softly.assertThat(generator.getInputDirectory().getName()).isEqualTo("myInputDir");
            softly.assertThat(generator.getOutputDirectory().getName()).isEqualTo("myOutputDir");
            softly.assertThat(generator.isDeleteTargetDirectory()).isTrue();
            softly.assertThat(generator.isForceFileOverride()).isTrue();
            softly.assertThat(generator.getPackageName()).isEqualTo("my.package");
            softly.assertThat(generator.getDefaultBasePath()).isEqualTo("my/base/path/");
            softly
                .assertThat(generator.getServiceNameMappings().getName())
                .isEqualTo("myServiceNameMappings.properties");
            softly
                .assertThat(generator.getNamingStrategy().getClass().getName())
                .isEqualTo(S4HanaNamingStrategy.class.getName());
            softly.assertThat(generator.getNameSource()).isEqualTo(NameSource.NAME);
            softly
                .assertThat(generator.getAnnotationStrategy().getClass().getName())
                .isEqualTo(DefaultAnnotationStrategy.class.getName());
            softly.assertThat(generator.isGeneratePojosOnly()).isTrue();
            softly.assertThat(generator.getExcludeFilePattern()).isEqualTo("**/myExclusions/**");
            softly.assertThat(generator.isGenerateLinksToApiBusinessHub()).isTrue();
            softly.assertThat(generator.getIncludedEntitySets()).contains("entitySet1", "entitySet2");
            softly.assertThat(generator.getIncludedFunctionImports()).contains("fnImport1", "fnImport2", "fnImport3");
            softly.assertThat(generator.isFailOnWarning()).isTrue();
            softly.assertThat(generator.getCopyrightHeader()).isEmpty();
            softly.assertThat(generator.isServiceMethodsPerEntitySet()).isTrue();
        });
    }

    private DataModelGeneratorMojo loadTestProject()
        throws Throwable
    {
        final URL resource = getClass().getClassLoader().getResource(getClass().getSimpleName());
        assertThat(resource).isNotNull();

        final File pomFile = new File(resource.getFile());

        final MojoRule rule = new MojoRule();
        // hacky workaround to invoke the internal call to "testCase.setUp()" inside MojoRule
        // exploiting the fact that the setup is not teared down after "evaluate" returns
        // this workaround is applied because "lookupConfiguredMojo" is not available on AbstractMojoTestCase
        // and this way we can skip the effort to re-implement what is already available in MojoRule
        rule.apply(new Statement()
        {
            @Override
            public void evaluate()
            {

            }
        }, Description.createSuiteDescription("dummy")).evaluate();
        return (DataModelGeneratorMojo) rule.lookupConfiguredMojo(pomFile, "generate");
    }
}
