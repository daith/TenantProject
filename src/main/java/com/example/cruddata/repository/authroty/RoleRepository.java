package com.example.cruddata.repository.authroty;

import com.example.cruddata.entity.authroty.Role;
import com.example.cruddata.entity.system.DataSourceConfig;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface RoleRepository extends CrudRepository<Role, Long> {

    @Override
    List<Role> findAll();

    List<Role> findByTenantId(Long tenantId);

    Role findByTenantIdAndRoleName(Long tenantId,String roleName);
}
