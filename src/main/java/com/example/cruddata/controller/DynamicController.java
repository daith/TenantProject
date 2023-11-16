package com.example.cruddata.controller;

import com.example.cruddata.constant.FunctionType;
import com.example.cruddata.dto.web.RoleFunctionData;
import com.example.cruddata.entity.authroty.Function;
import com.example.cruddata.exception.BusinessException;
import com.example.cruddata.service.DynamicService;
import com.example.cruddata.util.CommonUtils;
import com.example.cruddata.util.RedisUtil;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Api(tags ="DynamicController 維護 - 新增,修改,刪除")
public class DynamicController {

    @Autowired
    private final DynamicService dynamicService;

    private final RedisUtil redisUtil;
    @GetMapping("{modelName}/{tableName}")
    public ResponseEntity<?> getDynamicData(@RequestHeader HttpHeaders headers, @PathVariable(value = "modelName") String modelName , @PathVariable(value = "tableName") String tableName ) throws SQLException {
        String token = CommonUtils.getHeaderToken(headers);

        if (null == redisUtil.getHashEntries(token)) {
            throw new BusinessException("token not correct.");
        }

        RoleFunctionData roleFunction = (RoleFunctionData) redisUtil.getHashEntries(token).get("roleFunction");
        Map<String , Function> functionMap = roleFunction.getFunctionActions().get(tableName);
       if(null == functionMap){
           throw new BusinessException("you cant use this table.");
       }
        Function function = functionMap.get(FunctionType.QUERY.toString());
        if(null == function){
            throw new BusinessException("you cant use this table query.");
        }
        return ResponseEntity.ok( dynamicService.getAllDataFromTable(function));

    }

}
