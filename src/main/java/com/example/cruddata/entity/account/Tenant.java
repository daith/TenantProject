package com.example.cruddata.entity.account;

import com.example.cruddata.entity.BasicEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Tenant extends BasicEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String companyName;

    String status;



    public void preInsert() {
        super.preInsert();;
        this.status =(this.status == null)  ?"ACTIVE" :this.status;
    }
}
