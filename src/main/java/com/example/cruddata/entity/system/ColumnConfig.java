package com.example.cruddata.entity.system;


import com.example.cruddata.entity.BasicEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class ColumnConfig extends BasicEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String name;
    public String caption;
    public String description;
    public Integer displayOrder;

    public Integer updateById;
    public Integer createById;

    public Boolean isDefault;
    public String apiResourceNaming;
    public String apiParamNaming;
    public String objectNaming;

    public Long tenantId;
    public Long tableId;

    public String status;


    public void preInsert() {
        super.preInsert();
        this.status =(this.status == null)  ?"ACTIVE" :this.status;
        this.isDefault =(this.isDefault == null)  ? Boolean.TRUE :this.isDefault;
    }

}
