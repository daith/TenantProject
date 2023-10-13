package com.example.cruddata.entity.system;

import com.example.cruddata.entity.BasicEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
@Data
@Entity
public class RoleConfig extends BasicEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String roleName;
    public Boolean isDefault;

}
