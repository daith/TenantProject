package com.example.cruddata.repository.system;

import com.example.cruddata.entity.system.RoleConfig;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface RoleConfigRepository  extends CrudRepository<RoleConfig, Long> {
}
