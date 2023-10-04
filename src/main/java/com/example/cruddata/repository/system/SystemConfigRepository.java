package com.example.cruddata.repository.system;

import com.example.cruddata.entity.system.SystemConfig;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemConfigRepository extends CrudRepository<SystemConfig, Long> {
}
