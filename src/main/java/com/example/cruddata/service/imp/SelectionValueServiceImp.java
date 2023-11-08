package com.example.cruddata.service.imp;

import com.example.cruddata.entity.system.SelectionValue;
import com.example.cruddata.repository.system.SelectionValueRepository;
import com.example.cruddata.service.SelectionValueService;
import com.example.cruddata.util.RedisUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class SelectionValueServiceImp implements SelectionValueService {


    @Autowired
    RedisUtil redisUtil;
    @Autowired
    SelectionValueRepository selectionValueRepository;

    public void resetDataSourceRedisData() throws JsonProcessingException {
        List<SelectionValue> selectionValues = this.selectionValueRepository.findAll();

        for (SelectionValue selectionValue : selectionValues) {
            String data = selectionValue.getSystemColumnValue();
            String key = getSelectionRedisKey(selectionValue.getTableName(), selectionValue.getColumnName());
            if (null == redisUtil.getHashEntries(key)) {
                redisUtil.add(selectionValue.getClientColumnKey(), new HashMap<>());
            }
            redisUtil.add(key, selectionValue.getClientColumnKey(), data);
        }

    }

    private static String getSelectionRedisKey(String tableName, String columnName) {
        String key = tableName + "_" + columnName;
        return key;
    }

    @Override
    public Object mappingSystemSelectionValue(Long tenantId, String tableName, String column, Object clientValue) {
        String key = getSelectionRedisKey(tableName, column);
        Object selectionValue = clientValue;

        try {

            if (null != redisUtil.getHashEntries(key) && redisUtil.getHashEntries(key).size() > 0) {
                selectionValue = (String) redisUtil.getHashEntries(key).get(clientValue);
            } else {
                selectionValue = getValueFromDB(tenantId, tableName, column, clientValue, selectionValue);
            }

        } catch (Exception ex) {
            selectionValue = getValueFromDB(tenantId, tableName, column, clientValue, selectionValue);
        }


        return selectionValue;
    }

    @Override
    public void saveEntity(SelectionValue selectionValue) {
        this.selectionValueRepository.save(selectionValue);
    }

    private Object getValueFromDB(Long tenantId, String tableName, String column, Object clientValue, Object selectionValue) {
        Optional<SelectionValue> object = this.selectionValueRepository.findByTenantIdAndTableNameAndColumnNameAndClientColumnKeyAndIsDeleted(tenantId, tableName, column, clientValue.toString(), Boolean.FALSE);
        selectionValue = object.isPresent() ? object.get().getSystemColumnKey() : clientValue;
        return selectionValue;
    }


}
