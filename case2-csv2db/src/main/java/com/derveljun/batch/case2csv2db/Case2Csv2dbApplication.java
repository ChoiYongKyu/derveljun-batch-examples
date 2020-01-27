package com.derveljun.batch.case2csv2db;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing
@SpringBootApplication
public class Case2Csv2dbApplication {

    public static void main(String[] args) {
        SpringApplication.run(Case2Csv2dbApplication.class, args);
    }

}
