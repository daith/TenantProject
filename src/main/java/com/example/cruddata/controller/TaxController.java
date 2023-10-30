package com.example.cruddata.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TaxController {

    @GetMapping("/getTax")
    public ResponseEntity calculateTax(@RequestParam int income , @RequestParam int months){
        return ResponseEntity.ok().body(null);
    }

}
