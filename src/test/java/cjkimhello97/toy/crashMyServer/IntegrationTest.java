package cjkimhello97.toy.crashMyServer;

import com.redis.testcontainers.RedisContainer;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.junit.Ignore;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

@Ignore // 해당 클래스는 상속 목적이므로 테스트 제외
@Transactional // 각 테스트 실행 이후에 롤백
@SpringBootTest // 빈 등록
@ContextConfiguration(initializers = IntegrationTest.IntegrationTestInitializer.class)
@TestPropertySource(properties = "spring.config.location = classpath:application-test.yml")
public class IntegrationTest {

    static DockerComposeContainer rdbms;
    static MongoDBContainer mongodb;
    static RedisContainer redis;
    static KafkaContainer kafka;

    static {
        rdbms = new DockerComposeContainer(new File("docker-compose-local-test.yml"))
                .withExposedService("mysql", 3306);
        rdbms.start();

        mongodb = new MongoDBContainer("mongo:7.0")
                .withExposedPorts(27017);
        mongodb.start();

        redis = new RedisContainer(RedisContainer.DEFAULT_IMAGE_NAME.withTag("6"));
        redis.start();

        kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka"))
                .withKraft();
        kafka.start();
    }

    static class IntegrationTestInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            Map<String, String> properties = new HashMap<>();

            String rdbmsHost = rdbms.getServiceHost("mysql", 3306);
            Integer rdbmsPort = rdbms.getServicePort("mysql", 3306);
            properties.put("spring.datasource.url", "jdbc:mysql://" + rdbmsHost + ":" + rdbmsPort + "/crash_mysql");

            String redisHost = redis.getHost();
            Integer redisPort = redis.getFirstMappedPort();
            properties.put("spring.data.redis.host", redisHost);
            properties.put("spring.data.redis.port", redisPort.toString());

            String mongodbHost = mongodb.getHost();
            Integer mongodbPort = mongodb.getFirstMappedPort();
            properties.put("spring.data.mongodb.host", mongodbHost);
            properties.put("spring.data.mongodb.port", mongodbPort.toString());

            String kafkaBootstrapServers = kafka.getBootstrapServers();
            properties.put("spring.kafka.bootstrap-servers", kafkaBootstrapServers);

            TestPropertyValues.of(properties).applyTo(applicationContext);
        }
    }
}


