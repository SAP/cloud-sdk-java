package com.sap.cloud.sdk.testutil;

import com.sap.cloud.sdk.cloudplatform.security.principal.DefaultPrincipal;
import com.sap.cloud.sdk.cloudplatform.security.principal.Principal;
import com.sap.cloud.sdk.cloudplatform.security.principal.PrincipalThreadContextListener;

public interface PrincipalContextApi extends TestContextApi
{
    default Principal setPrincipal()
    {
        return setPrincipal("principal");
    }

    default Principal setPrincipal( String principal )
    {
        return setPrincipal(new DefaultPrincipal(principal));
    }

    default Principal setPrincipal( Principal principal )
    {
        setProperty(PrincipalThreadContextListener.PROPERTY_PRINCIPAL, principal);
        return principal;
    }

    default void clearPrincipal()
    {
        setPrincipal((Principal) null);
    }
}
