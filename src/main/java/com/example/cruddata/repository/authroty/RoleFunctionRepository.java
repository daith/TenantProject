package com.example.cruddata.repository.authroty;

import com.example.cruddata.entity.authroty.RoleFunction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface RoleFunctionRepository extends CrudRepository<RoleFunction, Long> {

    List<RoleFunction> findByRoleId(Long roleId);

    List<RoleFunction> findByFunctionIdIn(@Param("functionId") List<Long> functionIds);
}
