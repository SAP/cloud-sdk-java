package com.sap.security.um.service.api;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

/**
 * Stub for the UserSessionAccessor class that is normally provided by the SAP CP Neo runtime.
 * <p>
 * Must only be used within tests.
 */
@Slf4j
public class UserSessionAccessor
{
    /**
     * Initializes this {@link UserSessionAccessor}.
     *
     * @param request
     *            The {@link HttpServletRequest} that triggered the user session.
     */
    public static void initializeAccessor( @Nullable final HttpServletRequest request )
    {
        log.info("UserSessionAccessor testing stub: Invoked initializeAccessor().");
    }

    /**
     * Removes this {@link UserSessionAccessor}.
     */
    public static void removeSessionAccessor()
    {
        log.info("UserSessionAccessor testing stub: Invoked removeSessionAccessor().");
    }
}
