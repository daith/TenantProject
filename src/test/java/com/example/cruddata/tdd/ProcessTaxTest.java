package com.example.cruddata.tdd;

import com.example.cruddata.service.ProcessTaxService;

import com.example.cruddata.service.imp.ProcessTaxServiceImp;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

public class ProcessTaxTest {
    @InjectMocks
    ProcessTaxServiceImp processTax;
    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    public void testTaxCalcualtion(){
        int income =10;
        int months =5;
        double totalTax=processTax.calculate( income, months);
        Assertions.assertEquals(15.0,totalTax);
    }
}
