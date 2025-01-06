/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.generator;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

import javax.annotation.Nonnull;

import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JPackage;

class SafeCodeWriter extends CodeWriter
{
    private final File targetDirectory;

    public SafeCodeWriter( final File targetDirectory, final String encoding )
    {
        super.encoding = encoding;
        this.targetDirectory = targetDirectory;
    }

    @Override
    @Nonnull
    public OutputStream openBinary( @Nonnull final JPackage pkg, @Nonnull final String fileName )
        throws IOException
    {
        final File dir;

        if( pkg.isUnnamed() ) {
            dir = targetDirectory;
        } else {
            dir = new File(targetDirectory, toDirName(pkg));
        }

        if( !dir.exists() ) {
            final boolean success = dir.mkdirs();
            if( !success ) {
                throw new IOException("Could not create directory at '" + dir.getAbsolutePath() + "'");
            }
        }

        final File fn = new File(dir, fileName);

        if( fn.exists() ) {
            throw new IOException("Cannot override existing file: " + fn.getAbsolutePath());
        }

        return Files.newOutputStream(fn.toPath());
    }

    /**
     * Converts a package name to the directory name.
     */
    private static String toDirName( final JPackage pkg )
    {
        return pkg.name().replace('.', File.separatorChar);
    }

    @Override
    public void close()
    {
        // nothing to be done, as the FileOutputStream gets closed by the caller
    }
}
