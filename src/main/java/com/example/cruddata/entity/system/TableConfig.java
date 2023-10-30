package com.example.cruddata.entity.system;

import com.example.cruddata.constant.Status;
import com.example.cruddata.entity.BasicEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;


@Data
@Entity
public class TableConfig extends BasicEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String name;
    public String caption;
    public String description;
    public Integer displayOrder;

    public Integer updateById;
    public Integer createById;

    public Long dataSourceId;

    public Long tenantId;

    public String status;
    public void preInsert() {
        super.preInsert();
        this.status =(this.status == null)  ? Status.ACTIVE :this.status;
    }

}
