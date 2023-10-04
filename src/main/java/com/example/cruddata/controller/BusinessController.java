package com.example.cruddata.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
@Api(tags ="系統數據-增刪查改")
@RestController
@RequestMapping("/api/business/data")
public class BusinessController {
    private static final Logger log = LoggerFactory.getLogger(BusinessController.class);


}
