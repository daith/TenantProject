package com.example.cruddata.controller;

import com.example.cruddata.constant.ApiErrorCode;
import com.example.cruddata.dto.web.CreateEntityData;
import com.example.cruddata.exception.BusinessException;
import com.example.cruddata.service.DocumentService;
import com.example.cruddata.service.TableColumnService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;

@Api(tags ="資料源-資料表表")
@RestController
@RequestMapping("/api/metadata/tables")
@RequiredArgsConstructor
public class TableController {

    private final DocumentService documentService;

    private static final Logger log = LoggerFactory.getLogger(TableController.class);

    private final TableColumnService tableColumnService;


    @PostMapping
    public ResponseEntity<?> createTable(@RequestBody CreateEntityData createEntity, @RequestHeader(value = "X-TenantID") String tenantId) throws SQLException {

        log.info("[i] create table info {}", createEntity);


        if (createEntity.getTableName().isEmpty()  || createEntity.getColumnEntityList().isEmpty()) {
            log.error("[!] Received database params are incorrect or not full!");
            throw new BusinessException(ApiErrorCode.VALIDATED_ERROR , "[!] Received database params are incorrect or not full!",createEntity);
        }

        tableColumnService.createTable( createEntity, Long.valueOf(tenantId));
        log.info("[i] Loaded DataSource for tenant '{}'.", tenantId);
        return ResponseEntity.ok(createEntity);
    }

    @GetMapping("/{dataSourceId}")
    public ResponseEntity<?> getTables(@RequestHeader(value = "X-TenantID") String tenantId , @PathVariable(value = "dataSourceId") Long dataSourceId ) {

        log.info("[i] getTables table info {}", dataSourceId);

        return ResponseEntity.ok(tableColumnService.getTableConfigs(dataSourceId, null, Long.valueOf(tenantId)));
    }

    @DeleteMapping("/{dataSourceId}")
    public ResponseEntity<?> getTable(@RequestHeader(value = "X-TenantID") String tenantId , @PathVariable(value = "dataSourceId") Long dataSourceId , @RequestParam(name = "tableName" )String tableName ) {

        log.info("[i] getTable table info {}", dataSourceId +"("+tableName+")");

        return ResponseEntity.ok(tableColumnService.getTableConfigs(dataSourceId, tableName, Long.valueOf(tenantId)));
    }

    @DeleteMapping("deleteAll/{dataSourceId}")
    public ResponseEntity<?> deleteAll(@RequestHeader(value = "X-TenantID") String tenantId , @PathVariable(value = "dataSourceId") Long dataSourceId  ) {

        log.info("[i] deleteAll table info {}", dataSourceId);

        this.tableColumnService.deleteAllTableByDataSource(Long.valueOf(tenantId),dataSourceId);
        return ResponseEntity.ok(dataSourceId);
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},value="{dataSourceId}")
    public ResponseEntity<?> fileUploadImportCreateTables(@PathVariable(value = "dataSourceId",required = false) String dataSourceId, @RequestParam("file") MultipartFile file, @RequestHeader(value = "X-TenantID",required = false) String tenantId) throws Exception {

        log.info("[i] fileUploadImportCreateTables  info {}", file);

        if(file.getSize() ==0){
            log.error("[!]  file must not full!");
            throw new BusinessException(ApiErrorCode.VALIDATED_ERROR , "[!] file must not full!");
        }

        return ResponseEntity.ok(documentService.importTablesViaFile( Long.valueOf(dataSourceId), Long.valueOf(tenantId) , file));
    }

}
