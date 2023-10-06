package com.example.cruddata.controller;

import com.example.cruddata.config.MultiTenantManager;
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

    private static final String MSG_INVALID_TENANT_ID = "[!] DataSource not found for given tenant Id '{}'!";
    private static final String MSG_INVALID_DB_PROPERTIES_ID = "[!] DataSource properties related to the given tenant ('{}') is invalid!";
    private static final String MSG_RESOLVING_TENANT_ID = "[!] Could not resolve tenant ID '{}'!";

    private static final Logger log = LoggerFactory.getLogger(ModelController.class);

    private final EmployeeService employeeService;

    private final MultiTenantManager tenantManager;
    public ModelController(EmployeeService employeeService, MultiTenantManager tenantManager) {
        this.employeeService = employeeService;
        this.tenantManager = tenantManager;
    }

    @GetMapping
    public ResponseEntity<?> getAll(@RequestHeader(value = "X-TenantID") String tenantId) {
        setTenant(tenantId);
        return ResponseEntity.ok(employeeService.getAllEmployeeDetails());
    }



    private void setTenant(String tenantId) {
        try {
            tenantManager.setCurrentTenant(tenantId);
        } catch (SQLException e) {
            log.error(MSG_INVALID_DB_PROPERTIES_ID, tenantId);
            throw new InvalidDbPropertiesException();
        } catch (TenantNotFoundException e) {
            log.error(MSG_INVALID_TENANT_ID, tenantId);
            throw new InvalidTenantIdExeption();
        } catch (TenantResolvingException e) {
            log.error(MSG_RESOLVING_TENANT_ID, tenantId);
            throw new InvalidTenantIdExeption();
        }
    }
}
