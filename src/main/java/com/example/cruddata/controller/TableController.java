package com.example.cruddata.controller;

import com.example.cruddata.config.MultiTenantManager;
import com.example.cruddata.constant.DataSourceInfo;
import com.example.cruddata.constant.DatabaseType;
import com.example.cruddata.dto.ColumnCreated;
import com.example.cruddata.dto.CreateEntityInfo;
import com.example.cruddata.exception.*;
import com.example.cruddata.service.TableService;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Api(tags ="資料源-資料表表")
@RestController
@RequestMapping("/api/metadata/tables")
public class TableController {

    private static final Logger log = LoggerFactory.getLogger(TableController.class);

    private final MultiTenantManager tenantManager;

    private final TableService tableService;

    public TableController(TableService tableService, MultiTenantManager tenantManager) {
        this.tableService = tableService;
        this.tenantManager = tenantManager;
    }

    @PostMapping
    public ResponseEntity<?> createTable(@RequestBody CreateEntityInfo createEntityInfo, @RequestHeader(value = "X-TenantID") String tenantId) {

        log.info("[i] create table info {}", createEntityInfo);


        if (createEntityInfo.getTableName().isEmpty()  || createEntityInfo.getColumnEntityList().isEmpty()) {
            log.error("[!] Received database params are incorrect or not full!");
            throw new InvalidDbPropertiesException();
        }

        try {
            String dbName = DatabaseType.POSTSQL;
            DataSourceInfo.setTenant(this.tenantManager ,tenantId);
            log.info("[i] Loaded DataSource for tenant '{}'.", tenantId);
            tableService.createTable(dbName ,  createEntityInfo);
            return ResponseEntity.ok(createEntityInfo);
        } catch (SQLException e) {
            throw new LoadDataSourceException(e);
        }
    }


}
