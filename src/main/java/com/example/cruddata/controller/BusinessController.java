package com.example.cruddata.controller;

import com.example.cruddata.dto.web.UpdateEntityData;
import com.example.cruddata.service.DataService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags ="資料維護 - 新增,修改,刪除")
@RestController
@RequestMapping("/api/business/data")
@RequiredArgsConstructor
public class BusinessController {
    private static final Logger log = LoggerFactory.getLogger(BusinessController.class);

    private final DataService dataService;

    @PostMapping("/{tableName}")
    @ResponseBody
    public String updateDataByList(@RequestHeader(value = "X-TenantID") String tenantId,@PathVariable String tableName ,@RequestBody UpdateEntityData payload ) {
       /*
       *
       * 1. 取得 tenantId 下的 tableName 的欄位資訊
       * 2. 做欄位資訊的檢核
       * 3. 將檢核有問題的資料，做單拎錯誤訊息除理
       * 4. 針對重複上送的資料，當作是 Update 的處理
       * 5. 最後紀錄此次更新的資料總紀錄
       *
       * */
        return "ID: " + tableName;
    }

    @DeleteMapping("/{tableName}")
    @ResponseBody
    public String deleteDataByList(@RequestHeader(value = "X-TenantID") String tenantId,@PathVariable String tableName ,@RequestBody List<String> keyList ) {

        return "ID: " + tableName;
    }

}
