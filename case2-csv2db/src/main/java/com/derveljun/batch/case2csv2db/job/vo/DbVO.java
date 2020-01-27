package com.derveljun.batch.case2csv2db.job.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

// Lombok
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor

// JPA
@Entity
@Table(name = "stock_hist")
public class DbVO {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String stockCode;
    private double close;
    private double volume;
    private LocalDate date;
    private double start;
    private double high;
    private double low;

}
