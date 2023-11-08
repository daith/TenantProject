package com.example.cruddata.repository.system;

import com.example.cruddata.entity.system.SelectionValue;
import jakarta.transaction.Transactional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface SelectionValueRepository extends CrudRepository<SelectionValue, Long> {

    @Override
    List<SelectionValue> findAll();



    Optional<SelectionValue> findByTenantIdAndTableNameAndColumnNameAndClientColumnKeyAndIsDeleted(@Param("tenantId")Long tenantId , @Param("tableName") String tableName , @Param("columnName") String columnName ,@Param("clientColumnKey")String clientColumnKey ,@Param("isDeleted")Boolean isDeleted);
}
