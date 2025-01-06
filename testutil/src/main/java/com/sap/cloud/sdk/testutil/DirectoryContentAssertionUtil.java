/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.testutil;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import javax.annotation.Nonnull;

/**
 * Allows asserting that two given directories have the exact content in terms of folders and contents of all files.
 */
public class DirectoryContentAssertionUtil
{
    /**
     * Asserts that two given directories have the exact content in terms of folders and contents of all files.
     *
     * The implementation walks the file hierarchy recursively and invokes
     * {@link org.assertj.core.api.AbstractPathAssert#hasSameTextualContentAs(Path)} to compare two files.
     *
     * In case the textual content does not match, the test fails.
     *
     * @param one
     *            The first directory
     * @param other
     *            The second directory
     * @throws IOException
     *             In case file system operations fail
     */
    public static void assertThatDirectoriesHaveSameContent( @Nonnull final Path one, @Nonnull final Path other )
        throws IOException
    {
        Files.walkFileTree(one, new DirectoryContentComparisonVisitor(one, other));
        Files.walkFileTree(other, new DirectoryContentComparisonVisitor(other, one));
    }

    private static final class DirectoryContentComparisonVisitor extends SimpleFileVisitor<Path>
    {
        private final Path one;
        private final Path other;

        DirectoryContentComparisonVisitor( final Path one, final Path other )
        {
            assertThat(one).isDirectory().isReadable();
            assertThat(other).isDirectory().isReadable();

            this.one = one;
            this.other = other;
        }

        @Override
        @Nonnull
        public FileVisitResult visitFile( @Nonnull final Path file, @Nonnull final BasicFileAttributes attrs )
            throws IOException
        {
            final FileVisitResult result = super.visitFile(file, attrs);

            // get the relative file name from path "one"
            final Path relativePath = one.relativize(file);
            // construct the path for the counterpart file in "other"
            final Path fileInOther = other.resolve(relativePath);

            assertThat(file).hasSameTextualContentAs(fileInOther);

            return result;
        }
    }
}
