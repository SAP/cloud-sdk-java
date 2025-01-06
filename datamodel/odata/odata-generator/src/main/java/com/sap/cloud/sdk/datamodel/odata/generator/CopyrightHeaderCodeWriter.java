package com.sap.cloud.sdk.datamodel.odata.generator;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JPackage;

class CopyrightHeaderCodeWriter extends CodeWriter
{
    private final CodeWriter delegate;
    private final String copyrightHeader;

    CopyrightHeaderCodeWriter( final CodeWriter codeWriter, final String copyrightHeader, final String encoding )
    {
        this.copyrightHeader = copyrightHeader;
        this.delegate = codeWriter;
        super.encoding = encoding;
    }

    @Override
    public OutputStream openBinary( final JPackage pkg, final String fileName )
        throws IOException
    {
        final OutputStream result = delegate.openBinary(pkg, fileName);
        result.write(copyrightHeader.getBytes(StandardCharsets.UTF_8));
        return result;
    }

    @Override
    public void close()
        throws IOException
    {
        delegate.close();
    }
}
