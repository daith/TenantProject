package com.example.cruddata.entity.account;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Tenant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String companyName;

    String status;

    Boolean isDeleted;
}
