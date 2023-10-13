package com.example.cruddata.controller;

import com.example.cruddata.dto.web.SystemConfigEntity;
import com.example.cruddata.service.SystemService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags ="系統設定")
@RestController
@RequestMapping("/api/system")
public class SystemController {
    @Autowired
    SystemService systemService;
    @GetMapping("system")
    public ResponseEntity<List<SystemConfigEntity>> getSystemConfigs(){
        return null;
    }
}
