package com.example.cruddata.entity.system;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.PrePersist;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@IdClass(OperatedDataLog.class)
@Entity
public class OperatedDataLog implements Serializable {

    @Id
    public String tokeId;
    @Id
    public String tenantId;
    @Id
    public String api;
    @Id
    public String error;
    @Id
    public String responseMsg;
    @Id
    public String parameter;
    @Id

    public Date entryTime;
    @Id
    public Date leaveTime;


}
