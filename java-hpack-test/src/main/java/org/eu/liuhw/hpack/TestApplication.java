package org.eu.liuhw.hpack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author JavierHouse
 */
@SpringBootApplication(scanBasePackages = {"org.eu.liuhw"})
public class TestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }
}
