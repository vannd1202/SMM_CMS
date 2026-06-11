package com.example.smm_cms.dto.request.report;

import com.example.smm_cms.dto.request.PageRequest;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class RevenueReportRequest extends PageRequest {
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate fromDate;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate toDate;

}
