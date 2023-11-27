package com.example.cruddata.service.imp;

import com.example.cruddata.constant.ApiErrorCode;
import com.example.cruddata.constant.RedisKeyConsts;
import com.example.cruddata.constant.Status;
import com.example.cruddata.entity.system.DataSourceConfig;
import com.example.cruddata.exception.BusinessException;
import com.example.cruddata.repository.system.ColumnConfigRepository;
import com.example.cruddata.repository.system.DataSourceConfigRepository;
import com.example.cruddata.service.DataSourceService;
import com.example.cruddata.service.TenantService;
import com.example.cruddata.util.CommonUtils;
import com.example.cruddata.util.RedisUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DataSourceServiceImp implements DataSourceService {
    @Autowired
    private DataSourceConfigRepository dataSourceConfigRepository;

    @Autowired
    ColumnConfigRepository columnConfigRepository;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    private TenantService tenantService;


    @Override
    public void createDataSourceConfig(DataSourceConfig record) {

        tenantService.validatedTenantProcess(record.getTenantId());
        if( null != record.getId()){
           Optional<DataSourceConfig> dataSourceConfig = dataSourceConfigRepository.findById(record.getId());
           if(dataSourceConfig.isPresent() && dataSourceConfig.get().getTenantId() !=  record.getTenantId()){
               throw new BusinessException(ApiErrorCode.AUTH_ERROR,"This tenant Id could not maintain this data source", record);
           }

            if(!dataSourceConfig.isPresent() ){
                throw new BusinessException(ApiErrorCode.VALIDATED_ERROR,"Data source not exist!", record);
            }
        }else{
            record.setCreateTime(new Date());
            record.setUpdateTime(new Date());
            record.setStatus(Status.ACTIVE);
            record.setIsDeleted(Boolean.FALSE);
        }


        dataSourceConfigRepository.save(record);

    }

    @Override
    public List<DataSourceConfig> getAllDataSourceByCondition(Boolean idDeleted, String status,Long tenantId) {

        return dataSourceConfigRepository.findByTenantIdAndIsDeletedAndStatue(idDeleted , status  , tenantId);

    }

    @Override
    public DataSourceConfig getDataSourceById(Long dataSourceId) {
        return this.dataSourceConfigRepository.findById(dataSourceId).get();
    }

    @Override
    public void resetDataSourceRedisData() throws JsonProcessingException {
        List<DataSourceConfig> dataSourceConfigList = this.getAllDataSourceByCondition(Boolean.FALSE, Status.ACTIVE,null);
        if(null != redisUtil.getHashEntries(RedisKeyConsts.DATA_SOURCE)){
            redisUtil.delete(RedisKeyConsts.DATA_SOURCE);
        }

        for(DataSourceConfig dataSourceConfig:dataSourceConfigList){
            String data=  CommonUtils.objectToJsonStr(dataSourceConfig);
            if(null == redisUtil.getHashEntries(RedisKeyConsts.DATA_SOURCE)){
                redisUtil.add(RedisKeyConsts.DATA_SOURCE,new HashMap<>());
            }
            redisUtil.add(RedisKeyConsts.DATA_SOURCE,String.valueOf(dataSourceConfig.getId()),data);
        }
    }


}
