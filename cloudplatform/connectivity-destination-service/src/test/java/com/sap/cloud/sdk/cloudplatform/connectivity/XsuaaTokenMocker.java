package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.util.Collections;
import java.util.Map;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

class XsuaaTokenMocker
{
    static DecodedJWT mockXsuaaToken()
    {
        final Map<String, String> attrEnhancer =
            Collections
                .singletonMap(
                    DestinationRetrievalStrategyResolver.JWT_ATTR_ENHANCER,
                    DestinationRetrievalStrategyResolver.JWT_ATTR_XSUAA);
        final String jwt =
            JWT
                .create()
                .withClaim(DestinationRetrievalStrategyResolver.JWT_ATTR_EXT, attrEnhancer)
                .sign(Algorithm.none());
        return JWT.decode(jwt);
    }
}
