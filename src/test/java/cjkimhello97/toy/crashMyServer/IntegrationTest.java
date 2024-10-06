package cjkimhello97.toy.crashMyServer;

import java.io.File;
import java.time.Duration;
import org.junit.Ignore;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

@Ignore
@Transactional
@SpringBootTest
public class IntegrationTest {

    static DockerComposeContainer containers;

    static {
        containers = new DockerComposeContainer(new File("docker-compose-local-test.yml"))
                .withExposedService(
                        "mysql",
                        3306,
                        Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(1000))
                )
                .withExposedService(
                        "mongodb",
                        27017,
                        Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(1000))
                )
                .withExposedService(
                        "redis",
                        6379,
                        Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(1000))
                )
                .withExposedService(
                        "zookeeper",
                        2181,
                        Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(1000))
                )
                .withExposedService(
                        "kafka",
                        9092,
                        Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(1000))
                )
                .withExposedService(
                        "app",
                        8080,
                        Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(1000))
                );

        containers.start();
    }
}


