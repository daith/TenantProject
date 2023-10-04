package com.example.cruddata.controller;

import com.example.cruddata.config.DataSourceContextHolder;
import com.example.cruddata.entity.data.Employee;
import com.example.cruddata.service.EmployeeService;
import com.example.cruddata.util.DataSourceEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DetailsController {

    private final EmployeeService employeeService;
    private final DataSourceContextHolder dataSourceContextHolder;
    @GetMapping(value="/getEmployeeDetails/{dataSourceType}" , produces= MediaType.APPLICATION_JSON_VALUE)
    public List<Employee> getAllEmployeeDetails(@PathVariable("dataSourceType") String dataSourceType){
        if(DataSourceEnum.DATASOURCE_TWO.toString().equals(dataSourceType)){
            dataSourceContextHolder.setBranchContext(DataSourceEnum.DATASOURCE_TWO);
        }else{
            dataSourceContextHolder.setBranchContext(DataSourceEnum.DATASOURCE_ONE);
        }
        return employeeService.getAllEmployeeDetails();
    }
}
