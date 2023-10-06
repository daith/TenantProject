package com.example.cruddata.controller;

import com.example.cruddata.config.MultiTenantManager;
import com.example.cruddata.constant.DataSourceInfo;
import com.example.cruddata.exception.InvalidDbPropertiesException;
import com.example.cruddata.exception.InvalidTenantIdExeption;
import com.example.cruddata.exception.TenantNotFoundException;
import com.example.cruddata.exception.TenantResolvingException;
import com.example.cruddata.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

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
}
