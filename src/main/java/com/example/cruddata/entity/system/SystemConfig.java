package com.example.cruddata.entity.system;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class SystemConfig {
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
    public Boolean isDefault;
    public String apiResourceNaming;
    public String apiParamNaming;
    public String objectNaming;


}
