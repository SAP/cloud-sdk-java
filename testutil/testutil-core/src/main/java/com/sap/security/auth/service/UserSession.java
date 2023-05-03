package com.sap.security.auth.service;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

/**
 * Stub for the UserSession class that is normally provided by the SAP CP Neo runtime.
 * <p>
 * Must only be used within tests.
 */
@Slf4j
public class UserSession
{
    /**
     * Initializes this {@link UserSession}.
     *
     * @param request
     *            The {@link HttpServletRequest} that triggered the user session.
     */
    public static void initialize( @Nullable final HttpServletRequest request )
    {
        log.info("UserSession testing stub: Invoked initialize().");
    }

    /**
     * Removes this {@link UserSession}.
     */
    public static void removeInstance()
    {
        log.info("UserSession testing stub: Invoked removeInstance().");
    }
}
