package com.example.cruddata.controller;

import com.example.cruddata.config.MultiDataSourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/models")
public class ModelController {


    private static final Logger log = LoggerFactory.getLogger(ModelController.class);


    private final MultiDataSourceManager multiDataSourceManager;
    public ModelController( MultiDataSourceManager multiDataSourceManager) {
        this.multiDataSourceManager = multiDataSourceManager;
    }

}
