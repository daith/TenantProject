package com.example.cruddata.controller;

import com.example.cruddata.constant.ApiErrorCode;
import com.example.cruddata.constant.FunctionType;
import com.example.cruddata.dto.web.TokenRoleFunctionResult;
import com.example.cruddata.entity.authroty.Function;
import com.example.cruddata.exception.BusinessException;
import com.example.cruddata.exception.InvalidDbPropertiesException;
import com.example.cruddata.service.AccountService;
import com.example.cruddata.service.DocumentService;
import com.example.cruddata.service.SwaggerDocService;
import com.example.cruddata.util.CommonUtils;
import com.example.cruddata.util.RedisUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.patterns.IToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
public class FileController {

    private static final Logger log = LoggerFactory.getLogger(FileController.class);
    @Autowired
    private  DocumentService documentService;
    @Autowired
    private  SwaggerDocService swaggerDocService;
    @Autowired
    private AccountService accountService;

    @GetMapping(value="{dataSourceId}/{tableName}",produces=MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> excelDownloadFormDataImport( @PathVariable(value = "dataSourceId",required = false) String dataSourceId, @PathVariable(value = "tableName") String tableName, @RequestHeader(value = "X-TenantID") String tenantId) {

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
    @Operation(summary = "file Upload Import data of table", description = "匯入資料表的資料")
    public ResponseEntity<?> fileUploadImportData(@RequestHeader HttpHeaders headers,@PathVariable(value = "dataSourceId",required = false) String dataSourceId, @PathVariable(value = "tableName",required = false) String tableName, @RequestHeader(value = "X-TenantID",required = false) String tenantId, @RequestParam("file") MultipartFile file ) throws Exception {
        String token = CommonUtils.getHeaderToken(headers);
        Function function = null;

        TokenRoleFunctionResult tokenRoleFunctionResult = accountService.tokenRoleFunctionValidation(token,tableName);
        if(tokenRoleFunctionResult.isCorrect()){

            function = tokenRoleFunctionResult.getFunctionMapByToken().get(FunctionType.CREATE.toString());

        }
        if(null == function){
            throw new BusinessException(ApiErrorCode.AUTH_ERROR,"you cant use this table query.");
        }
        List<Object> result = documentService.importDataViaFile( Long.valueOf(dataSourceId),tableName, Long.valueOf(tenantId) , file);

        return ResponseEntity.ok(result);
    }



    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},value="codeList/{dataSourceId}")
    public ResponseEntity<?> fileUploadImportCodeList(@PathVariable(value = "dataSourceId",required = false) String dataSourceId,  @RequestParam("file") MultipartFile file, @RequestHeader(value = "X-TenantID",required = false) String tenantId) throws Exception {

        log.info("[i] fileUploadImportCreateTables  info {}", file);

        if(file.getSize() ==0){
            log.error("[!]  file must not full!");
            return ResponseEntity.ok(new InvalidDbPropertiesException());
        }

        List<Object> result = documentService.importCodeListViaFile( Long.valueOf(dataSourceId), Long.valueOf(tenantId) , file);
        return ResponseEntity.ok(result);
    }

    @PostMapping(value="roleJsonFile/{roleId}")
    public ResponseEntity<?> roleJsonFile( @RequestParam("roleId") String roleId ) throws JsonProcessingException {

        return ResponseEntity.ok(swaggerDocService.genSwaggerDoc(Long.valueOf(roleId)));
    }


}
