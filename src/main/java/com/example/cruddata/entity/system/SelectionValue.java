package com.example.cruddata.entity.system;

import com.example.cruddata.entity.BasicEntity;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class SelectionValue extends BasicEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String tableName;

    public String columnName;

    public String systemColumnKey;
    public String systemColumnValue;

    public String clientColumnKey;

    public String parentColumnName;
    public String parentColumnKey;

    public Boolean used;

    public Long tenantId;

}
