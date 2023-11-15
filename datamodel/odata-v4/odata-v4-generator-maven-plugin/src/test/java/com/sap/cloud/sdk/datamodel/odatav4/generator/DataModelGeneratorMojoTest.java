/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.generator;

import com.sap.cloud.sdk.datamodel.odata.utility.NameSource;
import com.sap.cloud.sdk.datamodel.odata.utility.S4HanaNamingStrategy;
import com.sap.cloud.sdk.datamodel.odatav4.generator.annotation.DefaultAnnotationStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.File;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

public class DataModelGeneratorMojoTest
{
    @RegisterExtension
    static MavenPluginExtension extension =
            new MavenPluginExtension()
                    .withBasePath(new File(DataModelGeneratorMojoTest.class.getClassLoader().getResource(DataModelGeneratorMojoTest.class.getSimpleName()).getFile()))
                    .withGoal("generate");

    @Test
    void testMe()
    {
        final DataModelGeneratorMojo mojo = extension.getMojo();
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
}
