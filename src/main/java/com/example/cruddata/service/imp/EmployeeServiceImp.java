package com.example.cruddata.service.imp;

import com.example.cruddata.entity.data.Employee;
import com.example.cruddata.repository.EmployeeRepository;
import com.example.cruddata.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImp implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Override
    public List<Employee> getAllEmployeeDetails() {
        List<Employee> list = new ArrayList<>();
        employeeRepository.findAll().forEach(list::add);

        return list;
    }
}
