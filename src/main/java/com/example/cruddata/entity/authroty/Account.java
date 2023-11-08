package com.example.cruddata.entity.authroty;

import com.example.cruddata.entity.BasicEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.Date;

@Data
@Entity
public class Account extends BasicEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    Long tenantId;

    String name;

    String password;

    String token;
    Date tokenRenewTime;

}
