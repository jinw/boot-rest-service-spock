package hello;

import org.springframework.boot.SpringApplication
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

public class GreetingControllerSpec extends Specification {

    @Shared
    @AutoCleanup
    ConfigurableApplicationContext context

    void setupSpec() {
        Future future = Executors
                .newSingleThreadExecutor().submit(
                        new Callable() {
                            @Override
                            public ConfigurableApplicationContext call() throws Exception {
                                return (ConfigurableApplicationContext) SpringApplication
                                        .run(Application.class)
                            }
                        })
        context = future.get(60, TimeUnit.SECONDS)
    }

    void "should return default Greetings!"() {
        when:
        ResponseEntity entity = new RestTemplate().getForEntity("http://localhost:8080/greeting", String.class)

        then:
        entity.statusCode == HttpStatus.OK
        entity.body == '{"id":1,"content":"Hello, World!"}'
    }

    void "should return Greetings from input!"() {
        when:
        ResponseEntity<String> entity = new RestTemplate().getForEntity(url, String.class)

        then:
        entity.statusCode == HttpStatus.OK
        entity.body == reversedString

        where:
        url                                 || reversedString
        'http://localhost:8080/greeting?name=World' || '{"id":2,"content":"Hello, World!"}'
        'http://localhost:8080/greeting?name=Hello' || '{"id":3,"content":"Hello, Hello!"}'
    }

}
