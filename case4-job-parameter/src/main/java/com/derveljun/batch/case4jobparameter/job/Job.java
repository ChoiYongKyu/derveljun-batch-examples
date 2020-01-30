//package com.derveljun.batch.case4jobparameter.job;
//
//import com.derveljun.batch.case4jobparameter.job.vo.CsvVO;
//import com.derveljun.batch.case4jobparameter.job.vo.DbVO;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
//import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
//import org.springframework.batch.core.configuration.annotation.StepScope;
//import org.springframework.batch.item.ItemProcessor;
//import org.springframework.batch.item.database.JpaItemWriter;
//import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
//import org.springframework.batch.item.file.FlatFileItemReader;
//import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.Resource;
//
//import javax.persistence.EntityManagerFactory;
//
//@Slf4j
//@RequiredArgsConstructor
//
//@Configuration
//public class Job {
//
//    private final String PREFIX = "CSV2DB",
//            JOB_NAME = PREFIX + "JOB",
//            STEP_NAME = PREFIX + "STEP",
//            ITEM_READER_NAME = PREFIX + "READER";
//
//    private final JobBuilderFactory jobBuilderFactory;
//    private final StepBuilderFactory stepBuilderFactory;
//    private final EntityManagerFactory entityManagerFactory;
//
//    @Bean
//    public Step step() {
//        return stepBuilderFactory.get("STEP")
//                .<CsvVO, DbVO>chunk(5)
//                .reader(csvReader())
//                .processor(itemProcessor())
//                .writer(dbWriter())
//                .build();
//    }
//
//    @Bean
//    @StepScope // JobParameter를 바인딩한다
//    public FlatFileItemReader<CsvVO> csvReader(@Value("classpath:#{jobParameters['input.fileName']}") Resource csvFile) {
//        return new FlatFileItemReaderBuilder()
//                .name(ITEM_READER_NAME)
//                .resource(csvFile)
//                .targetType(CsvVO.class)
//                .delimited()
//                .names(CsvVO.CsvStockDataFields)
//                .build();
//    }
//
//    @Bean
//    public ItemProcessor<CsvVO, DbVO> itemProcessor() {
//        return new CsvItemProcessor();
//    }
//
//
//    @Bean
//    public JpaItemWriter<DbVO> dbWriter() {
//        return new JpaItemWriterBuilder<DbVO>()
//                .entityManagerFactory(entityManagerFactory)
//                .build();
//    }
//}
