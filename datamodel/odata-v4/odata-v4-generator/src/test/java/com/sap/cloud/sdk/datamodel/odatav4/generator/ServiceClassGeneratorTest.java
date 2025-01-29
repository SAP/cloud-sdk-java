package com.sap.cloud.sdk.datamodel.odatav4.generator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.datamodel.odata.utility.LegacyClassScanner;
import com.sap.cloud.sdk.datamodel.odata.utility.NameSource;
import com.sap.cloud.sdk.datamodel.odata.utility.NamingStrategy;
import com.sap.cloud.sdk.datamodel.odata.utility.S4HanaNamingStrategy;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JPackage;

class ServiceClassGeneratorTest
{
    private JCodeModel model;
    private JPackage mwPackage;
    private JPackage namespacePackage;
    private Service mwServiceDetails;
    private NamingStrategy testingCodeNamingStrategy;

    @BeforeEach
    void setUp()
    {
        // prepare minimum working models
        model = new JCodeModel();
        mwPackage = model._package("test.service.package");
        namespacePackage = model._package("test.namespace.package");
        mwServiceDetails = mock(Service.class);
        testingCodeNamingStrategy = new S4HanaNamingStrategy(NameSource.NAME);

        when(mwServiceDetails.getJavaClassName()).thenReturn("RandomTest");
        when(mwServiceDetails.getInfoDescription()).thenReturn("Random description.");
    }

    private ServiceClassGenerator getServiceClassGenerator()
    {
        return new ServiceClassGenerator(
            model,
            mwPackage,
            namespacePackage,
            testingCodeNamingStrategy,
            false,
            LegacyClassScanner.DISABLED);
    }

    @Test
    void testJavadocEscaping()
    {
        when(mwServiceDetails.getInfoDescription()).thenReturn("Javadoc with escaped characters: & < > ' \".");

        final JDefinedClass serviceInterface =
            getServiceClassGenerator().getOrGenerateServiceInterfaceClass(mwServiceDetails);

        assertThat(serviceInterface.javadoc().get(0).toString())
            .contains("Javadoc with escaped characters: &amp; &lt; &gt; &#39; &quot;.");
    }

    @Test
    void testTerminalFullStopNeeded()
    {
        when(mwServiceDetails.getInfoDescription()).thenReturn("Javadoc without full stop");

        final JDefinedClass serviceInterface =
            getServiceClassGenerator().getOrGenerateServiceInterfaceClass(mwServiceDetails);

        assertThat(serviceInterface.javadoc().get(0).toString()).contains("Javadoc without full stop.");
    }

    @Test
    void testTerminalQuestionMark()
    {
        when(mwServiceDetails.getInfoDescription()).thenReturn("Javadoc with question mark?");

        final JDefinedClass serviceInterface =
            getServiceClassGenerator().getOrGenerateServiceInterfaceClass(mwServiceDetails);

        assertThat(serviceInterface.javadoc().get(0).toString()).contains("Javadoc with question mark?");
        assertThat(serviceInterface.javadoc().get(0).toString()).doesNotContain("Javadoc with question mark?.");
    }

    @Test
    void testTerminalExclamationMark()
    {
        when(mwServiceDetails.getInfoDescription()).thenReturn("Javadoc with exclamation mark!");

        final JDefinedClass serviceInterface =
            getServiceClassGenerator().getOrGenerateServiceInterfaceClass(mwServiceDetails);

        assertThat(serviceInterface.javadoc().get(0).toString()).contains("Javadoc with exclamation mark!");
        assertThat(serviceInterface.javadoc().get(0).toString()).doesNotContain("Javadoc with exclamation mark!.");
    }

    @Test
    void testTerminalFullStop()
    {
        when(mwServiceDetails.getInfoDescription()).thenReturn("Javadoc with full stop.");

        final JDefinedClass serviceInterface =
            getServiceClassGenerator().getOrGenerateServiceInterfaceClass(mwServiceDetails);

        assertThat(serviceInterface.javadoc().get(0).toString()).contains("Javadoc with full stop.");
        assertThat(serviceInterface.javadoc().get(0).toString()).doesNotContain("Javadoc with full stop..");
    }

    @Test
    void testNameGenerationInterface()
    {
        when(mwServiceDetails.getJavaClassName()).thenReturn("CustomText");

        final JDefinedClass serviceInterface =
            getServiceClassGenerator().getOrGenerateServiceInterfaceClass(mwServiceDetails);

        assertThat(serviceInterface.name()).isEqualTo("CustomTextService");
    }

    @Test
    void testNameGenerationImplementation()
    {
        when(mwServiceDetails.getJavaClassName()).thenReturn("CustomText");

        final ServiceClassGenerator sut = getServiceClassGenerator();

        final JDefinedClass serviceInterface = sut.getOrGenerateServiceInterfaceClass(mwServiceDetails);
        final JDefinedClass serviceImplementation =
            sut.getOrGenerateServiceImplementationClass(mwServiceDetails, serviceInterface);

        assertThat(serviceImplementation.name()).isEqualTo("DefaultCustomTextService");
    }
}
