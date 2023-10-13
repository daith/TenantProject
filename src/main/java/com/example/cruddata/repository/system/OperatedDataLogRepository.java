package com.example.cruddata.repository.system;

import com.example.cruddata.entity.system.OperatedDataLog;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface OperatedDataLogRepository  extends CrudRepository<OperatedDataLog,OperatedDataLog> {
}
