#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import static java.lang.Thread.currentThread;

import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextExecutor;
// import com.sap.cloud.security.config.Service;
// import com.sap.cloud.security.test.api.SecurityTestContext;
// import com.sap.cloud.security.test.extension.SecurityTestExtension;
// import com.sap.cloud.security.token.TokenClaims;
import org.apache.commons.io.IOUtils;
// import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static java.lang.Thread.currentThread;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(
        properties = {
                "sap.security.services.xsuaa.uaadomain=http://localhost:9001",
                "sap.security.services.xsuaa.xsappname=xsapp!t0815",
                "sap.security.services.xsuaa.clientid=sb-clientId!t0815" } )
class HelloWorldControllerTest
{
    @Autowired
    private MockMvc mvc;

    /*
    @RegisterExtension
    static SecurityTestExtension extension = SecurityTestExtension.forService(Service.XSUAA) // or Service.IAS
            .setPort(9001);

    private String jwt;
    */

    @Test
    void test(/*SecurityTestContext context*/)
    {
        /*
        jwt = context.getPreconfiguredJwtGenerator()
                .withLocalScopes("Display")
                .withClaimValue(TokenClaims.XSUAA.ORIGIN, "sap-default") // optional
                .withClaimValue(TokenClaims.USER_NAME, "John") // optional
                .createToken()
                .getTokenValue();
        jwt = "Bearer " + jwt;
        */

        final InputStream inputStream = currentThread().getContextClassLoader().getResourceAsStream("expected.json");

        ThreadContextExecutor.fromNewContext().execute(() -> {
            mvc
                    .perform(MockMvcRequestBuilders.get("/hello")
                            // .header(HttpHeaders.AUTHORIZATION, jwt)
                    )
                    .andExpect(status().isOk())
                    .andExpect(content().json(IOUtils.toString(inputStream, StandardCharsets.UTF_8)));
        });
    }
}
