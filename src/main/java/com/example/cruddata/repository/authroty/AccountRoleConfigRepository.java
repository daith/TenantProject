package com.example.cruddata.repository.authroty;

import com.example.cruddata.entity.authroty.AccountRole;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface AccountRoleConfigRepository extends CrudRepository<AccountRole, Long> {

    AccountRole findByAccountId(Long accountId);

    List<AccountRole> findByRoleId(Long roleId);
}
