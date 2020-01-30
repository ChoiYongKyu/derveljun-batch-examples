package com.derveljun.batch.case4jobparameter.job;



import com.derveljun.batch.case4jobparameter.job.vo.CsvVO;
import com.derveljun.batch.case4jobparameter.job.vo.DbVO;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Processor 는
 * ItemReader 를 통해 받아온 CsvVO 에 대해서
 * Validation 체크, 데이터 변환 작업 등을 할 수 있다.
 *
 * process() 에서 null 을 반환할 경우, 배치가 중단 된다.
 */
public class CsvItemProcessor implements org.springframework.batch.item.ItemProcessor<CsvVO, DbVO> {

    private String stockCode = "";

    private boolean isValid(CsvVO vo) {
        // TODO 유형성 검사
        return true;
    }

    @Override
    public DbVO process(CsvVO csvVO) throws Exception {

        // CSV 데이터 유효성검사
        if(!isValid(csvVO))
            return null;

        // 예제 csv 파일 첫 행에만 Stock Code 가 존재한다.
        if(csvVO.getStockCode() != null && stockCode.isEmpty())
            stockCode = csvVO.getStockCode();

        // 데이터 형변환 작업과 DbVO 객체 생성 작업
        return DbVO.builder()
                .stockCode(stockCode)
                .date(LocalDate.parse(csvVO.getDate(), DateTimeFormatter.BASIC_ISO_DATE))
                .start(csvVO.getStart())
                .high(csvVO.getHigh())
                .low(csvVO.getLow())
                .close(csvVO.getHigh())
                .volume(csvVO.getVolume())
                .build();
    }
}
