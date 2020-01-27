# Spring Batch

## Batch란?

Batch란 영어단어가 *일괄*  이란 뜻 답게 일괄적으로 처리한다는 뜻입니다. 보통 업계에서는 데이터를 일괄되게 추출(Extract)하거나, 변환(Transformation)하거나, 적재(Load)하는 작업을 앞자를 따서 ETL이라 합니다. 이런 ETL 과정을 일정한 시간과 순서, 조건에 따라 수행하는 작업을 Batch라고 합니다.

스프링 배치는 Job과 Step을 기준으로 배치를 수행하기 쉽게하고, 대용량 데이터를 처리하는 데에도 편리하도록 뭉텅이로 잘라 ETL 작업을 할 수 있는 Chunk 지향처리를 제공하고 있습니다.



## 개념 길라잡이

### 핵심

앞서 언급했듯, 스프링 배치는 Job과 Step을 기준으로 배치 단위를 실행합니다. 그리고 Job과 Step안에서는 배치에  필요하고 중요한 개념을 아래에서 설명하겠습니다.



### 런타임 메타데이터 모델

스프링배치는 배치실행에 관련한 모든 정보를 DB에 저장하고, 참조하며 순차적으로 실행합니다. 이러한 형태를 런타임 메타데이터 모델이라 합니다. 



### JobRepository

스프링배치는 미리 정의된 스키마에 메타데이터와 배치 실행 계획을 저장한 다음, 차례대로 실행해가며 결과를 저장합니다. 이런 실행 내용은 `JobRepository` 가 중심이 되어 처리합니다. `JobRepository`는 여러분이 설정한 `Job`과 `Step`등의 정보를 불러와 순차적으로 실행합니다.

- Spring batch 스키마 정보

![spring-batch-schema](Resource\spring-batch-schema.png)

### Job

Job은 하나의 배치 실행 단위입니다.

#### JobInstance

Job을 식별하기 위해 만들어낸 Instance입니다.  이때, Job 실행에 필요한 매개변수인 JobParameter를 Job에 담아 Instance로 만듭니다.

#### JobExecution

JobInstance가 실행되는 것을 JobExecution이라 합니다. JobInstance는 JobExecution과 1:1관계입니다. 즉, JobInstance를 다시 실행해야한다면, JobExecution 또한 하나더 생성되겠죠.



### Step

Step은 Job에서 실행하는 작은 실행단위입니다.  Step은 조건에 따라, 다음 스텝으로 진행하거나 진행을 멈출 수도 있고, 진행을 다시 실행할 수도 있습니다. 

그리고, 쓰레드를 통한 동시처리(병렬처리)와 같은 형태로 Step을 처리할 수도 있습니다.

#### ItemReader > ItemProcessor > ItemWriter

Step은 Step 자체로도 배치를 처리할 수도 있지만, 좀 더 세분화하여 순차적으로 실행할 수도 있습니다.

- ItemReader는 데이터를 읽어들이거나, File과 같은 것을 읽어들일 때 사용합니다.

- ItemProcessor는 읽어들인 데이터를 가공할 내용을 정의합니다. 생략할 수도 있습니다.
- ItemWriter는 읽어들인 데이터 또는 가공이 끝난 데이터를 저장할 때 사용합니다. 

#### Tasklet과 Chunk

Step은 처리과정에서 Tasklet을 직접 정의하거나, Chunk 지향처리로 처리할 수도 있습니다.

##### Chunk 지향처리

Chunk란 풀이하자면, 데이터 또는 파일 등 한번에 로드하기에 버거운 작업을 할 때 일정한 크기로 자른 뭉텅이라고 생각하시면됩니다. 데이터를 일정한 크기만큼 읽고, 가공한 다음, 저장하는 것을 의미합니다. 

만약, 여러분이 일배치로 1억건의 데이터를 받았다고 생각해봅시다. 1억건의 데이터를 모두 메모리로 읽어들이고, 가공한 다음, DB에 저장합니다. 읽어들이고 가공하고 저장할 때 아무런 에러없이 끝났다면 다행이지만, 만약 한 건의 데이터라도 예상과 다른 유형의 데이터가 섞여있다면? Retry하는 과정에서 다시 1억건의 데이터를 읽고 가공하고 저장하고의 과정을 반복해야할 겁니다. 우선 메모리 용량도 용량이지만, 한번에 모든 데이터를 가공할 때의 과정도 만만치 않으며, DB Insert나 File Out 하는 과정에서 트랜젝션을 묶고 있어야한다는 부담도 있습니다. 



# 간단한 실행 예제 - Hello Batch Job Step 

## Dependency Injection

### maven

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-batch</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

