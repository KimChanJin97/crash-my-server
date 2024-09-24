package cjkimhello97.toy.crashMyServer.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "Crash My Server API Docs",
                description = "Description",
                version = "v1"
        )
)
@Configuration
public class SwaggerConfig {

}
