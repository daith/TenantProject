package com.example.cruddata.entity.system;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.Date;

@Data
@Entity
public class LogData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String methodType;

    public String urlData;

    public String remoteAddrIp;

    public String controller;

    public String header;

    public String parameterData;

    public String requestBody;

    public String response;
    public Date entryTime;

    public Date responseTime;

    public long wasteTime;
}
