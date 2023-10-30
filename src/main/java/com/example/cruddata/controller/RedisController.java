package com.example.cruddata.controller;

import com.example.cruddata.constant.RedisKeyConsts;
import com.example.cruddata.service.DataSourceService;
import com.example.cruddata.service.TenantService;
import com.example.cruddata.util.RedisUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Api(tags ="Redis 維護 - reset , retriveal")
@RestController
@RequestMapping("/api/redis/data")
@RequiredArgsConstructor
public class RedisController {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    TenantService tenantService;

    @Autowired
    DataSourceService dataSourceService;

    @GetMapping
    public ResponseEntity<?> getRedisByKey(@RequestParam(name = "key" )String key ){
        
        if (null == redisUtil.getHashEntries(key)) {
            return ResponseEntity.ok(redisUtil.getHashEntries(key));
        }
        return ResponseEntity.ok(redisUtil.get(key));
    }

    @PostMapping("/{tableName}")
    public ResponseEntity<?> resetTenantRedisByKey(@PathVariable(value = "tableName") String tableName) throws JsonProcessingException {
        List<String> tableNames= tableName.toLowerCase().equals("all") ? RedisKeyConsts.getRedisInitKeys(): Arrays.asList(tableName.toLowerCase());
        resetProcess(tableNames);
        return ResponseEntity.ok(tableName);
    }

    private void resetProcess(List<String> tableNames) throws JsonProcessingException {

       for(String key:tableNames){
            if(RedisKeyConsts.TENANT.toLowerCase().equals(key)){
                if (null != redisUtil.getHashEntries(key)) {
                    tenantService.resetTenantRedisData();
                }
            } else if (RedisKeyConsts.DATA_SOURCE.toLowerCase().equals(key)) {
                if (null != redisUtil.getHashEntries(key)) {
                    dataSourceService.resetDataSourceRedisData();
                }
            }
        }
    }
}
