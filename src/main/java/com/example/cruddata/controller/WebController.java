package com.example.cruddata.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller("")

public class WebController {

    private static final Logger log = LoggerFactory.getLogger(WebController.class);

    @GetMapping("/")
    public String hello(){
        return "hello";
    }


    @GetMapping("/web/{company}/swagger")
    public String swaggerForm(@PathVariable(value = "company") String company , Model model) {
        log.info("swaggerForm...");
        model.addAttribute("company", company);
        return "index";
    }
}
