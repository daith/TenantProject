package com.example.cruddata.repository.authroty;

import com.example.cruddata.entity.authroty.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface AccountRepository extends CrudRepository<Account, Long> {

    List<Account> findByTenantId(Long tenantId);

    Account findByTenantIdAndName(Long tenantId,String name);

}
