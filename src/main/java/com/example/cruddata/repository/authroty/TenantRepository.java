package com.example.cruddata.repository.authroty;

import com.example.cruddata.entity.authroty.Tenant;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public interface TenantRepository extends CrudRepository<Tenant, Long> {


    @Query("SELECT c FROM Tenant c WHERE (:isDeleted is null or c.isDeleted = :isDeleted) and (:status is null or c.status = :status)")
    public List<Tenant> findByStatusAndStatus(String status , Boolean isDeleted);

    @Override
    List<Tenant> findAll();
}
