package com.example.cruddata.entity.system;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.Date;


@Data
@Entity
public class SystemDataSource {

    @Id
    public Long id;
    public String name;
    public String caption;
    public String description;
    public Integer displayOrder;
    public Date createTime;
    public Date updateTime;
    public Date ownerId;
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


}
