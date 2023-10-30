package com.example.cruddata.repository.system;

import com.example.cruddata.entity.account.Tenant;
import com.example.cruddata.entity.system.DataSourceConfig;
import com.example.cruddata.entity.system.TableConfig;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface DataSourceConfigRepository extends CrudRepository<DataSourceConfig, Long> {

    Optional<DataSourceConfig> findByIdAndTenantIdAndIsDeleted(@Param("tenantId") Long tenantId ,@Param("id")  Long datasourceId ,@Param("isDeleted")  boolean isDeleted);

    @Query("SELECT c FROM DataSourceConfig c WHERE (:isDeleted is null or c.isDeleted = :isDeleted) and (:status is null or c.status = :status) "
            +"and (:tenantId is null or c.tenantId = :tenantId)")
    public List<DataSourceConfig> findByTenantIdAndIsDeletedAndStatue(@Param("isDeleted")Boolean isDeleted,@Param("status") String status,@Param("tenantId")Long tenantId);

    @Override
    List<DataSourceConfig> findAll();
}
