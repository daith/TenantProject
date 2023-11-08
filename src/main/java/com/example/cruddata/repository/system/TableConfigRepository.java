package com.example.cruddata.repository.system;

import com.example.cruddata.entity.system.TableConfig;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface TableConfigRepository  extends CrudRepository<TableConfig, Long> {
    @Query("SELECT c FROM TableConfig c WHERE (:isDeleted is null or c.isDeleted = :isDeleted) and (:name is null or c.name = :name) "
            +"and (:tenantId is null or c.tenantId = :tenantId) and (:dataSourceId is null or c.dataSourceId = :dataSourceId)")
    List<TableConfig> findTableConfigByNameAndTenantIdAndIsDeletedAndDataSourceId(String name, Long tenantId , boolean isDeleted,Long dataSourceId);

    @Override
    List<TableConfig> findAllById(Iterable<Long> tableIds);
}
