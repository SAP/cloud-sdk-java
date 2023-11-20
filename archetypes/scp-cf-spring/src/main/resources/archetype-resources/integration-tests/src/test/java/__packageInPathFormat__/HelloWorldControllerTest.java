#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import static java.lang.Thread.currentThread;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
//import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextExecutor;
//import com.sap.cloud.security.config.Service;
//import com.sap.cloud.security.test.SecurityTest;
//import com.sap.cloud.security.token.TokenClaims;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(
        properties = {
                "sap.security.services.xsuaa.uaadomain=localhost",
                "sap.security.services.xsuaa.xsappname=xsapp!t0815",
                "sap.security.services.xsuaa.clientid=sb-clientId!t0815" } )
class HelloWorldControllerTest
{
    @Autowired
    private MockMvc mvc;

//    private static final SecurityTest rule = new SecurityTest(Service.XSUAA);
//    private static String jwt;

    @BeforeAll
    static void beforeClass()
            throws Exception
    {
//        rule.setup();
//        jwt =
//                rule
//                        .getPreconfiguredJwtGenerator()
//                        .withLocalScopes("Display")
//                        .withClaimValue(TokenClaims.XSUAA.ORIGIN, "sap-default") // optional
//                        .withClaimValue(TokenClaims.USER_NAME, "John") // optional
//                        .createToken()
//                        .getTokenValue();
//        jwt = "Bearer " + jwt;
    }

    @AfterAll
    static void afterClass()
    {
//        rule.tearDown();
    }

    @Test
    void test()
    {
        final InputStream inputStream = currentThread().getContextClassLoader().getResourceAsStream("expected.json");

        ThreadContextExecutor.fromNewContext().execute(() -> {
            mvc
                    .perform(MockMvcRequestBuilders.get("/hello")
//                    .header(HttpHeaders.AUTHORIZATION, jwt)
                    )
                    .andExpect(status().isOk())
                    .andExpect(content().json(IOUtils.toString(inputStream, StandardCharsets.UTF_8)));
        });
    }
}
