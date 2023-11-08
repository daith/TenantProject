package com.example.cruddata.repository.authroty;

import com.example.cruddata.entity.authroty.Function;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface FunctionRepository extends CrudRepository<Function, Long> {


    List<Function> findByIdInAndGroupTypeOrderByDisplayOrder(@Param("idList")List<Long> idList , @Param("groupType")String groupType);

    List<Function> findByFunctionNameInAndGroupTypeAndTenantIdAndDataSourceIdOrderByDisplayOrder(@Param("functionNames")List<String> functionNames , @Param("groupType")String groupType, @Param("tenantId")Long tenantId, @Param("dataSourceId")Long dataSourceId );


    List<Function> findByTenantIdAndFunctionNameAndGroupTypeAndDataSourceId(@Param("tenantId")Long tenantId , @Param("functionName")String functionName, @Param("groupType")String groupType , @Param("dataSourceId")Long dataSourceId);

    @Override
    List<Function> findAllById(Iterable<Long> ids);

}
