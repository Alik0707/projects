package com.example.report.controller;

import com.example.report.dto.ProductDeliveryReport;
import com.example.report.service.ReportService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/report")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/delivery-status")
    public Flux<ProductDeliveryReport> getDeliveryStatus(
            @RequestParam("ids") List<Long> ids) {
        return reportService.getDeliveryReport(ids);
    }
}