package com.example.cruddata.service.imp;

import com.example.cruddata.constant.ApiErrorCode;
import com.example.cruddata.dto.web.DeleteEntityData;
import com.example.cruddata.dto.web.InsertEntityData;
import com.example.cruddata.dto.web.SampleSelectionEntityData;
import com.example.cruddata.dto.web.UpdateEntityData;
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
import java.util.Map;
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
            throw new BusinessException(ApiErrorCode.SQL_ERROR,"table not set column , Please confirm with admin");
        }
        SampleSelectionEntityData entityData =new SampleSelectionEntityData();
        entityData.setTableName(function.getFunctionName());
        entityData.setColumnSelectedList(columnNames);

        DataSourceConfig dataSourceConfig =  dataSourceService.getDataSourceById(function.getDataSourceId());

        return dataService.queryDataBySampleCondition(function.getDataSourceId(),dataSourceConfig.getDatabaseType(),entityData);
    }

    @Override
    public List<?> createDataList(Function function, List<Map<String,Object>> dataImport) throws SQLException {

        List<ColumnConfig> columnConfigs = columnConfigRepository.findByTableIdIn((Arrays.asList(function.getTableId())));

        if(columnConfigs.size() ==0) {
            throw new BusinessException(ApiErrorCode.SQL_ERROR,"table not set column , Please confirm with admin");
        }
        InsertEntityData insertEntityData = new InsertEntityData();
        insertEntityData.setColumnNameList(columnConfigs);
        insertEntityData.setTableName(function.getFunctionName());
        insertEntityData.setRecordList(dataImport);

        DataSourceConfig dataSourceConfig =  dataSourceService.getDataSourceById(function.getDataSourceId());
        dataService.insertData(function.getDataSourceId(),dataSourceConfig.getDatabaseType(),insertEntityData);

        return dataImport;
    }

    @Override
    public List<?> deleteDataList(Function function, List<Map<String,Object>> dataImport) throws SQLException {

        List<ColumnConfig> columnConfigs = columnConfigRepository.findByTableIdIn((Arrays.asList(function.getTableId())));


        if(columnConfigs.size() ==0) {
            throw new BusinessException(ApiErrorCode.SQL_ERROR,"table not set column , Please confirm with admin");
        }
        DeleteEntityData deleteEntityData = new DeleteEntityData();
        deleteEntityData.setTableName(function.getFunctionName());
        deleteEntityData.setRecordList(dataImport);

        DataSourceConfig dataSourceConfig =  dataSourceService.getDataSourceById(function.getDataSourceId());
        dataService.deleteData(function.getDataSourceId(),dataSourceConfig.getDatabaseType(),deleteEntityData);

        return dataImport;
    }

    @Override
    public List<?> updateDataList(Function function, List<Map<String, Object>> dataImport) throws SQLException {

        List<ColumnConfig> columnConfigs = columnConfigRepository.findByTableIdIn((Arrays.asList(function.getTableId())));

        if(columnConfigs.size() ==0) {
            throw new BusinessException(ApiErrorCode.SQL_ERROR,"table not set column , Please confirm with admin");
        }

        List<String> keyColumns = columnConfigs.stream().filter(item ->
                item.getPkType().equals("PRIMARY")).map((ColumnConfig) -> ColumnConfig.getName()).toList();


        List<String> dataColumns = columnConfigs.stream().filter(item ->
                !item.getPkType().equals("PRIMARY")).map((ColumnConfig) -> ColumnConfig.getName()).toList();
        UpdateEntityData updateEntityData = new UpdateEntityData();
        updateEntityData.setRecordList(dataImport);
        updateEntityData.setKeyColumns(keyColumns);
        updateEntityData.setDataColumns(dataColumns);
        updateEntityData.setTableName(function.getFunctionName());

        DataSourceConfig dataSourceConfig =  dataSourceService.getDataSourceById(function.getDataSourceId());
        dataService.UpdateData(function.getDataSourceId(),dataSourceConfig.getDatabaseType(),updateEntityData);

        return dataImport;
    }
}
