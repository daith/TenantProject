package com.example.cruddata.entity;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.Data;

import java.util.Date;
@Data
@MappedSuperclass
public class BasicEntity {

    public Date createTime;
    public Date updateTime;
    public Boolean isDeleted;
    @PrePersist
    public void preInsert() {
        this.createTime =(this.createTime == null)  ?new Date() :this.createTime;
        this.updateTime =(this.updateTime == null)  ?new Date() :this.updateTime;
        this.isDeleted =(this.isDeleted == null)  ? Boolean.FALSE :this.isDeleted;
    }
}
