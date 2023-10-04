package com.example.cruddata.repository.system;

import com.example.cruddata.entity.system.SystemDataSource;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemDataSourceRepository extends CrudRepository<SystemDataSource, Long> {
}
