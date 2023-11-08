package com.example.cruddata.controller;

import com.example.cruddata.dto.web.CreateEntityData;
import com.example.cruddata.entity.system.TableConfig;
import com.example.cruddata.exception.InvalidDbPropertiesException;
import com.example.cruddata.service.DocumentService;
import com.example.cruddata.service.SystemService;
import com.example.cruddata.service.TableColumnService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags ="資料源-資料表表")
@RestController
@RequestMapping("/api/metadata/tables")
@RequiredArgsConstructor
public class TableController {

    private static final Logger log = LoggerFactory.getLogger(TableController.class);

    private final SystemService systemService;

    private final TableColumnService tableColumnService;


    @PostMapping
    public ResponseEntity<?> createTable(@RequestBody CreateEntityData createEntity, @RequestHeader(value = "X-TenantID") String tenantId) {

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
        }
    }

    @GetMapping("/{dataSourceId}")
    public ResponseEntity<?> getTables(@RequestHeader(value = "X-TenantID") String tenantId , @PathVariable(value = "dataSourceId") Long dataSourceId ) {

        log.info("[i] create table info {}", dataSourceId);



        try {
            List<TableConfig> tables = systemService.getTableConfigs(dataSourceId, null, Long.valueOf(tenantId));
            return ResponseEntity.ok(tables);
        } catch (Exception e) {
            return ResponseEntity.ok(e);
        }
    }

    @DeleteMapping("/{dataSourceId}")
    public ResponseEntity<?> getTables(@RequestHeader(value = "X-TenantID") String tenantId , @PathVariable(value = "dataSourceId") Long dataSourceId , @RequestParam(name = "tableName" )String tableName ) {

        log.info("[i] create table info {}", dataSourceId);



        try {
            List<TableConfig> tables = systemService.getTableConfigs(dataSourceId, null, Long.valueOf(tenantId));
            return ResponseEntity.ok(tables);
        } catch (Exception e) {
            return ResponseEntity.ok(e);
        }
    }

    @DeleteMapping("deleteAll/{dataSourceId}")
    public ResponseEntity<?> deleteAll(@RequestHeader(value = "X-TenantID") String tenantId , @PathVariable(value = "dataSourceId") Long dataSourceId  ) {

        log.info("[i] deleteAll table info {}", dataSourceId);

        try {
            this.tableColumnService.deleteAllTableByDataSource(Long.valueOf(tenantId),dataSourceId);
            return ResponseEntity.ok(dataSourceId);
        } catch (Exception e) {
            return ResponseEntity.ok(e);
        }
    }

}
