package com.derveljun.batch.case2csv2db.job;

import com.derveljun.batch.case2csv2db.job.vo.CsvVO;
import com.derveljun.batch.case2csv2db.job.vo.DbVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import javax.persistence.EntityManagerFactory;

/**
 * CSV 파일을 DB에 저장하는 간단한 예제
 */
@Slf4j
@RequiredArgsConstructor

@Configuration
public class Csv2DbJob {

    private final String PREFIX = "CSV2DB",
                        JOB_NAME = PREFIX + "JOB",
                        STEP_NAME = PREFIX + "STEP",
                        ITEM_READER_NAME = PREFIX + "READER";

    @Value("classpath:files/000120.csv")
    Resource csvFile;

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    @Bean
    /**
     * 배치 시작점
     */
    public Job job() {
        return jobBuilderFactory.get(JOB_NAME)
                .start(step())
                .build();
    }

    @Bean
    /**
     * Step 에서 CSV 파일을 읽고,
     *   ItemReader - 파일 읽기
     *   ItemProcessor - 데이터 정제
     *   ItemWriter - DB 저장
     * 하는 과정을 수행한다.
     */
    public Step step() {
        return stepBuilderFactory.get(STEP_NAME)
                .<CsvVO, DbVO>chunk(5) // <InputVO, OutputVO> chunk (파일을 나눠서 읽을 단위)
                .reader(csvReader())
                .processor(itemProcessor()) // Processor 는 간단히 함수로 표현하기도 한다. csvVo
                .writer(dbWriter())
                .build();
    }

    @Bean
    public FlatFileItemReader<CsvVO> csvReader() {
        return new FlatFileItemReaderBuilder()
                .name(ITEM_READER_NAME)
                .resource(csvFile)
                .targetType(CsvVO.class)
                .delimited()
                .names(CsvVO.CsvStockDataFields)
                .build();
    }

    @Bean
    public ItemProcessor<CsvVO, DbVO> itemProcessor() {
        return new Csv2DbItemProcessor();
    }


    @Bean
    public JpaItemWriter<DbVO> dbWriter() {
        return new JpaItemWriterBuilder<DbVO>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }


}
