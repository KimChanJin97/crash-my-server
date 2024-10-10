package cjkimhello97.toy.crashMyServer;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@OpenAPIDefinition(
		servers = {
				@Server(url = "https://crash-my-server.site", description = "Default Server URL"),
				@Server(url = "http://localhost:8080", description = "Localhost Spring URL"),
		}
)
@EnableJpaAuditing
@SpringBootApplication
public class CrashMyServerApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
		SpringApplication.run(CrashMyServerApplication.class, args);
	}

}
