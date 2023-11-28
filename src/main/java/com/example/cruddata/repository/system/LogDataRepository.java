package com.example.cruddata.repository.system;

import com.example.cruddata.entity.system.ColumnConfig;
import com.example.cruddata.entity.system.LogData;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface LogDataRepository extends CrudRepository<LogData, Long> {

}
