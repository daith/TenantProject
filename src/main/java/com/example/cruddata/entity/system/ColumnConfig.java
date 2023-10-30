package com.example.cruddata.entity.system;


import com.example.cruddata.constant.Status;
import com.example.cruddata.entity.BasicEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;


//public String dataType;
//public String name;
//public String defaultValue;
//public Boolean nullable;
//public Boolean autoIncrement;
//public String indexType;
//public String caption;
//public Integer length;
@Entity
@Data
public class ColumnConfig extends BasicEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String name;
    public String caption;
    public String description;
    public String defaultValue;

    public Integer displayOrder;
    public Boolean nullable;

    public Integer updateById;
    public Integer createById;
    public String dataType;
    public String apiResourceNaming;
    public String apiParamNaming;
    public String objectNaming;

    public Long tenantId;
    public Long tableId;

    public String status;


    public void preInsert() {
        super.preInsert();
        this.status =(this.status == null)  ? Status.ACTIVE :this.status;
    }

}
