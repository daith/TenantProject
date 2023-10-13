package com.example.cruddata.controller;

import com.example.cruddata.config.MultiTenantManager;
import com.example.cruddata.constant.DataSourceInfo;
import com.example.cruddata.constant.DatabaseType;
import com.example.cruddata.dto.web.CreateEntity;
import com.example.cruddata.exception.*;
import com.example.cruddata.service.DataService;
import com.example.cruddata.service.SystemService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@Api(tags ="資料源-資料表表")
@RestController
@RequestMapping("/api/metadata/tables")
@RequiredArgsConstructor
public class TableController {

    private static final Logger log = LoggerFactory.getLogger(TableController.class);

    private final SystemService systemService;


    @PostMapping
    public ResponseEntity<?> createTable(@RequestBody CreateEntity createEntity, @RequestHeader(value = "X-TenantID") String tenantId) {

        log.info("[i] create table info {}", createEntity);


        if (createEntity.getTableName().isEmpty()  || createEntity.getColumnEntityList().isEmpty()) {
            log.error("[!] Received database params are incorrect or not full!");
            return ResponseEntity.ok(new InvalidDbPropertiesException());
        }

        try {
            systemService.createTable( createEntity, Long.valueOf(tenantId));
            log.info("[i] Loaded DataSource for tenant '{}'.", tenantId);


            return ResponseEntity.ok(createEntity);
        } catch (Exception e) {
            return ResponseEntity.ok(e);
//            throw new LoadDataSourceException(e);
        }
    }


}
