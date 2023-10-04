package com.example.cruddata.repository;


import com.example.cruddata.entity.data.Employee;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface EmployeeRepository extends CrudRepository<Employee, Integer> {

}
