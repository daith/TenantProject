package com.example.cruddata.controller;

import com.example.cruddata.constant.ApiErrorCode;
import com.example.cruddata.constant.Status;
import com.example.cruddata.dto.business.TenantData;
import com.example.cruddata.entity.system.DataSourceConfig;
import com.example.cruddata.exception.BusinessException;
import com.example.cruddata.service.DataSourceService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(tags ="dataSource 維護 - 新增,修改,刪除")
@RestController
@RequestMapping("/api/DataSource/data")
@RequiredArgsConstructor
public class DataSourceController {
    private static final Logger log = LoggerFactory.getLogger(DataSourceController.class);
    private final DataSourceService dataSourceService;

    @GetMapping
    public ResponseEntity<?> getAllDataSourceByStatus(@RequestParam("idDelete")String idDelete , @RequestParam("status") String status, @RequestParam("tenantId") String tenantId) {
        return ResponseEntity.ok(dataSourceService.getAllDataSourceByCondition(Boolean.valueOf(idDelete), Status.getStatus(status) ,Long.valueOf(tenantId) ));
    }

    @PostMapping
    public ResponseEntity<?> AddDataSource(@RequestBody DataSourceConfig dataSourceConfig ,@RequestHeader(value = "X-TenantID",required = false) String tenantId) {

        dataSourceConfig.setTenantId(Long.valueOf(tenantId));
        dataSourceService.createDataSourceConfig(dataSourceConfig);
        return ResponseEntity.ok(dataSourceConfig);
    }

}
