/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.openapi.generator;

import java.io.File;
import java.nio.file.Paths;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;
import org.openapitools.codegen.TemplateManager;

import lombok.extern.slf4j.Slf4j;

/**
 * Validates that the custom mustache templates are accessible on the classpath at runtime. It invokes the same
 * mechanism to access resources like the OpenAPI generator. See this pull request for reference:
 * https://github.com/OpenAPITools/openapi-generator/pull/7587
 *
 * If we would not validate the availability of custom templates, the OpenAPI generator falls back to its embedded
 * default templates which do not want. Hence, it's good choice to fail then.
 */
@Slf4j
class ClassPathResourceValidator
{
    void assertTemplatesAvailableOnClasspath(
        @Nonnull final String templateDirectory,
        @Nonnull final String libraryName )
    {
        log.debug("Defined directory for custom templates on classpath: " + templateDirectory);

        final String fullTemplatePath = getFullTemplatePath(templateDirectory, libraryName, "api.mustache");

        log.debug("OpenAPI generator determined full path for api.mustache as follows: " + fullTemplatePath);

        if( fullTemplatePath == null ) {
            throw new IllegalStateException("OpenAPI generator determined the path to api.mustache as null");
        }

        if( !fullTemplatePath.startsWith(templateDirectory) ) {
            throw new IllegalStateException(
                "OpenAPI generator determined its own embdded library as path to api.mustache instead of the custom templates.");
        }

        final String cpResourcePath = TemplateManager.getCPResourcePath(fullTemplatePath);

        if( getClass().getClassLoader().getResource(cpResourcePath) == null ) {
            throw new IllegalStateException("Resource with URL " + cpResourcePath + " not found on the classpath.");
        }
    }

    private
        String
        buildLibraryFilePath( @Nonnull final String dir, @Nonnull final String library, @Nonnull final String file )
    {
        return Paths.get(dir, "libraries", library, file).normalize().toString();
    }

    private boolean classpathTemplateExists( @Nonnull final String name )
    {
        return this.getClass().getClassLoader().getResource(TemplateManager.getCPResourcePath(name)) != null;
    }

    private String getFullTemplatePath(
        @Nonnull final String templateDir,
        @Nonnull final String library,
        @Nonnull final String relativeTemplateFile )
    {
        if( StringUtils.isNotEmpty(library) ) {
            final String template = this.buildLibraryFilePath(templateDir, library, relativeTemplateFile);
            if( new File(template).exists() || this.classpathTemplateExists(template) ) {
                return template;
            }
        }
        final String template = templateDir + File.separator + relativeTemplateFile;
        if( new File(template).exists() || this.classpathTemplateExists(template) ) {
            return template;
        }
        return null;
    }
}
