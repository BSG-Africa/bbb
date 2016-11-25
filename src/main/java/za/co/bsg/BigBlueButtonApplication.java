package za.co.bsg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class BigBlueButtonApplication {
    public static void main(String[] args) {
        SpringApplication.run(BigBlueButtonApplication.class, args);
    }
}
