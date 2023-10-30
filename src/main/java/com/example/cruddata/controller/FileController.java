package com.example.cruddata.controller;

import com.example.cruddata.exception.InvalidDbPropertiesException;
import com.example.cruddata.service.DocumentService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Api(tags ="檔案處理")
@RestController
@RequestMapping("/api/doc")
@RequiredArgsConstructor
public class FileController {

    private static final Logger log = LoggerFactory.getLogger(FileController.class);

    private final DocumentService documentService;

    @GetMapping(value="{dataSourceId}/{tableName}",produces=MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> excelDownload(@PathVariable(value = "dataSourceId",required = false) String dataSourceId, @PathVariable(value = "tableName") String tableName, @RequestHeader(value = "X-TenantID") String tenantId) {

        log.info("[i] excelDownload table info {}", tableName);


        try {
            ByteArrayOutputStream inputStreamResource = documentService.excelDownloadＶaiTable( Long.valueOf(dataSourceId),tableName, Long.valueOf(tenantId));

            HttpHeaders headers = new HttpHeaders();

            String fileName = tableName+".xls";

            headers.setContentType(new MediaType("application", "force-download"));
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+fileName);

            return ResponseEntity.ok().headers(headers)
                    .body(inputStreamResource.toByteArray());

        } catch (Exception e) {
            return ResponseEntity.ok(e);
        }
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},value="{dataSourceId}/{tableName}")
    public ResponseEntity<?> fileUploadImportData(@PathVariable(value = "dataSourceId",required = false) String dataSourceId, @PathVariable(value = "tableName",required = false) String tableName, @RequestHeader(value = "X-TenantID",required = false) String tenantId, @RequestParam("file") MultipartFile file) {

        log.info("[i] fileUploadImportData  info {}", tableName);

        if(file.getSize() ==0){
            log.error("[!]  file must not full!");
            return ResponseEntity.ok(new InvalidDbPropertiesException());
        }

        try {
           List<Object> result = documentService.importDataViaFile( Long.valueOf(dataSourceId),tableName, Long.valueOf(tenantId) , file);


            return ResponseEntity.ok(result);


        } catch (Exception e) {
            return ResponseEntity.ok(e);
        }
    }

}