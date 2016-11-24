package za.co.bsg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BigBlueButtonApplication {

    public static void main(String[] args) {
        System.out.print("hello world");
        SpringApplication.run(BigBlueButtonApplication.class, args);
    }
}
