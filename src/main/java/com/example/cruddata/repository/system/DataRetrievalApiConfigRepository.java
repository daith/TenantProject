package com.example.cruddata.repository.system;

import com.example.cruddata.entity.system.DataRetrievalApiConfig;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface DataRetrievalApiConfigRepository extends CrudRepository<DataRetrievalApiConfig, Long> {
}
