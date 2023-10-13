package com.example.cruddata.controller;

import com.example.cruddata.config.MultiTenantManager;
import com.example.cruddata.constant.DataSourceInfo;
import com.example.cruddata.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/models")
public class ModelController {


    private static final Logger log = LoggerFactory.getLogger(ModelController.class);

    private final EmployeeService employeeService;

    private final MultiTenantManager tenantManager;
    public ModelController(EmployeeService employeeService, MultiTenantManager tenantManager) {
        this.employeeService = employeeService;
        this.tenantManager = tenantManager;
    }

    @GetMapping
    public ResponseEntity<?> getAll(@RequestHeader(value = "X-TenantID") String tenantId) {
        DataSourceInfo.setTenant(tenantManager , tenantId);
        return ResponseEntity.ok(employeeService.getAllEmployeeDetails());
    }

    @PostMapping("/{tableName}")
    public ResponseEntity<?> insertData(@RequestHeader(value = "X-TenantID") String tenantId, List<Map<String,Object>> recordList, @PathVariable(value = "tableName") String tableName) {
        DataSourceInfo.setTenant(tenantManager , tenantId);
        return ResponseEntity.ok(employeeService.getAllEmployeeDetails());
    }
}
