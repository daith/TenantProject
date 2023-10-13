package com.example.cruddata.entity.system;

import com.example.cruddata.entity.BasicEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class FunctionConfig extends BasicEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String functionType;
    public String functionName;
    public String parentId;

    public Integer displayOrder;

    public Integer updateById;
    public Integer createById;

}
