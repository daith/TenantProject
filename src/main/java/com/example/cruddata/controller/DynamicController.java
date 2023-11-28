package com.example.cruddata.controller;

import com.example.cruddata.constant.ApiErrorCode;
import com.example.cruddata.constant.FunctionType;
import com.example.cruddata.constant.SystemConsts;
import com.example.cruddata.dto.web.RoleFunctionData;
import com.example.cruddata.dto.web.TokenRoleFunctionResult;
import com.example.cruddata.entity.authroty.Function;
import com.example.cruddata.exception.BusinessException;
import com.example.cruddata.service.AccountService;
import com.example.cruddata.service.DynamicService;
import com.example.cruddata.util.CommonUtils;
import com.example.cruddata.util.RedisUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@Api(tags ="DynamicController 維護 - 新增,修改,刪除")
public class DynamicController {

    private static final Logger log = LoggerFactory.getLogger(DataSourceController.class);
    @Autowired
    private  DynamicService dynamicService;

    @Autowired
    private AccountService accountService;

    @GetMapping("{modelName}/{tableName}")
    public ResponseEntity<?> getDynamicData(@RequestHeader HttpHeaders headers, @PathVariable(value = "modelName") String modelName , @PathVariable(value = "tableName") String tableName ) throws SQLException, JsonProcessingException {
        String token = CommonUtils.getHeaderToken(headers);

        Function function = null;

        TokenRoleFunctionResult tokenRoleFunctionResult = accountService.tokenRoleFunctionValidation(token,tableName);
        if(tokenRoleFunctionResult.isCorrect()){

            function = tokenRoleFunctionResult.getFunctionMapByToken().get(FunctionType.QUERY.toString());

        }

        if(null == function){
            throw new BusinessException(ApiErrorCode.AUTH_ERROR,"you cant use this table query.");
        }


        return ResponseEntity.ok( dynamicService.getAllDataFromTable(function));
    }

    @PutMapping("{modelName}/{tableName}")
    public ResponseEntity<?> createDynamicData(@RequestHeader HttpHeaders headers, @PathVariable(value = "modelName") String modelName , @PathVariable(value = "tableName") String tableName ,@RequestBody List<Map<String,Object>>  dynamicEntityData ) throws SQLException, JsonProcessingException {
        log.info(dynamicEntityData.toString());
        String token = CommonUtils.getHeaderToken(headers);

        Function function = null;

        TokenRoleFunctionResult tokenRoleFunctionResult = accountService.tokenRoleFunctionValidation(token,tableName);
        if(tokenRoleFunctionResult.isCorrect()){

            function = tokenRoleFunctionResult.getFunctionMapByToken().get(FunctionType.CREATE.toString());

        }
        if(null == function){
            throw new BusinessException(ApiErrorCode.AUTH_ERROR,"you cant use this table CREATE data.");
        }


        return ResponseEntity.ok( dynamicService.createDataList(function ,dynamicEntityData ));
    }

    @DeleteMapping("{modelName}/{tableName}")
    public ResponseEntity<?> deleteDynamicData(@RequestHeader HttpHeaders headers, @PathVariable(value = "modelName") String modelName , @PathVariable(value = "tableName") String tableName ,@RequestBody List<Map<String,Object>>  dynamicEntityData ) throws SQLException, JsonProcessingException {
        log.info(dynamicEntityData.toString());
        String token = CommonUtils.getHeaderToken(headers);


        Function function = null;

        TokenRoleFunctionResult tokenRoleFunctionResult = accountService.tokenRoleFunctionValidation(token,tableName);
        if(tokenRoleFunctionResult.isCorrect()){

            function = tokenRoleFunctionResult.getFunctionMapByToken().get(FunctionType.DELETE.toString());

        }
        if(null == function){
            throw new BusinessException(ApiErrorCode.AUTH_ERROR,"you cant use this table query.");
        }


        return ResponseEntity.ok( dynamicService.deleteDataList(function ,dynamicEntityData ));
    }

    @PostMapping("{modelName}/{tableName}")
    public ResponseEntity<?> maitainDynamicData(@RequestHeader HttpHeaders headers, @PathVariable(value = "modelName") String modelName , @PathVariable(value = "tableName") String tableName ,@RequestBody List<Map<String,Object>>  dynamicEntityData ) throws SQLException, JsonProcessingException {
        log.info(dynamicEntityData.toString());
        String token = CommonUtils.getHeaderToken(headers);

        Function function = null;


        TokenRoleFunctionResult tokenRoleFunctionResult = accountService.tokenRoleFunctionValidation(token,tableName);
        if(tokenRoleFunctionResult.isCorrect()){

            function = tokenRoleFunctionResult.getFunctionMapByToken().get(FunctionType.UPDATE.toString());

        }
        if(null == function){
            throw new BusinessException(ApiErrorCode.AUTH_ERROR,"you cant use this table query.");
        }


        return ResponseEntity.ok( dynamicService.updateDataList(function ,dynamicEntityData ));
    }

}
