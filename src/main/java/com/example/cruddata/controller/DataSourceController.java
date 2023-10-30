package com.example.cruddata.controller;

import com.example.cruddata.service.DataSourceService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags ="dataSource 維護 - 新增,修改,刪除")
@RestController
@RequestMapping("/api/DataSource/data")
@RequiredArgsConstructor
public class DataSourceController {
    private static final Logger log = LoggerFactory.getLogger(DataSourceController.class);
    private final DataSourceService dataSourceService;

    @GetMapping
    public ResponseEntity<?> getAllDataSourceByStatus(@RequestParam("idDelete")String idDelete , @RequestParam("status") String status, @RequestParam("tenantId") String tenantId) {
        return ResponseEntity.ok(dataSourceService.getAllDataSourceByCondition(Boolean.valueOf(idDelete),status ,Long.valueOf(tenantId) ));
    }

}
