package com.example.cruddata.entity.system;

import com.example.cruddata.entity.BasicEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;


@Data
@Entity
public class DataSourceConfig extends BasicEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String name;
    public String caption;
    public String description;
    public Integer displayOrder;
    public Date createTime;
    public Date updateTime;

    public Integer updateById;
    public Integer createById;
    public String status;
    public Boolean isDeleted;
    public String databaseType;
    public String driverClassName;
    public String url;
    public String username;

    public String password;
    public String metadataTablePrefix;
    public String metadataDatabaseNaming;
    public String businessTablePrefix;
    public String businessDatabaseNaming;

    public String tenantId;

    public void preInsert() {
        super.preInsert();;
        this.status =(this.status == null)  ?"ACTIVE" :this.status;
    }

}
