    package com.bialas.robert.devicemanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.Clock;
import java.time.ZoneId;

@SpringBootApplication
public class DevicemanagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DevicemanagerApplication.class, args);
    }

    @Bean
    public Clock clock() {
        return Clock.system(ZoneId.of("UTC"));
    }

}
