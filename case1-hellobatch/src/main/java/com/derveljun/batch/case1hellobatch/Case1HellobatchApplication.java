package com.derveljun.batch.case1hellobatch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing // Batch 실행 설정
@SpringBootApplication
public class Case1HellobatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(Case1HellobatchApplication.class, args);
    }

}
