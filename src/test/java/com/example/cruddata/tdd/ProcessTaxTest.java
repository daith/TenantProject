package com.example.cruddata.tdd;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
