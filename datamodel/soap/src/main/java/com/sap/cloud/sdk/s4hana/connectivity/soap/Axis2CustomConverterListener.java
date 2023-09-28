package com.sap.cloud.sdk.s4hana.connectivity.soap;

import javax.annotation.Nonnull;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.axis2.databinding.utils.ConverterUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * Servlet listener that is responsible for registering a default custom converter class in the Axis2 framework.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@WebListener
@Slf4j
@Deprecated
public class Axis2CustomConverterListener implements ServletContextListener
{
    @Override
    public void contextInitialized( @Nonnull final ServletContextEvent servletContextEvent )
    {
        registerCustomConverterForSoap(SoapCustomConverter.class);
    }

    @Override
    public void contextDestroyed( @Nonnull final ServletContextEvent servletContextEvent )
    {

    }

    private void registerCustomConverterForSoap( final Class<? extends ConverterUtil> converterClass )
    {
        try {
            SoapRequest.registerCustomConverter(converterClass);

            if( log.isInfoEnabled() ) {
                log
                    .info(
                        "Axis2 Custom Converter Class "
                            + converterClass.getSimpleName()
                            + " registered during startup.");
            }
        }
        catch( final SoapException e ) {
            log
                .error(
                    "Error during registering Axis2 Custom Converter Class "
                        + converterClass.getSimpleName()
                        + ": "
                        + e.getMessage(),
                    e);
        }
    }
}
