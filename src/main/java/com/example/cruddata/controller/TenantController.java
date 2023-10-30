package com.example.cruddata.controller;

import com.example.cruddata.config.MultiDataSourceManager;
import com.example.cruddata.dto.business.TenantData;
import com.example.cruddata.entity.account.Tenant;
import com.example.cruddata.exception.InvalidDbPropertiesException;
import com.example.cruddata.exception.LoadDataSourceException;
import com.example.cruddata.service.TenantService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.Map;

@RestController
@RequestMapping("/tenants")
@RequiredArgsConstructor
public class TenantController {

    private static final Logger log = LoggerFactory.getLogger(TenantController.class);


    private final MultiDataSourceManager datasourceManager;

    private final TenantService tenantService;


    @GetMapping
    public ResponseEntity<?> getAllTenantByStatus(@RequestParam(name = "idDelete" ,required = false)String idDelete , @RequestParam(name = "status",required = false) String status) {
        return ResponseEntity.ok(tenantService.getAllTenantByStatus(null == idDelete ? null:Boolean.valueOf(idDelete),status));
    }

    @PostMapping
    public ResponseEntity<?> addTenant(@RequestBody TenantData tenantData) {
        Tenant tenant =new Tenant();
        tenant.setCompanyName(tenantData.getCompanyName());
        try {
            tenantService.addTenant( tenant);
        } catch (JsonProcessingException e) {
            return ResponseEntity.ok( e);
        }
        return ResponseEntity.ok( tenant);
    }


    public ResponseEntity<?> add(@RequestBody Map<String, String> dbProperty) {

        log.info("[i] Received add new tenant params request {}", dbProperty);

        String tenantId = dbProperty.get("tenantId");
        String url = dbProperty.get("url");
        String username = dbProperty.get("username");
        String password = dbProperty.get("password");
        String drive = dbProperty.get("drive");

        if (tenantId == null || url == null || username == null || password == null) {
            log.error("[!] Received database params are incorrect or not full!");
            throw new InvalidDbPropertiesException();
        }

        try {
            datasourceManager.addDataSource(tenantId, url, username, password,drive);
            log.info("[i] Loaded DataSource for tenant '{}'.", tenantId);
            return ResponseEntity.ok(dbProperty);
        } catch (SQLException e) {
            throw new LoadDataSourceException(e);
        }
    }
}
