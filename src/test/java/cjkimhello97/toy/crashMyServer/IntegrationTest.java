package cjkimhello97.toy.crashMyServer;

import java.io.File;
import java.time.Duration;
import org.junit.Ignore;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

@Ignore // 해당 (부모)클래스는 동작 X
@Transactional // 테스트 이후 롤백 실행
@SpringBootTest // Spring Application을 실제 구동하는 것처럼 빈을 모두 스캔하여 등록
public class IntegrationTest {

    static DockerComposeContainer containers;

    static {
        containers = new DockerComposeContainer(new File("docker-compose-test.yml"))
                .withExposedService(
                        "mysql",
                        3306,
                        Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(300))
                )
                .withExposedService(
                        "mongodb",
                        27017,
                        Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(300))
                )
                .withExposedService(
                        "redis",
                        6379,
                        Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(300))
                )
                .withExposedService(
                        "zookeeper",
                        2181,
                        Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(300))
                )
                .withExposedService(
                        "kafka",
                        9092,
                        Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(300))
                );

        containers.start();
    }
}


