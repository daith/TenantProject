package com.example.cruddata.repository.system;

import com.example.cruddata.entity.system.TableConfig;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface TableConfigRepository  extends CrudRepository<TableConfig, Long> {

    List<TableConfig> findTableConfigByNameAndTenantIdAndIsDeletedAndDataSourceId(String name, Long tenantId , boolean isDeleted,Long datasourceId);
}
