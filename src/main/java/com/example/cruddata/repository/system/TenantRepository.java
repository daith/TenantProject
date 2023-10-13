package com.example.cruddata.repository.system;

import com.example.cruddata.entity.account.Tenant;
import jakarta.transaction.Transactional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface TenantRepository extends CrudRepository<Tenant, Long> {
}
