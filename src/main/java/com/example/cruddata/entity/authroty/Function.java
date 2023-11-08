package com.example.cruddata.entity.authroty;

import com.example.cruddata.entity.BasicEntity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Function extends BasicEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    // data-swagger / System-Function
    public String groupType;

    // update / create  / delete / query / file upload
    public String functionType;
    public String functionName;

    public String description;
    public Integer displayOrder;

    public Long tenantId;

    public Long dataSourceId;

    public Long tableId;

}
