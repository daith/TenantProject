package com.example.cruddata.service.imp;

import com.example.cruddata.constant.RedisKeyConsts;
import com.example.cruddata.constant.Status;
import com.example.cruddata.entity.system.DataSourceConfig;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public List<Map<String, Object>> list() {
        return null;
    }

    @Override
    public void deleteDataSourceConfig(DataSourceConfig recordList) {

    }

    @Override
    public void updateDataSourceConfig(DataSourceConfig recordList) {

    }

    @Override
    public void createDataSourceConfig(DataSourceConfig record) {

        tenantService.validatedTenantProcess(record.getTenantId());

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
