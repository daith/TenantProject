package com.example.cruddata.repository.system;

import com.example.cruddata.entity.system.FunctionConfig;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface FunctionConfigRepository extends CrudRepository<FunctionConfig, Long> {
}
