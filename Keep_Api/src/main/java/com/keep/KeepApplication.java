package com.keep;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;


@SpringBootApplication
@MapperScan(basePackages = {"com.keep.mapper"})
@ComponentScan(basePackages = {"com.keep","org.n3r.idworker"})
public class KeepApplication {

    public static void main(String[] args) {

        SpringApplication.run(KeepApplication.class, args);

    }

}

