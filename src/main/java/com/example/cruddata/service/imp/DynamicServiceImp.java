package com.example.cruddata.service.imp;

import com.example.cruddata.dto.web.SampleSelectionEntityData;
import com.example.cruddata.entity.authroty.Function;
import com.example.cruddata.entity.system.ColumnConfig;
import com.example.cruddata.entity.system.DataSourceConfig;
import com.example.cruddata.exception.BusinessException;
import com.example.cruddata.repository.system.ColumnConfigRepository;
import com.example.cruddata.service.DataService;
import com.example.cruddata.service.DataSourceService;
import com.example.cruddata.service.DynamicService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DynamicServiceImp implements DynamicService {

    public final DataService dataService;

    public final DataSourceService dataSourceService;

    public final ColumnConfigRepository columnConfigRepository;



    @Override
    public List<?> getAllDataFromTable(Function function) throws SQLException {

        List<ColumnConfig> columnConfigs = columnConfigRepository.findByTableIdIn((Arrays.asList(function.getTableId())));
        List<String> columnNames = columnConfigs.stream()
                .map(ColumnConfig ::getName )
                .collect(Collectors.toList());
        if(columnConfigs.size() ==0) {
            throw new BusinessException("table not set column , Please confirm with admin");
        }
        SampleSelectionEntityData entityData =new SampleSelectionEntityData();
        entityData.setTableName(function.getFunctionName());
        entityData.setColumnSelectedList(columnNames);

        DataSourceConfig dataSourceConfig =  dataSourceService.getDataSourceById(function.getDataSourceId());

        return dataService.queryDataBySampleCondition(function.getDataSourceId(),dataSourceConfig.getDatabaseType(),entityData);
    }
}