- spring-boot-starter-data-jpa
- h2

## Simple Job Configuration

### 스프링배치 스키마 설치

#### 만약 JobRepository를 설정하지 않았다면?

배치 실행에 있어 중요한 설정이기 때문에 로컬에 다음과 같은 DB가 설치되어 있다면, 자동으로 스키마 생성을 합니다.

​	batch-derby.properties
​	batch-h2.properties
​	batch-hsql.properties
​	batch-mysql.properties
​	batch-oracle.properties
​	batch-postgresql.properties
​	batch-sqlf.properties
​	batch-sqlserver.properties
​	batch-sybase.properties

 JPA를 기본으로 설정해두었다면, `JobConfigurer`에서 자동으로 스키마를 생성해줍니다.

#### 자동설정이 아닌 직접 DDL을 통해 스키마를 생성하고 싶다면?

1. `spring-batch-core-4.x.x.RELEASE.jar` 파일 찾으세요.
   인텔리J라면 External Libraries 안에서 찾으실 수 있습니다.
2. `org/springframework/batch/core` 를 찾아가시면, DDL 파일을 찾으실 수 있습니다.



## Environment Configuration

### 환경설정

- 임베디드 H2 사용 설정

```yaml
spring:
  profiles: local
  datasource:
    hikari:
      jdbc-url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
      username: sa
      password:
      driver-class-name: org.h2.Driver
  jpa:
    show-sql: true
```



### Java Configuration

- `@EnableBatchProcessing` 을 설정해야만, 배치 실행이 가능하다.

```java
@EnableBatchProcessing // Batch 실행 설정
@SpringBootApplication
public class Case1HellobatchApplication {
    public static void main(String[] args) {
        SpringApplication.run(Case1HellobatchApplication.class, args);
    }
}
```



### 실행계획

- Job 에서 Step1, Step2 를 차례대로 실행해보자.

```java
package com.derveljun.batch.case1hellobatch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@RequiredArgsConstructor // 간편하게 생성자 주입을 위한 Lombok 사용

@Configuration
public class HelloBatchJob {

    private final String BATCH_NAME = "HelloBatchJob_";

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job job(){
        return jobBuilderFactory.get(BATCH_NAME)
                .start(step1())
                .next(step2())
                .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get(BATCH_NAME + "Step1")
                .tasklet((stepContribution, chunkContext) -> {
                    log.info(BATCH_NAME + "Step1 Started");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Step step2() {
        return stepBuilderFactory.get(BATCH_NAME + "Step2")
                .tasklet((stepContribution, chunkContext) -> {
                    log.info((BATCH_NAME + "Step2 Started"));
                    return RepeatStatus.FINISHED;
                }).build();
    }

}
```



### 실행결과

- 설정된 데이터베이스와 DDL 이 없으니 자동으로 설정해주는 로그를 확인할 수 있다.

```
2020-01-27 15:54:24.808  INFO 11460 --- [           main] o.s.b.c.r.s.JobRepositoryFactoryBean     : No database type set, using meta data indicating: H2
```



- Job 실행에서부터 Step1 , 2가 차례대로 실행한 걸 볼 수 있다.

````
2020-01-27 15:54:24.981  INFO 11460 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=HelloBatchJob_]] launched with the following parameters: [{}]
2020-01-27 15:54:25.004  INFO 11460 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Executing step: [HelloBatchJob_Step1]
2020-01-27 15:54:25.010  INFO 11460 --- [           main] c.d.batch.case1hellobatch.HelloBatchJob  : HelloBatchJob_Step1 Started
2020-01-27 15:54:25.014  INFO 11460 --- [           main] o.s.batch.core.step.AbstractStep         : Step: [HelloBatchJob_Step1] executed in 10ms
2020-01-27 15:54:25.019  INFO 11460 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Executing step: [HelloBatchJob_Step2]
2020-01-27 15:54:25.021  INFO 11460 --- [           main] c.d.batch.case1hellobatch.HelloBatchJob  : HelloBatchJob_Step2 Started
2020-01-27 15:54:25.022  INFO 11460 --- [           main] o.s.batch.core.step.AbstractStep         : Step: [HelloBatchJob_Step2] executed in 3ms
2020-01-27 15:54:25.024  INFO 11460 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=HelloBatchJob_]] completed with the following parameters: [{}] and the following status: [COMPLETED] in 29ms
````



### 



# 참고

- https://docs.spring.io/spring-batch/docs/4.0.x/reference/html/index-single.html#chunkOrientedProcessing
- 스프링 레시피
- https://jojoldu.tistory.com/325?category=635883
- https://blog.woniper.net/356
- https://12bme.tistory.com/365