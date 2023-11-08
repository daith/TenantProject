package com.example.cruddata.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DynamicController {


    @GetMapping(value="/{tableName}")
    public ResponseEntity<?> getAllDataFromTable(@RequestParam(name = "tableName" ,required = true)String tableName ) {
        return ResponseEntity.ok(tenantService.getAllTenantByStatus(null == idDelete ? null:Boolean.valueOf(idDelete),status));
    }
}
